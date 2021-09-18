<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title><spring:message code="system.title"/> | Log in</title>
  <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
  <!-- Bootstrap 3.3.6 -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/bootstrap/css/bootstrap.min.css">
  <!-- Font Awesome -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/font-awesome/css/font-awesome.min.css">
  <!-- Ionicons -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/ionicons/ionicons.min.css">
  <!-- Theme style -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/dist/css/AdminLTE.min.css">
  <!-- iCheck -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/square/blue.css">
  <style type="text/css">
    .login-box-body .has-success .form-control-feedback {
        color: #00a65a!important;
    }
    
    .login-box-body .has-error .form-control-feedback {
        color: #dd4b39!important;
    }
    
    #captcha {
        vertical-align: middle;
    }
    #img_captcha {
    	height: 30px;
    }
  </style>
</head>
<body class="hold-transition login-page">
<div class="login-box">
  <div class="login-logo">
    <a href="index"><b><spring:message code="system.title"/></b></a>
  </div>
  <!-- /.login-logo -->
  <div class="login-box-body">
    <p class="login-box-msg">${requestScope.error}</p>

    <form action="login.html" id="loginForm">
	  <div class="form-group has-feedback text-danger">
	  	<%-- <spring:message code="system.error.${requestScope.message}"/> --%>
	  </div>
      <div class="form-group has-feedback">
        <input type="text" name="username" class="form-control" placeholder="账号">
        <span class="glyphicon glyphicon-envelope form-control-feedback"></span>
      </div>
      <div class="form-group has-feedback">
        <input type="password" name="password" class="form-control" placeholder="密码">
        <span class="glyphicon glyphicon-lock form-control-feedback"></span>
      </div>
      <div class="form-group has-feedback">
      	<input type="text" id="captcha" name="captcha" size="10" maxlength="10" class="required" />
      	<label for="codeImg" class="field">
      	     <img title="点击更换" id="img_captcha" onclick="javascript:refreshCaptcha();" src="${pageContext.request.contextPath}/captchaCode.html">
      	     (看不清<a href="javascript:void(0)" onclick="javascript:refreshCaptcha()">换一张</a>)
		</label>
      </div>
      <div class="row">
        <div class="col-xs-8">
          <!-- <div class="checkbox icheck">
            <label>
              <a href="#">忘记密码</a>
            </label>
          </div> -->
        </div>
        <!-- /.col -->
        <div class="col-xs-4">
          <button type="submit" class="btn btn-primary btn-block btn-flat">登录</button>
        </div>
        <!-- /.col -->
      </div>
    </form>
  </div>
  <!-- /.login-box-body -->
</div>
<!-- /.login-box -->

<!-- jQuery 2.2.3 -->
<script src="${pageContext.request.contextPath}/static/plugins/jQuery/jquery-2.2.3.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<script src="${pageContext.request.contextPath}/static/bootstrap/js/bootstrap.min.js"></script>
<!-- bootstrap-validator -->
<script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
<!-- iCheck -->
<script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>

<script src="${pageContext.request.contextPath}/static/common/js/HashEncrypt.min.js"></script>
<script src="${pageContext.request.contextPath}/static/common/js/base.js"></script>
<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>

<script>
	var _captcha_id = "#img_captcha";
	var basePath = "${pageContext.request.contextPath}";
	function refreshCaptcha() {
	    $(_captcha_id).attr("src", basePath + "/captchaCode.html?t=" + Math.random());
	}
	$(function () {
		$('input').iCheck({
		  checkboxClass: 'icheckbox_square-blue',
		  radioClass: 'iradio_square-blue',
		  increaseArea: '20%' // optional
		});
		$("#loginForm").bootstrapValidator({
		    message: '请输入有效值',
		    submitHandler: function (validator, form, submitButton) {
	            var params = form.serializeArray();
	            //params[1].value = HashEncrypt.SHA1(params[1].value, true, false);
	            params[1].value = HashEncrypt.encryptHash(params[1].value, params[0].value);
	            params = $.param(params);
	            var url = $(form).attr("action");
	            ajaxPost(basePath + "/sys/login.json", params, function (data) {
	            	$("p.login-box-msg").html("");
                    if (data.error) {
                        $("p.login-box-msg").html(data.error);
                        refreshCaptcha();
                    } else {
                        window.location.href = basePath + "/sys/success.html";
                    }
	            })
		    },
		    fields : {
		    	username : {
		            validators : {
		                notEmpty : {
		                    message : '请输入用户名'
		                }
		            }
		        },
		        password : {
		            validators : {
		                notEmpty : {
		                    message : '请输入用户密码'
		                }
		            }
		        }
		    }
		});
	});
</script>
</body>
</html>
