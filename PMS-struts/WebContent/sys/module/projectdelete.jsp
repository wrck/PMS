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
<meta name="function" content="<s:text name='sys.project.management' />">
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript">
    $(function(){
    	var result =  "${batchChangeResult}";
    	if (result) {
    		if (result != "authError") {
    			var flag = $("input[name='modifyflag']:checked").val();
    			if (flag == 1) {
    				alert("批量删除成功！\r\n共删除" + result + "条记录");
    			} else {
    				alert("批量失效成功！\r\n共失效" + result + "个项目");
    			}
    			window.location.href="module/clearProject.action";;
    		} else if (result == -1){
    			alert("没有删除权限！");
    		} else {
    			alert("没有删除权限！");
    		}
    	}
        $("#create").click(function(){
        	if(!$("input[name='upload']").val()) {
        		alert("请选择需要上传的xls文件")
        		return false;
        	}
        	if(!$("input[name='modifyflag']:checked").val()) {
        		alert("请选择删除方式");
                return false;
        	}
        	$("button").button("loading");
	        $("#mainForm").submit();
	        return true;
        });
        
        $("input[name='modifyflag']").change(function() {
        	var flag = $(this).val();
        	if (flag == 1) {
        		$("#create").text("批量删除");
        	} else {
        		$("#create").text("批量失效");
        	}
        })
    });
</script>
</head>
<body>
    <div class="container-flux">
    
        <div class="row">
            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                <div class="listView divHeader">
                    <img src="images/right_zhishi.gif" border="0">
                    <s:text name="project.pm.project.batchDeleteProject"></s:text>
                </div>
                <s:form method="post" action="module/clearProject.action" id="mainForm"
                    cssClass="form-horizontal" name="mainForm" enctype="multipart/form-data">
					<div class="panel panel-default mb-2">
						<div class="panel-body text-center">
						    <div style="display: inline-block;">
                                <div class="form-group text-left">
                                    <label for="attachments" class="control-label"><s:text name="prob.info.attachments"></s:text></label>
                                    <input type="file" name="upload" class="form-control" style="width: auto;display: inline-block;"/>
                                </div>
                                <div class="form-group text-left text-warning">
                                    <span>提示：请按模板格式上传项目合同号</span>
                                    <span class="pull-right"><img src="images/right_zhishi.gif" border="0"><a href="module/DownloadFile.action?downname=删除借货项目模板.xlsx&downpath=/template/deleteProject_template.xlsx">删除借货项目模板</a></span>
                                </div>
                                <div class="form-group text-center">
                                    <!-- <button type="button" id="back" style="width: 80px;" class="btn btn-default btn-sm" onclick="window.history.back(-1)">返回</button> -->
                                    <s:if test="(user.isHasRole(13) || user.isHasRole(1))">
                                        <s:if test="project.projectId == 0">
                                            <span style="margin-right:2rem;">
                                                <label for="modifyflag-1" class="control-label">删除方式:</label>
                                                <s:radio name="modifyflag" id="modifyflag" list="#{'-1':'失效','1':'删除'}" listKey="key" listValue="value"/>
                                            </span>
                                            <button type="button" id="create" style="width: 80px;" class="btn btn-default btn-sm" data-loading-text="正在处理...">批量删除</button>
                                        </s:if>
                                    </s:if>
                                </div>
                            </div>
						    <div class="text-left col-sm-5 pull-right col-md-pull-1" style="display: inline-block;">
						        <div class="tip text-primary" >
						            <h4>注意:</h4>
						            <p>该功能模块用于批量删除已创建的借货合同项目。请注意以下几点：</p>
						            <ol style="padding-left:0.5rem;">
						                <li>1、请确保上传的合同号为需要删除的借货合同，并确认不再继续跟踪该合同项目</li>
						                <li>2、<span class="text-warning">“失效”选项</span>将使合同创建项目置为不可见状态，保留项目记录</li>
						                <li>3、<span class="text-danger">“删除”选项</span>将清除所有该合同创建的项目记录，请谨慎操作</li>
						            </ol>
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