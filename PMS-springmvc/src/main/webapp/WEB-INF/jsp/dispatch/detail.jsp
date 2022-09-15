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
			<!-- <h1 id="pageTitle">
				<template v-if="isShow">
					<span>{{targetValue.projectCode}}</span><small>{{targetValue.projectName}}</small>
				</template>
				<template v-else>
					<span></span><small></small>
				</template>
			</h1>
			<span class="display-none"></span> -->
			<span></span>
			<ol class="breadcrumb">
			</ol>
		</section>
		<section class="content">
			<div class="row">
				<div class="col-xs-12">
					<div class="box box-info">
						<form id="commonForm" method="post" :action="formAction" name="commonForm" class="form-inline fade" :class="{in: isShow}">
							<div id="formDiv" class="box-body row ml-0">
								<%-- <%@include file="../template/vue-form-component.jsp" %> --%>
								<form-inputs ref="formInputs" :form-cols="formCols" :field-list="fieldList" :target-name="targetName" :target-value="targetValue" :is-create="isCreate" :permission-type="permissionType" :permissions="permissions" :roles="roles" :model="model"></form-inputs>
							</div>
							<!-- /.box-body -->
							<div class="box-footer text-right">
								<button type="button" class="btn btn-info pull-left" v-if="targetValue.dispatched && targetValue.type == 'frameworkAgreement'" data-btn-type="exportDispatchInfo">外派单</button>
								
								<button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">{{isModals ? "取消" : "返回"}}</button>
								<button type="submit" v-if="targetValue.id > 0 && !targetValue.dispatched" class="btn btn-success" data-btn-type="submit">派单</button>
								<button type="button" v-if="targetValue.dispatched && !targetValue.settled" class="btn btn-primary" data-btn-type="settle">结算</button>
								<button type="submit" class="btn btn-primary" data-btn-type="save">保存</button>
							</div>
							<!-- /.box-footer -->
						</form>
					</div>
					<div id="tabDiv" class="fade" :class="{in: isShow}">
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
	<script src="${pageContext.request.contextPath}/static/plugins/select2/select2.full.min.js"></script>
	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/initComm.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/router.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/tab-init.js"></script>
	<script src="${pageContext.request.contextPath}/static/vue/vue.min.js"></script>
