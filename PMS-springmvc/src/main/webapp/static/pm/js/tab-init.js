/**
 * 该js，用于订单详情页面加载数据，以及相应事件方法
 */
var contractMoney;
var collectionTable;
var calloutColor = ["border-primary", "border-info", "border-success", "border-teal", "border-warning", "border-purple", "border-orange", "border-maroon"];
var colorSize = calloutColor.length - 1;
var collectionList;
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
            var config = $(tabId).data("config");
			initTabData(config);
		} else {
			$(tabId).find(".dataTables_scrollBody table").dataTable().api().columns.adjust();
		}
	});
});

/**
 * config: {
 * 	url, // 数据源
 * 	type, // 数据类型
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
            paging: false,
            rowId: 'projectId',
            info: false
        }
	$(".box-body .overlay", $navTab).show();
    ajaxPost(url, params, function(resultMap) {
        var columns = resultMap.columns || resultMap.pageParam.columns || [];
        var data = resultMap.data;
        if (refresh) {
            $("#" + tableId, $navTab).empty();
            $(".box-body #" + calloutId, $navTab).remove();
        }
        tableConfig.columns = columns;
        $(".box-body", $navTab).append("<div class='callout border-1 " +calloutColor[Math.round(Math.random()*colorSize)] +"' id='" + calloutId + "'></div>");
        $(".box-body #" + calloutId, $navTab).append("<table class='table table-bordered table-striped table-hover' id='" + tableId + "'></table>");
        if (data) {
            $(".box-body #" + calloutId, $navTab).append("<h4>" + config.title + "</h4>")
            new CommonLocalTable(tableId, data, $.extend(true, {}, defaultConfig, tableConfig));
            $(".box-body  #" + calloutId + " h4", $navTab).appendTo($("#" + tableId +  "_wrapper .row:first > :first"));
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
        } 
        $(".box-body .overlay", $navTab).hide();
        $navTab.addClass("loaded");
    });
}

function markQuantity(data,type,row){
	if(data > 0) {
		return "<b class='text-danger'>"+data+"</b>";
	}
	return data;
}
function renderLineType(data,type,row){
	var deliverQuantity = row.deliverQuantity;
	var openQuantity = row.openQuantity;
	var canceled = row.canceled;
	var mark = "";
	if (canceled == "Y") {
		mark = "(已取消)";
	} else if(deliverQuantity == openQuantity && openQuantity == 0) {
		mark = "（已取消）";// 历史
	}
	if(data == 0) {
		return "正常订单" + mark;
	}
	if(data == 1) {
		return "退货订单" + mark;
	}
	return data;
}

function adjustOrderQuantity(data,type,row){
    var deliverQuantity = row.deliverQuantity;
    var openQuantity = row.openQuantity;
    var lineStatus = row.lineStatus;
    if ((openQuantity == 0 && deliverQuantity > 0) && data > deliverQuantity && lineStatus == "C") {
        data = deliverQuantity;
    }
    return data;
}

function drawTimeLine(containerId, result, isAll) {
	//isAll = isAll == true ? true : false;
	var nodeEventList = result.nodeEventList;// 获取t_node_info集合
    var eventLineHtml = "";
	var typeCount = {contract:0,prepare:0,shipment:0,rma:0,execute:0};
    for (var x=0; x < nodeEventList.length; x++) {
        var nodeInfo = nodeEventList[x];
        var nodeInfoId = nodeInfo.id;
        var orderNumber = nodeInfo.nodeAttached;
        var nodeTypeCode = nodeInfo.nodeTypeCode;
        if ((nodeInfo.nodeBeginTime == null && nodeInfo.nodeEndTime == null) 
                || nodeTypeCode >= 50 && nodeInfo.nodeEndTime == null
                || showFlag == "forPRM" && nodeTypeCode >= 50) {
            continue;
        }
        /* if (showFlag == "forPRM" && nodeTypeCode == 20 && nodeInfo.dataType != 1) {
            continue;
        } */
        
        var nodeTime = "";
        if (nodeInfo.nodeEndTime  != null && (nodeTypeCode != 30 && nodeTypeCode != 40)) {
            nodeTime = nodeInfo.nodeEndTime;
        } else {
            nodeTime = nodeInfo.nodeBeginTime;
        }
        var moreInfo = "";
        if (nodeTypeCode == 10 || (showFlag == "forPRM" && isAll)) {
        	moreInfo = " -- " + nodeInfo.executeId;
        } else if($.inArray(nodeTypeCode, [20,30,40]) > -1 && isAll) {
        	moreInfo = " -- " + nodeInfo.contractId;
        }
        var nodeTypeName = nodeInfo.nodeTypeName;
        if (nodeInfo.dataType == 1 && nodeTypeCode==20) {
        	nodeTypeName = "退货";
        } else if (nodeTypeCode == 20 && showFlag == "forPRM") {
        	nodeTypeName = "总代下单";
        } else if (nodeTypeCode == 40 && showFlag == "forPRM") {
        	nodeTypeName = "迪普" + nodeTypeName;
        }
        //eventLineHtml += "<span id='" + containerId + "_nodeInfoId"+x+"' style='display:none;' >"+nodeInfo.id+"</span>";
        //eventLineHtml += "<li class=\"time-label timeline-inline\"><span class=\"bg-green\">"+nodeTime+"</span></li>";
        eventLineHtml += "<li class='mr-0'><span class='time-inline bg-green'>" + nodeTime + "</span><div class=\"timeline-item timeline-inline box border-0 collapsed-box mb-1\"><h3 class=\"timeline-header box-header\" data-widget-target='collapse'>"
           +"<div class='box-tools pull-right'><button type='button' class='btn btn-box-tool' data-widget='collapse'><i class='fa fa-plus'></i></button></div><a>" + nodeTypeName + moreInfo + "</a>"
           +"<span class='display-none nodeInfo'>"+JSON.stringify(nodeInfo)+"</span></h3>";
        eventLineHtml += "<div class='box-body' data-toggle='silde'><div class='overlay'><i class='fa fa-refresh fa-spin'></i></div>";
        var nodeIndex = 0;
        if (nodeTypeCode == 10) {
            eventLineHtml += "<div class=\"timeline-content\">"+nodeTypeName+"开始时间："+ nodeInfo.nodeBeginTime +"</div>";
            eventLineHtml += "<div class=\"timeline-content\">"+nodeTypeName+"结束时间："+ nodeInfo.nodeEndTime +"</div>";
            if (showFlag == "forPRM") {
            	typeCount.execute += 1;
            	nodeIndex = typeCount.execute;
            	var divId = containerId + "_executeDetail" + nodeInfoId;
                eventLineHtml += "<div id=\""+divId+"\" nodeIndex="+nodeIndex+"><div class=\"mb-0\"><div class=\"box-body\"><div class='callout border-1 " +calloutColor[Math.round(Math.random()*colorSize)] +"'></div></div></div>";
            }
        } else {
            if (nodeInfo.nodeBeginTime != null) {
                eventLineHtml += "<div class=\"timeline-content\">"+nodeTypeName+"时间："+ nodeInfo.nodeBeginTime +"</div>"
            } else {
            	eventLineHtml += "<div class=\"timeline-content\">"+nodeTypeName+"时间："+ nodeInfo.nodeEndTime +"</div>"
            }
            if (nodeTypeCode == 40 && nodeInfo.nodeEndTime != null){
                eventLineHtml += "<div class=\"timeline-content\">签收时间："+ nodeInfo.nodeEndTime +"</div>"
            }
        }
        
        if (nodeTypeCode == 20) {
            //var nodeRemark = nodeInfo.nodeRemark == null ? "无" : nodeInfo.nodeRemark;
            var nodeRemark = nodeInfo.nodeRemark;
            var dataType = nodeInfo.dataType == 0 ? "正常订单" : "退货订单";
            //var orderType = nodeInfo.orderType;
            var divId = containerId + "_contractDetail" + orderNumber;
            if (nodeInfo.dataType == 0) {
            	typeCount.contract += 1;
            	nodeIndex = typeCount.contract;
            	if (nodeRemark) {
            		if (showFlag == "forPRM") {
                        eventLineHtml += "<div class=\"timeline-content\">订单备注："+nodeRemark+"</div>"
                    } else {
                        eventLineHtml += "<div class=\"timeline-content\">合同备注："+nodeRemark+"</div>"
                    }
            	}
                eventLineHtml += "<div class=\"timeline-content\">订单类型："+dataType+"</div>"
            } else {
            	typeCount.rma += 1;
            	nodeIndex = typeCount.rma;
            	eventLineHtml += "<div class=\"timeline-content\">备注："+nodeRemark+"</div>"
            }
            //eventLineHtml += "<div class=\"timeline-content\">订单类型："+orderType+"</div>"
            //if (showFlag != "forPRM") {
                eventLineHtml += "<div id=\""+divId+"\" nodeIndex="+ nodeIndex +"><div class=\"mb-0\"><div class=\"box-body\"><div class='callout border-1 " +calloutColor[Math.round(Math.random()*colorSize)] +"'></div></div></div>";
            //}
        } else if (nodeTypeCode == 30) {
        	typeCount.prepare += 1;
        	nodeIndex = typeCount.prepare;
            var divId = containerId + "_prepareDetail" + nodeInfoId;
            eventLineHtml += "<div id=\""+divId+"\" nodeIndex="+ nodeIndex +"><div class=\"mb-0\"><div class=\"box-body\"><div class='callout border-1 " +calloutColor[Math.round(Math.random()*colorSize)] +"'></div></div></div>";
        } else if (nodeTypeCode == 40) {
        	typeCount.shipment += 1;
        	nodeIndex = typeCount.shipment;
            var divId = containerId + "_deliveryDetail" + nodeInfoId;
            eventLineHtml += "<div id=\""+divId+"\" nodeIndex="+ nodeIndex +"><div class=\"mb-0\"><div class=\"box-body\"><div class='callout border-1 " +calloutColor[Math.round(Math.random()*colorSize)] +"'></div></div></div>";
        }
        eventLineHtml +="</div></div></li>";
    }
    $("#" + containerId).append("<li class='event_header clearfix'><i class='fa fa-play-circle-o bg-gray'></i>"
               +"<button id='toggleNodeEvent' class='btn btn-primary pull-right mr-1'>查看" + (isAll == true ? "当前合同相关" : "整个项目") + "事件</button><button id='toggleSlideBtn' class='btn btn-success pull-right mr-1'>展开所有</button></li>" 
               + eventLineHtml + "<li><i class='fa fa-clock-o bg-gray'></i></li>");
    
    drawTimeLineDetails(containerId, nodeEventList, typeCount, lazyLoadEvent);
    $("#nodeEvent .box-body .overlay:first").hide();
}

