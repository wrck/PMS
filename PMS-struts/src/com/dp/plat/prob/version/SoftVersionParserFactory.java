package com.dp.plat.prob.version;

import java.util.Arrays;
import java.util.List;

public class SoftVersionParserFactory {

    private final List<AbstractSoftVersionStrategy> parsers = Arrays.asList(
        new SoftVersionStrategy(),
        new NewSoftVersionStrategy()
    );

    public AbstractSoftVersionStrategy getParserFor(String input) {
        for (AbstractSoftVersionStrategy parser : parsers) {
            if (parser.matches(input)) {
                return parser;  
            }
        }
        throw new IllegalArgumentException("No suitable parser found for the given input: " + input);
    }
}
