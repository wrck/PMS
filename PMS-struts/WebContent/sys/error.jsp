<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<html>
<head>
<title>发生未知错误</title>
<dp:link href="../css/common.css" type="text/css" rel="stylesheet" />
<!-- 添加css和js的引用在这里 -->
</head>
<body style="BACKGROUND-COLOR: #efefef;">
	<div align=center>
		<table>
			<tbody>
				<tr>
					<td width=210 height=40 align=center>
						程序发送未知错误或请求的页面不存在，请联系管理员，谢谢！
					</td>
				</tr>
				<tr>
				  <td id="error">
		            <dp:errormsg />
		          </td>
				</tr>
			</tbody>
		</table>
	</div>
</body>
</html>