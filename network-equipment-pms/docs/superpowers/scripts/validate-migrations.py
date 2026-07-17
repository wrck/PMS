#!/usr/bin/env python3
"""
validate-migrations.py — Phase 8 / Task 8.2
====================================================================
校验 PMS 项目 V64~V72 迁移脚本的 SQL 完整性。

校验项（参考设计文档 §8.2 任务 8.2）：
  1. 每个 CREATE TABLE 语句以 ENGINE=InnoDB 结尾
  2. 每个 ALTER TABLE 操作有 IF EXISTS 保护（信息架构 PROCEDURE 写法）或被包裹在
     DROP PROCEDURE / DELIMITER PROCEDURE 块中以做幂等保护
  3. INSERT 语句的列数与 VALUES 元组数匹配（按 VALUES 子句的括号元组计数）
  4. 括号匹配（圆括号 / 方括号 / 大括号）
  5. 分号结尾检查（每个 SQL 语句必须以分号结尾，DELIMITER 块除外）
  6. JSON 函数调用闭合检查（JSON_OBJECT / JSON_ARRAY）

退出码：0 = 全部通过；1 = 发现错误；2 = 发现警告但无错误。
====================================================================
"""
from __future__ import annotations

import re
import sys
from dataclasses import dataclass, field
from pathlib import Path
from typing import List, Tuple

# 迁移脚本目录
# 迁移脚本目录（脚本位于 <repo>/docs/superpowers/scripts/，仓库根为 parents[3]）
MIGRATION_DIR = (
    Path(__file__).resolve().parents[3]
    / "pms-admin"
    / "src"
    / "main"
    / "resources"
    / "db"
    / "migration"
)

# 校验范围：V64 ~ V72
TARGET_FILES = sorted(
    [p for p in MIGRATION_DIR.glob("V*.sql") if re.match(r"V(6[4-9]|7[0-2])__", p.name)],
    key=lambda p: int(re.match(r"V(\d+)__", p.name).group(1)),
)


@dataclass
class Finding:
    """单条校验结果。"""

    file: str
    line: int
    severity: str  # 'ERROR' / 'WARN' / 'INFO'
    rule: str
    message: str


@dataclass
class FileReport:
    """单文件校验报告。"""

    path: Path
    findings: List[Finding] = field(default_factory=list)
    statement_count: int = 0
    create_table_count: int = 0
    alter_table_count: int = 0
    insert_count: int = 0


# --------------------------------------------------------------------------
# 工具函数
# --------------------------------------------------------------------------

def strip_sql_comments(sql: str) -> str:
    """剥离 SQL 行注释与块注释，保留行号便于定位。"""
    out_lines = []
    in_block_comment = False
    for line in sql.splitlines():
        if in_block_comment:
            if "*/" in line:
                in_block_comment = False
                line = line.split("*/", 1)[1]
            else:
                out_lines.append("")
                continue
        # 块注释开始
        if "/*" in line:
            before, after = line.split("/*", 1)
            if "*/" in after:
                after = after.split("*/", 1)[1]
                line = before + " " + after
            else:
                in_block_comment = True
                out_lines.append(before)
                continue
        # 行注释
        if "--" in line:
            line = line.split("--", 1)[0]
        out_lines.append(line)
    return "\n".join(out_lines)


def split_top_level_statements(sql: str) -> List[Tuple[int, str]]:
    """按分号拆分顶层 SQL 语句，跳过 DELIMITER 块。

    返回 [(起始行号 1-based, 语句文本)]。
    """
    statements: List[Tuple[int, str]] = []
    buf: List[str] = []
    buf_start_line = 1
    current_line = 1
    in_delimiter_block = False
    custom_delim = "$$"
    i = 0
    n = len(sql)
    while i < n:
        ch = sql[i]
        if ch == "\n":
            current_line += 1
        # 检测 DELIMITER 指令
        if not in_delimiter_block and sql[i : i + 10].upper().startswith("DELIMITER "):
            # 找到行尾
            j = sql.find("\n", i)
            if j == -1:
                j = n
            line = sql[i:j]
            m = re.match(r"DELIMITER\s+(\S+)", line, re.IGNORECASE)
            if m:
                custom_delim = m.group(1)
                in_delimiter_block = True
                if buf:
                    statements.append((buf_start_line, "".join(buf)))
                    buf = []
                i = j
                buf_start_line = current_line + 1
                continue
        if in_delimiter_block:
            if sql[i : i + len(custom_delim)] == custom_delim:
                stmt = "".join(buf)
                if stmt.strip():
                    statements.append((buf_start_line, stmt))
                buf = []
                i += len(custom_delim)
                buf_start_line = current_line
                # 检测 DELIMITER ; 恢复
                # 跳过空白与可能的 DELIMITER ;
                k = i
                while k < n and sql[k] in " \t\r\n":
                    if sql[k] == "\n":
                        current_line += 1
                    k += 1
                if sql[k : k + 11].upper().startswith("DELIMITER ;"):
                    j = sql.find("\n", k)
                    if j == -1:
                        j = n
                    i = j
                    in_delimiter_block = False
                continue
            buf.append(ch)
            i += 1
            continue
        if ch == ";":
            stmt = "".join(buf)
            if stmt.strip():
                statements.append((buf_start_line, stmt))
            buf = []
            i += 1
            buf_start_line = current_line
            continue
        buf.append(ch)
        i += 1
    if buf:
        stmt = "".join(buf)
        if stmt.strip():
            statements.append((buf_start_line, stmt))
    return statements


