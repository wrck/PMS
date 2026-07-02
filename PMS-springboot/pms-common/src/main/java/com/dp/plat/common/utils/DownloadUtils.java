package com.dp.plat.common.utils;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
public class DownloadUtils {
    public static void download(HttpServletResponse response, File file, String fileName) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        try (InputStream in = new FileInputStream(file); OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[4096]; int len;
            while ((len = in.read(buffer)) != -1) out.write(buffer, 0, len);
        }
    }
}
