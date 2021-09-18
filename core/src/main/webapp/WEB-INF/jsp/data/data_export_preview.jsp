<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %> 
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="system.title" /></title>
<cssTag> 
<%-- <!-- DataTables -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datatables/media/css/dataTables.bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datatables/extensions/Select/css/select.bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/plugins/select2/select2.min.css"> --%>
<style>
#searchDiv {
	display: none;
	margin-bottom: 1rem;
}
</style>
</cssTag>
</head>
<body>
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1></h1>
		<ol class="breadcrumb">
		</ol>
	</section>

	<!-- Main content -->
	<section class="content">
		<!-- Default box -->
		<div class="box box-primary">
			<!-- /.box-header -->
			<div class="box-body">
				<div class="row">
					<div class="col-sm-12">
						<div id="exportPreviewTableSearchDiv" class="text-right">
							<input type="hidden" name="columnsStr" data-type="search">
							<input type="hidden" name="objectKV" data-type="search">
							<div class="btn-group operate-btn-group">
								<button type="button" class="btn btn-default" data-btn-type="export">导出</button>
							</div>
						</div>
						<table id="exportPreviewTable" class="table table-bordered table-striped table-hover dataTable">
						</table>
						<form id="perviewExportForm" action="${pageContext.request.contextPath}/data/operation/${id}.xlsx" method="post">
							<input type="hidden" name="__RequestVerificationToken" value="${__RequestVerificationToken}">
							<input type="hidden" name="columns" value="">
							<input type="hidden" name="objectName" value="${objectName}">
							<input type="hidden" name="objectKV" value="">
							<input type="hidden" name="pageParamKV" value="">
						</form>
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
<%-- <script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/jquery.dataTables.min.js"></script>
<script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/dataTables.bootstrap.min.js"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/static/plugins/select2/select2.min.js"></script>

<script src="${pageContext.request.contextPath}/static/common/js/dataTablesExt.js"></script>
<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script> --%>
<script>
    var exportPreviewTable;
    var winId = "exportPreviewWin";
    var exportId = "${id}" || "0";
    var operationName = "${operationName}";
    var config = {
    		searching: true,
    		stateSave: false,
    		/* scrollY: '60vh',
    		scrollX: '100%',
    		scrollCollapse : true, */
    		disableSlimScroll : true,
    		initCallback: function () {
    			/* $("#exportPreviewTable").parent().addClass("table-responsive"); */
    		}
    	}
    	
	$(document).ready(function() {
		var columns = localStorage.getItem("export_preview_columns_" + exportId) || "";
		var objectKV = localStorage.getItem("export_preview_objectKV_" + exportId) || "";
		$("#exportPreviewTableSearchDiv input[name='columnsStr']").val(columns);
		$("#exportPreviewTableSearchDiv input[name='objectKV']").val(objectKV);
		localStorage.removeItem("export_preview_objectKV_" + exportId);
		exportPreviewTable = new CommonTable("exportPreviewTable", basePath + "/data/export/preview/" + exportId + ".json", "exportPreviewTableSearchDiv", config);
		
		$("#exportPreviewTableSearchDiv button[data-btn-type='export']").click(function() {
			$("#perviewExportForm  input[name='columns']").val(columns);
			$("#perviewExportForm  input[name='objectKV']").val(objectKV);
			$("#perviewExportForm  input[name='objectName']").val("java.util.HashMap");
			$("#perviewExportForm").submit();
			showExportProcess(operationName);
		})
	});
	
	
</script>
</jsTag>
</html>