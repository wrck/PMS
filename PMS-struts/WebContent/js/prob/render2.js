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
		data:{
			"softVersion.manualEntry": softVersion.manualEntry,
			"softVersion.affectedType": softVersion.affectedType,
		},
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
				var html = renderSoftVersion({
					manualEntry: entryKey, 
					entryMap, 
					affectedType,
					groupId: timestamp
				}, {$container});
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
				$($container).append(html);
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
        $($container).append(html);
    }
}

var affectedTypeMap = {
	undefined: "所有系列",
	"": "所有系列",
	0: "所有系列",
	1: "盒式系列",
	2: "框式系列"
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

	var html = `<div class="${cssClass}" groupId="${groupId}" groupIndex="${groupIndex}">
		${isNotGroup ? "" : `<div class="groupManualEntry">`}
		<div class="softVersionContainer" index="${index}">
			<span class="rowNum">${(isSub ? offset : groupIndex) + 1}</span>.&nbsp;`+
			//`${isNotGroupEdit ? `<input type="hidden" name="softVersionList[${index}].affectedType" value="${softVersion.affectedType}">` : ""}` +
			`${readOnly ? (!isSub ? `${affectedTypeMap[softVersion.affectedType]}：` : "") : (!isSub ? renderAffectedTypeSelected(isNotGroupEdit ? `softVersionList[${index}].affectedType` : "", softVersion.affectedType) : `<input type="hidden" name="softVersionList[${index}].affectedType" value="${softVersion.affectedType}">`)}` +
			//`${!isSub ? `${affectedTypeMap[softVersion.affectedType]}：` : ""}` +
			`${isNotGroupEdit ? `<input type="hidden" name="softVersionList[${index}].affectedTypeName" value="${softVersion.affectedTypeName}">` : ""}` +
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
			${isSub && softVersion.manualEntrySub ? `${softVersion.manualEntrySub} -> ` : ""}
			${isSubEdit && softVersion.entryStart ? `<input type="hidden" name="softVersionList[${index}].entryStart" value="${softVersion.entryStart}">` : ""}
			${isSubEdit && softVersion.entryEnd ? `<input type="hidden" name="softVersionList[${index}].entryEnd" value="${softVersion.entryEnd}">` : ""}` +
			`${isSub ? `${softVersion.entryStart}~${softVersion.entryEnd}` : ""}` +
			`${isSubEdit && softVersion.markStart ? `<input type="hidden" name="softVersionList[${index}].markStart" value="${softVersion.markStart}">` : ""}
			${isSubEdit && softVersion.markEnd ? `<input type="hidden" name="softVersionList[${index}].markEnd" value="${softVersion.markEnd}">` : ""}` +
			//`${isSub ? `${softVersion.markStart}~${softVersion.markEnd}` : ""}` +
			`${isSubEdit && softVersion.groupId ? `<input type="hidden" name="softVersionList[${index}].groupId" value="${softVersion.groupId}">` : ""}
		</div>
		${!readOnly ? `<span class="glyphicon glyphicon-minus text-danger pull-right flex-algin-self-center softVersionDel"></span>` : ""}
		${isNotGroup ? "" : `</div>`}`;
	if (softVersion.children) {
		var i = 0;
		var children = softVersion.children;
		for ( var childIndex in children) {
			html += renderSoftVersion(children[childIndex], {
				$container,
				//affectedType: softVersion.affectedType || options.affectedType,
				readOnly, ignoreSub, groupId, groupIndex, 
				rowNum: index, 
				offset: i++
			});
		}
	} else if (softVersion.entryMap) {
		var i = 0;
		var entryMap = softVersion.entryMap;
		for(var subKey in entryMap) {
			var ranges = entryMap[subKey] || [];
			html += renderSoftVersionSub(groupIndex, index, i++, {
				affectedType: softVersion.affectedType,
				manualEntry: softVersion.manualEntry, 
				manualEntrySub :subKey, 
				ranges, groupId
			});
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
	var html = `<div class="softVersionSub" groupId="${groupId}" groupIndex="${groupIndex}">
		<div class="softVersionContainer" index="${index}">
			<span class="rowNum">${offset + 1}</span>.&nbsp;
			${affectedType ? `<input type="hidden" name="softVersionList[${index}].affectedType" value="${affectedType}">` : ""}` +
			//`${affectedType ? `${affectedTypeMap[affectedType]}` : "所有系列"}：` +
			`${affectedType ? `<input type="hidden" name="softVersionList[${index}].affectedTypeName" value="${affectedTypeName}">` : ""}` +
			`<input type="hidden" name="softVersionList[${index}].manualEntry" value="${manualEntry}">
			<input type="hidden" name="softVersionList[${index}].manualEntrySub" value="${manualEntrySub}">
			${manualEntrySub} -> 
			<input type="hidden" name="softVersionList[${index}].entryStart" value="${ranges[0].version}">
			<input type="hidden" name="softVersionList[${index}].entryEnd" value="${ranges[1].version}">` +
			`${ranges[0].version}~${ranges[1].version}` +
			`<input type="hidden" name="softVersionList[${index}].markStart" value="${ranges[0].mark}">
			<input type="hidden" name="softVersionList[${index}].markEnd" value="${ranges[1].mark}">` +
			//`${ranges[0].mark}~${ranges[1].mark}` +
			`<input type="hidden" name="softVersionList[${index}].groupId" value="${groupId}">
		</div>
		<span class="glyphicon glyphicon-minus text-danger pull-right softVersionDel"></span>
	</div>`;
	return html.replace(/(\n[\s\t]*\r*\n)/g, '\n').replace(/^[\n\r\n\t]*|[\n\r\n\t]*$/g, '');
}
function clearSoftVersion($container) {
	$($container).find(".softVersion").remove();
}

function initProbProductBySelect2(select2Id, $container) {
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
        var $selectAll = $('<button type="button" class="btn-select-all">全选</button>')
            .css({
                width: '100%',
                padding: '5px',
                background: '#f0f0f0',
                border: 'none',
                textAlign: 'center',
                cursor: 'pointer'
            })
            .on('click', () => {
                $(".select2-results__option--selectable", $dropdown).not(".select2-results__option--selected").trigger("mouseup");
            });

        // 插入到下拉面板顶部
        $dropdown.find('.select2-results').prepend($selectAll);
        $dropdown.find('.select2-results').prepend($dropdown.find('.select2-search'));

        return $dropdown;
    };

    var options = {
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
        placeholder: '搜索产品编码/型号/描述，空格分隔可组合',
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
        templateSelection: function(repo) {
            return repo.text || repo.id;
        }
    };
    
    var defaultOptions = $.fn.select2.defaults.apply(options);
    options.dropdownAdapter = defaultOptions.dropdownAdapter;
    // 增加下拉框内搜索框
    options.dropdownAdapter = Utils.Decorate(options.dropdownAdapter, DropdownSearch);
    // 添加全选按钮
    options.dropdownAdapter = Utils.Decorate(options.dropdownAdapter, DropdownSelectAll);
    $select.select2(options);
}