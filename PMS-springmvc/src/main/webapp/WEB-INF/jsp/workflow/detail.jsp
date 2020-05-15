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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datatables/media/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datatables/extensions/Select/css/select.bootstrap.min.css">

	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datepicker/datepicker3.css">
	
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/select2/select2.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datetimepicker/css/bootstrap-datetimepicker.min.css">
	
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
		
		.select2-result-repository__title, .select2-result-repository__statistics {
			display: flex;
    		justify-content: space-between;
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
					<span>{{workflow.title}}</span>
				</template>
				<template v-else>
					<span></span><small></small>
				</template>
			</h1>
			<span class="display-none"></span>
			<span></span>
			<ol class="breadcrumb">
			</ol>
		</section>
		<section class="content">
			<div class="row">
				<div class="col-xs-12">
					<div class="box box-info formContainer">
						<div class="box-header">审核内容</div>
						<div id="taskEntityFormDiv" class="box-body row ml-0 form-inline" v-if="isShow">
							<form-inputs :form-cols="formCols" :field-list="fieldList" :target-name="targetName" :target-value="targetValue" :permissions="permissions" :roles="roles" :model="model"></form-inputs>
						</div>
						<div class="box-footer text-right" v-if="!workflow.hasTask">
							<button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">取消</button>
						</div>
					</div>
					<div class="box box-info formContainer mt-1" v-if="workflow.hasTask">
						<div class="box-header">任务办理</div>
						<form id="commonForm" method="post" :action="formAction" name="commonForm" class="form-inline">
							<!-- /.box-body -->
							<div id="formDiv" class="box-body row ml-0" v-if="isShow">
								<form-inputs :form-cols="1" :field-list="workflowFieldList" :target-name="'workflow'" :target-value="workflow" :permissions="permissions" :roles="roles" :model="model"></form-inputs>
							</div>
							<div class="box-footer text-right">
								<button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">取消</button>
								<button type="submit" class="btn btn-primary" data-btn-type="submit" v-if="workflow.hasTask">提交</button>
							</div>
							<!-- /.box-footer -->
						</form>
					</div>
					<div id="tabDiv" class="tabContainer" v-if="isShow">
						<%-- <%@include file="../template/vue-tab-component.jsp" %> --%>
						<nav-tab ref="workflowTab" tab-content-id="workflow" :tab-list="tabList" :target-name="'workflow'" :target-value="workflow" :permissions="permissions" :roles="roles" :model="model"></nav-tab>
					</div>
				</div>
			</div>
		</section>
	</div>
</body>
<jsTag>
<!-- DataTables -->
    <script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/jquery.dataTables.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/dataTables.bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/common/js/dataTablesExt.js"></script>
    <script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/datepicker/bootstrap-datepicker.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/datetimepicker/js/bootstrap-datetimepicker.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/select2/select2.full.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/autocomplete/jquery.autocomplete.js"></script>
	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/initComm.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/router.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/tab-init.js"></script>
	<script src="${pageContext.request.contextPath}/static/vue/vue.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/vue-form-inputs-component.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/vue-tab-pane-component.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/vue-tab-component.js"></script>
	<script>
	    //tableId,queryId,conditionContainer
	    $(function () {
	    	console.log(1);
	    	var taskEntityForm = null;
		    var form = null;
	        var commonTable;
	        var urlNamespace = "${urlNamespace}";
	        var model = "${model}";
	        var appId = model + "App";
	        var winId= model + "Win";
	        var formId = model + "Form";
	        var keyword = "${keyword}" || "id";
		    var id = "${id}" || 0;
		    var userId = "<shiro:principal property='userId'></shiro:principal>";
	   		var sysData =[],inputData=[],varFields={};
	   		var isModals = '${isModals}' == 'true';
	   		var search = '${pageContext.request.queryString}' || location.search;
	   		var taskId = '${taskId}' || 0;
	   		var vm;
	    	$("#commonForm").attr({id:formId, name: formId});
	    	$("#app").attr({id: appId});
	    	$("#tabDiv").attr({id: model + "TabDiv"});
	    	var url = id == 0 ? router(urlNamespace).api(model).create(search) : router(urlNamespace).api(model).detail(id, search);
    		ajaxGet(url, null, function(data, status){
				if (status == 'success') {
					vm = new Vue($.extend(true, {
							components: {
							    'form-inputs': FormInputs,
							    'nav-tab': NavTab,
							    'tab-pane': TabPane,
							},
						}, {
							el: "#" + appId,
							data: $.extend({}, data, {
								isModals,
								isCreate: id == 0,
								isShow: true,
								dataType: "form",
								formCols: 2,
								//formGroupClass: "col-sm-6 col-md-3",
								//formGroupTextareaClass: "col-sm-12 col-md-6",
								formAction: router(urlNamespace).api(model).detail(id),
	   							fieldList: data.fieldList || [],
	   							targetName: data.targetName || "",
	    						targetValue: data.targetValue || {},
	    						
	    						workflowFieldList: data.workflowFieldList || [],
	    						workflow: data.workflow || {},
	    						
	    						// 权限控制参数
	    						model: data.model || model,
	    						permissions: data.permissions || [],
	    						roles: data.roles || []
	    				 	}),
    				 	}
					));
					taskEntityForm = $("#taskEntityFormDiv").form();
					taskEntityForm.initFormData(data.targetValue);
					
					form = $("#" + formId).form();
					form.initFormData(data.workflow);
					var $container = $("#" + formId);
		    		$("#" + formId).bootstrapValidator({
		                message: '请输入有效值',
		                feedbackIcons:sys.common.feedbackIcons,
		                submitHandler: function(validator, form2, submitButton){
		                	var btnType = $(submitButton).data("btn-type");
		                	var confirmText = btnType == "submit" ? "提交" : "保存";
		                	modals.confirm({text:'确认' + confirmText + '？', 
		                		callback: function () {
			                		var index3 = layer.load(1);
			                		var formData = form.getFormSimpleData();
			                		var url = basePath + "/workflow/complete/" + formData.taskId + ".json";
			                		ajaxPost(url, formData,function(data,status){
			                			if(data.status){
			        						modals.correct(confirmText + "成功");
			        						handleResult.call(form2, data);
			        					} else{
			        						modals.error('操作失败！<br>' + (data.message || ""));
			        					}
			                		},null,null,function(){
			    						layer.close(index3);
			    						$("[type='submit']", form2).removeAttr("disabled");
			    					})
		                		}, 
		                		cancel_call: function() {
		                			$("[type='submit']", form2).removeAttr("disabled");
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
    			/* var targetName = results.targetName || model;
    			var targetValue = results[targetName] || {};
    			keyword = keyword || window.keyword || "id";
    			id = targetValue[keyword] || targetValue.id || 0; */
        		if (isCreate) {
        			if (isModals) {
        				var currentWinId = winId;
        				if (!$("#" + currentWinId).length) {
        					currentWinId = $(this).parents(".modal.in:first").attr("id");
        				}
        				modals.hideWin(currentWinId);
        				//modals.closeWin(winId);
        			} else {
		        		window.location.replace(router(urlNamespace).html(model).detail(id));
        			}
        		} else {
        			ajaxGet(router(urlNamespace).api(model).detail(id), null, function(data, status){
	    				if (status == 'success') {
	    					vm._data.fieldList = data.fieldList || [];
	    					vm._data.tabList = data.tabList || [];
	   						vm._data.targetValue = data.targetValue;
	   						
	   						vm._data.workflowFieldList = data.workflowFieldList || [];
	   						vm._data.workflow = data.workflow || {};
	   						
	   						var wfvm = vm.$refs['workflowTab'];
	   						wfvm.refreshNavTab({target: $('li.active a[data-toggle="tab"]', wfvm.$el)});
	   						//form.initFormData(data.targetValue);
	    				}
	        		});
        		}
    		}
		});
	</script>
</jsTag>
</html>