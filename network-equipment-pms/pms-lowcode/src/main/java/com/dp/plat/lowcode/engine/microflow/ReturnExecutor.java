package com.dp.plat.lowcode.engine.microflow;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 返回节点执行器：设置 context.result 并标记 terminated。
 *
 * <p>节点 config: {expression: "Groovy 表达式"}</p>
 */
@Slf4j
@Component
public class ReturnExecutor implements MicroflowNodeExecutor {

    @Override
    public MicroflowNodeType getNodeType() {
        return MicroflowNodeType.RETURN;
    }

    @Override
    public String execute(Map<String, Object> nodeDef, MicroflowContext context) {
        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) nodeDef.get("config");
        if (config != null && config.containsKey("expression")) {
            Binding binding = new Binding(context.getVariables());
            Object value = new GroovyShell(binding).evaluate((String) config.get("expression"));
            context.setResult(value);
        }
        context.setTerminated(true);
        log.debug("ReturnExecutor: result={}", context.getResult());
        return null;
    }
}
