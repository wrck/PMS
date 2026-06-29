<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %> 
<%@taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib  prefix="dp" uri="/myTag" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="system.title" /></title>
<cssTag>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css"/>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/select2/select2.min.css">
	<dp:link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css" />
</cssTag>
</head>
<body>
	<!-- Content Header (Page header) -->
	<section class="content-header">
	    <h1>
	        <span>用户管理</span>
	        <small>新增</small>
	    </h1>
	    <ol class="breadcrumb">
	    </ol>
	</section>
	<section class="content">
	    <div class="row">
	        <div class="col-xs-12">
	            <div class="box box-info">
		            <form id="vacation-form" name="vacation-form" class="form-horizontal">
		                <div class="box-body">
		                    <div class="col-md-6">
	                    	 	<div class="form-group">
		                            <label for="beginDate" class="col-sm-3 control-label">开始时间：</label>
		                            <div class="col-sm-8">
		                                <input type="date" class="form-control" id="beginDate" name="beginDate"
		                                       placeholder="开始时间" >
		                            </div>
		                        </div>
		                        <div class="form-group">
		                            <label for="endDate" class="col-sm-3 control-label">结束时间：</label>
		
		                            <div class="col-sm-8">
		                                <input type="date" class="form-control" id="endDate" name="endDate" placeholder="结束时间">
		                            </div>
		                        </div>
		                        <div class="form-group">
		                            <label for="workDays" class="col-sm-3 control-label">请假天数：</label>
		                            <div class="col-sm-8">
		                                <input type="text" class="form-control" id="workDays" name="workDays" placeholder="请假天数">
		                            </div>
		                        </div>
		                    </div>
		                    <div class="col-md-6">
		                    	<div class="form-group">
		                            <label for="vacType" class="col-sm-3 control-label">休假类型：</label>
		                            <div class="col-sm-8">
                                        <select name="vacType" id="vacType">
			                                <option>--请选择--</option>
			                                <option value="0">年假</option>
			                                <option value="1">事假</option>
			                                <option value="2">病假</option>
			                            <select>
		                            </div>
		                        </div>
		                        <div class="form-group">
		                            <label for="reason" class="col-sm-3 control-label">原因：</label>
		                            <div class="col-sm-8">
		                                <textarea name="reason" cols="33" rows="5"></textarea>
		                            </div>
		                        </div>
		                    </div>
		                </div>
		                <!-- /.box-body -->
		                <div class="box-footer text-right">
		                	<div class="pull-right">
			                    <button type="button" class="btn btn-default" data-btn-type="cancel" >取消 </button>
			                    <button type="submit" class="btn btn-primary" data-btn-type="save">提交</button>
		                	</div>
	                	</div>
		                <!-- /.box-footer -->
		            </form>
	            </div>
	        </div>
	    </div>
	</section>
</body>
<jsTag>
	<script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/datepicker/bootstrap-datepicker.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/select2/select2.min.js"></script>
	<%-- <dp:script src="${pageContext.request.contextPath}/static/common/js/base.js"></dp:script> --%>
	<dp:script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></dp:script>
	<dp:script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></dp:script>
	<script>
	    //tableId,queryId,conditionContainer
	    var form = null;
	   // var id = "{id?default(0)}";
	    var id = "${id!=0 && id!=null?id:0}";
	    var userId = "<shiro:principal property='userId'></shiro:principal>";
	    $(function () {
	        //数据校验
	        $("#vacation-form").bootstrapValidator({
	            message: '请输入有效值',
	            feedbackIcons: {
	                valid: 'glyphicon glyphicon-ok',
	                invalid: 'glyphicon glyphicon-remove',
	                validating: 'glyphicon glyphicon-refresh'
	            },
	            submitHandler: function (validator, vacationform, submitButton) {
	                modals.confirm('确认保存？', function () {
	                    //Save Data，对应'submit-提交'
	                    var params = form.getFormSimpleData();
	                    var url = '/workflow/vacation/detail.json';
	                    if(id !="0"){
	                    	url = '/workflow/vacation/'+id+'.json';
	                    }
	                    ajaxPost(basePath + url, params, function (data, status) {
	                    	if(data.status){
                                modals.correct(data.message);
                                taskTable.reloadRowData();
                            }else{
                                modals.error(data.message);
                            }
						});
					});
				}
			});
			//初始化控件
			form = $("#vacation-form").form();
			// 初始化角色列表
			/* var roleData = [];
            ajaxPost(basePath+"/workflow/role/list.json",null,function(data){
                $.each(data.data,function(index,item){
                	var role = {};
                	role.id = item.roleId;
                	role.text = item.roleNameZn;
                    roleData.push(role);
                });
                $("#roleSelect").select2({width:'100%',tags: true,data: roleData,placeholder:"角色"});
            }); */
            
			//回填id
			if (id != "0") {
				$("#vacation-form").prepend('<input type="hidden" name="_method" value="PUT">');
				ajaxPost(basePath + "/workflow/vacation/" + id + ".json", {
					id : id
				}, function(result) {
					var vacation = result.vacation;
					form.initFormData(vacation);
					$(".content-header h1 small").html("编辑申请单");
				});
			}

			//cancel
			$("[data-btn-type='cancel']").click(function() {
				window.history.back();
			})

		});

		function resetForm() {
			form.clearForm();
			$("#vacation-form").data('bootstrapValidator').resetForm();
		}

	</script>
</jsTag>
</html>