package com.dp.plat.lowcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.lowcode.entity.LowCodeComment;
import com.dp.plat.lowcode.mapper.LowCodeCommentMapper;
import com.dp.plat.lowcode.service.LowCodeCommentService;
import com.dp.plat.notification.entity.Notification;
import com.dp.plat.notification.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LowCodeCommentServiceImpl extends ServiceImpl<LowCodeCommentMapper, LowCodeComment>
        implements LowCodeCommentService {

    private final INotificationService notificationService;

    private static final Pattern MENTION_PATTERN = Pattern.compile("@\\[([^\\]]+)\\]\\((\\d+)\\)");

    @Override
    public List<LowCodeComment> listByConfig(String configType, Long configId) {
        return list(new LambdaQueryWrapper<LowCodeComment>()
                .eq(LowCodeComment::getConfigType, configType)
                .eq(LowCodeComment::getConfigId, configId)
                .orderByAsc(LowCodeComment::getCreateTime));
    }

    @Override
    public LowCodeComment addComment(LowCodeComment comment) {
        // 解析 @mention 格式: @[用户名](用户ID)
        Set<Long> mentionedUserIds = parseMentions(comment.getContent());
        if (!mentionedUserIds.isEmpty()) {
            comment.setMentions(mentionedUserIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
        save(comment);
        // 发送通知
        if (!mentionedUserIds.isEmpty()) {
            sendMentionNotifications(comment, mentionedUserIds);
        }
        return comment;
    }

    private Set<Long> parseMentions(String content) {
        if (content == null) return new HashSet<>();
        Set<Long> ids = new HashSet<>();
        Matcher matcher = MENTION_PATTERN.matcher(content);
        while (matcher.find()) {
            ids.add(Long.valueOf(matcher.group(2)));
        }
        return ids;
    }

    private void sendMentionNotifications(LowCodeComment comment, Set<Long> userIds) {
        String title = "您在配置 " + comment.getConfigType() + "#" + comment.getConfigId() + " 中被 @提及";
        String content = comment.getUserName() + " 评论: " + comment.getContent();
        for (Long userId : userIds) {
            try {
                Notification notification = new Notification();
                notification.setUserId(userId);
                notification.setTitle(title);
                notification.setContent(content);
                notification.setCategory("LOWCODE_MENTION");
                notification.setBizType(comment.getConfigType());
                notification.setBizId(comment.getConfigId());
                Set<String> channels = new HashSet<>(Arrays.asList("IN_APP", "WS"));
                notificationService.multiChannelSend(notification, channels);
            } catch (Exception e) {
                log.warn("发送 @提及通知失败: userId={}", userId, e);
            }
        }
    }
}
