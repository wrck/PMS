<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css">
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
		<h1>系统运行日志管理</h1>
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
			            <div id="searchDiv" >
			                <div class="form-group">
                                <label>搜索：</label>
                                <input type="search" name="fuzzy" class="form-control" placeholder="日志描述、操作人、请求IP" id="fuzzySearch" style="min-width: 250px;">
                            </div>
                            <div class="form-group">
				            	<label>日志类型：</label>
				                <select class="form-control" id="status-search" name="type" type="search">
				                    <option value="">全部</option>
				                    <option value="0">正常日志</option>
				                    <option value="1">异常日志</option>
				                </select>
			                </div>
			                <div class="btn-group">
								<button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
								<button type="button" class="btn btn-default" data-btn-type="reset">重置</button>
							</div>
						</div>
						<table id="sysLogTable"
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
	<script src="${pageContext.request.contextPath}/static/common/js/dataTablesExt.js"></script>
	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script>
		$(document).ready(function() {
			var tableId = "sysLogTable";
			var config = {
				searching:false,
				searchInline: true,
				rowId: 'filePathEncode',
				orderFixed: [[0, 'asc', 'typeLevel']],
				order: [[3, 'desc', 'lastModified']],
				"columns" : [
					{
						name : "typeLevel",
						title : "类型",
						data : "typeLevel",
						visible: false,
						sortable: true,
					},
					{
						name : "fileName",
						title : "名称",
						data : "fileName",
						visible: true,
						sortable: true,
						render: function(data, type, row) {
							var icon = row.type == 'dir' ? '<i class="fa fa-folder-open-o"></i>' : '<i class="fa fa-file-o"></i>';
							return icon + " <span>" + data + "</span>";
						}
					},
					{
						name : "filePath",
						title : "路径",
						data : "filePath",
						visible: true,
						sortable: true
					},{
						name : "fileSize",
						title : "大小",
						data : "fileSize",
						visible: true,
						sortable: true,
						render: function(data, type, row) {
						    var units = ["B", "KB", "MB", "GB", "TB"];
						    var size = Number(data) || 0;
						    var unitSize = 1024;
						    var loop = parseInt(Math.log(size)/Math.log(unitSize)) || 0, 
						    	unit = units[loop];
						    while (loop--) {
						        size = (size / unitSize).toFixed(2);
						    }
						    return size + unit;
						}
					},
					{
						name : "lastModified",
						title : "操作时间",
						data : "lastModified",
						visible: true,
						sortable: true,
						render: function(data, type, row) {
							return formatDate(data, "yyyy-MM-dd HH:mm:ss");
						}
					}],
				"columnDefs" : [ {
	                // 定义操作列,######以下是重点########
	                targets : 4,//操作按钮目标列
	                data : "type",
	                title: "操作",
	                sortable: false,
	                render : function(data, type, row) {
	                	var filePath = row.filePathEncode;
	                	var html = "";
	                	if (data == "dir") {
	                   		html += "<a class='btn btn-xs btn-success' href='javascript:void(0)' data-btn-type='open'><i class='icon-ok'></i>打开</a>"
	                	} else {
	                	}
                		html += "<a class='btn btn-xs btn-success' href='javascript:void(0)' data-btn-type='download'><i class='icon-ok'></i>下载</a>"
	                    return html;
	                }
	            } ]
			};
			var commonTable = null;
			var cacheData = {};
			function openDisk(path, isBack) {
				if (cacheData[path]) {
					initTableData(path, cacheData[path], isBack);
				} else {
					ajaxPost("${pageContext.request.contextPath}/sys/syslog/disk.json", {path: path}, function(data) {
						initTableData(path, data.data, isBack);
						cacheData[path] = data.data;
					})
				}
			}
			function initTableData(path, data, isBack) {
				if (commonTable == null) {
					commonTable = new CommonLocalTable(tableId, data, config);
				} else {
					commonTable.reloadData(data);
					var searchPath = location.search || "";
					var newPath = '?path=' + path;
					if (!isBack && newPath != searchPath) {
						history.pushState({path:path}, '', newPath);
					}
				}
			}
			// var commonTable= new CommonTable(tableId, "${pageContext.request.contextPath}/sys/syslog/disk.json?path=${path}", "searchDiv", config);
			openDisk("${path}");
			
			function download(paths) {
				paths = paths || [];
				var path = paths.join(";");
				var url = "${pageContext.request.contextPath}/sys/syslog/download.json";
				/* ajaxPost("${pageContext.request.contextPath}/sys/syslog/download.json", {path: path}, function(data) {
					initTableData(path, data.data, isBack);
					cacheData[path] = data.data;
				}) */
				postDownload(url, {path: path});
			}
			
			var preTargger = "";
            $(document).off('click', "#" + tableId  + '_wrapper [data-btn-type]');
            $(document).on('click', "#" + tableId  + '_wrapper [data-btn-type]', function() {
                var action = $(this).attr('data-btn-type');
                var rowId= commonTable.getSelectedRowId();
                switch (action) {
                case 'open':
                	if(!rowId){
                        modals.info('请选择要编辑的行');
                        return false;
                    }
                	openDisk(rowId);
                    break;
                case 'download':
                    if(!rowId){
                        modals.info('请选择要编辑的行');
                        return false;
                    }
                    download([rowId]);
                    break;
                case 'downloadBatch':
                	var rowIds= commonTable.getCheckedRowIds();
                    if(!rowIds){
                        modals.info('请选择要编辑的行');
                        return false;
                    }
                    download(rowIds);
                    break;
                }
            });

            $(document).off("dblclick", "#" + tableId  + " tbody tr");
            $(document).on("dblclick", "#" + tableId  + " tbody tr", function () {
                var rowId = commonTable.getSelectedRowId();
                if(rowId == null){
                    modals.info('请点击需要查看的行');
                    return false;
                }
                $(this).find("[data-btn-type]:first").click();
            });
            
            var prevState = {};
            window.addEventListener('popstate', function(e) {
            	var state = e.state || {};
           	    openDisk(state.path || "", true || (state.path || "") == (prevState.path || ""));
            	prevState = history.state;
           	});
		});
	</script>
</jsTag>
</html>