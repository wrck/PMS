package com.dp.plat.core.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Name;

public class SQLParser {

	private static final String regex = "\\b(?:from|join|insert\\s+into|insert|update|delete\\s+from|delete)\\s+`?(\\w+)`?\\s*";

	private static final Pattern parserSqlTablePattern = Pattern.compile(regex,
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNICODE_CASE);

	public static List<SQLStatement> parseStatements(String sql, DbType dbType) {
		String result = SQLUtils.format(sql, dbType);
        return SQLUtils.parseStatements(result, dbType);
	}
	
	public static SQLStatement parseSingleStatement(String sql, DbType dbType) {
		String result = SQLUtils.format(sql, dbType);
        return SQLUtils.parseSingleStatement(result, dbType);
	}
	
	public static List<SchemaStatVisitor> parseStatementsVisitors(String sql, DbType dbType) {
		List<SQLStatement> statements = parseStatements(sql, dbType);
		List<SchemaStatVisitor> visitors = new ArrayList<SchemaStatVisitor>(statements.size());
		for (SQLStatement sqlStatement : statements) {
//            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
			SchemaStatVisitor visitor = new SchemaStatVisitor(dbType);
			sqlStatement.accept(visitor);
			visitors.add(visitor);
		}
        return visitors;
	}
	
	public static SchemaStatVisitor parseStatementsVisitor(String sql, DbType dbType) {
		SQLStatement singleStatement = parseSingleStatement(sql, dbType);
//            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        SchemaStatVisitor visitor = new SchemaStatVisitor(dbType);
        singleStatement.accept(visitor);
        return visitor;
	}
	
	public static Set<String> parseTables(String sql, DbType dbType) {
		List<SchemaStatVisitor> visitors = parseStatementsVisitors(sql, dbType);
		Set<String> tables = new HashSet<String>();
		for (SchemaStatVisitor visitor : visitors) {
			Set<Name> keySet = visitor.getTables().keySet();
			List<String> names = keySet.stream().map(Name::getName).collect(Collectors.toList());
			tables.addAll(names);
		}
		return tables;
	}
	
//	public static Set<String> parserSqlTables(String key) {
//		Set<String> tables = new HashSet<>();
//		Matcher matcher = parserSqlTablePattern.matcher(key.toString());
//		while (matcher.find()) {
//			String tableName = matcher.group(1);
//			if (tableName != null && tableName.trim().length() > 0) {
//				tables.add(tableName);
//			}
//		}
//		return tables;
//	}
	
	public static Set<String> parserSqlTables(String sql) {
		return parseTables(sql, null);
	}

	/**
	 * 根据正则匹配是否全部匹配，如果全部匹配则返回true，否则返回false
	 * 
	 * @param tables
	 * @param regex
	 * @return
	 */
	public static boolean matcherAll(String sql, String regex) {
		Set<String> tables = parserSqlTables(sql);
		return matcherAll(tables, regex);
	}
	
	/**
	 * 根据正则匹配是否全部匹配，如果全部匹配则返回true，否则返回false
	 * 
	 * @param tables
	 * @param regex
	 * @return
	 */
	public static SqlParserResult matcherSqlTables(String sql, String regex) {
		Set<String> tables = parserSqlTables(sql);
		return matcherTables(tables, regex);
	}
	
	/**
	 * 根据正则匹配是否全部不匹配，如果存在匹配项则返回false，否则返回true
	 * 
	 * @param tables
	 * @param regex
	 * @return
	 */
	public static boolean unMatcherAll(String sql, String regex) {
		Set<String> tables = parserSqlTables(sql);
		return unMatcherAll(tables, regex);
	}

	/**
	 * 根据正则匹配是否全部不匹配，如果存在匹配项则返回false，否则返回true
	 * 
	 * @param tables
	 * @param regex
	 * @return
	 */
	public static SqlParserResult unMatcherSqlTables(String sql, String regex) {
		Set<String> tables = parserSqlTables(sql);
		return unMatcherTables(tables, regex);
	}

