package com.dp.plat.decorators;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;

import com.dp.plat.context.HttpContext;

public class AmountThousandth implements DisplaytagColumnDecorator {

	@Override
	public Object decorate(Object arg0, PageContext arg1, MediaTypeEnum arg2) throws DecoratorException {
		if (arg0 != null) {
			String amount = arg0.toString();
			DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");
			BigDecimal b = new BigDecimal(amount);
			return decimalFormat.format(b);
		}
		return HttpContext.getMessage("sys.wraper.unkownerror") + "&nbsp&nbsp&nbsp&nbsp";
	}

}
