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
	
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css" />
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
		<h1></h1>
		<ol class="breadcrumb">
		</ol>
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">
			<div class="col-sm-12">
				<div class="nav-tabs-custom">
	                <ul class="nav nav-tabs pull-right">
	                    <li class=""><a href="#tab-content-todo" data-toggle="tab" id="nav-tab-todo" aria-expanded="false"><i class="fa fa-edit"></i></a></li>
	                    <li class="active"><a href="#tab-content-finished" data-toggle="tab" id="nav-tab-finished" aria-expanded="true"><i class="fa fa-list-ul"></i></a></li>
	                    <li class="pull-left header"><i class="fa fa-user"></i><small>用户列表</small></li>
	                </ul>
	                <div class="tab-content">
	                    <div class="tab-pane active" id="tab-content-finished">
	                        <!-- Default box -->
					        <div class="box box-primary">
					            <!-- /.box-header -->
					            <div class="box-body">
			                        <div id="searchDiv" >
			                            <div id="operate-btn-group" class="btn-group operate-btn-group">
			                                <button type="button" class="btn btn-default" data-btn-type="deploy">新增</button>
			                                <!-- <button type="button" class="btn btn-default" data-btn-type="edit">编辑</button>
			                                <button type="button" class="btn btn-default" data-btn-type="delete">删除</button> -->
			                            </div>
			                        </div>
			                        <table id="taskTable"
			                            class="table table-bordered table-striped table-hover dataTable">
			                        </table>
			                        <!-- /.box -->
			                    </div>
		                    </div>
	                    </div>
	                    <!-- /.tab-pane -->
	                    <div class="tab-pane" id="tab-content-todo">
	                        <!-- Default box -->
                                  <div class="box box-primary">
                                      <!-- /.box-header -->
                                      <div class="box-body">
			                        <div id="searchFinishedDiv" >
			                            <div id="operate-btn-group" class="btn-group operate-btn-group">
			                                <button type="button" class="btn btn-default" data-btn-type="deploy">新增</button>
			                                <!-- <button type="button" class="btn btn-default" data-btn-type="edit">编辑</button>
			                                <button type="button" class="btn btn-default" data-btn-type="delete">删除</button> -->
										</div>
									</div>
									<table id="finishedTaskTable" class="table table-bordered table-striped table-hover dataTable">
									</table>
									<!-- /.box -->
								</div>
							</div>
						</div>
						<!-- /.tab-pane -->
					</div>
					<!-- /.tab-content -->
				</div>
			</div>
		</div>
	</section>
	<!-- /.content -->
	
	<div id="selectUserWin" class="modal fade in" aria-labelledby="myModalLabel" aria-hidden="true" style="display: none;">
        <div class="modal-dialog" style="width: 400px;">
            <div class="modal-content">
                <div class="modal-header">
                	<button type="button" class="close" data-dismiss="modal" aria-hidden="true"><li class="fa fa-remove"></li></button>
                	<h5 class="modal-title">请选择<span class="chooseType"></span>人</h5>
                </div>
                <form id="selectUserForm" class="form-horizontal bv-form" method="post">
                    <div class="box-body">
                        <div class="col-sm-12">
                            <div>
                                <input type="hidden" name="taskId">
                                <input type="hidden" name="processInstanceId">
                            </div>
                            <div class="form-group has-feedback">
                                <label for="userId" class="col-sm-3 control-label"><span class="chooseType"></span>人：</label>
                                <div class="col-sm-8">
                                    <select id="selectUsers" class="form-control select2" multiple="multiple" data-placeholder="--请选择--" name="userId">
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="box-footer text-right">
                        <button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">取消</button>
                        <button type="button" class="btn btn-primary" data-btn-type="submit">提交</button>
                    </div>
                </form>
			</div>
		</div>
	</div>
