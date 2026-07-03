package com.dp.plat.workflow.config;

import org.flowable.common.engine.impl.history.HistoryLevel;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flowable workflow engine configuration.
 *
 * <p>Configures the process engine to auto-update the database schema and to
 * record the {@link HistoryLevel#FULL} history level so that the full process
 * execution trail is available for auditing and withdrawal operations.</p>
 */
@Configuration
public class WorkflowConfig {

    /**
     * Registers a {@link ProcessEngineConfigurationConfigurer} that enables
     * database schema auto-update and FULL history level on the
     * {@link SpringProcessEngineConfiguration} created by the Flowable
     * Spring Boot auto-configuration.
     *
     * @return the process engine configuration customizer
     */
    @Bean
    public ProcessEngineConfigurationConfigurer processEngineConfigurationConfigurer() {
        return configuration -> {
            configuration.setDatabaseSchemaUpdate("true");
            configuration.setHistoryLevel(HistoryLevel.FULL);
        };
    }
}
