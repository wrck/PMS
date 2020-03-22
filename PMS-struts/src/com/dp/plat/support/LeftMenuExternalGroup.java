package com.dp.plat.support;

import javax.servlet.jsp.PageContext;

import com.dp.plat.context.HttpContext;
import com.dp.plat.context.VContext;

public class LeftMenuExternalGroup extends LeftMenuGroup {

	private LeftMenuExternalLi[] children;

	public LeftMenuExternalLi[] getChildren() {
		return children;
	}

	public void setChildren(LeftMenuExternalLi[] children) {
		this.children = children;
	}

	@Override
	public void drow(PageContext pageContext) {
		this.setLis(children);
		VContext.getVM(pageContext.getOut(), "com/dp/plat/vmpage/LeftMenuGroup.vm", "imgurl", getImgurl(), "title",
				HttpContext.getText(getTitlesrc()), "lis", getLis(), "pageContext", pageContext);
	}

}

class LeftMenuExternalLi extends LeftMenuLi {
	public void drow(PageContext pageContext) {
		VContext.getVM(pageContext.getOut(), "com/dp/plat/vmpage/LeftMenuLi.vm", "imgurl", getImgurl(), "text",
				HttpContext.getText(getText()), "title", HttpContext.getText(getTitle()), "url", getUrl(), "target", "_blank");
	}

	public void childdrow(PageContext pageContext) {
		VContext.getVM(pageContext.getOut(), "com/dp/plat/vmpage/LeftMenuLichild.vm", "imgurl", getChildimgurl(),
				"text", HttpContext.getText(getText()), "title", HttpContext.getText(getTitle()), "url", getUrl(), "target", "_blank");
	}

}
