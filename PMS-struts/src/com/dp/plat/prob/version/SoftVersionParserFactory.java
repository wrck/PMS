package com.dp.plat.prob.version;

import java.util.Arrays;
import java.util.List;

import com.dp.plat.exception.CustomRuntimeException;

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
        throw new CustomRuntimeException("没有匹配的软件版本格式: " + input);
    }
}
