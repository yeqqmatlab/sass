package com.zsy.sass.report.dao.impl;

//import com.zhixinhuixue.jdbc.context.ZSYDao;

import com.zsy.sass.model.bo.StudentExam;
import com.zsy.sass.model.pojo.Exam;
import com.zsy.sass.report.dao.ExamReportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yqq on 2019/6/27.
 */
@Repository
public class ExamReportDaoImpl implements ExamReportDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;




    @Override
    public Exam findExamById(Long id) {
        String sql = "select * from exam where exam_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id},new BeanPropertyRowMapper<>(Exam.class));
    }

    @Override
    public List<StudentExam> selectByExamId(String examId, String schoolId, List<String> subjectIds) {
        StringBuffer sql = new StringBuffer();

        sql.append(" SELECT ");
        sql.append("     ess.exam_id, ");
        sql.append("     ess.school_id, ");
        sql.append("     ess.class_id, ");
        sql.append("     ess.student_id, ");
        sql.append("     ess.subject_id, ");
        sql.append("     ess.is_miss_exam, ");
        sql.append("     ess.scoring, ");
        sql.append("     exam_class.class_name, ");
        sql.append("     exam_subject.total_score ");
        sql.append(" FROM ");
        sql.append("     exam_subject_student ess ");
        sql.append("     LEFT JOIN exam_subject ON ess.exam_id = exam_subject.exam_id ");
        sql.append("     AND ess.subject_id = exam_subject.subject_id ");
        sql.append("     LEFT JOIN exam_class ON ess.exam_id = exam_class.exam_id ");
        sql.append("     AND ess.school_id = exam_class.school_id ");
        sql.append("     AND ess.class_id = exam_class.class_id ");
        sql.append(" WHERE ");
        sql.append("     ess.exam_id = ? ");
        sql.append("     AND ess.school_id = ? ");
        sql.append("     AND ess.subject_id in("+subjectIds.stream().map(subjectId -> "?").collect(Collectors.joining(",")) +")");



        //return selectList(sql.toString(),StudentExamBOMap, builder.get());

        return null;
    }

    @Override
    public BigDecimal getTotalScoreByExamIdAndSubjectIds(String examId, List<String> subjectIds) {

        String sql = "SELECT sum(exam_subject.total_score) as totalScore from exam_subject where exam_subject.exam_id =? and exam_subject.subject_id in ("+subjectIds.stream().map(subjectId -> "?").collect(Collectors.joining(",")) +")";
        jdbcTemplate.queryForMap(sql,new Object[]{examId});
        return null;
    }
}
