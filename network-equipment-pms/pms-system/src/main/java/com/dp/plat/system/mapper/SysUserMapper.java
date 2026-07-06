package com.dp.plat.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Mapper for {@link SysUser}.
 *
 * <p>phone / email 字段加解密通过实体上的 {@code @TableField(typeHandler=...)} +
 * {@code autoResultMap=true} 由 BaseMapper 自动处理；同时在 SysUserMapper.xml 中
 * 提供显式声明 typeHandler 的自定义查询/写入作为标准用法参考。</p>
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 按用户名查询（phone/email 自动解密）。
     *
     * @param username 用户名
     * @return 用户实体
     */
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 按 id 查询（phone/email 自动解密）。
     *
     * @param id 主键
     * @return 用户实体
     */
    SysUser selectByIdWithDecrypt(@Param("id") Long id);

    /**
     * 新增用户（phone/email 自动加密）。
     *
     * @param user 用户实体
     * @return 影响行数
     */
    int insertUser(SysUser user);

    /**
     * 更新用户（phone/email 自动加密）。
     *
     * @param user 用户实体
     * @return 影响行数
     */
    int updateUser(SysUser user);
}
