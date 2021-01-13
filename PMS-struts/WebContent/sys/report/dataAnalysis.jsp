<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.powermanage' />">
<meta name="function"content="<s:text name='pm.data.analysis' />">
<style type="text/css">
.form-group-head-width{
	width: 70px;
}
.form-group-width{
	width: 165px;
}

.ui-multiselect {
padding: 2px 0 2px 4px;
text-align: left;
min-height: 35px;
min-width: 382px;
}

table thead th{
	white-space: nowrap;
}
</style>
<script type="text/javascript">

	var realnameArr=new Array();
	var usernameArr=new Array();
	
	function queryalluser(){
		$.ajax({
			url:'queryalluser.action',
			type:'post',
			dataType:'json',
			data:{},
			success:queryuser2
		});
	}
	function queryuser2(json){
		var userlist = json.allusernameList;
		for(var i = 0;i < userlist.length;i++){
			usernameArr[i] = userlist[i].username;
			realnameArr[i] = userlist[i].username+"-"+userlist[i].realName;
		}
	}
	$(function(){
		date_picker("startTime");
		date_picker("endTime");
		
		date_picker("cbStartTime");
		date_picker("cbEndTime");
		
		multiselect("officeCodes", "officeCodesHide" ,410,240);
		
		queryalluser();
		$("#pm").autocomplete({
			source: realnameArr
		});
		
/* 		if("<s:property value='returnType'/>"==2){//回访数据查询
			clickNavLi(1,'20');
			$("#cbStartTime").val($("#hideCbStartTime").text());
			$("#cbEndTime").val($("#hideCbEndTime").text());
		} */		
	});
	
	
	function fillPm(){
		commonfill("pm", "pmhide", usernameArr, realnameArr);
	}
	
	
</script>
</head>
<body>
<nav class="navbar navbar-default" role="navigation" style="margin-top: 20px;">
	<div>
	    <ul class="nav navbar-nav">
	    	<s:iterator value="navTabList" var="nav" status="index">
				<s:if test="%{#index.index == 0}">
	    			<li name="navli" class="nav<s:property value='#index.index'/>" onclick="clickSwitchTab(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>','module/report_show.action')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
	    		</s:if>
	    		<s:else>
	    			<li name="navli" class="active nav<s:property value='#index.index'/>" onclick="clickSwitchTab(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>','module/DataAnalysis.action?1=1')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
	    		</s:else>
	    	</s:iterator>
		</ul>
	</div>
</nav>

<div class="navDiv hideDiv 10">
</div>

<div class="navDiv 20">
	<div class="panel panel-default">
  	 	<div class="panel-body" style="width: 1400px">
			<s:form id="" method="POST" action="module/DataAnalysis.action">
				<input type="hidden" id="returnType" name="returnType" value="2"/>
			      <div class="form-group form-group-query">
					<label for="cbStartTime" style="font-weight: 500;"><s:text name="回访时间段：" /></label>
					<span class="hideMark" id="hideCbStartTime"><s:date name="dataQueryParam.cbStartTime" format="yyyy-MM-dd"/></span>
			    	<s:textfield id="cbStartTime" placeholder="开始时间"  name="dataQueryParam.cbStartTime" cssClass="form-control" cssStyle="width: 150px;display: inline-block;" >
			    		<s:param name="value">
			    			<s:date name="dataQueryParam.cbStartTime" format="yyyy-MM-dd"></s:date>
			    		</s:param>
			    	</s:textfield>
			    	<span style="margin: 0 5px;">----</span>
			    	<span class="hideMark" id="hideCbEndTime"><s:date name="dataQueryParam.cbEndTime" format="yyyy-MM-dd" /></span>
			    	<s:textfield id="cbEndTime" placeholder="结束时间"  name="dataQueryParam.cbEndTime" cssClass="form-control" cssStyle="width: 150px;display: inline-block;" >
			    		<s:param name="value">
			    			<s:date name="dataQueryParam.cbEndTime" format="yyyy-MM-dd"></s:date>
			    		</s:param>
		    		</s:textfield>
		    		<label for="serviceType" style="font-weight: 500;margin-left:10px;"><s:text name="实施方式：" /></label>
					<s:select name="dataQueryParam.serviceType" id="serviceType"
						listKey="basicDataId" cssClass="form-control" headerKey=""
						headerValue="--请选择--" cssStyle="width:140px;display:inline-block;"
						listValue="basicDataName" list="%{serviceTypeList}"
						theme="simple" />
					<button style="margin-left: 50px;" type="submit"  class="btn btn-info btn-sm"><s:text name="sys.confirm"></s:text></button>	
				</div>
			</s:form>
		
		     <div id="pmCLList" style="margin-top:20px;">
				 <display:table
						name="pmClCBDataList" pagesize="${displayParam.pagesize }" export="true"
						size="${pmClCBDataList.size()}" sort="external"
						requestURI="module/DataAnalysis.action"
						decorator="com.dp.plat.decorators.Wrapper" class="table table-striped"
						partialList="true">
						<display:column property="dataCbProName" titleKey="pm.cl.cbdata.projectName" media="html"></display:column>
						<display:column property="projectName" titleKey="pm.cl.cbdata.projectName" media="excel"></display:column>
						<display:column property="pmRealName" titleKey="pm.cl.cbdata.pmRealName" ></display:column>
						<display:column property="officeName" titleKey="pm.cl.cbdata.officeName" ></display:column>
						<display:column property="cbTime" titleKey="pm.cl.cbdata.cbTime" format="{0,date,yyyy-MM-dd}"></display:column>
						<display:column property="times" titleKey="pm.cl.cbdata.times"></display:column>
						
						<display:column property="projectScore" titleKey="pm.cl.cbdata.projectScore"></display:column>
						<display:column property="otherScore" titleKey="pm.cl.cbdata.otherScore" ></display:column>
						<display:column property="equScore" titleKey="pm.cl.cbdata.equScore"></display:column>
						<display:column property="engScore" titleKey="pm.cl.cbdata.engScore"></display:column>
						<display:column property="totalScore" titleKey="pm.cl.cbdata.totalScore"></display:column>
						<display:column property="serviceTypeWrapper" titleKey="pm.project.implement"></display:column>
						<display:column property="dataCbResult" titleKey="pm.cl.cbdata.cbResult"></display:column>
                        <display:column property="approveRemark" style="width:200px" titleKey="pm.cl.cbdata.opinion"></display:column>
						<display:column property="dataCbOpion" titleKey="pm.cl.cbdata.opinion"  media="html"></display:column>
						<display:column property="dataCbEquain" titleKey="pm.cl.cbdata.equExplain" media="html"></display:column>
						
						<display:column property="opinion" style="width:200px" titleKey="pm.cl.cbdata.opinion" media="excel"></display:column>
						<display:column property="equExplain" titleKey="pm.cl.cbdata.equExplain" media="excel"></display:column>
						<display:setProperty name="export.excel.filename"
								value='<%=StringEscUtil.getText("pm.cl.cbdata.list")%>'>
							</display:setProperty>
					</display:table> 
			 </div>
		</div>
	</div>	
</div> 



</body>
</html>