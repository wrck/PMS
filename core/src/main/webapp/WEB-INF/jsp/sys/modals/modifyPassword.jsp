<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<style>
/* 	.has-feedback {
		margin-bottom: 27px;
	}
	
	.has-feedback small.help-block {
		position: absolute;
	} */
</style>
<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal" aria-hidden="true"><li class="fa fa-remove"></li></button>
	<h5 class="modal-title">修改密码</h5>
</div>
<div class="modal-body" id="modPwDiv">
	<form method="post" action="${pageContext.request.contextPath}/modifyPassword.html" id="modPwForm" name="modPwForm" class="form-horizontal" onsubmit="changelogin()">
		<div class="box-body">
			<div class="form-group" id="div_old">
	        	<label for="oldPassword" class="col-sm-3 control-label">原密码：</label>
	        	<div class="col-sm-8">
	        		<input type="password" name="oldPassword" class="form-control" id="oldPassword" placeholder="原密码"/>
	        	</div>
	        </div>
	        
			<div class="form-group" id="div_new">
				<label for="newPassword" class="col-sm-3 control-label">新密码：</label>
	        	<div class="col-sm-8">
	        		<input type="password" name="newPassword" class="form-control" id="newPassword" placeholder="新密码"/>
	       		</div>
	        </div>
			<div class="form-group" id="div_confirm">
				<label for="confirmPassword" class="col-sm-3 control-label">确认密码：</label>
	        	<div class="col-sm-8">
	        		<input type="password" name="confirmPassword" class="form-control" id="confirmPassword" placeholder="确认密码"/>
	        	</div>
        	</div>
        </div>
    	<div class="box-footer text-right">
			<button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">取消</button>
			<button type="submit" class="btn btn-primary" id="logsub" data-btn-type="save">提交</button>
		</div>
	</form>
</div>
<script>
	var lold = 0, lnew = 0, lcon = 0, cold = 0, cnew = 0, ccon = 0;
	window.onload = function() {
		var errorMsg = '${errorMsg}';
		if (errorMsg != null && errorMsg != '') {
			alert(errorMsg);
		}
		
		//showErrorMsgInInit();
	};
	
	function changelogin() {
		/* $("#oldPassword").val(hex_md5($("#oldPassword").val()));
		$("#newPassword").val(hex_md5($("#newPassword").val()));
		$("#confirmPassword").val(hex_md5($("#confirmPassword").val())); */
	}
	
	
	function checkloginold(item){
		$("#oldPassword_i").text("");
		if($(item).val() != null && $(item).val() != ""){
			lold = 1;
			$("#div_old").removeClass("has-error").addClass("has-success");
		}else{
			lold = 0;
			$("#div_old").addClass("has-error");
			$("#oldPassword_i").text('原密码不能为空');
		}
		cl();
	}
	function checkloginnew(item){
		$("#newPassword_i").text("");
		if($(item).val() != null && $(item).val() != ""){
			if($(item).val().length < 8){
				lnew = 0;
				$("#div_new").addClass("has-error");
				$("#newPassword_i").text('新密码不得小于8位');
			} else {
				if(Evaluate($(item).val()) < 2){
					lnew = 0;
					$("#div_new").addClass("has-error");
					$("#newPassword_i").text('密码复杂度太弱');
				} else {
					lnew = 1;
					$("#div_new").removeClass("has-error").addClass("has-success");
				}
			}
			//checklogincon($("#confirmPassword"));
		}else{
			lnew = 0;
			$("#div_new").addClass("has-error");
			$("#newPassword_i").text('新密码不能为空');
		}
		
		cl();
	}
	function checklogincon(item){
		$("#confirmPassword_i").text("");
		if($(item).val() != null && $(item).val() != ""){
			if($(item).val() == $("#newPassword").val()){
				lcon = 1;
				$("#div_confirm").removeClass("has-error").addClass("has-success");
			} else {
				lcon = 0;
				$("#div_confirm").addClass("has-error");
				$("#confirmPassword_i").text('两次输入密码不一致');
			}
		}else{
			lcon = 0;
			$("#div_confirm").addClass("has-error");
			$("#confirmPassword_i").text('确认密码不能为空');
		}
		
		cl();
	}
	
	function cl(){
		if(lold == 1 && lnew == 1 && lcon == 1){
			$("#logsub").removeAttr("disabled"); 
		} else {
			$("#logsub").attr("disabled", "disabled");
		}
	}
	
	function Evaluate(pass){
		 return pass.match(/[a-z](?![^a-z]*[a-z])|[A-Z](?![^A-Z]*[A-Z])|\d(?![^\d]*\d)|[^a-zA-Z\d](?![a-zA-Z\d]*[^a-zA-Z\d])/g).length;
	}
	
	var needChangePwd = "${param.needChangePwd}" == "true"? true:false;
	$(function() {
		//数据校验
		$("#modPwForm").bootstrapValidator({
			message : '请输入有效值',
			feedbackIcons : {
				valid : 'glyphicon glyphicon-ok',
				invalid : 'glyphicon glyphicon-remove',
				validating : 'glyphicon glyphicon-refresh'
			},
			submitHandler : function(validator,roleform, submitButton) {
				console.log(validator);
				modals.confirm('确认保存？', function() {
					//Save Data，对应'submit-提交'
					var params = modPwForm.getFormSimpleData();
					console.log(params);
					var path = '/modifyPassword.json';
					ajaxPost(basePath + '/modifyPassword.json', params, function(data, status) {
						if(data.successMsg){
							modals.closeWin("modifyPasswordWin"); 
							modals.info(data.successMsg);
						} else {
							modals.error(data.errorMsg);
						}
					});
				});
			},
			fields : {
				oldPassword : {
					validators : {
						notEmpty : {
							message : '请输入原密码'
						}
					}
				},
				newPassword : {
					validators : {
						notEmpty : {
							message : '请输入新密码'
						},
						stringLength: {/*长度提示*/
                            min: 8,
                            message: '新密码长度不得小于8位'
                        },
                        different: {//不能和用户名相同
                            field: 'oldPassword',//需要进行比较的input name值
                            message: '新密码不能和原密码相同'
                        },
                        regexp: {/* 只需加此键值对，包含正则表达式，和提示 */
                            regexp: /^(?![a-zA-z]+$)(?!\d+$)(?![\W]+$)(?![a-zA-z\d]+$)(?![a-zA-z\W]+$)(?![\d\W]+$)[\S]+$/,
                            message: '包含数字、字母、除空格外的特殊字符.'
                        },
					}
				},
				confirmPassword :{
					validators : {
						notEmpty : {
							message : '请输入确认密码'
						},
						identical: {//相同
	                         field: 'newPassword', //需要进行比较的input name值
	                         message: '两次密码不一致'
	                    },
					}
				}
			}
		});
		
		$("#modPwForm input").blur(function(){
			var field = $(this).attr("name");
			if(field=="oldPassword"){
				$("#modPwForm").data('bootstrapValidator').updateStatus("newPassword",'NOT_VALIDATED').validateField("newPassword");
			} else if(field=="newPassword"){
				$("#modPwForm").data('bootstrapValidator').updateStatus("confirmPassword",'NOT_VALIDATED').validateField("confirmPassword");
			}
			$("#modPwForm").data('bootstrapValidator').validateField(field);
		})
		
		if(needChangePwd) {
			$("#modPwForm .box-footer").prepend("<span class='pull-left text-info mt-1'>当前密码为系统随机密码，请尽快进行修改！</span>");
		}
		//初始化控件
        var modPwForm = $("#modPwForm").form();
	});
</script>