</c:if>
	<script src="${pageContext.request.contextPath}/static/plugins/autocomplete/jquery.autocomplete.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/vue-form-input-component.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/vue-form-component.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/vue-tab-pane-component.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/vue-tab-component.js"></script>
	<script>
	    $(function () {
		    //tableId,queryId,conditionContainer
		    var form = null;
	        var commonTable;
	        var urlNamespace = "/pm/";
	        var model = "dispatch";
	        var appId = model + "App";
	        var winId= model + "Win";
	        var formId = model + "Form";
	        var keyword = "id";
		    var id = "${id}" || 0;
		    var userId = "<shiro:principal property='userId'></shiro:principal>";
	   		var sysData =[],inputData=[],varFields={};
	   		var isModals = '${isModals}' == 'true';
	   		var search = '${pageContext.request.queryString}' || location.search;
	   		var vm;
	   		var $container = $("#" + formId);
	    	$("#commonForm").attr({id:formId, name: formId});
	    	$("#app").attr({id: appId});
	    	$("#tabDiv").attr({id: model + "TabDiv"});
	    	var url = id == 0 ? pm.router.api(model).create(search) : pm.router.api(model).detail(id);
    		ajaxGet(url, null, function(data, status){
    			console.log(isModals);
				if (status == 'success') {
					vm = new Vue($.extend(true, {
							components: {
							    'form-inputs': FormInputs,
							    'nav-tab': NavTab,
							    'tab-pane': TabPane,
							},
						}, /* formVueConfig || {}, tabVueConfig || {}, */ {
							el: "#" + appId,
							data: $.extend({}, data, {
								isModals: isModals,
								isCreate: id == 0,
								isShow: true,
								dataType: "form",
								formCols: 2,
								//formGroupClass: "col-xs-12 col-sm-12 col-md-6",
								//formGroupTextareaClass: "col-sm-12 col-md-6",
								formAction: pm.router.api(model).detail(id),
	   							fieldList: data.fieldList || [],
	   							tabList: data.tabList || [],
	   							targetName: data.targetName || model,
	    						targetValue: data.targetValue || {customInfo: {}},
	    						
	    						// 权限控制参数
	    						model: data.model || model,
	    						permissionType: data.permissionType || "",
	    						permissions: data.permissions || [],
	    						roles: data.roles || []
	    				 	}),
    				 	}
					));
					// 获取表单验证要求
					varFields = vm.$refs["formInputs"].fieldValidators;
					
					form = $("#" + formId).form();
					form.initFormData(data.targetValue);
					$container = $("#" + formId);
		    		$("#" + formId).bootstrapValidator({
		                message: '请输入有效值',
		                feedbackIcons:sys.common.feedbackIcons,
		                submitHandler: function(validator, form2, submitButton){
		                	var btnType = $(submitButton).data("btn-type");
		                	var confirmText = btnType == "save" ? "保存" : "派单";
		                	confirmText = $(submitButton).text() || confirmText;
		                	modals.confirm({text:'确认' + confirmText + '？', 
		                		callback: function () {
		                			formSubmit.call(vm, validator, form2, submitButton);
			                		/* var index3 = layer.load(1);
			                		var formData = form.getFormSimpleData();
			                		var url = btnType == 'submit' ? pm.router.api(model).submit(id) : (id == 0 ? pm.router.api(model).create() : pm.router.api(model).update(id));
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
			    					}) */
		                		}, 
		                		cancel_call: function() {
		                			$("[type='submit']", form2).removeAttr("disabled");
		                		}
		                	});
		                }, 
		    			fields : varFields
		    		});
		    		$container.data("formSubmit", formSubmit);
		    		
		    		// 服务商Select2初始化完成之后，添加change事件，避免直接添加change事件，无法获取原始保存的服务商信息
		    		var selectedId = (data.targetValue || {}).projectIds;
		    		var selectedText = (data.targetValue || {}).dispatchName;
		    		var project = ((data.targetValue || {}).customInfo || {}).project || {};
		    		$("#projectIds", $container).select2({
		    			allowClear: true,
		    			dropdownAutoWidth: true,
		    			data: selectedId ? [$.extend(true, {}, project, {id: selectedId, text: selectedText})] : [],// 设置初始值
		    			ajax: {
		    			    url: basePath + "/pm/project/list.json",
		    			    dataType: 'json',
		    			    delay: 250,
		    			    data: function (params) {
		    			      return {
		    			        fuzzy: params.term, // search term
		    			        fuzzySearch: true,
		    			        pageSize: 30,
		    			        start: (params.page - 1) * 30 || 0
		    			      };
		    			    },
		    			    processResults: function (data, params) {
		    			      	params.page = params.page || 1;
							  	var list = data.data || [];
							  	var results = $.map(list, function (obj) {
							  		obj.id = obj.id || obj.projectId;
							  		obj.text = obj.projectName;
								  	return obj;
								});
		    			      	return {
		    			        	results: results,
		    			        	pagination: {
		    			          		more: (params.page * 30) < data.pageParam.filtered
		    			       		}
		    			      	};
		    			    },
		    			    cache: true
		    			  },
		    			  placeholder: '搜索项目名称',
		    			  minimumInputLength: 4,
		    			  templateResult: formatRepo,
		    			  templateSelection: formatRepoSelection
		    		});
		    		/* if (projectIdsPlaceholder) {
		    			$(".select2-selection__placeholder").css("color", 'inherit');
		    		} */
		    		
		    		// 项目名称初始化完成之后，添加change事件，避免直接添加change事件，无法获取原始保存的信息
		    		$("#projectIds", $container).siblings(".select2-container").one("click", function(e) {
		    			$("#projectIds", $container).on("change", function(e){
		    				try{
		    					var source = $(this).select2("data");
		    					console.log(source);
		    					if (source.length > 0) {
		    						source = source[0];
		    					} else {
		    						source = {};
		    					}
		    					var targetValue = vm._data.targetValue || {};
		    					var customInfo = targetValue.customInfo || {};
		    					targetValue.projectName = source.projectName;
		    					targetValue.dispatchName = source.projectName;
		    					targetValue.smsProjectCode = source.smsProjectCode || (source.customInfo || {}).smsProjectCode;
		    					targetValue.smsSubmitTime = source.smsSubmitTime || (source.customInfo || {}).smsSubmitTime;
		    					targetValue.smsProjectAmount = source.smsProjectAmount || (source.customInfo || {}).smsProjectAmount;
		    					targetValue.smsAfProjectAmount = source.smsAfProjectAmount || (source.customInfo || {}).smsAfProjectAmount;
		    					
		    					targetValue.customInfo = customInfo;
				    			targetValue.customInfo.project = source;
				    			targetValue.customInfo.projectIds = source.projectId;
				    			targetValue.customInfo.project.projectId = source.projectId;
				    			vm._data.targetValue = targetValue;
				    			
		    					/* $("#projectName", $container).val(source.projectName);
		    					$("#dispatchName", $container).val(source.projectName);
			    				$("#smsProjectCode", $container).val(source.smsProjectCode || (source.customInfo || {}).smsProjectCode);
				    			$("#smsSubmitTime", $container).val(source.smsSubmitTime || (source.customInfo || {}).smsSubmitTime);
				    			$("#smsProjectAmount", $container).val(source.smsProjectAmount || (source.customInfo || {}).smsProjectAmount);
				    			$("#smsAfProjectAmount", $container).val(source.smsAfProjectAmount || (source.customInfo || {}).smsAfProjectAmount); */
			    			} catch(e){}
		    			});
		    		});
		    		// 检查是否已派单
		    		checkDispatched(data);
		    		
		    		// 服务商Select2初始化完成之后，添加change事件，避免直接添加change事件，无法获取原始保存的服务商信息
/* 		    		if ((data.targetValue || {}).facilitatorId && !isDispatched) {
			    		$("#facilitatorId", $container).one("change", changeFacilitator);
		    		}
 */		    		
					/* $($container).one("click", "#facilitatorId~.select2-container", function(e) {
	 					$("#facilitatorId", $container).on("change", changeFacilitator)
		    		}); */
 					/* $("#facilitatorId", $container).siblings(".select2-container").one("click", function(e) {
	 					$("#facilitatorId", $container).on("change", changeFacilitator)
		    		}); */
		    		
    			 }
    		})
    		
    		$(document).off('click', "#" + appId +' [data-btn-type]');
    		$(document).on("click", "#" + appId +' [data-btn-type]', function(e) {
    			var action = $(this).attr('data-btn-type');
                switch (action) {
               	case 'settle':
               		var url = pm.router.html('settlement').create("dispatchId=" + id, true);
               		console.log(1);
                	if(id && url) {
                		modals.openWin({
	                         winId: "settlementWin",
	                         title:'新增记录',
	                         width: '75vw',
	                         url: url,
	                         hideFunc: function() {
	                        	 var config = $("#settlementTab").data();
	                        	 initTabData(config, true);
	                         }
                        });
                	}
                	break;
               	case 'exportDispatchInfo': 
                   	var $btn = $(this);
               		$container.data("submitCallback", function() {
               			var url = router(urlNamespace).html(model).exportDispatchInfo(id);
                        $btn.button("loading");
                        router.postDownload(url);
                        setTimeout(function() {
                       	 	$btn.button("reset");
                        }, 2000);
					});
               		var $submitButton = $container.find("[data-btn-type='save']");
					if (typeof formSubmit == "function") {
						var $bootstrapValidator = $container.data('bootstrapValidator');
	               		$submitButton = $bootstrapValidator.$submitButton || $submitButton;
						$bootstrapValidator.$submitButton = null;
						formSubmit.call(vm, $bootstrapValidator, $container, $submitButton, true);
					} else {
						$submitButton.click();
//						$container.submit();
					}
               	}
    		});
    		
    		// 检查是否已派单
    		function checkDispatched(data) {
    			// 如果已经派单则不允许修改项目和服务商
	    		var isDispatched = (data.targetValue || {}).dispatched;
	    		var dispatchType = (data.targetValue || {}).type;
	    		if (isDispatched == true) {
	    			$("#projectIds", $container).attr("disabled", true);
	    			$("#facilitatorId", $container).attr("disabled", true);
	    			$("#dispatchSeq", $container).attr("disabled", true);
	    			if (dispatchType == 'frameworkAgreement') {
		    			$("#dispatchNo", $container).attr("disabled", true);
	    			} else {
	    				$("#dispatchNo", $container).attr("disabled", false);
	    			}
	    		}
	    		
	    		$($container).off("change", "#facilitatorId", changeFacilitator);
	    		if ((data.targetValue || {}).facilitatorId && !isDispatched) {
		    		//$($container).one("change", "#facilitatorId", changeFacilitator);
		    		$($container).on("change", "#facilitatorId", changeFacilitator);
	    		} else {
	    			$($container).one("click", "#facilitatorId~.select2-container", function(e) {
	 					$("#facilitatorId", $container).on("change", changeFacilitator)
		    		});
	    		}
    		}
    		
    		function formSubmit(validator, form2, submitButton, hideInfo) {
    			var isValid = validator.isValid() || validator.validate().isValid();
    			/* needValid = needValid == true ? true : false;
    			var isValid = !needValid; 
    			if (needValid) {
	    			var $bootstrapValidator = $(form2).data('bootstrapValidator');
					isValid = $bootstrapValidator.validate().isValid();
    			}*/
				if (isValid) {
	    			var index3 = layer.load(1);
	    			var baseForm = $(form2).data("baseForm");
	    			var formData = null;
	    			if (baseForm) {
	    				formData = baseForm.getFormSimpleData();
	    			} else {
	    				formData = $(form2).serializeArray();
	    			}
	    			var btnType = $(submitButton, form2).data("btn-type");
                	var confirmText = btnType == "save" ? "保存" : $(submitButton, form2).text();
            		var url = btnType == 'submit' ? pm.router.api(model).submit(id) : (id == 0 ? pm.router.api(model).create() : pm.router.api(model).update(id));
            		ajaxPost(url, formData,function(data,status){
            			if(data.status){
            				if (!hideInfo) {
	    						modals.correct(confirmText + "成功");
	        				}
            				handleResult.call(form2, data);
    					} else{
    						modals.error('操作失败！<br>' + (data.message || ""));
    					}
            		},null,null,function(){
						layer.close(index3);
						$("[type='submit']", form2).removeAttr("disabled");
					})
				}
    		}
    		
    		function handleData() {
    			
    		}
    		
    		function handleResult(results){
    			var _this = this;
    			var isCreate = id == 0;
    			var targetName = results.targetName || model;
    			var targetValue = results[targetName] || {};
    			keyword = window.keyword || "id";
    			id = targetValue[keyword] || targetValue.id || 0;
        		if (isCreate) {
	        		if (isModals) {
	        			console.log(1);
	        			modals.removeData(winId);
        				$("#" + winId).modal({ 
							remote: pm.router.html(model).detail(id, true)
						});
        				
        				// 回调函数
        				if (router(urlNamespace).callback(model).detail) {
			        		var cbFunc = (router(urlNamespace).callback(model).detail || {}).modalCreateCallback;
			        		if (typeof cbFunc == 'function') {
			        			var url = pm.router.html(model).detail(id, true);
			        			cbFunc.call(this, {winId: currentWinId, url: url});
			        		}
			        	}
        				
        				var shouldHide = false;
			        	if (router(urlNamespace).callback(model).detail) {
			        		var cbFunc = (router(urlNamespace).callback(model).detail || {}).shouldHideWin;
			        		if (typeof cbFunc == 'function') {
			        			shouldHide = cbFunc.call(vm);
			        		}
			        	}
			        	if (shouldHide) {
	        				modals.hideWin(currentWinId);
			        	}
        				//modals.hideWin(winId);
        				//modals.closeWin(winId);
        			} else {
		        		window.location.replace(pm.router.html(model).detail(id));
        			}
        		} else {
        			ajaxGet(pm.router.api(model).detail(id), null, function(data, status){
	    				if (status == 'success') {
	    					vm._data.fieldList = data.fieldList || [];
	    					vm._data.tabList = data.tabList || [];
	   						vm._data.targetValue = data.targetValue;
	   						// 检查是否已派单
	   			    		checkDispatched(data);
	   						
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
    		function formatRepo (repo) {
    			if (repo.loading) {
    				return repo.text;
    			}

    			var $container = $(
    				"<div class='select2-result-repository clearfix'>" +
    			      "<div class='select2-result-repository__meta'>" +
    			        "<div class='select2-result-repository__title'></div>" +
    			        "<div class='select2-result-repository__description'></div>" +
    			        "<div class='select2-result-repository__statistics'>" +
    			          "<div class='select2-result-repository__smsSubmitTime'></div>" +
    			          "<div class='select2-result-repository__smsProjectAmount'></div>" +
    			        "</div>" +
    			      "</div>" +
    			    "</div>"
    			);

    			$container.find(".select2-result-repository__title").append("<div>" + repo.projectCode + "</div>");
    			$container.find(".select2-result-repository__title").append("<div>" + (repo.contractNo || "") + "</div>");
    			$container.find(".select2-result-repository__description").text(repo.projectName);
    			$container.find(".select2-result-repository__forks").append(repo.contractNo);
    			$container.find(".select2-result-repository__smsSubmitTime").append((repo.customInfo || {}).smsSubmitTime);
    			$container.find(".select2-result-repository__smsProjectAmount").append((repo.customInfo || {}).smsProjectAmount);

    			return $container;
    		}

    		function formatRepoSelection (repo) {
    			return repo.projectName || repo.text;
    		}
    		
    		function changeFacilitator(e) {
    			console.log("changeFacilitator");
    			var $this = $(this);
				var source = $this.select2('data');
    			if (source.length > 0) {
    				source = source[0]; 
   				} else {
   					source = {}; 
 				}
    			var element = source.element;
				var source = $(element).data("source") || source.source || source || {};
				$("#facilitatorCode", $container).val(source.code);
    			$("#facilitatorName", $container).val(source.name);
    			$("#bankInfo", $container).val(source.bankInfo);
    			$("#bankAccount", $container).val(source.bankAccount);
    			
   				var render = function(data) {
    				var placeholder = $("#dispatchSeq", $container).data("placeholder") || $("#dispatchSeq", $container).attr("placeholder");
    				var newPlaceholder = data.dispatchSeq || placeholder;
    				$("#dispatchSeq", $container).data("placeholder", placeholder);
    				$("#dispatchSeq", $container).attr("placeholder", newPlaceholder);
    				var dispatchSeq = $("#dispatchSeq", $container).val();
    				if (dispatchSeq != newPlaceholder) {
    					$("#dispatchSeq", $container).val("");
    				} else {
	    				//$("#dispatchSeq", $container).val(newPlaceholder == dispatchSeq ? dispatchSeq : "");
    				}
    				
    				var dispatchNoPlaceholder = $("#dispatchNo", $container).data("placeholder") || $("#dispatchNo", $container).attr("placeholder");
    				var newdispatchNoPlaceholder = data.dispatchNo || dispatchNoPlaceholder;
    				$("#dispatchNo", $container).data("placeholder", dispatchNoPlaceholder);
    				$("#dispatchNo", $container).attr("placeholder", data.dispatchNo || dispatchNoPlaceholder);
    				var dispatchNo = $("#dispatchNo", $container).val();
    				if (dispatchNo != newdispatchNoPlaceholder) {
    					$("#dispatchNo", $container).val("");
    				} else {
	    				//$("#dispatchNo", $container).val(newdispatchNoPlaceholder == dispatchNo ? dispatchNo : "");
    				}
   				};
    			if (source.code) {
    				ajaxGet(pm.router.api(model).generateDispatchSeq(), {facilitatorCode: source.code}, render);
    			} else if (source.selected) {// 选择空项时
    				render({});
    			} else {// 初始化时
    				render({
    					dispatchSeq: $("#dispatchSeq", $container).val() || "",
    					dispatchNo: $("#dispatchNo", $container).val() || "",
   					});
    			}
    		}
		});
	</script>
</jsTag>
</html>