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
    </script>
  </head>
  <body>
  	<s:form action="module/sub/presales_upload.action" name="uploadFrom" enctype="multipart/form-data" method="post">
		<s:hidden name="projectDeliver.projectId"></s:hidden>
        <s:hidden name="projectDeliver.dataTypeCode"></s:hidden>
        <s:hidden name="projectDeliver.basicDataId"></s:hidden>
        <s:hidden name="projectDeliver.column010"></s:hidden>
        <s:hidden name="projectDeliver.column011"></s:hidden>
        <s:hidden name="projectDeliver.contractNo"></s:hidden>
        <s:hidden name="redirect"></s:hidden>
        <div class="col-sm-10">
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
                            <s:file name="projectDeliverList[%{#u.index}].uploaddelivery" label="File" cssClass="filetext"></s:file>
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
			<button type="submit" class="btn btn-info">
	        	<span class="glyphicon glyphicon-upload"></span> 确认上传
	        </button>
		</div>
  	</s:form>
  </body>
</html>