</body>
<jsTag>
    <!-- DataTables -->
    <script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/jquery.dataTables.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/dataTables.bootstrap.min.js"></script>
	
	<script src="${pageContext.request.contextPath}/static/common/js/dataTablesExt.js"></script>
	
	<!-- 表单验证相关 -->
    <script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>
	
	<script src="${pageContext.request.contextPath}/static/plugins/select2/select2.min.js"></script>
	
	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script>
		var taskTable,finishedTaskTable;
		var winId = "taskWin";
		var showWinId = "selectUserWin";
		$(document).ready(function() {
			taskTable= new CommonTable("taskTable", "task/todoTask.json", "searchDiv",
					{
					searching:true,
					rowId: 'taskId',
					"columns" : [
						{
							title : "任务ID",
							data : "taskId",
							visible: true,
							sortable: false,
						},
                        {
                            title : "任务名称",
                            data : "taskName",
                            visible: true,
                            sortable: false,
                        },
                        {
                            title : "流程实例ID",
                            data : "processInstanceId",
                            visible: true,
                            sortable: false,
                        },
                        {
                            title : "流程名称",
                            data : "processInstanceName",
                            visible: true,
                            sortable: false,
                        },
                        {
                            title : "审批人",
                            data : "assign",
                            visible: true,
                            sortable: false,
                            render: function(data,type,row) {
                            	if (row.owner && row.owner != data) {
                            		return data + "（原责任人："+row.owner+"）";
                            	}
                            	return data;
                            }
                        },
                        {
                            title : "发起人",
                            data : "userName",
                            visible: true,
                            sortable: false,
                        },
                        {
                            title : "状态",
                            data : "suspended",
                            visible: true,
                            sortable: false,
                            render: function(data, type, row) {
                                if (data) {
                                    return '<span class="label label-danger">挂起</span>';
                                } else {
                                    return '<span class="label label-success">正常</span>';
                                }
                            }
                        }],
					"columnDefs" : [ {
						// 定义操作列,######以下是重点########
						targets : 7,//操作按钮目标列
						data : "taskId",
						title:"操作",
						sortable: false,
						class: "text-center",
						render : function(data,
								type, row) {
							var id = row.taskId;
							//var html = "<a class='btn btn-xs btn-primary' href='task/"+id+"?_method=PATCH'><i class='fa fa-fw fa-times'></i>部署</a>";
							//html += "<a class='btn btn-xs btn-success ml-1' href='task/"+id+"' target='_blank'><i class='fa fa-fw fa-edit'></i>编辑</a>";
							//html += "<a class='btn btn-xs btn-danger ml-1' href='task/"+id+"?_method=DELETE'><i class='fa fa-fw fa-times'></i>删除</a>";
							var html = "";
							if (row.assign) {
								if (row.taskDefinitionKey.indexOf("modify") >= 0) {
									html += "<button class='btn btn-xs btn-success' data-btn-type='modify'><i class='fa fa-fw fa-eye'></i>办理</button>";
								} else {
									html += "<button class='btn btn-xs btn-success' data-btn-type='complete'><i class='fa fa-fw fa-eye'></i>办理</button>";
								}
								
                                html += "<button class='btn btn-xs btn-warning ml-1' data-btn-type='delegate'><i class='fa fa-fw fa-eye'></i>委托</button>";
                                html += "<button class='btn btn-xs btn-warning ml-1' data-btn-type='transfer'><i class='fa fa-fw fa-eye'></i>转办</button>";
                                html += "<button class='btn btn-xs btn-warning ml-1' data-btn-type='jump'><i class='fa fa-fw fa-eye'></i>跳转</button>";
                                html += "<button class='btn btn-xs btn-warning ml-1' data-btn-type='uncliam'><i class='fa fa-fw fa-eye'></i>取消签收</button>";
                            } else {
                            	html += "<button class='btn btn-xs btn-primary' data-btn-type='claim'><i class='fa fa-fw fa-eye'></i>签收</button>";
							} 
                           	html += "<button class='btn btn-xs btn-info ml-1' data-btn-type='view'><i class='fa fa-fw fa-cloud-download'></i>流程明细</button>";
                           	if (row.userName == row.assign) {
                           		html += "<button class='btn btn-xs btn-info ml-1' data-btn-type='revoke'><i class='fa fa-fw fa-cloud-download'></i>撤回</button>";
                           	}
                           	return html;
						}
					} ]
				});
			/* finishedTaskTable= new CommonTable("finishedTaskTable", "task/endTask.json", "searchFinishedDiv",
                    {
                    searching:true,
                    rowId: 'taskId',
                    "columns" : [
                        {
                            title : "流程实例ID",
                            data : "processInstanceId",
                            visible: true,
                            sortable: false,
                        },
                        {
                            title : "流程名称",
                            data : "processInstanceName",
                            visible: true,
                            sortable: false,
                        },
                        {
                            title : "发起人",
                            data : "userName",
                            visible: true,
                            sortable: false,
                        },
                        {
                            title : "流程实例ID",
                            data : "processInstanceId",
                            visible: true,
                            sortable: false,
                        },
                        {
                            title : "流程实例名称",
                            data : "processInstanceName",
                            visible: true,
                            sortable: false,
                        },
                        {
                            title : "启动时间",
                            data : "startTime",
                            visible: true,
                            sortable: false,
                        },
                        {
                            title : "签收时间",
                            data : "claimTime",
                            visible: true,
                            sortable: false,
                        },
                        {
                            title : "结束时间",
                            data : "endTime",
                            visible: true,
                            sortable: false,
                        },
                        {
                            title : "状态",
                            data : "deleteReason",
                            visible: true,
                            sortable: false,
                        }],
                    "columnDefs" : [ {
                        // 定义操作列,######以下是重点########
                        targets : 8,//操作按钮目标列
                        data : "taskId",
                        title:"操作",
                        sortable: false,
                        class: "text-center",
                        render : function(data,
                                type, row) {
                            var id = row.taskId;
                            //var html = "<a class='btn btn-xs btn-primary' href='task/"+id+"?_method=PATCH'><i class='fa fa-fw fa-times'></i>部署</a>";
                            //html += "<a class='btn btn-xs btn-success ml-1' href='task/"+id+"' target='_blank'><i class='fa fa-fw fa-edit'></i>编辑</a>";
                            //html += "<a class='btn btn-xs btn-danger ml-1' href='task/"+id+"?_method=DELETE'><i class='fa fa-fw fa-times'></i>删除</a>";
                            var html = "<button class='btn btn-xs btn-success' data-btn-type='view'><i class='fa fa-fw fa-eye'></i>查看</button>";
                            html += "<button class='btn btn-xs btn-danger ml-1' data-btn-type='delete'><i class='fa fa-fw fa-times'></i>删除</button>";
                            html += "<button class='btn btn-xs btn-primary ml-1' data-btn-type='convert'><i class='fa fa-fw fa-retweet'></i>转模型</button>";
                            html += "<button class='btn btn-xs btn-info ml-1' data-btn-type='download' data-type='xml'><i class='fa fa-fw fa-cloud-download'></i>XML</button>";
                            html += "<button class='btn btn-xs btn-info ml-1' data-btn-type='download' data-type='png'><i class='fa fa-fw fa-cloud-download'></i>PNG</button>";
                            return html;
                        }
                    } ]
                }); */
			$(document).on("click", 'button[data-btn-type]', function() {
				var action = $(this).attr('data-btn-type');
				var rowId = taskTable.getSelectedRowId();
				var basePath ="${pageContext.request.contextPath}";
				switch (action) {
				case 'claim':
					if(!rowId){
                        modals.info('请选择要签收的任务');
                        return false;
                    }
					modals.confirm("是否要签收该任务？",function(){
                        ajaxPost(basePath+"/workflow/task/claim/"+rowId+".json",null,function(data, status){
                            if(data.status){
                                modals.correct(data.message);
                                taskTable.reloadRowData();
                            }else{
                                modals.error(data.message);
                            }
                        });
                    })
					break;
				case 'complete':
					if(!rowId){
						modals.info('请选择要办理的任务');
						return false;
					}
					var rowData = taskTable.getSelectedRowData(rowId);
					/* modals.openWin({
                       	winId: winId,
                       	title: "办理【" + rowData.taskName + "】",
                       	width: "1000px",
                       	url:basePath+"/workflow/modals/task/" + rowData.processDefinitionKey + "/" + rowData.processInstanceId + "/" + rowId+ "?businessKey=" + rowData.businessKey
                    }); */
                    window.open(basePath + rowData.formUrl + "?taskId=" + rowId);
				   	break;
				case 'modify':
                    if(!rowId){
                        modals.info('请选择要办理的任务');
                        return false;
                    }
                    var rowData = taskTable.getSelectedRowData(rowId);
                    /* modals.openWin({
                        winId: winId,
                        title: "办理【" + rowData.taskName + "】",
                        width: "1000px",
                        url:basePath+"/workflow/modals/task/" + rowData.processDefinitionKey + "/" + rowData.processInstanceId + "/" + rowId+ "?taskDefKey="+rowData.taskDefinitionKey +"&businessKey=" + rowData.businessKey
                    }); */
                    window.open(basePath + rowData.formUrl + "?taskId=" + rowData.taskId);
                    break;
				case 'delegate':
					if(!rowId){
						modals.info('请选择要委派的任务');
						return false;
					}
					/* modals.openWin({
                        winId: "taskId",
                        title: "委派【" + rowData.taskName + "】",
                        width: "1000px",
                        url:basePath+"/workflow/modals/task/"+rowId
                    }); */
                    if (selectUsersData.length == 0 ) {
                    	initSelect2("selectUsers");
                    }
                    
                    $("#selectUserForm").attr("action","task/delegate/" + rowId + ".json");
                    $("#selectUserForm input[name='taskId']").val(rowId);
                    $("#selectUserForm input[name='processInstanceId']").val("");
					$("#" + showWinId + " span.chooseType").text("委派");
                    modals.showWin(showWinId);
					/* modals.confirm("是否要委派该任务？",function(){
						ajaxPost(basePath+"/workflow/task/"+rowId+".json?_method=DELETE",null,function(data, status){
							if(data.status){
								modals.correct(data.message);
								taskTable.reloadRowData();
							}else{
								modals.error(data.message);
							}
						});
					}) */
					break;
				case 'transfer':
					if(!rowId){
                        modals.info('请选择要转办的任务');
                        return false;
                    }
					/* modals.openWin({
                        winId: winId,
                        title: "转办【" + rowData.name + "】xml&image",
                        width: "1000px",
                        url:basePath+"/workflow/modals/task/"+rowId
                    }); */
                    
                    if (selectUsersData.length == 0 ) {
                        initSelect2("selectUsers");
                    }
                    var rowData = taskTable.getSelectedRowData(rowId);
                    $("#selectUserForm").attr("action","task/transfer/" + rowId + ".json");
                    $("#selectUserForm input[name='taskId']").val(rowId);
                    $("#selectUserForm input[name='processInstanceId']").val(rowData.processInstanceId);
                    $("#" + showWinId + " span.chooseType").text("转办");
                    modals.showWin(showWinId);
                    break;
				case 'jump':
                    if(!rowId){
                        modals.info('请选择要跳转的任务');
                        return false;
                    }
                    modals.openWin({
                        winId: winId,
                        title: "【" + rowData.name + "】跳转至指定节点",
                        width: "1000px",
                        url:basePath+"/workflow/modals/task/"+rowId + ".html"
                    });
                    break;
				case 'revoke':
                    if(!rowId){
                        modals.info('请选择要撤回的任务');
                        return false;
                    }
                    var rowData = taskTable.getSelectedRowData(rowId);
                    var processInstanceId = rowData.processInstanceId;
                    modals.confirm("是否要撤回该任务？",function(){
                        ajaxPost(basePath+"/workflow/task/revoke/"+processInstanceId+"/"+rowId+".json",null,function(data, status){
                            if(data.status){
                                modals.correct(data.message);
                                taskTable.reloadRowData();
                            }else{
                                modals.error(data.message);
                            }
                        });
                    });
                    break;
				case 'submit':
                    var params = $("#selectUserForm").serializeArray();
                    var url = $("#selectUserForm").attr("action");
                    var title = $("#selectUserForm span.chooseType").text();
                    var userName = $("#selectUserForm select>option:selected").text();
                    modals.confirm("是否要"+title+"该任务给[" + userName + "]？",function(){
                        ajaxPost(url, params,function(data, status){
                        	modals.hideWin(showWinId);
                        	$("#selectUsers").val(null).trigger("change");
                            if(data.status){
                                modals.correct(data.message);
                                taskTable.reloadRowData();
                            }else{
                                modals.error(data.message);
                            }
                        });
                    });
                    break;
				case 'view':
                    if(!rowId){
                        modals.info('请选择要查看的行');
                        return false;
                    }
                    var rowData = taskTable.getSelectedRowData(rowId);
                    var processInstanceId = rowData.processInstanceId;
                    modals.openWin({
                        winId: "flowId",
                        title: "【" + rowData.name + "】xml&image",
                        width: "1000px",
                        url:basePath+"/workflow/modals/instance/"+processInstanceId + ".html"
                    });
                    break;
				case 'uncliam':
                    if(!rowId){
                        modals.info('请选择要取消签收的任务');
                        return false;
                    }
                    modals.confirm("是否要取消签收该任务？",function(){
                        ajaxPost(basePath+"/workflow/task/unclaim/"+rowId+".json",null,function(data, status){
                            if(data.status){
                                modals.correct(data.message);
                                taskTable.reloadRowData();
                            }else{
                                modals.error(data.message);
                            }
                        });
                    })
                    break;
                }
				
			});
			
		});
		
		var selectUsersData = [];
		function initSelect2(id) {
			ajaxPost(basePath + "/sys/user/list.json",{"pageSize":-1,"status":1},function(data, status){
				data = data.data;
				for ( var index in data) {
					var item = {};
					item.text = data[index].userName + "-" + data[index].realName;
					item.id = data[index].userId;
					selectUsersData.push(item);
				}
	            $("#" + id).select2({width:'100%',tags: false,placeholder:"请选择人员",data: selectUsersData});
			});
		}

		function showResource(rowId) {
			modals.openWin({
				winId : winId,
				title : '编辑资源',
				width : '600px',
				url : basePath + "/workflow/modals/task_detail.html?id=" + rowId
			});
		}
		function deleteResource(rowId) {
			modals.confirm("是否要删除该行数据？", function() {
				ajaxPost(basePath + "/workflow/task/" + rowId
						+ ".json?_method=DELETE", null, function(data, status) {
					if (status == 'success') {
						//modals.correct("已删除该数据");
						taskTable.reloadRowData();
					} else {
						modals.error("用户数据被引用，不可删除！");
					}
				});
			})
		}
	</script>
</jsTag>
</html>