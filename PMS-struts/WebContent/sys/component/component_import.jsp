<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.powermanage' />">
<meta name="function" content="<s:text name='component.manage' />">
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript">
    $(function(){
    	var result =  "${result}";
    	if (result) {
    		if (result == "success") {
    			alert("批量导入产品组件成功！");
    		} else if (result == "authError"){
    			alert("没有导入权限！");
    		}
    	}
        $("#create").click(function(){
        	if(!$("input[name='upload']").val()) {
        		alert("请选择需要导入的Excel文件")
        		return false;
        	}
        	$("button").button("loading");
	        $("#mainForm").submit();
	        return true;
        });
    });
</script>
</head>
<body>
    <div class="container-flux">
    
        <div class="row">
            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                <div class="listView divHeader">
                    <img src="images/right_zhishi.gif" border="0">
                    <s:text name="component.manage.import"></s:text>
                </div>
                <s:form method="post" action="module/component_import.action" id="mainForm"
                    cssClass="form-horizontal" name="mainForm" enctype="multipart/form-data">
					<div class="panel panel-default mb-2">
						<div class="panel-body text-center">
                            <div style="display: inline-block;">
							    <div class="form-group text-left">
							        <label for="attachments" class="control-label"><s:text name="component.info.attachments"></s:text></label>
						            <input type="file" name="upload" class="form-control" style="width: auto;display: inline-block;"/>
	                            </div>
							    <div class="form-group text-left text-warning">
	                                <span>提示：请按模板格式导入产品组件</span>
	                                <span class="pull-right"><img src="images/right_zhishi.gif" border="0"><a href="module/DownloadFile.action?downname=产品组件导入模板.xlsx&downpath=/template/productComponent_template.xlsx">产品组件导入模板</a></span>
	                            </div>
	                            <div class="form-group text-center">
	                                <s:if test="user.isHasAnyRole(1,22)">
	                                    <s:if test="productComponent.id == 0 || productComponent.id == null">
	                                        <button type="button" id="create" style="width: 80px;" class="btn btn-default btn-sm" data-loading-text="正在处理...">批量导入</button>
	                                    </s:if>
	                                </s:if>
	                            </div>
                            </div>
                        </div>
                    </div>
                </s:form>
            </div>
            
        </div>
    </div>
</body>
</html>