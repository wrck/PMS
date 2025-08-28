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
	   .table#syslogbase>tbody th {
	       background-color: #f9f9f9;
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
	                      <table id="syslogbase" class="table table-bordered"></table>
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
					var sysLog = result.sysLog;
					$(".content-header h1 small").html(
							"查看【" + sysLog.description + " - " + sysLog.id + "】");
					var temp = {};
					for(var key in sysLog) {
						var exclueArray = ["id", "exception_detail", "params"];
						/* if ($.inArray(key, exclueArray) >= 0) {
							continue;
						} */
						var value = sysLog[key] ? sysLog[key] : "";
						if (key == "exceptionDetail" && value) {
							//value = "<pre>" + value + "</pre>";// 如果存在<>会解析为html标签，故改用text进行转义
							value = $("<pre></pre>").text(value)[0].outerHTML;
							//$("#exceptionDetail").html(sysLog[key]);
						} else if (key == "params") {
							try {
								var obj = JSON.parse(sysLog[key]);
								value = JSON.stringify(obj, null, 4);
	                            //$("#params").html(JSON.stringify(obj, null, 4));
							} catch(e) {
								//$("#params").html(sysLog[key]);
							}
							//value = "<pre>" + value + "</pre>";// 如果存在<>会解析为html标签，故改用text进行转义
							value = $("<pre></pre>").text(value)[0].outerHTML;
						} else {
							//if (key == "type" && sysLog[key] == 1) {
							//	$("#exception").show();
							//}
							//$("#syslogbase").append("<tr><th>" + key +"</th>" + "<td>" + (sysLog[key] ? sysLog[key] : "") + "</td></tr>");
						}
						if (key.indexOf("exception") >= 0) {
                            temp[key] = value;
                            continue;
                        }
						$("#syslogbase").append("<tr><th>" + key +"</th>" + "<td>" + value + "</td></tr>");
					}
					for (var key in temp) {
						$("#syslogbase").append("<tr><th>" + key +"</th>" + "<td>" + temp[key] + "</td></tr>");
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