// 在加载时直接画出所有时间详细信息
function drawTimeLineDetails(containerId, nodeEventList, typeCount, lazyLoad) {
	contractIndex = typeCount.contract > 1 ? 1 : 0;
	prepareIndex = typeCount.prepare > 1 ? 1 : 0;
	shipmentIndex = typeCount.shipment > 1 ? 1 : 0;
	rmaIndex = typeCount.rma > 1 ? 1 : 0;
	executeIndex = typeCount.execute > 1 ? 1 : 0;
	if (lazyLoad) {
		lazyLoadEvent = lazyLoad;
		return;
	}
    for (var x=0; x < nodeEventList.length; x++) {
        var nodeInfo = nodeEventList[x];
        drawTimeLineDetail(containerId, nodeInfo);
    }
}

function drawTimeLineDetail(containerId, nodeInfo, lazyLoad) {
	var contractId = nodeInfo.contractId;
    var nodeInfoId = nodeInfo.id;
    var orderNumber = nodeInfo.nodeAttached;
    var nodeTypeCode = nodeInfo.nodeTypeCode;
    var dataType = nodeInfo.dataType;
    var nodeBeginTime = nodeInfo.nodeBeginTime;
    var nodeEndTime = nodeInfo.nodeEndTime;
    var param = nodeInfo.executeId + "-exe";
    if ((nodeBeginTime == null && nodeEndTime == null) ||
           // (showFlag == "forPRM" && nodeTypeCode == 20 && dataType == 0) ||    // 合同签订即为总代下单
            (showFlag != "forPRM" && nodeTypeCode == 10)        
            ) {
        return;
    }
    // 合同签订详情、、执行单详情（PLM用执行单查找项目）
    if (nodeTypeCode == 20 || nodeTypeCode == 10) {
        var divId = containerId + "_contractDetail" + orderNumber;
        if (nodeTypeCode == 10) {
            divId = containerId + "_executeDetail" + nodeInfoId;
        }
        var tempParam = contractId;
        if (showFlag == "forPRM" && nodeTypeCode == 10) {
            tempParam = nodeInfo.executeId + "-exe";
        }
        if($("#"+divId+" table").length == 0){
            ajaxPost("findOrderDetail.json", 
                {
                    "contractId":tempParam,
                    "orderNumber":orderNumber,
                    "dataType":dataType
                }, 
                function(data,status){
                    var orderDetails = data.tableDatas;
                    if(orderDetails.length == 0) {
                        $("#"+divId).remove();
                        return false;
                    }
                    var columns = data.columns;
                    if (showFlag == "forPRM") {
                        columns[0] = {title : "执行单号", data :"orderExecCode"};
                    }
                    var titleName = dataType == 1 ? "退货" : (showFlag == "forPRM" && nodeTypeCode == 20 ? "订单设备" : (showFlag == "forPRM" ? "执行单设备" : "合同设备"));
                    for(var i in orderDetails) {
                    	var nodeIndex = nodeIndex = $("#"+divId).attr("nodeIndex");
                    	if (nodeTypeCode == 10 && showFlag == "forPRM" && executeIndex == 0) {
                    		nodeIndex = 0;
                    	}
                   		if (nodeTypeCode == 20 && dataType == 1 && rmaIndex == 0) {
                            nodeIndex = 0;
                        } else if (nodeTypeCode == 20 && dataType == 0 && contractIndex == 0){
                            nodeIndex = 0;
                        }
                        var orderDetail = orderDetails[i];
                        $("#"+divId+" .box-body .callout").append("<h4>" + titleName + "清单" + (nodeIndex > 0 ? "[" + (nodeIndex) + "]" : '') + "</h4>");
                        var tableId = containerId + "_contractTable" + orderNumber;
                        if (nodeTypeCode == 10) {
                            tableId = containerId + "_executeTable" + nodeInfoId;
                        }
                        $("#"+divId+" .box-body .callout").append("<table class='table table-bordered table-striped table-hover dataTable' id='" + tableId +"'></table>");
                        new CommonLocalTable(tableId, orderDetail, {
                            rowId: 'id',
                            columns : columns,
                            paging: false,
                            scrollY: "50vh",
                            scrollCollapse: true,
                        });
                        $("#"+divId+" .box-body .callout h4").appendTo($("#" + tableId +"_wrapper .row:first > :first"));
                    }
                }, lazyLoad ? true : false
            );
        }
    }
    // 备货详情
    
    if (nodeTypeCode == 30) {
        var divId = containerId + "_prepareDetail" + nodeInfoId;
        if (showFlag == "forPRM") {
        	contractId = "";
        }
        if($("#"+divId+" table").length == 0){
            ajaxPost('getNodeDetail.json', {
                nodeId : 3,//备货的nodeId是3
                contractId : contractId,
                nodeInfoId : nodeInfoId,
                param : param,
                isSplit : 99
            }, function(result){
                var resultMap = result.resultMap;
                var useDataTable = resultMap.useDataTable;
                var branchInfo = resultMap.branchInfo;
                if(useDataTable == true){
                    var columns = resultMap.columns;
                    var nodeIndex = nodeIndex = $("#"+divId).attr("nodeIndex");
                    if (prepareIndex == 0) {
                        nodeIndex = 0;
                    }
                    $("#"+divId+" .box-body .callout").append("<h4>备货清单" + (nodeIndex > 0 ? "[" + (nodeIndex) + "]" : '') + "</h4>");
                    var tableId = containerId + "_prepareTable" + nodeInfoId;
                    $("#"+divId+" .box-body .callout").append("<table class='table table-bordered table-striped table-hover dataTable' id='" + tableId +"'></table>");
                    new CommonLocalTable(tableId, resultMap['dataTable'+nodeInfoId], {
                        rowId: 'id',
                        columns : columns,
                        paging: false,
                        scrollY: "50vh",
                        scrollCollapse: true,
                    });
                    $("#"+divId+" .box-body .callout h4").appendTo($("#" + tableId +"_wrapper .row:first > :first"));
                }
            }, lazyLoad ? true : false);
        }
    } else if (nodeTypeCode == 40) {// 发货详情
        var divId = containerId + "_deliveryDetail" + nodeInfoId;
        if (showFlag == "forPRM") {
            contractId = "";
        }
        if($("#"+divId+" table").length == 0){
            ajaxPost('getNodeDetail.json', {
                nodeId : 4,//发货的nodeId是4
                contractId : contractId,
                nodeInfoId : nodeInfoId,
                param : param
            }, function(result){
                var resultMap = result.resultMap;
                var useDataTable = resultMap.useDataTable;
                var branchInfo = resultMap.branchInfo;
                if(useDataTable == true){
                    var columns = resultMap.columns;
                    var nodeIndex = nodeIndex = $("#"+divId).attr("nodeIndex");
                    if (shipmentIndex == 0) {
                        nodeIndex = 0;
                    }
                    $("#"+divId+" .box-body .callout").append("<h4>发货清单" + (nodeIndex > 0 ? "[" + (nodeIndex) + "]" : '') + "</h4>");
                    var tableId = containerId + "_deliveryTable" + nodeInfoId;
                    $("#"+divId+" .box-body .callout").append("<table class='table table-bordered table-striped table-hover dataTable' id='" + tableId +"'></table>");
                    new CommonLocalTable(tableId, resultMap['dataTable'+nodeInfoId], {
                        rowId: 'id',
                        columns : columns,
                        paging: false,
                        scrollY: "50vh",
                        scrollCollapse: true,
                    });
                    $("#"+divId+" .box-body .callout h4").appendTo($("#" + tableId +"_wrapper .row:first > :first"));
                }
            }, lazyLoad ? true : false);
        }
    }
}

