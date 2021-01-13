<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.powermanage' />">
<meta name="function" content="<s:text name='sys.project.management' />">
<%-- <meta name="child" content=";<s:text name='pm.project.createproject' />">	 --%>
<style type="text/css">
.updateMark {
	color: green;
}

.createMark {
	color: blue;
}

.ui-corner-all {
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
}

.ui-menu-item a {
	height: 25px;
	font-size: 14px;
}

.ui-menu-item {
	height: 25px;
}

.form-group-width-1 {
	width: 280px;
}

.form-group-width {
	width: 230px;
}

label {
	width: 99px;
}
a.btn-default{
	color:#333!important;
}
</style>
<script>
var realnameArr=new Array();
var usernameArr=new Array();

var realnameArr2 = new Array();
var usernameArr2  = new Array();

var realnameArr3 = new Array();
var usernameArr3  = new Array();

$(document).ready(function(){
	
	queryalluser();
	$("#salesname").autocomplete({
		source: realnameArr,
	});
	queryallsysuser();
	$("#sm").autocomplete({
		source: realnameArr2,
	});
	queryprogramuser();
	$("#pm").autocomplete({
		source: realnameArr3,
	});
	
	
	$(".table-striped th").last().css({"text-align":"center"});
	
	date_picker2("createTime1");
	date_picker2("createTime2");
	
	date_picker2("refreshTime1");
	date_picker2("refreshTime2");
	
	date_picker2("closeTime1");
	date_picker2("closeTime2");
	var timeType = $("#projectTime").val();
	$(".projectTime").hide();
	switch(parseInt(timeType)){
	case 10:
		$("#createTime1").show();
		$("#createTime2").show();
		break;
	case 20:
		$("#refreshTime1").show();
		$("#refreshTime2").show();
		break;
	case 30:
		$("#closeTime1").show();
		$("#closeTime2").show();
		break;
	}
	$("#projectTime").change(function(){
		var time1 = "";
		$(".time1").each(function(){
			time1 += this.value;
		});
		var time2 = "";
		$(".time2").each(function(){
			time2 += this.value;
		});
		$(".projectTime").hide();
		$(".projectTime").val("");
		switch(parseInt(this.value)){
			case 10:
				$("#createTime1").show();
				$("#createTime2").show();
				$("#createTime1").val(time1);
				$("#createTime2").val(time2);
				break;
			case 20:
				$("#refreshTime1").show();
				$("#refreshTime2").show();
				$("#refreshTime1").val(time1);
				$("#refreshTime2").val(time2);
				break;
			case 30:
				$("#closeTime1").show();
				$("#closeTime2").show();
				$("#closeTime1").val(time1);
				$("#closeTime2").val(time2);
				break;
		}
	});
	
	if($("#marketName").val()) {
		$("#projectColumn11").val($("#projectColumn11").val() + "_" + $("#marketName").val());
	}
});

function queryalluser(){
	$.ajax({
		url:'queryperson.action',
		type:'post',
		dataType:'json',
		data:{},
		success:queryuser2
	});
}
function queryuser2(json){
	var userlist = json.personList;
	for(var i = 0;i < userlist.length;i++){
		usernameArr[i] = userlist[i].salesmanCode;
		realnameArr[i] = userlist[i].salesmanCode+"-"+userlist[i].salesmanName;
	}
}

function queryallsysuser(){
	$.ajax({
		url:'queryalluser.action',
		type:'post',
		dataType:'json',
		data:{roleid :11},
		success:querysysuser2
	});
}
function querysysuser2(json){
	var userlist = json.allusernameList;
	for(var i = 0;i < userlist.length;i++){
		usernameArr2[i] = userlist[i].username;
		realnameArr2[i] = userlist[i].username+"-"+userlist[i].realName;
	}
}

