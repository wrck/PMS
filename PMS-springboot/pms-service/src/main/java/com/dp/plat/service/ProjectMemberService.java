package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.PmsProjectMember;
import java.util.List;

/**
 * 项目成员服务 - migrated from Struts
 */
public interface ProjectMemberService {

    IPage<PmsProjectMember> queryPage(Integer pageNum, Integer pageSize);

    PmsProjectMember getById(Long id);

    void add(PmsProjectMember entity);

    void update(PmsProjectMember entity);

    void delete(Long id);

    List<PmsProjectMember> listAll();

}