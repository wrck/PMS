<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %> 
<html>
<head>
<title>正在执行...</title>
<LINK href="css/common.css" type=text/css rel=stylesheet>
<!-- 添加css和js的引用在这里 -->
</head>

<body style="BACKGROUND-COLOR: #efefef;">
<div align=center>
<table>
	<tbody>
		<tr>
			<td width=210 height=40 align=center><LABEL style=""><sec:authentication property="name"/>请求的页面不存在，请联系管理员...</LABEL>
			</td>
		</tr>
		<tr>
			<td width=210 height=40 align=center><dp:errormsg /></td>
		</tr>
	</tbody>
</table>
</div>
</body>
</html>