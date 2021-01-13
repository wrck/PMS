<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.context.UserContext"%>
<%@page import="com.dp.plat.context.SpringContext"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.powermanage' />">
<meta name="function" content="<s:text name='pm.presales.flow' />">
<style type="text/css">
legend {
	font: 12px/24px "微软雅黑"
}
.input-width{
	width: 
}
fieldset:not(:first-child) {
    margin-bottom: 20px;
}
fieldset .table-condensed {
    margin-bottom: 0;
}
fieldset .table>thead>tr>th {
    font-weight: normal;
    border-bottom: 0;
}
</style>
<script type="text/javascript">
$(function(){
	queryallsysuser();
	$("#serviceManager").autocomplete({
		source: realnameArr2,
	});
	queryprogramuser();
	$("#projectManager").autocomplete({
		source: realnameArr3,
	});
	
	$("#submitBtn").click(function(){
		$("#result").val(1);
		var sm = document.getElementById("sm_hide").value;
		var pm = document.getElementById("pm_hide").value;
		var type = $("#projectType").val();
		if(pm != ''){
			$("#result").val(2);
		}
		if(sm == ''){
			alert("请填写项目服务经理!");
			return false;
		}
		if (type) {
			$("#projectType_hide").val(type);
		} else {
			alert("请选择项目类型!");
            return false;
		}
		if(confirm("请确认是否开始项目！")){
			$("#applyForm").submit();
		}else{
			return false;
		}
		
	});
	
	$("#closedBtn").click(function(){
		$("#result").val(-1);
		if(confirm("请确认是否闭环项目！")){
			$("#applyForm").submit();
		}else{
			return false;
		}
	});
	
	/* ajaxLoadInfo("shipmentInfo");
    ajaxLoadInfo("lend2RmaInfo", function() {
        var nth = 0;
        var trLen = $("#lend2RmaTable tbody tr").removeClass("even odd").not(".even, .odd").length;
        while($("#lend2RmaTable tbody tr").not(".even, .odd").length > 0) {
            var prevClass = "." + $("#lend2RmaTable tbody tr").not(".even, .odd").children("td[class^='pc']:first").prop("class");
            $(prevClass).parent().addClass(nth++ % 2 ? "even" : "odd");
        }
    });
    ajaxLoadInfo("lend2SaleInfo"); */
});
var realnameArr2=new Array();
var usernameArr2=new Array();

var realnameArr3 = new Array();
var usernameArr3  = new Array();

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

function fillsm(){
	var obj=document.getElementById("serviceManager");
	if(obj.value==""){
		document.getElementById("sm_hide").value="";
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
			document.getElementById("sm_hide").value=usernameArr2[i];
		}
	}
}

function fillpm(){
	var obj=document.getElementById("projectManager");
	if(obj.value==""){
		document.getElementById("pm_hide").value="";
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
			document.getElementById("pm_hide").value=usernameArr3[i];
		}
	}
}

/* function ajaxLoadInfo(type, callback) {
    if (!type) {
        return false;
    }
    if ($("#" + type).length == 0) {
        $("#product").after("<div id='wrapper-" + type + "'></div>");
    }
    var presalesCode = $("input[type='hidden'][name='presales.presalesCode']").val();
    $.ajax({
        url:"module/presales_" + type + ".action",
        type:"post",
        dataTpe:"html",
        data:{"presalesCode":presalesCode},
        success: function(data) {
            data = data.substring(data.indexOf("<fieldset>"), data.indexOf("</fieldset>"));
            if ($("#" + type).length > 0) {
                $("#" + type).replaceWith(data);
            } else if ($("#wrapper-" + type).length > 0) {
                $("#wrapper-" + type).replaceWith(data);
            } else {
                $("#product").after(data);
            }
            if (callback) {
                callback();
            }
        }
    })
} */

