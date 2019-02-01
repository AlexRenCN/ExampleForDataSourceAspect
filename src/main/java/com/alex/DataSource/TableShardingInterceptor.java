package com.alex.DataSource;

import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mybatis分表插件
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Slf4j
public class TableShardingInterceptor implements Interceptor {
    /**
     * 虚拟节点数
     */
    private static final int VIRTUAL_NODE=128;
    /**
     * 实际节点数
     */
    private static final int REAL_NODE=8;
    /**
     * 范围
     */
    private static final int RANGE=VIRTUAL_NODE/REAL_NODE;

    private static final ObjectFactory OBJECT_FACTORY = new DefaultObjectFactory();

    private static final ObjectWrapperFactory OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();

    private static final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();

    /**
     * 实现拦截逻辑
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler, OBJECT_FACTORY, OBJECT_WRAPPER_FACTORY, REFLECTOR_FACTORY);
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        //根据指定参数进行一致性哈希
        String orderNo=(String)boundSql.getAdditionalParameter("orderNo");
        if(null==orderNo){
            return invocation.proceed();
        }
        String oldSql = boundSql.getSql();
        if (log.isDebugEnabled()) {
            log.debug("DATA SHARD:before convert-{}", oldSql);
        }
        String newSql = convert(oldSql,orderNo);
        if (log.isDebugEnabled()) {
            log.debug("DATA SHARD:after convert-{}", newSql);
        }
        metaObject.setValue("delegate.boundSql.sql", newSql);
        return invocation.proceed();
    }

    private String convert(String oldSql,String orderNo) {
        List<String> oldTableName=match(oldSql);
        if(oldTableName.isEmpty()){
            log.error("Mybatis plugin ERROR: can't match tableName,{}",oldSql);
            throw new RuntimeException("Mybatis plugin ERROR: can't match tableName");
        }
        int allocation=allocation(orderNo);
        String newSql=null;
        for (String oldTable:
        oldTableName) {
            newSql=oldSql.replaceAll(oldTable,oldTableName+"_"+allocation);
        }
        log.info("Shoujinwang Mybatis plugin: newSql:{}",newSql);
        return newSql;
    }


    /**
     * 生成对目标对象的代理
     *
     * @param target
     * @return
     */
    @Override
    public Object plugin(Object target) {

        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    /**
     * 设置额外参数
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {

    }
    //根据表名拦截和匹配
    private static List<String> match(String sql) {
        List<String> list=new ArrayList<>();
        //通过表名进行匹配，这里并没有写全，需要匹配可能出现的括号逻辑
        String patternString = "\\btd_order_\\S*?\\b";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            String tableName=matcher.group();
            log.debug("Mybatis plugin: match TableName:{}",tableName);
            list.add(tableName);
        }
        return list;
    }
    private static int allocation(String orderNo){
        int candidate= Hashing.consistentHash(orderNo.hashCode(),VIRTUAL_NODE);
        return candidate/RANGE;
    }

    public static void main(String[] args) {
        String insertSql = "insert into td_order_cashOrder(id,status) values('111',0)";
        String deleteSql = "delete from td_order_cashOrder  where 1=1";
        String updateSql = "update td_order_cashOrder set status=1 where 1=1";
        String selectSql = "select * from td_order_cashOrder where 1=1";
        String select2Sql = "select * from td_order_cashOrder,td_order_cashOrder where 1=1";

        String insertStlectSql = "insert into td_order_cashOrder (select * from td_order_Order where 1=1)";
        match(insertStlectSql);
    }
}
