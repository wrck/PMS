<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<html>
<dp:base />
<head>
<script type="text/javascript">
</script>
</head>
<body>
    <style>
        .readLine .uploadWrapper {
            display: none;
        }
        .deliverList {
            /* width: 30vw; */
            max-width: 20vw;
        }
        #subcontractMultiDimInfoTable {
            margin-top: 20px;
        }
        #subcontractMultiDimInfoTable tr {
            height: 3rem;
        }
        #subcontractPaymentPrintTable tr td:first-child {
            max-width: 200px !important;
            width: 200px !important;
            text-align: right;
        }
    </style>
    <div style="text-align: left;">
        <div>
            <h3>项目信息</h3>
            <table id="subcontractPaymentPrintTable" class="table table-bordered table-hover table-striped ">
                <tr style="display:none"><td><s:hidden name="subcontract.id"/></td></tr>
                <tr>
                    <td class="nowrap">所属公司:</td>
                    <td class="nowrap"><s:property value="compList.get(subcontract.orgId).name"/></td>
                </tr>
                <tr>
                    <td class="nowrap">采购订单号:</td>
                    <td><s:property value="subcontract.customInfo.purchId"/></td>
                </tr>
                <tr>
                    <td class="nowrap"><s:text name="pm.subcontract.subcontractNo"></s:text>:</td>
                    <td><s:property value="subcontract.subcontractNo"/></td>
                </tr>
                <tr>
                
                    <td><s:text name="pm.subcontract.project"></s:text>:</td>
                    <td><s:hidden id="projectIds" name="subcontract.projectIds" cssClass="smEdit canEmpty"></s:hidden>
                        <div id="paymentProjectListDiv">
                           <display:table style="text-align: left;margin-bottom:0;"
                                name="projectList" pagesize="${projectList.size()}" export="false" id="paymentProjectDisplayTable"
                                size="${projectList.size()}" sort="external"
                                decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-condensed table-hover table-striped" >
                                <display:column class="hidden " headerClass="hidden" media="html">
                                    <input class="transferOfficeCode" value="${projectDisplayTable.column001}" data-type="${projectDisplayTable.salesType}" />
                                </display:column>
                                <display:column property="projectNameWithCodeWarrper" class="transferProjectName" titleKey="pm.project.projectName" media="html"></display:column> 
                                <display:column property="contractNo" titleKey="pm.contract" class="transferContractNo" decorator="com.dp.plat.decorators.ContractNoList"></display:column>
                            </display:table>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>销售合同号:</td>
                    <td><s:property value="subcontract.contractNos" /></td>
                </tr>
                <tr>
                    <td>我司回款情况:</td>
                    <td><s:property value="subcontract.customInfo.collectedAmount" /></td>
                </tr>
                <tr>
                    <td>付款比例（含本次）:</td>
                    <td><span id="paymentSumRatio"></span>%</td>
                </tr>
                <tr>
                    <td>此次付款说明:</td>
                    <td><span id="paymentRemark"></span></td>
                </tr>
            </table>
        </div>
        <div>
            <h3>发票清单</h3>
            <display:table style="text-align: left;"
                name="subcontractPaymentList" export="false" id="subcontractPaymentTable"
                size="${subcontractPaymentList.size()}" sort="external" requestURI="module/sub/querySubcontractPayment.action"
                decorator="com.dp.plat.subcontract.decorators.SubcontractDecorator" class="displayTable table table-condensed table-hover table-striped">
                <s:set value="%{column.extData}" var="extData"></s:set>
                
                <display:column title="序号" headerClass="text-center nowrap" class="text-center rowNum nowrap">
                    ${subcontractPaymentTable_rowNum}
                </display:column>
                <display:column property="id" headerClass="hidden" class="text-input id hidden" title="id" media="html"></display:column>
                <display:column property="subcontractId" headerClass="hidden" class="text-input subcontractId hidden ignore" title="subcontractId" media="html"></display:column>
                <display:column property="ratio" class="text-input ratio nowrap" titleKey="pm.subcontract.ratio"></display:column>
                <display:column property="amount" class="text-input amount nowrap" titleKey="pm.subcontract.amount"></display:column>
                <%-- <s:if test="%{workflowCommonParam.outcome == 'acceptanceTask'}"> --%>
                <display:column property="paymentApprovedAmountWrapper" class="approvedAmount nowrap"  title="审批金额" media="html">
                    <input name="subcontractPaymentList[0].customStrInfo.approvedAmount" value="${subcontractPaymentTable.customStrInfo.approvedAmount != null ? subcontractPaymentTable.customStrInfo.approvedAmount : subcontract.subcontractAmount}" class="form-control" >
                </display:column>
                <%-- </s:if> --%>
                <%-- <c:if test="${subcontractPaymentTable.paymentTime != null}">
                <display:column class="paiedAmount nowrap ${subcontractPaymentTable.paymentTime != null ? '' : 'hidden'}" headerClass="${subcontractPaymentTable.paymentTime != null ? '' : 'hidden'}" title="已付金额" media="html">
                    ${subcontractPaymentTable.customStrInfo.paiedAmount != null ? subcontractPaymentTable.customStrInfo.paiedAmount : subcontractPaymentTable.amount}
                </display:column>
                </c:if> --%>
                <%-- <display:column class="paiedAmount nowrap " headerClass="nowrap" title="已付金额" media="html">
                    ${subcontractPaymentTable.customStrInfo.paidAmount != null && subcontractPaymentTable.customStrInfo.paidAmount != 0 ? subcontractPaymentTable.customStrInfo.paidAmount : subcontractPaymentTable.amount}
                </display:column> --%>
                <display:column property="paidAmountWrapper" class="paidAmount nowrap" headerClass="nowrap" title="已付金额" media="html"></display:column>
                <display:column property="confirmTime" headerClass="nowrap" class="confirmTime nowrap" titleKey="pm.subcontract.confirmTime" format="{0,date,yyyy-MM-dd HH:mm:ss}"></display:column>
                <display:column property="paymentTime" headerClass="nowrap" class="paymentTime nowrap" titleKey="pm.subcontract.paymentTime" format="{0,date,yyyy-MM-dd HH:mm:ss}"></display:column>
                <display:column property="remark" class="text-input remark" titleKey="pm.subcontract.remark"></display:column>
                <display:column class="text-input customStrInfo.invoiceNumber" title="发票号码">${subcontractPaymentTable.customStrInfo.invoiceNumber}</display:column>
                <display:column class="status nowrap" title="状态">${subcontractPaymentTable.customStrInfo.status}${subcontractPaymentTable.customStrInfo.paystate != null ? '，'.concat(subcontractPaymentTable.customStrInfo.paystate) : ''}</display:column>
                <display:column property="deliverableName" class="deliverList" title="附件"></display:column>
                <s:if test="%{workflowCommonParam.outcome == 'applyPaymentTask'}">
                <display:column title="操作" media="html">
                    <span class="glyphicon glyphicon-plus text-success"></span>
                    <span class="glyphicon glyphicon-minus text-danger"></span>
                </display:column>
                </s:if>
                <display:column property="customInfo.readonly" headerClass="hidden" class="readonly hidden" title=""></display:column>
                <display:footer>
                    <%-- <s:if test="%{subcontractPaymentList.size() == 0}"> --%>
                        <tr class="empty-template hidden inputLine">
                            <td class="text-center rowNum">1</td>
                            <td class="text-input subcontractId hidden ignore"><input name="subcontractPaymentList[0].subcontractId" value='<s:property value="subcontract.id"/>' class="form-control ignore"></td>
                            <td class="text-input ratio"><input name="subcontractPaymentList[0].ratio" class="form-control" readonly="readonly"></td>
                            <td class="text-input amount"><input name="subcontractPaymentList[0].amount" class="form-control" ></td>
                            <td class="approvedAmount"><!-- <input name="subcontractPaymentList[0].approvedAmount" class="form-control"> --></td>
                            <td class="paidAmount"><!-- <input name="subcontractPaymentList[0].paidAmount" class="form-control"> --></td>
                            <td class="confirmTime"><!-- <input name="subcontractPaymentList[0].confirmTime" class="form-control"> --></td>
                            <td class="paymentTime"><!-- <input name="subcontractPaymentList[0].paymentTime" class="form-control"> --></td>
                            <td class="text-input remark"><input name="subcontractPaymentList[0].remark" class="form-control"></td>
                            <td class="text-input customStrInfo.invoiceNumber"><input name="subcontractPaymentList[0].customStrInfo.invoiceNumber" class="form-control" placeholder="发票号码"></td>
                            <td class="status"><input name="subcontractPaymentList[0].customStrInfo.status" class="form-control hidden ignore" value="待审批"></td>
                            <td class="deliverList">
                                <s:if test="commonMap.inspectionFileTypes.size() > 0">
                                    <s:iterator value="commonMap.inspectionFileTypes" var="entry">
                                        <span class="uploadWrapper">
                                            <s:text name="#entry.value"/>
                                            <span class="inspection">
                                                <s:file label="File" name="paymentDeliverList[0].uploads" cssClass="form-control multipleFileType" multiple="true" />
                                                <s:hidden class="ignore" name="paymentDeliverList[0].type" value="" data-type="%{#entry.key}"/>
                                            </span>
                                        </span>
                                    </s:iterator>
                                </s:if>
                                <s:else>
                                    <span class="uploadWrapper">
                                        <s:text name="验收材料"/>
                                        <span class="inspection">
                                            <s:file label="File" name="paymentDeliverList[0].uploads" cssClass="form-control multipleFileType" multiple="true" />
                                            <s:hidden class="ignore" name="paymentDeliverList[0].type" value="" data-type="发票原件"/>
                                        </span>
                                    </span>
                                    <span class="uploadWrapper">
                                        <s:text name="发票原件"/>
                                        <span class="inspection">
                                            <s:file label="File" name="paymentDeliverList[0].uploads" cssClass="form-control multipleFileType" multiple="true" />
                                            <s:hidden class="ignore" name="paymentDeliverList[0].type" value="" data-type="发票原件"/>
                                        </span>
                                    </span>
                                </s:else>
                            </td>
                            <td>
                                <span class="glyphicon glyphicon-plus text-success" ></span>
                                <span class="glyphicon glyphicon-minus text-danger" style="display:none;"></span>
                            </td>
                        </tr>
                    <%-- </s:if> --%>
                    <tr id="sumTr" class='text-danger' style='font-weight: 800;'>
                        <td colspan='1' class='text-right'>合计</td>
                        <td colspan='1' id="sumRatio"></td>
                        <td colspan='1' ><div id="sumAmount"></div><div id="paiedAmount"></div></td>
                        <s:if test="%{workflowCommonParam.outcome == 'acceptanceTask'}">
                        <td colspan='1' ><div id="sumApprovedAmount"></div><div id="approvingAmount"></div></td>
                        </s:if>
                        <td  id="delIds"></td>
                        <td class="mergeTdBorder">
                            <s:if test="%{(user.isHasRole(10) || user.isHasRole(13) || workflowCommonParam.outcome == 'applyPaymentTask') && subcontract.state != 100}">
                               <button id="paymentSubmitBtn" type="submit" class="btn btn-success"><span class="glyphicon glyphicon-floppy-save" style="font-size:12px;"></span> 保存</button>
                            </s:if>
                        </td>
                        <td class="mergeTdBorder"></td>
                        <td class="mergeTdBorder"></td>
                        <td class="mergeTdBorder"></td>
                    </tr>
                </display:footer>
            </display:table>
        </div>
        <script type="text/javascript">
            $(function(){
                function printPayment(payment) {
                    payment = payment || {};
                    $("#paymentProjectListDiv").html($("#projectDisplayTable").clone());
                    $("#paymentSumRatio").text($("#sumRatio").text());
                    $("#paymentRemark").text(payment.remark);
                }
                setTimeout(() => {
                    printPayment();
                }, 1000);
            })
        </script>
    </div>
</body>
</html>