package com.dp.plat.framework.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 可排序的分页参数
 *
 * <p>直接复用自 yudao-framework。
 *
 * @author yudao
 */
@Schema(description = "可排序的分页参数")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SortablePageParam extends PageParam {

    @Schema(description = "排序字段")
    private List<SortingField> sortingFields;

}
