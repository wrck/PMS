<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<head>
<s:head />
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.powermanage' />">
<meta name="function" content="<s:text name='sys.project.management' />">
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
.tip{
	margin: 15px 0;
	padding:15px;
	border: 1px solid #ddd;
	border-radius: 5px;
	height:195px;
}
.tip p{
	text-indent:2rem;
}
.tip ol{
	padding-left:2rem;
}
</style>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript">
	var usernameArr = new Array();
	var realnameArr = new Array();
	var dpNoArr = new Array();
	
	var usernameArr2 = new Array();
	var realnameArr2 = new Array();
	var roleid = 11;
	$(document).on('change','#changeType',function(){
		var changeType = $("#changeType").val();
		if(changeType == 'program'){
			roleid = 12;
			$("label[for='oldMemberCode']").html('<span class="redmark">*</span>原项目经理:');
			$("label[for='newMemberName']").html('<span class="redmark">*</span>新项目经理:');
		}else if(changeType == 'service'){
			roleid = 11;
			$("label[for='oldMemberCode']").html('<span class="redmark">*</span>原服务经理:');
			$("label[for='newMemberName']").html('<span class="redmark">*</span>新服务经理:');
		}else{
			roleid = 11;
			$("label[for='oldMemberCode']").html('<span class="redmark">*</span>原项目成员:');
			$("label[for='newMemberName']").html('<span class="redmark">*</span>新项目成员:');
		}
		queryAllRoleUser();
	});
	$(document).on('change','#dpNo',function(){
		$("#dpName").val($("#dpNo option:selected").text());
	});
	function queryAllRoleUser(){
		$.ajax({
			url:'queryDpNoRoleUser.action',
			type:'post',
			dataType:'json',
			data:{roleid :roleid},
			success:allRoleUserCb
		});
	};
	function queryuser(){
		usernameArr2 = new Array();
		realnameArr2 = new Array();
		var dpNo = $("#dpNo").val();
		$.ajax({
			url:'queryDpNoRoleUser.action',
			type:'post',
			dataType:'json',
			data:{roleid :roleid,'batchCgMb.dpNo':dpNo},
			success:queryUserCb
		});
	};
	function allRoleUserCb(json){
		var userlist = json.allusernameList;
		for(var i = 0;i < userlist.length;i++){
			usernameArr[i] = userlist[i].username;
			realnameArr[i] = userlist[i].username+"-"+userlist[i].realName;
			dpNoArr[i] = userlist[i].dpNo;
		}
		$("#oldMemberCode").autocomplete({
			source: realnameArr,
		});
	};
	function queryUserCb(json){
		var userlist = json.allusernameList;
		for(var i = 0;i < userlist.length;i++){
			usernameArr2[i] = userlist[i].username;
			realnameArr2[i] = userlist[i].username+"-"+userlist[i].realName;
		}
		$("#newMemberName").autocomplete({
			source: realnameArr2,
		});
	};
	function fill(tis){
		var obj=$(tis);
		if(obj.val()==""){
			obj.next().value="";
		}
		if(obj.val()!=""){
			var i=0;
			var userName = usernameArr;
			var realName = realnameArr;
			if(obj[0] == $("#newMemberName")[0]){
				userName = usernameArr2;
				realName = realnameArr2;
			}
			for(;i<realName.length;i++){
				if(realName[i]==obj.val()){
					break;
				}
			}
			if(i==realName.length){
				return false;
			} else{
				obj.next().val(userName[i]);
				if(obj[0] == $("#oldMemberCode")[0]){
					$("#dpNo").val(dpNoArr[i]);
					$("#dpName").val($("#dpNo option:selected").text());
				}
			}	
		}
	}
	
	$(function(){
		var result = '${batchChangeResult}';
		if(result){
			result = result.split(":");
			alert("批量更新项目服务经理和项目经理成功！\r\n更新服务经理："+result[0]+"个项目\r\n更新项目经理："+result[01]+"个项目");
			location.href="module/BatchChangeProjectMember.action";
		}
	});
	$(document).ready(function(){
		$("form").submit(function(e){
			var changeType = $("#changeType").val().trim();
			var oldMemberCode = $("#oldMemberCodeHide").val().trim();
			var newMemberName = $("#newMemberName").val().trim();
			var dpNo = $("#dpNo").val();
			var dpName = $("#dpName").val();
			if(!changeType){
				alert("请选择变更类型！");
				return false;
			}else{
				changeType = $("#changeType option:selected").text();
			}
			if(!oldMemberCode){
				alert("请输入"+$("label[for='oldMemberCode']").text());
				return false;
			}
			/* if(!dpNo){
				alert("请选择变更类型！");
				return false;
			} */
			if(!newMemberName){
				alert("请输入"+$("label[for='newMemberName']").text());
				return false;
			}
			if(confirm("确认要批量修改【"+dpName+":"+oldMemberCode+"】所在项目的【"+changeType+"】为【"+newMemberName+"】?")){
				$("button").button("loading");
				return true;
			}
			return false;
		});
	});
	$("button").button("loading");
