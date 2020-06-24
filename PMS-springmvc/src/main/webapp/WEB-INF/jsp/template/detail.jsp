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
<c:if test="${!isModals}">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datatables/media/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datatables/extensions/Select/css/select.bootstrap.min.css">

	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datepicker/datepicker3.css">
	
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/select2/select2.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css">
</c:if>	
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
			<h1 id="pageTitle" class="fade" :class="{in: isShow}"></h1>
			<span></span>
			<ol class="breadcrumb">
			</ol>
		</section>
		<section class="content">
			<div class="row">
				<div class="col-xs-12">
					<div class="box box-info formContainer">
						<form id="commonForm" method="post" :action="formAction" name="commonForm" class="form-inline fade" :class="{in: isShow}">
							<div id="formDiv" class="box-body row ml-0">
								<%-- <%@include file="../template/vue-form-component.jsp" %> --%>
								<form-inputs ref="formInputs" :form-cols="formCols" :field-list="fieldList" :target-name="targetName" :target-value="targetValue" :is-create="isCreate" :permission-type="permissionType" :permissions="permissions" :roles="roles" :model="model"></form-inputs>
							</div>
							<!-- /.box-body -->
							<div class="box-footer text-right">
								<div class="pull-left">
									<button type="button" class="btn btn-success" v-if="hasTask" data-btn-type="completeTask">任务办理</button>
									<button type="button" class="btn btn-primary" v-if="canStartProcess" data-btn-type="startProcess">提交审批</button>
								</div>
								<button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">{{isModals ? "取消" : "返回"}}</button>
								<button type="submit" class="btn btn-primary" v-if="permissionType && permissionType != 'view'" data-btn-type="save">保存</button>
							</div>
							<!-- /.box-footer -->
						</form>
					</div>
					<div id="tabDiv" class="tabContainer fade" :class="{in: isShow}">
						<%-- <%@include file="../template/vue-tab-component.jsp" %> --%>
						<nav-tab ref="formTab" :tab-list="tabList" :target-name="targetName" :target-value="targetValue" :permission-type="permissionType" :permissions="permissions" :roles="roles" :model="model"></nav-tab>
					</div>
				</div>
			</div>
		</section>
	</div>
</body>
<jsTag>
<c:if test="${!isModals}">
<!-- DataTables -->
    <script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/jquery.dataTables.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/dataTables.bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/common/js/dataTablesExt.js"></script>
    <script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/datepicker/bootstrap-datepicker.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/select2/select2.full.js"></script>
	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/initComm.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/router.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/tab-init.js"></script>
	<script src="${pageContext.request.contextPath}/static/vue/vue.min.js"></script>
