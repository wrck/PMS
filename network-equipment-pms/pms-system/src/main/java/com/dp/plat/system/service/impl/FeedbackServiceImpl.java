package com.dp.plat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.ResultCode;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.system.entity.Feedback;
import com.dp.plat.system.entity.SysUser;
import com.dp.plat.system.mapper.FeedbackMapper;
import com.dp.plat.system.service.IFeedbackService;
import com.dp.plat.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link IFeedbackService}.
 *
 * <p>{@link #save(Feedback)} 自动填充当前登录用户的 {@code userId} / {@code username}
 * 与默认状态 {@code PENDING}，调用方无需也无法伪造提交人。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback>
        implements IFeedbackService {

    /** 默认初始状态 */
    private static final String STATUS_PENDING = "PENDING";
    /** 回复后将 PENDING 推进到 PROCESSING */
    private static final String STATUS_PROCESSING = "PROCESSING";
    /** 关闭后落到 CLOSED */
    private static final String STATUS_CLOSED = "CLOSED";
    /** 已解决 */
    private static final String STATUS_RESOLVED = "RESOLVED";

    private final ISysUserService sysUserService;

    @Override
    public boolean save(Feedback feedback) {
        String username = SecurityUtils.getCurrentUsername();
        feedback.setUsername(username);
        // 通过用户名反查 userId（用于按用户筛选与统计）；查不到时置空，不阻塞提交
        SysUser user = sysUserService.getByUsername(username);
        feedback.setUserId(user != null ? user.getId() : null);
        if (!StringUtils.hasText(feedback.getStatus())) {
            feedback.setStatus(STATUS_PENDING);
        }
        return super.save(feedback);
    }

    @Override
    public List<Feedback> listByUser(String username) {
        return this.list(new LambdaQueryWrapper<Feedback>()
                .eq(Feedback::getUsername, username)
                .orderByDesc(Feedback::getCreateTime));
    }

    @Override
    public List<Feedback> listAll(String status, String category) {
        return this.list(new LambdaQueryWrapper<Feedback>()
                .eq(StringUtils.hasText(status), Feedback::getStatus, status)
                .eq(StringUtils.hasText(category), Feedback::getCategory, category)
                .orderByDesc(Feedback::getCreateTime));
    }

    @Override
    public boolean reply(Long id, String reply, String replyBy) {
        Feedback existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        Feedback update = new Feedback();
        update.setId(id);
        update.setReply(reply);
        update.setReplyBy(replyBy);
        update.setReplyAt(LocalDateTime.now());
        // 仅当当前为 PENDING 时推进到 PROCESSING；RESOLVED/CLOSED 不回退
        if (STATUS_PENDING.equals(existing.getStatus())) {
            update.setStatus(STATUS_PROCESSING);
        }
        return super.updateById(update);
    }

    @Override
    public boolean close(Long id) {
        Feedback existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        Feedback update = new Feedback();
        update.setId(id);
        update.setStatus(STATUS_CLOSED);
        return super.updateById(update);
    }

    @Override
    public Map<String, Long> countByStatus() {
        // 显式指定 Wrapper 类型，避免 list(null) 在 list(Wrapper) 与 list(IPage) 之间产生歧义
        List<Feedback> all = this.list(new LambdaQueryWrapper<>());
        Map<String, Long> result = new HashMap<>();
        // 初始化所有状态为 0，保证前端图表稳定展示
        result.put(STATUS_PENDING, 0L);
        result.put(STATUS_PROCESSING, 0L);
        result.put(STATUS_RESOLVED, 0L);
        result.put(STATUS_CLOSED, 0L);
        for (Feedback f : all) {
            String s = f.getStatus();
            if (StringUtils.hasText(s)) {
                result.merge(s, 1L, Long::sum);
            }
        }
        return result;
    }
}
