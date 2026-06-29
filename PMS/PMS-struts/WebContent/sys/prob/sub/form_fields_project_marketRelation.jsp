<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
    <label for="marketCode" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label form-control-label"><s:text name="pm.project.market" /></label>
    <div class="col-xs-7 col-sm-7 col-md-7 col-lg-7 display-flex select-group">
        <s:select id="marketCode" name="probRestore.marketCode" data-selected="%{probRestore.marketCode}"
            data-parent-code="" data-child-code="systemCode"
            list="commonMap.marketRelationsWithSubMap" listKey="marketName" listValue="marketName" 
            cssClass="form-control marketRelation" headerValue="--请选择--" headerKey="" /> 
        <s:select id="systemCode" name="probRestore.systemCode" data-selected="%{probRestore.systemCode}"
            data-parent-code="marketCode" data-child-code="expendCode"
            list="#{}" listKey="systemName" listValue="systemName" 
            cssClass="form-control marketRelation" headerValue="--请选择--" headerKey="" /> 
        <s:select id="expendCode" name="probRestore.expendCode" data-selected="%{probRestore.expendCode}"
            data-parent-code="systemCode" data-child-code="industryCode"
            list="#{}" listKey="expendName" listValue="expendName" 
            cssClass="form-control marketRelation" headerValue="--请选择--" headerKey="" /> 
        <s:select id="industryCode" name="probRestore.industryCode" data-selected="%{probRestore.industryCode}"
            data-parent-code="expendCode" data-child-code=""
            list="#{}" listKey="industryName" listValue="industryName" 
            cssClass="form-control marketRelation" headerValue="--请选择--" headerKey="" /> 
        <script type="text/javascript">
            var marketRelationsWithSubMap = [];
            try {
                marketRelationsWithSubMap = "${commonMap.marketRelationsWithSubMap}".replace(/=/g, "':'").replace(/\{/g, "{'").replace(/, /g, "', '").replace(/\}/g, "'}").replace(/\}', '\{/g, "}, {").replace(/\]', /g, "], ").replace(/':'\[/g, "':[").replace(/\]'\}/g, "]}").replace(/\}\]'/g, "}]").replace(/'/g,'"');
                marketRelationsWithSubMap = JSON.parse(marketRelationsWithSubMap);
            } catch (e) {
                marketRelationsWithSubMap = [];
            }
            var selectedRelationsMap = {};
            function changeMarketRelations(){
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
        </script>
    </div>