<%@page import="org.apache.shiro.SecurityUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="/shiro" %>
<%@ taglib prefix="dp" uri="/myTag" %>
<!-- Logo -->
<a href="${pageContext.request.contextPath}/success.html" class="logo"> <!-- mini logo for sidebar mini 50x50 pixels -->
	<span class="logo-mini"><b><spring:message code="system.title.en"/></b></span> <!-- logo for regular state and mobile devices -->
	<span class="logo-lg"><b><spring:message code="system.title"/></b></span>
</a>

<!-- Header Navbar: style can be found in header.less -->
<nav class="navbar navbar-static-top">
	<!-- Sidebar toggle button-->
	<a href="#" class="sidebar-toggle" data-toggle="offcanvas"
		role="button"> <span class="sr-only">Toggle navigation</span>
	</a>
	<!-- Navbar Right Menu -->
	<div class="navbar-custom-menu">
		<ul class="nav navbar-nav">
		    <li class="dropdown user user-menu">
		      <dp:changeCompany isNav="true"></dp:changeCompany>
		    </li>
			<!-- User Account: style can be found in dropdown.less -->
			<li class="dropdown user user-menu">
				<a href="#" class="dropdown-toggle" data-toggle="dropdown"> <%-- <img
					src="${pageContext.request.contextPath}<shiro:principal property='avatar' defaultValue='/static/common/images/avatar.png'/>" class="user-image"
					alt="User Image" onerror="this.src='${pageContext.request.contextPath}/static/common/images/avatar.png'"> --%><span class="hidden-xs"><shiro:principal property="userName"/>-<shiro:principal property="realName"/></span>
				</a>
				<ul class="dropdown-menu pt-0">
					<!-- User image -->
					<li class="user-header"><img src="${pageContext.request.contextPath}<shiro:principal property='avatar' defaultValue='/static/common/images/avatar.png'/>"
						class="img-circle" alt="User Image"  onerror="this.src='${pageContext.request.contextPath}/static/common/images/avatar.png'">
						<p>
							<span><shiro:principal property="compName"/></span><br>
							<span><shiro:principal property="userName"/> - <shiro:principal property="realName"/></span>
							<small><shiro:principal property="email"/></small>
							<%-- <small>
                                <dp:changeCompany></dp:changeCompany>
	                        </small> --%>
						</p>
					</li>
					<!-- Menu Body -->
					<!--<li class="user-body">
						<div class="row">
							<div class="col-xs-4 text-center">
								<a href="#">Followers</a>
							</div>
							<div class="col-xs-4 text-center">
								<a href="#">Sales</a>
							</div>
							<div class="col-xs-4 text-center">
								<a href="#">Friends</a>
							</div>
						</div> /.row
					</li>-->
					<!-- Menu Footer-->
					<li class="user-footer">
						<div class="pull-left">
							<a href="${pageContext.request.contextPath}/sys/user/<shiro:principal property='userId'/>.html" class="btn btn-default btn-flat"><spring:message code="sys.personal.setting"/></a>
						</div>
						<div class="pull-right">
							<a href="${pageContext.request.contextPath}/logout.html" class="btn btn-default btn-flat"><spring:message code="login.out"/></a>
						</div>
					</li>
				</ul>
			</li>
			<!-- Control Sidebar Toggle Button -->
			<li><a href="#" data-toggle="control-sidebar"><i
					class="fa fa-gears"></i></a></li>
		</ul>
	</div>

</nav>
