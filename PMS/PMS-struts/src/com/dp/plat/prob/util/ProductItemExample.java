package com.dp.plat.prob.util;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemExample {

    private List<ProductItemConditionGroup> itemGroups = new ArrayList<>();
    private List<ProductItemConditionGroup> itemFilters = new ArrayList<>();
    
    // 可选：系统上下文用于加载预定义 filter
    private Map<String, Object> systemConfig;
    
    // ====== 链式追加方法 ======

    /**
     * 追加一个 itemGroup 条件组（多个 itemGroup 是 OR 关系）
     */
    public ProductItemExample addItemGroup(ProductItemConditionGroup group) {
        this.itemGroups.add(group);
        return this;
    }

    /**
     * 批量追加 itemGroup 条件组
     */
    public ProductItemExample addItemGroups(List<ProductItemConditionGroup> groups) {
        this.itemGroups.addAll(groups);
        return this;
    }

    /**
     * 追加一个 itemFilter 过滤条件组（预设过滤条件）
     */
    public ProductItemExample addItemFilter(ProductItemConditionGroup filter) {
        this.itemFilters.add(filter);
        return this;
    }

    /**
     * 给指定 index 的 itemGroup 添加 orGroup（AND 关系）
     */
    public ProductItemExample addOrGroupToItemGroup(int index, ProductItemConditionGroup orGroup) {
        if (index >= 0 && index < itemGroups.size()) {
            ProductItemConditionGroup group = itemGroups.get(index);
            if (group.getOrGroups() == null) {
                group.setOrGroups(new ArrayList<>());
            }
            group.getOrGroups().add(orGroup);
        } else {
            throw new IndexOutOfBoundsException("Invalid itemGroup index: " + index);
        }
        return this;
    }

    /**
     * 给最后一个 itemGroup 添加 orGroup
     */
    public ProductItemExample addOrGroupToLastItemGroup(ProductItemConditionGroup orGroup) {
        if (!itemGroups.isEmpty()) {
            int lastIndex = itemGroups.size() - 1;
            addOrGroupToItemGroup(lastIndex, orGroup);
        }
        return this;
    }

    public ProductItemExampleBuilder toBuilder() {
        return new ProductItemExampleBuilder(this);
    }
}