package com.dp.plat.implementation.saga;

import com.dp.plat.common.saga.SagaContext;
import com.dp.plat.implementation.entity.Settlement;
import lombok.Getter;
import lombok.Setter;

/**
 * 结算单提交 Saga 的上下文：在步骤间传递结算单及中间产物。
 *
 * <p>携带的中间数据包括：OA 待办的 businessKey（用于补偿时删除待办）、
 * FP 推送的错误信息（用于补偿时记录）、原始状态（用于校验与回退）。</p>
 */
@Getter
@Setter
public class SettlementSagaContext extends SagaContext {

    /** 结算单实体，步骤中直接修改其状态字段并持久化。 */
    private Settlement settlement;

    /** Saga 开始前结算单的原始状态，用于校验与异常回退。 */
    private String originalStatus;

    /** OA 待办的业务键（结算单号），补偿时用于调用 completeTodo 删除待办。 */
    private String oaBusinessKey;

    /** FP 推送失败时的错误信息，补偿时写入 pushResponse。 */
    private String fpPushError;

    public SettlementSagaContext(Settlement settlement) {
        super("SettlementSubmit");
        this.settlement = settlement;
        this.originalStatus = settlement == null ? null : settlement.getStatus();
    }
}
