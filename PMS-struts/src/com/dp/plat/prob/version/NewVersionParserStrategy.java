package com.dp.plat.prob.version;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.dp.plat.prob.util.SoftNewVersionUtil;

public class NewVersionParserStrategy extends AbstractVersionParserStrategy {

    private static final Pattern PATTERN = SoftNewVersionUtil.PATTERN;

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
        return SoftNewVersionUtil.createSoftVersionRangeParsers(softVersion, fixedSoftVersionTypes, defaultMarkAll);
    }

    @Override
    protected List<SoftVersionParser> parseVersion(String softVersion, boolean defaultMarkAll) {
        return SoftNewVersionUtil.createSoftVersionParser(softVersion, defaultMarkAll);
    }
    
}
