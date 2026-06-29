<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<style type="text/css">
legend {
	font: 12px/24px "微软雅黑"
}
</style>
</head>
<body>
    <fieldset>
        <legend><b>临时授权信息</b></legend>
        <table class="table table-bordered table-hover table-striped ">
            <tr>
                <td class="col-sm-2">申请人:</td>
                <td class="col-sm-4"><s:property value="presales.customInfo.applyUserCode"/>-<s:property value="presales.customInfo.applyUserName"/></td>
                <td class="col-sm-2">申请部门:</td>
                <td class="col-sm-4"><!-- <s:property value="presales.customInfo.applyDeptCode"/> --><s:property value="presales.customInfo.applyDeptName"/></td>
            </tr>
            <tr>
                <td class="col-sm-2">申请日期:</td>
                <td class="col-sm-4"><s:property value="presales.customInfo.applyDate"/></td>
                <td class="col-sm-2">申请类型:</td>
                <td class="col-sm-4"><s:property value="presales.customInfo.applyTypeName"/></td>
            </tr>
            <tr>
                <td class="col-sm-2">项目名称:</td>
                <td class="col-sm-4"><s:property value="presales.customInfo.projectName"/></td>
                <td class="col-sm-2">产品线:</td>
                <td class="col-sm-4"><s:property value="presales.customInfo.productLineName"/></td>
            </tr>
            <tr>
                <td class="col-sm-2">申请原因:</td>
                <td class="col-sm-4"><s:property value="presales.customInfo.applyCause"/></td>
                <td class="col-sm-2">后续计划:</td>
                <td class="col-sm-4"><s:property value="presales.customInfo.followUpPlan"/></td>
            </tr>
            <tr>
                <td class="col-sm-2">测试起止时间:</td>
                <td class="col-sm-4">
                    <s:property value="presales.customInfo.testStartTime"/>
                    ~
                    <s:property value="presales.customInfo.testEndTime"/>
                </td>
                <td class="col-sm-2">正式授权承诺销售计划日期:</td>
                <td class="col-sm-4"><s:property value="presales.customInfo.authPlanDate"/></td>
            </tr>
        </table>
        <legend><b>临时授权信息</b></legend>
        <!-- pagesize="${commonList.size() }" -->
        <display:table id="tempAuthInfoTable" name="commonList" 
            export="false" size="${commonList.size() }" sort="external"
            requestURI="module/presales_tempAuthInfo.action"
            class="table table-condensed table-hover table-striped" partialList="false">
            <display:column headerClass="warning" property="contractNum" titleKey="pm.shipment.contractNo"></display:column>
            <display:column headerClass="warning" property="deviceSerialnum" titleKey="pm.shipment.barCode"></display:column>
            <display:column headerClass="warning" property="modelNum" titleKey="pm.ps.pro.itemmodel"></display:column>
            <display:column headerClass="warning" property="isSoftware" title="是否为软件"></display:column>
            <display:column headerClass="warning" property="applyCount" title="已申请次数"></display:column>
        </display:table>
    </fieldset>
</body>
</html>