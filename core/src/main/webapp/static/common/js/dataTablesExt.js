// 去除jquery.dataTable的警告弹窗
if ($.fn.dataTable) {
	$.fn.dataTable.ext.errMode = function(settings, tn, msg) {
		// 4为字段属性不存在时的错误提示
		if (tn != 4) {
			console.error(msg);
		}
	};
}
/**
 * 对datatables组件进行参数封装
 */
/*常量*/
var CONSTANT = {
        DATA_TABLES : {
            DEFAULT_OPTION : { //DataTables服务器初始化选项
            	"sErrMode": "throw",
            	"errMode": "throw",
                language: {
                    "sProcessing":   "<div class='overlay'><i class='fa fa-refresh fa-spin'></i></div>",
                    "sLengthMenu":   "每页 _MENU_ 项",
                    "sZeroRecords":  "没有匹配结果",
                    "sInfo":         "当前显示第 _START_ 至 _END_ 项，共 _TOTAL_ 项。",
                    "sInfoEmpty":    "当前显示第 0 至 0 项，共 0 项",
                    "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
                    "sInfoPostFix":  "",
                    "sSearch":       "搜索:",
                    "sUrl":          "",
                    "sEmptyTable":     "表中数据为空",
                    "sLoadingRecords": "载入中...",
                    "sInfoThousands":  ",",
                    "oPaginate": {
                        "sFirst":    "首页",
                        "sPrevious": "上页",
                        "sNext":     "下页",
                        "sLast":     "末页",
                        "sJump":     "跳转"
                    },
                    "oAria": {
                        "sSortAscending":  ": 以升序排列此列",
                        "sSortDescending": ": 以降序排列此列"
                    }
                },
                //ordering : false,
                lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "全部"]],
                lengthChange: true,
                stateSave : true,
                paging : true,
                pagingType : "full_numbers",// 分页样式
                info : true,
                displayLength : 10,
                autoWidth: true,   //自动调整列宽
                stripeClasses: ["odd", "even"],//为奇偶行加上样式，兼容不支持CSS伪类的场合
                order: [],          //默认排序查询
                processing: true,  //加载提示
                serverSide: true,   //服务器端分页
                searching: true,    //原生搜索
                filter: true,
                retrieve : true,
                singleSelect: true
            },
            LOCAL_DEFAULT_OPTION : { //DataTables本地初始化选项
            	"sErrMode": "throw",
            	"errMode": "throw",
                language: {
                    "sProcessing":   "处理中...",
                    "sLengthMenu":   "每页 _MENU_ 项",
                    "sZeroRecords":  "没有匹配结果",
                    "sInfo":         "当前显示第 _START_ 至 _END_ 项，共 _TOTAL_ 项。",
                    "sInfoEmpty":    "当前显示第 0 至 0 项，共 0 项",
                    "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
                    "sInfoPostFix":  "",
                    "sSearch":       "搜索:",
                    "sUrl":          "",
                    "sEmptyTable":     "表中数据为空",
                    "sLoadingRecords": "载入中...",
                    "sInfoThousands":  ",",
                    "oPaginate": {
                        "sFirst":    "首页",
                        "sPrevious": "上页",
                        "sNext":     "下页",
                        "sLast":     "末页",
                        "sJump":     "跳转"
                    },
                    "oAria": {
                        "sSortAscending":  ": 以升序排列此列",
                        "sSortDescending": ": 以降序排列此列"
                    }
                },
                ordering : true,
                order: [],  
                lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "全部"]],
                lengthChange: true,
                stateSave : false,
                paging : true,
                pagingType : "full_numbers",// 分页样式
                info : true,
                displayLength : 10,
                autoWidth: true,   //自动调整列宽
                stripeClasses: ["odd", "even"],//为奇偶行加上样式，兼容不支持CSS伪类的场合
                processing: true,  //加载提示
                serverSide: false,   //服务器端分页
                searching: true,    //原生搜索
                filter: true,
                retrieve : true,
                singleSelect: true
            },
            /*COLUMN: {
                CHECKBOX: { //复选框单元格
                	title: "<i class='icheckbox_flat-green checkbox-toggle'></i>",
                    className: "td_checkbox text-center",
                    width: "40px",
                    data: "id",
                    render: function (data, type, row, meta) {
                        return '<input type="checkbox" value="' + data + '" class="iCheck">';
                    }
                }
            },
            RENDER: {   //常用render可以抽取出来，如日期时间、头像等
                ELLIPSIS: function (data, type, row, meta) {
                    data = data||"";
                    return '<span title="' + data + '">' + data + '</span>';
                }
            }*/
        }
};

/**
 * 本地模式
 * @param tableId
 * @param data
 * @param config
 * @returns
 */
function CommonLocalTable(tableId, data, config) {
	this.tableId = tableId;
	this.data = data;
	this.searchDiv = config.searchDiv || "undifined";
	if (!config) {
		config = {};
	}
	this.config = config;
	
	// 是否启用行选择，即添加.selected类：默认启用
	this.disableSelect = config.disableSelect || false;
	// 表格增加复选框
	if (typeof config.checkbox != "undefined") {
		this.initCheckbox = true;
		this.checkboxFlag = "iCheck";
		if (typeof config.checkbox.flag != "undefined") {
			this.checkboxFlag = config.checkbox.flag;
		}
		/*
		 * {
		 *   rowId:
		 *   actionClass:
		 *   styleClass:
		 *   titleClass:
		 * }
		 */
		this.checkbox = config.checkbox;
	}
	// 表格横向自适应 
	$("#" + this.tableId).css("width", "100%");
	// 初始化表格
	this.initLocalTable(tableId, data);
}

/**
 * 服务端模式
 * @param tableId
 * @param url
 * @param searchDiv
 * @param config
 * @returns
 */
function CommonTable(tableId, url, searchDiv, config) {
	this.tableId = tableId;
	this.url = url;
	this.load = false;
	this.data = {};
	this.searchDiv = searchDiv;
	if (!config) {
		config = {};
	}
	this.config=config;
	this.fuzzySearch = true;
	this.advancedSearch = ".toggle-advanced-search";
	this.searchButton = $("#" + searchDiv + " button[data-btn-type='search']");
	this.restButton = $("#" + searchDiv + " button[data-btn-type='reset']");
	this.customSearch = {};
	this.stateData = {};
	this.exportData = config.exportData;
	this.searchDivInline = config.searchInline;
	if(this.exportData != undefined && JSON.stringify(this.exportData) != "{}" ) {
		this.exporting = true;
	}
	// 是否启用行选择，即添加.selected类：默认启用
	this.disableSelect = config.disableSelect || false;
	// checked是否与selected同时触发，默认false，单独
	this.sameTrigger = config.sameTrigger || false;
	// 表格增加复选框
	if (typeof config.checkbox != "undefined") {
		this.initCheckbox = true;
		this.checkboxFlag = "iCheck";
		if (typeof config.checkbox.flag != "undefined") {
			this.checkboxFlag = config.checkbox.flag;
		}
		/*
		 * {
		 *   rowId:
		 *   actionClass:
		 *   styleClass:
		 *   titleClass:
		 * }
		 */
		this.checkbox = config.checkbox;
	}
	
	// 表格横向自适应 
	$("#" + this.tableId).css("width", "100%");
	
	// 初始化表格
	this.initTable(tableId, url, searchDiv);
}

/**
 * 本地模式初始化表格
 */
