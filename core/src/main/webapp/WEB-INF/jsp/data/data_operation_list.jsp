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
<cssTag> <!-- DataTables -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datatables/media/css/dataTables.bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datatables/extensions/Select/css/select.bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/plugins/select2/select2.min.css">
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
						<div id="searchDiv" class="text-right">
							<shiro:hasRole name="admin">
							<div class="btn-group operate-btn-group">
								<button type="button" class="btn btn-default"
									data-btn-type="add">新建</button>
								<button type="button" class="btn btn-default"
									data-btn-type="edit">编辑</button>
								<button type="button" class="btn btn-default"
									data-btn-type="copy">复制</button>
								<button type="button" class="btn btn-default"
									data-btn-type="delete">删除</button>
							</div>
							</shiro:hasRole>
						</div>
						<table id="dataOperationTable"
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

<script type="text/javascript" src="${pageContext.request.contextPath}/static/plugins/select2/select2.min.js"></script>

<script src="${pageContext.request.contextPath}/static/common/js/dataTablesExt.js"></script>
<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
<shiro:lacksRole name="admin">
<script>
	var config = {
		searching :true,
		rowId: 'id',
		'columns':[{
			data: 'name',
			title: '操作名',
			visible: true,
		}, {
			data: 'description',
			title: '操作描述',
			visible: true,
		}, {
			data: 'type',
			title: '操作类型',
			visible: true,
			render: function(data, type , row) {
				if (data == 1) {
					data = "<a class='btn btn-xs btn-success' href='javascript:showOperationForm(\"" + row.name + "\", " + data + ")'><i class='glyphicon glyphicon-import'></i>导入</a>"
				} else if (data == 0) {
					data = "<a class='btn btn-xs btn-info' href='javascript:showOperationForm(\"" + row.name + "\", " + data + ")'><i class='glyphicon glyphicon-export'></i>导出</a>"
				    data += "<a class='btn btn-xs btn-info ml-1' href='javascript:showOperationForm(\"" + row.id + "\", \"preview\")'><i class='glyphicon glyphicon-search'></i>预览</a>";
				}
				return data;
			}
		}]
	}
</script>
</shiro:lacksRole>
<shiro:hasRole name="admin">
<script>
	var config = {
		searching :true,
		rowId: 'id',
		'columns':[{
			data: 'name',
			title: '操作名',
			visible: true,
		}, {
			data: 'description',
			title: '操作描述',
			visible: true,
		}, {
			data: 'type',
			title: '操作类型',
			visible: true,
			render: function(data, type , row) {
				if (data == 1) {
					data = "<a class='btn btn-xs btn-success' href='javascript:showOperationForm(\"" + row.name + "\", " + data + ")'><i class='glyphicon glyphicon-import'></i>导入</a>";
				} else if (data == 0) {
					data = "<a class='btn btn-xs btn-info' href='javascript:showOperationForm(\"" + row.name + "\", " + data + ")'><i class='glyphicon glyphicon-export'></i>导出</a>";
					data += "<a class='btn btn-xs btn-info ml-1' href='javascript:showOperationForm(\"" + row.id + "\", \"preview\")'><i class='glyphicon glyphicon-search'></i>预览</a>";
				}
				return data;
			}
		}, {
			data: "clazz",
			title: "操作类",
			visible: true,
		}, {
			data: "method",
			title: "操作方法",
			visible: true,
		}]
	}
	
	$(function(){
		//button event   
        $('button[data-btn-type]').click(function() {
            var action = $(this).attr('data-btn-type');
            var rowId = dataOperationTable.getSelectedRowId();
            switch (action) {
            case 'add':
                   modals.openWin({
                    winId:winId,
                    title:'新建操作',
                    width:'800px',
                    url:basePath+"/sys/modals/dataOperation_detail.html"
                   });                        
                break;
            case 'edit':
                if(!rowId){
                    modals.info('请选择要编辑的行');
                    return false;
                }
                modals.openWin({
                    winId:winId,
                    title:'编辑操作【'+dataOperationTable.getSelectedRowData().name+'】',
                    width:'800px',
                    url:basePath+"/sys/modals/dataOperation_detail.html?id="+rowId
               }); 
               break;
            case 'copy':
                if(!rowId){
                    modals.info('请选择要复制的行');
                    return false;
                }
                modals.openWin({
                    winId:winId,
                    title:'通过复制新建操作',
                    width:'800px',
                    url:basePath+"/sys/modals/dataOperation_detail.html?id="+rowId+"&copyFlag=1"
               });
               break;
            case 'delete':
                if(!rowId){
                    modals.info('请选择要删除的行');
                    return false;
                }
                modals.confirm("是否要删除该行数据？",function(){
                    ajaxPost(basePath+"/data/"+rowId+".json?_method=DELETE",null,function(data,status){
                        if(status == "success"){
                            //modals.correct("已删除该数据");
                            dataOperationTable.reloadData();
                        }else{  
                            //setTimeout(function(){modals.info(data.message)},2000);
                            modals.info(data); 
                        }  
                    });
                })
                break;  
            }
        });
	})
