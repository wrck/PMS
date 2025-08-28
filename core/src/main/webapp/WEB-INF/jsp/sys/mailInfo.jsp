<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
			            <div id="searchDiv" >
			                <div class="form-group">
			                    <label>搜索：</label>
                                <input type="search" name="fuzzy" class="form-control" placeholder="主题、内容、邮箱地址" id="fuzzySearch" style="min-width: 250px;">
			            	</div>
			            	<div class="form-group">
                                <label>邮箱类型：</label>
	                            <select class="form-control" id="isInner-search" name="isInner" type="search">
	                                <option value="">全部</option>
	                                <option value="0">外网邮箱</option>
	                                <option value="1">内网邮箱</option>
	                            </select>
                            </div>
			            	<div class="form-group">
                                <label>发送状态：</label>
                                <select class="form-control" id="sendFlag-search" name="sendFlag" type="search">
                                    <option value="">全部</option>
                                    <option value="0">未发送</option>
                                    <option value="1">已发送</option>
                                </select>
                            </div>
			                <div class="btn-group">
								<button type="button" class="btn btn-primary" data-btn-type="search">查询</button>
								<button type="button" class="btn btn-default" data-btn-type="reset">重置</button>
							</div>
						</div>
						<table id="mailInfoTable"
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
	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></dp:script> --%>
	<dp:script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></dp:script>
	<dp:script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></dp:script>
	<script>
		var commonTable;
		$(document).ready(function() {
			commonTable = new CommonTable("mailInfoTable", "mailInfo/list.json", "searchDiv",
					{
					searching: false,
					searchInline: true,
					rowId: 'id',
					"columns" : [
						{
						    title : "ID",
						    data : "id",
						    visible: false,
						    sortable: true,
						},
                        {
                            title : "主题",
                            data : "subject",
                            visible: true,
                            sortable: true,
                        },
						{
							title : "内容",
							data : "content",
							visible: true,
							sortable: true
						},
                        {
                            title : "收件人",
                            data : "tos",
                            visible: true,
                            sortable: true,
                            render : wrapperEmailAddress
                        },
                        {
                            title : "抄送",
                            data : "ccs",
                            visible: true,
                            sortable: true,
                            render : wrapperEmailAddress
                        },
                        {
                            title : "密送",
                            data : "bccs",
                            visible: true,
                            sortable: true,
                            render : wrapperEmailAddress
                        },
                        {
                            title : "实际地址",
                            data : "actualSendAddress",
                            visible: true,
                            sortable: true,
                            render : wrapperEmailAddress
                        },
						{
							title : "邮箱类型",
							data : "isInner",
							visible: true,
							sortable: true,
							render : function(data, type, row) {
                            	if (data) {
                            		return "内网";
                            	}
                            	return "外网";
                            }
						},
						{
							title : "预期时间",
							data : "expectSendTime",
							visible: true,
							sortable: true,
						},
						{
							title : "实际时间",
							data : "sendTime",
							visible: true,
							sortable: true,
						},
						{
							title : "失败次数",
							data : "failedCount",
							visible: true,
							sortable: true,
						},
						{
							title : "发送状态",
							data : "sendFlag",
							visible: true,
							sortable: true,
							render : function(data, type, row) {
                            	if (data) {
                            		return "已发送";
                            	}
                            	return "未发送";
                            }
						}
						],
					"columnDefs" : [ {
		                // 定义操作列,######以下是重点########
		                targets : 12,//操作按钮目标列
		                data : "id",
		                title: "操作",
		                sortable: false,
		                render : function(data, type, row) {
		                    var id = '"' + row.id + '"';
		                    //<a href='javascript:void(0);'  class='delete btn btn-default btn-xs'  ><i class='fa fa-times'></i> 查看</a>
		                    var html = "<a class='btn btn-xs btn-success' href='mailInfo/"+data+".html'><i class='fa fa-eye'></i> 查看</a>";
		                    if (row.sendFlag == false && row.expectSendTime && row.failedCount < 3) {
		                    	html += "<a class='btn btn-xs btn-danger mt-1 ml-1'  href='javascript:invalidMail("+data+")'><i class='fa fa-close'></i> 失效</a>"
		                    	html += "<a class='btn btn-xs btn-info mt-1 ml-1 btn-send'  href='javascript:sendMail("+data+")'><i class='fa fa-envelope'></i> 发送</a>"
		                    }
		                    //html += "<a class='btn btn-xs btn-warning'  href='${pageContext.request.contextPath}/sys/user/"+data+".html'><i class='icon-pencil'></i>编辑</a>"
		                    return html;
		                }
		            } ]
				});
		});
		
		function invalidMail(id) {
			if (id != "0") {
            	ajaxPost(basePath + "/sys/mailInfo/invalid.json", {id: id}, function(data) {
            		if (data.status) {
            			modals.info("操作成功！");
	           			commonTable.reloadRowData();
            		} else {
            			modals.error(data.message);
            		}
            	})
			}
		}
		
		function sendMail(id) {
			if (id != "0") {
				$(".btn-send").button("loading");
            	ajaxPost(basePath + "/sys/mailInfo/send.json", {id: id}, function(data) {
            		if (data.status) {
            			modals.info("发送成功！");
            		} else {
            			modals.error(data.message || "发送失败!");
            		}
           			commonTable.reloadRowData();
            	})
			}
		}
		
		function wrapperEmailAddress(data, type, row) {
			if (data) {
        		data = data.replace(/;/g, "; ");
        		return data.replace(/,/g, ", ");
        	}
        	return data;
		}
	</script>
</jsTag>
</html>