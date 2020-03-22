<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<html>
<dp:base />
<head>
<script type="text/javascript">
/*
保存安装地址		
*/
function shipformSubmit(){
	if(!checkshipform()){
		return false;
	}
	var projectId = $("#projectId").val();
	$.ajax({
		url :"<%=request.getContextPath()%>/SaveInstallAdress.action",
		type :"post",
		dataType :"json",
		data : $("#shipmentForm").serialize(),
		success:function(data){
			var result = data.result;
			if(result == 303){
				alert("更新成功!");
				window.location.reload(true);
				//parent.closeWindow("BudgetUpload");
			}else{
				alert("更新失败，请联系管理员!");
			}
		}
	});
}

function checkshipform(){
	var receiveAddress = $("#receiveAddress").val();
	if(receiveAddress.trim() == ""){
		$("#receiveAddress").focus();
		alert("请填写安装地址信息");
		return false;
	}
	var sel = "input[name='selected']";
	for(var i = 0;i < $(sel).length;i++){
		if($(sel).eq(i).is(":checked")){
			return true;
		}
	}
	alert("请至少选择一条序列号信息");
	return false;
}
/**
 * 删除安装信息
 */
function deleteShipmentInfo() {
	$.ajax({
        url:'projectAjax_updateProjectExecutionState.action',
        type:'post',
        dataType:'json',
        data: $("#shipmentForm").serialize(),
        success: function(data) {
            if (data.result == 313) {
                location.reload();
            } else {
                alert("删除失败!");
            }
        },
        error: function(err) {
            alert("删除失败！错误信息：\r" + JSON.stringify(err));
        }
    });
}