CommonLocalTable.prototype.initLocalTable = function(tableId, data) {
	var that = this;
	var columns = that.config.columns;
	for(var i in columns) {
		var column = columns[i];
		if(column.render) {
			var render = null;
			try {
//				render = eval(eval('"' + column.render + '"'));
				render = eval(column.render);
			} catch(e) {
				if (e instanceof ReferenceError) {
					modals.error(column.render + " 未定义！");
				} else {
					modals.error(JSON.stringify(e.message));
				}
			}
			column.render = render;
		}
	}
	
	var rowId = this.data.rowId || that.config.rowId;
	if (that.initCheckbox) {
		var checkboxColumn = this.initCheckboxColumn(that.checkbox);
		that.checkboxColumn
		columns.unshift(checkboxColumn);
	}
	
	this.table = $('#' + tableId).dataTable($.extend(true,{},CONSTANT.DATA_TABLES.LOCAL_DEFAULT_OPTION,{
		data : that.data,
		fnInitComplete : $.proxy(that.fnInitComplete,that)
	}, that.config)).api();
//	$("#"+this.tableId +"_filter input[type='search']").addClass("fuzzySearch").attr("placeholder","模糊查找").addClass("form-control").removeClass("input-sm");
//	$("#" + that.tableId + '_wrapper .dataTables_filter input[type="search"]').bind('input', function(e) {
//        that.table.search(this.value).draw(false);
//    });
//	
//	if(("scrollY" in that.config || "scrollX" in that.config) ) {
//		if($.fn.slimScroll){
//	//		var height = $("#" + that.tableId + "_wrapper .dataTables_scrollBody").height();
//	//		if(height > 0) {
//				$("#" + that.tableId + "_wrapper .dataTables_scrollBody").slimScroll({
//					height: "auto",
//					size: 8,
//					disableFadeOut: true,
//					allowPageScroll: true,
//					//railVisible: true
//				});
//				$("#" + that.tableId + "_wrapper .dataTables_scrollBody").css("height","");
//				$("#" + that.tableId + "_wrapper .slimScrollDiv").css("height","");
////				$("#" + that.tableId + "_wrapper .dataTables_scrollHeadInner").css({"paddingRight":"","width":""});
////				$("#" + that.tableId + "_wrapper .dataTables_scrollHeadInner > .dataTable").css({"marginLeft":"","width":""});
//	//		}
}

/**
 * 服务端模式初始化表格
 */
CommonTable.prototype.initTable = function(tableId, url, searchDiv) {
	this.getServerData(null, this.initConfig);
};

CommonTable.prototype.initConfig = function(tableId, url, searchDiv) {
	var that = this;
	var columns = this.data.columns || [];
	for(var i in columns) {
		var column = columns[i];
		if(column.render) {
			var render = null;
			try {
//				render = eval(eval('"' + column.render + '"'));
				render = eval(column.render);
			} catch(e) {
				if (e instanceof ReferenceError) {
					modals.error(column.render + " 未定义！");
				} else {
					modals.error(JSON.stringify(e.message));
				}
			}
			column.render = render;
		}
	}
	
	// that.config.columns = [];
	// 前后端列模式，limit限制服务端的列数,local以前端传入的columns为准；server以后端为准；默认前后端深度拷贝，数组、字段合并，相同时以前端为准
	if (that.config.columnsModel == 'limit') {
		var limit = that.config.columnsLimit;
		if (limit) {
			columns = columns.slice(limit[0], limit[1]);
			that.config.columns = [];
		}
	} else if (that.config.columnsModel == 'server') {
		that.config.columns = [];
	} else if (that.config.columnsModel == 'local') {
		columns = [];
	} else if (columns.length == 0) {
		columns = that.config.columns;
	}
	var rowId = this.data.rowId || that.config.rowId;
	if (that.initCheckbox) {
		var checkboxColumn = this.initCheckboxColumn(that.checkbox);
		that.checkboxColumn
		columns.unshift(checkboxColumn);
	}
	
	// datatable 初始化之前触发
	if (that.config.beforeInitConfig) {
		that.config.beforeInitConfig.call(that);
	}
	
    this.table = $('#' + tableId).dataTable($.extend(true,{},CONSTANT.DATA_TABLES.DEFAULT_OPTION,{
    	rowId : rowId,
    	columns : columns,
    	ajax : $.proxy(that.fnServerData,that)
    	/*ajax : function(data, callback, settings) {//ajax配置为function,手动调用异步查询
    	    //封装请求参数
    	    var param = that.getQueryCondition(data);
    	    $.ajax({
    	        type: "POST",
    	        url: url,
    	        cache : false,  //禁用缓存
    	        data: param,    //传入已封装的参数
    	        dataType: "json",
    	        success: function(result) {
    	            //异常判断与处理
//    	            if (result.errorCode) {
//    	                $.dialog.alert("查询失败。错误码："+result.errorCode);
//    	                return;
//    	            }
    	            //封装返回数据，这里仅演示了修改属性名
    	            var returnData = {};
    	            returnData.draw = result.pageParam.draw;//这里直接自行返回了draw计数器,应该由后台返回
    	            returnData.recordsTotal = result.pageParam.total;
    	            returnData.recordsFiltered = result.pageParam.filtered;//后台不实现过滤功能，每次查询均视作全部结果
    	            returnData.data = result.data;
    	            //调用DataTables提供的callback方法，代表数据已封装完成并传回DataTables进行渲染
    	            //此时的数据需确保正确无误，异常判断应在执行此回调前自行处理完毕
    	            callback(returnData);
    	        },
    	        error: function(XMLHttpRequest, textStatus, errorThrown) {
    	        	if(textStatus == "parsererror" && XMLHttpRequest.status == 200){
    	        		var start = XMLHttpRequest.responseText.indexOf("<body ");
    	        		var end = XMLHttpRequest.responseText.indexOf("</body>");
    	        		$("body").html(XMLHttpRequest.responseText.substring(start,end));
    	        	} else{
    	        		modals.error({
    	        			text:"查询失败!\r\n"+XMLHttpRequest.responseText,
    	        			large:'lg'
    	        		});
    	        	}
    	        }
    	    });
       }*/,
       fnInitComplete : $.proxy(that.fnInitComplete,that),
       fnStateLoaded : $.proxy(that.fnStateLoaded,that),
       fnStateSaveParams : $.proxy(that.fnStateSaveParams,that),
       fnDrawCallback : $.proxy(that.fnDrawCallback,that),
    },that.config)).api();//此处需调用api()方法,否则返回的是JQuery对象而不是DataTables的API对象
    
    if(this.advancedSearch){
    	$(this.table.table().container()).on("click","#" + this.tableId +"_filter "+ this.advancedSearch,function(){
    		$("i",this).toggleClass("fa-angle-double-down fa-angle-double-up");
    		$("#"+that.tableId +"_filter input[type='search']").attr("readonly",that.fuzzySearch);
			$("#"+searchDiv).slideToggle("fast");
			
			that.fuzzySearch = !that.fuzzySearch;
			that.table.draw(false);
    	})
    	// 该段代码调用在fnInitComplete之前，无法触发
    	/*if(!that.fuzzySearch) {
    		that.fuzzySearch = !that.fuzzySearch;
    		$("#" + this.tableId + "_filter " + this.advancedSearch, this.table.table().container()).click();
    	}*/
	}
    if(this.searchButton){
		this.searchButton.click(function() {
			that.table.page('first').draw(false);
			that.fuzzySearch = false;
			// 执行查询的回调函数 		
			if (that.searchButton.attr("callback") != null && that.searchButton.attr("callback") != undefined) {
				eval(that.searchButton.attr("callback"));
			}
		});
	}
    
    if(this.restButton){
		this.restButton.click(function() {
			$("#" + that.searchDiv +" input[type='search']",that.table.table().container()).val("");
			$("#" + that.searchDiv +" input[data-type='search']",that.table.table().container()).val("");
			$("#" + that.searchDiv +" select[type='search']",that.table.table().container()).val("");
			if($.fn.select2) {
				$("#" + that.searchDiv +" select.select2",that.table.table().container()).trigger("change");
			}
			if($.fn.daterangepicker) {
				$("#" + that.searchDiv +" .daterange-span",that.table.table().container()).text("不限");
				$("#" + that.searchDiv +" .daterange-btn",that.table.table().container()).each(function() {
					var minDate = $(this).data("daterangepicker").minDate;
					var maxDate = $(this).data("daterangepicker").maxDate;
					var _this = this;
					_ti = setInterval(function() {
						$(_this).data('daterangepicker').setStartDate(minDate);
						$(_this).data('daterangepicker').setEndDate(maxDate);
						clearInterval(_ti);
					}, 200);
				})
				
				/*if (startTime && endTime) {
					$(this).find(".daterange-span").text(startTime + " - " + endTime.split(" 23:59:59")[0]);
					var _this = this;
					_ti = setInterval(function() {
						$(_this).data('daterangepicker').setStartDate(startTime);
						$(_this).data('daterangepicker').setEndDate(endTime);
						clearInterval(_ti);
					}, 200);
				} else {
					$(this).find(".daterange-span").text("不限");
				}*/
			}
			that.table.page('first').draw(false);  
			that.fuzzySearch = false;
			// 执行查询的回调函数 		
			if (that.restButton.attr("callback") != null && that.restButton.attr("callback") != undefined) {
				eval(that.restButton.attr("callback"));
			}
		});
	}
    
    if(this.exporting) {
    	var $tableContainer = that.table.table().container();
		var exportBtnGroup = this.createExportBtnGroup();
		if( $("#"+this.tableId +"_length", $tableContainer).length > 0 ) {
			$("#"+this.tableId +"_length", $tableContainer).append(exportBtnGroup);
		} else if( $("#"+this.tableId +"_filter", $tableContainer).length > 0) {
			$("#"+this.tableId +"_filter", $tableContainer).parent().prev().append(exportBtnGroup);
		} else if( $("#"+this.searchDiv, $tableContainer).length > 0 ) {
			 $("#"+this.searchDiv, $tableContainer).append(exportBtnGroup);
			 $("#"+this.searchDiv + " .export-btn-group", $tableContainer).addClass("pull-right");
		} else {
			
		}
		
		$($tableContainer).on("click",".export-btn-group .btn", function() {
			var sourceObject = $(this).attr("data-source-object");
			var exportType = $(this).attr("data-export-type");
			var fullServiceName = $(this).attr("data-source-fullServiceName");
//			var simpleServiceName = $(this).attr("data-source-simpleServiceName");
			if( sourceObject && exportType) {
				var data = {};
				data.draw = 0;
				data.start = 0;
				data.length = -1;
				data.order = that.table.table().order();
				var params = that.getQueryCondition(data);
				var paramsStr = {};
				var sourceObjectKV = [];
				var pageParamKV = [];
				if(!params.fuzzySearch) {
					var flag = false;
					for(var field in params) {
						if(flag) {
							sourceObjectKV.push(field + "=" + params[field]);
						} else {
							pageParamKV.push(field + "=" + params[field]);
						}
						if(field === "fuzzySearch") {
							flag = true;
						}
					}
				} else {
					var flag = true;
					for(var field in params) {
						if(flag) {
							pageParamKV.push(field + "=" + params[field]);
						} else {
							sourceObjectKV.push(field + "=" + params[field]);
						}
						if(field === "fuzzySearch") {
							flag = false;
						}
					}
				}
						
				if(sourceObjectKV.length > 0) {
					paramsStr.objectKV = sourceObjectKV.join(";");
				}
				if(pageParamKV.length > 0) {
					paramsStr.pageParamKV = pageParamKV.join(";");
				}
				paramsStr.objectName = sourceObject;
				paramsStr.exportType = exportType;
				paramsStr.fullServiceName = fullServiceName;
//				paramsStr.simpleServiceName = simpleServiceName;
				paramsStr = $.param(paramsStr);
				modals.openWin({
					winId: "exportData",
                	title: '导出字段选择',
                	width: '600px',
                	url: basePath+"/export/showExportColumns.html?" + paramsStr
				});
			}
		});
    }
    
    if (this.config.rowReorder) {
    	var _this = this;
    	this.table.on('row-reorder', function ( e, diff, edit ) {
        	if (diff.length == 0) {
        		return;
        	}
            var list = [];
            var dataSrc = edit.dataSrc;
            var rowId = edit.triggerRow.table().context[0].rowId;
            if(_this.config.rowReorderInTurn){
            	diff.sort(function(a,b){  
                    return a.oldPosition - b.oldPosition;  
                });
            	for(var i in diff){
            		var obj = {};
            		obj[rowId] = diff[i].node.id;
            		obj[dataSrc] = diff[i].newData;
            		list.push(obj);
            	}
            }else{
            	var values = edit.values;
            	for(var id in values) {
            		var obj = {};
            		obj[rowId] = id;
            		obj[dataSrc] = values[id];
            		list.push(obj);
            	}
            }
            var url = that.url;
            var namepace = url.substr(0, url.lastIndexOf("/"));
            ajaxPost(namepace + "/reorder.json", JSON.stringify(list), function(result) {
            	that.reloadData();
            }, false, "application/json");
        });
    }
	
//    this.initThead(table);
    //行点击事件
//    $("tbody",$("#" + that.tableId)).on("click","tr",function(event) {
//        $(this).addClass("selected").siblings().removeClass("selected");
//        //获取该行对应的数据
//        var item = that.table.row($(this).closest('tr')).data();
////        userManage.currentItem = item;
////        userManage.showItemDetail(item);
//    });
}

