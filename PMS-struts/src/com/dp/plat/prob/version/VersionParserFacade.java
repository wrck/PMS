package com.dp.plat.prob.version;

import java.util.List;
import java.util.Map;

public class VersionParserFacade {

    private static final VersionParserFactory factory = new VersionParserFactory();
    
    public static AbstractVersionParserStrategy getParser(String input) {
        return factory.getParserFor(input);
    }

    public static List<SoftVersionParser> parseVersion(String softVersion) {
        return parseVersion(softVersion, false);
    }
    
    public static List<SoftVersionParser> parseVersion(String softVersion, boolean defaultMarkAll) {
        return getParser(softVersion).parseVersion(softVersion, defaultMarkAll);
    }
    
    public static Map<String, Map<String, List<SoftVersionParser>>> parseVersionRanges(String softVersion) {
        return parseVersionRanges(softVersion, "", false);
    }
    
    public static Map<String, Map<String, List<SoftVersionParser>>> parseVersionRanges(String softVersion, String fixedSoftVersionTypes) {
        return parseVersionRanges(softVersion, fixedSoftVersionTypes, false);
    }
    
    public static Map<String, Map<String, List<SoftVersionParser>>> parseVersionRanges(String softVersion, String fixedSoftVersionTypes, boolean defaultMarkAll) {
        return getParser(softVersion).parseVersionRanges(softVersion, fixedSoftVersionTypes, defaultMarkAll);
    }

}
