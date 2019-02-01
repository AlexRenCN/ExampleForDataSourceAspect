package com.alex.config;



import com.alex.DataSource.DataSourceEnum;
import com.alex.DataSource.DynamicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 多数据源匹配
 */
@Configuration
public class MysqlDataSourceConfig {
    @Primary
    @Bean(name = "mysqlDataSource")
    @Qualifier("mysqlDataSource")
    @ConfigurationProperties(prefix="spring.datasource.mysql")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("dynamicDataSource")
    @Qualifier("dynamicDataSource")
    public DataSource  dynamicDataSource(@Qualifier("mysqlDataSource")DataSource mysqlDataSource, @Qualifier("oracleDataSource")DataSource oracleDataSource){
        Map<Object,Object> dataSourceMap=new HashMap<>(4);
        dataSourceMap.put(DataSourceEnum.MYSQL,mysqlDataSource);
        dataSourceMap.put(DataSourceEnum.ORACLE,oracleDataSource);
        DynamicDataSource dynamicDataSource=new DynamicDataSource();
        //配置多数据源
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        //默认使用新核心数据源
        dynamicDataSource.setDefaultTargetDataSource(mysqlDataSource);
        return dynamicDataSource;
    }

}
