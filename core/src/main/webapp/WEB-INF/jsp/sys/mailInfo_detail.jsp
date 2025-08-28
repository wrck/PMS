<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %> 
<%@taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib  prefix="dp" uri="/myTag" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="system.title" /></title>
<cssTag>
	<dp:link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css" />
	
	<style>
	   .table#mailInfobase>tbody th {
	       background-color: #f9f9f9;
	       /* width: 200px; */
	   }
	   
	   .table#mailInfobase>tbody td input {
	       /* width: 100%; */
	   }
	</style>
</cssTag>
</head>
<body>
	<!-- Content Header (Page header) -->
	<section class="content-header">
	    <h1>
	        <span>系统运行日志管理</span>
	        <small>查看</small>
	    </h1>
	    <ol class="breadcrumb">
	    </ol>
	</section>
	<section class="content">
	    <div class="row">
	        <div class="col-xs-12">
	            <div class="box box-info">
	                <div class="box-header">
	                    <div class="col-xs-12">
		                    <table id="mailInfobase" class="table table-bordered"></table>
	                    </div>
	                </div>
	               <!--  <div class="box-body">
                        <div class='col-xs-12'>
                            <div>
                                <h4>参数值：</h4>
                                <pre id="params">
                                </pre>
                            </div>
                            <div id="exception" style="display:none;">
                                <h4>异常信息：</h4>
                                <pre id="exceptionDetail">
                                </pre>
                            </div>
	                   </div>
	                </div> -->
	                <div class="box-footer">
	                   	<div class='col-xs-12'>
	                       	<button type="button" class="btn btn-default" data-btn-type="back" data-dismiss="modal">返回</button>
						   	<button type="button" id="invalidBtn" class="btn btn-primary ml-1" data-btn-type="cancel" style="display:none" data-dismiss="modal">失效</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</section>
</body>
<jsTag>
	<dp:script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></dp:script>
	<script>
	    //tableId,queryId,conditionContainer
	    var form = null;
	   // var id = "{id?default(0)}";
	    var id = "${id!=0 && id!=null?id:0}";
	    $(function () {
			//回填id
			if (id != "0") {
				ajaxPost(id + ".json", {
					id : id
				}, function(result) {
					var mailInfo = result.mailInfo;
					$(".content-header h1 small").html(
							"查看【" + mailInfo.description + " - " + mailInfo.id + "】");
					var temp = {};
					var exclueArray = ["id", "sendTime", "createBy", "createTime", "actualSendAddress", "attachFiles"];
					for(var key in mailInfo) {
						/* if ($.inArray(key, exclueArray) >= 0) {
							continue;
						} */
						var value = mailInfo[key] != null ? mailInfo[key] : "";
						if (key == 'failedMessage' && value && typeof renderFailedMessage != 'undefined') {
							value = renderFailedMessage(value);
						}
						$("#mailInfobase").append("<tr><th>" + key +"</th>" + "<td>" + value + "</td></tr>");
						/* if ($.inArray(key, exclueArray) >= 0) {
							$("#mailInfobase").append("<tr><th>" + key +"</th>" + "<td>" + value + "</td></tr>");
						} else {
							$("#mailInfobase").append("<tr><th>" + key +"</th>" + "<td><input name='" + key + "' value='" + value + "' class='form-control'></td></tr>");
						} */
						if (key == 'sendFlag' && !value && mailInfo['expectSendTime']) {
							$("#invalidBtn").show();
						}
					}
					for (var key in temp) {
						var value = temp[key] != null ? temp[key] : "";
						$("#mailInfobase").append("<tr><th>" + key +"</th>" + "<td>" + value + "</td></tr>");
						/* if ($.inArray(key, exclueArray) >= 0) {
							$("#mailInfobase").append("<tr><th>" + key +"</th>" + "<td>" + value + "</td></tr>");
						} else {
							$("#mailInfobase").append("<tr><th>" + key +"</th>" + "<td><input name='" + key + "' value='" + value + "' class='form-control'></td></tr>");
						} */
					}
				});
			}

			$("[data-btn-type='back']").click(function() {
                window.history.back();
            });
			
			$("[data-btn-type='cancel']").click(function() {
				if (id != "0") {
					$(this).hide();
                	ajaxPost(basePath + "/sys/mailInfo/invalid.json", {id: id}, function(data) {
                		if (data.status) {
                			modals.info({
               					text:"操作成功！",
               					cancel_call: function () {
            						window.location.reload();
           						}
                			});
                		} else {
                			modals.error(data.message);
                		}
                	})
				}
            })
		});

	    function renderFailedMessage(errorIds) {
	    	errorIds = errorIds || "";
	    	var idArr = errorIds.split(",");
	    	var html = "";
	    	for(var i in idArr) {
	    		var id = idArr[i];
	    		if (id) {
	    			html += "<a class='btn btn-xs btn-success' href='" + basePath +"/sys/syslog/"+id+".html'><i class='icon-ok'></i>错误日志" + id + "</a>";
	    		}
	    	}
	    	return html;
	    }
	</script>
</jsTag>
</html>