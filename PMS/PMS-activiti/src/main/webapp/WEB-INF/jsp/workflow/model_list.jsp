<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib  prefix="dp" uri="/myTag" %>
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
	<dp:link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css" />

	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">

	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/select2/select2.min.css">

	<style>
	#searchDiv {
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
						<div id="searchDiv">
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
			<div id="dialog-form" title="创建模型" style="display: none;">
		         <p class="validateTips">All form fields are required.</p>
		         <form id="modelForm" action="${ctx}/workflow/create" target="_blank" method="post">
		            <fieldset>
		              <label for="name">名称:</label>
		              <input type="text" name="name" id="name" class="text ui-widget-content ui-corner-all">
		              <label for="key">KEY:</label>
		              <input type="text" name="key" id="key" class="text ui-widget-content ui-corner-all">
		              <label for="description">描述:</label>
		              <textarea name="description" id="description" class="text ui-widget-content ui-corner-all"></textarea>
		              <!-- Allow form submission with keyboard without duplicating the dialog button -->
		              <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">
		            </fieldset>
		         </form>
		    </div>
			<!-- /.box-body -->
		</div>
		<!-- /.box -->
	</section>
	<!-- /.content -->

	<div id="createModelWin" class="modal fade in" aria-labelledby="myModalLabel" aria-hidden="true" style="display: none;">
        <div class="modal-dialog" style="width: 400px;">
            <div class="modal-content">
                <div class="modal-header">
                	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                		<li class="fa fa-remove"></li>
                	</button>
                	<h5 class="modal-title">创建模型</h5>
                </div>
                <form id="modelForm" class="form-horizontal bv-form" action="model/create" target="_blank" method="post">
                    <div class="box-body">
                        <div class="col-sm-12">
                            <div class="form-group has-feedback">
                                <div class="col-sm-8">
                                    <input id="token" type="hidden" name="__RequestVerificationToken" value="${__RequestVerificationToken}" class="form-control">
                                </div>
                            </div>
                            <div class="form-group has-feedback">
								<label for="name" class="col-sm-3 control-label">名称：</label>
								<div class="col-sm-8">
								    <input type="text" name="name" class="form-control" id="name" placeholder="Model名称">
								</div>
							</div>
							<div class="form-group has-feedback">
								<label for="key" class="col-sm-3 control-label">KEY：</label>
								<div class="col-sm-8">
								    <input type="text" name="key" class="form-control" id="key" placeholder="Model KEY">
								</div>
							</div>
							<div class="form-group has-feedback">
								<label for="description" class="col-sm-3 control-label">描述：</label>
								<div class="col-sm-8">
								    <input type="text" name="description" class="form-control" id="description" placeholder="Model描述">
								</div>
							</div>
	                    </div>
                    </div>
                    <div class="box-footer text-right">
                        <button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">取消</button>
                        <button type="submit" class="btn btn-primary" id="logsub" data-btn-type="submit">提交</button>
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

    <dp:script src="${pageContext.request.contextPath}/static/common/js/dataTablesExt.js"></dp:script>

    <!-- 表单验证相关 -->
    <script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
    <script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>

    <script src="${pageContext.request.contextPath}/static/plugins/select2/select2.min.js"></script>

    <%-- <dp:script src="${pageContext.request.contextPath}/static/common/js/base.js"></dp:script> --%>
    <dp:script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></dp:script>
    <dp:script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></dp:script>
    <script>
		var resourceTable;
		var winId = "resourceWin";
		$(document).ready(function() {
			resourceTable= new CommonTable("resourceTable", "model/list.json", "searchDiv",
					{
					searching:true,
					rowId: 'id',
					"columns" : [
						{
							title : "Id",
							data : "id",
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
                            title : "NAME",
                            data : "name",
                            visible: true,
                            sortable: false,
                        },
						{
							title : "VERSION",
							data : "version",
							visible: true,
							sortable: false,
						},
						{
							title : "创建时间",
							data : "createTime",
							visible: true,
							sortable: false,
						},
						{
                            title : "最后更新时间",
                            data : "lastUpdateTime",
                            visible: true,
                            sortable: false,
                        },
                        {
                            title : "元数据",
                            data : "metaInfo",
                            visible: true,
                            sortable: false,
                        }],
					"columnDefs" : [ {
						// 定义操作列,######以下是重点########
						targets : 7,//操作按钮目标列
						data : "id",
						title:"操作",
						sortable: false,
						class: "text-center",
						render : function(data,
								type, row) {
							var id = row.id;
							//var html = "<a class='btn btn-xs btn-primary' href='model/"+id+"?_method=PATCH'><i class='fa fa-fw fa-times'></i>部署</a>";
							//html += "<a class='btn btn-xs btn-success ml-1' href='model/"+id+"' target='_blank'><i class='fa fa-fw fa-edit'></i>编辑</a>";
							//html += "<a class='btn btn-xs btn-danger ml-1' href='model/"+id+"?_method=DELETE'><i class='fa fa-fw fa-times'></i>删除</a>";
							var html = "<button class='btn btn-xs btn-primary' data-btn-type='deploy'><i class='fa fa-fw fa-wrench'></i>部署</button>";
                            html += "<button class='btn btn-xs btn-success ml-1' data-btn-type='edit'><i class='fa fa-fw fa-edit'></i>编辑</button>";
                            html += "<button class='btn btn-xs btn-danger ml-1' data-btn-type='delete'><i class='fa fa-fw fa-times'></i>删除</button>";
							return html;
						}
					} ]
				});
			$(document).on("click", 'button[data-btn-type]', function() {
				var action = $(this).attr('data-btn-type');
				var rowId = resourceTable.getSelectedRowId();
				var basePath ="${pageContext.request.contextPath}";
				switch (action) {
				case 'add':
                    /* modals.openWin({
                    	winId:winId,
                    	title:'新增资源',
                    	width:'600px',
                    	url:basePath+"/workflow/modals/resource_detail.html"
                    });      */
                    modals.showWin("createModelWin");
					break;
				case 'edit':
					if(!rowId){
						modals.info('请选择要编辑的行');
						return false;
					}
					modals.confirm("是否要编辑该行数据？",function(){
						   window.open("model/"+rowId+ ".html");
					});
					/* modals.openWin({
                       	winId:winId,
                       	title:'编辑资源',
                       	width:'600px',
                       	url:basePath+"/workflow/model/"+rowId
                    }); */
				   	break;
				case 'delete':
					if(!rowId){
						modals.info('请选择要删除的行');
						return false;
					}
					modals.confirm("是否要删除该行数据？",function(){
						ajaxPost(basePath+"/workflow/model/"+rowId+".json?_method=DELETE",null,function(data, status){
							if(status == 'success'){
								modals.correct("已删除该数据");
								resourceTable.reloadRowData();
							}else{
								modals.error("用户数据被引用，不可删除！");
							}
						});
					})
					break;
				case 'deploy':
					if(!rowId){
                        modals.info('请选择要部署的行');
                        return false;
                    }
					modals.confirm("是否要部署该行数据？",function(){
                        ajaxPost(basePath+"/workflow/model/"+rowId+".json?_method=PATCH",null,function(data, status){
                            if(data.status){
                            	modals.correct(data.message);
                            }else{
                                modals.error(data.message);
                            }
                        });
                    })
                    break;
				}
			});

	        $('#create').click(function() {
	            $('#dialog-form').dialog({
	                  height: 400,
	                  width: 500,
	                  modal: true,
	                  buttons: [
	                    {text: '创建',
	                     click: function() {
	                        if (!$('#name').val()) {
	                            alert('请填写名称！');
	                            $('#name').focus();
	                            return;
	                        }
	                        setTimeout(function() {
	                              location.reload();
	                          }, 1000);
	                            $('#modelForm').submit();
	                    }},
	                    {text:'取消',
	                     click: function() {
	                         $(this).dialog("close");
	                    }}
	                    ]
	            })
		    });
		});
		function showResource(rowId) {
			modals.openWin({
               	winId:winId,
               	title:'编辑资源',
               	width:'600px',
               	url:basePath+"/workflow/modals/resource_detail.html?id="+rowId
            });
		}
		function deleteResource(rowId) {
			modals.confirm("是否要删除该行数据？",function(){
				ajaxPost(basePath+"/workflow/model/"+rowId+".json?_method=DELETE",null,function(data, status){
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