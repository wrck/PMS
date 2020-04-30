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
		<!-- Default box -->
		<div class="box box-primary">
			<!-- /.box-header -->
			<div class="box-body">
				<div class="row">
					<div class="col-sm-12">
						<div id="searchDiv">
							<div id="operate-btn-group" class="btn-group operate-btn-group">
								<button type="button" class="btn btn-default" data-btn-type="deploy">新增</button>
								<!-- <button type="button" class="btn btn-default" data-btn-type="edit">编辑</button>
								<button type="button" class="btn btn-default" data-btn-type="delete">删除</button> -->
							</div>
						</div>
						<table id="definitionTable"
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
	<div id="deployProcessWin" class="modal fade in" aria-labelledby="myModalLabel" aria-hidden="true" style="display: none;">
        <div class="modal-dialog" style="width: 400px;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true"><li class="fa fa-remove"></li></button>
                    <h5 class="modal-title">部署新流程</h5>
                </div>
                <div class="box-body">
                    <div class="col-sm-12">
                        <div class="form-group has-feedback">
							<label for="file" class="col-sm-3 control-label">文件：</label>
							<div class="col-sm-8">
							    <input type="file" name="deployFile" class="form-control" id="deployFile" placeholder="zip、bar、bpmn、bpmn20.xml文件">
							</div>
				        </div>
                    </div>
                </div>
                <div class="box-footer text-right">
                    <button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">取消</button>
                    <button type="submit" class="btn btn-primary" data-btn-type="deploySubmit">提交</button>
                </div>
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
	<script src="${pageContext.request.contextPath}/static/plugins/ajaxfileupload/ajaxfileupload.js"></script>
	<script>
		var definitionTable;
		var winId = "definitionWin";
		$(document).ready(function() {
			definitionTable= new CommonTable("definitionTable", "definition/list.json", "searchDiv",
					{
					searching:true,
					rowId: 'deploymentId',
					"columns" : [
						{
							title : "流程定义ID",
							data : "id",
							visible: true,
							sortable: false,
						},
						{
							title : "部署ID",
							data : "deploymentId",
							visible: true,
							sortable: false,
						},
                        {
                            title : "名称",
                            data : "name",
                            visible: true,
                            sortable: false,
                        },
                        {
                            title : "KEY",
                            data : "key",
                            visible: true,
                            sortable: false,
                        },
                        {
                            title : "版本号",
                            data : "version",
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
						targets : 6,//操作按钮目标列
						data : "id",
						title:"操作",
						sortable: false,
						class: "text-center",
						render : function(data,
								type, row) {
							var id = row.id;
							//var html = "<a class='btn btn-xs btn-primary' href='definition/"+id+"?_method=PATCH'><i class='fa fa-fw fa-times'></i>部署</a>";
							//html += "<a class='btn btn-xs btn-success ml-1' href='definition/"+id+"' target='_blank'><i class='fa fa-fw fa-edit'></i>编辑</a>";
							//html += "<a class='btn btn-xs btn-danger ml-1' href='definition/"+id+"?_method=DELETE'><i class='fa fa-fw fa-times'></i>删除</a>";
							var html = "<button class='btn btn-xs btn-success' data-btn-type='view'><i class='fa fa-fw fa-eye'></i>查看</button>";
                            html += "<button class='btn btn-xs btn-danger ml-1' data-btn-type='delete'><i class='fa fa-fw fa-times'></i>删除</button>";
                            html += "<button class='btn btn-xs btn-primary ml-1' data-btn-type='convert'><i class='fa fa-fw fa-retweet'></i>转模型</button>";
                            html += "<button class='btn btn-xs btn-info ml-1' data-btn-type='download' data-type='xml'><i class='fa fa-fw fa-cloud-download'></i>XML</button>";
                           	html += "<button class='btn btn-xs btn-info ml-1' data-btn-type='download' data-type='png'><i class='fa fa-fw fa-cloud-download'></i>PNG</button>";
                           	return html;
						}
					} ]
				});
			$(document).on("click", 'button[data-btn-type]', function() {
				var action = $(this).attr('data-btn-type');
				var rowId = definitionTable.getSelectedRowId();
				var basePath ="${pageContext.request.contextPath}";
				switch (action) {
				case 'deploy':
                    /* modals.openWin({
                    	winId:winId,
                    	title:'新增资源',
                    	width:'600px',
                    	url:basePath+"/workflow/modals/definition_detail.html"
                    });      */  
                    modals.showWin("deployProcessWin");
					break;
				case 'deploySubmit':
					if($("#deployFile").val()==''){
                        modals.info('请选择要导入的文件。');
                    }else{
                        modals.confirm('确定导入数据？',function(){
                            $.ajaxFileUpload({
                                url: basePath+'/workflow/definition/deploy.json?__RequestVerificationToken=' + __RequestVerificationToken, //用于文件上传的服务器端请求地址
                                secureuri: false, //是否需要安全协议，一般设置为false
                                fileElementId: 'deployFile', //文件上传域的ID
                                dataType: 'json', //返回值类型 一般设置为json
                                success: function (data, status)  //服务器成功响应处理函数
                                {
                                	if (data.status) {
                                		modals.info(data.message);
                                	} else {
                                		modals.error(data.message);
                                	}
                                },
                                error: function (data, status, e)//服务器响应失败处理函数
                                {
                                    modals.error(data.message);
                                }
                            }); 
                        });
                    }
					break;
				case 'view':
					if(!rowId){
						modals.info('请选择要查看的行');
						return false;
					}
					var rowData = definitionTable.getSelectedRowData(rowId);
					var rowId = rowData.id;
					modals.openWin({
                       	winId: "flowId",
                       	title: "【" + rowData.name + "】xml&image",
                       	width: "1000px",
                       	url:basePath+"/workflow/modals/definition/"+rowId + ".html"
                    });
				   	break;
				case 'delete':
					if(!rowId){
						modals.info('请选择要删除的行');
						return false;
					}
					modals.confirm("是否要删除该行数据？",function(){
						ajaxPost(basePath+"/workflow/definition/"+rowId+".json?_method=DELETE",null,function(data, status){
							if(data.status){
								modals.correct(data.message);
								definitionTable.reloadRowData();
							}else{
								modals.error(data.message);
							}
						});
					})
					break;
				case 'convert':
					if(!rowId){
                        modals.info('请选择要转模型的行');
                        return false;
                    }
					var rowData = definitionTable.getSelectedRowData();
					var rowId = rowData.id;
					modals.confirm("是否要将该流程定义转为模型？",function(){
                        ajaxPost(basePath+"/workflow/definition/model/"+rowId+".json",null,function(data, status){
                            if(data.status){
                            	modals.correct(data.message);
                            	definitionTable.reloadRowData();
                            }else{
                                modals.error(data.message);
                            }
                        });
                    })
                    break;
				case 'download':
                    if(!rowId){
                        modals.info('请选择要下载的行');
                        return false;
                    }
                    var rowData = definitionTable.getSelectedRowData();
                    var rowId = rowData.id;
                    var sourceType = $(this).attr('data-type');
                    window.open(basePath+"/workflow/definition/" + sourceType + "/" + rowId + ".html");
                    break;
				}
			});
			
		});
		function showResource(rowId) {
			modals.openWin({
               	winId:winId,
               	title:'编辑资源',
               	width:'600px',
               	url:basePath+"/workflow/modals/definition_detail.html?id="+rowId
            });
		}
		function deleteResource(rowId) {
			modals.confirm("是否要删除该行数据？",function(){
				ajaxPost(basePath+"/workflow/definition/"+rowId+".json?_method=DELETE",null,function(data, status){
					if(status == 'success'){
						//modals.correct("已删除该数据");
						definitionTable.reloadRowData();
					}else{
						modals.error("用户数据被引用，不可删除！");
					}
				});
			})
		}
	</script>
</jsTag>
</html>