package com.zsy.sass.report.controller;

import com.zsy.sass.common.tools.ZSYResult;
import com.zsy.sass.model.pojo.Exam;
import com.zsy.sass.report.service.ExamService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;



/**
 * Created by yqq on 2019/6/27.
 */
@RestController
@RequestMapping("/exam")
public class ExamController {

    private static final Log log = LogFactory.getLog(ExamController.class);

    @Autowired
    private ExamService examService;

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public String findAll(){
        log.info("find all exams");
        return ZSYResult.success().data(examService.findAll()).build();
    }













}
