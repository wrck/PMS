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
	
	<style>
		.display-flex {
			display: flex;
	        align-items: baseline;
		}
		.flex-shrink-0 {
			flex-shrink: 0;
		}
		.flex-shrink-1 {
			flex-shrink: 1;
		}
		.flex-grow-1 {
			flex-shrink: 1;
		}
		.flex-grow-2 {
			flex-grow: 2;
		}
		.flex-grow-3 {
			flex-grow: 3;
		}
		.form-inline .form-group {
		    margin-bottom: 7px;
		    padding-left: 0!important;
		}
		
		.form-inline .display-flex {
		    display: inline-flex;
	        align-items: baseline;
		}
		
		.form-inline .control-label {
		    margin-right: 7px;
		}
		
		.form-inline .display-flex .form-control {
		    width: 100%;
		}
		
	</style>
</cssTag>
</head>
<body>
	<div id="app">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1 id="pageTitle">
				<template v-if="isShow">
					<span>{{targetValue.projectCode}}</span><small>{{targetValue.projectName}}</small>
				</template>
				<template v-else>
					<span></span><small></small>
				</template>
			</h1>
			<span class="display-none"></span>
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
						<form id="projectForm" method="post" :action="formAction" name="projectForm" class="form-inline">
							<div class="box-body row" v-if="isShow">
								<!-- <div class="field-inputs">
									<template v-for="field in fieldList" v-if="field.type != 'textarea'">
										<div class="form-group">
											<label :for="field.field" style="text-align: right;" :style="{width: maxLabelWidth}" class="control-label">{{field.name}}</label>
											<input :id="field.cssId || field.field" :type="field.type" class="form-control" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
													:value="targetValue[field.field]" :placeholder="field.name || field.title" :style="field.cssStyle">
										</div>
									</template>
								</div>
							    <div class="field-textarea row">
							    	<template v-for="field in fieldList" v-if="field.type == 'textarea'">
							    		<div class="form-group col-sm-6">
											<label :for="field.field" style="text-align: right;" :style="{width: maxLabelWidth}" class="control-label">{{field.name}}</label>
											<textarea :id="field.cssId || field.field" :type="field.type" class="form-control" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
													:value="targetValue[field.field]" :placeholder="field.name || field.title" :style="field.cssStyle"></textarea>
										</div>
									</template>
							    </div> -->
								<template v-for="field in fieldList">
									<template v-if="field.type == 'hidden' || !field.visible">
										<input :id="field.cssId || field.field" type="hidden" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
												:value="getFieldValue(field)" :placeholder="field.name || field.title" :style="field.cssStyle"
										>
									</template>
									<template v-else-if="field.type == 'textarea'">
										<div class="form-group display-flex col-sm-12 col-md-6">
											<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
											<textarea :id="field.cssId || field.field" :type="field.type" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
													:value="getFieldValue(field)" :placeholder="field.name || field.title" :style="field.cssStyle" rows="2" style="resize:none;" draggable="false"
													:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required"
													></textarea>
										</div>
									</template>
									<template v-else-if="field.type == 'date'">
										<div class="form-group display-flex col-sm-6 col-md-3">
											<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
											<input :id="field.cssId || field.field" type="text" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
													:value="getFieldValue(field)" :placeholder="field.name || field.title" :style="field.cssStyle"
													data-flag="datepicker" :data-format="field.render" autocomplete="off"
													:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required"
											>
										</div>
									</template>
									<template v-else-if="field.type == 'datetime'">
										<div class="form-group display-flex col-sm-6 col-md-3">
											<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
											<input :id="field.cssId || field.field" type="text" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
													:value="getFieldValue(field)" :placeholder="field.name || field.title" :style="field.cssStyle"
													data-flag="datetimepicker" data-format="field.render" autocomplete="off"
													:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required"
											>
										</div>
									</template>
									<template v-else-if="field.type == 'select'">
										<div class="form-group display-flex col-sm-6 col-md-3">
											<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
											<select :id="field.cssId || field.field" type="text" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
													:value="getFieldValue(field)" :placeholder="field.name || field.title" :style="field.cssStyle"
													:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required">
												<option :value="item[field.extKey]" v-for="item in getDataValue(field.extData)" :selected="item[field.extKey] == getFieldValue(field)" >{{item[field.extValue]}}</option>
											</select>
										</div>
									</template>
									<template v-else>
										<div class="form-group display-flex col-sm-6 col-md-3">
											<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
											<input :id="field.cssId || field.field" :type="field.type" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
													:value="getFieldValue(field)" :placeholder="field.name || field.title" :style="field.cssStyle" 
													:readonly="field.readonly" :required="field.required" autocomplete="off">
										</div>
									</template>
								</template>
							</div>
							<!-- /.box-body -->
							<div class="box-footer text-right">
								<button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">取消</button>
								<button type="submit" class="btn btn-primary" data-btn-type="save">保存</button>
							</div>
							<!-- /.box-footer -->
						</form>
					</div>
				</div>
			</div>
		</section>
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
	<script src="${pageContext.request.contextPath}/static/pm/js/initComm.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/router.js"></script>
	<script src="${pageContext.request.contextPath}/static/vue/vue.min.js"></script>
	<script>
	    //tableId,queryId,conditionContainer
	    var form = null;
	    var id = "${id}" || 0;
	    var userId = "<shiro:principal property='userId'></shiro:principal>";
   		var sysData =[],inputData=[],varFields={};
   		var vm;
	    $(function () {
	    	var url = id == 0 ? pm.project.api.create(location.search) : pm.project.api.detail(id);
    		ajaxGet(url, null, function(data, status){
				if (status == 'success') {
					vm = new Vue({
						el: "#app",
						data: $.extend({}, data, {
							isCreate: id != 0,
							isShow: true,
							formAction: pm.project.api.detail(id),
   							fieldList: data.fieldList || [],
   							targetName: data.targetName,
    						targetValue: data.targetValue
    				 	}),
    				 	computed: {
    				 		maxLabelWidth: function() {
    				 			var fieldList = this.fieldList || [];
    				 			var width = "";
    				 			var maxLen = 0;
    				 			for (var i = 0; i < fieldList.length; i++) {
									var field = fieldList[i];
									var name = field.name || "";
									if (maxLen < name.length) {
										maxLen = name.length;
									}
								}
    				 			return maxLen + "rem";
    				 		}
    				 	},
    				 	methods: {
    				 		getFieldValue: function(field) {
    				 			var value;
    				 			try {
    				 				value = eval("this.targetValue." + field.field);
    				 			} catch(e) {}
   				 				try {
    				 				value = value || eval("this.targetValue." + field.alias);
   				 				} catch(e) {}
    				 			return value;
    				 		},
    				 		getDataValue: function(key) {
    				 			var value;
    				 			try {
    				 				value = eval("this." + key);
    				 			} catch(e) {
    				 			}
   				 				try {
    				 				value = value || JSON.parse(key);
   				 				} catch(e) {
   				 				}
    				 			try {
    				 				value = value || eval(key);
   				 				} catch(e) {
   				 					value = key;
   				 				}
    				 			return value;
    				 		}
    				 		
    				 	}
    				});
					
					form = $("#projectForm").form();
					form.initFormData(data.targetValue);
		    		$("#projectForm").bootstrapValidator({
		                message: '请输入有效值',
		                feedbackIcons:sys.common.feedbackIcons,
		                submitHandler: function(validator, form2, submitButton){
		                	modals.confirm({text:'确认执行分摊？', 
		                		callback: function () {
			                	
			                		var headers = {};
			                		headers['__RequestVerificationToken'] = __RequestVerificationToken;
			                		var index3 = layer.load(1);
			                		var formData = form.getFormSimpleData();
			                		var url = id == 0 ? pm.project.api.create() : pm.project.api.update(id);
			                		ajaxPost(url, formData,function(data,status){
			                			if(data.status){
			        						modals.correct("保存成功");
			        						handleResult(data);
			        					} else{
			        						modals.error('操作失败！<br>' + (data.message || ""));
			        					}
			                		},null,null,function(){
			    						layer.close(index3);
			    						$(submitButton).removeAttr("disabled");
			    					})
		                		}, 
		                		cancel_call: function() {
		                			$(submitButton).removeAttr("disabled");
		                		}
		                	});
		                }, 
		    			fields : varFields
		    		});
    			 }
    		})
    		
    		function handleData() {
    			
    		}
    		
    		function handleResult(results){
    			var isCreate = id == 0;
    			id = (results.projectVO || {}).id || (results.projectVO || {}).projectId || 0;
        		if (isCreate) {
	        		window.location.replace(pm.project.html.detail(id));
        		} else {
        			ajaxGet(pm.project.api.detail(id), null, function(data, status){
	    				if (status == 'success') {
	   						vm._data.targetValue = data.targetValue;
	    				}
	        		});
        		}
    		}
    		
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