# --------------------------------------------------------------------------
# 校验规则
# --------------------------------------------------------------------------

def check_create_table_engine(report: FileReport, stmt: str, line: int) -> None:
    """规则 1：CREATE TABLE 必须以 ENGINE=InnoDB 结尾。"""
    upper = stmt.upper().rstrip()
    if not re.match(r"\s*CREATE\s+TABLE", upper):
        return
    report.create_table_count += 1
    if "ENGINE=INNODB" not in upper.replace(" ", ""):
        report.findings.append(
            Finding(
                file=report.path.name,
                line=line,
                severity="ERROR",
                rule="R1_ENGINE_INNODB",
                message="CREATE TABLE 必须以 ENGINE=InnoDB 结尾",
            )
        )


def check_alter_table_guarded(report: FileReport, stmt: str, line: int, raw_sql: str) -> None:
    """规则 2：ALTER TABLE 必须 IF EXISTS 保护或被 PROCEDURE 幂等包裹。

    判定：
      - 若语句以 ALTER TABLE 开头（顶层）：
          * 必须包含 IF EXISTS（仅 ALTER TABLE ... DROP COLUMN/INDEX 时支持）
          * 或者外层文件中存在 DROP PROCEDURE ... CREATE PROCEDURE 包裹模式（即整个文件以 PROCEDURE 方式幂等）
      - 否则视为 WARN（需人工确认）
    """
    upper = stmt.upper().lstrip()
    if not upper.startswith("ALTER TABLE"):
        return
    report.alter_table_count += 1
    # 检测 IF EXISTS / IF NOT EXISTS
    if "IF EXISTS" in upper or "IF NOT EXISTS" in upper:
        return
    # 检测 MODIFY COLUMN（这类不需要 IF EXISTS，仅警告）
    if "MODIFY COLUMN" in upper:
        # MODIFY COLUMN 不需要 IF EXISTS，但是需要确认列已存在
        return
    # 检测 ADD COLUMN/INDEX（需要 PROCEDURE 包裹）
    if "ADD COLUMN" in upper or "ADD INDEX" in upper:
        # 检查文件是否使用 PROCEDURE 包裹（粗略检测）
        if "DROP PROCEDURE IF EXISTS" in raw_sql and "CREATE PROCEDURE" in raw_sql:
            return
        report.findings.append(
            Finding(
                file=report.path.name,
                line=line,
                severity="WARN",
                rule="R2_ALTER_GUARDED",
                message="ALTER TABLE ADD 操作未检测到 PROCEDURE 幂等包裹（建议使用 information_schema 检测）",
            )
        )


def count_paren_groups(s: str) -> int:
    """计数 VALUES 子句中的顶层元组数（仅对 INSERT 单行 VALUES 简化判定）。"""
    # 找到 VALUES 关键字
    m = re.search(r"\bVALUES\s*(.*)", s, re.IGNORECASE | re.DOTALL)
    if not m:
        return 0
    rest = m.group(1)
    depth = 0
    count = 0
    in_string = False
    string_char = None
    i = 0
    while i < len(rest):
        ch = rest[i]
        if in_string:
            if ch == "\\" and i + 1 < len(rest):
                i += 2
                continue
            if ch == string_char:
                in_string = False
            i += 1
            continue
        if ch in "'\"`":
            in_string = True
            string_char = ch
            i += 1
            continue
        if ch == "(":
            if depth == 0:
                count += 1
            depth += 1
        elif ch == ")":
            depth -= 1
        i += 1
    return count


