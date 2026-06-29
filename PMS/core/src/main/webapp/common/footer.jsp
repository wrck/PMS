<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@page import="java.util.Calendar"%>
<!-- <div class="pull-right hidden-xs">
	<b>Version</b> 0.1
</div> -->
<strong>Copyright &copy; 2017-<%=Calendar.getInstance().get(Calendar.YEAR) %>
	<a href="<spring:message code='copyright.url'/>"><spring:message code='copyright.company'/></a>.
</strong>
All rights reserved.