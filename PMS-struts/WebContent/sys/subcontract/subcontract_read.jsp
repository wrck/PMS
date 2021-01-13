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
<meta name="function" content="<s:text name='pm.subcontract.manage' />">
<style type="text/css">
legend {
	font: 12px/24px "微软雅黑"
}

table#subcontractInfoTable > tbody > tr > td:nth-child(odd) {
    text-align: right;
}

.mergeTdBorder {
    border-right:none!important;
}
.mergeTdBorder + .mergeTdBorder {
    border-left:none!important;
}
</style>
<script type="text/javascript">
/**
 * 数据初始化
 */
$(document).ready(function(){
	autoCompleteFacilitator();
	queryContractNoEngineeFee();
	querySubcontractPayment();
	querySubcontractCallback();
	var result = "<s:property value='result'/>";
	if (result) {
		alert(result);
	}
});

/**
 * 事件定义
 */
$(document).ready(function(){
    var t1 = 0;
    var t2 = 0;
    $(window).scroll(function(){
        var st=$(window).scrollTop();
        var rheight = $(window).height();
        var height = $("html").height();
        if(st > rheight){
            $(".backTop").fadeIn(500);
        }else{
            $(".backTop").fadeOut(1000);
        }
        if(st > rheight && st < height - 2*rheight){
            $(".rollBottom").fadeIn(500);
        }else{
            $(".rollBottom").fadeOut(1000);
        }
    })
         
    $(document).on('click', ".backTop", function() {
        /* var mainHeight = $("#mainForm").height();
        var mainHeaderHeight = $(".mainframe_head").height();
        var navHegiht = $(".navibar").height();
        var st = mainHeight + navHegiht + mainHeaderHeight || 0; */
        var st = 0;
        $('body,html').animate({scrollTop: st},500);
    });
    $(document).on('click', ".rollBottom", function() {
        var height = $("html").height();
        $('body,html').animate({scrollTop: height},500);
    });
    
    $(document).on('change', "#contractNos", function() {
    	chooseSubcontractProject();
    });
    
    var checking = false;
    var $preSelectProject;
    $(document).on('change', "#projectDisplayTable input[name='selected'], #projectDisplayTable input[name='checkall']", function() {
    	var $selectedProject = $("#projectDisplayTable input[name='selected']:checked");
    	if ($selectedProject.length > 0) {
    		var projectIds = [];
    		$selectedProject.each(function() {
    			projectIds.push(this.value);
    		});
    		$("#projectIds").val(projectIds.join(","));
    	} else {
    		$("#projectIds").val("");
    	}
   		var $firstProject = $($selectedProject[0]);
        if ($preSelectProject == $firstProject[0]) {
            return;
        }
        $preSelectProject = $firstProject[0];
        if ($firstProject.length > 0) {
            var projectName = $($firstProject).parents("tr:first").find(".updateMark").text();
            if ($.trim(projectName) && !checking) {
                checking = true;
                projectName += " — 转包";
                $.ajax({
                    url:"module/s/subcontractAjax_checkSubcontractName.action",
                    type:"post",
                    async:false,
                    dataType:"json",
                    data:{"subcontract.subcontractName": projectName},
                    success:function(data){
                        var result = data.result;
                        result = Number(result);
                        try {
                            result += 1 
                            if (result < 10) {
                                result = "0" + result;
                            }
                        } catch (e) {
                            result = "01";
                        }
                        projectName += "-" + result;
                        $("#subcontractName").val(projectName);
                    },
                    error:function(XMLHttpRequest,textStatus,errorThrown){
                        alert("获取发货数据失败！错误信息如下：<br>"+XMLHttpRequest.responseText);
                    },
                    complete:function(data){
                        checking = false;
                    }
                })
            }
        } else {
            $("#subcontractName").val("");
        }
    });
    
    $(document).on('click', "#saveBtn", function() {
    	submitSave();
    });
    $(document).on('click', "#applyBtn", function() {
    	submitApply();
    });
    $(document).on('change', "input[type='file']", function() {
        var file = $(this).val();
        var $newFile = $(this).clone();
        $(this).siblings("input[type='file']").each(function(index) {
        	if (!$(this).val()) {
        		$(this).remove();
        	}
        })
        if (file) {
        	$(this).parent().append($newFile);
        }
    });
});

