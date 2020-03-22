<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<html>
<head>
<dp:base />
<style type="text/css">
input[type=file]{
	display: inline-block;
}
</style>
    <script>
	//上传附件jS
	$(function() {
        $("#uploadButton").click(function() {
            var message = "${message}";
            if (message == "isAjax") {
                ajaxSubmit();
            } else if (message == 'commonUpload') {
            	ajaxSubmitCommomUnpload();
            } else if (message == "returnForm") {
            	if(window.parent.returnFormCallback) {
            		window.parent.returnFormCallback($("#uploadForm"));
            	}
            } else {
                $("form").submit();
            }
        });
    });
	function morefile(e){
		var upload = $("#uploaddivhidden").clone(true);
		$(e).next().after(upload.html());
        var prevname = $(e).prevAll("input[name*='deliverableType']:last").attr("name");
        var deliverableType = $(e).prevAll("input[name*='deliverableType']:last").val();
        $(e).nextAll("input[name*='deliverableType']:first").attr("name", prevname);
        $(e).nextAll("input[name*='deliverableType']:first").val(deliverableType);
        
        prevname = $(e).prevAll("input[name*='uploaddelivery']:last").attr("name");
        $(e).nextAll("input[name*='uploaddelivery']:first").attr("name", prevname);
	}
	function delfile(_this){
        $(_this).prev().prev().prev().remove();
		$(_this).prev().prev().remove();
		$(_this).prev().remove();
		$(_this).remove();
	}
	function initCommonUploadForm() {
        var formData = new FormData();
        $("input[name*='uploaddelivery']").each(function() {
        	var files = $(this)[0].files;
        	if (files.length) {
        		var fileType = $.trim($(this).prev().val());
        		for(var file of files) {
            		formData.append("upload", file);
            		formData.append("uploadFileType", fileType);
            	}
        	}
        })
        return formData;
	}
	function ajaxSubmitCommomUnpload() {
        $.ajax({
            url : $("base").attr("href") + '/ajax/upload.action',
            type : 'POST',
            cache: false,
            data: initCommonUploadForm(),
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
  	<s:form id="uploadForm" action="module/sub/UploadDeliverableFile.action" name="uploadFrom" enctype="multipart/form-data" method="post">
  		<s:hidden name="projectDeliver.projectId"></s:hidden>
  		<s:hidden name="projectDeliver.dataTypeCode"></s:hidden>
  		<s:hidden name="projectDeliver.basicDataId"></s:hidden>
  		<s:hidden name="projectDeliver.column010"></s:hidden>
  		<s:hidden name="projectDeliver.column011"></s:hidden>
  		<s:hidden name="projectDeliver.contractNo"></s:hidden>
		<div id="uploaddiv" class="col-sm-10">
			<table>
				<s:iterator value="projectDeliverList" id="pd" status="u">
					<tr style="height: 40px;">
						<td>
							<input type="hidden" name="projectDeliver.deliverId" value="<s:property value='#pd.id'/>">
						</td>
						<td width="200px">
							<s:if test="#pd.isNeed==1">
								<span class="redmark">*</span>
							</s:if>
							<s:else>
								<span class="blackmark">*</span>
							</s:else>
			 				<s:property value="#pd.deliverValue"/>
			 			</td>
			 			<td>
                            <s:hidden name="projectDeliverList[%{#u.index}].deliverableType" value="%{#pd.deliverValue}"></s:hidden>
							<s:file name="projectDeliverList[%{#u.index}].uploaddelivery" label="File" cssClass="filetext" multiple="multiple"></s:file>
							<img class="img" name="add" src="images/plus3.gif" onclick="morefile(this);" />
							<img class="img" src="images/minus5.gif" onclick="delfile(this);" style="display: none;" />
						</td>
					</tr>
				</s:iterator>
			</table>
	 	 </div>
	 	 <div id="uploaddivhidden" style="display: none;">
                <s:hidden name="deliverableType"></s:hidden>
				<s:file name="uploaddelivery" label="File" cssClass="filetext"></s:file>
				<img class="img" src="images/plus3.gif" onclick="morefile(this);" />
				<img class="img" src="images/minus5.gif" onclick="delfile(this);" />
		</div>
		<br/><br/>
		<div class="col-sm-10">
			<button id="uploadButton" type="button" class="btn btn-info">
	        	<span class="glyphicon glyphicon-upload"></span> 确认上传
	        </button>
		</div>
  	</s:form>
  </body>
</html>