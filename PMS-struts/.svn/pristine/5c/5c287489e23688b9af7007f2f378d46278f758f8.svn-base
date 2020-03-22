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
<meta name="function" content="<s:text name='sys.project.maintenance.management' />">
<style type="text/css">
#mainForm div lable{
    width: 100px;
}
</style>
<script type="text/javascript">
    $(function(){
        date_picker("processStartTime");
        date_picker("processEndTime");
        queryPowerUser();
        $("#createUser").autocomplete({
            source: realnameArr
        });
        var categoryWithSubMap = [];
        var subCategory = "${projectMaintenance.subCategory}";
        try {
            categoryWithSubMap = "${cbForm.categoryWithSubMap}".replace(/=/g, "':'").replace(/\{/g, "{'").replace(/, /g, "', '").replace(/\}/g, "'}").replace(/\}', '\{/g, "}, {").replace(/':'\[\{/g, "':[{").replace(/\]'\}/g, "]}").replace(/'/g,'"');
            categoryWithSubMap = JSON.parse(categoryWithSubMap);
        } catch (e) {
            categoryWithSubMap = [];
        }
        function changeCategory(){
            var index = $("#maintenanceCategory option:selected").index();
            console.log(index);
            $("#maintenanceSubCategory").html("<option value=''>请选择</option>");
            if(index > 0 && categoryWithSubMap.length >= index){
                index--;
                var subCategory = categoryWithSubMap[index].children;
                $(subCategory).each(function(){
                    $("#maintenanceSubCategory").append("<option value='"+this.subCategory+"'>"+this.subCategoryName+"</option>");                 
                });
            }
        }
        changeCategory();
        $("#maintenanceSubCategory").val(subCategory);
        $("#maintenanceCategory").on("change", changeCategory);
        
        /* $("#hasReport").on("change", function() {
    		var name = $(this).attr("name");
        	if($(this).val() === "") {
        		$(this).attr("name", name + "_");
        	} else {
        		$(this).attr("name", name.replace("_", ""));
        	}
        }) */
        
        $("#mainForm").submit(function() {
        	var name = $("#hasReport").attr("name");
            if($("#hasReport").val() === "") {
            	$("#hasReport").attr("name", name + "_");
            } else {
            	$("#hasReport").attr("name", name.replace("_", ""));
            }
            return true;
        })
    });
    function updateProject(obj, type){
    	if (type == 10) {
            window.open("module/ProjectModify.action?project.paramId="+obj + "&result=313");
    	} else if (type == 20){
    		// window.open("module/presales_read.action?presales.presalesId="+obj + "&navBarCode=maintenanceInfo");
    		window.open("module/presales_read.action?presales.presalesId="+obj + "#navBarCode=maintenanceInfo");
    	} else {
    		alert("没有相关的项目信息");
    	}
    }
    var usernameArr = [];
    var realnameArr = [];
    function queryPowerUser(){
        $.ajax({
            url:'module/s/supervisionAjax_queryPowerUser.action',
            type:'post',
            dataType:'json',
            success: function(data) {
                try {
                    initPowerUser({allusernameList: JSON.parse(data.message)});
                } catch(e) {
                }
            }
        });
    }
    function initPowerUser(json){
        var userlist = json.allusernameList;
        for(var i = 0;i < userlist.length;i++){
            usernameArr[i] = userlist[i].username;
            realnameArr[i] = userlist[i].username+"-"+userlist[i].realName;
        }
    }
    function fillPwoerUser(obj) {
        if(obj.value==""){
            $(obj).next().val("");
        }
        if(obj.value!=""){
            var i=0;
            for(;i<realnameArr.length;i++){
                if(realnameArr[i]==obj.value){
                    break;
                }
            }
            if(i==realnameArr.length){
                $(obj).next().val("");
                return false;
            } else{
                $(obj).next().val(usernameArr[i]);
            }
        }
    }
</script>
</head>
<body>
    <!-- 查询 -->
    <s:form id="mainForm" name="mainForm" cssClass="form-inline" action="module/maintenance.action">
        <s:hidden name="displayParam.pagesize" value="50"></s:hidden>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="projectName"><s:text name="pm.project.projectName" /></label>
            <s:textfield name="projectMaintenance.projectName" id="projectName"
                placeholder="支持模糊搜索" cssStyle="width:163px" cssClass="form-control" />
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="projectCode">&nbsp;&nbsp;&nbsp;<s:text name="pm.project.projectCode" /></label>
            <s:textfield name="projectMaintenance.projectCode" id="projectCode"
                 cssStyle="width:163px" cssClass="form-control" placeholder="项目编码" />
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="officeCode"><s:text name="pm.project.officeName" /></label>
            <s:select name="projectMaintenance.officeCode" id="officeCode"
                listKey="departmentNum" cssClass="form-control" headerKey=""
                headerValue="--请选择--" cssStyle="width:163px"
                listValue="departmentName" list="%{departmentList}" theme="simple" />
        </div>
        <div class="form-group form-group-query">
            <label for="createUser"><s:text name="pm.project.maintenance.createUser"/></label>
            <s:textfield id="createUser" placeholder='' cssClass="form-control" name="projectMaintenance.createUser"
                cssStyle="width: 163px;display: inline-block;" onfocus="fillPwoerUser(this)" onblur="fillPwoerUser(this)" />
            <s:textfield name="projectMaintenance.createBy" type="hidden" id="createBy"></s:textfield>
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="subcontractType"><s:text name="pm.project.maintenance.type" /></label>
            <s:select list="maintenanceTypeList" name="projectMaintenance.type" id="subcontractType"
                cssClass="form-control" listKey="basicDataId" listValue="basicDataName"
                headerKey="" headerValue="--请选择--" cssStyle="width:163px;"></s:select>  
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="maintenanceCategory"><s:text name="pm.project.maintenance.category"/></label>
            <s:select list="cbForm.categoryWithSubMap" id="maintenanceCategory" 
                name="projectMaintenance.category" listKey="category" listValue="categoryName" 
                cssClass="form-control" headerValue="--请选择--" headerKey="" cssStyle="width: 163px;" />
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="subcontractType"><s:text name="pm.project.maintenance.subCategory" /></label>
            <s:select list="cbForm.categoryWithSubMap" id="maintenanceSubCategory" 
                name="projectMaintenance.subCategory" listKey="category" listValue="categoryName" 
                cssClass="form-control" headerValue="--请选择--" headerKey="" cssStyle="width: 163px;" /> 
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="hasReport"><s:text name="pm.project.maintenance.hasReport" /></label>
            <s:select list="#{true: '有', false: '无'}" id="hasReport" 
                name="projectMaintenance.hasReport"  
                cssClass="form-control" headerValue="--请选择--" headerKey="" cssStyle="width: 163px;" /> 
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <label><s:text name="pm.project.maintenance.processTime"/></label>
            <s:textfield id="processStartTime" name="projectMaintenance.processStartTime" cssClass="form-control" style="width: 100px; display: inline-block;" placeholder="开始时间"></s:textfield>
            <s:textfield id="processEndTime" name="projectMaintenance.processEndTime" cssClass="form-control" style="width: 100px; display: inline-block;" placeholder="结束时间"></s:textfield>
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <button class="btn btn-default btn-sm">
                <span class="glyphicon glyphicon-search"></span> 查询
            </button>
        </div>
        
    </s:form>
    <!-- 蓝色箭头，项目列表 -->
    <div class="divHeader div-height">
        <img src="images/right_zhishi.gif" border="0">
        <s:text name="pm.project.maintenance.list"></s:text>
    </div>
    <div>
    <s:if test="user.isHasRole(11) || user.isHasRole(12)">
        <button onclick="javascript:popWindow('module/sub/maintenance_createProjectMaintenance.action?projectMaintenance.category=nonBusiness', 1000, 650,'<s:text name="sys.project.maintenance.management"></s:text>', 'BudgetUpload', true);" value="pmAddPrjMaintenanceButton" type="button" class="btn btn-default" style="margin-right:4px;">
            <span class="glyphicon glyphicon-plus" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;非业务类录入</span>
        </button>
        <button onclick="javascript:popWindow('module/sub/maintenance_createProjectMaintenance.action?projectMaintenance.projectType=40', 1000, 650,'<s:text name="sys.project.maintenance.management"></s:text>', 'BudgetUpload', true);" value="pmAddPrjMaintenanceButton" type="button" class="btn btn-default" style="margin-right:4px;">
            <span class="glyphicon glyphicon-plus" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;自定义录入</span>
        </button>
        <!-- <a href="module/BatchChangeProjectMember.action" target="_blank" style="margin-bottom:1rem;" class="btn btn-default btn-sm">非业务类录入</a> -->
    </s:if>
    </div>
    <div>
        <!-- 分页，项目列表 -->
        <display:table id="maintenanceList" class="table table-striped"
            name="maintenanceMapList" pagesize="${displayParam.pagesize}" 
            size="${displayParam.totalcount}" sort="external" export="true"  requestURI="module/maintenance.action" 
            decorator="com.dp.plat.decorators.MaintenanceDecorator"
            partialList="true">
            <display:column property="categoryName" titleKey="pm.project.maintenance.category"></display:column>
            <display:column property="subCategoryName" titleKey="pm.project.maintenance.subCategory"></display:column>
            <display:column property="typeName" titleKey="pm.project.maintenance.type" media="excel"></display:column>
            <display:column property="projectCode" titleKey="pm.project.projectCode" media="excel"></display:column>
            <display:column property="projectNameWithURL" titleKey="pm.project.projectName" media="html"></display:column>
            <display:column property="projectName" titleKey="pm.project.projectName" media="excel"></display:column>
            <display:column property="contractNo" titleKey="pm.project.contractNo" media="excel"></display:column>
            <display:column property="officeName" titleKey="pm.project.officeName"></display:column>
            <%-- <display:column property="programManagerA" titleKey="pm.project.programManagerA"></display:column>
            <display:column property="programManagerB" titleKey="pm.project.programManagerB"></display:column>
             --%>
            <display:column property="processDesc" titleKey="pm.project.maintenance.processDesc" maxLength="90" style="max-width: 230px"></display:column>
            <display:column property="processStep" titleKey="pm.project.maintenance.processStep" maxLength="90" style="max-width: 230px"></display:column>
            <display:column property="processTime" titleKey="pm.project.maintenance.processTime" format="{0,date,yyyy-MM-dd}"></display:column>
            <display:column property="transitHour" titleKey="pm.project.maintenance.transitHour" style="width: 52px"></display:column>
            <display:column property="processHour" titleKey="pm.project.maintenance.processHour" style="width: 52px"></display:column>
            <display:column property="createUser" titleKey="pm.project.maintenance.createUser"></display:column>
            <display:column property="itemModel" titleKey="pm.project.maintenance.itemModel" media="excel"></display:column>
            <display:column property="softVersion" titleKey="pm.project.maintenance.softVersion" media="excel"></display:column>
            <display:column property="enabledFeatures" titleKey="pm.project.maintenance.enabledFeatures" media="excel"></display:column>
            <display:column property="expendMaintenanceQuesResult" title="${projectMaintenance.questionColumns.tableQuestionHeader}" headerScope="splitCell=true" media="excel"></display:column>
            <display:column titleKey="pm.project.maintenance.hasReport" media="excel">${maintenanceList.hasReport == true ? '有' : '无'}</display:column>
            <%-- <display:column property="expendDeliverFilesURL" title="附件" media="html"></display:column> --%>
            
            <display:column property="expendDeliverFiles" title="附件" media="excel"></display:column>
            <display:column property="deliverTypes" title="附件类型" media="excel"></display:column>
            <display:column titleKey="pm.project.projectType" media="excel">
                ${maintenanceList.projectType == 10 ? '售后项目' : (maintenanceList.projectType == 20 ? '售前测试' : (maintenanceList.projectType == 40 ? '自定义' : '无'))}
            </display:column>
            <%-- <display:column property="createTime" class="nowrap" titleKey="sys.create.time" format="{0,date,yyyy-MM-dd HH:mm}" media="excel"></display:column> --%>
            <display:column property="operateTime" titleKey="sys.create.time" format="{0,date,yyyy-MM-dd HH:mm}" media="excel"></display:column>
            <display:setProperty name="export.excel.filename" value="${project.projectName}维护记录.xls" />
        </display:table>
    </div>
</body>
</html>