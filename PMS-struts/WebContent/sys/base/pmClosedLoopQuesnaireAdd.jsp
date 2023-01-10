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
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.basemanage' />">
<meta name="function" content="<s:text name='pm.cl.quesMana' />">
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
		 
		$("button[checkButton='check']").each(function(){ 
			$(this).click(function(){
				pccAddSubmit(this);
			});	
		}); 
		
		initializeFun();
		
	});
	
	function initializeFun(){
		var d=new Date();
		var dString=d.getFullYear()+"年"+(d.getMonth()+1)+"月"+d.getDate()+"日";
		$("#pccCreatedTime").text("创建时间："+dString);
		var realName='<%String realNameStr=UserContext.getUserContext().getUser().getRealName(); out.print(realNameStr);%>';
		$("#pccCreatePerson").text("创建人姓名："+realName);
	}
	
	function pccAddSubmit(this_obj){
		$("#questionnaireStatusVal").val($(this_obj).val());	//状态设置
		$("button[checkButton='check']").each(function(){ 
			$(this).attr("disabled","disabled");
		}); 
		
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
			$("#quesWar").text("计分规则不能为空哦！");
			$("button[checkButton='check']").each(function(){ 
				$(this).removeAttr("disabled");
			}); 
			return false;
		}
		
		var regex=/^\s*$/;
		if(!checkSubmitValue($("#questionnaireTemplateNameVal").val(),regex,false,"问卷名称","不能为空！","quesWar")){
			$("button[checkButton='check']").each(function(){ 
				$(this).removeAttr("disabled");
			}); 
			return false;
		}
		regex=/^\d+[\.\d{2}]*$/;
		if(!checkSubmitValue($("#questionnaireScoreVal").val(),regex,true,"问卷分数","为整数或两位小数！","quesWar")){
			$("button[checkButton='check']").each(function(){ 
				$(this).removeAttr("disabled");
			});
			return false;
		}
		if(!checkSubmitValue($("#questionnairePassScoreVal").val(),regex,true,"问卷达标分数","为整数或两位小数！","quesWar")){
			$("button[checkButton='check']").each(function(){ 
				$(this).removeAttr("disabled");
			});
			return false;
		}
		
		if(parseInt($("#questionnaireScoreVal").val())<parseInt($("#questionnairePassScoreVal").val())){
			$("#quesWar").text("问卷达标分数不能大于问卷总分！");
			$("button[checkButton='check']").each(function(){ 
				$(this).removeAttr("disabled");
			});
			return false;
		}

		$("#pmClosedLoopQuesnaireForm").submit();
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

	
</script>
</head>
<body>
	<div class="panel panel-default">
	   <div class="panel-heading">
		     <span><img src="images/right_zhishi.gif" border="0" ></span>
			<s:text name="pm.cl.quesAdd"></s:text>
	   </div>
	   <div class="panel-body">	
		 <div>
	    	<s:form method="POST" action="base/SubmitPmClLQues.action" id="pmClosedLoopQuesnaireForm">
				<input id="questionnaireStatusVal" name="pmClosedLoopQuesnaire.questionnaireStatus" type="hidden">
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
				    		<option value="<s:property value='%{#objd.basicDataId}'/>"><s:property value="%{#objd.basicDataName}"/></option>
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
		      	
		      	<span id="quesWar"  style="color:red; font-size:16px;"></span>
				
				<div  class="form-group form-group-query">
		    		  <label for="quesTypeSelect" style="width: 90px;"></label>
					  <button value="-1" checkButton="check" type="button" class="btn btn-info" >
					  	<span class="glyphicon glyphicon-ok"  style="font-size:12px;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="pm.cl.quesDraft"></s:text></span>
					  </button>
				</div>
	    </s:form>
    	</div>			
	   </div>
	</div>
</body>
</html>