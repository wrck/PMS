<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
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

function checkallsub(){	
	var submitButtonObj=$("#submitButton");
	submitButtonObj.attr("disabled","disabled");	
	if(minxueCheckSubmit($("#username").val(),"用户名称",submitButtonObj,new Array(1,1))){
		return false;
	}
	if(minxueCheckSubmit($("#realName").val(),"真实姓名",submitButtonObj,new Array(1,1))){
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

		if(!confirm("确定用户生效？")){
			$("#submitButton").removeAttr("disabled");
			return false;
		}
	return true;
	
}
$(function(){
	
	setselect("role","rolehide");
	multiselect("role","rolehide");
	
	setselect("userarea","userareahide");
	multiselect("userarea","userareahide");
	
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

//下拉选择多选
function multiselect(selectID, inputID) {
	$("#" + selectID).multiselect({
		header : true,
		height : 200,
		minWidth : 385,
		selectedList : 50,//预设值最多显示10被选中项
		hide : [ "explode", 500 ],
		checkAllText : "全选",
		uncheckAllText : '取消',
		noneSelectedText : '==请选择==',
		close : function() {
			var values = $("#" + selectID).val();
			$("#" + inputID).val(values);
		}
	});
}
//下拉默认多选
function setselect(selectID , inputID){
	var input_value = $("#"+inputID).val();
	 
	 $("#"+selectID + "  option").each(function(){
		 if(input_value.toString().indexOf(";"+$(this).val().toString()+";")> -1){
			 $(this).attr("selected", "selected"); 
		 }	
	 });
} 


function checkUsername(){
	var username = $("#username").val();
	if(username != ""){
		$.ajax({
			url:"checkUsername.action",
			type:"post",
			dataType:"json",
			data:{username:username},
			success:function(data){
				var result = data.result;
				if(result == 0){
					$("#usernamemark").text("该账号可以使用");
					$("#usernamemark").css({"color":"green"});
				}else{
					$("#usernamemark").text("该账号已经存在，请重新输入");
					$("#username").val("");
					$("#username").focus();
					$("#usernamemark").css({"color":"red"});
				}
			},
		});
	}
}
</script>
</head>
<body>
<div class="divHeader div-bottom" >
	<img src="images/right_zhishi.gif" border="0">
				<s:text name="sys.user.addDetail"></s:text>
</div> 
<s:form method="post" action="base/UserAdd.action" id="mainForm" onsubmit="return checkallsub();" cssClass="form-horizontal" name="mainForm">
<s:hidden name="usermenuids" id="usermenuids"></s:hidden>
	<div class="panel panel-default">
	   <div class="panel-body">
	      <div class="form-group">
		    <label for="username" class="col-sm-1 control-label"><span class="redmark">*</span><s:text name="sys.user.name"></s:text><span>:</span></label>
		    <div class="col-sm-4">
		      <s:textfield cssClass="form-control" name="user.username" id="username" onblur="checkUsername()" placeholder="请输入用户账号"/>
		    </div>
		    <div class="col-sm-2"> 
		     	<span id="usernamemark"></span>
		    </div>
	    </div>
	      <div class="form-group">
		    <label for="realName"  class="col-sm-1 control-label"><span class="redmark">*</span><s:text name="sys.user.realname"></s:text><span>:</span></label>
		    <div class="col-sm-4">
		      <s:textfield name="user.realName" id="realName" cssClass="form-control" placeholder="请输入真实姓名" />
		    </div>
	    </div>
	     <div class="form-group">
		    <label for="role"  class="col-sm-1 control-label"><span class="redmark">*</span><s:text name="sys.user.rolename"></s:text><span>:</span></label>
		    <div class="col-sm-4">
		    	<s:hidden name="user.roleids" id="rolehide"></s:hidden>
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
		    <label for="mail" class="col-sm-1 control-label"><span class="redmark">*</span><s:text name="sys.user.email"></s:text><span>:</span></label>
		    <div class="col-sm-4">
		      <s:textfield name="user.email" id="mail" cssClass="form-control" placeholder="请输入用户邮箱 "/>
		    </div>
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
								<label class="checkbox-inline" >
									<s:checkbox checkValue="0" cssClass="form-control-checkbox" name="menus" check="supermenu_%{#val.id}" onclick="checkAllCode(this,'usermenu_','%{#val.id}')" fieldValue="%{#val.id}"></s:checkbox>
									<span style="color:black;font-weight: bold"><s:property value="#val.menuName"/></span>
								</label>
								<br/>
								<s:iterator value="#val.userMenuList" id="v" status="ind">
									<label class="checkbox-inline" >
									   <s:checkbox checkValue="%{#v.menuName}" cssClass="form-control-checkbox" name="menus"  check="usermenu_%{#v.superId}" onclick="checkSuperCode(this,'supermenu_','%{#v.superId}')" fieldValue="%{#v.id}"></s:checkbox>
									   <s:property value="#v.menuName"/>
									</label>
									<s:if  test="#v.userMenuList.size() > 0">
										<br/>
									</s:if>
									<s:iterator value="#v.userMenuList" id="sub" status="ind">
										<label class="checkbox-inline" >
										  <s:checkbox checkValue="%{#sub.menuName}" cssClass="form-control-checkbox"  name="menus" check="supermenu_%{#sub.superId}" onclick="checkSuperCode(this,'supermenu_','%{#sub.superId}')" fieldValue="%{#sub.id}"></s:checkbox>
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
			      <select name="user.defaultPage" id="defaultPage" class="form-control">
			      	<option>请选择</option>
			      </select>
			    </div>
		    </div>
   		</div>
   	</div>
   	 <div class="form-group">
        <label  class="col-sm-1 control-label">&nbsp;</label>
	    <div class="col-sm-1">
		   	<button type="submit" id="submitButton" style="width: 80px" class="btn btn-default  btn-block btn-sm"><s:text name='sys.confirm' /></button>
	    </div>
	     <div class="col-sm-1">
			<button type="button" style="width: 80px" class="btn btn-default btn-block btn-sm" onclick="javascript:history.go(-1)"><s:text name='sys.back' /></button>
	    </div>
    </div>	
</s:form>
</body>
</html>