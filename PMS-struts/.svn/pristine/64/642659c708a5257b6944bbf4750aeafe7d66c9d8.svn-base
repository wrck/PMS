<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<dp:base />
<head>
<!-- 新 Bootstrap 核心 CSS 文件 -->
<link rel="stylesheet" href="../../bootstrap-3.3.4-dist/css/bootstrap.min.css" />
<!-- 引入对Bootstrap的自扩展文件 -->
<link rel="stylesheet" href="../../css/bootstrap-ex.css" />
<!-- 可选的Bootstrap主题文件（一般不用引入mian） -->
<link rel="stylesheet" href="../../bootstrap-3.3.4-dist/css/bootstrap-theme.min.css" />
<script type="text/javascript" src="../../js/jquery-2.1.4.min.js"></script>
<style type="text/css">
.pccSubmitDiv {
	margin-top: 10px;
	height: 120px;
}

.headerSpan {
	font-size: 14px;
	font-weight: 700;
}

a span {
	font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
	font-size: 14px;
	line-height: 1.428571429;
}

.pmclnotice {
	line-height: 24px;
	background: #F7F7F7;
	border: 1px dashed #CCC;
	padding: 15px;
	margin: 15px 0;
	color: #666;
	clear: both;
}

.headerLi {
	float: left;
	font-size: 12px;
	height: 30px;
	line-height: 30px;
	margin-right: 20px;
}

.pmclquescontent {
	line-height: 1.5;
	margin: 0 0 14px 10px;
	font-size: 14px;
	color: #333;
}

.content_pm_proplem {
	width: 820px;
	padding: 10px;
	width: 820px;
	padding-bottom: 15px;
	border-bottom: #e3ebeb 1px solid;
	margin-top: 10px;
}

.content_pm_proplem_type {
	color: #999;
	font-size: 12px;
	margin-left: 10px;
}

.content_pm_sort {
	font-weight: bold;
	color: #1473CB;
}

.mainDiv {
	height: auto !important;
	height: 500px;
	min-height: 500px;
	border: 2px solid #88ABDA;
	color: #999;
	padding: 26px 33px;
	text-align: left;
	border-radius: 10px 10px 10px 10px;
}
</style>
<script type="text/javascript">
	$(function() {
		$("#pmCLChoseQuesButt").change(function() {
			$("#cbCLDivChoseForm").submit();

		});
		//问卷保存草稿
		$("#quesnaire_draft").click(function(){
			$("#cbCLForm").submit();
		});
		//问卷提交
		$("#quesnaire_submit").click(function(){
			$("#quesnaireState").val(1);
			$("#cbCLForm").submit();
		});
		
		//submitFlow
		var quesnaireState = "<s:property value='subcontractCallback.quesnaireState' default='0'/>";
		$("#submitCallback").click(function(){
			var approveStatus = $("#submitCallbackWorkFlow input[name='workflowCommonParam.approveStatus']:checked").val();
			if (!approveStatus) {
				alert("请选择回访意见！");
                return false;
			}
			if (approveStatus != "4" && quesnaireState != 1) {
				alert("请先提交回访问卷！");
				return false;
			}
			$("#submitCallbackWorkFlow").submit();
		});
		
		$("#submitCallbackWorkFlow input[name='workflowCommonParam.approveStatus']").change(function() {
			var approveStatus = this.value;
			if (approveStatus == "4") {
				$("#quesnairePanel").hide();
			} else {
				$("#quesnairePanel").show();
			}
			adjustFrame();
		})
		
		$("span.quesTypeScore").each(function(){
			if($(this).text().indexOf("|")>-1){
				var typeValue=$(this).text().split("|")[0];			 
				
				if($(this).attr("scoreType")=="3"){
					if($(this).text().split("|")[1]=="30"){  
						if($(this).next("span").text()){
							score30Value=parseInt($(this).next().text());						
						}
					}
					
					if($(this).text().split("|")[1]=="10"){  
						if($(this).next("span").text()){
							score10Value=parseInt($(this).next().text());						
						}
					}
				}
				$(this).text(typeValue);
				
			}
			
		});
		
		function adjustFrame() {
			var cbh = $("body").outerHeight(true) + 20;
	        $("#subcontractCallbackFrame", parent.document).height(cbh);
		}
		adjustFrame();
		
		if ("<s:property value='redirect' default=''/>") {
			window.parent.location.reload();
		}
	});
