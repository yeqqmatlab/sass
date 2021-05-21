package com.zsy.sass.report.controller;


import com.zsy.sass.common.tools.ZSYResult;
import com.zsy.sass.report.service.RedisService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private static final Log log = LogFactory.getLog(RedisController.class);

    @Autowired
    private RedisService redisServiceImpl;

    @RequestMapping(value = "/findExamInfo/{key}", method = RequestMethod.GET)
    public String findExamInfo(@PathVariable("key") String key){
        log.info("info of exams");
        return ZSYResult.success().data(redisServiceImpl.findExamInfo(key)).build();
    }

}
