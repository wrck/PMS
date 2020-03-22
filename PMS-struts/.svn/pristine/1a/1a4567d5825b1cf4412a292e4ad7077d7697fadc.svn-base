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
	.pccSubmitDiv{
	margin-top: 10px;
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
	margin-top: 10px;
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
var pmCLProjectTaskId="";
	$(function(){
		pmCLIniFunVar();
		$("#pmCLChoseQuesButt").change(function(){
			$("#cbCLDivChoseForm").submit();
		}); 
		$("button[checkButton='check']").each(function(){ 
			$(this).click(function(){
				var success = pmCLAddSubmit(this);
				if(!success){
					$("button[checkButton='check']").each(function(){ 
						$(this).removeAttr("disabled");
					});
					$("#cantCBButton").removeAttr("disabled");
				}
			});
		}); 
		
		$("textarea[textSize]").each(function(){
			$(this).dblclick(function(){
				textSize(this);
			});
		});
		
		$("#cantCBButton").click(function(){
			cantCBDiv(this);
		});
	}); 
	
	function pmCLIniFunVar(){
		$("div[divshowArr='pmcl']").each(function(){
			$(this).hide();
		});
		
		pmCliniPower('<s:property value="pmClosedLoopResultType"/>');
		
		$("#pmCLWarDom").hide();
		
		if($("span.pagebanner")){
			$("span.pagebanner").remove();
		}
		if($("span.onepagelinks")){
			$("span.onepagelinks").remove();
		}
		
		$("#seePmEvaluationComment").val(""); 
		
		$("#seePmEvaluationScore").val("<s:property value='pmClEvaluationHeader.evaluationScore'/>");
		
		if("<s:property value='pmClQuesnaireResultHeader.quesMarkResult'/>"=="-1"){
			$("#cbResultRadio").hide();
		}
		
		if("<s:property value='pmClosedLoopResultType'/>"!="10"){
		/*	
		 * $("#pmEvaluationComment").val("<s:property value='pmClApplyHeader.evaluationComment'/>");
		 */
			$("#pmEvaluationComment").attr("disabled","disabled");
			
			$("#pmCreatedPerson").val("<s:property value='pmClApplyHeader.evaluationPeopleId'/>"+"-"+"<s:property value='pmClApplyHeader.evaluationPeopleName'/>");
			
			$("#pmCLNextAcceptPerson").val("<s:property value='pmClApplyHeader.nextAcceptPerson'/>"+"-"+"<s:property value='pmClApplyHeader.nextAcceptPersonName'/>");
		}
		var score30Value=0;
		var score10Value=0;
		
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
		if("<s:property value='pmClosedLoopResultType'/>"=="-100"){
			$("#pmEvaluationComment").css("float","none");
			$("#seeTotalScore").show();
			var clScore=0;
			if($("#totalscore_4")){
				clScore=parseInt($("#totalscore_4").text());
			}
			
			$("#histotalScore").val(score30Value+clScore+score10Value);
			$("#his30Score").val(score30Value);
			$("#his10Score").val(score10Value);
			$("#hisclScore").val(clScore);
			
		} 
		
	}
	
	function pmCliniPower(returnData){
		if(returnData){	//项目闭环申请阶段
			switch(parseInt(returnData)){
				case 10:	
					$("#pmCLDiv").show();
					break;
				case 20:
					$("#smCLDiv").show();
					break;
				case 30:
					$("#cbCLDivChose").show();
					$("#cbCLForm").attr("action","module/sub/PmClosedLoopSub_addCbCLApply.action");
					$("#seeScoreForm").attr("action","module/sub/PmClosedLoopSub_addCbCLApply.action");
					break;	
				case 40:
					$("#cbCLDivChose").show();
					$("#cbCLForm").attr("action","module/sub/PmClosedLoopSub_addClCLApply.action");
					$("#seeScoreForm").attr("action","module/sub/PmClosedLoopSub_addClCLApply.action");
					break;											
				default:
					break;
			}
		}else{
			alert("系统错误，请联系管理员！");
		}
	}
	
	function pmCLProcessView(workData){
		if(!workData){
			alert("系统错误，请联系管理员！");
			return;
		}
		var deploymentIdVal=workData.deploymentId;
		var imageNameVal=workData.imageName;
		var htmlVal='work/WorkFlowViewImage.action?param.deploymentId='+deploymentIdVal+'&param.imageName='+imageNameVal;
		$("#queryProcessView").attr("href",htmlVal);
		
		$("#pmCLWarDom").hide();
	}
	
	function textSize(this_obj){
		var h=65;
		if($(this_obj).html()){
		/* 	var line=$(this_obj).html().length;
			h=(line/30)*30;
			if($(this_obj).html().split("\n").length>1){ */
			var arr=$(this_obj).html().split("\n");
			h=($(this_obj).html().split("\n").length)*30;
			for(var i=0;i<arr.length;i++)
				if(arr[i].length>=30)h=h+((arr[i].length)/30)*30;			
/* 			} */
		}else
			h=120;
		
		if(h<65)h=65;
		var w=h==65?350:500;
		if($(this_obj).attr("textSize")=="small"){
			$(this_obj).css("width",w+"px");
			$(this_obj).css("height",h+"px");			
			$(this_obj).attr("textSize","big");
		}else{
			$(this_obj).css("width","350px");
			$(this_obj).css("height","auto");
			$(this_obj).attr("textSize","small");
		}
		
	}
	
	function cantCBDiv(this_obj){
		$("#cantCBDiv").show();
		$("#cbCLDiv").hide();
		$(this_obj).attr("disabled","disabled");
			
	}
	
	function pmCLAJAX(urlText,paraData,callBackFun){
		$.ajax({
			url:urlText,
			type:"post",
			dataType:"json",
			data:paraData,
			success:function(data){
				callBackFun(data);
			}
		});
	}
	
	function pmCLAddSubmit(this_obj){
		$("button[checkButton='check']").each(function(){ 
			$(this).attr("disabled","disabled");
		}); 
		var regex=/^\s*$/;
		var thisObjVal=$(this_obj).val();
		var submitFormId="";
		var text = "确认要提交审批意见么？";
		switch(thisObjVal){   
			case "pmAddPCLQButton":
				submitFormId="pmCLForm";
				text = "确认要提交闭环申请么？";
				break;
			case "smAddPCLQButton": 
				submitFormId="smCLForm";
					if(!'<s:property value="project.taskId"/>'){
						alert("系统出错，请联系管理员");
						$("button[checkButton='check']").each(function(){ 
							$(this).removeAttr("disabled");
						}); 
						return false;
					}
				break;
			case "cbAddPCLQButton_draft":				
				submitFormId="cbCLForm";
				text = "是否将问卷结果保存成草稿？";
				break;
			case "cbAddPCLQButton_submit":  
				$("#cbQuesPmCLStatus").val(1);	//提交问卷状态        
				submitFormId="cbCLForm";
				text = "是否提交问卷？";
				break;
			case "addSeeScoreButton":
				submitFormId="seeScoreForm";
				break;	
			case "cantCBSubmit":
				submitFormId="cantCBForm";
				$("#cantCBButton").attr("disabled","disabled");
				text = "是否提交回复结果为“无法回访”？";
				if(!$("#cantCBReason").val()){
					alert("请输入无法回访原因！");
					return false;
				}
				break;
			default:
				alert("系统错误，请重新提交");
				$("button[checkButton='check']").each(function(){ 
					$(this).removeAttr("disabled");
				}); 
				return false;
				break;
		}
		
		
		if(confirm(text)){
			$("#"+submitFormId).submit();
			return true;
		}else{
			$("button[checkButton='check']").each(function(){ 
				$(this).removeAttr("disabled");
			});
			$("#cantCBButton").removeAttr("disabled");
			return false;
		}
	}

	function pmCLcheckSubmitValue(submitValue,regex,isFormatCheck,waringText1,waringText2,waringObjId){
		var regexIsEmpty=/^\s*$/;
		if(regexIsEmpty.test(submitValue)){ //非空验证
			$("#"+waringObjId).text("");
			$("#"+waringObjId).show();
			$("#"+waringObjId).text(waringText1);
			return false;
		}else{
			if(isFormatCheck){
				if(!regex.test(submitValue)){ //字段格式验证
					$("#"+waringObjId).text("");
					$("#"+waringObjId).show();
					$("#"+waringObjId).text(waringText1+waringText2);
					return false;
				}else{
					$("#"+waringObjId).text("");
					$("#"+waringObjId).hide();
					 return true;
				}
			}else{
				$("#"+waringObjId).text("");
				$("#"+waringObjId).hide();
				return true;
			}
		}
	}
	
	function queryalluser(){
		$.ajax({
			url:'queryalluser.action',
			type:'post',
			dataType:'json',
			data:{},
			success:queryuser2
		});
	}
	function queryuser2(json){
		var userlist = json.allusernameList;
		for(var i = 0;i < userlist.length;i++){
			usernameArr[i] = userlist[i].username;
			realnameArr[i] = userlist[i].username+"-"+userlist[i].realName;
		}
	}
	function commonfill(idjson, id){
		var obj=document.getElementById(idjson);
		if(obj.value==""){
			document.getElementById(id).value="";
		}
		if(obj.value!=""){
			var i=0;
			for(;i<realnameArr.length;i++){
				if(realnameArr[i]==obj.value){
					break;
				}
			}
			if(i==realnameArr.length){
				return false;
			} else{
				document.getElementById(id).value=usernameArr[i];
			}
		}
	}
	
	/* $(function(){
		// 回访人员权限
		var cbRole = window.top.pmCLPersonRoleArr.cb;
		// 工程管理部权限
		var gcbRole = window.top.pmCLPersonRoleArr.cl;
		if(gcbRole){
			$("#pmCLChoseQuesButt").val(1);
		}
		if(cbRole){
			var column12 = $(window.top.document).find("#programManagerTxt").val();
			var column11 = $(window.top.document).find("#column011").val();
			if(column11 == 20 && column12 == 1){
				
			}else{
				$("#pmCLChoseQuesButt").val(2);
			}
		}
		if($("#pmCLChoseQuesButt").val() != "0"){
			$("#cbCLDivChoseForm").submit();
		}
	}) */
</script>
</head>
<body style="width: 98%;height: 98%">
	<!-- 项目基本信息 -->
	<div>
		<div class="panel panel-default">
		   <div class="panel-body">
				<ul> 
					<li class="headerLi" style="width:20%">
						<span style="font-weight: 700;"><s:text name="pm.project.projectCode"></s:text>：</span><span><s:property value="project.projectCode"/></span>
					</li>
					<li class="headerLi">
						<span style="font-weight: 700;"><s:text name="pm.project.projectName"></s:text>：</span><span><s:property value="project.projectName"/></span>
					</li>
					<li class="headerLi" style="width:20%">
						<span style="font-weight: 700;"><s:text name="pm.project.finalCustomerName"></s:text>：</span><span><s:property value="project.column013"/></span>
					</li>
				</ul>
				<ul>
					<li class="headerLi" style="width:20%">
						<span style="font-weight: 700;"><s:text name="pm.project.finalCustomerName"></s:text>联系人：</span>
					</li>
				</ul>
				<s:iterator value="projectMemberList" id="objmeber" status="indexmeber">
				    <s:if test="#objmeber.memberRole==60&&#objmeber.dataState==1">
						<ul>
							<li class="headerLi" style="width:20%">
								<span style="font-weight: 700;"><s:text name="pm.cl.name"></s:text>：</span><span><s:property value="%{#objmeber.memberName}"/></span>
							</li>
							<li class="headerLi" style="width:20%">
								<span style="font-weight: 700;"><s:text name="pm.cl.phnoe"></s:text>：</span><span><s:property value="%{#objmeber.phoneNum}"/></span>
							</li>
							<li class="headerLi" >
								<span style="font-weight: 700;"><s:text name="pm.cl.mail"></s:text>：</span><span><s:property value="%{#objmeber.email}"/></span>
							</li>
						</ul>
					</s:if>
				</s:iterator>
			 </div>
		</div>
	</div>
	
	<!-- 表单区域 -->
	
	<div class="panel panel-default">
	   <div class="panel-heading">
	      项目闭环申请
	   </div>
	   <div class="panel-body">
		    <div id="mainFormDiv">
		    		<s:form method="POST" action="module/sub/PmClosedLoopSub_addPmCLApply.action" id="pmCLForm">					
					<input type="hidden" id="pmCLProjectId" value='<s:property value="project.projectId"/>' name="project.projectId"/>

					<div class="form-group form-group-query" style="width: 46%;">
						<label for="pmCreatedPerson"  style="width: 90px;"><span class="redmark" style="color:black;">*</span><s:text name="pm.cl.clApply" /></label>
				    	<%-- <input disabled="disabled" id="pmCreatedPerson"  class="form-control" value="<s:property value="project.programManagerCodeforjson"/>" style="width: 200px;display: inline-block;" />
						 --%>
						 <input disabled="disabled" id="pmCreatedPerson"  class="form-control" value="<s:property value="closeApplyUser"/>" style="width: 200px;display: inline-block;" />
					</div>				
						
					<div class="form-group form-group-query" >
						<label for="pmEvaluationComment" style="width: 90px;float:left;"><span class="redmark" style="color:black;">*</span><s:text name="pm.cl.evaluHeader.applyRemark" /></label>
						<s:if test="%{pmClosedLoopResultType!=10}">
							<s:textarea id="pmEvaluationComment" name="pmClApplyHeader.evaluationComment" cssClass="form-control" rows="3"  cssStyle="width: 350px;float:left;"/>
						</s:if>
						<s:else>
							<textarea id="pmEvaluationComment" name="pmClEvaluationHeader.evaluationComment" class="form-control" rows="3"  style="width: 350px;float:left;"></textarea>
						</s:else>
						<div id="pmCLDiv" divshowArr="pmcl" class="btn-group btn-group-sm" style="margin-top:32px;margin-left:10px;">
						  <button checkButton='check' value="pmAddPCLQButton" type="button" class="btn btn-info" style="margin-right:4px;">
						  	<span class="glyphicon glyphicon-ok" style="font-size:12px;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="pm.cl.evaluHeader.submitApply" /></span>
						  </button>
						</div>
					</div>
				</s:form> 				
				<div class="form-group form-group-query" divshowArr="pmcl" id="seeTotalScore">					
					<label for=""  style="width: 90px;float:left;"><span class="redmark" style="color:black;">*</span><s:text name="pm.cl.totalScore" /></label>
			    	 <div class="input-group" style="width: 150px;float:left;">
				         <span class="input-group-addon"><s:text name="pm.cl.totalScore" /></span>
				         <input disabled="disabled" type="text" id="histotalScore" class="form-control"  style="float:left;background-color: bisque;z-index: inherit;">
				    </div>
				    <span style="float:left;margin-left: 16px;margin-top: 10px;">=</span>
				    <div class="input-group"  style="width: 150px;float:left;margin-left: 16px;">
				         <span class="input-group-addon"><s:text name="pm.cl.cbScore01" /></span>
				         <input disabled="disabled" type="text" id="his30Score" class="form-control" style="background-color: bisque;z-index: inherit;">
				    </div>
				     <span style="float:left;margin-left: 16px;margin-top: 10px;">+</span>
				    <div class="input-group" style="width: 150px;float:left;margin-left: 16px;">
				         <span class="input-group-addon"><s:text name="pm.cl.cbScore02" /></span>
				         <input disabled="disabled" id="his10Score" type="text" class="form-control" style="background-color: bisque;z-index: inherit;">
				    </div>					    
				     <span style="float:left;margin-left: 16px;margin-top: 10px;">+</span>
				    <div class="input-group" style="width: 150px;margin-left: 660px;">
				         <span class="input-group-addon"><s:text name="pm.cl.clScore" /></span>
				         <input disabled="disabled" id="hisclScore" type="text" class="form-control" style="background-color: bisque;z-index: inherit;">
				    </div>	  	 
				</div>	
				
			</div> 
			
			<!-- 项目闭环历史列表 -->
			<div id="pmCLList" style="margin-top:20px;">
				 <display:table
						name="pmClEvaluationHeaderListHis" pagesize="${pmClEvaluationHeaderListHis.size()}" export="false"
						size="${pmClEvaluationHeaderListHis.size()}" sort="external"
						decorator="com.dp.plat.decorators.Wrapper" class="table table-striped"
						partialList="true">
						<display:column property="evaluationPeopleId" titleKey="pm.cl.evaluHeader.approvePerId"></display:column>
						<display:column property="evaluationPeopleName" titleKey="pm.cl.evaluHeader.approvePerName" ></display:column>
						<display:column property="evaluationTime" titleKey="pm.cl.evaluHeader.approveTime" format="{0,date,yyyy-MM-dd HH:mm:ss}"></display:column>
						<display:column property="pmCLApproveResult" titleKey="pm.cl.evaluHeader.approveResult"></display:column>
						<display:column property="pmclEvaluationComment" titleKey="pm.cl.evaluHeader.approveRemark"></display:column>
						<display:column property="pmSeeCbCl" titleKey="pm.cl.quesSee"></display:column>
					</display:table> 
			</div>
			
			<!-- 问卷历史  不需要展示，统一通过查看问卷点击进行查看-->
			<s:if test="pmClEvaResultList.size()>0">
			<s:iterator value="pmClEvaResultList" id="objeva" status="indexeva">
			<div id="cbCLDivHis"  divshowArr="pmcl">
			<div class="panel-group" id="cbaccordion_<s:property value='%{#objeva.id}'/>">
			   <div class="panel panel-default">
			      <div class="panel-heading">
			            <a data-toggle="collapse" data-parent="#cbaccordion_<s:property value='%{#objeva.id}'/>" 
			               href="#cbcollapseOne_<s:property value='%{#objeva.id}'/>">
				              <s:if test="#objeva.evaluationType==3">
				              	最新回访结果
				              </s:if>
				               <s:if test="#objeva.evaluationType==4">
				              	最新闭环建议
				              </s:if>
			            </a>
			      </div>
		<s:iterator value="#objeva.resultHeaderList" id="objrheader" status="indexrheader">
	      <div id="cbcollapseOne_<s:property value='%{#objeva.id}'/>" class="panel-collapse collapse in">
	         <div class="panel-body">         
			<div class="pmclnotice">
			<div>
				<span class="headerSpan"><s:property value="%{#objrheader.quesnaireTemp.questionnaireTemplateName}"/></span>
			</div>
			<div class="info clearfix" style="height:30px">
				<ul>
					<li class="headerLi">
						<s:text name="pm.cl.createdPerson"></s:text>：<span class="color-blue"><s:property value="%{#objrheader.quesnaireTemp.createdPerson}"/></span>
					</li>
					<li class="headerLi">
						<s:text name="pm.cl.createdTime"></s:text>：<span class="color-blue"><s:date name="%{#objrheader.quesnaireTemp.createdTime}" format="yyyy-MM-dd"/></span></li>
					<li class="headerLi">
							<span><s:text name="pm.cl.questionnaireScore"></s:text>:<s:property value="%{#objrheader.quesnaireTemp.questionnaireScore}"/></span>
					</li>
					<li class="headerLi">
						<span><s:text name="pm.cl.questionnairePassScore"></s:text>:<s:property value="%{#objrheader.quesnaireTemp.questionnairePassScore}"/></span>
					</li>
					<li class="headerLi">
						<span><s:text name="pm.cl.quesTyle"></s:text>:<s:property value="%{#objrheader.quesnaireTemp.quesTypeName}"/></span>
					</li>
				</ul>
			</div>
			</div>
		
			<s:form>
				<s:iterator value="#objrheader.quesnaireTemp.pmCLQuesLineList" id="obja" status="indexa">
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
										<span class="content_pm_proplem_type">[<s:text name="pm.cl.quesOne"></s:text>]</span>
									</s:if>								
									<s:if test="#obja.questionType==3">
										<span class="content_pm_proplem_type">[<s:text name="pm.cl.quesAnw"></s:text>]</span>									
									</s:if>
									<s:iterator value="quesTypeList" id="objqt" status="indexqt">
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
									<s:iterator value="#objrheader.quesnaireTemp.pmCLQuesOptList" id="objOpt" status="indexOpt">
										<s:if test="#objOpt.questionId==(#obja.id)">
											<s:if test="#obja.questionType==1">	
												<s:iterator value="#objrheader.resultLineList" id="objRLine" status="indexRLine">
													<s:if test="#objRLine.quesnaireTemplateLineId==#obja.id">
														<s:if test="#objRLine.questionTemplateOptId==#objOpt.id">
															<s:if test="#objRLine.quesEvaResult==-1">
																<td><input disabled="disabled" type="radio" checked="checked"  value="<s:property value="%{#objOpt.id}"/>"  style="margin-right: 10px;"></td>
																<td style="color:red;"><label style="margin-right: 28px;" ><s:property value="%{#objOpt.questionOptionsContent}"/><span style="font-weight: 100;font-size: 8px;" class="content_pm_proplem_type">（<s:property value="%{#objOpt.questionOptionScore}"/>分）</span></label></td>
															</s:if><s:else>
																<td><input disabled="disabled" type="radio" checked="checked"   style="margin-right: 10px;"></td>
																<td><label style="margin-right: 28px;" ><s:property value="%{#objOpt.questionOptionsContent}"/><span style="font-weight: 100;font-size: 8px;" class="content_pm_proplem_type">（<s:property value="%{#objOpt.questionOptionScore}"/>分）</span></label></td>
															</s:else>															
														</s:if>
														<s:else>
															<td><input disabled="disabled" type="radio" value="<s:property value="%{#objOpt.id}"/>" style="margin-right: 10px;"></td>
															<td><label style="margin-right: 28px;" ><s:property value="%{#objOpt.questionOptionsContent}"/><span style="font-weight: 100;font-size: 8px;" class="content_pm_proplem_type">（<s:property value="%{#objOpt.questionOptionScore}"/>分）</span></label>
															</td>
														</s:else>
													</s:if>
												</s:iterator>												
											</s:if>
										</s:if>
									</s:iterator>
									<s:if test="#obja.questionType==3">	
										<s:iterator value="#objrheader.resultLineList" id="objRLine" status="indexRLine">
											<s:if test="#objRLine.quesnaireTemplateLineId==#obja.id">
												<td><textarea  disabled="disabled" name="" class="form-control" rows="3"  style="float:left; width: 350px;"><s:property value="%{#objRLine.questionAnswer}"/></textarea></td>
											</s:if>
										</s:iterator>
									</s:if>
								</tr>
								</tbody>
								</table>
								</div>
							</div>
						</div>
				</s:iterator>					
				</s:form>
				 <div> 
					<div class="pmclnotice">
						<span><s:text name="pm.cl.markRule"></s:text>：</span><br/>
						<s:iterator value="#objrheader.quesnaireTemp.markList" id="objmark" status="indexmark">
							<span class="glyphicon glyphicon-star" style="color:#2aabd2;font-size:8px;"></span><s:property value="%{#objmark.markExplain}"/><br/>
						</s:iterator><br/>
						<ul>
						<li class="headerLi">测评结果：</li>					
							<li class="headerLi">
								<span>（<s:text name="pm.cl.testTime"></s:text>：<s:date name="%{#objeva.evaluationTime}" format="yyyy-MM-dd HH:mm:ss"/></span>
							</li>
							<li class="headerLi">
								<span><s:text name="pm.cl.testPerson"></s:text>：<s:property value="%{#objeva.evaluationPeopleName}"/>）</span>
							</li>						
						</ul>
						<s:iterator value="#objrheader.quesResultMarkList"  id="objRMark" status="indexRmark">
							<s:if test="#indexRmark.odd">
								<span class="glyphicon glyphicon-star" style="color:red;font-size:8px;"></span>
								<span class="quesTypeScore" scoreType="<s:property value='%{#objeva.evaluationType}'/>"><s:property value="%{#objRMark}"/></span>得分：
							</s:if>							
							<s:else>
								<span><s:property value="%{#objRMark}"/></span><br/>
							</s:else>						
						</s:iterator>					
						<span class="glyphicon glyphicon-star" style="color:red;font-size:8px;"></span><s:text name="pm.cl.testTotalScore"></s:text>：<span id="totalscore_<s:property value='%{#objeva.evaluationType}'/>"><s:property value="%{#objrheader.quesMarkScore}"/></span><br/>
						<span class="glyphicon glyphicon-star" style="color:red;font-size:8px;"></span><s:text name="pm.cl.testTotalResult"></s:text>：					
						<s:if test="#objrheader.quesMarkResult==-1">
							 测评不通过
						</s:if><s:else>
							测评通过
						</s:else>					
					</div>
				</div> 
				
			 </div>
	      </div>
	     </s:iterator>
	      
	   </div>
	</div>
	</div>
</s:iterator>
</s:if>				
<!-- 问卷历史 -->
		
   </div>
</div>
	
	<!-- 服务经理审核 -->
	<div id="smCLDiv" divshowArr="pmcl">
		<div class="panel panel-default">
		   <div class="panel-heading">
		     	 项目回访申请
		   </div>
			<s:form  method="POST" action="module/sub/PmClosedLoopSub_addSmCLApply.action" id="smCLForm" cssClass="form-inline">				
				<input type="hidden" value='<s:property value="project.projectId"/>' name="project.projectId"/>
				<input type="hidden" id="smProjectTaskId" value='<s:property value="project.taskId"/>' name="workflowCommonParam.taskId"/>
				<div class="form-group form-group-query">
					<label for="smEvaluationComment" style="width: 90px;float:left;"><span class="redmark" style="color:black;">*</span><s:text name="pm.cl.evaluHeader.approveRemark" /></label>
					<textarea id="smEvaluationComment" name="pmClEvaluationHeader.evaluationComment" class="form-control" rows="3"  style="float:left; width: 350px;"></textarea>
				</div>
				<div class="form-group form-group-query" style="margin-top:10px;">
					<label class="checkbox-inline" style="margin-top: 30px;">
			      	<input type="radio" name="pmClEvaluationHeader.evaluationResult" id="smEvaluationResultNoYes" value="1" checked> <s:text name="pm.cl.cbAgree"></s:text>
				   	</label>
				   	<label class="checkbox-inline" style="margin-top: 30px;">
				      	<input type="radio" name="pmClEvaluationHeader.evaluationResult" id="smEvaluationResultNo" value="-1"> <s:text name="pm.cl.cbReject"></s:text>
				   	</label>
					<div class="btn-group btn-group-sm" style="margin-top: 20px;margin-left:60px;">
					  <button checkButton='check' value="smAddPCLQButton" type="button" class="btn btn-info" style="">
					  	<span class="glyphicon glyphicon-ok" style="font-size:12px;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="pm.cl.submitApprove"></s:text></span>
					  </button>
					</div>
				</div>
			</s:form>
		</div>
	</div>
	
	<!-- 回访人员回访\工程人员闭环 -->		
	<div id="cbCLDivChose" divshowArr="pmcl"><!-- 选着回访问卷 -->
		<s:if test="pmClQuesnaireResultHeader.status!=1">
			<s:if test="pmClosedLoopResultType==30">
				<s:form method="POST" action="module/sub/PmClosedLoopSub_execute.action" id="cbCLDivChoseForm" cssClass="form-inline" style="float:left;">			
					<input type="hidden" name="project.projectId" value="<s:property value='project.projectId'/>"/>
					<input type="hidden" name="pmClosedLoopResultType" value="30"/>
					<div class="form-group form-group-query">
						<label for="pmCLQuesName" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.cl.questionnaireName" /></label>
				    	<%-- <select name="pmClosedLoopQuesnaire.id" id="pmCLChoseQuesButt" class="form-control  btn-info" style="width: 160px;display: inline-block;">
				    		<option value="0">--请选择--</option>
				    		<s:iterator value="pmClosedLoopQuesnaireList" id="objd" status="indexd">
					    		<option value="<s:property value='%{#objd.id}'/>"><s:property value="%{#objd.questionnaireTemplateName}"/></option>
				    		</s:iterator>
				    	</select> headerKey="" headerValue="--请选择--"--%>
				    	<s:select name="pmClosedLoopQuesnaire.id" list="pmClosedLoopQuesnaireList" id="pmCLChoseQuesButt" cssClass="form-control  btn-info" cssStyle="width: 160px;display: inline-block;" 
				    	 listKey="id" listValue="questionnaireTemplateName">
				    	</s:select>
					</div>
				 </s:form>
				 <s:form method="POST" action="module/sub/PmClosedLoopSub_cantCB.action" id="cantCBForm" cssClass="form-inline" >
					<div class="btn-group btn-group-sm" style="margin-top:5px;">
					  <button value="hide" id="cantCBButton" type="button" class="btn btn-default" style="margin-right:4px; width: 100px; height: 34px;">
					  	<span class="glyphicon glyphicon-remove" style="font-size:12px;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="无法回访"></s:text></span>
					  </button>
					</div>
				  <div divshowArr="pmcl" id="cantCBDiv" style="margin-top: 21px;">
				  	<div class="panel panel-default">
					   <div class="panel-heading">
					     	无法回访
					   </div>
					    <input type="hidden" id="canCBTaskId" value='<s:property value="project.taskId"/>' name="workflowCommonParam.taskId"/>
						<input type="hidden" name="project.projectId" value="<s:property value='project.projectId'/>"/>
						<div class="form-group form-group-query" style="margin-top:10px;">
							<label for="cantCBReason" style="width: 90px;float:left;"><span class="redmark">*</span><s:text name="无法回访原因" /></label>
							<textarea id="cantCBReason" name="pmClEvaluationHeader.evaluationComment" class="form-control" rows="3"  style="float:left; width: 350px;"></textarea>
							
							<div class="btn-group btn-group-sm" style="margin-left:80px;margin-top:34px;">
							  <button value="cantCBSubmit" checkButton='check' type="button" class="btn btn-info" style="margin-right:4px;">
							  	<span class="glyphicon glyphicon-ok" style="font-size:12px;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="退至上一级"></s:text></span>
							  </button>
							</div>
					  </div>
					</div>
				  </div>
				</s:form>
			</s:if>
		</s:if>
		
		<s:if test="pmClosedLoopQuesnaireLineList!=null">
		 <div id="cbCLDiv" style="margin-top: 21px;">
			<div class="panel-group" id="cbaccordion">
			   <div class="panel panel-default">
			      <div class="panel-heading">
			            <a data-toggle="collapse" data-parent="#cbaccordion" 
			               href="#cbcollapseOne">
				              <s:if test="pmClosedLoopResultType==30">
				              	 回访问卷
				              </s:if>
				               <s:if test="pmClosedLoopResultType==40">
				              	闭环建议
				              </s:if>
			            </a>
			      </div>
	      <div id="cbcollapseOne" class="panel-collapse collapse in">
	         <div class="panel-body">
	         	<!-- 回访问卷描述 -->         
				<div class="pmclnotice">
					<div>
						<span class="headerSpan"><s:property value="pmClosedLoopQuesnaire.questionnaireTemplateName"/></span>
					</div>
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
								<span><s:text name="pm.cl.quesTyle"></s:text>:<s:property value="pmClosedLoopQuesnaire.quesTypeName}"/></span>
							</li>
						</ul>
					</div>
				</div>
		
			<s:form method="post" action="" id="cbCLForm">
				<input type="hidden" name="pmClosedLoopResultType" value="1"/>
				<input type="hidden" name="project.projectId" value="<s:property value='project.projectId'/>"/>
				<input type="hidden" name=workflowCommonParam.taskId value="<s:property value='project.taskId'/>"/>
				<input type="hidden" value='<s:property value="project.projectCode"/>' name="project.projectCode"/>
				<input type="hidden" name="pmClQuesnaireResultHeader.quesnaireTemplateHeaderId" value="<s:property value='pmClosedLoopQuesnaire.id'/>"/>
				<input type="hidden" name="pmClQuesnaireResultHeader.id" value="<s:property value='pmClQuesnaireResultHeader.id'/>"/>
				<input type="hidden" id="cbQuesPmCLStatus" name="pmClQuesnaireResultHeader.status" value="<s:property value='pmClQuesnaireResultHeader.status'/>"/>
				<input type="hidden" name="pmClEvaluationHeader.id" value="<s:property value='pmClEvaluationHeader.id'/>" />
			
				<s:iterator value="pmClosedLoopQuesnaireLineList" id="obja" status="indexa">
					<input type="hidden" name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].quesnaireTemplateLineId"  value="<s:property value="%{#obja.id}"/>"/>
					<input type="hidden" name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].quesTemplateLineNum"  value="<s:property value="%{#obja.questionNum}"/>"/>
					<input type="hidden" name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].quesTypeForCB"  value="<s:property value="%{#obja.questionTypeForCB}"/>"/>
					
					<div class="pmclquescontent">
						<div class="content_pm_proplem">
							<div class="yl_header">
							<table border="0" cellpadding="0" cellspacing="0" width="100%">
							<tbody>
							<tr>
							<td valign="top" style="width:10px;">
							<s:if test="#obja.questionType==3">
								<div><span class="redmark" style="color:black;">*</span><s:property value="#obja.questionNum"/>.</div>
							</s:if><s:else>
								<div><span class="redmark">*</span><s:property value="#obja.questionNum"/>.</div>
							</s:else>
							</td>
							<td>
							<div class="yl_title" id="q_t_6416">
								<p style="margin-right: 0px;">
									<span><s:property value="#obja.questionContent"/>
									</span>
									<s:if test="#obja.questionType==1">
										<span class="content_pm_proplem_type">[<s:text name="pm.cl.quesOne"></s:text>]</span>
									</s:if>								
									<s:if test="#obja.questionType==3">
										<span class="content_pm_proplem_type">[<s:text name="pm.cl.quesAnw"></s:text>]</span>									
									</s:if>
									<s:iterator value="quesTypeList" id="objqt" status="indexqt">
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
												<s:if test="pmClQuesnaireResultLineList==null">
													<td><input type="radio" value="<s:property value="%{#objOpt.id}"/>" id="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>" name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionTemplateOptId" style="margin-right: 10px;"></td>
													<td><label style="margin-right: 28px;" for="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"><s:property value="%{#objOpt.questionOptionsContent}"/><span style="font-weight: 100;font-size: 8px;" class="content_pm_proplem_type">（<s:property value="%{#objOpt.questionOptionScore}"/>分）</span></label>
													</td>
												</s:if>
												<s:else>
													<s:iterator value="pmClQuesnaireResultLineList" id="objRLine" status="indexRLine">
														<s:if test="#objRLine.quesnaireTemplateLineId==#obja.id">
															<s:if test="#objRLine.questionTemplateOptId==#objOpt.id">
																<s:if test="#objRLine.quesEvaResult==-1">
																	<td><input type="radio" checked="checked"  value="<s:property value="%{#objOpt.id}"/>" id="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>" name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionTemplateOptId" style="margin-right: 10px;"></td>
																	<td style="color:red;"><label style="margin-right: 28px;" for="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"><s:property value="%{#objOpt.questionOptionsContent}"/><span style="font-weight: 100;font-size: 8px;" class="content_pm_proplem_type">（<s:property value="%{#objOpt.questionOptionScore}"/>分）</span></label></td>
																</s:if><s:else>
																	<td><input type="radio" checked="checked"  value="<s:property value="%{#objOpt.id}"/>" id="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>" name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionTemplateOptId" style="margin-right: 10px;"></td>
																	<td><label style="margin-right: 28px;" for="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"><s:property value="%{#objOpt.questionOptionsContent}"/><span style="font-weight: 100;font-size: 8px;" class="content_pm_proplem_type">（<s:property value="%{#objOpt.questionOptionScore}"/>分）</span></label></td>
																</s:else>															
															</s:if>
															<s:else>
																<td><input type="radio" value="<s:property value="%{#objOpt.id}"/>" id="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>" name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionTemplateOptId" style="margin-right: 10px;"></td>
																<td><label style="margin-right: 28px;" for="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"><s:property value="%{#objOpt.questionOptionsContent}"/><span style="font-weight: 100;font-size: 8px;" class="content_pm_proplem_type">（<s:property value="%{#objOpt.questionOptionScore}"/>分）</span></label>
																</td>
															</s:else>
														</s:if>
													</s:iterator>
												</s:else>
											</s:if>
										</s:if>
									</s:iterator>
									<s:if test="#obja.questionType==3">
										<s:if test="pmClQuesnaireResultLineList==null">
											<td><textarea name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionAnswer" id="" name="" textSize="small" class="form-control" rows="3"  style="float:left; width: 350px;"></textarea><span class="content_pm_proplem_type">[双击可放大或缩小]</span></td>
										</s:if>
										<s:else>
											<s:iterator value="pmClQuesnaireResultLineList" id="objRLine" status="indexRLine">
												<s:if test="#objRLine.quesnaireTemplateLineId==#obja.id">
													<td><textarea name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionAnswer" id="" name="" textSize="small" class="form-control" rows="3"  style="float:left; width: 350px;"><s:property value="%{#objRLine.questionAnswer}"/></textarea><span class="content_pm_proplem_type">[双击可放大或缩小]</span></td>
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
					<s:if test="pmClQuesnaireResultHeader.status!=1">
						<div>
							<s:if test="pmClosedLoopResultType==30">
								<div class="btn-group btn-group-sm" style="margin-left:20px;">
								  <button value="cbAddPCLQButton_draft"  checkButton='check' type="button" class="btn btn-info" style="margin-right:4px;">
								  	<span class="glyphicon glyphicon-plus" style="font-size:12px;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="pm.cl.quesDraft"></s:text></span>
								  </button>
								</div>
							</s:if>
							<div class="btn-group btn-group-sm" style="margin-left:20px;">
							  <button value="cbAddPCLQButton_submit"  checkButton='check' type="button" class="btn btn-info" style="margin-right:4px;">
							  	<span class="glyphicon glyphicon-ok" style="font-size:12px;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="pm.cl.quesSubmit"></s:text></span>
							  </button>
							</div>
						</div>
					</s:if>	
				</s:form>
				<s:if test="pmClQuesnaireResultHeader.status==1">
					<div> 
						<div class="pmclnotice">
							<span><s:text name="pm.cl.markRule"></s:text>：</span><br/>
							<s:iterator value="pmClosedLoopQuesnaire.markList" id="objmark" status="indexmark">
								<span class="glyphicon glyphicon-star" style="color:#2aabd2;font-size:8px;"></span><s:property value="%{#objmark.markExplain}"/><br/>
							</s:iterator><br/>
							<ul>
							<li class="headerLi">本次测评结果：</li>								
								<li class="headerLi">
									<span>（<s:text name="pm.cl.testTime"></s:text>：<s:date name="pmClEvaluationHeader.evaluationTime" format="yyyy-MM-dd hh:mm:ss"/></span>
								</li>
								<li class="headerLi">
									<span><s:text name="pm.cl.testPerson"></s:text>：<s:property value="pmClEvaluationHeader.evaluationPeopleName"/>）</span>
								</li>								
							</ul>
							<s:iterator value="pmClQuesnaireResultHeader.quesResultMarkList" id="objRMark" status="indexRmark">
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
				
				<!-- 问卷结果 -->				
				<div class="panel panel-default">
				   		<div class="panel-heading">
				      		请提交问卷结果
						</div>
						<s:form method="post" action="" cssClass="form-inline" id="seeScoreForm">							
							<input type="hidden" id="projectTaskId" value='<s:property value="project.taskId"/>' name="workflowCommonParam.taskId"/>
							<input type="hidden" name="project.projectId" value="<s:property value='project.projectId'/>"/>
							<input type="hidden" name="pmClosedLoopResultType" value="2"/>
							<input type="hidden" name="pmClQuesnaireResultHeader.id" value="<s:property value='pmClQuesnaireResultHeader.id'/>"/>
							<div class="form-group form-group-query" style="margin-top:10px;">
								<label for="seeScoreEvaluationComment" style="width: 90px;float:left;"><span class="redmark" style="color:black;">*</span><s:text name="审核备注" /></label>
								<textarea id="seeScoreEvaluationComment" name="pmClEvaluationHeader.evaluationComment" class="form-control" rows="3"  style="float:left; width: 350px;"></textarea>
								
								<div class="form-group form-group-query" style="margin-top:10px;">
									<div id="cbResultRadio">								
										<label class="checkbox-inline" style="margin-left:80px;margin-top:20px;">
								      		<input type="radio" name="pmClEvaluationHeader.evaluationResult" id="seeEvaluationResultNoYes" 
								         value="1" checked> <s:text name="pm.cl.cbAgree"></s:text>
								   		</label>
								   		<label class="checkbox-inline" style="margin-right:4px;margin-top:20px;">
								      		<input type="radio" name="pmClEvaluationHeader.evaluationResult" id="seeEvaluationResultNo" 
								         	value="-1"> <s:text name="pm.cl.cbReject"></s:text>
								   		</label>
							   		</div>
								<div class="btn-group btn-group-sm" style="margin-left:80px;margin-top:20px;">
								<s:if test="pmClQuesnaireResultHeader.quesMarkResult==-1">
									 <button value="addSeeScoreButton" checkButton='check' type="button" class="btn btn-info" style="margin-right:4px;">
									  	<span class="glyphicon glyphicon-ok" style="font-size:12px;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="pm.cl.cbReject"></s:text></span>
									  </button>
								</s:if><s:else>
									 <button value="addSeeScoreButton" checkButton='check' type="button" class="btn btn-info" style="margin-right:4px;">
									  	<span class="glyphicon glyphicon-ok" style="font-size:12px;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="pm.cl.submitApprove"></s:text></span>
									  </button>
								</s:else>	
								</div>
							</div>
						</div>
					</s:form>
				</div>
			</s:if>
				
			 </div>
	      </div>
	   </div>
	</div>
	</div>

</s:if>
		
	</div>	
			
	
</body>
</html>