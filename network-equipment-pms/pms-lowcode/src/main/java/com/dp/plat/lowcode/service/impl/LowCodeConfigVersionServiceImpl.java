package com.dp.plat.lowcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.lowcode.dto.ConfigPackageDTO;
import com.dp.plat.lowcode.dto.VersionDiffDTO;
import com.dp.plat.lowcode.dto.VersionTreeNode;
import com.dp.plat.lowcode.entity.LowCodeConfigVersion;
import com.dp.plat.lowcode.mapper.LowCodeConfigVersionMapper;
import com.dp.plat.lowcode.service.LowCodeConfigVersionService;
import com.dp.plat.lowcode.version.VersionDiffCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 低代码配置版本管理服务实现。
 */
@Service
@RequiredArgsConstructor
public class LowCodeConfigVersionServiceImpl
        extends ServiceImpl<LowCodeConfigVersionMapper, LowCodeConfigVersion>
        implements LowCodeConfigVersionService {

    private final VersionDiffCalculator diffCalculator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LowCodeConfigVersion createSnapshot(SnapshotContext context) {
        // 查询当前最大版本号
        List<LowCodeConfigVersion> existing = baseMapper.selectList(
                new LambdaQueryWrapper<LowCodeConfigVersion>()
                        .eq(LowCodeConfigVersion::getConfigType, context.configType())
                        .eq(LowCodeConfigVersion::getConfigId, context.configId())
                        .eq(LowCodeConfigVersion::getEnvironment, "DEV")
                        .orderByDesc(LowCodeConfigVersion::getVersion));
        int nextVersion = existing.isEmpty() ? 1 : existing.get(0).getVersion() + 1;

        LowCodeConfigVersion snapshot = LowCodeConfigVersion.builder()
                .configType(context.configType())
                .configId(context.configId())
                .configCode(context.configCode())
                .version(nextVersion)
                .snapshot(context.snapshot())
                .changeLog(context.changeLog())
                .status("ACTIVE")
                .environment("DEV")
                .build();
        baseMapper.insert(snapshot);
        return snapshot;
    }

    @Override
    public List<LowCodeConfigVersion> getVersionHistory(String configType, Long configId) {
        return baseMapper.selectList(new LambdaQueryWrapper<LowCodeConfigVersion>()
                .eq(LowCodeConfigVersion::getConfigType, configType)
                .eq(LowCodeConfigVersion::getConfigId, configId)
                .orderByDesc(LowCodeConfigVersion::getVersion));
    }

    @Override
    public List<VersionTreeNode> getVersionTree(String configType, Long configId) {
        // 查询全部版本并按版本号升序（无 parentVersionId，线性构建链式树）
        List<LowCodeConfigVersion> versions = baseMapper.selectList(
                new LambdaQueryWrapper<LowCodeConfigVersion>()
                        .eq(LowCodeConfigVersion::getConfigType, configType)
                        .eq(LowCodeConfigVersion::getConfigId, configId)
                        .orderByAsc(LowCodeConfigVersion::getVersion));
        if (versions.isEmpty()) {
            return new ArrayList<>();
        }
        // 升序构建链：v1 为根，v2 挂在 v1 下，v3 挂在 v2 下……
        // 返回列表仅含根节点（前端 el-tree 接收根数组）
        VersionTreeNode root = toTreeNode(versions.get(0));
        VersionTreeNode current = root;
        for (int i = 1; i < versions.size(); i++) {
            VersionTreeNode child = toTreeNode(versions.get(i));
            current.getChildren().add(child);
            current = child;
        }
        List<VersionTreeNode> tree = new ArrayList<>();
        tree.add(root);
        return tree;
    }

    /** 实体 → 树节点（createTime 转字符串，便于前端直接展示） */
    private VersionTreeNode toTreeNode(LowCodeConfigVersion v) {
        return VersionTreeNode.builder()
                .version(v.getVersion())
                .configCode(v.getConfigCode())
                .changeLog(v.getChangeLog())
                .status(v.getStatus())
                .environment(v.getEnvironment())
                .createBy(v.getCreateBy())
                .createTime(v.getCreateTime() == null ? null : v.getCreateTime().toString())
                .children(new ArrayList<>())
                .build();
    }

    @Override
    public VersionDiffDTO diff(String configType, Long configId,
                                Integer fromVersion, Integer toVersion) {
        LowCodeConfigVersion from = getVersion(configType, configId, fromVersion);
        LowCodeConfigVersion to = getVersion(configType, configId, toVersion);
        return diffCalculator.diff(from.getSnapshot(), to.getSnapshot(),
                fromVersion, toVersion);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LowCodeConfigVersion rollback(String configType, Long configId,
                                          Integer targetVersion, String changeLog) {
        LowCodeConfigVersion target = getVersion(configType, configId, targetVersion);
        // 回滚 = 用历史快照生成新版本（不删除历史）
        SnapshotContext context = new SnapshotContext(
                configType, configId, target.getConfigCode(),
                target.getSnapshot(),
                StringUtils.hasText(changeLog) ? changeLog : "回滚到版本 " + targetVersion);
        return createSnapshot(context);
    }

    @Override
    public ConfigPackageDTO exportPackage(String sourceEnvironment, List<String> configCodes) {
        List<LowCodeConfigVersion> versions = baseMapper.selectList(
                new LambdaQueryWrapper<LowCodeConfigVersion>()
                        .eq(LowCodeConfigVersion::getEnvironment, sourceEnvironment)
                        .in(LowCodeConfigVersion::getConfigCode, configCodes)
                        .eq(LowCodeConfigVersion::getStatus, "ACTIVE"));

        List<ConfigPackageDTO.PackageItem> items = versions.stream()
                .map(v -> ConfigPackageDTO.PackageItem.builder()
                        .configType(v.getConfigType())
                        .configId(v.getConfigId())
                        .configCode(v.getConfigCode())
                        .version(v.getVersion())
                        .snapshot(v.getSnapshot())
                        .build())
                .collect(Collectors.toList());

        return ConfigPackageDTO.builder()
                .sourceEnvironment(sourceEnvironment)
                .items(items)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importPackage(ConfigPackageDTO pkg) {
        for (ConfigPackageDTO.PackageItem item : pkg.getItems()) {
            // 在目标环境创建新版本快照
            LowCodeConfigVersion snapshot = LowCodeConfigVersion.builder()
                    .configType(item.getConfigType())
                    .configId(item.getConfigId())
                    .configCode(item.getConfigCode())
                    .version(item.getVersion())
                    .snapshot(item.getSnapshot())
                    .changeLog("从 " + pkg.getSourceEnvironment() + " 环境晋升")
                    .status("ACTIVE")
                    .environment(pkg.getTargetEnvironment())
                    .build();
            baseMapper.insert(snapshot);
        }
    }

    private LowCodeConfigVersion getVersion(String configType, Long configId, Integer version) {
        LowCodeConfigVersion v = baseMapper.selectOne(new LambdaQueryWrapper<LowCodeConfigVersion>()
                .eq(LowCodeConfigVersion::getConfigType, configType)
                .eq(LowCodeConfigVersion::getConfigId, configId)
                .eq(LowCodeConfigVersion::getVersion, version));
        if (v == null) {
            throw new IllegalArgumentException("版本不存在: " + configType + "/" + configId + "/v" + version);
        }
        return v;
    }
}