function findOrderDetails(containerId, isDetail, $this, $that) {
	if($("#orderDetail #" + containerId + " table").length == 0){
        var projectCode = $("#projectCode").text().trim();
        var orderCreateTime = $("#main_step>li[nodeid='2']>*").attr("data-time");
        var tempParam = contractId;
        var title = "合同";
        if (showFlag == 'forPRM') {
            tempParam = param;
            title = "执行单";
        }
        ajaxPost("findOrderDetail.json", {"contractId":tempParam,"projectCode":projectCode,"orderCreateTime":orderCreateTime, "isDetail": isDetail}, function(data,status){
        	var orderDetails = data.tableDatas;
            var columns = data.columns;
            var historyOrderDetails = data.historyTableDatas;
            var simplifyOrderDetails = data.simplifyTableDatas;
            if (simplifyOrderDetails !== undefined) {
                if($("#simplifyOrderDetailTable").length == 0){
                    $("#orderDetail .box-body #" + containerId).append("<div class='callout border-1 " +calloutColor[Math.round(Math.random()*colorSize)] +"' id='simplifyCallout'></div>");
                    $("#orderDetail .box-body #" + containerId + " #simplifyCallout").append("<h4>项目设备清单<button id='orderDetailBtn' class='btn btn-link pt-0'><i class='fa fa-hand-o-right'></i>查看当前明细</button></h4>");
                    $("#orderDetail .box-body #" + containerId + " #simplifyCallout").append("<table class='table table-bordered table-striped table-hover dataTable' id='simplifyOrderDetailTable'></table>");
                } else {
                    $("#simplifyOrderDetailTable").DataTable().destroy();
                    $("#simplifyOrderDetailTable").empty();
                }
                var simpColumns = columns;
                var column = {title:"下单时间",data:"orderCreateTime"};
                simpColumns.push(column);
                new CommonLocalTable("simplifyOrderDetailTable", simplifyOrderDetails, {
                    stateSave: false,
                    rowId: 'id',
                    columns : simpColumns,
                    paging: false,
                    scrollY: "50vh",
                    scrollCollapse: true,
                });
                $("#orderDetail .box-body #" + containerId + " #simplifyCallout h4").appendTo($("#simplifyOrderDetailTable_wrapper .row:first > :first"));
            } else if(orderDetails.length == 0 && (historyOrderDetails === undefined)) {
                var button = "";
                if($("#simplify").text().trim() !="" ) {
                	button = "<button id='orderDetailBtn' class='btn btn-link pt-0'><i class='fa fa-hand-o-right'></i>查看项目设备清单</button>";
                }
                $("#orderDetail .box-body #" + containerId).append("<table class='table table-bordered table-striped table-hover'><tr><td>无设备清单详情" + button + "</td></tr></table>");
            } else {
                $.each(orderDetails, function(i, item){
                    if($("#orderDetailTable" + i).length == 0){
                        $("#orderDetail .box-body  #" + containerId).append("<div class='callout border-1 " +calloutColor[Math.round(Math.random()*colorSize)] +"' id='callout"+i+"'></div>");
                        if(orderDetails.length == 1){
                            $("#orderDetail .box-body #" + containerId + " #callout"+i).append("<h4>当前" + title + "设备清单<button id='orderDetailBtn' class='btn btn-link pt-0'><i class='fa fa-hand-o-right'></i>查看项目设备清单</button></h4>");
                        }
                        if(orderDetails.length > 1){
                            $("#orderDetail .box-body #" + containerId + " #callout"+i).append("<h4>当前" + title + "设备清单[" + (i+1) + "]"+ (i == 0 ?"<button id='orderDetailBtn' class='btn btn-link pt-0'><i class='fa fa-hand-o-right'></i>查看项目设备清单</button>" : "") +"</h4>");
                        }
                        $("#orderDetail .box-body #" + containerId + " #callout"+i).append("<table class='table table-bordered table-striped table-hover dataTable' id='orderDetailTable" + i +"'></table>");
                    } else {
                        $("#orderDetailTable" + i).DataTable().destroy();
                        $("#orderDetailTable" + i).empty();
                    }
                    new CommonLocalTable("orderDetailTable"+i, item, {
                        stateSave: false,
                        rowId: 'id',
                        columns : columns,
                        paging: false,
                        scrollY: "50vh",
                        scrollCollapse: true,
                    });
                    $("#orderDetail .box-body  #" + containerId + " #callout"+i +" h4").appendTo($("#orderDetailTable" + i +"_wrapper .row:first > :first"));
                });
                /* if(historyOrderDetails) {
                    if($("#historyOrderDetailTable").length == 0){
                        $("#orderDetail .box-body #" + containerId).append("<div class='callout border-1 " +calloutColor[Math.round(Math.random()*colorSize)] +"' id='historyCallout'></div>");
                        $("#orderDetail .box-body #" + containerId + " #historyCallout").append("<h4>项目历史设备清单汇总</h4>");
                        $("#orderDetail .box-body #" + containerId + " #historyCallout").append("<table class='table table-bordered table-striped table-hover dataTable' id='historyOrderDetailTable'></table>");
                    } else {
                        $("#historyOrderDetailTable").DataTable().destroy();
                        $("#historyOrderDetailTable").empty();
                    }
                    var hisColumns = columns;
                    var column = {title:"下单时间",data:"orderCreateTime"};
                    hisColumns.push(column);
                    new CommonLocalTable("historyOrderDetailTable", historyOrderDetails, {
                        stateSave: false,
                        rowId: 'id',
                        columns : hisColumns,
                        paging: false,
                        scrollY: "50vh",
                        scrollCollapse: true,
                    });
                    $("#orderDetail .box-body #" + containerId + " #historyCallout h4").appendTo($("#historyOrderDetailTable_wrapper .row:first > :first"));
                } */
            }
            if ($this) {
                $this.hide();
                $that.show();
            }
            $("#orderDetail .box-body .overlay").hide();
        });
    }
}

