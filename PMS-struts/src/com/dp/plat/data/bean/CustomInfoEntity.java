package com.dp.plat.data.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

/**
 * @author w02611
 *
 */
public class CustomInfoEntity implements Serializable {

    private static final long serialVersionUID = 7977954432644560580L;

    // 自定义信息
    private JsonCustomInfo<String, Object> customInfo;
    
    // 自定义信息委派
    private JsonCustomInfo<String, String> customStrInfo;
    
    /**
     * 获取自定义信息
     *
     * @return customInfo - 自定义信息
     */
    public Map<String, Object> getCustomInfo() {
        return customInfo;
    }

    /**
     * 设置自定义信息
     *
     * @param customInfo 自定义信息
     */
    public void setCustomInfo(Map<String, Object> customInfo) {
        this.customInfo = new JsonCustomInfo<String, Object>(customInfo);
        
        // 建立委派关系
        if (this.customStrInfo != null) {
            this.customStrInfo.setDelegate(this.customInfo);
        }
    }

    /**
     * 获取自定义信息，指定key
     *
     * @return customInfo.get(Key) - 自定义信息
     */
    public Object getCustomInfoByKey(String key) {
        Map<String, Object> customInfo = getCustomInfo();
        if (customInfo != null && !customInfo.isEmpty()) {
            return customInfo.get(key);
        }
        return null;
    }
    
    /**
     * 获取自定义信息，指定key，带默认值
     *
     * @param key 
     * @param defaultValue
     * @return customInfo.getOrDefault(Key, defaultValue) - 自定义信息
     */
    public Object getCustomInfoByKey(String key, Object defaultValue) {
        Map<String, Object> customInfo = getCustomInfo();
        if (customInfo != null && !customInfo.isEmpty()) {
            return customInfo.getOrDefault(key, defaultValue);
        }
        return defaultValue;
    }

    /**
     * 设置自定义信息, key, value
     *
     * @param customInfo.put(key, value) 自定义信息
     */
    public void setCustomInfoByKey(String key, Object value) {
        // 不用this.getCustomInfo()防止出现循环调用
        Map<String, Object> customInfo = this.customInfo;
        if (customInfo == null) {
            customInfo = new HashMap<>();
            this.setCustomInfo(customInfo);
            customInfo = this.getCustomInfo();
        }
        customInfo.put(key, value);
    }
    
    /**
     * 用于前端传值的自定义信息的辅助集合，转换为String,String
     * @return
     */
    public JsonCustomInfo<String, String> getCustomStrInfo() {
        if (this.customStrInfo == null) {
//            this.customStrInfo = new HashMap<String, String>();
//            if (this.customInfo != null) {
//                for (Entry<String, Object> entry : this.customInfo.entrySet()) {
//                    if (entry.getValue() instanceof String) {
//                        this.customStrInfo.put(entry.getKey(), (String) entry.getValue());
//                    }
//                }
//            }
            // 直接使用JSON转化，可能会导致部分对象转换为字符串
            String custonInfoStr = JSON.toJSONString(this.customInfo);
            this.customStrInfo = new JsonCustomInfo<String, String>(this.customInfo, JSON.parseObject(custonInfoStr, new TypeReference<Map<String, String>> () {}));
        } 
//        else {
//            this.setCustomStrInfo(customStrInfo);
//        }
        return customStrInfo;
    }

    /**
     * 用于前端传值的自定义信息的辅助集合，转换为String,String
     * @return
     */
    public void setCustomStrInfo(Map<String, String> customStrInfo) {
        if (this.customInfo == null) {
            this.setCustomInfo(new HashMap<String, Object>());
        }
        
//        this.customStrInfo = new JsonCustomInfo<String, String>(this.customInfo, customStrInfo);
//        if (customStrInfo instanceof JsonCustomInfo) {
//            ((JsonCustomInfo<String, String>) customStrInfo).setDelegate(this.customStrInfo);
//        }
        
        // 如果传入的customStrInfo本身就有值，则需要调用一次putAll，向customInfo传值
        if (customStrInfo instanceof JsonCustomInfo) {
            this.customStrInfo = ((JsonCustomInfo<String, String>) customStrInfo);
            this.customStrInfo.setDelegate(this.customInfo);
            this.customStrInfo.putAll(customStrInfo);
        } else {
            this.customStrInfo = new JsonCustomInfo<String, String>(this.customInfo, customStrInfo);
        }
    }

//    /**
//     * 自定义信息的辅助字段，转换为String,String
//     * @return
//     */
//    public Map<String, String> getCustomStrInfo() {
//        if (this.customStrInfo == null) {
////            this.customStrInfo = new HashMap<String, String>();
////            if (this.customInfo != null) {
////                for (Entry<String, Object> entry : this.customInfo.entrySet()) {
////                    if (entry.getValue() instanceof String) {
////                        this.customStrInfo.put(entry.getKey(), (String) entry.getValue());
////                    }
////                }
////            }
//            // 直接使用JSON转化，可能会导致部分对象转换为字符串
//            String custonInfoStr = JSON.toJSONString(this.customInfo);
//            this.customStrInfo = JSON.parseObject(custonInfoStr, new TypeReference<Map<String, String>> () {});
//        } else {
//            this.setCustomStrInfo(customStrInfo);
//        }
//        return customStrInfo;
//    }
//
//    /**
//     * 自定义信息的辅助字段，转换为String,String
//     * @return
//     */
//    public void setCustomStrInfo(Map<String, String> customStrInfo) {
//        this.customStrInfo = customStrInfo;
//        if (this.customStrInfo != null && !this.customStrInfo.isEmpty()) {
//            for (Entry<String, String> kv : this.customStrInfo.entrySet()) {
//                this.setCustomInfoByKey(kv.getKey(), kv.getValue());
//            }
//        }
//    }
    
}
