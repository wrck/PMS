package com.dp.plat.pms.extend.fp.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.dp.plat.rules.util.AviatorUtils;

import cn.hutool.core.map.MapUtil;

public class InvoiceUtil {
    
    private static Supplier<Map<String, Object>> configSupplier;
    
    public synchronized static void initConfig(Supplier<Map<String, Object>> configSupplier) {
        InvoiceUtil.configSupplier = configSupplier;
    }
    
    public static Map<String, Object> getConfig() {
        try {
            return configSupplier.get();
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    /**
     * 获取发票的发票号
     * @param invoice
     * @return
     */
    public static String getUniqueInvoiceNumber(Map<String, Object> invoice) {
        if (invoice == null || invoice.isEmpty()) {
            return null;
        }
        String uniqueInvoiceNumber = MapUtil.getStr(invoice, "uniqueInvoiceNumber");
        if (StringUtils.isNotBlank(uniqueInvoiceNumber)) {
            return uniqueInvoiceNumber;
        }
        String invoiceCode = MapUtil.getStr(invoice, "invoice_code");
        String invoiceNumber = MapUtil.getStr(invoice, "invoice_number");
        List<String> uniqueInvoiceParts = Arrays.asList(invoiceCode, invoiceNumber).stream()
                .filter(s -> s != null && StringUtils.isNotBlank(s))
                .collect(Collectors.toList());
        if (uniqueInvoiceParts.isEmpty()) {
            return null;
        }
        uniqueInvoiceNumber = StringUtils.join(uniqueInvoiceParts, "-");
        return uniqueInvoiceNumber;
    }
    
    /**
     * 返回交付件发票原件类型
     * @return
     */
    public static <T> T getFileInvoiceType(T defalutValue) {
        return getFileInvoiceType(getConfig(), defalutValue);
    }
    
    /**
     * 返回交付件发票原件类型
     * @return
     */
    public static <T> T getFileInvoiceType(Map<String, Object> config, T defalutValue) {
        return (T) MapUtils.getObject(config, "invoiceType", defalutValue);
    }
    
    /**
     * 返回交付件验收材料类型
     * @return
     */
    public static <T> T getFileInspectionType(T defalutValue) {
        return getFileInspectionType(getConfig(), defalutValue);
    }
    
    /**
     * 返回交付件验收材料类型
     * @return
     */
    public static <T> T getFileInspectionType(Map<String, Object> config, T defalutValue) {
        return (T) MapUtils.getObject(config, "inspectionType", defalutValue);
    }
    
    /**
     * 检查是否是发票类型
     * @return
     */
    public static boolean checkFileInvoiceType(Map<String, Object> invoice) {
        if (invoice == null) {
            return false;
        }
        return checkFileInvoiceType(invoice, getConfig());
    }
    
    /**
     * 检查是否是发票状态
     * @return
     */
    public static boolean checkFileInvoiceType(Map<String, Object> invoice, Map<String, Object> config) {
        if (invoice == null) {
            return false;
        }
        String condition = (String) invoice.get("condition");
        
        condition = MapUtils.getString(config, "invoiceTypeCondition", condition);
        try {
            if (StringUtils.isNotBlank(condition)) {
                Map<String, Object> env = new HashMap<String, Object>();
                env.put("entity", Collections.singletonMap("entity", invoice));
                return Boolean.TRUE.equals(AviatorUtils.exceute(condition, env));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !(!MapUtil.getBool(invoice, "needVerify", false) && StringUtils.isBlank(MapUtil.getStr(invoice, "invoice_number")));
    }
    
    /**
     * 检查是否是发票状态
     * @return
     */
    public static boolean checkFileInvoiceStatus(Map<String, Object> invoice) {
        if (invoice == null) {
            return false;
        }
        return checkFileInvoiceStatus(invoice, getConfig());
    }
    
    public static boolean checkFileInvoiceStatus(Map<String, Object> invoice, Map<String, Object> config) {
        if (invoice == null) {
            return false;
        }
        String condition = (String) invoice.get("condition");
        
        condition = MapUtils.getString(config, "invoiceStatusCondition", condition);
        try {
            if (StringUtils.isNotBlank(condition)) {
                Map<String, Object> env = new HashMap<String, Object>();
                env.put("entity", Collections.singletonMap("entity", invoice));
                return Boolean.TRUE.equals(AviatorUtils.exceute(condition, env));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Boolean identify = MapUtil.getBool(invoice, "identify", false);
        Boolean needVerify = MapUtil.getBool(invoice, "needVerify", false);
        Boolean verified = MapUtil.getBool(invoice, "verified_status", false);
        return identify && (!needVerify || verified);
    }
}
