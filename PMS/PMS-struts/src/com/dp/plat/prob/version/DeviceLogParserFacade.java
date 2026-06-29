package com.dp.plat.prob.version;

import com.dp.plat.prob.bean.DeviceVersionInfo;
import com.dp.plat.prob.util.DeviceVersionLogParser;

public class DeviceLogParserFacade {

    public static DeviceVersionInfo parse(String logContent) {
        return DeviceVersionLogParser.parse(logContent);
    }

    public static boolean matches(String logContent) {
        return DeviceVersionLogParser.matches(logContent);
    }
}
