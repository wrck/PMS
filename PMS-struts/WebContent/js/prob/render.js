$(document).ready(function() {
	$(document).on("change", ".affectedType", function(){
		var $sourceTrs = $(this).parents(".softVersionSub:first,.softVersion:first");
       	var $sourceTr = $sourceTrs.first();
       	var affectedType = $(this).val() || 0;
       	$sourceTr.find("input[name$='affectedType']").val(affectedType);
       	$sourceTr.find("input[name$='affectedTypeName']").val($(this).find("option:checked").text());
	});
	$(document).on("click", ".glyphicon-minus", function(){
        var $sourceTrs = $(this).parents(".softVersionSub:first,.softVersion:first");
       	var $sourceTr = $sourceTrs.first();
		var $container = $sourceTr.parents(".softVersionList:first");
       	if ($sourceTr.hasClass("softVersionSub") && $sourceTr.siblings(".softVersionSub").length == 0) {
       		$sourceTr.parents(".softVersion:first").remove();
       	} else {
           	$sourceTr.remove();
       	}
        $(".rowNum", $container).each(function(index){
        	var $tr = $(this).parents(".softVersionSub:first,.softVersion:first").first();
        	var groupIndex = $(".softVersion", $container).index($tr);
        	var rowNum = groupIndex;
        	if ($tr.hasClass("softVersionSub")) {
        		rowNum = $tr.parents(".softVersion:first").find(".softVersionSub").index($tr);
        	} else {
	        	$tr.attr("groupIndex", rowNum);
        	}
        	$tr.find(".softVersionSub").attr("groupIndex", rowNum);
            $(this).text(rowNum + 1);
        	var newIndex = $(".rowNum", $container).not($(".softVersion", $container).has(".softVersionSub").find(".rowNum:first")).index($(this));
            $(this).parent().attr("index", newIndex).find("input[name],select[name]").each(function () {
                var name = $(this).attr("name");
                $(this).attr("name", name.replace(/\[\d+\]/, "[" + newIndex + "]"));
            });
        });
	});
});
function parserSoftVersion(softVersion, $container) {
	$.ajax({
		url:"sys/probAjax_parserSoftVersion.action",
		type:"post",
		dataType:"json",
		data: flattenObject({
			softVersion: softVersion
		}),
		success:function(data){
			var affectedType = softVersion.affectedType;
			var versionMap = JSON.parse(data.result);
			console.log(versionMap);
			for (var entryKey in versionMap) {
				var timestamp = new Date().getTime();
				var prevGroupCount = $(".softVersion", $container).length;
				var prevIndex = $(".rowNum", $container).not($(".softVersion", $container).has(".softVersionSub").find(".rowNum:first")).length;
				var i = 0;
				var entryMap = versionMap[entryKey] || {};
				var html = renderSoftVersion($.extend({}, softVersion, {
					manualEntry: entryKey, 
					entryMap, 
					affectedType,
					groupId: timestamp
				}), {$container});
				/*var isNotGroup = $.isEmptyObject(entryMap);
				var	html =`<div class="softVersion">
					${isNotGroup ? "" : `<div class="groupManualEntry">`}
						<div class="softVersionContainer">
							<span class="rowNum">${prevGroupCount + 1}</span>.&nbsp;
							${isNotGroup ? `<input type="hidden" name="softVersionList[${prevIndex + i}].manualEntry" value="${manualEntry}">` : ""}
							${entryKey}
						</div>
						<span class="glyphicon glyphicon-minus text-danger pull-right flex-algin-self-center softVersionDel"></span>
					${isNotGroup ? "" : `</div>`}
					`;
				for(var subKey in entryMap) {
					var ranges = entryMap[subKey] || [];
					// 如果内容相同，则不分组
					var groupId = entryKey != subKey ? timestamp : 0;
					html += renderSoftVersionSub(prevGroupCount, prevIndex, i++, {
						manualEntry: entryKey, 
						manualEntrySub: subKey, 
						affectedType, ranges, groupId
					});
				}
				html += "</div>";*/
				appendSoftVersion($container, html);
			}
		},
		complete:function(){
			$("#manualEntry").val("");
		}
	});
};
		
function renderSoftVersions(json, options) {
	options = options || {};
	var $container = options.$container, 
		readOnly = options.readOnly, 
		ignoreSub = options.ignoreSub, 
		onlyAppend = options.onlyAppend;
	ignoreSub = ignoreSub == true ? true : false;
	readOnly = readOnly == true ? true : false;
	onlyAppend = onlyAppend == true ? true : false;
	if (!onlyAppend) {
	    clearSoftVersion($container);
	}
    var softVersionList = [];
    if ($.isArray(json)) {
    	softVersionList = json
    } else if (typeof json == "string"){
    	try {
	    	softVersionList = JSON.parse(json || "[]");
	    } catch(e) {
	    	console.error(e);
	    }
    } else {
    	softVersionList = [json];
    }
    var softVersionGroupMap = {};
    var softVersions = [];
    for(var i in softVersionList) {
    	var softVersion = softVersionList[i];
        var groupId = softVersion.groupId;
        if (groupId) {
            if (!softVersionGroupMap[groupId]) {
                softVersionGroupMap[groupId] = softVersion;
                softVersions.push(softVersion);
            }
            var group = softVersionGroupMap[groupId];
            var children = group.children || [];
            children.push($.extend(true, {}, softVersion));
            group.children = children;
        } else {
            softVersions.push(softVersion);
        }
    }
    for(var softVersion of softVersions) {
        var html = renderSoftVersion(softVersion, {
	        $container, 
	        readOnly, 
	        ignoreSub
        });
        appendSoftVersion($container, html);
    }
}

