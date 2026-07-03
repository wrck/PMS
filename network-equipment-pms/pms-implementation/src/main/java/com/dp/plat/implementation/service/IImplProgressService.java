package com.dp.plat.implementation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.implementation.entity.ImplProgress;

import java.util.List;

/**
 * Service for {@link ImplProgress}.
 */
public interface IImplProgressService extends IService<ImplProgress> {

    /**
     * List progress logs by task id.
     */
    List<ImplProgress> listByTaskId(Long taskId);

    /**
     * Create a progress log.
     */
    ImplProgress create(ImplProgress progress);
}
