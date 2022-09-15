package com.dp.plat.data.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Json自定义信息
 * @author w02611
 *
 */
public class JsonCustomInfo extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    JsonCustomInfo(Map<String, Object> map) {
        super();
        if (map != null) {
            this.putAll(map);
        }
    }
}
