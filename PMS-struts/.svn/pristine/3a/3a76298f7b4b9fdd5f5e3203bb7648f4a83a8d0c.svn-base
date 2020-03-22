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
<meta name="group" content="<s:text name='sys.leftmenu.powermanage' />">
<meta name="function" content="<s:text name='pm.cl.quesMana' />">
<style type="text/css">	
	.pccSubmitDiv{
	margin-top: 10px;
	/* background-color: bisque; */
	/* background-color: aliceblue; */
	/* text-align: center; */
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
var totalLineScore=0;
	$(function(){
		$("button[checkButton='check']").each(function(){ 
			$(this).click(function(){
				pccAddSubmit(this);
			});	
		}); 
		$("input.checkQuesLine").each(function(){
			$(this).click(function(){
				clickCheckQuesLine(this);
			});
		});
		var markChecked="<s:property value='pmClosedLoopQuesnaire.markIndexs'/>";
		if(markChecked){
			$("#markhide").val(markChecked);			
			
			if(markChecked.indexOf(",")){
				var checkedVal=markChecked.split(",");
				for(var i=0;i<checkedVal.length;i++){
					$("input.markCheckbox").each(function(){						
						if($(this).val()==checkedVal[i]){
							$(this).attr("checked","checked");
						}
					});
				}
			}else{
				$("input.markCheckbox").each(function(){						
					if($(this).val()==markChecked){
						$(this).attr("checked","checked");
					}
				});
			}
		}
		var lineLength=parseInt("<s:property value='pmClosedLoopQuesnaireLineList.size()'/>");
		for(var i=0;i<lineLength;i++){
			totalLineScore+=parseInt($("#questionScoreVal_"+i).text());
		}
		$("#totalLineScore").text(totalLineScore);
		$("#addNextLineButton").click(function(){
			if(totalLineScore>parseInt("<s:property value='pmClosedLoopQuesnaire.questionnaireScore'/>")){
				alert("问卷已添加问题分数已等于问卷总分，不能添加题目");
				return;
			}
			popWindow('module/sub/PmClQuesSub_addLine.action?pmClosedLoopQuesnaire.id=<s:property value='pmClosedLoopQuesnaire.id'/>', 1000, 650,'<%=StringEscUtil.getText("pm.cl.nextQuesLine") %>', 'BudgetUpload', true);
		});
	});
	
	function clickCheckQuesLine(this_obj){
		if($(this_obj).attr("checked")=="checked"){
			$("input.checkQuesLine").each(function(){
				$(this).removeAttr("checked");
			});
			$(this_obj).prop("checked","checked");
			$("#selectLineId").val($(this_obj).val());
		}else{
			$("#selectLineId").val("");
		}
	}
	
	function pccAddSubmit(this_obj){
		$("button[checkButton='check']").each(function(){ 
			$(this).attr("disabled","disabled");
		});
		
		if($(this_obj).attr("id")=="effectiveButton"){
			$("#effectiveQuesForm").submit();
		}else if($(this_obj).attr("id")=="deleteButton"){ 
			if(checkQuesSelect()){
				$("#deleteLineForm").submit();
			}else{
				return false;
			}
			/* if(!$("#selectLineId").val()){
				$("button[checkButton='check']").each(function(){ 
					$(this).removeAttr("disabled","disabled");
				});
				return false;
			} */
		}else if($(this_obj).attr("id")=="editButton"){
			if(!checkQuesSelect()){
				return false;
			}
			if($("#selectLineId").val()){
				popWindow('module/sub/PmClQuesSub_addLine.action?doType=2&pmClosedLoopQuesnaireLine.id='+$("#selectLineId").val()+'', 1000, 650,'<%=StringEscUtil.getText("pm.cl.editQuesLine") %>', 'BudgetUpload', true);
			}
		}else{
			if(checkQues(this_obj)){
				$("#pmClosedLoopQuesnaireForm").submit();
			}else{
				return false;
			}
		}
		
		return true; 
	}
	
	function checkSubmitValue(submitValue,regex,isFormatCheck,waringText1,waringText2,waringObjId){
		var regexIsEmpty=/^\s*$/;
		if(regexIsEmpty.test(submitValue)){ //非空验证
			$("#"+waringObjId).text(waringText1+"不能为空！");
			return false;
		}else{
			if(isFormatCheck){
				if(!regex.test(submitValue)){ //字段格式验证
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
	}
	
	function checkQues(this_obj){
		$("#questionnaireStatusVal").val($(this_obj).val());	//状态设置
		$("input.markCheckbox:checked").each(function(){
			if($(this).val()){
				if(!$("#markIndexsVal").val()){
					$("#markIndexsVal").val($(this).val());
				}else{
					$("#markIndexsVal").val($("#markIndexsVal").val()+","+$(this).val());
				}
			}
		});
		
		if(!$("#markIndexsVal").val()){
			$("#questionnaireTemplateNameValWar").text("计分规则不能为空哦！");
			$("button[checkButton='check']").each(function(){ 
				$(this).removeAttr("disabled");
			}); 
			return false;
		}
		var regex=/^\s*$/;
		if(!checkSubmitValue($("#questionnaireTemplateNameVal").val(),regex,false,"问卷名称","不能为空！","questionnaireTemplateNameValWar")){
			$("button[checkButton='check']").each(function(){ 
				$(this).removeAttr("disabled");
			}); 
			return false;
		}
		regex=/^\d+[\.\d{2}]*$/;
		if(!checkSubmitValue($("#questionnaireScoreVal").val(),regex,true,"问卷分数","为整数或两位小数！","questionnaireTemplateNameValWar")){
			$("button[checkButton='check']").each(function(){ 
				$(this).removeAttr("disabled");
			});
			return false;
		}
		if(!checkSubmitValue($("#questionnairePassScoreVal").val(),regex,true,"问卷达标分数","为整数或两位小数！","questionnaireTemplateNameValWar")){
			$("button[checkButton='check']").each(function(){ 
				$(this).removeAttr("disabled");
			});
			return false;
		}
		if(parseInt($("#questionnaireScoreVal").val())<parseInt($("#questionnairePassScoreVal").val())){
			$("#questionnaireTemplateNameValWar").text("问卷达标分数不能大于问卷总分！");
			$("button[checkButton='check']").each(function(){ 
				$(this).removeAttr("disabled");
			});
			return false;
		}
		return true;
	}
	
	function checkQuesSelect(){
		var selectNum = $("input[class='checkQuesLine']:checked").length;
		if(selectNum != 1){
			if(selectNum>1){
				alert("一个一个慢慢来");
			}else if(selectNum == 0){
				alert("至少选择一个");
			}
			$("button[checkButton='check']").each(function(){ 
				$(this).removeAttr("disabled","disabled");
			});
			return false;
		}
		$("#selectLineId").val(checkDeleteLineID());
		$("button[checkButton='check']").each(function(){ 
			$(this).removeAttr("disabled","disabled");
		});
		return true;
	}
	
	function checkDeleteLineID(){
		var selectLineIdList = [];
		$("input[class='checkQuesLine']:checked").each(function(){
			selectLineIdList.push($(this).val());
		});
		var selectLineIdStr = selectLineIdList.join(",");
		return selectLineIdStr;
	}
	/* setInterval(checkDeleteLineID, 1000); */
</script>
</head>
<body>

	<div class="panel panel-default">
	   <div class="panel-heading">
		     <span><img src="images/right_zhishi.gif" border="0" ></span>
			<s:text name="pm.cl.quesEdit"></s:text>
	   </div>
	   <div class="panel-body">	
		 <div>
	    	<s:form method="POST" action="base/PmClQues_updateQues.action" id="pmClosedLoopQuesnaireForm">
	    		<input name="pmClosedLoopQuesnaire.id" value="<s:property value='pmClosedLoopQuesnaire.id'/>" type="hidden">
				
				<div class="form-group form-group-query">
					<label for="questionnaireTemplateNameVal" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.cl.questionnaireName" /></label>
			    	<s:textfield  id="questionnaireTemplateNameVal" placeholder="问卷名称"  name="pmClosedLoopQuesnaire.questionnaireTemplateName"  cssClass="form-control" cssStyle="width: 200px;display: inline-block;" />
				</div>
				
				<div class="form-group form-group-query">
					<label for="questionnaireScoreVal" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.cl.questionnaireScore" /></label>
			    	<s:textfield id="questionnaireScoreVal" placeholder="问卷分数"   name="pmClosedLoopQuesnaire.questionnaireScore" cssClass="form-control" cssStyle="width: 200px;display: inline-block;" />
				</div>  
				
				<div class="form-group form-group-query">
					<label for="questionnairePassScoreVal" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.cl.questionnairePassScore" /></label>
			    	<s:textfield id="questionnairePassScoreVal" placeholder="问卷达标分数"   name="pmClosedLoopQuesnaire.questionnairePassScore" cssClass="form-control" cssStyle="width: 200px;display: inline-block;" />
				</div>  
			
				<div  class="form-group form-group-query">
			       <label for="quesTypeSelect" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.cl.quesTyle" /></label>
			       <select name="pmClosedLoopQuesnaire.quesType" id="quesTypeSelect" class="form-control" style="width: 200px;display: inline-block;">
			    		<s:iterator value="quesTypeList" id="objd" status="indexd">
			    			<s:if test="#objd.basicDataId==pmClosedLoopQuesnaire.quesType">
			    				<option selected="selected" value="<s:property value='%{#objd.basicDataId}'/>"><s:property value="%{#objd.basicDataName}"/></option>
			    			</s:if><s:else>
			    				<option value="<s:property value='%{#objd.basicDataId}'/>"><s:property value="%{#objd.basicDataName}"/></option>
			    			</s:else>
			    		</s:iterator>
			    	</select>
		      	</div>
		      	
		      	<div class="form-group form-group-query">
				    <label for=""  style="width: 90px;float:left;" ><span class="redmark">*</span><s:text name="pm.cl.markRule"></s:text></label>
						<s:iterator value="markList" var="v" status="index">
							<s:if test="#index.index>0">
								<label for=""  style="width: 90px;float:left;" >&nbsp;</label>
							</s:if>
						 	
							<input type="checkbox" class="markCheckbox" id="mark_<s:property value='#index.index'/>"  value="<s:property value='#index.index'/>" /><label style="font-weight:600;" for="mark_<s:property value='#index.index'/>"><s:property value="#v" /></label><br/>
						</s:iterator>				
				    <input type="hidden" name="pmClosedLoopQuesnaire.markIndexs" id="markIndexsVal"  value=""/>
			    </div>
				
		      	<span id="questionnaireTemplateNameValWar"  style="color:red; font-size:16px;"></span>

				<div  class="form-group form-group-query">
		    		  <label for="" style="width: 90px;"></label>
					  <button value="-1" checkButton="check" type="button" class="btn btn-info" >
					  	<span class="glyphicon glyphicon-ok"  style="font-size:12px;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="pm.cl.quesDraft"></s:text></span>
					  </button>
				</div>
	    </s:form>
    	</div>			
	   </div>
	</div>
	<div class="panel-group" style="margin-top: -14px;" id="claccordion">
	   <div class="panel panel-default">
	      <div class="panel-heading">
             <span><img src="images/right_zhishi.gif" border="0" ></span>
  			 <s:text name="pm.cl.quesLineEdit"></s:text>
	      </div>
	         <div class="panel-body">
	         	<s:if test="pmClosedLoopQuesnaireLineList.size()==0">	         	
	         		<div  class="form-group form-group-query">		    		  
					  <button  onclick="javascript:popWindow('module/sub/PmClQuesSub_addLine.action?pmClosedLoopQuesnaire.id=<s:property value='pmClosedLoopQuesnaire.id'/>', 1000, 650,'<%=StringEscUtil.getText("pm.cl.quesLineAdd") %>', 'BudgetUpload', true);" value="" type="button" class="btn btn-default" style="margin-right:4px;">
					  	<span class="glyphicon glyphicon-plus" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name='pm.cl.quesLineAdd'/></span>
					  </button>
					</div>
	         	</s:if>	
	         	<s:if test="pmClosedLoopQuesnaireLineList==null">
	         		<div  class="form-group form-group-query">		    		  
					  <button onclick="javascript:popWindow('module/sub/PmClQuesSub_addLine.action?pmClosedLoopQuesnaire.id=<s:property value='pmClosedLoopQuesnaire.id'/>', 1000, 650,'<%=StringEscUtil.getText("pm.cl.quesLineAdd") %>', 'BudgetUpload', true);" value="" type="button" class="btn btn-default" style="margin-right:4px;">
					  	<span class="glyphicon glyphicon-plus" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name='pm.cl.quesLineAdd'/></span>
					  </button>
					</div>
	         	</s:if>	
	         	
	         	<s:if test="pmClosedLoopQuesnaireLineList.size()>0">	         	 
	         		<div  class="form-group form-group-query">		    		  
					  <button id="addNextLineButton"  type="button" class="btn btn-default" style="margin-right:4px;">
					  	<span class="glyphicon glyphicon-plus" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name='pm.cl.nextQuesLine'/></span>
					  </button>
					  
					    <button id="deleteButton" checkButton="check" type="button" class="btn btn-default" style="margin-right:4px;">
					  		<span class="glyphicon glyphicon-minus" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name='pm.cl.deleteQuesLine'/></span>
					   </button>
					  
					    <button id="editButton"  checkButton="check" type="button" class="btn btn-default" style="margin-right:4px;">
					  		<span class="glyphicon glyphicon-pencil" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name='pm.cl.editQuesLine'/></span>
					  </button>
					  <span>（已添加问题总分：<span id="totalLineScore"></span>）</span>
					</div>
	         	</s:if>	 
	         	<s:form method="POST" action="base/PmClQues_deleteLine.action" id="deleteLineForm">
	         		<input type="hidden" name="pmClosedLoopQuesnaireLine.id" id="selectLineId"/>
	         	</s:form>
				<s:iterator value="pmClosedLoopQuesnaireLineList" id="obja" status="indexa">
					<div class="pmclquescontent">
						<div class="content_pm_proplem">
							<div class="yl_header">
							<table border="0" cellpadding="0" cellspacing="0" width="100%">
							<tbody>
							<tr>
							<td valign="top" style="width:26px;"><input name="pm" value="<s:property value='#obja.id'/>" type="checkbox" class="checkQuesLine"/></td>
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
									<s:iterator value="quesLineTypeList" id="objqt" status="indexqt">
										<s:if test="#obja.questionTypeForCB==#objqt.basicDataId">
											<span class="content_pm_proplem_type">[<s:property value="%{#objqt.basicDataName}"/>]</span>
										</s:if>
									</s:iterator>
									<span  class="content_pm_proplem_type">[<span id="questionScoreVal_<s:property value='%{#indexa.index}'/>"><s:property value="#obja.questionScore"/></span>分]</span>
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
													<td><input disabled="disabled" type="radio" value="<s:property value="%{#objOpt.id}"/>"  style="margin-right:10px;"></td>
													<td><label style="margin-right: 28px;"><s:property value="%{#objOpt.questionOptionsContent}"/><span style="font-weight: 100;font-size: 8px;" class="content_pm_proplem_type">（<s:property value="%{#objOpt.questionOptionScore}"/>分）</span></label>
													</td>											
											</s:if>
										</s:if>
									</s:iterator>
									<s:if test="#obja.questionType==3">									
											<td><textarea  disabled="disabled"  class="form-control" rows="3"  style="float:left; width: 350px;"></textarea></td>																	
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
   		
   		<div class="panel panel-default">
		   <div class="panel-heading">
		     	<span><img src="images/right_zhishi.gif" border="0" ></span>
  			 	<s:text name="pm.cl.quesEffective"></s:text>
		   </div>
		   <div class="panel-body">
		      <span class="glyphicon glyphicon-star" style="color:red;"></span>
		      <span>生效后，问卷基本信息以及问卷题目将不能进行任何增删改操作。<br/>&nbsp;&nbsp;&nbsp;&nbsp;如果该份问卷为闭环建议，系统将失效之前添加的所有闭环建议问卷，只保留该份问卷作为有效的闭环建议，请确认是否生效。</span>
		      <s:form method="POST" action="base/PmClQues_startEffective.action" id="effectiveQuesForm">
		      	  <input name="pmClosedLoopQuesnaire.id" value="<s:property value='pmClosedLoopQuesnaire.id'/>" type="hidden">
		      	  <input name="pmClosedLoopQuesnaire.quesType" value="<s:property value='pmClosedLoopQuesnaire.quesType'/>" type="hidden">
			      <div  class="form-group form-group-query" style="margin-top:10px;">
					  <button id="effectiveButton" value="1" checkButton="check" type="button" class="btn btn-info" >
					  	<span class="glyphicon glyphicon-ok"  style="font-size:12px;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="问卷生效"></s:text></span>
					  </button>
				  </div>
				</s:form>
		   </div>
		</div>
   		
   		
	</div>
</body>
</html>
