package com.dp.plat.asset.listener;

import com.dp.plat.asset.service.IAssetService;
import com.dp.plat.project.event.FinalAcceptanceApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener that recycles all project-bound equipment once the project's final
 * acceptance is approved.
 *
 * <p>This component lives in the asset module and depends on the project
 * module only to obtain the event type, avoiding a circular dependency
 * (pms-project does not depend on pms-asset).</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FinalAcceptanceApprovedEventListener {

    private final IAssetService assetService;

    /**
     * Recycle allocated assets when a final acceptance is approved.
     *
     * @param event the final acceptance approved event
     */
    @EventListener
    public void onFinalAcceptanceApproved(FinalAcceptanceApprovedEvent event) {
        Long projectId = event.getProjectId();
        if (projectId == null) {
            return;
        }
        int count = assetService.recycleByProject(projectId);
        log.info("项目 {} 终验后自动回收 {} 台设备", projectId, count);
    }
}