/**
 * 
 */
CommonTable.prototype.fnInitComplete = function (oSettings, json) {
	// 移动查询框的位置 与记录/页同行
	var _this=this;
	var $tableContainer;
	try{
		$tableContainer = this.table.table().container();
	} catch (e) {
		$tableContainer = $("#" + this.tableId + "_wrapper")[0];
	}
	
	// datatable初始化之前的回调函数
    if (_this.config.beforeInitCallback) {
    	_this.config.beforeInitCallback.call(_this);
    }
	
	$("#" +this.tableId +"_wrapper").prepend("<div class='row'><div class='col-sm-12'></div></div>");
	$("#" +this.searchDiv).appendTo($(".row:eq(0) .col-sm-12:eq(0)", $tableContainer ) );
	if ($("#" +this.searchDiv).children().legnth > 0) {
		$("#" +this.searchDiv).css("display","block");
	}
	var advancedSearchContent = $("#" +this.searchDiv).find("input").not("[type='hidden']").not(".operate-btn-group").not(".export-btn-group").length>0;
	if(advancedSearchContent && oSettings.oInit.bFilter){
		$("#" +this.searchDiv).css("display","none")
		$("#"+this.tableId +"_filter input[type='search']").after('<div class="btn-group">'
//					+ '<button type="button" class="btn" id="btn-simple-search"><i class="fa fa-search"></i></button>'
					+ '<button type="button" class="btn toggle-advanced-search" title="高级查询">'
					+ '  <i class="fa fa-angle-double-up"></i>'
					+ '</button>' 
			  + '</div>');
	}
	if(oSettings.oInit.bFilter==true){
		$("#"+this.tableId +"_filter input[type='search']").addClass("fuzzySearch").attr("placeholder","模糊查找").addClass("form-control").removeClass("input-sm");
		$("#" +this.searchDiv +" .operate-btn-group").appendTo($('#'+this.tableId+'_filter', $tableContainer ));
	}else{
		if (this.searchDivInline == true) {
			$("#" +this.searchDiv).appendTo($('#'+this.tableId+'_length', $tableContainer ).parent().next().addClass("dataTables_filter"));
		} else {
			$("#" +this.searchDiv +" .operate-btn-group").appendTo($('#'+this.tableId+'_length', $tableContainer ).parent().next().addClass("dataTables_filter"));
		}
		//$("#" +this.searchDiv).css("display","block");
		//		if(!$('.col-sm-6:eq(1)', $tableContainer ).html()){ 
//			$("#" +this.searchDiv).css("display","block").appendTo($('.col-sm-6:eq(1)', $tableContainer ) );
//		}
		
	}
	
	// 当存在自定义查询时，默认展开高级搜索
	if(!this.fuzzySearch) {
		this.fuzzySearch = !this.fuzzySearch;
		$("#" + this.tableId + "_filter " + this.advancedSearch, this.table.table().container()).click();
	}
	
	// 列头文本居中 
	//this.tableId=oSettings.sTableId
	$("#" + this.tableId + " thead tr th").removeClass("text-left").removeClass("text-right").addClass("text-center")
			//.removeClass("sorting_desc").removeClass("sorting_asc");
	
	var columns = oSettings.aoColumns;
	for(var i in columns){
		var column = columns[i];
		// 对可见的允许排序的标题栏列添加id，用于排序选择
		if(column.bVisible && column.bSortable){
			// name为排序列数据库列名， 如果有则为以此添加id；若没有默认按data作为排序列字段
			// 主要用于java属性名与数据库列名不一致的情况
			if(column.name){
				column.nTh.id = "th_"+column.name;
			}else{
				column.nTh.id = "th_"+column.data;
			}
		}
	}
	
	//行单选 	
	if (!this.disableSelect) {
		if(oSettings.oInit.singleSelect==true){
		    $('#'+this.tableId+' tbody').on( 'click', 'tr', function () {
				//HNAZO modify
		        /*if ( $(this).hasClass('selected') ) {
		            $(this).removeClass('selected');
		        } else {
		            _this.table.$('tr.selected').removeClass('selected');
		            $(this).addClass('selected');
		        } */
				if (!$(this).hasClass('selected')) {
					$('tr.selected', $tableContainer).removeClass('selected');
					$(this).addClass('selected');

					if(oSettings.oInit.rowClick){
						oSettings.oInit.rowClick.call(this,_this.getSelectedRowData(),$(this).hasClass('selected'));
					}
				}
		    });
		}else if(oSettings.oInit.singleSelect==false){
			$('#'+this.tableId+' tbody').on( 'click', 'tr', function (e) {
				//selected
				e.stopPropagation();
				if (_this.sameTrigger) {
					if (!$(this).hasClass('checked')) {
						if (_this.checkboxFlag == "iCheckCustom") {
							$("i." + _this.checkbox.singleCheckboxClass, $(this)).addClass("checked");
						} else if ($.fn.iCheck) {
							$("input[type='checkbox']", $(this)).iCheck("check");
						} else {
							$("input[type='checkbox']", $(this)).prop("checked","checked");
						}
					} else {
						if (_this.checkboxFlag == "iCheckCustom") {
							$("i." + _this.checkbox.singleCheckboxClass, $(this)).removeClass("checked");
						} else if ($.fn.iCheck) {
							$("input[type='checkbox']", $(this)).iCheck("uncheck");
						} else {
							$("input[type='checkbox']:checked", $(this)).removeAttr("checked");
						}
					}
					$(this).toggleClass('checked selected');
		    	} else {
		    		$(this).toggleClass('selected');
		    	}
			});
		}
	}
	
	$("#"+this.tableId+"_wrapper div.row").eq(1).find("div.col-sm-6").eq(0).removeClass("col-sm-6").addClass("col-sm-2");
	$("#"+this.tableId+"_wrapper div.row").eq(1).find("div.col-sm-6").eq(0).removeClass("col-sm-6").addClass("col-sm-10");
    //如果分页不可选 则空出位置 让条件区域更宽
    if(!oSettings.oInit.lengthChange){
    	$("#"+this.tableId+"_wrapper div.row").eq(1).find("div.col-sm-2").remove();
		$("#"+this.tableId+"_wrapper div.row").eq(1).find("div.col-sm-10").removeClass("col-sm-10").addClass("col-sm-12");
    }
    
    //Y轴滚动时，设置列头自适应
    if(oSettings.oInit.scrollY && !(oSettings.oInit.disableSlimScroll || false)){
    	if($.fn.slimScroll){
			$("#" + _this.tableId + "_wrapper .dataTables_scrollBody").slimScroll({
				height: "auto",
				size: 8,
				disableFadeOut: true,
				allowPageScroll: true,
				//railVisible: true
			});
			$("#" + _this.tableId + "_wrapper .dataTables_scrollBody").css("height","");
			$("#" + _this.tableId + "_wrapper .slimScrollDiv").css("height","");
//    		$("#" + that.tableId + "_wrapper .dataTables_scrollHeadInner").css({"paddingRight":"","width":""});
//    		$("#" + that.tableId + "_wrapper .dataTables_scrollHeadInner > .dataTable").css({"marginLeft":"","width":""});
		}
    	setTimeout(function(){_this.table.columns.adjust();},200); 
    	//setTimeout(function(){_this.fixHeaderWidth()},100);
    }   
    
    $("#" + _this.tableId + '_wrapper .dataTables_filter input[type="search"]').bind('keyup', function(e) {
        if (e.keyCode == 13 || (e.keyCode == 8 && (this.value.length == 0))) {
        	_this.table.page("first");
            _this.table.search(this.value).draw(false);
        }
    });
    $("#" + _this.tableId + '_wrapper #'+ _this.searchDiv +' input[type="search"]').bind('keyup', function(e) {
        if (e.keyCode == 13 || (e.keyCode == 8 && (this.value.length == 0))) {
        	_this.searchButton.click();
        }
    });
    
    // 初始化复选框事件
    if (_this.initCheckbox) {
    	var toggleClass = "checked";
    	if (_this.sameTrigger) {
    		toggleClass = "checked selected";
    	}
		if ($.fn.iCheck && _this.checkboxFlag == "iCheck") {
			$($tableContainer).on("ifClicked", "table input[type='checkbox']", function() {
				$(this).parents("tr").toggleClass(toggleClass);
				$(this).parents("tr").click();
			});
			
/*			$('input[type="checkbox"]', $tableContainer).iCheck({
	            checkboxClass: _this.checkbox.styleClass,
	        });
*/			
			$($tableContainer).on("click", "." + _this.checkbox.actionClass, function() {
				var clicks = $(this).data('clicks');
				if (clicks) {
					// 取消全选
					$("table input[type='checkbox']", $tableContainer).iCheck("uncheck").parents("tr").removeClass(toggleClass);
//					$("input[type='checkbox']", $tableContainer).parents("tr").removeClass("checked");
					$(this).removeClass(toggleClass);
				} else {
					// 全选
					$("table input[type='checkbox']", $tableContainer).iCheck("check").parents("tr").addClass(toggleClass);
//					$("input[type='checkbox']", $tableContainer).parents("tr").addClass("checked");
					$(this).addClass(toggleClass);
				}
				$(this).data("clicks", !clicks);
			});
		} else if (_this.checkboxFlag == "iCheckCustom") {
			$($tableContainer).on("click", "." + _this.checkbox.actionClass, function() {
				var clicks = $(this).data('clicks');
				if (clicks) {
					// 取消全选
					$("table i.checked." + _this.checkbox.singleCheckboxClass, $tableContainer).removeClass("checked").parents("tr").removeClass(toggleClass);
					$(this).removeClass(toggleClass);
				} else {
					// 全选
					$("table i." + _this.checkbox.singleCheckboxClass, $tableContainer).not(".checked").addClass("checked").parents("tr").addClass(toggleClass);
					$(this).addClass(toggleClass);
				}
				$(this).data("clicks", !clicks);
			});
			
			$($tableContainer).on("click", "table i." + _this.checkbox.singleCheckboxClass, function(e) {
				$(this).toggleClass("checked").parents("tr").toggleClass(toggleClass);
			});
		} else {
			$($tableContainer).on("click", "." + _this.checkbox.actionClass, function() {
				var clicks = $(this).data('clicks');
				if (clicks) {
					// 取消全选
					$("table input[type='checkbox']:checked", $tableContainer).removeAttr("checked").parents("tr").removeClass(toggleClass);
//					$("input[type='checkbox']:checked", $tableContainer).parents("tr").removeClass("checked");
					$(this).removeClass(toggleClass);
				} else {
					// 全选
					$("table input[type='checkbox']", $tableContainer).not(":checked").prop("checked","checked").parents("tr").addClass(toggleClass);
//					$("input[type='checkbox']", $tableContainer).not(":checked").parents("tr").addClass("checked");
					$(this).addClass(toggleClass);
				}
				$(this).data("clicks", !clicks);
			});
			
			$($tableContainer).on("click", "table input[type='checkbox']", function() {
				$(this).parents("tr").toggleClass(toggleClass);
			});
		}
		
    }
    
    if (_this.config.lazyLoad) {
    	if ($(".dataTables_empty", $tableContainer).length > 0) {
    		$(".dataTables_info", $tableContainer).text("暂无分页信息");
    		$(".dataTables_empty", $tableContainer).text("数据未查询");
    	}
    }
    
    //$("#" + _this.tableId).parent().addClass("table-responsive");
    $("#" + _this.tableId).wrap("<div class='table-responsive'></div>");
    
    //动态隐藏或显示列 by 01441  结合bootstrap-multiselect
    var columnSelect = $("#"+_this.tableId+"_wrapper").find('.columnSelect')
    if(columnSelect.length > 0){
    	var columns = this.config.columns;
    	
    	var localData = localStorage.getItem("DataTables_" + _this.tableId + "_" + location.pathname);
    	localData = JSON.parse(localData) || {};
		
    	initColumnSelect(columnSelect[0],columns,localData.columns);
    	//动态显示、隐藏列
        $(columnSelect[0]).change(function(event){
        	var selectName = $(this).val();
            for(var i=0,l=columns.length;i<l;i++){
                var columnName = columns[i]['title'];
                var checked = $.inArray(columnName, selectName) != -1;
                if (_this.table.column(i).visible() != checked) {
                	_this.table.column(i).visible(checked);
                }
            }
        })
    }
    
    // datatable初始化之后的回调函数
    if (_this.config.initCallback) {
    	_this.config.initCallback.call(_this);
    }
}
var initColumnSelect = function(ele, columns,localColums){
    var select = $(ele);
    for(var i=0,l=columns.length;i<l;i++){
    	if(localColums!= undefined && localColums != null){
    		if(localColums[i].visible){
    			select.append('<option selected value="'+columns[i].title+'">'+columns[i].title+'</option>')
    		}else{
    			select.append('<option value="'+columns[i].title+'">'+columns[i].title+'</option>')
    		}
    	}else{
    		select.append('<option selected value="'+columns[i].title+'">'+columns[i].title+'</option>')
    	}
    }
    //多选
    select.multiselect({
        nonSelectedText :'请选择要隐藏/显示的列',
        buttonWidth:'200px' ,
        maxHeight :400,
        numberDisplayed:0,
        buttonText:function(){
        	return '选择要隐藏/显示的列';
        }
    })
}

