# Story 5 端到端验收报告 — 交付件修订新版本 + 阶段必需交付件校验

> Phase 8 / Task 8.10 — 端到端验收场景 5（Story 5）代码审查
> 验收场景：
>   5.1 修订创建新版本，不覆盖原版本（PUBLISHED/REFERENCED → 新建 v(n+1) → DRAFT）
>   5.2 必需交付件未批准，阻止阶段完成（advancePhase 调用阶段退出校验）
> 关联设计文档：§9.1（行 2551-2552）、§3.4（行 393-428）、§5.6（行 1024-1079）、§6.8
> 校验方式：静态代码审查
> 校验日期：2026-07-17

## 0. 验收范围与依据

| 维度 | 说明 |
|---|---|
| 后端模块 | `pms-deliverable`（新建模块：7 态状态机 + 版本/签名/引用） |
| 数据库迁移 | V70 `pms_deliverable` 扩展 + `pms_deliverable_version` / `_signature` / `_reference` 三新表 |
| 涉及实体 | `Deliverable`、`DeliverableVersion`、`DeliverableSignature`、`DeliverableReference`、`DeliverableStatus`(枚举)、`MandatoryDeliverableValidationResult`、`IllegalStateTransitionException` |
| 跨模块协作 | `pms-project` 的 `ProjectPhaseServiceImpl.validateExitGate`（阶段退出校验 DELIVERABLE 分支） |
| 前端模块 | `api/deliverable.ts`、`views/deliverable/{index,lifecycle,detail/index}.vue` |
| 设计验收点 | §9.1 行 2551-2552；§3.4 行 421-427；§5.6 行 1038-1078 |

---

## 1. 验收场景 5.1 — 修订创建新版本，不覆盖原版本

### 1.1 验收步骤

```
Given  交付件 D2001（实施计划书，status=PUBLISHED，currentVersion=1）
       已有版本记录 v1（filePath=/files/impl-plan-v1.docx, status=PUBLISHED, 不可变）
When   用户在「交付件全生命周期」页点击 D2001 的「修订」，填写：
       - filePath=/files/impl-plan-v2.docx
       - changeLog="增加备件清单章节"
       调用 POST /api/deliverable/{2001}/revise?filePath=...&changeLog=...
Then   系统返回新版本记录：
       { id:12002, deliverableId:2001, versionNo:2, filePath:"/files/impl-plan-v2.docx",
         uploadedAt:"2026-07-17T15:30:00", changeLog:"增加备件清单章节", status:"DRAFT" }
       交付件 D2001 更新：currentVersion=2, status=DRAFT, filePath=/files/impl-plan-v2.docx
       旧版本 v1 记录保留不变（filePath/status 均未改动）
       前端提示「修订成功，新版本 v2，旧版本保留不变」
```

### 1.2 链路代码审查

#### (1) 后端 Controller

**文件**：`pms-deliverable/.../controller/DeliverableController.java` 行 143-152

```java
@Operation(summary = "修订：新建版本不覆盖旧版本（Story 5 验收 1）")
@PostMapping("/{id}/revise")
@PreAuthorize("hasAuthority('project:deliverable:add')")
@OperLog(title = "交付件-修订", businessType = 1)
public Result<DeliverableVersion> revise(@PathVariable Long id,
                                         @RequestParam String filePath,
                                         @RequestParam(required = false) String changeLog,
                                         @RequestParam(required = false) Long uploadedBy) {
    return Result.ok(deliverableService.revise(id, filePath, changeLog, uploadedBy));
}
```

**校验**：
- API 路径 `POST /api/deliverable/{id}/revise` 与设计 §5.6 行 1034 一致 ✅
- 权限码 `project:deliverable:add` 与设计 §5.6 一致（修订复用 add 权限）✅
- `filePath` 必填、`changeLog`/`uploadedBy` 可选，参数设计合理 ✅

#### (2) 后端 Service — revise（4 步实现）

**文件**：`pms-deliverable/.../service/impl/DeliverableServiceImpl.java` 行 162-199