function findComments(url, navId, refresh) {
	$("#comment .box-body .overlay").show();
	var projectCode = $("#projectCode").text().trim();
    var executeId = $("#executeId").text().trim();
    var channelCode = $("#channelCode").text().trim();
    var mainDataId = $("#id").text().trim();
    ajaxPost("findComments.json", {projectCode:projectCode,executeId:executeId,channelCode: channelCode}, function(resultMap) {
        var columns = resultMap.columns;
        var comments = resultMap.comments;
        if (refresh) {
            $("#commentTable").empty();
            $("#comment .box-body #calloutComment").remove();
        }
        $("#comment .box-body").append("<div class='callout border-1 " +calloutColor[Math.round(Math.random()*colorSize)] +"' id='calloutComment'></div>");
        $("#comment .box-body #calloutComment").append("<table class='table table-bordered table-striped table-hover' id='commentTable'></table>");
        if (comments) {
            $("#comment .box-body #calloutComment").append("<h4>评论记录</h4>")
            new CommonLocalTable('commentTable', comments, {
                stateSave: false,
                searching: false,
                paging: false,
                rowId: 'id',
                info: false,
                columns : columns
            });
            $("#comment .box-body  #calloutComment h4").appendTo($("#commentTable_wrapper .row:first > :first"));
            try {
            	if (guest) {
            		$("#commentTable_wrapper .row:first > :last").append("<button class='pull-right btn btn-primary comment ml-1' data-id='"+ mainDataId +"'>新增评论</button>");
            	}
            } catch(e) {
            }
        } 
        $("#comment .box-body .overlay").hide();
    });
}