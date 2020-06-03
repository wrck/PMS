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
</cssTag>
</head>
<body>
	<div id="app">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<!-- <h1 id="pageTitle">
				<template v-if="isShow">
					<span>{{targetValue.projectCode}}</span><small>{{targetValue.projectName}}</small>
				</template>
				<div v-else>
					<span>　</span><small>　</small>
				</div>
			</h1> -->
			<h1 id="pageTitle" class="fade" :class="{in: isShow}">
				<span>{{targetValue.projectCode}}</span><small>{{targetValue.projectName}}</small>
			</h1>
			<span class="display-none"></span>
			<ol class="breadcrumb">
			</ol>
		</section>
		<section class="content">
			<div class="row">
				<div class="col-xs-12">
					<div class="box box-info">
						<form id="commonForm" method="post" :action="formAction" name="commonForm" class="form-inline fade" :class="{in: isShow}">
							<div class="box-body row ml-0">
								<%-- <%@include file="../template/vue-form-component.jsp" %> --%>
								<form-inputs :form-cols="formCols" :field-list="fieldList" :target-name="targetName" :target-value="targetValue" :is-created="isCreate" :permission-type="permissionType" :permissions="permissions" :roles="roles" :model="model"></form-inputs>
							</div>
							<!-- /.box-body -->
							<div class="box-footer text-right">
								<div id="projectStatusDiv" class="pull-left pt-05">
									<div id="projectStatus" class="display-inline-block control-label label" :class="projectStateLabel">
										<div v-if="projectState.show" style="line-height: 1.45;">
											<span style="line">项目状态：</span>
											<span>{{projectState.name || "未知"}}</span>
										</div>
										<!-- <select id="projectState" type="search" name="projectState" data-selected="100" placeholder="项目状态" data-flag="urlSelector" data-src="/api/basicDataByType.json?basicDataTypeCode=afss_projectState" data-autoload="true" data-text="basicDataName" data-value="basicDataId" data-select2-config="{&quot;placeholder&quot;:&quot;--请选择--&quot;,&quot;tags&quot;:false,&quot;allowClear&quot;:false,&quot;dropdownAutoWidth&quot;:true}" class="label-primary no-border" style="
    height: 1.5em;
"> -->
										<!-- <select :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
												:value="getFieldValue(field)" :data-selected="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
												:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required"
												:data-flag="field.type" :data-src="(parseValue(field, 'extData') || {}).src || field.extData" :data-autoload="field.extData['autoload']"
												:data-src-data="field.extData['src-data']" :data-text="field.extKey" :data-value="field.extValue" :data-store-source="field.extData['store-source']"
												:data-blank="field.extData.blank || false" :data-blank-value="field.extData['blank-value']" :data-blank-text="field.extData['blank-text']"
												:data-select2-config="JSON.stringify(field.extData['select2-config'])"
												>
										</select> -->
										<form-input ref="projectState" :field="projectStateField" :form-cols="formCols" :is-created="isCreate" :data-type="dataType" :target-value="targetValue" :permission-type="permissionType" :permissions="permissions" :roles="roles" :model="model"></form-input>
									</div>
									<!-- <div id="projectProgressDiv" class="display-inline-block control-label label" :class="projectStateLabel">
										<form-input ref="projectProgress" :field="projectProgressField" :form-cols="formCols" :is-created="isCreate" :data-type="dataType" :target-value="targetValue" :permission-type="permissionType" :permissions="permissions" :roles="roles" :model="model"></form-input>
									</div> -->
								</div>
								<button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">{{isModals ? "取消" : "返回"}}</button>
								<button type="submit" class="btn btn-primary" v-if="permissionType && permissionType != 'view'" data-btn-type="save">保存</button>
							</div>
							<!-- /.box-footer -->
						</form>
					</div>
					<div id="tabDiv" class="fade" :class="{in: isShow}">
						<%-- <%@include file="../template/vue-tab-component.jsp" %> --%>
						<nav-tab :tab-list="tabList" :target-name="targetName" :target-value="targetValue" :permission-type="permissionType" :permissions="permissions" :roles="roles" :model="model"></nav-tab>
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
	<script src="${pageContext.request.contextPath}/static/plugins/select2/select2.full.min.js"></script>
	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/initComm.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/router.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/tab-init.js"></script>
	<script src="${pageContext.request.contextPath}/static/vue/vue.min.js"></script>
