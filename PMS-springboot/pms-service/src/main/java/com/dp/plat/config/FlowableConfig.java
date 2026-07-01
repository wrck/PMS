package com.dp.plat.config;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flowable工作流引擎配置
 * 迁移自老系统 Activiti 配置
 */
@Configuration
public class FlowableConfig {

    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> engineConfigurer() {
        return configuration -> {
            // 设置流程引擎的字体（解决中文乱码）
            configuration.setActivityFontName("宋体");
            configuration.setLabelFontName("宋体");
            configuration.setAnnotationFontName("宋体");

            // 自动部署流程定义（从classpath/processes目录加载BPMN文件）
            configuration.setDatabaseSchemaUpdate("true");

            // 设置流程历史级别：full级别记录所有历史数据
            configuration.setHistoryLevel(org.flowable.common.engine.api.history.HistoryLevel.FULL);
        };
    }
}
