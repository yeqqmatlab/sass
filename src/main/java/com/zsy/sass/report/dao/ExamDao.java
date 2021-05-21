package com.zsy.sass.report.dao;

import com.zsy.sass.model.pojo.Exam;
import java.util.List;

/**
 * Created by yqq on 2019/6/27.
 */
public interface ExamDao {

    /**
     *find exam by id
     */
    Exam findByID(Long exam_id);

    /**
     * find all exam
     */
    List<Exam> findAll();
}