```java
@Override
@Transactional(rollbackFor = Exception.class)
public DeliverableVersion revise(Long deliverableId, String filePath, String changeLog, Long uploadedBy) {
    Deliverable deliverable = loadOrThrow(deliverableId);
    DeliverableStatus current = DeliverableStatus.of(deliverable.getStatus());

    // 1. 校验当前状态允许修订（仅 PUBLISHED 或 REFERENCED 可修订）
    if (current != DeliverableStatus.PUBLISHED && current != DeliverableStatus.REFERENCED) {
        throw new BusinessException(
                "仅 PUBLISHED 或 REFERENCED 状态的交付件可修订，当前状态：" + deliverable.getStatus());
    }
    if (filePath == null || filePath.isBlank()) {
        throw new BusinessException("修订需提供新文件路径");
    }

    // 2. 版本号 +1
    int newVersionNo = (deliverable.getCurrentVersion() == null ? 0 : deliverable.getCurrentVersion()) + 1;

    // 3. 新建版本记录（versionNo = newVersionNo，旧版本记录保留不变）
    DeliverableVersion newVersion = DeliverableVersion.builder()
            .deliverableId(deliverableId)
            .versionNo(newVersionNo)
            .filePath(filePath)
            .uploadedBy(uploadedBy)
            .uploadedAt(LocalDateTime.now())
            .changeLog(changeLog)
            .status(DeliverableStatus.DRAFT.code())
            .build();
    deliverableVersionMapper.insert(newVersion);

    // 4. 更新 Deliverable.currentVersion + status=DRAFT + filePath=新文件（旧版本历史不受影响）
    deliverable.setCurrentVersion(newVersionNo);
    deliverable.setStatus(DeliverableStatus.DRAFT.code());
    deliverable.setFilePath(filePath);
    updateById(deliverable);

    log.info("交付件修订：id={} 新版本 v{}，旧版本保留不变", deliverableId, newVersionNo);
    return newVersion;
}
```

**校验**（对照设计 §3.4 行 421-425 4 步要求）：

| 设计要求 | 实现位置 | 结果 |
|---|---|---|
| 1. 校验当前状态为 PUBLISHED 或 REFERENCED | 行 167-171 | ✅ 完全符合 |
| 2. 新建 DeliverableVersion，versionNo = currentVersion + 1，status = DRAFT | 行 177、180-188 | ✅ 完全符合 |
| 3. 更新 Deliverable.currentVersion 和 status = DRAFT | 行 192-194 | ✅ 完全符合 |
| 4. 旧版本记录保留不变 | 行 189 仅 insert 新版本，未触及旧版本记录 | ✅ 完全符合 |

- `@Transactional(rollbackFor = Exception.class)` 保证新建版本 + 更新交付件单事务原子性（设计 §4.5 事务边界）✅
- 新版本 `status=DRAFT`，符合 7 态状态机「PUBLISHED → DRAFT（修订新建版本）」流转 ✅
- 旧版本记录通过「仅 insert 新记录、不 update 旧记录」保证不可变 ✅

#### (3) 状态机枚举 — PUBLISHED 允许修订回 DRAFT

**文件**：`pms-deliverable/.../enums/DeliverableStatus.java` 行 59-69

```java
public Set<DeliverableStatus> allowedNextStates() {
    return switch (this) {
        case DRAFT -> EnumSet.of(SUBMITTED);
        case SUBMITTED -> EnumSet.of(REVIEWED, DRAFT);
        case REVIEWED -> EnumSet.of(SIGNED, DRAFT);
        case SIGNED -> EnumSet.of(PUBLISHED);
        case PUBLISHED -> EnumSet.of(REFERENCED, DRAFT);  // 修订新建版本回 DRAFT
        case REFERENCED -> EnumSet.of(ARCHIVED);
        case ARCHIVED -> EnumSet.noneOf(DeliverableStatus.class);
    };
}
```

**校验**：
- `PUBLISHED → {REFERENCED, DRAFT}` 包含修订路径 ✅
- `transition` 方法行 110-113 显式拦截 `PUBLISHED → DRAFT` 直接流转，强制走 `revise` 接口（需提供新文件）✅
- `REFERENCED` 状态 `allowedNextStates` 仅含 `ARCHIVED`，但 `revise` 方法行 168 允许 REFERENCED 修订 —— 这是合理的，因为修订是新建版本而非状态流转，绕过 transition 状态机校验 ✅

#### (4) 实体 + 不可变版本记录

**文件**：
- `pms-deliverable/.../entity/Deliverable.java` 行 31-69
- `pms-deliverable/.../entity/DeliverableVersion.java` 行 25-49

