package com.dp.plat.common.saga;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * 通用 Saga 上下文基类：承载业务数据并在步骤间传递。
 *
 * <p>子类可扩展具体的业务字段（如结算单、订单等）。基类提供 Saga 标识、
 * 时间戳等通用元数据，便于日志追踪与持久化。</p>
 *
 * <p>注意：本类<b>非线程安全</b>，Saga 在单线程内顺序执行，无需考虑并发。</p>
 */
@Getter
@Setter
public class SagaContext {

    /** Saga 唯一标识（自动生成 UUID），用于全链路日志追踪。 */
    private final String sagaId;

    /** Saga 名称，标识业务流程类型（如 SettlementSubmit）。 */
    private final String sagaName;

    /** Saga 开始时间戳（毫秒）。 */
    private final long startedAtMillis;

    protected SagaContext(String sagaName) {
        this.sagaId = UUID.randomUUID().toString();
        this.sagaName = sagaName;
        this.startedAtMillis = System.currentTimeMillis();
    }
}
