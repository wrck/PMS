package com.dp.plat.prob.util;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;


public class ProductItemExampleBuilder {
    
    public static final TypeReference<List<ProductItemConditionGroup>> CONDITIONGROUPTY_TYPE_REFERENCE = new TypeReference<List<ProductItemConditionGroup>>() {};
    
    private final ProductItemExample example;
    
    // 可选：系统上下文用于加载预定义 filter
    private Map<String, Object> systemConfig;
    
    // 默认构造器：新建一个空的 Example
    public ProductItemExampleBuilder() {
        this(new ProductItemExample());
    }
    
    // 构造器：基于已有的 Example 进行扩展
    public ProductItemExampleBuilder(ProductItemExample existingExample) {
        this.example = existingExample != null ? existingExample : new ProductItemExample();
    }

    /**
     * 从 criteriaMap 构造 Example 实例
     */
    public static ProductItemExample buildFromSearchCriteria(Map<String, Object> criteriaMap, Map<String, Object> systemConfig) {
        return appendToExistingExample(ProductItemExampleBuilder.build(null), criteriaMap, systemConfig);
    }
    
    /**
     * 静态方法：基于已有的 Example 和新搜索参数，构建出最终的 Example
     */
    public static ProductItemExample appendToExistingExample(ProductItemExample existingExample, Map<String, Object> criteriaMap, Map<String, Object> systemConfig) {
        return new ProductItemExampleBuilder(existingExample)
                .setSystemConfig(systemConfig)
                .processSearchTerms(criteriaMap)
                .processExcludeTerms(criteriaMap)
                .processPredefinedFilters(criteriaMap)
                .build();
    }

    /**
     * 处理普通搜索词（itemSearch），拆分后逐个添加
     */
    private ProductItemExampleBuilder processSearchTerms(Map<String, Object> criteriaMap) {
        if (criteriaMap == null || criteriaMap.isEmpty()) {
            return this;
        }
        List<String> itemSearchs = MapUtil.get(criteriaMap, "itemSearch", List.class, Collections.emptyList());
        for (String itemSearch : itemSearchs) {
            if (StringUtils.isNotBlank(itemSearch)) {
                String[] terms = itemSearch.split("\\s+");
                for (String term : terms) {
                    addSimpleSearchTerm(term);
                }
            }
        }
        
        return this;
    }
    
