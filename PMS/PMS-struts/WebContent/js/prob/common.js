try {
	var scripts = document.getElementsByTagName("script");
	eval(scripts[ scripts.length - 1 ].innerHTML);
} catch(e) {
	console.log(e);
}
function changeMarketRelations() {
	var $this = $(this);
	var $selected = $("option:selected", $this);
	//var value = $selected.val() || $selected.data("selected");
	var index = $selected.index();
	var parentCode = $this.data("parentCode");
	var childCode = $this.data("childCode");
	var currentCode = $this.attr("id");
	// 获取上级的所选值
	var relations = selectedRelationsMap[parentCode] || {children: marketRelationsWithSubMap};
	// 获取当前属性的所选值
	relations = (relations.children || [])[index - 1] || {};
	// 缓存当前属性的所选值，便于下级属性联动时获取所有可选值
	selectedRelationsMap[currentCode] = currentCode ? relations : null;
	// 清除下级属性的所选值缓存
	selectedRelationsMap[childCode] = null;
	// 获取下级属性的所有可选值
	var children = relations.children || [];
	// 移除子属性的所有动态值
	$("#" + childCode).find(".dynamic-option").remove();
	$("#" + childCode).nextAll(".marketRelation").find(".dynamic-option").remove();
	// 添加子属性新的动态选项
	var $child = $("#" + childCode);
	var childName = childCode.replace("Code", "Name");
	$(children).each(function(){
	    $child.append("<option class='dynamic-option' value='"+this[childName]+"'>"+this[childName]+"</option>");
	});
	// 赋子属性的初始值
	var childSelected = $child.data("selected");
	if (childSelected) {
	    // 获取初始值后进行清空，避免上级属性发生变更后，下级仍然赋值的问题
	    $child.data("selected", null);
	    // 赋值后触发change事件，进行下级属性的联动
	    $child.val(childSelected).trigger("change");
    }
}
$(document).ready(function(){
    $(".marketRelation").on("change", changeMarketRelations);
    $("#marketCode").trigger("change");
});