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

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/select2/select2.min.css">
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
        <h1></h1>
        <ol class="breadcrumb">
        </ol>
    </section>

    <!-- Main content -->
    <section class="content">
        <div class="row">
            <!-- /.col -->
            <div class="col-md-12">
                <div class="box box-primary mb-2">
                    <!-- /.box-header -->
                    <div class="box-body">
                        <div id="searchDiv" class="text-left">
                        	<form id="searchForm">
	                            <%-- <%@include file="../template/vue-form-component.jsp" %> --%>
	                            <%@include file="../template/vue-table-search-component.jsp" %>
		                        <div class="btn-group">
									<button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
									<button type="button" class="btn btn-default" data-btn-type="reset">重置</button>
								</div>
							</form>
                            <div class="btn-group operate-btn-group">
                                <button type="button" class="btn btn-default" data-btn-type="import" v-if="checkPermit('import')">导入</button>
                                <button type="button" class="btn btn-default" data-btn-type="add" v-if="checkPermit('add')">新增</button>
                                <button type="button" class="btn btn-default" data-btn-type="edit" v-if="checkPermit('edit')">编辑</button>
                                <button type="button" class="btn btn-default" data-btn-type="delete" v-if="checkPermit('delete')">删除</button>
                            </div>
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
    <script>
        //tableId,queryId,conditionContainer
        var commonTable;
        var urlNamespace = "${urlNamespace}";
        var model = "${model}";
        var keyword = "${keyword}" || "id";
        var winId= model + "Win";
        var tableId = model + "Table";
        $(function() {
        	var search = '${pageContext.request.queryString}' || location.search;
        	$("#commonTable").attr("id", tableId);
            commonTable = new CommonTable(tableId, router(urlNamespace).api(model).list(search), "searchDiv",{
                searching :true,
                rowId: keyword,
                beforeInitConfig: function() {
                	vm = new Vue($.extend(true, {}, formVueConfig || {}, {
							el: "#" + this.searchDiv,
							data: {
								dataTable: this,
								targetValue: this.data.extData.targetValue,
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
                	form = $("#searchForm").form();
                	
                	// 回调函数
		        	if (router(urlNamespace).callback(model).list) {
		        		var vueCallback = (router(urlNamespace).callback(model).list || {}).vueCallback;
		        		if (typeof vueCallback == 'function') {
		        			vueCallback.call(vm, this, $("#" + tableId));
		        		}
		        	}
                },
            });

            //button event
            var preTargger = "";
            $(document).off('click', "#" + tableId  + '_wrapper .operate-btn-group button[data-btn-type]');
            $(document).on('click', "#" + tableId  + '_wrapper .operate-btn-group button[data-btn-type]', function() {
                var action = $(this).attr('data-btn-type');
                var rowId= commonTable.getSelectedRowId();
                switch (action) {
                case 'import':
                    modals.openWin({
                         winId: winId,
                         title:'导入信息',
                         width: '75vw',
                         url: router(urlNamespace).html(model).import(search, true)
                    });
                    break;
                case 'add':
                    /*    modals.openWin({
                        winId:winId,
                        title:'新增考核计划',
                        width:'800px',
                        url:basePath+"/perf/modals/project_detail"
                       });  */
                    window.location.href = router(urlNamespace).html(model).create();
                    break;
                case 'edit':
                    if(!rowId){
                        modals.info('请选择要编辑的行');
                        return false;
                    }
                    /*
                    modals.openWin({
                        winId:winId,
                        title:'编辑考核计划【'+commonTable.getSelectedRowData().name+'】',
                        width:'600px',
                        url:basePath+"/perf/modals/project_detail?id="+rowId
                   });
                    */
                   window.location.href = router(urlNamespace).html(model).detail(rowId);
                   break;
                case 'delete':
                    if(!rowId){
                        modals.info('请选择要删除的行');
                        return false;
                    }
                    modals.confirm("是否要删除该行数据？",function(){
                        ajaxPost(router(urlNamespace).api(model).delete(rowId),null,function(data,status){
                            if(data.status){
                                modals.info("删除成功！");
                                commonTable.reloadData();
                            }else {
                                modals.info(data.message || "删除失败！");
                            }
                        });
                    })
                    break;
                }
            });

            $(document).off("dblclick", "#" + tableId  + " tbody tr");
            $(document).on("dblclick", "#" + tableId  + " tbody tr", function () {
            	console.log("#" + tableId  + " tbody tr:dblclick");
                var rowId = commonTable.getSelectedRowId();
                if(rowId == null){
                    modals.info('请点击需要查看的行');
                    return false;
                }
            	var row = commonTable.getSelectedRowData();
                var url = router(urlNamespace).html(model).detail(rowId, null, row);
                window.open(url);
            });
            
         	// 页面加载完成回调函数函数
        	if (router(urlNamespace).callback(model).list) {
        		var complate = (router(urlNamespace).callback(model).list || {}).complete;
        		if (typeof complate == 'function') {
        			complate.call(this);
        		}
        	}
        })
    </script>
</jsTag>