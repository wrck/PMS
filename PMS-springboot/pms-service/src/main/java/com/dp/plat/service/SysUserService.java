package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.base.BaseService;
import com.dp.plat.model.dto.UserDTO;
import com.dp.plat.model.entity.SysUser;
import com.dp.plat.model.vo.UserVO;

public interface SysUserService extends BaseService<SysUser> {

    /** 分页查询用户 */
    IPage<UserVO> queryUserPage(Integer pageNum, Integer pageSize, String username, String realname, Long deptId);

    /** 根据ID查询用户 */
    UserVO getUserById(Long id);

    /** 创建用户 */
    void addUser(UserDTO userDTO);

    /** 更新用户 */
    void updateUser(UserDTO userDTO);

    /** 删除用户 */
    void deleteUser(Long id);

    /** 重置密码 */
    void resetPassword(Long userId);

    /** 修改密码 */
    void changePassword(Long id, String oldPassword, String newPassword);

    /**
     * 强制指定用户下线
     * 迁移自: PasswordServiceImpl.forcedOffline()
     */
    void forcedOffline(String username);
}
