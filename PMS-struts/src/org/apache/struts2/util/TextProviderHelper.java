package org.apache.struts2.util;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import java.util.Collections;
import java.util.List;

public class TextProviderHelper {
	private static final Logger LOG = LoggerFactory.getLogger(TextProviderHelper.class);

	public static String getText(String key, String defaultMessage, List<Object> args, ValueStack stack) {
		return getText(key, defaultMessage, args, stack, true);
	}

	public static String getText(String key, String defaultMessage, List<Object> args, ValueStack stack,
			boolean searchStack) {
		String msg = null;
		TextProvider tp = null;
		for (Object o : stack.getRoot()) {
			if ((o instanceof TextProvider)) {
				tp = (TextProvider) o;
				msg = tp.getText(key, null, args, stack);

				break;
			}
		}
		if (msg == null) {
			if (searchStack) {
				msg = stack.findString(defaultMessage);
			}
			if (msg == null) {
				msg = defaultMessage;
			}
			if (LOG.isWarnEnabled()) {
				if (tp != null) {
					LOG.warn("The first TextProvider in the ValueStack (" + tp.getClass().getName()
							+ ") could not locate the message resource with key '" + key + "'", new String[0]);
				} else {
					LOG.warn("Could not locate the message resource '" + key
							+ "' as there is no TextProvider in the ValueStack.", new String[0]);
				}
				if (defaultMessage.equals(msg)) {
					LOG.warn("The default value expression '" + defaultMessage
							+ "' was evaluated and did not match a property.  The literal value '" + defaultMessage
							+ "' will be used.", new String[0]);
				} else {
					LOG.warn("The default value expression '" + defaultMessage + "' evaluated to '" + msg + "'",
							new String[0]);
				}
			}
		}
		return msg;
	}

	public static String getText(String key, String defaultMessage, ValueStack stack) {
		return getText(key, defaultMessage, Collections.emptyList(), stack);
	}
}
