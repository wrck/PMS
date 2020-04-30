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
				<div v-else>
					<span>　</span><small>　</small>
				</div>
			</h1>
			<span class="display-none"></span>
			<ol class="breadcrumb">
			</ol>
		</section>
		<section class="content">
			<div class="row">
				<div class="col-xs-12">
					<div class="box box-info">
						<form id="commonForm" method="post" :action="formAction" name="commonForm" class="form-inline">
							<div class="box-body row ml-0" v-if="isShow">
								<%@include file="../template/vue-form-component.jsp" %>
							</div>
							<!-- /.box-body -->
							<div class="box-footer text-right">
								<button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">取消</button>
								<button type="submit" class="btn btn-primary" data-btn-type="save">保存</button>
							</div>
							<!-- /.box-footer -->
						</form>
					</div>
					<div id="tabDiv" class="" v-if="isShow">
						<%@include file="../template/vue-tab-component.jsp" %>
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
	<script src="${pageContext.request.contextPath}/static/plugins/select2/select2.min.js"></script>
	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/initComm.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/router.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/tab-init.js"></script>
	<script src="${pageContext.request.contextPath}/static/vue/vue.min.js"></script>
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
	   		var isModals = '${isModals}';
	   		var search = '${pageContext.request.queryString}' || location.search;
	    	$("#commonForm").attr({id:formId, name: formId});
	    	$("#app").attr({id: appId});
	    	$("#tabDiv").attr({id: model + "TabDiv"});
	    	var $container = $("#" + formId);
	    	var url = id == 0 ? pm.router.api(model).create(search) : pm.router.api(model).detail(id);
    		ajaxGet(url, null, function(data, status){
				if (status == 'success') {
					vm = new Vue($.extend(true, {}, formVueConfig || {}, tabVueConfig || {}, {
							el: "#" + appId,
							data: $.extend({}, data, {
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
	    						targetValue: data.targetValue
	    				 	}),
    				 	}
					));
					
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
	    					vm._data.fieldList = data.fieldList || [];
	    					vm._data.tabList = data.tabList || [];
	   						vm._data.targetValue = data.targetValue;
	   						// form.initFormData(data.targetValue);
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
	    		}
	    	})
	    }
	</script>
</jsTag>
</html>