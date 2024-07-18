<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<fieldset>
        <legend><b>基本信息</b></legend>
        <table class="table table-bordered table-hover table-striped ">
            <tr>
                <td><s:text name="pm.presales.projectcode"></s:text>:</td>
                <td><s:property value="presales.presalesCode"/><s:hidden name="presales.presalesCode"/></td>
                <td><s:text name="pm.presales.projectname"></s:text>:</td>
                <td>
                    <s:property value="presales.projectName"/>
                    <s:if test="presales.hasTransfer == 1">
                        <span class="text-danger text-unselected">(<s:text name="pm.presales.hasTransfer"/>)</span>
                    </s:if>
                </td>
            </tr>
            <tr>
                <td><s:text name="pm.presales.marketname"></s:text>:</td>
                <td><s:property value="presales.marketName"/></td>
                <td><s:text name="pm.presales.systemname"></s:text>:</td>
                <td><s:property value="presales.systemName"/></td>
            </tr>
            <tr>
                <td><s:text name="pm.presales.expendName"></s:text>:</td>
                <td><s:property value="presales.expendName"/></td>
                <td><s:text name="pm.presales.industryName"></s:text>:</td>
                <td><s:property value="presales.industryName"/></td>
            </tr>
            <tr>
                <td><s:text name="pm.presales.officeName"></s:text>:</td>
                <td><s:property value="presales.officeName"/></td>
                <td><s:text name="pm.presales.salesman"></s:text>:</td>
                <td><s:property value="presales.salesman"/></td>
            </tr>
            <tr>
                <td><s:text name="pm.presales.productmanager"></s:text>:</td>
                <td><s:property value="presales.productManager"/></td>
                <td><s:text name="pm.presales.salesmanlink"></s:text>:</td>
                <td><s:property value="presales.salesmanLink"/></td>
            </tr>
            <tr>
                <td><s:text name="pm.presales.sm"></s:text>:</td>
                <td>
                    <s:textfield name="presales.serviceManagerName" id="serviceManager" 
                        cssClass="form-control" cssStyle="width:200px;"
                        placeholder="支持模糊搜索" onfocus="fillsm()" readonly="%{queryPath != 'input'}"
                        onblur="fillsm()"></s:textfield>
                </td>
                <td><s:text name="pm.presales.pm"></s:text>:</td>
                <td>
                    <s:textfield name="presales.projectManagerName" id="projectManager" 
                        cssClass="form-control" cssStyle="width:200px;"
                        placeholder="支持模糊搜索" onfocus="fillpm()" readonly="%{queryPath != 'input' && queryPath != 'smaduit'}"
                        onblur="fillpm()"></s:textfield>
                </td>
            </tr>
            <tr>
                <td><s:text name="pm.presales.projectType"></s:text>:</td>
                <td>
                <s:if test="queryPath == 'input' || queryPath == 'emaduit'">
                    <%-- <s:property value="presales.projectTypeName"/> --%>
                    <s:select list="projectTypeList" name="presales.projectType" id="projectType" listKey="basicDataId" listValue="basicDataName"
                      headerValue="--请选择--" headerKey="" cssClass="form-control" cssStyle="width:200px;"></s:select>
            	</s:if>
                <s:else>
                    <s:property value="presales.projectTypeName"/>
                    <s:if test="queryPath == 'pmaduit'">
                        <s:hidden id="projectType" name="presales.projectType"/>
                    </s:if>
                </s:else>
                </td>
            <s:if test="queryPath == 'input'">
                <td></td>
                <td></td>
            </s:if>
            <s:else>
                <td>项目起止时间:</td>
                <td>
                    <s:date name="presales.applyTime" format="yyyy-MM-dd HH:mm:ss"/>
                    ~
                    <s:if test="presales.endTime != null">
                        <s:date name="presales.endTime" format="yyyy-MM-dd HH:mm:ss"/>
                    </s:if>
                    <s:else>至今</s:else>（<s:property value="presales.totalDuration"/>）
                </td>
                <%-- <td><s:text name="pm.presales.applyTime"/>:</td>
                <td><s:date name="presales.applyTime" format="yyyy-MM-dd HH:mm:ss"/></td>
                <td><s:text name="pm.presales.endTime"/>:</td>
                <td><s:date name="presales.endTime" format="yyyy-MM-dd HH:mm:ss"/></td> --%>
            </s:else>
            </tr>
        </table>
    <s:if test="queryPath != 'input'">
        <table class="table table-no-border" style="margin-top: -20px;margin-bottom: 0;">
            <tr>
                <td <s:if test='presales.projectState == 30'>class="text-success current-state"</s:if>><s:text name="pm.presales.serviceDuration"></s:text>:<s:property value="presales.serviceDuration"/><span style="cursor: help;" title="<s:text name='pm.presales.applyDuration'/>">(<s:property value="presales.applyDuration"/>)</span></td>
                <td <s:if test='presales.projectState == 31'>class="text-success current-state"</s:if>><s:text name="pm.presales.programDuration"></s:text>:<s:property value="presales.programDuration"/></td>
                <td <s:if test='presales.projectState == 32'>class="text-success current-state"</s:if>><s:text name="pm.presales.testDuration"></s:text>:<s:property value="presales.testDuration"/></td>
            <s:if test="queryPath == 'emaduit'">
                <td <s:if test='presales.projectState == 33'>class="text-success current-state"</s:if>><s:text name="pm.presales.callbackDuration"></s:text>:<s:property value="presales.callbackDuration"/></td>
            </s:if>
                <td <s:if test='presales.projectState == 34'>class="text-success current-state"</s:if>><s:text name="pm.presales.serviceApproveDuration"></s:text>:<s:property value="presales.serviceApproveDuration"/></td>
            </tr>
        </table>
    </s:if>
    </fieldset>