function chooseShipmentInfoPop(){
    var projectIds = $("#projectIds").val();
    var contractNos = $("#contractNos").val();
    if (!contractNos) {
    	alert("请输入项目合同号");
    	$("#contractNos").focus();
    	return false;
    }
    if (!projectIds) {
        alert("请选择需要转包的项目");
        $("#projectIds").focus();
        return false;
    }
    popWindow('module/sub/chooseShipmentInfo.action?projectIds='+projectIds+'&contractNos='+contractNos + '&redirect='+window.location.href, 1100, 650, '选择序列号', 'BudgetUpload', true);
    return false;
}


var loadingHtml = "<div id='loading' style='height:180px;display:none;display:-webkit-flex;display: flex;justify-content:center;align-items:center;'>"+
"<img src='./images/loading-circle.gif'/>" +
"</div>";
var flagS = true;
function chooseShipmentInfo(){
	var projectIds = $("#projectIds").val();
	var contractNos = $("#contractNos").val();
	if (!contractNos) {
	    alert("请输入项目合同号");
	    $("#contractNos").focus();
	    return false;
	}
	if (!projectIds) {
	    alert("请选择需要转包的项目");
	    $("#projectIds").focus();
	    return false;
	}
    if(flagS){
    	$("#shipmentListDiv").html(loadingHtml);
        flagS = false;
        $.ajax({
            url:"module/sub/chooseShipmentInfo.action",
            type:"post",
            dataType:"html",
            data:{"projectIds":projectIds,"contractNos":contractNos},
            success:function(data){
                data = data.substring(data.indexOf("<body"),data.indexOf("</body>")+7);
                $("#shipmentListDiv").html(data);
            },
            error:function(XMLHttpRequest,textStatus,errorThrown){
                $("#shipmentListDiv").html("获取序列号数据失败！错误信息如下：<br>"+XMLHttpRequest.responseText);
            },
            complete:function(data){
                flagS = true;
            }
        })
    }
    return false;
}

var flagP = true;
function chooseSubcontractProject(){
    var contractNos = $("#contractNos").val();
    if (!contractNos) {
        alert("请输入项目合同号");
        $("#contractNos").focus();
        return false;
    }
    if(flagP){
        $("#projectListDiv").html(loadingHtml);
        flagP = false;
        $.ajax({
            url:"module/sub/chooseSubcontractProject.action",
            type:"post",
            dataType:"html",
            data:{"contractNos":contractNos},
            success:function(data){
                data = data.substring(data.indexOf("<body"),data.indexOf("</body>")+7);
                $("#projectListDiv").html(data);
            },
            error:function(XMLHttpRequest,textStatus,errorThrown){
                $("#projectListDiv").html("获取转包项目列表失败！错误信息如下：<br>"+XMLHttpRequest.responseText);
            },
            complete:function(data){
            	flagP = true;
            }
        })
    }
    return false;
}

var flagE = true;
function queryContractNoEngineeFee(){
    var contractNos = $("#contractNos").val();
    var subcontractId = $("#subcontractId").val();
    if (!subcontractId) {
    	return false;
    }
    if (!contractNos) {
        alert("请输入项目合同号");
        $("#contractNos").focus();
        return false;
    }
    if(flagE){
        $("#engineeFeeDiv").html(loadingHtml);
        flagE = false;
        $.ajax({
            url:"module/sub/queryContractNoEngineeFee.action",
            type:"post",
            dataType:"html",
            data:{"contractNos":contractNos, "subcontract.id": subcontractId},
            success:function(data){
                data = data.substring(data.indexOf("<body"),data.indexOf("</body>")+7);
                $("#engineeFeeDiv").html(data);
            },
            error:function(XMLHttpRequest,textStatus,errorThrown){
                $("#engineeFeeDiv").html("获取工程服务费失败！错误信息如下：<br>"+XMLHttpRequest.responseText);
            },
            complete:function(data){
                flagE = true;
            }
        })
    }
    return false;
}