function queryprogramuser(){
	$.ajax({
		url:'queryalluser.action',
		type:'post',
		dataType:'json',
		data:{roleid :12},
		success:queryprogramuser2
	});
}
function queryprogramuser2(json){
	var userlist = json.allusernameList;
	for(var i = 0;i < userlist.length;i++){
		usernameArr3[i] = userlist[i].username;
		realnameArr3[i] = userlist[i].username+"-"+userlist[i].realName;
	}
}

function fillsalesman(){
	var obj=document.getElementById("salesname");
	if(obj.value==""){
		document.getElementById("salesnamehide").value="";
	}
	if(obj.value!=""){
		var i=0;
		for(;i<realnameArr.length;i++){
			if(realnameArr[i]==obj.value){
				break;
			}
		}
		if(i==realnameArr.length){
			return false;
		} else{
			document.getElementById("salesnamehide").value=usernameArr[i];
		}
	}
}

function fillsm(){
	var obj=document.getElementById("sm");
	if(obj.value==""){
		document.getElementById("smhide").value="";
	}
	if(obj.value!=""){
		var i=0;
		for(;i<realnameArr2.length;i++){
			if(realnameArr2[i]==obj.value){
				break;
			}
		}
		if(i==realnameArr2.length){
			return false;
		} else{
			document.getElementById("smhide").value=usernameArr2[i];
		}
	}
}

function fillpm(){
	var obj=document.getElementById("pm");
	if(obj.value==""){
		document.getElementById("pmhide").value="";
	}
	if(obj.value!=""){
		var i=0;
		for(;i<realnameArr3.length;i++){
			if(realnameArr3[i]==obj.value){
				break;
			}
		}
		if(i==realnameArr3.length){
			return false;
		} else{
			document.getElementById("pmhide").value=usernameArr3[i];
		}
	}
}

function createProject(obj){
	window.location = "module/ProjectCreate.action?project.contractNo="+obj; 
}

function updateProject(obj){
	//window.location = "module/ProjectModify.action?project.paramId="+obj;
	window.open("module/ProjectModify.action?project.paramId="+obj);
}

