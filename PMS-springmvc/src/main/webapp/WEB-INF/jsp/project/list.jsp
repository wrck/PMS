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
        span.warn{
		    color:red;
		    margin-left: 3px;
		}
		li.active span.warn{
		    color:white;
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
          		<div class="tab-content" style="border:1px solid #ddd;background-color: white;">
                    <ul class="nav nav-tabs">
                        <li><a href="#tab-project-all" data-toggle="tab" id="nav-tab-all" aria-expanded="false">已创建项目</a></li>
                        <li><a href="#tab-project-create" data-toggle="tab" id="nav-tab-create" aria-expanded="true">待创建项目</a></li>
                    </ul>
                    <div class="tab-pane" id="tab-project-all">
                        <div class="box box-primary">
		                    <!-- /.box-header -->
		                    <div class="box-body">
		                        <div id="all_searchDiv" class="text-left searchDiv">
		                        	<form id="all_searchForm">
			                            <%@include file="../template/vue-table-search-component.jsp" %>
				                        <div class="btn-group">
											<button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
											<button type="button" class="btn btn-default" data-btn-type="reset">重置</button>
										</div>
									</form>
		                            <div class="btn-group operate-btn-group">
		                                <button type="button" class="btn btn-default" data-btn-type="add">创建项目</button>
		                                <!-- <button type="button" class="btn btn-default" data-btn-type="edit">编辑</button>
		                                <button type="button" class="btn btn-default" data-btn-type="delete">删除</button> -->
		                            </div>
		                        </div>
		                        <table id="all_project_table" class="table table-bordered table-striped table-hover">
		                        </table>
		                    </div>
		                    <!-- /.box-body -->
		                </div>
                    </div>
                    <div class="tab-pane" id="tab-project-create">
                    	<div class="box box-primary">
		                    <!-- /.box-header -->
		                    <div class="box-body">
		                        <div id="create_searchDiv" class="text-left searchDiv">
		                        	<form id="create_searchForm">
			                            <%@include file="../template/vue-table-search-component.jsp" %>
				                        <div class="btn-group">
											<button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
											<button type="button" class="btn btn-default" data-btn-type="reset">重置</button>
										</div>
									</form>
									<div class="btn-group operate-btn-group">
		                                <button type="button" class="btn btn-default" data-btn-type="add">创建项目</button>
		                                <!-- <button type="button" class="btn btn-default" data-btn-type="edit">编辑</button>
		                                <button type="button" class="btn btn-default" data-btn-type="delete">删除</button> -->
		                            </div>
		                        </div>
		                        <table id="create_project_table" class="table table-bordered table-striped table-hover">
		                        </table>
		                    </div>
		                    <!-- /.box-body -->
		                </div>
                	</div>
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
        var projectTable;
        var winId="projectWin";
        $(function() {
        	initTabByStorage();
        	
        	var search = '${pageContext.request.queryString}' || location.search;
            projectTable = new CommonTable("all_project_table", pm.project.api.list(search), "all_searchDiv",{
                searching :true,
                rowId: 'projectId',
                exportData: {
                	url: pm.project.api.list(search).replace(".json", ".xlsx"),
                	fileName: "项目清单",
                	type: ["excel"]
                },
                beforeInitConfig: function() {
                	vm = new Vue($.extend(true, {}, formVueConfig || {}, {
							el: "#" + this.searchDiv,
							data: {
								targetValue: this.data.extData.projectVO,
	   							fieldList: this.data.columns || []
	    				 	},
	                	})
                	);
                	
                	this.searchButton = $("#" + this.searchDiv + " button[data-btn-type='search']");
                	this.restButton = $("#" + this.searchDiv + " button[data-btn-type='reset']");
                	form = $("#all_searchForm").form();
                },
                customDrawCallback: function() {
                    initWarnNum(this, "all", false);
                }
            });
            
            createProjectTable = new CommonTable("create_project_table", pm.project.api.list("projectState=10"), "create_searchDiv", {
                searching :true,
                rowId: 'projectId',
                exportData: {
                	url: pm.project.api.list("projectState=10").replace(".json", ".xlsx"),
                	fileName: "待创建项目清单",
                	type: ["excel"]
                },
                beforeInitConfig: function() {
                	vm = new Vue($.extend(true, {}, formVueConfig || {}, {
							el: "#" + this.searchDiv,
							data: {
								targetValue: this.data.extData.projectVO,
	   							fieldList: this.data.columns || []
	    				 	},
	                	})
                	);
                	
                	this.searchButton = $("#" + this.searchDiv + " button[data-btn-type='search']");
                	this.restButton = $("#" + this.searchDiv + " button[data-btn-type='reset']");
                	createform = $("#create_searchForm").form();
                },
                customDrawCallback: function() {
                	$("#projectState", $("#" + this.searchDiv)).parent().remove();
                	$("#programManagerCodeforjson", $("#" + this.searchDiv)).parent().remove();
                    initWarnNum(this, "create", true);
                }
            });

            //button event
            var preTargger = "";
            $(document).on("click", "button[data-btn-type]", function () {
            //$('button[data-btn-type]').click(function() {
                var action = $(this).attr('data-btn-type');
                var rowId=projectTable.getSelectedRowId();
                switch (action) {
                case 'add':
                    /*    modals.openWin({
                        winId:winId,
                        title:'新增考核计划',
                        width:'800px',
                        url:basePath+"/perf/modals/project_detail"
                       });  */
                    window.location.href = basePath + "/pm/project/detail.html";
                    break;
                case 'edit':
                    if(!rowId){
                        modals.info('请选择要编辑的行');
                        return false;
                    }
                    /*
                    modals.openWin({
                        winId:winId,
                        title:'编辑考核计划【'+projectTable.getSelectedRowData().name+'】',
                        width:'600px',
                        url:basePath+"/perf/modals/project_detail?id="+rowId
                   });
                    */
                   window.location.href = basePath + "/perf/project/" + rowId + ".html";
                   break;
                case 'delete':
                    if(!rowId){
                        modals.info('请选择要删除的行');
                        return false;
                    }
                    modals.confirm("是否要删除该行数据？",function(){
                        ajaxPost(basePath+"/perf/project/"+rowId+".json?_method=DELETE",null,function(data,status){
                        	if(data.status){
                                modals.info("删除成功！");
                                commonTable.reloadData();
                            }else {
                                modals.info(data.message);
                            }
                        });
                    })
                    break;
                }
            });

            $(document).off("dblclick", "#all_project_table tbody tr, #create_project_table tbody tr");
            $(document).on("dblclick", "#all_project_table tbody tr, #create_project_table tbody tr", function () {
            	var dataTable = $(this).parents("table.dataTable:first").data("dataTable");
            	var row = dataTable.getSelectedRowData();
                if(row == null){
                    modals.info('请点击需要查看的项目行');
                    return false;
                }
                var id = row.id || row.projectId;
                var search = $.param({contractNo: row.contractNo, projectType:row.projectType});
                var url = id ? pm.project.html.detail(id) : pm.project.html.create(search);
                window.open(url);
            });

            $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
                var tabId = $(e.target).attr("href");
                var table = tabId.replace("#tab-project-", "");
                try {
                    eval(table+ "Table").table.columns.adjust();
                } catch(e) {
                    //console.log(e);
                }
                if(typeof(Storage)!=="undefined"){
                	var path = location.pathname || "";
                	sessionStorage[path + "_tabFlag"] = table;
                }
            });
            
            function initTabByStorage(){
            	var path = location.pathname || "";
            	var tabFlag = (sessionStorage || {})[path + "_tabFlag"];
            	if(typeof(Storage)!=="undefined" && tabFlag){
           			$("section.content ul.nav.nav-tabs li.active").removeClass("active");
           			$("section.content .tab-pane.active").removeClass("active");
           			$("#nav-tab-" + tabFlag).parent().addClass("active");
           			$("#tab-project-" + tabFlag).addClass("active");
            	}else{
            		$("#nav-tab-all").parent().addClass("active");
           			$("#tab-project-all").addClass("active");
            	}
            }
            
          	//初始化数量提醒
            function initWarnNum(typeTable, type, noWarn){
                var total = typeTable.table.page.info().recordsTotal;
                $("#nav-tab-"+type + " .banner").remove();
                if(total > 0){
                	var bannerClass = noWarn == false ? "" : "warn";
                    $("#nav-tab-"+type).append("<span class='banner " + bannerClass + "'>("+total+")</span>");
                }
            }
        })
    </script>
</jsTag>