package com.dp.plat.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.base.BaseService;
import com.dp.plat.model.dto.UserDTO;
import com.dp.plat.model.entity.SysUser;
import com.dp.plat.model.vo.UserVO;

public interface SysUserService extends BaseService<SysUser> {

    Page<UserVO> listUsers(int pageNum, int pageSize, String username, String realname, Long deptId);

    UserVO getUserById(Long id);

    void createUser(UserDTO userDTO);

    void updateUser(UserDTO userDTO);

    void deleteUser(Long id);

    void resetPassword(Long id);

    void changePassword(Long id, String oldPassword, String newPassword);
}
