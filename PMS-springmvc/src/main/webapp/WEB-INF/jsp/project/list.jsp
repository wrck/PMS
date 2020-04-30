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
	                            <%@include file="../template/vue-table-search-component.jsp" %>
		                        <div class="btn-group">
									<button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
									<button type="button" class="btn btn-default" data-btn-type="reset">重置</button>
								</div>
							</form>
                            <!-- <div class="btn-group operate-btn-group">
                                <button type="button" class="btn btn-default" data-btn-type="add">新增</button>
                                <button type="button" class="btn btn-default" data-btn-type="edit">编辑</button>
                                <button type="button" class="btn btn-default" data-btn-type="delete">删除</button>
                            </div> -->
                        </div>
                        <table id="project_table" class="table table-bordered table-striped table-hover">
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
        var projectTable;
        var winId="projectWin";
        $(function() {
            projectTable = new CommonTable("project_table", pm.project.api.list(), "searchDiv",{
                searching :true,
                rowId: 'projectId',
                /* "columns" : [
                    {
                        title : "项目名",
                        data : "projectName",
                        visible: true,
                        sortable: true,
                        render : function(data, type, row) {
                            var id = row.id;
                            var html = "<a class='link' href='"+ pm.project.html.detail(id) + "'>" + data + "</a>"
                            return html;
                        }
                    },
                    {
                        title : "项目编码",
                        data : "projectCode",
                        visible: true,
                        sortable: true,
                    },
                    {
                        title : "状态",
                        data : "projectState",
                        visible: true,
                        sortable: true,
                    },
                    {
                        title : "状态",
                        data : "customInfo.int1",
                        visible: true,
                        sortable: true,
                    }
                ], */
                /* "columnDefs" : [ {
                    // 定义操作列,######以下是重点########
                    targets : 8,//操作按钮目标列
                    data : "id",
                    title: "操作",
                    sortable: false,
                    render : function(data, type, row) {
                        var id = '"' + row.id + '"';
                        //<a href='javascript:void(0);'  class='delete btn btn-default btn-xs'  ><i class='fa fa-times'></i> 查看</a>
                        var html = "<a class='btn btn-xs btn-success' href='${pageContext.request.contextPath}/perf/project/"+data+".html'><i class='icon-ok'></i>详情</a>"
                        return html;
                    }
                } ] */
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
                	form = $("#searchForm").form();
                },
            });

            //button event
            var preTargger = "";
            $('button[data-btn-type]').click(function() {
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
                    window.location.href = basePath + "/perf/project/detail.html";
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
                            if(status == "success"){
                                modals.info("更新成功！");
                                projectTable.reloadData();
                            }else{
                                modals.info(data);
                            }
                        });
                    })
                    break;
                }
            });

            $(document).on("dblclick", "#project_table tbody tr", function () {
                var row = projectTable.getSelectedRowData();
                if(row == null){
                    modals.info('请点击需要查看的项目行');
                    return false;
                }
                var id = row.id ||row.projectId;
                var search = $.param({contractNo: row.contractNo, projectType:row.projectType});
                var url = id ? pm.project.html.detail(id) : pm.project.html.create(search);
                window.open(url);
            });

        })
    </script>
</jsTag>