var affectedTypeMap = {
	undefined: "所有系列",
	"": "所有系列",
	0: "所有系列",
	1: "盒式系列",
	2: "框式系列",
	"-1": "其它系列"
};
function renderAffectedTypeSelected(name, affectedType) {
	var select = `<select name="${name}" class="form-control affectedType" style="width: auto;">
	    <option value="0">所有系列</option>
	    <option value="1">盒式系列</option>
	    <option value="2">框式系列</option>
	</select>`;
	var $select = $(select);
	$select.find(`[value='${affectedType || 0}']`).attr("selected", true);
	$select.val(affectedType || 0).trigger("change");
	return $select[0].outerHTML;
}

var platformTypeMap = {
	undefined: "Conplat平台",
	"": "Conplat平台",
	"conplat": "Conplat平台",
	"other": "其它平台"
};
function renderPlatformTypeSelected(name, platformType) {
	var select = `<select name="${name}" class="form-control platformType" style="width: auto;">
	    <option value="conplat">Conplat平台</option>
	    <option value="other">其它平台</option>
	</select>`;
	var $select = $(select);
	$select.find(`[value="${platformType || 'conplat'}"]`).attr("selected", true);
	$select.val(platformType || 'conplat').trigger("change");
	return $select[0].outerHTML;
}

var platformTypeMap = {
	undefined: "Conplat平台",
	"": "Conplat平台",
	"conplat": "Conplat平台",
	"other": "其它平台"
};
var releaseTypeMap = {
	"S": "S",
	"B": "B",
	"A": "A",
	"H": "H"
};
var architectureTypeMap = {
	"111": "111",
	"211": "211",
	"311": "311",
	"511": "511"
};
var branchTypeMap = {
	undefined: "Conplat平台",
	"": "Conplat平台",
	"conplat": "Conplat平台",
	"other": "其它平台"
};
var renderTypeMap = {
	affectedType: affectedTypeMap,
	platformType: platformTypeMap,
	releaseType: releaseTypeMap,
	architectureType: architectureTypeMap,
	branchType: branchTypeMap
}
// 从级联参数中获取，防止重复定义
renderTypeMap = extractMapFromResolvedCascade(resolveRefs(cascadeData, cascadeData.definitions));
renderTypeMap['affectedType'] = renderTypeMap['affectedType'] || affectedTypeMap;

function getTypeSelected(name, renderType, renderValue) {
	var typeMap = renderTypeMap[renderType] || {};
	return `${typeMap[renderValue]}` || '';
}
function renderTypeSelected(name, renderType, renderValue) {
	var select = `<select name="${name}" class="form-control sofoVersionType ${renderType}" style="width: auto;">
	</select>`;
	var $select = $(select);
	var typeMap = renderTypeMap[renderType] || {};
	for ( var type in typeMap) {
		if (type == undefined || type == 'undefined' || type == '' || type == 'null') {
			continue;
		}
		$select.append(`<option value="${type}">${typeMap[type]}</option>`);
	}
	$select.find(`[value="${renderValue || 'conplat'}"]`).attr("selected", true);
	$select.val(renderValue || 'conplat').trigger("change");
	return $select[0].outerHTML;
}

function softVersionTypeRender(softVersion, renderType, options) {
	options = options || {};
	var renderTypeValue = softVersion[renderType];
	renderTypeValue = renderTypeValue != undefined ? renderTypeValue : "";
	var renderTypeLabelValue = softVersion[`${renderType}Name`];
	var renderTypeLabel = renderTypeMap[renderType][renderTypeValue];
	var renderTypeName = `softVersionList[${options.index}].${renderType}`;
	var renderTypeNameName = `${renderTypeName}Name`;
	return `${options.readOnly ? 
		(!options.isSub ? `<span class="${renderType}-label">${renderTypeLabel ? renderTypeLabel + '：' : ''}</span>` : "") 
		: 
		(!options.isSub ? renderTypeSelected(options.isNotGroupEdit ? renderTypeName : "", renderType, renderTypeValue) 
			: `<input type="hidden" name="${renderTypeName}" value="${renderTypeValue}">`)}
			${options.isNotGroupEdit ? `<input type="hidden" name="${renderTypeNameName}" value="${renderTypeLabelValue || renderTypeLabel || ''}">` : ""}`;
}

