<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
		<h1>字典管理</h1>
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
			            	<label>字典类型：
			                <select class="form-control" id="status-search" name="dicTypeId" type="search">
			                    <option value="">全部</option>
			                    <c:forEach var="item" items="${dicMap}"> 
			                    	<option value="${item.key}">${item.value}</option>  
								</c:forEach>  
			                </select>
			                </label>
			            	<label>字典编码：
							<input type="search" name="dicKey" class="form-control" placeholder="字典编码" id="dicKey-search">
			                </label>
			                <label>字典值：
			                	<input type="search" name="dicValue" class="form-control" placeholder="字典值" id="dicValue-search">
			                </label>
			                <label>状态：
			                <select class="form-control" id="status-search" name="status" type="search">
			                    <option value="">全部</option>
			                    <option value="1">有效</option>
			                    <option value="0">失效</option>
			                </select>
			                </label>
			                <div class="btn-group">
								<button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
								<button type="button" class="btn btn-default" data-btn-type="reset">重置</button>
							</div>
							<div id="operate-btn-group" class="btn-group operate-btn-group">
								<button type="button" class="btn btn-default" data-btn-type="add">新增</button>
								<!--  <button type="button" class="btn btn-default" data-btn-type="delete">删除</button> -->
							</div>
						</div>
						<table id="userTable"
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
			var commonTable= new CommonTable("userTable", "${pageContext.request.contextPath}/sys/dictionary/list.json", "searchDiv",
					{
					searching:true,
					rowId: 'id',
					"columns" : [
						{
							name : "dic_type_name",
							title : "字典类型",
							data : "dicTypeName",
							visible: true,
							sortable: true,
						},
						{
							name : "dic_key",
							title : "字典编码",
							data : "dicKey",
							visible: true,
							sortable: true,
						},
						{
							name : "dic_value",
							title : "字典值",
							data : "dicValue",
							visible: true,
							sortable: false,
						},
						{
							title : "状态",
							data : "status",
							visible: true,
							sortable: false,
							render : function(data, type, row) {
								if (data == 1) {
									return "有效";
								} else{
									return "失效";
								}
							}
						},{
							title : "id",
							data : "id",
							visible: false,
						}],
				"columnDefs" : [{
					// 定义操作列,######以下是重点########
					targets : 5,//操作按钮目标列
					data : "id",
					title:"操作",
					sortable: false,
					render : function(data,type, row) {
						var id = '"' + row.id + '"';
						var html = "<center><a class='btn btn-xs btn-success' href='${pageContext.request.contextPath}/sys/dictionary/"+data+".html'><i class='icon-ok'></i>修改</a>"
						html += "<a class='btn btn-xs btn-danger' href='javascript:delDic("+data+")'><i class='icon-remove'></i>删除</a></center>"
						return html;
					}
				}]
			});
			$('button[data-btn-type]').click(function() {
				var action = $(this).attr('data-btn-type');
				var rowId = commonTable.getSelectedRowId();
				var basePath ="${pageContext.request.contextPath}";
				switch (action) {
				case 'add':
					window.location.href = basePath+"/sys/dictionary/add.html";
					break;
				case 'delete':
					if(!rowId){
						modals.info('请选择要删除的行');
						return false;
					}
					modals.confirm("是否要删除该行数据？",function(){
						ajaxPost(basePath+"/sys/dictionary/"+rowId+".html?_method=DELETE",null,function(data){
							if(data.success){
								commonTable.reloadRowData();
							}else{
								modals.error("用户数据被引用，不可删除！");
							}
						});
					})
					break;
				}
			});
		});
		
		function delDic(id) {
			var basePath ="${pageContext.request.contextPath}";
			modals.confirm("是否要删除该行数据？",function(){
				ajaxPost(basePath+"/sys/dictionary/"+id+".html?_method=DELETE",null,function(data){
					if(data.success){
						window.location.href = basePath+"/sys/dictionary.html";
					}else{
						modals.error("未成功删除");
					}
				});
			});
		}
	</script>
</jsTag>
</html>