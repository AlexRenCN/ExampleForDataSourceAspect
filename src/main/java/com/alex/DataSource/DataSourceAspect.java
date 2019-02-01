package com.alex.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据源切换切面
 */
@Aspect
@Component
@Slf4j
public class DataSourceAspect {

    @Pointcut("@annotation(com.alex.DataSource.DataSourceMatch)")
    public void setSource() {

    }

    @Around("setSource()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        DataSourceMatch ds = method.getAnnotation(DataSourceMatch.class);
        if (ds == null) {
            DataSourceType.setSource(DataSourceEnum.ORACLE);
            log.debug("DataSource choose {}",DataSourceEnum.ORACLE);
        } else {
            DataSourceType.setSource(ds.dbName());
            log.debug("DataSource choose {}",ds.dbName());
        }
        try {
               return point.proceed();
        } finally {
            log.debug("DataSource clear ");
            DataSourceType.clearSource();
        }
    }

}
