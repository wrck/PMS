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
<meta name="function" content="<s:text name='sys.project.warrantyCallback.management' />">
<style type="text/css">
#mainForm div lable{
    width: 100px;
}
</style>
<script type="text/javascript">
    $(function(){
        date_picker("warrantyEndTimeStart");
        date_picker("warrantyEndTimeEnd");
        date_picker("processStartTime");
        date_picker("processEndTime");
        date_picker("nextCallbackTimeStart");
        date_picker("nextCallbackTimeEnd");
        /* queryPowerUser();
        $("#createUser").autocomplete({
            source: realnameArr
        }); */
    });
    function updateProject(obj){
    	var namespace = "${namespace}";
    	var url = "${namespace}/ProjectModify.action?project.paramId="+obj + "&result=315";
    	if (namespace.lastIndexOf("/sub") > -1) {
    		popWindow(url, "95vw", 600, "项目信息")
    	} else {
	        window.open(url);
    	}
    }
    /* var usernameArr = [];
    var realnameArr = [];
    function queryPowerUser(){
        $.ajax({
            url:'${namespace}/s/warrantyCallbackAjax_queryPowerUser.action',
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
    } */
</script>
</head>
<body>
    <!-- 查询 -->
    <s:form id="mainForm" name="mainForm" cssClass="form-inline" action="%{namespace}/warrantyCallback_projectWarranty.action">
        <s:if test="%{projectWarrantyCallback.customerNameNotFuzzy != null}">
	        <s:hidden name="projectWarrantyCallback.customerNameNotFuzzy"></s:hidden>
        </s:if>
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
            <s:select list="#{-1: '未回访', 1: '有', 0: '无', 2: '待定'}" name="projectWarrantyCallback.renewalIntentionInt" id="renewalIntention"
                cssClass="form-control" headerKey="" headerValue="--请选择--" cssStyle="width:163px;"></s:select>  
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="hasRenewal"><s:text name="pm.project.warrantyCallback.hasRenewal" /></label>
            <s:select list="#{1: '有', 0: '无'}" name="projectWarrantyCallback.hasRenewal" id="hasRenewal"
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
        </div>
    </s:form>
    <!-- 蓝色箭头，项目列表 -->
    <div class="divHeader div-height">
        <img src="images/right_zhishi.gif" border="0">
        <%-- <span><s:text name="pm.project.warrantyCallback.list"></s:text></span> --%>
        <span>项目维保回访列表</span>
    </div>
    <div>
        <!-- 分页，项目列表 -->
        <display:table id="warrantyCallbackList" class="table table-striped table-title-nowrap"
            name="warrantyCallbackMapList" pagesize="${displayParam.pagesize}" 
            size="${displayParam.totalcount}" sort="external" export="true"  requestURI="${namespace}/warrantyCallback_projectWarranty.action" 
            decorator="com.dp.plat.warrantyCallback.decorators.WarrantyCallbackDecorator"
            partialList="true">
            <display:column property="officeName" class="nowrap" titleKey="pm.project.officeName"></display:column>
            <display:column property="industryName" class="nowrap" titleKey="pm.project.maintenance.industryName"></display:column>
            <display:column property="contractNos" titleKey="pm.project.contractNo" decorator="com.dp.plat.decorators.ContractNoList"></display:column>
            <display:column property="projectCode" class="nowrap" titleKey="pm.project.projectCode" media="excel"></display:column>
            <display:column property="projectNameWithURL" class="longTextTd" titleKey="pm.project.projectName" media="html"></display:column>
            <display:column property="projectName" titleKey="pm.project.projectName" media="excel"></display:column>
            <display:column property="serviceName" class="nowrap" titleKey="pm.project.implement"></display:column>
            <display:column property="agentChannel" headerClass="nowrap" class="longTextTd" titleKey="pm.project.agentChannel"></display:column>
            <display:column property="finalCustomerName" headerClass="nowrap" class="longTextTd" titleKey="pm.project.finalCustomerName"></display:column>
            <display:column property="warrantyEndTime" class="nowrap" title="维保结束日期" format="{0,date,yyyy-MM-dd}"></display:column>
            <display:column property="customer1" class="nowrap" title="客户联系人"></display:column>
            <display:column property="customerContact1" title="客户联系方式"></display:column>
            <display:column property="customer2" class="nowrap" title="客户联系人(SMS)"></display:column>
            <display:column property="customerContact2" title="客户联系方式(SMS)"></display:column>
            <display:column property="latestRenewalIntention" headerClass="nowrap" titleKey="pm.project.warrantyCallback.renewalIntention" decorator="com.dp.plat.warrantyCallback.decorators.RenewalIntentionDecorator"></display:column>
            <%-- <display:column headerClass="nowrap" titleKey="pm.project.warrantyCallback.renewalIntention">
            	${!empty warrantyCallbackList.latestRenewalIntention ? (warrantyCallbackList.latestRenewalIntention ? "有" : "无") : "未回访"}
            </display:column> --%>
            <display:column headerClass="nowrap" property="callbackCount" titleKey="pm.project.warrantyCallback.callbackCount"></display:column>
            <display:column headerClass="nowrap" titleKey="pm.project.warrantyCallback.hasRenewal">
            	${!empty warrantyCallbackList.hasRenewal ? (warrantyCallbackList.hasRenewal == 1 ? "有" : "无") : "无"}
            </display:column>
            <display:column property="latestCallbackTime" class="nowrap" titleKey="pm.project.warrantyCallback.callbackTime" format="{0,date,yyyy-MM-dd}"></display:column>
            <display:column property="latestNextCallbackTime" class="nowrap" titleKey="pm.project.warrantyCallback.nextCallbackTime" format="{0,date,yyyy-MM-dd}"></display:column>
            <display:column property="latestRemark" headerClass="nowrap" titleKey="pm.remark"></display:column>

            <%-- <s:if test="user.isHasRole(10) || user.isHasRole(13) || user.isHasRole(14)"> --%>
            <%-- <display:column property="operateUrl" class="nowrap" title="操作" media="html"></display:column>
	        </s:if> --%>
	        <display:column title="操作">
	            <a class="btn btn-xs btn-info" href='javascript:popWindow("module/sub/warrantyCallback_createProjectWarrantyCallback.action?project.projectId=${warrantyCallbackList.projectId}&projectWarrantyCallback.id=${warrantyCallbackList.id}", "75vw", 600, "维保回访")'>回访</a>
            	${!empty warrantyCallbackList.latestRenewalIntention ? "".format('<a class="btn btn-xs btn-success" href=\'javascript:popWindow("module/sub/warrantyCallback_projectWarrantyCallback.action?projectWarrantyCallback.projectId=%s&projectWarrantyCallback.id=%s", "95vw", 600, "维保回访记录")\'>查看</a>', warrantyCallbackList.projectId, warrantyCallbackList.id != null ? warrantyCallbackList.id : "") : ""}
	        </display:column>
	        <%-- </s:if> --%>
            <display:setProperty name="export.excel.filename" value="${project.projectName}项目维保记录.xls" />
        </display:table>
    </div>
</body>
</html>