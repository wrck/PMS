<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %> 
<%@taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="system.title" /></title>
<cssTag>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datepicker/datepicker3.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/select2/select2.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css">
</cssTag>
</head>
<body>
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>
			<span>用户管理</span> <small>新增</small>
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"><i class="fa fa-dashboard"></i> 首页</a></li>
			<li><a href="#">系统管理</a></li>
			<li class="active">用户管理</li>
		</ol>
	</section>
	<section class="content">
		<div class="row">
			<div class="col-xs-12">
				<div class="box box-info">
					<form id="user-form" name="user-form" class="form-horizontal">
						<input type="hidden" name="userId"> <input type="hidden"
							name="compID"> <input type="hidden" id="avatar"
							name="avatar">
						<div class="box-header">
							<div class="col-xs-12 text-center">
								<div class="avatar-container text-center">
									<img
										src="${pageContext.request.contextPath}/static/common/images/avatar.png"
										id="avatarImg" class="avatar-img"
										onerror="this.src='${pageContext.request.contextPath}/static/common/images/avatar.png'" />
								</div>
								<div>
									<button type="button" class="btn btn-sm btn-camera"
										data-btn-type="upload">
										<i class="fa fa-camera">&nbsp;上传/更改头像</i>
									</button>
								</div>
							</div>
						</div>
						<div class="box-body">
							<div class="col-md-6">
								<div class="form-group">
									<label for="userName" class="col-sm-3 control-label">登录名</label>
									<div class="col-sm-8">
										<input type="text" class="form-control" id="userName"
											name="userName" placeholder="登录名"
											${id!=null && id!=0?"readonly":""}>
									</div>
								</div>
								<!--  <div class="form-group">
		                            <label for="birthday" class="col-sm-3 control-label">出生日期</label>
		                            <div class="input-group date col-sm-8">
		                                <div class="input-group-addon">
		                                    <i class="fa fa-calendar"></i>
		                                </div>
		                                <input type="text" class="form-control" data-flag="datepicker" data-format="yyyy-MM-dd"
		                                       id="birthday" name="birthday"
		                                       placeholder="出生日期">
		                            </div>
		                        </div> -->
								<div class="form-group">
									<label for="roleIds" class="col-sm-3 control-label">角色</label>
									<div class="col-sm-8">
										<shiro:lacksRole name="admin">
											<mvc:select id="roleSelect" path="roles" multiple="multiple"
												cssClass="col-sm-9 form-control" disabled="true">
												<mvc:options items="${roles}" itemValue="roleId"
													itemLabel="roleNameZn" />
											</mvc:select>
										</shiro:lacksRole>
										<shiro:hasRole name="admin">
											<mvc:select id="roleSelect" path="roles" multiple="multiple"
												cssClass="col-sm-9 form-control">
												<mvc:options items="${roles}" itemValue="roleId"
													itemLabel="roleNameZn" />
											</mvc:select>
										</shiro:hasRole>

										<!-- <select id="roleSelect" multiple="multiple" class="col-sm-9 form-control">
                                        </select> -->
									</div>
								</div>
								<div class="form-group">
									<label for="telphone" class="col-sm-3 control-label">座机</label>

									<div class="col-sm-8">
										<input type="text" class="form-control" id="telphone"
											name="telphone" placeholder="座机">
									</div>
								</div>
								<div class="form-group">
									<label for="email" class="col-sm-3 control-label">Email</label>

									<div class="col-sm-8">
										<input type="text" class="form-control" id="email"
											name="email" placeholder="Email">
									</div>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label for="realName" class="col-sm-3 control-label">姓名</label>
									<div class="col-sm-8">
										<input type="text" class="form-control" id="realName"
											name="realName" placeholder="姓名">
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 control-label">性别</label>
									<div class="col-sm-8">
										<label class="control-label"> <input type="radio"
											name="sex" data-flag="icheck" value="1"> 男
										</label> &nbsp; <label class="control-label"> <input
											type="radio" name="sex" data-flag="icheck" value="0">
											女
										</label>
									</div>
								</div>
								<div class="form-group">
									<label for="mobile" class="col-sm-3 control-label">手机</label>

									<div class="col-sm-8">
										<input type="text" class="form-control" id="mobile"
											name="mobile" placeholder="手机">
									</div>
								</div>
								<div class="form-group">
									<label for="status" class="col-sm-3 control-label">状态</label>
									<div class="col-sm-8">
										<label class="control-label"> <input type="radio"
											name="status" data-flag="icheck" value="1"> 有效
										</label> &nbsp; <label class="control-label"> <input
											type="radio" name="status" data-flag="icheck" value="0">
											失效
										</label> &nbsp; <label class="control-label"> <input
											type="radio" name="status" data-flag="icheck" value="2">
											锁定
										</label>
									</div>
								</div>
							</div>
						</div>
						<!-- /.box-body -->
						<div class="box-footer text-right">
							<div class="pull-left">
								<c:if test='${isCurrentUser && isCas == "0"}'>
									<button type="button" class="btn btn-success"
										data-btn-type="modify">修改密码</button>
								</c:if>
								<shiro:hasRole name="admin">
									<c:if test='${isCas == "0"}'>
										<button type="button" class="btn btn-danger"
											data-btn-type="rest">重置密码</button>
									</c:if>
								</shiro:hasRole>
							</div>
							<div class="pull-right">
								<button type="button" class="btn btn-default"
									data-btn-type="cancel">取消</button>
								<button type="submit" class="btn btn-primary"
									data-btn-type="save">提交</button>
							</div>
						</div>
						<!-- /.box-footer -->
					</form>
				</div>
			</div>
		</div>
	</section>
	<div id="resetPasswordWin" class="modal fade in"
		aria-labelledby="myModalLabel" aria-hidden="true"
		style="display: none;">
		<div class="modal-dialog" style="width: 700px;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">
						<li class="fa fa-remove"></li>
					</button>
					<h5 class="modal-title">重置密码</h5>
				</div>
				<div class="modal-body" id="modRestDiv">
					<form id="modRestForm" name="modRestForm"
						class="form-horizontal bv-form" novalidate="novalidate">
						<input type="hidden" name="userId"> <input type="hidden"
							name="userName">
						<div class="box-body">
							<div class="form-group has-feedback" id="div_old">
								<label for="receiveEmail" class="col-sm-3 control-label">邮箱地址：</label>
								<div class="col-sm-8">
									<input type="text" name="email" class="form-control"
										id="receiveEmail" placeholder="随机密码接收邮箱地址，默认不填为用户邮箱！">
								</div>
							</div>
						</div>
						<div class="box-footer text-right">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">取消</button>
							<button type="button" class="btn btn-primary"
								data-btn-type="submit">提交</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
