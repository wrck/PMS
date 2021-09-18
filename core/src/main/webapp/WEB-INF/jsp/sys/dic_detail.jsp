<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="system.title" /></title>
<cssTag>
	<!-- DataTables -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css"/>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datepicker/datepicker3.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css">
</cssTag>
</head>
<body>
	<!-- Content Header (Page header) -->
	<section class="content-header">
	    <h1>
	        <span>字典管理</span>
	        <small>
	        	<c:if test="${dic == null}">
	        		新增
	        	</c:if>
	        	<c:if test="${dic != null}">
	        		修改
	        	</c:if>
	        </small>
	    </h1>
	    <ol class="breadcrumb">
	        <li><a href="#"><i class="fa fa-dashboard"></i> 首页</a></li>
	        <li><a href="#">系统管理</a></li>
	        <li class="active">字典管理</li>
	    </ol>
	</section>
	<section class="content">
	    <div class="row">
	        <div class="col-xs-12">
	            <div class="box box-info">
		            <form id="dic-form" name="dic-form" class="form-horizontal">
		            	<div class="box-body">
		                    <div class="col-md-6">
		                        <div class="form-group">
		                            <label for="dicTypeName" class="col-sm-3 control-label">字典类型</label>
		                            <div class="col-sm-8">
		                                <input type="text" class="form-control" id="dicTypeName" name="dicTypeName" placeholder="字典类型">
		                            </div>
		                        </div>
		                        <div class="form-group">
		                            <label for="dicKey" class="col-sm-3 control-label">字典编码</label>
		                            <div class="col-sm-8">
		                                <input type="text" class="form-control" id="dicKey" name="dicKey" placeholder="字典编码">
		                            </div>
		                        </div>
		                        <div class="form-group">
		                            <label for="dicValue" class="col-sm-3 control-label">字典值</label>
		                            <div class="col-sm-8">
		                                <input type="text" class="form-control" id="dicValue" name="dicValue" placeholder="字典值">
		                            </div>
		                        </div>
		                        <div class="form-group">
		                            <label for="status" class="col-sm-3 control-label">有效状态</label>
		                            &nbsp;&nbsp;&nbsp;&nbsp;
		                            <div class="col-sm-8">
										<label class="control-label"> 
											<input type="radio" name="status" checked="checked" data-flag="icheck" class="flat-red" value="1"> 有效
										</label>
										<label class="control-label">
											<input type="radio" name="status" data-flag="icheck"class="flat-red" value="0"> 失效 
										</label>
									</div>
		                        </div>
		                    </div>
		                </div>
		                <div class="box-footer text-center">
		                    <!--以下两种方式提交验证,根据所需选择-->
		                    <button type="button" class="btn btn-default" data-btn-type="cancel" >取消 </button>
		                    <button type="submit" class="btn btn-primary" data-btn-type="save">提交</button>
		                </div>
		                <!-- /.box-footer -->
		            </form>
	            </div>
	        </div>
	    </div>
	</section>
</body>
<jsTag>
	<script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/datepicker/bootstrap-datepicker.js"></script>
	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script>
	    //tableId,queryId,conditionContainer
	    var form = null;
	    var basePath = "${pageContext.request.contextPath}";
	   // var id = "{id?default(0)}";
	     var id = "${id!=0 && id!=null?id:0}";
	    $(function () {
	        //数据校验
	        $("#dic-form").bootstrapValidator({
	            message: '请输入有效值',
	            feedbackIcons: {
	                valid: 'glyphicon glyphicon-ok',
	                invalid: 'glyphicon glyphicon-remove',
	                validating: 'glyphicon glyphicon-refresh'
	            },
	            submitHandler: function (validator, userform, submitButton) {
	                modals.confirm('确认保存？', function () {
	                    //Save Data，对应'submit-提交'
	                    var params = form.getFormSimpleData();
	                    var url = '/sys/dictionary/detail.json';
	                    if(id !="0"){
	                    	url = '/sys/dictionary/'+id+'.json';
	                    }
	                    ajaxPost(basePath + url, params, function (data, status) {
 	                        if (status == 'success') {
 	                            if (id != "0") {//更新
 	                            	console.log("更新");
 	                            	//window.location.reload(true);
 	                                //gotolist(id);
 	                            } else {//新增
 	                            	console.log("新增");
 	                            	modals.info("数据保存成功");
									//gotolist();
								}
 	                            window.location.href = basePath + "/sys/dictionary.html";
							}
						});
					});
				},
				fields : {
					dicTypeName : {
						validators : {
							dicTypeName : {
								message : '请输入字典类型'
							}
						}
					},
					dicKey : {
						validators : {
							dicTypeName : {
								message : '请输入字典编码'
							}
						}
					},
					dicValue : {
						validators : {
							dicTypeName : {
								message : '请输入字典值'
							}
						}
					},
					status : {
						validators : {
							notEmpty : {
								message : '请选择状态'
							}
						}
					}
				}
			});
			//初始化控件
			form = $("#dic-form").form();
			//回填id
			if (id != "0") {
				$("#dic-form").prepend('<input type="hidden" name="_method" value="PUT">');
				ajaxPost(basePath + "/sys/dictionary/" + id + ".json", {
					id : id
				}, function(result) {
					var dic = result.dic;
					form.initFormData(dic);
				});
			}
			//cancel
			$("[data-btn-type='cancel']").click(function() {
				window.history.back();
			})
		});

		//function gotolist(id) {
		//	window.loadPage(basePath + "/sys/dictionary/page/list?id=" + id);
		//}

		function resetForm() {
			form.clearForm();
			$("#dic-form").data('bootstrapValidator').resetForm();
		}

	</script>
</jsTag>
</html>