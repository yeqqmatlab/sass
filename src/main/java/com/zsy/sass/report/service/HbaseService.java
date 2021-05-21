package com.zsy.sass.report.service;

import com.zsy.sass.model.pojo.Demo;

import java.util.List;

public interface HbaseService {

    List<Demo> getByRowKey(String rowKey);

}
