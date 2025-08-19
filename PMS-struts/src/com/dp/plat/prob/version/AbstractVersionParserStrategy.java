package com.dp.plat.prob.version;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class AbstractVersionParserStrategy {
    
    // 判断该解析器是否适用于当前输入
    public boolean matches(String input) {
        return getPattern().matcher(input).find();
    }

    // 获取当前解析器使用的正则表达式 Pattern
    public abstract Pattern getPattern();

    // 对输入进行预处理（如替换特殊符号）
    protected String preprocess(String input) {
        return input;
    }
    
    // 模板方法：定义了解析的整体流程
    
    // 执行实际的解析逻辑（由子类实现）
    protected final Map<String, Map<String, List<SoftVersionParser>>> parseVersionRanges(String softVersion) {
        return parseVersionRanges(softVersion, "", false);
    }
    
    protected final Map<String, Map<String, List<SoftVersionParser>>> parseVersionRanges(String softVersion, String fixedSoftVersionTypes) {
        return parseVersionRanges(softVersion, fixedSoftVersionTypes, false);
    }
    
    protected abstract Map<String, Map<String, List<SoftVersionParser>>> parseVersionRanges(String softVersion, String fixedSoftVersionTypes, boolean defaultMarkAll);

    protected final List<SoftVersionParser> parseVersion(String softVersion) {
        return parseVersion(softVersion, false);
    }
    
    protected abstract List<SoftVersionParser> parseVersion(String softVersion, boolean defaultMarkAll);
    
}