function renderSoftVersion(softVersion, options) {
	options = options || {};
	var $container = options.$container, 
		readOnly = options.readOnly, 
		ignoreSub = options.ignoreSub, 
		groupId = options.groupId || softVersion.groupId, 
		groupIndex = options.groupIndex, 
		rowNum = options.rowNum, 
		offset = options.offset;
	groupIndex = groupIndex != undefined ? groupIndex : $(".softVersion", $container).length;
	rowNum = rowNum != undefined ? rowNum : $(".rowNum", $container).not($(".softVersion", $container).has(".softVersionSub").find(".rowNum:first")).length;
	readOnly = readOnly == true ? true : false;
	ignoreSub = ignoreSub == true ? true : false;
	var isSub = offset != undefined;
	var isNotGroup = !(groupId || softVersion.groupId) || isSub || ignoreSub;
	var isGroup = !isNotGroup;
	var index = (rowNum || 0) + (offset || 0);
	var cssClass = "softVersion" + (isSub ? "Sub" : "");
	var isNotGroupEdit = isNotGroup && !readOnly;
	var isSubEdit = isSub && !readOnly;
	if (ignoreSub && isSub) {
		return "";
	}
	
	var renderOptions = {groupId, groupIndex, rowNum, offset, readOnly, isSub, isSubEdit, ignoreSub, isNotGroup, isNotGroupEdit, index, cssClass};
	var renderSoftVersionTypeOptions = $.extend(true, {}, renderOptions, {readOnly: !isSub || readOnly});
	var html = `<div class="${cssClass}" groupId="${groupId}" groupIndex="${groupIndex}">
		${isNotGroup ? "" : `<div class="groupManualEntry">`}
		<div class="softVersionContainer" index="${index}">
			<span class="rowNum">${(isSub ? offset : groupIndex) + 1}</span>.&nbsp;`+
//			//`${isNotGroupEdit ? `<input type="hidden" name="softVersionList[${index}].affectedType" value="${softVersion.affectedType}">` : ""}` +
//			`${readOnly ? (!isSub ? `${affectedTypeMap[softVersion.affectedType]}：` : "") : (!isSub ? renderAffectedTypeSelected(isNotGroupEdit ? `softVersionList[${index}].affectedType` : "", softVersion.affectedType) : `<input type="hidden" name="softVersionList[${index}].affectedType" value="${softVersion.affectedType}">`)}` +
//			//`${!isSub ? `${affectedTypeMap[softVersion.affectedType]}：` : ""}` +
			`${softVersionTypeRender(softVersion, 'affectedType', renderOptions)}` +
//			`${isNotGroupEdit ? `<input type="hidden" name="softVersionList[${index}].affectedTypeName" value="${softVersion.affectedTypeName}">` : ""}` +
			//`${readOnly ? (!isSub ? `${platformTypeMap[softVersion.platformType]}：` : "") : (!isSub ? renderPlatformTypeSelected(isNotGroupEdit ? `softVersionList[${index}].platformType` : "", softVersion.platformType) : `<input type="hidden" name="softVersionList[${index}].platformType" value="${softVersion.platformType}">`)}` +
			`${softVersionTypeRender(softVersion, 'platformType', renderSoftVersionTypeOptions)}` +
			`${softVersionTypeRender(softVersion, 'releaseType', renderSoftVersionTypeOptions)}` +
			`${softVersionTypeRender(softVersion, 'architectureType', renderSoftVersionTypeOptions)}` +
			`${softVersionTypeRender(softVersion, 'branchType', renderSoftVersionTypeOptions)}` +
			`${isNotGroupEdit && softVersion.conp ? `<input type="hidden" name="softVersionList[${index}].conp" value="${softVersion.conp}">` : ""}
			${softVersion.conp ? `conp:${softVersion.conp}` : ""}
			${isNotGroupEdit && softVersion.boot ? `<input type="hidden" name="softVersionList[${index}].boot" value="${softVersion.boot}">` : ""}
			${softVersion.boot ? `boot:${softVersion.boot}` : ""}
			${isNotGroupEdit && softVersion.cpld ? `<input type="hidden" name="softVersionList[${index}].cpld" value="${softVersion.cpld}">` : ""}
			${softVersion.cpld ? `cpld:${softVersion.cpld}` : ""}
			${isNotGroupEdit && softVersion.pcb ? `<input type="hidden" name="softVersionList[${index}].pcb" value="${softVersion.pcb}">` : ""}
			${softVersion.pcb ? `pcb:${softVersion.pcb}` : ""}
			
			${isNotGroupEdit && softVersion.manualEntry ? `<input type="hidden" name="softVersionList[${index}].manualEntry" value="${softVersion.manualEntry}">` : ""}
			${!isSub && softVersion.manualEntry ? `${softVersion.manualEntry}` : ""}
			${isSubEdit && softVersion.manualEntrySub ? `<input type="hidden" name="softVersionList[${index}].manualEntrySub" value="${softVersion.manualEntrySub}">` : ""}
			${isSub && softVersion.manualEntrySub ? `<span class="softVersionRangePart label label-info">${softVersion.manualEntrySub}</span> -> ` : ""}
			${isSubEdit && softVersion.entryStart ? `<input type="hidden" name="softVersionList[${index}].entryStart" value="${softVersion.entryStart}">` : ""}
			${isSubEdit && softVersion.entryEnd ? `<input type="hidden" name="softVersionList[${index}].entryEnd" value="${softVersion.entryEnd}">` : ""}` +
			`${isSub ? `<span class="softVersionRange label label-primary">${softVersion.entryStart}</span> ~ <span class="softVersionRange label label-primary">${softVersion.entryEnd}</span>` : ""}` +
			`${isSubEdit && softVersion.markStart ? `<input type="hidden" name="softVersionList[${index}].markStart" value="${softVersion.markStart}">` : ""}
			${isSubEdit && softVersion.markEnd ? `<input type="hidden" name="softVersionList[${index}].markEnd" value="${softVersion.markEnd}">` : ""}` +
			//`${isSub ? `<span class="softVersionRange label label-primary">${softVersion.markStart}</span> ~ <span class="softVersionRange label label-primary">${softVersion.markEnd}</span>` : ""}` +
			`${isSubEdit && softVersion.groupId ? `<input type="hidden" name="softVersionList[${index}].groupId" value="${softVersion.groupId}">` : ""}
		</div>
		${!readOnly ? `<span class="glyphicon glyphicon-minus text-danger pull-right flex-algin-self-center softVersionDel"></span>` : ""}
		${isNotGroup ? "" : `</div>`}`;
	if (softVersion.children) {
		var i = 0;
		var children = softVersion.children;
		for ( var childIndex in children) {
			html += renderSoftVersion(children[childIndex], $.extend({}, softVersion, {
				$container,
				//affectedType: softVersion.affectedType || options.affectedType,
				readOnly, ignoreSub, groupId, groupIndex, 
				rowNum: index, 
				offset: i++
			}));
		}
	} else if (softVersion.entryMap) {
		var i = 0;
		var entryMap = softVersion.entryMap;
		for(var subKey in entryMap) {
			var ranges = entryMap[subKey] || [];
			html += renderSoftVersionSub(groupIndex, index, i++, $.extend({}, softVersion, {
				affectedType: softVersion.affectedType,
				manualEntry: softVersion.manualEntry, 
				manualEntrySub :subKey, 
				ranges, groupId
			}));
		}
	}
	html += `</div>`;
	return html.replace(/(\n[\s\t]*\r*\n)/g, '\n').replace(/^[\n\r\n\t]*|[\n\r\n\t]*$/g, '');
}

