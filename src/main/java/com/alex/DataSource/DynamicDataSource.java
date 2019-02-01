package com.alex.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;

/**
 * 动态设置数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSource.class);
    @Override
    protected final DataSource determineTargetDataSource(){
        DataSource dataSource=super.determineTargetDataSource();
        log.info("切换到数据源{}",dataSource.getClass().getName());
        return dataSource;

    }
    @Override
    protected DataSourceEnum determineCurrentLookupKey() {
        log.info("设置数据源为{}",DataSourceType.getSource());
        return DataSourceType.getSource();
    }
}