</c:if>
	<script src="${pageContext.request.contextPath}/static/pm/js/vue-form-input-component.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/vue-form-component.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/vue-tab-pane-component.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/vue-tab-component.js"></script>
	<script>
	    //tableId,queryId,conditionContainer
	    $(function () {
		    var form = null;
	        var keyword = "projectId";
		    var id = "${id}" || 0;
		    var urlNamespace = "/pm/";
		    var model = "project";
	   		var appId = model + "App";
	        var winId= model + "Win";
	        var formId = model + "Form";
		    var userId = "<shiro:principal property='userId'></shiro:principal>";
	   		var sysData =[],inputData=[],varFields={};
	   		var vm;
	   		var isModals = '${isModals}' == 'true';
	   		var search = '${pageContext.request.queryString}' || location.search;
	    	$("#commonForm").attr({id:formId, name: formId});
	    	$("#app").attr({id: appId});
	    	$("#tabDiv").attr({id: model + "TabDiv"});
	    	var $container = $("#" + formId);
	    	var url = id == 0 ? pm.router.api(model).create(search) : pm.router.api(model).detail(id);
    		ajaxGet(url, null, function(data, status){
    			console.log(isModals);
				if (status == 'success') {
					vm = new Vue($.extend(true, {
							components: {
								'form-input': FormInput,
							    'form-inputs': FormInputs,
							    'nav-tab': NavTab,
							    'tab-pane': TabPane,
							},
						}, /* formVueConfig || {}, tabVueConfig || {}, */ {
							el: "#" + appId,
							data: $.extend({}, data, {
								isModals,
								isCreate: id != 0,
								isShow: true,
								dataType: "form",
								formCols: 4,
								// formGroupClass: "col-sm-6 col-md-3",
								// formGroupTextareaClass: "col-sm-12 col-md-6",
								formAction: pm.router.api(model).detail(id),
	   							fieldList: data.fieldList || [],
	   							tabList: data.tabList || [],
	   							targetName: data.targetName,
	    						targetValue: data.targetValue,
	    						
	    						// 项目状态
	    						projectStateField: {},
	    						projectState: {
	    							state: data.targetValue.projectState,
	    							name: data.targetValue.projectStateName,
	    							show: false
	    						},
	    						
	    						// 项目进度
	    						/* projectProgressField: {},
	    						projectProgress: {
	    							progress: (data.targetValue.customInfo || {}).projectProgress || 0,
	    							show: false
	    						}, */
	    						
	    						// 权限控制参数
	    						model: data.model || model,
	    						permissionType: data.permissionType || "",
	    						permissions: data.permissions || [],
	    						roles: data.roles || []
	    				 	}),
	    				 	created: function() {
	    				 		/* var fieldList = this.fieldList || [];
	    						for ( var i in fieldList) {
	    							var field = fieldList[i];
	    							if (field.field == 'projectState') {
	    								field.name += "：";
	    								field.cssClass = "({groupClass:'m-0', selfClass:'no-border " + this.projectStateLabel + "'})";
	    								this.projectStateField = field;
	    								fieldList.splice(i, 1);
	    							} else if (field.field == 'customInfo.projectProgress') {
	    								field.name += "：";
	    								field.cssClass = "({groupClass:'m-0', selfClass:'no-border " + this.projectStateLabel + "'})";
	    								this.projectProgressField = field;
	    								fieldList.splice(i, 1);
	    							}
	    						} */
	    				 		this.initFieldList(this.fieldList);
	    				 	},
	    				 	mounted: function() {
	    				 		var $projectState = this.$refs.projectState;
	    				 		if ($projectState) {
	    				 			var $el = $projectState.$el;
	    				 			$(".form-control",  $el).removeClass("form-control");
	    				 			$("label.control-label",  $el).addClass("m-0");
		    				 		this.projectState.show = !$projectState.isPermit;
	    				 		} else {
	    				 			this.projectState.show = true;
	    				 		}
	    				 		/* var $projectProgress = this.$refs.projectProgress;
	    				 		if ($projectProgress) {
	    				 			var $el = $projectProgress.$el;
	    				 			$(".form-control",  $el).removeClass("form-control");
	    				 			$("label.control-label",  $el).addClass("m-0");
		    				 		this.projectProgress.show = !$projectProgress.isPermit;
	    				 		} else {
	    				 			this.projectProgress.show = true;
	    				 		} */
	    				 	},
	    				 	computed: {
	    				 		/* projectStateShow: function() {
	    				 			return !(this.$refs.projectState || {}).isPermit;
	    				 		}, */
	    				 		projectStateLabel: function() {
	    				 			var labelStyle = {
    				 					"": "label-danger",
	    				 				"<10": "label-danger",
	    				 				"=10": "label-warning",
	    				 				"<50": "label-primary",
	    				 				"=50": "label-info",
	    				 				"<=100": "label-success"
	    				 			}
	    				 			var projectState = this.projectState.state || "0";
	    				 			var key = "";
	    				 			if (projectState < "10") {
	    				 				key = "<10";
	    				 			} else if (projectState == "10") {
	    				 				key = "=10";
	    				 			} else if (projectState < "50") {
	    				 				key = "<50";
	    				 			} else if (projectState = "50") {
	    				 				key = "=50";
	    				 			} else if (projectState <= "100") {
	    				 				key = "<=010";
	    				 			}
	    				 			return labelStyle[key];
	    				 		}
	    				 	},
	    				 	methods: {
	    				 		initFieldList: function(fieldList) {
	    				 			//var fieldList = this.fieldList || [];
	    				 			fieldList = fieldList || this.fieldList || [];
		    						for ( var i in fieldList) {
		    							var field = fieldList[i];
		    							if (field.field == 'projectState') {
		    								field.name += "：";
		    								field.cssClass = "({groupClass:'m-0', selfClass:'no-border " + this.projectStateLabel + "'})";
		    								this.projectStateField = field;
		    								fieldList.splice(i, 1);
		    								break;
		    							}/*  else if (field.field == 'customInfo.projectProgress') {
		    								field.name += "：";
		    								field.cssClass = "({groupClass:'m-0', selfClass:'no-border " + this.projectStateLabel + "'})";
		    								this.projectProgressField = field;
		    								fieldList.splice(i, 1);
		    							} */
		    						}
		    						return fieldList;
	    				 		}
	    				 	}
    				 	}
					));
					window.projectVm = vm;
					
					form = $("#" + formId).form();
					form.initFormData(data.targetValue);
					var $container = $("#" + formId);
		    		$("#" + formId).bootstrapValidator({
		                message: '请输入有效值',
		                feedbackIcons:sys.common.feedbackIcons,
		                submitHandler: function(validator, form2, submitButton){
		                	var title = id != 0 ? '保存' : '创建';
		                	modals.confirm({text:'确认' + title + '项目？', 
		                		callback: function () {
			                		var index3 = layer.load(1);
			                		var formData = form.getFormSimpleData();
			                		var url = id == 0 ? pm.router.api(model).create() : pm.router.api(model).update(id);
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
    			var targetName = results.targetName || "projectVO";
    			var targetValue = results[targetName] || {};
    			keyword = typeof keyword == 'undefined' ? "id" : keyword ||  "id";
    			id = targetValue[keyword] || targetValue.id || 0;
        		if (isCreate) {
	        		window.location.replace(pm.router.html(model).detail(id));
        		} else {
        			ajaxGet(pm.router.api(model).detail(id), null, function(data, status){
	    				if (status == 'success') {
	    					vm._data.fieldList = vm.initFieldList(data.fieldList) || [];
	    					vm._data.tabList = data.tabList || [];
	   						vm._data.targetValue = data.targetValue;
	   						// form.initFormData(data.targetValue);
	   						
	   						refreshProjectStateCallback(data.targetValue);
	    				}
	        		});
        		}
    		}
    		
		});
	    
	    function uploadDeliverFile(target) {
			var row = null, $target = $(target);
	    	try {
	    		row = JSON.parse($target.data("row"));
	    	} catch(e) {
		    	try {
		    		row = row || eval("(" + $target.data("row") + ")");
	    		} catch(e2) {
	    			console.error(e, e2);
	    		}
    		}
    		row = row || {};
	    	uploadDeliverFileWin = window.uploadDeliverFileWin || "uploadDeliverFileWin";
	    	modals.openWin({
	    		title: "上传交付件",
	    		winId: uploadDeliverFileWin,
	    		url: basePath + '/pm/project/task/modals/upload.html?' + $.param(row),
	    		hideFunc: function(e) {
	    			var config = $target.parents(".tab-pane:first").data();
	    			initTabData(config, true);
	    			if (window.refreshProjectState && window.projectVm) {
	    				refreshProjectStateCallback(row);
	    			}
	    		}
	    	})
	    }
	    
	    function refreshProjectStateCallback(params) {
	    	ajaxGet(basePath + '/pm/project/' + params.projectId + "/state.json", {}, function(data) {
	    		var projectState = window.projectVm._data.projectState || {};
	    		if (projectState.show) {
					projectState.state = data.projectState;
					projectState.name = data.projectStateName;
	    		} else {
	    			window.projectVm._data.targetValue.projectState = data.projectState;
	    		}
				
				window.refreshProjectState = null;
			});
	    }
	</script>
</jsTag>
</html>