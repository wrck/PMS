package com.dp.plat.job;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.dp.plat.util.JDBCPropertiesUtil;

/**
 * 从销售管理系统抓取SMS收款计划数据
 * @author admin
 *
 */
public class PlanGetBySMS{
	public static synchronized void work(){
		Connection conn = null;
		Connection smsConn = null;
		PreparedStatement smsPs = null;
		ResultSet smsRs = null;
		try {
			//spms本身数据库
			String driver = JDBCPropertiesUtil.returnParam("main.database.driverClassName");
			String url = JDBCPropertiesUtil.returnParam("main.database.url");//?autoReconnect=true
			String user = JDBCPropertiesUtil.returnParam("main.database.username");
			String pwd = JDBCPropertiesUtil.returnParam("main.database.password"); 
			
			//sms数据库
			String smsDriver = JDBCPropertiesUtil.returnParam("sms.database.driverClassName");
			String smsUrl = JDBCPropertiesUtil.returnParam("sms.database.url");//?autoReconnect=true
			String smsUser = JDBCPropertiesUtil.returnParam("sms.database.username");
			String smsPwd = JDBCPropertiesUtil.returnParam("sms.database.password"); 
			
			//查询SMS系统财务收款计划数据
			Class.forName(smsDriver);
			smsConn = DriverManager.getConnection(smsUrl,smsUser,smsPwd);
			String smssql = "select contract_num, batch_code, money_item_name, reference_event_name, event_plan_happen_date, after_days_num, "
							+ "event_actual_finish_date, marketing_feedback "
							+ "from v_sms_pb_plan";
			smsPs = smsConn.prepareStatement(smssql);
			smsRs = smsPs.executeQuery();
			
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pwd);
			conn.setAutoCommit(false);
			
			Statement cs = conn.createStatement();
			String truncatesql = "truncate table pm_pb_plan_from_sms";//清空表
			cs.addBatch(truncatesql);
//			String beforeDate = Util.formatDate("yyyy-MM-dd", Util.getTimeAddDay(new Date(), -1));//获取前一天日期
			//遍历sms查询集合，插入工程相关表
			while(smsRs.next()){
				String contractNo = smsRs.getString("contract_num");
				String batchCode = smsRs.getString("batch_code");
				String basicDataName = smsRs.getString("money_item_name");
				String referenceEventName = smsRs.getString("reference_event_name");
				Date eventPlanHappenDate = smsRs.getDate("event_plan_happen_date");
				Integer afterDaysNum = smsRs.getInt("after_days_num");
				Date eventActualFinishDate = smsRs.getDate("event_actual_finish_date");
				String marketingFeedback = smsRs.getString("marketing_feedback");
				
				//插入pm_pb_plan_from_sms
				String spmssql = "insert into pm_pb_plan_from_sms (contractNo, batchCode, basicDataName, referenceEventName, eventPlanHappenDate, "
						+ "afterDaysNum, eventActualFinishDate, marketingFeedback, createBy, createTime, updateBy, updateTime, effectiveFrom, effectiveTo) "
						+ "values ('" + contractNo + "', '" + batchCode + "', '" + basicDataName + "', '" + referenceEventName + "', "
						+ (eventPlanHappenDate == null || ("".equals(eventPlanHappenDate)) ? "null" : "'" + eventPlanHappenDate + "'") + ", '" + (afterDaysNum == null ? "" : afterDaysNum) + "', " 
						+ (eventActualFinishDate == null || ("".equals(eventActualFinishDate)) ? "null" : "'" + eventActualFinishDate + "'") + ", '" + (marketingFeedback == null ? "" : marketingFeedback) +  "'"
						+ ", 'admin', NOW(), 'admin', NOW(), '2015-05-01', null)";
				cs.addBatch(spmssql);
				//查询事件名称所对应的编码
				String queryeventname = "select dataTypeCode, basicDataId from fnd_basic_data "
										+ "where basicDataName = '" + referenceEventName + "' and "
										+ "effectiveFrom <= NOW() and (effectiveTo > NOW() or effectiveTo is null) limit 1";
				ResultSet rs = cs.executeQuery(queryeventname);
				String dataTypeCode = null, basicDataId = null;
				if(rs.next()){
					dataTypeCode = rs.getString("dataTypeCode");
					basicDataId = rs.getString("basicDataId");
				}
				
				if(dataTypeCode != null && basicDataId != null){
					//更新pm_project_task
					String updatesql = "update pm_project_task set eventPlanHappenDate = " + (eventPlanHappenDate == null ? "null" : "'" + eventPlanHappenDate + "'") + ", visibleFlag = '1'"
							+ " where taskTypeCode = '" + dataTypeCode + "' and taskTypeId = '" + basicDataId + "' and contractNo = '" + contractNo
							+ "' and effectiveFrom <= NOW() and (effectiveTo > NOW() or effectiveTo is null)";
					cs.addBatch(updatesql);
				}
			}
			
			cs.executeBatch();
			conn.commit();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			closeJDBCObj(conn);
			closeJDBCObj(smsConn);
			closeJDBCObj(smsPs);
			closeJDBCObj(smsRs);
		}
	}
	
	private static void closeJDBCObj(Object obj){
		if(obj != null ){
			if(obj instanceof Connection){
				try {
					((Connection)obj).close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else if(obj instanceof PreparedStatement){
				try {
					((PreparedStatement)obj).close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else if(obj instanceof ResultSet){
				try {
					((ResultSet)obj).close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else{
				throw new RuntimeException("[" + obj + "]对象无法关闭，类型为：" + obj.getClass());
			}
		}
	}
}