</script>
</shiro:hasRole>
<script>
    var dataOperationTable;
    var winId = "dataOperationWin";
	$(document).ready(function() {
		dataOperationTable = new CommonTable("dataOperationTable", basePath + "/data/list.json", "searchDiv", config);
	});
	
	function showOperationForm(name, type) {
		if (type == "1") {
			modals.openWin({
				winId:'import',
				url:basePath + '/data/import.html?operationName='+name,
				width: '1080px'
			})
		} else if (type == "0") {
			modals.openWin({
				winId:'export',
				url:basePath + '/data/export.html?operationName='+name,
				width: '1080px'
			})
		} else if (type == 'preview'){
			modals.openWin({
				winId:'preview',
				url:basePath + '/data/export/preview/' + name + '.html',
				width: '80%'
			})
		} else {
			modals.error("操作类型不正确");
		}
	}
	
	var exportProgressInterval;
	var interval = 100;
	var flag = true;
	var trySecond = 2;
	var tryCount = trySecond * 1000 / interval;
	function showExportProcess(operationName) {
		flag = true;
		tryCount = trySecond * 1000 / interval;
		modals.openWin({
			winId: operationName,
			width: "600px",
			backdrop: "static",
			url: basePath+'/base/progress/' + operationName + '.json',
			showFunc: function() {
				$(".modal-content", "#" + operationName).css("cssText", "border-radius:1rem!important").html('<div class="progress progress-striped active mb-0" style="min-width:60px;border-radius: 1rem;">'+
						'<div class="progress-bar progress-bar-success" role="progressbar" style="width: 0%;">' +
						'</div>' +
					'</div>');
				exportProgressInterval = window.setInterval(getProgressBar, interval);
			}
		})
		// 防止json内容显示
	    $(".modal-content", "#" + operationName).css("color", "transparent");
	}
	
	function getProgressBar(container){
		ajaxPost(basePath+'/base/progress/' + operationName + '.json', null, function(data){
			var progress = data.progress;
			if(progress){
				flag = false;
				loadProgress(progress, container);
			}
			console.log(tryCount);
			if ((!flag && !progress) || (flag && tryCount-- < 0)) {
				loadProgress("100%", container);
				window.clearInterval(exportProgressInterval);
				modals.closeWin(operationName);
			}
		});
	}
	
	function loadProgress(progress, container) {
		if (container != undefined && $(container).length > 0) {
			$("div[role='progressbar']", $(container)).css("width", progress);
			$("div[role='progressbar']", $(container)).text(progress);
		} else {
			$("div[role='progressbar']").css("width", progress);
			$("div[role='progressbar']").text(progress);
		}
	}
</script>
</jsTag>
</html>