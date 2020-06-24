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
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datetimepicker/css/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/plugins/summernote/dist/summernote.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/select2/select2.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css">
    <style>
        .datepicker {
            z-index: 1500!important;
        }
        #columns-alias {
        	margin-bottom: 5px;
        }
        .column-alias {
        	height: auto;
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
			<form id="dataOperation-form" name="dataOperation-form" class="form-horizontal">
				<input type="hidden" name="id" id="id">
				<div class="box-body">
					<div class="col-md-12">
						<div class="form-group">
							<label for="name" class="col-sm-3 control-label">操作名</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="name" name="name" data-flag="icheck" placeholder="操作名">
							</div>
						</div>
						<div class="form-group">
							<label for="description" class="col-sm-3 control-label">操作描述</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="description" name="description" data-flag="icheck" placeholder="操作描述">
							</div>
						</div>
						<div class="form-group">
							<label for="type" class="col-sm-3 control-label">操作类型</label>
							<div class="col-sm-8 ">
								<label class="control-label"><input type="radio" name="type" data-flag="icheck" value="1"> 导入</label>
								<label class="control-label"><input type="radio" name="type" data-flag="icheck" value="0"> 导出</label>
							</div>
						</div>
						<div class="form-group import">
	                        <label for="clazz" class="col-sm-3 control-label">操作所在类</label>
	                        <div class="col-sm-8">
	                            <input type="text" class="form-control" id="clazz" name="clazz" data-flag="icheck" placeholder="请输入操作所在类/BeanName">
	                        </div>
	                    </div>
	                    <div class="form-group import">
	                        <label for="method" class="col-sm-3 control-label">操作类的方法</label>
	                        <div class="col-sm-8">
	                            <input type="text" class="form-control" id="method" name="method" data-flag="icheck" placeholder="请输入操作类的方法">
	                        </div>
	                    </div>
	                    <div class="form-group import">
	                        <label for="method" class="col-sm-3 control-label">操作方法的参数</label>
	                        <div class="col-sm-8">
	                            <input type="text" class="form-control" id="parameterTypes" name="parameterTypes" data-flag="icheck" placeholder="操作方法的参数">
	                        </div>
	                    </div>
	                    <div class="form-group import">
	                        <label for="formHtml" class="col-sm-3 control-label">额外表单内容</label>
	                        <div class="col-sm-8">
	                            <textarea class="form-control" id="formHtml" name="formHtml" data-flag="summernote" placeholder="额外表单内容" style="resize: vertical;"></textarea>
	                        </div>
	                    </div>
	                    <div class="form-group export">
	                        <label for="script" class="col-sm-3 control-label">脚本</label>
	                        <div class="col-sm-8">
	                            <textarea class="form-control" id="script" name="script" data-flag="summernote" placeholder="导入JS/导出SQL" style="resize: vertical;"></textarea>
	                        </div>
	                    </div>
	                    <div class="form-group export">
	                        <label for="columns" class="col-sm-3 control-label">导出列名<br><a href='javascript:void(0)' id="queryExportColumns" class="" title="点击查询SQL包含的字段名">(关联列)</a></label>
	                        <div class="col-sm-8">
	                        	<ul id="columns-alias" class="list-inline">
	                        		<li class="columns-tip">点击“关联列”查询SQL包含的字段名</li>
	                        	</ul>
	                        	<input type="hidden" name="columns">
	                        </div>
	                    </div>
	                    <div class="form-group">
	                        <label for="empPower" class="col-sm-3 control-label">员工权限</label>
	                        <div class="col-sm-8">
	                            <select class="form-control select2" style="width:100%" id="empPower" multiple="multiple" name="empPower" data-flag="urlSelector" data-src="/sys/user/param.json?menuName=数据管理&compID=<shiro:principal property='compId' defaultValue='0'/>" data-text="userName realName" data-value="userInfoId" placeholder="请选择员工权限"></select>
	                        </div>
	                    </div>
	                    <div class="form-group">
	                        <label for="depPower" class="col-sm-3 control-label">部门权限</label>
	                        <div class="col-sm-8">
	                            <select class="form-control" id="depPower" name="depPower" data-flag="select2" placeholder="请选择部门权限"></select>
	                        </div>
	                    </div>
						<div class="form-group">
							<label for="effectiveFrom" class="col-sm-3 control-label">生效时间</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="effectiveFrom" name="effectiveFrom" data-flag="datetimepicker" data-format="yyyy-MM-dd HH:mm:ss" placeholder="生效时间">
							</div>
						</div>
						<div class="form-group">
							<label for="effectiveTo" class="col-sm-3 control-label">失效时间</label>
							<div class="col-sm-8">
	                            <input type="text" class="form-control" id="effectiveTo" name="effectiveTo" data-flag="datetimepicker" data-format="yyyy-MM-dd HH:mm:ss" placeholder="失效时间">
							</div>
						</div>
						<div class="form-group">
							<label for="state" class="col-sm-3 control-label ">状态</label>
							<div class="col-sm-8">
								<label class="control-label"><input type="radio" name="state" data-flag="icheck" value="1"> 有效 </label>
								 &nbsp;&nbsp;&nbsp; 
								<label class="control-label"><input type="radio" name="state" data-flag="icheck" value="0"> 失效 </label>
							</div>
						</div>
					</div>
				</div>
				<!-- /.box-body -->
				<div class="box-footer text-right">
					<button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">取消</button>
					<button type="submit" class="btn btn-primary" data-btn-type="save">提交</button>
				</div>
				<!-- /.box-footer -->
			</form>
		</div>
	</section>
</body>
<jsTag>
    <script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/datetimepicker/js/bootstrap-datetimepicker.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/plugins/summernote/dist/summernote.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/plugins/summernote/dist/lang/summernote-zh-CN.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/plugins/summernote/summernote-util.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/select2/select2.min.js"></script>
	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script>
		//tableId,queryId,conditionContainer
		var form = null;
	 	var id = "${id!=0 && id!=null?id:0}";
	 	var copyFlag = "${pageContext.request.getParameter('copyFlag')}";
		$(function() {
			//数据校验
			$("#dataOperation-form").bootstrapValidator({
				message : '请输入有效值',
				feedbackIcons : {
					valid : 'glyphicon glyphicon-ok',
					invalid : 'glyphicon glyphicon-remove',
					validating : 'glyphicon glyphicon-refresh'
				},
				submitHandler : function(validator,dataOperationform, submitButton) {
					modals.confirm('确认保存？', function() {
						//Save Data，对应'submit-提交'
						if ($("input[name='type']:checked").val() == "0") {
							columnStringify();
						}
						var params = form.getFormSimpleData();
						var path = '/data/';
	                    if(id !="0"){
	                    	path += id+'.json';
	                    }else{
	                    	path += 'detail.json';
	                    }
						ajaxPost(basePath + path, params, function(data, status) {
							modals.closeWin(winId); 
							if(status == 'success'){
								if(id!="0"){//更新
									modals.info("更新成功！");
									dataOperationTable.reloadRowData(id); 
								}else{//新增 
									 modals.info("保存成功!");
									dataOperationTable.reloadData(); 
								}
							}
						});
					});
				},
				fields : {
					name : {
						validators : {
							notEmpty : {
								message : '请输入操作名'
							}
						}
					},
					type :{
						validators : {
							notEmpty : {
								message : '请选择操作类型'
							},
						}
					},
					clazz :{
						validators : {
							notEmpty : {
								message : '请输入操作类'
							},
						}
					},
					method :{
						validators : {
							notEmpty : {
								message : '请输入操作方法'
							},
						}
					},
					effectiveFrom : {
						validators : {
							notEmpty : {
								message : '请选择生效时间'
							}
						}
					},
					state :{
						validators : {
							notEmpty : {
								message : '请选择状态'
							},
						}
					},
				}
			});
			//初始化控件
			form=$("#dataOperation-form").form();
			//回填id		
			if(id!="0"){
				ajaxPost(basePath+"/data/"+id+".json", null, function(data){
					form.initFormData(data.data);
					columnParse(data.data.columns);
				})
				if (copyFlag != "1") {
					$("#dataOperation-form").prepend('<input type="hidden" name="_method" value="PUT">');
				} else {
					id = 0;
				}
			}
			
			$("input[name='type']").on('ifChecked', function(event){  
				var type = $(this).val();
				if (type == 1) {
					$(".import").show();
					$(".export").hide();
				}
				if (type == 0) {
					$(".import").hide();
					$(".export").show();
				}
			});
			
			var flag = true;
			$("#queryExportColumns").click(function(){
				if (flag) {
					flag = false;
					var sql = $("#script").val();
					ajaxPost(basePath + "/data/export/queryExportColumns.json", {sql: sql}, function(data) {
						columnParse(data.columns);
						/* var columns = data.columns || [];
						//$("#columns-alias").html("");
						$("#columns-alias ." + relatedClass).removeClass("relatedClass");
						for (var i = 0; i < columns.length; i++) {
							var column = columns[i];
							if ($("#alias-title-" + column).length == 0) {
								$("#columns-alias").append("<li class='col-sm-3 " + relatedClass + "'><span class='column' id='alias-title-"+ column +"'>" + column + "</span><input class='column-alias form-control' placeholder='" + column + "别名'></li>");
							} else {
								$("#alias-title-" + column).parent().addClass(relatedClass);
							}
						}
						$("#columns-alias li").not("." + relatedClass).remove(); */
					}, false);
					flag = true;
				}
			});
		});
		
		function columnParse(columns) {
			var relatedClass = "column-related";
			if (typeof columns == "string") {
				columns = columns.split(";");
			}
			columns = columns || [];
			//$("#columns-alias").html("");
			$("#columns-alias ." + relatedClass).removeClass(relatedClass);
			for (var i = 0; i < columns.length; i++) {
				var column = columns[i];
				var kv = column.split("=");
				var alias = "";
				if (kv.length == 2) {
					column = kv[0];
					alias = kv[1];
					if (column == alias) {
						alias = "";
					}
				} else {
					column = kv[0];
				}
				// 防止特殊字符报错
				var encodeColumn = encodeURIComponent(column).replace(/%/g, "");
				if ($("#alias-title-" + encodeColumn).length == 0) {
					if ($("#columns-alias li:eq("+ i +")").length == 1) {
						$("#columns-alias li:eq("+ i +")").before("<li class='col-sm-3 " + relatedClass + "'><span class='column' id='alias-title-"+ encodeColumn +"'>" + column + "</span><input class='column-alias form-control' placeholder='" + column + "别名' value='" + alias + "'></li>");
					} else {
						$("#columns-alias li:eq("+ (i - 1) +")").after("<li class='col-sm-3 " + relatedClass + "'><span class='column' id='alias-title-"+ encodeColumn +"'>" + column + "</span><input class='column-alias form-control' placeholder='" + column + "别名' value='" + alias + "'></li>");
					}
					//$("#columns-alias").append("<li class='col-sm-3 " + relatedClass + "'><span class='column' id='alias-title-"+ column +"'>" + column + "</span><input class='column-alias form-control' placeholder='" + column + "别名' value='" + alias + "'></li>");
				} else {
					var $li = $("#alias-title-" + encodeColumn).parent();
					$li.addClass(relatedClass);
					if ($li.index() != i) {
						$li.insertBefore($("#columns-alias li:eq("+ i +")"));
						console.log(i);
					}
				}
			}
			$("#columns-alias li").not("." + relatedClass).remove();
		}
		
		function columnStringify() {
			var stringify = [];
			$("input.column-alias").each(function() {
				var columnName = $(this).prev().text();
				var alias = this.value || columnName;
				stringify.push(columnName + "=" + alias);
			})
			$("input[name='columns']").val(stringify.join(";"));
		}
		function resetForm(){
			form.clearForm();
	        $("#dataOperation-form").data('bootstrapValidator').resetForm();
		}
	</script>
</jsTag>
</html>