```java
@TableName("pms_deliverable")
public class Deliverable extends BaseEntity {
    private Long projectId;
    private String deliverableName;
    private String filePath;          // 最新版本文件路径
    private String status;            // 7 态
    private Long phaseId;
    private Integer currentVersion;   // 当前版本号，修订时 +1
    private Boolean mandatory;        // 必需交付件（影响阶段退出）
    private String approverRole;
    private LocalDateTime publishedAt;
    private LocalDateTime archivedAt;
}

@TableName("pms_deliverable_version")
public class DeliverableVersion extends BaseEntity {
    private Long deliverableId;
    private Integer versionNo;        // 版本号 1,2,3...（同一交付件内唯一）
    private String filePath;
    private String fileChecksum;      // SHA256
    private Long uploadedBy;
    private LocalDateTime uploadedAt;
    private String changeLog;
    private String status;            // 该版本流转状态
}
```

**校验**：
- `DeliverableVersion` 注释明确「已发布版本的 file_path 不允许覆盖；修订时新建 versionNo + 1 的记录，旧版本保留不变」✅
- `currentVersion` 字段驱动版本号递增 ✅

#### (5) 前端 API 封装

**文件**：`pms-frontend/src/api/deliverable.ts` 行 244-250

```typescript
/** 修订：新建版本不覆盖旧版本（Story 5 验收 1） */
export function reviseDeliverable(id: number, body: ReviseRequest): Promise<DeliverableVersion> {
  const qs = new URLSearchParams({ filePath: body.filePath })
  if (body.changeLog) qs.set('changeLog', body.changeLog)
  if (body.uploadedBy != null) qs.set('uploadedBy', String(body.uploadedBy))
  return post<DeliverableVersion>(`/api/deliverable/${id}/revise?${qs.toString()}`)
}
```

**校验**：
- 路径与后端 Controller 一致 ✅
- `ReviseRequest` 接口（行 173-177）含 filePath/changeLog/uploadedBy ✅
- 返回类型 `DeliverableVersion` 与后端 DTO 对齐 ✅

#### (6) 前端视图 — 修订交互

**文件**：`pms-frontend/src/views/deliverable/lifecycle.vue` 行 226-251

```vue
<script setup lang="ts">
// ============ 修订（Story 5 验收 1）============
const reviseVisible = ref(false)
const reviseRow = ref<Deliverable | null>(null)
const reviseForm = ref<ReviseRequest>({ filePath: '', changeLog: '' })

function openRevise(row: Deliverable) {
  reviseRow.value = row
  reviseForm.value = { filePath: '', changeLog: '' }
  reviseVisible.value = true
}

async function handleRevise() {
  if (!reviseRow.value?.id) return
  if (!reviseForm.value.filePath) {
    ElMessage.warning('请填写新版本文件路径')
    return
  }
  try {
    const newVersion = await reviseDeliverable(reviseRow.value.id, reviseForm.value)
    ElMessage.success(`修订成功，新版本 v${newVersion.versionNo}，旧版本保留不变`)
    reviseVisible.value = false
    await loadData()
  } catch {
    /* handled by interceptor */
  }
}
</script>
```

**校验**：
- 修订弹窗收集 filePath + changeLog，调用 `reviseDeliverable` ✅
- 成功提示「修订成功，新版本 v{n}，旧版本保留不变」明确告知用户旧版本保留 ✅
- `await loadData()` 刷新列表，展示新状态 DRAFT + 新版本号 ✅

#### (7) 数据库 Schema — 版本表唯一键保证不可变

**文件**：`pms-admin/src/main/resources/db/migration/V70__deliverable_full_lifecycle.sql` 行 105-124

```sql
CREATE TABLE pms_deliverable_version (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    deliverable_id  BIGINT       NOT NULL,
    version_no      INT          NOT NULL,
    file_path       VARCHAR(500) NOT NULL,
    file_checksum   VARCHAR(128) NULL,
    uploaded_by     BIGINT       NULL,
    uploaded_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    change_log      VARCHAR(500) NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    -- ... 审计字段 ...
    version_lock    INT          NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id),
    UNIQUE KEY uk_deliverable_version (deliverable_id, version_no) COMMENT '同一交付件版本号唯一',
    KEY idx_deliverable_id (deliverable_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交付件版本';
```

**校验**：
- `uk_deliverable_version (deliverable_id, version_no)` 唯一键保证版本号唯一，防止重复 insert ✅
- 表注释「版本不可变：已发布版本的 file_path 不允许覆盖；修订时新建 version_no+1 的记录」✅
- `pms_deliverable` 扩展字段 `current_version`/`mandatory`/`phase_id`/`published_at`/`archived_at` 幂等新增（行 17-94）✅
- 索引 `idx_phase_mandatory (phase_id, mandatory)` 支撑验收 5.2 的必需交付件查询 ✅

### 1.3 场景 5.1 验收结论

