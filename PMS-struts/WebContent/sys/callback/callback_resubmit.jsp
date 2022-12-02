<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<head>
<dp:base />
<style type="text/css">
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
		$("#submitFlow").click(function(){
			$("#submitWorkFlow").submit();
		});
		
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
	});
</script>
</head>
<body>
	<div>
		<!-- 项目基本信息 -->
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">项目基本信息</h3>
			</div>
			<div class="panel-body">
				<ul>
					<li class="headerLi" style="width: 20%"><span
						style="font-weight: 700;"><s:text
								name="pm.project.projectCode"></s:text>：</span><span><s:property
								value="project.projectCode" /></span></li>
					<li class="headerLi"><span style="font-weight: 700;"><s:text
								name="pm.project.projectName"></s:text>：</span><span><s:property
								value="project.projectName" /></span></li>
					<li class="headerLi" style="width: 20%"><span
						style="font-weight: 700;"><s:text
								name="pm.project.finalCustomerName"></s:text>：</span><span><s:property
								value="project.column013" /></span></li>
				</ul>
				<ul>
					<li class="headerLi" style="width: 20%"><span
						style="font-weight: 700;"><s:text
								name="pm.project.finalCustomerName"></s:text>联系人：</span></li>
				</ul>
				<s:iterator value="projectMemberList" id="objmeber"
					status="indexmeber">
					<s:if test="#objmeber.memberRole=='60'&&#objmeber.dataState==1">
						<ul>
							<li class="headerLi" style="width: 20%"><span
								style="font-weight: 700;"><s:text name="pm.cl.name"></s:text>：</span><span><s:property
										value="%{#objmeber.memberName}" /></span></li>
							<li class="headerLi" style="width: 20%"><span
								style="font-weight: 700;"><s:text name="pm.cl.phnoe"></s:text>：</span><span><s:property
										value="%{#objmeber.phoneNum}" /></span></li>
							<li class="headerLi"><span style="font-weight: 700;"><s:text
										name="pm.cl.mail"></s:text>：</span><span><s:property
										value="%{#objmeber.email}" /></span></li>
						</ul>
					</s:if>
				</s:iterator>
				<ul>
					<li class="headerLi" style="width: 20%">
						<span style="font-weight: 700;">备注信息：</span><span>
						<s:property value="callBack.remark"/>
					</li>
				</ul>
			</div>
		</div>
		
 		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">项目回访</h3>
			</div>
			<div class="panel-body">
				<!-- 审批意见 -->
				<display:table
					name="commentList" pagesize="${commentList.size()}" export="false"
					size="${commentList.size()}" sort="external"
					decorator="com.dp.plat.decorators.Wrapper" class="table"
					partialList="true">
					<display:column property="assigneeName" titleKey="workflow.transactor"></display:column>
					<display:column property="assigneeTime" titleKey="sys.create.time" format="{0,date,yyyy-MM-dd}"></display:column>
					<display:column property="resultName" titleKey="pm.cl.evaluHeader.approveResult" ></display:column>
					<display:column property="message" titleKey="pm.cl.evaluHeader.approveRemark"></display:column>
					<display:column property="seeQuesnaire" titleKey="pm.cl.evaluHeader.viewQuesnare"></display:column>
				</display:table>
				
				<s:form method="POST" action="module/sub/callback_resubmit.action" id="callbackForm" cssClass="form-inline" >
					
					<s:hidden name="param.instId" value="%{callBack.instId}"></s:hidden>
					<s:hidden name="param.objId" value="%{callBack.callBackId}"></s:hidden>
					<s:hidden name="param.outcome" value="0"></s:hidden>
					<s:hidden name="param.comment" value="提交申请"></s:hidden>
					<s:hidden name="callBack.projectId"></s:hidden>
					<s:hidden name="callBack.callBackId"></s:hidden>	
					<div class="form-group form-group-query">
						<label for="remark" style="width: 90px;">*备注信息</label>
						<s:textarea name="callBack.remark" cssClass="form-control"></s:textarea>
					</div>
					<br/>
					<div class="form-group form-group-query">
						<button class="btn btn-info btn-block">提交申请</button>
					</div>
				</s:form> 
			</div>
		</div>
	</div>
</body>
</html>