<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.context.UserContext"%>
<%@page import="com.dp.plat.context.SpringContext"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<html>
<head>
<dp:base />

<style type="text/css">
.ui-widget-header {
	border: 1px solid #4297d7;
	background: #2191c0 url(images/ui-bg_gloss-wave_75_2191c0_500x100.png)
		50% 50% repeat-x;
	color: white;
	font-weight: bold;
}

.pccSubmitDiv {
	margin-top: 10px;
	/* background-color: bisque; */
	/* background-color: aliceblue; */
	/* text-align: center; */
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
		$("span.quesTypeScore").each(function() {
			if ($(this).text().indexOf("|") > -1) {
				var typeValue = $(this).text().split("|")[0];
				$(this).text(typeValue);
			}
		});

		$("textarea[textSize]").each(function() {
			$(this).dblclick(function() {
				textSize(this);
			});
		});
	});

	function textSize(this_obj) {
		var h = 65;
		if ($(this_obj).html()) {
			var arr = $(this_obj).html().split("\n");
			h = ($(this_obj).html().split("\n").length) * 30;
			for (var i = 0; i < arr.length; i++)
				if (arr[i].length >= 30)
					h = h + ((arr[i].length) / 30) * 30;
		}
		if (h < 65)
			h = 65;
		var w = h == 65 ? 350 : 500;
		if ($(this_obj).attr("textSize") == "small") {
			$(this_obj).css("width", w + "px");
			$(this_obj).css("height", h + "px");
			$(this_obj).attr("textSize", "big");
		} else {
			$(this_obj).css("width", "350px");
			$(this_obj).css("height", "auto");
			$(this_obj).attr("textSize", "small");
		}

	}
</script>
</head>
<body style="width: 98%; height: 98%">
	<div id="cbCLDiv" style="margin-top: 21px;">
		<div class="panel-group" id="cbaccordion">
			<div class="panel panel-default">
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
												name="pmClosedLoopQuesnaire.createdTime" format="yyyy-MM-dd" /></span></li>
									<li class="headerLi"><span><s:text
												name="pm.cl.questionnaireScore"></s:text>:<s:property
												value="pmClosedLoopQuesnaire.questionnaireScore" /></span></li>
									<li class="headerLi"><span><s:text
												name="pm.cl.questionnairePassScore"></s:text>:<s:property
												value="pmClosedLoopQuesnaire.questionnairePassScore" /></span></li>
									<li class="headerLi"><span><s:text
												name="pm.cl.quesTyle"></s:text>:<s:property
												value="pmClosedLoopQuesnaire.quesTypeName" /></span></li>
								</ul>
							</div>
						</div>

						<!-- 问卷已提交 -->
						<div>
							<div class="pmclnotice">
								<span><s:text name="pm.cl.markRule"></s:text>：</span><br />
								<s:iterator value="pmClosedLoopQuesnaire.markList" id="objmark"
									status="indexmark">
									<span class="glyphicon glyphicon-star"
										style="color: #2aabd2; font-size: 8px;"></span>
									<s:property value="%{#objmark.markExplain}" />
									<br />
								</s:iterator>
								<br />
								<ul>
									<li class="headerLi">本次测评结果：</li>
									<%-- <li class="headerLi"><span>（<s:text
												name="pm.cl.testTime"></s:text>：<s:date
												name="cbQuesnaire.createTime" format="yyyy-MM-dd HH:mm:ss" /></span> --%>
									<li class="headerLi"><span>（<s:text
                                                name="pm.cl.testTime"></s:text>：<s:date
                                                name="pmClQuesnaireResultHeader.createdTime" format="yyyy-MM-dd HH:mm:ss" /></span>
									</li>
									<li class="headerLi"><span><s:text
												name="pm.cl.testPerson"></s:text>：<s:property
												value="pmClQuesnaireResultHeader.createdPerson" />）</span></li>
								</ul>
								<s:iterator value="cbQuesnaire.quesResultMarkList" id="objRMark"
									status="indexRmark">
									<s:if test="#indexRmark.odd">
										<span class="glyphicon glyphicon-star"
											style="color: red; font-size: 8px;"></span>
										<span class="quesTypeScore"><s:property
												value="%{#objRMark}" /></span>得分：
													</s:if>
									<s:else>
										<s:property value="%{#objRMark}" />
										<br />
									</s:else>
								</s:iterator>
								<span class="glyphicon glyphicon-star"
									style="color: red; font-size: 8px;"></span>
								<s:text name="pm.cl.testTotalScore"></s:text>
								：
								<s:property value="pmClQuesnaireResultHeader.quesMarkScore" />
								<br /> <span class="glyphicon glyphicon-star"
									style="color: red; font-size: 8px;"></span>
								<s:text name="pm.cl.testTotalResult"></s:text>
								：
								<s:if test="pmClQuesnaireResultHeader.quesMarkResult==-1">
													 测评不通过
												</s:if>
								<s:else>
													测评通过
												</s:else>
							</div>
						</div>
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
										<table border="0" cellpadding="0" cellspacing="0" width="100%">
											<tbody>
												<tr>
													<td valign="top" style="width: 10px;"><s:if
															test="#obja.questionType==3">
															<div>
																<span class="redmark" style="color: black;">*<s:property
																		value="#obja.questionNum" />.
																</span>
															</div>
														</s:if> <s:else>
															<div>
																<span class="redmark">*<s:property
																		value="#obja.questionNum" />.
																</span>

															</div>
														</s:else></td>
													<td>
														<div class="yl_title" id="q_t_6416">
															<p style="margin-right: 0px;">
																<span><s:property value="#obja.questionContent" />
																</span>
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
																	textSize="small" class="form-control"
																	rows="3" style="float: left; width: 350px;"></textarea><span
																class="content_pm_proplem_type">[双击可放大或缩小]</span></td>
														</s:if>
														<s:else>
															<s:iterator value="pmClQuesnaireResultLineList"
																id="objRLine" status="indexRLine">
																<s:if test="#objRLine.quesnaireTemplateLineId==#obja.id">
																	<td>
                                                                        <textarea name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionAnswer"
																			textSize="small" class="form-control" style="float: left;"><s:property value="%{#objRLine.questionAnswer}" /></textarea>
                                                                        <span class="content_pm_proplem_type">[双击可放大或缩小]</span>
                                                                    </td>
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
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>