</script>
</head>
<body>
    <s:if test="workflowCommonParam.taskId != ''">
	<div>
	    <%-- <s:if test="subcontractCallback.quesnaireState == 1"> --%>
	    <div class="panel panel-default">
		    <div class="panel-body">
            <s:form id="submitCallbackWorkFlow" name="submitCallbackWorkFlow" action="querySubcontractCallback.action" method="post" class="">
             <s:hidden name="subcontractCallback.id"></s:hidden>
             <s:hidden name="subcontractCallback.subcontractId"></s:hidden>
             <s:hidden name="workflowCommonParam.instId"></s:hidden>
             <s:hidden name="workflowCommonParam.taskId"></s:hidden>
             <s:hidden name="redirect" value="true"/>
             <div class="form-group form-group-query" style="margin-top:10px;">
                 <div id="cbResultRadio">
                     <label for="seeScoreEvaluationComment" style="width: 90px;float:left;"><span class="redmark">*</span>回访意见</label>
                     <label class="checkbox-inline" style="margin-left:0px;">
                         <input type="radio" name="workflowCommonParam.approveStatus" value="3"> 回访通过
                     </label>
                     <label class="checkbox-inline" style="margin-right:4px;">
                            <input type="radio" name="workflowCommonParam.approveStatus" value="4"> 无法回访
                        </label>
                     <label class="checkbox-inline" style="margin-right:4px;">
                         <input type="radio" name="workflowCommonParam.approveStatus" value="-3"> 回访不通过
                     </label>
                 </div>
             </div>
             <div class="form-group form-group-query" style="margin-top:10px;">
                 <label for="seeScoreEvaluationComment" style="width: 90px;float:left;"><span class="redmark" style="color:black;">*</span>回访备注</label>
                 <textarea id="seeScoreEvaluationComment" name="workflowCommonParam.comment" class="form-control" rows="3" style="float:left; width: 350px;"></textarea>
             </div>
             <br>
             <div class="btn-group btn-group-sm" style="margin-left:80px;margin-top:20px;">
                    <button id="submitCallback" type="button" class="btn btn-info" style="margin-right:4px;">
                        <span class="glyphicon glyphicon-ok" style="font-size:12px;"></span> 提交审核
                 </button>
                </div>
            </s:form>
		    </div>
        </div>
	    <%-- </s:if>
	    <s:else> --%>
	    <div id="quesnairePanel" style="display:none;">
	 		<div class="panel panel-default">
				<div class="panel-body">
					<s:form method="POST" action="querySubcontractCallback.action"
						id="cbCLDivChoseForm" cssClass="form-inline" style="float:left;">
						<s:hidden name="subcontract.id"></s:hidden>
						<s:hidden name="subcontractCallback.subcontractId"></s:hidden>
						<s:hidden name="redirect"></s:hidden>
						<div class="form-group form-group-query">
							<label for="pmCLQuesName" style="width: 90px;"><span class="redmark">*</span>
							<s:text name="pm.cl.questionnaireName" /></label><%--  <select
								name="pmClosedLoopQuesnaire.id" id="pmCLChoseQuesButt"
								class="form-control  btn-info"
								style="width: 160px; display: inline-block;">
								<option value="0">--请选择--</option>
								<s:iterator value="pmClosedLoopQuesnaireList" id="objd"
									status="indexd">
									<option value="<s:property value='%{#objd.id}'/>"><s:property
											value="%{#objd.questionnaireTemplateName}" /></option>
								</s:iterator>
							</select> --%>
							<s:select id="pmCLChoseQuesButt" cssClass="form-control  btn-info" cssStyle="width: 160px; display: inline-block;" list="pmClosedLoopQuesnaireList" listKey="id" listValue="questionnaireTemplateName" headerKey="0" headerValue="--请选择--" name="pmClosedLoopQuesnaire.id"></s:select>
						</div>
					</s:form>
				</div>
			</div>
		<%-- </s:else> --%>
		<s:if test="pmClosedLoopQuesnaireLineList.size()>0">
			<div id="cbCLDiv" style="margin-top: 21px;">
				<div class="panel-group" id="cbaccordion">
					<div class="panel panel-default">
						<div class="panel-heading">
						      回访问卷
						</div>
						<div id="cbcollapseOne" class="panel-collapse collapse in">
							<div class="panel-body">
								<!-- 回访问卷描述 -->
								<div class="pmclnotice">
									<div>
										<span class="headerSpan"><s:property
												value="pmClosedLoopQuesnaire.questionnaireTemplateName" /></span>
									</div>
									<div class="info clearfix" style="height: 30px">
										<ul>
											<li class="headerLi"><s:text name="pm.cl.createdPerson"></s:text>：<span
												class="color-blue"><s:property
														value="pmClosedLoopQuesnaire.createdPerson" /></span></li>
											<li class="headerLi"><s:text name="pm.cl.createdTime"></s:text>：<span
												class="color-blue"><s:date
														name="pmClosedLoopQuesnaire.createdTime"
														format="yyyy-MM-dd" /></span></li>
											<li class="headerLi"><span><s:text
														name="pm.cl.questionnaireScore"></s:text>:<s:property
														value="pmClosedLoopQuesnaire.questionnaireScore" /></span></li>
											<li class="headerLi"><span><s:text
														name="pm.cl.questionnairePassScore"></s:text>:<s:property
														value="pmClosedLoopQuesnaire.questionnairePassScore" /></span></li>
											<li class="headerLi"><span><s:text
														name="pm.cl.quesTyle"></s:text>:<s:property
														value="pmClosedLoopQuesnaire.quesTypeName}" /></span></li>
										</ul>
									</div>
								</div>

								<s:form method="post" action="querySubcontractCallback.action" id="cbCLForm">
									<s:hidden name="pmClQuesnaireResultHeader.quesnaireTemplateHeaderId" value="%{pmClosedLoopQuesnaire.id}"></s:hidden>
									<s:hidden name="pmClQuesnaireResultHeader.quesTotalScore" value="%{pmClosedLoopQuesnaire.questionnaireScore}"></s:hidden>
									<s:hidden name="pmClQuesnaireResultHeader.quesPassScore" value="%{pmClosedLoopQuesnaire.questionnairePassScore}"></s:hidden>
									<s:hidden name="subcontract.id"></s:hidden>
									<s:hidden name="subcontractCallback.id"></s:hidden>
									<s:hidden name="subcontractCallback.subcontractId"></s:hidden>
									<s:hidden name="subcontractCallback.taskId"></s:hidden>
									<s:hidden name="pmClQuesnaireResultHeader.status" value="-1" id="quesnaireState"></s:hidden>
									<s:hidden name="redirect"></s:hidden>
									<!-- 问卷已提交 -->
									<s:if test="subcontractCallback.quesnaireState == 1">
										<div> 
											<div class="pmclnotice">
												<span><s:text name="pm.cl.markRule"></s:text>：</span><br/>
												<s:iterator value="pmClosedLoopQuesnaire.markList" id="objmark" status="indexmark">
													<span class="glyphicon glyphicon-star" style="color:#2aabd2;font-size:8px;"></span><s:property value="%{#objmark.markExplain}"/><br/>
												</s:iterator><br/>
												<ul>
												<li class="headerLi">本次测评结果：</li>								
													<li class="headerLi">
														<span>（<s:text name="pm.cl.testTime"></s:text>：<s:date name="pmClQuesnaireResultHeader.createdTime" format="yyyy-MM-dd HH:mm:ss"/></span>
													</li>
													<li class="headerLi">
														<span><s:text name="pm.cl.testPerson"></s:text>：<s:property value="pmClQuesnaireResultHeader.createdPerson"/>）</span>
													</li>								
												</ul>
												<s:iterator value="quesResultMarkList" id="objRMark" status="indexRmark">
													<s:if test="#indexRmark.odd">
														<span class="glyphicon glyphicon-star" style="color:red;font-size:8px;"></span><span class="quesTypeScore"><s:property value="%{#objRMark}"/></span>得分：
													</s:if>
													<s:else>
														<s:property value="%{#objRMark}"/><br/>
													</s:else>						
												</s:iterator>					
												<span class="glyphicon glyphicon-star" style="color:red;font-size:8px;"></span><s:text name="pm.cl.testTotalScore"></s:text>：<s:property value="pmClQuesnaireResultHeader.quesMarkScore"/><br/>
												<span class="glyphicon glyphicon-star" style="color:red;font-size:8px;"></span><s:text name="pm.cl.testTotalResult"></s:text>：					
												<s:if test="pmClQuesnaireResultHeader.quesMarkResult==-1">
													 测评不通过
												</s:if><s:else>
													测评通过
												</s:else>					
											</div>
										</div>
									</s:if> 
									<s:iterator value="pmClosedLoopQuesnaireLineList" id="obja"
										status="indexa">
										<input type="hidden"
											name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].quesnaireTemplateLineId"
											value="<s:property value="%{#obja.id}"/>" />
										<input type="hidden"
											name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].quesTemplateLineNum"
											value="<s:property value="%{#obja.questionNum}"/>" />
										<input type="hidden"
											name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].quesTypeForCB"
											value="<s:property value="%{#obja.questionTypeForCB}"/>" />

										<div class="pmclquescontent">
											<div class="content_pm_proplem">
												<div class="yl_header">
													<table border="0" cellpadding="0" cellspacing="0"
														width="100%">
														<tbody>
															<tr>
																<td valign="top" style="width: 10px;"><s:if
																		test="#obja.questionType==3">
																		<div>
																			<span class="redmark" style="color: black;">*<s:property value="#obja.questionNum" />.</span>
																		</div>
																	</s:if>
																	<s:else>
																		<div>
																			<span class="redmark">*<s:property value="#obja.questionNum" />.</span>
																			
																		</div>
																	</s:else>
																</td>
																<td>
																	<div class="yl_title" id="q_t_6416">
																		<p style="margin-right: 0px;">
																			<span><s:property
																					value="#obja.questionContent" /> </span>
																			<s:if test="#obja.questionType==1">
																				<span class="content_pm_proplem_type">[<s:text
																						name="pm.cl.quesOne"></s:text>]
																				</span>
																			</s:if>
																			<s:if test="#obja.questionType==3">
																				<span class="content_pm_proplem_type">[<s:text
																						name="pm.cl.quesAnw"></s:text>]
																				</span>
																			</s:if>
																			<s:iterator value="quesTypeList" id="objqt"
																				status="indexqt">
																				<s:if
																					test="#obja.questionTypeForCB==#objqt.basicDataId">
																					<span class="content_pm_proplem_type">[<s:property
																							value="%{#objqt.basicDataName}" />]
																					</span>
																				</s:if>
																			</s:iterator>
																			<span class="content_pm_proplem_type">[<span><s:property
																						value="#obja.questionScore" /></span>分]
																			</span>
																		</p>
																	</div>
																	<div class="yl_tip"></div>
																</td>
															</tr>
														</tbody>
													</table>
												</div>

												<div class="yl_one_item">
													<table class="yl_one_item_tbl">
														<tbody>
															<tr>
																<s:iterator value="pmClosedLoopQuesnaireOptList"
																	id="objOpt" status="indexOpt">
																	<s:if test="#objOpt.questionId==(#obja.id)">
																		<s:if test="#obja.questionType==1">
																			<s:if test="pmClQuesnaireResultLineList==null">
																				<td><input type="radio"
																					value="<s:property value="%{#objOpt.id}"/>"
																					id="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"
																					name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionTemplateOptId"
																					style="margin-right: 10px;"></td>
																				<td><label style="margin-right: 28px;"
																					for="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"><s:property
																							value="%{#objOpt.questionOptionsContent}" /><span
																						style="font-weight: 100; font-size: 8px;"
																						class="content_pm_proplem_type">（<s:property
																								value="%{#objOpt.questionOptionScore}" />分）
																					</span></label></td>
																			</s:if>
																			<s:else>
																				<s:iterator value="pmClQuesnaireResultLineList"
																					id="objRLine" status="indexRLine">
																					<s:if
																						test="#objRLine.quesnaireTemplateLineId==#obja.id">
																						<s:if
																							test="#objRLine.questionTemplateOptId==#objOpt.id">
																							<s:if test="#objRLine.quesEvaResult==-1">
																								<td><input type="radio" checked="checked"
																									value="<s:property value="%{#objOpt.id}"/>"
																									id="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"
																									name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionTemplateOptId"
																									style="margin-right: 10px;"></td>
																								<td style="color: red;"><label
																									style="margin-right: 28px;"
																									for="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"><s:property
																											value="%{#objOpt.questionOptionsContent}" /><span
																										style="font-weight: 100; font-size: 8px;"
																										class="content_pm_proplem_type">（<s:property
																												value="%{#objOpt.questionOptionScore}" />分）
																									</span></label></td>
																							</s:if>
																							<s:else>
																								<td><input type="radio" checked="checked"
																									value="<s:property value="%{#objOpt.id}"/>"
																									id="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"
																									name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionTemplateOptId"
																									style="margin-right: 10px;"></td>
																								<td><label style="margin-right: 28px;"
																									for="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"><s:property
																											value="%{#objOpt.questionOptionsContent}" /><span
																										style="font-weight: 100; font-size: 8px;"
																										class="content_pm_proplem_type">（<s:property
																												value="%{#objOpt.questionOptionScore}" />分）
																									</span></label></td>
																							</s:else>
																						</s:if>
																						<s:else>
																							<td><input type="radio"
																								value="<s:property value="%{#objOpt.id}"/>"
																								id="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"
																								name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionTemplateOptId"
																								style="margin-right: 10px;"></td>
																							<td><label style="margin-right: 28px;"
																								for="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"><s:property
																										value="%{#objOpt.questionOptionsContent}" /><span
																									style="font-weight: 100; font-size: 8px;"
																									class="content_pm_proplem_type">（<s:property
																											value="%{#objOpt.questionOptionScore}" />分）
																								</span></label></td>
																						</s:else>
																					</s:if>
																				</s:iterator>
																			</s:else>
																		</s:if>
																	</s:if>
																</s:iterator>
																<s:if test="#obja.questionType==3">
																	<s:if test="pmClQuesnaireResultLineList==null">
																		<td><textarea
																				name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionAnswer"
																				id="" name="" textSize="small" class="form-control"
																				rows="3" style="float: left; width: 350px;"></textarea><span
																			class="content_pm_proplem_type">[双击可放大或缩小]</span></td>
																	</s:if>
																	<s:else>
																		<s:iterator value="pmClQuesnaireResultLineList"
																			id="objRLine" status="indexRLine">
																			<s:if
																				test="#objRLine.quesnaireTemplateLineId==#obja.id">
																				<td><textarea
																						name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionAnswer"
																						id="" name="" textSize="small"
																						class="form-control" rows="3"
																						style="float: left; width: 350px;"><s:property
																							value="%{#objRLine.questionAnswer}" /></textarea><span
																					class="content_pm_proplem_type">[双击可放大或缩小]</span></td>
																			</s:if>
																		</s:iterator>
																	</s:else>
																</s:if>
															</tr>
														</tbody>
													</table>
												</div>
											</div>
										</div>
									</s:iterator>
									<s:if test="subcontractCallback.quesnaireState != 1">
										<!-- 新问卷或者草稿 -->
										<div>
											<div class="btn-group btn-group-sm" style="margin-left:20px;">
											  <button id="quesnaire_draft"  type="button" class="btn btn-info" style="margin-right:4px;">
											  	<span class="glyphicon glyphicon-plus" style="font-size:12px;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="pm.cl.quesDraft"></s:text></span>
											  </button>
											</div>
											<div class="btn-group btn-group-sm" style="margin-left:20px;">
											  <button id="quesnaire_submit"  checkButton='check' type="button" class="btn btn-info" style="margin-right:4px;">
											  	<span class="glyphicon glyphicon-ok" style="font-size:12px;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="pm.cl.quesSubmit"></s:text></span>
											  </button>
											</div>
										</div>
									</s:if>
								</s:form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</s:if>
		</div>
	</div>
	</s:if>
	<s:else>
        <div class="col-sm-12"><span>没有回访任务</span></div>
	</s:else>
</body>
</html>