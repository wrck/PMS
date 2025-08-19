package com.dp.plat.pms.extend.fp.util;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.ByteString;

public class MultipartBodyBuilder {
    
    private final List<MultipartFormItem> formItems = new ArrayList<>();
    
    // 表单项（支持 File / InputStream / String）
    private static class MultipartFormItem {
        final String name;
        final Object value; // File, InputStream, 或 String

        MultipartFormItem(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }
    
    /**
     * 批量设置表单数据（模仿 Hutool）
     *
     * @param form 表单数据，key 为字段名，value 支持 String、File、File[]、Iterable、数组等
     * @return this
     */
    public MultipartBodyBuilder form(Map<String, Object> form) {
        if (form == null) {
            return this;
        }

        for (Map.Entry<String, Object> entry : form.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            this.form(key, value); // 复用已有的 form(String, Object) 方法
        }
        return this;
    }
    
    /**
     * 设置表单数据（模仿 Hutool）
     */
    public MultipartBodyBuilder form(String name, Object value) {
        if (name == null || value == null) {
            return this; // 忽略 null
        }

        // 处理文件
        if (value instanceof File) {
            return this.form(name, (File) value);
        }

        if (value instanceof File[]) {
            return this.form(name, (File[]) value);
        }

        if (value instanceof Iterable<?>) {
            Iterable<?> iterable = (Iterable<?>) value;
            List<String> list = new ArrayList<>();
            for (Object item : iterable) {
                if (item != null) {
                    list.add(item.toString());
                }
            }
            return putToForm(name, String.join(",", list));
        }

        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            List<String> list = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                Object item = Array.get(value, i);
                if (item != null) {
                    list.add(item.toString());
                }
            }
            return putToForm(name, String.join(",", list));
        }

        // 其他类型转字符串
        return putToForm(name, value.toString());
    }

    /**
     * 添加单个文件
     */
    public MultipartBodyBuilder form(String name, File file) {
        if (name != null && file != null && file.exists()) {
            formItems.add(new MultipartFormItem(name, file));
        }
        return this;
    }

    /**
     * 添加多个文件
     */
    public MultipartBodyBuilder form(String name, File[] files) {
        if (name == null || files == null) return this;
        for (File file : files) {
            if (file != null && file.exists()) {
                formItems.add(new MultipartFormItem(name, file));
            }
        }
        return this;
    }

    /**
     * 添加 InputStream（模拟 Resource）
     */
    public MultipartBodyBuilder form(String name, String filename, InputStream inputStream, String contentType) {
        if (name != null && inputStream != null) {
            formItems.add(new MultipartFormItem(name, new StreamPart(filename, inputStream, contentType)));
        }
        return this;
    }

    /**
     * 内部：添加普通文本字段
     */
    private MultipartBodyBuilder putToForm(String name, String value) {
        if (name != null && value != null) {
            formItems.add(new MultipartFormItem(name, value));
        }
        return this;
    }

    // ================ 执行请求 ================

    /**
     * 执行请求
     */
    public MultipartBody.Builder buildOkHttp() throws IOException {
        // 构建 multipart body
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        for (MultipartFormItem item : formItems) {
            if (item.value instanceof File) {
                File file = (File) item.value;
                MediaType mediaType = MediaType.parse(guessContentType(file));
                RequestBody fileBody = RequestBody.create(file, mediaType);
                bodyBuilder.addFormDataPart(item.name, file.getName(), fileBody);
            } else if (item.value instanceof byte[]) {
                // 字节数组类型，假设为文件内容
                RequestBody streamBody = InputStreamRequestBody.create((byte[]) item.value);
                bodyBuilder.addFormDataPart(item.name, item.name, streamBody);
            } else if (item.value instanceof StreamPart) {
                StreamPart sp = (StreamPart) item.value;
                MediaType mediaType = MediaType.parse(sp.contentType);
                RequestBody streamBody = new InputStreamRequestBody(mediaType, sp.inputStream);
                bodyBuilder.addFormDataPart(item.name, sp.filename, streamBody);
            } else if (item.value instanceof String) {
                bodyBuilder.addFormDataPart(item.name, (String) item.value);
            }
        }

        return bodyBuilder;
    }
    
    public MultipartEntityBuilder buildHttp() {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE); // 浏览器兼容模式
        builder.setCharset(java.nio.charset.StandardCharsets.UTF_8);

        for (MultipartFormItem item : formItems) {
            if (item.value instanceof File) {
                File file = (File) item.value;
                ContentType mediaType = ContentType.parse(guessContentType(file));
                builder.addBinaryBody(item.name, file, mediaType, file.getName());
            } else if (item.value instanceof byte[]) {
                // 字节数组类型，假设为文件内容
                builder.addBinaryBody(item.name, (byte[]) item.value);
            } else if (item.value instanceof StreamPart) {
                StreamPart sp = (StreamPart) item.value;
                ContentType mediaType = ContentType.parse(sp.contentType);
                builder.addBinaryBody(item.name, sp.inputStream, mediaType, sp.filename);
            } else if (item.value instanceof String) {
                builder.addTextBody(item.name, (String) item.value);
            }
        }
        return builder;
    }

    // ================ 工具方法 ================

    private String guessContentType(File file) {
        String type = URLConnection.guessContentTypeFromName(file.getName());
        return type != null ? type : "application/octet-stream";
    }

    // 用于封装 InputStream 的内部类
    private static class StreamPart {
        final String filename;
        final InputStream inputStream;
        final String contentType;

        StreamPart(String filename, InputStream inputStream, String contentType) {
            this.filename = filename;
            this.inputStream = inputStream;
            this.contentType = contentType;
        }
    }

    // 用于封装 InputStream 的 RequestBody
    private static class InputStreamRequestBody extends RequestBody {
        private final MediaType contentType;
        private final InputStream inputStream;

        InputStreamRequestBody(MediaType contentType, InputStream inputStream) {
            this.contentType = contentType;
            this.inputStream = inputStream;
        }

        @Override
        public MediaType contentType() {
            return contentType;
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                sink.write(ByteString.of(buffer, 0, bytesRead));
            }
        }

        @Override
        public long contentLength() throws IOException {
            return -1; // 未知长度
        }
    }
}