/**
 * 
 */
CommonLocalTable.prototype.fnInitComplete = function (oSettings, json) {
	// 移动查询框的位置 与记录/页同行
	var _this=this;
	var $tableContainer;
	try{
		$tableContainer = this.table.table().container();
	} catch (e) {
		$tableContainer = $("#" + this.tableId + "_wrapper")[0];
	}
	// 列头文本居中 
	$("#" + this.tableId + " thead tr th").removeClass("text-left").removeClass("text-right").addClass("text-center")
			//.removeClass("sorting_desc").removeClass("sorting_asc");
	
	//行单选 	
	if (!this.disableSelect) {
		if(oSettings.oInit.singleSelect==true){
		    $('#'+this.tableId+' tbody').on( 'click', 'tr', function () {
				//HNAZO modify
		        /*if ( $(this).hasClass('selected') ) {
		            $(this).removeClass('selected');
		        } else {
		            _this.table.$('tr.selected').removeClass('selected');
		            $(this).addClass('selected');
		        } */
				if (!$(this).hasClass('selected')) {
					$('tr.selected', $tableContainer).removeClass('selected');
					$(this).addClass('selected');

					if(oSettings.oInit.rowClick){
						oSettings.oInit.rowClick.call(this,_this.getSelectedRowData(),$(this).hasClass('selected'));
					}
				}
		     });
		}else if(oSettings.oInit.singleSelect==false){
			$('#'+this.tableId+' tbody').on( 'click', 'tr', function (){
				//selected
				 $(this).toggleClass('selected');
			})
		}
	}
	
	$("#"+this.tableId+"_wrapper div.row").eq(1).find("div.col-sm-6").eq(0).removeClass("col-sm-6").addClass("col-sm-2");
	$("#"+this.tableId+"_wrapper div.row").eq(1).find("div.col-sm-6").eq(0).removeClass("col-sm-6").addClass("col-sm-10");
    //如果分页不可选 则空出位置 让条件区域更宽
    if(!oSettings.oInit.lengthChange){
    	$("#"+this.tableId+"_wrapper div.row").eq(1).find("div.col-sm-2").remove();
		$("#"+this.tableId+"_wrapper div.row").eq(1).find("div.col-sm-10").removeClass("col-sm-10").addClass("col-sm-12");
    }
    
    $("#"+this.tableId +"_filter input[type='search']").addClass("fuzzySearch").attr("placeholder","模糊查找").addClass("form-control").removeClass("input-sm");
	$("#" + _this.tableId + '_wrapper .dataTables_filter input[type="search"]').bind('input', function(e) {
		_this.table.search(this.value).draw(false);
    });
	
    //Y轴滚动时，设置列头自适应
    if(oSettings.oInit.scrollY && !(oSettings.oInit.disableSlimScroll || false)){
    	if($.fn.slimScroll){
			$("#" + _this.tableId + "_wrapper .dataTables_scrollBody").slimScroll({
				height: "auto",
				size: 8,
				disableFadeOut: true,
				allowPageScroll: true,
				//railVisible: true
			});
			$("#" + _this.tableId + "_wrapper .dataTables_scrollBody").css("height","");
			$("#" + _this.tableId + "_wrapper .slimScrollDiv").css("height","");
		}
    	setTimeout(function(){_this.table.columns.adjust();},200); 
    }   
    
    if(oSettings.oInit.bFilter==true){
		$("#"+this.tableId +"_filter input[type='search']").addClass("fuzzySearch").attr("placeholder","模糊查找").addClass("form-control").removeClass("input-sm");
		$("#" +this.searchDiv +" .operate-btn-group").appendTo($('#'+this.tableId+'_filter', $tableContainer ));
	}
    
 // 初始化复选框事件
    if (_this.initCheckbox) {
    	var toggleClass = "checked";
    	if (_this.sameTrigger) {
    		toggleClass = "checked selected";
    	}
		if ($.fn.iCheck && _this.checkboxFlag == "iCheck") {
			$($tableContainer).on("ifClicked", "table input[type='checkbox']", function() {
				$(this).parents("tr").toggleClass(toggleClass);
				$(this).parents("tr").click();
			});
			
/*			$('input[type="checkbox"]', $tableContainer).iCheck({
	            checkboxClass: _this.checkbox.styleClass,
	        });
*/			
			$($tableContainer).on("click", "." + _this.checkbox.actionClass, function() {
				var clicks = $(this).data('clicks');
				if (clicks) {
					// 取消全选
					$("table input[type='checkbox']", $tableContainer).iCheck("uncheck").parents("tr").removeClass(toggleClass);
//					$("input[type='checkbox']", $tableContainer).parents("tr").removeClass("checked");
					$(this).removeClass(toggleClass);
				} else {
					// 全选
					$("table input[type='checkbox']", $tableContainer).iCheck("check").parents("tr").addClass(toggleClass);
//					$("input[type='checkbox']", $tableContainer).parents("tr").addClass("checked");
					$(this).addClass(toggleClass);
				}
				$(this).data("clicks", !clicks);
			});
		} else if (_this.checkboxFlag == "iCheckCustom") {
			$($tableContainer).on("click", "." + _this.checkbox.actionClass, function() {
				var clicks = $(this).data('clicks');
				if (clicks) {
					// 取消全选
					$("table i.checked." + _this.checkbox.singleCheckboxClass, $tableContainer).removeClass("checked").parents("tr").removeClass(toggleClass);
					$(this).removeClass(toggleClass);
				} else {
					// 全选
					$("table i." + _this.checkbox.singleCheckboxClass, $tableContainer).not(".checked").addClass("checked").parents("tr").addClass(toggleClass);
					$(this).addClass(toggleClass);
				}
				$(this).data("clicks", !clicks);
			});
			
			$($tableContainer).on("click", "table i." + _this.checkbox.singleCheckboxClass, function(e) {
				$(this).toggleClass("checked").parents("tr").toggleClass(toggleClass);
			});
		} else {
			$($tableContainer).on("click", "." + _this.checkbox.actionClass, function() {
				var clicks = $(this).data('clicks');
				if (clicks) {
					// 取消全选
					$("table input[type='checkbox']:checked", $tableContainer).removeAttr("checked").parents("tr").removeClass(toggleClass);
//					$("input[type='checkbox']:checked", $tableContainer).parents("tr").removeClass("checked");
					$(this).removeClass(toggleClass);
				} else {
					// 全选
					$("table input[type='checkbox']", $tableContainer).not(":checked").prop("checked","checked").parents("tr").addClass(toggleClass);
//					$("input[type='checkbox']", $tableContainer).not(":checked").parents("tr").addClass("checked");
					$(this).addClass(toggleClass);
				}
				$(this).data("clicks", !clicks);
			});
			
			$($tableContainer).on("click", "table input[type='checkbox']", function() {
				$(this).parents("tr").toggleClass(toggleClass);
			});
		}
		
    }
    
    //$("#" + _this.tableId).parent().addClass("table-responsive");
    $("#" + _this.tableId).wrap("<div class='table-responsive'></div>");
    
    //动态隐藏或显示列 by 01441  结合bootstrap-multiselect
    var columnSelect = $("#"+_this.tableId+"_wrapper").find('.columnSelect')
    if(columnSelect.length > 0){
    	var columns = this.config.columns;
    	
    	var localData = localStorage.getItem("DataTables_" + _this.tableId + "_" + location.pathname);
    	localData = JSON.parse(localData) || {};
		
    	initColumnSelect(columnSelect[0],columns,localData.columns);
    	//动态显示、隐藏列
        $(columnSelect[0]).change(function(event){
        	var selectName = $(this).val();
            for(var i=0,l=columns.length;i<l;i++){
                var columnName = columns[i]['title'];
                var checked = $.inArray(columnName, selectName) != -1;
                if (_this.table.column(i).visible() != checked) {
                	_this.table.column(i).visible(checked);
                }
            }
        })
    }
    
    // datatable初始化之后的回调函数
    if (_this.config.initCallback) {
    	_this.config.initCallback.call(_this);
    }
}


