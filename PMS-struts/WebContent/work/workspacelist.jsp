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
<meta name="function" content="<s:text name='sys.todo.options' />">
<style>
	table thead th, .nowrap{
		white-space: nowrap;
	}
    .timeTd {
        min-width: 6.3em;
    }
</style>
<script type="text/javascript">
var realnameArr3 = new Array();
var usernameArr3  = new Array();
$(function(){
	var firstTab = "<s:property value='navTabList[0].basicDataId'/>";
	//$("."+firstTab).removeClass("hideDiv");	
	
	$("#history").click(function(){
		if($("#history").is(":checked")){
			$(".hideMark").show();
		}else{
			$(".hideMark").hide();
		}
	});
	
	queryprogramuser();
	$("#pm").autocomplete({
		source: realnameArr3,
	});
	$("#pm2").autocomplete({
		source: realnameArr3,
	});
	
	var tabIndex = "<s:property value='tabIndex'/>";
	var tabName = "<s:property value='tabName'/>";
	//tabIndex = $(".nav li[name='navli'][onclick*='" + tabName + "']").index();
	tabIndex = $(".nav li[name='navli'][data-name='" + tabName + "']").data("sort");
	
	tabIndex = tabIndex == -1 ? 0 : tabIndex;
	clickNavLi(tabIndex, tabName);
	/* switch(parseInt(tabIndex)){
		case 0 :
			clickNavLi(0,'dailyTask');
			break;
		case 1 :
			clickNavLi(1,'task');
			break;
		case 2 :
			clickNavLi(2,'notice');
			break;
		case 3 :
			clickNavLi(3,'probTask');
			break;
		case 4 :
			clickNavLi(4,'hisselftask');
			break;
		default:
			clickNavLi(0,'dailyTask');
	}; */
	
	if ("<s:property value='isCbRole' />" == "true") {
		var arr = $("#dapdlist tbody tr");
	    if (arr.length > 2) {
	        arr.sort(function(a, b){
	            if ($("td:eq(2)", $(a)).text() === $("td:eq(2)", $(b)).text()) {
	                return $("td:eq(4)", $(a)).text().localeCompare($("td:eq(4)", $(b)).text());
	            } else {
	                return $("td:eq(2)", $(a)).text().localeCompare($("td:eq(2)", $(b)).text());
	            }
	        });
	        $("#dapdlist tbody").html(arr);
	    }
	}
});

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

