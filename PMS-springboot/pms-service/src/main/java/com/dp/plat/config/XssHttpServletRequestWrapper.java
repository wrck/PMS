package com.dp.plat.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public XssHttpServletRequestWrapper(HttpServletRequest request) { super(request); }
    @Override public String getParameter(String name) { return clean(super.getParameter(name)); }
    @Override public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) return null;
        String[] clean = new String[values.length];
        for (int i = 0; i < values.length; i++) clean[i] = clean(values[i]);
        return clean;
    }
    private String clean(String value) {
        if (value == null) return null;
        return value.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
            .replaceAll("\"", "&quot;").replaceAll("'", "&#39;");
    }
}
