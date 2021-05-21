package com.zsy.sass.report.dao;

import com.zsy.sass.model.bo.StudentExam;
import com.zsy.sass.model.pojo.Exam;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by yqq on 2019/6/27.
 */
public interface ExamReportDao {

    /**
     *find exam by id
     */
    Exam findExamById(Long id);

    /**
     * student exam info
     * @param examId
     * @param schoolId
     * @param subjectIds
     * @return
     */
    List<StudentExam> selectByExamId(String examId, String schoolId, List<String> subjectIds);


    BigDecimal getTotalScoreByExamIdAndSubjectIds(String examId, List<String> subjectIds);
}