/**
 * 获取查询条件
 * @param data
 * @returns param
 */
CommonTable.prototype.getQueryCondition = function(data) {
	//this.showProcessing();
	var param = {};
	//组装分页参数
	param.draw = data.draw;
    param.start = data.start;
    param.pageSize = data.length;
	
	if(!this.load){
		this.load = true
		if(data.search.value != ""){
			param.fuzzySearch = true;
			param.fuzzy = data.search.value;
		}
		return param;
	}
	var _this = this;
	//组装排序参数
	if (data.order&&data.order.length) {
		var orderByList = [];
		for(var i in data.order){
			var order = data.order[i];
			var orderBy = "";
			if(order){
				var column = "";
				if (data.columns && data.columns.length > 0) {
					column = data.columns[order.column];
				}
				if(order instanceof Array) {
					var columnName = order[2];
					if(column && !columnName) {
						columnName = column.name || column.data;
					}
					if(!columnName) {
						columnName = $.trim($("thead th", this.table.table().container()).eq(order[0]).attr("id")).replace("th_","");
					}
					if(columnName){
						orderBy = columnName+" "+order[1];
						orderByList.push(orderBy);
					}
				} else {
					//与数据库表字段对于
					var columnName = "";
					if(column) {
						columnName = column.name || column.data;
					}
					if(!columnName) {
						columnName = $.trim($("thead th", this.table.table().container()).eq(order.column).attr("id")).replace("th_","");
					}
					if(columnName){
						orderBy = columnName+" "+order.dir;
						orderByList.push(orderBy);
					}
				}
			}
		}
		if(orderByList.length>0){
			param.orderBy = orderByList.join(",");
		}
	}
//	var cachedSearchData = localStorage.getItem("DataTables_" + this.tableId + "_" + location.pathname);
//	cachedSearchData = JSON.parse(cachedSearchData);
//	if(!cachedSearchData) {
//		cachedSearchData = {};
//	}
    //组装查询参数
	param.fuzzy = "";
    param.fuzzySearch = $(".fuzzySearch", this.table.table().container()).length>0 && this.fuzzySearch?true:false;
    if (param.fuzzySearch) {
//    	cachedSearchData.fuzzySearch = true;
        param.fuzzy = $(".fuzzySearch", this.table.table().container()).val();
    } else{
//    	cachedSearchData.fuzzySearch = false;
//    	cachedSearchData.customSearch = {};
    	this.fuzzySearch = false;
    	this.customSearch = {};
//    	param.model = {};
    	$("input[type='search']", this.table.table().container()).not(".fuzzySearch").each(function(){
    		var value = $(this).val();
    		var name = $(this).attr("name");
    		if(value && name){
//    			param["model."+name] = value;
    			param[name] = value;
//    			cachedSearchData.customSearch[name] = value;
    			_this.customSearch[name] = value;
    		}
    	});
    	$("select[type='search']", this.table.table().container()).each(function(){
    		var value = $(this).val();
    		var name = $(this).attr("name");
    		if(value && name){
//    			param["model."+name] = value;
    			try{
    				if (value instanceof Array) {
						try{
    						parseInt(value);
    						param[name] = value.toString();
    					} catch (e) {
    						//param[name] = "'" + value.join("','") + "'";
    						// 使用FIND_IN_SET 字符串不需要加'';
    						param[name] = value.toString();
    					}
    				} else {
    					if(isNaN(Number(value))){
    						param[name] = value;
    					}else{
    						param[name] = Number(value);
    					}
    				}
//    				cachedSearchData.customSearch[name] = value;
					_this.customSearch[name] = value;
    			}catch(e){
    				param[name] = value;
//    				cachedSearchData.customSearch[name] = value;
    				_this.customSearch[name] = value;
    			}
    		}
    	});
    }
    // 增加type=hidden 的隐藏域参数
	$("input[data-type='search']", this.table.table().container()).each(function(){
		var value = $(this).val();
		var name = $(this).attr("name");
		var type = $(this).attr("type");
		if ((type == "checkbox" || type == "radio") && !this.checked) {
			return 
		}
		if(value && name){
//    			param["model."+name] = value;
			param[name] = value;
//    			cachedSearchData.customSearch[name] = value;
			if (typeof _this.customSearch === "undefined") {
				_this.customSearch = {};
			}
			_this.customSearch[name] = value;
		}
	});
    
	if (_this.searchDivInline) {
		param.fuzzySearch = true;
	}
    //localStorage.setItem("DataTables_" + this.tableId + "_" + location.pathname,JSON.stringify(cachedSearchData));
    return param;
}

