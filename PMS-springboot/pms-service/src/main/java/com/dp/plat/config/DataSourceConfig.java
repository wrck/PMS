package com.dp.plat.config;

import com.alibaba.druid.spring.boot3.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 多数据源配置 - 迁移自老系统
 * 数据源: local(主), sms, ehr, d365, crm, itr, oa
 */
@Configuration
public class DataSourceConfig {

    @Primary
    @Bean(name = "localDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.local")
    public DataSource localDataSource() { return DruidDataSourceBuilder.create().build(); }

    @Bean(name = "smsDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.sms")
    public DataSource smsDataSource() { return DruidDataSourceBuilder.create().build(); }

    @Bean(name = "ehrDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.ehr")
    public DataSource ehrDataSource() { return DruidDataSourceBuilder.create().build(); }

    @Bean(name = "d365DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.d365")
    public DataSource d365DataSource() { return DruidDataSourceBuilder.create().build(); }

    @Bean(name = "crmDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.crm")
    public DataSource crmDataSource() { return DruidDataSourceBuilder.create().build(); }

    @Bean(name = "itrDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.itr")
    public DataSource itrDataSource() { return DruidDataSourceBuilder.create().build(); }

    @Bean(name = "oaDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.oa")
    public DataSource oaDataSource() { return DruidDataSourceBuilder.create().build(); }

    @Bean(name = "dynamicDataSource")
    public DataSource dynamicDataSource(
            DataSource localDataSource, DataSource smsDataSource,
            DataSource ehrDataSource, DataSource d365DataSource,
            DataSource crmDataSource, DataSource itrDataSource,
            DataSource oaDataSource) {
        DynamicDataSource ds = new DynamicDataSource();
        Map<Object, Object> map = new HashMap<>();
        map.put("local", localDataSource);
        map.put("sms", smsDataSource);
        map.put("ehr", ehrDataSource);
        map.put("d365", d365DataSource);
        map.put("crm", crmDataSource);
        map.put("itr", itrDataSource);
        map.put("oa", oaDataSource);
        ds.setTargetDataSources(map);
        ds.setDefaultTargetDataSource(localDataSource);
        return ds;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dynamicDataSource) {
        return new DataSourceTransactionManager(dynamicDataSource);
    }
}
