<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib  prefix="dp" uri="/myTag" %>
<!DOCTYPE html>
<html>
<head>
<dp:JSDebugger type="redirect" content="${pageContext.request.contextPath}/404.html"></dp:JSDebugger>
<title><sitemesh:write property='title'></sitemesh:write></title>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<dp:link rel="icon" href="${pageContext.request.contextPath}/static/common/images/favicon.ico" type="image/x-icon" />
<!-- Tell the browser to be responsive to screen width -->
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
<!-- Bootstrap 3.3.6 -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/bootstrap/css/bootstrap.min.css">
<!-- Font Awesome -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/font-awesome/css/font-awesome.min.css">
<!-- Ionicons -->
<%-- <link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/ionicons/ionicons.min.css"> --%>
<!-- pace -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/pace/pace.min.css">

<!-- 引入被装饰页面的自定义cssTag标签内的css内容等 -->
<sitemesh:write property='cssTag'/>	
<!-- Theme style -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/dist/css/AdminLTE.min.css">
<!-- AdminLTE Skins. Choose a skin from the css/skins folder instead of downloading all of them to reduce the load. -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/dist/css/skins/_all-skins.min.css">
<dp:link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css" />
<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
  <!--<script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>-->
  <![endif]-->

<style type="text/css">
.fade {
    opacity: 0;
}
</style>