//新增，刷新界面
CommonTable.prototype.reloadData=function(){
	this.table.page('first').draw(false);  
}

//刷新当前页面，并定位到行
CommonTable.prototype.reloadRowData = function(rowId) {
	var pageIndex = this.table.page();
	this.table.page(pageIndex).draw(false);
	if (rowId) {// 定位选中到当前行
		this.selectRow(rowId);
	}
}

//刷新本地数据
CommonLocalTable.prototype.reloadData = function (data) {
	if (!data) {
		data = this.table.data();
	}
    this.table.clear();
    this.table.rows.add(data);
    this.table.page("first").draw(false);  
}

// 刷新本地数据当前页面，并定位到行
CommonLocalTable.prototype.reloadRowData = function(data, rowId) {
	var currentPage = this.table.page();
	this.table.clear()  
    this.table.rows.add(data)  
	this.table.page(currentPage).draw(false);
	if (rowId) {// 定位选中到当前行
		this.selectRow(rowId);
	}
}

// 获取选中行数据
CommonTable.prototype.getSelectedRowId = getSelectedRowId;
CommonTable.prototype.getSelectedRowsId = getSelectedRowsId;
CommonTable.prototype.getSelectedRowData = getSelectedRowData;
CommonTable.prototype.getSelectedRowsData = getSelectedRowsData;
CommonTable.prototype.getCheckedRowId = getCheckedRowId;
CommonTable.prototype.getCheckedRowsId = getCheckedRowsId;
CommonTable.prototype.getCheckedRowData = getCheckedRowData;
CommonTable.prototype.getCheckedRowsData = getCheckedRowsData;

