package com.alex.DataSource;

/**
 * 线程持有的数据源
 */
public class DataSourceType {
    private static final ThreadLocal<DataSourceEnum> TYPE=new ThreadLocal<>();

    /**
     * 设置数据源信息
     * @param dataSourceEnum
     */
    public static void setSource(DataSourceEnum dataSourceEnum){
        if(dataSourceEnum==null){
            throw new NullPointerException("当前方法需要指定数据源！！");
        }
        TYPE.set(dataSourceEnum);
    }

    /**
     * 获得数据源信息
     * @return
     */
    public static DataSourceEnum getSource(){
        return TYPE.get()==null? DataSourceEnum.MYSQL:TYPE.get();
    }

    /**
     * 清除数据源信息
     */
    public static void clearSource(){
        TYPE.remove();
    }
}