| 检查项 | 结果 |
|---|---|
| 后端 revise 4 步实现（校验状态/版本+1/新建版本/更新交付件） | ✅ PASS |
| 旧版本记录保留不变（仅 insert 新记录） | ✅ PASS |
| 仅 PUBLISHED/REFERENCED 状态允许修订 | ✅ PASS |
| 新版本 status=DRAFT，符合 7 态状态机 | ✅ PASS |
| 单事务原子性（@Transactional） | ✅ PASS |
| Controller API 路径 + 权限码与设计 §5.6 一致 | ✅ PASS |
| 前端 reviseDeliverable API 封装 | ✅ PASS |
| 前端 lifecycle.vue 修订弹窗 + 成功提示「旧版本保留不变」 | ✅ PASS |
| V70 pms_deliverable_version 唯一键保证版本不可变 | ✅ PASS |

**场景 5.1 验收结论：✅ PASS**

---

## 2. 验收场景 5.2 — 必需交付件未批准，阻止阶段完成

### 2.1 验收步骤

```
Given  项目 P1001 阶段 PH5002（实施阶段，IN_PROGRESS）
       阶段退出条件 PhaseExitGate.requiredDeliverables = [
         { deliverableId:2001, deliverableName:"实施计划书", requiredStatus:"PUBLISHED" }
       ]
       交付件 D2001 当前 status=SIGNED（未达到已批准）
When   用户在「项目详情」阶段 Tab 点击「推进阶段」
       调用 POST /api/project/phase/{5002}/advance
Then   系统拦截，抛出 PhaseExitGateFailedException，携带 violations：
       [{ gateType:"DELIVERABLE", businessId:2001, businessName:"实施计划书",
          expectedStatus:"PUBLISHED", actualStatus:"SIGNED",
          message:"必需交付件未达到要求状态" }]
       阶段状态保持 IN_PROGRESS，前端展示未满足退出条件列表
```

### 2.2 链路代码审查

#### (1) 独立校验 API — validateMandatoryDeliverables

**文件**：`pms-deliverable/.../controller/DeliverableController.java` 行 205-210

```java
@Operation(summary = "阶段必需交付件校验（Story 5 验收 2，供 advancePhase 调用）")
@GetMapping("/phase/{phaseId}/validate")
public Result<MandatoryDeliverableValidationResult> validateMandatoryDeliverables(
        @PathVariable Long phaseId) {
    return Result.ok(deliverableService.validateMandatoryDeliverables(phaseId));
}
```

**文件**：`pms-deliverable/.../service/impl/DeliverableServiceImpl.java` 行 226-265

```java
@Override
public MandatoryDeliverableValidationResult validateMandatoryDeliverables(Long phaseId) {
    // 1. 查询阶段下所有 mandatory=true 的交付件
    List<Deliverable> mandatoryDeliverables = list(new LambdaQueryWrapper<Deliverable>()
            .eq(Deliverable::getPhaseId, phaseId)
            .eq(Deliverable::getMandatory, Boolean.TRUE));
    // 2. 过滤 status 未达到「已批准」（PUBLISHED/REFERENCED/ARCHIVED）的条目
    List<MandatoryDeliverableValidationResult.Item> unmet = mandatoryDeliverables.stream()
            .filter(d -> {
                DeliverableStatus status = DeliverableStatus.of(d.getStatus());
                return status == null || !status.isApproved();
            })
            .map(d -> {
                DeliverableStatus status = DeliverableStatus.of(d.getStatus());
                boolean approved = status != null && status.isApproved();
                return MandatoryDeliverableValidationResult.Item.builder()
                        .deliverableId(d.getId())
                        .deliverableName(d.getDeliverableName())
                        .mandatory(Boolean.TRUE)
                        .expectedStatus(DeliverableStatus.PUBLISHED.code())
                        .actualStatus(d.getStatus())
                        .approved(approved)
                        .build();
            })
            .collect(Collectors.toList());
    // 3. allApproved = 未满足项为空
    boolean allApproved = unmet.isEmpty();
    return MandatoryDeliverableValidationResult.builder()
            .allApproved(allApproved)
            .items(unmet)
            .build();
}
```

**校验**：
- API 路径 `GET /api/deliverable/phase/{phaseId}/validate` 与设计 §5.6 行 1078 一致 ✅
- 校验逻辑按 `isApproved()`（PUBLISHED/REFERENCED/ARCHIVED 集合）判断，**完全符合设计 §3.4 行 427「达到 PUBLISHED/REFERENCED/ARCHIVED（即已批准）」语义** ✅
- 返回 `MandatoryDeliverableValidationResult { allApproved, items[] }` 结构清晰 ✅
- `DeliverableStatus.isApproved()` (行 120-122) 定义 `PUBLISHED || REFERENCED || ARCHIVED` ✅

