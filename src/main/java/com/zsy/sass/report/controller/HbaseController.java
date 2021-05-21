package com.zsy.sass.report.controller;


import com.zsy.sass.common.tools.ZSYResult;
import com.zsy.sass.model.pojo.Demo;
import com.zsy.sass.report.service.HbaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by yqq on 2019/6/27.
 */
@RestController
@RequestMapping("/hbase")
public class HbaseController {

    @Autowired
    private HbaseService hbaseService;

    @GetMapping("/get/{rowKey}")
    public String getByRowKey(@PathVariable String rowKey){
        try {
            List<Demo> list = hbaseService.getByRowKey(rowKey);
            return ZSYResult.success().data(list).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ZSYResult.success().data(null).build();
        }

    }

}
