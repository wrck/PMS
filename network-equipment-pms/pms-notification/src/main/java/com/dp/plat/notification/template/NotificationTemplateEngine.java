package com.dp.plat.notification.template;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.notification.entity.NotificationTemplate;
import com.dp.plat.notification.mapper.NotificationTemplateMapper;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.Map;

/**
 * 通知模板引擎。
 *
 * <p>基于 Freemarker 渲染存储在 {@code pms_notification_template} 中的模板。
 * 每次渲染都新建 {@link Configuration} 与 {@link StringTemplateLoader}，
 * 避免多线程共享可变模板加载器带来的线程安全问题。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationTemplateEngine {

    private final NotificationTemplateMapper notificationTemplateMapper;

    /**
     * 渲染指定模板。
     *
     * @param templateCode 模板编码
     * @param variables    模板变量
     * @return 渲染后的标题与正文
     * @throws BusinessException 模板不存在或渲染失败
     */
    public RenderedTemplate render(String templateCode, Map<String, Object> variables) {
        NotificationTemplate template = notificationTemplateMapper.selectOne(
                new LambdaQueryWrapper<NotificationTemplate>()
                        .eq(NotificationTemplate::getTemplateCode, templateCode));
        if (template == null) {
            throw new BusinessException("通知模板不存在: " + templateCode);
        }
        String subject = renderString("subject_" + templateCode, template.getSubject(), variables);
        String body = renderString("body_" + templateCode, template.getBody(), variables);
        return new RenderedTemplate(subject, body);
    }

    /**
     * 使用临时 StringTemplateLoader 渲染单个字符串模板。
     */
    private String renderString(String name, String templateText, Map<String, Object> variables) {
        if (templateText == null || templateText.isEmpty()) {
            return "";
        }
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
            StringTemplateLoader loader = new StringTemplateLoader();
            loader.putTemplate(name, templateText);
            cfg.setTemplateLoader(loader);
            Template template = cfg.getTemplate(name);
            StringWriter writer = new StringWriter();
            template.process(variables == null ? Map.of() : variables, writer);
            return writer.toString();
        } catch (Exception e) {
            log.error("通知模板渲染失败 template={}", name, e);
            throw new BusinessException("通知模板渲染失败: " + e.getMessage());
        }
    }

    /**
     * 渲染结果：标题与正文。
     */
    public record RenderedTemplate(String subject, String body) {
    }
}
