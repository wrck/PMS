<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<dp:base />
<head>
<style>
	.chk_style{
		display: none;
	}
	.chk_success + label{
		background-color:#98DCA6!important;
	}
	.chk_error + label{
		background-color:#F1B8B8!important;
	}
	.chk_style + label {
		background-color: #DEDEDE;
		border: 1px solid #C1CACA;
		box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05), inset 0px -15px 10px -12px rgba(0, 0, 0, 0.05);
		padding: 5.5px;
    	border-radius: 3px;
		display: inline-block;
		position: relative;
	    margin-top: 2px;
	    margin-right: 3px;
	  	vertical-align: middle;
	}
	.chk_style + label:active {
		box-shadow: 0 1px 2px rgba(0,0,0,0.05), inset 0px 1px 3px rgba(0,0,0,0.1);
	}
	
	.chk_style:checked + label {
		background-color: #ECF2F7;
		border: 1px solid #92A1AC;
		box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05), inset 0px -15px 10px -12px rgba(0, 0, 0, 0.05), inset 15px 10px -12px rgba(255, 255, 255, 0.1);
		color: #243441;
	}
	
	.chk_style:checked + label:after {
		content: '\2714';
		position: absolute;
		top: -3px;
		left: 0px;
		/* color: #758794; */
		width: 100%;
		text-align: center;
		/* font-size: 1.4em;
		padding: 1px 0 0 0; */
		vertical-align: text-top;
	}
</style>
<script type="text/javascript">
function checkAll(){
	var ischecked = $("#checkHeader").is(":checked");
	$("input[type='checkbox']").not(".chk_style").each(function(){
		if(ischecked){
			$(this).prop("checked","checked");
		}else{
			$(this).removeAttr("checked");
		}
	});
}

function checkAllSuccess(){
	var ischecked = $("#checkbox_success").is(":checked");
	$(".success input[type='checkbox']").each(function(){
		if(ischecked){
			$(this).prop("checked","checked");
		}else{
			$(this).removeAttr("checked");
		}
	});
}

function checkAllError(){
	var ischecked = $("#checkbox_error").is(":checked");
	$(".danger input[type='checkbox']").each(function(){
		if(ischecked){
			$(this).prop("checked","checked");
		}else{
			$(this).removeAttr("checked");
		}
	});
}

var realnameArr=new Array();
var usernameArr=new Array();
function queryallsysuser(){
	$.ajax({
		url:'${pageContext.request.contextPath }/queryalluser.action',
		type:'post',
		dataType:'json',
		data:{},
		success:querysysuser2
	});
}
function querysysuser2(json){
	var userlist = json.allusernameList;
	for(var i = 0;i < userlist.length;i++){
		usernameArr[i] = userlist[i].username;
		realnameArr[i] = userlist[i].username+"-"+userlist[i].realName;
	}
}

