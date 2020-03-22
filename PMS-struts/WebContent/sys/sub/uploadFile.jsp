<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<html>
<dp:base />
  <head>
  	<script type="text/javascript" src="js/jquery-2.1.4.min.js"></script> 
    <script>
	//上传附件jS
	
	function morefile(e){
		var upload = $("#uploaddivhidden").clone(true);
		$("#add").after(upload.html());
	}
	function delfile(_this){
		$(_this).prev().prev().remove();
		$(_this).prev().remove();
		$(_this).remove();
	}
    </script>
  </head>
  <body>
  	<s:form action="module/sub/UploadFile.action" name="uploadFrom" enctype="multipart/form-data" method="post">
  		<s:hidden name="projectWeekly.weeklyId"></s:hidden>
  		<s:hidden name="projectWeekly.projectId"></s:hidden>
		<div class="col-sm-10">
		 	<div id="uploaddiv">
				<s:file name="upload" id="upload" label="File"
						cssClass="filetext"></s:file>
					<img class="img" id="add" src="images/plus3.gif"
						onclick="morefile(this);" />
			</div>
	 	 </div>
	 	 <div id="uploaddivhidden" style="display: none;">
				<s:file name="upload" label="File" cssClass="filetext"></s:file>
				<img class="img" src="images/plus3.gif"
					onclick="morefile(this);" /> <img class="img"
					src="images/minus5.gif" onclick="delfile(this);" />
		</div>
		<br/><br/>
		<button type="submit" class="btn btn-info">
          <span class="glyphicon glyphicon-upload"></span> 确认上传
        </button>
  	</s:form>
  </body>
</html>