var flagP = true;
function querySubcontractPayment(){
    var contractNos = $("#subcontractForm #contractNos").val();
    var subcontractId = $("#subcontractForm #subcontractId").val();
    var subcontractNo = $("#subcontractForm #subcontractNo").val();
    var isAccrued = $("#subcontractForm input[name='subcontract.isAccrued']:checked").val();
    if (!subcontractId) {
        return false;
    }
    if (!contractNos) {
        alert("请输入项目合同号");
        $("#contractNos").focus();
        return false;
    }
    if(flagP){
        $("#subcontractPaymentDiv").html(loadingHtml);
        flagP = false;
        $.ajax({
            url:"module/sub/querySubcontractPayment.action",
            type:"post",
            dataType:"html",
            data:{"subcontract.id": subcontractId, "subcontract.subcontractNo": subcontractNo,"subcontract.isAccrued": isAccrued},
            success:function(data){
                data = data.substring(data.indexOf("<body"),data.indexOf("</body>")+7);
                $("#subcontractPaymentDiv").html(data);
            },
            error:function(XMLHttpRequest,textStatus,errorThrown){
                $("#subcontractPaymentDiv").html("获取付款信息失败！错误信息如下：<br>"+XMLHttpRequest.responseText);
            },
            complete:function(data){
            	flagP = true;
            }
        })
    }
    return false;
}

var flagC = true;
function querySubcontractCallback(){
    var subcontractId = $("#subcontractForm #subcontractId").val();
    if (!subcontractId) {
        return false;
    }
    if(flagC){
       
    	$("#subcontractCallbackDiv").html(" <iframe id='subcontractCallbackFrame' src='module/sub/querySubcontractCallback.action?subcontractCallback.subcontractId=" + subcontractId
                + "' style=\"width:100%;height:100%;background-color:transparent;\" frameborder=\"0\" allowtransparency=\"true\"  "
                + "> </iframe>"); 
        $("#subcontractCallbackFrame").contents().find("body").append("<div style='height:100%;display:-webkit-flex;display: flex;justify-content:center;align-items:center;'><img src='./images/loading-circle.gif'/></div>");
        var t = setInterval(function(){
            if($("#subcontractCallbackFrame").contents().find("body").children().length>0){
            	var cbh = $("#subcontractCallbackFrame").contents().find("body").height();
            	$("#subcontractCallbackFrame").height(cbh);
                clearInterval(t);
            }
        }, 200);
        /* 
        $("#subcontractCallbackDiv").html(loadingHtml);
        flagC = false;
        $.ajax({
            url:"module/sub/querySubcontractCallback.action",
            type:"post",
            dataType:"html",
            data:{"subcontract.id": subcontractId, "subcontractCallback.subcontractId": subcontractId},
            success:function(data){
                data = data.substring(data.indexOf("<body"),data.indexOf("</body>")+7);
                $("#subcontractCallbackDiv").html(data);
            },
            error:function(XMLHttpRequest,textStatus,errorThrown){
                $("#subcontractCallbackDiv").html("获取服务回访失败！错误信息如下：<br>"+XMLHttpRequest.responseText);
            },
            complete:function(data){
            	flagC = true;
            }
        }) */
    }
    return false;
}

var flagM = true;
function querySubcontractCallback(){
    var subcontractId = $("#subcontractForm #subcontractId").val();
    if (!subcontractId) {
        return false;
    }
    if(flagM){
        $("#subcontractCommentListDiv").html(loadingHtml);
        flagM = false;
        $.ajax({
            url:"module/sub/querySubcontractComment.action",
            type:"post",
            dataType:"html",
            data:{"subcontract.id": subcontractId},
            success:function(data){
                data = data.substring(data.indexOf("<body"),data.indexOf("</body>")+7);
                $("#subcontractCommentListDiv").html(data);
            },
            error:function(XMLHttpRequest,textStatus,errorThrown){
                $("#subcontractCommentListDiv").html("获取流程记录失败！错误信息如下：<br>"+XMLHttpRequest.responseText);
            },
            complete:function(data){
            	flagM = true;
            }
        })
    }
    return false;
}