	/**
	 * 根据正则匹配是否全部匹配，如果全部匹配则返回true，否则返回false
	 * 
	 * @param tables
	 * @param regex
	 * @return
	 */
	public static boolean matcherAll(Set<String> tables, String regex) {
		Pattern compile = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		if (regex != null) {
			for (String tableName : tables) {
				if (!compile.matcher(tableName).matches()) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 根据正则匹配是否全部不匹配，如果存在匹配项则返回false，否则返回true
	 * 
	 * @param tables
	 * @param regex
	 * @return
	 */
	public static SqlParserResult matcherTables(Set<String> tables, String regex) {
		Set<String> unMatcherTable = new HashSet<String>(tables.size());
		if (regex != null) {
			Pattern compile = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			for (String tableName : tables) {
				if (!compile.matcher(tableName).matches()) {
					unMatcherTable.add(tableName);
				}
			}
		}
		return new SqlParserResult(unMatcherTable.size() == 0, unMatcherTable);
	}

	/**
	 * 根据正则匹配是否全部不匹配，如果存在匹配项则返回false，否则返回true
	 * 
	 * @param tables
	 * @param regex
	 * @return
	 */
	public static boolean unMatcherAll(Set<String> tables, String regex) {
		if (regex != null) {
			Pattern compile = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			for (String tableName : tables) {
				if (compile.matcher(tableName).matches()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 根据正则匹配是否全部不匹配，如果存在匹配项则返回false，否则返回true
	 * 
	 * @param tables
	 * @param regex
	 * @return
	 */
	public static SqlParserResult unMatcherTables(Set<String> tables, String regex) {
		Set<String> unMatcherTable = new HashSet<String>(tables.size());
		if (regex != null) {
			Pattern compile = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			for (String tableName : tables) {
				if (compile.matcher(tableName).matches()) {
					unMatcherTable.add(tableName);
				}
			}
		}
		return new SqlParserResult(unMatcherTable.size() == 0, unMatcherTable);
	}
	
	public static class SqlParserResult {
		private boolean valid;

		private Set<String> matchTables;

		public SqlParserResult() {
			super();
		}

		public SqlParserResult(boolean valid, Set<String> matchTables) {
			super();
			this.valid = valid;
			this.matchTables = matchTables;
		}

		public boolean isValid() {
			return valid;
		}

		public void setValid(boolean valid) {
			this.valid = valid;
		}

		public Set<String> getMatchTables() {
			return matchTables;
		}

		public void setMatchTables(Set<String> matchTables) {
			this.matchTables = matchTables;
		}

	}
	
	public static void main(String[] args) {
		//		SQLSelectQueryBlock queryBlock = SQLParserUtils.createSelectQueryBlock(DbType.mysql);
		String sql = "SELECT \r\n" + 
				"    p.projectCode AS 项目编码, p.`projectName` AS 项目名称,\r\n" + 
				"    p.`customInfo` ->> '$.contractNo' AS 合同号,\r\n" + 
				"    p.`customInfo` ->> '$.officeName' AS 办事处,\r\n" + 
				"    p.`customInfo` ->> '$.salesManName' AS 销售代表,\r\n" + 
				"    GROUP_CONCAT(DISTINCT m.`memberName`) AS 项目经理, \r\n" + 
				"    tt.taskCount AS 要求规范项总数, IFNULL(t2.taskCount, 0) AS 项目规范项, \r\n" + 
				"    COUNT(DISTINCT IF(progress = 100, taskTypeId, NULL)) AS 已完成规范项,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='10',IF(progress = 100, taskTypeId, NULL),NULL)) AS `一、宣贯项目注意事项`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='20',IF(progress = 100, taskTypeId, NULL),NULL)) AS `二、核实《工作内容表》`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='30',IF(progress = 100, taskTypeId, NULL),NULL)) AS `三、建微信工作群（纯实施）`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='40',IF(progress = 100, taskTypeId, NULL),NULL)) AS `四、建微信工作群（办事处+实施）`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='50',IF(progress = 100, taskTypeId, NULL),NULL)) AS `五、制定《服务方案》`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='60',IF(progress = 100, taskTypeId, NULL),NULL)) AS `六、制定《工作计划书》`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='70',IF(progress = 100, taskTypeId, NULL),NULL)) AS `七、制定《启动会议 PPT》`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='80',IF(progress = 100, taskTypeId, NULL),NULL)) AS `八、召开项目启动会，输出《会议纪要》`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='90',IF(progress = 100, taskTypeId, NULL),NULL)) AS `九、获得客户《服务授权书》`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='100',IF(progress = 100, taskTypeId, NULL),NULL)) AS `十、获得客户《开工申请》反馈（按需）`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='110',IF(progress = 100, taskTypeId, NULL),NULL)) AS `十一、项目日报、周报、进度表`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='120',IF(progress = 100, taskTypeId, NULL),NULL)) AS `十二、行业资产、漏洞信息（项目经理单独录入）`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='130',IF(progress = 100, taskTypeId, NULL),NULL)) AS `十三、项目阶段性总结PPT`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='140',IF(progress = 100, taskTypeId, NULL),NULL)) AS `十四、召开项目阶段汇报会，输出《会议纪要》`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='150',IF(progress = 100, taskTypeId, NULL),NULL)) AS `十五、阶段《满意度调研表》`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='160',IF(progress = 100, taskTypeId, NULL),NULL)) AS `十六、《项目总结PPT》`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='170',IF(progress = 100, taskTypeId, NULL),NULL)) AS `十七、召开项目总结/验收会议，输出《会议纪要》`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='180',IF(progress = 100, taskTypeId, NULL),NULL)) AS `十八、《验收报告》`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='190',IF(progress = 100, taskTypeId, NULL),NULL)) AS `十九、总结验收阶段《满意度调研表》`,\r\n" + 
				"    COUNT(DISTINCT IF(taskTypeId='200',IF(progress = 100, taskTypeId, NULL),NULL)) AS `二十、项目进度百分比更新`\r\n" + 
				"FROM\r\n" + 
				"    `pm_project` p \r\n" + 
				"    LEFT JOIN \r\n" + 
				"        (SELECT DISTINCT \r\n" + 
				"            projectId, projectType, taskTypeCode, taskTypeId, progress \r\n" + 
				"        FROM\r\n" + 
				"            pm_project_task \r\n" + 
				"        WHERE taskTypeCode = 'afProjectTaskType' \r\n" + 
				"            AND `effectiveTo` IS NULL \r\n" + 
				"            AND progress = 100) t \r\n" + 
				"        ON p.`projectId` = t.`projectId` \r\n" + 
				"        AND p.`projectType` = t.`projectType` \r\n" + 
				"    LEFT JOIN \r\n" + 
				"        (SELECT projectId, projectType, COUNT(DISTINCT taskTypeId ) AS taskCount\r\n" + 
				"        FROM\r\n" + 
				"            pm_project_task \r\n" + 
				"        WHERE taskTypeCode = 'afProjectTaskType' \r\n" + 
				"            AND `effectiveTo` IS NULL\r\n" + 
				"        GROUP BY projectId, projectType, taskTypeCode) t2 \r\n" + 
				"        ON p.`projectId` = t2.`projectId` \r\n" + 
				"        AND p.`projectType` = t2.`projectType` \r\n" + 
				"    LEFT JOIN `pm_project_member` m \r\n" + 
				"        ON p.`projectId` = m.`projectId` \r\n" + 
				"        AND p.`projectType` = m.`projectType` \r\n" + 
				"        AND m.`memberRole` = \"30\" \r\n" + 
				"        AND m.`effectiveTo` IS NULL \r\n" + 
				"    LEFT JOIN \r\n" + 
				"        (SELECT \r\n" + 
				"            COUNT(1) AS taskCount \r\n" + 
				"        FROM\r\n" + 
				"            fnd_basic_data \r\n" + 
				"        WHERE dataTypeCode = 'afProjectTaskType' \r\n" + 
				"            AND effectiveTo IS NULL \r\n" + 
				"        GROUP BY dataTypeCode) tt \r\n" + 
				"        ON 1 = 1 \r\n" + 
				"WHERE p.`projectType` IN ('afss', 'afxx') \r\n" + 
				"GROUP BY p.`projectId`";
		
		System.out.println(parserSqlTables(sql));
		
		sql = "select * from pm_project,t_user";
		System.out.println(parserSqlTables(sql));
		
		sql = "SELECT \r\n" + 
				"    createBy, reportName, count(distinct processTime) days, sum(transitHour) as transitHour, sum(processHour) as processHour,  ROUND(SUM(dayDegree), 2) AS weekDegree\r\n" + 
				"FROM\r\n" + 
				"    (SELECT \r\n" + 
				"        createBy, reportName, processTime, MAX(transitHour) transitHour, SUM(processHour) processHour , MAX(transitHour) * 0.2 + SUM(processHour) AS dayDegree\r\n" + 
				"    FROM\r\n" + 
				"        (SELECT DISTINCT \r\n" + 
				"            dr.*, bpt.`basicDataName` AS projectTypeName, bt.`basicDataName` AS typeName, bc.`basicDataName` AS categoryName, bsc.`basicDataName` AS subCategoryName ,\r\n" + 
				"            dr.customInfo ->> \"$.createName\" as reportName\r\n" + 
				"        FROM\r\n" + 
				"            pm_daily_report AS `dr` \r\n" + 
				"            LEFT JOIN pm_project ph \r\n" + 
				"                ON dr.projectId = ph.`projectId` \r\n" + 
				"                AND dr.`projectType` = ph.`projectType` \r\n" + 
				"            LEFT JOIN pm_project_member pm \r\n" + 
				"                ON ph.projectId = pm.projectId \r\n" + 
				"                AND pm.effectiveFrom <= NOW() \r\n" + 
				"                AND (\r\n" + 
				"                    pm.effectiveTo > NOW() \r\n" + 
				"                    OR pm.effectiveTo IS NULL\r\n" + 
				"                ) \r\n" + 
				"                AND pm.`memberRole` IN (\"20\", \"30\", \"80\") \r\n" + 
				"            LEFT JOIN `fnd_basic_data` bpt \r\n" + 
				"                ON bpt.`dataTypeCode` = 'af_dailyReportProjectType' \r\n" + 
				"                AND dr.`projectType` = bpt.`basicDataId` \r\n" + 
				"            LEFT JOIN `fnd_basic_data` bt \r\n" + 
				"                ON bt.`dataTypeCode` = 'af_dailyReportType' \r\n" + 
				"                AND dr.`type` = bt.`basicDataId` \r\n" + 
				"            LEFT JOIN `fnd_basic_data` bc \r\n" + 
				"                ON bc.`dataTypeCode` = 'af_dailyReportCategory' \r\n" + 
				"                AND dr.`category` = bc.`basicDataId` \r\n" + 
				"            LEFT JOIN `fnd_basic_data` bsc \r\n" + 
				"                ON bsc.`dataTypeCode` = 'af_dailyReportSubCategory' \r\n" + 
				"                AND dr.`subCategory` = bsc.`basicDataId` \r\n" + 
				"        WHERE (\r\n" + 
				"                (\r\n" + 
				"                    FIND_IN_SET(\r\n" + 
				"                        dr.`projectType`, CONCAT(\"afxx,afss\", \",30,40\")\r\n" + 
				"                    )\r\n" + 
				"                ) \r\n" + 
				"                OR (\r\n" + 
				"                    pm.memberCode = 'w02332' \r\n" + 
				"                    OR dr.customInfo -> \"$.serviceManagerCode\" = 'w02332' \r\n" + 
				"                    OR dr.customInfo -> \"$.programManagerCode\" = 'w02332' \r\n" + 
				"                    OR dr.createBy = 'w02332'\r\n" + 
				"                )\r\n" + 
				"            ) \r\n" + 
				"            AND dr.`disabled` = FALSE \r\n" + 
				"            AND dr.`type` = 'report' \r\n" + 
				"            AND dr.`processTime` >= DATE_ADD(\r\n" + 
				"                CURDATE(), INTERVAL 0 - WEEKDAY(CURDATE())               - 7 \r\n" + 
				"                DAY\r\n" + 
				"            ) \r\n" + 
				"            AND dr.`processTime` <= DATE_ADD(\r\n" + 
				"                CURDATE(), INTERVAL 6 - WEEKDAY(CURDATE())              - 7 \r\n" + 
				"                DAY\r\n" + 
				"            )) t \r\n" + 
				"    GROUP BY createBy, processTime)  t\r\n" + 
				"GROUP BY createBy";
		System.out.println(parserSqlTables(sql));
		
	}
}