<jsTag>
    <script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/datepicker/bootstrap-datepicker.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/select2/select2.min.js"></script>
	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script>
	    //tableId,queryId,conditionContainer
	    var form = null;
	    var needChangePwd = "${param.needChangePwd}" == "true"? true:false;
	    var basePath = "${pageContext.request.contextPath}";
	    //var id = "${id!=0 && id!=null?id:0}";
	    var id = "${id}";
	    var userId = "<shiro:principal property='userId'></shiro:principal>";
	    $(function () {
	        //数据校验
	        $("#user-form").bootstrapValidator({
	            message: '请输入有效值',
	            feedbackIcons: {
	                valid: 'glyphicon glyphicon-ok',
	                invalid: 'glyphicon glyphicon-remove',
	                validating: 'glyphicon glyphicon-refresh'
	            },
	            submitHandler: function (validator, userform, submitButton) {
	                modals.confirm('确认保存？', function () {
	                    //Save Data，对应'submit-提交'
	                    var params = form.getFormSimpleData();
	                    var roleIds = $("#roleSelect").val();
	                    params["roleIds"] = roleIds ? roleIds.join() : "";
	                    var url = '/sys/user/detail.json';
	                    //if(id !="0"){
	                    if(id != ""){
	                    	url = '/sys/user/'+id+'.json';
	                    }
	                    ajaxPost(basePath + url, params, function (data, status) {
 	                        if (status == 'success') {
 	                            //if (id != "0") {//更新
 	                            if (id != "") {//更新
 	                            	window.location.reload(true);
 	                                //gotolist(id);
 	                            } else {//新增
									window.location.href = basePath + "/sys/user/" + data.userId + ".html";
									// modals.info("数据保存成功");
									// gotolist();
								}
							}
						});
					});
				},
				fields : {
					realName : {
						validators : {
							notEmpty : {
								message : '请输入姓名'
							}
						}
					},
					sex : {
						validators : {
							notEmpty : {
								message : '请选择性别'
							}
						}
					},
					birthday : {
						validators : {
							notEmpty : {
								message : '请输入出生日期'
							},
							date : {
								format : "YYYY-MM-DD",
								message : '请输入有效日期'
							}
						}
					},
					userName : {
						validators : {
							notEmpty : {
								message : '请输入登录名'
							},
							remote:{
								message: "该用户名已存在，请重新输入",
								url: "checkUnique.json",
								type: "POST",
								delay: "1000",
								data: function(validator) {
	                               return {
	                            	   __RequestVerificationToken: __RequestVerificationToken,
	                               };
	                            }
							}
						}
					},
					email : {
						validators : {
							notEmpty : {
								message : '请输入邮件',
							},
							emailAddress : {
								message : '非法的邮件格式',
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
			form = $("#user-form").form();
			// 初始化角色列表
			/* var roleData = [];
            ajaxPost(basePath+"/sys/role/list.json",null,function(data){
                $.each(data.data,function(index,item){
                	var role = {};
                	role.id = item.roleId;
                	role.text = item.roleNameZn;
                    roleData.push(role);
                });
                $("#roleSelect").select2({width:'100%',tags: true,data: roleData,placeholder:"角色"});
            }); */
            
            $("#roleSelect").select2({width:'100%',tags: true,placeholder:"角色"});
			//回填id
			//if (id != "0") {
			if (id != "") {//更新
				$("#user-form").data('bootstrapValidator').removeField("userName");
				$("#user-form").prepend('<input type="hidden" name="_method" value="PUT">');
				ajaxPost(basePath + "/sys/user/" + id + ".json", {
					id : id
				}, function(result) {
					var user = result.user;
					form.initFormData(user);
					$("#modRestForm").form().initFormData(user);
					$(".content-header h1 small").html(
							"编辑用户【" + user.userName + "】");

					//用户信息回填
					var userInfo = result.userInfo;
					form.initFormData(userInfo);
					//头像回填
					if(userInfo && userInfo.avatar){
						$("#avatarImg").attr("src", basePath + userInfo.avatar);
					}
					
					var roleIds = result.roleIds;
					if (roleIds) {
						$("#roleSelect").val(roleIds.split(",")).trigger('change');
					}
					
					//头像回填
					/*  ajaxPost(basePath+"/sys/user/getAvatar",{userId:id},function(result){
					     setAvatar(result.id,result.src,false);
					 }) */
				});
			}

			//cancel
			$("[data-btn-type='cancel']").click(function() {
				window.history.back();
			})

			$("[data-btn-type='upload']").click(function() {
				uploadAvatar();
			})
			
			$("[data-btn-type='modify']").click(function() {
				modals.openWin({
					winId : 'modifyPasswordWin',
					title : '修改密码',
					width : '700px',
					url : basePath + "/sys/modals/password.html?needChangePwd=" + needChangePwd
				});
			})
			
			$("[data-btn-type='rest']").click(function() {
				modals.showWin("resetPasswordWin");
			})
			
			if(needChangePwd) {
				$("[data-btn-type='modify']").click();
			}
			
			$("#modRestForm button[data-btn-type='submit']").click(function(){
				var params = $("#modRestForm").form().getFormSimpleData();
				ajaxPost(basePath + "/admin/resetPassword.json", params, function(data,status){
					if(data.successMsg) {
						modals.hideWin("resetPasswordWin");
						modals.info(data.successMsg);
					} else {
						modals.info(data.errorMsg);
					}
				})
			})
			
			/* $("#userName").blur(function() {
				var userName = $(this).val().trim();
				ajaxPost("checkUnique.json", {userName: userName}, function() , async, contentType)
			}); */
		});

		function gotolist(id) {
			window.loadPage(basePath + "/sys/user/page/list?id=" + id);
		}

		var avatarWin = "avatarWin";
		function uploadAvatar() {
			modals.openWin({
				winId : avatarWin,
				title : '上传头像',
				width : '700px',
				url : basePath + "/sys/modals/avatar?userId=" + id
			});
		}

		function resetForm() {
			form.clearForm();
			$("#user-form").data('bootstrapValidator').resetForm();
		}

		function setAvatar(avatar_id, avatar_url, isAdd) {
			$("#avatarImg").attr("src", basePath + avatar_url);
			//如果是新增 绑定用户
			if (isAdd) {
				$("#avatarId").val(avatar_id);
			} else {
				$("#avatarId").val(null);
			}
			$("input[name='avatar']").val(avatar_url);
		}
		
		function modifyPassword() {
			modals.openWin({
				winId : modifyPasswordWin,
				title : '修改密码',
				width : '700px',
				url : basePath + "/sys/modals/password.html"
			});
		}
		
		function resetPassword() {
			modals.openWin({
				winId : modifyPasswordWin,
				title : '修改密码',
				width : '700px',
				url : basePath + "/sys/modals/password.html"
			});
		}
	</script>
</jsTag>
</html>