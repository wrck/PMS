<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib prefix="decorator"
	uri="http://www.opensymphony.com/sitemesh/decorator"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<dp:base />
<title>
	<s:text name="sys.title"></s:text>
</title>
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="x-ua-compatible" content="ie=10" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<!-- IE6-10 -->
<!-- <link rel="shortcut icon" href="images/icon/favicon.ico"/> -->
<!-- Everybody else -->
<!-- <link rel="icon" href="images/icon/favicon.ico"/> -->

<!--mian 新 Bootstrap 核心 CSS 文件 -->
<link rel="stylesheet" href="bootstrap-3.3.4-dist/css/bootstrap.min.css" />
<!-- 引入对Bootstrap的自扩展文件 -->
<link rel="stylesheet" href="css/bootstrap-ex.css" />
<!-- 可选的Bootstrap主题文件（一般不用引入） -->
<link rel="stylesheet" href="bootstrap-3.3.4-dist/css/bootstrap-theme.min.css" />

<link rel="stylesheet" type="text/css" href="css/common.css" />
<link rel="stylesheet" type="text/css" href="css/displaytag.css" />
<link rel="stylesheet" type="text/css" href="css/jquery-ui-1.10.4.custom.min.css" />

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script type="text/javascript" src="js/jquery-2.1.4.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.10.4.custom.min.js"></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script type="text/javascript" src="bootstrap-3.3.4-dist/js/bootstrap.min.js"></script>
<!-- 解决jQueryUI button 与 bootstrap button 的冲突 -->
<script type="text/javascript"> $.fn.bootstrapBtn =$.fn.button.noConflict(); </script>
<script type="text/javascript" src="js/jquery.ui.datepicker-zh-CN.min.js"></script>

<!-- 引入额外扩展js文件 -->
<script type="text/javascript" src="js/bootstrap-ex.js"></script>
<script type="text/javascript" src="js/dp-ui.js"></script>
<script type="text/javascript" src="js/util.js"></script>
<script type="text/javascript" src="js/zxml.src.js"></script>
<script type="text/javascript" src="js/zxmlqueue.js"></script>
<script type="text/javascript" src="js/calendar.js"></script>
<script type="text/javascript" src="js/common.js"></script>

<!-- 下拉多选 -->
<link rel="stylesheet" type="text/css" href="multiselect/jquery.multiselect.css" />
<script type="text/javascript" src="multiselect/jquery.multiselect.js"></script>

<decorator:head />
</head>
<!-- oncopy="return false;" -->
<body>
	<div id="whole">
		<table class="mainframe" cellpadding="0" cellspacing="0"
			style="width: 100%;">
			<colgroup>
				<col style="width: 200px;" />
				<col style="width: 100%" />
			</colgroup>
			<!-- 用于限制页面宽度 -->
			<!-- 页面的Banner -->
			<tr>
				<td colspan="2" class="mainframe_head"><%@include
						file="/plat/common/header.jsp"%></td>
			</tr>
			<tr>
				<td colspan="2">
					<table cellpadding="0" cellspacing="0" class="window_border"
						width="100%">
						<colgroup>
							<col style="width: 200px;" />
							<col style="width: 100%" />
						</colgroup>
						<tr>
							<!-- 页面的左侧下拉菜单 -->
							<td class="mainframe_left" valign="top" style="vertical-align: top;">
								<table cellspacing="0" cellpadding="0">
									<tr>
										<td id="menu_container"><dp:leftmenu /> <br /></td>
									</tr>
								</table>
							</td>
							<td class="mainframe_right"
								style="background: white; padding-left: 5px;">
								<!-- 页面的右侧页面导航栏 -->
								<table class="navibar" cellspacing="0" cellpadding="0">
									<tr>
										<!-- 导航栏 -->
										<td class="navileft">

											<ol class="breadcrumb">
												<li><decorator:usePage id="mypage"></decorator:usePage>
													<%
														String netm = mypage.getProperty("meta.netm");
														if (netm == null || netm.equals("0")) {
															out.print(mypage.getProperty("meta.group"));
														} else {
															String netmgroup = mypage.getProperty("meta.netmgroup");
															if (netmgroup == null) {
																out.print(mypage.getProperty("meta.group"));
															} else {
																out.print(netmgroup);
															}
														}
													%></li>

												<%
													String sup = mypage.getProperty("meta.supfunction");
													if (sup != null && sup != "") {
												%>
												<li>
													<!--   <img src="images/right_jiantou.png" border="0"
							align="absmiddle" /> --> <decorator:getProperty
														property="meta.supfunction"></decorator:getProperty>
												</li>
												<%
													}
												%>
												<li>
													<!-- <img src="images/right_jiantou.png" border="0"
							align="absmiddle" /> --> <span class="navileft_current"><decorator:getProperty
															property="meta.function"></decorator:getProperty> </span>
												</li>
											</ol>
										</td>
										<!-- 页面附加链接或按钮 -->
										<td class="naviright"></td>
									</tr>
								</table> <!-- 内容页面 -->
								<table class="s_pagecontainer" cellspacing="0" cellpadding="0">
									<tr>
										<td><decorator:body /></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr>
				<td colspan="2" class="mainframe_foot"><%@include
						file="/plat/common/footer.jsp"%></td>
			</tr>
		</table>
	</div>
</body>
</html>
