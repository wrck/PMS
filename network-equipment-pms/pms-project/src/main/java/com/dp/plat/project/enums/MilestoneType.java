package com.dp.plat.project.enums;

/**
 * Project milestone types following the PPDIOO network lifecycle methodology.
 *
 * <p>The 12 milestone types span the five PPDIOO phases (Prepare, Plan, Design,
 * Implement, Operate) and are ordered by {@link #sortOrder} to enforce stage-gate
 * progression during project delivery.</p>
 */
public enum MilestoneType {

    /** On-site survey and requirement gathering. */
    SITE_SURVEY(PpdiooPhase.PREPARE, "现场勘察", 1),
    /** Network topology and solution design. */
    NETWORK_DESIGN(PpdiooPhase.PLAN, "网络设计", 2),
    /** Equipment procurement. */
    PROCUREMENT(PpdiooPhase.PLAN, "设备采购", 3),
    /** Staging and pre-configuration before shipment. */
    STAGING(PpdiooPhase.DESIGN, "预配置", 4),
    /** Factory Acceptance Test. */
    FAT(PpdiooPhase.DESIGN, "工厂验收测试", 5),
    /** Equipment arrival on site. */
    ARRIVAL(PpdiooPhase.IMPLEMENT, "设备到货", 6),
    /** On-site installation. */
    INSTALLATION(PpdiooPhase.IMPLEMENT, "现场安装", 7),
    /** System testing. */
    TESTING(PpdiooPhase.IMPLEMENT, "系统测试", 8),
    /** System commissioning. */
    COMMISSIONING(PpdiooPhase.IMPLEMENT, "系统调测", 9),
    /** Site Acceptance Test. */
    SAT(PpdiooPhase.IMPLEMENT, "现场验收测试", 10),
    /** User Acceptance Test. */
    UAT(PpdiooPhase.OPERATE, "用户验收测试", 11),
    /** Final acceptance. */
    FINAL_ACCEPTANCE(PpdiooPhase.OPERATE, "终验", 12);

    /** PPDIOO phase this milestone belongs to. */
    private final PpdiooPhase ppdiooPhase;
    /** Human-readable description of the milestone. */
    private final String description;
    /** Sort order enforcing stage-gate progression (1-12). */
    private final int sortOrder;

    MilestoneType(PpdiooPhase ppdiooPhase, String description, int sortOrder) {
        this.ppdiooPhase = ppdiooPhase;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    public PpdiooPhase getPpdiooPhase() {
        return ppdiooPhase;
    }

    public String getDescription() {
        return description;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * Resolve a milestone type from its string name, ignoring case.
     *
     * @param name milestone type name
     * @return resolved milestone type, or {@code null} if the name is blank or unknown
     */
    public static MilestoneType fromName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        try {
            return MilestoneType.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
