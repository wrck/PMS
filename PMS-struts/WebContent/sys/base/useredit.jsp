<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<head>
<s:head />
<dp:base />
<meta name="menu" content="SysMenuGroupStaticDataManage">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.basemanage' />">
<meta name="function" content="<s:text name='sys.leftmenu.usermanage' />">


<style type="text/css">
.buttonDiv{
	float:left;
	margin-left: 50px;
	margin-bottom: 30px;
	margin-top: 50px;	
}
.ui-multiselect {
	padding: 2px 0 2px 4px;
	text-align: left;
	min-height: 35px;
	min-width: 382px;
}
</style>
<link rel="stylesheet" type="text/css" href="multiselect/jquery.multiselect.filter.css" />
<script type="text/javascript" src="multiselect/jquery.multiselect.filter.js"></script>
<script type="text/javascript">

$(function(){
	var menus = document.getElementsByName("menus");
	var usermenuids = document.getElementById("usermenuids");
	var defaultPage="<s:property value='user.defaultPage'/>";
	$("#defaultPage").empty();
	var ids = new Array();
	ids = usermenuids.value.split(",");
	for(var i = 0;i < ids.length;i++){
		for(var j = 0;j < menus.length;j++){
			if(ids[i] == menus[j].value){
				menus[j].checked = true;
				var menuName=menus[j].getAttribute("checkValue");
				if(menuName!=0){	
					$("#defaultPage").append('<option value="'+menus[j].value+'">'+menuName+'</option>');	
				}
				if(menus[j].value==defaultPage){
					$("#defaultPage option[value='"+defaultPage+"']").attr("selected", "selected");
				}
			}
		}
	}
	var userIdValue="<s:property value='user.id'/>";
	$("#userId").val(userIdValue);
	var userStatus='<s:property value="user.status"/>';
	if(userStatus!=1){
		$("#status0").attr("checked","checked");
	}
	setselect("userarea","userareahide");
	multiselect("userarea","userareahide");
	
	setselect("role","rolehide",";");
	multiselect("role","rolehide",";");
	
	if ($.fn.multiselectfilter) {
		$("#userarea").multiselectfilter({
			label: "搜索",
			autoReset: true,
			placeholder: "支持模糊搜索",
		    filter: function(event, matches){
		        if( !matches.length ){
		            // do something
		        }
		    }
		});
	}
});
function checkAllCode(_this,prefix,code){
	if($(_this).is(':checked')){
		var obj=$("input[check='"+prefix+code+"']").toArray();
		$("input[check='"+prefix+code+"']").prop("checked","checked"); 
		for(var i=0;i<obj.length;i++){
			checkSuperCode(obj[i],'supermenu_',obj[i].value);
		}		
	}else {
		var obj=$("input[check='"+prefix+code+"']").toArray();
		$("input[check='"+prefix+code+"']").removeAttr("checked"); 
		for(var i=0;i<obj.length;i++){
			checkSuperCode(obj[i],'supermenu_',obj[i].value);
		}
	}
	var usermenuids = document.getElementById("usermenuids");
	usermenuids.value = "";
	$("#defaultPage").empty();
	var menus = document.getElementsByName("menus");
	for(var k = 0;k < menus.length;k++){
		if(menus[k].checked){
			usermenuids.value += menus[k].value+",";
			var menuName=menus[k].getAttribute("checkValue");
			if(menuName!=0){
				$("#defaultPage").append('<option value="'+menus[k].value+'">'+menuName+'</option>');
			}
		}
	}
	usermenuids.value = usermenuids.value.substring(0,usermenuids.value.length-1);
}

