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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datatables/extensions/RowReorder/css/rowReorder.bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css">
	
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css"/>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">
	
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/select2/select2.min.css">
	
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
		<h1>资源管理</h1>
		<ol class="breadcrumb">
			<li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
			<li><a href="#">Examples</a></li>
			<li class="active">Blank page</li>
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
							<div id="operate-btn-group" class="btn-group operate-btn-group">
								<button type="button" class="btn btn-default" data-btn-type="add">新增</button>
								<!-- <button type="button" class="btn btn-default" data-btn-type="edit">编辑</button>
								<button type="button" class="btn btn-default" data-btn-type="delete">删除</button> -->
							</div>
						</div>
						<table id="resourceTable"
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
	<script src="${pageContext.request.contextPath}/static/plugins/datatables/extensions/RowReorder/js/dataTables.rowReorder.min.js"></script>
	
	<script src="${pageContext.request.contextPath}/static/common/js/dataTablesExt.js"></script>
	
	<!-- 表单验证相关 -->
	<script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>
	
	<script src="${pageContext.request.contextPath}/static/plugins/select2/select2.min.js"></script>
	
	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script>
		var resourceTable;
		var winId = "resourceWin";
		$(document).ready(function() {
			resourceTable= new CommonTable("resourceTable", "resource/list.json", "searchDiv",
					{
					searching:true,
					rowId: 'id',
					rowReorder: true,
					rowReorder: {
						update: false,
						selector: 'tr td:not(:last-child)',
						dataSrc: 'priority'
					},
					displayLength: -1,
					paging: false,
					"columns" : [
						{
							title : "URL",
							data : "url",
							visible: true,
							sortable: false,
						},
						{
							title : "授权控制",
							data : "authc",
							visible: true,
							sortable: false,
						},
                        {
                            title : "排序",
                            data : "priority",
                            visible: true,
                            sortable: false,
                        },
						{
							title : "备注说明",
							data : "remark",
							visible: true,
							sortable: false,
						},
						{
							title : "状态",
							data : "status",
							visible: true,
							sortable: false,
							render : function(
									data, type,
									row) {
								if (data == 1) {
									return "有效";
								} else if(data == 2){
									return "锁定";
								} else{
									return "失效";
								}
							}
						} ],
					"columnDefs" : [ {
						// 定义操作列,######以下是重点########
						targets : 5,//操作按钮目标列
						data : "id",
						title:"操作",
						sortable: false,
						render : function(data,
								type, row) {
							var id = '"' + row.id
									+ '"';
							//<a href='javascript:void(0);'  class='delete btn btn-default btn-xs'  ><i class='fa fa-times'></i> 查看</a>
							var html = "<a class='btn btn-xs btn-success' href='javaScript:void(0)' onclick='showResource("+id+")'><i class='fa fa-fw fa-edit'></i>编辑</a>"
							//html += "<a class='btn btn-xs btn-warning'  href='${pageContext.request.contextPath}/sys/user/"+data+".html'><i class='icon-pencil'></i>编辑</a>"
							html += "<a class='btn btn-xs btn-danger ml-1' href='javaScript:void(0)' onclick='deleteResource("+id+")'><i class='fa fa-fw fa-times'></i>删除</a>"
							return html;
						}
					} ]
				});
			$('button[data-btn-type]').click(function() {
				var action = $(this).attr('data-btn-type');
				var rowId = resourceTable.getSelectedRowId();
				var basePath ="${pageContext.request.contextPath}";
				switch (action) {
				case 'add':
                    modals.openWin({
                    	winId:winId,
                    	title:'新增资源',
                    	width:'600px',
                    	url:basePath+"/sys/modals/resource_detail.html"
                    });                
					break;
				case 'edit':
					if(!rowId){
						modals.info('请选择要编辑的行');
						return false;
					}
					modals.openWin({
                       	winId:winId,
                       	title:'编辑资源',
                       	width:'600px',
                       	url:basePath+"/sys/modals/resource_detail?id="+rowId
                    });
				   	break;
				case 'delete':
					if(!rowId){
						modals.info('请选择要删除的行');
						return false;
					}
					modals.confirm("是否要删除该行数据？",function(){
						ajaxPost(basePath+"/sys/resource/"+rowId+".json?_method=DELETE",null,function(data, status){
							if(status == 'success'){
								//modals.correct("已删除该数据");
								resourceTable.reloadRowData();
							}else{
								modals.error("用户数据被引用，不可删除！");
							}
						});
					})
					break;
				}
			});
		});
		function showResource(rowId) {
			modals.openWin({
               	winId:winId,
               	title:'编辑资源',
               	width:'600px',
               	url:basePath+"/sys/modals/resource_detail?id="+rowId
            });
		}
		function deleteResource(rowId) {
			modals.confirm("是否要删除该行数据？",function(){
				ajaxPost(basePath+"/sys/resource/"+rowId+".json?_method=DELETE",null,function(data, status){
					if(status == 'success'){
						//modals.correct("已删除该数据");
						resourceTable.reloadRowData();
					}else{
						modals.error("用户数据被引用，不可删除！");
					}
				});
			})
		}
	</script>
</jsTag>
</html>