<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">
    <style>
        #searchDiv{
            display: none;
            margin-bottom: 1rem;
        }
        #modFwdTable {
            width:30%;
            align:center;
        }
        #modPwDiv {
            width:30%;
            margin:0 auto;
        }
    </style>
</cssTag>
</head>
<body>
    <div id="modPwDiv">
        <form method="post" action="${pageContext.request.contextPath}/modifyPassword.html" id="modPwForm" onsubmit="changelogin()">
            <div class="form-group" id="div_old">
                <label class="control-label" for="inputSuccess"><i id="oldPassword_i"></i></label>
                <input type="password" name="oldPassword" class="form-control" id="oldPassword" placeholder="原密码" onblur="checkloginold(this)"/>
            </div>
            <div class="form-group" id="div_new">
                <label class="control-label" for="inputSuccess"><i id="newPassword_i"></i></label>
                <input type="password" name="newPassword" class="form-control" id="newPassword" placeholder="新密码" onKeyUp="checkloginnew(this)"/>
            </div>
            <div class="form-group" id="div_confirm">
                <label class="control-label" for="inputSuccess"><i id="confirmPassword_i"></i></label>
                <input type="password" name="confirmPassword" class="form-control" id="confirmPassword" placeholder="确认密码" onKeyUp="checklogincon(this)"/>
            </div>
            <div class="box-footer">
                <button type="submit" class="btn btn-primary" id="logsub" disabled="disabled">修改</button>
            </div>
        </form>
    </div>
</body>
</html>
<jsTag>
    <!-- DataTables -->
    <script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/jquery.dataTables.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/dataTables.bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/common/js/dataTablesExt.js"></script>
    <!-- 表单验证相关 -->
    <script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
    <script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>

    <%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
    <script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
    <script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
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
        $("#oldPassword").val(hex_md5($("#oldPassword").val()));
        $("#newPassword").val(hex_md5($("#newPassword").val()));
        $("#confirmPassword").val(hex_md5($("#confirmPassword").val()));
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
    </script>
</jsTag>