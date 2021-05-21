package com.zsy.sass.report.dao.impl;

import com.zsy.sass.common.tools.HBaseDaoUtil;
import com.zsy.sass.model.pojo.Demo;
import com.zsy.sass.report.dao.HbaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HbaseDaoImpl implements HbaseDao {

    @Autowired
    private HBaseDaoUtil hBaseDaoUtil;

    @Override
    public List<Demo> getByRowKey(Demo demo, String rowKey) {

        return hBaseDaoUtil.get(demo, rowKey);
    }
}
