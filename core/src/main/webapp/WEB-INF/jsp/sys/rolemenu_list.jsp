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
	
	<!-- treeview -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-treeview/bootstrap-treeview.min.css">
	
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
	    <h1>角色授权</h1>
	    <ol class="breadcrumb">
	        <li><a href="#"><i class="fa fa-dashboard"></i> 首页</a></li>
	        <li><a href="#">系统管理</a></li>
	        <li class="active">角色授权</li>
	    </ol>
	</section>
	
	<!-- Main content -->
	<section class="content">
	
	    <div class="row">
	        <!-- /.col -->
	        <div class="col-md-7">
	            <div class="box box-primary">
	                <!-- /.box-header -->
	                <div class="dataTables_filter" id="searchDiv">
	                    <input placeholder="请输入名称" name="name" class="form-control" type="search" likeOption="true"/>
	                    <div class="btn-group">
	                        <button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
	                    </div>
	                </div>
	                <div class="box-body">
	                    <table id="role_table" class="table table-bordered table-striped table-hover">
	                    </table>
	                </div>
	                <!-- /.box-body -->
	            </div>
	        </div>
	        <div class="col-md-5">
	            <!-- Profile Image -->
				<div class="box box-primary">
					<div class="box-header pb-0">
						<h5 class="pull-left">
							菜单权限设置
						</h5>
						<div class="btn-group pull-right">
	                        <button type="button" class="btn btn-primary" id="update">更新授权</button>
	                    </div>
					</div>
					<div class="box-body box-profile">
						<div id="tree"></div>
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

	<!-- TreeView -->
	<script src="${pageContext.request.contextPath}/static/plugins/bootstrap-treeview/bootstrap-treeview.js"></script>

	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script>
	    //tableId,queryId,conditionContainer
	    var roleTable, roleFuncTable;
	    
	    $(function () {
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
						initTree(row.roleId);
					}
			} 
	        roleTable = new CommonTable("role_table", "${pageContext.request.contextPath}/sys/role/list.json", "searchDiv",role_config);
			
	        //init userrole table
	        //roleFuncTable = new CommonTable("roleFunc_table", "${pageContext.request.contextPath}/sys/rolemenu/list.json", "searchDiv_roleFunc");
	
	        //默认选中第一行
	        setTimeout(function () {
	            roleTable.selectFirstRow(true);
	        }, 10);
	
	        //button event
	        $('button[data-btn-type]').click(function () {
	            var action = $(this).attr('data-btn-type');
	            var rowId = roleTable.getSelectedRowId();
	            switch (action) {
	                case 'selectRoleFunc':
	                    if (!rowId) {
	                        modals.info('请选择角色');
	                        return;
	                    }
	                    modals.openWin({
	                        winId: 'roleFuncWin',
	                        width: 900,
	                        title: '角色【' + roleTable.getSelectedRowData().name + '】绑定功能',
	                        url: basePath + '/sys/rolemenu/select/'+rowId,
	                        hideFunc: function () {
	                            roleFuncTable.reloadData();
	                        }
	                    });
	                    break;
	                case 'deleteRoleFunc':
	                    var rowId_ur = roleFuncTable.getSelectedRowId();
	                    if (!rowId_ur) {
	                        modals.info("请选择要解绑的功能");
	                        return false;
	                    }
	                    modals.confirm("是否要删除该行数据", function () {
	                        ajaxPost(basePath + "/sys/rolemenu/delete/"+rowId_ur,null, function (data) {
	                            if (data.success) {
	                                roleFuncTable.reloadData();
	                            } else {
	                                modals.info(data.message);
	                            }
	                        })
	                    });
	                    break;
	                default:
	                    break;
	            }
	
	        });
	        
	        function initTree(roleId){
				var treeData = '';
				ajaxPost(basePath + "/sys/rolemenu/list.json", {'roleId':roleId}, function(data) {
					treeData = data.data;
					//console.log(JSON.stringify(treeData));
					
					$("#tree").treeview({
						data : treeData,
						showBorder : true,
						expandIcon : "glyphicon glyphicon-chevron-right",
						collapseIcon : "glyphicon glyphicon-chevron-down",
						levels : 1,
						showCheckbox : true,
						onNodeSelected : function(event, data) {
							$(event.currentTarget).data('treeview').checkNode(data.nodeId);
							/*   alert("i am selected");
							  alert(data.nodeId); */
							/* fillDictForm(data);
							formReadonly(); */
							//console.log(JSON.stringify(data));
						},
						onNodeUnchecked : function(event, data) {
							data.state.checked = false;
							checkParentAndChildren(event, data);
							/* var parent = $(event.currentTarget).data('treeview').getParent(data);
							if(parent){
								parent.state.checked = false;
								var siblings = $(event.currentTarget).data("treeview").getSiblings(data);
								for(var i in siblings){
									var node = siblings[i];
									if(node.state.checked){
										$(event.currentTarget).data('treeview').getParent(node).state.checked = true;
									}
								}
							}
							
							var children = data.nodes;
							for(var i in children){
								var node = children[i];
								$(event.currentTarget).data('treeview').uncheckNode(node);
							} */
							
						},
						onNodeChecked : function(event, data) {
							var parent = $(event.currentTarget).data('treeview').getParent(data);
							if(parent){
								parent.state.checked = true;
							} else{
								return false;
							}
						}
						
					});
					if(treeData.length==0)
						return;
					
					// 展开所有节点
					$("#tree").data('treeview').expandAll();
				});
			}
	        
	        function checkParentAndChildren(event, data){
	        	var parent = $(event.currentTarget).data('treeview').getParent(data);
				if(parent){
					parent.state.checked = false;
					var siblings = $(event.currentTarget).data("treeview").getSiblings(data);
					for(var i in siblings){
						var node = siblings[i];
						if(node.state.checked){
							$(event.currentTarget).data('treeview').getParent(node).state.checked = true;
						}
					}
				}
				
				var children = data.nodes;
				for(var i in children){
					var node = children[i];
					$(event.currentTarget).data('treeview').uncheckNode(node);
				}
	        };
	        
	        $("#update").click(function(){
	        	modals.confirm("确认要更新角色菜单？", function () {
	        		var btn = $(this).button("loading");
		        	var roleId = roleTable.getSelectedRowId();
		        	var nodes = $("#tree").data('treeview').getChecked();
		        	$("#tree").data('treeview').getChecked();
		        	var menuIds = [];
		        	for(var i in nodes){
		        		var node = nodes[i];
		        		menuIds.push(node.id);
		        	}
		        	menuIds = menuIds.join(",");
		        	ajaxPost(basePath + "/sys/rolemenu/updateRoleMenu.json", {'roleId':roleId,'menuIds':menuIds}, function(data) {
		        		modals.info("更新成功！");
					});
	        	});
	        });
	        //form_init();
	    })
	</script>
</jsTag>