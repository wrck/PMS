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
		$("#effectiveFrom").val($("#hide_effectiveFrom").text());
		var effectiveTo = $("#hide_effectiveTo").text();
		if(effectiveTo != ""){
			$("#effectiveTo").val(effectiveTo);
		}
	});
</script>
</head>
<body>
	<div class="divHeader div-bottom" >
	<img src="images/right_zhishi.gif" border="0">
				<s:text name="fnd.basic.data.update"></s:text>
	</div>
	<s:form id="mainForm" action="base/BasicdataUpdate.action" method="post" cssClass="form-horizontal">
		<s:hidden name="basicData.id"></s:hidden>
		<s:hidden name="basicData.basicDataTypeCode"></s:hidden>
		<div class="panel panel-default">
   			<div class="panel-body">
      			<div class="form-group">
      				<label  class="col-sm-1 control-label"><s:text name="fnd.basic.data.type"></s:text><span>:</span></label>
				    <div class="col-sm-4">
				      	<s:textfield name="basicData.basicDataTypeName" readonly="true" cssClass="form-control"/>
				    </div>
      			</div>
      			<div class="form-group">
      				<label  class="col-sm-1 control-label"><s:text name="fnd.basic.data.code"></s:text><span>:</span></label>
				    <div class="col-sm-4">
				      	<s:textfield name="basicData.basicDataId" readonly="true" cssClass="form-control"/>
				    </div>
      			</div>
      			<div class="form-group">
      				<label  class="col-sm-1 control-label"><s:text name="fnd.basic.data.name"></s:text><span>:</span></label>
				    <div class="col-sm-4">
				      	<s:textfield name="basicData.basicDataName" cssClass="form-control"/>
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
				      	<s:textfield name="basicData.effectiveFrom" id="effectiveFrom" readonly="true" cssClass="form-control"/>
				      	<span id="hide_effectiveFrom" class="hideMark"><s:date name="basicData.effectiveFrom" format="yyyy-MM-dd"/></span>
				    </div>
      			</div>
      			<div class="form-group">
      				<label  class="col-sm-1 control-label"><s:text name="fnd.basic.data.effectiveTo"></s:text><span>:</span></label>
				    <div class="col-sm-4">
				      	<s:textfield name="basicData.effectiveTo" id="effectiveTo" cssClass="form-control"/>
				      	<span id="hide_effectiveTo" class="hideMark"><s:date name="basicData.effectiveTo" format="yyyy-MM-dd"/></span>
				    </div>
      			</div>
      		</div>
      	</div>
      	<div class="form-group">
        <label  class="col-sm-1 control-label">&nbsp;</label>
	    <div class="col-sm-1">
	      <button type="submit" id="submitButton"  class="btn btn-default  btn-block btn-sm form-control"><s:text name='sys.confirm' /></button>
	    </div>
	     <div class="col-sm-1">
	      <button type="button" onclick="javascript:history.go(-1)"  class="btn btn-default  btn-block btn-sm form-control"><s:text name='sys.back' /></button>
	    </div>
    </div>		
	</s:form> 
</body>
</html>