<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<s:head />
<dp:base />
<meta name="menu" content="SysMenuGroupStaticDataManage">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.powermanage' />">
<meta name="function" content="<s:text name='procDefDelegate.title' />">
<script type="text/javascript">
var realnameArr=new Array();
var usernameArr=new Array();

 $(function(){
	 if(_current_username != "admin"){
		 $("#ownerShow").val(_current_username + "-" +_current_realName);
		 $("#ownerShow").attr("readonly",true);
	 }
	 var owner = '<s:property value="procdefDelegate.owner"/>';
	var ownerName = '<s:property value="procdefDelegate.ownerName"/>';
	var assignee = '<s:property value="procdefDelegate.assignee"/>';
	var assigneeName = '<s:property value="procdefDelegate.assigneeName"/>';
	$("#ownerShow").val(owner.substring(1)+"-"+ownerName);
	$("#assigneeShow").val(assignee.substring(1)+"-"+assigneeName);
	getuser();
	$("#ownerShow" ).autocomplete({
        source: realnameArr 
       });
	$("#assigneeShow" ).autocomplete({
        source: realnameArr 
       });
});

 function getuser(){
	 $.ajax({
			url:'getuser.action',
			type:'post',
			dataType:'json',
			data:{},
			success:getuser2
		});
 }
 function getuser2(json){
	 var userlist = json.usernamelist;
	 for(var i = 0;i < userlist.length;i++){
		 usernameArr[i] = userlist[i].username;
		 realnameArr[i] = userlist[i].username.substring(1)+"-"+userlist[i].realName;
	 }
 } 
function fillusernamec(show, hide){
	 var obj=document.getElementById(show);
		if(obj.value==""||obj.value=="支持模糊搜索"){
			document.getElementById(hide).value="";
		}
		if(obj.value!=""&&obj.value!="支持模糊搜索"){
			var i=0;
			for(;i<realnameArr.length;i++){
				if(realnameArr[i]==obj.value){
					break;
				}
			}
			if(i==realnameArr.length){
				return false;
			} else{
				document.getElementById(hide).value=usernameArr[i];
			}
		}
}
function b(obj){
	 if(obj.value==""){
			obj.value="支持模糊搜索";
		}
}
function f(obj){
	 if(obj.value=="支持模糊搜索"){
			obj.value="";
		}
}
function checkbox(){
}
</script>