</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<header class="main-header">
			<!-- 头信息 -->
			<%@include file="/common/header.jsp"%>
		</header>
		<!-- Left side column. contains the logo and sidebar -->
		<aside class="main-sidebar">
			<!-- 菜单信息 -->
			<%@include file="/common/menu.jsp"%>
		</aside>
		<!-- Content Wrapper. Contains page content -->
		<!-- 主题内容 被装饰内容  -->
		<div class="content-wrapper" style="min-height: calc(100vh - 50px - 51px);">
			<!-- 引入被装饰页面的body标签的内容 即要呈现的主要内容 -->
			<sitemesh:write property='body'></sitemesh:write>
		</div>

		<footer class="main-footer text-center">
			<!-- 页脚信息 -->
			<%@include file="/common/footer.jsp"%>
		</footer>
		<!-- Control Sidebar -->
		<aside class="control-sidebar control-sidebar-dark"> <!-- Create the tabs -->
			<!-- Tab panes -->
			<div class="tab-content">
				<div id="control-sidebar-theme-demo-options-tab"
					class="tab-pane active">
					<div>
						<h4 class="control-sidebar-heading"><spring:message code='sys.layoutOption'/></h4>
						<%-- <div class="form-group">
							<label class="control-sidebar-subheading"><input
								type="checkbox" data-layout="fixed" class="pull-right">
								<spring:message code='sys.layoutOption.fixLayout'/></label>
							<p><spring:message code='sys.layoutOption.fixLayout.content'/></p>
						</div> --%>
						<div class="form-group">
							<label class="control-sidebar-subheading"><input
								type="checkbox" data-layout="layout-boxed" class="pull-right">
								<spring:message code='sys.layoutOption.boxedLayout'/></label>
							<p><spring:message code='sys.layoutOption.boxedLayout.content'/></p>
						</div>
						<div class="form-group">
							<label class="control-sidebar-subheading"><input
								type="checkbox" data-layout="sidebar-collapse"
								class="pull-right"> <spring:message code='sys.layoutOption.toggleLeftSidebar'/></label>
							<p><spring:message code='sys.layoutOption.toggleLeftSidebar.content'/></p>
						</div>
						<div class="form-group">
							<label class="control-sidebar-subheading"><input
								type="checkbox" data-enable="expandOnHover" class="pull-right">
								<spring:message code='sys.layoutOption.sidebarExpandOnHover'/></label>
							<p><spring:message code='sys.layoutOption.sidebarExpandOnHover.content'/></p>
						</div>
						<div class="form-group">
							<label class="control-sidebar-subheading"><input
								type="checkbox" data-controlsidebar="control-sidebar-open"
								class="pull-right"> <spring:message code='sys.layoutOption.toggleRightSiderBar.slide'/></label>
							<p><spring:message code='sys.layoutOption.toggleRightSiderBar.slide.content'/></p>
						</div>
						<div class="form-group">
							<label class="control-sidebar-subheading"><input
								type="checkbox" data-sidebarskin="toggle" class="pull-right">
								<spring:message code='sys.layoutOption.toggleRightSiderBar.skin'/></label>
							<p><spring:message code='sys.layoutOption.toggleRightSiderBar.skin.content'/></p>
						</div>
						<h4 class="control-sidebar-heading"><spring:message code='sys.theme.skins'/></h4>
						<ul class="list-unstyled clearfix">
							<li style="float: left; width: 33.33333%; padding: 5px;"><a
								href="javascript:void(0);" data-skin="skin-blue"
								style="display: block; box-shadow: 0 0 3px rgba(0, 0, 0, 0.4)"
								class="clearfix full-opacity-hover"><div>
										<span
											style="display: block; width: 20%; float: left; height: 7px; background: #367fa9;"></span><span
											class="bg-light-blue"
											style="display: block; width: 80%; float: left; height: 7px;"></span>
									</div>
									<div>
										<span
											style="display: block; width: 20%; float: left; height: 20px; background: #222d32;"></span><span
											style="display: block; width: 80%; float: left; height: 20px; background: #f4f5f7;"></span>
									</div></a>
							<p class="text-center no-margin"><spring:message code='sys.theme.skin.blue'/></p></li>
							<li style="float: left; width: 33.33333%; padding: 5px;"><a
								href="javascript:void(0);" data-skin="skin-black"
								style="display: block; box-shadow: 0 0 3px rgba(0, 0, 0, 0.4)"
								class="clearfix full-opacity-hover"><div
										style="box-shadow: 0 0 2px rgba(0, 0, 0, 0.1)"
										class="clearfix">
										<span
											style="display: block; width: 20%; float: left; height: 7px; background: #fefefe;"></span><span
											style="display: block; width: 80%; float: left; height: 7px; background: #fefefe;"></span>
									</div>
									<div>
										<span
											style="display: block; width: 20%; float: left; height: 20px; background: #222;"></span><span
											style="display: block; width: 80%; float: left; height: 20px; background: #f4f5f7;"></span>
									</div></a>
							<p class="text-center no-margin"><spring:message code='sys.theme.skin.black'/></p></li>
							<li style="float: left; width: 33.33333%; padding: 5px;"><a
								href="javascript:void(0);" data-skin="skin-purple"
								style="display: block; box-shadow: 0 0 3px rgba(0, 0, 0, 0.4)"
								class="clearfix full-opacity-hover"><div>
										<span
											style="display: block; width: 20%; float: left; height: 7px;"
											class="bg-purple-active"></span><span class="bg-purple"
											style="display: block; width: 80%; float: left; height: 7px;"></span>
									</div>
									<div>
										<span
											style="display: block; width: 20%; float: left; height: 20px; background: #222d32;"></span><span
											style="display: block; width: 80%; float: left; height: 20px; background: #f4f5f7;"></span>
									</div></a>
							<p class="text-center no-margin"><spring:message code='sys.theme.skin.purple'/></p></li>
							<li style="float: left; width: 33.33333%; padding: 5px;"><a
								href="javascript:void(0);" data-skin="skin-green"
								style="display: block; box-shadow: 0 0 3px rgba(0, 0, 0, 0.4)"
								class="clearfix full-opacity-hover"><div>
										<span
											style="display: block; width: 20%; float: left; height: 7px;"
											class="bg-green-active"></span><span class="bg-green"
											style="display: block; width: 80%; float: left; height: 7px;"></span>
									</div>
									<div>
										<span
											style="display: block; width: 20%; float: left; height: 20px; background: #222d32;"></span><span
											style="display: block; width: 80%; float: left; height: 20px; background: #f4f5f7;"></span>
									</div></a>
							<p class="text-center no-margin"><spring:message code='sys.theme.skin.green'/></p></li>
							<li style="float: left; width: 33.33333%; padding: 5px;"><a
								href="javascript:void(0);" data-skin="skin-red"
								style="display: block; box-shadow: 0 0 3px rgba(0, 0, 0, 0.4)"
								class="clearfix full-opacity-hover"><div>
										<span
											style="display: block; width: 20%; float: left; height: 7px;"
											class="bg-red-active"></span><span class="bg-red"
											style="display: block; width: 80%; float: left; height: 7px;"></span>
									</div>
									<div>
										<span
											style="display: block; width: 20%; float: left; height: 20px; background: #222d32;"></span><span
											style="display: block; width: 80%; float: left; height: 20px; background: #f4f5f7;"></span>
									</div></a>
							<p class="text-center no-margin"><spring:message code='sys.theme.skin.red'/></p></li>
							<li style="float: left; width: 33.33333%; padding: 5px;"><a
								href="javascript:void(0);" data-skin="skin-yellow"
								style="display: block; box-shadow: 0 0 3px rgba(0, 0, 0, 0.4)"
								class="clearfix full-opacity-hover"><div>
										<span
											style="display: block; width: 20%; float: left; height: 7px;"
											class="bg-yellow-active"></span><span class="bg-yellow"
											style="display: block; width: 80%; float: left; height: 7px;"></span>
									</div>
									<div>
										<span
											style="display: block; width: 20%; float: left; height: 20px; background: #222d32;"></span><span
											style="display: block; width: 80%; float: left; height: 20px; background: #f4f5f7;"></span>
									</div></a>
							<p class="text-center no-margin"><spring:message code='sys.theme.skin.yellow'/></p></li>
							<li style="float: left; width: 33.33333%; padding: 5px;"><a
								href="javascript:void(0);" data-skin="skin-blue-light"
								style="display: block; box-shadow: 0 0 3px rgba(0, 0, 0, 0.4)"
								class="clearfix full-opacity-hover"><div>
										<span
											style="display: block; width: 20%; float: left; height: 7px; background: #367fa9;"></span><span
											class="bg-light-blue"
											style="display: block; width: 80%; float: left; height: 7px;"></span>
									</div>
									<div>
										<span
											style="display: block; width: 20%; float: left; height: 20px; background: #f9fafc;"></span><span
											style="display: block; width: 80%; float: left; height: 20px; background: #f4f5f7;"></span>
									</div></a>
							<p class="text-center no-margin" style="font-size: 12px">
								<spring:message code='sys.theme.skin.blueLight'/>
							</p></li>
							<li style="float: left; width: 33.33333%; padding: 5px;"><a
								href="javascript:void(0);" data-skin="skin-black-light"
								style="display: block; box-shadow: 0 0 3px rgba(0, 0, 0, 0.4)"
								class="clearfix full-opacity-hover">
									<div style="box-shadow: 0 0 2px rgba(0, 0, 0, 0.1)"
										class="clearfix">
										<span
											style="display: block; width: 20%; float: left; height: 7px; background: #fefefe;"></span><span
											style="display: block; width: 80%; float: left; height: 7px; background: #fefefe;"></span>
									</div>
									<div>
										<span
											style="display: block; width: 20%; float: left; height: 20px; background: #f9fafc;"></span><span
											style="display: block; width: 80%; float: left; height: 20px; background: #f4f5f7;"></span>
									</div></a>
								<p class="text-center no-margin" style="font-size: 12px">
									<spring:message code='sys.theme.skin.blackLight'/>
								</p>
							</li>
							<li style="float: left; width: 33.33333%; padding: 5px;"><a
								href="javascript:void(0);" data-skin="skin-purple-light"
								style="display: block; box-shadow: 0 0 3px rgba(0, 0, 0, 0.4)"
								class="clearfix full-opacity-hover"><div>
										<span
											style="display: block; width: 20%; float: left; height: 7px;"
											class="bg-purple-active"></span><span class="bg-purple"
											style="display: block; width: 80%; float: left; height: 7px;"></span>
									</div>
									<div>
										<span
											style="display: block; width: 20%; float: left; height: 20px; background: #f9fafc;"></span><span
											style="display: block; width: 80%; float: left; height: 20px; background: #f4f5f7;"></span>
									</div></a>
							<p class="text-center no-margin" style="font-size: 12px">
								<spring:message code='sys.theme.skin.purpleLight'/>
							</p></li>
							<li style="float: left; width: 33.33333%; padding: 5px;"><a
								href="javascript:void(0);" data-skin="skin-green-light"
								style="display: block; box-shadow: 0 0 3px rgba(0, 0, 0, 0.4)"
								class="clearfix full-opacity-hover"><div>
										<span
											style="display: block; width: 20%; float: left; height: 7px;"
											class="bg-green-active"></span><span class="bg-green"
											style="display: block; width: 80%; float: left; height: 7px;"></span>
									</div>
									<div>
										<span
											style="display: block; width: 20%; float: left; height: 20px; background: #f9fafc;"></span><span
											style="display: block; width: 80%; float: left; height: 20px; background: #f4f5f7;"></span>
									</div></a>
							<p class="text-center no-margin" style="font-size: 12px">
								<spring:message code='sys.theme.skin.greenLight'/>
							</p></li>
							<li style="float: left; width: 33.33333%; padding: 5px;"><a
								href="javascript:void(0);" data-skin="skin-red-light"
								style="display: block; box-shadow: 0 0 3px rgba(0, 0, 0, 0.4)"
								class="clearfix full-opacity-hover"><div>
										<span
											style="display: block; width: 20%; float: left; height: 7px;"
											class="bg-red-active"></span><span class="bg-red"
											style="display: block; width: 80%; float: left; height: 7px;"></span>
									</div>
									<div>
										<span
											style="display: block; width: 20%; float: left; height: 20px; background: #f9fafc;"></span><span
											style="display: block; width: 80%; float: left; height: 20px; background: #f4f5f7;"></span>
									</div></a>
							<p class="text-center no-margin" style="font-size: 12px">
								<spring:message code='sys.theme.skin.redLight'/>
							</p></li>
							<li style="float: left; width: 33.33333%; padding: 5px;"><a
								href="javascript:void(0);" data-skin="skin-yellow-light"
								style="display: block; box-shadow: 0 0 3px rgba(0, 0, 0, 0.4)"
								class="clearfix full-opacity-hover"><div>
										<span
											style="display: block; width: 20%; float: left; height: 7px;"
											class="bg-yellow-active"></span><span class="bg-yellow"
											style="display: block; width: 80%; float: left; height: 7px;"></span>
									</div>
									<div>
										<span
											style="display: block; width: 20%; float: left; height: 20px; background: #f9fafc;"></span><span
											style="display: block; width: 80%; float: left; height: 20px; background: #f4f5f7;"></span>
									</div></a>
							<p class="text-center no-margin" style="font-size: 12px;">
								<spring:message code='sys.theme.skin.yellowLight'/>
							</p></li>
						</ul>
					</div>
				</div>
			</div>
		</aside>
		<!-- /.control-sidebar -->
	</div>
	<dp:csrftoken></dp:csrftoken>
	<!-- jQuery 2.2.3 -->
	<script
		src="${pageContext.request.contextPath}/static/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script
		src="${pageContext.request.contextPath}/static/bootstrap/js/bootstrap.min.js"></script>
	<!-- AdminLTE App -->
	<%-- <script
		src="${pageContext.request.contextPath}/static/dist/js/app.min.js"></script> --%>
	<!-- 修改了app.min.js 菜单伸缩不影响同级 -->
	<script
		src="${pageContext.request.contextPath}/static/dist/js/app.min_modify.js"></script>
	<script
		src="${pageContext.request.contextPath}/static/dist/js/app.setting.js"></script>
	<!-- SlimScroll 1.3.0 -->
	<script src="${pageContext.request.contextPath}/static/plugins/slimScroll/jquery.slimscroll.min.js"></script>
	<!-- basePath -->
    <script type="text/javascript">
        var basePath = "${pageContext.request.contextPath}";
        var ctx = basePath;
    </script>
    <!-- 菜单展开、导航栏以及滚动条 -->
    <dp:script src="${pageContext.request.contextPath}/static/common/js/main-init.js"></dp:script>
    
    <!-- pace -->
    <script src="${pageContext.request.contextPath}/static/plugins/pace/pace.min.js">
        $(document).ajaxStart(function() { Pace.restart(); });
    </script>
	<dp:script src="${pageContext.request.contextPath}/static/common/js/base.js"></dp:script>
	<!-- autosize textarea 高度自适应 -->
	<%-- <script src="${pageContext.request.contextPath}/static/plugins/autosize/autosize.min.js"></script> --%>
	
	<!-- layer.js -->
	<script src="${pageContext.request.contextPath}/static/plugins/layer-v3.1.1/layer/layer.js"></script>
	<!-- 引入被装饰页面的jsTag标签内的内容 如JS等 -->
	<sitemesh:write property='jsTag'/>
</body>

</html>
