package com.dp.plat.prob.util;

import com.dp.plat.prob.bean.DeviceVersionInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class DeviceVersionLogParser {
    
    private static final Pattern PATTERN_SOFTWARE_RELEASE = Pattern.compile("Software Release\\s+(\\S+)");
    private static final Pattern PATTERN_PCB_VERSION = Pattern.compile("PCB Version:\\s*(\\S+)");
    private static final Pattern PATTERN_CPLD_VERSION = Pattern.compile("CPLD Version:\\s*(\\S+)");
    private static final Pattern PATTERN_SERIAL_NUMBER = Pattern.compile("Serial Number:\\s*(\\S+)");
    private static final Pattern PATTERN_BASIC_CONBOOT = Pattern.compile("BASIC CONBOOT Version:\\s*(\\S+)");
    
    private static final Pattern PATTERN_SLOT_DEVICE = Pattern.compile(
            "\\[(?:CHASSIS\\s+(\\d+)\\s+)?SLOT\\s+(\\d+)\\]\\s+(\\S+(?:\\s+\\S+)*?)\\s+" +
            "\\(PCB\\)(\\S+),?\\s*" +
            "\\(CPLD\\)(\\S+),?\\s*" +
            "(?:\\(FPGA\\)([^,]+),?\\s*)?" +
            "(?:\\(CONBOOT\\)([^,]*),?\\s*)?" +
            "(?:\\(SERIAL\\)(\\S+))?"
    );
    
    public static DeviceVersionInfo parse(String logContent) {
        if (StringUtils.isBlank(logContent)) {
            return null;
        }

        DeviceVersionInfo result = new DeviceVersionInfo();
        
        Matcher matcher = PATTERN_SOFTWARE_RELEASE.matcher(logContent);
        if (matcher.find()) {
            result.setConp(StringUtils.trimToNull(matcher.group(1)));
        }
        
        matcher = PATTERN_PCB_VERSION.matcher(logContent);
        if (matcher.find()) {
            result.setPcb(StringUtils.trimToNull(matcher.group(1)));
        }
        
        matcher = PATTERN_CPLD_VERSION.matcher(logContent);
        if (matcher.find()) {
            result.setCpld(StringUtils.trimToNull(matcher.group(1)));
        }
        
        matcher = PATTERN_SERIAL_NUMBER.matcher(logContent);
        if (matcher.find()) {
            String serial = StringUtils.trimToNull(matcher.group(1));
            if (StringUtils.isNotBlank(serial)) {
                result.setSerial(serial);
            }
        }
        
        matcher = PATTERN_BASIC_CONBOOT.matcher(logContent);
        if (matcher.find()) {
            result.setBoot(StringUtils.trimToNull(matcher.group(1)));
        }
        
        matcher = PATTERN_SLOT_DEVICE.matcher(logContent);
        while (matcher.find()) {
            DeviceVersionInfo slotInfo = new DeviceVersionInfo();
            slotInfo.setChassis(StringUtils.trimToNull(matcher.group(1)));
            slotInfo.setSlot(StringUtils.trimToNull(matcher.group(2)));
            slotInfo.setDeviceName(StringUtils.trimToNull(matcher.group(3)));
            slotInfo.setPcb(StringUtils.trimToNull(matcher.group(4)));
            slotInfo.setCpld(StringUtils.trimToNull(matcher.group(5)));
            slotInfo.setFpga(StringUtils.trimToNull(matcher.group(6)));
            slotInfo.setBoot(StringUtils.trimToNull(matcher.group(7)));
            slotInfo.setSerial(StringUtils.trimToNull(matcher.group(8)));

            if (StringUtils.isNotBlank(slotInfo.getSerial())) {
                result.addSlotDevice(slotInfo);
            }
        }
        
        return result;
    }
    
    public static boolean matches(String logContent) {
        if (logContent == null || logContent.trim().isEmpty()) {
            return false;
        }
        return PATTERN_SOFTWARE_RELEASE.matcher(logContent).find() || 
               PATTERN_PCB_VERSION.matcher(logContent).find() ||
               PATTERN_SLOT_DEVICE.matcher(logContent).find();
    }
}
