package com.dp.plat.security.xss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dp.plat.security.util.JsoupUtil;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final Log logger = LogFactory.getLog(XssHttpServletRequestWrapper.class);

    public XssHttpServletRequestWrapper(HttpServletRequest request) {

        super(request);
    }

    @Override

    public String getHeader(String name) {
        String strHeader = super.getHeader(name);
        if (StringUtils.isEmpty(strHeader)) {
            return strHeader;
        }
        return JsoupUtil.clean(super.getHeader(name));

    }

    @Override

    public String getParameter(String name) {

        String strParameter = super.getParameter(name);

        if (StringUtils.isEmpty(strParameter)) {

            return strParameter;

        }

        return JsoupUtil.clean(super.getParameter(name));

    }

    @Override

    public String[] getParameterValues(String name) {

        String[] values = super.getParameterValues(name);

        if (values == null) {

            return values;

        }

        int length = values.length;

        String[] escapseValues = new String[length];

        for (int i = 0; i < length; i++) {

            // 过滤一切可能的xss攻击字符串
            escapseValues[i] = JsoupUtil.clean(values[i]).trim();

            if (!StringUtils.equals(escapseValues[i], values[i])) {

                logger.debug("xss字符串过滤前：" + values[i] + "\r\n" + "过滤后：" + escapseValues[i]);

            }

        }

        return escapseValues;

    }

}