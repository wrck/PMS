package com.dp.plat.lowcode.engine.microflow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 微流执行引擎。
 *
 * <p>遍历微流定义中的 DAG 节点，按节点类型分发到对应的 NodeExecutor 执行。
 * 支持顺序执行 + 条件跳转 + 提前终止（RETURN/END）。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MicroflowEngine {

    private final ObjectMapper objectMapper;
    private final List<MicroflowNodeExecutor> executors;

    /**
     * 执行微流。
     *
     * @param definitionJson 微流定义 JSON（含 nodes: [{id, type, config}], edges: [{source, target}]）
     * @param inputs         输入参数
     * @return 执行上下文（含 result）
     */
    @SuppressWarnings("unchecked")
    public MicroflowContext execute(String definitionJson, Map<String, Object> inputs) {
        try {
            Map<String, Object> definition = objectMapper.readValue(definitionJson,
                    new TypeReference<Map<String, Object>>() {});
            List<Map<String, Object>> nodes = (List<Map<String, Object>>) definition.get("nodes");
            List<Map<String, Object>> edges = (List<Map<String, Object>>) definition.get("edges");

            // 构建节点查找表
            Map<String, Map<String, Object>> nodeMap = nodes.stream()
                    .collect(Collectors.toMap(n -> (String) n.get("id"), n -> n, (a, b) -> a, HashMap::new));

            // 构建边查找表 source → target
            Map<String, String> edgeMap = edges.stream()
                    .collect(Collectors.toMap(e -> (String) e.get("source"), e -> (String) e.get("target"), (a, b) -> a));

            // 找 START 节点
            String currentNodeId = nodes.stream()
                    .filter(n -> "START".equals(n.get("type")))
                    .map(n -> (String) n.get("id"))
                    .findFirst()
                    .orElse(nodes.isEmpty() ? null : (String) nodes.get(0).get("id"));

            MicroflowContext context = new MicroflowContext(inputs);

            // 循环执行
            int safetyCounter = 0;
            while (currentNodeId != null && !context.isTerminated() && safetyCounter++ < 1000) {
                Map<String, Object> node = nodeMap.get(currentNodeId);
                if (node == null) break;

                String type = (String) node.get("type");
                MicroflowNodeType nodeType = MicroflowNodeType.valueOf(type);
                MicroflowNodeExecutor executor = findExecutor(nodeType);

                String nextNodeId = null;
                if (executor != null) {
                    nextNodeId = executor.execute(node, context);
                }
                // 如果执行器未指定下一节点，按默认边走
                if (nextNodeId == null && !context.isTerminated()) {
                    nextNodeId = edgeMap.get(currentNodeId);
                }
                currentNodeId = nextNodeId;
            }
            return context;
        } catch (Exception e) {
            throw new RuntimeException("微流执行失败", e);
        }
    }

    private MicroflowNodeExecutor findExecutor(MicroflowNodeType type) {
        return executors.stream()
                .filter(e -> e.getNodeType() == type)
                .findFirst()
                .orElse(null);
    }
}
