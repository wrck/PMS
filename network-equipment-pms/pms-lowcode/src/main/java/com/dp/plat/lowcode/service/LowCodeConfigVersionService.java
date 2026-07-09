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
     * 回滚预览：对比"当前版本"与"目标版本"的差异，不实际执行回滚。
     *
     * <p>当前版本 = DEV 环境最新 ACTIVE 版本；目标版本 = targetVersion 指定的历史版本。
     * 借鉴 OutSystems LifeTime 的回滚预览功能。</p>
     *
     * @param configType    配置类型
     * @param configId      配置 ID
     * @param targetVersion 要回滚到的目标版本号
     * @return 版本差异 DTO
     */
    VersionDiffDTO rollbackPreview(String configType, Long configId, Integer targetVersion);

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