CommonLocalTable.prototype.getSelectedRowId = getSelectedRowId;
CommonLocalTable.prototype.getSelectedRowsId = getSelectedRowsId;
CommonLocalTable.prototype.getSelectedRowData = getSelectedRowData;
CommonLocalTable.prototype.getSelectedRowsData = getSelectedRowsData;
CommonLocalTable.prototype.getCheckedRowId = getCheckedRowId;
CommonLocalTable.prototype.getCheckedRowsId = getCheckedRowsId;
CommonLocalTable.prototype.getCheckedRowData = getCheckedRowData;
CommonLocalTable.prototype.getCheckedRowsData = getCheckedRowsData;

/**
 * 获取当前选中行的id 单选
 */
function getSelectedRowId() {
	if(this.table.row('.selected').length>0)
	   return this.table.row('.selected').id();
	return null; 
}

/**
 * 获取当前选中行的id 多选
 */
function getSelectedRowsId() {
	if(this.table.row('.selected').length>0)
	   return this.table.rows('.selected').ids();
	return null; 
}

/**
 * 获取当前选中行的数据 单选
 */
function getSelectedRowData() {
	if(this.table.row('.selected').length>0)
		return this.table.row('.selected').data();
	return null;
}

/**
 * 获取当前选中行的数据 多选
 */
function getSelectedRowsData(){
	var datas=null;
    var rows=this.table.rows('.selected').data();
    if(rows.length==0)
    	return datas;
    datas=[];
    for(var i=0;i<rows.length;i++){
    	datas.push(rows[i]);
    }
    return datas;
}

/**
 * 获取复选框选中行的id 返回第一个id
 */
function getCheckedRowId() {
	if(this.table.row('.checked').length>0)
	   return this.table.row('.checked').id();
	return null; 
}

/**
 * 获取复选框选中行的id 多选
 */
function getCheckedRowsId() {
	if(this.table.row('.checked').length>0)
	   return this.table.rows('.checked').ids();
	return null; 
}

/**
 * 获取复选框选中行的数据 单选返回第一行数据
 */
function getCheckedRowData() {
	if(this.table.row('.checked').length>0)
		return this.table.row('.checked').data();
	return null;
}

/**
 * 获取复选框选中行的数据 多选
 */
function getCheckedRowsData(){
	var datas=null;
    var rows=this.table.rows('.checked').data();
    if(rows.length==0)
    	return datas;
    datas=[];
    for(var i=0;i<rows.length;i++){
    	datas.push(rows[i]);
    }
    return datas;
}

//选中行
CommonTable.prototype.selectRow=function(rowId, triggerEvent){
	if(rowId){
		this.selectRowWithSelector("#"+rowId, triggerEvent);
	}
}

//选中行
CommonLocalTable.prototype.selectRow=function(rowId, triggerEvent){
	if(rowId){
		this.selectRowWithSelector("#"+rowId, triggerEvent);
	}
}

//选中第一行
CommonTable.prototype.selectFirstRow=function(triggerEvent){
	this.selectRowWithSelector("tr:first",triggerEvent);
}

//通用选择
CommonTable.prototype.selectRowWithSelector=function(selector,triggerEvent){
	if(selector){
		if(triggerEvent){
			this.table.$(selector).click();
		}else{ 
		    this.table.$('tr.selected').removeClass('selected');
	        this.table.$(selector).addClass('selected');
		}
	}
}
CommonLocalTable.prototype.selectRowWithSelector=function(selector,triggerEvent){
	if(selector){
		if(triggerEvent){
			this.table.$(selector).click();
		}else{ 
		    this.table.$('tr.selected').removeClass('selected');
	        this.table.$(selector).addClass('selected');
		}
	}
}
/**
 * 初始化表格标题栏，对可见的允许排序的标题栏列添加id，用于排序选择
 */
CommonTable.prototype.initThead = function () {  
	var columns = this.table.context[0].aoColumns;
	for(var i in columns){
		var column = columns[i];
		// 对可见的允许排序的标题栏列添加id，用于排序选择
		if(column.bVisible && column.bSortable){
			// name为排序列数据库列名， 如果有则为以此添加id；若没有默认按data作为排序列字段
			// 主要用于java属性名与数据库列名不一致的情况
			if(column.name){
				column.nTh.id = "th_"+column.name;
			}else{
				column.nTh.id = "th_"+column.data;
			}
		}
	}
	
//	$("#"+this.tableId +"_filter input[type='search']").attr("id","fuzzySearch");
	var $fuzzySearch = $("#"+this.tableId +"_filter input[type='search']");
	if($fuzzySearch){
		$fuzzySearch.attr("id","fuzzySearch");
		$("#" + this.tableId + "_filter").append('<label><div class="btn-group">'
							+ '<button type="button" class="btn" id="btn-simple-search"><i class="fa fa-search"></i></button>'
							+ '<button type="button" class="btn" title="高级查询" id="toggle-advanced-search">'
							+ '  <i class="fa fa-angle-double-up"></i>'
							+ '</button>' 
					  + '</div></label>');
		$("#" +this.searchDiv).appendTo($('.col-sm-6:eq(0)', this.table.table().container()) );
	}else{
		$("#" +this.searchDiv).appendTo($('.col-sm-6:eq(0)', this.table.table().container()) );
	}
}
 
/**
 * 获取数据
 * 
 * @param aoData
 * @param fnCallback
 * @param oSettings
 */
CommonTable.prototype.getServerData = function(aoData, fnCallback, oSettings) {//ajax配置为function,手动调用异步查询
    var that = this;
	//封装请求参数
	var params = {};
	if(aoData) {
		params = this.getQueryCondition(aoData);
	} else {
		var searchData = localStorage.getItem("DataTables_" + that.tableId + "_" + location.pathname);
		searchData = JSON.parse(searchData);
		params.lazyLoad = that.config.lazyLoad;
		if(searchData) {
			params.draw = 0;
		    params.start = searchData.start;
		    params.pageSize = searchData.length;
		    params.order = searchData.order;
		    if(searchData.order && searchData.order.length) {
		    	var orderByList = [];
	    	 	for(var i in searchData.order) {
		 	    	var order = searchData.order[i];
		 			var orderBy = "";
		 			if(order){
		 				//与数据库表字段对于
		 				var columnName = order[2];
		 				if(columnName){
		 					orderBy = columnName+" "+order[1];
		 					orderByList.push(orderBy);
		 				}
		 			}
		 	    }
		 	    if(orderByList.length>0){
		 			params.orderBy = orderByList.join(",");
		 		}
		    }
		    if(searchData.fuzzySearch || searchData.fuzzySearch == null) {
		    	if(searchData.search) {
			    	params.fuzzySearch = true;
			 		params.fuzzy = searchData.search.search;
			    }
		    } else {
		    	if(searchData.customSearch) {
		    		for(var name in searchData.customSearch) {
		    			if (searchData.customSearch[name] instanceof Array) {
		    				var value = searchData.customSearch[name];
		    				try{
	    						parseInt(value);
	    						params[name] = value.toString();
	    					} catch (e) {
	    						params[name] = "'" + value.join("','") + "'";
	    					}
		    			} else {
		    				params[name] = searchData.customSearch[name];
		    			}
		    			that.customSearch[name] = searchData.customSearch[name];
		    		}
		    		params.lazyLoad = false;
		    	}
		    }
		} else {
			params.draw = 0;
		    params.start = 0;
		    params.pageSize = 10;
		}
		if (that.config.paging == false) {
	    	params.pageSize = -1;
	    }
		if (that.searchDivInline) {
			params.fuzzySearch = true;
		}
		$("input[data-type='search']", $("#" + that.searchDiv)).each(function(){
			var value = $(this).val();
			var name = $(this).attr("name");
			var type = $(this).attr("type");
			if ((type == "checkbox" || type == "radio") && !this.checked) {
				return 
			}
			if(value && name){
//	    			param["model."+name] = value;
				params[name] = value;
//	    			cachedSearchData.customSearch[name] = value;
				if (typeof that.customSearch === "undefined") {
					that.customSearch = {};
				}
				that.customSearch[name] = value;
			}
		});
	}
	var retData = {};
	ajaxGet(this.url, params, function(result) {
		 //封装返回数据，这里仅演示了修改属性名
		var tableData = {};
        tableData.draw = result.pageParam.draw;//这里直接自行返回了draw计数器,应该由后台返回
        tableData.recordsTotal = result.pageParam.total;
        tableData.recordsFiltered = result.pageParam.filtered;//后台不实现过滤功能，每次查询均视作全部结果
        tableData.data = result.data;
        retData.columns = result.pageParam.columns;
        retData.rowId = result.pageParam.rowId;
        retData.tableData = tableData;
        that.data = retData;
        
        // 删除以保存的属性，保存返回的其他拓展属性
        delete result.pageParam;
        delete result.data;
        retData.extData = result;
        
        if(!that.load && that.data) {
        	fnCallback.call(that, that.tableId, that.url, that.searchDiv);
        } else {
        	fnCallback.call(that, tableData);
        }
        
        //调用DataTables提供的callback方法，代表数据已封装完成并传回DataTables进行渲染
        //此时的数据需确保正确无误，异常判断应在执行此回调前自行处理完毕
	});
}

