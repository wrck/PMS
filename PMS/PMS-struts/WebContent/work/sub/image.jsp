<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查看当前流程图</title>
</head>
<body>
<!-- 1.获取到规则流程图 -->
<img style="position: absolute;top: 0px;left: 0px;" src="work/sub/WorkFlowViewImage.action?param.deploymentId=<s:property value='processDefinition.deploymentId'/>&param.imageName=<s:property value='processDefinition.diagramResourceName'/>">
<!-- 2.根据当前活动的坐标，动态绘制DIV -->
<div style="position: absolute;border:3px solid red;top:<s:property value='map.y'/>px;left: <s:property value='map.x'/>px;width: <s:property value='map.width'/>px;height:<s:property value='map.height'/>px;   "></div>
</body>
</html>