</head>
<body>
<s:form method="post" action="work/UpdateProcDefDelegate.action" id="UpdateProcDefDelegate">
	<s:hidden name="procdefDelegate.id"></s:hidden>
	<table class="listView" cellSpacing="0" cellPadding="0">
		<tbody>
			<TR>
				<TH class="tableHeader"><img src="images/right_zhishi.gif"
					border="0"><s:text name="procDefDelegate.add"></s:text></TH>
			</TR>
		</tbody>
	</table>
	<div class="editdiv">
	<table width=100% cellSpacing="0" cellPadding="0">
		<tbody>
			<tr>
				<td align="left" width="60%">
				<table class="edittable" width="100%" border="0">
					<tbody align="left">
						<tr>
							<td colspan="3"><br/>
							</td>
						</tr>
						<tr>
							<td colspan="3"><dp:fielderror accesskey="errmsg"
								onlyone="true" /></td>
						</tr>
						<tr>
							<td class="normalText" width="10%">
								<s:text name="procDefDelegate.owner" /></td>
							<td>
								<s:textfield name="procdefDelegate.owner" type="hidden"
									id="ownerHide"></s:textfield>
								<s:textfield name="owner" value="支持模糊搜索" id="ownerShow"
								onfocus="fillusernamec('ownerShow','ownerHide');f(this)" onblur="fillusernamec('ownerShow','ownerHide');b(this)"
								cssClass="normalInput" cssStyle="width:200px"></s:textfield></td>
								
							<td class="normalText" width="10%">
								<s:text name="procDefDelegate.assignee" /></td>
							<td>
								<s:textfield name="procdefDelegate.assignee" type="hidden"
									id="assigneeHide"></s:textfield>
								<s:textfield name="assignee" value="支持模糊搜索" id="assigneeShow"
								onfocus="fillusernamec('assigneeShow','assigneeHide');f(this)" onblur="fillusernamec('assigneeShow','assigneeHide');b(this)"
								cssClass="normalInput" cssStyle="width:200px"></s:textfield></td>
						</tr>
						<tr>
							<td class="normalText"><s:text
								name="procDefDelegate.startTime" /></td>
							<td>
								<INPUT type="text" class="normalInput" style="width: 200px;"
									id="startTime" name="procdefDelegate.startTime" value="<s:date format="yyyy-MM-dd" name="procdefDelegate.startTime"/>"
									size=10 /> <a href="javascript:void(0);"><img src="images/right_riqi.jpg"
									id="startTimeImg" width="16" height="16" border="0"
									alt='<s:text name="protect.gmpage.choosetime"/>' align="absmiddle">
								</a> <SCRIPT type=text/javascript>
											Calendar.setup( {
												inputField : "startTime", // id of the input field
												ifFormat : "%Y-%m-%d", // format of the input field  %Y-%m-%d %H:%M
												showsTime : false,
												button : "startTimeImg", // trigger for the calendar (button ID)					                 
												align : "B", // alignment (defaults to "Bl")
												singleClick : true,
												firstDay : 1
											});
										</SCRIPT> 
							</td>
								
							<td class="normalText"><s:text
								name="procDefDelegate.endTime" /></td>
							<td>
								<INPUT type="text" class="normalInput" style="width: 200px;"
									id="endTime" name="procdefDelegate.endTime" value="<s:date format="yyyy-MM-dd" name="procdefDelegate.endTime"/>"
									size=10 /> <a href="javascript:void(0);"><img src="images/right_riqi.jpg"
									id="endTimeImg" width="16" height="16" border="0"
									alt='<s:text name="protect.gmpage.choosetime"/>' align="absmiddle">
								</a> <SCRIPT type=text/javascript>
											Calendar.setup( {
												inputField : "endTime", // id of the input field
												ifFormat : "%Y-%m-%d", // format of the input field  %Y-%m-%d %H:%M
												showsTime : false,
												button : "endTimeImg", // trigger for the calendar (button ID)					                 
												align : "B", // alignment (defaults to "Bl")
												singleClick : true,
												firstDay : 1
											});
										</SCRIPT> 
							</td>
						</tr>
						<tr>
							<td class="normalText" colspan="4">
								 <s:optiontransferselect  
							        	cssStyle="width:230px;height:200px;"      
							            name="procdefIds"
							            leftTitle="所有流程定义"
							            rightTitle="被委派的流程定义"
							            list="daptlist" 
							            listKey="procDefKey"
							            listValue="desc"
							            multiple="true"
							            doubleList="daptlistselect" 
							            doubleListKey="procDefKey"
							            doubleListValue="desc"
							            doubleName="procdefDelegate.procdefId"
							            doubleCssStyle="width:230px;height:200px;"
							            doubleMultiple="true" />
							</td>
						</tr>
						<tr>
							<td><s:text name="sys.project.isapply"></s:text></td>
							<td colspan="3"><s:radio name="procdefDelegate.status" id="status" list="#{0:'失效',1:'有效'}"/></td>
						</tr>
						<tr>
							<td><s:text name="procDefDelegate.cause"></s:text></td>
							<td colspan="3">
								<s:textarea name="procdefDelegate.cause" cssStyle="height:60px;"></s:textarea>
							</td>
						</tr>
					</tbody>
				</table>
				</td>
				<td align="left" width="50%">
				
				</td>
			</tr>
			<tr class="tableline">
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>
				<table width=100% cellSpacing="15">
					<tbody>
						<tr align="center">
							<td align="right"><s:submit
								value="%{getText('button.confirm')}" cssClass="buttonNormal"></s:submit>
							</td>
							<td align="left"><s:reset value="%{getText('button.back')}"
								cssClass="buttonNormal"
								onclick="javascript:history.go(-1);"></s:reset>
							</td>
						</tr>
					</tbody>
				</table>
				<br>
				</td>
			</tr>
		</tbody>
	</table>
	</div>
	
</s:form>
</body>
</html>
