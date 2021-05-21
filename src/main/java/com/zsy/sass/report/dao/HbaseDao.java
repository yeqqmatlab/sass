package com.zsy.sass.report.dao;


import com.zsy.sass.model.pojo.Demo;

import java.util.List;

public interface HbaseDao {

    List<Demo> getByRowKey(Demo demo,String rowKey);

}
