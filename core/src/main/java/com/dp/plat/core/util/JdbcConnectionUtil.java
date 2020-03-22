package com.dp.plat.core.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class JdbcConnectionUtil {
	
	private static Connection getConnection(String driverName , String url , String username ,String password){
		try {
			Class.forName(driverName);
			Connection conn = DriverManager.getConnection(url, username, password);	
			return conn;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取PMS数据库的链接
	 * @return
	 */
	public static Connection getPmsConnection(){
		//PMS数据库
		String pmsDriver = JDBCPropertiesUtil.returnParam("pms.database.driverClassName");
		String pmsUrl = JDBCPropertiesUtil.returnParam("pms.database.url");//?autoReconnect=true
		String pmsUser = JDBCPropertiesUtil.returnParam("pms.database.username");
		String pmsPwd = JDBCPropertiesUtil.returnParam("pms.database.password");
		return getConnection(pmsDriver, pmsUrl, pmsUser, pmsPwd);
	}
	/**
	 * 获取SAP数据库链接
	 * @return
	 */
	public static Connection getSapConnection(){
		
		String driver = JDBCPropertiesUtil.returnParam("sap.database.driverClassName");
		String url = JDBCPropertiesUtil.returnParam("sap.database.url");//?autoReconnect=true
		String user = JDBCPropertiesUtil.returnParam("sap.database.username");
		String pwd = JDBCPropertiesUtil.returnParam("sap.database.password"); 
		
		return getConnection(driver, url, user, pwd);
	}
	
	/**
	 * 获取本地数据库链接
	 * @return
	 */
	public static Connection getLocalConnection(){
		//SMS数据库
		String driver = JDBCPropertiesUtil.returnParam("main.database.driverClassName");
		String url = JDBCPropertiesUtil.returnParam("main.database.url");//?autoReconnect=true
		String user = JDBCPropertiesUtil.returnParam("main.database.username");
		String pwd = JDBCPropertiesUtil.returnParam("main.database.password"); 
		
		return getConnection(driver, url, user, pwd);
	}
	
	public static void cloesConnection(Connection conn){
		if(conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
