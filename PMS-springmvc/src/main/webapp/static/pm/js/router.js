$.namespace("pm.router");
pm.router = function(model) {
	return {
		api: (model) => {
			var api;
			try {
				api = eval("pm." + model + ".api");
			} catch(e) {
				api = pm.common(ctx + "/pm/" + model).api;
			}
			return api;
		},
		html: (model) => {
			var html;
			try {
				html = eval("pm." + model + ".html");
			} catch(e) {
				html = pm.common(ctx + "/pm/" + model).html;
			}
			return html;
		}
	}
}();

$.namespace("pm.common");
pm.common = function(namespace) {
	return {
		api:((namespace) => {
			return {
				// 列表数据
				list: () => namespace + "/list.json",
				// 新增报告
				create: (search)=> namespace + "/detail.json" + (search ? "?" + search : "").replace("??", "?"),
				// 更新报告
				update: (id) => namespace + "/" + id + ".json?_method=PUT",
				// 删除报告
				delete:(id) => namespace + "/" + id + ".json?_method=DELETE",
				// 查询指定报告
				detail:(id) => namespace + "/" + id + ".json"
			}
		})(namespace),
		html: ((namespace) => {
			return {
				list: () => namespace + ".html",
				detail: (id) => namespace + "/" + id + ".html",
				create: (search, isModals) => namespace + (isModals ? "/modals" : "") + "/detail.html"+ (search ? "?" + search : "").replace("??", "?")
			}
		})(namespace)
	}
};

/**
 * 项目管理
 */
$.namespace("pm.project");
pm.project = function() {
	var namespace =  ctx + "/pm/project";
	return {
		api:((namespace) => {
			var api = pm.common(namespace).api;
			return $.extend({}, api, {
				// 查询报告类型
				orderDetail: (projectId, projectType, contractNo) => namespace + (projectId ? ("/" + projectId) : "") + "/orderDetail.json?" + $.param({projectType, contractNo}),
				// 初始化报告数据
				initData: (projectId) => namespace + "/" + projectId + "/initData.json",
				// 报告数据查询、调整
				lineList: (projectId) => namespace + "/" + projectId + "/list.json",
				// 报告数据列对应关系
				columnMapping: (reportType) => namespace + "/columnMapping.json?reportType=" + reportType,
				// 产品分级数据
				productLevelData: (reportType) => namespace + "/productLevel.json?reportType=" + reportType,
				// 产品分级调整
				reportDataAdjust: (projectId) => namespace + "/" + projectId + "/adjust.json",
				// 导入数据预览
				importPreview: (projectId, importType) => namespace + "/" + projectId + "/" + importType + "/preview.json",
				// 导入数据预览
				previewTempTable: (projectId, tempTableName) => namespace + "/" + projectId + "/previewTempTable.json?tempTableName="+tempTableName,
				// 删除临时表
				dropTempTable: (projectId, tempTableName) => namespace + "/" + projectId + "/dropTempTable.json?tempTableName="+tempTableName,
				// 导入数据提交
				importSubmit: (projectId, importType, tempTableName) => namespace + "/" + projectId + "/" + importType + "/submit" + (tempTableName ? "TempTable" : "") + ".json",
				// 日志查询
				logView: (projectId) => namespace + "/" + projectId + "/log.json",
			});
		})(namespace),
		html: ((namespace) => {
			var html = pm.common(namespace).html;
			return $.extend({}, html, {
				lineList: (projectId) => namespace + "/" + projectId + "/list.html",
				// 日志查询
				logView: (projectId, isModals) => namespace + "/" + projectId + (isModals ? "/modals" : "") + "/log.html",
				// 导入模态页
				importModals: (projectId, importType) => namespace + "/" + projectId + "/modals/" + (importType || "adjustData") + ".html",
			});
		})(namespace)
	}
}();

/**
 * 派单管理
 */
