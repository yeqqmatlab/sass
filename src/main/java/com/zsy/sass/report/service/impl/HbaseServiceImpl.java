package com.zsy.sass.report.service.impl;

import com.zsy.sass.model.pojo.Demo;
import com.zsy.sass.report.dao.HbaseDao;
import com.zsy.sass.report.service.HbaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HbaseServiceImpl implements HbaseService {

    @Autowired
    private HbaseDao hbaseDao;


    @Override
    public List<Demo> getByRowKey(String rowKey) {


        List<Demo> list = hbaseDao.getByRowKey(new Demo(), rowKey);

        return list;
    }
}
