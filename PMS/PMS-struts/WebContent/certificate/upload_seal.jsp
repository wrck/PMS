<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String flag = (String)request.getAttribute("flag"); 
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<s:head />
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.base.link' />">
<meta name="function" content="<s:text name='sys.leftmenu.warranty' />">
<script type="text/javascript">
function importBar(){
    if($("#rmafile").val()==""){
        alert("请选择要导入的文件");
    }else{
        document.mainForm.submit();
    }
}
</script>
</head>
<body>
    <table class="listView" cellSpacing="0" cellPadding="0">
        <tbody>
            <TR>
                <TH class="tableHeader">
                    印章登记表上传<span id="dispName"></span>
                </TH>
            </TR>
        </tbody>
    </table>
    <form method="post" action="sys/uploadSealInfo.action" id="mainForm" name="mainForm" ENCTYPE="multipart/form-data">
        <s:file name="file" label="File"  id="rmafile" ></s:file>
        <s:textfield type="button" value="确定" onclick="importBar()"></s:textfield>          
    </form>
</body>
</html>