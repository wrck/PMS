<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title><s:text name="sys.title"/></title>
<dp:link rel="stylesheet" type="text/css" href="css/login.css" />
<dp:script type="text/javascript" src="js/util.js"></dp:script>
<script type="text/javascript" src="js/md5.js"></script>
<script type="text/javascript" src="js/jquery-2.1.4.min.js"></script>
<script>
	function init() {
		getObj("username").focus();
	}
	sys_addinit(init);

	function change(){
		document.getElementById("pass").value = hex_md5($("#username").val().trim() + $("#pass").val().trim());
	}
	function reloadImage(url){
		$.ajax({
			url:url,
			type:"get",
			success:function(){
				document.validationImg.src = url;
			}			
		});
	}
</script>
</head>
<body class="login_bg">
<div id="center">
<table width="643" height="393" class="login_window" border="0"
	cellpadding="0" cellspacing="0" align="center">
	<tr>
		<td width="100%" height="100%"><s:form action="Login.action"
			method="post" onsubmit="change()">
			<table>
				<tr>
					<td colspan="3" height="228">&nbsp;</td>
				</tr>
				<tr>
					<td width="270" height="28" align="right"></td>
					<td><s:textfield name="user.username" cssClass="txtbox" value=""
						id="username" /></td>
					<td></td>
				</tr>
				<tr>
					<td width="270" height="28" align="right"></td>
					<td><s:password name="user.password" cssClass="txtbox" id="pass"/></td>
					<td></td>
				</tr>
			 	<tr>
					<td width="270" height="28" align="right"></td>
					<td><s:textfield name="user.validation" cssClass="txtbox" maxlength="4"/>&nbsp;<img id="validationImg" name="img1" src="image.jsp" onclick="reloadImage('image.jsp')" /></td>
					<td><s:if test="fieldErrors.size()>0">
						<span style="font: 12px; color: darkgray; padding-left: 12px;"><dp:fielderror
							onlyone="true" /> </span>
					</s:if></td>
				</tr>
				<tr>
					<td width="270" height="28"></td>
					<td colspan="2"><s:submit value=" " title="Login"
						cssClass="login_btn" />
					</td>
				</tr>
				<tr>
					<td colspan="3"></td>
				</tr>
			</table>
		</s:form></td>
	</tr>
</table>
</div>
</body>
</html>