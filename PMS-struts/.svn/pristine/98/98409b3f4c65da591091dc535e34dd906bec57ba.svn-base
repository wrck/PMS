<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<html>
<head>
<dp:base />
<script type="text/javascript">
	//上传附件jS
    $(function() {
    	$("#uploadButton").click(function() {
    		var isAjax = ${isAjax};
    		if (isAjax) {
    			ajaxSubmit();
    		} else {
    			$("form").submit();
    		}
    	});
    });
	function morefile(e) {
		var upload = $("#uploaddivhidden").clone(true);
		$("#add").after(upload.html());
	}
	function delfile(_this) {
		$(_this).prev().prev().remove();
		$(_this).prev().remove();
		$(_this).remove();
	}
	function ajaxSubmit() {
		$.ajax({
			url : $("base").attr("href") + '/ajax/upload.action',
			type : 'POST',
			cache: false,
	        data: new FormData($('#upload')[0]),
			contentType : false, //禁止设置请求类型
			processData : false, //禁止jquery对DAta数据的处理,默认会处理
			//禁止的原因是,FormData已经帮我们做了处理
			success : function(result) { //成功
			    var fileNameArr = [];
			    try {
			    	$("#uploaddiv input[type='file']").each(function() {
	                    var fileName = $(this).val();
	                    if (fileName) {
	                        fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
	                        fileNameArr.push(fileName);
	                    }
	                });
			    } catch(e) {}
			    if (window.parent.uploadCallback) {
			        window.parent.uploadCallback(result.fileIds, fileNameArr);
			    }
			},
			error: function() {
				alert("上传失败！");
			}
		});
	}
</script>
</head>
<body>
	<s:form action="module/sub/upload.action" name="uploadFrom"
		enctype="multipart/form-data" method="post">
		<s:hidden name="redirect"></s:hidden>
		<div class="col-sm-10">
			<div id="uploaddiv">
				<s:file name="upload" id="upload" label="File" cssClass="filetext"></s:file>
				<img class="img" id="add" src="images/plus3.gif"
					onclick="morefile(this);" />
			</div>
		</div>
		<div id="uploaddivhidden" style="display: none;">
			<s:file name="upload" label="File" cssClass="filetext"></s:file>
			<img class="img" src="images/plus3.gif" onclick="morefile(this);" />
			<img class="img" src="images/minus5.gif" onclick="delfile(this);" />
		</div>
		<br />
		<br />
		<button id="uploadButton" type="button" class="btn btn-info" style="margin-left: 20px;">
			<span class="glyphicon glyphicon-upload"></span> 确认上传
		</button>
	</s:form>
</body>
</html>