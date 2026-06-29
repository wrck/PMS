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
<meta name="function" content="<s:text name='sys.leftmenu.departmentmanage' />">
<style type="text/css">
.buttonDiv{
	float:left;
	margin-left: 50px;
	margin-bottom: 30px;
	margin-top: 50px;	
}
</style>
<script type="text/javascript">

</script>
</head>
<body>
<s:form method="post" action="base/UserAddSubmit.action" id="mainForm" name="mainForm">
	<table class="listView" cellSpacing="0" cellPadding="0">
		<tbody>
			<TR>
				<TH class="tableHeader"><img src="images/right_zhishi.gif"
					border="0"><s:text name="sys.department.addDetail"></s:text></TH> 
			</TR>
		</tbody>
	</table>
	<div class="editdiv">
	<table width=100% cellSpacing="0" cellPadding="0">
		<tbody>
			<tr>
				<td align="left" width="100%">
				<table class="edittable" width="100%" border="0">
					<tbody align="left">
						<tr>
							<td class="redmark" width="5%">&nbsp;</td>
							<td class="normalText" width="10%" ><s:text name="sys.department.num"></s:text><span>:</span></td>
							<td ><s:textfield name="department.departmentNum" id="departmentNum" cssClass="normalInput" cssStyle="width:130px"/>
							</td>																			
						</tr>
						<tr>
							<td class="redmark" width="20px">&nbsp;</td>
							<td class="normalText" width="50px"><s:text name="sys.department.name"></s:text><span>:</span></td>
							<td><s:textfield name="department.departmentName" id="departmentName" cssClass="normalInput" cssStyle="width:130px"/>
							</td>					
						</tr>
						<tr><td><br/></td></tr>
						<tr>
							<td class="redmark" width="3%">&nbsp;</td>
							<td class="normalText" width="10%"><s:text name="sys.department.status"></s:text><span>:</span></td>
							<td>
							<input type="radio" name="department.status" id="status1" checked="checked" value="1"><label for="status1">有效</label>
							<input type="radio" name="department.status" id="status0" value="0"><label for="status0">失效</label>
							</td>
						</tr>	
					</tbody>
				</table>
				</td>
			</tr>
			<tr>
				<td>
					<div class="buttonDiv">
						<input class="buttonNormal" type="submit" value="<s:text name='sys.confirm' />" >
					</div>
					<div class="buttonDiv">
						<input class="buttonNormal" type="button" value="<s:text name='sys.back' />" onclick="javascript:history.go(-1)">
					</div>
				</td>
			</tr>
		</tbody>
	</table>
	</div>
</s:form>
</body>
</html>