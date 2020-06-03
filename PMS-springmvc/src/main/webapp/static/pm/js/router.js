$.namespace("router");
router = function(namespace) {
	return {
		api: (model) => {
			var api;
			try {
				namespace = (namespace || window.location.pathname.replace(ctx, "").match(/(\/[^\/]+\/)/g)[0]);
			} catch(e) {}
			try {
				var path = (namespace || "").replace(/\//g, ".");
				if (path.startsWith(".")) {
					path = path.replace(".", "");
				}
				api = eval(path + model + ".api");
			} catch(e) {
				var tm = model.replace(/([^A-Z]+)([A-Z]+)/g, function(word,a,b,c){
					return a + "/" + b.toLowerCase() ;
				});
				api = router.common(ctx + namespace + tm).api;
			}
			return api;
		},
		html: (model) => {
			var html;
			try {
				namespace = (namespace || window.location.pathname.replace(ctx, "").match(/(\/[^\/]+\/)/g)[0]);
			} catch(e) {}
			try {
				var path = (namespace || "").replace(/\//g, ".");
				if (path.startsWith(".")) {
					path = path.replace(".", "");
				}
				html = eval(path + model + ".html");
			} catch(e) {
				var tm = model.replace(/([^A-Z]+)([A-Z]+)/g, function(word,a,b,c){
					return a + "/" + b.toLowerCase() ;
				});
				html = router.common(ctx + namespace + tm).html;
			}
			return html;
		},
		callback: (model) => {
			var callback;
			try {
				namespace = (namespace || window.location.pathname.replace(ctx, "").match(/(\/[^\/]+\/)/g)[0]);
			} catch(e) {}
			try {
				var path = (namespace || "").replace(/\//g, ".");
				if (path.startsWith(".")) {
					path = path.replace(".", "");
				}
				callback = eval(path + model + ".callback");
			} catch(e) {
				var tm = model.replace(/([^A-Z]+)([A-Z]+)/g, function(word,a,b,c){
					return a + "/" + b.toLowerCase() ;
				});
				callback = router.common(ctx + namespace + tm).callback;
			}
			return callback;
		},
		methods: (model) => {
			var methods;
			try {
				namespace = (namespace || window.location.pathname.replace(ctx, "").match(/(\/[^\/]+\/)/g)[0]);
			} catch(e) {}
			try {
				var path = (namespace || "").replace(/\//g, ".");
				if (path.startsWith(".")) {
					path = path.replace(".", "");
				}
				methods = eval(path + model + ".methods");
			} catch(e) {
				var tm = model.replace(/([^A-Z]+)([A-Z]+)/g, function(word,a,b,c){
					return a + "/" + b.toLowerCase() ;
				});
				methods = router.common(ctx + namespace + tm).methods;
			}
			return methods;
		}
	}
};

$.namespace("router.common");
router.common = function(namespace) {
	return {
		api:((namespace) => {
			return {
				// 列表数据
				list: (search) => namespace + "/list.json" + (search ? "?" + search : "").replace("??", "?"),
				// 新增报告
				create: (search)=> namespace + "/detail.json" + (search ? "?" + search : "").replace("??", "?"),
				// 更新报告
				update: (id) => namespace + "/" + id + ".json?_method=PUT",
				// 删除报告
				delete:(id) => namespace + "/" + id + ".json?_method=DELETE",
				// 查询指定报告
				detail:(id, search) => namespace + "/" + id + ".json" + (search ? "?" + search : "").replace("??", "?"),
				// 导入数据预览
				importPreview: (search) => namespace + "/import/preview.json" + (search ? "?" + search : "").replace("??", "?"),
				// 导入数据预览
				previewTempTable: (tempTableName) => namespace + "/previewTempTable.json?tempTableName="+tempTableName,
				// 删除临时表
				dropTempTable: (tempTableName) => namespace + "/dropTempTable.json?tempTableName="+tempTableName,
				// 导入数据提交
				importSubmit: (search, tempTableName) => namespace + "/import/submit" + (tempTableName ? "TempTable" : "") + ".json" + (search ? "?" + search : "").replace("??", "?"),
				// 附件上传
				upload: (search) => namespace + "/upload.json" + (search ? "?" + search : "").replace("??", "?"),
				uploadList: (search) => namespace + "/upload/list.json" + (search ? "?" + search : "").replace("??", "?"),
			}
		})(namespace),
		html: ((namespace) => {
			return {
				list: (search) => namespace + ".html" + (search ? "?" + search : "").replace("??", "?"),
				detail: (id, isModals) => namespace + (isModals ? "/modals" : "") + "/" + id + ".html",
				create: (search, isModals) => namespace + (isModals ? "/modals" : "") + "/detail.html"+ (search ? "?" + search : "").replace("??", "?"),
				// 导入模态页
				import: (search, isModals) => namespace + (isModals ? "/modals" : "") + "/import.html"+ (search ? "?" + search : "").replace("??", "?"),
				// 附件上传
				upload: (search, isModals) => namespace + (isModals ? "/modals" : "") + "/upload.html"+ (search ? "?" + search : "").replace("??", "?"),
				// 下载
				download: (search) => namespace + "/download.html"+ (search ? "?" + search : "").replace("??", "?"),
			}
		})(namespace),
		callback: {
			detail: {
				vueCallback: function(data, $container) {},
				modalCreateCallback:null,
				shouldHideWin:null
			},
		},
		methods: {
//			canStartProcess: function(entity) {
//				return false;
//			},
//			startProcess: function(el, entity) {
//			}
		}
	}
};

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
		},
		callback: (model) => {
			var callback;
			try {
				callback = eval("pm." + model + ".callback");
			} catch(e) {
				callback = pm.common(ctx + "/pm/" + model).callback;
			}
			return callback;
		},
	}
}();

$.namespace("pm.common");
pm.common = function(namespace) {
	return router.common(namespace);
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
				// 查询设备清单
				orderDetail: (projectId, projectType, contractNo) => namespace + (projectId ? ("/" + projectId) : "") + "/orderDetail.json?" + $.param({projectType, contractNo}),
				// 查询任务类别
				projectTask: (projectId, projectType, contractNo) => namespace + (projectId ? ("/" + projectId) : "") + "/task.json?" + $.param({projectType, contractNo}),
				// 查询项目资产
				projectAsset: (projectId, projectType) => namespace + (projectId ? ("/" + projectId) : "") + "/asset.json?" + $.param({projectType}),
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
		})(namespace),
		callback:((namespace) => {
			var callback = pm.common(namespace).callback;
			return $.extend({}, callback, {
				list: function() {
					console.log("callback");
				}
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
				generateDispatchSeq:()=> namespace + "/generateDispatchSeq.json"
			};
		})(namespace),
		html: ((namespace) => {
			return {
				exportDispatchInfo: (dispatchId, exportType) => {
					var exportType = exportType || "doc";
					return namespace + "/" + dispatchId + "/" + exportType + "/info.html";
				},
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
				exportProjectInfo: (settleId, exportType) => {
					var exportType = exportType || "doc";
					return namespace + "/" + settleId + "/projectInfoDoc.html";
				},
			};
		})(namespace),
	});
}();

