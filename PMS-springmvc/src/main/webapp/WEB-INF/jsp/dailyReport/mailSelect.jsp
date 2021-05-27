<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
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
    <style>
        #searchDiv{
            display: none;
            margin-bottom: 1rem;
        }

        .dataTable input,.dataTable select,.dataTable textarea{
            color: black;
        }
    </style>
</cssTag>
</head>
<body>
	<section class="content-header">
		<h4 id="pageTitle" class="fade in m-0">请选择需要在邮件中体现的日报记录行<!-- <small>（多次发送将产生多封邮件）</small> --></h4>
		<ol class="breadcrumb"></ol>
		<button class="close" type="button" data-dismiss="modal" aria-label="Close">
			<span aria-hidden="true">×</span>
		</button>
	</section>
	<!-- Main content -->
    <section class="content">
        <div class="row">
            <!-- /.col -->
            <div class="col-md-12">
                <div class="box box-primary mb-2">
                    <!-- /.box-header -->
                    <div class="box-body">
                        <div id="mailSelectSearchDiv" class="text-left">
                        	<form id="mailSelectSearchForm">
	                            <%@include file="../template/vue-table-search-component.jsp" %>
		                        <div class="btn-group">
									<button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
									<button type="button" class="btn btn-default" data-btn-type="reset">重置</button>
								</div>
                                <div class="btn-group operate-btn-group">
                                    <button type="button" class="btn btn-default" data-btn-type="${mailType}" v-if="checkPermit('add')">发送</button>
                                </div>
                            </form>
                        </div>
                        <table id="commonTable" class="table table-bordered table-striped table-hover">
                        </table>
                    </div>
                    <!-- /.box-body -->
                </div>
            </div>
        </div>
        <!-- /.row -->

    </section>
</body>
</html>
<jsTag>
<c:if test="${!isModals}">
    <!-- DataTables -->
    <script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/jquery.dataTables.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/dataTables.bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/common/js/dataTablesExt.js"></script>

	<script src="${pageContext.request.contextPath}/static/plugins/select2/select2.min.js"></script>
	
    <script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
    <script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
   	<script src="${pageContext.request.contextPath}/static/pm/js/initComm.js"></script>
   	<script src="${pageContext.request.contextPath}/static/pm/js/router.js"></script>
  	<script src="${pageContext.request.contextPath}/static/vue/vue.min.js"></script>
</c:if>
    <script>
        $(function() {
            //tableId,queryId,conditionContainer
            var commonTable;
            var urlNamespace = "${urlNamespace}";
            var model = "${model}";
            var winId= model + "MailSelectWin";
            var tableId = model + "MailSelectTable";
            var mailType = "${mailType}";
        	var search = '${pageContext.request.queryString}' || location.search;
            var form = null;
        	$("#commonTable").attr("id", tableId);
            commonTable = new CommonTable(tableId, router(urlNamespace).api(model).mailSelect(mailType, search), "mailSelectSearchDiv",{
                checkbox: {
                    flag: "iCheckCustom",
                },
                paging: false,
                lengthChange: false,
                searchInline: true,
                sameTrigger: true,
                singleSelect: false,
                stateSave: false,
                "scrollX":"100%",
                "scrollY":"50vh",
                "scrollCollapse": true,
                disableSlimScroll: true,
                searching :false,
                rowId: 'id',
                beforeInitConfig: function() {
                    console.log(this);
                	vm = new Vue($.extend(true, {}, formVueConfig || {}, {
							el: "#" + this.searchDiv,
							data: {
								targetValue: this.data.extData.dailyReportVO,
	   							fieldList: this.data.columns || [],
	   						
	   							// 权限控制参数
	    						model: this.data.extData.model || model,
	    						permissionType: this.data.extData.permissionType || "",
	    						permissions: this.data.extData.permissions || [],
	    						roles: this.data.extData.roles || []
	    				 	},
	    				 	methods: {
	    				 		checkPermit: function(btn) {
	    				 			/* var target = event.currentTarget;
	    				 			btn = btn || $(target).data("btnType"); */
	    				 			var permissionType = this.permissionType || "";
	    				 			var permissions = this.permissions || [];
	    			 				var model = this.model || "";
	    			 				var permission = model + ":" + btn;
	    			 				var checkPermitCallback = (router(urlNamespace).callback(model).list || {}).operationCallback;
	    				 			console.log(permission);
	    				 			var isPermit = false;
	    				 			if ((permissionType == "all" 
	    				 					|| permissionType == "edit" && RegExp(/:(add|edit|upload|delete|import)\b,?/).test(permission) 
	    				 					|| (permissionType == "edit" || permissionType == "view") && RegExp(/:(list|detail|download|batchDownload)\b,?/).test(permission))
	    				 					&& ($.inArray(permission, permissions) > -1 || $.inArray(model + ":*", permissions) > -1)) {
	    				 				isPermit = true;
	    							}
	    				 			if (typeof checkPermitCallback == 'function') {
	    				 				try {
	    				 					isPermit = checkPermitCallback.call(this, btn) || isPermit;
	    				 				} catch(e) {}
	    				 			}
	    				 			return isPermit;
	    						},
	    				 	}
	                	})
                	);
                	
                	this.searchButton = $("#" + this.searchDiv + " button[data-btn-type='search']");
                	this.restButton = $("#" + this.searchDiv + " button[data-btn-type='reset']");
                	form = $("#mailSelectSearchForm").form();
                	// 回调函数
		        	// if (router(urlNamespace).callback(model).list) {
		        	// 	var vueCallback = (router(urlNamespace).callback(model).list || {}).vueCallback;
		        	// 	if (typeof vueCallback == 'function') {
		        	// 		vueCallback.call(vm, this, $("#" + tableId));
		        	// 	}
		        	// }
                },
                customDrawCallback: function() {
                    form.initFormData(vm.targetValue);
                }
            });

            //button event
            var preTargger = "";
            $(document).off('click', "#" + tableId  + '_wrapper .operate-btn-group button[data-btn-type]');
            $(document).on('click', "#" + tableId  + '_wrapper .operate-btn-group button[data-btn-type]', function() {
                var action = $(this).attr('data-btn-type');
                var rowIds= commonTable.getCheckedRowsId();
                switch (mailType) {
                case 'daily':
                    if(!rowIds){
                        modals.info('请选择要发送的日报');
                        return false;
                    }
                    modals.confirm("是否要发送所选日报？",function(){
                        ajaxPost(router(urlNamespace).api(model).mailReport(mailType, search), {idsStr: rowIds.join(",")},function(data,status){
                            if(data.status){
                                modals.info("发送成功！");
                                commonTable.reloadData();
                            }else {
                                modals.info(data.message);
                            }
                        });
                    })
                    break;
                }
            });

            $(document).off("dblclick", "#" + tableId  + " tbody tr");
            $(document).on("dblclick", "#" + tableId  + " tbody tr", function () {
            	console.log("#" + tableId  + " tbody tr:dblclick");
                var rowId =  $(this).attr("id") || commonTable.getSelectedRowId();
                if(rowId == null){
                    modals.info('请点击需要查看的行');
                    return false;
                }
                var url = router(urlNamespace).html(model).detail(rowId);
                window.open(url);
            });
        })
    </script>
</jsTag>