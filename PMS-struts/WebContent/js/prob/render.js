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