package com.dp.plat.decorators;

import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;

import com.dp.plat.context.HttpContext;

public class ContractNoList implements DisplaytagColumnDecorator {

	@Override
	public Object decorate(Object arg0, PageContext arg1, MediaTypeEnum arg2)
			throws DecoratorException {
		if (arg0 != null)
		{
			String contractNo = arg0.toString();
			return contractNo.replace(",", ",<br/>");
		}
		return HttpContext.getMessage("sys.wraper.unkownerror")+"&nbsp&nbsp&nbsp&nbsp";
	}

}
