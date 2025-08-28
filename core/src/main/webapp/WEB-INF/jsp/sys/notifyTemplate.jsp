<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
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
		<h1>模板管理</h1>
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
                                <!-- <button type="button" class="btn btn-default" data-btn-type="edit">编辑</button>
                                <button type="button" class="btn btn-default" data-btn-type="delete">删除</button> -->
                            </div>
                        </div>
						<table id="notifyTemplateTable"
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
	    var notifyTemplateTable;
	    var winId = "notifyTemplateWin";
		$(document).ready(function() {
			notifyTemplateTable = new CommonTable("notifyTemplateTable", "${pageContext.request.contextPath}/sys/notifyTemplate/list.json", "searchDiv",{
				"columnDefs" : [ {
                    // 定义操作列,######以下是重点########
                    targets : 4,//操作按钮目标列
                    data : "id",
                    title: "操作",
                    sortable: false,
                    render : function(data, type, row) {
                        var id = '"' + row.id
                                + '"';
                        //<a href='javascript:void(0);'  class='delete btn btn-default btn-xs'  ><i class='fa fa-times'></i> 查看</a>
                        var html = "<a class='btn btn-xs btn-success' href='notifyTemplate/"+data+".html'><i class='icon-ok'></i>查看</a>";
                        //html += "<a class='btn btn-xs btn-warning'  href='${pageContext.request.contextPath}/sys/user/"+data+".html'><i class='icon-pencil'></i>编辑</a>"
                        html += "<a class='btn btn-xs btn-danger' href='javascript:del(\"sys/notifyTemplate\", " + data + ")'><i class='icon-remove'></i>删除</a>";
                        return html;
                    }
                }]
			});
			//button event   
            $('button[data-btn-type]').click(function() {
                var action = $(this).attr('data-btn-type');
                var rowId = notifyTemplateTable.getSelectedRowId();
                switch (action) {
                case 'add':
                	window.location.href = basePath+"/sys/notifyTemplate/detail.html";
                      /*  modals.openWin({
                        winId:winId,
                        title:'新增通知模板',
                        width:'1000px',
                        url:basePath+"/sys/modals/notifyTemplate_detail"
                       });    */         
                    break;
                case 'edit':
                    if(!rowId){
                        modals.info('请选择要编辑的行');
                        return false;
                    }
                    window.location.href = basePath+"/sys/notifyTemplate/"+rowId+".html";
                    /* modals.openWin({
                        winId:winId,
                        title:'编辑模板【'+notifyTemplateTable.getSelectedRowData().templateCode+'】',
                        width:'1000px',
                        url:basePath+"/sys/modals/notifyTemplate_detail?id="+rowId
                    });  */
                    break;
                case 'delete':
                    if(!rowId){
                        modals.info('请选择要删除的行');
                        return false;
                    }
                    modals.confirm("是否要删除该行数据？",function(){
                        ajaxPost(basePath+"/sys/notifyTemplate/"+rowId+".json?_method=DELETE",null,function(data,status){
                            if(status == "success"){
                                //modals.correct("已删除该数据");
                                notifyTemplateTable.reloadData();
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
		
		function templateStatusRender(data, type, row) {
			var effectiveTo = row.effectiveTo;
			var date = new Date().getTime();
			var text = "";
			if (data <= date && (!effectiveTo || effectiveTo > date)) {
				text = "已生效";
			} else if (data > date) {
				text = "待生效";
			} else {
				text = "已失效";
			}
			return text;
		}
		
		function del(namepace, rowId) {
			modals.confirm("是否要删除该行数据？",function(){
                ajaxPost(basePath+"/" + namepace + "/"+rowId+".json?_method=DELETE",null,function(data,status){
                    if(status == "success"){
                        modals.correct("已删除该数据!");
                        notifyTemplateTable.reloadData();
                    }else{  
                        //setTimeout(function(){modals.info(data.message)},2000);
                        modals.error(data); 
                    }
                });  
            })
		}
	</script>
</jsTag>
</html>