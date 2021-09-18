<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="system.title" /></title>
<cssTag>
	<!-- DataTables -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datatables/media/css/dataTables.bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datatables/extensions/Select/css/select.bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css">
	<style>
		#searchDiv{
			display: none;
			margin-bottom: 1rem;
		}
	</style>
</cssTag>
</head>
<body>
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>系统运行日志管理</h1>
		<ol class="breadcrumb">
		</ol>
	</section>

	<!-- Main content -->
	<section class="content">
		<!-- Default box -->
		<div class="box box-primary">
			<!-- /.box-header -->
			<div class="box-body">
				<div class="row">
					<div class="col-sm-12">
			            <div id="searchDiv" >
			                <div class="form-group">
                                <label>搜索：</label>
                                <input type="search" name="fuzzy" class="form-control" placeholder="日志描述、操作人、请求IP" id="fuzzySearch" style="min-width: 250px;">
                            </div>
                            <div class="form-group">
				            	<label>日志类型：</label>
				                <select class="form-control" id="status-search" name="type" type="search">
				                    <option value="">全部</option>
				                    <option value="0">正常日志</option>
				                    <option value="1">异常日志</option>
				                </select>
			                </div>
			                <div class="btn-group">
								<button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
								<button type="button" class="btn btn-default" data-btn-type="reset">重置</button>
							</div>
						</div>
						<table id="sysLogTable"
							class="table table-bordered table-striped table-hover dataTable">
						</table>
					</div>
				</div>
			</div>
			<!-- /.box-body -->
		</div>
		<!-- /.box -->
	</section>
	<!-- /.content -->
</body>
<jsTag>
	<!-- DataTables -->
	<script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/jquery.dataTables.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/dataTables.bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/dataTablesExt.js"></script>
	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script>
		$(document).ready(function() {
			var commonTable= new CommonTable("sysLogTable", "${pageContext.request.contextPath}/sys/syslog/list.json", "searchDiv",
					{
					searching:false,
					searchInline: true,
					rowId: 'id',
					"columns" : [
			            {
			            	data: "id",
			            	title: "ID",
			            	visible: true,
			            	sortable: true
			            },
						{
							name : "description",
							title : "操作",
							data : "description",
							visible: true,
							sortable: true
						},
						{
							name : "create_by",
							title : "操作人",
							data : "createBy",
							visible: true,
							sortable: true
						},
						{
							name : "create_date",
							title : "操作时间",
							data : "createDate",
							visible: true,
							sortable: true
						},
						{
							title : "日志类型",
							data : "type",
							visible: true,
							sortable: false,
							render : function(data, type, row) {
								if (data == 0) {
									return "正常日志";
								} else{
									return "异常日志";
								}
							}
						},{
							name : "request_ip",
							title : "请求IP",
							data : "requestIp",
							visible: true,
							sortable: false
						}],
					"columnDefs" : [ {
		                // 定义操作列,######以下是重点########
		                targets : 6,//操作按钮目标列
		                data : "id",
		                title: "操作",
		                sortable: false,
		                render : function(data, type, row) {
		                    var id = '"' + row.id + '"';
		                    //<a href='javascript:void(0);'  class='delete btn btn-default btn-xs'  ><i class='fa fa-times'></i> 查看</a>
		                    var html = "<a class='btn btn-xs btn-success' href='syslog/"+data+".html'><i class='icon-ok'></i>查看详情</a>"
		                    //html += "<a class='btn btn-xs btn-warning'  href='${pageContext.request.contextPath}/sys/user/"+data+".html'><i class='icon-pencil'></i>编辑</a>"
		                    return html;
		                }
		            } ]
				});
		});
	</script>
</jsTag>
</html>