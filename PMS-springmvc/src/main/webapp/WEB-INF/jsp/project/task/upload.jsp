<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="myTag" uri="/myTag"%>
<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true"><li class="fa fa-remove"></li></button>
		<h5 class="modal-title">新增评论</h5>
	</div>
	<div class="modal-body">
	  	<mvc:form modelAttribute="projectDeliver" action="task/uploadDeliverFile.html" name="uploadFrom" enctype="multipart/form-data" method="post">
	  		<input type="hidden" name="__RequestVerificationToken" value="${__RequestVerificationToken}">
	  		<mvc:hidden path="projectId"></mvc:hidden>
	  		<mvc:hidden path="projectType"></mvc:hidden>
	  		<mvc:hidden path="taskId"></mvc:hidden>
	  		<mvc:hidden path="eventKey"></mvc:hidden>
	  		<mvc:hidden path="dataTypeCode"></mvc:hidden>
	  		<mvc:hidden path="basicDataId"></mvc:hidden>
	  		<mvc:hidden path="column010"></mvc:hidden>
	  		<mvc:hidden path="column011"></mvc:hidden>
	  		<mvc:hidden path="contractNo"></mvc:hidden>
			<div class="box-body">
				<table>
					<c:forEach items="${projectDeliverList }" var="pd" varStatus="u">
						<tr style="height: 40px;">
							<td width="200px">
								<c:if test="${pd.isNeed==1}">
									<span class="redmark">*</span>
								</c:if>
				 				${pd.deliverValue}
				 			</td>
				 			<td>
								<input type="hidden" name="deliverId" value="${pd.id}">
	                            <input type="hidden" name="deliverTypes" value="${pd.deliverValue}">
								<input type="file" class="custom-file-input form-control" name="deliverFiles" multiple="multiple" label="File" cssClass="filetext">
							</td>
						</tr>
					</c:forEach>
				</table>
		 	 </div>
	  	</mvc:form>
	</div>
	<div class="modal-footer text-right">
		<button type="button" class="btn btn-info" data-btn-type="submit">
        	<span class="glyphicon glyphicon-upload"></span> 确认上传
        </button>
	</div>

  <script type="text/javascript">
  	$(function() {
  		$('button[data-btn-type]').click(function(e) {
  			var action = $(this).attr('data-btn-type');
            switch (action) {
            case 'submit':
            	var myform = new FormData();
            	$("#projectDeliver").find(":input").not("[name='deliverId'],[name='deliverTypes']").each(function() {
            	    var name = this.name;
            	    var value = this.value;
            	    var type = this.type;
            	    if (type == 'file') {
            	        var files = this.files;
            	        var $prev = $(this).prev();
            	        var deliverTypeName = $prev.attr("name");
            	        var deliverType = $prev.val();
            	        var deliverIdName = $prev.prev().attr("name");
            	        var deliverId = $prev.prev().val();
            	        for(var i=0; i<files.length; i++) {
            	            var file = files[i];
            	            myform.append(name, file);
            	            myform.append(deliverTypeName, deliverType);
            	            myform.append(deliverIdName, deliverId);
            	        }
            	    } else if(name) {
            	        myform.append(name, value);
            	    }
            	});
            	ajaxupload(myform, router("/pm/").api("projectTask").upload(), function(data) {
            		modals.hideWin("uploadDeliverFileWin");
            	})
           	}
  		})
  	});
  </script>
