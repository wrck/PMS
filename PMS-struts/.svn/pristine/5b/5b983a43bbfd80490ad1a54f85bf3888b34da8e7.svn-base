package com.dp.plat.util;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import com.dp.plat.data.bean.MailSenderInfo;

public class PmClosedLoopUtil {
	//生成编号,规则：numberHeader+当前日期+三位流水号
	public static String geneticSerialNumber(String numberHeader,String maxSerialNumber){
		DecimalFormat df1 = new DecimalFormat("00");
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(new Date());
		String nowDateString=numberHeader+calendar.get(Calendar.YEAR)+df1.format(calendar.get(Calendar.MONTH)+1)+df1.format(calendar.get(Calendar.DATE));
		
		if (maxSerialNumber!=null&&!(maxSerialNumber.equals(""))) {
			DecimalFormat df2 = new DecimalFormat("000");
			maxSerialNumber=nowDateString+df2.format(1+Integer.parseInt(maxSerialNumber.substring(maxSerialNumber.length()-3,maxSerialNumber.length())));
		}else{
			maxSerialNumber=nowDateString+"001";
		}
		
		return maxSerialNumber;
	}
	
	public static MailSenderInfo pmCLSendMail(String mailSubject,String mailContent,String mailTos,String mailCcs ){
		MailSenderInfo mailSenderInfo=new MailSenderInfo();
		
		mailSenderInfo.setSubject(mailSubject);
		mailSenderInfo.setContent(mailContent);
		mailSenderInfo.setTos(mailTos);
		mailSenderInfo.setCcs(mailCcs);
		
		mailSenderInfo.setMailExpectSendTime(new Date());
		mailSenderInfo.setSendFlag(0);
		mailSenderInfo.setMailServerHost("172.153.254.12");
		mailSenderInfo.setMailServerPort("25");
		mailSenderInfo.setFromAddress("pms@dptech.com");
		mailSenderInfo.setUserName("pms@dptech.com");
		mailSenderInfo.setPassword("2Bk29UamZr");
		return mailSenderInfo;
	}
	
	public static String geneticSqlText(Map<String, String> tableInfoMap,String tableName,int sqlTextType,String listName){
		Iterator<String> iteratorSet=tableInfoMap.keySet().iterator();
		String sqlColumn="";
		String classPropertyString="";
		String javaBeanTextString="";
		StringBuilder builder=new StringBuilder();
		while (iteratorSet.hasNext()) {
			String tableColumnName =iteratorSet.next();
			String tableColumnType=tableInfoMap.get(tableColumnName);
			if(sqlTextType==5){
				String javaBeanTextStringOne="private ";
				if(tableColumnType.equals("varchar")){
					javaBeanTextStringOne+="String ";
				}else if(tableColumnType.equals("int")){
					javaBeanTextStringOne+="int ";
				}else if(tableColumnType.equals("double")){
					javaBeanTextStringOne+="double ";
				}else if(tableColumnType.equals("datetime")){
					javaBeanTextStringOne+="Date ";
				}
				javaBeanTextStringOne+=tableColumnName+";";
				javaBeanTextString+=javaBeanTextStringOne+"\n";
			}else if(sqlTextType==6){
				builder.append("<result property=\""+tableColumnName+"\" column=\""+tableColumnName+"\" ");
				if(tableColumnType.equals("varchar")){
					builder.append("/>");
				}else if(tableColumnType.equals("int")){
					builder.append("nullValue=\"0\" />");
				}else if(tableColumnType.equals("double")){
					builder.append("nullValue=\"0\" />");
				}else if(tableColumnType.equals("datetime")){
					builder.append("jdbcType=\"DATE\" />");
				}
				builder.append("\n");
			}else if(sqlTextType==7){
				if(!tableColumnName.equals("id")){
					sqlColumn+=tableColumnName+" ,";
					classPropertyString+="#"+listName+"[]."+tableColumnName+"# ,";
				}
			}else if(sqlTextType==1){
				if(!tableColumnName.equals("id")){
					sqlColumn+=tableColumnName+" ,";
					classPropertyString+="#"+tableColumnName+"# ,";
				}
			}
			
		}
		if(sqlColumn!=null&&sqlColumn.length()>2){
			sqlColumn=sqlColumn.substring(0, sqlColumn.length()-1);
			classPropertyString=classPropertyString.substring(0, classPropertyString.length()-1);
		}
		switch (sqlTextType) {
		case 1:
				return "insert into "+tableName+"("+sqlColumn+")"+" values("+classPropertyString+")";
		case 5:
			return javaBeanTextString;
		case 6:
			return builder.toString();
		case 7:
			return "insert into "+tableName+"("+sqlColumn+")"+" values("+classPropertyString+")";
		default:
			return null;
		}
		
	}

}

