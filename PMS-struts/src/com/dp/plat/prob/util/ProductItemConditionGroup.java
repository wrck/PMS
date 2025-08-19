package com.dp.plat.prob.util;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemConditionGroup {
    private String itemCodeLike;
    private String itemModelLike;
    private String itemDescLike;
    private List<String> itemCodeIn;
    private List<String> itemModelIn;
    private List<String> itemDescLIn;
    private List<String> itemCodeNotIn;
    private List<String> itemModelNotIn;
    private List<String> itemDescLNotIn;
    private List<ProductItemConditionGroup> orGroups;

//    // 私有构造函数，只能通过 Builder 构建
//    private ProductItemConditionGroup(Builder builder) {
//        this.itemCodeLike = builder.itemCodeLike;
//        this.itemModelLike = builder.itemModelLike;
//        this.itemDescLike = builder.itemDescLike;
//        this.itemCodeIn = builder.itemCodeIn;
//        this.itemModelIn = builder.itemModelIn;
//        this.itemDescLIn = builder.itemDescLIn;
//        this.itemCodeNotIn = builder.itemCodeNotIn;
//        this.itemModelNotIn = builder.itemModelNotIn;
//        this.itemDescLNotIn = builder.itemDescLNotIn;
//        this.orGroups = builder.orGroups;
//    }

//    public static class Builder {
//        private String itemCodeLike;
//        private String itemModelLike;
//        private String itemDescLike;
//        private List<String> itemCodeIn;
//        private List<String> itemModelIn;
//        private List<String> itemDescLIn;
//        private List<String> itemCodeNotIn;
//        private List<String> itemModelNotIn;
//        private List<String> itemDescLNotIn;
//        private List<ProductItemConditionGroup> orGroups;
//
//        public Builder itemCodeLike(String itemCodeLike) {
//            this.itemCodeLike = itemCodeLike;
//            return this;
//        }
//
//        public Builder itemModelLike(String itemModelLike) {
//            this.itemModelLike = itemModelLike;
//            return this;
//        }
//
//        public Builder itemDescLike(String itemDescLike) {
//            this.itemDescLike = itemDescLike;
//            return this;
//        }
//
//        public Builder itemCodeIn(List<String> itemCodeIn) {
//            this.itemCodeIn = itemCodeIn;
//            return this;
//        }
//
//        public Builder itemModelIn(List<String> itemModelIn) {
//            this.itemModelIn = itemModelIn;
//            return this;
//        }
//
//        public Builder itemDescLIn(List<String> itemDescLIn) {
//            this.itemDescLIn = itemDescLIn;
//            return this;
//        }
//
//        public Builder itemCodeNotIn(List<String> itemCodeNotIn) {
//            this.itemCodeNotIn = itemCodeNotIn;
//            return this;
//        }
//
//        public Builder itemModelNotIn(List<String> itemModelNotIn) {
//            this.itemModelNotIn = itemModelNotIn;
//            return this;
//        }
//
//        public Builder itemDescLNotIn(List<String> itemDescLNotIn) {
//            this.itemDescLNotIn = itemDescLNotIn;
//            return this;
//        }
//
//        public Builder orGroups(List<ProductItemConditionGroup> orGroups) {
//            this.orGroups = orGroups;
//            return this;
//        }
//
//        public ProductItemConditionGroup build() {
//            return new ProductItemConditionGroup(this);
//        }
//    }
//
//    public String getItemCodeLike() {
//        return itemCodeLike;
//    }
//
//    public String getItemModelLike() {
//        return itemModelLike;
//    }
//
//    public String getItemDescLike() {
//        return itemDescLike;
//    }
//
//    public List<String> getItemCodeIn() {
//        return itemCodeIn;
//    }
//
//    public List<String> getItemModelIn() {
//        return itemModelIn;
//    }
//
//    public List<String> getItemDescLIn() {
//        return itemDescLIn;
//    }
//
//    public List<String> getItemCodeNotIn() {
//        return itemCodeNotIn;
//    }
//
//    public List<String> getItemModelNotIn() {
//        return itemModelNotIn;
//    }
//
//    public List<String> getItemDescLNotIn() {
//        return itemDescLNotIn;
//    }
//
//    public List<ProductItemConditionGroup> getOrGroups() {
//        return orGroups;
//    }
//
//    public void setItemCodeLike(String itemCodeLike) {
//        this.itemCodeLike = itemCodeLike;
//    }
//
//    public void setItemModelLike(String itemModelLike) {
//        this.itemModelLike = itemModelLike;
//    }
//
//    public void setItemDescLike(String itemDescLike) {
//        this.itemDescLike = itemDescLike;
//    }
//
//    public void setItemCodeIn(List<String> itemCodeIn) {
//        this.itemCodeIn = itemCodeIn;
//    }
//
//    public void setItemModelIn(List<String> itemModelIn) {
//        this.itemModelIn = itemModelIn;
//    }
//
//    public void setItemDescLIn(List<String> itemDescLIn) {
//        this.itemDescLIn = itemDescLIn;
//    }
//
//    public void setItemCodeNotIn(List<String> itemCodeNotIn) {
//        this.itemCodeNotIn = itemCodeNotIn;
//    }
//
//    public void setItemModelNotIn(List<String> itemModelNotIn) {
//        this.itemModelNotIn = itemModelNotIn;
//    }
//
//    public void setItemDescLNotIn(List<String> itemDescLNotIn) {
//        this.itemDescLNotIn = itemDescLNotIn;
//    }
//
//    public void setOrGroups(List<ProductItemConditionGroup> orGroups) {
//        this.orGroups = orGroups;
//    }

}