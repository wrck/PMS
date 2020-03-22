<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html lang="zh-CN">
<head>
<dp:base />
<meta name="menu" content="SysMenuGroupStaticDataManage">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.basemanage' />">
<meta name="function" content="<s:text name='fnd.basic.data' />">
<script type="text/javascript">
	$(function(){
		date_picker("effectiveTo");
		date_picker("effectiveFrom");
	});
	
	function checkCode(){
		var basicId = $("#basicDataId").val();
		var dataTypeCode = $("#basicDataTypeCode").val();
		$.ajax({
			url:"findbasicdataid.action",
			type:"post",
			dataType:"json",
			data:{dataTypeCode:dataTypeCode,basicDataId:basicId},
			success:function(data){
				var result = data.result;
				if(result != 0){
					$("#basicDataId").val("");
					$("#tishi").text("该编码重复，请重新输入，谢谢");
					$("#tishi").css("color","red");
				}else{
					$("#tishi").text("可以使用");
					$("#tishi").css("color","green");
				}
			}
		});
	}
	function submitAdd(){
		var basicId = $("#basicDataId").val();
		if(basicId == ""){
			alert("编码不能为空，请输入！");
			 $("#basicDataId").focus();
			return false;
		}
		var basicDataName = $("#basicDataName").val();
		if(basicDataName == ""){
			alert("名称不能为空，请输入！");
			$("#basicDataName").focus();
			return false;
		}
		$("#mainForm").submit();
	}
	
</script>
</head>
<body>
	<div class="divHeader div-bottom" >
	<img src="images/right_zhishi.gif" border="0">
				<s:text name="fnd.basic.data.add"></s:text>
	</div>
	<s:form id="mainForm" action="base/BasicdataInsert.action" method="post" cssClass="form-horizontal">
		<div class="panel panel-default">
   			<div class="panel-body">
      			<div class="form-group">
      				<label  class="col-sm-1 control-label"><s:text name="fnd.basic.data.type"></s:text><span>:</span></label>
				    <div class="col-sm-4">
				      	<s:select name="basicData.basicDataTypeCode" list="basicDataTypeList" listKey="basicDataTypeCode" id="basicDataTypeCode"
				      		listValue="basicDataTypeName" headerKey="" headerValue="--请选择--" cssClass="form-control"></s:select>
				    </div>
      			</div>
      			<div class="form-group">
      				<label  class="col-sm-1 control-label"><s:text name="fnd.basic.data.code"></s:text><span>:</span></label>
				    <div class="col-sm-4">
				      	<s:textfield name="basicData.basicDataId"  id="basicDataId" onblur="checkCode()" cssClass="form-control"/>
				    </div>
				    <span id="tishi"></span>
      			</div>
      			<div class="form-group">
      				<label  class="col-sm-1 control-label"><s:text name="fnd.basic.data.name"></s:text><span>:</span></label>
				    <div class="col-sm-4">
				      	<s:textfield name="basicData.basicDataName" id="basicDataName" cssClass="form-control"/>
				    </div>
      			</div>
      			<div class="form-group">
      				<label  class="col-sm-1 control-label"><s:text name="fnd.basic.data.sort"></s:text><span>:</span></label>
				    <div class="col-sm-4">
				      	<s:textfield name="basicData.sortId" id="sortId" cssClass="form-control"/>
				    </div>
      			</div>
      			<div class="form-group">
      				<label  class="col-sm-1 control-label"><s:text name="fnd.basic.data.effectiveFrom"></s:text><span>:</span></label>
				    <div class="col-sm-4">
				      	<s:textfield name="basicData.effectiveFrom" id="effectiveFrom" cssClass="form-control"/>
				    </div>
      			</div>
      			<div class="form-group">
      				<label  class="col-sm-1 control-label"><s:text name="fnd.basic.data.effectiveTo"></s:text><span>:</span></label>
				    <div class="col-sm-4">
				      	<s:textfield name="basicData.effectiveTo" id="effectiveTo" cssClass="form-control"/>
				    </div>
      			</div>
      		</div>
      	</div>
      	<div class="form-group">
        <label  class="col-sm-1 control-label">&nbsp;</label>
	    <div class="col-sm-1">
	      <button type="button" id="submitButton" onclick="submitAdd()" class="btn btn-default  btn-block btn-sm form-control"><s:text name='sys.confirm' /></button>
	    </div>
	     <div class="col-sm-1">
	      <button type="button" onclick="javascript:history.go(-1)"  class="btn btn-default  btn-block btn-sm form-control"><s:text name='sys.back' /></button>
	    </div>
    </div>		
	</s:form> 
</body>
</html>