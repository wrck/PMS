package com.dp.plat.asset.service;

import com.dp.plat.asset.enums.AssetStatus;
import com.dp.plat.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates legal transitions between {@link AssetStatus} values according to the
 * 9-state asset lifecycle matrix.
 *
 * <pre>
 * ORDERED        → {IN_TRANSIT, RECEIVED}
 * IN_TRANSIT     → {RECEIVED}
 * RECEIVED       → {STAGED, IN_TRANSIT, INSTALLED, RMA, DECOMMISSIONED}
 * STAGED         → {INSTALLED, RECEIVED}
 * INSTALLED      → {COMMISSIONED, RMA, DECOMMISSIONED}
 * COMMISSIONED   → {IN_PRODUCTION, RMA, DECOMMISSIONED}
 * IN_PRODUCTION  → {RMA, DECOMMISSIONED}
 * RMA            → {RECEIVED, DECOMMISSIONED}
 * DECOMMISSIONED → {} (terminal)
 * </pre>
 */
@Component
public class AssetStateTransitionValidator {

    private final Map<AssetStatus, Set<AssetStatus>> transitions = new EnumMap<>(AssetStatus.class);

    public AssetStateTransitionValidator() {
        transitions.put(AssetStatus.ORDERED, EnumSet.of(AssetStatus.IN_TRANSIT, AssetStatus.RECEIVED));
        transitions.put(AssetStatus.IN_TRANSIT, EnumSet.of(AssetStatus.RECEIVED));
        transitions.put(AssetStatus.RECEIVED, EnumSet.of(
                AssetStatus.STAGED, AssetStatus.IN_TRANSIT, AssetStatus.INSTALLED,
                AssetStatus.RMA, AssetStatus.DECOMMISSIONED));
        transitions.put(AssetStatus.STAGED, EnumSet.of(AssetStatus.INSTALLED, AssetStatus.RECEIVED));
        transitions.put(AssetStatus.INSTALLED, EnumSet.of(
                AssetStatus.COMMISSIONED, AssetStatus.RMA, AssetStatus.DECOMMISSIONED));
        transitions.put(AssetStatus.COMMISSIONED, EnumSet.of(
                AssetStatus.IN_PRODUCTION, AssetStatus.RMA, AssetStatus.DECOMMISSIONED));
        transitions.put(AssetStatus.IN_PRODUCTION, EnumSet.of(
                AssetStatus.RMA, AssetStatus.DECOMMISSIONED));
        transitions.put(AssetStatus.RMA, EnumSet.of(AssetStatus.RECEIVED, AssetStatus.DECOMMISSIONED));
        transitions.put(AssetStatus.DECOMMISSIONED, Collections.emptySet());
    }

    /**
     * Whether transitioning from {@code from} to {@code to} is allowed by the matrix.
     *
     * @param from current status (nullable; null means the asset has no prior state)
     * @param to   target status
     * @return true if the transition is legal, false otherwise
     */
    public boolean canTransition(AssetStatus from, AssetStatus to) {
        if (to == null) {
            return false;
        }
        if (from == null) {
            // Initial state assignment is always allowed.
            return true;
        }
        if (from == to) {
            // No-op transition.
            return true;
        }
        Set<AssetStatus> targets = transitions.get(from);
        return targets != null && targets.contains(to);
    }

    /**
     * Validate that transitioning from {@code from} to {@code to} is legal.
     *
     * @param from current status (nullable)
     * @param to   target status
     * @throws BusinessException if the transition is illegal
     */
    public void validate(AssetStatus from, AssetStatus to) {
        if (canTransition(from, to)) {
            return;
        }
        Set<AssetStatus> targets = (from == null) ? Collections.emptySet() : transitions.getOrDefault(from, Collections.emptySet());
        String validTargets = targets.stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        String fromName = (from == null) ? "null" : from.name();
        throw new BusinessException("非法状态迁移: " + fromName + " → " + to.name()
                + "，合法路径: " + fromName + " → [" + validTargets + "]");
    }
}