function renderSoftVersionSub(groupIndex, rowNum, offset, softVersion) {
	var manualEntry = softVersion.manualEntry || "",
		manualEntrySub = softVersion.manualEntrySub || "", 
		affectedType = softVersion.affectedType || "",
		affectedTypeName = softVersion.affectedTypeName || affectedTypeMap[affectedType],
		ranges= softVersion.ranges || "", 
		groupId= softVersion.groupId || "";
	var index = (rowNum || 0) + (offset || 0);
	var renderOptions = {groupId, rowNum, offset, readOnly : false, isSub : true, isNotGroupEdit:true, index};
	var renderSoftVersionTypeOptions = $.extend(true, {}, renderOptions, {readOnly: false});
	var html = `<div class="softVersionSub" groupId="${groupId}" groupIndex="${groupIndex}">
		<div class="softVersionContainer" index="${index}">
			<span class="rowNum">${offset + 1}</span>.&nbsp;` +
//			`${affectedType ? `<input type="hidden" name="softVersionList[${index}].affectedType" value="${affectedType}">` : ""}` +
//			//`${affectedType ? `${affectedTypeMap[affectedType]}` : "所有系列"}：` +
//			`${affectedType ? `<input type="hidden" name="softVersionList[${index}].affectedTypeName" value="${affectedTypeName}">` : ""}` +
			`${softVersionTypeRender(softVersion, 'affectedType', renderOptions)}` +
			`${softVersionTypeRender(softVersion, 'platformType', renderSoftVersionTypeOptions)}` +
			`${softVersionTypeRender(softVersion, 'releaseType', renderSoftVersionTypeOptions)}` +
			`${softVersionTypeRender(softVersion, 'architectureType', renderSoftVersionTypeOptions)}` +
			`${softVersionTypeRender(softVersion, 'branchType', renderSoftVersionTypeOptions)}` +
			`<input type="hidden" name="softVersionList[${index}].manualEntry" value="${manualEntry}">
			<input type="hidden" name="softVersionList[${index}].manualEntrySub" value="${manualEntrySub}">
			<span class="softVersionRangePart label label-info">${manualEntrySub}</span> -> 
			<input type="hidden" name="softVersionList[${index}].entryStart" value="${ranges[0].version}">
			<input type="hidden" name="softVersionList[${index}].entryEnd" value="${ranges[1].version}">` +
			`<span class="softVersionRange label label-primary">${ranges[0].version}</span> ~ <span class="softVersionRange label label-primary">${ranges[1].version}</span>` +
			`<input type="hidden" name="softVersionList[${index}].markStart" value="${ranges[0].mark}">
			<input type="hidden" name="softVersionList[${index}].markEnd" value="${ranges[1].mark}">` +
			//`<span class="softVersionRange label label-primary">${ranges[0].mark}</span> ~ <span class="softVersionRange label label-primary">${ranges[1].mark}</span>` +
			`<input type="hidden" name="softVersionList[${index}].groupId" value="${groupId}">
		</div>
		<span class="glyphicon glyphicon-minus text-danger pull-right softVersionDel"></span>
	</div>`;
	return html.replace(/(\n[\s\t]*\r*\n)/g, '\n').replace(/^[\n\r\n\t]*|[\n\r\n\t]*$/g, '');
}

