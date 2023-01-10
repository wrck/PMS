<%@page import="com.dp.plat.param.WorkspaceLiParam"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.basemanage' />">
<meta name="function" content="<s:text name='pm.cl.quesMana' />">
<style type="text/css">	
	.pccSubmitDiv{
	margin-top: 10px;
	background-color: bisque;
	background-color: aliceblue; 
	text-align: center; 
	height: 120px;
}

.headerSpan{
	font-size: 14px;
	font-weight: 700;
}

a span{
		font-family: "Helvetica Neue",Helvetica,Arial,sans-serif;
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
	
.headerLi{
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
	margin-top: 24px;
	}
.content_pm_proplem_type {
	color: #999;
	font-size: 12px;
	margin-left: 10px;
	}
.content_pm_sort{
	font-weight: bold;
	color: #1473CB;
}	
.mainDiv{
	height: auto!important;
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
	$(function(){
		$("div.headerMenu").each(function(){ 
			$(this).mouseover(function(){
				headerMenuMouseover(this);
			});	
			$(this).mouseout(function(){
				headerMenuMouseout(this);
			});	
		});
		$("#addPCLQButton").click(function(){
			addPCLQClick();
		});
		
	});
	
	
	function addPCLQClick(){
		window.location.href="base/AddPmClosedLoopQuesnaire.action";
	}
	
</script>
</head>
<body>
	<div>
		<div class="panel-group" id="claccordion">
		   <div class="panel panel-default">
		      <div class="panel-heading">
	             <span><img src="images/right_zhishi.gif" border="0" ></span>
	  			 <s:text name="pm.cl.quesSee"></s:text>
		      </div>
		         <div class="panel-body">		 
					<div>
						<span class="headerSpan"><s:property value="pmClosedLoopQuesnaire.questionnaireTemplateName"/></span>
					</div>
					<div class="pmclnotice">
						<div class="info clearfix" style="height:30px">
							<ul>
								<li class="headerLi">
									<s:text name="pm.cl.createdPerson"></s:text>：<span class="color-blue"><s:property value="pmClosedLoopQuesnaire.createdPerson"/></span>
								</li>
								<li class="headerLi">
									<s:text name="pm.cl.createdTime"></s:text>：<span class="color-blue"><s:date name="pmClosedLoopQuesnaire.createdTime" format="yyyy-MM-dd"/></span></li>
								<li class="headerLi">
										<span><s:text name="pm.cl.questionnaireScore"></s:text>:<s:property value="pmClosedLoopQuesnaire.questionnaireScore"/></span>
								</li>
								<li class="headerLi">
									<span><s:text name="pm.cl.questionnairePassScore"></s:text>:<s:property value="pmClosedLoopQuesnaire.questionnairePassScore"/></span>
								</li>
								<li class="headerLi">
									<span><s:text name="pm.cl.quesTyle"></s:text>:<s:property value="pmClosedLoopQuesnaire.quesTypeName"/></span>
								</li>
							</ul>
						</div>
					</div>

			<s:iterator value="pmClosedLoopQuesnaireLineList" id="obja" status="indexa">
				<div class="pmclquescontent">
					<div class="content_pm_proplem">
						<div class="yl_header">
						<table border="0" cellpadding="0" cellspacing="0" width="100%">
						<tbody>
						<tr>
						<td valign="top" style="width:10px;">
						<div><span class="redmark">*</span><s:property value="#obja.questionNum"/>.</div>
						</td>
						<td>
						<div class="yl_title" id="q_t_6416">
							<p style="margin-right: 0px;">
								<span><s:property value="#obja.questionContent"/>
								</span>
								<s:if test="#obja.questionType==1">
									<span class="content_pm_proplem_type">[单选题]</span>
								</s:if>
								<s:if test="#obja.questionType==3">
									<span class="content_pm_proplem_type">[问答题]</span>
								</s:if>
								<s:iterator value="quesLineTypeList" id="objqt" status="indexqt">
									<s:if test="#obja.questionTypeForCB==#objqt.basicDataId">
										<span class="content_pm_proplem_type">[<s:property value="%{#objqt.basicDataName}"/>]</span>
									</s:if>
								</s:iterator>
								<span  class="content_pm_proplem_type">[<span><s:property value="#obja.questionScore"/></span>分]</span>
							</p>
						</div>
						<div class="yl_tip"></div></td></tr></tbody></table></div>

						<div class="yl_one_item">
							<table class="yl_one_item_tbl">
							<tbody>
							<tr>
								<s:iterator value="pmClosedLoopQuesnaireOptList" id="objOpt" status="indexOpt">
									<s:if test="#objOpt.questionId==(#obja.id)">
										<s:if test="#obja.questionType==1">											
												<td><input type="radio" value="<s:property value="%{#objOpt.id}"/>" id="clopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>" name="clClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionTemplateOptId" style="margin-right: 10px;"></td>
												<td><label style="margin-right: 28px;" for="clopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"><s:property value="%{#objOpt.questionOptionsContent}"/><span style="font-weight: 100;font-size: 8px;" class="content_pm_proplem_type">（<s:property value="%{#objOpt.questionOptionScore}"/>分）</span></label>
												</td>											
										</s:if>
									</s:if>
								</s:iterator>
								<s:if test="#obja.questionType==3">									
										<td><textarea  id="" name="" class="form-control" rows="3"  style="float:left; width: 350px;"></textarea></td>																	
								</s:if>
							</tr>
							</tbody>
							</table>
							</div>
						</div>
					</div>
			</s:iterator>
			<div class="pmclnotice">
				<span><s:text name="pm.cl.markRule"></s:text>：</span><br/>
				<s:iterator value="markList" id="objmark" status="indexmark">
					<span class="glyphicon glyphicon-star" style="color:#2aabd2;font-size:8px;"></span><s:property value="%{#objmark}"/><br/>
				</s:iterator>
			</div>
		 </div>
   </div>
	</div>
	</div>

</body>
</html>
