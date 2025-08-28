<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib  prefix="dp" uri="/myTag" %>
<cssTag>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/select2/select2.min.css">
	<dp:link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css" />
	<style>
		#exportSearch *{
			display: inline-block;
		}
	</style>
</cssTag>
<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal"
		aria-hidden="true">
		<li class="fa fa-remove"></li>
	</button>
	<h5 class="modal-title">导出字段选择</h5>
</div>

<div class="modal-body">
	<div class="col-sm-12">
		<h6>查询参数</h6>
		<ul id="exportSearch" class="form-inline list-inline text-right">
			<c:forEach items="${columns}" var="item" varStatus="status">
				<li class='col-sm-3 mb-1'>
					<label class=" control-label">${item.value}:</lable>
					<input name="${item.key}" class="form-control" placeholder="${item.value}">
				</li>
			</c:forEach>
				${formHtml}
		</ul>
	</div>
	<form id="export-form" name="export-form" action="${pageContext.request.contextPath}/data/operation/${id}.xlsx" method="POST" class="form-inline">
		<input type="hidden" name="__RequestVerificationToken" value="${__RequestVerificationToken}">
		<input type="hidden" name="columns" value="">
		<input type="hidden" name="objectName" value="${objectName}">
		<input type="hidden" name="objectKV" value="${objectKV}">
		<input type="hidden" name="pageParamKV" value="${pageParamKV}">
		
		<%-- <div class="col-sm-12">
			<h6>查询参数</h6>
			<ul id="exportSearch" class="list-inline text-right">
				<c:forEach items="${columns}" var="item" varStatus="status">
					<li class='col-sm-3 mb-1'>
						<label class=" control-label">${item.value}:</lable>
						<input name="${item.key}" class="form-control" placeholder="${item.value}">
					</li>
				</c:forEach>
					${formHtml}
			</ul>
		</div> --%>
		
		<div class="text-center">
			<p>
				<label class="text-primary text-left display-inline-block">提示：<br>
					1、若无已选择字段，则导出全部字段 <br>
					2、Excel文件列按字段顺序导出
				</label>
				<!-- <label class="display-inline-block text-right text-primary" style="width:136px;">
					<span class="display-inline-block text-left ">
						Excel文件类型:<br>
						<label><input type="radio" name="suffix" value=".xls">.xls</label>
						<label class="ml-1"><input type="radio" name="suffix" value=".xlsx" checked="checked">.xlsx</label>
					</span>
				</label> -->
			</p>
			<fieldset class="display-inline-block text-left">
				<label>未选择字段</label>
				<select class="mr-1" id="exprotColumnsUnSelect" multiple="multiple">
					<c:forEach items="${columns}" var="item" varStatus="status">
						<option value="${item.key}" selected="selected">${item.value}</option>
					</c:forEach>
				</select>
			</fieldset>
			
			<div class="btn-group-vertical display-inline-block">
				<button type="button" class="btn btn-default" id="selectOne">></button>
				<button type="button" class="btn btn-default" id="selectAll">>></button>
				<button type="button" class="btn btn-default" id="unselectAll"><<</button>
				<button type="button" class="btn btn-default" id="unselectOne"><</button>
			</div>
			<fieldset class="display-inline-block  text-left">
				<label class="ml-1">已选择字段</label>
				<select class="ml-1" id="exprotColumnsSelected" multiple="multiple">
					<%-- <c:forEach items="${columns}" var="item">
						<option value="${item.key}" selected="selected">${item.value}</option>
					</c:forEach> --%>
				</select>
			</fieldset>
		</div>
	</form>
</div>
<div class="box-footer text-right">
	<button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">取消</button>
	<button type="button" class="btn btn-info" data-btn-type="preview">预览</button>
	<button type="submit" class="btn btn-primary" data-btn-type="save">导出</button>
