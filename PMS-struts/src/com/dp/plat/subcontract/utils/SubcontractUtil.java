package com.dp.plat.subcontract.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dp.plat.context.SpringContext;
import com.dp.plat.context.SystemContext;
import com.dp.plat.subcontract.constant.SubcontractConstant;
import com.dp.plat.util.AviatorUtils;

import cn.hutool.core.map.MapUtil;

public class SubcontractUtil {

    /**
     * 返回交付件发票原件类型
     * @return
     */
    public static String getDeliveryInoviceType() {
        return SystemContext.getSystemContext().getTextValue(SubcontractConstant.SUBCONTRACT_INSPECTION_DELIVERY_TYPES_INVOICE, "发票原件");
    }
    
    /**
     * 返回交付件验收材料类型
     * @return
     */
    public static String getDeliveryInspectionType() {
        return SystemContext.getSystemContext().getTextValue(SubcontractConstant.SUBCONTRACT_INSPECTION_DELIVERY_TYPES_INSPECTION, "验收材料");
    }
    
    /**
     * 检查是否是发票类型
     * @return
     */
    public static boolean checkDeliveryInoviceType(Map<String, Object> invoice) {
        if (invoice == null) {
            return false;
        }
        String condition = (String) invoice.get("condition");
        
        if (SpringContext.getApplicationContext() != null) {
            condition = SystemContext.getSystemContext().getTextValue(SubcontractConstant.SUBCONTRACT_INSPECTION_DELIVERY_CHECK_INVOICE_CONDITION, condition);
        }
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
    
    public static boolean checkDeliveryInvoiceStatus(Map<String, Object> invoice) {
        if (invoice == null) {
            return false;
        }
        String condition = (String) invoice.get("condition");
        
        if (SpringContext.getApplicationContext() != null) {
            condition = SystemContext.getSystemContext().getTextValue(SubcontractConstant.SUBCONTRACT_INSPECTION_DELIVERY_CHECK_INVOICE_STATUS_CONDITION, condition);
        }
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
