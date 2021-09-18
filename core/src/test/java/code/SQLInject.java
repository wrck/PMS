package code;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLInject {

	public static void main(String[] args) {
		String sql = "select * from ( select 1 union all select @@global.version_compile_os from mysql.user) t;INSERT /*+append parallel(view_warranty view_warranty_source)*/ INTO view_warranty;/*!40014 SET @OLD_SQL_LOG_BIN=@@SQL_LOG_BIN, SESSION SQL_LOG_BIN=OFF */;/*!40014 SET @OLD_INNODB_FLUSH_LOG_AT_TRX_COMMIT=@@INNODB_FLUSH_LOG_AT_TRX_COMMIT, global INNODB_FLUSH_LOG_AT_TRX_COMMIT=0 */; select * from t_user where createBy = '1'; select table_name from information_schema.tables;select @@version; select version(); select version ();select user(); select database() ;create    database if not exists tssss; drop database if exists tssss; show databases; use tssss;use `tssss`;create table if not exists t select 1;alter table t add index `1`(`1`);#create table if not exists t select 1;-- alter table t add index `1`(`1`);select * from (select 1  ) t;select * from (select 1  ) t;";
		//String regex = ";|--|#|@@|@@version|information_schema|(version|user|database)\\W*\\(\\)|(create|drop|show|delete|update|insert|alter)\\W+(database|table)s?|use\\W+\\w+";
		String regex = ";|--|#|0x|@@|@@version|@@datadir|@@plugin_dir|mysql|current_user|information_schema|(load_|out|dump)file|(version|user|database)\\W*\\(\\)|(create|drop|show|delete|update|insert|alter)\\W+(global)?\\W*(database|table|column|variable|function|procedure|processlist)s?|use\\W+\\w+|set\\W+global\\W+\\w+";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.UNICODE_CASE);
		Matcher matcher = pattern.matcher(sql);
		System.out.println(matcher.find());
		matcher.reset();
		int i = 1;
		while(matcher.find()) {
			System.out.println(i++ + ":" + matcher.group());
		}
		sql = "select \r\n" + 
				"    * \r\n" + 
				"from\r\n" + 
				"    \r\n" + 
				"    (select \r\n" + 
				"        1 \r\n" + 
				"    union\r\n" + 
				"    all \r\n" + 
				"    select \r\n" + 
				"        @@global.version_compile_os \r\n" + 
				"    from\r\n" + 
				"        mysql.user) t ;\r\n" + 
				"\r\n" + 
				" \r\n" + 
				"select \r\n" + 
				"    * \r\n" + 
				"from\r\n" + 
				"    t_user \r\n" + 
				"where createBy = '1' ;\r\n" + 
				"\r\n" + 
				" \r\n" + 
				"select \r\n" + 
				"    table_name \r\n" + 
				"from\r\n" + 
				"    information_schema.tables ;\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"select \r\n" + 
				"    @@version ;\r\n" + 
				"\r\n" + 
				" \r\n" + 
				"select \r\n" + 
				"    version() ;\r\n" + 
				"\r\n" + 
				" \r\n" + 
				"select \r\n" + 
				"    version() ;\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"select \r\n" + 
				"    user() ;\r\n" + 
				"\r\n" + 
				" \r\n" + 
				"select \r\n" + 
				"    database() ;\r\n" + 
				"\r\n" + 
				"create \r\n database if not exists tssss ;\r\n" + 
				"\r\n" + 
				" drop database if exists tssss ;\r\n" + 
				"\r\n" + 
				" show databases ;\r\n" + 
				"\r\n" + 
				" use tssss ;\r\n" + 
				"\r\n" + 
				"USE \r\n`tssss` ;\r\n" + 
				"\r\n" + 
				"create table if not exists t \r\n" + 
				"select \r\n" + 
				"    1 ;\r\n" + 
				"\r\n" + 
				"alter table t \r\n" + 
				"    add index `1` (`1`) ;\r\n" + 
				"\r\n" + 
				"#create table if not exists t select 1;-- alter table t add index `1`(`1`);select * from (select 1  ) t;select * from (select 1  ) t;\r\n" + 
				";";
		matcher = pattern.matcher(sql);
		System.out.println(matcher.find());
		i = 1;
		while(matcher.find()) {
			System.out.println(i++ + ":" + matcher.group());
		}
		sql = "SELECT \r\n" + 
				"  m.`memberCode` AS 工号, m.`memberName` as 姓名, COUNT(DISTINCT p.`projectId`) AS `项目总数`,\r\n" + 
				"  COUNT(DISTINCT IF(projectState='32', p.projectId, NULL)) AS `实施中`,COUNT(DISTINCT IF(projectState='50', p.projectId, NULL)) AS `已验收`,COUNT(DISTINCT IF(projectState='100', p.projectId, NULL)) AS `已完成`,\r\n" + 
				"  COUNT(DISTINCT IF(progress = 100, taskTypeId, NULL)) AS 已完成规范项,\r\n" + 
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
				"FROM `pm_project` p \r\n" + 
				"LEFT JOIN \r\n" + 
				"(SELECT DISTINCT projectId, projectType, taskTypeCode, taskTypeId, progress FROM pm_project_task \r\n" + 
				"WHERE taskTypeCode = 'afProjectTaskType' AND `effectiveTo` IS NULL AND progress = 100 ) t \r\n" + 
				"  ON p.`projectId` = t.`projectId` \r\n" + 
				"  AND p.`projectType` = t.`projectType` \r\n" + 
				"LEFT JOIN `pm_project_member` m \r\n" + 
				"  ON p.`projectId` = m.`projectId` \r\n" + 
				"  AND p.`projectType` = m.`projectType` \r\n" + 
				"  AND m.`memberRole` = \"30\" \r\n" + 
				"  AND m.`effectiveTo` IS NULL\r\n" + 
				"WHERE m.`memberRole` = \"30\" \r\n" + 
				"  AND p.`projectType` = 'afss'\r\n" + 
				"GROUP BY m.`memberCode`";
		matcher = pattern.matcher(sql);
		System.out.println(matcher.find());
		while(matcher.find()) {
			System.out.println(matcher.group());
		}
	}
}
