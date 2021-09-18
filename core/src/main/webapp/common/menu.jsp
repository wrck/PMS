<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="shiro" uri="/shiro" %> 
<!-- sidebar: style can be found in sidebar.less -->
<section class="sidebar" style="padding-bottom: 0px;">
	<!-- sidebar menu: : style can be found in sidebar.less -->
	<shiro:principal property='menus'/>
</section>
<!-- /.sidebar -->