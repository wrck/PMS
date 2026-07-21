package com.dp.plat.deliverable.spi;

import com.dp.plat.common.dto.TemplateSnapshot.DeliverableDef;
import com.dp.plat.common.spi.DeliverableBatchCreator;
import com.dp.plat.deliverable.entity.Deliverable;
import com.dp.plat.deliverable.mapper.DeliverableMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 交付件批量创建 SPI 实现（TD-P8-003 模板深拷贝用）。
 *
 * <p>从模板创建项目时，{@code pms-project} 通过 {@link DeliverableBatchCreator} 跨模块调用本实现，
 * 批量插入交付件记录到 {@code pms_deliverable} 表，避免 {@code pms-project} 直接依赖
 * {@code pms-deliverable}（防止新的依赖环）。</p>
 *
 * <p>初始化策略：
 * <ul>
 *   <li>status = DRAFT（交付件 7 态状态机起点，参见 §3.4）</li>
 *   <li>currentVersion = 1</li>
 *   <li>mandatory / approverRole / deliverableType / deliverableName / phaseId / projectId 来自模板定义</li>
 * </ul>
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeliverableBatchCreatorImpl implements DeliverableBatchCreator {

    private static final String INITIAL_STATUS = "DRAFT";
    private static final Integer INITIAL_VERSION = 1;

    private final DeliverableMapper deliverableMapper;

    @Override
    public void batchCreateDeliverables(Long projectId, Long phaseId, List<DeliverableDef> deliverableDefs) {
        if (projectId == null || phaseId == null || deliverableDefs == null || deliverableDefs.isEmpty()) {
            return;
        }
        for (DeliverableDef def : deliverableDefs) {
            Deliverable deliverable = Deliverable.builder()
                    .projectId(projectId)
                    .phaseId(phaseId)
                    .deliverableName(def.getDeliverableName())
                    .deliverableType(def.getDeliverableType())
                    .status(INITIAL_STATUS)
                    .currentVersion(INITIAL_VERSION)
                    .mandatory(def.getMandatory() != null ? def.getMandatory() : Boolean.FALSE)
                    .templateInherited(Boolean.TRUE)
                    .approverRole(def.getApproverRole())
                    .build();
            deliverableMapper.insert(deliverable);
        }
        log.info("模板深拷贝：批量创建交付件成功 projectId={} phaseId={} count={}",
                projectId, phaseId, deliverableDefs.size());
    }
}
