<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<dp:base />
<head>
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
					<div class="panel panel-default">
						<!-- 请求路径改为绝对路径 ，避免因路径问题产生其他资源加载问题
					求绝对路径行号
					${pageContext.request.contextPath}
					<%=request.getContextPath()%>
				 -->
				<form method="post" action="${pageContext.request.contextPath }/module/sub/checkhistsoftversion.action" id="QueryForm"
					class="form-horizontal" name="QueryForm">
						<s:hidden name="softChangeLog.projectId" value="%{softChangeLog.projectId}"></s:hidden>
						<div class="panel-body">
							<div class="form-group">
								<label for="pcb" class="col-xs-2 col-sm-2 col-md-2 col-lg-2"><s:text name="prob.info.change.log"></s:text></label>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<s:select list="changeLogList" name="softChangeLog.id" listKey="id" listValue="versionAndCreateTime"
										cssClass="form-control"></s:select>
								</div>
								<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
								   	<button type="submit" id="submit"  class="btn btn-default"><s:text name='sys.query' /></button>
							    </div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 col-sm-2 col-md-2 col-lg-2"><s:text name="prob.info.change.remark"></s:text></label>
								<div class="col-xs-10 col-sm-10 col-md-10 col-lg-10">
									<s:property value="softChangeLog.changeRemark" escapeHtml="false"/>
								</div>
							</div>
						</div>
						
					
						<div class="panel-body">
							<table class="table table-striped">
								<tr>
									<th><s:text name="pm.orderdata.barcode"></s:text></th>
									<th width="180px"><s:text name="prob.info.conp"></s:text></th>
									<th><s:text name="prob.info.boot"></s:text></th>
									<th><s:text name="prob.info.cpld"></s:text></th>
									<th><s:text name="prob.info.pcb"></s:text></th>
									<th width="120px"><s:text name="prob.info.update.time"></s:text></th>
									<th width="150px"><s:text name="prob.info.createTime"></s:text></th>
								</tr>
								<s:iterator value="softversionList" var="soft" status="index" >
									<tr>
										<td>
                                            <s:property value="#soft.barCode"/>
                                            <s:if test="#soft.barCode2 != null">
                                                <br><span class='text-danger'>(<s:property value="#soft.barCode2"/>)</span>
                                            </s:if>
                                        </td>
										<s:if test="#soft.conpChange == 0">
											<td><s:property value="#soft.conp"/></td>
										</s:if>
										<s:else>
											<td class="redMark">
												<s:property value="#soft.conpBak"/>->
												<s:property value="#soft.conp"/>
											</td>
										</s:else>
										<s:if test="#soft.bootChange == 0">
											<td><s:property value="#soft.boot"/></td>
										</s:if>
										<s:else>
											<td class="redMark">
												<s:property value="#soft.bootBak"/>->
												<s:property value="#soft.boot"/>
											</td>
										</s:else>
										<s:if test="#soft.cpldChange == 0">
											<td><s:property value="#soft.cpld"/></td>
										</s:if>
										<s:else>
											<td class="redMark">
												<s:property value="#soft.cpldBak"/>->
												<s:property value="#soft.cpld"/>
											</td>
										</s:else>
										<s:if test="#soft.pcbChange == 0">
											<td><s:property value="#soft.pcb"/></td>
										</s:if>
										<s:else>
											<td class="redMark">
												<s:property value="#soft.pcbBak"/>->
												<s:property value="#soft.pcb"/>
											</td>
										</s:else>
										<td><s:date name="#soft.executeTime" format="yyyy-MM-dd"/></td>
										<td><s:date name="#soft.createTime" format="yyyy-MM-dd HH:mm"/></td>
									</tr>
								</s:iterator>
							</table>
						</div>
					
						</form>
					</div>
			</div>
		</div>
	</div>
</body>
</html>