<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.powermanage' />">
<%-- <meta name="supfunction" content="<s:text name='sys.project.management' />"> --%>
<meta name="function"
	content="<s:text name='sys.project.management' />">	
<script>
	var realnameArr=new Array();
	var usernameArr=new Array();
	var realnameArr2=new Array();
	var usernameArr2=new Array();
	var allUserNameArr = new Array();
	var allRealNameArr = new Array();
	$(document).ready(function(){
		queryserviceuser(11);
		$("#serviceManagerCodeforjson").autocomplete({
			source: realnameArr
		});
		queryprogramuser(12);
		$("#programManagerCodeforjson").autocomplete({
			source: realnameArr2
		});
		
		$("#programManagerCodeforjsonB").autocomplete({
			source: realnameArr2
		});
		
		queryalluser();
        $("#salesManName").autocomplete({
            source: allRealNameArr
        });
        
		if("<s:property value =  'user.isHasRole(13)'/>"){//工程管理部
			$(".engineeManagerTxt").removeAttr("disabled");
			$(".submitBtn").removeAttr("disabled");
		} 
		//控指合同号不以 312/313开头的项目类型为非直签
		var contract = $("#contractNo").val();
		var diff = contract.substr(0,3);
		if(diff != '312' && diff != '313'){
			$("#column011").val(20);
		}else{
			$("#column011").val(10);
		}
		
		
		var implement = document.getElementById("column012");
		changeProgramManagerTxt(implement);
		
		$("#orderCreateTime").val(CurentDate());
		$("#orderCreateTime").datepicker({
          changeMonth: true,
          changeYear: true,
        });
	});
	
	
	function checkSubmit(){
		if(!checkForm()){
			return false;
		}
		$("#mainForm").submit();
	}
	
	function checkForm(){
		if($("#projectCode").val() == ''){
            alert("请填写项目编码");
            $("#projectCode").focus();
            return false;
        }
		if($("#projectName").val() == ''){
            alert("请填写项目名称");
            $("#projectName").focus();
            return false;
        }
		if($("#contractNo").val() == ''){
            alert("请填写合同号");
            $("#contractNo").focus();
            return false;
        }
		if($("#column001").val() == ''){
            alert("请选择办事处");
            $("#column001").focus();
            return false;
        }
		if($("#compId").val() == ''){
            alert("请选择所属公司");
            $("#compId").focus();
            return false;
        }
		var serviceManagerCode = $("#serviceManagerCode").val();//服务经理
		if(serviceManagerCode == ''){
			alert("请填写服务经理");
			$("#serviceManagerCodeforjson").focus();
			return false;
		}
		return true;
	}
	function checkNotGrantTailCause(){
		var notGrantTailCause = $("#notGrantTailCause").val();//服务经理
		if(notGrantTailCause == ''){
			alert("请输入不予跟踪原因!");
			$("#notGrantTailCause").focus();
			return false;
		}
		return true;
	}
	
	function denySubmit(){
		if(checkForm()&&checkNotGrantTailCause()){
			$("#mainForm").submit();
		}
	}
	
	function queryalluser(obj){
        $.ajax({
            url:'queryalluser.action',
            type:'post',
            dataType:'json',
            data:{roleid:obj},
            success: queryalluser2
        });
    }
    function queryalluser2(json){
        var userlist = json.allusernameList;
        for(var i = 0;i < userlist.length;i++){
        	allUserNameArr[i] = userlist[i].username;
        	allRealNameArr[i] = userlist[i].username+"-"+userlist[i].realName;
        }
    }
    
	function queryserviceuser(obj){
		$.ajax({
			url:'queryalluser.action',
			type:'post',
			dataType:'json',
			data:{roleid:obj},
			success:queryserviceuser2
		});
	}
	function queryserviceuser2(json){
		var userlist = json.allusernameList;
		for(var i = 0;i < userlist.length;i++){
			usernameArr[i] = userlist[i].username;
			realnameArr[i] = userlist[i].username+"-"+userlist[i].realName;
		}
	}
	
	function queryprogramuser(obj){
		$.ajax({
			url:'queryalluser.action',
			type:'post',
			dataType:'json',
			data:{roleid:obj},
			success:queryprogramuser2
		});
	}
	function queryprogramuser2(json){
		var userlist = json.allusernameList;
		for(var i = 0;i < userlist.length;i++){
			usernameArr2[i] = userlist[i].username;
			realnameArr2[i] = userlist[i].username+"-"+userlist[i].realName;
		}
	}
	
	function fillservicemanager(){
		var obj=document.getElementById("serviceManagerCodeforjson");
		if(obj.value==""){
			document.getElementById("serviceManagerCode").value="";
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
				document.getElementById("serviceManagerCode").value=usernameArr[i];
			}
		}
	}
	function fillprogrammanager(obj){
		//var obj=document.getElementById("programManagerCodeforjson");
		if(obj.value==""){
			//document.getElementById("programManagerCode").value="";
			$(obj).next().val("");
		}
		if(obj.value!=""){
			var i=0;
			for(;i<realnameArr2.length;i++){
				if(realnameArr2[i]==obj.value){
					break;
				}
			}
			if(i==realnameArr2.length){
				return false;
			} else{
				//document.getElementById("programManagerCode").value=usernameArr2[i];
				$(obj).next().val(usernameArr2[i]);
			}
		}
	}
	function fillSalesMan(){
        var obj=document.getElementById("salesManName");
        if(obj.value==""){
            document.getElementById("salesManCode").value="";
        }
        if(obj.value!=""){
            var i=0;
            for(;i<allRealNameArr.length;i++){
                if(allRealNameArr[i]==obj.value){
                    break;
                }
            }
            if(i==allRealNameArr.length){
                return false;
            } else{
                document.getElementById("salesManCode").value=allUserNameArr[i];
            }
        }
    }
	function changeProgramManagerTxt(e){
		var sel = $(e).val();
		if(sel == 0 || sel == 4){//原厂直服、原厂集成
			$("#programManagerDiv2").show();
			//$("#programManagerDiv3").hide();
			$("#programManagerDiv3").show();
		}else if(sel == 1 || sel == 3){
			$("#programManagerDiv2").hide();
			$("#programManagerDiv3").show();
		}
	}
	
	function fillChContractNo(obj) {
		var contractNos = $(obj).val() || "";
		if (!contractNos.trim()) {
			return;
		}
		var contractNoArr = contractNos.split(",");
		for (var i = 0; i < contractNoArr.length; i++) {
			var contractNo = contractNoArr[i];
			contractNoArr[i] = contractNo.replace(/-C/ig, "") + "-C";
		}
		$(obj).val(contractNoArr.join(","));
	}
