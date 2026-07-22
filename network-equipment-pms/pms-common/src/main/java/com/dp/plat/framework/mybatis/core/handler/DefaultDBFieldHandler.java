package com.dp.plat.framework.mybatis.core.handler;

import com.dp.plat.common.entity.BaseEntity;
import com.dp.plat.framework.mybatis.core.dataobject.BaseDO;
import com.dp.plat.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 通用参数填充实现类
 *
 * <p>直接复用自 yudao-framework。对 {@link BaseDO} 实体自动填充
 * createTime/updateTime/creator/updater（取自当前登录用户编号）。
 *
 * <p>同时兼容历史 {@link BaseEntity}：自动填充
 * createTime/updateTime/createBy/updateBy(用户名)/deleted(0)。
 *
 * @author yudao
 */
public class DefaultDBFieldHandler implements MetaObjectHandler {

    @Override
    @SuppressWarnings("PatternVariableCanBeUsed")
    public void insertFill(MetaObject metaObject) {
        if (Objects.nonNull(metaObject)) {
            Object original = metaObject.getOriginalObject();
            if (original instanceof BaseDO baseDO) {
                LocalDateTime current = LocalDateTime.now();
                // 创建时间为空，则以当前时间为插入时间
                if (Objects.isNull(baseDO.getCreateTime())) {
                    baseDO.setCreateTime(current);
                }
                // 更新时间为空，则以当前时间为更新时间
                if (Objects.isNull(baseDO.getUpdateTime())) {
                    baseDO.setUpdateTime(current);
                }
                Long userId = SecurityFrameworkUtils.getLoginUserId();
                // 当前登录用户不为空，创建人为空，则当前登录用户为创建人
                if (Objects.nonNull(userId) && Objects.isNull(baseDO.getCreator())) {
                    baseDO.setCreator(userId.toString());
                }
                // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
                if (Objects.nonNull(userId) && Objects.isNull(baseDO.getUpdater())) {
                    baseDO.setUpdater(userId.toString());
                }
            } else if (original instanceof BaseEntity) {
                // 兼容历史 BaseEntity：填充 createBy/updateBy(用户名)/deleted(0)
                String username = currentUsername();
                LocalDateTime now = LocalDateTime.now();
                strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
                strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
                strictInsertFill(metaObject, "createBy", String.class, username);
                strictInsertFill(metaObject, "updateBy", String.class, username);
                strictInsertFill(metaObject, "deleted", Integer.class, 0);
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object original = metaObject != null ? metaObject.getOriginalObject() : null;
        if (original instanceof BaseDO) {
            // 更新时间：yudao 模式下直接更新
            Object modifyTime = getFieldValByName("updateTime", metaObject);
            if (Objects.isNull(modifyTime)) {
                setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
            }
            Long userId = SecurityFrameworkUtils.getLoginUserId();
            Object modifier = getFieldValByName("updater", metaObject);
            if (Objects.nonNull(userId) && Objects.isNull(modifier)) {
                setFieldValByName("updater", userId.toString(), metaObject);
            }
        } else {
            // 兼容历史 BaseEntity：填充 updateBy(用户名)/updateTime
            String username = currentUsername();
            strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            strictUpdateFill(metaObject, "updateBy", String.class, username);
        }
    }

    /**
     * 获取当前登录用户名（兼容 yudao LoginUser 与历史 UserDetails）。
     */
    private String currentUsername() {
        return com.dp.plat.common.util.SecurityUtils.getCurrentUsername();
    }
}
