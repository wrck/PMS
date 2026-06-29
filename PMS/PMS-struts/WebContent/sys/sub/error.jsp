<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<html>
<head>
<title>正在执行...</title>
<dp:link href="../css/common.css" type="text/css" rel="stylesheet" />
<!-- 添加css和js的引用在这里 -->
</head>

<body style="BACKGROUND-COLOR: #efefef;">
<div align=center>
<table>
	<tbody>
		<tr>
			<td width=210 height=40 align=center><LABEL style=""></LABEL>
			</td>
		</tr>
		<tr>
			<td width=210 height=40 align=center><dp:errormsg /></td>
		</tr>
		<tr>
            <td><pre>${errmsg}</pre></td>
        </tr>
	</tbody>
</table>
</div>
</body>
</html>