    /**
     * 处理排除项（itemSearchExclude）
     */
    private ProductItemExampleBuilder processExcludeTerms(Map<String, Object> criteriaMap) {
        if (criteriaMap == null || criteriaMap.isEmpty()) {
            return this;
        }
        try {
            List<ProductItemConditionGroup> itemSearchExcludes = MapUtil.get(criteriaMap, "itemSearchExclude", CONDITIONGROUPTY_TYPE_REFERENCE, Collections.emptyList());
            if (!itemSearchExcludes.isEmpty()) {
                for (ProductItemConditionGroup excludeGroup : itemSearchExcludes) {
                    addExcludeGroup(excludeGroup);
                }
            }
        } catch (Exception e) {
            List<String> itemSearchExcludes = MapUtil.get(criteriaMap, "itemSearchExclude", List.class, Collections.emptyList());
            if (!itemSearchExcludes.isEmpty()) {
                for (String itemSearchExclude : itemSearchExcludes) {
                    if (StringUtils.isNotBlank(itemSearchExclude)) {
                        try {
                            ProductItemConditionGroup excludeGroup = JSON.parseObject(itemSearchExclude, ProductItemConditionGroup.class);
                            addExcludeGroup(excludeGroup);
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
        return this;
    }
    
    /**
     * 添加预定义过滤条件（如 itemFilters）
     */
    private ProductItemExampleBuilder processPredefinedFilters(Map<String, Object> criteriaMap) {
        if (criteriaMap == null || criteriaMap.isEmpty()) {
            criteriaMap = Collections.emptyMap();
        }
        List<ProductItemConditionGroup> itemFilters = MapUtil.get(criteriaMap, "itemFilters", CONDITIONGROUPTY_TYPE_REFERENCE, Collections.emptyList());
        if (!itemFilters.isEmpty()) {
            for (ProductItemConditionGroup filterGroup : itemFilters) {
                example.addItemFilter(filterGroup);
            }
        }

        processPredefinedFilters();

        return this;
    }

    /**
     * 添加预定义过滤条件（如 itemFilters）
     */
    private ProductItemExampleBuilder processPredefinedFilters() {
        if (systemConfig != null && systemConfig.containsKey("itemFilters")) {
            addPredefinedFilter("itemFilters");
        }
        return this;
    }
    
    /**
     * 构造基础搜索项：每个词生成一个 orGroup（code/model/desc 模糊匹配）
     */
    public ProductItemExampleBuilder addSimpleSearchTerm(String term) {
        if (term == null || term.trim().isEmpty()) return this;

        term = StringUtils.trimToEmpty(term);
        
        boolean isStartWith = term.startsWith("^");
        boolean isEndWith = term.endsWith("$");
        boolean isIn = term.matches(".*[,，、].*");
        
        term = isStartWith ? StringUtils.substringAfter(term, "^") : term;
        term = isEndWith ? StringUtils.substringBeforeLast(term, "$") : term;
        
        StringBuilder fuzzyBuidler = new StringBuilder(term);
        fuzzyBuidler.insert(0, isStartWith ? "" : "%").append(isEndWith ? "" : "%");
        
        String fuzzy = fuzzyBuidler.toString();
        
        List<String> inList = isIn ? Arrays.asList(StringUtils.split(term, ",，、")) : Collections.emptyList();
        if (isIn) {
            fuzzy = null;
        }
        
        ProductItemConditionGroup group = ProductItemConditionGroup.builder()
                .itemCodeLike(fuzzy)
                .itemModelLike(fuzzy)
                .itemDescLike(fuzzy)
                .build();
        
        if (isIn) {
            group.setItemCodeIn(inList);
        }
        
        if (example.getItemGroups().isEmpty()) {
            example.addItemGroup(ProductItemConditionGroup.builder().build());
        }

        // 创建新的 itemGroup 并追加进去
        int lastIndex = example.getItemGroups().size() - 1;
        example.addOrGroupToItemGroup(lastIndex, group);

        return this;
    }

    /**
     * 添加一个排除项（如 itemCodeNotIn 等），自动添加到当前 itemGroup 的 orGroups 中
     */
    public ProductItemExampleBuilder addExcludeGroup(ProductItemConditionGroup exclude) {
        if (exclude == null) return this;

        if (example.getItemGroups().isEmpty()) {
            example.addItemGroup(ProductItemConditionGroup.builder().build());
        }

        int lastIndex = example.getItemGroups().size() - 1;
        example.addOrGroupToItemGroup(lastIndex, exclude);

        return this;
    }

    /**
     * 从系统配置中加载预定义 filter 并添加到 itemFilters
     */
    public ProductItemExampleBuilder addPredefinedFilter(String filterName) {
        if (systemConfig == null || !systemConfig.containsKey(filterName)) {
            return this;
        }

        List<ProductItemConditionGroup> filterList = MapUtil.get(systemConfig, filterName, CONDITIONGROUPTY_TYPE_REFERENCE, Collections.emptyList());
        if (!filterList.isEmpty()) {
            for (ProductItemConditionGroup filterGroup : filterList) {
                example.addItemFilter(filterGroup);
            }
        }
        return this;
    }

    /**
     * 设置系统配置源（用于加载预定义 filter）
     */
    public ProductItemExampleBuilder setSystemConfig(Map<String, Object> config) {
        this.systemConfig = config;
        return this;
    }

    /**
     * 获取最终构建好的 ProductItemExample 实例
     */
    public ProductItemExample build() {
        return example;
    }
    
    /**
     * 获取最终构建好的 ProductItemExample 实例
     */
    public static ProductItemExample build(ProductItemExampleBuilder builder) {
        return builder != null ? builder.build() : new ProductItemExample();
    }

    /**
     * 校验 ProductItemExample 是否有效（至少有一个 itemGroup 或 itemFilter）
     */
    public boolean validate() {
        return !example.getItemGroups().isEmpty() || !example.getItemFilters().isEmpty();
    }
    
    /**
     * 校验 ProductItemExample 是否有效（至少有一个 itemGroup 或 itemFilter）
     */
    public static boolean validate(ProductItemExample example) {
        return example != null && (!example.getItemGroups().isEmpty() || !example.getItemFilters().isEmpty());
    }

    // 辅助方法：
    // 将 Map 转换为 ProductItemExample
    public static ProductItemExample convertMapToExample(Map<String, Object> map) {
        if (map == null) return new ProductItemExampleBuilder().build();
        return JSON.toJavaObject((JSON) JSON.toJSON(map), ProductItemExample.class);
    }
    
    // 将 Map 转换为 ProductItemConditionGroup
    public static ProductItemConditionGroup convertMapToConditionGroup(Map<String, Object> map) {
        if (map == null) return ProductItemConditionGroup.builder().build();

        return JSON.toJavaObject((JSON) JSON.toJSON(map), ProductItemConditionGroup.class);
    }

    @SuppressWarnings("unchecked")
    private static List<String> getList(Object obj) {
        if (obj instanceof List<?>) {
            return ((List<?>) obj).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}