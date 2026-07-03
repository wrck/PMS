package com.dp.plat.workflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * Request payload for starting a process instance by key.
 */
@Data
@Schema(description = "启动流程请求")
public class StartProcessRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "流程定义Key", requiredMode = Schema.RequiredMode.REQUIRED)
    private String processDefinitionKey;

    @Schema(description = "业务Key")
    private String businessKey;

    @Schema(description = "流程变量")
    private Map<String, Object> variables;
}