function appendSoftVersion($container, html) {
	$($container).prepend(html);
}

function clearSoftVersion($container) {
	$($container).find(".softVersion").remove();
}

function initProbProductBySelect2(select2Id, $container, customOption) {
    $container = $container || $("#mainForm");
    
    var $select = $("#" + select2Id, $container);
    var $selectHidden = $("#" + select2Id + "_hidden", $container);
    if ($selectHidden.length == 0) {
    	$selectHidden = $(`<input type='hidden' id='${select2Id}_hidden' name='${select2Id}_hidden' />`);
    	$select.before($selectHidden);
    }
    var processDataFunc = function (data, params) {
        var list = JSON.parse(data.result || "[]");
        var results = $.map(list, function (obj) {
            obj.source = $.extend(true, {}, obj);
            obj.id = obj.itemCode;
            obj.text = obj.itemModel || obj.itemDesc;
            obj.selected = params.selected || false;
            return obj;
        });
        return {
            results: results,
            /* pagination: {
                more: (params.page * 30) < data.pageParam.filtered
            } */
        };
    }
    var selectedData = [];
    try {
    	selectedData = $selectHidden.val();
    	selectedData = processDataFunc({result: selectedData}, {selected: true}).results;
    } catch (e) {
	}
    

    var options = $.extend({
        allowClear: true,
        closeOnSelect: false,
        language: "zh-CN",
        width: 'auto',
        multiple: true,
        dropdownAutoWidth: true,
        dropdownParent: $select.parent(),
        data: selectedData || [],// 设置初始值
        ajax: {
            url: "probAjax_listProductItem.action?result=json",
            dataType: 'json',
            method: 'POST',
            type: 'POST',
            delay: 250,
            data: function (params) {
                const selectedIds = $select.val() || []; // 获取已选的值
                return {
                    "commonMap.itemSearch": params.term, // search term
                    "commonMap.itemSearchExclude": JSON.stringify({
                        itemCodeNotIn: selectedIds
                    })
                };
            },
            processResults: processDataFunc,
            cache: true
        },
        placeholder: '搜索产品编码/型号/描述，空格分隔可组合。（前缀匹配每段以^开头，后缀匹配每段以$结尾）',
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
            
            $container.find(".select2-result-repository__title").append("<div>" + (repo.itemModel || "") + "</div>");
            $container.find(".select2-result-repository__title").append("<div>" + repo.itemCode + "</div>");
            $container.find(".select2-result-repository__description").text(repo.itemDesc);
            
            return $container;
        },
        templateSelection: function(data, container) {
            return data.text || data.id;
        }
    }, customOption || {});
    
    var defaultOptions = $.fn.select2.defaults.apply(options);
    options.dropdownAdapter = defaultOptions.dropdownAdapter;
    options.selectionAdapter = defaultOptions.selectionAdapter;
    
    initDropdownSelectAll(options);
    
    initCustomSelection(options);
    
    $select.select2(options);
}

function initProbSelectBySelect2(select2Id, $container, customOption) {
    $container = $container || $("#mainForm");
    
    var $select = $("#" + select2Id, $container);
    var $selectHidden = $("#" + select2Id + "_hidden", $container);
    if ($selectHidden.length == 0) {
    	$selectHidden = $(`<input type='hidden' id='${select2Id}_hidden' name='${select2Id}_hidden' />`);
    	$select.before($selectHidden);
    }
    
    var processDataFunc = function (data, params) {
        var list = JSON.parse(data.result || "[]");
        return {
            results: list,
        };
    }
    var selectedData = [];
    try {
    	selectedData = $selectHidden.val();
    	selectedData = processDataFunc({result: selectedData}, {selected: true}).results;
    } catch (e) {
	}
    
    var options = $.extend({
        allowClear: true,
        closeOnSelect: false,
        language: "zh-CN",
        width: 'auto',
        multiple: true,
        dropdownAutoWidth: true,
        dropdownParent: $select.parent(),
        placeholder: "请选择"
    }, customOption || {});
    
    var defaultOptions = $.fn.select2.defaults.apply(options);
    options.dropdownAdapter = defaultOptions.dropdownAdapter;
    options.selectionAdapter = defaultOptions.selectionAdapter;
    
    initDropdownSelectAll(options);
    
    initCustomSelection(options);
    
    $select.val(selectedData);
    $select.select2(options);
}