$(function(){
	//增加模糊搜索功能
	queryallsysuser();
	$("#assignee").autocomplete({
		source: realnameArr
	});
	
	//显示第一个选项卡内容
	var firstTab = "<s:property value='navTabList[0].basicDataId'/>";
	$("."+firstTab).removeClass("hideDiv");
	checkSoftwares();
	
	var tabIndex = "<s:property value='probRestore.restoreStatus'/>";
	switch(parseInt(tabIndex)){
		case 30 :
			clickNavLi(0,'closing');
			break;
		case 20 :
			clickNavLi(1,'backing');
			break;
		case 31 :
			clickNavLi(2,'closed');
			break;
		default:
			clickNavLi(0,'closing');
	};
	
	$("#retrieve").click(function(){
		if(!$(".retrieve").is(":visible")){
			$(".retrieve").show();
			$("#retrieve").text("取消");
		}else{
			$(".retrieve").hide();
			$("#retrieve").text("检索");
		}
		return false;
	});
	
	$("#submit").click(function(){
		if(($("#probRestore_restoreStatus").val() == "31") && ($(".probRestoreTask.success input:checked").length<$("input:checked").not("#checkHeader,.chk_style").length)){
			if(!confirm("已更新影响版本的设备数量小于勾选的设备数量，确认要闭环这些子任务？"))
				return false;
		}
		restoreids = '';
		$("input:checked").not("#checkHeader,.chk_style").each(function(){
			restoreids += $(this).val();
			restoreids += ',';
		});
		/* remark = $("textarea[name='probRestore.restoreRemark']").eq(0).val().trim();
		if(remark == ''){
			alert("请填写备注说明！");
			return false;
		} */
		if(restoreids!=''){
			restoreids += 0;
			$("#restoreIds").val(restoreids);
			return true;
		}else{
			alert("请至少选择一个数据项进行操作");
		}
		return false;
	});
	
	//***选项卡2 返回的数据********************************************************************************************************
	
	$("#retrieve_backing").click(function(){
		if(!$(".retrieve_backing").is(":visible")){
			$(".retrieve_backing").show();
			$("#retrieve_backing").text("取消");
		}else{
			$(".retrieve_backing").hide();
			$("#retrieve_backing").text("检索");
		}
		return false;
	});
	
	$("#submit_backing").click(function(){
		restoreids = '';
		$("input:checked").not("#checkHeader").each(function(){
			restoreids += $(this).val();
			restoreids += ',';
		});
		remark = $("textarea[name='probRestore.restoreRemark']").eq(1).val().trim();
		if(remark == ''){
			alert("请填写备注说明！");
			return false;
		}
		if(restoreids!=''){
			restoreids += 0;
			$("#restoreIds_backing").val(restoreids);
			return true;
		}else{
			alert("请至少选择一个数据项进行操作");
		}
		return false;
	});
	
	
});

function fillAssignee(){
	var obj=document.getElementById("assignee");
	if(obj.value==""){
		document.getElementById("assigneeHidden").value="";
	}
	if(obj.value!=""){
		var i=0;
		for(;i<realnameArr.length;i++){
			if(realnameArr[i] == obj.value){
				break;
			}
		}
		if(i==realnameArr.length){
			return false;
		} else{
			document.getElementById("assigneeHidden").value=usernameArr[i];
		}
	}
}

function checkSoftwares(){
	var softWares = ${result};
	$(".probRestoreTask").each(function(){
		var latestConp = $(this).find(".latestConp").text().trim();
		var latestCpld = $(this).find(".latestCpld").text().trim();
		var latestBoot = $(this).find(".latestBoot").text().trim();
		var latestPcb = $(this).find(".latestPcb").text().trim();
		var flag = true;
		for(var i in softWares){
			var softWare = softWares[i];
			if((softWare.conp && softWare.conp == latestConp)){
				flag = false;
				break;
			}
			if((softWare.cpld && softWare.cpld == latestCpld)){
				flag = false;
				break;
			}
			if((softWare.boot && softWare.boot == latestBoot)){
				flag = false;
				break;
			}
			if((softWare.pcb && softWare.pcb == latestPcb)){
				flag = false;
				break;
			}
		}
		if(flag){
			$(this).addClass("success");
		}else{
			$(this).addClass("danger");
		}
	});
}

