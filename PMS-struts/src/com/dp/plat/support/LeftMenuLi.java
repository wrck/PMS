package com.dp.plat.support;

import javax.servlet.jsp.PageContext;

import com.dp.plat.context.HttpContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.context.VContext;

public class LeftMenuLi implements LeftMenuLiInterface {
	private String imgurl = "images/left_biao.gif";
	private String text = "";
	private String title = "";
	private String url = "";
	private LeftMenuLiInterface[] childlis = null;
	private String childimgurl = "images/right_jiantou.png";

	public void drow(PageContext pageContext) {
		if (UserContext.getUserContext().getPermissionMap().get(text) != null
				&& UserContext.getUserContext().getPermissionMap().get(text) == 1) {
			VContext.getVM(pageContext.getOut(),
					"com/dp/plat/vmpage/LeftMenuLi.vm", "imgurl", imgurl,
					"text", HttpContext.getText(text), "title",
					HttpContext.getText(title), "url", url);
		}
	}

	public void childdrow(PageContext pageContext) {
		if (UserContext.getUserContext().getPermissionMap().get(text) != null
				&& UserContext.getUserContext().getPermissionMap().get(text) == 1) {
			VContext.getVM(pageContext.getOut(),
					"com/dp/plat/vmpage/LeftMenuLichild.vm", "imgurl",
					childimgurl, "text", HttpContext.getText(text), "title",
					HttpContext.getText(title), "url", url);
		}
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public LeftMenuLiInterface[] getChildlis() {
		return childlis;
	}

	public void setChildlis(LeftMenuLiInterface[] childlis) {
		this.childlis = childlis;
	}

	public String getChildimgurl() {
		return childimgurl;
	}

	public void setChildimgurl(String childimgurl) {
		this.childimgurl = childimgurl;
	}

}
