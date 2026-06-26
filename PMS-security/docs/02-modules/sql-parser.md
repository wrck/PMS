# SQL 解析组件

## 1. 概述

`SQLParser` 是基于 **Druid SQLUtils** 的 SQL 解析工具类，提供表名提取、正则匹配校验、SQL 变量解析与填充能力。

> ⚠️ **重要**：本组件使用 Druid 的 `com.alibaba.druid.sql.SQLUtils`，**不是 JSQLParser**。旧版文档中关于 JSQLParser 的描述均为虚构。

---

## 2. 类定义

```java
package com.dp.plat.security.util;

public class SQLParser {
    private static final String regex = "\\b(?:from|join|insert\\s+into|insert|update|delete\\s+from|delete)\\s+`?(\\w+)`?\\s*";
    private static final Pattern parserSqlTablePattern = Pattern.compile(regex, 
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNICODE_CASE);
}
```

---

## 3. 依赖

| 依赖 | 来源 | 用途 |
|------|------|------|
| `com.alibaba.druid.sql.SQLUtils` | druid 1.2.8 | SQL 格式化与解析 |
| `com.alibaba.druid.sql.ast.SQLStatement` | druid | SQL AST |
| `com.alibaba.druid.sql.visitor.SchemaStatVisitor` | druid | 表名访问者 |
| `com.alibaba.druid.stat.TableStat.Name` | druid | 表名 |
| `com.alibaba.druid.DbType` | druid | 数据库类型枚举 |
| `com.alibaba.fastjson.JSON` | fastjson | 变量解析配置 |
| `org.apache.commons.lang3.StringUtils` | commons-lang3 | 字符串处理 |

---

## 4. 核心功能

### 4.1 SQL 语句解析

| 方法 | 返回值 | 说明 |
|------|--------|------|
| `parseStatements(String sql, DbType dbType)` | `List<SQLStatement>` | 格式化并解析为语句列表 |
| `parseSingleStatement(String sql, DbType dbType)` | `SQLStatement` | 解析单条语句 |
| `parseStatementsVisitors(String sql, DbType dbType)` | `List<SchemaStatVisitor>` | 解析并返回访问者列表 |
| `parseStatementsVisitor(String sql, DbType dbType)` | `SchemaStatVisitor` | 解析单条语句并返回访问者 |

```java
public static List<SQLStatement> parseStatements(String sql, DbType dbType) {
    String result = SQLUtils.format(sql, dbType);  // 先格式化
    return SQLUtils.parseStatements(result, dbType);
}
```

### 4.2 表名提取

```java
public static Set<String> parseTables(String sql, DbType dbType) {
    List<SchemaStatVisitor> visitors = parseStatementsVisitors(sql, dbType);
    Set<String> tables = new HashSet<>();
    for (SchemaStatVisitor visitor : visitors) {
        Set<Name> keySet = visitor.getTables().keySet();
        List<String> names = keySet.stream().map(Name::getName).collect(Collectors.toList());
        tables.addAll(names);
    }
    return tables;
}

public static Set<String> parseTables(String sql) {
    return parseTables(sql, null);  // dbType 为 null
}
```

### 4.3 正则匹配校验

#### matcherAll（全部匹配）

```java
public static boolean matcherAll(String sql, String regex) {
    Set<String> tables = parseTables(sql);
    return matcherAll(tables, regex);
}

public static boolean matcherAll(Set<String> tables, String regex) {
    Pattern compile = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    for (String tableName : tables) {
        if (!compile.matcher(tableName).matches()) {
            return false;  // 任一不匹配则 false
        }
    }
    return true;
}
```

#### unMatcherAll（全部不匹配）

```java
public static boolean unMatcherAll(Set<String> tables, String regex) {
    Pattern compile = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    for (String tableName : tables) {
        if (compile.matcher(tableName).matches()) {
            return false;  // 任一匹配则 false
        }
    }
    return true;
}
```

#### matcherSqlTables（返回详细结果）

