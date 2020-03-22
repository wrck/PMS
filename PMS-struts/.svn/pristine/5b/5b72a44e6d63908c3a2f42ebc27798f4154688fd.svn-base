<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<s:head />
<dp:base />
<meta name="menu" content="SysMenuGroupStaticDataManage">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.basemanage' />">
<meta name="function" content="<s:text name='sys.leftmenu.rolemanage' />">
<style type="text/css">
.buttonDiv{
	float:left;
	margin-left: 50px;
	margin-bottom: 30px;
	margin-top: 50px;	
}
.line{
	width:100%;
	border-top: 1px solid #CCCCCC;
}
</style>
<script type="text/javascript">
$(function(){
	var roleName='<s:property value="role.roleName"/>';
	$("#roleName").val(roleName);
	var roleStatus='<s:property value="role.status"/>';
	if(roleStatus!=1){
		$("#status0").attr("checked","checked");
	}
	var roleId='<s:property value="role.id"/>';
	$("#roleId").val(roleId);
	var roleRemark='<s:property value="role.roleRemark"/>';
	$("#roleRemark").val(roleRemark);
	var listSize='<s:property value="rolemenuidList.size()"/>';
	for(var i=0;i<listSize;i++){
		var roleMenuValue=$("#rol_"+i).val();
		$("input[name='menus']").each(function(j){
			var menusValue=$(this).val();
			if(menusValue==roleMenuValue){
				$(this).prop("checked","checked");
				var roleMenuPowerVal=$("#rol_"+i).attr("roleMenuPower");
				var roleMenuPowerArr=roleMenuPowerVal.split(",");
				for(var k=0;k<roleMenuPowerArr.length;k++){
					$("input[name='mod_"+menusValue+"']").each(function(j){
						if($(this).val()==roleMenuPowerArr[k]){
							$(this).prop("checked","checked");
						}
					});
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
}

function checkSuperCode(_this,prefix,code){
	if($(_this).is(':checked')){ 
		$("input[check='"+prefix+code+"']").prop("checked",true);
	}else {
		$("input[check='"+prefix+code+"']").attr("checked",false);
	} 
}

function checkModule(_this,obj){
	if($(_this).is(':checked')){
		$("#"+obj).attr("checked",true);
	}
}

function checkRolemenuids(){
	$("#rolemenuids").empty();
	var index=0;
	$("input[name='menus']:checked").each(function(i){
		if($(this).attr("checkValue")!=0){
			var menusValue=$(this).val();
			/* var html="<s:hidden name='rolemenuidList["+i+"].menuId' value='"+menusValue+"'></s:hidden>"; */
			var html='<input type="hidden" name="rolemenuidList['+index+'].menuId" value="'+menusValue+'"></input>';
			$("#rolemenuids").append(html);
			var modValue="";
			$("input[name='mod_"+menusValue+"']:checked").each(function(j){
				modValue+=$(this).val()+",";
			});
			if(modValue!=""){
				var subhtml='<input type="hidden" name="rolemenuidList['+index+'].menuPower" value="'+modValue+'"></input>';
				$("#rolemenuids").append(subhtml);
				index++;
			}else{
				alert("角色菜单权限选择错误");
				return false;
			}
		}
		
	});
	if(index==0){
		alert("角色菜单权限选择错误");
		return false;
	}
	return true;
}

function checkallsub(){	
	$("#submitButton").attr("disabled","disabled");
	if(!checkRolemenuids()){
		$("#submitButton").removeAttr("disabled");
		return false;
	};
	if($("#roleName").val()==null||$("#roleName").val()==""){
		alert("角色名称填写错误");
		$("#submitButton").removeAttr("disabled");
		return false;
	}
	if ($("#roleName").val().indexOf(" ") >=0) {
		 alert("输入有空格！");
		 $("#submitButton").removeAttr("disabled");
		 return false;
	 } 
	if($("input:radio[name='role.status']:checked").val()==1){
		var r=confirm("确定要修改角色数据么？");
		if(!r){
			$("#submitButton").removeAttr("disabled");
			return false;
		}
	} 
	return true;
	
}

</script>
</head>
<body>
<div class="div-bottom divHeader" >
	<img src="images/right_zhishi.gif" border="0"><s:text name="sys.role.addDetail"></s:text>
</div>
<s:form method="post" action="base/RoleEditSubmit.action" id="mainForm" onsubmit="return checkallsub();" name="mainForm" cssClass="form-horizontal">
	<input type="hidden" id="roleId" name="role.id">
	<s:if test="rolemenuidList.size() > 0">
	<s:iterator value="rolemenuidList" id="rol" status="rolstat">
		<s:hidden roleMenuPower="%{#rol.menuPower}" id="rol_%{#rolstat.index}" value="%{#rol.menuId}"></s:hidden>
	</s:iterator>
	</s:if>
	<div class="panel panel-default">
   		<div class="panel-body">
   				<div class="form-group">
					 <label for="roleName" class="col-sm-1 control-label"><span class="redmark" >*</span><s:text name="sys.role.name"></s:text></label>
				    <div class="col-sm-3">
				      <s:textfield type="text" cssClass="form-control" name="role.roleName" id="roleName" placeholder="请填入角色名称"/>
				    </div>
				</div>
				<div class="form-group">
					<label for="permission" class="col-sm-1 control-label"><span class="redmark" >*</span><s:text name="sys.role.permission"></s:text></label>
					<div class="col-sm-3">
						<s:if test="userMenuList.size() > 0">
							<s:iterator value="userMenuList" id="val" status="index">
								<s:if test="%{#val.menuLevel == 1}">
									<s:checkbox checkValue="0" cssClass="form-control-checkbox"  name="menus" check="supermenu_%{#val.id}" onclick="checkAllCode(this,'usermenu_','%{#val.id}')" fieldValue="%{#val.id}"></s:checkbox>
									<span style="color:black;font-weight: bold"><s:property value="#val.menuName"/></span>
									<br/>
									<s:iterator value="#val.userMenuList" id="v" status="ind">
										<s:checkbox checkValue="%{#v.menuName}" cssClass="form-control-checkbox" name="menus" id="mod_%{#v.id}" check="usermenu_%{#v.superId}" onclick="checkSuperCode(this,'supermenu_','%{#v.id}')" fieldValue="%{#v.id}"></s:checkbox>
										<s:property value="#v.menuName"/>
										<span>&nbsp;&nbsp;</span>
										<s:checkbox name="mod_%{#v.id}" cssClass="form-control-checkbox" onclick="checkModule(this,'mod_%{#v.id}')" fieldValue="8"></s:checkbox>增加
										<s:checkbox name="mod_%{#v.id}" cssClass="form-control-checkbox" onclick="checkModule(this,'mod_%{#v.id}')" fieldValue="1"></s:checkbox>删除
										<s:checkbox name="mod_%{#v.id}" cssClass="form-control-checkbox" onclick="checkModule(this,'mod_%{#v.id}')" fieldValue="4"></s:checkbox>查询
										<s:checkbox name="mod_%{#v.id}" cssClass="form-control-checkbox" onclick="checkModule(this,'mod_%{#v.id}')" fieldValue="2"></s:checkbox>更新
										<br/>
									<s:iterator value="#v.userMenuList" id="sub" status="ind">
										<s:checkbox checkValue="%{#sub.menuName}" cssClass="form-control-checkbox" name="menus" id="mod_%{#sub.id}" check="supermenu_%{#sub.superId}" onclick="checkSuperCode(this,'supermenu_','%{#sub.id}')" fieldValue="%{#sub.id}"></s:checkbox>
										<s:property value="#sub.menuName"/>	
										<span>&nbsp;&nbsp;</span>
										<s:checkbox name="mod_%{#sub.id}" cssClass="form-control-checkbox" onclick="checkModule(this,'mod_%{#sub.id}')" fieldValue="8"></s:checkbox>增加
										<s:checkbox name="mod_%{#sub.id}" cssClass="form-control-checkbox" onclick="checkModule(this,'mod_%{#sub.id}')" fieldValue="1"></s:checkbox>删除
										<s:checkbox name="mod_%{#sub.id}" cssClass="form-control-checkbox" onclick="checkModule(this,'mod_%{#sub.id}')" fieldValue="4"></s:checkbox>查询
										<s:checkbox name="mod_%{#sub.id}" cssClass="form-control-checkbox" onclick="checkModule(this,'mod_%{#sub.id}')" fieldValue="2"></s:checkbox>更新
										<br/>
									</s:iterator>
									</s:iterator>
									<br/>
								</s:if>
							</s:iterator>
						</s:if>
					</div>
				</div>
				<div class="form-group">
					 <label for="status" class="col-sm-1 control-label"><span class="redmark" >*</span><s:text name="sys.role.status"></s:text></label>
					<div class="col-sm-3">
						<input type="radio" class="form-control-radio" name="role.status" id="status1" checked="checked" value="1"><label for="status1">有效</label>
						<input type="radio" class="form-control-radio" name="role.status" id="status0" value="0"><label for="status0">失效</label>
					</div>
				</div>
				<div class="form-group">
					 <label for="roleRemark" class="col-sm-1 control-label"><s:text name="sys.role.remark"></s:text></label>
					 <div class="col-sm-3">
					 	<textarea class="form-control" rows="3" id="roleRemark" name="role.roleRemark"></textarea>
					 </div>
				</div>
				<div id="rolemenuids">
					
				</div>
   		</div>
   </div>
	 <div class="form-group">
        <label  class="col-sm-1 control-label">&nbsp;</label>
	    <div class="col-sm-1">
			<button type="submit" class="btn btn-default  btn-block btn-sm form-control"><s:text name='sys.confirm' /></button>
	    </div>
	     <div class="col-sm-1">
			<button type="button" class="btn btn-default  btn-block btn-sm form-control" onclick="javascript:history.go(-1)"><s:text name='sys.back' /></button>
	    </div>
    </div>
	
</s:form>
</body>
</html>