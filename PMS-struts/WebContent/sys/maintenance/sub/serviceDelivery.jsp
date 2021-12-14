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
        date_picker("serviceDate");
        queryPowerUser();
        $("#createUser").autocomplete({
            source: realnameArr
        });
        var categoryWithSubMap = [];
        var subCategory = "${projectMaintenance.subCategory}";
        try {
            categoryWithSubMap = "${cbForm.categoryWithSubMap}".replace(/=/g, "':'").replace(/\{/g, "{'").replace(/, /g, "', '").replace(/\}/g, "'}").replace(/\}', '\{/g, "}, {").replace(/':'\[\{/g, "':[{").replace(/\]'\}/g, "]}").replace(/\}\]'/g, "}]").replace(/'/g,'"');
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
        });
        $("#hasReport").change(function() {
        	var hasReport = $(this).val();
        	if (hasReport == "true") {
        		$("#deliverFiles").parents(".form-group-query:first").show();
        	} else {
        		$("#deliverFiles").parents(".form-group-query:first").hide();
        	}
        });
        $("#hasReport").change();
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
    <s:form id="mainForm" name="mainForm" cssClass="form-inline" action="module/sub/maintenance_serviceDelivery.action">
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
            <label for="contractNo">&nbsp;&nbsp;&nbsp;<s:text name="pm.project.contractNo" /></label>
            <s:textfield name="projectMaintenance.contractNo" id="contractNo"
                 cssStyle="width:163px" cssClass="form-control" placeholder="合同号" />
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <label for="compId"><s:text name="pm.project.company" /></label>
            <s:select name="projectMaintenance.compId" id="compId"
                listKey="id" cssClass="form-control" headerKey=""
                headerValue="--请选择--" cssStyle="width:163px"
                listValue="abbr" list="%{companyList}" theme="simple" />
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="officeCode"><s:text name="pm.project.officeName" /></label>
            <s:select name="projectMaintenance.officeCode" id="officeCode"
                listKey="departmentNum" cssClass="form-control" headerKey=""
                headerValue="--请选择--" cssStyle="width:163px"
                listValue="departmentName" list="%{departmentList}" theme="simple" />
        </div>
        <%-- <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="warrantyStatus"><s:text name="pm.project.maintenance.warrantyStatus" /></label>
            <s:select id="warrantyStatus" list="#{-1:'维保内', 0:'部分保内', 1:'维保外'}"
                name="projectMaintenance.queryWarrantyStatus"
                cssClass="form-control" headerValue="--请选择--" headerKey="" cssStyle="width: 163px;" /> 
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="warrantyGrade"><s:text name="pm.project.warrantyGrade" /></label>
            <s:select id="warrantyGrade" list="#{1:'基本维保', 2:'中级维保', 3: '高级维保'}"
                name="projectMaintenance.queryWarrantyGrade"
                cssClass="form-control" headerValue="--请选择--" headerKey="" cssStyle="width: 163px;" /> 
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="wafService"><s:text name="pm.project.wafService" /></label>
            <s:select id="wafService" list="#{1:'策略调优服务'}"
                name="projectMaintenance.queryWafService"
                cssClass="form-control" headerValue="--请选择--" headerKey="" cssStyle="width: 163px;" /> 
        </div> --%>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="hasQuarterDeliveried">服务交付</label>
            <s:select list="#{true: '已完成', false: '未完成'}" id="hasReport" 
                name="projectMaintenance.hasQuarterDeliveried"  
                cssClass="form-control" headerValue="--请选择--" headerKey="" cssStyle="width: 163px;" /> 
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="serviceType">服务类型</label>
            <s:select id="serviceType" list="#{'warrantyGrade': '高级维保服务', 'wafService' : 'WAF策略调优服务'}"
                name="projectMaintenance.serviceType"
                cssClass="form-control" headerValue="--请选择--" headerKey="" cssStyle="width: 163px;" /> 
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <label>服务时间</label>
            <s:textfield id="serviceDate" name="projectMaintenance.serviceDate" cssClass="form-control" style="width: 163px; display: inline-block;" placeholder="服务时间"></s:textfield>
            <s:checkbox id="serviceQuarter" name="projectMaintenance.serviceQuarter" title="服务时间按季度" label="按季度" theme="xhtml" ></s:checkbox>
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
        <!-- 分页，项目列表 -->
        <display:table id="maintenanceList" class="table table-striped"
            name="maintenanceMapList" pagesize="${displayParam.pagesize}" 
            size="${displayParam.totalcount}" sort="external" export="true"  requestURI="module/sub/maintenance_serviceDelivery.action" 
            decorator="com.dp.plat.maintenance.decorators.MaintenanceDecorator"
            partialList="true">
            <display:column property="projectCode" titleKey="pm.project.projectCode" media="excel"></display:column>
            <display:column property="projectNameWithURL" titleKey="pm.project.projectName" media="html"></display:column>
            <display:column property="projectName" titleKey="pm.project.projectName" media="excel"></display:column>
            <display:column property="contractNos" titleKey="pm.project.contractNo" media="excel"></display:column>
            <display:column property="contractNos" titleKey="pm.project.contractNo" decorator="com.dp.plat.decorators.ContractNoList" media="html"></display:column>
            <display:column property="officeName" titleKey="pm.project.officeName" class="nowrap"></display:column>
            <display:column property="serviceName" title="服务类型" class="nowrap"></display:column>
            <display:column title="服务季度">${projectMaintenance.serviceDateQuarter}</display:column>
            <%-- <display:column title="服务交付" class="nowrap">
                ${maintenanceList.hasQuarterDeliveried != null ? (maintenanceList.hasQuarterDeliveried == 1 ? "已完成" : "未完成") : (maintenanceList.quarterCount > 0 ? "已完成" : "未完成")}
            </display:column> --%>
            <display:column title="服务交付" class="nowrap">${maintenanceList.hasQuarterDeliveried == 1 ? "已完成" : "未完成"}</display:column>
            <display:column property="quarterCount" title="上传附件次数" media="excel"></display:column>
            <display:column property="serviceCount" title="已完成次数"></display:column>
            <display:column property="remainedCount" title="未完成次数"></display:column>
            <display:column property="serviceStartDate" title="开始日期" format="{0,date,yyyy-MM-dd}" class="nowrap" ></display:column>
            <display:column property="serviceEndDate" title="截止日期" format="{0,date,yyyy-MM-dd}" class="nowrap"></display:column>
           
            <display:column property="serviceManager" titleKey="pm.project.serviceManager" class="nowrap"></display:column>
            <display:column titleKey="pm.project.programManager" class="nowrap" media="html">${maintenanceList.programManagerA}${maintenanceList.programManagerB != null ? "<br>": ""}${maintenanceList.programManagerB}</display:column>
            <display:column property="programManagerA" titleKey="pm.project.programManagerA" media="excel"></display:column>
            <display:column property="programManagerB" titleKey="pm.project.programManagerB" media="excel"></display:column>
            
            <display:column property="marketName" titleKey="pm.project.marketName" media="excel"></display:column>
            <display:column property="systemName" titleKey="pm.project.systemName" media="excel"></display:column>
            <display:column property="expendName" titleKey="pm.project.expendName" media="excel"></display:column>
            <display:column property="industryName" titleKey="pm.project.industryName" media="excel"></display:column>
            <display:column property="finalCustomerName" titleKey="pm.project.finalCustomerName" ></display:column>
            
            <display:column property="warrantyStatusName" titleKey="pm.project.maintenance.warrantyStatus" media="excel"></display:column>
            <display:column property="warrantyGradeName" titleKey="pm.project.warrantyGrade" media="excel"></display:column>
            <display:column property="wafServiceName" titleKey="pm.project.wafService" media="excel"></display:column>
            <display:column property="warrantyStatusDesc" title="维保合同信息" media="excel"></display:column>
            <display:column property="warrantyGradeDesc" title="维保级别信息" media="excel"></display:column>
            <display:column property="warrantyServiceDesc" title="其他服务信息" media="excel"></display:column>
            
            <display:setProperty name="export.excel.filename" value="${projectMaintenance.serviceDateQuarter}服务交付记录.xls" />
        </display:table>
    </div>
</body>
</html>