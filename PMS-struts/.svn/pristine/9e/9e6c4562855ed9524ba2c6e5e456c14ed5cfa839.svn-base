<%@page import="com.dp.plat.context.UserContext"%>
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
<style type="text/css">
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
</style>
<script type="text/javascript">
var lastQuestionDivId="";
var QuestionDivIdArr=new Array("questionnaireHeaderDivCheckOne","","questionnaireHeaderDivQuesAnw","");
var optName=new Array("A","B","C","D","E","F","G","H","I","J","K","L","M","N");
var totalLineScore;
	$(function(){
		$("div.headerMenu").each(function(){ 
			$(this).mouseover(function(){
				headerMenuMouseover(this);
			});	
			$(this).mouseout(function(){
				headerMenuMouseout(this);
			});	
		});
		
		$("button[checkButton='check']").each(function(){ 
			$(this).click(function(){
				pccAddSubmit(this);
			});	
		}); 
		
		$("a.pclAddQuesOption").each(function(){   
			$(this).click(function(){
				pclAddQuesOptionClick();
			});	
		});
		
		$("a.pclDeleteQuesOption").each(function(){   
			$(this).click(function(){
				pclDeleteQuesOptionClick();
			});	
		});
		
	/* 	$("#questionContent_one").val(""); */
		
		lastQuestionDivId=QuestionDivIdArr[0];
		$("#questionnaireHeaderDivQuesAnw").hide();
		$("button[checkButton='questionType']").each(function(){
			switch($(this).val()){
			case "1":
				$(this).click(function(){
					questionAddDivShow(this,QuestionDivIdArr[0]);
					$("button[checkButton='questionType']").each(function(){
						$(this).removeAttr("disabled","disabled");
					});
					$(this).prop("disabled","disabled");
				});	
				$(this).click();
				break;
			case "2":
				break;
			case "3":
				$(this).click(function(){
					questionAddDivShow(this,QuestionDivIdArr[2]);
					$("button[checkButton='questionType']").each(function(){
						$(this).removeAttr("disabled","disabled");
					});
					$(this).prop("disabled","disabled");
				});	
				break;
			case "4":
				break;
			default:
					break;
			}
			if($(this).val()=="<s:property value='pmClosedLoopQuesnaireLine.questionType'/>"){
				$(this).click();
			}
		});
		
		initializeFun();
		
	});
	
	function initializeFun(){ 
		totalLineScore=parseInt("<s:property value='returnTotalLineScore'/>")-parseInt("<s:property value='pmClosedLoopQuesnaireLine.questionScore'/>");
		if("<s:property value='doType'/>"=="2"){	//编辑
			var questionType="<s:property value='pmClosedLoopQuesnaireLine.questionType'/>";
			$("#questionContent_"+questionType).val("<s:property value='pmClosedLoopQuesnaireLine.questionContent'/>");	
			$("#questionScore_"+questionType).val("<s:property value='pmClosedLoopQuesnaireLine.questionScore'/>");

			var optLength="<s:property value='pmClosedLoopQuesnaireOptList.size()'/>";			
			for(var i=0;i<optLength;i++){
				if(i>1){
					pclAddQuesOptionClick();
				}
				$("#opt_"+i+"_content").val($("#optList_"+i+"_content").val());
				$("#opt_"+i+"_score").val($("#optList_"+i+"_score").val());
				
			}
			
		}
	}
	
	var lastOptId=1;
	function pclAddQuesOptionClick(){
		lastOptId++;
	  	$("#questionnaireHeaderDivCheckOne").append($("#opt_1").clone(true));
	  	$(".opt_clone").last().attr("id","opt_"+lastOptId); 
	  	$("#opt_"+lastOptId+" input").first().attr("id","opt_"+lastOptId+"_content");
	  	$("#opt_"+lastOptId+" input").first().attr("name","pmClosedLoopQuesnaireOptList["+lastOptId+"].questionOptionsContent");
	  	
	  	$("#opt_"+lastOptId+" input").last().attr("id","opt_"+lastOptId+"_score");
	  	$("#opt_"+lastOptId+" input").last().attr("name","pmClosedLoopQuesnaireOptList["+lastOptId+"].questionOptionScore");
	  	
	  	$("#opt_"+lastOptId+" span").first().next().text("选项"+optName[lastOptId]);
	}
	
	function pclDeleteQuesOptionClick(){
		if(lastOptId>1){
			var optId="opt_"+lastOptId;
			lastOptId--;
			$("#"+optId).remove();
		}	
	}
	
	function questionAddDivShow(this_obj,questionDivId){
		$("#"+questionDivId).show();
		if(lastQuestionDivId!=questionDivId){
			$("#"+lastQuestionDivId).hide();
			lastQuestionDivId=questionDivId;
		}
		$("#questionTypeVal").val($(this_obj).val());
	}
	
	function pccAddSubmit(this_obj){
		$("#questionnaireStatusVal").val($(this_obj).val());	//状态设置
		$("button[checkButton='check']").each(function(){ 
			$(this).attr("disabled","disabled");
		}); 
		var questionTypeVal=$("#questionTypeVal").val();
		var checkSubmitInput=QuestionDivIdArr[(parseInt(questionTypeVal)-1)];
		var regex=/^\s*$/;
		var isSubmit=false;
		$("input[checkData='"+checkSubmitInput+"']").each(function(){ 
			if(!checkSubmitValue($(this).val(),regex,new Array(1,0),"问卷内容","不能为空！","quesWarObj")){
				$("button[checkButton='check']").each(function(){ 
					$(this).removeAttr("disabled");
				}); 
				isSubmit=true;
				return false;
			}
		});
		
		if(isSubmit){
			return false;
		}
		
		regex=/^\d+[\.\d{2}]*$/;
		if($("#questionScore_"+questionTypeVal).val()!=""){
			if(!checkSubmitValue($("#questionScore_"+questionTypeVal).val(),regex,new Array(0,1),"问题分数","为整数或两位小数！","quesWarObj")){
				$("button[checkButton='check']").each(function(){ 
					$(this).removeAttr("disabled");
				}); 
				return false;
			}
		}else{
			$("#questionScore_"+questionTypeVal).val(0);
		}
		
		if(questionTypeVal!=3){
			for(var i=0;i<=lastOptId;i++){
				if($("#opt_"+i+"_score").val()!=""){
					if(parseInt($("#opt_"+i+"_score").val())>parseInt($("#questionScore_"+questionTypeVal).val())){
						$("button[checkButton='check']").each(function(){ 
							$(this).removeAttr("disabled");
						});
						$("#quesWarObj").text("");
						$("#quesWarObj").text("选项分数不能大于问题分数！");
						isSubmit=true;
						return false;
					}
					if(!checkSubmitValue($("#opt_"+i+"_score").val(),regex,new Array(0,1),"选项分数","为整数或两位小数！","quesWarObj")){
						$("button[checkButton='check']").each(function(){ 
							$(this).removeAttr("disabled");
						});
						isSubmit=true;
						return false;
					}
				}else{
					$("#opt_"+i+"_score").val(0);
				}
			}
		}

		if(isSubmit){
			return false;
		}
		totalLineScore+=parseInt($("#questionScore_"+questionTypeVal).val());
		if(totalLineScore>parseInt("<s:property value='pmClosedLoopQuesnaire.questionnaireScore'/>")){
			totalLineScore-=parseInt($("#questionScore_"+questionTypeVal).val());
			$("button[checkButton='check']").each(function(){ 
				$(this).removeAttr("disabled");
			}); 
			alert("问卷已添加问题分数（"+totalLineScore+"）不能大于问卷总分（"+parseInt("<s:property value='pmClosedLoopQuesnaire.questionnaireScore'/>")+"）");
			return false;
		}
		
		$("button[checkButton='questionType']").each(function(){
			if(questionTypeVal!=$(this).val()){
				if(QuestionDivIdArr[(parseInt($(this).val())-1)]){
					$("#"+QuestionDivIdArr[(parseInt($(this).val())-1)]).empty();
				}
			}
		});
		$("#pmClosedLoopQuesnaireLineForm").submit();
	}
	
	function checkSubmitValue(submitValue,regex,checkType,waringText1,waringText2,waringObjId){
		if(checkType[0]==1){
			var regexIsEmpty=/^\s*$/;
			if(regexIsEmpty.test(submitValue)){ //非空验证
				$("#"+waringObjId).text("");
				$("#"+waringObjId).text(waringText1+"不能为空！");
				return false;
			}
		}
		
		if(checkType[1]==1){
			if(!regex.test(submitValue)){ //字段格式验证
				$("#"+waringObjId).text("");
				$("#"+waringObjId).text(waringText1+waringText2);
				return false;
			}else{
				$("#"+waringObjId).text("");
				 return true;
			}
		}else{
			$("#"+waringObjId).text("");
			return true;
		}
			
	}
	