function initDropdownSelectAll(options) {
	// 获取 Select2 工具类和默认组件
    const Utils = $.fn.select2.amd.require("select2/utils");
    const Dropdown = $.fn.select2.amd.require("select2/dropdown");
    const DropdownSearch = $.fn.select2.amd.require("select2/dropdown/search");
    // 自定义 全选 包装器
    const DropdownSelectAll = function () {};

    // 重写 render 方法，在下拉菜单中添加一个“全选”按钮
    DropdownSelectAll.prototype.render = function (decorated) {
    	var $dropdown = decorated.call(this);

        // 添加一个“全选”按钮
        var $selectAll = $('<button type="button" class="select2-search--dropdown--selectall">全选</button>')
            .on('click', () => {
                $(".select2-results__option--selectable", $dropdown).not(".select2-results__option--selected").trigger("mouseup");
            });

        // 插入到下拉面板顶部
        $dropdown.find('.select2-results').prepend($selectAll);
        $dropdown.find('.select2-results').prepend($dropdown.find('.select2-search'));

        return $dropdown;
    };
    
    // 增加下拉框内搜索框
    options.dropdownAdapter = Utils.Decorate(options.dropdownAdapter, DropdownSearch);
    // 添加全选按钮
    options.dropdownAdapter = Utils.Decorate(options.dropdownAdapter, DropdownSelectAll);
    
    return DropdownSelectAll;
}

function initCustomSelection(options) {
	options = options || {};
	var maxSelectedLength = options.maxSelectedLength || 0;
    if (maxSelectedLength == 0 || maxSelectedLength == undefined) {
        return;
    }
    // 获取 Select2 工具类和默认组件
    const Utils = $.fn.select2.amd.require("select2/utils");
    // 自定义 全选 包装器
    const CustomSelectionAdapter = function () {};
    
    CustomSelectionAdapter.prototype.render = function (decorated) {
    	var $selection = decorated.call(this);

        $selection[0].classList.add('select2-selection--multiple-limit');

        return $selection;
    };
    
    // 覆写 update 方法
    CustomSelectionAdapter.prototype.update = function(decorated, data) {
    	// 调用上级方法
    	decorated.call(this, data);
		
    	// 设置 title 属性为所有选中的选项文本
    	var selectedTexts = [];
    	
	    for (var d = 0; d < data.length; d++) {
	        var selection = data[d];
	        var title = selection.title || selection.text;
	    
	        if (title) {
	            selectedTexts.push(title);
	        }
	    }
	    
	    var $rendered = this.$selection.find('.select2-selection__rendered');
	    if (data.length > maxSelectedLength) {
	    	$rendered.html($('<span class="select2-selection__choice__ellipsis">')
	            	.text('已选择 ' + data.length + ' 个')
	        );
	  	}
	    this.container.$container.attr('title', selectedTexts.join(', '));
    };
    
    // 增加超出多长结果不显示明细
//    options.selectionAdapter = CustomSelectionAdapter;
    options.selectionAdapter = Utils.Decorate(options.selectionAdapter, CustomSelectionAdapter);
    
    return CustomSelectionAdapter;
}

function initProbProductByMultiSelect($container) {
    $container = $container || $("#mainForm");
    
    // 初始化 multiselect 组件
    var selectID = 'probProducts2';
    $("#probProducts2", $container).multiselect({
        header : true,
        /*height : height,
        minWidth : width,*/
        selectedList : 50,//预设值最多显示10被选中项
        hide : [ "explode", 500 ],
        checkAllText : "全选",
        uncheckAllText : '取消',
        noneSelectedText : '==请选择==',
        close : function() {
            var values = $("#" + selectID).val();
            $("#" + inputID).val(values);
        },
        selectableHeader: "<input type='text' class='search-input' placeholder='搜索...'>",
        afterInit: function(ms){
            var that = this,
                $searchBox = $('.search-input');

            $searchBox.on('keyup', function(){
                var searchTerm = $searchBox.val();
                if (searchTerm.length >= 2) { // 当输入至少两个字符时开始搜索
                    $.ajax({
                        url: 'probAjax_listProductItem.action?result=json', // 替换为你的搜索接口地址
                        data: {query: searchTerm},
                        success: function(response){
//                            // 清空当前选项
//                            that.options.find('option').remove();
                            
                            // 添加新选项
                            $.each(response.data, function(index, item){
                                that.options.append($('<option></option>').attr("value", item.id).text(item.name));
                            });
                            
                            // 刷新组件
                            that.refresh();
                        }
                    });
                }
            });
        }
    });
}

