package com.alex.service.impl;

import com.alex.persistence.TestMapper;
import com.alex.service.TestService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 自动投标订单
 */
@Service
@Slf4j
public class TestServiceImpl implements TestService {

    @Autowired
    private TestMapper testMapper;

    @Override
    public String numberSender(String userId) {
        return null;
    }

    @Override
    public void test() {
        testMapper.getSelect("123");
    }
}
