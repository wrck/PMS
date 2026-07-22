package com.dp.plat.common.entity;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 实体基类（兼容层，继承 yudao {@link BaseDO}）。
 * <p>yudao {@code BaseDO} 不包含 {@code id} 字段，本类补充声明。
 * 同时提供旧 PMS API 的兼容方法（{@code setCreateBy}/{@code setUpdateBy}/{@code setDeleted(int)}），
 * 委托到 BaseDO 的 {@code setCreator}/{@code setUpdater}/{@code setDeleted(Boolean)}，
 * 避免业务代码大面积改动。</p>
 */
public abstract class BaseEntity extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 兼容旧 PMS API：{@code setCreateBy(String)} 委托到 BaseDO {@code setCreator(String)}。
     * yudao BaseDO 的 creator 字段为 String 类型（存储用户标识），与旧代码语义一致。
     */
    public void setCreateBy(String createBy) {
        setCreator(createBy);
    }

    /**
     * 兼容旧 PMS API：{@code setUpdateBy(String)} 委托到 BaseDO {@code setUpdater(String)}。
     */
    public void setUpdateBy(String updateBy) {
        setUpdater(updateBy);
    }

    /**
     * 兼容旧 PMS API：{@code getCreateBy()} 委托到 BaseDO {@code getCreator()}。
     */
    public String getCreateBy() {
        return getCreator();
    }

    /**
     * 兼容旧 PMS API：{@code getUpdateBy()} 委托到 BaseDO {@code getUpdater()}。
     */
    public String getUpdateBy() {
        return getUpdater();
    }

    /**
     * 兼容旧 PMS API：{@code setDeleted(int)} 委托到 BaseDO {@code setDeleted(Boolean)}。
     * 0 → false（未删除），非 0 → true（已删除）。
     */
    public void setDeleted(int deleted) {
        super.setDeleted(deleted != 0);
    }
}
