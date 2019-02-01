package com.alex.DataSource;

import java.lang.annotation.*;

/**
 * 配置数据源的注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSourceMatch {
    /**
     * 选择数据库
     * @return
     */
    DataSourceEnum dbName() default DataSourceEnum.MYSQL;

}
