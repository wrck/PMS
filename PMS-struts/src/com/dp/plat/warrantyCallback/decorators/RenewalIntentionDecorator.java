package com.dp.plat.warrantyCallback.decorators;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.PageContext;

import org.apache.commons.collections.MapUtils;
import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;

public class RenewalIntentionDecorator implements DisplaytagColumnDecorator {
	
	private static Map<?, ?> kvMap = Collections.emptyMap();
	
	static {
		kvMap = MapUtils.putAll(new HashMap<Integer, String>(),
				new Object[] { null, "未回访", 0, "无", 1, "有", 2, "待定" });
	}

	@Override
	public Object decorate(Object columnValue, PageContext pageContext, MediaTypeEnum media)
			throws DecoratorException {
		return kvMap.get(columnValue);
	}
	
}