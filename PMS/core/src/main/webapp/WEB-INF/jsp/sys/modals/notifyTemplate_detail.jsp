<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib  prefix="dp" uri="/myTag" %>
<cssTag>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datetimepicker/css/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/plugins/summernote/dist/summernote.css" />
    <style>
        .datepicker {
            z-index: 1500!important;
        }
    </style>
</cssTag>
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true"><li class="fa fa-remove"></li></button>
		<h5 class="modal-title">新增通知模板</h5>
	</div>
	
	<div class="modal-body">
		<form id="notifyTemplate-form" name="notifyTemplate-form" class="form-horizontal">
			<input type="hidden" name="id" id="id">
			<div class="box-body">
				<div class="col-md-12">
					<div class="form-group">
						<label for="templateCode" class="col-sm-3 control-label">模板编码</label>
						<div class="col-sm-8">
							<input type="text" class="form-control" id="templateCode" name="templateCode" data-flag="icheck" placeholder="模板编码">
						</div>
					</div>
					<div class="form-group">
						<label for="subject" class="col-sm-3 control-label">模板标题</label>
						<div class="col-sm-8">
							<input type="text" class="form-control" id="subject" name="subject" data-flag="icheck" placeholder="模板标题">
						</div>
					</div>
					<div class="form-group">
                        <label for="content" class="col-sm-3 control-label">模板内容</label>
                        <div class="col-sm-8">
                            <textarea class="form-control" id="content" name="content" data-flag="summernote" placeholder="请输入模板内容" style="resize: vertical;"></textarea>
                        </div>
                    </div>
					<div class="form-group">
						<label for="effectiveFrom" class="col-sm-3 control-label">生效时间</label>
						<div class="col-sm-8">
							<input type="text" class="form-control" id="effectiveFrom" name="effectiveFrom" data-flag="datetimepicker" data-format="yyyy-MM-dd HH:mm:ss" placeholder="生效时间">
						</div>
					</div>
					<div class="form-group">
						<label for="effectiveTo" class="col-sm-3 control-label">失效时间</label>
						<div class="col-sm-8">
                            <input type="text" class="form-control" id="effectiveTo" name="effectiveTo" data-flag="datetimepicker" data-format="yyyy-MM-dd HH:mm:ss" placeholder="失效时间">
						</div>
					</div>
				</div>
			</div>
			<!-- /.box-body -->
			<div class="box-footer text-right">
				<button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">取消</button>
				<button type="submit" class="btn btn-primary" data-btn-type="save">提交</button>
			</div>
			<!-- /.box-footer -->
		</form>
	
	</div>
<jsTag>
    <script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/datetimepicker/js/bootstrap-datetimepicker.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/plugins/summernote/dist/summernote.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/plugins/summernote/dist/lang/summernote-zh-CN.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/plugins/summernote/summernote-util.js"></script>
	<dp:script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></dp:script>
	<dp:script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></dp:script>
	<script>
		//tableId,queryId,conditionContainer
		var form = null;
	 	var id = "${id!=0 && id!=null?id:0}";
		$(function() {
			//数据校验
			$("#notifyTemplate-form").bootstrapValidator({
				message : '请输入有效值',
				feedbackIcons : {
					valid : 'glyphicon glyphicon-ok',
					invalid : 'glyphicon glyphicon-remove',
					validating : 'glyphicon glyphicon-refresh'
				},
				submitHandler : function(validator,notifyTemplateform, submitButton) {
					modals.confirm('确认保存？', function() {
						//Save Data，对应'submit-提交'
						var params = form.getFormSimpleData();
						var path = '/sys/notifyTemplate/';
	                    if(id !="0"){
	                    	path += id+'.json';
	                    }else{
	                    	path += 'detail.json';
	                    }
						ajaxPost(basePath + path, params, function(data, status) {
							modals.closeWin(winId); 
							if(status == 'success'){
								if(id!="0"){//更新
									modals.info("更新成功！");
									notifyTemplateTable.reloadRowData(id); 
								}else{//新增 
									 modals.info("保存成功!");
									notifyTemplateTable.reloadData(); 
								}
							}
						});
					});
				},
				fields : {
					templateCode : {
						validators : {
							notEmpty : {
								message : '请输入模板编码'
							}
						}
					},
					subject : {
						validators : {
							notEmpty : {
								message : '请输入模板标题'
							},
						}
					},
					content :{
						validators : {
							notEmpty : {
								message : '请输入模板内容'
							},
						}
					},
					effectiveFrom : {
						validators : {
							notEmpty : {
								message : '请选择生效时间'
							}
						}
					}
				}
			});
			//初始化控件
			form=$("#notifyTemplate-form").form();
			//回填id		
			if(id!="0"){
				$("#sys/notifyTemplate-form").prepend('<input type="hidden" name="_method" value="PUT">');
				ajaxPost(basePath+"/sys/notifyTemplate/"+id+".json",null,function(data){
					form.initFormData(data.template);
				})
			}
			
			$('#content').summernote({       
	            focus: true,   
	            lang:'zh-CN',
	            placeholder:'请输入模板内容',
	            minHeight:'100px',
	            // 重写图片上传  
	            callbacks: {
	                onImageUpload: function(files) {  
	                    saveImageUpload(files,this);
	                }
	            }
	        });
			$('#content').summernote('code',$("#content").val());
		});
		
		
		function resetForm(){
			form.clearForm();
	        $("#notifyTemplate-form").data('bootstrapValidator').resetForm();
		}
	</script>
</jsTag>