#### (2) advancePhase 内联校验 — validateExitGate DELIVERABLE 分支

**文件**：`pms-project/.../service/impl/ProjectPhaseServiceImpl.java` 行 137-203

```java
private List<PhaseExitGateViolation> validateExitGate(ProjectPhase phase) {
    List<PhaseExitGateViolation> violations = new ArrayList<>();
    PhaseExitGate gate = phase.getExitCriteria();
    if (gate == null) {
        return violations; // 未配置退出条件，直接通过
    }

    // 1. 必需交付件：状态须等于 requiredStatus
    if (gate.getRequiredDeliverables() != null) {
        for (PhaseExitGate.RequiredDeliverable req : gate.getRequiredDeliverables()) {
            Deliverable d = deliverableMapper.selectById(req.getDeliverableId());
            if (d == null) {
                violations.add(PhaseExitGateViolation.builder()
                        .gateType("DELIVERABLE")
                        .message("必需交付件不存在")
                        .businessId(req.getDeliverableId())
                        .businessName(req.getDeliverableName())
                        .expectedStatus(req.getRequiredStatus())
                        .actualStatus(null)
                        .build());
            } else if (req.getRequiredStatus() != null
                    && !req.getRequiredStatus().equals(d.getStatus())) {
                // ⚠️ 精确匹配 requiredStatus，未按 isApproved() 集合判断
                violations.add(PhaseExitGateViolation.builder()
                        .gateType("DELIVERABLE")
                        .message("必需交付件未达到要求状态")
                        .businessId(d.getId())
                        .businessName(d.getDeliverableName())
                        .expectedStatus(req.getRequiredStatus())
                        .actualStatus(d.getStatus())
                        .build());
            }
        }
    }
    // 2. 必需里程碑...
    // 3. 必需任务（TODO，pms-project 未依赖 pms-implementation）
    // 4. 必需审批（TODO，pms-project 未依赖 pms-workflow）
    return violations;
}
```

**校验**：
- `advancePhase` (行 92-127) 调用 `validateExitGate`，violations 非空时抛 `PhaseExitGateFailedException` 阻止推进 ✅
- DELIVERABLE 分支遍历 `gate.getRequiredDeliverables()` 显式配置列表 ✅
- ⚠️ **技术债 TD-P8-011**：行 157-158 按 `req.getRequiredStatus().equals(d.getStatus())` **精确匹配**，与设计 §3.4 行 427「达到 PUBLISHED/REFERENCED/ARCHIVED（即已批准）」语义不符。例如配置 `requiredStatus=PUBLISHED`，交付件当前为 `REFERENCED`（已是已批准状态，比 PUBLISHED 更靠后），按设计应通过，但精确匹配会判定为 violation，错误阻止阶段推进。
- ⚠️ **技术债 TD-P8-012**：`validateExitGate` **未调用** `DeliverableService.validateMandatoryDeliverables`，两套校验逻辑并行存在但语义不一致（一个按 mandatory 标志 + isApproved 集合，一个按 requiredDeliverables 显式列表 + 精确匹配）。`pms-project` 模块未依赖 `pms-deliverable`，无法直接调用 `DeliverableService`。

#### (3) PhaseExitGate DTO — requiredDeliverables 结构

**文件**：`pms-common/.../dto/PhaseExitGate.java` 行 27-33

```java
@Data
public static class RequiredDeliverable implements Serializable {
    private Long deliverableId;
    private String deliverableName;
    private String requiredStatus; // PUBLISHED/REFERENCED/ARCHIVED
}
```

**校验**：注释明确 `requiredStatus` 取值 PUBLISHED/REFERENCED/ARCHIVED（即已批准三态），印证设计语义是「达到任一已批准状态即可」，而非精确匹配某一状态 ✅（但 validateExitGate 实现未遵循此语义 → TD-P8-011）

#### (4) 前端 API 封装

**文件**：`pms-frontend/src/api/deliverable.ts` 行 283-286

```typescript
/** 阶段必需交付件校验（Story 5 验收 2） */
export function validateMandatoryDeliverables(phaseId: number): Promise<MandatoryDeliverableValidationResult> {
  return get<MandatoryDeliverableValidationResult>(`/api/deliverable/phase/${phaseId}/validate`)
}
```

**校验**：API 封装完整，路径与后端一致 ✅

