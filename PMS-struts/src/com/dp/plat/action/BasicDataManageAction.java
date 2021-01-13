package com.dp.plat.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.service.BasicDataService;

public class BasicDataManageAction extends BaseAction {

	private static final long serialVersionUID = 2681920322095754101L;
	private BasicDataService basicDataService;
	private List<BasicDataBean> basicDataTypeList;
	private List<BasicDataBean> basicDataList;
	private BasicDataBean basicData;
	private String dataTypeCode;
	private String basicDataId;
	private int result;
	private String executeSql;
	private String msg;
	
	public String execute(){
		basicDataTypeList = basicDataService.queryBasicDataType();
		if(basicData != null){
			basicDataList = basicDataService.queryBasicDataBeanAll(basicData.getBasicDataTypeCode());
		}else{
			basicDataList = new ArrayList<BasicDataBean>();
		}
		return SUCCESS;
	}
	
	public String basicdataUpdate(){
		if(basicData.getId()!=0 && basicData.getBasicDataId() == null){
			basicData = basicDataService.queryBasicDataBean(basicData.getId());
			return INPUT;
		}
		//更新
		basicDataService.updateBasicData(basicData);
		dataTypeCode = basicData.getBasicDataTypeCode();
		return SUCCESS;
	}
	
	public String basicdataInsert(){
		if(basicData == null){
			basicDataTypeList = basicDataService.queryBasicDataType();
			return INPUT;
		}
		basicDataService.insertBasicDataBean(basicData);
		dataTypeCode = basicData.getBasicDataTypeCode();
		return SUCCESS;
	}
	/**
	 * ajax获取当前编码是否存在
	 * @return
	 */
	public String findBasicDataId(){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("dataTypeCode", dataTypeCode);
		paramMap.put("basicDataId", basicDataId);
		result = basicDataService.findBasicDataId(paramMap);
		return SUCCESS;
	}
	
	public String executeSql(){
		
		try {
			if(executeSql.toLowerCase().indexOf("where") != -1){
				basicDataService.executeSql(executeSql);
				msg = "执行更新或删除成功";
			}else if(executeSql.toLowerCase().indexOf("insert") != -1){
				basicDataService.executeSql(executeSql);
				msg = "执行插入成功";
			}else{
				msg = "执行的SQL没有where条件，不允许执行";
			}
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setHeader("Access-Control-Allow-Origin", "*");
		} catch (Exception e) {
			msg = "执行出现了错误："+e.getMessage();
		}
		return SUCCESS;
	}
	public List<BasicDataBean> getBasicDataTypeList() {
		return basicDataTypeList;
	}

	public void setBasicDataTypeList(List<BasicDataBean> basicDataTypeList) {
		this.basicDataTypeList = basicDataTypeList;
	}

	public List<BasicDataBean> getBasicDataList() {
		return basicDataList;
	}

	public void setBasicDataList(List<BasicDataBean> basicDataList) {
		this.basicDataList = basicDataList;
	}

	public BasicDataBean getBasicData() {
		return basicData;
	}

	public void setBasicData(BasicDataBean basicData) {
		this.basicData = basicData;
	}

	public void setBasicDataService(BasicDataService basicDataService) {
		this.basicDataService = basicDataService;
	}

	public String getDataTypeCode() {
		return dataTypeCode;
	}

	public void setDataTypeCode(String dataTypeCode) {
		this.dataTypeCode = dataTypeCode;
	}

	public String getBasicDataId() {
		return basicDataId;
	}

	public void setBasicDataId(String basicDataId) {
		this.basicDataId = basicDataId;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getExecuteSql() {
		return executeSql;
	}

	public void setExecuteSql(String executeSql) {
		this.executeSql = executeSql;
	}
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