/**
 * 人员管理
 */
$.namespace("pm.projectTask");
pm.projectTask = function() {
	var namespace =  ctx + "/pm/project/task";
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
		callback: ((namespace) => {
			return {
				detail: {
					modalCreateCallback: function(options) {
						console.log("modalCreateCallback");
						var winId = options.winId;
        				if (!$("#" + winId).length) {
        					winId = $(this).parents(".modal.in:first").attr("id");
        				}
						modals.removeData(winId);
        				$("#" + winId).modal({ 
							remote: options.url
						});
					},
					shouldHideWin: function() {
						return false;
					}
				}
			};
		})(namespace),
		methods: {
			canStartProcess: function(data) {
				var hasTask = !!(data.customInfo || {}).currentTaskId;
				var canStart = !hasTask && !isNaN(data.status) && data.progress == 100;
				return canStart;
			},
			startProcess: function(el, data, callback) {
				var _this = this;
				data = data || $(el).data("entity");
		    	if (!data) {
		        	return false;
		    	}
		    	try {
		    		data = JSON.parse(data.replace(/'/g, '"'));
		    	} catch(e) {}
		    	
				var entity = {
					processKey: "QualityApproveTrack",
					objId: data.projectId,
					objType: 'project',
					dataId: data.taskId,
					dataType: "projectTask"
	            };
				sys.common.startProcess.call(this, el, entity, callback);
			}
		}
	});
}();

/**
 * 人员管理
 */
