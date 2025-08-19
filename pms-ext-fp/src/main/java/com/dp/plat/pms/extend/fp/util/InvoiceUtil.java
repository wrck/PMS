package com.dp.plat.pms.extend.fp.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import cn.hutool.core.map.MapUtil;

public class InvoiceUtil {

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
}
