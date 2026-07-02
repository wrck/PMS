package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.MailContent;
import java.util.List;

/**
 * 邮件信息服务 - migrated from Struts
 */
public interface MailInfoService {

    IPage<MailContent> queryPage(Integer pageNum, Integer pageSize);

    MailContent getById(Long id);

    void add(MailContent entity);

    void update(MailContent entity);

    void delete(Long id);

    List<MailContent> listAll();

}