</div>
<!-- /.box-footer -->
<jsTag> 
<script src="${pageContext.request.contextPath}/static/plugins/select2/select2.min.js"></script>
<%-- <dp:script src="${pageContext.request.contextPath}/static/common/js/base.js"></dp:script> --%>
<dp:script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></dp:script>
<dp:script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></dp:script>
<script>
	var objectName = "${objectName}"; //tableId,queryId,conditionContainer
	var keys = [];
	var values = [];
	var exportId = "${id}" || "0";
	var operationName = "${operationName}";
	$(function() {
		/* ajaxPost("selectExportColumns.json", {
			objectName : objectName
		}, function(data) {
			var columns = data.columns;
			for(var key in columns) {
				keys.push(key);
				values.push(columns[key]);
			}
			$("#exprotColumnsSelect").select2({
				width : '100%',
				data : values
			}); 
		});*/
		
		$(document).on("click","#export-form button[type='button'][id*='select']",function(){
			var id =  $(this).attr("id");
			if(id == "selectOne") {
				$("#exprotColumnsUnSelect option:selected").appendTo('#exprotColumnsSelected');	
			} else if(id == "selectAll") {
				$("#exprotColumnsUnSelect option").appendTo('#exprotColumnsSelected');
			} else if(id == "unselectAll") {
				$("#exprotColumnsSelected option").appendTo('#exprotColumnsUnSelect');
			} else {
				$("#exprotColumnsSelected option:selected").appendTo('#exprotColumnsUnSelect');
			}
		});

		$("#exportData").on("hide.bs.modal", function(){
			$("#orderStatusTable_processing").hide();
		});
		
		$(document).on("click","button[data-btn-type='preview']",function(){
			prevForm();
			showOperationForm("${id}", "preview");
		});
		
		$(document).on("click","button[type='submit']",function(){
			/* $("#exprotColumnsSelected option").attr("selected",true);
			var columns = [];
			$("#exprotColumnsSelected option").each(function() {
				var name = $.trim($(this).text());
				var value = $.trim(this.value);
				if (name && value) {
					columns.push(value + "=" + name);
				} else if (value) {
					columns.push(value);
				}
			});
			console.log(columns);
			if(columns) {
				$("#export-form input[name='columns']").val(columns.join(";"));
			}
			console.log($("#export-form input[name='columns']").val());
			
			var params = [];
			$("#exportSearch [name]").each(function() {
				var name = $.trim(this.name);
				var value = $.trim(this.value);
				if (name && value) {
					params.push(name + "=" + value);
				}
			})
			$("#export-form input[name='objectKV']").val(params.join(";")); */
			prevForm();
			var action = $.trim($("#export-form").attr("action"));
			var path = action.split(".");
			var suffix = $("#export-form input[name='suffix']:checked").val();
			if (suffix) {
				$("#export-form").attr("action", path[0] + suffix);
			}
			console.log($("#export-form").attr("action"));
			$("#export-form").submit();
			$("#orderStatusTable_processing").hide();
			modals.closeWin("export");
			showExportProcess(operationName);
			/* modals.info("数据导出中，请耐心等待！"); */
		});
		
		
		function prevForm() {
			$("#exprotColumnsSelected option").attr("selected",true);
			var columns = [];
			$("#exprotColumnsSelected option").each(function() {
				var name = $.trim($(this).text());
				var value = $.trim(this.value);
				if (name && value) {
					columns.push(value + "=" + name);
				} else if (value) {
					columns.push(value);
				}
			});
			console.log(columns);
			if(columns) {
				$("#export-form input[name='columns']").val(columns.join(";"));
			}
			console.log($("#export-form input[name='columns']").val());
			
			var params = [];
			$("#exportSearch [name]").each(function() {
				var name = $.trim(this.name);
				var value = $.trim(this.value);
				if (name && value) {
					params.push(name + "=" + value);
				}
			})
			$("#export-form input[name='objectKV']").val(params.join(";"));
			
			localStorage.setItem("export_preview_columns_" + exportId, $.trim($("#export-form input[name='columns']").val()));
			localStorage.setItem("export_preview_objectKV_" + exportId, $.trim($("#export-form input[name='objectKV']").val()));
		}
	});
</script> </jsTag>