<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal" aria-hidden="true"><li class="fa fa-remove"></li></button>
	<h5 class="modal-title">新增用户</h5>
</div>

<div class="modal-body">
	<form id="role-form" name="role-form" class="form-horizontal">
		<input type="hidden" name="roleId" id="id">
		<div class="box-body">
			<div class="col-md-12">
				<div class="form-group">
					<label for="roleNameZn" class="col-sm-3 control-label">角色名</label>
					<div class="col-sm-8">
						<input type="text" class="form-control" id="roleNameZn" name="roleNameZn" data-flag="icheck" placeholder="角色名">
					</div>
				</div>
				<div class="form-group">
					<label for="roleName" class="col-sm-3 control-label">角色CODE</label>
					<div class="col-sm-8">
						<input type="text" class="form-control" id="roleName" name="roleName" data-flag="icheck" placeholder="角色CODE">
					</div>
				</div>
				<div class="form-group">
					<label for="homePage" class="col-sm-3 control-label">首页</label>
					<div class="col-sm-8">
						<input type="text" class="form-control" id="homePage" name="homePage" data-flag="icheck" placeholder="首页">
					</div>
				</div>
				<div class="form-group">
					<label for="priority" class="col-sm-3 control-label">优先级</label>
					<div class="col-sm-8">
						<input type="text" class="form-control" id="priority" name="priority" data-flag="icheck" placeholder="优先级">
					</div>
				</div>
				<div class="form-group">
					<label for="status" class="col-sm-3 control-label">状态</label>
					<div class="col-sm-8">
						<label class="control-label"> <input type="radio" name="status" data-flag="icheck" checked="checked"
							value="1"> 启用
						</label> &nbsp;&nbsp;&nbsp; <label class="control-label"> <input type="radio" name="status" data-flag="icheck"
							value="0"> 禁用
						</label>
					</div>
				</div>
				<div class="form-group">
					<label for="remark" class="col-sm-3 control-label">说明</label>
					<div class="col-sm-8">
						<textarea class="form-control" id="remark" name="remark" placeholder="说明"></textarea>
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
<script>
	//tableId,queryId,conditionContainer
	var form = null;
 	var id = "${id!=0 && id!=null?id:0}";
	$(function() {
		//数据校验
		$("#role-form").bootstrapValidator({
			message : '请输入有效值',
			feedbackIcons : {
				valid : 'glyphicon glyphicon-ok',
				invalid : 'glyphicon glyphicon-remove',
				validating : 'glyphicon glyphicon-refresh'
			},
			submitHandler : function(validator,roleform, submitButton) {
				modals.confirm('确认保存？', function() {
					//Save Data，对应'submit-提交'
					var params = form.getFormSimpleData();
					console.log(params);
					var path = '/sys/role/';
                    if(id !="0"){
                    	path += id+'.json';
                    }else{
                    	path += 'detail.json';
                    }
					ajaxPost(basePath + path, params, function(data, status) {
						if(status == 'success'){
							if(id!="0"){//更新
							    roleTable.reloadRowData(id); 
							}else{//新增 
								 //modals.info("数据保存成功");
								roleTable.reloadData(); 
							}
						}	
						modals.closeWin(winId); 
					});
				});
			},
			fields : {
				roleNameZn : {
					validators : {
						notEmpty : {
							message : '请输入角色名称'
						}
					}
				},
				roleName : {
					validators : {
						notEmpty : {
							message : '请输入角色CODE'
						},
						/* remote:{
				        	url:basePath+"/base/checkUnique", 
				        	data: function(validator) {
		                        return { 
		                            className:'com.cnpc.framework.base.entity.Role',
		                            fieldName:'code',
		                            fieldValue:$('#code').val(),
		                            id:$('#id').val()
		                        };
		                    },
				        	message:'该编码已被使用'
				        } */
					}
				},
				homePage :{
					validators : {
						notEmpty : {
							message : '请输入默认首页'
						},
					}
				},
				priority : {
					validators : {
						notEmpty : {
							message : '请输入排序'
						},
						integer:{
							message:'请输入整数'
						},
						greaterThan:{
							value:0,
							inclusive:true,
							message:'请输入大于0的整数'
						}
					}
				},
				status : {
					validators : {
						notEmpty : {
							message : '请选择状态'
						}
					}
				}
			}
		});
		//初始化控件
		form=$("#role-form").form();
		//回填id		
		if(id!="0"){
			$("#role-form").prepend('<input type="hidden" name="_method" value="PUT">');
			ajaxPost(basePath+"/sys/role/"+id+".json",null,function(data){
				form.initFormData(data.role);
			})
		}
	});
	
	
	function resetForm(){
		form.clearForm();
        $("#role-form").data('bootstrapValidator').resetForm();
	}
</script>