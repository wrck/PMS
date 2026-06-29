<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib  prefix="myTag" uri="/myTag" %>	
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-multiselect/css/bootstrap-multiselect.css">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true"><li class="fa fa-remove"></li></button>
		<h5 class="modal-title">导入数据</h5>
	</div>
	<div class="modal-body">
		<form id="importform" method="post" name="importform" class="form-horizontal">
			
			<div class="box-body">
				<div class="col-sm-12">
				     <div class="form-group">
				     	<div class="col-sm-4">
					     	<myTag:fileinput fileType="1" name="itemFile" multiple="multiple" cssClass="custom-file-input form-control" id="inputFile"></myTag:fileinput>
				     	</div>
					</div>
				</div>
				<div class="col-sm-12">
                    <div id="searchDiv" class="searchDiv">
                        <div id="operate-btn-group" class="btn-group operate-btn-group">
                            <select multiple class="form-control columnSelect">
                            </select>
                        </div>
                    </div>
					<table id="importTable" class="table table-bordered table-striped table-hover dataTable">
					</table>
				</div>
			</div>
			<!-- /.box-body -->
			<div class="box-footer text-right">
				<button type="button" class="btn btn-default" data-btn-type="upload" >上传</button>
				<button type="button" class="btn btn-primary" id="importSubmit" disabled="disabled" data-btn-type="importSubmit">确认导入</button>
				<button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">取消</button>
			</div>
			<!-- /.box-footer -->
		</form>
	</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/plugins/bootstrap-multiselect/js/bootstrap-multiselect.js"/>
<script>
	$(function() {
		var excelPath = '';
		var urlNamespace = "${urlNamespace}";
		var model = "${model}";
		var importAdjustDataTable;
	    var assetId = "${assetId}" || "0";
	    var importType = "${importType}";
	    var adjustType = importType;
	    var tableId = "importTable";
	    var importWinId = model + "Win";
	    var tempTableName = "";
	    var search = '${pageContext.request.queryString}' || location.search;
	    var columnKeys = [];
	    var config = {
	    		searchDiv: "searchDiv",
	            searching: true,
	            stateSave: false,
	            rowId: "oldId",
	            scrollY: '60vh',
	            scrollX: '100%',
	            scrollCollapse : true, 
	            disableSlimScroll : true,
	            initCallback: function () {
	                $("#" + tableId).parent().addClass("table-responsive");
	            }
	        }
        var reportDataColumns = JSON.parse(localStorage.getItem("report_import_columns") || "[]");
        localStorage.removeItem("report_import_columns");
		//上传读取
		$("button[data-btn-type='upload']").click(function(){
			if (!importAdjustDataTable) {
    			$("#importSubmit").attr('disabled', true);
			}
			dropTempTable(tempTableName);
			simpleAjaxUploadFile($("#inputFile"),function(result){
				var result = eval('(' + result + ')');
				//上传成功
				if(result.success){
					$("#importSubmit").removeAttr('disabled')
					excelPath = result.data[0].path;
					loaderId = layer.load(2);
					ajaxPost(router(urlNamespace).api(model).importPreview(search), {excelPath:excelPath}, function(data){
					//ajaxPost(basePath + "/af/industry/leak/modals/preview.json", {excelPath:excelPath}, function(data){
						if (data.success) {
							data = data.data || {};
							adjustType = data.adjustType || importType;
							columnKeys = data.columnKeys || [];
							/* for (var column of reportDataColumns) {
				                if ($.inArray(column.data, columnKeys) != -1) {
				                    column.render = $.proxy(function(data, type, row) {
				                        console.log(this);
				                        var oldVal = row["old_" + this.data] || 'EMPTY';
				                        return "<span class='newValue'>" + data + "</span>" + "（<span class='oldValue'>" + oldVal + "</span>）";
				                    }, column);
				                    columns.push(column);
				                }
				            } */
				            
				            //config.columns = data.columns || reportDataColumns;
				            tempTableName = data.tempTableName;
				            if (importAdjustDataTable) {
				            	try {
	    			            	$("#" + tableId).DataTable().destroy();
	    			            	$("#" + tableId).empty();
				            	} catch(e) {}
				            	importAdjustDataTable = null;
				            }
				            console.log(tempTableName);
				            if (tempTableName) {
				            	config.columns = data.columns || reportDataColumns;
				            	var url = router(urlNamespace).api(model).previewTempTable(tempTableName);
				            	//var url = basePath + "/af/industry/leak/previewTempTable.html?tempTableName=" + tempTableName;
	                            importAdjustDataTable = new CommonTable(tableId, url, null, config);
				            } else {
				            	/* columnKeys = data.columnKeys || [];
				            	var columns = [];
								for (var column of (data.columns || reportDataColumns)) {
					                if ($.inArray(column.data, columnKeys) != -1) {
					                    column.render = $.proxy(function(data, type, row) {
					                        console.log(this);
					                        var oldVal = row["old_" + this.data] || 'EMPTY';
					                        return "<span class='newValue'>" + data + "</span>" + "（<span class='oldValue'>" + oldVal + "</span>）";
					                    }, column);
					                    columns.push(column);
					                }
					            } */
								config.columns = data.columns || reportDataColumns;
	                            importAdjustDataTable = new CommonLocalTable(tableId, data.data || [], config);
				            }
						} else {
							modals.error(data.message);
						}
					}, true, null, function() {
		                layer.close(loaderId);
		            })
					$("#inputFile").val('');
				} else {
					$("#importSubmit").attr('disabled', true);
					modals.error(result.message);
				}
			})
		})
		//导入确认
		$("button[data-btn-type='importSubmit']").click(function(){
			if(excelPath == '') {
				modals.info('没有可导入的数据');
				return ;
			}
			modals.confirm("确认导入？", function() {
				loaderId = layer.load(6);
				//ajaxPost(router(urlNamespace).api(model).importSubmit(adjustType, tempTableName), {excelPath, tempTableName, columns:JSON.stringify(columnKeys)}, function(data,status){
	            ajaxPost(router(urlNamespace).api(model).importSubmit(search, tempTableName), {excelPath, tempTableName, columns:JSON.stringify(columnKeys)}, function(data,status){
					if(data.success){
	                    modals.info('导入成功');
	                    //commonTable.reloadData();
	                    modals.hideWin(importWinId);
	                }else{
	                    modals.info('导入失败');
	                }
	                layer.close(loaderId);
	            })
			})
		})
		
		//取消
        $("button[data-btn-type='cancel'], button.close[data-dismiss='modal']").click(function(){
        	dropTempTable(tempTableName);
        })
		
		function dropTempTable(tempTableName) {
			if(tempTableName) {
				ajaxPost(router(urlNamespace).api(model).dropTempTable(tempTableName), {}, function(data) {
	                console.log("删除临时表", tempTableName);
	            });
			}
		}
	})	
</script>