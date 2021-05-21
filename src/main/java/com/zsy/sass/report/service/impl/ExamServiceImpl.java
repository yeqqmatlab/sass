package com.zsy.sass.report.service.impl;

import com.zsy.sass.model.pojo.Exam;
import com.zsy.sass.report.dao.ExamDao;
import com.zsy.sass.report.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Created by yqq on 2019/6/27.
 */
@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamDao examDao;

    @Override
    public List<Exam> findAll() {

        String name = null;

        List<Exam> examList = examDao.findAll();
        return examList;
    }
}