</script>
</head>
<body>
<div class="divHeader div-bottom" >
	<img src="images/right_zhishi.gif" border="0">
				<s:text name="sys.project.batchChangeMember"></s:text>
</div> 
<s:form method="post" action="module/BatchChangeProjectMember.action" id="mainForm" cssClass="form-horizontal" name="mainForm">
	<div class="panel panel-default container-fluid">
	   <div class="panel-body col-sm-6">
		    <div class="form-group">
			    <label for=changeType class="col-sm-2 control-label"><span class="redmark">*</span><s:text name="pm.project.batchChangeMember.type"></s:text><span>:</span></label>
			    <div class="col-sm-8">
			      <s:select id="changeType" name="batchCgMb.changeType" value="" cssClass="form-control" headerKey="" headerValue="--请选择--"
						list="#{'service':'服务经理','program':'项目经理','both':'服务经理和项目经理'}"  theme="simple" />
				</div>
		    </div>
	    
		    <div class="form-group">
			    <label for="oldMemberCode"  class="col-sm-2 control-label"><span class="redmark">*</span><s:text name="pm.project.batchChangeMember.oldMember"></s:text><span>:</span></label>
			    <div class="col-sm-8">
					<s:textfield id="oldMemberCode" name="batchCgMb.oldMemberName" onfocus="fill(this)" onblur="fill(this)"
						placeholder="支持模糊搜索" cssClass="form-control" />
					<s:hidden name="batchCgMb.oldMemberCode" value="" id="oldMemberCodeHide"></s:hidden>
				</div>
		    </div>
		    <div class="form-group">
			    <label  class="col-sm-2 control-label"><span class="redmark">*</span><s:text name="pm.project.batchChangeMember.department"></s:text><span>:</span></label>
			    <div class="col-sm-8">
			      	<s:select id="dpNo" name="batchCgMb.dpNo" value="" listKey="departmentNum" cssClass="form-control" headerKey="" headerValue="--请选择--"
											listValue="departmentName" list="%{departmentList}"  theme="simple" />
			    	<s:hidden name="batchCgMb.dpName" value=""  id="dpName"></s:hidden>
			    </div>
		    </div>
		    <div class="form-group">
			    <label for="newMemberName"  class="col-sm-2 control-label"><span class="redmark">*</span><s:text name="pm.project.batchChangeMember.newMember"></s:text><span>:</span></label>
			    <div class="col-sm-8">
					<s:textfield id="newMemberName" name="batchCgMb.newMemberName" value="" onfocus="queryuser(this)" onblur="fill(this)"
					  placeholder="支持模糊搜索" cssClass="form-control" />
				</div>
		    </div>
	   	</div>
	   	<div class="col-sm-6 tip text-info" >
	   		<h4>提示:</h4>
	   		<p>该功能模块用于批量更改项目的服务经理或项目经理，指定原项目服务经理或项目经理，指定项目所在部门，以及需要指派的新服务经理或项目经理。请注意以下几点：</p>
	   		<ol>
	   			<li>1、一旦确认，默认将修改指定部门、指定原经理名下的所有项目为新经理;若要变更所有项目请将部门选为'请选择'</li>
	   			<li>2、请确保新指派用户拥有相应的角色，否则项目列表将无法通过用户进行查询</li>
	   			<li class="text-danger">3、该功能不可逆，请谨慎操作！</li>
	   		</ol>
	   	</div>
	</div>
  	<div class="form-group">
        <label  class="col-sm-1 control-label">&nbsp;</label>
	    <div class="col-sm-2 text-center">
		   	<button type="submit" id="submitButton" style="width: 80px;display:inline-block;" class="btn btn-default  btn-block btn-sm"  data-loading-text="正在处理..." ><s:text name='sys.confirm' /></button>
	    </div>
	     <div class="col-sm-2 text-center">
			<button type="button" style="width: 80px" class="btn btn-default btn-block btn-sm" onclick="javascript:history.go(-1)"  data-loading-text="正在处理..."><s:text name='sys.back' /></button>
	    </div>
    </div>	
</s:form>
</body>
</html>