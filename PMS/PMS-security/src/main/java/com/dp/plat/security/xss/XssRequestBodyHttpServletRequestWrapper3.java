package com.dp.plat.security.xss;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 对每个post请求的参数过滤一些关键字，替换成安全的，例如：< > ' " \ / # & 防止SQL注入和XSS攻击
 * 
 * @author w02611
 */
public class XssRequestBodyHttpServletRequestWrapper3 extends HttpServletRequestWrapper {
    private static final String DEFAULT_CHARSET = "UTF-8";

    private CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(this.getServletContext());
    private HttpServletRequest orginRequest;
    private boolean isMultipart;
    private MultipartHttpServletRequest multipartRequest;
    
    private byte[] requestBody;
    private Charset charSet;
    private final Map<String, ArrayList<String>> paramHashValues = new LinkedHashMap<>();
    protected Map<String, String[]> parameterMap;

    public XssRequestBodyHttpServletRequestWrapper3(HttpServletRequest request) {
        super(request);
        orginRequest = request;
        String contentType = request.getContentType();
        isMultipart = multipartResolver.isMultipart(request);
        String method = request.getMethod().toUpperCase();
        if ("POST".equals(method)) {
            // 缓存请求body
            String requestBodyStr = null;
            try {
                requestBodyStr = getRequestBody(request);
                if (null != requestBodyStr && !"".equals(requestBodyStr)) {
                    String temp = escapeHtml(requestBodyStr);
                    JSONObject resultJson = JSON.parseObject(temp);
                    requestBody = resultJson.toString().getBytes(getCharset());
                } else {
                    requestBody = new byte[0];
                }
            } catch (Throwable e) {
                if (null == requestBody && null != requestBodyStr) {
                    if (!isMultipart) {
                        requestBodyStr = escapeHtml(requestBodyStr);
                    }
                    requestBody = requestBodyStr.getBytes(getCharset());
                }
            } finally {
                try {
                    processParameters(requestBody, 0, getContentLength(), getCharset());
                } catch (Exception e) {
                    processParameters(requestBody, 0, requestBody.length, getCharset());
                }
            }
        }
        // 缓存请求查询参数
        String queryParams = request.getQueryString();
        if (null != queryParams && "" != queryParams) {
            byte[] bytes = queryParams.getBytes(getCharset());
            processParameters(bytes, 0, bytes.length, getCharset());
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if (null != requestBody && requestBody.length > 0) {
            if (null != parameterMap) {
                return parameterMap;
            }
            parameterMap = new LinkedHashMap<String, String[]>();
            Enumeration<String> enumeration = getParameterNames();
            while (enumeration.hasMoreElements()) {
                String name = enumeration.nextElement();
                String[] values = getParameterValues(name);
                parameterMap.put(name, values);
            }

            return parameterMap;
        } else {
            return super.getParameterMap();
        }
    }

    @Override
    public String[] getParameterValues(String parameter) {
        List<String> values = null;
        String[] temp = null;
        if (requestBody == null || requestBody.length == 0) {
            temp = super.getParameterValues(parameter);
            values = null;
            if (null == temp) {
                values = null;
            } else {
                values = Arrays.asList(temp);
            }
        } else {
            values = paramHashValues.get(parameter);
        }
        if (values == null) {
            return null;
        }
//      System.err.println(parameter + ":" + String.valueOf(values));
        if ("password".equals(parameter)) {
            return values.toArray(new String[values.size()]);
        }
        int count = values.size();
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = escapeHtml(values.get(i));
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        List<String> values = null;
        String temp = null;
        if (requestBody == null|| requestBody.length == 0) {
            temp = super.getParameter(parameter);
            values = null;
            if (null == temp) {
                values = null;
            } else {
                values = Arrays.asList(temp);
            }
        } else {
            values = paramHashValues.get(parameter);
        }
        if (values == null) {
            return null;
        }
//      System.err.println(parameter + ":" + String.valueOf(values));
        if (values.size() == 0) {
            return "";
        }
        String value = values.get(0);
        if ("password".equals(parameter)) {
            return value;
        }
        // return StringEscapeUtils.escapeHtml(value);
        return escapeHtml(value);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        if (null != requestBody && requestBody.length != 0) {
            return Collections.enumeration(paramHashValues.keySet());
        } else {
            return super.getParameterNames();
        }
    }

    public String getRequestBody(HttpServletRequest request) throws IOException {
        return StreamUtils.copyToString(request.getInputStream(), getCharset());
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    public ServletInputStream getInputStream() throws IOException {
        if ((isMultipart && requestBody == null) || requestBody == null) {
            return super.getInputStream();
        }

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBody);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    /**
     * 重写StringEscapeUtils.escapeHtml()方法，避免过滤中文
     * 
     * @param s
     * @return
     */
    public static String escapeHtml(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
            case '>':
                sb.append("&gt;");
                break;
            case '<':
                sb.append("&lt;");
                break;
            case '&':
                sb.append('＆');
                break;
//          case ';':
//              sb.append('；');
//              break;  
            default:
                sb.append(c);
                break;
            }
        }
        return sb.toString();
    }