</script>
</head>
<body>
	<s:form enctype="multipart/form-data" method="POST" action="module/createCHProject.action" cssClass="form-inline" id="mainForm">
		<s:hidden value="%{project.column012}" id="isRead"></s:hidden>
		<s:hidden name="project.column012Readonly" value="%{project.column012}"></s:hidden>
		<s:hidden name="project.validateFlag" value="3b108349b93f7c8c4e2346f8d48c092a"></s:hidden>
		<div class="form-group form-group-query">
			<label for="projectCode" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.projectCode" /></label>
	    	<s:textfield  name="project.projectCode" id="projectCode" placeholder="项目编码..." cssClass="form-control" cssStyle="width: 180px;display: inline-block;" />
		</div>
		<div class="form-group form-group-query">
			<label for="projectName" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.projectName" /></label>
	    	<s:textfield  name="project.projectName" id="projectName" placeholder="项目名称..." cssClass="form-control" cssStyle="width: 180px;display: inline-block;" />
		</div>
		<div class="form-group form-group-query">
			<label for="contractNo" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.contractNo" /></label>
	    	<s:textfield  name="project.contractNo" id="contractNo" placeholder="合同号..." onblur="fillChContractNo(this)" cssClass="form-control" cssStyle="width: 350px;display: inline-block;" />
		</div><br/>
		<div class="form-group form-group-query">
			<label for="usernamec" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.usernamec" /></label>
	    	<s:textfield  name="project.salesManName" id="salesManName" placeholder="销售代表..." cssClass="form-control" 
	    	  cssStyle="width: 180px;display: inline-block;" onfocus="fillSalesMan()" onblur="fillSalesMan()" disabled="true" />
		    <s:textfield name="project.salesManCode" type="hidden" id="salesManCode"></s:textfield>
		</div>
		<div class="form-group form-group-query">
			<label for="column001" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.officeName" /></label>
	    	<%-- <s:textfield  name="project.officeName" id="officeName" placeholder="办事处..." cssClass="form-control" cssStyle="width: 180px;display: inline-block;" /> --%>
		    <s:select name="project.column001" id="column001" listKey="departmentNum" cssClass="form-control" headerKey=""
                headerValue="--请选择--" cssStyle="width: 180px;display: inline-block;" listValue="departmentName" list="%{departmentList}" theme="simple" />
		</div>
		<div class="form-group form-group-query">
			<label for="column004" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.market" /></label>
	    	<s:textfield  name="project.column004" id="column004" placeholder="市场部..." cssClass="form-control" cssStyle="width: 100px;display: inline-block;" disabled="true"/>
	    	<s:textfield  name="project.column005" id="column005" placeholder="系统部..." cssClass="form-control" cssStyle="width: 80px;display: inline-block;" disabled="true"/>
	    	<s:textfield  name="project.column006" id="column006" placeholder="拓展部..." cssClass="form-control" cssStyle="width: 80px;display: inline-block;" disabled="true"/>
	    	<s:textfield  name="project.column007" id="column007" placeholder="子行业..." cssClass="form-control" cssStyle="width: 80px;display: inline-block;" disabled="true"/>
		</div><br/>
		<div class="form-group form-group-query">
			<label for="orderCreateTime" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.orderCreateTime" /></label>
	    	<s:textfield  name="project.column009" id="orderCreateTime" placeholder="订单创建时间..." cssClass="form-control" cssStyle="width: 180px;display: inline-block;" />
		</div>
		<div class="form-group form-group-query">
			<label for="projectStartTime" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.order.create.time" /></label>
	    	<s:textfield  name="project.projectStartTime" id="projectStartTime" placeholder="项目开始时间..." cssClass="form-control" cssStyle="width: 180px;display: inline-block;" disabled="true"/>
		</div>
		<div class="form-group form-group-query">
			<label for="projectCategory" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.projectCategory" /></label>
	    	<s:select list="#{10:'直签', 20:'非直签'}" cssClass="form-control writeTxt engineeManagerTxt" cssStyle="width: 180px;display: inline-block;"
	    		name="project.column011" id="column011"></s:select>
		</div>
        <div class="form-group form-group-query">
            <label for="compId" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.company" /></label>
            <s:select list="companyList" cssClass="form-control writeTxt engineeManagerTxt" cssStyle="width: 180px;display: inline-block;"
                headerKey="" headerValue="--请选择--" listKey="id" listValue="name"
                name="project.compId" id="compId"></s:select>
        </div>
        <br/>
		<div class="form-group form-group-query">
			<label for="serviceManagerCodeforjson" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.serviceManager" /></label>
	    	<s:textfield id="serviceManagerCodeforjson" placeholder="服务经理..." cssClass="form-control writeTxt engineeManagerTxt" name="project.serviceManagerCodeforjson"
	    		cssStyle="width: 180px;display: inline-block;" onfocus="fillservicemanager()" onblur="fillservicemanager()" />
			<s:textfield name="project.serviceManagerCode" type="hidden" id="serviceManagerCode"></s:textfield>
		</div>
		<div class="form-group form-group-query">
			<label for="column010" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.projectType" /></label>
	    	<s:select list="#{10:'普通类', 20:'工程类'}" cssClass="form-control writeTxt engineeManagerTxt" cssStyle="width: 180px;display: inline-block;"
	    		name="project.column010" id="column010"></s:select>
		</div>
		<div class="form-group form-group-query">
			<label for="programManagerCodeforjson" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.programManager"/>A</label>
	    	<s:textfield id="programManagerCodeforjson" placeholder="项目经理A..." cssClass="form-control writeTxt serviceManagerTxt" name="project.programManagerCodeforjson"
	    		cssStyle="width: 180px;display: inline-block;" onfocus="fillprogrammanager(this)" onblur="fillprogrammanager(this)" />
			<s:textfield name="project.programManagerCode" type="hidden" id="programManagerCode"></s:textfield>
		</div>
		<div class="form-group form-group-query">
			<label for="programManagerCodeforjsonB" style="width: 90px;"><s:text name="pm.project.programManager"/>B</label>
	    	<s:textfield id="programManagerCodeforjsonB" placeholder="项目经理B..." cssClass="form-control writeTxt serviceManagerTxt" name="project.programManagerCodeforjsonB" 
	    		cssStyle="width: 180px;display: inline-block;" onfocus="fillprogrammanager(this)" onblur="fillprogrammanager(this)" />
			<s:textfield name="project.programManagerCodeB" type="hidden" id="programManagerCodeB"></s:textfield>
		</div>
		<br/>
		<div class="form-group form-group-query">
			<label for="column012" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.implement" /></label>
			<s:select name="project.column012" id="column012" cssClass="form-control writeTxt engineeManagerTxt programManagerTxt" onchange="changeProgramManagerTxt(this)" 
				list="#{0:'原厂直服',1:'原厂督导',4:'原厂集成',3:'代理商自服' }" style="width: 180px;display: inline-block;"/>
		</div>
		<!-- 出货渠道注销掉 -->
		<%-- <div class="form-group form-group-query" style="display: none;" id="programManagerDiv1">
			<label for="deliverChannel" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.deliverChannel" /></label>
	    	<s:textfield name="project.deliverChannel" id="deliverChannel" placeholder="出货渠道..." cssClass="form-control writeTxt engineeManagerTxt programManagerTxt" cssStyle="width: 180px;display: inline-block;" />
		</div> --%>
		<div class="form-group form-group-query" style="display: none;" id="programManagerDiv3">
			<label for="agentChannel" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.agentChannel" /></label>
	    	<s:textfield name="project.agentChannel" id="agentChannel" placeholder="施工代理商..." cssClass="form-control writeTxt engineeManagerTxt programManagerTxt" cssStyle="width: 180px;display: inline-block;" />
		</div>
		<div class="form-group form-group-query" style="display: none;" id="programManagerDiv2">
			<label for="serviceChannel" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.serviceChannel" /></label>
	    	<s:textfield name="project.serviceChannel" id="serviceChannel" placeholder="服务提供商..." cssClass="form-control writeTxt engineeManagerTxt programManagerTxt" cssStyle="width: 180px;display: inline-block;" />
		</div>
		<div class="form-group form-group-query">
			<label for="column013" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.finalCustomerName" /></label>
	    	<s:textfield name="project.column013" id="column013" placeholder="最终客户..." cssClass="form-control writeTxt engineeManagerTxt programManagerTxt" cssStyle="width: 180px;display: inline-block;" />
		</div><br/>
		<div class="form-group form-group-query">
			<label for="notGrantTailCause" style="width: 90px;"><span class="redmark">*</span><s:text name="pm.project.notGrantTailCause" /></label>
			<textarea class="form-control" rows="3" name="project.column008" id="notGrantTailCause" style="width: 400px;" ></textarea>
		</div>
		<div class="form-group form-group-query" >
			<button type="button" onclick="checkSubmit()" class="btn btn-info btn-block writeTxt submitBtn" style="width: 60px;"><s:text name="pm.project.btn"></s:text></button>
		</div>
		<div class="form-group form-group-query" >
			<button type="button" onclick="denySubmit()" class="btn btn-default btn-block writeTxt submitBtn" style="width: 80px;"><s:text name="pm.deny.tail"></s:text></button>
		</div>
	</s:form>
</body>
</html>
