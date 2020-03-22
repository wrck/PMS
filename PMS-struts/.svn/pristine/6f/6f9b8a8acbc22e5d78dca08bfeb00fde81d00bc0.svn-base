﻿<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<s:head />
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.powermanage' />">
<meta name="function" content="<s:text name='sys.leftmenu.certificate' />">
<style type="text/css">
    #barcode {
        width: 170px;
        padding: 3px 10px;
    }
    
    #showFrame {
        border: none;
        width: 100%;
        height: calc(100vh - 330px);
        min-height: 50vh;
        font-size: 12px;
    }
    
    #showFrame html {
        font-size: 12px;
    }
</style>
<script type="text/javascript">
    function uploadSealInfo() {
        if ($("#uploadFile").val() == "") {
            alert("请选择要导入的文件");
        } else {
            document.uploadForm.submit();
        }
    }
    
    $(function(){
        $("#doPrint").click(function() {
            var iframe = document.getElementById('showFrame');  
            iframe.contentWindow.focus();  
            iframe.contentWindow.print();
        })
    })
</script>
</head>
<body>
    <s:if test="results.canUpload == 'true'">
        <table class="listView" cellSpacing="0" cellPadding="0">
            <tbody>
                <tr>
                    <th class="tableHeader">
                        印章登记表上传
                    </th>
                </tr>
            </tbody>
        </table>
        <br>
		<form method="post" action="module/uploadSealInfo.action" id="uploadForm"
			name="uploadForm" ENCTYPE="multipart/form-data" class="form-inline">
			<s:file name="file" cssClass="form-control" label="File" id="uploadFile"></s:file>
			<button type="button" class="btn btn-default" onclick="uploadSealInfo()">确定</button>
        </form>
        <br>
    </s:if>
    <table class="listView" cellSpacing="0" cellPadding="0">
        <tbody>
            <tr>
                <th class="tableHeader">
                                                            合格证查询
                </th>
            </tr>
        </tbody>
    </table>
    <br>
    
	<form method="post" action="module/certificate.action" id="mainForm"
        name="mainForm" ENCTYPE="multipart/form-data" class="form-inline">
        <label for="barcode" class="form-label">设备序列号：</label>
        <s:textfield name="barcode" label="设备序列号" id="barcode" class="form-control" placeholder="请输入设备序列号"></s:textfield>
        <button class="btn btn-default">查询</button>
        <button id="doPrint" class="btn btn-default">打印</button>
    </form>
    <br>
    <s:if test="barcode != '' && barcode != null">
		<iframe id="showFrame" src="module/sub/queryCertificate.action?barcode=${barcode}">
        </iframe>
    </s:if>
    <s:else>
           请输入设备序列号！
    </s:else>
</body>
</html>