function fillpm2(){
	var obj=document.getElementById("pm2");
	if(obj.value==""){
		document.getElementById("pm2hide").value="";
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
			document.getElementById("pm2hide").value=usernameArr3[i];
		}
	}
}
</script>
</head>
<body>
	<!--待办事项的子菜单： 日常项目跟踪，流程业务处理，系统通知 -->
	<nav class="navbar navbar-default" role="navigation" style="margin-top: 20px;">
		<div>
		    <ul class="nav navbar-nav">
		    	<s:iterator value="navTabList" var="nav" status="index">
		    		<%-- <s:if test="%{#index.index == 0}">
		    			<li name="navli" class="active nav<s:property value='#index.index'/>" onclick="clickSwitchTab(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>','module/Workspace!<s:property value='#nav.basicDataId'/>.action')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
		    		</s:if>
		    		<s:else>
		    			<li name="navli" class="nav<s:property value='#index.index'/>" onclick="clickSwitchTab(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>','module/Workspace!<s:property value='#nav.basicDataId'/>.action')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
		    		</s:else> --%>
		    		<%-- <s:if test="%{#index.index == 0}">
                        <li name="navli" class="nav<s:property value='#nav.sortId'/>" data-sort="<s:property value='#nav.sortId'/>" data-name="<s:property value='#nav.basicDataId'/>" onclick="clickSwitchTab(<s:property value='#nav.sortId'/>,'<s:property value='#nav.basicDataId'/>','module/Workspace!<s:property value='#nav.basicDataId'/>.action')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
                    </s:if>
                    <s:else>
                        <li name="navli" class="nav<s:property value='#nav.sortId'/>" data-sort="<s:property value='#nav.sortId'/>" data-name="<s:property value='#nav.basicDataId'/>" onclick="clickSwitchTab(<s:property value='#nav.sortId'/>,'<s:property value='#nav.basicDataId'/>','module/Workspace!<s:property value='#nav.basicDataId'/>.action')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
                    </s:else> --%>
                    <li name="navli" class="${nav.basicDataId == tabName ? 'active' : ''} nav<s:property value='#nav.sortId'/>" data-sort="<s:property value='#nav.sortId'/>" data-name="<s:property value='#nav.basicDataId'/>" onclick="clickSwitchTab(<s:property value='#nav.sortId'/>,'<s:property value='#nav.basicDataId'/>','module/Workspace!<s:property value='#nav.basicDataId'/>.action')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
		    	</s:iterator>
			</ul>
		</div>
	</nav>
	<!-- 系统通知菜单 -->
	<div class="navDiv hideDiv notice">
		<!-- 搜索栏：项目名称，办事处，项目经理 -->
		<s:form id="notice" name="notice" cssClass="form-inline" action="module/Workspace!notice.action">
			<div class="form-group form-group-query form-group-width-1">
				<label for="projectName"><s:text
						name="pm.project.projectName" /></label>
				<s:textfield name="notifyQueryParam.projectName" id="projectName"
					placeholder="支持模糊搜索" cssStyle="width:163px" cssClass="form-control" />
			</div>
			<div class="form-group form-group-query form-group-width-1">
				<label for="officearea"><s:text name="pm.officearea" /></label>
				<s:select name="notifyQueryParam.officeCode" id="officearea"
					listKey="departmentNum" cssClass="form-control" headerKey=""
					headerValue="--请选择--" cssStyle="width:163px"
					listValue="departmentName" list="%{departmentList}" theme="simple" />
			</div>
			<div class="form-group form-group-query form-group-width-1">
				<label for="pm2"><s:text name="pm.project.programManager" /></label>
				<s:textfield id="pm2" onfocus="fillpm2()" onblur="fillpm2()"
					cssStyle="width:163px" placeholder="支持模糊搜索" cssClass="form-control" />
				<s:hidden name="notifyQueryParam.programManager" value="" id="pm2hide"></s:hidden>
			</div>
			<div class="form-group form-group-query form-group-width-1">
				<button class="btn btn-default btn-sm" style="margin-left: 100px;" id="noticeQuery"> <span
					class="glyphicon glyphicon-search"></span> <s:text name="sys.query"></s:text>
				</button>
			</div>
			<br/>
			<br/>
			<br/>
		</s:form>
		<!-- 系统通知列表 -->
		<display:table id="notificationList"
			name="notificationList" pagesize="${notifyDisplayParam.pagesize}" 
			size="${notifyDisplayParam.totalcount}" sort="external" export="false"
			requestURI="module/Workspace!notice.action"
			decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-striped"
			partialList="true">
			<%-- <display:column property="projectCode" titleKey="pm.project.projectCode"></display:column> --%>
			<display:column property="projectName" titleKey="pm.project.projectName"></display:column>
			<display:column property="officeName" titleKey="pm.project.officeName" style="width:100px;"></display:column>
			<display:column property="programManager" titleKey="pm.project.programManager" style="width:110px;"></display:column>
			<display:column property="createTime" titleKey="sys.create.time" style="width:150px;" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
			<display:column property="createBy" titleKey="sys.create.by" style="width:110px;"></display:column>
			<display:column property="notifyContent" titleKey="pm.notification.content"></display:column>
		</display:table>
	</div>
	<!-- 日常项目跟踪 -->
	<div class="navDiv hideDiv dailyTask ">
		<!-- 搜索栏： 项目名称，办事处，项目经理-->
		<s:form id="dailyTask" name="dailyTask" cssClass="form-inline" action="module/Workspace.action?tabIndex=0">
			<div class="form-group form-group-query form-group-width-1">
				<label for="projectName"><s:text
						name="pm.project.projectName" /></label>
				<s:textfield name="taskQueryParam.projectName" id="projectName"
					placeholder="支持模糊搜索" cssStyle="width:163px" cssClass="form-control" />
			</div>
			<div class="form-group form-group-query form-group-width-1">
				<label for="officearea"><s:text name="pm.officearea" /></label>
				<s:select name="taskQueryParam.officeCode" id="officearea"
					listKey="departmentNum" cssClass="form-control" headerKey=""
					headerValue="--请选择--" cssStyle="width:163px"
					listValue="departmentName" list="%{departmentList}" theme="simple" />
			</div>
			<div class="form-group form-group-query form-group-width-1">
				<label for="pm"><s:text name="pm.project.programManager" /></label>
				<s:textfield id="pm" onfocus="fillpm()" onblur="fillpm()"
					cssStyle="width:163px" placeholder="支持模糊搜索" cssClass="form-control" />
				<s:hidden name="taskQueryParam.programManager" value="" id="pmhide"></s:hidden>
			</div>
			<div class="form-group form-group-query form-group-width-1">
				<button class="btn btn-default btn-sm" style="margin-left: 100px;" id="dailyTaskQuery"> <span
					class="glyphicon glyphicon-search"></span> <s:text name="sys.query"></s:text>
				</button>
			</div>
			<br/>
			<br/>
			<br/>
		</s:form>
		<display:table id="dailyTaskList"
			name="dailyTaskList" pagesize="${displayParam.pagesize}" 
			size="${displayParam.totalcount}" sort="external" export="false"
			requestURI="module/Workspace.action"
			decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-striped"
			partialList="true">
			<display:column property="projectCode" titleKey="pm.project.projectCode"></display:column>
			<display:column property="projectNameEr" titleKey="pm.project.projectName"></display:column>
			<display:column property="projectPlanStateName" titleKey="pm.engineerStateName"></display:column>
			<display:column property="name" titleKey="sys.self.task"></display:column>
			<display:column property="officeName" titleKey="pm.project.officeName"></display:column>
			<display:column property="assigneeName" titleKey="pm.project.programManager"></display:column>
			<display:column property="planDiffTime" titleKey="pm.project.plan.diff.time"></display:column>
			<display:column property="pmCLWorkSpaceOpe" titleKey="display.operate"></display:column>
		</display:table>
	</div>
	<!-- 流程业务处理 -->
	<div class="navDiv hideDiv task">
        <!-- 搜索栏： 项目名称，办事处，项目经理-->
        <s:form id="dapdTask" name="dapdTask" cssClass="form-inline" action="module/Workspace!task.action">
            <div class="form-group form-group-query form-group-width-1">
                <label for="procKey"><s:text name="workflow.name" /></label>
                <s:select name="queryParams.procKey" id="procKey"
                    cssClass="form-control" headerKey=""
                    headerValue="所有流程" cssStyle="width:163px"
                    list="#{
                        'PmClosedLoop':'项目闭环/回访', 
                        'ProjectBack': '项目回退',
                        'ProjectTrack': '项目不予跟踪确认',
                        'ProjectSupervision': '项目督导',
                        'Presales': '售前测试'
                    }" theme="simple" />
            </div>
            <div class="form-group form-group-query form-group-width-1">
                <button class="btn btn-default btn-sm" style="margin-left: 100px;" id="dailyTaskQuery"> <span
                    class="glyphicon glyphicon-search"></span> <s:text name="sys.query"></s:text>
                </button>
            </div>
            <br/>
            <br/>
            <br/>
        </s:form>
		<display:table id="dapdlist"
			name="dapdlist" pagesize="${dapdlist.size()}" 
			size="${dapdlist.size()}" sort="external" export="false"
			decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-striped"
			partialList="true">
			<display:column property="projectCode" titleKey="pm.project.projectCode"  class="nowrap"></display:column>
			<display:column property="projectName" titleKey="pm.project.projectName"></display:column>
			<%-- <display:column property="procTypeNameWrapper" titleKey="pm.cl.evaluHeader.processName"></display:column> --%>
			<display:column property="officeName" titleKey="pm.project.officeName" class="nowrap"></display:column>
			<display:column property="realName" titleKey="pm.project.programManager"  class="nowrap"></display:column>
			<s:if test="isCbRole == true">
				<display:column property="projectCustomer" titleKey="pm.project.customerName" style="max-width:92px;"></display:column>
				<display:column property="projectImpl" titleKey="pm.project.implement"></display:column>
			</s:if>
			<display:column property="procTypeDesc" titleKey="workflow.name" class="nowrap"></display:column>
			<display:column property="name" titleKey="pm.cl.evaluHeader.taskName" class="nowrap"></display:column>
			<display:column property="assigneeName" titleKey="workflow.transactor" class="nowrap"></display:column>
			<display:column property="createTime" titleKey="fnd.baisc.data.createTime" format="{0,date,yyyy-MM-dd HH:mm}" class="nowrap"></display:column>
			<display:column property="pmCLWorkSpaceOpe" titleKey="display.operate" class="nowrap"></display:column>
		</display:table>
	</div>
	<div class="navDiv hideDiv probTask">
		<display:table id="probTaskList"
			name="probTaskList" pagesize="50" 
			size="${probTaskList.size()}" sort="external" export="false"
			decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-striped"
			partialList="true">
			<display:column property="probNum" titleKey="prob.info.num" class="nowrap" url="/module/prob_edit.action" paramId="prob.probId" paramProperty="probId"></display:column>
			<display:column property="theme" titleKey="prob.info.theme"></display:column>
			<display:column property="watchName" titleKey="prob.info.watch"></display:column>
			<display:column property="priorityName" titleKey="prob.info.level"></display:column>
			<display:column property="statusName" titleKey="prob.info.status"></display:column>
			<display:column property="trackingUsername" titleKey="prob.tracking.user"></display:column>
			<display:column property="createTime" titleKey="prob.info.createTime" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
			<display:column property="probNumCheck" titleKey="display.operate" class="nowrap"></display:column>
		</display:table>
	</div>
	<div class="navDiv hideDiv hisselftask">
        <!-- 搜索栏： 项目名称，办事处，项目经理-->
        <s:form id="hisselftask" name="hisselftask" cssClass="form-inline" action="module/Workspace!hisselftask.action">
            <div class="form-group form-group-query form-group-width-1">
                <label for="projectName"><s:text
                        name="pm.project.projectName" /></label>
                <s:textfield name="taskQueryParam.projectName" id="projectName"
                    placeholder="支持模糊搜索" cssStyle="width:163px" cssClass="form-control" />
            </div>
            <div class="form-group form-group-query form-group-width-1">
                <label for="officearea"><s:text name="pm.officearea" /></label>
                <s:select name="taskQueryParam.officeCode" id="officearea"
                    listKey="departmentNum" cssClass="form-control" headerKey=""
                    headerValue="--请选择--" cssStyle="width:163px"
                    listValue="departmentName" list="%{departmentList}" theme="simple" />
            </div>
            <div class="form-group form-group-query form-group-width-1">
                <label for="pm"><s:text name="pm.project.customerName" /></label>
                <s:textfield id="projectCustomter" name="taskQueryParam.projectCustomer" cssStyle="width:163px" placeholder="支持模糊搜索" cssClass="form-control" />
            </div>
            <div class="form-group form-group-query form-group-width-1">
                <button class="btn btn-default btn-sm" style="margin-left: 100px;" id="dailyTaskQuery"> <span
                    class="glyphicon glyphicon-search"></span> <s:text name="sys.query"></s:text>
                </button>
            </div>
            <br/>
            <br/>
            <br/>
        </s:form>
		<%-- <display:table id="dpHisList"
			name="dpHisList" pagesize="50" 
			size="${dpHisList.size()}" sort="external" export="true"
			decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-striped"
			requestURI="module/Workspace!hisselftask.action"
			partialList="true"> --%>
        <display:table id="dpHisList"
            name="dpHisList" pagesize="${displayParam.pagesize}" 
            size="${displayParam.totalcount}" sort="external" export="true"
            decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-striped"
            requestURI="module/Workspace!hisselftask.action"
            partialList="true">
			<display:column property="projectCode" titleKey="pm.project.projectCode"></display:column>
			<display:column property="projectName" titleKey="pm.project.projectName" style="width:200px;"></display:column>
			<display:column property="procTypeName" titleKey="pm.cl.evaluHeader.processName"></display:column>
			<display:column property="name" titleKey="pm.cl.evaluHeader.taskName"></display:column>
			<display:column property="username" titleKey="pm.cl.evaluHeader.applyPerId"></display:column>
			<display:column property="realName" titleKey="pm.cl.evaluHeader.applyPerName"></display:column>			
			<s:if test="isCbRole == true">
                <display:column property="projectCustomer" titleKey="pm.project.customerName" style="max-width:92px;" media="excel"></display:column>
                <display:column titleKey="pm.project.customerName" class="nowrap" style="max-width:92px;" media="html">
                    ${dpHisList.projectCustomer.replace(",", "<br>")}
                </display:column>
                <%-- <display:column property="projectImpl" titleKey="pm.project.implement"></display:column> --%>
            </s:if>
            
            <display:column property="assigneeName" titleKey="pm.cl.evaluHeader.hisApprovePer"></display:column>
			<display:column property="endTime" titleKey="pm.cl.evaluHeader.approveTime" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
			<display:column property="workspEvaluaResult" titleKey="pm.cl.evaluHeader.approveResult"></display:column>
			<display:column property="pmCLWorkSpaceOpe" media="html" titleKey="display.operate"></display:column>
		</display:table>
	</div>
	<div class="navDiv hideDiv subcontractTask">
    <s:if test="%{tabName == 'subcontractTask'}">
        <!-- 搜索栏：项目名称，办事处，项目经理 -->
        <s:form id="subcontractTaskForm" name="subcontractTaskForm" cssClass="form-inline" action="module/Workspace!subcontractTask.action">
            <div class="form-group form-group-query form-group-width-1">
                <dp:fielderror accesskey="errmsg" onlyone="true" />
                <label for="subcontractName"><s:text name="pm.subcontract.subcontractName" /></label>
                <s:textfield name="queryParams.subcontractName" id="subcontractName"
                    placeholder="支持模糊搜索" cssStyle="width:163px" cssClass="form-control" />
            </div>
            <div class="form-group form-group-query form-group-width-1">
                <dp:fielderror accesskey="errmsg" onlyone="true" />
                <label for="contractNos">&nbsp;&nbsp;&nbsp;<s:text name="pm.subcontract.contractNos" /></label>
                <s:textfield name="queryParams.contractNos" id="contractNos"
                     cssStyle="width:163px" cssClass="form-control" placeholder="合同号/转包合同号" />
            </div>
            <%-- <s:if test="user.isHasRole(1) || user.isHasRole(10) || user.isHasRole(13) || user.isHasRole(16)"> --%>
            <div class="form-group form-group-query form-group-width-1">
                <dp:fielderror accesskey="errmsg" onlyone="true" />
                <label for="purchId">采购订单号</label>
                <s:textfield name="queryParams.purchId" id="purchId"
                     cssStyle="width:163px" cssClass="form-control" placeholder="采购订单号" />
            </div>
            <%-- </s:if> --%>
            <div class="form-group form-group-query form-group-width-1">
                <dp:fielderror accesskey="errmsg" onlyone="true" />
                <label for="subcontractOrgId"><s:text name="pm.project.company" /></label>
                <s:select id="subcontractOrgId" list="returnParams.compList" name="queryParams.orgId" cssClass="form-control " listKey="id" listValueKey="abbr" headerKey="" headerValue="--请选择--"/>
            </div>
             
            <div class="form-group form-group-query form-group-width-1">
                <button class="btn btn-default btn-sm" style="margin-left: 100px;" id="noticeQuery"> <span
                    class="glyphicon glyphicon-search"></span> <s:text name="sys.query"></s:text>
                </button>
            </div>
            <br/>
            <br/>
            <br/>
        </s:form>
        <display:table id="subcontractTaskList"
            name="subcontractTaskList" pagesize="${subcontractTaskList.size()}" 
            size="${subcontractTaskList.size()}" sort="external" export="false"
            decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-striped"
            partialList="true">
            <display:column property="purchId" title="采购订单号"></display:column>
            <display:column property="subcontractNo" titleKey="pm.subcontract.subcontractNo"></display:column>
            <display:column property="subcontractName" titleKey="pm.subcontract.subcontractName"></display:column>
            <display:column property="contractNos" titleKey="pm.subcontract.contractNos" decorator="com.dp.plat.decorators.ContractNoList "></display:column>
            <display:column property="officeName" titleKey="pm.subcontract.officeName"></display:column>
            <display:column property="profitDepName" titleKey="pm.subcontract.profitDep"></display:column>
            <%-- <display:column property="processName" titleKey="pm.subcontract.processName"></display:column> --%>
            <display:column property="taskName" titleKey="pm.subcontract.taskName"></display:column>
            <display:column property="assigneeName" titleKey="workflow.transactor" class="nowrap"></display:column>
            <%-- <display:column property="paymentStatus" titleKey="pm.subcontract.state" class="nowrap"></display:column> --%>
            <display:column property="compAbbr" titleKey="pm.project.company"></display:column>
            <display:column property="createTime" titleKey="fnd.baisc.data.createTime" headerClass="timeTd" format="{0,date,yyyy-MM-dd HH:mm:ss}"></display:column>
            <display:column property="subcontractWsOperator" titleKey="display.operate" class="nowrap"></display:column>
        </display:table>
    </s:if>
    </div>
</body>
</html>