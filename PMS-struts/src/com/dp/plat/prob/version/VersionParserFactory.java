package com.dp.plat.prob.version;

import java.util.Arrays;
import java.util.List;

public class VersionParserFactory {

    private final List<AbstractVersionParserStrategy> parsers = Arrays.asList(
        new LegacyVersionParserStrategy(),
        new NewVersionParserStrategy()
    );

    public AbstractVersionParserStrategy getParserFor(String input) {
        for (AbstractVersionParserStrategy parser : parsers) {
            if (parser.matches(input)) {
                return parser;
            }
        }
        throw new IllegalArgumentException("No suitable parser found for the given input: " + input);
    }
}