function checkSuperCode(_this,prefix,code){
	if($(_this).is(':checked')){ 
        $("input[check='"+prefix+code+"']").prop("checked",true);
    } else if (prefix == "supermenu_" && $("input[check='usermenu_" + code + "']:checked").length > 0) { 
        $("input[check='"+prefix+code+"']").prop("checked",true);
    } else {
        $("input[check='"+prefix+code+"']").attr("checked",false);
    } 
	var usermenuids = document.getElementById("usermenuids");
	usermenuids.value = "";
	$("#defaultPage").empty();
	var menus = document.getElementsByName("menus");
	for(var k = 0;k < menus.length;k++){
		if(menus[k].checked){
			usermenuids.value += menus[k].value+",";
			var menuName=menus[k].getAttribute("checkValue");
			if(menuName!=0){
				$("#defaultPage").append('<option value="'+menus[k].value+'">'+menuName+'</option>');
			}
		}
	}
	usermenuids.value = usermenuids.value.substring(0,usermenuids.value.length-1);
}
var oldRoles = "${user.roleids}";
function rolesCheck(){
	var newRoles = $("#role").val();
	var isEffective = $("input[name='user.status']:checked").val();
	if(newRoles){
		newRoles = ";" + newRoles.join(";,;") + ";";
	}else{
		newRoles = "";
	}
	var type =new Array();//项目角色变更参数
	if(isEffective == '0'){
		if(oldRoles.indexOf(";11;")!=-1){
			type.push("service");
		}
		if(oldRoles.indexOf(";12;")!=-1){
			type.push("program");
		}
	}else{
		// 原来有服务经理权限，现在没有
		if(oldRoles.indexOf(";11;")!=-1 && newRoles.indexOf(";11;")==-1){
			type.push("service");
		}
		if(oldRoles.indexOf(";12;")!=-1 && newRoles.indexOf(";12;")==-1){
			type.push("program");
		}
	}
	if(type.length>1){
		type = "both";
	}else{
		type = type.pop();
	}
	var confirmInfo = "";
	var promptInfo = ""; 
	switch(type){
	case "service":
		confirmInfo = "该用户原来拥有服务经理角色，是否需要更新所管项目的服务经理？";
		promptInfo = "请输入新指派的服务经理用户名称："; 
		break;
	case "program":
		confirmInfo = "该用户原来拥有项目经理角色，是否需要更新所管项目的项目经理？";
		promptInfo = "请输入新指派的项目经理用户名称："; 
		break;
	case "both":	
		confirmInfo = "该用户原来拥有服务经理、项目经理角色，是否需要更新所管项目的服务经理和项目经理？";
		promptInfo = "请输入新指派的服务经理和项目经理用户名称："; 
		break;
	default:return true;
	}
	var newMemberCode ="";
	var changeMember = true;
	while(changeMember && !newMemberCode){
		if(confirm(confirmInfo)){
			changeMember = true;
			newMemberCode = prompt(promptInfo);
			if(newMemberCode == null)
				return false;
			if(newMemberCode){
				newMemberCode = newMemberCode.replace(/\s/g,"");
				$.ajax({
					url:"base/checkMemberCode",
					type:"post",
					dataType:"json",
					data:{'username':newMemberCode},
					success:function(data){
						var user = data.user;
						if(user){
							$("#newMemberCode").val(user.username+"-"+user.realName);
							$("#changeType").val(type);
							$("#dpName").val($("#dpNo option:selected").text());
							$("form").submit();
						}else{
							$("#submitButton").removeAttr("disabled");
							alert("该用户名不存在，请输入有效的用户名称！");
						}
					}
				})
			}
		}else{
			changeMember = false;
		}
	}
	if(!changeMember){
		return true;
	}
}

function checkallsub(){	
	var submitButtonObj=$("#submitButton");
	submitButtonObj.attr("disabled","disabled");	
	if(minxueCheckSubmit($("#username").val(),"用户名称",submitButtonObj,new Array(1,1))){
		return false;
	}
	if(minxueCheckSubmit($("#name").val(),"真实姓名",submitButtonObj,new Array(1,1))){
		return false;
	}
	if(minxueCheckSubmit($("#mail").val(),"用户邮箱",submitButtonObj,new Array(1,1))){
		return false;
	}
	if(minxueCheckSubmit(($("#usermenuids").val().replace(/,/g,"-")),"用户菜单权限",submitButtonObj,new Array(1,0))){
		return false;
	}
	if(minxueCheckSubmit($("#defaultPage").val(),"默认登录页面",submitButtonObj,new Array(1,0))){
		return false;
	}
	var status = $("input:radio[name='user.status']:checked").val();
	if( status == 1){
		if(!confirm("确定修改用户数据么？")){
			$("#submitButton").removeAttr("disabled");
			return false;
		}
	}else{
		if(!confirm("确定要失效该账号么？失效后用户将被限制登录系统")){
			$("#submitButton").removeAttr("disabled");
			return false;
		}
	}
	if(rolesCheck()){
		$("form").submit();
	}else{
		$("#submitButton").removeAttr("disabled");
	}
};
function restPassword() {
	$("#restPasswordButton").attr("disabled", true);
	var userId = $("#userId").val();
	var userName = $("#username").val() + "-" + $("#name").val();
	if (confirm("确认重置【" + (userName.length > 1 ? userName : "该用户") + "】的密码?")) {
    	$.ajax({
            url:"resetPassword.action",
            type:"post",
            dataType:"json",
            data:{'user.id':userId},
            success:function(data){
                var result = data.result;
                if(result){
                	alert("密码重置成功，请查收邮箱！");
                }else{
                    alert("密码重置失败！");
                }
            },
            complete:function() {
            	$("#restPasswordButton").removeAttr("disabled");
            }
        })
	} else {
		$("#restPasswordButton").removeAttr("disabled");
	}
}
$("#submitButton").click(function(){
	checkallsub();
});
</script>
</head>
<body>
<div class="divHeader div-bottom" >
	<img src="images/right_zhishi.gif" border="0">
				<s:text name="sys.user.edit"></s:text>
