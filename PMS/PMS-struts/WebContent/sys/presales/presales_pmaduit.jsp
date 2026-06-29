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
	font: 12px/24px "微软雅黑";
}
.taskEdit{
    display: inline-block;
    vertical-align: middle;
    margin-right: 15px;
}
.current-state:before {
    content: '▶ ';
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
	$("#submitBtn").click(function(){
		$("#result").val(1);
		
		var files = $("#file").find("li").length;
		var confirmFileId = $("#confirmFileId").val();
		if(files == 0 && confirmFileId == ''){
			alert("请上传现场测试服务确认单!");
			return false;
		}
		if(confirm("请确认是否提交！")){
			$("#applyForm").submit();
		}else{
			return false;
		}
		
	});
	
	$("#backBtn").click(function(){
		$("#result").val(-1);
		if(confirm("请确认是否返回到服务经理！")){
			$("#applyForm").submit();
		}else{
			return false;
		}
			
	});
	
	var taskSize = $(".presalesTask").size();
	$('#finishTime_0').removeAttr("disabled");
	$('#finishTime_0').parents('tr.presalesTask').find(".btn").removeAttr("disabled");
	for(var i = 0;i < taskSize; i ++){
		date_picker3("finishTime_"+i);
		autoTextarea($("#remark_" + i)[0]);
		if($.trim($('#finishTime_'+i).val())){
			$('#finishTime_'+i).removeAttr("disabled");
			$('#finishTime_'+(i+1)).parents('tr.presalesTask').find(".btn").removeAttr("disabled");
			if($('#finishTime_'+(i+1)).parents('tr.presalesTask').find(".redMark").length == 0) {
				$('#finishTime_'+(i+1)).removeAttr("disabled");
			} else {
				var files = $.trim($('tr.presalesTask').eq(i+1).children().eq(4).text());
				if(files && $('#finishTime_'+(i+1)).val()){
					$('#finishTime_'+(i+1)).removeAttr("disabled");
				}
			}
		};
	}
	
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

/**
 * 已废弃，使用uploadTaskFile(presalesId ,taskId， eventKey)替代
 */
function uploadTaskFile(presalesId ,taskId){
	var host = window.location.href;
	host = host.substring(0,host.indexOf("?"));
	url = host + "?urlParams=" + presalesId + "|"+taskId;
	popWindow('module/sub/upload.action?redirect='+url, 700, 450,'上传附件', 'BudgetUpload', true);
	return false;
}

function deleteTaskfile(_this,fileId ,index){
	var fileSize = $(_this).parent().children().size()/2;
	var taskId = $("#presalesTaskId_"+index).val();
	if(confirm("确认要删除该附件吗?")){
		$.ajax({
			url:"deleteFile.action",
			type:"post",
			dataType:"json",
			data:{fileId:fileId},
			success:function(data){
				$(_this).parent().html("");
				if(fileSize == 1 && index == 2){//ajax删除时间
					$.ajax({
						url:"updatePresalesTask.action",
						type:"post",
						dataType:"json",
						data:{presalesTaskId:taskId ,taskFinshedTime:''},
						success:function(data2){
							
						}
					});
				}
				var presalesId =$('#presales_pmaduit_presales_presalesId').val();
				$.ajax({
					url:"updateConfirmFiles.action",
					type:"post",
					dataType:"json",
					data:{'presales.presalesId':presalesId, presalesTaskId:taskId, fileId:fileId},
					success:function(data2){
						
					}
				});
				alert(data.message);
			},
			error:function(){
				alert('error!');
			},
			complete:function(){
				window.location.reload();
			}
		});
	}
	
}
/**
 * 更新计划任务完成时间
 */
function updateTask(index){
	var finshTime = $.trim($("#finishTime_"+index).val());
	var preFinshTime = $.trim($("#finishTime_"+(index-1)).val());
	var nextFinshTime = $.trim($("#finishTime_"+(index+1)).val());
	var taskId = $("#presalesTaskId_"+index).val();
	var taskState = $.trim($("#stateName_"+index).text());
	var preTaskState = $.trim($("#stateName_"+ (index-1)).text());
	if( preTaskState == "未完成"){
		alert("请先完成上一阶段计划！");
		return false;
	}
	if( taskState == "未完成" && finshTime == ""){
		alert("请先选择完成时间！");
		return false;
	}
	if( finshTime > '<s:date name="taskFinshedTime" format="yyyy-MM-dd"/>' && finshTime !=''){
        alert("完成时间不能大于当前时间！");
        return false;
    }
	if( finshTime < preFinshTime && finshTime !=''){
		alert("当前阶段完成时间不能早于上阶段完成时间！");
		$("#finishTime_"+index).val(oldFinishTime);
		return false;
	}
	if( finshTime > nextFinshTime && nextFinshTime !='' ){
		alert("当前阶段完成时间不能晚于下阶段完成时间！");
		$("#finishTime_"+index).val(oldFinishTime);
		return false;
	}
	var remark = $.trim($("#remark_"+index).val());
	$.ajax({
		url:"updatePresalesTask.action",
		type:"post",
		dataType:"json",
		data:{presalesTaskId:taskId ,taskFinshedTime:finshTime, remark: remark},
		beforeSend:function(){
			$("#ok_"+index).hide();	
		},
		success:function(data){
			alert(data.message);
			if(data.message == "更新成功!"){
				if(finshTime != ""){
					$("#stateName_"+index).text("已完成");
				}else{
					$("#stateName_"+index).text("未完成");
				}
			}
		},
		complete:function(){
			//$("#ok_"+index).show();
			window.location.reload();
		},
		error:function(){
			alert("error!");
		}
		
	});
}
function updateTask_(index,obj){
	var redMarks = $(obj).parents('tr.presalesTask').find(".redMark").length;
	var files = $.trim($(obj).parents('tr.presalesTask').children().eq(4).text());
	if(redMarks >0 && !files){
		alert("请先上传附件再更新完成时间!");
		return false;
	}else{
		updateTask(index);
		return true;
	}
}
function updateTaskRemark_(index){
	var taskId = $("#presalesTaskId_"+index).val();
	var remark = $.trim($("#remark_"+index).val());
	if (!remark) {
		return false;
	}
    $.ajax({
        url:"updatePresalesTask.action",
        type:"post",
        dataType:"json",
        data:{presalesTaskId:taskId, remark: remark},
        beforeSend:function(){
            $("#remarkok_"+index).hide(); 
        },
        success:function(data){
            alert(data.message);
        },
        complete:function(){
            window.location.reload();
        },
        error:function(){
            alert("error!");
        }
        
    });
}

var oldFinishTime;
$(document).on('focus',"[id^='finishTime_']",function(){
	oldFinishTime = $.trim($(this).val());
	console.log(oldFinishTime);
});

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

function uploadTaskFile(presalesId, taskId, eventKey){
	var target = (window.event || {}).target || (arguments.callee.caller.arguments[0] || {}).target;
	if (!target) {
		return alert("请点击上传附件按钮");
	}
	$tr = $(target).parents("tr:first").prev(".presalesTask");
	if ($tr.length == 1 && !$tr.hasClass("success")) {
		return alert("请先完成上一阶段");
	}
    var host = window.location.href;
    host = host.substring(0,host.indexOf("?"));
    url = host + "?urlParams=" + presalesId + "|"+taskId;

    var projectId = $('#presales_pmaduit_presales_presalesId').val();
    var column010 = "presales";
    var column011 = "";//$("#projectType").val();
    var contractNo = $("#presales_presalesCode").val();
    popWindow("module/sub/presales_upload.action?projectDeliver.projectId=" + projectId + "&projectDeliver.contractNo=" + contractNo + "&projectDeliver.column010=" + column010 +
    //popWindow("module/sub/presales_toUploadPresalesDeliverFile.action?projectDeliver.projectId=" + projectId + "&projectDeliver.contractNo=" + contractNo + "&projectDeliver.column010=" + column010 +
        "&projectDeliver.column011=" + column011 + "&projectDeliver.eventKey=" + eventKey + "&redirect=" + url, 700, 450,'上传交付件', 'BudgetUpload', true);
}

function deleteDeliverById(deliverid, e){
    if(!confirm("是否确认删除？")){
        return;
    }
    $.ajax({
        url :"presalesAjax_deleteDeliverById.action",
        type :"post",
        dataType :"json",
        data : {fileId : deliverid},
        success:function(data){
            if(data.fileId != 0){
                alert("删除成功！");
            }else{
                alert("删除失败！");
            }
        },
        error:function(){
            alert('error!');
        },
        complete:function(){
            window.location.reload();
        }
    });
}
function updateDeliverById(fileId, target) {
    if(!confirm("是否确认更新交付件类型？")){
        return;
    }
    var $option = $(target).parents("tr:first").find("option:selected");
    var deliverId = $option.val();
    var deliverType = $option.text();
    $.ajax({
        url :"presalesAjax_updateDeliverById.action",
        type :"post",
        dataType :"json",
        data : {fileId: fileId, "projectDeliver.id" : fileId, "projectDeliver.deliverId": deliverId, "projectDeliver.deliverableType": deliverType},
        success:function(data){
            if(data.fileId != 0){
                alert("更新成功！");
            }else{
                alert("更新失败！");
            }
        },
        error:function(){
            alert('error!');
        },
        complete:function(){
            window.location.reload();
        }
    });
}
</script>
<dp:script type="text/javascript" src="js/presales/initNavBar.js">
var presalesId = ${presales.presalesId};
var officeCode = "${presales.officeCode}";
var presalesSource = "${presales.source}";
</dp:script>
</head>
<body>
	<%-- <fieldset>
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
				<td><s:text name="pm.presales.marketName"></s:text>:</td>
				<td><s:property value="presales.marketName"/></td>
				<td><s:text name="pm.presales.systemName"></s:text>:</td>
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
						placeholder="支持模糊搜索"  readonly="true"
						></s:textfield>
				</td>
				<td><s:text name="pm.presales.pm"></s:text>:</td>
				<td>
					<s:textfield name="presales.projectManagerName" id="projectManager" 
						cssClass="form-control" cssStyle="width:200px;" readonly="true"
						placeholder="支持模糊搜索" ></s:textfield>
				</td>
			</tr>
			<tr>
                <td><s:text name="pm.presales.projectType"></s:text>:</td>
                <td>
                    <s:property value="presales.projectTypeName"/>
                    <s:hidden id="projectType" name="presales.projectType"/>
                </td>
                <td>项目起止时间:</td>
                <td>
                    <s:date name="presales.applyTime" format="yyyy-MM-dd HH:mm:ss"/>
                    ~
                    <s:if test="presales.endTime != null">
                        <s:date name="presales.endTime" format="yyyy-MM-dd HH:mm:ss"/>
                    </s:if>
                    <s:else>至今</s:else>（<s:property value="presales.totalDuration"/>）
                </td>
                <td><s:text name="pm.presales.applyTime"/>:</td>
                <td><s:date name="presales.applyTime" format="yyyy-MM-dd HH:mm:ss"/></td>
                <td><s:text name="pm.presales.endTime"/>:</td>
                <td><s:date name="presales.endTime" format="yyyy-MM-dd HH:mm:ss"/></td>
            </tr>
        </table>
        <table class="table table-no-border" style="margin-top: -20px;margin-bottom: 0;">
            <tr>
                <td <s:if test='presales.projectState == 30'>class="text-success current-state"</s:if>><s:text name="pm.presales.serviceDuration"></s:text>:<s:property value="presales.serviceDuration"/><span style="cursor: help;" title="<s:text name='pm.presales.applyDuration'/>">(<s:property value="presales.applyDuration"/>)</span></td>
                <td <s:if test='presales.projectState == 31'>class="text-success current-state"</s:if>><s:text name="pm.presales.programDuration"></s:text>:<s:property value="presales.programDuration"/></td>
                <td <s:if test='presales.projectState == 32'>class="text-success current-state"</s:if>><s:text name="pm.presales.testDuration"></s:text>:<s:property value="presales.testDuration"/></td>
                <td <s:if test='presales.projectState == 33'>class="text-success current-state"</s:if>><s:text name="pm.presales.callbackDuration"></s:text>:<s:property value="presales.callbackDuration"/></td>
            
             </tr>
        </table>
	</fieldset> --%>
    <jsp:include page="./presales_basic_info.jsp"></jsp:include>
	<fieldset>
		<legend><b>工程计划</b></legend>
		<table class="table table-condensed table-hover table-striped">
			<tr class="warning">
				<td width="10%"><s:text name="pm.test.stage"></s:text></td>
				<td width="10%"><s:text name="pm.test.state"></s:text></td>
				<td width="20%" style="text-align: center;" colspan="1"><s:text name="pm.test.finshed"></s:text></td>
				<td width="25%"><s:text name="pm.test.process"></s:text></td>
				<td width="35%" style="text-align: center;" colspan="2"><s:text name="pm.test.deliver"></s:text></td>
			</tr>
			<s:iterator value="taskList" var="t" status="index">
				<s:if test="#t.taskState==1">
					<tr class="success presalesTask">
				</s:if>
				<s:else>
					<tr class="presalesTask">
				</s:else>
					<td>
						<s:property value="#t.taskName"/>
					</td>
					<td>
						<span id="stateName_<s:property  value='#index.index'/>">
							<s:property value="#t.taskStateName"/>
						</span>
					</td>
					<td>
						<s:hidden value="%{#t.taskId}" id="presalesTaskId_%{#index.index}"></s:hidden>
					
						<s:if test="#t.deliverFileIds == null & #index.index == 2">
							<s:textfield value="%{#t.eventActualFinishDateStr}" cssClass="form-control taskEdit" cssStyle="width:130px;" id="finishTime_%{#index.index}" disabled="true" autocomplete="off"></s:textfield>
						</s:if>
						<s:else>
							<s:textfield value="%{#t.eventActualFinishDateStr}" cssClass="form-control taskEdit" cssStyle="width:130px;" id="finishTime_%{#index.index}" disabled="true" autocomplete="off"></s:textfield>
						</s:else> 
					<!-- </td>
					<td> -->
						<s:if test="#t.deliverFileIds == null">
							<a href="javascript:void(0)"  onclick="updateTask_(<s:property value='#index.index'/>,this)">
					        	<span class="glyphicon glyphicon-ok" id="ok_<s:property  value='#index.index'/>"></span>
					        </a>
					    </s:if>
					    <s:else>
					    	<a href="javascript:void(0)"  onclick="updateTask_(<s:property value='#index.index'/>,this)">
					        	<span class="glyphicon glyphicon-ok" id="ok_<s:property  value='#index.index'/>"></span>
					        </a>
					    </s:else>					
					</td>
					<td>
                        <s:textarea value="%{#t.remark}" cssClass="form-control taskEdit" cssStyle="width:calc(100% - 35px);" id="remark_%{#index.index}"></s:textarea>
                        <a href="javascript:void(0)"  onclick="updateTaskRemark_(<s:property value='#index.index'/>,this)">
                            <span class="glyphicon glyphicon-ok" id="remarkok_<s:property  value='#index.index'/>"></span>
                        </a>
                    </td>
					<td>
					 	<s:iterator value="#t.fileMap" var="file">
				 			<a href="module/download.action?fileId=<s:property value='key'/>" title="点击下载"> <s:property value="value"/> </a>  	
				 			<a href="javascript:void(0)" onclick="deleteTaskfile(this,<s:property value='key'/>,<s:property value='#index.index'/>)" title="删除"> 
				 				<img alt="" src="images/delete_profile.gif">
				 			</a>  	
					 	</s:iterator>
                        <s:iterator value="#t.fileParams" var="f">
                            <a href="module/DownloadFile.action?downname=<s:property value='#f.fileName'/>&downpath=<s:property value="#f.filePath"/>" title="点击下载"><s:property value="#f.fileName"/></a>
                            <a href="javascript:void(0)" onclick="deleteDeliverById(<s:property value='id'/>, this)" title="删除"> 
                                <img alt="" src="images/delete_profile.gif">
                            </a>
                        </s:iterator>
					</td>
					<td>
    					<%-- <s:if test="#index.index == 2">
    						<a class="btn ttext-success disabled href="javascript:void(0)" onclick="uploadTaskFile(<s:property value='presales.presalesId' />,<s:property value='#t.taskId'/>, '<s:property value='#t.taskTypeCode' />-<s:property value='#t.taskTypeId' />')"
    				 		  style="height: 30px" class="redMark" ><span class="glyphicon glyphicon-upload"></span>上传现场测试报告(必传) </a> 
    					</s:if>
    					<s:else>
    						<a class="btn" disabled href="javascript:void(0)" onclick="uploadTaskFile(<s:property value='presales.presalesId' />,<s:property value='#t.taskId'/>, '<s:property value='#t.taskTypeCode' />-<s:property value='#t.taskTypeId' />')"
    				 		  style="height: 30px"  ><span class="glyphicon glyphicon-upload"></span> 上传附件</a> 
    					</s:else> --%>
                        
                        <a class="btn ${index.index == 2 ? 'text-danger redMark' : ''}" disabled href="javascript:void(0)" onclick="uploadTaskFile(<s:property value='presales.presalesId' />,<s:property value='#t.taskId'/>, '<s:property value='#t.taskTypeCode' />-<s:property value='#t.taskTypeId' />')"
                          style="height: 30px"  ><span class="glyphicon glyphicon-upload"></span> 上传附件</a> 
					</td>
				</tr>
			</s:iterator>
		</table>
	</fieldset>

	<fieldset>
		<legend><b>流程办理</b></legend>
		<s:form cssClass="form-inline" action="module/presales_pmaduit.action" method="post" name="aduitForm">
			<s:hidden name="presales.serviceManager" id="sm_hide"></s:hidden>
			<s:hidden name="presales.projectManager" id="pm_hide"></s:hidden>
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
			<s:if test="%{presales.finshedTime == null}">
				<button type="submit" class="btn btn-success" disabled="disabled" id="submitBtn">确认提交</button>
			</s:if>
			<s:else>
				<button type="submit" class="btn btn-success" id="submitBtn">确认提交</button>
			</s:else>
			<button type="submit" class="btn btn-info" id="backBtn">返回服务经理</button>
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
                <td><s:text name="pm.deliverdetail.operate"></s:text></td>
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
                        <s:elseif test="#f.path == 3">
                            <a href="<s:property value='#f.filePath'/>">
                                <s:property value="#f.fileName"/>
                            </a>
                        </s:elseif>
                        <s:else>
                            <a href="module/DownloadFile.action?downname=<s:property value='#f.fileName'/>&downpath=<s:property value="#f.filePath"/>"><s:property value="#f.fileName"/></a>
                        </s:else>
					</td>
                    <td>
                        <s:if test="#f.path == 1">
                            <s:property value="#f.fileType" default="SMS附件"/>
                        </s:if>
                        <s:elseif test="#f.path == 0">
                            <s:property value="#f.fileType" default="历史附件"/>
                        </s:elseif>
                        <s:elseif test="#f.path == 3">
                            <s:property value="#f.fileType" default="OA附件"/>
                        </s:elseif>
                        <s:else>
                            <select>
                            <s:iterator value="projectDeliverList" var="pd" status="ss">
                                <s:if test="#pd.eventKey == #f.flag">
                                    <s:if test="#pd.deliverValue == #f.fileType">
                                        <option value="<s:property value='#pd.id'/>" selected><s:property value='#pd.deliverValue'/></option>
                                    </s:if>
                                    <s:else>
                                        <option value="<s:property value='#pd.id'/>"><s:property value='#pd.deliverValue'/></option>
                                    </s:else>
                                </s:if>
                            </s:iterator>
                            </select>
                        </s:else>
                    </td>
					<td><s:property value="#f.uploadBy"/></td>
                    <td><s:date name="#f.uploadTime" format="yyyy-MM-dd HH:mm"></s:date></td>
                    <td>
                        <s:if test="#f.path == 0">
                            <a href="javascript:void(0)" onclick="deleteTaskfile(this,<s:property value='id'/>)" title="删除"> 
                                <img alt="" src="images/delete_profile.gif">
                            </a>
                        </s:if>
                        <s:elseif test="#f.path == 2">
                        <a style="margin-right: 10px;" href="javascript:void(0)" onclick="updateDeliverById(<s:property value='id'/>, this)" title="更新交付件类型">
                                <span class="glyphicon glyphicon-ok" style="vertical-align: middle;"></span>
                            </a>
                            <a href="javascript:void(0)" onclick="deleteDeliverById(<s:property value='id'/>, this)" title="删除"> 
                                <img alt="" src="images/delete_profile.gif">
                            </a>
                        </s:elseif>
                    </td>
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
				<td><s:text name="pm.ps.pro.remark"></s:text></td>
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
</body>
</html>
