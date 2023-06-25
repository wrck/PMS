<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<html>
<head>
<title>正在执行...</title>
<LINK href="../css/common.css" type=text/css rel=stylesheet>
<!-- 添加css和js的引用在这里 -->
</head>
<body style="BACKGROUND-COLOR: #efefef;">
    <div align=center>
        <table>
        	<tbody>
        		<tr>
        			<td align=center><label style="">程序发送未知错误，请联系管理员，谢谢！</label></td>
        		</tr>
                <tr>
                    <td id="error"><dp:errormsg /></td>
                </tr>
        	</tbody>
        </table>
    </div>
</body>
</html>