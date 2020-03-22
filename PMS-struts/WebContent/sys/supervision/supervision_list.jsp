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
<meta name="function" content="<s:text name='sys.project.supervision.management' />">
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
    });
    function updateProject(obj){
        window.open("module/ProjectModify.action?project.paramId="+obj + "&result=314");
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
    <s:form id="mainForm" name="mainForm" cssClass="form-inline" action="module/supervision.action">
        <s:hidden name="displayParam.pagesize" value="50"></s:hidden>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="projectName"><s:text name="pm.project.projectName" /></label>
            <s:textfield name="projectSupervision.projectName" id="projectName"
                placeholder="支持模糊搜索" cssStyle="width:163px" cssClass="form-control" />
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="projectCode">&nbsp;&nbsp;&nbsp;<s:text name="pm.project.projectCode" /></label>
            <s:textfield name="projectSupervision.projectCode" id="projectCode"
                 cssStyle="width:163px" cssClass="form-control" placeholder="项目编码" />
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="channel"><s:text name="pm.project.supervision.channel" /></label>
            <s:textfield name="projectSupervision.channel" id="channel"
                placeholder="支持模糊搜索" cssStyle="width:163px" cssClass="form-control" />
        </div>
        <div class="form-group form-group-query">
            <label for="createUser" style="width: 90px;"><s:text name="sys.create.by"/></label>
            <s:textfield id="createUser" placeholder="操作用户" cssClass="form-control" name="projectSupervision.createUser"
                cssStyle="width: 180px;display: inline-block;" onfocus="fillPwoerUser(this)" onblur="fillPwoerUser(this)" />
            <s:textfield name="projectSupervision.createBy" type="hidden" id="createBy"></s:textfield>
        </div>
        <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="officeCode"><s:text name="pm.project.officeName"/></label>
            <s:select name="projectSupervision.officeCode" id="officeCode"  cssClass="form-control" cssStyle="width:163px;"
                list="departmentList" listKey="departmentNum" listValue="departmentName"
                headerKey="" headerValue="--请选择--"></s:select>  
        </div>
        <%-- <div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="subcontractType"><s:text name="pm.project.supervision.type" /></label>
            <s:select list="supervisionTypeList" name="projectSupervision.type" id="subcontractType"
                cssClass="form-control" listKey="basicDataId" listValue="basicDataName"
                headerKey="" headerValue="--请选择--" cssStyle="width:163px;"></s:select>  
        </div> --%>
        <div class="form-group form-group-query form-group-width-1">
            <label><s:text name="pm.project.supervision.processTime"/></label>
            <s:textfield id="processStartTime" name="projectSupervision.processStartTime" cssClass="form-control" style="width: 100px; display: inline-block;" placeholder="开始时间"></s:textfield>
            <s:textfield id="processEndTime" name="projectSupervision.processEndTime" cssClass="form-control" style="width: 100px; display: inline-block;" placeholder="结束时间"></s:textfield>
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
        <s:text name="pm.project.supervision.list"></s:text>
    </div>
    <div>
        <!-- 分页，项目列表 -->
        <display:table id="supervisionList" class="table table-striped"
            name="supervisionMapList" pagesize="${displayParam.pagesize}" 
            size="${displayParam.totalcount}" sort="external" export="true"  requestURI="module/supervision.action" 
            decorator="com.dp.plat.supervision.decorators.SupervisionDecorator"
            partialList="true">
            <display:column property="projectCode" class="nowrap" titleKey="pm.project.projectCode"></display:column>
            <display:column property="projectNameWithURL" titleKey="pm.project.projectName" media="html"></display:column>
            <display:column property="projectName" titleKey="pm.project.projectName" media="excel"></display:column>
            <display:column property="officeName" class="nowrap" titleKey="pm.project.officeName"></display:column>
            <display:column property="channel" titleKey="pm.project.supervision.channel"></display:column>
            <display:column property="createUser" class="nowrap" titleKey="sys.create.by"></display:column>
            <%-- <display:column property="programManagerA" class="nowrap" titleKey="pm.project.programManagerA"></display:column>
            <display:column property="programManagerB" class="nowrap" titleKey="pm.project.programManagerB"></display:column> --%>
            <display:column property="processTime" class="nowrap" titleKey="pm.project.supervision.processTime" format="{0,date,yyyy-MM-dd}"></display:column>
            <display:column property="expendSupervisionQuesResult" title="${projectSupervision.questionColumns.tableQuestionHeader}" headerScope="splitCell=true"></display:column>
            <%-- <display:column titleKey="pm.project.supervision.state">${supervisionList.state == true ? '有' : '无'}</display:column> --%>
            <%-- <display:column property="typeName" class="nowrap" titleKey="pm.project.supervision.type"></display:column> --%>
            <%-- <display:column property="expendDeliverFilesURL" title="附件" media="html"></display:column> --%>
            
            <display:column property="expendDeliverFiles" title="督查表" media="excel"></display:column>
            <display:column property="createTime" class="nowrap" titleKey="sys.create.time" format="{0,date,yyyy-MM-dd HH:mm}" media="excel"></display:column>
            <display:setProperty name="export.excel.filename" value="${project.projectName}督查记录.xls" />
        </display:table>
    </div>
</body>
</html>