function renderProbProducts(json, options) {
	options = options || {};
	var $container = options.$container, 
		readOnly = options.readOnly, 
		ignoreSub = options.ignoreSub, 
		onlyAppend = options.onlyAppend;
	
	var probProductList = [];
    if ($.isArray(json)) {
    	probProductList = json
    } else if (typeof json == "string"){
    	try {
    		probProductList = JSON.parse(json || "[]");
	    } catch(e) {
	    	console.error(e);
	    }
    } else {
    	probProductList = [json];
    }
    var probProductGroupMap = {};
    var probProducts = [];
    for(var i in probProductList) {
    	var probProduct = probProductList[i];
        var groupId = probProduct.itemModel;
        if (groupId) {
            if (!probProductGroupMap[groupId]) {
                probProductGroupMap[groupId] = probProduct;
                probProducts.push(probProduct);
            }
            var group = probProductGroupMap[groupId];
            var children = group.children || [];
            children.push($.extend(true, {}, probProduct));
            group.children = children;
        } else {
            probProducts.push(probProduct);
        }
    }
    probProducts.sort(function(a, b) {
    	return (a.itemModel || "").localeCompare(b.itemModel);
    })
    for(var probProduct of probProducts) {
        $($container).append(`<span class="probProduct label label-primary">${probProduct.itemModel || probProduct.itemDesc || ''}</span>`);
    }
}

function renderCommonLabel(json, options) {
	options = options || {};
	var $container = options.$container, 
		readOnly = options.readOnly, 
		ignoreSub = options.ignoreSub, 
		onlyAppend = options.onlyAppend;
	
	var list = [];
    if ($.isArray(json)) {
    	list = json
    } else if (typeof json == "string"){
    	try {
    		list = JSON.parse(json || "[]");
	    } catch(e) {
	    	console.error(e);
	    }
    } else {
    	list = [json];
    }
    var itemGroupMap = {};
    var items = [];
    var key = options.key || "id";
    var text = options.text || "text";
    for(var i in list) {
    	var item = list[i];
        var groupId = item[key];
        if (groupId) {
            if (!itemGroupMap[groupId]) {
                itemGroupMap[groupId] = item;
                items.push(item);
            }
            var group = itemGroupMap[groupId];
            var children = group.children || [];
            children.push($.extend(true, {}, item));
            group.children = children;
        } else {
            items.push(item);
        }
    }
    items.sort(function(a, b) {
    	return (a[key] || "").localeCompare(b[key]);
    })
    var labelClass = options.labelClass || 'label-primary';
    for(var item of items) {
        $($container).append(`<span class="prob-label label ${labelClass}">${item[text] || ''}</span>`);
    }
}

