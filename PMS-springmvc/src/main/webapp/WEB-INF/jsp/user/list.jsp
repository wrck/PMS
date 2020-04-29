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
		<h1>用户管理</h1>
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
						<!-- <div class="row-fluid" id="search">
			                <form class="form-inline well">
			                    <span>用户名:</span>
			                    <input type="text" name="userName" class="input-medium" placeholder="用户名" id="userName-search">
			                    <span>最后登录时间:</span>
			                    <input type="text" name="lastLoginTime" class="input-medium" placeholder="最后登录时间" id="lastLoginTime-search">
			                    <span>最后登录Ip:</span>
			                    <input type="text" name="lastLoginIp" class="input-medium" placeholder="最后登录Ip" id="lastLoginIp-search">
			                    <span>状态:</span>
			                    <select class="input-small" id="status-search">
			                        <option value="">全部</option>
			                        <option value="1">有效</option>
			                        <option value="0">失效</option>
			                    </select>
			                    <button type="button" class="btn" id="btn-advanced-search"><i class="fa fa-search"></i> 查询</button>
			                </form>
			            </div> -->
			            <div id="searchDiv" >
			            	<label>用户名：
							<input type="search" name="userName" class="form-control" placeholder="用户名" id="userName-search">
			                </label>
			                <label>姓名：
			                	<input type="search" name="realName" class="form-control" placeholder="姓名" id="lastLoginTime-search">
			                </label>
			                <label>Email：
			                	<input type="search" name="email" class="form-control" placeholder="Email" id="lastLoginIp-search">
			                </label>
			                <label>状态：
			                <select class="form-control" id="status-search" name="status" type="search">
			                    <option value="">全部</option>
			                    <option value="1">有效</option>
			                    <option value="0">失效</option>
			                    <option value="2">锁定</option>
			                </select>
			                </label>
		                    <label>角色：
			                <select class="form-control" id="status-search" name="roleId" type="search">
			                    <option value="">全部</option>
			                    <option value="1">管理员</option>
			                    <option value="2">用户</option>
			                    <option value="3">测试</option>
			                </select>
			                </label>
			                <div class="btn-group">
								<button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
								<button type="button" class="btn btn-default" data-btn-type="reset">重置</button>
							</div>
							<div id="operate-btn-group" class="btn-group operate-btn-group">
								<button type="button" class="btn btn-default" data-btn-type="add">新增</button>
								<button type="button" class="btn btn-default" data-btn-type="edit">编辑</button>
								<button type="button" class="btn btn-default" data-btn-type="delete">删除</button>
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
			var commonTable= new CommonTable("userTable", "${pageContext.request.contextPath}/sys/user/list.json", "searchDiv",
				{
					exportData:{
						sourceObject : "UserDetail",
						type:["excel"],
						fullServiceName:"com.dp.plat.core.service.impl.DataExportService",
						simpleServiceName:"dataExportService"
					},
					searching:true,
					rowId: 'userId',
					"columns" : [
						{
							name : "user_name",
							title : "用户名",
							data : "userName",
							visible: true,
							sortable: true,
						},
						{
							title : "姓名",
							data : "realName",
							visible: true,
							sortable: true,
						},
						{
							title : "角色",
							data : "roles",
							visible: true,
							sortable: false,
						},
						{
							title : "Email",
							data : "email",
							visible: true,
							sortable: true,
						},
						{
							title : "座机",
							data : "telphone",
							visible: true,
							sortable: true,
						},
						{
							title : "手机",
							data : "mobile",
							visible: true,
							sortable: true,
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
					targets : 7,//操作按钮目标列
					data : "userId",
					title:"操作",
					sortable: false,
					render : function(data,
							type, row) {
						var id = '"' + row.id
								+ '"';
						//<a href='javascript:void(0);'  class='delete btn btn-default btn-xs'  ><i class='fa fa-times'></i> 查看</a>
						var html = "<a class='btn btn-xs btn-success' href='${pageContext.request.contextPath}/sys/user/"+data+".html'><i class='icon-ok'></i>查看</a>"
						//html += "<a class='btn btn-xs btn-warning'  href='${pageContext.request.contextPath}/sys/user/"+data+".html'><i class='icon-pencil'></i>编辑</a>"
						html += "<a class='btn btn-xs btn-danger' href='${pageContext.request.contextPath}/sys/user/"+data+".html?method=DELETE'><i class='icon-remove'></i>删除</a>"
						return html;
					}
				} ]
			});
			$('button[data-btn-type]').click(function() {
				var action = $(this).attr('data-btn-type');
				var rowId = commonTable.getSelectedRowId();
				var basePath ="${pageContext.request.contextPath}";
				switch (action) {
				case 'add':
					window.location.href = basePath+"/sys/user/detail.html";
                    /* modals.openWin({
                    	winId:winId,
                    	title:'新增用户',
                    	width:'900px',
                    	url:basePath+"/sys/user/detail"
                    	/*, hideFunc:function(){
                    		modals.info("hide me");
                    	},
                    	showFunc:function(){
                    		modals.info("show me");
                    	} //
                    });       */                  
					break;
				case 'edit':
					if(!rowId){
						modals.info('请选择要编辑的行');
						return false;
					}
					window.location.href = basePath+"/sys/user/"+rowId+".html";
					/* modals.openWin({
                       	winId:winId,
                       	title:'编辑用户【'+commonTable.getSelectedRowData().name+'】',
                       	width:'900px',
                       	url:basePath+"/sys/user/"+rowId+".html?_method=PUT"
                    });  */
				   	break;
				case 'delete':
					if(!rowId){
						modals.info('请选择要删除的行');
						return false;
					}
					modals.confirm("是否要删除该行数据？",function(){
						ajaxPost(basePath+"/sys/user/"+rowId+".html?_method=DELETE",null,function(data){
							if(data.success){
								//modals.correct("已删除该数据");
								commonTable.reloadRowData();
							}else{
								modals.error("用户数据被引用，不可删除！");
							}
						});
					})
					break;
				}
			});
			/* $('#example1').DataTable({
				"paging" : true,
				"iDisplayLength" : 10, //默认每页数量
				"bPaginate" : true, //翻页功能
				"bLengthChange" : true, //改变每页显示数据数量
				"bFilter" : true, //过滤功能
				"bSort" : true, //排序功能
				"bInfo" : true, //页脚信息
				"bAutoWidth" : true, //自动宽度
				"bRetrieve" : true,
				"processing" : true,
				//"serverSide" : true,//服务器端进行分页处理的意思
				"bPaginate" : true,
				"bProcessing" : true,
				"ajax" : {
					url : "${pageContext.request.contextPath}/sys/user/list.json",
					dataSrc : function(result) {
						//这里result和上面jquery的ajax的代码类似，也是可以得到data.json的数据，但是这样的格式，Datatables不能直接使用，这时候需要在这里处理一下
						//直接返回Datatables需要的那部分数据即可
						return result.list;
					}
				},
				"columns" : [
						{
							data : "userName"
						},
						{
							data : "lastLoginTime"
						},
						{
							data : "lastLoginIp"
						},
						{
							data : "enabled",
							render : function(
									data, type,
									row) {
								if (data == true) {
									return "有效";
								} else {
									return "失效";
								}

							}
						} ],
				"columnDefs" : [ {
					// 定义操作列,######以下是重点########
					"targets" : 4,//操作按钮目标列
					"data" : "id",
					"render" : function(data,
							type, row) {
						var id = '"' + row.id
								+ '"';
						//<a href='javascript:void(0);'  class='delete btn btn-default btn-xs'  ><i class='fa fa-times'></i> 查看</a>
						var html = "<a class='btn btn-xs btn-success' href='/sys/user/"+data+".html'><i class='icon-ok'></i>查看</a>"
						html += "<a class='btn btn-xs btn-warning' onclick='edit("
								+ this
								+ ")'><i class='icon-pencil'></i>编辑</a>"
						html += "<a class='btn btn-xs btn-danger' onclick='del("
								+ this
								+ ")'><i class='icon-remove'></i>删除</a>"
						return html;
					}
				} ]
			}); */
		});
	</script>
</jsTag>
</html>