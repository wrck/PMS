package com.dp.plat.core.pojo;

public class UserRole {

    // 用户-角色  一对多
    private Integer id;

    // 用户ID
    private Integer userId;

    // 角色ID
    private Integer roleId;

    // 公司ID
    private Integer compId;

    /**
     * 获取用户-角色  一对多
     *
     * @return id - 用户-角色  一对多
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置用户-角色  一对多
     *
     * @param id 用户-角色  一对多
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取用户ID
     *
     * @return user_id - 用户ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取角色ID
     *
     * @return role_id - 角色ID
     */
    public Integer getRoleId() {
        return roleId;
    }

    /**
     * 设置角色ID
     *
     * @param roleId 角色ID
     */
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    /**
     * 获取公司ID
     *
     * @return comp_id - 公司ID
     */
    public Integer getCompId() {
        return compId;
    }

    /**
     * 设置公司ID
     *
     * @param compId 公司ID
     */
    public void setCompId(Integer compId) {
        this.compId = compId;
    }
}