#### (5) 前端视图集成

**校验**：通过 Grep 搜索 `validateMandatoryDeliverables` 在 `pms-frontend/src/views` 下的调用，**仅 `api/deliverable.ts` 定义，无任何视图组件调用**。

⚠️ **技术债 TD-P8-012（前端部分）**：设计 §9.1 行 2552 要求验收 5.2 关键页面为 `project/detail (阶段 Tab)`，即项目详情页阶段 Tab 应展示必需交付件校验结果。当前：
- `views/project/detail/index.vue` 阶段 Tab 未调用 `validateMandatoryDeliverables`
- `views/deliverable/lifecycle.vue` 也未集成阶段校验入口
- 前端无法独立触发「必需交付件校验」展示，只能依赖 `advancePhase` 失败时的 `PhaseExitGateFailedException` violations 被动展示

### 2.3 场景 5.2 验收结论

| 检查项 | 结果 |
|---|---|
| 后端独立 API `validateMandatoryDeliverables` 按 isApproved() 集合判断 | ✅ PASS |
| 后端 `DeliverableStatus.isApproved()` 定义 PUBLISHED/REFERENCED/ARCHIVED | ✅ PASS |
| 后端 `MandatoryDeliverableValidationResult` DTO 结构完整 | ✅ PASS |
| Controller API 路径与设计 §5.6 一致 | ✅ PASS |
| 前端 API 封装 `validateMandatoryDeliverables` | ✅ PASS |
| V70 `idx_phase_mandatory` 索引支撑 mandatory 查询 | ✅ PASS |
| **advancePhase 的 validateExitGate DELIVERABLE 分支按 isApproved() 集合判断** | ⚠️ **FAIL** — 精确匹配 requiredStatus，见 TD-P8-011 |
| **advancePhase 调用 validateMandatoryDeliverables 复用校验逻辑** | ⚠️ **FAIL** — 两套逻辑并行，未复用，见 TD-P8-012 |
| **前端 project/detail 阶段 Tab 展示必需交付件校验结果** | ⚠️ **FAIL** — 无视图调用，见 TD-P8-012 |

**场景 5.2 验收结论：⚠️ PARTIAL PASS**

- 独立校验 API `validateMandatoryDeliverables` 实现**完整且符合设计语义**（按 isApproved 集合判断），**PASS**
- `advancePhase` 内联的 DELIVERABLE 校验逻辑**与设计语义不符**（精确匹配 vs 集合判断），且**未复用**独立 API，**部分 FAIL**
- 前端**无视图调用** `validateMandatoryDeliverables`，设计 §9.1 要求的「project/detail (阶段 Tab)」展示缺失，**部分 FAIL**

实际效果：当 PhaseExitGate.requiredDeliverables 配置 `requiredStatus=PUBLISHED` 且交付件已流转到 REFERENCED/ARCHIVED 时，`advancePhase` 会**错误阻止**阶段推进（误判为 violation），需修复 TD-P8-011。

---

## 3. 技术债清单

### TD-P8-011：validateExitGate DELIVERABLE 分支按精确匹配，与设计「已批准集合」语义不符

| 项 | 内容 |
|---|---|
| 发现位置 | `ProjectPhaseServiceImpl.validateExitGate` 行 157-158 |
| 问题描述 | DELIVERABLE 分支按 `req.getRequiredStatus().equals(d.getStatus())` **精确匹配**单一状态，但设计 §3.4 行 427 明确要求「检查必需交付件是否达到 PUBLISHED/REFERENCED/ARCHIVED（即已批准）」，即应按 `isApproved()` 集合判断。 |
| 影响 | 当 `requiredStatus=PUBLISHED` 而交付件已流转到 `REFERENCED` 或 `ARCHIVED`（均为已批准状态，比 PUBLISHED 更靠后）时，`advancePhase` 会**错误判定为 violation 并阻止阶段推进**，与设计语义冲突。 |
| 代码佐证 | `ProjectPhaseServiceImpl.java` 行 157-158：`else if (req.getRequiredStatus() != null && !req.getRequiredStatus().equals(d.getStatus()))` |
| 根因 | `pms-project` 模块未依赖 `pms-deliverable`，无法调用 `DeliverableStatus.isApproved()` 或 `DeliverableService.validateMandatoryDeliverables`，自行实现时采用了简化精确匹配，偏离设计语义。 |
| 修复建议 | 方案 A（推荐）：`pms-project` 依赖 `pms-deliverable`，`validateExitGate` DELIVERABLE 分支改为调用 `deliverableService.validateMandatoryDeliverables(phaseId)` 复用已批准集合判断（需评估模块依赖环风险）。方案 B：在 `pms-common` 提供工具类 `DeliverableStatusUtils.isApproved(String code)`，`pms-project` 直接调用，避免模块依赖。方案 C：保持精确匹配但改为「状态序号比较」—— 定义 7 态序号，`actualStatus.ordinal() >= requiredStatus.ordinal()` 视为满足（需 PUBLISHED/REFERENCED/ARCHIVED 序号递增）。 |
| 优先级 | 高 — 影响验收 5.2 正确性，可能导致已批准交付件被误判 |
| 关联 | 设计 §3.4 行 427、§3.2 PhaseExitGate、`DeliverableStatus.isApproved()` 行 120-122 |