/**
 * 获取数据
 * 
 * @param sSource
 * @param aoData
 * @param fnCallback
 * @param oSettings
 */
CommonTable.prototype.fnServerData = function(aoData, fnCallback, oSettings) {
	this.showProcessing();
	var _this = this;
	setTimeout(function() {
		if(!_this.load && _this.data) {
			_this.load = true;
			fnCallback(_this.data.tableData);
		} else {
			var rtnData = _this.getServerData(aoData, fnCallback, oSettings);
			//fnCallback(rtnData.tableData);
			//_this.table.columns.adjust();
		}
	}, 0);
}

/**
 * 重绘的回调函数
 * 
 * @param sSource
 * @param aoData
 * @param fnCallback
 * @param oSettings
 */
CommonTable.prototype.fnDrawCallback = function(oSettings) {
	// 复选框iCheck样式
	var _this = this;
    if (_this.initCheckbox) {
    	var $tableContainer = _this.table.table().container();
    	$("."+ _this.checkbox.actionClass, $tableContainer).removeClass("checked");
    	if ($.fn.iCheck && _this.checkboxFlag == "iCheck") {
			$('table input[type="checkbox"]', $tableContainer).iCheck({
	            checkboxClass: _this.checkbox.styleClass,
	        });
		}
    }
    _this.table.columns.adjust();
    // 自定义drawCallback
    if (oSettings.oInit.customDrawCallback) {
    	oSettings.oInit.customDrawCallback.call(this, oSettings);
    }
}

CommonTable.prototype.fnStateLoaded = function (settings, data) {
	console.log("fnStateLoaded");
	this.stateData = data;
	if(data.fuzzySearch != null) {
		this.fuzzySearch = data.fuzzySearch;
	}
	this.customSearch = data.customSearch;
	var customSearch = data.customSearch;
	if(customSearch) {
		for(var name in customSearch) {
			var value = customSearch[name];
			$("input[name='"+name+"']", $("#" + this.searchDiv)).val(value);
			$("select[name='"+name+"']", $("#" + this.searchDiv)).val("");
			$("select[name='"+name+"']", $("#" + this.searchDiv)).val(value);
			$("input[data-type='search'][name='"+name+"']", $("#" + this.searchDiv)).val(value);
		}
//		if($.fn.select2) {
//			$("select.select2", $("#" + this.searchDiv)).trigger("change");
//		}
		$(".daterange-btn", $("#" + this.searchDiv)).each(function(i,item){
			var startTime = $(this).find(".daterange-input-start").val();
			var endTime = $(this).find(".daterange-input-end").val();
			if (startTime && endTime) {
				$(this).find(".daterange-span").text(startTime + " - " + endTime.split(" 23:59:59")[0]);
				var _this = this;
				_ti = setInterval(function() {
					$(_this).data('daterangepicker').setStartDate(startTime);
					$(_this).data('daterangepicker').setEndDate(endTime);
					clearInterval(_ti);
				}, 200);
			} else {
				$(this).find(".daterange-span").text("不限");
			}
		});
	}
}

CommonTable.prototype.fnStateSaveParams = function (settings, data) {
//	if(!this.fuzzySearch && JSON.stringify(this.customSearch) != "{}") {
	for(var i in settings.aaSorting ) {
		var sort = settings.aaSorting[i];
		var column = settings.aoColumns[sort[0]];
		if(column.name){
			sort[2] = column.name;
		}else{
			sort[2] = column.data;
		}
		settings.aaSorting[i] = sort;
	}
	data.order = $.extend( true, [], settings.aaSorting );
	if(JSON.stringify(this.customSearch) != "{}") {
		data.fuzzySearch = this.fuzzySearch;
		data.customSearch = this.customSearch;
	} else {
		delete data.fuzzySearch;
		delete data.customSearch;
	}
}

CommonTable.prototype.createExportBtnGroup = function () {
	if(this.exporting) {
		var sourceObject = this.exportData.sourceObject;
		var exportTypes = this.exportData.type;
		var fullServiceName = this.exportData.fullServiceName;
//		var simpleServiceName = this.exportData.simpleServiceName;
		if(exportTypes === undefined || exportTypes.length == 0) {
			exportTypes = ["excel"];
		} else if ( $.inArray("excel", exportTypes) == -1 ) {
			exportTypes.push("excel");
		}
		var html = "<div class='export-btn-group btn-group'>";
		for(var i in exportTypes ) {
			var type = exportTypes[i];
			var typeTemp = type.substr(0,1).toUpperCase() + type.substr(1);
			html += '<a class="btn export'+typeTemp+'Btn" data-source-fullServiceName="'+fullServiceName+'"   data-source-object="' + sourceObject + '" data-export-type="'+type+'" title="导出'+typeTemp+'">'
					  +'<i class="fa fa-fw fa-file-' + type + '-o text-success"></i>'
				  +'</a>';
		}
		html += "</div>"
		return html;
	}
} 

CommonTable.prototype.showProcessing = function() {
	var pheight = $("#" + this.tableId).parents(".box-body:first").height();
	var cheight = $("#" + this.tableId + "_processing").css("height","100%").height();
	var footerHeight = $("#" + this.tableId + "_wrapper .row:last").height();
	var topOffset = pheight - cheight - footerHeight;
	if(pheight > cheight) {
		$("#" + this.tableId + "_processing").css("height",pheight);
	}
	
	$("#" + this.tableId + "_processing").css("top", - topOffset);
	$("#" + this.tableId + "_processing").show();
}

function initCheckboxColumn(checkbox_config) {
	if (typeof checkbox_config !== "object") {
		checkbox_config = {};
	} 
	var actionClass = checkbox_config.actionClass || "checkbox-toggle-all";
	var singleCheckboxClass = checkbox_config.singleCheckboxClass || "checkbox-toggle-single";
	var styleClass = checkbox_config.styleClass || "icheckbox_flat-green";
	var titleClass = checkbox_config.titleClass || "td-checkbox";
	var rowId = checkbox_config.rowId || "id";
	var checkboxFlag = this.checkboxFlag;
	this.checkbox = {
			rowId:rowId,
			actionClass:actionClass,
			styleClass:styleClass,
			titleClass:titleClass,
			flag: checkboxFlag,
			singleCheckboxClass: singleCheckboxClass,
	}
	var checkboxColumn = {
		title: "<i class='" + styleClass + " " + actionClass +"'></i>",
	    className: "text-center " + titleClass,
	    width: "40px",
	    data: rowId,
	    sortable: false,
	    render: function (data, type, row, meta) {
	    	if (checkboxFlag == "iCheckCustom") {
	    		return '<i class="icheckbox_flat-green checkbox-toggle-single"></i>';
	    	} else {
	    		return '<input type="checkbox" value="' + data + '" class="iCheck">'; 
	    	}
	    }
	}
	return checkboxColumn;
}

/*
 * 初始化复选框列配置
 */
CommonTable.prototype.initCheckboxColumn = initCheckboxColumn;
CommonLocalTable.prototype.initCheckboxColumn = initCheckboxColumn;