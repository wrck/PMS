<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true"><li class="fa fa-remove"></li></button>
		<h5 class="modal-title">新增资源</h5>
	</div>
	
	<div class="modal-body">
		<form id="resource-form" name="resource-form" class="form-horizontal">
			<input type="hidden" name="id" id="id">
			<div class="box-body">
				<div class="col-md-12">
					<div class="form-group">
						<label for="url" class="col-sm-3 control-label">URL</label>
						<div class="col-sm-8">
							<input type="text" class="form-control" id="url" name="url" data-flag="icheck" placeholder="URL">
						</div>
					</div>
					<div class="form-group">
						<label for="authc" class="col-sm-3 control-label">授权控制</label>
						<div class="col-sm-8" id="authICheck">
							<div style="font-size:12px;" class="clearfix mb-1">
								<label for="authType" class="col-sm-3 control-label text-left pl-0">* 认证方式:</label>
								<div>
									<label class="control-label"> <input type="radio" id="authType" name="authType" data-flag="icheck" checked="checked"
										value="anon">匿名访问
									</label> &nbsp;&nbsp;&nbsp; <label class="control-label"> <input type="radio" id="authType" name="authType" data-flag="icheck"
										value="authc">身份认证
									</label>
								</div>
							</div>
							<div style="font-size:12px;" class="clearfix mb-1">
								<label for="roleCode" class="col-sm-3 text-left control-label pl-0">角色控制:</label>
								<div class="col-sm-7 pl-0">
									<select id="roleSelect" multiple="multiple" class="col-sm-9 form-control">
									</select>
								</div>
								<label class="control-label pull-left"> <input type="checkbox" class="icheckbox_square-green" id="isAny" value="1">任一</label>
							</div>
							<div style="font-size:12px;" class="clearfix">
								<label for="perms" class="col-sm-3 text-left control-label pl-0">权限控制:</label>
								<div class="col-sm-7 pl-0">
									<input type="text" id="perms" class="form-control pr-1" style="border-color: #d2d6de!important;" placeholder="例如：perms[user:*]、rest[user]">
								</div>
								<label class="control-label pull-left"> <input type="checkbox" class="icheckbox_square-green" id="isSsl" value="1">SSL</label>
							</div>
							<input type="hidden" class="form-control" id="authc" name="authc" data-flag="icheck" placeholder="授权控制">
						</div>
					</div>
					<div class="form-group">
						<label for="status" class="col-sm-3 control-label ">状态</label>
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
		$("#resource-form").bootstrapValidator({
			message : '请输入有效值',
			feedbackIcons : {
				valid : 'glyphicon glyphicon-ok',
				invalid : 'glyphicon glyphicon-remove',
				validating : 'glyphicon glyphicon-refresh'
			},
			submitHandler : function(validator,roleform, submitButton) {
				modals.confirm('确认保存？', function() {
					//Save Data，对应'submit-提交'
					var authType = $("#authType:checked").val();
					var roles = $("#roleSelect").val();
					var perms = $("#perms").val();
					var isAnyRoles = $("#isAny:checked").val();
					var isSsl = $("#isSsl:checked").val();
					var authc = [];
					authc.push(authType);
					if(isAnyRoles && roles){
						authc.push("anyRoles[" + roles.toString() + "]");
					} else if(roles){
						authc.push("roles[" + roles.toString() + "]");
					}
					if(perms){
						authc.push(perms);
					}
					if(isSsl){
						authc.push("ssl");
					}
					authc = authc.join(",");
					if(authc){
						$("#authc").val(authc);
					}
					var params = form.getFormSimpleData();
					console.log(params);
					var path = '/sys/resource/';
                    if(id !="0"){
                    	path += id+'.json';
                    }else{
                    	path += 'detail.json';
                    }
					ajaxPost(basePath + path, params, function(data, status) {
						if(status == 'success'){
							if(id!="0"){//更新
								resourceTable.reloadRowData(id); 
							}else{//新增 
								 //modals.info("数据保存成功");
								resourceTable.reloadData(); 
							}
						}
						modals.closeWin(winId); 
					});
				});
			},
			fields : {
				url : {
					validators : {
						notEmpty : {
							message : '请输入URL'
						}
					}
				},
				/* authc : {
					validators : {
						notEmpty : {
							message : '请输入授权控制'
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
				/*	}
				}, */
				status : {
					validators : {
						notEmpty : {
							message : '请选择状态'
						}
					}
				},
				authType : {
					validators : {
						notEmpty : {
							message : '请输入认证方式'
						},
					}
				}
			}
		});
		var roleData = [];
		ajaxPost(basePath+"/sys/role/list.json",null,function(data){
			$.each(data.data,function(index,item){
				roleData.push(item.roleName);
			});
			$("#roleSelect").select2({width:'100%',tags: true,data: roleData,placeholder:"角色控制"});
		});
		$("#authICheck input[type='checkbox']").iCheck({
		    checkboxClass: 'icheckbox_square-green'
	  	});
		
		//初始化控件
		form=$("#resource-form").form();
		//回填id		
		if(id!="0"){
			$("#resource-form").prepend('<input type="hidden" name="_method" value="PUT">');
			ajaxPost(basePath+"/sys/resource/"+id+".json",null,function(data){
				var authc = data.resource.authc;
				var temp1 = authc.replace(/\,.*\[.*\,.*\]/g,"").split(",");
				var temp2 = authc.match(/\,.*\[.*\,.*\]/g);
				var temp3 = [];
				if(temp2){
					$.each(temp2,function(i,item){
						item = item.replace(/^\,/,"");
						item = item.replace(/\],/g,"];");
						temp1.push(item);
					});
				}
				temp3 = temp1.join(";");
				temp3 = temp3.split(";");
				$.each(temp3,function(index,item){
					if(item){
						if(item == "anon"){
							$("#authType[value='anon']").iCheck("check");
							//$("#authType[value='anon']").prop("checked",true);
						} else if(item == "authc"){ 
							$("#authType[value='authc']").iCheck("check");
							//$("#authType[value='authc']").prop("checked",true);
						} else if(item == "ssl"){
							$("#isSsl").iCheck("check");
						} else if(item.toLowerCase().indexOf("roles") > -1){
							var start = item.indexOf("[");
							var end = item.indexOf("]");
							if(item.indexOf("roles") > -1){
								item = item.replace("roles","");
							} else if(item.indexOf("anyRoles") > -1){
								item = item.replace("anyRoles","");
								$("#isAny").iCheck("check");
							}
							item = item.replace(/\[|\]/g,"");
							//console.log(item.split(","));
							$("#roleSelect").val(item.split(",")).trigger('change');
						} else if (item.indexOf("perms") || item.indexOf("rest")){
							$("#perms").val(item);
						}
					}
				});
				form.initFormData(data.resource);
			})
		} else {
			var length = resourceTable.data.tableData.data.length;
			var nextPriority = length ? length : 0;
			$("#resource-form").prepend('<input type="hidden" name="priority" value="' + nextPriority + '">');
		}
		
	});
	
	function resetForm(){
		form.clearForm();
        $("#resource-form").data('bootstrapValidator').resetForm();
	}
</script>