### TD-P8-012：validateMandatoryDeliverables 独立 API 未被 advancePhase 复用 + 前端无视图调用

| 项 | 内容 |
|---|---|
| 发现位置 | `DeliverableController.validateMandatoryDeliverables` 行 205-210 + 前端 `api/deliverable.ts` 行 284-286 |
| 问题描述 | (1) 后端：`validateMandatoryDeliverables` 独立 API 实现完整且符合设计语义，但 `advancePhase` 的 `validateExitGate` **未调用**它，而是自行实现了一套语义不一致的 DELIVERABLE 校验（TD-P8-011），导致两套并行逻辑。(2) 前端：`validateMandatoryDeliverables` API 已封装但**无任何视图组件调用**，设计 §9.1 行 2552 要求的「project/detail (阶段 Tab)」展示缺失。 |
| 影响 | (1) 后端逻辑重复维护，且两套语义不一致（mandatory 标志 + isApproved 集合 vs requiredDeliverables 列表 + 精确匹配），易产生行为偏差。(2) 前端用户无法在阶段推进前主动查看必需交付件校验结果，只能依赖 advancePhase 失败时的异常被动展示。 |
| 代码佐证 | 后端：`ProjectPhaseServiceImpl.validateExitGate` 行 145-169 未引用 `DeliverableService`；前端：Grep `validateMandatoryDeliverables` 在 `views/` 下无调用。 |
| 修复建议 | (1) 后端：见 TD-P8-011 修复方案 A/B，让 `validateExitGate` 复用 `validateMandatoryDeliverables` 或统一到 isApproved 集合判断。(2) 前端：在 `views/project/detail/index.vue` 阶段 Tab 增加「校验必需交付件」按钮，调用 `validateMandatoryDeliverables(phaseId)` 并展示 `MandatoryDeliverableValidationResult`（allApproved + items 列表），与 advancePhase 失败时的 violations 展示对齐。 |
| 优先级 | 中 — 影响验收 5.2 前端展示完整性 |
| 关联 | 设计 §9.1 行 2552、§5.6 行 1078、TD-P8-011 |

---

## 4. 端到端验收总结

| 验收场景 | 关键 API | 后端实现 | 前端实现 | DB Schema | 结论 |
|---|---|---|---|---|---|
| 5.1 修订创建新版本，不覆盖原版本 | `POST /api/deliverable/{id}/revise` | ✅ 4 步实现 + 单事务 + 旧版本不可变 | ✅ lifecycle.vue 修订弹窗 + 成功提示 | ✅ V70 版本表唯一键 | **✅ PASS** |
| 5.2 必需交付件未批准，阻止阶段完成 | `GET /api/deliverable/phase/{phaseId}/validate` + `POST /api/project/phase/{id}/advance` | ✅ 独立 API 完整；⚠️ advanceExitGate 精确匹配语义不符 + 未复用独立 API | ✅ API 封装；⚠️ 无视图调用 | ✅ V70 mandatory + idx_phase_mandatory | **⚠️ PARTIAL PASS**（TD-P8-011/012） |

### 4.1 整体结论

- **场景 5.1（交付件修订新版本）**：完整实现，revise 4 步严格遵循设计 §3.4，单事务保证原子性，旧版本记录通过「仅 insert 新记录」保证不可变，前端修订弹窗 + 成功提示「旧版本保留不变」，**验收通过**。
- **场景 5.2（必需交付件校验）**：独立 API `validateMandatoryDeliverables` 实现完整且符合设计语义（isApproved 集合判断），**部分验收通过**；但 `advancePhase` 的 `validateExitGate` DELIVERABLE 分支采用精确匹配（TD-P8-011），与设计「已批准集合」语义不符，且未复用独立 API（TD-P8-012），前端无视图调用，**部分验收不通过**，需修复 TD-P8-011（高优先级）+ TD-P8-012（中优先级）。