$.namespace("pm.projectMember");
pm.projectMember = function() {
	var namespace =  ctx + "/pm/member";
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
 * 行业资产
 */
$.namespace("af.industryAsset");
af.industryAsset = function() {
	var namespace =  ctx + "/af/industry/asset";
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
		methods: {
			canStartProcess: function(data) {
				var hasTask = !!(data.customInfo || {}).currentTaskId;
				var canStart = !hasTask && !isNaN(data.status);
				return canStart;
			},
			startProcess: function(el, data, callback) {
				var _this = this;
				data = data || $(el).data("entity");
		    	if (!data) {
		        	return false;
		    	}
		    	try {
		    		data = JSON.parse(data.replace(/'/g, '"'));
		    	} catch(e) {}
		    	
				var entity = {
					processKey: "QualityApproveTrack",
					objId: data.id,
					objType: 'industryAsset',
					dataId: data.id,
					dataType: "industryAsset"
	            };
				sys.common.startProcess.call(this, el, entity, callback);
			}
		}
	});
}();

/**
 * 行业漏洞
 */
$.namespace("af.industryLeak");
af.industryLeak = function() {
	var namespace =  ctx + "/af/industry/leak";
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
		methods: {
			canStartProcess: function(data) {
				var hasTask = !!(data.customInfo || {}).currentTaskId;
				var canStart = !hasTask && !isNaN(data.status);
				return canStart;
			},
			startProcess: function(el, data, callback) {
				var _this = this;
				data = data || $(el).data("entity");
		    	if (!data) {
		        	return false;
		    	}
		    	try {
		    		data = JSON.parse(data.replace(/'/g, '"'));
		    	} catch(e) {}
		    	
				var entity = {
					processKey: "QualityApproveTrack",
					objId: data.id,
					objType: 'industryLeak',
					dataId: data.id,
					dataType: "industryLeak"
	            };
				sys.common.startProcess.call(this, el, entity, callback);
			}
		}
	});
}();

/**
 * 项目资产
 */
$.namespace("pm.projectAsset");
pm.projectAsset = function() {
	var namespace =  ctx + "/pm/project/asset";
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
		methods: {
			canStartProcess: function(data) {
				var hasTask = !!(data.customInfo || {}).currentTaskId;
				var canStart = !hasTask && !isNaN(data.status);
				return canStart;
			},
			startProcess: function(el, data, callback) {
				var _this = this;
				data = data || $(el).data("entity");
		    	if (!data) {
		        	return false;
		    	}
		    	try {
		    		data = JSON.parse(data.replace(/'/g, '"'));
		    	} catch(e) {}
		    	
				var entity = {
					processKey: "QualityApproveTrack",
					objId: data.projectId,
					objType: 'project',
					dataId: data.assetId,
					dataType: "industryAsset"
	            };
				sys.common.startProcess.call(this, el, entity, callback);
			}
		}
	});
}();

/**
 * 项目资产
 */
$.namespace("af.industryWarningAsset");
af.industryWarningAsset = function() {
	var namespace =  ctx + "/af/industry/warning/asset";
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
		callback: ((namespace) => {
			return {
				list: {
					complete: function() {
						$(".operate-btn-group").remove();
						$(document).off("dblclick", "#" + tableId  + " tbody tr");
					}
				}
			};
		})(namespace),
	});
}();

/**
 * 项目资产漏洞
 */
