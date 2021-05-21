package com.zsy.sass.report.service;

import com.zsy.sass.model.pojo.Exam;
import java.util.List;

/**
 * Created by yqq on 2019/6/27.
 */
public interface ExamService {

    /**
     * find all exam
     */
    List<Exam> findAll();
}
