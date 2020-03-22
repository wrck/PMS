<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<html>
<head>
<dp:base />
<style type="text/css">
input[type=file] {
	display: inline-block;
}
</style>
<script type="text/javascript">
	$(function(){
		var result =  "${batchChangeResult}";
		if (result) {
		    if (result != "authError") {
		        alert(result);
		    } else {
		    	alert("没有导入权限！");
		    }
		}
	});
</script>
</head>
<body>
	<s:form action="module/sub/importSpotCheckIgnoreItem.action"
		name="uploadFrom" enctype="multipart/form-data" method="post">
		<div class="col-sm-10">
			<div class="form-group text-left">
				<label for="attachments" class="control-label"><s:text name="prob.info.attachments"></s:text></label>
				<input type="file" name="upload" class="form-control" style="width: auto; display: inline-block;" />
			</div>
		</div>
		<br/>
		<div class="col-sm-10">
			<button type="submit" class="btn btn-info">
				<span class="glyphicon glyphicon-upload"></span> 确认上传
			</button>
		</div>
	</s:form>
</body>
</html>