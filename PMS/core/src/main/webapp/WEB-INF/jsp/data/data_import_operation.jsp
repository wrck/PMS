<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<cssTag>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/static/plugins/datetimepicker/css/bootstrap-datetimepicker.min.css">
<style>
.datepicker {
	z-index: 1500 !important;
}
</style>
</cssTag>
<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal"
		aria-hidden="true">
		<li class="fa fa-remove"></li>
	</button>
	<h5 class="modal-title"></h5>
</div>

<div class="modal-body">
	<div class="box-body">
		<div class="col-sm-12">
			<div class="row">
				<div class="form-group clearfix">
					<div class="col-sm-2 text-right"><label class="control-label">请选择excel文件</label></div>
					<div class="col-sm-9">
						<input type="file" id="fileExcel" name="fileExcel" onchange="resetProgress();" style="display: block;float: left;">
						<div class="progress progress-striped active">
						    <div class="progress-bar progress-bar-success" role="progressbar" style="width: 0%;">
						    </div>
						</div>
					</div>
				</div>
			</div>
			<form id="importForm" name="importForm" class="form-horizontal" action="javaScript:void(0)">
				${formHtml}
			</form>
		</div>
	</div>
	<!-- /.box-body -->
	<div class="box-footer">
		<div class="col-sm-12">
			<div class="col-sm-3 col-sm-offset-9 text-right">
				<button class="btn btn-default" data-btn-type='cancel' data-dismiss="modal">取消</button>
				<button class="btn btn-primary start" data-btn-type='importFile'>确定导入</button>
				<button class="btn btn-primary" data-btn-type='continueImport' style="display: none;">继续导入</button>
				<button class="btn btn-warning" id='loading' style="display: none;">导入中...</button>
			</div>
		</div>
	</div>
	
	<div id="errorWin" class="modal fade in" aria-labelledby="myModalLabel" aria-hidden="true" style="display: none;z-index:1111;">
	    <div class="modal-dialog" style="width: 800px;">
	        <div class="modal-content">
                <div class="box box-success">
					<div class="modal-header box-header with-border">
					    <button type="button" class="close" data-dismiss="modal" aria-hidden="true"><li class="fa fa-remove"></li></button>
					    <h5 class="modal-title">错误信息</h5>
					</div>
					<div class="modal-body box-body">
		                <div class="overlay"><i class="fa fa-refresh fa-spin"></i></div>
			            <table id="errorTable" class="table table-bordered table-striped table-hover">
			            </table>
		            </div>
	            </div>
	        </div>
	    </div>
	</div>
	<!-- /.box-footer -->