```java
public static SqlParserResult matcherSqlTables(String sql, String regex, DbType dbType) {
    Set<String> tables = parseTables(sql, dbType);
    return matcherTables(tables, regex);
}

public static SqlParserResult matcherTables(Set<String> tables, String regex) {
    Set<String> unMatcherTable = new HashSet<>();
    Pattern compile = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    for (String tableName : tables) {
        if (!compile.matcher(tableName).matches()) {
            unMatcherTable.add(tableName);
        }
    }
    return new SqlParserResult(unMatcherTable.size() == 0, unMatcherTable);
}
```

### 4.4 方法重载清单

| 方法 | DbType 参数 |
|------|------------|
| `parseTables(String sql)` | 无（null） |
| `parseTables(String sql, DbType dbType)` | 有 |
| `matcherAll(String sql, String regex)` | 无 |
| `matcherAll(String sql, String regex, DbType dbType)` | 有 |
| `matcherSqlTables(String sql, String regex)` | 无 |
| `matcherSqlTables(String sql, String regex, DbType dbType)` | 有 |
| `unMatcherAll(String sql, String regex)` | 无 |
| `unMatcherAll(String sql, String regex, DbType dbType)` | 有 |
| `unMatcherSqlTables(String sql, String regex)` | 无 |
| `unMatcherSqlTables(String sql, String regex, DbType dbType)` | 有 |

---

## 5. SQL 变量解析与填充

### 5.1 默认变量分隔符

```java
private final static String DEFALUE_SQL_PARAMS_PARTS = 
    "{\"${|}\":{\"before\":\"${\",\"after\":\"}\",\"quote\":false}," +
    "\"#{|}\":{\"before\":\"#{\",\"after\":\"}\",\"quote\":\"'\"}," +
    "\"$|$\":{\"before\":\"$\",\"after\":\"$\",\"quote\":false}," +
    "\"#|#\":{\"before\":\"#\",\"after\":\"#\",\"quote\":\"'\"}}";
```

| 分隔符 | before | after | quote | 示例 |
|--------|--------|-------|-------|------|
| `${|}` | `${` | `}` | false（不加引号） | `${userId}` |
| `#{|}` | `#{` | `}` | `'`（加单引号） | `#{userId}` |
| `$|$` | `$` | `$` | false | `$userId$` |
| `#|#` | `#` | `#` | `'` | `#userId#` |

### 5.2 parseSqlParams

```java
public static Map<String, Map<String, Object>> parseSqlParams(String sql) {
    Map<String, Map<String, Object>> splitPartMap = JSON.parseObject(DEFALUE_SQL_PARAMS_PARTS, MapMapType);
    return parseSqlParams(sql, splitPartMap);
}

public static Map<String, Map<String, Object>> parseSqlParams(String sql, 
        Map<String, Map<String, Object>> splitPartMap) {
    Map<String, Map<String, Object>> params = new HashMap<>();
    for (Map<String, Object> splitPart : splitPartMap.values()) {
        String beforeSplit = quoteSplit((String) splitPart.get("before"));
        String afterSplit = quoteSplit((String) splitPart.get("after"));
        String regex = beforeSplit + "([^" + beforeSplit + afterSplit + "\\ ,]*)" + afterSplit;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            String param = matcher.group();
            if (params.containsKey(param)) continue;
            params.put(param, splitPart);
        }
    }
    return params;
}
```

### 5.3 fillSqlParams

```java
public static String fillSqlParams(String sql, Map<String, Object> values) {
    Map<String, Map<String, Object>> params = parseSqlParams(sql);
    for (Entry<String, Map<String, Object>> paramMap : params.entrySet()) {
        String field = paramMap.getKey();
        Map<String, Object> param = paramMap.getValue();
        param.put("field", field);
        Object value = parseObjectValue(param, values);
        if (value instanceof Collection) {
            value = StringUtils.join((Collection<?>) value, ",");
        }
        String valueRegx = "\\Q" + field + "\\E";
        try {
            sql = sql.replaceAll(valueRegx, value.toString());
        } catch (Exception e) {
            value = Matcher.quoteReplacement(value.toString());
            sql = sql.replaceAll(valueRegx, value.toString());
        }
    }
    return sql;
}
```

