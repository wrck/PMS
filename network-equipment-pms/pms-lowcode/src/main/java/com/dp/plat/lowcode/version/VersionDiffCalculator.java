package com.dp.plat.lowcode.version;

import com.dp.plat.lowcode.dto.VersionDiffDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 版本 Diff 计算器。
 *
 * <p>对比两个 JSON 快照，返回结构化差异（新增/删除/修改 + 字段路径）。</p>
 */
@Component
@RequiredArgsConstructor
public class VersionDiffCalculator {

    private final ObjectMapper objectMapper;

    public VersionDiffDTO diff(String oldJson, String newJson, int fromVersion, int toVersion) {
        List<VersionDiffDTO.DiffEntry> entries = new ArrayList<>();
        try {
            JsonNode oldNode = objectMapper.readTree(oldJson);
            JsonNode newNode = objectMapper.readTree(newJson);
            compareNodes(oldNode, newNode, "", entries);
        } catch (Exception e) {
            throw new RuntimeException("Diff 计算失败", e);
        }
        return VersionDiffDTO.builder()
                .fromVersion(fromVersion)
                .toVersion(toVersion)
                .entries(entries)
                .build();
    }

    private void compareNodes(JsonNode oldNode, JsonNode newNode, String path,
                               List<VersionDiffDTO.DiffEntry> entries) {
        if (oldNode.isObject() && newNode.isObject()) {
            // 合并所有 key
            Iterator<String> oldFields = oldNode.fieldNames();
            Iterator<String> newFields = newNode.fieldNames();
            Set<String> allKeys = new TreeSet<>();
            oldFields.forEachRemaining(allKeys::add);
            newFields.forEachRemaining(allKeys::add);

            for (String key : allKeys) {
                String childPath = path.isEmpty() ? key : path + "." + key;
                if (!oldNode.has(key)) {
                    entries.add(VersionDiffDTO.DiffEntry.builder()
                            .changeType("ADDED")
                            .fieldPath(childPath)
                            .newValue(newNode.get(key).asText())
                            .build());
                } else if (!newNode.has(key)) {
                    entries.add(VersionDiffDTO.DiffEntry.builder()
                            .changeType("REMOVED")
                            .fieldPath(childPath)
                            .oldValue(oldNode.get(key).asText())
                            .build());
                } else {
                    compareNodes(oldNode.get(key), newNode.get(key), childPath, entries);
                }
            }
        } else if (oldNode.isArray() && newNode.isArray()) {
            // 数组：简化处理，逐元素对比
            int maxLen = Math.max(oldNode.size(), newNode.size());
            for (int i = 0; i < maxLen; i++) {
                String childPath = path + "[" + i + "]";
                if (i >= oldNode.size()) {
                    entries.add(VersionDiffDTO.DiffEntry.builder()
                            .changeType("ADDED")
                            .fieldPath(childPath)
                            .newValue(newNode.get(i).asText())
                            .build());
                } else if (i >= newNode.size()) {
                    entries.add(VersionDiffDTO.DiffEntry.builder()
                            .changeType("REMOVED")
                            .fieldPath(childPath)
                            .oldValue(oldNode.get(i).asText())
                            .build());
                } else {
                    compareNodes(oldNode.get(i), newNode.get(i), childPath, entries);
                }
            }
        } else {
            // 叶子节点：比较值
            if (!oldNode.equals(newNode)) {
                entries.add(VersionDiffDTO.DiffEntry.builder()
                        .changeType("MODIFIED")
                        .fieldPath(path)
                        .oldValue(oldNode.asText())
                        .newValue(newNode.asText())
                        .build());
            }
        }
    }
}