### 4.2 技术债统计

| 编号 | 标题 | 优先级 | 影响验收 |
|---|---|---|---|
| TD-P8-011 | validateExitGate DELIVERABLE 分支精确匹配，与设计「已批准集合」语义不符 | 高 | 5.2 部分FAIL（已批准交付件可能被误判） |
| TD-P8-012 | validateMandatoryDeliverables 独立 API 未被 advancePhase 复用 + 前端无视图调用 | 中 | 5.2 前端展示缺失 |

### 4.3 与设计文档符合度

| 设计要求 | 实现状态 |
|---|---|
| §3.4 7 态状态机（DRAFT→SUBMITTED→REVIEWED→SIGNED→PUBLISHED→REFERENCED→ARCHIVED） | ✅ 完全符合（DeliverableStatus 枚举 + allowedNextStates + transition 校验） |
| §3.4 Story 5 验收 1 修订 4 步（校验状态/版本+1/新建版本/更新交付件/旧版本保留） | ✅ 完全符合 |
| §3.4 Story 5 验收 2 必需交付件达到 PUBLISHED/REFERENCED/ARCHIVED（已批准集合） | ⚠️ 独立 API 符合；advanceExitGate 精确匹配不符（TD-P8-011） |
| §5.6 9 个 API 端点（CRUD 5 + 状态机 5 + 版本 3 + 签名 2 + 引用 2 + 校验 1） | ✅ 完全符合（实际 16 个端点，超设计） |
| §5.6 验收 1 响应样例（versionNo=2, status=DRAFT, changeLog） | ✅ 完全符合 |
| §2.2 DeliverableVersion 不可变（已发布版本 file_path 不允许覆盖） | ✅ 完全符合（仅 insert 新记录 + 唯一键） |
| §2.2 Deliverable 扩展字段（phase_id/current_version/mandatory/published_at/archived_at） | ✅ 完全符合（V70 幂等新增） |
| §9.1 验收 5.2 关键页面 project/detail (阶段 Tab) | ⚠️ 前端无视图调用 validateMandatoryDeliverables（TD-P8-012） |
| 决策点 7 交付件状态机 7 态 | ✅ 完全符合 |

---

## 5. 关键文件清单

### 后端（pms-deliverable 模块）

| 文件 | 行数 | 用途 |
|---|---|---|
| `controller/DeliverableController.java` | 211 | 16 个 API（CRUD + 7 态状态机 + 版本/签名/引用 + 阶段校验） |
| `service/DeliverableService.java` | 191 | 服务接口 |
| `service/impl/DeliverableServiceImpl.java` | 377 | 7 态状态机 + revise + validateMandatoryDeliverables 实现 |
| `entity/Deliverable.java` | 69 | 交付件实体（7 态字段 + 版本/签核时间戳） |
| `entity/DeliverableVersion.java` | 49 | 版本记录（不可变） |
| `entity/DeliverableSignature.java` | - | 签名记录 |
| `entity/DeliverableReference.java` | - | 引用关系 |
| `enums/DeliverableStatus.java` | 113 | 7 态枚举 + allowedNextStates + isApproved |
| `dto/MandatoryDeliverableValidationResult.java` | 65 | 阶段校验结果（allApproved + items） |
| `exception/IllegalStateTransitionException.java` | 53 | 非法状态流转异常 |

### 跨模块（pms-project）

| 文件 | 行数 | 用途 |
|---|---|---|
| `service/impl/ProjectPhaseServiceImpl.java` | 238 | advancePhase + validateExitGate（DELIVERABLE/MILESTONE 分支，TASK/APPROVAL TODO） |
| `pms-common/dto/PhaseExitGate.java` | 55 | 阶段退出条件结构（4 类：requiredDeliverables/Tasks/Milestones/Approvals） |

### 前端

| 文件 | 用途 |
|---|---|
| `src/api/deliverable.ts` | 交付件 API（16 个端点封装，含 reviseDeliverable + validateMandatoryDeliverables） |
| `src/views/deliverable/lifecycle.vue` | 7 态状态机管理页（含修订弹窗） |
| `src/views/deliverable/index.vue` | 交付件列表页 |
| `src/views/deliverable/detail/index.vue` | 交付件详情页（版本历史 Tab） |

### 数据库迁移

| 文件 | 用途 |
|---|---|
| `V70__deliverable_full_lifecycle.sql` | pms_deliverable 扩展 + 3 新表（version/signature/reference） |
