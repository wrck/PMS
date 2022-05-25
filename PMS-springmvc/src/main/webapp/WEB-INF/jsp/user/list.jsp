<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
			                <mvc:select class="form-control" id="status-search" path="roles" name="roleId" type="search">
								<mvc:options items="${roles}" itemValue="roleId" itemLabel="roleNameZn" />
							</mvc:select>
			                <%-- <select class="form-control" id="status-search" name="roleId" type="search">
			                    <option value="">全部</option>
			                    <c:forEach items="${roles}" var="item">
			                    	<option value="${}">全部</option>
			                    </c:forEach>
			                	<mvc:options items="${roles}" itemValue="roleId" itemLabel="roleNameZn"/>
			                </select> --%>
			                </label>
			                <div class="btn-group">
								<button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
								<button type="button" class="btn btn-default" data-btn-type="reset">重置</button>
							</div>
							<div id="operate-btn-group" class="btn-group operate-btn-group">
								<button type="button" class="btn btn-default" data-btn-type="syncData">同步用户</button>
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
	<script src="${pageContext.request.contextPath}/static/pm/js/initComm.js"></script>
   	<script src="${pageContext.request.contextPath}/static/pm/js/router.js"></script>
  	<script src="${pageContext.request.contextPath}/static/vue/vue.min.js"></script>
	<script>
		var commonTable;
    	var urlNamespace = "${urlNamespace}";
    	var model = "${model}";
    	var winId= model + "Win";
    	var tableId = model + "Table";
		$(document).ready(function() {
			commonTable= new CommonTable("userTable", "${pageContext.request.contextPath}/pm/user/list.json", "searchDiv",
				{
					/* exportData:{
						sourceObject : "UserDetail",
						type:["excel"],
						fullServiceName:"com.dp.plat.core.service.impl.DataExportService",
						simpleServiceName:"dataExportService"
					}, */
					/* exportData: {
	                	url: router(urlNamespace).api(model).list().replace(".json", ".xlsx"),
	                	fileName: "用户列表",
	                	type: ["excel"]
	                }, */
					searching:true,
					rowId: 'userId',
			});
			$(document).off('click', "#" + tableId  + '_wrapper button[data-btn-type]');
            $(document).on('click', "#" + tableId  + '_wrapper button[data-btn-type]', function() {
			//$('button[data-btn-type]').click(function() {
				var action = $(this).attr('data-btn-type');
				var rowId = commonTable.getSelectedRowId();
				switch (action) {
				case 'syncData':
					modals.confirm("是否要同步用户数据？",function(){
						var layerId = layer.load(3);
						ajaxPost(basePath+"/ehr/syncData.json", null, function(data){
							modals.correct("同步成功");
							commonTable.reloadRowData();
							layer.close(layerId);
						});
					})
					break;
				case 'add':
					window.location.href = basePath+"/pm/user/detail.html";
					break;
				case 'edit':
					if(!rowId){
						modals.info('请选择要编辑的行');
						return false;
					}
					window.location.href = basePath+"/pm/user/"+rowId+".html";
				   	break;
				case 'delete':
					if(!rowId){
						modals.info('请选择要删除的行');
						return false;
					}
					modals.confirm("是否要删除该行数据？",function(){
						ajaxPost(basePath+"/pm/user/"+rowId+".json?_method=DELETE",null,function(data){
							if(data.success){
								//modals.correct("已删除该数据");
								commonTable.reloadRowData();
							}else{
								modals.error(data.message || "删除失败！");
							}
						});
					})
					break;
				}
			});
			
			$(document).off("dblclick", "#" + tableId  + " tbody tr");
            $(document).on("dblclick", "#" + tableId  + " tbody tr", function () {
            	console.log("#" + tableId  + " tbody tr:dblclick");
                var rowId = commonTable.getSelectedRowId();
                if(rowId == null){
                    modals.info('请点击需要查看的行');
                    return false;
                }
                var url = router(urlNamespace).html(model).detail(rowId);
                window.open(url);
            });
            
         	// 页面加载完成回调函数函数
        	if (router(urlNamespace).callback(model).list) {
        		var complate = (router(urlNamespace).callback(model).list || {}).complete;
        		if (typeof complate == 'function') {
        			complate.call(this);
        		}
        	}
		});
	</script>
</jsTag>
</html>