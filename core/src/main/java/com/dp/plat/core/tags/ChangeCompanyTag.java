package com.dp.plat.core.tags;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.vo.UserInfoVO;

/**
 * 绘制公司及下拉切换公司下拉菜单
 * 
 * @author w02611
 *
 */
public class ChangeCompanyTag extends TagSupport {

	private static final long serialVersionUID = -8877023339963565901L;

	private boolean isNav;

	public boolean getIsNav() {
		return isNav;
	}

	public void setIsNav(boolean isNav) {
		this.isNav = isNav;
	}

	@SuppressWarnings("static-access")
	@Override
	public int doEndTag() throws JspException {
		Principal principal = UserContext.getCurrentPrincipal();
		if (principal == null) {
			return super.EVAL_PAGE;
		}
		List<UserInfoVO> userInfoList = principal.getUserInfoList();
		UserInfo currentUserInfo = principal.getUserInfo();
		String compName = principal.getCompName();
		if (StringUtils.isBlank(compName)) {
			return super.EVAL_PAGE;
		}
		StringBuilder node = new StringBuilder();
		if (isNav) {
			if (userInfoList.size() <= 1) {
				node.append("<a>").append(principal.getCompName()).append("</a>");
			} else {
				node.append("<li class='dropdown'>")
						.append("<a href='#' class='dropdown-toggle' data-toggle='dropdown' aria-expanded='false'>")
						.append("<span type='button' class='hidden-xs'>").append(principal.getCompName())
						.append("<span class='caret'></span></span></a>").append("<ul class='dropdown-menu' style='width:100%;'>");
				for (UserInfoVO temp : userInfoList) {
					if (temp.getCompID().equals(currentUserInfo.getCompID())) {
						continue;
					}
					node.append("<li class='changeCompanyLink'><a href='javaScript:void(0)' data-value='")
							.append(temp.getCompID()).append("'>").append(temp.getCompName()).append("</a></li>");
				}
				node.append("</ul></li>");
			}
		} else {
			if (userInfoList.size() <= 1) {
				node.append(principal.getCompName());
			} else {
				node.append("<span class='btn-group clearfix' id='changeCompanyBtnGroup'>")
						.append("<button type='button' id='changeCompanyBtn' class='btn bt-sm btn-default dropdown-toggle' data-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>")
						.append(principal.getCompName()).append("<span class='caret'></span>").append("</button>")
						.append("<span class='dropdown-menu'>");
				for (UserInfoVO temp : userInfoList) {
					if (temp.getCompID().equals(currentUserInfo.getCompID())) {
						continue;
					}
					node.append("<span class='changeCompanyLink'><a href='javaScript:void(0)' data-value='")
							.append(temp.getCompID()).append("'>").append(temp.getCompName()).append("</a></span>");
				}
				node.append("</span></span>");
			}
		}
		JspWriter out = pageContext.getOut();
		try {
			out.print(node);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.EVAL_PAGE;
	}
}
