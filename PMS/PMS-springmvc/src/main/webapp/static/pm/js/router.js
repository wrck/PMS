$.initRouter = function(namespace) {
	// 创建命名空间
	var rt = $.namespace(namespace);
	// 创建缓存命名空间
	cachedRouters = $.namespace("cachedRouters");
	// 延迟进行缓存
	setTimeout(function() {
        cachedRouters[namespace] = $.namespace(namespace);
    }, 0);
}
$.initRouter("router");
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

$.initRouter("router.common");
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
				uploadDelete: (id, search) => namespace + "/upload/" + id + ".json?_method=DELETE" + (search ? "&" + search : "").replace("&?", "?"),
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

$.initRouter("pm.router");
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

$.initRouter("pm.common");
pm.common = function(namespace) {
	return router.common(namespace);
};

/**
 * 项目管理
 */
$.initRouter("pm.project");
pm.project = function() {
	var namespace =  ctx + "/pm/project";
	return {
		api:((namespace) => {
			var api = pm.common(namespace).api;
			return $.extend({}, api, {
				// 查询设备清单
				orderDetail: (projectId, projectType, contractNo) => namespace + (projectId ? ("/" + projectId) : "") + "/orderDetail.json?" + $.param({projectType, contractNo}),
				// 查询产品配置
				productInfo: (projectId, projectType, projectCode) => namespace + /*(projectId ? ("/" + projectId) : "") + */ "/productInfo.json?" + $.param({projectType, projectCode}),
				// 查询任务类别
				projectTask: (projectId, projectType, contractNo) => namespace + (projectId ? ("/" + projectId) : "") + "/task.json?" + $.param({projectType, contractNo}),
				// 查询项目资产
				projectAsset: (projectId, projectType) => namespace + (projectId ? ("/" + projectId) : "") + "/asset.json?" + $.param({projectType}),
				// 项目转移
				projectTransform: (projectId, transformType, search) => namespace + (projectId ? ("/" + projectId) : "") + "/transform/" + transformType + ".json" + (search ? "?" + search : "").replace("??", "?"),
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
				// 项目转移
				projectTransform: (projectId, transformType, search, isModals) => namespace + (isModals ? "/modals" : "") + (projectId ? ("/" + projectId) : "") + "/transform/" + transformType + ".html" + (search ? "?" + search : "").replace("??", "?"),
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
$.initRouter("pm.dispatch");
pm.dispatch = function() {
	var namespace =  ctx + "/pm/dispatch";
	var commonRouter = pm.common(namespace);
	var $router = $.extend(true, {}, commonRouter, {
		api:((namespace) => {
			return {
				submit:() => namespace + "/submit.json",
				generateDispatchSeq:()=> namespace + "/generateDispatchSeq.json",
				multiDimInfos:(dispatchId) => namespace + "/" + dispatchId + "/multiDimInfos.json",
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
		callback: ((namespace) => {
			return {
				list: {
					vueCallback: function(dataTable, $container) {
						// 添加Excel导出按钮
						var config = dataTable.config || {};
						config.exportData = {
		                	url: router(urlNamespace).api(model).list().replace(".json", ".xlsx"),
		                	fileName: "项目转包",
		                	type: ["excel"]
		                };
						dataTable.config = config;
						dataTable.exportData = config.exportData;
						dataTable.exporting = true;
					}
				},
				detail: {
					vueCallback: function(data, $container) {
						var _this = this;
						var vm = _this;
						// 服务商Select2初始化完成之后，添加change事件，避免直接添加change事件，无法获取原始保存的服务商信息
			    		var selectedId = (data.targetValue || {}).projectIds;
			    		var selectedText = (data.targetValue || {}).dispatchName;
			    		var project = ((data.targetValue || {}).customInfo || {}).project || {};
			    		$("#projectIds", $container).select2({
			    			allowClear: true,
			    			dropdownAutoWidth: true,
			    			data: selectedId ? [$.extend(true, {}, project, {id: selectedId, text: selectedText})] : [],// 设置初始值
			    			ajax: {
			    			    url: basePath + "/pm/project/list.json",
			    			    dataType: 'json',
			    			    delay: 250,
			    			    data: function (params) {
			    			      return {
			    			        fuzzy: params.term, // search term
			    			        fuzzySearch: true,
			    			        pageSize: 30,
			    			        start: (params.page - 1) * 30 || 0
			    			      };
			    			    },
			    			    processResults: function (data, params) {
			    			      	params.page = params.page || 1;
								  	var list = data.data || [];
								  	var results = $.map(list, function (obj) {
								  		obj.id = obj.id || obj.projectId;
								  		obj.text = obj.projectName;
									  	return obj;
									});
			    			      	return {
			    			        	results: results,
			    			        	pagination: {
			    			          		more: (params.page * 30) < data.pageParam.filtered
			    			       		}
			    			      	};
			    			    },
			    			    cache: true
			    			},
			    			placeholder: '搜索项目名称',
			    			minimumInputLength: 4,
			    			templateResult: function(repo) {
								if (repo.loading) {
									return repo.text;
								}
								
								var $container = $(
									"<div class='select2-result-repository clearfix'>" +
								      "<div class='select2-result-repository__meta'>" +
								        "<div class='select2-result-repository__title'></div>" +
								        "<div class='select2-result-repository__description'></div>" +
								        "<div class='select2-result-repository__statistics'>" +
								          "<div class='select2-result-repository__smsSubmitTime'></div>" +
								          "<div class='select2-result-repository__smsProjectAmount'></div>" +
								        "</div>" +
								      "</div>" +
								    "</div>"
								);
								
								$container.find(".select2-result-repository__title").append("<div>" + repo.projectCode + "</div>");
								$container.find(".select2-result-repository__title").append("<div>" + (repo.contractNo || "") + "</div>");
								$container.find(".select2-result-repository__description").text(repo.projectName);
								$container.find(".select2-result-repository__forks").append(repo.contractNo);
								$container.find(".select2-result-repository__smsSubmitTime").append((repo.customInfo || {}).smsSubmitTime);
								$container.find(".select2-result-repository__smsProjectAmount").append((repo.customInfo || {}).smsProjectAmount);
								
								return $container;
							},
			    			templateSelection: function(repo) {
			        			return repo.projectName || repo.text;
			        		}
			    		});
			    		/* if (projectIdsPlaceholder) {
			    			$(".select2-selection__placeholder").css("color", 'inherit');
			    		} */
			    		
			    		// 项目名称初始化完成之后，添加change事件，避免直接添加change事件，无法获取原始保存的信息
			    		$("#projectIds", $container).siblings(".select2-container").one("click", function(e) {
			    			$("#projectIds", $container).on("change", function(e){
			    				try{
			    					var source = $(this).select2("data");
			    					console.log(source);
			    					if (source.length > 0) {
			    						source = source[0];
			    					} else {
			    						source = {};
			    					}
			    					var targetValue = vm._data.targetValue || {};
			    					var customInfo = targetValue.customInfo || {};
			    					targetValue.projectName = source.projectName;
			    					targetValue.dispatchName = source.projectName;
			    					targetValue.smsProjectCode = source.smsProjectCode || (source.customInfo || {}).smsProjectCode;
			    					targetValue.smsSubmitTime = source.smsSubmitTime || (source.customInfo || {}).smsSubmitTime;
			    					targetValue.smsProjectAmount = source.smsProjectAmount || (source.customInfo || {}).smsProjectAmount;
			    					targetValue.smsAfProjectAmount = source.smsAfProjectAmount || (source.customInfo || {}).smsAfProjectAmount;
			    					
			    					targetValue.customInfo = customInfo;
					    			targetValue.customInfo.project = source;
					    			targetValue.customInfo.projectIds = source.projectId;
					    			targetValue.customInfo.project.projectId = source.projectId;
					    			vm._data.targetValue = targetValue;
					    			
			    					/* $("#projectName", $container).val(source.projectName);
			    					$("#dispatchName", $container).val(source.projectName);
				    				$("#smsProjectCode", $container).val(source.smsProjectCode || (source.customInfo || {}).smsProjectCode);
					    			$("#smsSubmitTime", $container).val(source.smsSubmitTime || (source.customInfo || {}).smsSubmitTime);
					    			$("#smsProjectAmount", $container).val(source.smsProjectAmount || (source.customInfo || {}).smsProjectAmount);
					    			$("#smsAfProjectAmount", $container).val(source.smsAfProjectAmount || (source.customInfo || {}).smsAfProjectAmount); */
				    			} catch(e){}
			    			});
			    		});
			    		// 检查是否已派单
			    		if (!_this.checkDispatched) {
			    			_this.checkDispatched = $router.methods.checkDispatched;
			    		}
			    		_this.checkDispatched.call(_this, data, $container);
					},
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
			startProcess: function(el, data, callback, ignoreForm) {
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
				sys.common.startProcess.call(this, el, entity, callback, ignoreForm);
			},
			checkDispatched: function(data, $container) {
				var _this = this;
    			// 如果已经派单则不允许修改项目和服务商
	    		var isDispatched = (data.targetValue || {}).dispatched;
	    		var dispatchType = (data.targetValue || {}).type;
	    		console.log("checkDispatched", isDispatched);
	    		if (isDispatched == true) {
	    			$("#projectIds", $container).attr("disabled", true);
	    			/*$("#dispatchType", $container).attr("disabled", true);
	    			$("#profitDepCode", $container).attr("disabled", true);
	    			$("#dispatchAmount", $container).attr("disabled", true);*/
	    			$("#facilitatorId", $container).attr("disabled", true);
	    			$("#dispatchSeq", $container).attr("disabled", true);
	    			if (dispatchType == 'frameworkAgreement') {
		    			$("#dispatchNo", $container).attr("disabled", true);
	    			} else {
	    				$("#dispatchNo", $container).attr("disabled", false);
	    			}
	    		}
	    		if (!_this.changeFacilitator && $router.methods.changeFacilitator) {
	    			_this.changeFacilitator = function(e) {
	    				$router.methods.changeFacilitator.call(this, e, $container);
	    			}
	    		}
	    		var changeFacilitator = _this.changeFacilitator;
	    		$($container).off("change", "#facilitatorId", changeFacilitator);
	    		if ((data.targetValue || {}).facilitatorId && !isDispatched) {
		    		//$($container).one("change", "#facilitatorId", changeFacilitator);
		    		$($container).on("change", "#facilitatorId", changeFacilitator);
	    		} else {
	    			$($container).one("click", "#facilitatorId~.select2-container", function(e) {
	 					$("#facilitatorId", $container).on("change", changeFacilitator)
		    		});
	    		}
    		},
    		changeFacilitator: function(e, $container) {
    			console.log("changeFacilitator");
    			var $this = $(this);
				var source = $this.select2('data');
    			if (source.length > 0) {
    				source = source[0]; 
   				} else {
   					source = {}; 
 				}
    			var element = source.element;
				var source = $(element).data("source") || source.source || source || {};
				var oldCode = $("#facilitatorCode", $container).val();
				$("#facilitatorCode", $container).val(source.code);
    			$("#facilitatorName", $container).val(source.name);
    			$("#bankInfo", $container).val(source.bankInfo);
    			$("#bankAccount", $container).val(source.bankAccount);
    			
   				var render = function(data) {
   					console.log("changeFacilitatorRender", data);
    				var placeholder = $("#dispatchSeq", $container).data("placeholder") || $("#dispatchSeq", $container).attr("placeholder");
    				var newPlaceholder = data.dispatchSeq || placeholder;
    				$("#dispatchSeq", $container).data("placeholder", placeholder);
    				$("#dispatchSeq", $container).attr("placeholder", newPlaceholder);
    				var dispatchSeq = $("#dispatchSeq", $container).val();
    				if (dispatchSeq != newPlaceholder) {
    					$("#dispatchSeq", $container).val("");
    				} else {
	    				//$("#dispatchSeq", $container).val(newPlaceholder == dispatchSeq ? dispatchSeq : "");
    				}
    				
    				var dispatchNoPlaceholder = $("#dispatchNo", $container).data("placeholder") || $("#dispatchNo", $container).attr("placeholder");
    				var newdispatchNoPlaceholder = data.dispatchNo || dispatchNoPlaceholder;
    				$("#dispatchNo", $container).data("placeholder", dispatchNoPlaceholder);
    				$("#dispatchNo", $container).attr("placeholder", data.dispatchNo || dispatchNoPlaceholder);
    				var dispatchNo = $("#dispatchNo", $container).val();
    				if (dispatchNo != newdispatchNoPlaceholder) {
    					$("#dispatchNo", $container).val("");
    				} else {
	    				//$("#dispatchNo", $container).val(newdispatchNoPlaceholder == dispatchNo ? dispatchNo : "");
    				}
   				};
    			if (source.code) {
    				ajaxGet($router.api.generateDispatchSeq(), {facilitatorCode: source.code}, render);
    			} else if (source.selected) {// 选择空项时
    				render({});
    			} else {// 初始化时
    				render({
    					dispatchSeq: $("#dispatchSeq", $container).val() || "",
    					dispatchNo: $("#dispatchNo", $container).val() || "",
   					});
    			}
    		}
		}
	});
	return $router;
}();

/**
 * 结算管理
 */
$.initRouter("pm.settlement");
pm.settlement = function() {
	var namespace =  ctx + "/pm/settlement";
	var commonRouter = pm.common(namespace);
	var $router = $.extend(true, {}, commonRouter, {
		api:((namespace) => {
			return {
				submit:() => namespace + "/submit.json",
				generateSettleSeq:()=> namespace + "/generateSettleSeq.json"
			};
		})(namespace),
		html: ((namespace) => {
			return {
				detail: (settleId, isModals, row) => {
					var rowId = row ? row.id : settleId;
	                var url = null;
	                if (!rowId) {
	                 	var dispatch = row.dispatch || {};
	                 	var dispatchId = dispatch.id || row.dispatchId || "";
	                 	url = commonRouter.html.create($.param({dispatchId}), isModals);
	                } else {
	                 	url = commonRouter.html.detail(rowId, isModals);
	                }
	                return url;
				},
				exportProjectInfo: (settleId, exportType) => {
					var exportType = exportType || "doc";
					return namespace + "/" + settleId + "/projectInfoDoc.html";
				},
			};
		})(namespace),
		callback: ((namespace) => {
			return {
				list: {
					vueCallback: function(dataTable, $container) {
						// 添加Excel导出按钮
						var config = dataTable.config || {};
						config.exportData = {
		                	url: router(urlNamespace).api(model).list().replace(".json", ".xlsx"),
		                	fileName: "外派结算",
		                	type: ["excel"]
		                };
						dataTable.config = config;
						dataTable.exportData = config.exportData;
						dataTable.exporting = true;
					}
				},
				detail: {
					vueCallback: function(data, $container) {
						var _this = this;
						var vm = _this;
						var search = vm.pathSearch || '';
						// 服务商Select2初始化完成之后，添加change事件，避免直接添加change事件，无法获取原始保存的服务商信息
			    		var dataCacheAdapter = $.fn.select2.amd.require('select2/data/dataCacheAdapter');
			    		var selectedId = (data.targetValue || {}).dispatchId;
			    		//var selectedText = (data.targetValue || {}).dispatchSeq;
			    		var selectedText = ((data.targetValue || {}).dispatch || {}).dispatchNo;
			    		$("#dispatchId", $container).select2({
		    			    dataAdapter: dataCacheAdapter,// 数据分页缓存适配器，在base-form中定义
			    			allowClear: true,
			    			dropdownAutoWidth:true,
			    			data: selectedId ? [$.extend({id: selectedId, text: selectedText}, data.targetValue.dispatch)] : [],// 设置初始值
			    			ajax: {
			    			    url: basePath + "/pm/dispatch/listWithSettleInfo.json?" + search,
			    			    dataType: 'json',
			    			    delay: 250,
			    			    data: function (params) {
			    			    	params.pageSize = 10;
				    			    return {
				    			    	// search term
				    			        //dispatchSeq: params.term,
				    			        dispatchNo: params.term,
				    			        fuzzySearch: true,
				    			        pageSize: params.pageSize || 10,
				    			        start: (params.page - 1) * params.pageSize || 0
				    			    };
			    			    },
			    			    processResults: function (data, params) {
			    			      	params.page = params.page || 1;
								  	var list = data.data || [];
								  	var results = $.map(list, function (obj) {
								  		obj.id = obj.id;
								  		//obj.text = obj.dispatchSeq;
								  		obj.text = obj.dispatchNo;
									  	return obj;
									});
			    			      	return {
			    			        	results: results,
			    			        	pagination: {
			    			          		more: (params.page * (params.pageSize || 10)) < data.pageParam.filtered
			    			       		}
			    			      	};
			    			    },
			    			    cache: true
			    			},
			    			//placeholder: '搜索派单编号',
			    			placeholder: '搜索转包合同号',
			    			minimumInputLength: 4,
			    			templateResult: function(repo) {
			    				if (repo.loading) {
									return repo.text;
								}
								
								var $container = $(
									"<div class='select2-result-repository clearfix'>" +
								      "<div class='select2-result-repository__meta'>" +
								        "<div class='select2-result-repository__title'></div>" +
								        "<div class='select2-result-repository__description'></div>" +
								        "<div class='select2-result-repository__statistics'>" +
								          "<div class='select2-result-repository__smsSubmitTime'></div>" +
								          "<div class='select2-result-repository__smsProjectAmount'></div>" +
								        "</div>" +
								      "</div>" +
								    "</div>"
								);
								
								$container.find(".select2-result-repository__title").append("<div style='margin-right:1rem'>" + repo.dispatchSeq + "</div>");
								$container.find(".select2-result-repository__title").append("<div>" + (repo.dispatchNo || "") + "</div>");
								$container.find(".select2-result-repository__description").text(repo.smsProjectName || repo.dispatchName);
								$container.find(".select2-result-repository__smsSubmitTime").append(repo.smsSubmitTime || (repo.customInfo || {}).smsSubmitTime);
								$container.find(".select2-result-repository__smsProjectAmount").append(repo.smsProjectAmount || (repo.customInfo || {}).smsProjectAmount);
								
								return $container;
			    			},
			    			templateSelection: function(repo) {
		    	    			return repo.projectName || repo.text;
		    	    		},
			    		});
			    		/* if (dispatchIdPlaceholder) {
			    			$(".select2-selection__placeholder").css("color", 'inherit');
			    		} */
			    		
			    		if (!_this.generateSettleSeq && $router.methods.generateSettleSeq) {
			    			_this.generateSettleSeq = function(e) {
			    				$router.methods.generateSettleSeq.call(this, e, $container);
			    			}
			    		}
			    		var generateSettleSeq = _this.generateSettleSeq;
			    		// 项目名称初始化完成之后，添加change事件，避免直接添加change事件，无法获取原始保存的信息
			    		$("#dispatchId", $container).siblings(".select2-container").one("click", function(e) {
			    			$("#dispatchId", $container).on("change", function(e){
			    				try{
			    					var source = $(this).select2("data");
			    					if (source.length > 0) {
			    						source = source[0];
			    					} else {
			    						source = {};
			    					}
			    					console.log(source, this.value);
			    					/* $("#smsProjectName", $container).val(source.smsProjectName || source.dispatchName);
			    					$("#contractNos", $container).val(source.contractNos);
			    					$("#dispatchSeq", $container).val(source.dispatchSeq);
				    				$("#smsProjectCode", $container).val(source.smsProjectCode);
				    				$("#smsOrderExecNumber", $container).val(source.smsOrderExecNumber || (source.customInfo || {}).smsOrderExecNumber);
					    			$("#smsSubmitTime", $container).val(source.smsSubmitTime);
					    			$("#smsProjectAmount", $container).val(source.smsProjectAmount); */
					    			var targetValue = vm._data.targetValue || {};
					    			targetValue.dispatch = source;
					    			targetValue.dispatchId = source.id;
					    			targetValue.dispatchSeq = source.dispatchSeq;
					    			targetValue.dispatch.dispatchNo = source.dispatchNo;
					    			
					    			/* var customInfo = source.customInfo || {};
					    			targetValue.dispatch.customInfo = customInfo;
					    			targetValue.dispatch.customInfo.projectProgress = (source.customInfo || {}).projectProgress; */
					    			vm._data.targetValue = targetValue;
					    			
					    			// 生成结算编号
					    			generateSettleSeq();
				    			} catch(e){}
			    			});
			    		});
			    		// 绑定当次付款比例、当次付款金额的change事件，生成结算编号
			    		$("#ratio,#amount", $container).change(generateSettleSeq);
					},
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
				var canStart = !hasTask && data.id > 0 && !data.settled;
				return canStart;
			},
			startProcess: function(el, data, callback, ignoreForm) {
				var _this = this;
				data = data || $(el).data("entity");
		    	if (!data) {
		        	return false;
		    	}
		    	try {
		    		data = JSON.parse(data.replace(/'/g, '"'));
		    	} catch(e) {}
		    	
				var entity = {
					processKey: "SubcontractInspection",
					objId: data.dispatchId,
					objType: 'dispatch',
					dataId: data.id,
					dataType: "settlement"
	            };
				sys.common.startProcess.call(this, el, entity, callback, ignoreForm);
			},
			generateSettleSeq(e, $container) {
    			var dispatchSeq = $("#dispatchSeq", $container).val();
    			var smsProjectName = $("#smsProjectName", $container).val();
    			var ratio = Number($.trim($("#ratio", $container).val()).replace("%", ""));
    			var amount = $("#amount", $container).val();
    			
    			$("#ratio", $container).val(ratio);
    			
    			if ($(this)[0] == $("#ratio", $container)[0]) {
    				var dispatchAmount = Number($.trim($("#dispatchAmount").val()).replace(",", "")) || 0;
       				amount = (dispatchAmount * ratio / 100).toFixed(2);
       				$("#amount", $container).val(amount);
    			}
   				
    			var settleSeq = [dispatchSeq, smsProjectName, ratio, amount];
    			$("#settleSeq", $container).val(settleSeq.join("-"));
    		}
		},
	});
	return $router;
}();

/**
 * 人员管理
 */
$.initRouter("pm.projectTask");
pm.projectTask = function() {
	var namespace =  ctx + "/pm/project/task";
	var commonRouter = pm.common(namespace);
	var $router = $.extend(true, {}, commonRouter, {
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
						var _this = this;
						_this.startProcessBtnText = "发起安服质量审核";
						var hasTask = (this.targetValue.customInfo || {}).currentTaskId > 0;
						if (_this.footerTipsOld == undefined) {
							_this.footerTipsOld = _this.footerTips;
						}
						if (!hasTask || (_this.canStartProcess && _this.permissionType && _this.permissionType != 'view')) {
							//_this.footerTips = "提示：进度达到100%，可发起安服质量审核，如需研发进行质量审核，请勾选“是”";
							_this.footerTips = "提示：进度达到100%，如需安服质量审核，可点击发起质量审核流程；若内容发生变更将会终止流程";
						} else {
							_this.footerTips = _this.footerTipsOld || "";
						}
					},
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
			startProcess: function(el, data, callback, ignoreForm) {
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
				sys.common.startProcess.call(this, el, entity, callback, ignoreForm);
			}
		}
	});
	return $router;
}();

/**
 * 人员管理
 */
$.initRouter("pm.projectMember");
pm.projectMember = function() {
	var namespace =  ctx + "/pm/member";
	var commonRouter = pm.common(namespace);
	var $router = $.extend(true, {}, commonRouter, {
		api:((namespace) => {
			return {
			};
		})(namespace),
		html: ((namespace) => {
			return {
			};
		})(namespace),
	});
	return $router;
}();

/**
 * 行业资产
 */
$.initRouter("af.industryAsset");
af.industryAsset = function() {
	var namespace =  ctx + "/af/industry/asset";
	var commonRouter = pm.common(namespace);
	var $router = $.extend(true, {}, commonRouter, {
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
			startProcess: function(el, data, callback, ignoreForm) {
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
				sys.common.startProcess.call(this, el, entity, callback, ignoreForm);
			}
		}
	});
	return $router;
}();

/**
 * 行业漏洞
 */
$.initRouter("af.industryLeak");
af.industryLeak = function() {
	var namespace =  ctx + "/af/industry/leak";
	var commonRouter = pm.common(namespace);
	var $router = $.extend(true, {}, commonRouter, {
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
			startProcess: function(el, data, callback, ignoreForm) {
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
				sys.common.startProcess.call(this, el, entity, callback, ignoreForm);
			}
		}
	});
	return $router;
}();

/**
 * 项目资产
 */
$.initRouter("pm.projectAsset");
pm.projectAsset = function() {
	var namespace =  ctx + "/pm/project/asset";
	var commonRouter = pm.common(namespace);
	var $router = $.extend(true, {}, commonRouter, {
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
				var canStart = !this.isCreate && !hasTask && !isNaN(data.status);
				return canStart;
			},
			startProcess: function(el, data, callback, ignoreForm) {
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
				sys.common.startProcess.call(this, el, entity, callback, ignoreForm);
			}
		}
	});
	return $router;
}();

/**
 * 项目资产
 */
$.initRouter("af.industryWarningAsset");
af.industryWarningAsset = function() {
	var namespace =  ctx + "/af/industry/warning/asset";
	var commonRouter = pm.common(namespace);
	var $router = $.extend(true, {}, commonRouter, {
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
	return $router;
}();

/**
 * 项目资产漏洞
 */
$.initRouter("pm.assetLeak");
pm.assetLeak = function() {
	var namespace =  ctx + "/pm/asset/leak";
	var commonRouter = pm.common(namespace);
	var $router = $.extend(true, {}, commonRouter, {
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
			    		$("#assetIds", $container).siblings(".select2-container").one("click", function(e) {
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
				var canStart = !this.isCreate && !hasTask && !isNaN(data.status);
				return canStart;
			},
			startProcess: function(el, data, callback, ignoreForm) {
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
				sys.common.startProcess.call(this, el, entity, callback, ignoreForm);
			}
		}
	});
	return $router;
}();

/**
 * 工作日报
 */
$.initRouter("pm.dailyReport");
pm.dailyReport = function() {
	var namespace =  ctx + "/pm/daily/report";
	var commonRouter = pm.common(namespace);
	var $router = $.extend(true, {}, commonRouter, {
		api:((namespace) => {
			return {
				mailSelect: (mailType, search) => namespace + "/mail/" + mailType + "/select.json" + (search ? "?" + search : "").replace("??", "?"),
				mailReport: (mailType, search) => namespace + "/mail/" + mailType + "/report.json" + (search ? "?" + search : "").replace("??", "?"),
			};
		})(namespace),
		html: ((namespace) => {
			return {
				exportWeekReport: (params) => {
					return namespace + "/export/daily/report.html" + (params ? "?" + $.param(params) : "");
				},
				exportReport: (type, params) => {
					return namespace + "/export/" + type +"/report.html" + (params ? "?" + $.param(params) : "");
				},
				mailSelect: (mailType, isModals) => namespace + (isModals ? "/modals" : "") + "/mail/" + mailType + "/select.html",
			}
		})(namespace),
		callback: ((namespace) => {
			return {
				list: {
					vueCallback: function(dataTable, $container) {
						// 添加复制按钮
						var data = this._data;
						var $addBtn = $("[data-btn-type='add']");
						if($addBtn.length) {
							var $copyBtn = $addBtn.clone();
							$copyBtn.data("data-btn-type", "copy").attr("data-btn-type", "copy").text("复制");
							$addBtn.after($copyBtn);
							
							$copyBtn.on('click', function() {
				                var action = $(this).attr('data-btn-type');
				                var rowId= commonTable.getSelectedRowId();
				                switch (action) {
				                case 'copy':
				                	if(!rowId){
				                        modals.info('请选择要复制的行');
				                        return false;
				                    }
				                	window.location.href = router(urlNamespace).html(model).create($.param({id: rowId}));
				                }
							});
						}
						
						if($addBtn.length) {
							var $mailBtnGroup = $('<div class="btn-group operate-btn-group">  <button type="button" data-btn-type="mailSelect" data-mail-type="daily" class="btn btn-primary">发送日报</button></div>');
							$addBtn.parents(".operate-btn-group:first").before($mailBtnGroup);
							
							var $mailBtn = $mailBtnGroup.find("[data-btn-type='mailSelect']");
							$mailBtn.on('click', function() {
				                var action = $(this).attr('data-btn-type');
				                var mailType = $(this).attr('data-mail-type');
				                switch (action) {
				                case 'mailSelect':
				                	modals.openWin({ 
				                		winId: model + "Win",
				                		title: "请选择需要在邮件中体现的日报记录",
				                		width: "80vw",
										url: router(urlNamespace).html(model).mailSelect(mailType, true)
									});
				                }
							});
						}
						
						var $exportWeekReportBtnGroup = $('<div class="btn-group operate-btn-group">  <button type="button" data-btn-type="exportWeekReport" class="btn btn-info">导出周报</button></div>');
						$addBtn.parents(".operate-btn-group:first").before($exportWeekReportBtnGroup);
						
						var $exportWeekReportBtn = $exportWeekReportBtnGroup.find("[data-btn-type='exportWeekReport']");
						$exportWeekReportBtn.on('click', function() {
			                var action = $(this).attr('data-btn-type');
			                switch (action) {
			                case 'exportWeekReport':
			                	modals.confirm({ 
			                		//winId: model + "Win",
			                		title: "导出周报",
			                		width: "400px",
			                		text: `<form id="exportWeekReportForm" class="form-horizontal" method="post">
						                    <div class="box-body p-0">
						                        <div class="col-sm-12">
						                            <div>
						                                <input type="hidden" name="taskId" value="467773">
						                                <input type="hidden" name="processInstanceId" value="366403">
						                            </div>
						                            <div class="form-group">
						                                <label for="userId" class="col-sm-4 control-label">指定周内日期：</label>
						                                <div class="col-sm-8">
						                                    <input id="processTime" class="form-control" autocomplete="off">
						                                </div>
						                            </div>
						                        </div>
						                    </div>
						                </form>`,
			                		callback: function() {
			                			var processTime = $("#processTime", "#exportWeekReportForm").val() || "";
			                			router.postDownload(pm.dailyReport.html.exportReport("week", {processTime}));
			                		}
									//url: router(urlNamespace).html(model).mailSelect(mailType, true)
								});
			                	var minDate = new Date();
			                	minDate.setFullYear(minDate.getFullYear() - 10);
			                	$("#processTime", "#exportWeekReportForm").daterangepicker({
			                		locale: {
			    	 					format : 'YYYY-MM-DD',
			    	 					applyLabel: '确认',
			    	 					cancelLabel: '取消',
			    	 					fromLabel : '起始时间',
			    	 					toLabel : '结束时间',
			    	 					customRangeLabel : '自定义',
			    	 					firstDay : 1,
			    	 					daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],  
			    	 		            monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月',  
			    	 		                    '七月', '八月', '九月', '十月', '十一月', '十二月' ], 
			    	 				},
			    	 				minDate: minDate,
			    	 				maxDate: new Date(),
			    	 				singleDatePicker: true,
			    	 				showDropdowns: true
			                	});
			                }
						});
					}
				},
				detail: {
					vueCallback: function(data, $container) {
						console.log("vueCallback");
						
						var targetValue = data.targetValue || {};
						var createName = (targetValue.customInfo || {}).createName;
						if (createName) {
							var type = targetValue.type == "plan" ? "计划" : "日报";
							var processTime = targetValue.processTime || "";
							var date = new Date(processTime.replace("-", "/"));
							var weekDays = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
							var day = date.getDay();
							var args = [createName, processTime, weekDays[day], type];
							var title = "<span>{0}</span><small>[{1}]{2}工作{3}</small>".replace(/\{(\d+)\}/g, function(m, i){  
						        return args[i];  
						    });
							$("#pageTitle", $(this.$el)).html(title);
							$("#pageTitle", $(this.$el)).next().hide();
						}
						// 添加项目类型change事件
						$("#projectType", $container).on("change", function(e) {
							var projectType = this.value;
							if (projectType == '30') {
								$container.bootstrapValidator('enableFieldValidators', 'projectId', false);
								$container.find("label[for='projectId'] .redMark").addClass("disabled");
						    } else {
						    	$container.find("label[for='projectId'] .redMark").removeClass("disabled");
						    	$container.bootstrapValidator('enableFieldValidators', 'projectId', true);
						    	$container.bootstrapValidator('validateField', 'projectId');
						    }
						});
						
						var currentDate = new Date();
						var day = currentDate.getDay();
						var nextWeekEndDate = new Date(currentDate.getTime() + (7 + (day ? 7:0) - day)*24*3600*1000);
						$("#type", $container).on("change", function(e) {
							var type = this.value;
							var typeShow = ".type_show_" + type;
								typeHide = ".type_hide_" + type;
							$("[class*='type_show_']", $container).parents(".form-group").hide();
							$("[class*='type_hide_']", $container).parents(".form-group").show();
							$(typeShow, $container).parents(".form-group").show();
							$(typeHide, $container).parents(".form-group").hide();
							
							if (type == 'report') {
								$("[data-flag='datepicker']", $container).each(function() {
									var endDate = $(this).data("dateEndDate");
									$(this).datepicker('setEndDate', endDate);
									$(this).datepicker('update', $(this).val());
								})
							} else {
								$("[data-flag='datepicker']", $container).datepicker('setEndDate', nextWeekEndDate);
							}
							 
						});
						var projectName = (data.targetValue || {}).projectName;
						var selectedId = (data.targetValue || {}).projectId;
			    		//var dataCacheAdapter = $.fn.select2.amd.require('select2/data/dataCacheAdapter');
			    		$.fn.select2.amd.require(["select2/utils", "select2/data/dataCacheAdapter", "select2/data/tags"],
	    				function (Utils, DataCacheAdapter, Tags) {
		    				var dataCacheAdapter = Utils.Decorate(DataCacheAdapter, Tags);
				    		$("#projectId", $container).select2({
			    			    dataAdapter: dataCacheAdapter,// 数据分页缓存适配器，在base-form中定义
				    			allowClear: true,
				    			tags: true,
				    			dropdownAutoWidth:true,
				    			data: selectedId && projectName ? [{id: selectedId, text: projectName}] : [],// 设置初始值
				    			ajax: {
				    			    url: pm.router.api("project").list(),
				    			    dataType: 'json',
				    			    delay: 250,
				    			    data: function (params) {
				    			    	var projectType = $("#projectType", $container).val();
				    			    	if (!projectType) {
				    			    		return false;
				    			    	}
				    			    	var projectTypes = null;
				    			    	if (projectType == "40") {
				    			    		projectTypes = [];
				    			    		$("#projectType option", $container).not("[value='30']").not("[value='40']").each(function(item, index) {
				    			    			$(this).val() && projectTypes.push($(this).val());
			    			    			});
				    			    		projectType = null;
				    			    		projectTypes = projectTypes.join(",");
				    			    	}
				    			    	params.pageSize = 10;
					    			    return $.extend({
					    			    	projectType: projectType,
					    			        fuzzy: params.term, // search term
					    			        fuzzySearch: true,
					    			        pageSize: params.pageSize || 10,
					    			        start: (params.page - 1) * params.pageSize || 0
					    			    }, projectTypes ? {
					    			    	projectTypes: projectTypes
					    			    } : {});
				    			    },
				    			    processResults: function (data, params) {
				    			      	params.page = params.page || 1;
				    			      	var list = data.data || [];
									  	var results = $.map(list, function (obj) {
									  		obj.id = obj.id || obj.projectId || -1;
									  		obj.text = obj.projectName;
										  	return obj;
										});
				    			      	return {
				    			        	results: results,
				    			        	pagination: {
				    			          		more: (params.page * (params.pageSize || 10)) < data.pageParam.filtered
				    			       		}
				    			      	};
				    			    },
				    			    cache: true
				    			  },
				    			  placeholder: '搜索项目名称、项目编码',
				    			  minimumInputLength: 2,
				    			  templateResult: function(repo) {
				    	    			if (repo.loading) {
				    	    				return repo.text;
				    	    			}
	
				    	    			var $container = $(
				    	    				"<div class='select2-result-repository clearfix'>" +
				    	    			      "<div class='select2-result-repository__meta'>" +
				    	    			        "<div class='select2-result-repository__title'></div>" +
				    	    			        "<div class='select2-result-repository__description'></div>" +
	//			    	    			        "<div class='select2-result-repository__statistics'>" +
	//			    	    			          "<div class='select2-result-repository__smsSubmitTime'></div>" +
	//			    	    			          "<div class='select2-result-repository__smsProjectAmount'></div>" +
	//			    	    			        "</div>" +
				    	    			      "</div>" +
				    	    			    "</div>"
				    	    			);
	
				    	    			$container.find(".select2-result-repository__title").append("<div>" + (repo.projectCode || "") + "</div>");
				    	    			$container.find(".select2-result-repository__title").append("<div>" + (repo.contractNo || "") + "</div>");
				    	    			$container.find(".select2-result-repository__description").text(repo.projectName || repo.text);
				    	    			$container.find(".select2-result-repository__forks").append(repo.contractNo);
	//			    	    			$container.find(".select2-result-repository__smsSubmitTime").append((repo.customInfo || {}).smsSubmitTime);
	//			    	    			$container.find(".select2-result-repository__smsProjectAmount").append((repo.customInfo || {}).smsProjectAmount);
	
				    	    			return $container;
				    	    	  },
				    			  templateSelection: function formatRepoSelection (repo) {
				    				  return repo.projectName || repo.text;
			    	    		  }
				    		});
			    		
				    		// 项目名称初始化完成之后，添加change事件，避免直接添加change事件，无法获取原始保存的信息
				    		$("#projectId", $container).on('select2:open', function(event) {
				    			var $select = $(this).parent();
				    			var $searchfield = $select.find('.select2-search__field');
				    			$searchfield.length || ($searchfield = $(`#select2-${this.id}-results`, $container).parents(".select2-dropdown:first").find(".select2-search__field"));
				    			setTimeout(function() {
				    				$searchfield.val($select.find('option:selected[value!=""]').text()).trigger("input");
				    			}, 2);
				    		});
				    		var projectChangeFunc = function(e){
			    				try{
			    					var source = $(this).select2("data");
			    					if (source.length > 0) {
			    						source = source[0];
			    					} else {
			    						source = {};
			    					}
			    					console.log(source, this.value);
			    					var isTag = $(source.element).data("select2Tag");
			    					if (isTag || source.id == source.text) {
			    						$("[value='-1']").attr("value", "0");
			    						$(source.element).attr("value", -1);
			    						this.value = -1;
			    					}
//					    			var targetValue = vm._data.targetValue || {};
//					    			targetValue.projectId = $("#projectId").val();
//					    			vm._data.targetValue = targetValue;
					    			$("#projectName", $container).val(source.projectName || source.text);
					    			$("#projectCode", $container).val(source.projectCode || "");
					    			$("#contractNo", $container).val(source.contractNo || "");
					    			$("#projectType", $container).val(source.projectType || $("#projectType", $container).val()).trigger("change");
					    			$("#officeCode", $container).val(source.column001 || $("#officeCode", $container).val()).trigger("change");
					    			var programManagerCode = "";
					    			var programManagerName = "";
					    			// 当初始化时，返回已填内容，如果是选择变更，这更新为项目对应的内容
					    			if (!this.value) {
					    				programManagerCode = $("#programManagerCode", $container).val();
					    				programManagerName = $("#programManagerName", $container).val();
					    			}
					    			$("#programManagerCode", $container).val((source.customInfo || {}).programManagerCode || programManagerCode).trigger("change");
					    			$("#programManagerName", $container).val((source.customInfo || {}).programManagerCodeforjson || programManagerName).trigger("change");
				    			} catch(e){}
			    			}
				    		$("#projectId", $container).siblings(".select2-container").one("click", function(e) {
				    			//$("#projectId", $container).on("change", projectChangeFunc);
				    			$("#projectId", $container).on("select2:select", projectChangeFunc);
				    		});
				    		// 手动调用查询，进行初始值回填更新
				    		var $projectId = $("#projectId", $container);
				    		var $select2 = $projectId.data("select2");
				    		if ($select2) {
				    			$select2.dataAdapter.query({term: projectName}, function (data) {
				    				console.log(data);
				    				var results = data.results || [];
				    				var result = null;
				    				for(var i in results) {
				    					if (results[i].projectId == selectedId) {
				    						result = results[i];
				    						break;
				    					}
				    				}
				    				if (result) {
				    					$projectId.empty();
				    					$select2.dataAdapter.select(result);
				    					projectChangeFunc.call($projectId);
				    				}
				    			});
				    		}
				    		
			    		}, null, true);
			    		
			    		// 如果是创建页面，在保存按钮之后添加继续添加按钮
			    		if (this.isCreate) {
			    			if ($("[data-btn-type='continue']", $container).length == 0) {
			    				var $continueBtn = $('<button type="button" data-btn-type="continue" class="btn btn-success" style="margin-left: 0.29rem;">保存并继续</button>');
			    				$("[data-btn-type='save']", $container).parent().append($continueBtn);
			    				var vm = this;
			    				$("[data-btn-type='continue']", $container).click(function() {
			    					var $bootstrapValidator = $container.data('bootstrapValidator');
			    					var isValid = $bootstrapValidator.validate().isValid();
			    					if (isValid) {
				    					var index3 = layer.load(1);
				    					var form = $container.data("baseForm");
			                			var formData = form.getFormSimpleData();
			                			var url = router(vm.urlNamespace).api(vm.model).create();
			                			$("[type='submit'], [data-btn-type]", $container).attr("disabled", true);
			                			ajaxPost(url, formData, function(data,status){
			                				if(data.status){
			                					modals.correct("保存成功，请继续添加下一条");
			                					var targetValue = {};
			                					var $inheritFields = $container.find(".save2continue_inherit_field");
			                					$inheritFields.each(function() {
			                						var name = $(this).attr("name");
			                						var value = $(this).val();
			                						targetValue = form.assemblyData(targetValue, name, value);
			                					});
			                					vm._data.targetValue = targetValue;
			                					$bootstrapValidator.resetForm()
			                					form.initFormData(data.targetValue);
			                				} else{
			                					modals.error('操作失败！<br>' + (data.message || ""));
			                				}
			                			},null,null,function(){
			                				layer.close(index3);
			                				$("[type='submit'], [data-btn-type]", $container).removeAttr("disabled");
			                			})
			                		}
			    				});
			    			}
			    		} else if (data.status){
			    			if ($("[data-btn-type='exportWeekDailyReport']", $container).length == 0) {
			    				var $btn = $('<button type="button" data-btn-type="exportWeekDailyReport" class="btn btn-info" style="margin-left: 0.29rem;">导出本周日报记录</button>');
			    				$(".box-footer>.pull-left", $container).append($btn);
			    				var vm = this;
			    				$("[data-btn-type='exportWeekDailyReport']", $container).click(function() {
			    					var $btn = $(this);
			    					var submitCallback = function() {
			    						var processTime = $("#processTime", $container).val();
				    					var createBy = targetValue.createBy;
				    					$btn.button("loading");
				    					router.postDownload(pm.dailyReport.html.exportWeekReport({createBy, processTime}));
				    					setTimeout(function() {
				                       	 	$btn.button("reset");
				                        }, 2000);
			    					};
			    					$container.data("submitCallback", submitCallback);
			    					var formSubmit = $container.data("formSubmit");
			    					var $submitButton = $container.find("[data-btn-type='save']");
			    					if (typeof formSubmit == "function") {
			    						var $bootstrapValidator = $container.data('bootstrapValidator');
			    						$submitButton = $bootstrapValidator.$submitButton || $submitButton;
			    						if ($submitButton == null || $submitButton.length == 0) {
		    			        			$container.data("submitCallback", null);
		    			        			submitCallback.call($container);
			    						} else {
			    							$bootstrapValidator.$submitButton = null;
			    							formSubmit.call(vm, $bootstrapValidator, $container, $submitButton, true);
			    						}
			    					} else {
			    						$submitButton.click();
//			    						$container.submit();
			    					}
			    				});
			    			}
			    		}
					}
				}
			};
		})(namespace),
		methods: {
			canStartProcess: function(data) {
//				var hasTask = !!(data.customInfo || {}).currentTaskId;
//				var isReported = data.isReported || false;
//				var canStart = !hasTask && isReported && !isNaN(data.status);
//				return canStart;
				return false;
			},
			startProcess: function(el, data, callback, ignoreForm) {
				modals.info("暂无日报审核流程");
//				var _this = this;
//				data = data || $(el).data("entity");
//		    	if (!data) {
//		        	return false;
//		    	}
//		    	try {
//		    		data = JSON.parse(data.replace(/'/g, '"'));
//		    	} catch(e) {}
//		    	
//				var entity = {
//					processKey: "QualityApproveTrack",
//					objId: data.projectId,
//					objType: 'project',
//					dataId: data.leakId,
//					dataType: "industryLeak"
//	            };
//				sys.common.startProcess.call(this, el, entity, callback, ignoreForm);
			}
		}
	});
	return $router;
}();

/**
 * 服务商管理
 */
$.initRouter("pm.facilitator");
pm.facilitator = function() {
	var namespace =  ctx + "/pm/facilitator";
	var commonRouter = pm.common(namespace);
	var $router = $.extend(true, {}, commonRouter, {
		api:((namespace) => {
			return {
			};
		})(namespace),
		html: ((namespace) => {
			return {
			}
		})(namespace),
		callback: ((namespace) => {
			return {
				list: {
					vueCallback: function(dataTable, $container) {
						// 添加导出按钮
						dataTable.exporting = true;
						dataTable.exportData = {
		                	url: router(urlNamespace).api(model).list().replace(".json", ".xlsx"),
		                	fileName: "服务商信息",
		                	type: ["excel"]
		                };
						// 添加复制按钮
						var data = this._data;
						var $addBtn = $("[data-btn-type='add']");
						if($addBtn.length) {
							var $importBtn = $addBtn.clone();
							$importBtn.data("data-btn-type", "import").attr("data-btn-type", "import").text("导入");
							$addBtn.before($importBtn);
							
							$importBtn.on('click', function() {
				                var action = $(this).attr('data-btn-type');
				                var rowId= commonTable.getSelectedRowId();
				                switch (action) {
				                case 'import':
				               	 	modals.openWin({
				                         winId: winId,
				                         title:'导入服务商信息',
				                         width: '75vw',
				                         url: router(urlNamespace).html(model).import(null, true)
				                    });
				                    break;
				                }
							});
						}
					}
				},
			}
		})(namespace),
	});
	return $router;
}();

/**
 * 流程
 */
$.initRouter("workflow");
workflow = function() {
	var namespace =  ctx + "/workflow";
	var commonRouter = pm.common(namespace);
	var $router = $.extend(true, {}, commonRouter, {
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
			return {
				detail: {
					vueCallback: function(data, $container) {
						var _this = this;
						var vm = _this;
						var targetObjType = (data.workflow || {}).objType;
						var targetModel = (data.workflow || {}).dataType;
						var targetRouter = null;
						for (var namespace in cachedRouters) {
							var regex = new RegExp("\\b" + targetModel + "\\b");
							if (regex.test(namespace)) {
								targetRouter = cachedRouters[namespace];
								break;
							}
						}
						var vueCallback = null;
						if (targetRouter) {
							vueCallback = (targetRouter.callback.detail || {}).vueCallback;
						} else {
							var urlNamespace =  _this.urlNamespace;
							if (router(urlNamespace).callback(targetModel).detail) {
								vueCallback = (router(urlNamespace).callback(targetModel).detail || {}).vueCallback;
							}
						}
						if (typeof vueCallback == 'function') {
							vueCallback.call(_this, data, $("#taskEntityFormDiv"));
						}
					}
				}
			}
		})(namespace),
	});
	return $router;
}();
$.initRouter("workflowTask");
workflowTask = function() {
	var namespace =  ctx + "/workflow/task";
	var commonRouter = pm.common(namespace);
	var $router = $.extend(true, {}, commonRouter, {
		api:((namespace) => {
			return {
			};
		})(namespace),
		html: ((namespace) => {
			return {
			};
		})(namespace),
		callback: workflow.callback,
	});
	return $router;
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
		url = url + (url.indexOf("?") != -1 ? "&" : "?") + "__RequestVerificationToken=" + __RequestVerificationToken;
	}
	form.action = url;
	$("body").append(form);
	form.submit();
	$(form).remove();
}