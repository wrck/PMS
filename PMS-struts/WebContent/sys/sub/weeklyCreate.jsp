<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<html>
<head>
<style type="text/css">
.table tr{
	 font-size: 12px; 
}
.form-control-ex{
	width: 87%;
	height: 34px;
	padding: 6px 12px;
	font-size: 12px;
	line-height: 1.42857143;
	color: #555;
	background-color: #fff;
	background-image: none;
	border: 1px solid #ccc;
	border-radius: 4px;
	-webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
	box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
	-webkit-transition: border-color ease-in-out .15s,-webkit-box-shadow ease-in-out .15s;
	-o-transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
	transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
}
	
</style>
<script type="text/javascript">
	$(function(){
		date_picker("startTime");
		date_picker("endTime");
		date_picker("weeklystartTime");
		date_picker("weeklyendTime");
		$("#weeklystartTime").val("<s:date name='projectWeekly.weeklyStartTime' format='yyyy-MM-dd' />");
		$("#weeklyendTime").val("<s:date name='projectWeekly.weeklyEndTime' format='yyyy-MM-dd' />");
	});
	
	//渲染序号
	function altTable(className){
		var index = 1;
		$("."+className+" tr").each(function() {
			if($(this).children().eq(0).text() != "序号"){
				$(this).children().eq(0).text(index);
				index +=1;
			}
		});
	}
	
	var workindex = 0;
	function moreWork(obj){
		if(workindex == 0){
			workindex = $(".workSize").size();
		}
		
		workindex ++;
		
		copy = "<tr class='workSize pmWork"+workindex+"'>";
		copy += $("#workCopy").html();
		copy += "</tr>";
		copy = copy.toString().replace(new RegExp('\\[0\\]','gm') ,workindex);
		$(".pmWork"+obj).after(copy);
		
		altTable("worktable");
	}
	
	var riskindex = 0;
	function moreRisk(obj){
		if(riskindex == 0){
			riskindex = $(".riskSize").size();
		}
		riskindex ++;
		copy = "<tr class='riskSize pmRisk"+riskindex+"'>";
		copy += $("#riskCopy").html();
		copy += "</tr>";
		copy = copy.toString().replace(new RegExp('\\[0\\]','gm') ,riskindex);
		$(".pmRisk"+obj).after(copy);
		
		altTable("risktable");
	}
	
	var helpindex = 0;
	function moreHelp(obj){
		if(helpindex == 0){
			helpindex = $(".helpSize").size();
		}
		helpindex ++;
		copy = "<tr class='helpSize pmHelp"+helpindex+"'>";
		copy += $("#helpCopy").html();
		copy += "</tr>";
		copy = copy.toString().replace(new RegExp('\\[0\\]','gm') ,helpindex);
		$(".pmHelp"+obj).after(copy);
		
		altTable("helptable");
	}
	
	var progressindex = 0;
	function moreProgress(obj){
		if(progressindex == 0){
			progressindex = $(".progressSize").size();
		}
		progressindex ++;
		copy = "<tr class='progressSize pmProgress"+progressindex+"'>";
		copy += $("#progressCopy").html();
		copy += "</tr>";
		copy = copy.toString().replace(new RegExp('\\[0\\]','gm') ,progressindex);
		$(".pmProgress"+obj).after(copy);
		
		altTable("progresstable");
	}
	var planindex = 0;
	function morePlan(obj){
		if(planindex == 0){
			planindex = $(".planSize").size();
		}
		planindex ++;
		copy = "<tr class='planSize pmPlan"+planindex+"'>";
		copy += $("#planCopy").html();
		copy += "</tr>";
		copy = copy.toString().replace(new RegExp('\\[0\\]','gm') ,planindex);
		$(".pmPlan"+obj).after(copy);
		
		altTable("plantable");
	}
	
	var mailindex = 0;
	function moreMail(obj){
		if(mailindex == 0){
			mailindex = $(".mailSize").size();
		}
		mailindex ++;
		copy = "<tr class='mailSize mail"+mailindex+"'>";
		copy += $("#mailCopy").html();
		copy += "</tr>";
		copy = copy.toString().replace(new RegExp('\\[0\\]','gm') ,mailindex);
		$(".mail"+obj).after(copy);
		
		altTable("mailtable");
	}
	
	function deleteItem(_this ,obj){
		$(_this).parent().parent().remove();
		altTable(obj);
	}
	
	function saveDraft(){
		var weeklystartTime = $("#weeklystartTime").val();
		var weeklyendTime = $("#weeklyendTime").val();
		var currentTask = $("#currentTask").val();
		var startTime = $("#startTime").val();
		var endTime = $("#endTime").val();
		if(weeklystartTime == ''){
			alert("请输入报告开始时间!");
			return false;
		}
		if(weeklyendTime == ''){
			alert("请输入报告结束时间!");
			return false;
		}
		if(currentTask == ''){
			alert("请选择项目当前所处的项目阶段");
			return false;
		}
		
		if(startTime == ''){
			alert("请输入项目当前阶段开始时间");
			return false;
		}
		if(endTime == ''){
			alert("请输入项目当前阶段结束时间");
			return false;
		}
		$.ajax({
			url:"SaveWeekly.action",
			type:"post",
			dataType:'json',
			data:$("#mainFrom").serialize(),
			success:function(data){
				var result = data.result;
				if(result != 0){
					alert("保存草稿成功");
					$("#weeklyId").val(result);
				}else{
					alert("保存失败，请联系管理员!");
				}
			}
		});
		
	}
	
	function submitReport(){
		var weeklystartTime = $("#weeklystartTime").val();
		var weeklyendTime = $("#weeklyendTime").val();
		var currentTask = $("#currentTask").val();
		var startTime = $("#startTime").val();
		var endTime = $("#endTime").val();
		var officeManager = $("#officeManager").val();
		if(weeklystartTime == ''){
			alert("请输入报告开始时间!");
			return false;
		}
		if(weeklyendTime == ''){
			alert("请输入报告结束时间!");
			return false;
		}
		if(currentTask == ''){
			alert("请选择项目当前所处的项目阶段");
			return false;
		}
		
		if(startTime == ''){
			alert("请输入项目当前阶段开始时间");
			return false;
		}
		if(endTime == ''){
			alert("请输入项目当前阶段结束时间");
			return false;
		}
		if(officeManager == ''){
			alert("办事处主任邮箱地址必须输入!");
			return false;
		}
		$.ajax({
			url:"SubmitWeekly.action",
			type:"post",
			dataType:'json',
			data:$("#mainFrom").serialize(),
			success:function(data){
				var result = data.result;
				if(result != 0){
					alert("提交成功!");
					$("#weeklyId").val(result);
			//		parent.closeWindow("BudgetUpload");
					window.parent.location.reload();
				}else{
					alert("提交失败，请联系管理员!");
				}
			}
		});
	}
	
	function uploadFile(){
		var weeklystartTime = $("#weeklystartTime").val();
		var weeklyendTime = $("#weeklyendTime").val();
		var currentTask = $("#currentTask").val();
		var startTime = $("#startTime").val();
		var endTime = $("#endTime").val();
		if(weeklystartTime == ''){
			alert("请输入报告开始时间!");
			return false;
		}
		if(weeklyendTime == ''){
			alert("请输入报告结束时间!");
			return false;
		}
		if(currentTask == ''){
			alert("请选择项目当前所处的项目阶段");
			return false;
		}
		
		if(startTime == ''){
			alert("请输入项目当前阶段开始时间");
			return false;
		}
		if(endTime == ''){
			alert("请输入项目当前阶段结束时间");
			return false;
		}
		
		$.ajax({
			url:"SaveWeekly.action",
			type:"post",
			dataType:'json',
			data:$("#mainFrom").serialize(),
			success:function(data){
				var result = data.result;
				if(result != 0){
					$("#weeklyId").val(result);
					var weeklyId = result;
					var projectId = $("#projectId").val();
					popWindow('module/sub/ToUploadFile.action?projectWeekly.weeklyId='+weeklyId+'&projectWeekly.projectId='+projectId+'', 700, 450,'上传附件', 'BudgetUpload', true);
				}else{
					alert("保存失败，请联系管理员!");
				}
			}
		});
		
	}
	
