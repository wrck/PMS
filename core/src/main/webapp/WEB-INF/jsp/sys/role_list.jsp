<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css"/>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">
	<style>
		#searchDiv{
			display: none;
			margin-bottom: 1rem;
		}
	</style>
</cssTag>
</head>
<body>
	<section class="content-header">
		<h1>角色管理</h1>
		<ol class="breadcrumb">
			<li><a href="#"><i class="fa fa-dashboard"></i> 首页</a></li>
			<li><a href="#">系统管理</a></li>
			<li class="active">角色管理</li>
		</ol>
	</section>

	<!-- Main content -->
	<section class="content">

		<div class="row">

			<!-- /.col -->
			<div class="col-md-12">
				<div class="box box-primary mb-2">
					<!-- /.box-header -->
					<div class="box-body">
						<div id="searchDiv" class="text-right">
							<!-- <input placeholder="请输入用户名" name="userName" class="form-control" type="search" likeOption="true" />
							<div class="btn-group">
								<button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
							</div> -->
							<div class="btn-group operate-btn-group">
								<button type="button" class="btn btn-default" data-btn-type="add">新增</button>
								<button type="button" class="btn btn-default" data-btn-type="edit">编辑</button>
								<button type="button" class="btn btn-default" data-btn-type="delete">删除</button>
							</div>
						</div>
						<table id="role_table" class="table table-bordered table-striped table-hover">
						</table>
					</div>
					<!-- /.box-body -->
				</div>
			</div>
			<div class="col-md-12">
				<!-- Profile Image -->
				<div class="box box-primary">
				<!-- /.box-header -->
					<div class="box-body">
						<div id="searchDiv_userRole" class="text-right">
							<h5 id='roleName' class='pull-left'>已绑定角色用户</h5>
						    <input type="hidden" name="roleId" data-type="search" value="-1" id="roleId"/>
							<!-- <input placeholder="请输入用户名" name="userName" class="form-control" type="search" likeOption="true" />
							<div class="btn-group">
								<button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
							</div> -->
							<div class="btn-group operate-btn-group">
								<button type="button" class="btn btn-default" data-btn-type="selectUserRole">批量绑定/解绑</button>
								<button type="button" class="btn btn-default"  data-btn-type="deleteUserRole" disabled>解绑</button>
							</div>
						</div>
						<table id="userRole_table" class="table table-bordered table-striped table-hover">
						</table>
					</div>
					<!-- /.box-body -->
				</div>
				<!-- /.box -->
			</div>
		</div>
		<!-- /.row -->

	</section>
</body>
</html>
<jsTag>
	<!-- DataTables -->
	<script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/jquery.dataTables.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/dataTables.bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/dataTablesExt.js"></script>
	<!-- 表单验证相关 -->
	<script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>

	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script>
		//tableId,queryId,conditionContainer
		var roleTable,userRoleTable;
		var winId="roleWin";
		$(function() {
			//init table and fill data
			var role_config={
					searching :true,
					rowId: 'roleId',
					"columns" : [
						{
						 	name : "role_id",
							title :"角色ID",
							data:"roleId",
							visible: true,
							sortable:true,
						},
						{
							name : "role_name",
							title : "角色CODE",
							data : "roleName",
							visible: true,
							sortable: true,
						},
						{
							name : "role_name_zn",
							title : "角色名",
							data : "roleNameZn",
							visible: true,
							sortable: false,
						},
						{
							name : "home_page",
							title : "默认首页",
							data : "homePage",
							visible: true,
							sortable: false,
						},
						{
							title : "优先级",
							data : "priority",
							visible: true,
							sortable: true,
						},
						{
							title : "状态",
							data : "status",
							visible: true,
							sortable: true,
							render:function( data, type, row) {
								if (data == 1) {
									return "有效";
								} else{
									return "失效";
								}
							}
						},
						{
							name : "create_time",
							title : "创建时间",
							data : "createTime",
							type : "date",
							visible: true,
							sortable: true,
							render:function( data, type, row) {
								return formatDate(data, "yyyy-MM-dd HH:mm:ss");
							}
						}
					],
					rowClick:function(row,isSelected){
						$("#roleId").val(isSelected?row.roleId:"-1");
						$("#roleName").remove();
						if(isSelected){
						   	$("#searchDiv_userRole").prepend("<h5 id='roleName' class='pull-left'>已绑定『"+row.roleNameZn+"』角色用户</h5>");
						   	$("button[data-btn-type='deleteUserRole']").removeAttr("disabled");
						}
						userRoleTable.reloadData();
					}
			}
			roleTable = new CommonTable("role_table", "${pageContext.request.contextPath}/sys/role/list.json", "searchDiv",role_config);

			//init userrole table
			userRoleTable=new CommonTable("userRole_table","${pageContext.request.contextPath}/sys/userrole/list.json","searchDiv_userRole",{
				rowId: 'id',
				searching :true,
				"columns" : [
					{
					 	name : "id",
						title :"序号",
						data:"id",
						visible: true,
						sortable:true,
					},
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
					}
				],
			});

			//默认选中第一行
			//setTimeout(function(){roleTable.selectFirstRow(false)},10);
			//make right table button on the same row

			//button event
			$('button[data-btn-type]').click(function() {
				var action = $(this).attr('data-btn-type');
				var rowId=roleTable.getSelectedRowId();
				switch (action) {
				case 'add':
                       modals.openWin({
                       	winId:winId,
                       	title:'新增角色',
                       	width:'600px',
                       	url:basePath+"/sys/modals/role_detail.html"
                       	/*, hideFunc:function(){
                       		modals.info("hide me");
                       	},
                       	showFunc:function(){
                       		modals.info("show me");
                       	} */
                       });
					break;
				case 'edit':
					if(!rowId){
						modals.info('请选择要编辑的行');
						return false;
					}
					modals.openWin({
                       	winId:winId,
                       	title:'编辑角色【'+roleTable.getSelectedRowData().roleName+'】',
                       	width:'600px',
                       	url:basePath+"/sys/modals/role_detail.html?id="+rowId
                   });
				   break;
				case 'delete':
					if(!rowId){
						modals.info('请选择要删除的行');
						return false;
					}
					modals.confirm("是否要删除该行数据？",function(){
						ajaxPost(basePath+"/sys/role/"+rowId+".json?_method=DELETE",null,function(data,status){
							if(status == "success"){
								//modals.correct("已删除该数据");
								roleTable.reloadData();
							}else{
								//setTimeout(function(){modals.info(data.message)},2000);
								modals.info(data);
							}
						});
					})
					break;
				case 'selectUserRole':
					if(!rowId){
						modals.info('请选择角色');
						return;
					}
					modals.openWin({
						winId:'userRoleWin',
						width:1080,
						title:'角色『'+roleTable.getSelectedRowData().roleName+'』绑定用户',
						url:basePath+'/sys/modals/userrole_selector.html?roleId='+rowId,
					    hideFunc:function(){userRoleTable.reloadData();}
					})
					break;
				case 'deleteUserRole':
					var rowId_ur=userRoleTable.getSelectedRowId();
					if(!rowId_ur){
						modals.info("请选择要删除的用户");
						return false;
					}
					modals.confirm("是否要删除该行数据",function(){
						ajaxPost(basePath+"/sys/userrole/"+rowId_ur+".json?_method=DELETE",{ids:rowId_ur},function(data ,status){
							if(status == "success"){
								userRoleTable.reloadData();
							}else{
								modals.info(data);
							}
						})
					});
					break;
				}

			});
			//form_init();
		})

	</script>
</jsTag>