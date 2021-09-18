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
	<div id="mergeProjectApp">
		<section class="content-header">
			<h4 id="pageTitle" class="fade m-0" :class="{in: isShow}">请选择需要{{title}}</h4>
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
	                        <div id="mergeSearchDiv" class="text-left searchDiv">
	                        	<form id="mergeSearchForm">
		                            <%@include file="../template/vue-table-search-component.jsp" %>
			                        <div class="btn-group">
										<button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
										<button type="button" class="btn btn-default" data-btn-type="reset">重置</button>
									</div>
	                                <div class="btn-group operate-btn-group">
	                                    <button type="button" class="btn btn-default" data-btn-type="${type}" v-if="checkPermit('add')">${typeName}</button>
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
    </div>
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
            var mergeProjectTable;
            var urlNamespace = "${urlNamespace}";
            var model = "${model}";
            var transformType = "${type}";
            var transformName = "${typeName}";
            var winId = model + transformType + "Win";
            var tableId = model + transformType + "Table";
        	var search = '${pageContext.request.queryString}' || location.search;
            var form = null;
            var projectId = Number("${projectId}") || 0;
            var isSingle = transformType == 'transfer' ? true : (projectId == 0 ? true : false);
            var transformDirection = transformType + (projectId > 0 ? "From" : "To");
            var transformDirectionTitles = {
            	mergeFrom: "合并到当前项目的合同号",
            	mergeTo: "合并当前合同号的项目",
            	transferFrom: "核销当前项目的合同号",
            	transferTo: "核销的项目",
            };
            var transformDirectionTips = {
               	mergeFrom: "将所选合同号合并到当前项目",
               	mergeTo: "将当前合同号合并到所选项目",
               	transferFrom: "以所选合同号核销当前项目",
               	transferTo: "以当前合同号核销所选项目",
               };
        	$("#commonTable").attr("id", tableId);
            mergeProjectTable = new CommonTable(tableId, router(urlNamespace).api(model).list(projectId > 0 ? "projectState=10" : ""), "mergeSearchDiv",{
                checkbox: isSingle ? undefined : {
                    flag: "iCheckCustom",
                },
                paging: true,
                lengthChange: false,
                searchInline: true,
                sameTrigger: true,
                singleSelect: isSingle || false,
                stateSave: false,
                "scrollX":"100%",
                "scrollY":"50vh",
                "scrollCollapse": true,
                disableSlimScroll: true,
                searching :false,
                rowId: 'projectId',
                beforeInitConfig: function() {
                    console.log(this);
                	vm = new Vue($.extend(true, {}, formVueConfig || {}, {
							el: "#mergeProjectApp" || "#" + this.searchDiv,
							data: {
								isShow: true,
								targetValue: this.data.extData.dailyReportVO,
	   							fieldList: this.data.columns || [],

	   							// 权限控制参数
	    						model: this.data.extData.model || model,
	    						permissionType: this.data.extData.permissionType || "",
	    						permissions: this.data.extData.permissions || [],
	    						roles: this.data.extData.roles || [],
	    						
	    						title: transformDirectionTitles[transformDirection] || ""
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
                	
                	// 重新绑定dataTable，避免Vue初始化dom发生变化后丢失data数据；
                	$("#" + this.tableId).data("dataTable", $("#" + this.tableId).data("dataTable") || this);
                	this.searchButton = $("#" + this.searchDiv + " button[data-btn-type='search']");
                	this.restButton = $("#" + this.searchDiv + " button[data-btn-type='reset']");
                	form = $("#mergeSearchForm").form();
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
                var rows = mergeProjectTable.getSelectedRowsData() || [];
                if(!rows.length){
                    modals.info("请选择需要" + transformDirectionTitles[transformDirection]);
                    return false;
                }
                var contractNos = [];
                var projectTypes = [];
                var projectIds = [];
                for (var i = 0; i < rows.length; i++) {
					var row = rows[i];
					var tProjectId = row.projectId;
					var tContractNo = row.contractNo || (row.customInfo || {}).contractNo;
					var tProjectType = row.projectType;
					if (contractNo && $.inArray(contractNo, contractNos) == -1) {
						projectIds.push(tProjectId);
						contractNos.push(tContractNo);
						projectTypes.push(tProjectType);
					}
				}
                var currentProject = $("#projectForm", projectVm.$el).data("baseForm").getFormSimpleData();
                modals.confirm("是否" + transformDirectionTips[transformDirection], function(){
                	var layerId = layer.load(3);
                    ajaxPost(router(urlNamespace).api(model).projectTransform(projectId, transformType), $.extend(currentProject, {projectIds: projectIds.join(","), contractNos: contractNos.join(","), projectTypes: projectTypes.join(",")}),function(data,status){
                        if(data.status){
                            modals.info(transformName + "成功！");
                            if (data.projectId) {
                            	projectVm.targetValue.projectId = data.projectId;
                            }
                            modals.hideWin(winId);
                        }else {
                            modals.info(data.message);
                        }
                    }, null, null,function() {
                        layer.close(layerId);
                    });
                });
                /* switch (transformType) {
                case 'merge':
                    if(!rowIds){
                        modals.info('请选择要合并的项目合同号');
                        return false;
                    }
                    modals.confirm("是否要合并所选项目合同号？",function(){
                        ajaxPost(router(urlNamespace).api(model).merge(transformType, search), {contractNo: contractNos.join(","), projectTypes: projectTypes.join(",")},function(data,status){
                            if(data.status){
                                modals.info("合并成功！");
                                mergeProjectTable.reloadData();
                            }else {
                                modals.info(data.message);
                            }
                        });
                    });
                    break;
	            case 'transfer':
	                if(!rowIds){
	                    modals.info('请选择要核销的项目合同号');
	                    return false;
	                }
	                break;
                } */
            });

            $(document).off("dblclick", "#" + tableId  + " tbody tr");
            $(document).on("dblclick", "#" + tableId  + " tbody tr", function () {
            	/* console.log("#" + tableId  + " tbody tr:dblclick");
                var rowId =  $(this).attr("id") || mergeProjectTable.getSelectedRowId();
                if(rowId == null){
                    modals.info('请点击需要查看的行');
                    return false;
                } */
                var dataTable = $(this).parents("table.dataTable:first").data("dataTable");
                var row = dataTable.getSelectedRowData();
                if(row == null){
                    modals.info('请点击需要查看的项目行');
                    return false;
                }
                var id = row.id || row.projectId;
                var search = $.param({contractNo: row.contractNo, projectType:row.projectType});
                var url = id ? pm.project.html.detail(id) : pm.project.html.create(search);
                // var url = router(urlNamespace).html(model).detail(rowId);
                window.open(url);
            });
        })
    </script>
</jsTag>