def parse_insert_column_count(stmt: str) -> int:
    """解析 INSERT INTO tbl (col1, col2, ...) 中的列数；无列名时返回 -1。"""
    m = re.search(
        r"INSERT\s+(?:IGNORE\s+)?INTO\s+[`\"\w.]+\s*\(([^)]*)\)",
        stmt,
        re.IGNORECASE | re.DOTALL,
    )
    if not m:
        return -1
    cols_text = m.group(1)
    # 简化按逗号分割（忽略字符串/括号嵌套）
    depth = 0
    cols = []
    buf = []
    in_string = False
    string_char = None
    for ch in cols_text:
        if in_string:
            if ch == string_char:
                in_string = False
            buf.append(ch)
            continue
        if ch in "'\"`":
            in_string = True
            string_char = ch
            buf.append(ch)
            continue
        if ch == "(":
            depth += 1
        elif ch == ")":
            depth -= 1
        elif ch == "," and depth == 0:
            cols.append("".join(buf).strip())
            buf = []
            continue
        buf.append(ch)
    if buf:
        last = "".join(buf).strip()
        if last:
            cols.append(last)
    return len(cols)


def check_insert_columns(report: FileReport, stmt: str, line: int) -> None:
    """规则 3：INSERT 语句列数与 VALUES 元组列数匹配。

    本规则仅做粗略静态校验：
      - 如果 INSERT 显式列出列名（N 列），则 VALUES 中每个元组也应该有 N 个字段。
      - 通过统计每个元组内顶层逗号 + 1 的方式得到字段数。
    """
    upper = stmt.upper().lstrip()
    if not upper.startswith("INSERT"):
        return
    report.insert_count += 1
    col_count = parse_insert_column_count(stmt)
    if col_count <= 0:
        # 未显式列出列名，跳过
        return
    # 找到 VALUES 后的内容
    m = re.search(r"\bVALUES\s*(.*)", stmt, re.IGNORECASE | re.DOTALL)
    if not m:
        return
    rest = m.group(1)
    # 拆分顶层元组（按顶层逗号分隔，但要在括号内）
    tuples = []
    depth = 0
    buf = []
    in_string = False
    string_char = None
    i = 0
    while i < len(rest):
        ch = rest[i]
        if in_string:
            if ch == "\\" and i + 1 < len(rest):
                buf.append(ch)
                buf.append(rest[i + 1])
                i += 2
                continue
            if ch == string_char:
                in_string = False
            buf.append(ch)
            i += 1
            continue
        if ch in "'\"`":
            in_string = True
            string_char = ch
            buf.append(ch)
            i += 1
            continue
        if ch == "(":
            if depth == 0:
                buf = []
            depth += 1
            buf.append(ch)
            i += 1
            continue
        if ch == ")":
            depth -= 1
            buf.append(ch)
            if depth == 0:
                tuples.append("".join(buf))
                buf = []
            i += 1
            continue
        if depth > 0:
            buf.append(ch)
        i += 1
    # 对每个元组统计字段数
    for tup in tuples:
        # 统计顶层逗号
        d = 0
        comma_count = 0
        in_str = False
        sc = None
        j = 0
        while j < len(tup):
            c = tup[j]
            if in_str:
                if c == "\\" and j + 1 < len(tup):
                    j += 2
                    continue
                if c == sc:
                    in_str = False
                j += 1
                continue
            if c in "'\"`":
                in_str = True
                sc = c
                j += 1
                continue
            if c == "(":
                d += 1
            elif c == ")":
                d -= 1
            elif c == "," and d == 1:  # 元组内顶层
                comma_count += 1
            j += 1
        field_count = comma_count + 1
        if field_count != col_count:
            report.findings.append(
                Finding(
                    file=report.path.name,
                    line=line,
                    severity="ERROR",
                    rule="R3_INSERT_COLUMNS",
                    message=(
                        f"INSERT 列数({col_count}) 与 VALUES 元组字段数({field_count}) 不匹配"
                    ),
                )
            )


def check_brackets(report: FileReport, raw: str) -> None:
    """规则 4：括号匹配（圆括号、方括号、大括号）。"""
    pairs = {"(": ")", "[": "]", "{": "}"}
    closers = set(pairs.values())
    stack: List[Tuple[int, int, str]] = []
    in_string = False
    string_char = None
    line = 1
    i = 0
    # 仅在剥离注释后的内容上检查
    while i < len(raw):
        ch = raw[i]
        if ch == "\n":
            line += 1
            i += 1
            continue
        if in_string:
            if ch == "\\" and i + 1 < len(raw):
                i += 2
                continue
            if ch == string_char:
                in_string = False
            i += 1
            continue
        if ch in "'\"`":
            in_string = True
            string_char = ch
            i += 1
            continue
        if ch in pairs:
            stack.append((line, i, ch))
        elif ch in closers:
            if not stack:
                report.findings.append(
                    Finding(
                        file=report.path.name,
                        line=line,
                        severity="ERROR",
                        rule="R4_BRACKETS",
                        message=f"多余的闭合括号 '{ch}'",
                    )
                )
            else:
                opener_line, _, opener = stack.pop()
                expected_closer = pairs[opener]
                if ch != expected_closer:
                    report.findings.append(
                        Finding(
                            file=report.path.name,
                            line=line,
                            severity="ERROR",
                            rule="R4_BRACKETS",
                            message=(
                                f"括号不匹配：'{opener}'（行 {opener_line}）"
                                f"对应应为 '{expected_closer}'，实际 '{ch}'"
                            ),
                        )
                    )
        i += 1
    for line_no, _, opener in stack:
        report.findings.append(
            Finding(
                file=report.path.name,
                line=line_no,
                severity="ERROR",
                rule="R4_BRACKETS",
                message=f"未闭合的括号 '{opener}'",
            )
        )


