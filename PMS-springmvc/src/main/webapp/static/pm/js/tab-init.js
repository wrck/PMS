/**
 * 该js，用于tab页面加载数据
 */
var calloutColor = ["border-primary", "border-info", "border-success", "border-teal", "border-warning", "border-purple", "border-orange", "border-maroon"];
var colorSize = calloutColor.length - 1;
var lazyLoadEvent = true;
	
// tab切换
$(function(){
	/*$(document).on('shown.bs.tab', 'a[data-toggle="tab"]', function (e) {
		// 获取已激活的标签页的名称
		var activeTab = $(e.target).text(); 
		// 获取前一个激活的标签页的名称
		var previousTab = $(e.relatedTarget).text(); 
		var tabId = $(e.target).attr("href");
		var $container = $(e.target).parents(".tab-content:first");
		if($(tabId, $container).hasClass("loaded") == '' && !$(tabId + " .overlay:first", $container).hasClass("loading")){
            $(tabId + " .overlay", $container).addClass("loading");
            var config = $(tabId, $container).data("config") || $(tabId, $container).data();
            config.container = $container;
			initTabData(config);
		} else {
			try {
				$(tabId, $container).find(".dataTables_scrollBody table").dataTable().api().columns.adjust();
			} catch(e) {}
		}
	});*/
	/*tabInterval = setInterval(() => {
		if($('a[data-toggle="tab"]').length > 0) {
			$('a[data-toggle="tab"]:first').click();
			clearInterval(tabInterval);
		}
	}, 500);*/
});

/**
 * config: {
 * 	url, // 数据源
 * 	type, // 数据类型
 * 	drawType, // 渲染类型，默认json,绘制表格
 * 	params,// 参数
 * 	tableConfig,// dataTableConfig配置
 * 	operations, // 操作
 * },
 * refresh: 刷新
 */
function initTabData(config, refresh, navTab) {
	if (!config) {
		return;
	}
	var type = config.type;
	var drawType =  config.drawType || "json";
	var url = (navTab || {}).url || config.url;
	var params = config.params;
	var timestamp = config.timestamp || "";
	var navTabId = type + "Tab" + timestamp;
	var $container = config.container || $(navTabId).parents(".tab-content:first");
	var $navTab = $("#" + navTabId, $container);
	var vm = $navTab.data("vm") || $container.data("vm") || this;
	var searchDiv = type + "SearchDiv" + timestamp;
	var tableId = type + "Table" + timestamp;
	var calloutId = type + "Callout" + timestamp;
	var tableConfig = config.tableConfig || {};
	var operations  = config.operations || [];
	var defaultConfig = {
			searchDiv: searchDiv,
            stateSave: false,
            searching: true,
            paging: true,
            rowId: 'id',
            info: true
        }
	$(".box-body .overlay", $navTab).show();
	if (drawType == 'json') {
		ajaxGet(url, params, function(resultMap) {
    		var columns = resultMap.columns || (resultMap.pageParam || {}).columns || [];
    		var data = resultMap.data;
    		var localTable = $("#" + tableId, $container).data("localTable");
    		vm.permissionType = resultMap.permissionType || "";
    		vm.permissions = resultMap.permissions || [];
//    		console.log(vm);
//    		var tabList = vm.tabList || [];
//    		for (var i = 0; i < tabList.length; i++) {
//				var tempTab = tabList[i];
//				if (tempTab.id == navTab.id) {
//					tempTab.permissionType = resultMap.permissionType || "";
//					break;
//				}
//			}
//    		vm.tabList = tabList;
    		if (refresh && localTable) {
//    			$("#" + tableId, $navTab).empty();
//    			$(".box-body #" + calloutId, $navTab).empty();
    			localTable.reloadRowData(data, localTable.getSelectedRowId());
    		} else {
    			tableConfig.columns = columns;
    			if (!refresh) {
    				$(".box-body", $navTab).append("<div class='callout border-1 " +calloutColor[Math.round(Math.random()*colorSize)] +"' id='" + calloutId + "'></div>");
    				$(".box-body #" + calloutId, $navTab).append("<table class='table table-bordered table-striped-vertical table-hover' id='" + tableId + "'></table>");
    			}
    			if (data) {
    				//$(".box-body #" + calloutId, $navTab).append("<h4>" + config.title + "</h4>")
    				var localTable = new CommonLocalTable(tableId, data, $.extend(true, {}, defaultConfig, tableConfig));
    				//$(".box-body  #" + calloutId + " h4", $navTab).appendTo($("#" + tableId +  "_wrapper .row:first > :first"));
    				$("#" + tableId, $container).data("localTable", localTable);
    				/*try {
		            	if (operations) {
		            		for (var i = 0; i < operations.length; i++) {
								var operation = operations[i];
								$operate = $("<button class='pull-right btn btn-primary ml-1' data-id='"+ operation.Id +"'>" + operation.text + "</button>");
								
								var events = operation.events || {};
								for ( var key in events) {
									$operate.on(key, events[key]);
								}
								$("#" + tableId + "_wrapper .row:first > :last").append($operate);
							}
		            	}
		            } catch(e) {
		            }*/
    			} else {
    				$(".box-body #" + tableId, $navTab).html("<tr><td>没有可显示的数据</td></tr>")
    			}
    		}
    		$(".box-body .overlay", $navTab).hide();
    		$navTab.addClass("loaded");
		});
	} else {
		$(".box-body", $navTab).append("<div class='callout border-1 " +calloutColor[Math.round(Math.random()*colorSize)] +"' id='" + calloutId + "'></div>");
		var $warpper = $(".box-body #" + calloutId, $navTab);
		$.ajax({
            url: url,
            type: "get",
            dataType: "html",
            data: params,
            success: function(data){
//                data = data.substring(data.indexOf("<body"),data.indexOf("</body>")+7);
                $warpper.html(data);
                if (typeof navTab.processCallback == "function") {
                	data = navTab.processCallback.call(vm, config, navTab, $warpper);
                }
            },
            error:function(XMLHttpRequest,textStatus,errorThrown){
            	$warpper.html("获取数据失败！错误信息如下：<br>"+XMLHttpRequest.responseText);
            },
            complete:function(data){
            	$(".box-body .overlay", $navTab).hide();
        		$navTab.addClass("loaded");
            }
	    })
	}
}

