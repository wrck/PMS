package com.dp.plat.framework.mybatis.core.dataobject;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体对象
 *
 * <p>直接复用自 yudao-framework（yudao-spring-boot-starter-mybatis）。
 * 移除了 Easy-Trans {@code TransPojo} 依赖以避免引入额外组件。
 *
 * <p>与历史 {@link com.dp.plat.common.entity.BaseEntity} 共存：
 * <ul>
 *   <li>新实体推荐继承 {@code BaseDO}（字段 creator/updater/deleted:Boolean）</li>
 *   <li>存量实体保留 {@code BaseEntity}（字段 createBy/updateBy/deleted:Integer）</li>
 * </ul>
 * 字段填充由 {@link com.dp.plat.framework.mybatis.core.handler.DefaultDBFieldHandler} 处理。
 *
 * @author yudao
 */
@Data
public abstract class BaseDO implements Serializable {

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * 创建者，目前使用 SysUser 的 id 编号
     *
     * <p>使用 String 类型的原因是，未来可能会存在非数值的情况，留好拓展性。
     */
    @TableField(fill = FieldFill.INSERT)
    private String creator;
    /**
     * 更新者，目前使用 SysUser 的 id 编号
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;
    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;

    /**
     * 把 creator、createTime、updateTime、updater 都清空，
     * 避免前端直接传递 creator 之类的字段，直接就被更新了
     */
    public void clean() {
        this.creator = null;
        this.createTime = null;
        this.updater = null;
        this.updateTime = null;
    }

}
