<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<dp:base />
<head>
</head>
<body>
	<s:if test="%{projectWarrantyCallback == null || projectWarrantyCallback.projectId == null}">
		<!-- 查询 -->
	    <s:form id="mainForm" name="mainForm" cssClass="form-inline" action="%{namespace}/warrantyCallback_projectWarrantyCallback.action">
	        <s:hidden name="displayParam.pagesize"></s:hidden>
	        <div class="form-group form-group-query form-group-width-1">
	            <dp:fielderror accesskey="errmsg" onlyone="true" />
	            <label for="projectName"><s:text name="pm.project.projectName" /></label>
	            <s:textfield name="projectWarrantyCallback.projectName" id="projectName"
	                placeholder="支持模糊搜索" cssStyle="width:163px" cssClass="form-control" />
	        </div>
	        <div class="form-group form-group-query form-group-width-1">
	            <dp:fielderror accesskey="errmsg" onlyone="true" />
	            <label for="projectCode"><s:text name="pm.project.projectCode" /></label>
	            <s:textfield name="projectWarrantyCallback.projectCode" id="projectCode"
	                 cssStyle="width:163px" cssClass="form-control" placeholder="项目编码" />
	        </div>
	        <div class="form-group form-group-query form-group-width-1">
	            <dp:fielderror accesskey="errmsg" onlyone="true" />
	            <label for="contractNos"><s:text name="pm.project.contractNo" /></label>
	            <s:textfield name="projectWarrantyCallback.contractNos" id="contractNos"
	                 cssStyle="width:163px" cssClass="form-control" placeholder="合同号" />
	        </div>
	        <div class="form-group form-group-query form-group-width-1">
	            <dp:fielderror accesskey="errmsg" onlyone="true" />
	            <label for="agentChannel"><s:text name="pm.project.warrantyCallback.channel" /></label>
	            <s:textfield name="projectWarrantyCallback.agentChannel" id="agentChannel"
	                placeholder="支持模糊搜索" cssStyle="width:163px" cssClass="form-control" />
	        </div>
	        <div class="form-group form-group-query form-group-width-1">
	            <dp:fielderror accesskey="errmsg" onlyone="true" />
	            <label for="finalCustomerName"><s:text name="pm.project.finalCustomerName" /></label>
	            <s:textfield name="projectWarrantyCallback.finalCustomerName" id="finalCustomerName"
	                placeholder="支持模糊搜索" cssStyle="width:163px" cssClass="form-control" />
	        </div>
	        <div class="form-group form-group-query form-group-width-1">
	            <dp:fielderror accesskey="errmsg" onlyone="true" />
	            <label for="officeCode"><s:text name="pm.project.officeName"/></label>
	            <s:select name="projectWarrantyCallback.officeCode" id="officeCode"  cssClass="form-control" cssStyle="width:163px;"
	                list="departmentList" listKey="departmentNum" listValue="departmentName"
	                headerKey="" headerValue="--请选择--"></s:select>  
	        </div>
	        <div class="form-group form-group-query form-group-width-1">
	            <dp:fielderror accesskey="errmsg" onlyone="true" />
	            <label for="industryName"><s:text name="pm.project.maintenance.industryName" /></label>
	            <s:textfield name="projectWarrantyCallback.industryName" id="industryName"
	                placeholder="支持模糊搜索" cssStyle="width:163px" cssClass="form-control" />
	        </div>
	        <div class="form-group form-group-query form-group-width-1">
	            <dp:fielderror accesskey="errmsg" onlyone="true" />
	            <label for="customerSearch"><s:text name="客户联系人" /></label>
	            <s:textfield name="projectWarrantyCallback.customerSearch" id="customerSearch"
	                placeholder="支持模糊搜索" cssStyle="width:163px" cssClass="form-control" />
	        </div>
	        <div class="form-group form-group-query form-group-width-1">
	            <dp:fielderror accesskey="errmsg" onlyone="true" />
	            <label for="serviceImpl"><s:text name="pm.project.implement" /></label>
	            <s:select name="projectWarrantyCallback.serviceImpl" id="serviceImpl"
					listKey="basicDataId" cssClass="form-control" headerKey=""
					headerValue="--请选择--" cssStyle="width:163px;"
					listValue="basicDataName" list="%{warrantyCallbackTypeList}"
					theme="simple" />
	        </div>
	        <div class="form-group form-group-query form-group-width-1">
	            <dp:fielderror accesskey="errmsg" onlyone="true" />
	            <label for="renewalIntention"><s:text name="pm.project.warrantyCallback.renewalIntention" /></label>
	            <s:select list="#{1: '有', 0: '无', 2: '待定'}" name="projectWarrantyCallback.renewalIntention" id="renewalIntention"
	                cssClass="form-control" headerKey="" headerValue="--请选择--" cssStyle="width:163px;"></s:select>  
	        </div>
	        <div class="form-group form-group-query form-group-width-1">
	            <label>维保结束日期</label>
	            <s:textfield id="warrantyEndTimeStart" name="projectWarrantyCallback.warrantyEndTimeStart" cssClass="form-control" style="width: 100px; display: inline-block;" placeholder="开始时间" autocomplete="off"></s:textfield>
	            <s:textfield id="warrantyEndTimeEnd" name="projectWarrantyCallback.warrantyEndTimeEnd" cssClass="form-control" style="width: 100px; display: inline-block;" placeholder="结束时间" autocomplete="off"></s:textfield>
	        </div>
	        <div class="form-group form-group-query form-group-width-1">
	            <label>回访日期</label>
	            <s:textfield id="processStartTime" name="projectWarrantyCallback.processStartTime" cssClass="form-control" style="width: 100px; display: inline-block;" placeholder="开始时间" autocomplete="off"></s:textfield>
	            <s:textfield id="processEndTime" name="projectWarrantyCallback.processEndTime" cssClass="form-control" style="width: 100px; display: inline-block;" placeholder="结束时间" autocomplete="off"></s:textfield>
	        </div>
	        <div class="form-group form-group-query form-group-width-1">
	            <label>下次回访日期</label>
	            <s:textfield id="nextCallbackTimeStart" name="projectWarrantyCallback.nextCallbackTimeStart" cssClass="form-control" style="width: 100px; display: inline-block;" placeholder="开始时间" autocomplete="off"></s:textfield>
	            <s:textfield id="nextCallbackTimeEnd" name="projectWarrantyCallback.nextCallbackTimeEnd" cssClass="form-control" style="width: 100px; display: inline-block;" placeholder="结束时间" autocomplete="off"></s:textfield>
	        </div>
	        <div class="form-group form-group-query form-group-width-1">
	            <button class="btn btn-default btn-sm">
	                <span class="glyphicon glyphicon-search"></span> 查询
	            </button>
	            <button class="btn btn-default btn-sm" type="reset">
	                <span class="glyphicon glyphicon-reset"></span> 重置
	            </button>
	        </div>
	        
	    </s:form>
	    <!-- 蓝色箭头，项目列表 -->
	    <div class="divHeader div-height">
	        <img src="images/right_zhishi.gif" border="0">
	        <s:text name="pm.project.warrantyCallback.list"></s:text>
	    </div>
	    <div>
		    <s:if test="%{projectWarrantyCallback.projectId == null && projectWarrantyCallback.hasPower == true}">
		        <button onclick="javascript:popWindow('module/sub/warrantyCallback_projectWarranty.action', '95vw', 650,'<s:text name="sys.project.warrantyCallback.management"></s:text>', 'BudgetUpload', true);" value="pmAddPrjWarrantyCallbackButton" type="button" rel="noreferrer" class="btn btn-default" style="margin-right:4px;margin-bottom:1rem;">
		            <span class="glyphicon glyphicon-list" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;项目列表</span>
		        </button>
		    </s:if>
	    </div>
	    
	    <script type="text/javascript">
	    	$(function(){
		        date_picker("warrantyEndTimeStart");
		        date_picker("warrantyEndTimeEnd");
		        date_picker("processStartTime");
		        date_picker("processEndTime");
		        date_picker("nextCallbackTimeStart");
		        date_picker("nextCallbackTimeEnd");
	    	});
	    </script>
    </s:if>
    <s:if test="%{projectWarrantyCallback.projectId != null && projectWarrantyCallback.hasPower == true}">
        <button onclick="javascript:popWindow('module/sub/warrantyCallback_createProjectWarrantyCallback.action?project.projectId=<s:property value='projectWarrantyCallback.projectId'/>&redirect=<s:property value='redirect'/>', 1000, 650,'<s:text name="sys.project.warrantyCallback.management"></s:text>', 'BudgetUpload', true);" value="pmAddPrjWarrantyCallbackButton" type="button" class="btn btn-default" style="margin-right:4px;">
            <span class="glyphicon glyphicon-plus" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;添加项目维保回访记录</span>
        </button>
    </s:if>
    <display:table id="warrantyCallbackList" class="table table-striped table-title-nowrap"
        name="warrantyCallbackMapList" pagesize="${displayParam.pagesize}" 
        size="${displayParam.totalcount}" sort="external" export="true"  requestURI="module/sub/warrantyCallback_projectWarrantyCallback.action" 
        decorator="com.dp.plat.warrantyCallback.decorators.WarrantyCallbackDecorator" excludedParams="displayParam.pagesize"
        partialList="true">
        <s:if test="displayParam != null">
	        <display:column title="序号">${displayParam.totalcount - displayParam.offset - warrantyCallbackList_rowNum + 1}</display:column>
        </s:if>
        <s:else>
	        <display:column title="序号">${warrantyCallbackMapList.size() - warrantyCallbackList_rowNum + 1}</display:column>
        </s:else>
        <display:column property="officeName" class="nowrap" titleKey="pm.project.officeName"></display:column>
        <display:column property="industryName" class="nowrap" titleKey="pm.project.maintenance.industryName"></display:column>
        <display:column property="contractNos" titleKey="pm.project.contractNo" decorator="com.dp.plat.decorators.ContractNoList" media="html"></display:column>
        <display:column property="contractNos" titleKey="pm.project.contractNo" media="excel"></display:column>
        <display:column property="projectCode" class="nowrap" titleKey="pm.project.projectCode" media="excel"></display:column>
        <display:column property="projectNameWithURL" class="longTextTd" titleKey="pm.project.projectName" media="html"></display:column>
        <display:column property="projectName" titleKey="pm.project.projectName" media="excel"></display:column>
        <display:column property="serviceName" class="nowrap" titleKey="pm.project.implement"></display:column>
        <display:column property="agentChannel" class="longTextTd" titleKey="pm.project.agentChannel"></display:column>
        <display:column property="finalCustomerName" class="longTextTd" headerClass="nowrap" titleKey="pm.project.finalCustomerName"></display:column>
        <display:column property="warrantyEndTime" class="nowrap" title="维保结束日期" format="{0,date,yyyy-MM-dd}"></display:column>
        <display:column property="customer1" class="nowrap" title="客户联系人"></display:column>
        <display:column property="customerContact1" title="客户联系方式"></display:column>
        <display:column property="customer2" class="nowrap" title="客户联系人2"></display:column>
        <display:column property="customerContact2" title="客户联系方式2"></display:column>
        <display:column property="renewalIntention" headerClass="nowrap" titleKey="pm.project.warrantyCallback.renewalIntention" decorator="com.dp.plat.warrantyCallback.decorators.RenewalIntentionDecorator"></display:column>
        <%-- <display:column headerClass="nowrap" titleKey="pm.project.warrantyCallback.renewalIntention">
        	${!empty warrantyCallbackList.renewalIntention ? (warrantyCallbackList.renewalIntention ? "有" : "无") : "未回访"}
        </display:column> --%>
        <%-- <display:column property="callbackCount" headerClass="nowrap" titleKey="pm.project.warrantyCallback.callbackCount"></display:column> --%>
        <display:column property="callbackTime" class="nowrap" titleKey="pm.project.warrantyCallback.callbackTime" format="{0,date,yyyy-MM-dd}"></display:column>
      	<display:column property="nextCallbackTime" class="nowrap" titleKey="pm.project.warrantyCallback.nextCallbackTime" format="{0,date,yyyy-MM-dd}"></display:column>
        <display:column property="remark" titleKey="pm.remark"></display:column>
        
        
        <display:column property="expendWarrantyCallbackQuesResult" headerClass="nowrap" title="${projectWarrantyCallback.questionColumns.tableQuestionHeader}" headerScope="splitCell=true"></display:column>
        <%-- <display:column titleKey="pm.project.warrantyCallback.state">${warrantyCallbackList.state == true ? '有' : '无'}</display:column> --%>
        <%-- <display:column property="typeName" class="nowrap" titleKey="pm.project.warrantyCallback.type"></display:column> --%>
        <%-- <display:column property="expendDeliverFilesURL" title="附件" media="html"></display:column> --%>
        
        <display:column property="expendDeliverFiles" title="项目回访表" media="excel"></display:column>
        <display:column property="createTime" class="nowrap" titleKey="sys.create.time" format="{0,date,yyyy-MM-dd HH:mm}" media="excel"></display:column>
        <display:setProperty name="export.excel.filename" value="${project.projectName}项目回访记录.xls" />
    </display:table>
    <div class="backTop">
        <i class='glyphicon glyphicon-arrow-up'></i>
    </div>
    <div class="rollBottom">
        <i class='glyphicon glyphicon-arrow-down'></i>
    </div>
    <script type="text/javascript">
	    function updateProject(obj){
	    	var namespace = "module/sub";
	    	var url = "module/sub/ProjectModify.action?project.paramId="+obj + "&result=315";
	    	if (namespace.lastIndexOf("/sub") > -1) {
	    		popWindow(url, "95vw", 600, "项目信息")
	    	} else {
		        window.open(url);
	    	}
	    }
        function openQuesTask(projectWarrantyCallbackId) {
        	popWindow('module/sub/warrantyCallback_createProjectWarrantyCallback.action?project.projectId=<s:property value="projectWarrantyCallback.projectId"/>&projectWarrantyCallback.id=' + projectWarrantyCallbackId +'&redirect=<s:property value="redirect"/>', 1000, 650,'<s:text name="sys.project.warrantyCallback.management"></s:text>', 'BudgetUpload', true);
    	}
        function deleteWarrantyCallback(projectWarrantyCallbackId) {
        	$.ajax({
        		url: 'ajax/projectWarrantyCallbackAjax_deleteProjectWarrantyCallback.action',
        		data: {"projectWarrantyCallback.id" : projectWarrantyCallbackId},
        		success: function(data) {
        			data = data || {};
        			alert(data.message || (data.result == "success" ? "处理成功" : "处理失败"));
    				if(data.result == "success") {
    					window.location.reload();
        			}
        		}
        	})
        }
    </script>
</body>
</html>