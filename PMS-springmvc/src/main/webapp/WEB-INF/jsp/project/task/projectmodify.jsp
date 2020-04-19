<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<form enctype="multipart/form-data" action="module/ProjectPlanEdit.action" method="post" id="planform">
	<input type="hidden" name="projectId"></s:hidden>
	<input type="hidden" name="project.contractNo" id="contractNoStr"></s:hidden>
	<table id="projectTaskTable" width="100%" class="table">
		<thead>
			<tr>
				<th>&nbsp;</th>
				<th><s:text name="sys.projectplan.contractNo"></s:text></th>
				<th><s:text name="sys.projectplan.referenceEventName"></s:text>
				</th>
				<th><s:text name="sys.projectplan.eventPlanHappenDate"></s:text>
				</th>
				<th><s:text name="sys.projectplan.eventPlanHappenDateENG"></s:text>
				</th>
				<th><s:text name="sys.projectplan.eventActualFinishDate"></s:text>
				</th>
				<s:if test="%{project.projectState != '100'}">
					<!-- 控制闭环状态不能再做操作 -->
					<th><s:text name="sys.projectplan.attachment"></s:text></th>
					<th><a href="javascript:void(0)"
						class="btn btn-default btn-sm endelete" onclick="addplan()"><span
							class="glyphicon glyphicon-plus"></span></a></th>
				</s:if>
			</tr>
		</thead>
		<tbody>
			<s:if test="projectTaskList == null || projectTaskList.size < 1">
				<s:iterator value="projectPlanEventList" id="ppe" status="p">
					<tr>
						<td><input type="hidden" name="projectTask.eventKeyStr"
							value="<s:property value='#ppe.eventKey'/>"></td>
						<td>&nbsp;</td>
						<td><s:property value='#ppe.eventValue' /></td>
						<td><span
							id="eventPlanHappenDateSpan<s:property value='#p.index'/>">
								<s:date name="#ppe.eventPlanHappenDate" format="yyyy-MM-dd" />
						</span> <input type="hidden"
							id="eventPlanHappenDateStr<s:property value='#p.index'/>"
							name="projectTask.eventPlanHappenDateStr"
							value="<s:property value='#ppe.eventPlanHappenDate'/>"></td>
						<td><input type="text"
							value="<s:property value='#ppe.eventPlanHappenDateENG'/>"
							id="eventPlanHappenDateENG<s:property value='#p.index'/>"
							name="projectTask.eventPlanHappenDateENGStr"></td>
						<td></td>
						<s:if test="%{project.projectState != '100'}">
							<!-- 控制闭环状态不能再做操作 -->
							<td><a href="javascript:void(0)" class="showBarcode"
								onclick="javascript:alert('请先填写施工计划');">上传交付件</a></td>
							<td><s:if test="#ppe.eventPlanHappenDate == null">
									<a href="javascript:void(0)" class="endelete"
										onclick="deleteplan(this)"> <span
										class="glyphicon glyphicon-remove"></span>
									</a>
								</s:if></td>
						</s:if>
					</tr>
				</s:iterator>
			</s:if>
			<s:else>
				<s:iterator value="projectTaskList" id="ppe" status="p">
					<s:if test="#ppe.visibleFlag == 1">
						<tr>
					</s:if>
					<s:else>
						<tr style="display: none;">
					</s:else>
					<td><input type="hidden" name="projectTask.eventKeyStr"
						value="<s:property value='#ppe.eventKeyStr'/>"> <span
						id="visibleFlag<s:property value='#p.index'/>"> <input
							type="hidden" name="projectTask.visibleFlag"
							value="<s:property value='#ppe.visibleFlag'/>">
					</span></td>
					<td><input type="hidden" name="projectTask.contractNo"
						value="<s:property value='#ppe.contractNo'/>"> <s:property
							value='#ppe.contractNo' /></td>
					<td><s:property value='#ppe.eventValue' /></td>
					<td><span
						id="eventPlanHappenDateSpan<s:property value='#p.index'/>">
							<s:date name="#ppe.eventPlanHappenDate" format="yyyy-MM-dd" />
					</span> <input type="hidden"
						id="eventPlanHappenDateStr<s:property value='#p.index'/>"
						name="projectTask.eventPlanHappenDateStr"
						value="<s:property value='#ppe.eventPlanHappenDate'/>"></td>
					<td><span
						id="eventPlanHappenDateENGSpan<s:property value='#p.index'/>"
						style="display: none;"> <s:date
								name="#ppe.eventPlanHappenDateENG" format="yyyy-MM-dd" />
					</span> <input type="text"
						id="eventPlanHappenDateENG<s:property value='#p.index'/>"
						name="projectTask.eventPlanHappenDateENGStr"></td>
					<td><span
						id="eventActualFinishDateSpan<s:property value='#p.index'/>"
						style="display: none;"> <s:date
								name="#ppe.eventActualFinishDate" format="yyyy-MM-dd" />
					</span> <input type="hidden"
						id="eventActualFinishDate<s:property value='#p.index'/>"
						name="projectTask.eventActualFinishDateStr"> <s:date
							name="#ppe.eventActualFinishDate" format="yyyy-MM-dd" /></td>
					<%-- <s:if test="%{project.projectState != '100'}"> --%>
					<!-- 控制闭环状态不能再做操作 -->
					<td><a href="javascript:void(0)" class="showBarcode"
						onclick="popWindow('module/sub/ToUploadDeliverableFile.action?projectDeliver.contractNo=<s:property value='#ppe.contractNo'/>&projectDeliver.projectId=<s:property value="project.projectId"/>&projectDeliver.column010=<s:property value="project.column010"/>&projectDeliver.column011=<s:property value="project.column011"/>&projectDeliver.eventKey=<s:property value='#ppe.eventKeyStr'/>'
												, 700, 450,'上传交付件', 'BudgetUpload', true);">上传交付件</a>
					</td>
					<td><s:if test="#ppe.eventPlanHappenDate == null">
							<a href="javascript:void(0)" class="endelete"
								onclick="deleteplan(this)"> <span
								class="glyphicon glyphicon-remove"></span>
							</a>
						</s:if></td>
					<%-- </s:if> --%>
					</tr>
				</s:iterator>
			</s:else>
		</tbody>
	</table>
	<s:if test="%{project.projectState != '100'}">
		<!-- 控制闭环状态不能再做操作 -->
		<button type="button" onclick="planformSubmit()"
			class="btn btn-default btn-block writeTxt submitBtn"
			style="width: 60px;">
			<s:text name="pm.project.btn"></s:text>
		</button>
	</s:if>
</s:form>
<div style="display: none;">
	<table id="addTable">
		<tr>
			<td></td>
			<td></td>
			<td><s:select list="projectPlanEventList" listKey="eventKey"
					listValue="eventValue" name="projectTask.eventKeyStr"
					onchange="changeEvent(this)" headerKey="0" headerValue="请选择"></s:select>
			</td>
			<td></td>
			<td><input type="text" id="eventPlanHappenDateENG"
				name="projectTask.eventPlanHappenDateENGStr"></td>
			<td><input type="hidden" id="eventActualFinishDate"
				name="projectTask.eventActualFinishDateStr"></td>
			<td><a href="javascript:void(0)" class="showBarcode">上传交付件</a></td>
			<td><a href="javascript:void(0)" class="endelete"
				onclick="deleteplan(this)"> <span
					class="glyphicon glyphicon-remove"></span>
			</a></td>
		</tr>
	</table>
</div>
<script>
	$(function(){
		
	})
</script>