function submitSave() {
	$("#subcontractForm").attr("action", "module/subcontract_create.action");
	$("#subcontractForm").submit();
}

function submitApply() {
	$("#subcontractForm").attr("action", "module/subcontract_apply.action");
    $("#subcontractForm").submit();
}

function autoCompleteFacilitator() {
	$.ajax({
	    url:"module/s/subcontractAjax_queryFacilitator.action",
	    type:"post",
	    dataType:"json",
	    data:{"subcontractFacilitator.state":true},
	    success:function(data){
	    	data = JSON.parse(data.result);
	    	$( "#subcontractForm_subcontract_facilitatorName").autocomplete({
	            minLength: 0,
	            source: data,
	            focus: function( event, ui ) {
	                //$("#subcontractForm_subcontract_facilitatorName").val( ui.item.label);
	                return false;
	            },
	            select: function( event, ui ) {
	                $("#subcontractForm_subcontract_facilitatorName").val(ui.item.label);
	                $("#subcontractForm_subcontract_facilitatorId").val(ui.item.id);
	                $("#subcontractForm_subcontract_bankInfo").val(ui.item.bankInfo);
	                $("#subcontractForm_subcontract_bankAccount").val(ui.item.bankAccount);
	                return false;
	            }
	        })
	    },
	    error:function(XMLHttpRequest,textStatus,errorThrown){
	        alert("获取服务商数据失败！错误信息如下：<br>"+XMLHttpRequest.responseText);
	    }
	})
}
</script>
</head>
<body>
    <s:form enctype="multipart/form-data" id="subcontractForm" action="module/subcontract_create.action" method="post" >
		<fieldset>
			<legend><b>基本信息</b></legend>
			<table id="subcontractInfoTable" class="table table-bordered table-hover table-striped ">
			    <tr style="display:none"><td><s:hidden name="subcontract.id" id="subcontractId"/></td></tr>
				<tr>
					<td><s:text name="pm.subcontract.contractNos"></s:text>:</td>
					<td class="col-sm-5"><s:textfield name="subcontract.contractNos" id="contractNos" cssClass="form-control" placeholder='请输入合同号（英文逗号分割）'/></td>
					<td><s:text name="pm.subcontract.project"></s:text>:</td>
					<td class="col-sm-5"><s:hidden id="projectIds" name="subcontract.projectIds"></s:hidden>
					    <s:property value="subcontract.projectName"/>
					    <div id="projectListDiv">
					       <display:table style="text-align: left;margin-bottom:0;"
					            name="projectList" pagesize="${projectList.size()}" export="false" id="projectDisplayTable"
					            size="${projectList.size()}" sort="external"
					            decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-condensed table-hover table-striped" >
					            <%-- <display:column property="projectCheckWrapper" titleKey="pm.shipment.check"></display:column> --%>
					            <%-- <display:column property="projectCode" titleKey="pm.project.projectCode" class="transferProjectCode"></display:column> --%>
					            <display:column property="projectNameWithCodeWarrper" class="transferProjectName" titleKey="pm.project.projectName" media="html"></display:column> 
					            <display:column property="contractNo" titleKey="pm.contract" class="transferContractNo" decorator="com.dp.plat.decorators.ContractNoList"></display:column>
					        </display:table>
					    </div>
					</td>
				</tr>
				<tr>
				    <td><s:text name="pm.subcontract.subcontractNo"></s:text>:</td>
	                <td><s:textfield name="subcontract.subcontractNo" id='subcontractNo' cssClass="form-control eng-write" placeholder='转包合同号'/></td>
				    <td><s:text name="pm.subcontract.subcontractName"></s:text>:</td>
					<td><s:textfield name="subcontract.subcontractName" id="subcontractName" cssClass="form-control" placeholder='转包名称'/></td>
	            </tr>
				<tr>
					<td><s:text name="pm.subcontract.type"></s:text>:</td>
	                <td><s:select list="typeList" name="subcontract.type" cssClass="form-control" listKey="basicDataId" listValueKey="basicDataName" headerKey="" headerValue="--请选择--"/></td>
	                <td><s:text name="pm.subcontract.facilitator"></s:text>:</td>
					<td><s:textfield name="subcontract.facilitatorName" cssClass="form-control" placeholder='服务商（模糊查询）'/><s:hidden name="subcontract.facilitatorId"/></td>
				</tr>
				<tr>
	                <td><s:text name="pm.subcontract.facilitator.bankInfo"></s:text>:</td>
	                <td><s:textfield name="subcontract.bankInfo" cssClass="form-control" placeholder='服务商开户行'/></td>
	                <td><s:text name="pm.subcontract.facilitator.bankAccount"></s:text>:</td>
	                <td><s:textfield name="subcontract.bankAccount" cssClass="form-control" placeholder='服务商收款账号'/></td>
	            </tr>
				<tr>
					<td><s:text name="pm.subcontract.officeName"></s:text>:</td>
					<s:if test="%{subcontract == null || subcontract.id == null}">
						<td><s:select list="depList" name="subcontract.officeCode" value="%{user.dpNo}" cssClass="form-control" listKey="departmentNum" listValueKey="departmentName" headerKey="" headerValue="--请选择--"/></td>
						<td><s:text name="pm.subcontract.createName"></s:text>:</td>
		                <td><s:textfield name="subcontract.createName" cssClass="form-control" value="%{user.username}-%{user.realName}"/></td>
                    </s:if>
                    <s:else>
                        <td><s:select list="depList" name="subcontract.officeCode" cssClass="form-control" listKey="departmentNum" listValueKey="departmentName" headerKey="" headerValue="--请选择--"/></td>
                        <td><s:text name="pm.subcontract.createName"></s:text>:</td>
                        <td><s:textfield name="subcontract.createName" cssClass="form-control"/></td>
                    </s:else>
	            </tr>
				<tr>
				    <td><s:text name="pm.subcontract.profitDep"></s:text>:</td>
	                <td><s:select list="profitDepList" name="subcontract.profitDepCode" cssClass="form-control" listKey="departmentNum" listValueKey="departmentName" headerKey="" headerValue="--请选择--"/></td>
					<td><s:text name="pm.subcontract.deliver"/>:</td>
					<td class="col-sm-5">
					    <span id="draftContracts" class="col-sm-6">
					        <s:text name="pm.subcontract.deliver.contract.draft"/>
					        <span class="draftContract">
					            <s:file label="File" name="uploadDeliverList[0].uploads" cssClass="form-control" />
					            <s:hidden name="uploadDeliverList[0].type" value="0"/>
					            <s:hidden name="deliverTypes" value="0"/>
					        </span>
				        </span>
				        <span id="serviceOrders" class="col-sm-6">
				            <s:text name="pm.subcontract.deliver.service"/>
	                        <span class="serviceOrder">
	                            <s:file label="File" name="uploadDeliverList[1].uploads" cssClass="form-control" />
                                <s:hidden name="uploadDeliverList[1].type" value="1"/>
	                            <%-- <s:textfield type="file" name="uploadFiles" cssClass="form-control" /> --%>
	                            <s:hidden name="deliverTypes" value="1"/>
	                        </span>
	                    </span>
			        </td>
				</tr>
				<tr>
				    <td><s:text name="pm.subcontract.reason"/>:</td>
	                <td colspan="1"><s:textarea name="subcontract.reason" cssClass="form-control" placeholder='请输入转包原因'/></td>
	                <td><s:text name="pm.subcontract.accrued"/></td>
	                <td class="mergeTdBorder">
	                   <s:radio name="subcontract.isAccrued" list="#{true:'是',false:'否'}"/>
	                  <!--  <button id="saveBtn" type="button" class="btn btn-success">确定</button>
	                   <button id="applyBtn" type="button" class="btn btn-success">提交</button> -->
                    </td>
				</tr>
				<tr>
                    <td><s:text name="pm.subcontract.price"/>:</td>
                    <td><s:property value="subcontract.subcontractAmount"/></td>
                    <td class="mergeTdBorder"><button id="saveBtn" type="button" class="btn btn-success"><span class="glyphicon glyphicon-floppy-save" style="font-size:12px;"></span> 确定</button></td>
                    <td class="mergeTdBorder"><button id="applyBtn" type="button" class="btn btn-info"><span class="glyphicon glyphicon-ok" style="font-size:12px;"></span> 提交</button></td>
                </tr>
			</table>
		</fieldset>
		<nav class="navbar navbar-default" role="navigation" style="margin-top: 20px;">
	        <div>
	            <ul class="nav navbar-nav">
	               <%--  <s:iterator value="navTabList" var="nav" status="index">
	                        <li name="navli" class="active nav<s:property value='#index.index'/>" onclick="clickNavLi(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
                            <li id="pmCLHeader" name="navli" class="nav<s:property value='#index.index'/>" onclick="clickNavLi(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
                            <li name="navli" class="nav<s:property value='#index.index'/>" onclick="clickNavLi(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/><span class="badge "><s:property value="instructionList.size()"/></span></a></li>
                            <li name="navli" class="nav<s:property value='#index.index'/>" onclick="clickNavLi(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/><span class="badge pull-right"><s:property value="weeklyList.size()"/></span></a></li>
                            <li name="navli" class="nav<s:property value='#index.index'/>" onclick="clickNavLi(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
	               </s:iterator> --%>
                    <li name="navli" class="active nav0" onclick="clickNavLi(0, 'shipmentListDiv')"><a href="javascript:void(0)">转包清单</a></li>
                    <li name="navli" class="nav1" onclick="clickNavLi(1, 'engineeFeeDiv')"><a href="javascript:void(0)">工程服务费</a></li>
                    <li name="navli" class="nav2" onclick="clickNavLi(2, 'subcontractPaymentDiv')"><a href="javascript:void(0)">付款信息</a></li>
                    <li name="navli" class="nav3" onclick="clickNavLi(3, 'subcontractDeliverListDiv')"><a href="javascript:void(0)">附件列表</a></li>
                    <li name="navli" class="nav4" onclick="clickNavLi(4, 'subcontractCallbackDiv')"><a href="javascript:void(0)">服务回访</a></li>
                    <li name="navli" class="nav5" onclick="clickNavLi(5, 'subcontractCommentListDiv')"><a href="javascript:void(0)">流程记录</a></li>
                </ul>
	        </div>
	    </nav>
		<!-- <fieldset id="subcontractLines">
			<legend><b>转包清单</b><button id="choiceShipemnt" type="button" class="btn btn-xs btn-success pull-right" onclick="chooseShipmentInfo()">序列号选择</button></legend> -->
			<div class="navDiv shipmentListDiv">
			<div id="shipmentListDiv">
			    <button id="choiceShipemnt" type="button" class="btn btn-xs btn-success pull-right" onclick="chooseShipmentInfo()">序列号选择</button>
				<display:table style="text-align: left;"
		            name="subcontractLineList" pagesize="${subcontractLineList.size()}" export="false" id="shipmentInfoTable"
		            size="${subcontractLineList.size()}" sort="external" requestURI="module/sub/checkShipmentInfo.action"
		            decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-condensed table-hover table-striped" 
		            partialList="true" >
		            <%-- <display:column property="checkboxWrapper" headerClass="warning" titleKey="pm.shipment.check"></display:column> --%>
		            <display:column property="contractNo" headerClass="warning" titleKey="pm.shipment.contractNo"></display:column>
		            <display:column property="barCode" headerClass="warning" titleKey="pm.shipment.barCode"></display:column>
		            <display:column property="itemCode" headerClass="warning" titleKey="pm.shipment.itemCode"></display:column>
		            <display:column property="itemName" headerClass="warning" titleKey="pm.shipment.itemName"></display:column>
		            <%-- <display:column property="transferFlagWrapper" headerClass="warning" titleKey="pm.project.transferFlag"></display:column> --%>
		        </display:table>
			</div>
			</div>
		<!-- </fieldset> -->
	</s:form>
	<!-- <fieldset>
        <legend><b>工程服务费</b></legend> -->
        <div id="engineeFeeDiv" class="navDiv hideDiv">
        </div>
    <!-- </fieldset> -->
    <!-- <fieldset>
        <legend><b>付款信息</b></legend> -->
        <div id="subcontractPaymentDiv" class="navDiv hideDiv">
        </div>
    <!-- </fieldset> -->
	<!-- <fieldset>
        <legend><b>项目附件</b></legend> -->
        <div id="subcontractDeliverListDiv" class="navDiv hideDiv">
            <display:table style="text-align: left;"
                name="subcontractDeliverList" pagesize="${subcontractDeliverList.size()}" export="false" id="subcontractDeliverTable"
                size="${subcontractDeliverList.size()}" sort="external"
                decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-condensed table-hover table-striped" 
                partialList="true" >
                <%-- <display:column property="checkboxWrapper" headerClass="warning" titleKey="pm.shipment.check"></display:column> --%>
                <display:column property="fileName" headerClass="warning" titleKey="file.name"></display:column>
                <display:column property="type" headerClass="warning" titleKey="file.name"></display:column>
                <display:column property="uploadBy" headerClass="warning" titleKey="file.uploadby"></display:column>
                <display:column property="uploadTime" headerClass="warning" titleKey="file.uploadtime" format="{0,date,yyyy-MM-dd HH:mm:ss}"></display:column>
                <%-- <display:column property="transferFlagWrapper" headerClass="warning" titleKey="pm.project.transferFlag"></display:column> --%>
            </display:table>
        </div>
        <%-- <table class="table table-condensed table-hover table-striped">
            <thead>
            <tr class="warning">
                <td><s:text name="file.name"></s:text></td>
                <td><s:text name="file.uploadby"></s:text></td>
                <td><s:text name="file.uploadtime"></s:text></td>
            </tr>
            </thead>
            <tbody>
            <s:if test="subcontract.fileParams.size() == 0">
                <tr>
                    <td colspan="8">
                        无可以显示的数据
                    </td>
                </tr>
            </s:if>
            <s:iterator value="subcontract.fileParams" var="f">
                <tr>
                    <td>
                        <s:if test="#f.path == 0">
                            <a href="module/download.action?fileId=<s:property value='#f.id'/>"><s:property value="#f.fileName"/></a>
                        </s:if>
                        <s:else>
                            <a href="http://sms.dptech.com/module/DocumentDownloadForPMS.action?id=<s:property value='subcontract.lendInfoId'/>&projectCode=<s:property value='subcontract.projectCode'/>&flag=<s:property value='#s.index'/>">
                                <s:property value="#f.fileName"/>
                            </a>
                        </s:else>
                    </td>
                    <td><s:property value="#f.uploadBy"/></td>
                    <td><s:date name="#f.uploadTime" format="yyyy-MM-dd HH:mm"></s:date></td>
                </tr>   
            </s:iterator>
            </tbody>
        </table> --%>
    <!-- </fieldset> -->
   <!--  <fieldset>
        <legend><b>服务回访</b></legend> -->
        <div id="subcontractCallbackDiv" class="navDiv hideDiv">
        </div>
        <div id="subcontractCommentListDiv" class="navDiv hideDiv">
        </div>
 <!--    </fieldset> -->
</body>
</html>
