package com.dp.plat.security.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Name;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class SQLParser {

	private static final String regex = "\\b(?:from|join|insert\\s+into|insert|update|delete\\s+from|delete)\\s+`?(\\w+)`?\\s*";

	private static final Pattern parserSqlTablePattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNICODE_CASE);
	
	private static final TypeReference<Map<String, Object>> MapType = new TypeReference<Map<String, Object>>() {};
	private static final TypeReference<Map<String, Map<String, Object>>> MapMapType = new TypeReference<Map<String, Map<String, Object>>>() {};

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
//			System.out.println(sqlStatement.toString());
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
	
	public static Set<String> parseTables(String sql) {
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
		Set<String> tables = parseTables(sql);
		return matcherAll(tables, regex);
	}
	
	/**
	 * 根据正则匹配是否全部匹配，如果全部匹配则返回true，否则返回false
	 * 
	 * @param tables
	 * @param regex
	 * @return
	 */
	public static boolean matcherAll(String sql, String regex, DbType dbType) {
		Set<String> tables = parseTables(sql, dbType);
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
		Set<String> tables = parseTables(sql);
		return matcherTables(tables, regex);
	}
	
	/**
	 * 根据正则匹配是否全部匹配，如果全部匹配则返回true，否则返回false
	 * 
	 * @param tables
	 * @param regex
	 * @return
	 */
	public static SqlParserResult matcherSqlTables(String sql, String regex, DbType dbType) {
		Set<String> tables = parseTables(sql, dbType);
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
		Set<String> tables = parseTables(sql);
		return unMatcherAll(tables, regex);
	}
	
	/**
	 * 根据正则匹配是否全部不匹配，如果存在匹配项则返回false，否则返回true
	 * 
	 * @param tables
	 * @param regex
	 * @return
	 */
	public static boolean unMatcherAll(String sql, String regex, DbType dbType) {
		Set<String> tables = parseTables(sql, dbType);
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
		Set<String> tables = parseTables(sql);
		return unMatcherTables(tables, regex);
	}
	
	/**
	 * 根据正则匹配是否全部不匹配，如果存在匹配项则返回false，否则返回true
	 * 
	 * @param tables
	 * @param regex
	 * @return
	 */
	public static SqlParserResult unMatcherSqlTables(String sql, String regex, DbType dbType) {
		Set<String> tables = parseTables(sql, dbType);
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
	
	/**
	 * 获取当前链接数据库的数据库类型
	 * 
	 * @return
	 */
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
					try {
						connection.close();
					} catch (SQLException e) {
					}
				}
			}
		}
		return DbType.of(dbType != null ? dbType.toLowerCase() : dbType);
	}
	
	private final static String DEFALUE_SQL_PARAMS_PARTS = "{\"${|}\":{\"before\":\"${\",\"after\":\"}\",\"quote\":false},\"#{|}\":{\"before\":\"#{\",\"after\":\"}\",\"quote\":\"'\"},\"$|$\":{\"before\":\"$\",\"after\":\"$\",\"quote\":false},\"#|#\":{\"before\":\"#\",\"after\":\"#\",\"quote\":\"'\"}}";

	/**
	 * 解析SQL中存在的变量
	 * 
	 * @param sql
	 * @param splitPartMap default DEFALUE_SQL_PARAMS_PARTS
	 * <pre>
	   {
		  "${|}": {
		    "before": "${",
		    "after": "}",
		    "quote": "'"
		  },
		  "#{|}": {
		    "before": "#{",
		    "after": "}",
		    "quote": false
		  },
		  "$|$": {
		    "before": "$",
		    "after": "$",
		    "quote": "'"
		  },
		  "#|#": {
		    "before": "#",
		    "after": "#",
		    "quote": false
		  }
		}
	 * </pre>
	 * 
	 * @return
	 */
	public static Map<String, Map<String, Object>> parseSqlParams(String sql) {
		Map<String, Map<String, Object>> splitPartMap = JSON.parseObject(DEFALUE_SQL_PARAMS_PARTS, MapMapType);
		return parseSqlParams(sql, splitPartMap);
	}
	
	/**
	 * 解析SQL中存在的变量
	 * @param sql
	 * @param splitPartMap
	 * @return
	 */
	public static Map<String, Map<String, Object>> parseSqlParams(String sql, Map<String, Map<String, Object>> splitPartMap) {
		Map<String, Map<String, Object>> params = new HashMap<String, Map<String, Object>>();
		for (Map<String, Object> splitPart : splitPartMap.values() ) {
			String beforeSplit = quoteSplit((String) splitPart.get("before"));
			String afterSplit = quoteSplit((String) splitPart.get("after"));
			String regex = beforeSplit  + "([^" + beforeSplit + afterSplit + "\\ ,]*)" + afterSplit;
//			String regex = beforeSplit  + "(?>" + beforeSplit + "(?<n>)|" + afterSplit + "(?<-n>)|(?!" + beforeSplit + "|" + afterSplit + ").)*(?(n)(?!))" + afterSplit;
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(sql);
			while (matcher.find()) {
				String param = matcher.group();
				if (params.containsKey(param)) {
					continue;
				}
				params.put(param, splitPart);
			}
		}
		return params;
	}
	
	/**
	 * 处理正则表达式中的特殊字符
	 * @param split
	 * @return quoteSplit
	 */
	public static String quoteSplit(String split) {
		if (!split.matches(".*[\\$|\\(|\\)|\\*|\\+|\\.|\\[|\\]|\\?|\\\\|\\/|\\^|\\{|\\}].*")) {
			return split;
		}
		char[] signs =  new char[] {'$','(',')','*','+','.','[',']','?','\\','/','^','{','}'};
		Arrays.sort(signs);
		StringBuilder sb = new StringBuilder();
        for (int i=0; i<split.length(); i++) {
            char c = split.charAt(i);
            if (Arrays.binarySearch(signs, c) >= 0) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
	}
	
	/**
	 * 解析field字段的值，例如，user.username,获取user对象的username属性值
	 * 
	 * @param field
	 * @param newParams
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public static Object parseObjectValue(Map<String, Object> param, Map<String, Object> values) {
		if (param == null || param.isEmpty()) {
			return "";
		}
		String field = (String) param.get("field");
		if (StringUtils.isBlank(field)) {
			return "";
		}
		String beforeSplit = quoteSplit((String) param.get("before"));
		String afterSplit = quoteSplit((String) param.get("after"));
		Object quote = param.get("quote");
		String key = field.replaceAll(beforeSplit + "|" + afterSplit, "");
		Object value = "";
		String[] relations = null;
		if (key.contains(".") && !values.containsKey(key)) {
			relations = key.split("\\.");
			StringBuilder prevRelation = new StringBuilder();
			for (int i = 0; i < relations.length; i++) {
				String relation = relations[i];
				value = values.getOrDefault(prevRelation + relation, "");
				try {
					if (value instanceof String || value instanceof Integer) {
						break;
					}

					Map<String, Object> parseMap = new HashMap<>();
					if (value instanceof Map) {
						parseMap = (Map<String, Object>) value;
					} else {
						String objStr = toJSONString(value);
						parseMap = JSON.parseObject(objStr, MapType);
					}
					for (Entry<String, Object> entry : parseMap.entrySet()) {
						Object tempValue = entry.getValue();
						if (tempValue != null) {
//							if (tempValue instanceof Date) {
//								tempValue = DateConverter.covert((Date) tempValue);
//							}
							values.put(prevRelation + relation + "." + entry.getKey(), tempValue);
						}
					}
					value = parseMap.getOrDefault(relation, "");
					prevRelation.append(relation).append(".");
				} catch (Exception e) {
				    handlerException(e);
				}
			}
		} else {
			value = values.getOrDefault(key, "");
		}
		if (Boolean.FALSE.equals(quote) || StringUtils.isBlank((String) quote)) {
			return value;
		} else {
			StringBuilder builder = new StringBuilder(String.valueOf(value));
			return builder.insert(0, quote).append(quote).toString();
		}
	}
	
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
				try {
					value = Matcher.quoteReplacement(value.toString());
					sql = sql.replaceAll(valueRegx, value.toString());
				} catch (Exception e2) {
					handlerException(e, e2);
				}
			}
		}
		return sql;
	}
	
	/**
	 * 转化为Json字符串
	 * @param obj
	 * @return
	 */
	public static String toJSONString(Object obj) {
//		try {
//			return Jackson2ObjectMapperBuilder.json().build()
//					.setSerializationInclusion(JsonInclude.Include.NON_NULL)
//					.writeValueAsString(obj);
//		} catch (JsonProcessingException e) {
//		}
		return JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat);
	}
	
	public static void handlerException(Throwable... e) {
	    
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
		
		System.out.println(parseTables(sql));
		
		sql = "select * from pm_project,t_user";
		System.out.println(parseTables(sql));
		
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
		System.out.println(parseTables(sql));
		
		sql = "SELECT \r\n" + 
				"    IFNULL(ph.`projectType`, dr.`projectType`) projectType, \r\n" + 
				"    IFNULL(ph.`projectId`, dr.`projectId`) `projectId`, \r\n" + 
				"    IFNULL(pgr.`smsProjectCode`, dr.`projectCode`) `projectCode`,\r\n" + 
				"    IFNULL(ph.`projectName`, dr.`projectName`) `projectName`,\r\n" + 
				"    IFNULL(group_concat(distinct pc.contractNo), dr.contractNos) as contractNos,\r\n" + 
				"    IFNULL(fd.departmentName, dr.officeName) as officeName, \r\n" + 
				"    bd.`basicDataName` as projectStateName, \r\n" + 
				"    bd2.`basicDataName` as projectTypeName, c.compAbbr, c.compName,\r\n" + 
				"    dr.`type`, dr.reportNames,\r\n" + 
				"    dr.transitDays, dr.processDays,\r\n" + 
				"    dr.transitDaySum, dr.processDaySum\r\n" + 
				"FROM\r\n" + 
				"    (\r\n" + 
				"	select dr.`projectType`, dr.`projectId`, dr.`projectCode`, \r\n" + 
				"	    dr.`projectName`, dr.`type`, dr.`processTime`, dr.contractNos,\r\n" + 
				"	    dr.officeName,\r\n" + 
				"	    group_concat(distinct createName order by dr.createBy) as reportNames,\r\n" + 
				"	    group_concat(dr.transitDays order by dr.createBy) as transitDays,\r\n" + 
				"	    group_concat(dr.processDays order by dr.createBy) as processDays,\r\n" + 
				"	    SUM(dr.transitDays) transitDaySum,\r\n" + 
				"	    SUM(dr.processDays) processDaySum\r\n" + 
				"	from(\r\n" + 
				"	select \r\n" + 
				"	    dr.`projectType`, dr.`projectId`, \r\n" + 
				"	    substring_index(dr.projectCode, \"-\", 1) as projectCode,\r\n" + 
				"	    dr.customInfo ->> \"$.officeName\" as officeName,\r\n" + 
				"	    dr.`projectName`, dr.`type`, dr.`processTime`, dr.createBy,\r\n" + 
				"	    group_concat(distinct dr.`contractNo`) as contractNos,\r\n" + 
				"	    group_concat(distinct dr.customInfo ->> \"$.createName\") as createName,\r\n" + 
				"	    SUM(IF(dr.`transitHour` / 8 > 0.5, 1, IF(dr.`transitHour` > 0, 0.5, 0))) transitDays,\r\n" + 
				"	    SUM(IF(dr.`processHour` / 8 > 0.5, 1, IF(dr.`processHour` > 0, 0.5, 0))) processDays\r\n" + 
				"	from\r\n" + 
				"	    `pm_daily_report` dr \r\n" + 
				"	    left join pm_project AS ph \r\n" + 
				"		on ph.`projectId` = dr.projectId\r\n" + 
				"		and dr.`projectType` = ph.`projectType`\r\n" + 
				"	where dr.projectType in (\"afss\", \"afxx\", \"40\") \r\n" + 
				"	and dr.type = \"report\"\r\n" + 
				"	and dr.disabled = false\r\n" + 
				"	and ph.customInfo ->> \"$.transferToProject.projectId\" is null\r\n" + 
				"	AND dr.processTime >= DATE_ADD(CURDATE(), INTERVAL 0 - WEEKDAY(CURDATE()) - 7 DAY) \r\n" + 
				"	AND dr.processTime <= DATE_ADD(CURDATE(), INTERVAL 6 - WEEKDAY(CURDATE()) - 7 DAY)\r\n" + 
				"	group by projectId, projectType, projectCode, projectName, createBy \r\n" + 
				"	) dr\r\n" + 
				"	group by projectId, projectType, projectCode, projectName    \r\n" + 
				"    ) dr\r\n" + 
				"    left join pm_project AS ph \r\n" + 
				"	on ph.`projectId` = dr.projectId\r\n" + 
				"	and ph.disabled = false\r\n" + 
				"    LEFT JOIN `pm_project_group_relationship` pgr \r\n" + 
				"        ON pgr.projectCode = ph.projectCode \r\n" + 
				"    LEFT JOIN `pm_project_contract` pc \r\n" + 
				"        ON pc.projectGroupCode = pgr.projectGroupCode \r\n" + 
				"    LEFT JOIN fnd_basic_data bd \r\n" + 
				"        ON bd.`dataTypeCode` = concat(\r\n" + 
				"            ph.`projectType`, \"_projectState\"\r\n" + 
				"        ) \r\n" + 
				"        AND bd.`basicDataId` = ph.`projectState` \r\n" + 
				"    left join `fnd_department` fd\r\n" + 
				"	on fd.departmentNum = ph.`column001`\r\n" + 
				"    LEFT JOIN fnd_basic_data bd2 \r\n" + 
				"        ON bd2.`dataTypeCode` = \"projectTypes\" \r\n" + 
				"        AND bd2.`basicDataId` = ph.projectType \r\n" + 
				"    LEFT JOIN `t_company` c \r\n" + 
				"        ON c.id = ph.`compId` \r\n" + 
				"GROUP BY dr.projectId, dr.projectType, dr.projectCode, dr.projectName\r\n" + 
				"order by dr.projectId desc";
		System.out.println(parseTables(sql, DbType.mysql));
		
		sql = "/*将projectTaskLog关联到新的projectTask的上*/\r\n" + 
				"update\r\n" + 
				"    `pm_common_related_data` pcr \r\n" + 
				"    inner join pm_project_task opt \r\n" + 
				"        on pcr.`objType` = \"project\" \r\n" + 
				"        and opt.taskId = cast(pcr.`customInfo` ->> \"$.task.taskId\" as signed)\r\n" + 
				"    inner join pm_project_task npt \r\n" + 
				"        on pcr.`objId` = npt.projectId \r\n" + 
				"        and pcr.`objType` = \"project\" \r\n" + 
				"        and (opt.customInfo = npt.`customInfo` \r\n" + 
				"            /*or opt.taskId = cast(npt.`customInfo` ->> \"$.oldPrimaryValues\" as signed)*/\r\n" + 
				"            or JSON_CONTAINS(npt.`customInfo` ->> \"$.oldPrimaryValues\", cast(opt.taskId as char))\r\n" + 
				"            or JSON_SEARCH(npt.`customInfo` ->> \"$.oldPrimaryValues\", 'one', opt.taskId) is not null\r\n" + 
				"        )\r\n" + 
				"        and opt.createTime = npt.createTime\r\n" + 
				"set pcr.`objId` = npt.`taskId`, pcr.`objType` = \"projectTask\",\r\n" + 
				"    pcr.customInfo = JSON_MERGE_PATCH(pcr.customInfo, JSON_OBject(\"task\", JSON_OBject(\"taskId\", npt.`taskId`, \"projectId\", npt.projectId, \"projectType\", npt.projectType)))\r\n" + 
				"where pcr.type = 'projectTaskLog'\r\n" + 
				"and pcr.objId = '$newProjectId$'\r\n" + 
				"and opt.createTime = npt.createTime;\r\n" + 
				"\r\n" + 
				"/*将projectTaskDelivery关联到新的projectTask的上*/\r\n" + 
				"update\r\n" + 
				"    `pm_basic_deliver_detail` pcr \r\n" + 
				"    inner join pm_project_task opt \r\n" + 
				"        on opt.taskId = pcr.taskId\r\n" + 
				"    inner join pm_project_task npt \r\n" + 
				"        on pcr.`projectId` = npt.projectId \r\n" + 
				"        and pcr.projectType = npt.projectType\r\n" + 
				"        and (opt.customInfo = npt.`customInfo` \r\n" + 
				"            /*or opt.taskId = cast(npt.`customInfo` ->> \"$.oldPrimaryValues\" as signed)*/\r\n" + 
				"            or JSON_CONTAINS(npt.`customInfo` ->> \"$.oldPrimaryValues\", cast(opt.taskId as char))\r\n" + 
				"            or JSON_SEARCH(npt.`customInfo` ->> \"$.oldPrimaryValues\", 'one', opt.taskId) is not null\r\n" + 
				"        )\r\n" + 
				"set pcr.taskId = npt.`taskId`\r\n" + 
				"where pcr.taskId != npt.taskId\r\n" + 
				"and pcr.projectId = '$newProjectId$'\r\n" + 
				"and opt.createTime = npt.createTime;\r\n" + 
				"\r\n" + 
				"/*将projectworkflow关联到新的projectTask的上*/\r\n" + 
				"UPDATE\r\n" + 
				"    `pm_workflow` pw\r\n" + 
				"    inner join pm_project_task opt \r\n" + 
				"        on pw.`objType` = \"project\" \r\n" + 
				"        and pw.`objId` != opt.`projectId`\r\n" + 
				"        and opt.taskId = pw.`dataId`\r\n" + 
				"    inner join pm_project_task npt \r\n" + 
				"        on pw.`objId` = npt.projectId \r\n" + 
				"        and pw.`objType` = \"project\" \r\n" + 
				"        and (opt.customInfo = npt.`customInfo` \r\n" + 
				"            /*or opt.taskId = cast(npt.`customInfo` ->> \"$.oldPrimaryValues\" as signed)*/\r\n" + 
				"            or JSON_CONTAINS(npt.`customInfo` ->> \"$.oldPrimaryValues\", cast(opt.taskId as char))\r\n" + 
				"            or JSON_SEARCH(npt.`customInfo` ->> \"$.oldPrimaryValues\", 'one', opt.taskId) is not null\r\n" + 
				"        )\r\n" + 
				"    INNER JOIN pm_workflow opw\r\n" + 
				"        ON opw.`objType` = pw.objType\r\n" + 
				"        AND opw.`objId` != pw.objId\r\n" + 
				"        AND opw.dataId = pw.`dataId`\r\n" + 
				"        AND opw.procInstId = pw.procInstId\r\n" + 
				"    LEFT JOIN act_ru_task art\r\n" + 
				"        ON art.PROC_INST_ID_ = pw.procInstId\r\n" + 
				"SET pw.`dataId` = npt.`taskId`,\r\n" + 
				"    art.FORM_KEY_ = REPLACE(art.FORM_KEY_, opw.id, pw.id),\r\n" + 
				"    opw.status = IF(opw.status = 'PENDING', 'APPROVAL_CANCEL', opw.status)\r\n" + 
				"where pw.dataType = 'projectTask'\r\n" + 
				"and pw.objId = '$newProjectId$'\r\n" + 
				"and opt.createTime = npt.createTime;\r\n" + 
				"\r\n" + 
				"/*将projectworkflow关联到新的industryAsset的上*/\r\n" + 
				"UPDATE \r\n" + 
				"    `pm_workflow` pw\r\n" + 
				"    INNER JOIN af_industry_asset ia\r\n" + 
				"        ON pw.`objType` = 'project'\r\n" + 
				"        AND ia.id = pw.`dataId`\r\n" + 
				"    INNER JOIN pm_workflow opw\r\n" + 
				"        ON opw.`objType` = pw.objType\r\n" + 
				"        AND opw.`objId` != pw.objId\r\n" + 
				"        AND opw.dataId = pw.`dataId`\r\n" + 
				"        AND opw.procInstId = pw.procInstId\r\n" + 
				"    LEFT JOIN act_ru_task art\r\n" + 
				"        ON art.PROC_INST_ID_ = pw.procInstId\r\n" + 
				"SET art.FORM_KEY_ = REPLACE(art.FORM_KEY_, opw.id, pw.id),\r\n" + 
				"    opw.status = IF(opw.status = 'PENDING', 'APPROVAL_CANCEL', opw.status)\r\n" + 
				"WHERE pw.dataType = 'industryAsset'\r\n" + 
				"    AND pw.objId = '$newProjectId$';\r\n" + 
				"\r\n" + 
				"/*将projectworkflow关联到新的industryLeak的上*/\r\n" + 
				"UPDATE \r\n" + 
				"    `pm_workflow` pw\r\n" + 
				"    INNER JOIN af_industry_leak il\r\n" + 
				"        ON pw.`objType` = 'project'\r\n" + 
				"        AND il.id = pw.`dataId`\r\n" + 
				"    INNER JOIN pm_workflow opw\r\n" + 
				"        ON opw.`objType` = pw.objType\r\n" + 
				"        AND opw.`objId` != pw.objId\r\n" + 
				"        AND opw.dataId = pw.`dataId`\r\n" + 
				"        AND opw.procInstId = pw.procInstId\r\n" + 
				"    LEFT JOIN act_ru_task art\r\n" + 
				"        ON art.PROC_INST_ID_ = pw.procInstId\r\n" + 
				"SET art.FORM_KEY_ = REPLACE(art.FORM_KEY_, opw.id, pw.id),\r\n" + 
				"    opw.status = IF(opw.status = 'PENDING', 'APPROVAL_CANCEL', opw.status)\r\n" + 
				"WHERE pw.dataType = 'industryLeak'\r\n" + 
				"    AND pw.objId = '$newProjectId$'"
				+ "  AND pw.objId = '#newProjectId#'"
				+ "	 AND pw.objId = '${newProjectId}'"
				+ "  AND pw.objId = '#{newProjectId}'"
				+ "  AND pw.objId = ${newProjectId}"
				+ "  AND pw.objId = #{newProjectId}"
				+ "  AND pw.objId = '$project.projectId$'"
				+ "  AND pw.objId = '#project.projectId#'"
				+ "	 AND pw.objId = '${project.projectId}'"
				+ "  AND pw.objId = '#{project.projectId}'"
				+ "  AND pw.objId = ${project.projectId}"
				+ "  AND pw.objId = #{project.projectId}"
				+ ";";
		System.out.println(parseTables(sql, DbType.mysql));
		
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("newProjectId", 123456);
		values.put("project", Collections.singletonMap("projectId", 987654321));
		System.out.println(fillSqlParams(sql, values));

		sql = "/*将projectTaskLog关联到新的projectTask的上*/ update `pm_common_related_data` pcr inner join pm_project_task opt on pcr.`objType` = \"project\" and opt.taskId = cast(pcr.`customInfo` ->> \"$.task.taskId\" as signed) inner join pm_project_task npt on pcr.`objId` = npt.projectId and pcr.`objType` = \"project\" and (opt.customInfo = npt.`customInfo` /*or opt.taskId = cast(npt.`customInfo` ->> \"$.oldPrimaryValues\" as signed)*/ or JSON_CONTAINS(npt.`customInfo` ->> \"$.oldPrimaryValues\", cast(opt.taskId as char)) or JSON_SEARCH(npt.`customInfo` ->> \"$.oldPrimaryValues\", 'one', opt.taskId) is not null ) and opt.createTime = npt.createTime set pcr.`objId` = npt.`taskId`, pcr.`objType` = \"projectTask\", pcr.customInfo = JSON_MERGE_PATCH(pcr.customInfo, JSON_OBject(\"task\", JSON_OBject(\"taskId\", npt.`taskId`, \"projectId\", npt.projectId, \"projectType\", npt.projectType))) where pcr.type = 'projectTaskLog' and pcr.objId = '$newProjectId$' and opt.createTime = npt.createTime; /*将projectTaskDelivery关联到新的projectTask的上*/ update `pm_basic_deliver_detail` pcr inner join pm_project_task opt on opt.taskId = pcr.taskId inner join pm_project_task npt on pcr.`projectId` = npt.projectId and pcr.projectType = npt.projectType and (opt.customInfo = npt.`customInfo` /*or opt.taskId = cast(npt.`customInfo` ->> \"$.oldPrimaryValues\" as signed)*/ or JSON_CONTAINS(npt.`customInfo` ->> \"$.oldPrimaryValues\", cast(opt.taskId as char)) or JSON_SEARCH(npt.`customInfo` ->> \"$.oldPrimaryValues\", 'one', opt.taskId) is not null ) set pcr.taskId = npt.`taskId` where pcr.taskId != npt.taskId and pcr.projectId = '$newProjectId$' and opt.createTime = npt.createTime; /*将projectworkflow关联到新的projectTask的上*/ update `pm_workflow` pw inner join pm_project_task opt on pw.`objType` = \"project\" and pw.`objId` != opt.`projectId` and opt.taskId = pw.`dataId` inner join pm_project_task npt on pw.`objId` = npt.projectId and pw.`objType` = \"project\" and (opt.customInfo = npt.`customInfo` /*or opt.taskId = cast(npt.`customInfo` ->> \"$.oldPrimaryValues\" as signed)*/ or JSON_CONTAINS(npt.`customInfo` ->> \"$.oldPrimaryValues\", cast(opt.taskId as char)) or JSON_SEARCH(npt.`customInfo` ->> \"$.oldPrimaryValues\", 'one', opt.taskId) is not null ) INNER JOIN pm_workflow opw ON opw.`objType` = pw.objType AND opw.`objId` != pw.objId AND opw.dataId = pw.`dataId` AND opw.procInstId = pw.procInstId LEFT JOIN act_ru_task art ON art.PROC_INST_ID_ = pw.procInstId set pw.`dataId` = npt.`taskId`, art.FORM_KEY_ = REPLACE(art.FORM_KEY_, opw.id, pw.id), opw.status = IF(opw.status = 'PENDING', 'APPROVAL_CANCEL', opw.status) where pw.dataType = 'projectTask' and pw.objId = '$newProjectId$' and opt.createTime = npt.createTime;";
		System.out.println(parseTables(sql, DbType.mysql));
		
		System.out.println(fillSqlParams(sql, values));
		
		sql = "/*2222将projectTaskLog关联到新的projectTask的上*/"
				+ "SELECT IFNULL(ph.`projectType`, dr.`projectType`) AS projectType\r\n" + 
				"    , IFNULL(ph.`projectId`, dr.`projectId`) AS `projectId`\r\n" + 
				"    , IFNULL(pgr.`smsProjectCode`, dr.`projectCode`) AS `projectCode`\r\n" + 
				"    , IFNULL(ph.`projectName`, dr.`projectName`) AS `projectName`\r\n" + 
				"    , IFNULL(group_concat(DISTINCT pc.contractNo), dr.contractNos) AS contractNos\r\n" + 
				"    , IFNULL(fd.departmentName, dr.officeName) AS officeName, bd.`basicDataName` AS projectStateName\r\n" + 
				"    , bd2.`basicDataName` AS projectTypeName, ph.`customInfo` ->> '$.salesManName' AS salesManName, ph.`customInfo` ->> '$.serviceManagerCodeforjson' AS serviceManagerName\r\n" + 
				"    , ph.`customInfo` ->> '$.programManagerCodeforjson' AS programManagerName, dr.`type`, dr.reportNames\r\n" + 
				"    , dr.transitDays, dr.processDays, dr.transitDays + dr.processDays AS allDays, dr.transitDaySum\r\n" + 
				"    , dr.processDaySum, dr.transitDaySum + dr.processDaySum AS allDaySum\r\n" + 
				"    , concat(minProcessTime, ' ~ ', maxProcessTime) AS weekDate\r\n" + 
				"FROM (\r\n" + 
				"    SELECT dr.`projectType`, dr.`projectId`, dr.`projectCode`, dr.`projectName`, dr.`type`\r\n" + 
				"        , dr.`processTime`, dr.contractNos, dr.officeName, Min(minProcessTime) AS minProcessTime\r\n" + 
				"        , max(maxProcessTime) AS maxProcessTime, group_concat(DISTINCT createName ORDER BY dr.createBy) AS reportNames\r\n" + 
				"        , group_concat(dr.transitDays ORDER BY dr.createBy) AS transitDays, group_concat(dr.processDays ORDER BY dr.createBy) AS processDays\r\n" + 
				"        , SUM(dr.transitDays) AS transitDaySum, SUM(dr.processDays) AS processDaySum\r\n" + 
				"    FROM (\r\n" + 
				"        SELECT dr.`projectType`, dr.`projectId`\r\n" + 
				"            , substring_index(dr.projectCode, '-', 1) AS projectCode\r\n" + 
				"            , dr.customInfo ->> '$.officeName' AS officeName, dr.`projectName`, dr.`type`\r\n" + 
				"            , dr.`processTime`, dr.createBy\r\n" + 
				"            , Min(date_format(dr.processTime, '%Y-%m-%d')) AS minProcessTime\r\n" + 
				"            , max(date_format(dr.processTime, '%Y-%m-%d')) AS maxProcessTime\r\n" + 
				"            , group_concat(DISTINCT dr.`contractNo`) AS contractNos\r\n" + 
				"            , group_concat(DISTINCT concat(dr.createBy, '-', dr.customInfo ->> '$.createName')) AS createName\r\n" + 
				"            , SUM(IF(dr.`transitHour` / 8 > 0.5, 1, IF(dr.`transitHour` > 0, 0.5, 0))) AS transitDays\r\n" + 
				"            , SUM(IF(dr.`processHour` / 8 > 0.5, 1, IF(dr.`processHour` > 0, 0.5, 0))) AS processDays\r\n" + 
				"        FROM `pm_daily_report` dr\r\n" + 
				"            LEFT JOIN pm_project ph\r\n" + 
				"            ON ph.`projectId` = dr.projectId\r\n" + 
				"                AND dr.`projectType` = ph.`projectType`\r\n" + 
				"            left join pm_project_member pm \r\n" + 
				"                on ph.projectId = pm.projectId \r\n" + 
				"                and pm.effectiveFrom <= now() \r\n" + 
				"                and (\r\n" + 
				"                    pm.effectiveTo > now() \r\n" + 
				"                    or pm.effectiveTo is null\r\n" + 
				"                ) \r\n" + 
				"                and pm.`memberRole` in (\"20\", \"30\", \"80\") \r\n" + 
				"                and pm.memberCode = '${user.userName}'\r\n" + 
				"            left join t_user_info ui \r\n" + 
				"                on (\r\n" + 
				"                    dr.`officeCode` = ui.`custom3` \r\n" + 
				"                    or find_in_set(dr.`officeCode`, ui.`custom5`)\r\n" + 
				"                ) \r\n" + 
				"                and find_in_set(dr.projectType, concat(IFNULL(ui.custom4, \"\"), \",30,40\")) \r\n" + 
				"                and ui.compID = '${user.compId}'\r\n" + 
				"                AND ui.id = '${user.userInfoId}'\r\n" + 
				"        WHERE (\r\n" + 
				"                (FIND_IN_SET(dr.`projectType`, CONCAT('${user.userInfo.custom4}', \",30,40\")) AND \r\n" + 
				"                    (FIND_IN_SET(dr.`officeCode`, '${user.userInfo.custom5}') OR \r\n" + 
				"                    FIND_IN_SET('projectSubAdmin', '${user.roles}') \r\n" + 
				"                    )AND 1 = 1\r\n" + 
				"                )\r\n" + 
				"                OR (\r\n" + 
				"                    pm.memberCode = '${user.userName}' or \r\n" + 
				"                    dr.customInfo -> \"$.serviceManagerCode\" = '${user.userName}' or \r\n" + 
				"                    dr.customInfo -> \"$.programManagerCode\" = '${user.userName}' or \r\n" + 
				"                    dr.createBy = '${user.userName}' OR\r\n" + 
				"                    ui.id = '${user.userInfoId}' OR\r\n" + 
				"                    find_in_set('admin', '${user.roles}') OR\r\n" + 
				"                    find_in_set('projectAdmin', '${user.roles}')\r\n" + 
				"                )\r\n" + 
				"            ) AND dr.projectType IN ('afss', 'afxx', '40')\r\n" + 
				"            AND dr.type = 'report'\r\n" + 
				"            AND dr.disabled = false\r\n" + 
				"            AND ph.customInfo ->> '$.transferToProject.projectId' IS NULL\r\n" + 
				"            AND dr.processTime >= DATE_ADD(CURDATE(), INTERVAL 0 - WEEKDAY(CURDATE()) DAY)\r\n" + 
				"            AND dr.processTime <= DATE_ADD(CURDATE(), INTERVAL 6 - WEEKDAY(CURDATE()) DAY)\r\n" + 
				"        GROUP BY projectId, projectType, projectCode, projectName, createBy\r\n" + 
				"    ) dr\r\n" + 
				"    GROUP BY projectId, projectType, projectCode, projectName\r\n" + 
				") dr\r\n" + 
				"    LEFT JOIN pm_project ph\r\n" + 
				"    ON ph.`projectId` = dr.projectId\r\n" + 
				"        AND ph.disabled = false\r\n" + 
				"    LEFT JOIN `pm_project_group_relationship` pgr ON pgr.projectCode = ph.projectCode\r\n" + 
				"    LEFT JOIN `pm_project_contract` pc ON pc.projectGroupCode = pgr.projectGroupCode\r\n" + 
				"    LEFT JOIN fnd_basic_data bd\r\n" + 
				"    ON bd.`dataTypeCode` = concat(ph.`projectType`, '_projectState')\r\n" + 
				"        AND bd.`basicDataId` = ph.`projectState`\r\n" + 
				"    LEFT JOIN `fnd_department` fd ON fd.departmentNum = ph.`column001`\r\n" + 
				"    LEFT JOIN fnd_basic_data bd2\r\n" + 
				"    ON bd2.`dataTypeCode` = 'projectTypes'\r\n" + 
				"        AND bd2.`basicDataId` = ph.projectType\r\n" + 
				"GROUP BY dr.projectId, dr.projectType, dr.projectCode, dr.projectName\r\n" + 
				"ORDER BY dr.projectId DESC;";
//		values.put("user", UserContext.getCurrentPrincipal());
		sql = SQLUtils.formatMySql(sql);
		System.out.println(sql);
		System.out.println(parseTables(sql, DbType.mysql));
		System.out.println(fillSqlParams(sql, values));
	}
}