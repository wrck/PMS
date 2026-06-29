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
        
        showNextCallbackTips();
        
        $("button:not([type]),button[type='submit']").click(function() {
            $(this).bootstrapBtn("loading");
        })
    });
    function updateProject(obj){
        window.open("${namespace}/ProjectModify.action?project.paramId="+obj + "&result=315");
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
    function getFormParams(customerName, params, formatToJson) {
    	var params = params || {};
        if (!(customerName === null)) {
            customerName = customerName || "";
            params["projectWarrantyCallback.customerNameNotFuzzy"] = customerName;
        }
        var paramArray = $("#mainForm").serializeArray() || [];
        for (var name in params) {
            var value = params[name];
            var index = 0;
            var length = paramArray.length;
            for (index = 0; index < length; index++) {
                var item = paramArray[index];
                var k = item.name;
                if (k == name) {
                    item.value = value;
                    break;
                }/*  else if (item.value == "") {
                    paramArray[index] = undefined;
                } */
            }
            if (index == length) {
                paramArray.push({name: name, value: value});
            }
        }
        return paramArray;
    }
    function showNextCallbackTips() {
    	var storeKey = location.pathname + "_tipDayDiff";
    	var dayDiff = Number(localStorage.getItem(storeKey)) || 7;
        $("#tipDayDiff").val(dayDiff);
        
    	$("#tipDayDiff").off("change");
        $("#tipDayDiff").change(function() {
        	localStorage.setItem(storeKey, $(this).val());
        	showNextCallbackTips();
        })
        var currentDate = new Date();
        var startDate = new Date();
        var endDate = new Date();
        startDate = new Date(startDate.setDate(currentDate.getDay() - 1 - dayDiff ));
        endDate = new Date(endDate.setDate(currentDate.getDay() - 1 + dayDiff ));

        endDate = endDate.format('yyyy-MM-dd');
        startDate = startDate.format('yyyy-MM-dd');
        console.log(startDate, endDate);
        var queryStr = `{
            'projectWarrantyCallback.nextCallbackTimeStart': '\${startDate}',
            'projectWarrantyCallback.nextCallbackTimeEnd': '\${endDate}'
        }`.replace(/\s/g, '');
        var query = getFormParams(null, eval(`(\${queryStr})`));
        $.ajax({
            url:'${namespace}/s/warrantyCallbackAjax_projectWarranty.action?result=queryCount',
            type:'post',
            data: query,
            success: function(data) {
                var result = data.result;
                $("#nextCallbackTips").html(`<a href="javascript:void(0)" onclick="showProjectWarranty(null, \${queryStr})">下次回访日期\${startDate}~\${endDate}，共\${result}个项目</a>`);
            }
        });
    }
    function showProjectWarranty(customerName, params) {
    	var search = $.param(getFormParams(customerName, params));
    	popWindow("module/sub/warrantyCallback_projectWarranty.action?" + search, "95vw", 600, "维保回访", "CustomerProject", true);
    }
</script>
</head>
<body>
    <!-- 查询 -->
    <s:form id="mainForm" name="mainForm" cssClass="form-inline" action="%{namespace}/warrantyCallback_customerProject.action">
        <s:hidden name="displayParam.pagesize" value="50"></s:hidden>
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
            <label for="salerSearch"><s:text name="销售人员" /></label>
            <s:textfield name="projectWarrantyCallback.salerSearch" id="salerSearch"
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
            <s:select list="#{-1: '未回访', 1: '有', 0: '无', 2: '待定', 3: '未接听'}" name="projectWarrantyCallback.renewalIntentionInt" id="renewalIntention"
                cssClass="form-control" headerKey="" headerValue="--请选择--" cssStyle="width:163px;"></s:select>  
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="phoneAnswerState"><s:text name="pm.project.warrantyCallback.phoneAnswerState" /></label>
            <s:select name="projectWarrantyCallback.customStrInfo.phoneAnswerState" id="phoneAnswerState"
                listKey="basicDataId" cssClass="form-control" headerKey=""
                headerValue="--请选择--" cssStyle="width:163px;"
                listValue="basicDataName" list="%{cbForm.phoneAnswerStates}"
                theme="simple" />
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="hasRenewal"><s:text name="pm.project.warrantyCallback.hasRenewal" /></label>
            <s:select list="#{1: '有', 0: '无'}" name="projectWarrantyCallback.hasRenewal" id="hasRenewal"
                cssClass="form-control" headerKey="" headerValue="--请选择--" cssStyle="width:163px;"></s:select>  
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="hasLiscense"><s:text name="pm.project.warrantyCallback.hasLiscense" /></label>
            <s:select list="#{1: '有', 0: '无'}" name="projectWarrantyCallback.hasLiscense" id="hasLiscense"
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
        <span>客户项目统计表</span>
    </div>
    <div>
	    <s:if test="%{projectWarrantyCallback.hasPower == true}">
	        <button onclick="javascript:popWindow('module/sub/warrantyCallback_projectWarranty.action', '95vw', 650,'<s:text name="sys.project.warrantyCallback.management"></s:text>', 'ProjectWarranty', true);" value="pmAddPrjWarrantyCallbackButton" type="button" rel="noreferrer" class="btn btn-default" style="margin-right:4px;margin-bottom:1rem;">
	            <span class="glyphicon glyphicon-list" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;项目列表</span>
	        </button>
	        <button onclick="javascript:popWindow('module/sub/warrantyCallback_projectWarrantyCallback.action', '95vw', 650,'<s:text name="sys.project.warrantyCallback.management"></s:text>', 'ProjectWarrantyCallback', true);" value="pmAddPrjWarrantyCallbackButton" type="button" rel="noreferrer" class="btn btn-default" style="margin-right:4px;margin-bottom:1rem;">
	            <span class="glyphicon glyphicon-list" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;回访记录</span>
	        </button>
            <div style="display:inline-block;">
                <span>提示间隔<input class="form-control" id='tipDayDiff' placeholder="7" style="display: inline;width: 3em;height: 2em;padding:0;text-align:center;"/>天：</span>
                <span id="nextCallbackTips"></span>
            </div>
	    </s:if>
    </div>
    <div>
        <!-- 分页，项目列表 -->
        <display:table id="warrantyCallbackList" class="table table-striped table-title-nowrap"
            name="warrantyCallbackMapList" pagesize="${displayParam.pagesize}" 
            size="${displayParam.totalcount}" sort="external" export="true"  requestURI="${namespace}/warrantyCallback_customerProject.action" 
            decorator="com.dp.plat.warrantyCallback.decorators.WarrantyCallbackDecorator"
            partialList="true">
            <display:column property="finalCustomerName" titleKey="pm.project.finalCustomerName"></display:column>
            <display:column property="officeNames" titleKey="pm.project.officeName" style="word-break: keep-all;"></display:column>
            <display:column title="项目数量" media="html" headerClass="nowrap">
            	<a href="javascript:void(0)" onclick="showProjectWarranty('${warrantyCallbackList.finalCustomerName}')">${warrantyCallbackList.projectCount}</a>
            </display:column>
            <display:column title="已回访项目数量" media="html" headerClass="nowrap">
            	<a href="javascript:void(0)" onclick="showProjectWarranty('${warrantyCallbackList.finalCustomerName}', {'projectWarrantyCallback.hasCallback': 1})">${warrantyCallbackList.callbackProjectCount}</a>
            </display:column>
            <display:column title="有续保意向项目" media="html" headerClass="nowrap">
            	<a href="javascript:void(0)" onclick="showProjectWarranty('${warrantyCallbackList.finalCustomerName}', {'projectWarrantyCallback.renewalIntentionInt': 1})">${warrantyCallbackList.renewalIntentionCount}</a>
            </display:column>
            <display:column title="已回访次数" media="html" headerClass="nowrap">
            	<a href="javascript:void(0)" onclick="showProjectWarranty('${warrantyCallbackList.finalCustomerName}', {'projectWarrantyCallback.hasCallback': 1})">${warrantyCallbackList.callbackProjectCount}</a>
            </display:column>
            <display:column title="已续保项目" media="html" headerClass="nowrap">
            	<a href="javascript:void(0)" onclick="showProjectWarranty('${warrantyCallbackList.finalCustomerName}', {'projectWarrantyCallback.hasRenewal': 1})">${warrantyCallbackList.renewalCount}</a>
            </display:column>
            <display:column title="可续采软件项目" media="html" headerClass="nowrap">
                <a href="javascript:void(0)" onclick="showProjectWarranty('${warrantyCallbackList.finalCustomerName}', {'projectWarrantyCallback.hasLiscense': 1})">${warrantyCallbackList.liscenseCount}</a>
            </display:column>
            
            <display:column property="projectCount" title="项目数量" media="excel"></display:column>
            <display:column property="callbackProjectCount" title="已回访项目数量" media="excel">></display:column>
            <display:column property="renewalIntentionCount" title="有续保意向项目" media="excel">></display:column>
            <display:column property="callbackProjectCount" title="已回访次数" media="excel">></display:column>
            <display:column property="renewalCount" title="已续保项目" media="excel">></display:column>
            <display:column property="liscenseCount" title="可续采软件项目" media="excel">></display:column>
            <display:column property="latestCallbackTime" titleKey="pm.project.warrantyCallback.latestCallbackTime" format="{0,date,yyyy-MM-dd}"></display:column>
            <display:column property="latestNextCallbackTime" titleKey="pm.project.warrantyCallback.latestNextCallbackTime" format="{0,date,yyyy-MM-dd}"></display:column>
            <display:setProperty name="export.excel.filename" value="${project.projectName}项目维保记录.xls" />
        </display:table>
    </div>
</body>
</html>