<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<style>
table.probAffectedProjectSoftTable thead th, .probAffectedProjectSoftTable .nowrap{
    white-space: nowrap;
}
</style>
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
    <s:form method="post" action="module/prob_%{view}.action" id="probAffectedProjectSoftForm"
    cssClass="probAffectedProjectSoftForm form-horizontal clearfix" name="projectForm" enctype="multipart/form-data">
        <%-- <div class="col-xs-2">
            <label for="conp" class="control-label">软件版本</label>
            <div class="">
                <s:textfield id="conp" placeholder="软件版本" name="probRestore.conp" cssClass="form-control" />
            </div>
        </div>
        <div class="col-xs-2">
            <label for="itemCode" class="control-label">产品编码</label>
            <div class="">
                <s:textfield id="itemCode" placeholder="产品编码" name="probRestore.itemCode" cssClass="form-control" />
            </div>
        </div> --%>
        <div class="col-xs-2">
            <label for="projectCode" class="control-label"><s:text name="pm.project.projectCode"></s:text></label>
            <div class="">
                <s:textfield id="projectCode" placeholder="项目编码" name="probRestore.projectCode" cssClass="form-control"></s:textfield>
            </div>
        </div>
        <div class="col-xs-2">
            <label for="projectName" class="control-label"><s:text name="pm.project.projectName"></s:text></label>
            <div class="">
                <s:textfield id="projectName" placeholder="项目名称" name="probRestore.projectName" cssClass="form-control"></s:textfield>
            </div>
        </div>
        <div class="col-xs-2">
            <label for="contractNo" class="control-label"><s:text name="pm.project.contractNo"></s:text></label>
            <div class="">
                <s:textfield id="contractNo" placeholder="合同号" name="probRestore.contractNo" cssClass="form-control"></s:textfield>
            </div>
        </div>
        <div class="col-xs-2">
            <label for="officeCode" class="control-label"><s:text name="pm.officearea"></s:text></label>
            <div class="">
                <s:select name="probRestore.officeCode" id="officeCode"
                    listKey="departmentNum" cssClass="form-control" headerKey=""
                    headerValue="--请选择--" 
                    listValue="departmentName" list="%{departmentList}" theme="simple" />
            </div>
        </div>
        <div class="col-xs-4">
            <label for="marketCode" class="control-label"><s:text name="pm.project.market" /></label>
            <div class="display-flex select-group">
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
            </div>
        </div>
        <div class="col-xs-2">
            <s:hidden name="probStatistic.tabIndex" value="4"/>
            <s:hidden name="probStatistic.filterItem" value="true"/>
            <label for="projectCode" class="control-label"><s:text name="prob.info.num"></s:text></label>
            <div class="">
                <s:textfield id="projectCode" placeholder="公告编号" name="probRestore.prob.probNum" cssClass="form-control" />
            </div>
        </div>
        <div class="col-xs-2">
            <label for="serialNum" class="control-label"><s:text name="prob.info.serial.num"></s:text></label>
            <div class="">
                <s:textfield id="serialNum" placeholder="产品编码" name="probRestore.serialNum" cssClass="form-control"></s:textfield>
            </div>
        </div>
        <div class="col-xs-2">
            <label for="conp" class="control-label">软件版本</label>
            <div class="">
                <s:textfield id="conp" placeholder="软件版本" name="probRestore.version.conp" cssClass="form-control" />
            </div>
        </div>
        <div class="col-xs-2">
            <label for="itemModel" class="control-label"><s:text name="prob.info.product.type"></s:text></label>
            <div class="">
                <s:textfield id="itemModel" placeholder="产品型号支持前缀匹配" name="probRestore.itemModel" cssClass="form-control"></s:textfield>
            </div>
        </div>
        <div class="col-xs-1">
            <label for="submitBtn" class="control-label"></label>
            <div class="">
            <s:if test="view == 'statistics'">
                <button type="button" id="submit"  class="btn btn-default  btn-block btn-sm" onclick="submitStatistic(4)"><s:text name='sys.query' /></button>
            </s:if>
            <s:else>
                <button type="submit" id="submit"  class="btn btn-default  btn-block btn-sm"><s:text name='sys.query' /></button>
            </s:else>
            </div>
        </div>
        <%-- <div class="form-group col-xs-2">
            <div class="col-xs-7">
                <button type="button" id="submit"  class="btn btn-default  btn-block btn-sm" onclick="submitStatistic(4)"><s:text name='sys.query' /></button>
            </div>
        </div> --%>
    </s:form>
    <s:if test="view == 'statistics'">
    <br>
    </s:if>
    <s:else>
    <div class="divHeader div-height">
        <img src="images/right_zhishi.gif" border="0">
        <span>受影响软件版本记录</span>
    </div>
    </s:else>
    <display:table name="probRestoreList" pagesize="${displayParam.pagesize}" export="true"
        size="${displayParam.totalcount}" sort="external" id="probRestoreList"
        requestURI="${namespace}/prob_${view}.action"
        decorator="com.dp.plat.decorators.Wrapper" class="probAffectedProjectSoftTable displayTable table table-striped"
        partialList="true">
        <display:column title="技术公告" media="html">
            <span>${probRestoreList.prob.probNum}</span><br>
            <a href="module/prob_edit.action?prob.probId=${probRestoreList.prob.probId}" target="_blank">${probRestoreList.prob.theme}</a>
        </display:column>
        <%-- <display:column property="prob.probNum" titleKey="pm.prob.probNum" url="/module/prob_edit.action" paramId="prob.probId" paramProperty="probId"  media="html"></display:column> --%>
        <display:column property="prob.probNum" titleKey="prob.info.num" media="excel"></display:column>
        <display:column property="prob.theme" titleKey="prob.info.theme" media="excel"></display:column>
        <display:column property="serialNum" titleKey="prob.info.serial.num"></display:column>
        <display:column property="itemModel" titleKey="prob.info.product.type"></display:column>
        <display:column property="conp" titleKey="prob.info.conp"></display:column>
        <display:column property="cpld" titleKey="prob.info.cpld"></display:column>
        <display:column property="boot" titleKey="prob.info.boot"></display:column>
        <display:column property="pcb" titleKey="prob.info.pcb"></display:column>
        <display:column property="projectNameWithURL" titleKey="pm.project.projectName" media="html"></display:column>
        <display:column property="projectName" titleKey="pm.project.projectName" media="excel"></display:column>
        <display:column property="projectCode" titleKey="pm.project.projectCode" media="excel"></display:column>
        <display:column property="contractNo" titleKey="pm.contract"></display:column>
        <display:column property="officeName" titleKey="pm.officearea"></display:column>
        <display:column property="marketName" titleKey="pm.presales.marketName"></display:column>
        <display:column property="systemName" titleKey="pm.presales.systemName"></display:column>
        <display:column property="expendName" titleKey="pm.presales.expendName"></display:column>
        <display:column property="industryName" titleKey="pm.presales.industryName"></display:column>
        
        <display:setProperty name="export.excel.filename" value="受影响软件版本记录.xlsx" />
    </display:table>