$(document).ready(function(){
	$('textarea').bind('input propertychange blur', function() {
		$(this).css('min-height','48px');
		$(this).css('height','0px');
		$(this).css('height',this.scrollHeight + 'px');
	});
});
</script>
</head>
<body>
	<div>
		<nav class="navbar navbar-default" role="navigation"
			style="margin-top: 20px;">
			<div>
				<ul class="nav navbar-nav">
					<s:iterator value="navTabList" var="nav" status="index">
						<s:if test="%{#index.index == 0}">
							<li name="navli"
								class="active nav<s:property value='#index.index'/>"
								onclick="clickSwitchTab(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>','${pageContext.request.contextPath }/module/sub/manageAllTask.action?probRestore.probId=<s:property value='probRestore.probId'/>&probRestore.restoreStatus=<s:property value='#nav.basicDataAttri1'/>')"><a
								href="javascript:void(0)"><s:property
										value='#nav.basicDataName' /></a></li>
						</s:if>
						<s:else>
							<li name="navli" class="nav<s:property value='#index.index'/>"
								onclick="clickSwitchTab(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>','${pageContext.request.contextPath }/module/sub/manageAllTask.action?probRestore.probId=<s:property value='probRestore.probId'/>&probRestore.restoreStatus=<s:property value='#nav.basicDataAttri1'/>')"><a
								href="javascript:void(0)"><s:property
										value='#nav.basicDataName' /></a></li>
						</s:else>
					</s:iterator>
				</ul>
			</div>
		</nav>
		
		<div class="row navDiv closing">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
				<div class="panel panel-default">
					<!-- 请求路径改为绝对路径 ，避免因路径问题产生其他资源加载问题
				求绝对路径行号
				${pageContext.request.contextPath}
				< %=request.getContextPath()%>
			 -->
					<form method="post"
						action="${pageContext.request.contextPath }/module/sub/updateRestoreTask.action"
						id="updateForm" class="form-horizontal" name="updateForm">
						<input name="restoreIds" id="restoreIds" type="hidden">
						<s:hidden name="probRestore.probId"></s:hidden>
						<div class="panel-body">
							<div class="form-group">
								<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text
										name="prob.info.restore.status"></s:text></label>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<s:select name="probRestore.restoreStatus"
										list="restoreStatuList" listKey="basicDataId"
										listValue="basicDataName" cssClass="form-control"></s:select>
								</div>
								 
								<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text
										name="prob.info.remark"></s:text></label>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<textarea name="probRestore.restoreRemark" class="form-control" style="overflow-y:hidden;"></textarea>
								</div>
							</div>
							<div class="form-group">
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1" style="text-align: right;">备注：</div>
								<label class="col-xs-8 col-sm-8 col-md-8 col-lg-8 redMark">1.此列表展示的为办事处已经处理过的升级任务，请确认闭环或驳回重新处理！</label>
							</div>
							<div class="form-group">
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1"></div>
								<label class="col-xs-8 col-sm-8 col-md-8 col-lg-8 redMark">2.确认闭环（选择状态为：“已闭环”），驳回办事处重新处理（选择状态为：“已发布”），最后点击“更新”按钮！</label>
							</div>
							<div class="form-group">
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1"></div>
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
									<button type="submit" id="submit"
										class="btn btn-default  btn-block btn-sm">
										<s:text name='prob.info.update' />
									</button>
								</div>
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
									<button type="submit" id="retrieve"
										class="btn btn-default  btn-block btn-sm">
										<s:text name='prob.info.retrieve' />
									</button>
								</div>
							</div>
						</div>
					</form>
					<form method="post"
						action="${pageContext.request.contextPath }/module/sub/manageAllTask.action"
						id="QueryForm" class="form-horizontal" name="QueryForm">
						<s:hidden name="probRestore.probId"></s:hidden>
						<s:hidden name="probRestore.restoreStatus"></s:hidden>
						<div class="panel-body">
							<!-- 过滤 -->
							<div class="panel panel-default retrieve" style="display: none">
								<div class="panel-body">
									<div class="form-group">
										<label
											class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text
												name="prob.info.serial.num"></s:text></label>
										<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
											<s:textfield name="probRestore.serialNum"
												cssClass="form-control"></s:textfield>
										</div>
										<label
											class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text
												name="pm.project.projectName"></s:text></label>
										<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
											<s:textfield name="probRestore.projectName"
												cssClass="form-control"></s:textfield>
										</div>
										<label
											class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text
												name="pm.contract"></s:text></label>
										<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
											<s:textfield name="probRestore.contractNo"
												cssClass="form-control"></s:textfield>
										</div>
									</div>
									<div class="form-group">
										<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text name="pm.officearea"></s:text></label>
										<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
											<s:select name="probRestore.officeCode" id="officeCode"
														listKey="departmentNum" cssClass="form-control" headerKey=""
														headerValue="--请选择--" 
														listValue="departmentName" list="%{departmentList}" theme="simple" />
										</div>
										<label for="itemModel" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 form-control-label"><s:text name="prob.info.product.type"></s:text></label>
										<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
											<s:textfield id="itemModel" name="probRestore.itemModel" cssClass="form-control"></s:textfield>
										</div>
										<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
											<button type="submit" id="confirm"
												class="btn btn-default  btn-block btn-sm">
												<s:text name='button.confirm' />
											</button>
										</div>
									</div>
								</div>
							</div>
							<!-- 过滤 -->
							<s:if test="%{probRestore.restoreStatus == 30}"><!-- 待闭环任务 -->
							<table class="table table-striped">
								<thead>
									<tr>
										<td colspan="8">共查询到<s:property
												value="probRestoreTaskList.size()" />条数据记录
										</td>
									</tr>
								</thead>
								<tr>
									<td style="width:65px;"><s:text name="prob.info.checkboxthree"></s:text></td>
									<td><s:text name="prob.info.serial.num"></s:text></td>
									<td><s:text name="prob.info.product.type"></s:text></td>
									<%-- <td><s:text name="prob.info.restore.status"></s:text></td> --%>
									<td><s:text name="prob.info.latest.conp"></s:text></td>
									<td><s:text name="prob.info.latest.boot"></s:text></td>
									<td><s:text name="prob.info.latest.cpld"></s:text></td>
									<td><s:text name="prob.info.latest.pcb"></s:text></td>
									<td width="200px"><s:text name="pm.project.projectName"></s:text></td>
									<%-- <td><s:text name="pm.contract"></s:text></td> --%>
									<td><s:text name="pm.officearea"></s:text></td>
							
									<td><s:text name="prob.info.remark"></s:text></td>
								</tr>
								<s:iterator value="probRestoreTaskList" var="restore"
									status="index">
								<s:if test="#restore.executeTime != null">
									<tr class="probRestoreTask">
								</s:if>
								<s:else>
									<tr>
								</s:else>
										<td><input type="checkbox" name="restoreIds"
											value="<s:property value='#restore.id'/>"></td>
										<td><s:property value="#restore.serialNum" /></td>
										<td><s:property value="#restore.itemModel" /></td>
										<%-- <td><s:property value="#restore.restoreStatusName" /></td> --%>
										<td class="latestConp"><s:property value="#restore.latestConp"/></td>
										<td class="latestBoot"><s:property value="#restore.latestBoot"/></td>
										<td class="latestCpld"><s:property value="#restore.latestCpld"/></td>
										<td class="latestPcb"><s:property value="#restore.latestPcb"/></td>
										<td><a target="_blank" title="点击查看项目" href="${pageContext.request.contextPath }/module/ProjectModify.action?project.projectId=<s:property value="#restore.projectId" />"><s:property value="#restore.projectName" /></a></td>
									<%-- 	<td><s:property value="#restore.contractNo" /></td> --%>
										<td><s:property value="#restore.officeName" /></td>
										<td><s:property value="#restore.restoreRemark"/></td>
									</tr>
								</s:iterator>
							</table>
							</s:if>
						</div>
					</form>
				</div>
			</div>
		</div>
		<div class="row navDiv hideDiv backing">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
				<div class="panel panel-default">
					<form method="post"
						action="${pageContext.request.contextPath }/module/sub/updateRestoreTask.action"
						id="updateForm_backing" class="form-horizontal" name="updateForm_backing">
						<input name="restoreIds" id="restoreIds_backing" type="hidden">
						<s:hidden name="probRestore.probId"></s:hidden>
						<div class="panel-body">
							<div class="form-group">
								<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text
										name="prob.info.restore.status"></s:text></label>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<s:select name="probRestore.restoreStatus"
										list="restoreStatuList" listKey="basicDataId"
										listValue="basicDataName" cssClass="form-control"></s:select>
								</div>
								<label for="assignee" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text name="prob.info.assignee"></s:text></label>
							 	<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
								 	<s:hidden name="probRestore.assignee" id="assigneeHidden"></s:hidden>
									<s:textfield placeholder="支持模糊搜索，如不填，则默认不变" onfocus="fillAssignee()" onblur="fillAssignee()" id="assignee"  cssClass="form-control"></s:textfield>
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text
										name="prob.info.remark"></s:text></label>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<textarea name="probRestore.restoreRemark" class="form-control" style="overflow-y:hidden;"></textarea>
								</div>
							</div>
							<div class="form-group">
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1" style="text-align: right;">备注：</div>
								<label class="redMark">1.此列表展示为被办事处返回的升级任务，请选择直接闭环或重新发布！</label>
							</div>
							<div class="form-group">
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1"></div>
								<label class="redMark">2.直接闭环（选择状态为：“已闭环”），重新发布（选择状态为：“已发布”），最后点击“更新”按钮！</label>
							</div>
							<div class="form-group">
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1"></div>
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
									<button type="submit" id="submit_backing"
										class="btn btn-default  btn-block btn-sm">
										<s:text name='prob.info.update' />
									</button>
								</div>
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
									<button type="submit" id="retrieve_backing"
										class="btn btn-default  btn-block btn-sm">
										<s:text name='prob.info.retrieve' />
									</button>
								</div>
							</div>
						</div>
					</form>
					<form method="post"
						action="${pageContext.request.contextPath }/module/sub/manageAllTask.action"
						id="QueryForm_backing" class="form-horizontal" name="QueryForm_backing">
						<s:hidden name="probRestore.probId"></s:hidden>
						<s:hidden name="probRestore.restoreStatus"></s:hidden>
						<div class="panel-body">
							<!-- 过滤 -->
							<div class="panel panel-default retrieve_backing" style="display: none">
								<div class="panel-body">
									<div class="form-group">
										<label
											class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text
												name="prob.info.serial.num"></s:text></label>
										<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
											<s:textfield name="probRestore.serialNum"
												cssClass="form-control"></s:textfield>
										</div>
										<label
											class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text
												name="pm.project.projectName"></s:text></label>
										<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
											<s:textfield name="probRestore.projectName"
												cssClass="form-control"></s:textfield>
										</div>
										<label
											class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text
												name="pm.contract"></s:text></label>
										<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
											<s:textfield name="probRestore.contractNo"
												cssClass="form-control"></s:textfield>
										</div>
									</div>
									<div class="form-group">
										<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text name="pm.officearea"></s:text></label>
										<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
											<s:select name="probRestore.officeCode" id="officeCode"
														listKey="departmentNum" cssClass="form-control" headerKey=""
														headerValue="--请选择--" 
														listValue="departmentName" list="%{departmentList}" theme="simple" />
										</div>
										<label for="itemModel" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 form-control-label"><s:text name="prob.info.product.type"></s:text></label>
										<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
											<s:textfield id="itemModel" name="probRestore.itemModel" cssClass="form-control"></s:textfield>
										</div>
										<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
											<button type="submit" id="confirm"
												class="btn btn-default  btn-block btn-sm">
												<s:text name='button.confirm' />
											</button>
										</div>
									</div>
								</div>
							</div>
							<!-- 过滤 -->
							<s:if test="%{probRestore.restoreStatus == 20}"><!-- 办事处返回的任务 -->
							<table class="table table-striped">
								<thead>
									<tr>
										<td colspan="8">共查询到<s:property
												value="probRestoreTaskList.size()" />条数据记录
										</td>
									</tr>
								</thead>
								<tr>
									<td><s:text name="prob.info.checkbox"></s:text></td>
									<td><s:text name="prob.info.serial.num"></s:text></td>
									<td><s:text name="prob.info.product.type"></s:text></td>
									<%-- <td><s:text name="prob.info.restore.status"></s:text></td> --%>
									<%-- <td><s:text name="prob.info.conp"></s:text></td>
									<td><s:text name="prob.info.boot"></s:text></td>
									<td><s:text name="prob.info.cpld"></s:text></td>
									<td><s:text name="prob.info.pcb"></s:text></td> --%>
									<td width="200px"><s:text name="pm.project.projectName"></s:text></td>
									<td><s:text name="pm.contract"></s:text></td>
									<td><s:text name="pm.officearea"></s:text></td>
									<td><s:text name="prob.info.createTime"></s:text></td>
									<td><s:text name="prob.info.updateTime"></s:text></td>
									<td><s:text name="prob.info.remark"></s:text></td>
								</tr>
								<s:iterator value="probRestoreTaskList" var="restore"
									status="index">
									<tr>
										<td><input type="checkbox" name="restoreIds"
											value="<s:property value='#restore.id'/>"></td>
										<td><s:property value="#restore.serialNum" /></td>
										<td><s:property value="#restore.itemModel" /></td>
										<%-- <td><s:property value="#restore.restoreStatusName" /></td> --%>
										<%-- <td><s:property value="#restore.conp"/></td>
									<td><s:property value="#restore.boot"/></td>
									<td><s:property value="#restore.cpld"/></td>
									<td><s:property value="#restore.pcb"/></td> --%>
										<td><a target="blank" title="点击查看项目" href="${pageContext.request.contextPath }/module/ProjectModify.action?project.projectId=<s:property value="#restore.projectId" />"><s:property value="#restore.projectName" /></a></td>
										<td><s:property value="#restore.contractNo" /></td>
										<td><s:property value="#restore.officeName" /></td>
										<td><s:date name="#restore.createTime"
												format="yyyy-MM-dd" /></td>
										<td><s:date name="#restore.updateTime"
												format="yyyy-MM-dd" /></td>
										<td><s:property value="#restore.restoreRemark"/></td>		
									</tr>
								</s:iterator>
							</table>
							</s:if>
						</div>
					</form>
				</div>
			</div>
		</div>
		<div class="row navDiv hideDiv closed">
			<div class="form-group">
				<label class="redMark">&nbsp;&nbsp; &nbsp;&nbsp;  显示所有已闭环的子任务。</label>
			</div>
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
				<display:table name="probRestoreTaskList" pagesize="${restoreDisplayParam.pagesize }"
					export="true" size="${restoreDisplayParam.totalcount }" sort="external"
					requestURI="${pageContext.request.contextPath }/module/sub/manageAllTask.action"
					decorator="com.dp.plat.decorators.Wrapper"
					class="table table-striped" partialList="true" >
					<display:column property="serialNum" titleKey="prob.info.serial.num" sortable="true"></display:column>
					<display:column property="itemModel" titleKey="prob.info.product.type" sortable="true"></display:column>
					<%-- <display:column property="restoreStatusName" titleKey="prob.info.restore.status" sortable="true"></display:column> --%>
					<display:column property="projectNamea" style="width:200px;" titleKey="pm.project.projectName" sortable="true"></display:column>
					<display:column property="contractNo" titleKey="pm.contract" sortable="true"></display:column>
					<display:column property="officeName" titleKey="pm.officearea" sortable="true"></display:column>
					<display:column property="createTime" titleKey="prob.info.createTime" sortable="true" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
					<display:column property="updateTime" titleKey="prob.info.updateTime"  sortable="true" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
					<display:column property="assignee" titleKey="prob.info.assignee"></display:column>
					<display:column property="restoreRemark" titleKey="prob.info.remark"></display:column>
				</display:table>
			</div>
		</div>
	</div>
</body>
</html>