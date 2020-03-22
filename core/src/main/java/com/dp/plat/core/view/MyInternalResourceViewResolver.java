package com.dp.plat.core.view;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

public class MyInternalResourceViewResolver extends InternalResourceViewResolver {

	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		// if (viewName.matches(".*/\\d+") ||
		// viewName.matches(".*/\\bdetail.*"))
		// {
		String[] viewNames = viewName.split("/");
		int length = viewNames.length;
		viewName = "";
		if (length == 1) {
			return super.buildView(viewNames[0]);
		}
		for (int i = 0; i < length; i++) {
			String view = viewNames[i];
			if (view.matches("[0-9]{1,}") || view.matches("\\bdetail.*")) {
				view = "-detail";
			} else {
				if (i == length - 1 && length>2) {
					view = "-" + view;
				} else if (i != 0) {
					view = "/" + view;
				}
			}
			viewName += view;
		}
		// }
		return super.buildView(viewName);
	}

	@Override
	protected String[] getViewNames() {
		// TODO Auto-generated method stub
		return super.getViewNames();
	}

	@Override
	public void setViewNames(String[] viewNames) {
		// TODO Auto-generated method stub
		super.setViewNames(viewNames);
	}

}
