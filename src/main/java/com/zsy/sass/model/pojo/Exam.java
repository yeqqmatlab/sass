package com.zsy.sass.model.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yqq on 2019/6/27.
 */
public class Exam implements Serializable {

    private static final long serialVersionUID = 3537921742065870581L;

    private Long exam_id;
    private String exam_name;
    private int grade;
    private int type;
    private String remark;
    private Long begin_time;
    private Long end_time;
    private String signup;
    private Long mech_id;
    private int status;
    private Long create_by;
    private Long create_time;


    public Long getExam_id() {
        return exam_id;
    }

    public void setExam_id(Long exam_id) {
        this.exam_id = exam_id;
    }

    public String getExam_name() {
        return exam_name;
    }

    public void setExam_name(String exam_name) {
        this.exam_name = exam_name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(Long begin_time) {
        this.begin_time = begin_time;
    }

    public Long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Long end_time) {
        this.end_time = end_time;
    }

    public String getSignup() {
        return signup;
    }

    public void setSignup(String signup) {
        this.signup = signup;
    }

    public Long getMech_id() {
        return mech_id;
    }

    public void setMech_id(Long mech_id) {
        this.mech_id = mech_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getCreate_by() {
        return create_by;
    }

    public void setCreate_by(Long create_by) {
        this.create_by = create_by;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }
}