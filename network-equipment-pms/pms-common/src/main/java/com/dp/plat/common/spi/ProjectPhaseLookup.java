package com.dp.plat.common.spi;

/** 项目阶段归属查询端口。 */
public interface ProjectPhaseLookup {
    Long findProjectId(Long phaseId);
}