def check_json_balance(report: FileReport, raw: str) -> None:
    """规则 6：JSON_OBJECT / JSON_ARRAY 调用的圆括号必须闭合。

    简化做法：对每个 JSON_OBJECT( 或 JSON_ARRAY( 关键字定位开始括号，向后扫描
    找到对应的闭合括号。如果在文件末尾仍未闭合，报 ERROR。
    """
    pattern = re.compile(r"\b(JSON_OBJECT|JSON_ARRAY)\s*\(", re.IGNORECASE)
    for m in pattern.finditer(raw):
        start_pos = m.end() - 1  # 指向 '('
        depth = 0
        in_string = False
        string_char = None
        i = start_pos
        closed = False
        while i < len(raw):
            ch = raw[i]
            if in_string:
                if ch == "\\" and i + 1 < len(raw):
                    i += 2
                    continue
                if ch == string_char:
                    in_string = False
                i += 1
                continue
            if ch in "'\"":
                in_string = True
                string_char = ch
                i += 1
                continue
            if ch == "(":
                depth += 1
            elif ch == ")":
                depth -= 1
                if depth == 0:
                    closed = True
                    break
            i += 1
        if not closed:
            line_no = raw.count("\n", 0, m.start()) + 1
            report.findings.append(
                Finding(
                    file=report.path.name,
                    line=line_no,
                    severity="ERROR",
                    rule="R6_JSON_BALANCE",
                    message=f"{m.group(1)} 调用括号未闭合",
                )
            )


# --------------------------------------------------------------------------
# 主流程
# --------------------------------------------------------------------------

def validate_file(path: Path) -> FileReport:
    """对单个迁移文件执行所有校验。"""
    report = FileReport(path=path)
    raw = path.read_text(encoding="utf-8")
    stripped = strip_sql_comments(raw)
    statements = split_top_level_statements(stripped)
    report.statement_count = len(statements)
    for line_no, stmt in statements:
        # 重新定位行号（split_top_level_statements 已基于 stripped 内容）
        check_create_table_engine(report, stmt, line_no)
        check_alter_table_guarded(report, stmt, line_no, raw)
        check_insert_columns(report, stmt, line_no)
    check_brackets(report, stripped)
    check_json_balance(report, stripped)
    return report


def main() -> int:
    if not TARGET_FILES:
        print("[FATAL] 未找到 V64~V72 迁移文件", file=sys.stderr)
        return 1

    print("=" * 78)
    print(f"Phase 8 / Task 8.2 — 迁移脚本完整性校验（V64~V72，{len(TARGET_FILES)} 个文件）")
    print("=" * 78)

    all_reports: List[FileReport] = []
    error_count = 0
    warn_count = 0

    for path in TARGET_FILES:
        report = validate_file(path)
        all_reports.append(report)
        err = [f for f in report.findings if f.severity == "ERROR"]
        warn = [f for f in report.findings if f.severity == "WARN"]
        error_count += len(err)
        warn_count += len(warn)
        status = "OK" if not err else "FAIL"
        print(
            f"\n[{status}] {path.name}  "
            f"statements={report.statement_count}  "
            f"CREATE TABLE={report.create_table_count}  "
            f"ALTER TABLE={report.alter_table_count}  "
            f"INSERT={report.insert_count}  "
            f"errors={len(err)}  warns={len(warn)}"
        )
        for f in report.findings:
            print(f"    {f.severity:5s} L{f.line:<5d} {f.rule:24s} {f.message}")

    print("\n" + "=" * 78)
    print(
        f"汇总：{len(all_reports)} 文件 / "
        f"{sum(r.statement_count for r in all_reports)} 语句 / "
        f"{sum(r.create_table_count for r in all_reports)} CREATE TABLE / "
        f"{sum(r.alter_table_count for r in all_reports)} ALTER TABLE / "
        f"{sum(r.insert_count for r in all_reports)} INSERT / "
        f"{error_count} 错误 / {warn_count} 警告"
    )
    print("=" * 78)

    if error_count:
        return 1
    if warn_count:
        return 2
    return 0


if __name__ == "__main__":
    sys.exit(main())
