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
        /* height: calc(100vh - 330px); */
        min-height: 50vh;
        font-size: 12px;
    }
    
    #showFrame html {
        font-size: 12px;
    }
</style>

<%-- <script src="${pageContext.request.contextPath}/js/jquery.PrintArea.js" type="text/JavaScript" ></script>  --%>
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
    		
    		//handlePrint(iframe.contentWindow.document, "#printImg");
    		//handlePrint(iframe.contentWindow.document, "#pic");
    		
    	    iframe.contentWindow.focus();  
    	    iframe.contentWindow.print();
    	});
    	<%--
    	function handlePrint(context, el) {
    		let document = context.document;
    	    let printHtml = document.getElementById(el);
    	    //获取指定打印区域
    	    const iframe = document.getElementById('print-iframe');
    	    if (!iframe) {
    	        //var el = document.getElementById('printcontent');
    	        iframe = document.createElement('IFRAME');
    	        var doc = null;
    	        iframe.setAttribute('id', 'print-iframe');
    	        iframe.setAttribute('style', 'position: absolute; width: 0px; height: 0px;left:-500px;top:-500px;');
    	        document.body.appendChild(iframe);
    	        doc = iframe.contentWindow.document;
    	        //这里可以自定义样式
    	        doc.write('<style media="print">@page {size: auto; margin: 0 mm;}</style>');
    	        //解决出现页眉页脚和路径的问题
    	        doc.write('<div style="margin: 20px auto;">' + printHtml + '</div>');
    	        doc.close();
    	        iframe.contentWindow.focus();
    	    }
    	    setTimeout(function() {
    	        iframe.contentWindow.print();
    	    }, 50);
    	    //解决第一次样式不生效的问题
    	    if (navigator.userAgent.indexOf('MSIE') > 0) {
    	        document.body.removeChild(iframe);
    	    }
    	}
    	
    	function handlePrint(context, el) {
    		var mode = "iframe";//popup 或者 iframe
            var close = mode == "popup" && true;//当mode为popup起作用
            var extraCss = '';
            var print = $(el, context);//可打印的区域，为选择器
            var keepAttr = ["class","on"];
            var headElements = '';
            var options = { mode : mode, popClose : close, extraCss : extraCss, retainAttr : keepAttr, extraHead : headElements };
            $( print ).printArea( options );
    	}
        --%>
	})
</script>
<script type="text/javascript">
    //输入你希望根据页面高度自动调整高度的iframe的名称的列表
    //用逗号把每个iframe的ID分隔. 例如: ["myframe1", "myframe2"]，可以只有一个窗体，则不用逗号。
    //定义iframe的ID
    var iframeids=["showFrame"];
    //如果用户的浏览器不支持iframe是否将iframe隐藏 yes 表示隐藏，no表示不隐藏
    var iframehide="no";
    function dyniframesize() {
        var dyniframe = new Array();
        for (i = 0; i < iframeids.length; i++) {
            if (document.getElementById) {
                //自动调整iframe高度
                dyniframe[dyniframe.length] = document
                        .getElementById(iframeids[i]);
                if (dyniframe[i] && !window.opera) {
                    dyniframe[i].style.display = "block";
                    if (dyniframe[i].contentDocument
                            && dyniframe[i].contentDocument.body.offsetHeight)
                        //如果用户的浏览器是NetScape
                        dyniframe[i].height = dyniframe[i].contentDocument.body.offsetHeight;
                    else if (dyniframe[i].Document
                            && dyniframe[i].Document.body.scrollHeight)
                        //如果用户的浏览器是IE
                        dyniframe[i].height = dyniframe[i].Document.body.scrollHeight;
                }
            }
            //根据设定的参数来处理不支持iframe的浏览器的显示问题
            if ((document.all || document.getElementById)
                    && iframehide == "no") {
                var tempobj = document.all ? document.all[iframeids[i]]
                        : document.getElementById(iframeids[i]);
                tempobj.style.display = "block";
            }
        }
    }
    if (window.addEventListener)
        window.addEventListener("load", dyniframesize, false);
    else if (window.attachEvent)
        window.attachEvent("onload", dyniframesize);
    else window.onload = dyniframesize;
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
        <button type="button" id="doPrint" class="btn btn-default">打印</button>
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