function submit(){
	var column011 = $("#projectColumn11").val();
	if (column011) {
		var t = $.trim(column011).split("_");
		$("#projectColumn11").val(t[0]);
		$("#marketName").val(t[1]);
	}
	$("#mainForm").submit();
}
</script>
</head>
<body>
	<s:form id="mainForm" name="mainForm" cssStyle="width:1400px;" cssClass="form-inline" action="module/ProjectManage.action">
		<div class="form-group form-group-query form-group-width-1">
			<label for="projectName"><s:text
					name="pm.project.projectName" /></label>
			<s:textfield name="project.projectName" id="projectName"
				placeholder="支持模糊搜索" cssStyle="width:163px" cssClass="form-control" />
		</div>
		<div class="form-group form-group-query form-group-width-1">
			<label for="contract"><s:text name="pm.project.projectCode"></s:text>/<s:text
					name="pm.contract" /></label>
			<s:textfield name="project.contractNo" cssStyle="width:163px"
				id="contract" placeholder="支持模糊搜索" cssClass="form-control" />
		</div>
		<div class="form-group form-group-query form-group-width-1">
			<label for="officearea"><s:text name="pm.officearea" /></label>
			<s:select name="project.column001" id="officearea"
				listKey="departmentNum" cssClass="form-control" headerKey=""
				headerValue="--请选择--" cssStyle="width:163px" value="%{user.dpNo}"
				listValue="departmentName" list="%{departmentList}" theme="simple" />
		</div>
		<div class="form-group form-group-query form-group-width-1">
			<label for="salesname"><s:text name="pm.salesman" /></label>
			<s:textfield id="salesname" onfocus="fillsalesman()"
				onblur="fillsalesman()" cssStyle="width:163px" placeholder="支持模糊搜索"
				cssClass="form-control" />
			<s:hidden name="project.salesManCode" value="" id="salesnamehide"></s:hidden>
		</div>
		<div class="form-group form-group-query form-group-width-1">
			<label for="projectState"><s:text name="pm.project.state" /></label>
			<s:select name="project.projectState" id="projectState"
				listKey="basicDataId" cssClass="form-control" headerKey=""
				headerValue="--请选择--" cssStyle="width:163px"
				listValue="basicDataName" list="%{projectTypeList}" theme="simple" />
		</div>
        <div class="form-group form-group-query form-group-width-1">
            <label for="executionState"><s:text name="pm.project.executionState" /></label>
            <s:select name="project.executionState" id="executionState"
                listKey="basicDataId" cssClass="form-control" headerKey=""
                headerValue="--请选择--" cssStyle="width:163px"
                listValue="basicDataName" list="%{projectExecutionStateList}" theme="simple" />
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <label for="closeProcessState"><s:text name="pm.project.closeProcessState" /></label>
            <s:select name="project.closeProcessState" id="closeProcessState"
                listKey="basicDataId" cssClass="form-control" headerKey=""
                headerValue="--请选择--" cssStyle="width:163px"
                listValue="basicDataName" list="%{projectCloseProcessStateList}" theme="simple" />
        </div>
		<%-- <div class="form-group form-group-query form-group-width-1">
			<label for="projectPlanState"><s:text name="pm.engineerStateName" /></label>
			<s:select name="project.projectPlanState" id="projectPlanState"
				listKey="basicDataId" cssClass="form-control" headerKey=""
				headerValue="--请选择--" cssStyle="width:163px"
				listValue="basicDataName" list="%{projectPlanStateList}" theme="simple" />
		</div> --%>
		<div class="form-group form-group-query form-group-width-1">
			<label for="deliverstate"><s:text name="pm.deliverStateName"></s:text></label>
			<s:select name="project.shipmentState" id="deliverState"
				listKey="basicDataId" cssClass="form-control" headerKey=""
				headerValue="--请选择--" cssStyle="width:163px"
				listValue="basicDataName" list="%{deliverStateList}" theme="simple" />
		</div>
        <div class="form-group form-group-query form-group-width-1">
            <label for="projectColumn11"><s:text name="pm.project.projectCategory" /></label>
            <s:select name="project.column011" id="projectColumn11"
                cssClass="form-control" headerKey=""
                headerValue="--请选择--" cssStyle="width:163px"
                list="#{10:'直签', 20:'非直签', '10_运营商市场部': '运营商直签'}" theme="simple" />
                <s:hidden id="marketName" name="project.column004"></s:hidden>
        </div>
		<div class="form-group form-group-query form-group-width-1">
			<label for="projectColumn10"><s:text name="pm.project.projectType" /></label>
			<s:select name="project.column010" id="projectColumn10"
				listKey="basicDataName" cssClass="form-control" headerKey=""
				headerValue="--请选择--" cssStyle="width:163px"
				listValue="basicDataName" list="%{projectRankList}" theme="simple" />
		</div>
		<div class="form-group form-group-query form-group-width-1">
			<label for="sm"><s:text name="pm.project.serviceManager" /></label>
			<s:textfield id="sm" onfocus="fillsm()" onblur="fillsm()"
				cssStyle="width:163px" placeholder="支持模糊搜索" cssClass="form-control" />
			<s:hidden name="project.serviceManagerCode" value="" id="smhide"></s:hidden>
		</div>
		<div class="form-group form-group-query form-group-width-1">
			<label for="pm"><s:text name="pm.project.programManager" /></label>
			<s:textfield id="pm" onfocus="fillpm()" onblur="fillpm()"
				cssStyle="width:163px" placeholder="支持模糊搜索" cssClass="form-control" />
			<s:hidden name="project.programManagerCode" value="" id="pmhide"></s:hidden>
		</div>
		<div class="form-group form-group-query form-group-width-1">
			<label ><%-- <s:text name="pm.project.create.time" /> --%>
				<s:select id="projectTime" name="project.projectTimeType"
					listKey="basicDataId" cssClass="form-control" cssStyle="padding:0px;"
					listValue="basicDataName" list="%{projectTimeList}" theme="simple" />
			</label>
			<s:textfield id="createTime1" cssStyle="width:85px;" name="project.createStartTime" placeholder="开始时间" cssClass="form-control projectTime time1" />
			<s:textfield id="createTime2" cssStyle="width:85px;" name="project.createEndTime" placeholder="结束时间" cssClass="form-control projectTime time2" />
			
			<s:textfield id="refreshTime1" cssStyle="display:none" name="project.refreshStartTime" placeholder="开始时间" cssClass="form-control projectTime time1" />
			<s:textfield id="refreshTime2" cssStyle="display:none" name="project.refreshEndTime" placeholder="结束时间" cssClass="form-control projectTime time2" />
			
			<s:textfield id="closeTime1" cssStyle="display:none" name="project.closeStartTime" placeholder="开始时间" cssClass="form-control projectTime time1" />
			<s:textfield id="closeTime2" cssStyle="display:none" name="project.closeEndTime" placeholder="结束时间" cssClass="form-control projectTime time2" />
		</div>
		<div class="form-group form-group-query form-group-width-1">
			<label for="projectColumn12"><s:text name="pm.project.implement" /></label>
			<s:select name="project.column012" id="projectColumn12"
				listKey="basicDataId" cssClass="form-control" headerKey=""
				headerValue="--请选择--" cssStyle="width:163px"
				listValue="basicDataName" list="%{ssfsList}" theme="simple" />
		</div>
		<div class="form-group form-group-query form-group-width-1">
            <label for="itemModel"><s:text
                    name="pm.orderdata.model" /></label>
            <s:textfield name="project.itemModel" id="itemModel"
                placeholder="支持模糊搜索" cssStyle="width:163px" cssClass="form-control" />
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <label for="compId"><s:text name="pm.project.company" /></label>
            <s:select name="project.compId" id="compId"
                listKey="id" cssClass="form-control" headerKey=""
                headerValue="--请选择--" cssStyle="width:163px"
                listValue="name" list="%{companyList}" theme="simple" />
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <label for="partnerChannel"><s:text name="pm.project.partnerChannel" /></label>
            <s:textfield name="project.partnerChannel" id="partnerChannel"
                placeholder="支持模糊搜索" cssStyle="width:163px" cssClass="form-control" />
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <label for="barCode"><s:text name="pm.shipment.barCode" /></label>
            <s:textfield name="project.barCode" id="barCode"
                cssStyle="width:163px" cssClass="form-control" />
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <label for="warrantyStatus"><s:text name="pm.project.warrantyStatus" /></label>
            <s:select name="project.warrantyStatus" id="warrantyStatus"
                cssClass="form-control" headerKey=""
                headerValue="--请选择--" cssStyle="width:163px"
                list="#{-1:'维保内', 0:'部分保内', 1: '维保外'}" theme="simple" />
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <label for="warrantyGrade"><s:text name="pm.project.warrantyGrade" /></label>
            <s:select name="project.warrantyGrade" id="warrantyGrade"
                cssClass="form-control" headerKey=""
                headerValue="--请选择--" cssStyle="width:163px"
                list="#{1:'基本维保', 2:'中级维保', 3: '高级维保'}" theme="simple" />
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <label for="wafService"><s:text name="pm.project.wafService" /></label>
            <s:select name="project.wafService" id="wafService"
                cssClass="form-control" headerKey=""
                headerValue="--请选择--" cssStyle="width:163px"
                list="#{1:'策略调优服务'}" theme="simple" />
        </div>
		<div class="form-group form-group-query form-group-width-1">
			<a href="javascript:void(0)" onclick="submit()"
				class="btn btn-default btn-sm" style="margin-left: 100px;"> <span
				class="glyphicon glyphicon-search"></span> <s:text name="sys.query"></s:text>
			</a>
			<button type="reset" class="btn btn-default btn-sm" style="margin-left: 30px;"> 重置</button>
		</div>
	</s:form>
	<div class="divHeader div-height">
		<img src="images/right_zhishi.gif" border="0">
		<s:text name="projectmanage.projectlist.waitcreate"></s:text>
	</div>
	<s:if test="user.isHasRole(13) || user.isHasRole(1)">
		<a href="module/BatchChangeProjectMember.action" target="_blank" style="margin-bottom:1rem;" class="btn btn-default btn-sm">变更服务经理或项目经理</a>
		<a href="module/clearProject.action" target="_blank" style="margin-bottom:1rem;margin-left:1rem;" class="btn btn-default btn-sm">删除项目</a>
        <a href="module/createCHProject.action" target="_blank" style="margin-bottom:1rem;margin-left:1rem;" class="btn btn-default btn-sm">创建项目</a>
    </s:if>
	<div style="width:1700px;">
		<display:table name="projectlist" pagesize="${displayParam.pagesize }"
			export="true" size="${displayParam.totalcount }" sort="external"
			requestURI="module/ProjectManage.action"
			decorator="com.dp.plat.decorators.Wrapper"
			class="table table-striped" partialList="true">
			<s:if test="%{project.projectState != '10'}">
				<display:column property="projectCode" style="width:150px"
					titleKey="pm.project.projectCode"></display:column>
			</s:if>
			<display:column property="projectNameWarrper" style="width:240px"
				titleKey="pm.project.projectName" media="html"></display:column>
			<display:column property="projectName"
				titleKey="pm.project.projectName" media="excel"></display:column>
			<display:column property="contractNo" titleKey="pm.contract" decorator="com.dp.plat.decorators.ContractNoList" media="html"></display:column>
            <display:column property="contractNo" titleKey="pm.contract" media="excel"></display:column>
            <display:column property="compName" style="width:65px;" titleKey="pm.project.company"></display:column>
			<display:column property="column001Name" titleKey="pm.officearea"></display:column>
			<display:column property="projectStateName" titleKey="pm.project.state"></display:column>
			<s:if test="%{project.projectState != '10'}">
				<display:column property="column012Name" titleKey="pm.project.implement"></display:column>
				<display:column property="column010Name" titleKey="pm.project.projectType"></display:column>
			</s:if>
			<display:column property="salesManName" style="width:70px" titleKey="pm.project.usernamec"></display:column>
			<s:if test="%{project.projectState != '10'}">
				<display:column property="serviceManagerCodeforjson" titleKey="pm.project.programManager"></display:column>
			</s:if>
			<s:if test="%{project.projectState != '10'}">
				<display:column property="projectStartTime" format="{0,date,yyyy-MM-dd}" titleKey="pm.order.create.time"></display:column>
				<display:column property="projectRefreshTime" format="{0,date,yyyy-MM-dd}" titleKey="pm.project.refresh.time"></display:column>
			</s:if>
			<s:if test="%{project.projectState != '10'}">
			    <display:column property="executionStateName" titleKey="pm.project.executionState"></display:column>
                <display:column property="closeProcessStateName" titleKey="pm.project.closeProcessState"></display:column>
				<%-- <display:column property="projectPlanStateName" titleKey="pm.current.task"></display:column> --%>
				<display:column property="shipmentStateName" titleKey="pm.deliverStateName"></display:column>
                <%-- <display:column property="partnerChannel" titleKey="pm.project.partnerChannel"></display:column> --%>
                
                <display:column property="agentChannel" titleKey="pm.project.agentChannel"></display:column>
                <display:column property="serviceChannel" titleKey="pm.project.serviceChannel"></display:column>
            </s:if>
			<%-- <display:column property="handleWarrper" titleKey="sys.write" style="text-align:center"
				media="html"></display:column> --%>
            <display:setProperty name="export.excel.filename" value="项目清单.xls" />
		</display:table>
	</div>
</body>
</html>