</div> 
<s:form method="post" action="base/UserEdit.action" id="mainForm" cssClass="form-horizontal" name="mainForm">
<s:hidden name="usermenuids" id="usermenuids"></s:hidden>
<s:hidden name="user.id" id="userId"></s:hidden>
<div class="panel panel-default">
   <div class="panel-body">
      <div class="form-group">
	    <label  class="col-sm-1 control-label"><span class="redmark">*</span><s:text name="sys.user.name"></s:text><span>:</span></label>
	    <div class="col-sm-4">
	      <s:textfield cssClass="form-control" name="user.username" readonly="true" id="username" />
	    </div>
    </div>
     <div class="form-group">
	    <label  class="col-sm-1 control-label"><span class="redmark">*</span><s:text name="sys.user.realname"></s:text><span>:</span></label>
	    <div class="col-sm-4">
	      <s:textfield name="user.realName" id="name" cssClass="form-control" />
	    </div>
    </div>
     <div class="form-group">
	    <label  class="col-sm-1 control-label"><span class="redmark">*</span><s:text name="sys.user.rolename"></s:text><span>:</span></label>
	    <div class="col-sm-4">
	    	<s:hidden name="user.roleids" id="rolehide"></s:hidden>
	    	<s:hidden name="newMemberCode" id="newMemberCode"/>
	    	<s:hidden name="changeType" id="changeType"/>
	    	<s:hidden name="user.dpName" id="dpName"/>
			<select id="role" multiple="multiple" style="height:25px">
					<s:iterator value="rolelist" var="v">
						<option value="<s:property value='#v.id'/>"><s:property
								value="#v.roleName" /></option>
					</s:iterator>
			</select>						
	    </div>
    </div>
      <div class="form-group">
	    <label  class="col-sm-1 control-label"><s:text name="sys.user.department"></s:text><span>:</span></label>
	    <div class="col-sm-4">
	      <s:select name="user.dpNo" id="dpNo" listKey="departmentNum" cssClass="form-control" headerKey="" headerValue="--请选择--"
									listValue="departmentName" list="%{departments}"  theme="simple" />
	    </div>
    </div>
   
     <div class="form-group">
	    <label  class="col-sm-1 control-label"><span class="redmark">*</span><s:text name="sys.user.email"></s:text><span>:</span></label>
	    <div class="col-sm-4">
	      <s:textfield name="user.email" id="mail" cssClass="form-control" />
	    </div>
        <c:if test="${currentDisplayUser.isHasAnyRole(1,10,13) && !currentIsCas}">
        <div class="col-sm-1">
            <button type="button" id="restPasswordButton" onclick="restPassword()" class="btn btn-danger btn-block btn-sm form-control">重置密码</button>
        </div>
        </c:if>
    </div>
   </div>