</script>
</head>
<body style="width: 98%;height: 98%">
	<s:form cssClass="form-horizontal" id="mainFrom" role="form" >
		<s:hidden name="projectWeekly.projectId" id="projectId" value="%{project.projectId}"></s:hidden>
		<s:hidden name="projectWeekly.weeklyId" value="0" id="weeklyId"></s:hidden>
		<div class="form-group">
			      <label  style="width: 120px" class="col-sm-1 control-label"><s:text name="pm.weekly.start"></s:text></label>
			      <div class="col-sm-3">
			         <s:textfield type="text" cssClass="form-control" id="weeklystartTime"  name="projectWeekly.weeklyStartTime"
			            placeholder="周报开始时间"/>
			      </div>
			     <div class="col-sm-1">---</div>
			      <div class="col-sm-3">
			         <s:textfield type="text" cssClass="form-control" id="weeklyendTime" name="projectWeekly.weeklyEndTime"
			            placeholder="周报结束时间" />
			      </div>
	   	</div>
		<div class="panel panel-default">
			 <div class="panel-heading">
			      <h1 class="panel-title"><s:text name="pm.current.task"></s:text>:
			     <%--  <s:textfield name="projectWeekly.currentTask" id="currentTask" cssClass="form-control-ex" ></s:textfield> --%>
			      <s:select list="projectTaskList" cssClass="form-control-ex" id="currentTask" cssStyle="width:110px" name="projectWeekly.currentTask" listKey="eventValue" listValue="eventValue" headerKey="" headerValue="--请选择--"></s:select>
			      </h1>
			 </div>
	   		 <div class="form-group">
			      <label for="startTime" style="width: 120px" class="col-sm-1 control-label"><s:text name="pm.task.start.time"></s:text></label>
			      <div class="col-sm-3">
			         <s:textfield type="text" cssClass="form-control" id="startTime"  name="projectWeekly.taskStartTime"
			            placeholder="本阶段开始时间"/>
			      </div>
			      <label for="endTime" style="width: 120px" class="col-sm-1 control-label"><s:text name="pm.task.end.time"></s:text></label>
			      <div class="col-sm-3">
			         <s:textfield type="text" cssClass="form-control" id="endTime" name="projectWeekly.taskEndTime"
			            placeholder="本阶段结束时间" />
			      </div>
	   		</div>
   		</div>
   		<!-- 本阶段工作完成情况 -->
   		<div class="panel panel-default">
			 <div class="panel-heading">
			      <h1 class="panel-title"><s:text name="pm.work.performance"></s:text></h1>
			 </div>
			 <table class="table  worktable">
			 	<thead>
			 		<tr>
						<th width="45px"><s:text name="pm.index"></s:text></th>
						<th><s:text name="pm.work.breakdown"></s:text></th>
						<%-- <th><s:text name="pm.task.progress"></s:text></th> --%>
						<th width="45px">
							<a href="javascript:void(0)" >
					          <span class="glyphicon glyphicon-plus-sign"></span>
					        </a>
					        <a href="javascript:void(0)" >
					          <span class="glyphicon glyphicon-minus-sign"></span>
					        </a>
						</th>			 			
			 		</tr>
			 	</thead>
			 	<tr class="workSize  pmWork1">
			 		<td style="padding-top: 21px;text-align: center;">
			 			1
			 		</td>
			 		<td>
			 			<s:textarea name="workcontentList[0].optionDesc001" cssClass="form-control" rows="2"></s:textarea>
			 		</td>
			 		<%-- <td>
			 			<s:textarea name="workcontentList[0].optionDesc002" cssClass="form-control" rows="2"></s:textarea>
			 		</td> --%>
			 		<td style="padding-top: 21px" >
			 			<a href="javascript:void(0)" onclick="moreWork(1)">
				          <span class="glyphicon glyphicon-plus-sign"></span>
				        </a>
				        <a href="javascript:void(0)" >
				          <span class="glyphicon glyphicon-minus-sign"></span>
				        </a>
			 		</td>
			 	</tr>
			 	<s:if test="%{workcontentList!=null && workcontentList.size()>0}">
			 		<s:iterator value="workcontentList" var="v" status="index">
						<s:if test="%{#index.index > 0}">
							<tr class="workSize  pmWork<s:property value='#index.index+1'/>">
						 		<td style="padding-top: 21px;text-align: center;">
						 			<s:property value="#index.index+1"/>
						 		</td>
						 		<td>
						 			<s:textarea name="workcontentList[%{#index.index}].optionDesc001" cssClass="form-control" rows="2"></s:textarea>
						 		</td>
						 		<%-- <td>
						 			<s:textarea name="workcontentList[%{#index.index}].optionDesc002" cssClass="form-control" rows="2"></s:textarea>
						 		</td> --%>
						 		<s:if test="%{projectWeekly.weeklyState != 1}">
						 		<td style="padding-top: 21px" >
						 			<a href="javascript:void(0)" onclick="moreWork(<s:property value='#index.index+1'/>)">
							          <span class="glyphicon glyphicon-plus-sign"></span>
							        </a>
							        <a href="javascript:void(0)" onclick="deleteItem(this ,'worktable')">
							          <span class="glyphicon glyphicon-minus-sign"></span>
							        </a>
						 		</td>
						 		</s:if>
						 	</tr>
						</s:if>			 		
			 		</s:iterator>
			 	</s:if>
			 </table>
			 <!-- 隐藏元素提供复制功能 -->
			 <div  style="display:none">
			 	<table>
			 		<tr id="workCopy">
			 			<td style="padding-top: 21px;text-align: center;">
		 					1
				 		</td>
				 		<td>
				 			<s:textarea name="workcontentList[[0]].optionDesc001" cssClass="form-control" rows="2"></s:textarea>
				 		</td>
				 		<td style="padding-top: 21px" >
				 			<a href="javascript:void(0)" onclick="moreWork([0])">
					          <span class="glyphicon glyphicon-plus-sign"></span>
					        </a>
					        <a href="javascript:void(0)" onclick="deleteItem(this ,'worktable')">
					          <span class="glyphicon glyphicon-minus-sign"></span>
					        </a>
				 		</td>
			 		</tr>
			 	</table>
			 </div>
		</div>
		<!-- 本阶段实际进展与工程计划是否有偏差及偏差原因 -->
		<div class="panel panel-default">
			 <div class="panel-heading">
			      <h1 class="panel-title"><s:text name="pm.work.deviation"></s:text></h1>
			 </div>
			 <div class="form-group">
			 	<div class="col-sm-12">
			 		<s:textarea name="projectWeekly.taskDeviation" cssClass="form-control"></s:textarea>
			 	</div>
			 </div>
		</div>
		<!-- 项目存在问题/风险和措施分析 -->
		<div class="panel panel-default">
			 <div class="panel-heading">
			      <h1 class="panel-title"><s:text name="pm.problem.risk"></s:text></h1>
			 </div>
			 <table class="table risktable">
			 	<thead>
			 		<tr>
						<th width="45px"><s:text name="pm.index"></s:text></th>
						<th><s:text name="pm.problem"></s:text></th>
						<%-- <th><s:text name="pm.risk.analyze"></s:text></th> --%>
						<th width="45px">
							<a href="javascript:void(0)" >
					          <span class="glyphicon glyphicon-plus-sign"></span>
					        </a>
					        <a href="javascript:void(0)" >
					          <span class="glyphicon glyphicon-minus-sign"></span>
					        </a>
						</th>				 			
			 		</tr>
			 	</thead>
			 	<tr class="riskSize  pmRisk1">
			 		<td style="padding-top: 21px;text-align: center;">
			 			1
			 		</td>
			 		<td>
			 			<s:textarea name="riskcontentList[0].optionDesc001" cssClass="form-control" rows="2"></s:textarea>
			 		</td>
			 		<td style="padding-top: 21px" >
			 			<a href="javascript:void(0)" onclick="moreRisk(1)">
				          <span class="glyphicon glyphicon-plus-sign"></span>
				        </a>
				        <a href="javascript:void(0)" >
				          <span class="glyphicon glyphicon-minus-sign"></span>
				        </a>
			 		</td>
			 	</tr>
			 	<s:if test="%{riskcontentList!=null && riskcontentList.size()>0}">
			 		<s:iterator value="riskcontentList" var="v" status="index">
						<s:if test="%{#index.index > 0}">
							<tr class="riskSize  pmRisk<s:property value='#index.index+1'/>">
						 		<td style="padding-top: 21px;text-align: center;">
						 			<s:property value="#index.index+1"/>
						 		</td>
						 		<td>
						 			<s:textarea name="riskcontentList[%{#index.index}].optionDesc001" cssClass="form-control" rows="2"></s:textarea>
						 		</td>
						 		<%-- <td>
						 			<s:textarea name="workcontentList[%{#index.index}].optionDesc002" cssClass="form-control" rows="2"></s:textarea>
						 		</td> --%>
						 		<s:if test="%{projectWeekly.weeklyState != 1}">
						 		<td style="padding-top: 21px" >
						 			<a href="javascript:void(0)" onclick="moreRisk(<s:property value='#index.index+1'/>)">
							          <span class="glyphicon glyphicon-plus-sign"></span>
							        </a>
							        <a href="javascript:void(0)" onclick="deleteItem(this ,'risktable')">
							          <span class="glyphicon glyphicon-minus-sign"></span>
							        </a>
						 		</td>
						 		</s:if>
						 	</tr>
						</s:if>			 		
			 		</s:iterator>
			 	</s:if>
			 </table> 
			  <!-- 隐藏元素提供复制功能 -->
			 <div  style="display:none">
			 	<table>
			 		<tr id="riskCopy">
			 			<td style="padding-top: 21px;text-align: center;">
		 					1
				 		</td>
				 		<td>
				 			<s:textarea name="riskcontentList[[0]].optionDesc001" cssClass="form-control" rows="2"></s:textarea>
				 		</td>
				 		<%-- <td>
				 			<s:textarea name="riskcontentList[[0]].optionDesc002" cssClass="form-control" rows="2"></s:textarea>
				 		</td> --%>
				 		<td style="padding-top: 21px" >
				 			<a href="javascript:void(0)" onclick="moreRisk([0])">
					          <span class="glyphicon glyphicon-plus-sign"></span>
					        </a>
					        <a href="javascript:void(0)" onclick="deleteItem(this ,'risktable')">
					          <span class="glyphicon glyphicon-minus-sign"></span>
					        </a>
				 		</td>
			 		</tr>
			 	</table>
			 </div>
		</div>	
		<!-- 总部支持求助 -->
		<div class="panel panel-default">
			 <div class="panel-heading">
			      <h1 class="panel-title"><s:text name="pm.head.help"></s:text></h1>
			 </div>
			  <table class="table helptable">
			  	<thead>
			 		<tr>
						<th width="45px"><s:text name="pm.index"></s:text></th>
						<th><s:text name="pm.help.content"></s:text></th>
						<%-- <th><s:text name="pm.help.object"></s:text></th> --%>
						<th width="45px">
							<a href="javascript:void(0)" >
					          <span class="glyphicon glyphicon-plus-sign"></span>
					        </a>
					        <a href="javascript:void(0)" >
					          <span class="glyphicon glyphicon-minus-sign"></span>
					        </a>
						</th>					 			
			 		</tr>
			 	</thead>
			 	<tr class="helpSize  pmHelp1">
			 		<td style="padding-top: 21px;text-align: center;">
			 			1
			 		</td>
			 		<td>
			 			<s:textarea name="helpcontentList[0].optionDesc001" cssClass="form-control" rows="2"></s:textarea>
			 		</td>
			 		<%-- <td>
			 			<s:textarea name="helpcontentList[0].optionDesc002" cssClass="form-control" rows="2"></s:textarea>
			 		</td> --%>
			 		<td style="padding-top: 21px" >
			 			<a href="javascript:void(0)" onclick="moreHelp(1)">
				          <span class="glyphicon glyphicon-plus-sign"></span>
				        </a>
				        <a href="javascript:void(0)" >
				          <span class="glyphicon glyphicon-minus-sign"></span>
				        </a>
			 		</td>
			 	</tr>
			 	<s:if test="%{helpcontentList!=null && helpcontentList.size()>0}">
			 		<s:iterator value="helpcontentList" var="v" status="index">
						<s:if test="%{#index.index > 0}">
							<tr class="helpSize  pmHelp<s:property value='#index.index+1'/>">
						 		<td style="padding-top: 21px;text-align: center;">
						 			<s:property value="#index.index+1"/>
						 		</td>
						 		<td>
						 			<s:textarea name="helpcontentList[%{#index.index}].optionDesc001" cssClass="form-control" rows="2"></s:textarea>
						 		</td>
						 		<s:if test="%{projectWeekly.weeklyState != 1}">
						 		<td style="padding-top: 21px" >
						 			<a href="javascript:void(0)" onclick="moreHelp(<s:property value='#index.index+1'/>)">
							          <span class="glyphicon glyphicon-plus-sign"></span>
							        </a>
							        <a href="javascript:void(0)" onclick="deleteItem(this ,'risktable')">
							          <span class="glyphicon glyphicon-minus-sign"></span>
							        </a>
						 		</td>
						 		</s:if>
						 	</tr>
						</s:if>			 		
			 		</s:iterator>
			 	</s:if>
			 </table>
			  <!-- 隐藏元素提供复制功能 -->
			 <div  style="display:none">
			 	<table>
			 		<tr id="helpCopy">
			 			<td style="padding-top: 21px;text-align: center;">
		 					1
				 		</td>
				 		<td>
				 			<s:textarea name="helpcontentList[[0]].optionDesc001" cssClass="form-control" rows="2"></s:textarea>
				 		</td>
				 		<td style="padding-top: 21px" >
				 			<a href="javascript:void(0)" onclick="moreHelp([0])">
					          <span class="glyphicon glyphicon-plus-sign"></span>
					        </a>
					        <a href="javascript:void(0)" onclick="deleteItem(this ,'helptable')">
					          <span class="glyphicon glyphicon-minus-sign"></span>
					        </a>
				 		</td>
			 		</tr>
			 	</table>
			 </div>
			  
		 </div>
		 <!-- 本阶段工作进展 -->
		 <div class="panel panel-default">
			 <div class="panel-heading">
			      <h1 class="panel-title"><s:text name="pm.current.progress"></s:text></h1>
			 </div>
			  <table class="table progresstable">
			  	<thead>
			 		<tr>
						<th width="45px"><s:text name="pm.index"></s:text></th>
						<th><s:text name="pm.project.task"></s:text></th>
						<%-- <th><s:text name="pm.project.task.progress"></s:text></th> --%>
						<th width="45px">
							<a href="javascript:void(0)" >
					          <span class="glyphicon glyphicon-plus-sign"></span>
					        </a>
					        <a href="javascript:void(0)" >
					          <span class="glyphicon glyphicon-minus-sign"></span>
					        </a>
						</th>				 			
			 		</tr>
			 	</thead>
			 	<tr class="progressSize  pmProgress1">
			 		<td style="padding-top: 21px;text-align: center;">
			 			1
			 		</td>
			 		<td>
			 			<s:textarea name="progresscontentList[0].optionDesc001"  cssClass="form-control" rows="2"></s:textarea>
			 		</td>
			 		<td style="padding-top: 21px" >
			 			<a href="javascript:void(0)" onclick="moreProgress(1)">
				          <span class="glyphicon glyphicon-plus-sign"></span>
				        </a>
				        <a href="javascript:void(0)" >
				          <span class="glyphicon glyphicon-minus-sign"></span>
				        </a>
			 		</td>
			 	</tr>
			 	<s:if test="%{progresscontentList!=null && progresscontentList.size()>0}">
			 		<s:iterator value="progresscontentList" var="v" status="index">
						<s:if test="%{#index.index > 0}">
							<tr class="progressSize  pmProgress<s:property value='#index.index+1'/>">
						 		<td style="padding-top: 21px;text-align: center;">
						 			<s:property value="#index.index+1"/>
						 		</td>
						 		<td>
						 			<s:textarea name="progresscontentList[%{#index.index}].optionDesc001" value="%{#v.optionDesc001}" cssClass="form-control" rows="2"></s:textarea>
						 		</td>
						 		<td style="padding-top: 21px" >
						 			<a href="javascript:void(0)" onclick="moreProgress(<s:property value='#index.index+1'/>)">
							          <span class="glyphicon glyphicon-plus-sign"></span>
							        </a>
							        <a href="javascript:void(0)" onclick="deleteItem(this ,'progresstable')">
							          <span class="glyphicon glyphicon-minus-sign"></span>
							        </a>
						 		</td>
						 	</tr>
						</s:if>			 		
			 		</s:iterator>
			 	</s:if>
			 	
			 </table>
			  <!-- 隐藏元素提供复制功能 -->
			 <div  style="display:none">
			 	<table>
			 		<tr id="progressCopy">
			 			<td style="padding-top: 21px;text-align: center;">
		 					1
				 		</td>
				 		<td>
				 			<s:textarea name="progresscontentList[[0]].optionDesc001" cssClass="form-control" rows="2"></s:textarea>
				 		</td>
				 		<%-- <td>
				 			<s:textarea name="progresscontentList[[0]].optionDesc002" cssClass="form-control" rows="2"></s:textarea>
				 		</td> --%>
				 		<td style="padding-top: 21px" >
				 			<a href="javascript:void(0)" onclick="moreProgress([0])">
					          <span class="glyphicon glyphicon-plus-sign"></span>
					        </a>
					        <a href="javascript:void(0)" onclick="deleteItem(this ,'progresstable')">
					          <span class="glyphicon glyphicon-minus-sign"></span>
					        </a>
				 		</td>
			 		</tr>
			 	</table>
			 </div>
		 </div>
		  <!-- 下阶段工作计划 -->
		 <div class="panel panel-default">
			 <div class="panel-heading">
			      <h1 class="panel-title"><s:text name="pm.next.plan"></s:text></h1>
			 </div>
			    <table class="table plantable">
			  		<thead>
			 		<tr>
						<th width="45px"><s:text name="pm.index"></s:text></th>
						<th><s:text name="pm.project.task.plan"></s:text></th>
					<%-- 	<th><s:text name="pm.project.task.time"></s:text></th> --%>
						<th width="45px">
							<a href="javascript:void(0)" >
					          <span class="glyphicon glyphicon-plus-sign"></span>
					        </a>
					        <a href="javascript:void(0)" >
					          <span class="glyphicon glyphicon-minus-sign"></span>
					        </a>
						</th>				 			
			 		</tr>
				 </thead>
				 <tr class="planSize  pmPlan1">
			 		<td style="padding-top: 21px;text-align: center;">
			 			1
			 		</td>
			 		<td>
			 			<s:textarea name="plancontentList[0].optionDesc001" cssClass="form-control" rows="2"></s:textarea>
			 		</td>
			 		<td style="padding-top: 21px" >
			 			<a href="javascript:void(0)" onclick="morePlan(1)">
				          <span class="glyphicon glyphicon-plus-sign"></span>
				        </a>
				        <a href="javascript:void(0)" >
				          <span class="glyphicon glyphicon-minus-sign"></span>
				        </a>
			 		</td>
			 	</tr>
			 	<s:if test="%{plancontentList!=null && plancontentList.size()>0}">
			 		<s:iterator value="plancontentList" var="v" status="index">
						<s:if test="%{#index.index > 0}">
							<tr class="planSize  pmPlan<s:property value='#index.index+1'/>">
						 		<td style="padding-top: 21px;text-align: center;">
						 			<s:property value="#index.index+1"/>
						 		</td>
						 		<td>
						 			<s:textarea name="plancontentList[%{#index.index}].optionDesc001" value="%{#v.optionDesc001}" cssClass="form-control" rows="2"></s:textarea>
						 		</td>
						 		<td style="padding-top: 21px" >
						 			<a href="javascript:void(0)" onclick="morePlan(<s:property value='#index.index+1'/>)">
							          <span class="glyphicon glyphicon-plus-sign"></span>
							        </a>
							        <a href="javascript:void(0)" onclick="deleteItem(this ,'progresstable')">
							          <span class="glyphicon glyphicon-minus-sign"></span>
							        </a>
						 		</td>
						 	</tr>
						</s:if>			 		
			 		</s:iterator>
			 	</s:if>
			 </table>
			  <!-- 隐藏元素提供复制功能 -->
			 <div  style="display:none">
			 	<table>
			 		<tr id="planCopy">
			 			<td style="padding-top: 21px;text-align: center;">
		 					1
				 		</td>
				 		<td>
				 			<s:textarea name="plancontentList[[0]].optionDesc001" cssClass="form-control" rows="2"></s:textarea>
				 		</td>
				 		<%-- <td>
				 			<s:textarea name="plancontentList[[0]].optionDesc002" cssClass="form-control" rows="2"></s:textarea>
				 		</td> --%>
				 		<td style="padding-top: 21px" >
				 			<a href="javascript:void(0)" onclick="morePlan([0])">
					          <span class="glyphicon glyphicon-plus-sign"></span>
					        </a>
					        <a href="javascript:void(0)" onclick="deleteItem(this ,'plantable')">
					          <span class="glyphicon glyphicon-minus-sign"></span>
					        </a>
				 		</td>
			 		</tr>
			 	</table>
			 </div>
		 </div>
		   <!-- 周报抄送人 -->
		 <div class="panel panel-default">
			 <div class="panel-heading">
			      <h1 class="panel-title"><s:text name="pm.weekly.mails"></s:text></h1>
			 </div>
			    <table class="table mailtable">
			  		<thead>
			 		<tr>
						<th width="45px"><s:text name="pm.index"></s:text></th>
						<th><s:text name="pm.weekly.mail.name"></s:text></th>
					 	<th><s:text name="pm.weekly.mail.address"></s:text></th>
						<th width="45px">
							<a href="javascript:void(0)" >
					          <span class="glyphicon glyphicon-plus-sign"></span>
					        </a>
					        <a href="javascript:void(0)" >
					          <span class="glyphicon glyphicon-minus-sign"></span>
					        </a>
						</th>				 			
			 		</tr>
				 </thead>
				 <tr class="mailSize  mail1">
			 		<td style="padding-top: 21px;text-align: center;">
			 			1
			 		</td>
			 		<td>
			 			<s:textfield name="mailcontentList[0].optionDesc001" value="项目组成员" readonly="true" cssClass="form-control"></s:textfield>
			 		</td>
			 		<td>
			 			<s:textfield name="mailcontentList[0].optionDesc002" placeholder="系统自动获取" readonly="true"  cssClass="form-control" ></s:textfield>
			 		</td>
			 		<td style="padding-top: 21px" >
			 			<a href="javascript:void(0)" onclick="moreMail(1)">
				          <span class="glyphicon glyphicon-plus-sign"></span>
				        </a>
				        <a href="javascript:void(0)" >
				          <span class="glyphicon glyphicon-minus-sign"></span>
				        </a>
			 		</td>
			 	</tr>
			 	<tr class="mailSize  mail2">
			 		<td style="padding-top: 21px;text-align: center;">
			 			2
			 		</td>
			 		<td>
			 			<s:textfield name="mailcontentList[1].optionDesc001" value="sp_dr群组" readonly="true" cssClass="form-control"></s:textfield>
			 		</td>
			 		<td>
			 			<s:textfield name="mailcontentList[1].optionDesc002" placeholder="系统自动获取" readonly="true" cssClass="form-control" ></s:textfield>
			 		</td>
			 		<td style="padding-top: 21px" >
			 			<a href="javascript:void(0)" onclick="moreMail(2)">
				          <span class="glyphicon glyphicon-plus-sign"></span>
				        </a>
				        <a href="javascript:void(0)" >
				          <span class="glyphicon glyphicon-minus-sign"></span>
				        </a>
			 		</td>
			 	</tr>
			 	<tr class="mailSize  mail3">
			 		<td style="padding-top: 21px;text-align: center;">
			 			3
			 		</td>
			 		<td>
			 			<s:textfield name="mailcontentList[2].optionDesc001" placeholder="办事处主任" cssClass="form-control"></s:textfield>
			 		</td>
			 		<td>
			 			<s:textfield name="mailcontentList[2].optionDesc002" id="officeManager" placeholder="必须输入"  cssClass="form-control" ></s:textfield>
			 		</td>
			 		<td style="padding-top: 21px" >
			 			<a href="javascript:void(0)" onclick="moreMail(3)">
				          <span class="glyphicon glyphicon-plus-sign"></span>
				        </a>
				        <a href="javascript:void(0)" >
				          <span class="glyphicon glyphicon-minus-sign"></span>
				        </a>
			 		</td>
			 	</tr>
			 </table>
			  <!-- 隐藏元素提供复制功能 -->
			 <div  style="display:none">
			 	<table>
			 		<tr id="mailCopy">
			 			<td style="padding-top: 21px;text-align: center;">
		 					1
				 		</td>
				 		<td>
				 			<s:textfield name="mailcontentList[[0]].optionDesc001" cssClass="form-control"></s:textfield>
				 		</td>
				 		<td>
				 			<s:textfield name="mailcontentList[[0]].optionDesc002" cssClass="form-control"></s:textfield>
				 		</td>
				 		<td style="padding-top: 21px" >
				 			<a href="javascript:void(0)" onclick="moreMail([0])">
					          <span class="glyphicon glyphicon-plus-sign"></span>
					        </a>
					        <a href="javascript:void(0)" onclick="deleteItem(this ,'mailtable')">
					          <span class="glyphicon glyphicon-minus-sign"></span>
					        </a>
				 		</td>
			 		</tr>
			 	</table>
			 </div>
		 </div>
		 <!-- 备注 -->
		 <div class="panel panel-default">
			 <div class="panel-heading">
			      <h1 class="panel-title"><s:text name="pm.remark"></s:text></h1>
			 </div>
			 <div class="form-group">
			 	<div class="col-sm-12">
			 		<s:textarea cssClass="form-control" name="projectWeekly.remark"></s:textarea>
			 	</div>
			 </div>
			 <div class="form-group">
				<a href="javascript:void(0)" onclick="uploadFile()"
				  class="btn btn-default btn-block" style="width: 100px;height: 30px"  ><span class="glyphicon glyphicon-upload"></span> 上传附件</a> 
			 </div>
			 
			 
		</div>
		<a href="javascript:void(0)" class="btn btn-info" onclick="submitReport()">
          <span class="glyphicon glyphicon-ok"></span><s:text name="pm.submit.report"></s:text>
        </a>
         <a href="javascript:void(0)" class="btn btn-info" onclick="saveDraft()">
          <span class="glyphicon glyphicon-plus"></span> <s:text name="pm.save.draft"></s:text>
        </a>
	</s:form>
</body>
</html>