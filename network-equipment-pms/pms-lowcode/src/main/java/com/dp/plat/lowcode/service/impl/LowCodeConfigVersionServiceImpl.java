package com.dp.plat.lowcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.lowcode.dto.ConfigPackageDTO;
import com.dp.plat.lowcode.dto.VersionDiffDTO;
import com.dp.plat.lowcode.entity.LowCodeConfigVersion;
import com.dp.plat.lowcode.mapper.LowCodeConfigVersionMapper;
import com.dp.plat.lowcode.service.LowCodeConfigVersionService;
import com.dp.plat.lowcode.version.VersionDiffCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
