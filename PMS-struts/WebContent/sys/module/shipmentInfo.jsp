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
				parent.location.reload(true);
				parent.closeWindow("BudgetUpload");
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
</script>
</head>
<body>
	<div>
		<div class="row">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
					<div class="panel panel-default">
						<!-- 请求路径改为绝对路径 ，避免因路径问题产生其他资源加载问题
					求绝对路径行号
					${pageContext.request.contextPath}
					<%=request.getContextPath()%>
				 -->
				<form method="post"  id="shipmentForm"
					class="form-horizontal" name="shipmentForm">
						<s:hidden name="projectId"></s:hidden>
						<div class="panel-body">
							<div style="text-align: right;">
								 <display:table style="text-align: left;"
									name="shipmentInfoList" pagesize="${shipmentInfoList.size()}" export="true" id="displaytable3"
									size="${shipmentInfoList.size()}" sort="external" 
									requestURI="${pageContext.request.contextPath }/module/sub/checkShipmentInfo.action"
									decorator="com.dp.plat.decorators.Wrapper" class="displayTable table"
									partialList="true">
									<display:column property="checkboxWrapper" titleKey="pm.shipment.check" media="html"></display:column>
									<display:column property="contractNo" titleKey="pm.shipment.contractNo"></display:column>
									<display:column property="barCode" titleKey="pm.shipment.barCode"></display:column>
									<display:column property="itemCode" titleKey="pm.shipment.itemCode"></display:column>
									<display:column property="itemName" titleKey="pm.shipment.itemName"></display:column>
									<display:column property="receiveName" titleKey="pm.shipment.receiveName"></display:column>
									<display:column property="emsNum" titleKey="pm.shipment.emsNum"></display:column>
									<display:column property="packdate" titleKey="pm.shipment.packdate" format="{0,date,yyyy-MM-dd}"></display:column>
									<display:column property="emsCompany" titleKey="pm.shipment.emsCompany"></display:column>
									<display:column property="installAddress" titleKey="pm.project.receiveAddress"></display:column>
									<display:setProperty name="export.excel.filename" value='<%=StringEscUtil.getText("sys.shipment.shipmentinfolistxls")%>'>
									</display:setProperty>
								 </display:table>
							</div>
							<div class="form-group">
								<label for="receiveAddress" class="col-xs-2 col-sm-2 col-md-2 col-lg-2"><span class="redmark">*</span><s:text name="pm.project.receiveAddress" />:</label>
								<div class="col-xs-8 col-sm-8 col-md-8 col-lg-8">
									<textarea class="form-control" rows="3" name="installAddress" id="receiveAddress" cols="100"></textarea>
								</div>
							</div>
							<div class="form-group">
								<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
									<button type="button" onclick="shipformSubmit()" class="btn btn-default btn-block form-group-query" style="width: 60px;display: inline-block;"><s:text name="pm.project.btn"></s:text></button>
								</div>
							</div>
						</div>
						</form>
					</div>
			</div>
		</div>
	</div>
</body>
</html>