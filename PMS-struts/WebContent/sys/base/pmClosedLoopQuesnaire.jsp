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
<script type="text/javascript">
	$(function(){
		$("#addPCLQButton").click(function(){
			addPCLQClick();
		});
		$("a.deleteQues").click(function(){
			$(this).attr("disabled","disabled");
			if(!confirm("确定是否删除问卷")){
				$(this).removeAttr("disabled","disabled");
				return false;
			}else{
				$(this_obj).attr("disabled","disabled");
				return true;
			}
		});
		$("a.endEffective").click(function(){
			$(this).attr("disabled","disabled");
			if(!confirm("确定是否失效问卷，失效后问卷将不能使用")){
				$(this).removeAttr("disabled","disabled");
				return false;
			}else{
				$(this).attr("disabled","disabled");
				return true;
			}
		}); 
		
	});
	
	function aClick(this_obj,waring){
		if(!confirm(waring)){
			return false;
		}else{
			$(this_obj).prop("disabled","disabled");
			return true;
		}
	}
	
	function addPCLQClick(){
		window.location.href="base/AddPmClosedLoopQuesnaire.action";
	}
	
</script>
</head>
<body>

<div id="base" class=""> 
	    <s:form  method="POST" action="" id="" cssClass="form-inline">
	    	<div class="form-group form-group-query">
				<label for="seePmEvaluationScore" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.cl.questionnaireName" /></label>
		    	<s:textfield  value="" id="seePmEvaluationScore" placeholder="问卷名称"  name="pmClosedLoopQuesnaire.questionnaireTemplateName" cssClass="form-control" cssStyle="width: 200px;display: inline-block;" />
			</div>
			<div class="form-group form-group-query">
				<label for="seePmEvaluationScore" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.cl.questionnaireNum" /></label>
		    	<s:textfield  value="" id="seePmEvaluationScore" placeholder="问卷编号"   name="pmClosedLoopQuesnaire.questionnaireTemplateName" cssClass="form-control" cssStyle="width: 200px;display: inline-block;" />
			</div>  
		    <div class="btn-group btn-group-sm"  style="margin-left:8px;">
			  <button type="button" class="btn btn-default" style="margin-right:4px;">
			  	<span class="glyphicon glyphicon-search" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="pm.cl.quesQuery"></s:text></span>
			  </button>
			</div>
		</s:form>
		
		
		<div class="panel panel-default" style="margin-top:34px;">
		   <div class="panel-heading">
		      <span><img src="images/right_zhishi.gif" border="0" ></span><s:text name="pm.cl.quesList"></s:text>
		   </div>
		   <div class="panel-body">
			    <div class="btn-group btn-group-sm">
				  <button id="addPCLQButton" type="button" class="btn btn-default" style="margin-right:4px;">
				  	<span class="glyphicon glyphicon-plus" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;添加问卷</span>
				  </button>
				</div>
	    
			     <div style="margin-top:8px;">
			    	<display:table
						name="pmClosedLoopQuesnaireList" pagesize="${displayParam.pagesize}" export="true"
						size="${displayParam.totalcount}" sort="external"
						requestURI="module/PmClosedLoopQuesnaire.action"
						decorator="com.dp.plat.decorators.Wrapper" class="table table-striped"
						partialList="true">
						<display:column property="questionnaireTemplateNum" titleKey="pm.cl.questionnaireNum" ></display:column>
						<display:column property="questionnaireTemplateName" titleKey="pm.cl.questionnaireName"></display:column>
						<display:column property="questionnaireScore" titleKey="pm.cl.questionnaireScore" ></display:column>
						<display:column property="questionnairePassScore" titleKey="pm.cl.questionnairePassScore" ></display:column>
						<display:column property="quesTypeName" titleKey="pm.cl.quesTyle"></display:column>
						<display:column property="createdPerson" titleKey="pm.cl.createdPerson"></display:column>
						<display:column property="clQuestionnaireStatus" titleKey="pm.cl.status"></display:column>
						<display:column property="createdTime" titleKey="pm.cl.createdTime" format="{0,date,yyyy-MM-dd}"></display:column>	
						<display:column property="clQuestionnaireTemplateDo" titleKey="display.operate"></display:column>
						<display:column property="clQuestionnaireSee" titleKey="pm.cl.quesSee"></display:column>	
					</display:table>
			    </div>
		   </div>
		</div>   
</div>
</body>
</html>
