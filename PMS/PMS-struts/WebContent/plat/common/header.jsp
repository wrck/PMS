<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="com.dp.plat.context.SpringContext"%>
<%@page import="com.dp.plat.context.UserContext"%>
<style type="text/css">
.div{
width:100%;
background-image:url('images/h_top.jpg');
background-size:cover;
background-color:#0a6fc9;
filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod='scale',src='images/h_top.jpg');
}
</style>
<table class="div" cellSpacing="0" cellPadding="0" border="0" >
     <tr>
     	<td width="5%"></td>
     	<td width="70%"><span style="font-size:20px;color:white;letter-spacing:3px;">工程项目管理系统</span></td>
     	<td width="25%" colspan="2">
         	<span>
	         	<a href="Logout.action"><FONT color="white"><s:text name="plat.header.exit"></s:text></FONT></a>
                <strong class="fontWhite">[${currentDisplayUser.username}-${currentDisplayUser.realName}]</strong>
         		<%-- <strong class="fontWhite">[<%=((UserContext)SpringContext.getBean("userContext")).getUsername()%>-</strong>
         		<strong class="fontWhite"><%=((UserContext)SpringContext.getBean("userContext")).getUser().getRealName()%>]</strong> --%>
                <s:if test="%{!currentIsCas}">
                    <a href="${pageContext.request.contextPath}/sys/Password.action"><FONT color="white">修改密码</FONT></a>
                </s:if>
         	</span>
		</td>
      </tr>
</table>
