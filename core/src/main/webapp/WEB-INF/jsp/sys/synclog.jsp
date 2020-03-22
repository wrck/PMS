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
		<h1>数据同步日志管理</h1>
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
                                <input type="search" name="fuzzy" class="form-control" placeholder="同步方法、表实体、数据源" id="fuzzySearch" style="min-width: 250px;">
			            	</div>
			            	<div class="form-group">
                                <label>同步类型：</label>
	                            <select class="form-control" id="syncType-search" name="syncType" type="search">
	                                <option value="">全部</option>
	                                <option value="1">全量更新</option>
	                                <option value="2">增量更新</option>
	                            </select>
                            </div>
			            	<div class="form-group">
                                <label>同步状态：</label>
                                <select class="form-control" id="status-search" name="isSuccess" type="search">
                                    <option value="">全部</option>
                                    <option value="1">成功</option>
                                    <option value="0">失败</option>
                                </select>
                            </div>
			                <div class="btn-group">
								<button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
								<button type="button" class="btn btn-default" data-btn-type="reset">重置</button>
							</div>
						</div>
						<table id="syncLogTable"
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
			var commonTable= new CommonTable("syncLogTable", "synclog/list.json", "searchDiv",
					{
					searching: false,
					searchInline: true,
					rowId: 'id',
					"columns" : [
						{
						    title : "ID",
						    data : "id",
						    visible: true,
						    sortable: true,
						},
                        {
                            title : "同步类型",
                            data : "syncType",
                            visible: true,
                            sortable: true,
                            render : function(data, type, row) {
                                if (data == "0") {
                                    return "更新业务表";
                                } else if (data == "1"){
                                    return "全量同步";
                                } else if (data == "2"){
                                	return "增量同步";
                                }
                            }
                        },
						{
							title : "表实体",
							data : "tableObject",
							visible: true,
							sortable: true
						},
                        {
                            title : "同步数据源",
                            data : "dataFrom",
                            visible: true,
                            sortable: true
                        },
                        {
                            title : "目标数据源",
                            data : "dataTo",
                            visible: true,
                            sortable: true
                        },
                        {
                            title : "同步参数",
                            data : "syncParams",
                            visible: true,
                            sortable: true,
                            render : function(data, type, row) {
                            	if (data) {
                            		return data.replace(",",", ");
                            	}
                            	return data;
                            }
                        },
                        {
                            title : "同步状态",
                            data : "isSuccess",
                            visible: true,
                            sortable: true,
                            render : function(data, type, row) {
                                if (data) {
                                    return "成功";
                                } else{
                                    return "失败";
                                }
                            }
                        },
						{
							title : "同步开始时间",
							data : "syncStartTime",
							visible: true,
							sortable: true
						},
						{
							title : "同步结束时间",
							data : "syncEndTime",
							visible: true,
							sortable: true,
						}],
					"columnDefs" : [ {
		                // 定义操作列,######以下是重点########
		                targets : 9,//操作按钮目标列
		                data : "id",
		                title: "操作",
		                sortable: false,
		                render : function(data, type, row) {
		                    var id = '"' + row.id + '"';
		                    //<a href='javascript:void(0);'  class='delete btn btn-default btn-xs'  ><i class='fa fa-times'></i> 查看</a>
		                    var html = "<a class='btn btn-xs btn-success' href='synclog/"+data+".html'><i class='fa fa-search'></i></a>";
		                    //html += "<a class='btn btn-xs btn-warning'  href='${pageContext.request.contextPath}/sys/user/"+data+".html'><i class='icon-pencil'></i>编辑</a>"
		                    return html;
		                }
		            } ]
				});
		});
	</script>
</jsTag>
</html>