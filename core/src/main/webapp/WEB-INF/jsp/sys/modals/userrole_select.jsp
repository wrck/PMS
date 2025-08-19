<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal" aria-hidden="true"><li class="fa fa-remove"></li></button>
	<h5 class="modal-title">新增用户</h5>
</div> 

<div class="modal-body" style="height:600px;">
    <div class="row">
			<!-- /.col -->
			<div class="col-md-6">
				<div class="box box-primary">
					<!-- /.box-header -->
					<div class="box-header with-border">
					   <h5 class="box-title" style="font-size:14px;">未绑定该角色的用户列表</h5>
					   <button type="button" id="btn_add_ur" class="btn btn-sm close" title="用户绑定角色" ><li class="fa fa-arrow-right"></li></button>
					</div>
					<div class="dataTables_filter" id="searchDiv_unselected">
					    <input type="hidden" value="${roleId}" data-type="search" name="roleId"/>
						<!-- <input placeholder="请输入用户名" name="name" class="form-control form-control-sm" type="search" likeOption="true" />
						<div class="btn-group">
							<button type="button" class="btn btn-primary" data-btn-type="search" >查询</button>
						</div>	 --> 				
					</div>
					<div class="box-body">
						<table id="userRole_unselected_table" class="table table-bordered table-striped table-hover">
						</table>
					</div>
					<!-- /.box-body -->
				</div>
			</div>
		
			<div class="col-md-6">
				<!-- Profile Image -->
				<div class="box box-primary">
				<div class="box-header with-border">
					   <h5 class="box-title" style="font-size:14px;float:right">已绑定该角色的用户列表</h5>
					   <button type="button" id="btn_remove_ur" class="btn btn-sm close" style="float:left" title="用户解绑角色" ><li class="fa fa-arrow-left"></li></button>
					</div>
					<div class="dataTables_filter" id="searchDiv_selected">
					    <input type="hidden" value="${roleId}" data-type="search" name="roleId"/>
						<!-- <input placeholder="请输入用户名" name="user.name" class="form-control" type="search" likeOption="true" />
						<div class="btn-group">
							<button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
						</div>	 -->					
					</div>
					<div class="box-body">
						<table id="userRole_selected_table" class="table table-bordered table-striped table-hover">
						</table>
					</div>
					<!-- /.box-body -->
				</div>
				<!-- /.box -->
			</div>
		</div>

  </div>
<script>
	//tableId,queryId,conditionContainer
	var roleId="${roleId}";//role Id
	var unselectedTable,selectedTable;
	$(function() { 
		//the table config of opened window
		/* var table_config=null; */
		var common_config = {
							searching:true,
							pagingType :"full",//simple numbers simple_numbers full full_numbers
							rowId: 'id',
							displayLength : 8,
							lengthMenu: [[8, 16, 24, -1], [8, 16, 24, "全部"]],
							"columns" : [
								{
								 	name : "user_id",
									title :"UID",
									data:"userId",
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
								}
							]
		}
	 	var unselected_config=$.extend(true,{},common_config,{
						   "scrollY":"350px",
		                   "scrollCollapse": true,
		                   "singleSelect":false,
		                   "scrollXInner":"450px", 
		                   "autoWidth":false
		                   //"lengthChange":false
		                 });     
		//init table and fill data
		unselectedTable = new CommonTable("userRole_unselected_table", basePath+"/sys/userrole/list.json?isSelected=false&roleId="+roleId, "searchDiv_unselected",unselected_config);
		//setTimeout(function(){unselectedTable.table.columns.adjust();},100);
		//init userrole table  
		
		var selected_config=$.extend(true,{},common_config,{
					           "scrollY":"350px",
			                   "scrollCollapse": true,
			                   "singleSelect":false,
			                   "autoWidth":false
		                   });
		selectedTable=new CommonTable("userRole_selected_table", basePath+"/sys/userrole/list.json?isSelected=true&roleId="+roleId,"searchDiv_selected",selected_config);
		//button event  
		
		//绑定角色到用户
		$("#btn_add_ur").click(function(){
               var rows=unselectedTable.getSelectedRowsData();
               var userRoleList=[]; 
               //console.log(JSON.stringify(rows));                 
               if(!rows){ 
               	modals.info("请选择要绑定该角色的用户");
               	return;
               }
               $.each(rows,function(index,row){                	
               	var userRole={};
               	userRole.userId=row.userId;
               	userRole.roleId=roleId;
               	userRoleList.push(userRole);
               });
               ajaxPost(basePath+"/sys/userrole/bind.json",{"userRoleListStr":JSON.stringify(userRoleList)},function(data,status){
               	if(status == 'success'){                		
               		selectedTable.reloadData();
               		unselectedTable.reloadRowData();                		
               	}
               }); 
		});
		
		//解绑用户
		$("#btn_remove_ur").click(function(){
			var rows=selectedTable.getSelectedRowsData();
			if(!rows){
				modals.info("请选择要解绑的用户");
				return;
			}
			// userRole_id
			var idArr=[];
			$.each(rows,function(index,row){
				idArr.push(row.id);
			})
			ajaxPost(basePath+"/sys/userrole/unbind.json?_method=DELETE",{"ids":JSON.stringify(idArr)},function(data,status){
				if(status == 'success'){   
					unselectedTable.reloadRowData();
               		selectedTable.reloadData();                		
				}
			})
		})
	})
</script>