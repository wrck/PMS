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
				<li><a href="#"><i class="fa fa-dashboard"></i> 首页</a></li>
				<li><a href="#">系统管理</a></li>
				<li class="active">用户管理</li>
			</ol>
		</section>
		<section class="content">
			<div class="row">
				<div class="col-xs-12">
					<div class="box box-info">
						<form id="commonForm" method="post" :action="formAction" name="commonForm" class="form-inline">
							<div id="formDiv" class="box-body row ml-0" v-if="isShow">
								<%@include file="../template/vue-form-component.jsp" %>
							</div>
							<!-- /.box-body -->
							<div class="box-footer text-right">
								<button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">取消</button>
								<button type="submit" v-if="!targetValue.dispatched" class="btn btn-success" data-btn-type="submit">派单</button>
								<button type="submit" class="btn btn-primary" data-btn-type="save">保存</button>
							</div>
							<!-- /.box-footer -->
							<div id="tabDiv" class="box-body" v-if="isShow">
								<%@include file="../template/vue-tab-component.jsp" %>
							</div>
						</form>
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
	<script src="${pageContext.request.contextPath}/static/plugins/select2/select2.full.min.js"></script>
	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/initComm.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/router.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/tab-init.js"></script>
	<script src="${pageContext.request.contextPath}/static/vue/vue.min.js"></script>
	<script>
	    //tableId,queryId,conditionContainer
	    var form = null;
        var commonTable;
        var model = "dispatch";
        var winId= model + "Win";
        var formId = model + "Form";
        var keyword = "id";
	    var id = "${id}" || 0;
	    var userId = "<shiro:principal property='userId'></shiro:principal>";
   		var sysData =[],inputData=[],varFields={};
   		var vm;
	    $(function () {
	    	$("#commonForm").attr({id:formId, name: formId});
	    	var url = id == 0 ? pm.router.api(model).create(location.search) : pm.router.api(model).detail(id);
    		ajaxGet(url, null, function(data, status){
				if (status == 'success') {
					vm = new Vue($.extend(true, {}, formVueConfig || {}, tabVueConfig || {}, {
							el: "#app",
							data: $.extend({}, data, {
								isCreate: id == 0,
								isShow: true,
								dataType: "form",
								formGroupClass: "col-sm-12 col-md-6",
								formGroupTextareaClass: "col-sm-12 col-md-6",
								formAction: pm.router.api(model).detail(id),
	   							fieldList: data.fieldList || [],
	   							targetName: data.targetName,
	    						targetValue: data.targetValue
	    				 	}),
    				 	}
					));
					
					form = $("#" + formId).form();
					form.initFormData(data.targetValue);
		    		$("#" + formId).bootstrapValidator({
		                message: '请输入有效值',
		                feedbackIcons:sys.common.feedbackIcons,
		                submitHandler: function(validator, form2, submitButton){
		                	var btnType = $(submitButton).data("btn-type");
		                	var confirmText = btnType == "save" ? "保存" : "派单";
		                	modals.confirm({text:'确认' + confirmText + '？', 
		                		callback: function () {
			                		var index3 = layer.load(1);
			                		var headers = {};
			                		headers['__RequestVerificationToken'] = __RequestVerificationToken;
			                		var formData = form.getFormSimpleData();
			                		var url = btnType == 'submit' ? pm.router.api(model).submit() : (id == 0 ? pm.router.api(model).create() : pm.router.api(model).update(id));
			                		ajaxPost(url, formData,function(data,status){
			                			if(data.status){
			        						modals.correct(confirmText + "成功");
			        						handleResult(data);
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
		    		
		    		// 服务商Select2初始化完成之后，添加change事件，避免直接添加change事件，无法获取原始保存的服务商信息
		    		var projectIdsPlaceholder = data.targetValue.dispatchName;
		    		$("#projectIds").select2({
		    			allowClear: true,
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
		    			        	results: data.data,
		    			        	pagination: {
		    			          		more: (params.page * 30) < data.pageParam.filtered
		    			       		}
		    			      	};
		    			    },
		    			    cache: true
		    			  },
		    			  placeholder: projectIdsPlaceholder || '搜索项目名称',
		    			  minimumInputLength: 4,
		    			  templateResult: formatRepo,
		    			  templateSelection: formatRepoSelection
		    		});
		    		// 项目名称初始化完成之后，添加change事件，避免直接添加change事件，无法获取原始保存的信息
		    		$("#projectIds + .select2-container").one("click", function(e) {
		    			$("#projectIds").on("change", function(e){
		    				try{
		    					var source = $(this).select2("data");
		    					console.log(source);
		    					if (source.length > 0) {
		    						source = source[0];
		    					} else {
		    						source = {};
		    					}
		    					$("#projectName").val(source.projectName);
		    					$("#dispatchName").val(source.projectName);
			    				$("#smsProjectCode").val(source.smsProjectCode);
				    			$("#smsSubmitTime").val(source.smsSubmitTime);
				    			$("#smsProjectAmount").val(source.smsProjectAmount);
			    			} catch(e){}
		    			});
		    		});
		    		
		    		// 服务商Select2初始化完成之后，添加change事件，避免直接添加change事件，无法获取原始保存的服务商信息
		    		$("#facilitatorId + .select2-container").one("click", function(e) {
		    			$("#facilitatorId").on("change", function(e){
		    				var element;
			    			try{
			    				var element =  $($(this).select2("data")[0].element);
			    			} catch(e){}
		    				var source = $(element).data("source") || {};
		    				$("#facilitatorCode").val(source.code);
			    			$("#facilitatorName").val(source.name);
			    			$("#bankInfo").val(source.bankInfo);
			    			$("#bankAccount").val(source.bankAccount);
		    			});
		    		});
    			 }
    		})
    		
    		function handleData() {
    			
    		}
    		
    		function handleResult(results){
    			var isCreate = id == 0;
    			var targetName = results.targetName || model;
    			var targetValue = results[targetName] || {};
    			keyword = window.keyword || "id";
    			id = targetValue[keyword] || targetValue.id || 0;
        		if (isCreate) {
	        		window.location.replace(pm.router.html(model).detail(id));
        		} else {
        			ajaxGet(pm.router.api(model).detail(id), null, function(data, status){
	    				if (status == 'success') {
	   						vm._data.targetValue = data.targetValue;
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
		});
	</script>
</jsTag>
</html>