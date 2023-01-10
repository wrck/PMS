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
					<s:if test="%{#index.index == 0 || navTabList.size() == 1}">
		    			<li name="navli" class="active nav<s:property value='#index.index'/>" onclick="clickSwitchTab(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>','module/report_input.action')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
		    		</s:if>
		    		<s:else>
		    			<li name="navli" class="nav<s:property value='#index.index'/>" onclick="clickSwitchTab(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>','module/DataAnalysis.action?1=1')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
		    		</s:else>
		    	</s:iterator>
			</ul>
		</div>
	</nav>

<div class="navDiv  10">
<s:form id="mainForm" name="mainForm" cssClass="form-inline" action="module/report_show.action">
	<div class="panel panel-default">
  	 	<div class="panel-body">
			<div class="form-group form-group-query form-group-head-width">
				筛选时间段
			</div>
			<div class="form-group form-group-query">
		    	<s:textfield name="dataQueryParam.startTime" id="startTime" placeholder="输入开始时间" cssStyle="width:163px" cssClass="form-control" />
			</div>
			<div class="form-group form-group-query">
				------
			</div>
			<div class="form-group form-group-query ">
		    	 <s:textfield name="dataQueryParam.endTime" id="endTime" placeholder="输入结束时间" cssStyle="width:163px" cssClass="form-control" />
			</div>
			<br/>
			<div class="form-group form-group-query form-group-head-width">
				办事处
			</div>
			<div class="form-group form-group-query">
				<input name="dataQueryParam.officeCodes" type="hidden" id = "officeCodesHide"/>
				<select multiple="multiple" id="officeCodes">
					<s:iterator value="officeList" var="v">
						<option value="<s:property value='#v.departmentNum'/>"><s:property value='#v.departmentName'/></option>
					</s:iterator>
				</select>
			</div>
			<br/>
			<br/>
			<div class="form-group form-group-query form-group-head-width">
				<button  type="submit"  class="btn btn-info btn-sm"><s:text name="sys.confirm"></s:text></button>	
			</div>
		</div>
	</div>
</s:form>

</div>

<div class="navDiv hideDiv 20">
	
</div> 



</body>
</html>