$.namespace("pm.dispatch");
pm.dispatch = function() {
	var namespace =  ctx + "/pm/dispatch";
	var router = pm.common(namespace);
	return $.extend(true, {}, router, {
		api:((namespace) => {
			return {
				submit:() => namespace + "/submit.json",
			};
		})(namespace),
		html: ((namespace) => {
			return {
			};
		})(namespace),
	});
}();

/**
 * 结算管理
 */
$.namespace("pm.settlement");
pm.settlement = function() {
	var namespace =  ctx + "/pm/settlement";
	var router = pm.common(namespace);
	return $.extend(true, {}, router, {
		api:((namespace) => {
			return {
			};
		})(namespace),
		html: ((namespace) => {
			return {
			};
		})(namespace),
	});
}();

/**
 * 结算管理
 */
$.namespace('cm.accountPeriod');
cm.accountPeriod=function(){
	return {listUrl:ctx + '/cm/accountPeriod/jlist.json',
		changeStateUrl:ctx + '/cm/accountPeriod/changeState.json',
		tranferUrl:ctx + '/cm/accountPeriod/transfer.json'}
	
}();

/* item管理**/
$.namespace('cm.item.list');

cm.item.list=function(){
	return {
		listUrl : ctx + '/cm/item/jlist.json',
		importUrl:ctx + '/cm/item/modals/import.html',
		readUrl:ctx + '/cm/item/readAndWriteItemInfo.json',
		importSubmitUrl:ctx + '/cm/item/importSubmit.json',
	}
}();



/* item收入类型管理*/
$.namespace('cm.item.incometype.list');

cm.item.incometype.list = function(){
	return {
		listUrl: ctx+ '/cm/item/incomeType/jlist.json',
		submitUrl:ctx + '/cm/item/incomeType/submit.json',
	}
}();

/*分摊管理*/
$.namespace('ar.allocation.list');
ar.allocation.list = function(){
	//分摊列表
	var listUrl = ctx + '/ar/allocation/jlist.json';
	//新建分摊
	var submitUrl = ctx + '/ar/allocation/submit.json';
	
	var deleteUrl = ctx + '/ar/allocation/delete.json';
	
	var totalDataInput = ctx + '/ar/allocation/modals/totalDataInput.html';
	
	var submitDataInput = ctx + '/ar/allocation/submitTotalDataInput.json'
	
	var inVarListUrl = ctx + "/ar/allocation/rule/var/inVarListByAllocation.json";
	
	var sysVarListUrl =  ctx + "/ar/allocation/rule/var/sysVarListByAllocation.json";
	
	var executeAllocationUrl = ctx + '/ar/allocation/executeAllocation.json';
	//分摊数据调整入口
	var adjustlistUrl = ctx + '/ar/allocation/list/adjust.html';
	
//	var detaillistUrl = ctx + '/ar/allocation/list/adjust.html';
	
	return {listUrl:listUrl,
		submitUrl:submitUrl,
		deleteUrl:deleteUrl,
		totalDataInputUrl:totalDataInput,
		submitDataInput:submitDataInput,
		inVarListUrl:inVarListUrl,
		sysVarListUrl:sysVarListUrl,
		executeAllocationUrl:executeAllocationUrl,
		adjustlistUrl:adjustlistUrl
	};
}();

/* 分摊数据调整 */
$.namespace('ar.allocation.adjust.list');

ar.allocation.adjust.list = function(){
	return {
		listUrl : ctx + '/ar/allocation/jadjustList.json',
		incomeTypeAdjustUrl : ctx + '/ar/allocation/incomeTypeAdjust.json',
		cancelAllocationUrl:ctx + '/ar/allocation/cancelAllocation.json',
		agentAdjustUrl:ctx + '/ar/allocation/agentAdjust.json',
		agentListUrl:ctx + '/ar/allocation/agentList.json'
	}
}();  

/*分摊规则管理*/
$.namespace('ar.allocation.rule.list');

