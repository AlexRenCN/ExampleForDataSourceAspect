package com.alex.persistence;


import org.apache.ibatis.annotations.Param;

public interface TestMapper {
    /**
     * 订单列表
     * @param qo
     * @return
     */
    String getSelect(@Param("orderNo") String qo);


}
