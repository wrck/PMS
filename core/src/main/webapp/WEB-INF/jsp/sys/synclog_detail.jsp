<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %> 
<%@taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="system.title" /></title>
<cssTag>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css">
	
	<style>
	   .table#synclogbase>tbody th {
	       background-color: #f9f9f9;
	   }
	</style>
</cssTag>
</head>
<body>
	<!-- Content Header (Page header) -->
	<section class="content-header">
	    <h1>
	        <span>数据同步日志管理</span>
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
	                      <table id="synclogbase" class="table table-bordered"></table>
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
	                       <button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">返回</button>
	                   </div>
	                </div>
	            </div>
	        </div>
	    </div>
	</section>
</body>
<jsTag>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
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
					var syncLog = result.syncLog;
					var temp = {};
					for(var key in syncLog) {
						var value = syncLog[key] ? syncLog[key] : "";
						if (key == "exception" && value) {
							value = "<pre>" + value + "</pre>";
							//$("#exceptionDetail").html(syncLog[key]);
						} else if (key == "syncParams" && value) {
							try {
								var obj = JSON.parse(syncLog[key]);
								value = JSON.stringify(obj, null, 4);
	                            //$("#params").html(JSON.stringify(obj, null, 4));
							} catch(e) {
								//$("#params").html(syncLog[key]);
							}
							value = "<pre>" + value + "</pre>";
						} else if (key == "syncType"){
							switch (value) {
								case 0:value = "更新业务表";break;
								case 1:value = "全量同步";break;
								case 2:value = "增量同步";break;
							}
							$(".content-header h1 small").html("查看【" + value + " - " + syncLog.id + "】");
						}
						if (key.indexOf("exception") >= 0) {
                            temp[key] = value;
                            continue;
                        }
						$("#synclogbase").append("<tr><th>" + key +"</th>" + "<td>" + value + "</td></tr>");
					}
					for (var key in temp) {
						$("#synclogbase").append("<tr><th>" + key +"</th>" + "<td>" + temp[key] + "</td></tr>");
					}
				});
			}

			$("[data-btn-type='cancel']").click(function() {
                window.history.back();
            })
		});
	</script>
</jsTag>
</html>