function commonNavTableEdit(e, navTab) {
	var urlNamespace = navTab.urlNamespace || window.urlNamespace;
	var type = navTab.model || navTab.type;
	var title = navTab.title || "记录";
	var width = navTab.modalWidth;
	var $target = $(e.target);
	var localTable = $target.parents(".dataTables_wrapper:first").find("table.dataTable").data("localTable");
	var rowId = navTab.rowId || localTable.getSelectedRowId();
	if (!rowId) {
		modals.info('选择需要编辑的行');
		return;
	}
	var url = null;
	try {
		url = router(urlNamespace).html(type).detail(rowId,true);
	} catch(e) {
		console.error(e);
		try {
			url = pm.router.html(type).detail(rowId, true);
		} catch(e){
			console.error(e);
		}
	}
	if (!url) {
		modals.error('不支持该操作！');
		return;
	}
	console.log(1);
	if (rowId && url) {
		modals.openWin({
			winId : type + "Win",
			title : '编辑' + title,
			width : width || '75vw',
			url : url,
			contentClass : '.modal-content',
			hideFunc : function(e) {
				var config = $target.parents(".tab-pane:first").data();
				initTabData(config, true);
			}
		});
	}
}

function commonNavTableDelete(e, navTab) {
	var urlNamespace = navTab.urlNamespace || window.urlNamespace;
	var type = navTab.model || navTab.type;
	var $target = $(e.target);
	var localTable = $target.parents(".dataTables_wrapper:first").find("table.dataTable").data("localTable");
	var rowId = navTab.rowId || localTable.getSelectedRowId();
	if (!rowId) {
		modals.info('请选择要删除的行');
		return;
	}
	var url = null;
	try {
		url = router(urlNamespace).api(type).delete(rowId);
	} catch(e) {
		console.error(e);
		try {
			url = pm.router.api(type).delete(rowId);
		} catch(e){
			console.error(e);
		}
	}
	if (!url) {
		modals.error('不支持该操作！');
		return;
	}
    modals.confirm("是否要删除该行数据？",function(){
        ajaxPost(url, null, function(data, status){
            if(data.status){
                modals.info(data.message || "删除成功！");
                var config = $target.parents(".tab-pane:first").data();
				initTabData(config, true);
            }else{
                modals.info(data.message || "删除失败！");
            }
        });
    })
}

function commonNavTableDownload(e, navTab) {
	var urlNamespace = navTab.urlNamespace || window.urlNamespace;
	var type = navTab.model || navTab.type;
	var $target = $(e.target);
	var uploadPath = navTab.uploadPath;
	var paramsValue = navTab.paramsValue || {};
	var localTable = $target.parents(".dataTables_wrapper:first").find("table.dataTable").data("localTable");
	var rowId = navTab.rowId || localTable.getSelectedRowId();
	if (!rowId) {
		modals.info('选择需要下载的文件');
		return;
	}
	var url = uploadPath;
	paramsValue.ids = rowId;
	try {
		url = url || router(urlNamespace).html(type).download($.param(paramsValue));
	} catch(e) {
		console.error(e);
		try {
			url = url || pm.router.html(type).download($.param(paramsValue));
		} catch(e){
			console.error(e);
		}
	}
	if (!url) {
		modals.error('不支持该操作！');
		return;
	}
	console.log(1);
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

function commonNavTableZipDownload(e, navTab) {
	var urlNamespace = navTab.urlNamespace || window.urlNamespace;
	var type = navTab.model || navTab.type;
	var $target = $(e.target);
	var uploadPath = navTab.uploadPath;
	var paramsValue = navTab.paramsValue || {};
	var localTable = $target.parents(".dataTables_wrapper:first").find("table.dataTable").data("localTable");
	var rowId = navTab.rowIds || localTable.getCheckedRowsId() || localTable.getSelectedRowsId() || [];
	if (!rowId) {
		modals.info('选择需要下载的文件');
		return;
	}
	var url = uploadPath;
	paramsValue.ids = rowId.join(",");
	try {
		url = url || router(urlNamespace).html(type).download($.param(paramsValue));
	} catch(e) {
		console.error(e);
		try {
			url = url || pm.router.html(type).download($.param(paramsValue));
		} catch(e){
			console.error(e);
		}
	}
	if (!url) {
		modals.error('不支持该操作！');
		return;
	}
	console.log(1);
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