function initSoftVersionInputs(container, options) {
	var $container = $(container);
	$container = $container.length > 0 ? $container  : $(`#${container}`);
	
	// 添加软件版本类型选择框
	var $softVersionTypes = $container.find(`#${options.typeContainer || 'softVersionTypes'},.softVersionTypes`);
	renderSoftVersionTypesCascade($softVersionTypes, options.initTypeKey);
	
	// 软件版本范围输入框，添加提示信息
	var $softVersionInputs = $container.find(`#${options.inputContainer || 'softVersionInput'},.softVersionInput`);
	var title = $softVersionInputs.attr("title");
	$softVersionInputs.data("title", title).attr("title", "");
	
	var templateAll =  `<div style="text-align:left;">
	    <div>输入内容包含示例，版本类型选填，会自动根据选择补全</div>
	    <div>神州一号~神州六号版本：</div>
	    <ol style="padding-left:0;">
	    	<li>1、版本范围（均包含）：A211CM006D010P14PATCH08~A211CM006D012P25PATCH28</li>
	    	<li>2、版本范围（均包含）：D010P14PATCH08~D012P25PATCH28</li>
	    	<li>3、指定版本：A211CM006D010P14PATCH08</li>
	    	<li>4、指定版本：D010P14PATCH08</li>
    	</ol>
    	<div>神州七号之后版本：</div>
    	<ol style="padding-left:0;">
	    	<li>1、版本范围（均包含）： H9C7.1.35R45~H9C7.2.35R145</li>
	    	<li>2、版本范围（均包含）： C7.1.35R45~C7.2.35R145</li>
	    	<li>3、指定版本： H9C7.1.35R45</li>
	    	<li>4、指定版本： C7.1.35R45</li>
    	</ol>
    	<div>点击添加后请核对版本范围是否符合预期，不符合的范围请去除</div>
    	</div>`;
	var templatePart = `<div style="text-align:left;">
	    <div>版本填写规范示例：</div>
	    <ol style="padding-left:0;">
	    	<li>1、神州一号~神州六号版本：D010P14PATCH08</li>
	    	<li>2、神州七号之后版本：7.1.35R45</li>
    	</ol>
    	<div>点击添加后请核对版本范围是否符合预期，不符合的范围请去除</div>
    	</div>`
	$softVersionInputs.each(function() {
		var $this = $(this);
		var isPart = $this.hasClass("softVersionInputPart");
		$softVersionInputs.tooltip({
			title: isPart ? templatePart : templateAll,
			html:true
		});
	})
	
	// 版本添加按钮绑定添加事件
	var $versionAdd = $container.find(`#${options.versionAddBtn || 'manualSubmit'}`);
	var $softVersionList = $container.find(`#${options.softVersionListContainer || 'softVersionList'},.softVersionList`);
	if ($softVersionList.length == 0) {
		$softVersionList = $container.parent().find(`#${options.softVersionListContainer || 'softVersionList'},.softVersionList`);
	}
	var versionAddFunc = function(e) {
        var manualEntry = $.trim($("#manualEntry", $container).val());
        var manualEntryStart = $.trim($("#manualEntryStart", $container).val());
        var manualEntryEnd = $.trim($("#manualEntryEnd", $container).val());
        if (!manualEntry && !manualEntryStart && !manualEntryEnd) {
            $("#manualSoftVersion", $container).hide();
            return;
        }
        var affectedType = $.trim($("#affectedType", $container).val());
        var softVersion = {manualEntry, affectedType};
        var isValid = true;
        var softVersionTypes = [];
        $("#softVersionTypes select", $container).each(function() {
        	var versionTypeKey = $.trim($(this).data("key"));
        	var versionTypeValue = $.trim($(this).val());
        	var versionTypeLabel = $.trim($(this).data("label"));
        	if (versionTypeKey && !versionTypeValue) {
        		isValid = false;
        		alert("请选择" + versionTypeLabel);
        		return false;
        	}
        	softVersion[versionTypeKey] = versionTypeValue;
        	if (versionTypeKey !== 'platformType') {
        	    softVersionTypes.push(versionTypeValue);
        	}
        })
        if (!manualEntry && (!manualEntryStart || !manualEntryEnd)) {
        	alert("请填写完整的版本起止范围");
        	return false;
        }
        if (!isValid) {
        	return false;
        }
        softVersionTypes = softVersionTypes.join("");
        softVersion['softVersionTypes'] = softVersionTypes;
        if (!manualEntry) {
        	manualEntryStart = $.trim(manualEntryStart).toUpperCase();
        	manualEntryEnd = $.trim(manualEntryEnd).toUpperCase();
        	softVersion['manualEntry'] = `${softVersionTypes}${manualEntryStart}~${softVersionTypes}${manualEntryEnd}`;
        	softVersion['entryStart'] = `${softVersionTypes}${manualEntryStart}`;
        	softVersion['entryEnd'] = `${softVersionTypes}${manualEntryEnd}`;
        }
        parserSoftVersion(softVersion, $softVersionList);
    };
    $versionAdd.off("click", versionAddFunc);
	$versionAdd.click(versionAddFunc);
}

/*
 * 检查要提交的参数
*/
function checkPost(){
	$("input[name='prob.solution']").val($('#solution').summernote('code'));
	$("input[name='prob.desc']").val($('#desc').summernote('code'));
	var selectedData = $("#probProducts").select2("data") || [];
	var probProducts = [];
	for (var data of selectedData) {
		probProducts.push(objDeepOmit(data.source, ['id', 'status', 'createBy', 'createTime', 'updateBy', 'updateTime']));
	}
	$("#probProducts_hidden").val(JSON.stringify(probProducts));
	
	var selectedData = $("#relatedSceneTypes").select2("data") || [];
	var relatedSceneTypes = [];
	var relatedSceneTypesName = [];
	for (var data of selectedData) {
		relatedSceneTypes.push({
			id: data.id,
			text: data.text
		})
		relatedSceneTypesName.push(data.text);
	}
	$("#relatedSceneTypesJson_hidden").val(JSON.stringify(relatedSceneTypes));
	$("#relatedSceneTypesName_hidden").val(relatedSceneTypesName);

    fields = new Array('num','theme', 'probProducts');
    for(i = 0 ;i < fields.length ; i++){
        if(!checkField(fields[i])){
            return false;
        }
    }
    /* var manualEntry = $("#manualEntry").val();
    if (manualEntry) {
        var index = $(".softVersion").length;
        $("#manualEntry").attr("name", "softVersionList[" + index + "].manualEntry");
    } else {
        $("#manualEntry").attr("name", "");
    } */
    return true;
}
 
function checkField(fieldId){
    $field = $("#"+fieldId);
    var fieldValue = $field.val() || '';
    if(fieldValue == ''){
    	var $msg = $("#"+fieldId+"Msg");
    	$msg.text("此字段为必须输入项").addClass("redMark");
        if ($msg.length == 0) {
        	var title = $field.data("lable") || $field.data("title") || $field.attr("lable") || $field.closest(".form-group").find("label.control-label").text();
        	alert(title + `为必须输入项`);
        }
        $field.focus();
        return false;
    }else{
        $("#"+fieldId+"Msg").text("").removeClass("redMark");
        return true;
    }
    return true;
}