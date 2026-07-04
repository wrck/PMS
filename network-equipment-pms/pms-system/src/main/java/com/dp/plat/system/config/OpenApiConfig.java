package com.dp.plat.system.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 文档配置。
 */
@Configuration
public class OpenApiConfig {

    /**
     * OpenAPI 基础信息与 Bearer 鉴权方案。
     */
    @Bean
    public OpenAPI openAPI() {
        Contact contact = new Contact()
                .name("PMS Team")
                .email("pms@dp.com");
        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html");
        Info info = new Info()
                .title("网络设备工程项目管理系统 API")
                .version("1.0.0")
                .description("OpenAPI 3 文档")
                .contact(contact)
                .license(license);
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
        return new OpenAPI()
                .info(info)
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme));
    }

    /**
     * Bearer 鉴权方案定义。
     */
    @Bean
    public SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }

    @Bean
    public GroupedOpenApi systemGroup() {
        return GroupedOpenApi.builder().group("system").pathsToMatch("/api/system/**").build();
    }

    @Bean
    public GroupedOpenApi projectGroup() {
        return GroupedOpenApi.builder().group("project").pathsToMatch("/api/project/**").build();
    }

    @Bean
    public GroupedOpenApi assetGroup() {
        return GroupedOpenApi.builder().group("asset").pathsToMatch("/api/asset/**").build();
    }

    @Bean
    public GroupedOpenApi implementationGroup() {
        return GroupedOpenApi.builder().group("implementation").pathsToMatch("/api/implementation/**").build();
    }

    @Bean
    public GroupedOpenApi workflowGroup() {
        return GroupedOpenApi.builder().group("workflow").pathsToMatch("/api/workflow/**").build();
    }

    @Bean
    public GroupedOpenApi integrationGroup() {
        return GroupedOpenApi.builder().group("integration").pathsToMatch("/api/integration/**").build();
    }

    @Bean
    public GroupedOpenApi fileGroup() {
        return GroupedOpenApi.builder().group("file").pathsToMatch("/api/file/**").build();
    }

    @Bean
    public GroupedOpenApi notificationGroup() {
        return GroupedOpenApi.builder().group("notification").pathsToMatch("/api/notification/**").build();
    }

    @Bean
    public GroupedOpenApi governanceGroup() {
        return GroupedOpenApi.builder().group("governance").pathsToMatch("/api/governance/**").build();
    }
}
