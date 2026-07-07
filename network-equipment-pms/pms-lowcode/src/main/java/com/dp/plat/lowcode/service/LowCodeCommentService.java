package com.dp.plat.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.lowcode.entity.LowCodeComment;

import java.util.List;

public interface LowCodeCommentService extends IService<LowCodeComment> {
    List<LowCodeComment> listByConfig(String configType, Long configId);

    LowCodeComment addComment(LowCodeComment comment);
}