$.namespace("pm.assetLeak");
pm.assetLeak = function() {
	var namespace =  ctx + "/pm/asset/leak";
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
		callback: ((namespace) => {
			return {
				detail: {
					vueCallback: function(data, $container) {
						console.log("vueCallback");
						// 资产信息Select2初始化完成之后，添加change事件，避免直接添加change事件，无法获取原始保存的资产信息
			    		var selectedId = (data.targetValue || {}).assetIds;
			    		var projectId = (data.targetValue || {}).projectId;
			    		ajaxGet(pm.router.api("projectAsset").list(), {projectId}, function(data) {
			    			var list = data.data || [];
			    			var results = $.map(list, function (obj) {
			    				obj.id = obj.assetId;
			    				obj.text = obj.assetName;
			    				return obj;
			    			});
			    			$("#assetIds", $container).select2({
			    				multiple: true,
			    				allowClear: true,
			    				dropdownAutoWidth:true,
			    				data: results,// 设置初始值
			    				placeholder: '搜索项目资产名'
			    			})
			    		});
//			    		var dataCacheAdapter = $.fn.select2.amd.require('select2/data/dataCacheAdapter');
//			    		$("#assetIds", $container).select2({
//		    			    dataAdapter: dataCacheAdapter,// 数据分页缓存适配器，在base-form中定义
//		    			    multiple: true,
//			    			allowClear: true,
//			    			dropdownAutoWidth:true,
//			    			data: selectedId ? [{id: selectedId, text: ""}] : [],// 设置初始值
//			    			ajax: {
//			    			    url: pm.router.api("projectAsset").list("projectId=" + projectId),
//			    			    dataType: 'json',
//			    			    delay: 250,
//			    			    data: function (params) {
//			    			    	params.pageSize = 10;
//				    			    return {
//				    			        assetName: params.term, // search term
//				    			        fuzzySearch: true,
//				    			        pageSize: params.pageSize || 10,
//				    			        start: (params.page - 1) * params.pageSize || 0
//				    			    };
//			    			    },
//			    			    processResults: function (data, params) {
//			    			      	params.page = params.page || 1;
//								  	var list = data.data || [];
//								  	var results = $.map(list, function (obj) {
//								  		obj.id = obj.assetId;
//								  		obj.text = obj.assetName;
//									  	return obj;
//									});
//			    			      	return {
//			    			        	results: results,
//			    			        	pagination: {
//			    			          		more: (params.page * (params.pageSize || 10)) < data.pageParam.filtered
//			    			       		}
//			    			      	};
//			    			    },
//			    			    cache: true
//			    			  },
//			    			  placeholder: '搜索项目资产名',
//			    			  minimumInputLength: 2,
//			    			  templateResult: function (repo) {
//		    				  	  if (repo.loading) {
//	    	    					  return repo.text;
//			    	    		  }
//		    				  	  return repo.text;
//			    			  },
//			    			  templateSelection: function formatRepoSelection (repo) {
//			    				  return repo.text;
//		    	    		  }
//			    		});
			    		/* if (dispatchIdPlaceholder) {
			    			$(".select2-selection__placeholder").css("color", 'inherit');
			    		} */
			    		
			    		// 项目名称初始化完成之后，添加change事件，避免直接添加change事件，无法获取原始保存的信息
			    		$("#assetIds + .select2-container", $container).one("click", function(e) {
			    			$("#assetIds", $container).on("change", function(e){
			    				try{
			    					var source = $(this).select2("data");
			    					if (source.length > 0) {
			    						source = source[0];
			    					} else {
			    						source = {};
			    					}
			    					console.log(source, this.value);
					    			var targetValue = vm._data.targetValue || {};
					    			targetValue.assetIds = $("#assetIds").val();
					    			vm._data.targetValue = targetValue;
				    			} catch(e){}
			    			});
			    		});
					}
				}
			};
		})(namespace),
		methods: {
			canStartProcess: function(data) {
				var hasTask = !!(data.customInfo || {}).currentTaskId;
				var canStart = !hasTask && !isNaN(data.status);
				return canStart;
			},
			startProcess: function(el, data, callback) {
				var _this = this;
				data = data || $(el).data("entity");
		    	if (!data) {
		        	return false;
		    	}
		    	try {
		    		data = JSON.parse(data.replace(/'/g, '"'));
		    	} catch(e) {}
		    	
				var entity = {
					processKey: "QualityApproveTrack",
					objId: data.projectId,
					objType: 'project',
					dataId: data.leakId,
					dataType: "industryLeak"
	            };
				sys.common.startProcess.call(this, el, entity, callback);
			}
		}
	});
}();

/**
 * 流程
 */
$.namespace("workflow");
workflow = function() {
	var namespace =  ctx + "/workflow";
	var router = pm.common(namespace);
	return $.extend(true, {}, router, {
		api:((namespace) => {
			return {
				startProcess: () => namespace + "/startProcess.json",
				infoList: (search) => namespace + "/info/list.json" + (search ? "?" + search : "").replace("??", "?"),
				checkTask: (taskId, procInstId) => namespace + "/task/" + taskId + "/check.json" + (procInstId ? "?procInstId=" + procInstId : ""),
				taskDetail: (taskId) => namespace + "/task/" + taskId + ".json",
			};
		})(namespace),
		html: ((namespace) => {
			return {
				taskDetail: (taskId, isModals) => namespace + "/task" + (isModals ? "/modals" : "") + "/" + taskId + ".html",
			};
		})(namespace),
		callback: ((namespace) => {
		})(namespace),
	});
}();

router.getDownload = function(url, isInner) {
	if(!url) {
		modals.error('下载地址不存在！');
		return;
	}
	isInner = isInner == false ? false : true;
	var a = document.createElement('a');
	a.download = '';
	if (isInner && !url.startsWith(basePath)) {
		url = basePath + url;
	}
	a.href = url;
	$("body").append(a); // 修复firefox中无法触发click
	a.click();
	$(a).remove();
}

router.postDownload = function(url, isInner) {
	if(!url) {
		modals.error('下载地址不存在！');
		return;
	}
	isInner = isInner == false ? false : true;
	if (isInner && !url.startsWith(basePath)) {
		url = basePath + url;
	}
	var form = document.createElement('form');
	form.download = '';
	form.method = "post";
	if (typeof __RequestVerificationToken == "string") {
		url = url + "?__RequestVerificationToken=" + __RequestVerificationToken;
	}
	form.action = url;
	$("body").append(form);
	form.submit();
	$(form).remove();
}