ar.allocation.rule.list = function(){
	//分摊规则列表
	var listUrl = ctx + '/ar/allocation/rule/jlist.json';
	//新建分摊规则 提交
	var submitUrl = ctx + '/ar/allocation/rule/submit.json';
	
	var varListUrl = ctx + '/ar/allocation/rule/var/jlist.json';
	
	var varSubmitUrl = ctx + '/ar/allocation/rule/var/submit.json';;
	
	return {listUrl :listUrl ,submitUrl:submitUrl ,varListUrl:varListUrl,varSubmitUrl:varSubmitUrl}
}();


/*收入口径salesIn*/
$.namespace('ar.revenue.salesin');
ar.revenue.salesin=function(){
	return {
		jlistUrl : ctx + '/ar/revenue/salesIn/jlist.json'
	}
}();

/* 合并收入口径salesIn*/
$.namespace('ar.merge.revenue.salesin');
ar.merge.revenue.salesin=function(){
	return {
		jlistUrl:ctx + '/ar/merge_revenue/salesIn/jlist.json'
	}
}();

/*递延查询*/
$.namespace('ar.deferred.query');
ar.deferred.query=function(){
	return {
		jlistUrl:ctx + '/ar/deferred/query/jlist.json'
	}
}();


/**
 * 报告管理
 */
$.namespace("ar.report");
ar.report = function() {
	var reportNamespace =  ctx + "/ar/report";
	return {
		api:(() => {
			return {
				// 列表数据
				list: () => reportNamespace + "/list.json",
				// 新增报告
				create: ()=> reportNamespace + "/submit.json",
				// 更新报告
				update: (reportId) => reportNamespace + "/" + reportId + ".json?_method=PUT",
				// 删除报告
				delete:(reportId) =>  reportNamespace + "/" + reportId + ".json?_method=DELETE",
				// 查询指定报告
				findOne:(reportId) => reportNamespace + "/" + reportId + ".json",
				// 查询报告类型
				reportType: (dicTypeName) => ctx + "/sys/dictionary/list.json?dicTypeName=" + (dicTypeName || "报告类型"),
				// 初始化报告数据
				initData: (reportId) => reportNamespace + "/" + reportId + "/initData.json",
				// 报告数据查询、调整
				lineList: (reportId) => reportNamespace + "/" + reportId + "/list.json",
				// 报告数据列对应关系
				columnMapping: (reportType) => reportNamespace + "/columnMapping.json?reportType=" + reportType,
				// 产品分级数据
				productLevelData: (reportType) => reportNamespace + "/productLevel.json?reportType=" + reportType,
				// 产品分级调整
				reportDataAdjust: (reportId) => reportNamespace + "/" + reportId + "/adjust.json",
				// 导入数据预览
				importPreview: (reportId, importType) => reportNamespace + "/" + reportId + "/" + importType + "/preview.json",
				// 导入数据预览
				previewTempTable: (reportId, tempTableName) => reportNamespace + "/" + reportId + "/previewTempTable.json?tempTableName="+tempTableName,
				// 删除临时表
				dropTempTable: (reportId, tempTableName) => reportNamespace + "/" + reportId + "/dropTempTable.json?tempTableName="+tempTableName,
				// 导入数据提交
				importSubmit: (reportId, importType, tempTableName) => reportNamespace + "/" + reportId + "/" + importType + "/submit" + (tempTableName ? "TempTable" : "") + ".json",
				// 日志查询
				logView: (reportId) => reportNamespace + "/" + reportId + "/log.json",
			}
		})(),
		html: (() => {
			return {
				list: () => reportNamespace + ".html",
				lineList: (reportId) => reportNamespace + "/" + reportId + "/list.html",
				// 日志查询
				logView: (reportId, isModals) => reportNamespace + "/" + reportId + (isModals ? "/modals" : "") + "/log.html",
				// 导入模态页
				importModals: (reportId, importType) => reportNamespace + "/" + reportId + "/modals/" + (importType || "adjustData") + ".html",
			}
		})()
	}
}();