package com.dp.plat.common.page;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String orderBy;

    private boolean asc = true;

    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> toPage() {
        return new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
    }
}
