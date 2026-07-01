package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.PmsMaintenance;

import java.util.List;
import java.util.Map;

public interface MaintenanceService {
    IPage<PmsMaintenance> queryPage(Integer pageNum, Integer pageSize, Long projectId, String maintenanceType);

    PmsMaintenance getDetail(Long id);

    void create(PmsMaintenance maintenance);

    void update(PmsMaintenance maintenance);

    void delete(Long id);

    /** 查询服务交付记录 - 迁移自 MaintenanceAction.serviceDelivery() */
    List<Map<String, Object>> queryServiceDelivery(Long maintenanceId);

    /** 上传运维文件 - 迁移自 MaintenanceAction.uploadFileList() */
    void uploadFile(Long maintenanceId, String fileIds);

    /** 查询运维文件列表 - 迁移自 MaintenanceAction.toUploadFile() */
    List<Map<String, Object>> queryFiles(Long maintenanceId);
}
