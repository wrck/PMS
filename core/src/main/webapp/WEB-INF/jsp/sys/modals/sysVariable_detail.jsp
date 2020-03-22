<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<cssTag>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datetimepicker/css/bootstrap-datetimepicker.min.css">
    <style>
        .datepicker {
            z-index: 1500!important;
        }
    </style>
</cssTag>
<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal" aria-hidden="true"><li class="fa fa-remove"></li></button>
	<h5 class="modal-title">新增系统参数</h5>
</div>

<div class="modal-body">
	<form id="sysVariable-form" name="sysVariable-form" class="form-horizontal">
		<input type="hidden" name="id" id="id">
		<div class="box-body">
			<div class="col-md-12">
				<div class="form-group">
					<label for="code" class="col-sm-3 control-label">参数编码</label>
					<div class="col-sm-8">
						<input type="text" class="form-control" id="code" name="code" data-flag="icheck" placeholder="系统参数编码">
					</div>
				</div>
				<div class="form-group">
					<label for="var" class="col-sm-3 control-label">参数值</label>
					<div class="col-sm-8">
						<input type="text" class="form-control" id="var" name="var" data-flag="icheck" placeholder="系统参数值">
					</div>
				</div>
				<div class="form-group">
                       <label for="remark" class="col-sm-3 control-label">参数备注</label>
                       <div class="col-sm-8">
                           <textarea class="form-control" id="remark" name="remark" placeholder="系统参数备注" style="resize: vertical;"></textarea>
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
	<script>
		//tableId,queryId,conditionContainer
		var form = null;
	 	var id = "${id!=0 && id!=null?id:0}";
		$(function() {
			//数据校验
			$("#sysVariable-form").bootstrapValidator({
				message : '请输入有效值',
				feedbackIcons : {
					valid : 'glyphicon glyphicon-ok',
					invalid : 'glyphicon glyphicon-remove',
					validating : 'glyphicon glyphicon-refresh'
				},
				submitHandler : function(validator,sysVariableform, submitButton) {
					modals.confirm('确认保存？', function() {
						//Save Data，对应'submit-提交'
						var params = form.getFormSimpleData();
						var path = '/sys/sysVariable/';
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
									sysVariableTable.reloadRowData(id); 
								}else{//新增 
									 modals.info("保存成功!");
									sysVariableTable.reloadData(); 
								}
							}
						});
					});
				},
				fields : {
					code : {
						validators : {
							notEmpty : {
								message : '请输入系统参数编码'
							}
						}
					},
					"var" : {
						validators : {
							notEmpty : {
								message : '请输入系统参数值'
							},
						}
					},
					remark :{
						validators : {
							notEmpty : {
								message : '请输入系统参数备注'
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
			form=$("#sysVariable-form").form();
			//回填id		
			if(id!="0"){
				$("#sysVariable-form").prepend('<input type="hidden" name="_method" value="PUT">');
				ajaxPost(basePath+"/sys/sysVariable/"+id+".json",null,function(data){
					form.initFormData(data.variable);
				})
			}
		});
		
		
		function resetForm(){
			form.clearForm();
	        $("#sysVariable-form").data('bootstrapValidator').resetForm();
		}
	</script>
</jsTag>