    private void processParameters(byte bytes[], int start, int len, Charset charset) {
        if (isMultipart) {
            // 如果是multipart表单，增加multipartRequest解析
            multipartResolver.setDefaultEncoding(DEFAULT_CHARSET);
            multipartRequest = multipartResolver.resolveMultipart(this);
            Map<String, String[]> parameters = multipartRequest.getParameterMap();
            for (Entry<String, String[]> entry : parameters.entrySet()) {
                paramHashValues.put(entry.getKey(), new ArrayList<String>(Arrays.asList(entry.getValue())));
            }
            return;
        }
        
        int pos = start;
        int end = start + len;

        while (pos < end) {
            int nameStart = pos;
            int nameEnd = -1;
            int valueStart = -1;
            int valueEnd = -1;

            boolean parsingName = true;
            boolean decodeName = false;
            boolean decodeValue = false;
            boolean parameterComplete = false;

            do {
                switch (bytes[pos]) {
                case '=':
                    if (parsingName) {
                        // Name finished. Value starts from next character
                        nameEnd = pos;
                        parsingName = false;
                        valueStart = ++pos;
                    } else {
                        // Equals character in value
                        pos++;
                    }
                    break;
                case '&':
                    if (parsingName) {
                        // Name finished. No value.
                        nameEnd = pos;
                    } else {
                        // Value finished
                        valueEnd = pos;
                    }
                    parameterComplete = true;
                    pos++;
                    break;
                case '%':
                case '+':
                    // Decoding required
                    if (parsingName) {
                        decodeName = true;
                    } else {
                        decodeValue = true;
                    }
                    pos++;
                    break;
                default:
                    pos++;
                    break;
                }
            } while (!parameterComplete && pos < end);

            if (pos == end) {
                if (nameEnd == -1) {
                    nameEnd = pos;
                } else if (valueStart > -1 && valueEnd == -1) {
                    valueEnd = pos;
                }
            }

            if (nameEnd <= nameStart) {
                if (valueStart == -1) {
// &&
// Do not flag as error
                    continue;
                }
// &=foo&
                continue;
// invalid chunk - it's better to ignore
            }

            // &a=
//          if (valueEnd <= valueStart) {
//              continue;
//          }

            CharBuffer cb = charset.decode(ByteBuffer.wrap(bytes, nameStart, nameEnd - nameStart));
            String tmpName = new String(cb.array(), cb.arrayOffset(), cb.length());
            String tmpValue;

            // tmpName.setBytes(bytes, nameStart, nameEnd - nameStart);
            if (valueStart >= 0) {
                cb = charset.decode(ByteBuffer.wrap(bytes, valueStart, valueEnd - valueStart));
                tmpValue = new String(cb.array(), cb.arrayOffset(), cb.length());
//              tmpValue.setBytes(bytes, valueStart, valueEnd - valueStart);
            } else {
                cb = charset.decode(ByteBuffer.wrap(bytes, 0, 0));
                tmpValue = new String(cb.array(), cb.arrayOffset(), cb.length());
//              tmpValue.setBytes(bytes, 0, 0);
            }

            String name;
            String value;

            if (decodeName) {
                tmpName = urlDecode(tmpName);
            }
            name = tmpName.toString();

            if (valueStart >= 0) {
                if (decodeValue) {
                    tmpValue = urlDecode(tmpValue);
                }
                value = tmpValue.toString();
            } else {
                value = "";
            }

            try {
                addParameter(name, value);
            } catch (IllegalStateException ise) {
                break;
            }
        }
    }

    private void addParameter(String key, String value) throws IllegalStateException {
        if (key == null) {
            return;
        }

        ArrayList<String> values = paramHashValues.get(key);
        if (values == null) {
            values = new ArrayList<>(1);
            paramHashValues.put(key, values);
        }
        values.add(value);
    }

    private String urlDecode(String value) {
//      if (value.contains("%u")) {
//          return Encodes.urlDecode(value);
//      } else {
        try {
            return URLDecoder.decode(value, getCharset().name());
        } catch (UnsupportedEncodingException e) {
            return "";// 非UTF-8编码
        }
//      }
    }

    private Charset getCharset() {
        if (null == charSet) {
            String charSetStr = getCharacterEncoding();
            if (charSetStr == null) {
                charSetStr = DEFAULT_CHARSET;
            }
            charSet = Charset.forName(charSetStr);
        }
        return charSet;
    }

    /**
     * 获取最原始的request
     * 
     * @return
     */
    public HttpServletRequest getOrginRequest() {
        return orginRequest;
    }

    /**
     * 获取最原始的request的静态方法
     * 
     * @return
     */
    public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
        if (req instanceof XssRequestBodyHttpServletRequestWrapper3) {
            return ((XssRequestBodyHttpServletRequestWrapper3) req).getOrginRequest();
        }
        return req;
    }
}
