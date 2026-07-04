package com.dp.plat.common.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.dp.plat.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * EasyExcel-based helper for exporting/importing Excel files through HTTP.
 */
@Slf4j
@UtilityClass
public class ExcelUtils {

    private static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String CONTENT_DISPOSITION = "attachment;filename=%s";

    /**
     * Export a list of data to the response as an .xlsx file.
     *
     * @param response HTTP response to write to
     * @param fileName file name without extension
     * @param sheetName target sheet name
     * @param head     head class carrying EasyExcel annotations
     * @param data     rows to write
     * @param <T>      row type
     */
    public static <T> void export(HttpServletResponse response, String fileName, String sheetName,
                                  Class<T> head, List<T> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        prepareResponse(response, fileName);
        try (OutputStream out = response.getOutputStream()) {
            EasyExcel.write(out, head).sheet(sheetName).doWrite(data);
        } catch (IOException e) {
            log.error("Export excel failed: {}", fileName, e);
            throw new BusinessException("导出 Excel 失败: " + e.getMessage());
        }
    }

    /**
     * Export an empty template (header only) so users can fill it in.
     *
     * @param response HTTP response to write to
     * @param fileName file name without extension
     * @param sheetName target sheet name
     * @param head     head class carrying EasyExcel annotations
     * @param <T>      row type
     */
    public static <T> void exportTemplate(HttpServletResponse response, String fileName, String sheetName,
                                          Class<T> head) {
        export(response, fileName, sheetName, head, new ArrayList<>());
    }

    /**
     * Parse an uploaded Excel file into a list of DTOs.
     *
     * @param file uploaded file
     * @param head head class carrying EasyExcel annotations
     * @param <T>  row type
     * @return parsed rows
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> head) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("导入文件不能为空");
        }
        List<T> rows = new ArrayList<>();
        try (InputStream in = file.getInputStream()) {
            EasyExcel.read(in, head, new ReadListener<T>() {
                @Override
                public void invoke(T data, AnalysisContext context) {
                    rows.add(data);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    // no-op
                }
            }).sheet().doRead();
        } catch (IOException e) {
            log.error("Import excel failed", e);
            throw new BusinessException("解析 Excel 失败: " + e.getMessage());
        }
        return rows;
    }

    /**
     * Parse an uploaded Excel file and run a per-row validator. Rows that pass
     * validation are accumulated into {@link ExcelImportResult#getSuccessList()};
     * rows that fail (or throw) are recorded as {@link ExcelImportError}.
     *
     * @param file      uploaded file
     * @param head      head class carrying EasyExcel annotations
     * @param validator per-row validator; should throw a {@link BusinessException}
     *                  (or any {@link RuntimeException}) carrying the failure reason
     * @param <T>       row type
     * @return aggregated success/error result
     */
    public static <T> ExcelImportResult<T> importWithValidation(MultipartFile file, Class<T> head,
                                                                Consumer<T> validator) {
        List<T> rows = importExcel(file, head);
        List<T> successList = new ArrayList<>(rows.size());
        List<ExcelImportError> errors = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            T row = rows.get(i);
            // rowIndex is 1-based to align with the Excel row number excluding the header.
            int rowIndex = i + 1;
            try {
                if (validator != null) {
                    validator.accept(row);
                }
                successList.add(row);
            } catch (RuntimeException e) {
                errors.add(ExcelImportError.builder()
                        .rowIndex(rowIndex)
                        .rowData(rowToString(row))
                        .errorMessage(e.getMessage())
                        .build());
            }
        }
        return ExcelImportResult.<T>builder()
                .successList(successList)
                .errors(errors)
                .build();
    }

    /**
     * Configure the response headers for an .xlsx download.
     *
     * @param response HTTP response
     * @param fileName file name without extension
     */
    private static void prepareResponse(HttpServletResponse response, String fileName) {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String encoded;
        try {
            encoded = URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8.name())
                    .replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            encoded = fileName + ".xlsx";
        }
        response.setHeader("Content-Disposition", String.format(CONTENT_DISPOSITION, encoded));
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
    }

    /**
     * Best-effort flat representation of a row for error reporting.
     *
     * @param row row instance
     * @return comma-separated field values
     */
    private static String rowToString(Object row) {
        if (row == null) {
            return "";
        }
        try {
            java.lang.reflect.Field[] fields = row.getClass().getDeclaredFields();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fields.length; i++) {
                if (java.lang.reflect.Modifier.isStatic(fields[i].getModifiers())) {
                    continue;
                }
                fields[i].setAccessible(true);
                Object value = fields[i].get(row);
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(value == null ? "" : value.toString());
            }
            return sb.toString();
        } catch (Exception e) {
            return row.toString();
        }
    }
}
