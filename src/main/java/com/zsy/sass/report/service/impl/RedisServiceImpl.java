package com.zsy.sass.report.service.impl;

import com.zsy.sass.common.tools.RedisUtil;
import com.zsy.sass.report.dao.RedisDao;
import com.zsy.sass.report.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Map;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisDao redisDaoImpl;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public String findExamInfo(String key) {

        Object sm1 = redisUtil.hget(key, "599:6582234412584330221");

        System.out.println("sm---->"+sm1);

        return sm1.toString();
    }
}
