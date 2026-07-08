package com.dp.plat.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.lowcode.dto.VersionDiffDTO;
import com.dp.plat.lowcode.dto.ConfigPackageDTO;
import com.dp.plat.lowcode.dto.VersionTreeNode;
import com.dp.plat.lowcode.entity.LowCodeConfigVersion;

import java.util.List;

/**
 * 低代码配置版本管理服务。
 */
public interface LowCodeConfigVersionService extends IService<LowCodeConfigVersion> {

    /**
     * 创建版本快照（不可变）。
     */
    LowCodeConfigVersion createSnapshot(SnapshotContext context);

    /**
     * 查询配置的版本历史。
     */
    List<LowCodeConfigVersion> getVersionHistory(String configType, Long configId);

    /**
     * 查询配置的版本树（线性构建：v1 → v2 → v3 链式树）。
     *
     * <p>当前版本实体无 parentVersionId 字段，按版本号升序线性构建父子关系：
     * 最早版本为根，后续版本依次挂在前一版本下。</p>
     */
    List<VersionTreeNode> getVersionTree(String configType, Long configId);

    /**
     * 对比两个版本的差异。
     */
    VersionDiffDTO diff(String configType, Long configId, Integer fromVersion, Integer toVersion);

    /**
     * 回滚到指定版本（用历史快照覆盖当前配置，生成新版本）。
     */
    LowCodeConfigVersion rollback(String configType, Long configId, Integer targetVersion, String changeLog);

    /**
     * 导出配置包（用于环境晋升）。
     */
    ConfigPackageDTO exportPackage(String sourceEnvironment, List<String> configCodes);

    /**
     * 导入配置包（环境晋升）。
     */
    void importPackage(ConfigPackageDTO pkg);

    /**
     * 快照上下文。
     */
    record SnapshotContext(String configType, Long configId, String configCode,
                           String snapshot, String changeLog) {
    }
}