</script>
<script type="text/javascript" src="js/presales/initNavBar.js">
var presalesId = ${presales.presalesId};
var officeCode = "${presales.officeCode}";
</script>
</head>
<body>
	<fieldset>
		<legend><b>基本信息</b></legend>
		<table class="table table-bordered table-hover table-striped ">
			<tr>
				<td><s:text name="pm.presales.projectcode"></s:text>:</td>
				<td><s:property value="presales.presalesCode"/><s:hidden name="presales.presalesCode"/></td>
				<td><s:text name="pm.presales.projectname"></s:text>:</td>
				<td>
                    <s:property value="presales.projectName"/>
                    <s:if test="presales.hasTransfer == 1">
                        <span class="text-danger text-unselected">(<s:text name="pm.presales.hasTransfer"/>)</span>
                    </s:if>
                </td>
			</tr>
			<tr>
				<td><s:text name="pm.presales.marketname"></s:text>:</td>
				<td><s:property value="presales.marketName"/></td>
				<td><s:text name="pm.presales.systemname"></s:text>:</td>
				<td><s:property value="presales.systemName"/></td>
			</tr>
			<tr>
				<td><s:text name="pm.presales.expendName"></s:text>:</td>
				<td><s:property value="presales.expendName"/></td>
				<td><s:text name="pm.presales.industryName"></s:text>:</td>
				<td><s:property value="presales.industryName"/></td>
			</tr>
			<tr>
				<td><s:text name="pm.presales.officeName"></s:text>:</td>
				<td><s:property value="presales.officeName"/></td>
				<td><s:text name="pm.presales.salesman"></s:text>:</td>
				<td><s:property value="presales.salesman"/></td>
			</tr>
			<tr>
				<td><s:text name="pm.presales.productmanager"></s:text>:</td>
				<td><s:property value="presales.productManager"/></td>
				<td><s:text name="pm.presales.salesmanlink"></s:text>:</td>
				<td><s:property value="presales.salesmanLink"/></td>
			</tr>
			<tr>
				<td><s:text name="pm.presales.sm"></s:text>:</td>
				<td>
					<s:textfield name="presales.serviceManagerName" id="serviceManager" 
						cssClass="form-control" cssStyle="width:200px;"
						placeholder="支持模糊搜索" onfocus="fillsm()" 
						onblur="fillsm()"></s:textfield>
				</td>
				<td><s:text name="pm.presales.pm"></s:text>:</td>
				<td>
					<s:textfield name="presales.projectManagerName" id="projectManager" 
						cssClass="form-control" cssStyle="width:200px;"
						placeholder="支持模糊搜索" onfocus="fillpm()"
						onblur="fillpm()"></s:textfield>
				</td>
			</tr>
            <tr>
                <td><s:text name="pm.presales.projectType"></s:text>:</td>
                <td>
                    <s:select list="projectTypeList" name="presales.projectType" id="projectType" listKey="basicDataId" listValue="basicDataName"
                      headerValue="--请选择--" headerKey="" cssClass="form-control" cssStyle="width:200px;"></s:select>
                </td>
                <td></td>
                <td></td>
            </tr>
		</table>
	</fieldset>
	<fieldset>
		<legend><b>流程办理</b></legend>
		<s:form cssClass="form-inline" action="module/presales_apply.action" method="post" name="applyForm">
			<s:hidden name="presales.serviceManager" id="sm_hide"></s:hidden>
			<s:hidden name="presales.projectManager" id="pm_hide"></s:hidden>
            <s:hidden name="presales.projectType" id="projectType_hide"></s:hidden>
			<s:hidden name="presales.presalesId"></s:hidden>
			<s:hidden name="param.taskId" value="%{presales.taskId}"></s:hidden>
			<s:hidden name="param.instId" value="%{presales.instId}"></s:hidden>
			<!-- 审批结果 -->
			<s:hidden name="param.result" id="result"></s:hidden>
			备注信息:
			<s:textarea name="param.message" cssClass="form-control"></s:textarea>
			<br/>
			<br/>
			<br/>
			<button type="submit" class="btn btn-success" id="submitBtn">项目开始</button>
			<button type="submit" class="btn btn-info" id="closedBtn">直接闭环</button>
			<br/>
			<br/>
			<br/>
		</s:form>
	</fieldset>
	<fieldset>
		<legend><b>项目附件</b></legend>
		<table class="table table-condensed table-hover table-striped">
			<thead>
			<tr class="warning">
				<td><s:text name="file.name"></s:text></td>
                <td><s:text name="file.type"></s:text></td>
				<td><s:text name="file.uploadby"></s:text></td>
				<td><s:text name="file.uploadtime"></s:text></td>
			</tr>
			</thead>
			<tbody>
			<s:if test="presales.fileParams.size() == 0">
				<tr>
					<td colspan="8">
						无可以显示的数据
					</td>
				</tr>
			</s:if>
			<s:iterator value="presales.fileParams" var="f" status="s">
				<tr>
					<td>
						<s:if test="#f.path == 0">
							<a href="module/download.action?fileId=<s:property value='#f.id'/>"><s:property value="#f.fileName"/></a>
						</s:if>
                        <s:elseif test="#f.path == 1">
                            <%-- <a href="http://sms.dptech.com/module/DocumentDownloadForPMS.action?docFileName=<s:property value='#f.filePath'/>&presales.presalesId=<s:property value='presales.presalesId'/>">
                             --%>
                            <a href="http://sms.dptech.com/module/DocumentDownloadForPMS.action?id=<s:property value='presales.lendInfoId'/>&projectCode=<s:property value='presales.projectCode'/>&flag=<s:property value='#s.index'/>">
                                <s:property value="#f.fileName"/>
                            </a>
                        </s:elseif>
                        <s:else>
                            <a href="module/DownloadFile.action?downname=<s:property value='#f.fileName'/>&downpath=<s:property value="#f.filePath"/>"><s:property value="#f.fileName"/></a>
                        </s:else>
					</td>
                    <td><s:property value="#f.fileType" default="SMS附件"/></td>
					<td><s:property value="#f.uploadBy"/></td>
					<td><s:date name="#f.uploadTime" format="yyyy-MM-dd HH:mm"></s:date></td>
				</tr>
			</s:iterator>
			</tbody>
		</table>
	</fieldset>
	<fieldset id="product">
		<legend><b>产品配置</b></legend>
		<table class="table table-condensed table-hover table-striped">
			<thead>
			<tr class="warning">
				<td><s:text name="pm.ps.pro.fisrtname"></s:text></td>
				<td><s:text name="pm.ps.pro.typename"></s:text></td>
				<td><s:text name="pm.ps.pro.itemcode"></s:text></td>
				<td><s:text name="pm.ps.pro.itemmodel"></s:text></td>
				<td><s:text name="pm.ps.pro.itemdesc"></s:text></td>
				<td><s:text name="pm.ps.pro.num"></s:text></td>
                <%-- <td><s:text name="pm.ps.pro.transferNum"></s:text></td>
                <td><s:text name="pm.ps.pro.hexiaoNum"></s:text></td>
                <td><s:text name="pm.ps.pro.weihexiaoNum"></s:text></td> --%>
				<td width="100px;"><s:text name="pm.ps.pro.remark"></s:text></td>
			</tr>
			</thead>
			<tbody>
			<s:if test="productList.size()== 0">
				<tr>
					<td colspan="8">
						无可以显示的数据
					</td>
				</tr>
			</s:if>
			<s:iterator value="productList" var="p">
				<tr>
					<td><s:property value="#p.productFirstName"/></td>
					<td><s:property value="#p.productTypeName"/></td>
					<td><s:property value="#p.itemCode"/></td>
					<td><s:property value="#p.itemModel"/></td>
					<td><s:property value="#p.itemDesc"/></td>
					<td><s:property value="#p.productNum"/></td>
                    <%-- <td><s:property value="#p.transferNum"/></td>
                    <td><s:property value="#p.hexiaoNum"/></td>
                    <td><s:property value="#p.productNum - #p.hexiaoNum"/></td> --%>
					<td><s:property value="#p.remark"/></td>
				</tr>	
			</s:iterator>
			</tbody>
		</table>
	</fieldset>
	<s:if test="commentList.size()!= 0">
	<fieldset>
		<legend><b>项目流程记录</b></legend>
		<table class="table table-condensed table-hover table-striped">
			<thead>
			<tr class="warning">
				<td><s:text name="workflow.transactor"></s:text></td>
				<td><s:text name="workflow.assign.time"></s:text></td>
				<td><s:text name="workflow.assign.result"></s:text></td>
				<td><s:text name="workflow.assign.message"></s:text></td>
			</tr>
			</thead>
			<tbody>
			<s:if test="commentList.size()== 0">
				<tr>
					<td colspan="8">
						无可以显示的数据
					</td>
				</tr>
			</s:if>
			<s:iterator value="commentList" var="c">
				<tr>
					<td><s:property value="#c.assigneeName"/></td>
					<td><s:date name="#c.assigneeTime" format="yyyy-MM-dd HH:mm"></s:date></td>
					<td><s:property value="#c.resultName"/></td>
					<td>
						<s:property value="#c.message"/><br/>
						<s:if test="#c.quesnaireId !=0">
							<a href="javascript:popWindow('module/sub/callback_seeQuesnaire.action?quesnaireId=<s:property value='#c.quesnaireId'/>',880, 600,'查看测评问卷', 'BudgetUpload', true)">查看问卷</a> 
						</s:if>
					</td>
				</tr>	
			</s:iterator>
			</tbody>
		</table>
	</fieldset>
	</s:if>
</body>
</html>