</script>
</head>
<body>  
	<div>
			<div>
				<span style="font-size:18px;" class="headerSpan"><s:property value="pmClosedLoopQuesnaire.questionnaireTemplateName"/></span>
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
						<span><s:text name="pm.cl.quesTyle"></s:text>:<s:property value="pmClosedLoopQuesnaire.quesTypeName"/></span>
					</li>
				</ul>
			</div>
		</div>
			
			
			
			
			
		<s:form method="post" action="module/sub/PmClQuesSub_submitLine.action" id="pmClosedLoopQuesnaireLineForm">
				<input id="questionnaireStatusVal" name="pmClosedLoopQuesnaire.questionnaireStatus"  type="hidden">
				<div class="panel panel-default">
				   <div class="panel-heading">
				      选择题目类型
				   </div>
				   <div class="panel-body">
				  		<div class="" style="height:50px;margin-bottom:0px;border-radius: 10px 10px 0px 0px;">
							<div class="btn-group btn-group-sm"  style="margin-top:4px;">
							  <button value="1" checkButton="questionType" type="button" class="btn btn-default" style="margin-right:4px;">
							  	<span class="glyphicon glyphicon-record"  style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;单选</span>
							  </button>
						</div>
						<%--  <div class="btn-group btn-group-sm"  style="margin-top:4px;">
							  <button value="2" checkButton="questionType" type="button"  class="btn btn-default btn-sm" style="margin-right:4px;">
							  	<span class="glyphicon glyphicon-check" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;多选</span>
							  </button>
						</div> --%>
						
						 <div class="btn-group btn-group-sm"  style="margin-top:4px;">
							  <button value="3" checkButton="questionType" type="button"  class="btn btn-default btn-sm" style="margin-right:4px;">
							  	<span class="glyphicon glyphicon-text-height" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;问答</span>
							  </button>
						</div>
						
						<%--  <div class="btn-group btn-group-sm"  style="margin-top:4px;">
							  <button value="4" checkButton="questionType" type="button"  class="btn btn-default btn-sm" style="margin-right:4px;">
							  	<span class="glyphicon glyphicon-star" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;评分</span>
							  </button>
						</div> --%>
				   	 </div>
				   </div>
				</div>
				
				 <input type="hidden" name="pmClosedLoopQuesnaireLine.quesnaireTemplateHeaderId" value="<s:property value='pmClosedLoopQuesnaire.id'/>"/>
				 <input type="hidden"  value="<s:property value='pmClosedLoopQuesnaireLine.id'/>"  name="pmClosedLoopQuesnaireLine.id"/>
				 <input type="hidden"  value="<s:property value='doType'/>"  name="doType"/>
				 <input id="questionTypeVal" type="hidden"  value="1" name="pmClosedLoopQuesnaireLine.questionType"/>
				 <input id="questionNumVal" type="hidden" value="<s:property value='pmClosedLoopQuesnaireLine.questionNum'/>" name="pmClosedLoopQuesnaireLine.questionNum"/>
				
				<div class="panel panel-default">
				
				<div class="panel-heading" style="margin-top: -20px;">
				     第<s:property value='pmClosedLoopQuesnaireLine.questionNum'/>题
				   </div>
				   <div class="panel-body">
					   <div id="questionnaireHeaderDiv" class="" style="margin-top:0px;">						   
					      	<div class="form-group form-group-query" >						
								<div class="form-group form-group-query">
									<label for="questionTypeForCB" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.cl.quesLineTypeCB" /></label>
							    	<select  id="questionTypeForCB" name="pmClosedLoopQuesnaireLine.questionTypeForCB"  class="form-control" style="width: 200px;display: inline-block;" >
							    		<option value="0">--请选择--</option>
							    		<s:iterator value="quesLineTypeList" id="objType" status="indexType">
							    			<s:if test="#objType.basicDataId==pmClosedLoopQuesnaireLine.questionTypeForCB">
							    				<option selected="selected" value='<s:property value="%{#objType.basicDataId}"/>'><s:property value="%{#objType.basicDataName}"/></option>
							    			</s:if><s:else>
							    				<option value='<s:property value="%{#objType.basicDataId}"/>'><s:property value="%{#objType.basicDataName}"/></option>
							    			</s:else>
							    		</s:iterator>										    	
							    	</select>
								</div>										
						     </div>
					      	<div id="questionnaireHeaderDivCheckOne">
					      		<div class="form-group form-group-query" >				
							     	<div class="form-group form-group-query">
										<label for="questionContent_1" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.cl.quesLineContent" /></label>
								    	<input  id="questionContent_1" placeholder="问题描述"  name="pmClosedLoopQuesnaireLine.questionContent"  class="form-control" style="width: 370px;display: inline-block;" />
									</div>
							     </div>
							     
							     <div class="form-group form-group-query" >				
							     	<div class="form-group form-group-query">
										<label for="questionScore_1" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.cl.questionScore" /></label>
								    	<input  id="questionScore_1" placeholder="问题分数"  name="pmClosedLoopQuesnaireLine.questionScore"  class="form-control" style="width: 370px;display: inline-block;" />
									</div>
							     </div>
					      	
					      		<div id="opt_0" class="opt_clone">
								    <div class="form-group form-group-query" >
										<label for="" style="width: 90px;"><span class="redmark">*</span><span>选项A</span></label>
								     	<div class="input-group">
								     		<label for="" style="width: 38px;float:left;"></label>
									         <span class="input-group-addon"><s:text name="pm.cl.quesOptContent"></s:text></span>
									         <input id="opt_0_content" checkData="questionnaireHeaderDivCheckOne" name="pmClosedLoopQuesnaireOptList[0].questionOptionsContent" type="text" class="form-control" style="width: 300px;" placeholder="内容">
									   </div>
									   <div class="input-group">
									   		<label for="" style="width: 38px;float:left;"></label>
									         <span class="input-group-addon"><s:text name="pm.cl.quesOptScore"></s:text></span>
									         <input id="opt_0_score" type="text" name="pmClosedLoopQuesnaireOptList[0].questionOptionScore" class="form-control" placeholder="分数" style="width: 250px;">
									         <span class="input-group-addon" style="float: left;width: 50px;height: 34px;">
									   		  <a class="pclAddQuesOption" href="javascript:void(0)">
									   			 <span class="glyphicon glyphicon-plus"  style="font-size:8px; color:#428bca;"></span>
									   		  </a>
									   		  <a class="pclDeleteQuesOption" href="javascript:void(0)">
									   			 <span class="glyphicon glyphicon-remove"  style="font-size:8px; color:#428bca;"></span>
									           </a>
									        </span>
									   </div>
								     </div>
							     </div>
							     
							     <div id="opt_1" class="opt_clone">
								    <div class="form-group form-group-query" >
										<label for="questionnaireTemplateNameVal" style="width: 90px;"><span class="redmark">*</span><span>选项B</span></label>
								     	<div class="input-group" style="">
								     		<label for="questionnaireTemplateNameVal" style="width: 38px;float:left;"></label>
									         <span class="input-group-addon"><s:text name="pm.cl.quesOptContent"></s:text></span>
									         <input id="opt_1_content" checkData="questionnaireHeaderDivCheckOne" name="pmClosedLoopQuesnaireOptList[1].questionOptionsContent" type="text" class="form-control" style="width: 300px;" placeholder="内容">
									   </div>
									   <div class="input-group">
									   		<label for="questionnaireTemplateNameVal" style="width: 38px;float:left;"></label>
									         <span class="input-group-addon"><s:text name="pm.cl.quesOptScore"></s:text></span>
									         <input id="opt_1_score" name="pmClosedLoopQuesnaireOptList[1].questionOptionScore" type="text" class="form-control" placeholder="分数" style="width: 250px;">
									         <span class="input-group-addon" style="float: left;width: 50px;height: 34px;">
									   		  <a class="pclAddQuesOption" href="javascript:void(0)">
									   			 <span class="glyphicon glyphicon-plus"  style="font-size:8px; color:#428bca;"></span>
									   		  </a>
									   		  <a class="pclDeleteQuesOption" href="javascript:void(0)">
									   			 <span class="glyphicon glyphicon-remove"  style="font-size:8px; color:#428bca;"></span>
									           </a>
									        </span>
									    </div>
								    </div>       										   
							     </div>
						    </div>							    
				      	<div id="questionnaireHeaderDivQuesAnw">
				      	 	<div class="form-group form-group-query" >				
						     	<div class="form-group form-group-query">
									<label for="questionScore_3" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.cl.questionScore" /></label>
							    	<input  id="questionScore_3" placeholder="问题分数"  name="pmClosedLoopQuesnaireLine.questionScore"  class="form-control" style="width: 344px;display: inline-block;" />
								</div>
							</div>
					      	<label for="questionContent_3" style="width: 90px;float:left;"><span class="redmark">*</span><s:text name="pm.cl.quesLineContent" /></label>
							<textarea id="questionContent_3" name="pmClosedLoopQuesnaireLine.questionContent" class="form-control" rows="3"  style="float:left; width: 350px;"></textarea>						      	
				      	</div>
				      	<span id="quesWarObj"  style="color:red; font-size:16px;"></span>
		    			</div>
				   </div>			
				</div>
				
				<div id="optListValue">
					<s:iterator value="pmClosedLoopQuesnaireOptList" id="optObj" status="optIndex">
						<input id="optList_<s:property value='%{#optIndex.index}'/>_content" type="hidden" value="<s:property value='%{#optObj.questionOptionsContent}'/>"/>
						<input id="optList_<s:property value='%{#optIndex.index}'/>_score"  type="hidden" value="<s:property value='%{#optObj.questionOptionScore}'/>"/>
					</s:iterator>
				</div>
				         
				
	    		 <div class="btn-group btn-group-sm"  style="margin-left:108px;margin-top:4px;">
					  <button value="-1" checkButton="check" type="button" class="btn btn-info" style="margin-right:4px;">
					  	<span class="glyphicon glyphicon-ok"  style="font-size:12px;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="pm.cl.quesDraft"></s:text></span>
					  </button>
				</div>								    	
	    </s:form>

</body>
</html>