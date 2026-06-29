package com.dp.plat.security.xss.struts;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;

import com.dp.plat.security.util.JsoupUtil;
import com.opensymphony.xwork2.LocaleProvider;

public class MMultiPartRequestWrapper extends MultiPartRequestWrapper {

    public MMultiPartRequestWrapper(MultiPartRequest multiPartRequest, HttpServletRequest request, String saveDir,
            LocaleProvider provider) {
        super(multiPartRequest, request, saveDir, provider);
    }

    @Override
    public String getParameter(String name) {
        name = JsoupUtil.escape(name);
        // 返回值之前 先进行过滤
        return JsoupUtil.escape(super.getParameter(name));
    }

    @Override
    public String[] getParameterValues(String name) {
        name = JsoupUtil.escape(name);
        // 返回值之前 先进行过滤
        String[] values = super.getParameterValues(name);
        if(values != null){
            for (int i = 0; i < values.length; i++) {
                values[i] = JsoupUtil.escape(values[i]);
            }
        }
        return values;
    }

    @Override
    public Enumeration<String> getParameterNames() {
//        Enumeration<String> names = super.getParameterNames();
//        while(names.hasMoreElements()){
//            String name = names.nextElement();
//            name = JsoupUtil.escape(name);
//        }
//        return names;
        
        return super.getParameterNames();
    }
    
}