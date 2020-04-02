/**
 * 该js，用于tab页面加载数据
 */
var calloutColor = ["border-primary", "border-info", "border-success", "border-teal", "border-warning", "border-purple", "border-orange", "border-maroon"];
var colorSize = calloutColor.length - 1;
var lazyLoadEvent = true;
	
// tab切换
$(function(){
	$(document).on('shown.bs.tab', 'a[data-toggle="tab"]', function (e) {
		// 获取已激活的标签页的名称
		var activeTab = $(e.target).text(); 
		// 获取前一个激活的标签页的名称
		var previousTab = $(e.relatedTarget).text(); 
		var tabId = $(e.target).attr("href");
		if($(tabId).hasClass("loaded") == '' && !$(tabId + " .overlay:first").hasClass("loading")){
            $(tabId + " .overlay").addClass("loading");
            var config = $(tabId).data("config") || $(tabId).data();
			initTabData(config);
		} else {
			try {
				$(tabId).find(".dataTables_scrollBody table").dataTable().api().columns.adjust();
			} catch(e) {}
		}
	});
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
function initTabData(config, refresh) {
	if (!config) {
		return;
	}
	var type = config.type;
	var drawType =  config.drawType || "json";
	var url = config.url;
	var params = config.params;
	var navTabId = type + "Tab";
	var $navTab = $("#" + navTabId);
	var searchDiv = type + "SearchDiv";
	var tableId = type + "Table";
	var calloutId = type + "Callout";
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
		ajaxPost(url, params, function(resultMap) {
    		var columns = resultMap.columns || (resultMap.pageParam || {}).columns || [];
    		var data = resultMap.data;
    		if (refresh) {
//    			$("#" + tableId, $navTab).empty();
//    			$(".box-body #" + calloutId, $navTab).empty();
    			var localTable = $("#" + tableId).data("localTable");
    			localTable.reloadData(data);
    		} else {
    			tableConfig.columns = columns;
    			$(".box-body", $navTab).append("<div class='callout border-1 " +calloutColor[Math.round(Math.random()*colorSize)] +"' id='" + calloutId + "'></div>");
    			$(".box-body #" + calloutId, $navTab).append("<table class='table table-bordered table-striped table-hover' id='" + tableId + "'></table>");
    			if (data) {
    				//$(".box-body #" + calloutId, $navTab).append("<h4>" + config.title + "</h4>")
    				var localTable = new CommonLocalTable(tableId, data, $.extend(true, {}, defaultConfig, tableConfig));
    				//$(".box-body  #" + calloutId + " h4", $navTab).appendTo($("#" + tableId +  "_wrapper .row:first > :first"));
    				$("#" + tableId).data("localTable", localTable);
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
    				$(".box-body #" + tableId, $navTab).append("<tr><td>没有可显示的数据</td></tr>")
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
                data = data.substring(data.indexOf("<body"),data.indexOf("</body>")+7);
                $warpper.html(data);
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