</div>
<jsTag> 
<script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
<script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>
<script src="${pageContext.request.contextPath}/static/plugins/datetimepicker/js/bootstrap-datetimepicker.min.js"></script>
<script src="${pageContext.request.contextPath}/static/plugins/ajaxfileupload/ajaxfileupload.js"></script>
<script>
	$(function(){
		//按钮点击事件
		$("button[data-btn-type]").click(function(){
			var action = $(this).attr('data-btn-type');
			switch (action) {
            	case 'importFile':
            		if($("#fileExcel").val()==''){
            			modals.info('请选择要导入的excel。');
            		}else{
	            		modals.confirm('确定导入数据？',function(){
	            			$("button.start").hide();
	            			$("#loading").show();
	            			var progressInterval = window.setInterval(getImportProgress, 1000); 
	            			var ignoreError = $("input[name='ignoreError']:checked").val() || "false";
	            			$.ajaxFileUpload({
		                        url: basePath+'/data/import/${operationName}', //用于文件上传的服务器端请求地址
		                        data: importForm.getFormSimpleData(),
		                        secureuri: false, //是否需要安全协议，一般设置为false
		                        fileElementId: 'fileExcel', //文件上传域的ID
		                        dataType: 'text', //返回值类型 一般设置为json 为了兼容IE，设为text（后台需要调整response 的 contentType）
		                        success: function (repObj, status)  //服务器成功响应处理函数
		                        {
		                        	repObj = repObj.replace(/<pre.*?>/g, ''); // ajaxFileUpload会对服务器响应回来的text内容加上<pre style="....">text</pre>前后缀
									repObj = repObj.replace(/<PRE.*?>/g, '');
									repObj = repObj.replace("<PRE>", '');
									repObj = repObj.replace("</PRE>", '');
									repObj = repObj.replace("<pre>", '');
									repObj = repObj.replace("</pre>", '');
									var data = $.parseJSON(repObj);
									//console.log(data);
	                               	$("#loading").hide();
                                	window.clearInterval(progressInterval);
	                                if (data.errorMessage != '') {
	                                	try {
	                                		var errors = JSON.parse(data.errorMessage);
	                                		if (errorTable.table) {
	                        			    	errorTable.table.destroy();
	                        			    	$("#" + errorTable.tableId).html("");
	                        			    	$("#errorWin .overlay").show();
	                        			    }
	                        			    var view_config = {
                        			    		"columns" : [
                        			 					    {
                        			 					        "data": "message",
                        			 					        "title": "错误",
                        			 					        "visible": true,
                        			 					        "sortable": true,
                        			 					    },
                        			 					    {
                        			 					        "data": "rowIndex",
                        			 					        "title": "行号",
                        			 					        "visible": true,
                        			 					        "sortable": true,
                        			 					    },
                        			 					    {
                        			 					        "data": "cellIndex",
                        			 					        "title": "列号",
                        			 					        "visible": true,
                        			 					        "sortable": true,
                        			 					    },
                        			 					    {
                        			 					        "data": "progress",
                        			 					        "title": "进度",
                        			 					        "visible": true,
                        			 					        "sortable": true,
                        			 					    }
                        			 					],
	                        			    	paging: false,
	                        			    	initCallback: function() {
	                        			    		$("#errorWin .overlay").hide();
	                        			    	}
	                        			    };
                        			    	errorTable = new CommonLocalTable("errorTable", errors, view_config);
                        			    	$('#errorWin').modal({
                        			    		keyboard: false,
                        			    		backdrop: false,
                        			    		show: false
                        			    	})
                        			    	modals.showWin("errorWin");
	                                	} catch(e) {
	                                		console.log(e);
	                                		modals.info(data.errorMessage);
	                                	}
	                                	$("div[role='progressbar']").removeClass("progress-bar-success");
		                               	if (ignoreError == "true") {
			                               	$("div[role='progressbar']").addClass("progress-bar-warning");
			                               	loadProgress("100%");
		                               	} else {
			                               	clearFile("fileExcel");
			                               	$("div[role='progressbar']").addClass("progress-bar-danger");
			                               	loadProgress(data.progress);
		                               	}
		                                $("button.start").show();
	                                } else {
	                                	loadProgress("100%");
	                                	modals.info("导入成功！");
		                                $("button[data-btn-type='continueImport']").show();
	                                }
		                        },
		                        error: function (data, status, e)//服务器响应失败处理函数
		                        {
		                        	window.clearInterval(progressInterval);
		                        	modals.info(e);
		                        }
		                    }); 
	            		});
            		}
                break;
            	case 'continueImport':
            		 $("button[data-btn-type='continueImport']").hide();
            		 $("button.start").show();
            		 clearFile("fileExcel");
            		 resetProgress();
			}
		});
		
		var importForm = $("#importForm").form();
	})
	function getImportProgress(){
		ajaxPost(basePath+'/perf/download/progress/${operationName}.json',null,function(data){
			if(data.progress!=null && data.progress!=''){
				loadProgress(data.progress);
			}
		}, false);
	}
	function clearFile(fileId){
		var file = $("#"+fileId); 
		file.after(file.clone().val("")); 
		file.remove(); 
	}
	function resetProgress(){
       	$("div[role='progressbar']").removeClass("progress-bar-danger progress-bar-warning");
	 	$("div[role='progressbar']").addClass("progress-bar-success");
	 	loadProgress(0);
	 	$("button[data-btn-type='continueImport']").hide();
	 	$("button#loading").hide();
        $("button.start").show();
	}
	function loadProgress(progress){
		$("div[role='progressbar']").css("width", progress);
        $("div[role='progressbar']").text(progress);
	}
</script> 
</jsTag>