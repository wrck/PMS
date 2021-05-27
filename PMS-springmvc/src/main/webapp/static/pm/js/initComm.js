/**
 * 一些公共的变量
 */
$.namespace('sys.common');
sys.common = function(){
	tAdd = "<button class='btn btn-xs btn-success btn-add' type='button'><i class='fa fa-fw fa-plus'></i> 新建</button>";
	tEdit= function(rowid){
		return "<button data-rowid='"+rowid+"' class='btn btn-xs btn-success btn-edit' type='button'><i class='fa fa-fw fa-edit'></i> 编辑</button>";
	}
	tSave = function(rowid){
		return "<button data-rowid='"+rowid+"' class='btn btn-xs btn-success btn-save' type='button'><i class='fa fa-fw fa-save'></i> 保存</button>";
	} 
	tCancel = function(rowid){
		return "<button data-rowid='"+rowid+"' class='btn btn-xs btn-success btn-cancel' type='button'><i class='fa fa-fw fa-remove'></i> 取消</button>";
	} 
	tDetail = function(rowid){
		return "<button data-rowid='"+rowid+"' class='btn btn-xs btn-warning btn-deatil' type='button'><i class='fa fa-fw fa-table'></i> 明细</button>";
	} 
	
	feedbackIcons = {
        valid: 'glyphicon glyphicon-ok',
        invalid: 'glyphicon glyphicon-remove',
        validating: 'glyphicon glyphicon-refresh'
    }
	/**
	 * 表单是否禁用
	 */
	formDisabled = function(eleForm ,status){
		var $eleForm = $(eleForm);
		if(!status){
			$("input" ,$eleForm).attr('disabled','disabled');
			$("textarea" ,$eleForm).attr('disabled','disabled');
			$("select" ,$eleForm).attr('disabled','disabled');
			$("button[type='submit']" ,$eleForm).attr('disabled','disabled');
		}else{
			$("input",$eleForm).removeAttr('disabled');
			$("textarea",$eleForm).removeAttr('disabled');
			$("select",$eleForm).removeAttr('disabled');
			$("button[type='submit']",$eleForm).removeAttr('disabled');
		}
	}
	/**
	 * 失效数据项标红
	 */
	markRed = function(ele, status){
		if(!status){
			$(ele).css("color","red");
		}
	}
	
	var upload = function(fileType) {
		return ctx + "/file/baseUpload/" + fileType + ".json";
	}
	
	var baseUploadTabDownload = function(e, navTab) {
		var downloadType = 'down';
		var urlNamespace = navTab.urlNamespace || window.urlNamespace;
		var type = navTab.model || navTab.type;
		var $target = $(e.target);
		var downloadPath = navTab.downloadPath;
		var paramsValue = navTab.paramsValue || {};
		var localTable = $target.parents(".dataTables_wrapper:first").find("table.dataTable").data("localTable");
		var rowId = navTab.rowId || localTable.getSelectedRowId();
		if (!rowId) {
			modals.info('选择需要下载的文件');
			return;
		}
		var fileIds = rowId;
		paramsValue.ids = fileIds;
		var url = downloadPath;
		try {
			url = eval(downloadPath);
		} catch(e) {}
		var search = $.param(paramsValue)
		try {
			url = url || router(urlNamespace).html(type).download();
		} catch(e) {
			console.error(e);
			try {
				url = url || pm.router.html(type).download();
			} catch(e){
				console.error(e);
			}
		}
		if (!url) {
			modals.error('不支持该操作！');
			return;
		}
		url = url + (search ? "?" + search : "").replace("??", "?");
		if (rowId && url) {
			var a = document.createElement('a');
			a.download = '';
			if (!url.startsWith(basePath)) {
				url = basePath + url;
			}
			a.href = url;
			$("body").append(a); //修复firefox中无法触发click
			a.click();
			$(a).remove();
		}
	}
	
	var baseUploadTabZipDownload = function(e, navTab) {
		var downloadType = 'zipdown';
		var urlNamespace = navTab.urlNamespace || window.urlNamespace;
		var type = navTab.model || navTab.type;
		var $target = $(e.target);
		var downloadPath = navTab.downloadPath;
		var paramsValue = navTab.paramsValue || {};
		var localTable = $target.parents(".dataTables_wrapper:first").find("table.dataTable").data("localTable");
		var rowId = navTab.rowIds || localTable.getCheckedRowsId() || localTable.getSelectedRowsId() || [];
		if (!rowId || rowId.length == 0) {
			modals.info('选择需要下载的文件');
			return;
		}
		var fileIds = rowId.join(",");
		paramsValue.ids = fileIds;
		var url = downloadPath;
		try {
			url = eval(downloadPath);
		} catch(e) {}
		var search = $.param(paramsValue)
		try {
			url = url || router(urlNamespace).html(type).download();
		} catch(e) {
			console.error(e);
			try {
				url = url || pm.router.html(type).download();
			} catch(e){
				console.error(e);
			}
		}
		if (!url) {
			modals.error('不支持该操作！');
			return;
		}
		url = url + (search ? "?" + search : "").replace("??", "?");
		if (rowId && url) {
			var a = document.createElement('a');
			a.download = '';
			if (!url.startsWith(basePath)) {
				url = basePath + url;
			}
			a.href = url;
			$("body").append(a); //修复firefox中无法触发click
			a.click();
			$(a).remove();
		}
	}
	
	toDoWorkflowTask = function(elm, taskId, hideEntity, callback) {
		var _this = this;
		modals.openWin({
			url: workflow.html.taskDetail(taskId, true) + "?hideEntity=" + (hideEntity || ""),
			winId: "workflowWin",
			width: "75vw",
			hideFunc: function() {
				var $tabPane = $(elm).parents(".tab-pane:first");
				if ($tabPane.length > 0) {
					var config = $tabPane.data();
					initTabData(config, true);
				} else if (_this._isVue) {
    				_this.currentTaskId = 0;
    				_this.targetValue.hasTask = null;
    			}
    			if (callback && typeof callback == 'function') {
    				callback.call(_this, data);
    			}
			}
		});
	}
	
	startProcess = function(el, entity, callback, ignoreForm) {
		var _this = this;
		
		// 提交流程之前先保存表单，然后通过回调继续执行
		ignoreForm = ignoreForm == true ? true : false;
		if (!ignoreForm) {
			var $form = $(el).parents("form:first");
			if ($form.length != 0) {
				$form.data("submitCallback", function() {
					startProcess.call(_this, el, entity, callback, true);
				})
				var $submit = $form.find("button[data-btn-type='save']");
				if ($submit.length == 0) {
					$submit = $form.find("[type='submit']:first");
				}
				if ($submit.length > 0) {
					$submit.click();
				} else {
					$form.submit();
				}
				return;
			}
		}
		
		
		entity = entity || $(el).data("entity");
    	if (!entity) {
        	return false;
    	}
    	try {
    		entity = JSON.parse(entity.replace(/'/g, '"'));
    	} catch(e) {}
		
    	$(el).button("loading");
    	ajaxPost(router("/").api("workflow").startProcess(), entity, function(data) {
    		if (data.status) {
    			var $tabPane = $(el).parents(".tab-pane:first");
    			if ($tabPane.length > 0) {
    				var config = $tabPane.data();
    				initTabData(config, true);
    			} else if (_this._isVue) {
    				_this.currentTaskId = 0;
    				_this.targetValue.hasTask = null;
    				var targetValue = _this.targetValue || {};
    				var customInfo = targetValue.customInfo || {};
    				customInfo.currentTaskId = data.currentTaskId;
    				customInfo.currentProcInstId = data.currentProcInstId;
    				
    				targetValue.customInfo = customInfo;
    				_this.targetValue = targetValue;
    			}
    			if (callback && typeof callback == 'function') {
    				callback.call(_this, data);
    			}
    		}
    		modals.info(data.message);
    		$(el).button("reset");
    	})
    }
	
	startQualityApprove = startProcess;
	
	return {tEdit:tEdit,
		tSave:tSave,
		tCancel:tCancel,
		tDetail:tDetail ,
		feedbackIcons:feedbackIcons,
		formDisabled:formDisabled,
		markRed:markRed,
		upload: upload,
		baseUploadTabDownload: baseUploadTabDownload,
		baseUploadTabZipDownload: baseUploadTabZipDownload,
		toDoWorkflowTask: toDoWorkflowTask,
		startQualityApprove: startQualityApprove,
		startProcess: startProcess,
	};
}();
$(function() {
	$(document).off("click", '[data-btn-type="cancel"]').on("click", '[data-btn-type="cancel"]', cancelBtnClick);
	function cancelBtnClick() {
		if($(this).parents(".modal").length == 0) {
			if (window.history.length > 1) {
				history.back();
			} else {
				window.close();
			}
		}
	}
})