### 5.4 parseObjectValue（嵌套属性解析）

```java
public static Object parseObjectValue(Map<String, Object> param, Map<String, Object> values) {
    String field = (String) param.get("field");
    String beforeSplit = quoteSplit((String) param.get("before"));
    String afterSplit = quoteSplit((String) param.get("after"));
    Object quote = param.get("quote");
    String key = field.replaceAll(beforeSplit + "|" + afterSplit, "");
    
    // 支持 user.username 嵌套属性
    if (key.contains(".") && !values.containsKey(key)) {
        String[] relations = key.split("\\.");
        // 逐层解析嵌套对象
        // ...
    } else {
        value = values.getOrDefault(key, "");
    }
    
    // quote 处理
    if (Boolean.FALSE.equals(quote) || StringUtils.isBlank((String) quote)) {
        return value;  // 不加引号
    } else {
        return quote + value + quote;  // 加引号
    }
}
```

---

## 6. 辅助方法

### 6.1 quoteSplit（正则特殊字符转义）

```java
public static String quoteSplit(String split) {
    if (!split.matches(".*[\\$|\\(|\\)|\\*|\\+|\\.|\\[|\\]|\\?|\\\\|\\/|\\^|\\{|\\}].*")) {
        return split;
    }
    char[] signs = new char[] {'$','(',')','*','+','.','[',']','?','\\','/','^','{','}'};
    Arrays.sort(signs);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < split.length(); i++) {
        char c = split.charAt(i);
        if (Arrays.binarySearch(signs, c) >= 0) {
            sb.append('\\');
        }
        sb.append(c);
    }
    return sb.toString();
}
```

### 6.2 getCurrentDbType

```java
public static DbType getCurrentDbType(DataSource dataSource) {
    String dbType = null;
    if (dataSource != null) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            dbType = connection.getMetaData().getDatabaseProductName();
        } catch (Throwable e) {
        } finally {
            if (connection != null) {
                try { connection.close(); } catch (SQLException e) {}
            }
        }
    }
    return DbType.of(dbType != null ? dbType.toLowerCase() : dbType);
}
```

### 6.3 toJSONString

```java
public static String toJSONString(Object obj) {
    return JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat);
}
```

---

## 7. SqlParserResult 内部类

```java
public static class SqlParserResult {
    private boolean valid;
    private Set<String> matchTables;

    public SqlParserResult() {}
    public SqlParserResult(boolean valid, Set<String> matchTables) { ... }
    
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public Set<String> getMatchTables() { return matchTables; }
    public void setMatchTables(Set<String> matchTables) { this.matchTables = matchTables; }
}
```

---

## 8. 使用示例

### 8.1 提取表名

```java
String sql = "SELECT * FROM pm_project p LEFT JOIN pm_project_member m ON p.projectId = m.projectId";
Set<String> tables = SQLParser.parseTables(sql, DbType.mysql);
// 结果：[pm_project, pm_project_member]
```

### 8.2 校验表名是否匹配白名单

```java
String sql = "SELECT * FROM pm_project";
boolean valid = SQLParser.matcherAll(sql, "pm_.*", DbType.mysql);
// 结果：true（所有表名都以 pm_ 开头）
```

### 8.3 填充 SQL 变量

```java
String sql = "SELECT * FROM pm_project WHERE createBy = '${user.userName}' AND projectId = #{projectId}";
Map<String, Object> values = new HashMap<>();
values.put("user", Collections.singletonMap("userName", "w02611"));
values.put("projectId", 123456);
String filled = SQLParser.fillSqlParams(sql, values);
// 结果：SELECT * FROM pm_project WHERE createBy = 'w02611' AND projectId = '123456'
```

---

## 9. 相关文档

| 文档 | 说明 |
|------|------|
| [../05-standards/security-practices.md](../05-standards/security-practices.md) | SQL 注入防护实践 |
| [class-reference.md](class-reference.md) | 类参考清单 |
