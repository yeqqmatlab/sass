package com.zsy.sass.report.dao.impl;

import com.zsy.sass.model.pojo.Exam;
import com.zsy.sass.report.dao.ExamDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Created by yqq on 2019/6/27.
 */
@Repository
public class ExamDaoImpl implements ExamDao {



    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Exam findByID(Long exam_id) {
        return null;
    }

    @Override
    public List<Exam> findAll() {


        String sql = "select * from exam";
//        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Exam.class));
        return null;
    }
}