</div>
<div class="panel panel-default">
   <div class="panel-body">	
	    <div class="form-group">
		    <label  class="col-sm-1 control-label"><span class="redmark">*</span><s:text name="sys.user.menue"></s:text><span>:</span></label>
		    <div class="col-sm-10">
		      <s:if test="userMenuList.size() > 0">
					<s:iterator value="userMenuList" id="val" status="index">
						<s:if test="%{#val.menuLevel == 1}">
							<label style="color:black;font-weight: bold" class="checkbox-inline">
								<s:checkbox checkValue="0"  name="menus" cssClass="form-control-checkbox" check="supermenu_%{#val.id}" onclick="checkAllCode(this,'usermenu_','%{#val.id}')" fieldValue="%{#val.id}"></s:checkbox>
								<s:property value="#val.menuName"/>
							</label>
							<br/>
							<s:iterator value="#val.userMenuList" id="v" status="ind">
								<label class="checkbox-inline" >
									<s:checkbox checkValue="%{#v.menuName}"  name="menus" cssClass="second_%{#v.superId} form-control-checkbox" check="usermenu_%{#v.superId}" onclick="checkSuperCode(this,'supermenu_','%{#v.superId}')" fieldValue="%{#v.id}"></s:checkbox>
									<s:property value="#v.menuName"/>
								</label>
								<s:if  test="#v.userMenuList.size() > 0">
									<br/>
								</s:if>
								<s:iterator value="#v.userMenuList" id="sub" status="ind">
									<label class="checkbox-inline">
										<s:checkbox checkValue="%{#sub.menuName}" cssClass="form-control-checkbox"  name="menus" check="supermenu_%{#sub.superId}" onclick="checkSuperCode(this,'supermenu_','%{#v.superId}')" fieldValue="%{#sub.id}"></s:checkbox>
										<s:property value="#sub.menuName"/>	
									</label>
								</s:iterator>
							</s:iterator>
							<br/>
						</s:if>
					</s:iterator>
				</s:if>
		    </div>
	    </div>
	    <div class="form-group">
	     	<label  class="col-sm-1 control-label"><span class="redmark">*</span><s:text name="sys.user.area"></s:text><span>:</span></label>
	    	<div class="col-sm-4">
	    		<s:hidden name="user.areapower" id="userareahide"></s:hidden>
	    		<%-- <s:select name="user.dpNo" id="dpNo" listKey="departmentNum" cssClass="form-control" headerKey="" headerValue="--请选择--"
									listValue="departmentName" list="%{departments}"  theme="simple" /> --%>
	    		<select id="userarea" multiple="multiple" >
						<s:iterator value="departments" var="v">
							<option value="<s:property value='#v.departmentNum'/>"><s:property
									value="#v.departmentName" /></option>
						</s:iterator>
				</select>
	    	</div>
	    </div>
	      <div class="form-group">
		    <label  class="col-sm-1 control-label"><span class="redmark">*</span><s:text name="sys.user.defaultPage"></s:text><span>:</span></label>
		    <div class="col-sm-4">
		      <select name="user.defaultPage" id="defaultPage" class="form-control"></select>
		    </div>
	    </div>
        <div class="form-group">
            <label class="col-sm-1 control-label"><span class="redmark">*</span>序列号查项目<span>:</span></label>
            <div class="col-sm-4">
              <s:radio list="#{true: '禁用', false : '启用'}" value="%{user.customStrInfo.disableQueryProjectByBarcode != null ? user.customStrInfo.disableQueryProjectByBarcode : false}"  name="user.customStrInfo.disableQueryProjectByBarcode" id="disableQueryProjectByBarcode">
              </s:radio>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-1 control-label"><span class="redmark">*</span>项目名称模糊查项目<span>:</span></label>
            <div class="col-sm-4">
              <s:radio list="#{true: '禁用', false : '启用'}" value="%{user.customStrInfo.disableFuzzyQueryProjectByProjectName != null ? user.customStrInfo.disableFuzzyQueryProjectByProjectName : false}"  name="user.customStrInfo.disableFuzzyQueryProjectByProjectName" id="disableFuzzyQueryProjectByProjectName">
              </s:radio>
            </div>
        </div>
   </div>
</div>  
    
     <div class="form-group">
	    <label  class="col-sm-1 control-label"><span class="redmark">*</span><s:text name="sys.user.status"></s:text><span>:</span></label>
	   
    	<label class="radio-inline" for="status1" >
     		 <input type="radio" class="form-control-radio" name="user.status"  id="status1" checked="checked" value="1">有效
	  	</label>
	 	<label class="radio-inline" for="status0" >
	  		<input type="radio" class="form-control-radio" name="user.status"  id="status0" value="0">失效
    	</label>
	    
    </div>
     <div class="form-group">
        <label  class="col-sm-1 control-label">&nbsp;</label>
	    <div class="col-sm-1">
	       <button type="button" id="submitButton" onclick="checkallsub()" class="btn btn-default  btn-block btn-sm form-control"><s:text name='sys.confirm' /></button>
	    </div>
	    <div class="col-sm-1">
	       <button type="button" onclick="javascript:history.go(-1)"  class="btn btn-default  btn-block btn-sm form-control"><s:text name='sys.back' /></button>
	    </div>
    </div>
	
</s:form>
</body>
</html>