</script>
</head>
<body>
	<s:form cssClass="form-inline" id="shipmentForm">
	        <!-- <div style="text-align: right;"> -->
            <%-- <s:if test="%{project.projectState != '100' || (project.projectState == '100' && (user.isHasRole(13) || user.isHasRole(1)))}"><!-- 控制闭环状态不能再做操作 -->
                <div class="form-group">
                    <label for="receiveAddress"><span class="redmark">*</span><s:text name="pm.project.receiveAddress" />:</label>
                    <textarea class="form-control" rows="1" name="installAddress" id="receiveAddress" cols="100"></textarea>
                    <button type="button" onclick="shipformSubmit()" class="btn btn-default btn-block" style="width: 60px;display: inline-block;"><s:text name="pm.project.btn"></s:text></button>
                    <s:if test="user.isHasRole(13) || user.isHasRole(1) || user.areapower.contains(project.column001)">
                        <a onclick="deleteShipmentInfo(this)" class="btn btn-danger" href="javascript:void(0)"><span class="glyphicon glyphicon-retweet"></span> 删除安装信息</a>
                        
                        /**
                         * 删除安装信息
                         */ 
                        
                        <script>
                        function deleteShipmentInfo(_this) {
                            $.ajax({
                                url:'projectAjax_deleteShipmentInfo.action',
                                type:'post',
                                dataType:'json',
                                data: $("#shipmentForm").serialize(),
                                success: function(data) {
                                    if (data.result == 303) {
                                        checkShipmentInfo();
                                    } else {
                                        alert("删除失败!");
                                    }
                                },
                                error: function(err) {
                                    alert("删除失败！错误信息：\r" + JSON.stringify(err));
                                }
                            });
                        }
                        </script>
                    </s:if>
                </div>
            </s:if> --%>
            <div class='pull-right' style="margin-right: 15px;">
                <a onclick="exportSpotCheck(this)" class="btn btn-success" href="javascript:void(0)"><span class="glyphicon glyphicon-cloud-download"></span> 下载现场验货单</a>
                <s:if test="user.isHasRole(13) || user.isHasRole(1)">
                    <a onclick="importSpotCheckIgnoreItem(this)" class="btn btn-info" href="javascript:void(0)"><span class="glyphicon glyphicon-upload"></span> 导入非明细Item</a>
                    <a onclick="transferShipment(this)" class="btn btn-danger" href="javascript:void(0)"><span class="glyphicon glyphicon-retweet"></span> 转移</a>
                </s:if>
                <s:if test="result > 0 && (project.projectState != '100' || (project.projectState == '100' && (user.isHasRole(13) || user.isHasRole(1)))) && (user.isHasRole(13) || user.isHasRole(1) || user.areapower.contains(project.column001))">
                    <a onclick="deleteShipmentInfo(this)" class="btn btn-danger" href="javascript:void(0)"><span class="glyphicon glyphicon-trash"></span> 删除安装信息</a>
                    <%-- 
                    /**
                     * 删除安装信息
                     */ 
                     --%>
                    <script>
                    function deleteShipmentInfo(_this) {
                        $.ajax({
                            url:'projectAjax_deleteShipmentInfo.action',
                            type:'post',
                            dataType:'json',
                            data: $("#shipmentForm").serialize(),
                            success: function(data) {
                                if (data.result == 303) {
                                    checkShipmentInfo();
                                    alert("安装信息已删除，请重新填写安装地址!");
                                } else {
                                    alert("安装信息删除失败!");
                                }
                            },
                            error: function(err) {
                                alert("安装信息删除失败！错误信息：\r" + JSON.stringify(err));
                            }
                        });
                    }
                    </script>
                </s:if>
		    </div>
			<s:hidden name="projectId" value="%{project.projectId}" ></s:hidden>
            <s:hidden name="project.column001" value="%{project.column001}" ></s:hidden>
			<div style="text-align: left;">
				<display:table style="text-align: left;"
					name="shipmentInfoList" pagesize="${shipmentInfoList.size()}" export="true" id="displaytable3"
					size="${shipmentInfoList.size()}" sort="external" requestURI="module/sub/checkShipmentInfo.action"
					decorator="com.dp.plat.decorators.Wrapper" class="displayTable table"
					partialList="true">
					<display:column property="checkboxWrapper" titleKey="pm.shipment.check" media="html"></display:column>
					<display:column property="contractNo" titleKey="pm.shipment.contractNo"></display:column>
					<display:column property="barCodeRelation" titleKey="pm.shipment.barCode" media="html"></display:column>
					<display:column property="barCode" title='发货序列号' media="excel"></display:column>
					<display:column title='实物序列号' media="excel">
						${empty displaytable3.barCode2 ? displaytable3.barCode : displaytable3.barCode2 }
					</display:column>
					<display:column property="itemCodeRelation" titleKey="pm.shipment.itemCode"></display:column>
					<display:column property="itemNameRelation" titleKey="pm.shipment.itemName"></display:column>
					<display:column property="receiveName" titleKey="pm.shipment.receiveName"></display:column>
					<display:column property="emsNum" titleKey="pm.shipment.emsNum"></display:column>
					<display:column property="packdate" titleKey="pm.shipment.packdate" format="{0,date,yyyy-MM-dd}"></display:column>
					<display:column property="emsCompany" titleKey="pm.shipment.emsCompany"></display:column>
					<display:column property="installAddress" titleKey="pm.project.receiveAddress"></display:column>
					<display:column property="transferFlagWrapper" titleKey="pm.project.transferFlag" media="html"></display:column>
					<display:column titleKey="pm.project.transferFlag" media="excel">
						${displaytable3.transferFlag == 1 ? '已转出' : displaytable3.transferFlag == 0 ? '已转入' : '' }
					</display:column>
					<display:setProperty name="export.excel.filename" value='<%=StringEscUtil.getText("sys.shipment.shipmentinfolistxls")%>'>
					</display:setProperty>
				</display:table>
			</div>
            <s:if test="%{project.projectState != '100' || (project.projectState == '100' && (user.isHasRole(13) || user.isHasRole(1)))}"><!-- 控制闭环状态不能再做操作 -->
                <div class="form-group form-group-query">
                    <label for="receiveAddress"><span class="redmark">*</span><s:text name="pm.project.receiveAddress" />:</label>
                    <textarea class="form-control" rows="3" name="installAddress" id="receiveAddress" cols="100"></textarea>
                    <button type="button" onclick="shipformSubmit()" class="btn btn-default btn-block form-group-query" style="width: 60px;display: inline-block;"><s:text name="pm.project.btn"></s:text></button>
                </div>
            </s:if>
			<div class="backTop">
		        <i class='glyphicon glyphicon-arrow-up'></i>
		    </div>
		    <div class="rollBottom">
		        <i class='glyphicon glyphicon-arrow-down'></i>
		    </div>
		</s:form>
</body>
</html>