</c:if>
	<script src="${pageContext.request.contextPath}/static/plugins/autocomplete/jquery.autocomplete.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/datetimepicker/js/bootstrap-datetimepicker.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/vue-form-input-component.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/vue-form-component.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/vue-tab-pane-component.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/vue-tab-component.js"></script>
	<script>
	    //tableId,queryId,conditionContainer
	    $(function () {
	    	console.log(1);
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
							// mixins: [formVueConfig]
						}, /* formVueConfig || {}, tabVueConfig || {}, */ {
							el: "#" + appId,
							data: $.extend({}, data, {
								isModals,
								isCreate: id == 0,
								isShow: false,
								dataType: "form",
								formCols: 2,
								//formGroupClass: "col-sm-6 col-md-3",
								//formGroupTextareaClass: "col-sm-12 col-md-6",
								formAction: router(urlNamespace).api(model).detail(id),
	   							fieldList: data.fieldList || [],
	   							tabList: data.tabList || [],
	   							targetName: data.targetName || data.model || model,
	    						targetValue: data.targetValue || {},
	    						
	    						// 权限控制参数
	    						urlNamespace: data.urlNamespace || urlNamespace,
	    						model: data.model || model,
	    						keyword: data.keyword || keyword,
	    						permissionType: data.permissionType || "",
	    						//permissionType: data.permissionType == undefined ? "all" : (data.permissionType || ""),
	    						permissions: data.permissions || [],
	    						roles: data.roles || [],
	    						
	    						// 任务检查标记
	    						currentTaskId: "",
	    				 	}),
	    				 	computed: {
	    				 		hasTask: function() {
	    				 			var _this = this;
	    				 			var taskId = this.currentTaskId;
	    				 			var hasTask = this.targetValue.hasTask;
	    				 			if (typeof hasTask == "undefined" || hasTask == null) {
	    				 				var currentTaskId = (this.targetValue.customInfo || {}).currentTaskId || taskId;
	    				 				var currentProcInstId = (this.targetValue.customInfo || {}).currentProcInstId;
	    				 				console.log(currentTaskId);
	    				 				if (currentTaskId) {
	        				 				ajaxGet(router("/").api("workflow").checkTask(currentTaskId, currentProcInstId), {}, function(data) {
	        				 					_this.targetValue.hasTask = !!data.hasTask;
	        				 					_this.currentTaskId = data.currentTaskId || currentTaskId;
	        				                });
	    				 				}
	    				 				return false;
	    				 			} else {
	    				 				return hasTask;
	    				 			}
	    				 		},
	    				 		canStartProcess: function() {
	    				 			var _this = this;
	    				 			var taskId = this.currentTaskId;
	    				 			var modelMethods = router(urlNamespace).methods(model);
	    				 			var canStart = false;
	    				 			if (modelMethods && typeof modelMethods.canStartProcess == 'function') {
	    				 				canStart = modelMethods.canStartProcess.call(_this, this.targetValue) || false;
	    				 			}
	    				 			return canStart;
	    				 		}
	    				 	}/* ,
	    				 	mounted: function(e) {
	    				 		var _this = this;
	    				 		var currentTaskId = (this.targetValue.customInfo || {}).currentTaskId;
				 				console.log(currentTaskId);
				 				if (currentTaskId) {
    				 				ajaxGet(router("/").api("workflow").checkTask(currentTaskId), {}, function(data) {
    				 					_this.targetValue.hasTask = !!data.hasTask;
    				 					_this._data.targetValue = _this.targetValue;
    				                });
				 				}
				 				return false;
	    				 	} */
    				 	}
					));
					// 获取表单验证要求
					varFields = vm.$refs["formInputs"].fieldValidators;
					
					form = $("#" + formId).form();
					//form.initFormData(data.targetValue);
					var $container = $("#" + formId);
		    		$("#" + formId).bootstrapValidator({
		                message: '请输入有效值',
		                feedbackIcons:sys.common.feedbackIcons,
		                submitHandler: function(validator, form2, submitButton){
		                	var btnType = $(submitButton).data("btn-type");
		                	var confirmText = btnType == "save" ? "保存" : $(submitButton).text();
		                	modals.confirm({text:'确认' + confirmText + '？', 
		                		callback: function () {
			                		var index3 = layer.load(1);
			                		var formData = form.getFormSimpleData();
			                		var url = btnType == 'submit' ? router(urlNamespace).api(model).submit() : (id == 0 ? router(urlNamespace).api(model).create() : router(urlNamespace).api(model).update(id));
			                		ajaxPost(url, formData, function(data,status){
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
		    		
                	// 初始化完成回调函数
		        	if (router(urlNamespace).callback(model).detail) {
		        		var vueCallback = (router(urlNamespace).callback(model).detail || {}).vueCallback;
		        		if (typeof vueCallback == 'function') {
		        			vueCallback.call(vm, data, $container);
		        		}
		        	}
                	
		        	vm.isShow = true;
    			 }
    		})
    		
    		function handleData() {
    			
    		}
    		
    		function handleResult(results){
    			var _this = this;
    			var isCreate = id == 0;
    			var targetName = results.targetName || model;
    			var targetValue = results[targetName] || {};
    			keyword = keyword || window.keyword || "id";
    			id = targetValue[keyword] || targetValue.id || 0;
        		if (isCreate) {
        			if (isModals) {
        				var currentWinId = winId;
        				if (!$("#" + currentWinId).length) {
        					currentWinId = $(this).parents(".modal.in:first").attr("id");
        				}
        				// 回调函数
        				if (router(urlNamespace).callback(model).detail) {
			        		var cbFunc = (router(urlNamespace).callback(model).detail || {}).modalCreateCallback;
			        		if (typeof cbFunc == 'function') {
			        			var url = pm.router.html(model).detail(id, true);
			        			cbFunc.call(this, {winId: currentWinId, url});
			        		}
			        	}
        				
        				var shouldHide = true;
			        	if (router(urlNamespace).callback(model).detail) {
			        		var cbFunc = (router(urlNamespace).callback(model).detail || {}).shouldHideWin;
			        		if (typeof cbFunc == 'function') {
			        			shouldHide = cbFunc.call(vm);
			        		}
			        	}
			        	if (shouldHide) {
	        				modals.hideWin(currentWinId);
			        	}
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
	   						vm._data.currentTaskId = "";
	   						//form.initFormData(data.targetValue);
	   						
		                	// 回调函数
				        	if (router(urlNamespace).callback(model).detail) {
				        		var vueCallback = (router(urlNamespace).callback(model).detail || {}).vueCallback;
				        		if (typeof vueCallback == 'function') {
				        			vueCallback.call(vm, data, $("#" + appId));
				        		}
				        	}
		                	
				        	// 是否有临时的表单提交回调函数
			        		if ($(_this).data("submitCallback")) {
			        			var submitCallback = $(_this).data("submitCallback");
			        			$(_this).data("submitCallback", null);
			        			submitCallback.call();
			        		}
	    				}
	        		});
        		}
    		}
    		
    		$(document).off('click', "#" + appId +' [data-btn-type]');
    		$(document).on("click", "#" + appId +' [data-btn-type]', function(e) {
    			var action = $(this).attr('data-btn-type');
                switch (action) {
               	case 'completeTask':
               		sys.common.toDoWorkflowTask.call(vm, this, vm.currentTaskId, true);
               		break;
                case 'startProcess':
                	var modelMethods = router(urlNamespace).methods(model);
                	var callback = null;/* function() {
                		handleResult({targetName: vm.targetName, targetValue: vm.targetValue});
                	}; */
                	if (modelMethods && typeof modelMethods.startProcess == "function") {
                		modelMethods.startProcess.call(vm, this, vm.targetValue, callback);
                	} else {
	                	sys.common.startProcess.call(vm, this, vm.targetValue, callback);
                	}
                	break;
                };
    		});
		});
	</script>
</jsTag>
</html>