package com.dp.plat.prob.version;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.dp.plat.prob.util.LegacyVersionUtil;

public class LegacyVersionParserStrategy extends AbstractVersionParserStrategy {

    private static final Pattern PATTERN = LegacyVersionUtil.PATTERN;

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    protected String preprocess(String input) {
        return super.preprocess(input);
    }

    @Override
    protected Map<String, Map<String, List<SoftVersionParser>>> parseVersionRanges(String softVersion, String fixedSoftVersionTypes, boolean defaultMarkAll) {
        return LegacyVersionUtil.createSoftVersionRangeParsers(softVersion, fixedSoftVersionTypes, defaultMarkAll);
    }

    @Override
    protected List<SoftVersionParser> parseVersion(String softVersion, boolean defaultMarkAll) {
        return LegacyVersionUtil.createSoftVersionParser(softVersion, defaultMarkAll);
    }
    
}
