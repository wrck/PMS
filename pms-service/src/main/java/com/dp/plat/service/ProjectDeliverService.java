package com.dp.plat.service;

import com.dp.plat.model.entity.PmsProjectDeliver;

import java.util.List;

public interface ProjectDeliverService {

    List<PmsProjectDeliver> queryDeliversByProject(Long projectId);

    void addDeliver(PmsProjectDeliver deliver);

    void updateDeliver(PmsProjectDeliver deliver);

    void deleteDeliver(Long id);
}
