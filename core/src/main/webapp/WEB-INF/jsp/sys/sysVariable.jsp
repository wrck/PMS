<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib  prefix="dp" uri="/myTag" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="system.title" /></title>
<cssTag>
	<!-- DataTables -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datatables/media/css/dataTables.bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datatables/extensions/Select/css/select.bootstrap.min.css">
	<dp:link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css" />
	<style>
		#searchDiv{
			display: none;
			margin-bottom: 1rem;
		}
	</style>
</cssTag>
</head>
<body>
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>系统参数管理</h1>
	</section>

	<!-- Main content -->
	<section class="content">
		<!-- Default box -->
		<div class="box box-primary">
			<!-- /.box-header -->
			<div class="box-body">
				<div class="row">
					<div class="col-sm-12">
				        <div id="searchDiv" class="text-right">
                            <div class="btn-group operate-btn-group">
                                <button type="button" class="btn btn-default" data-btn-type="add">新增</button>
                                <button type="button" class="btn btn-default" data-btn-type="edit">编辑</button>
                                <button type="button" class="btn btn-default" data-btn-type="delete">删除</button>
                            </div>
                        </div>
						<table id="sysVariableTable"
							class="table table-bordered table-striped table-hover dataTable">
						</table>
					</div>
				</div>
			</div>
			<!-- /.box-body -->
		</div>
		<!-- /.box -->
	</section>
	<!-- /.content -->
</body>
<jsTag>
	<!-- DataTables -->
	<script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/jquery.dataTables.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/dataTables.bootstrap.min.js"></script>
	<dp:script src="${pageContext.request.contextPath}/static/common/js/dataTablesExt.js"></dp:script>
	<%-- <dp:script src="${pageContext.request.contextPath}/static/common/js/base.js"></dp:script> --%>
	<dp:script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></dp:script>
	<dp:script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></dp:script>
	<script>
	    var sysVariableTable;
	    var winId = "sysVariableWin";
		$(document).ready(function() {
			sysVariableTable = new CommonTable("sysVariableTable", "${pageContext.request.contextPath}/sys/sysVariable/list.json", "searchDiv",{});
			//button event   
            $('button[data-btn-type]').click(function() {
                var action = $(this).attr('data-btn-type');
                var rowId = sysVariableTable.getSelectedRowId();
                switch (action) {
                case 'add':
                       modals.openWin({
                        winId:winId,
                        title:'新增系统参数',
                        width:'600px',
                        url:basePath+"/sys/modals/sysVariable_detail.html"
                       });                        
                    break;
                case 'edit':
                    if(!rowId){
                        modals.info('请选择要编辑的行');
                        return false;
                    }
                    modals.openWin({
                        winId:winId,
                        title:'编辑参数【'+sysVariableTable.getSelectedRowData().code+'】',
                        width:'600px',
                        url:basePath+"/sys/modals/sysVariable_detail.html?id="+rowId
                   }); 
                   break;
                case 'delete':
                    if(!rowId){
                        modals.info('请选择要删除的行');
                        return false;
                    }
                    modals.confirm("是否要删除该行数据？",function(){
                        ajaxPost(basePath+"/sys/sysVariable/"+rowId+".json?_method=DELETE",null,function(data,status){
                            if(status == "success"){
                                //modals.correct("已删除该数据");
                                sysVariableTable.reloadData();
                            }else{  
                                //setTimeout(function(){modals.info(data.message)},2000);
                                modals.info(data); 
                            }  
                        });  
                    })
                    break;  
                }
            });
            //form_init();
		});
	</script>
</jsTag>
</html>