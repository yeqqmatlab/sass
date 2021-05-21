package com.zsy.sass.model.bo;

/**
 * Created by yqq on 2019/6/28.
 */
public class StudentExam {

    /**
     * 学生ID
     */
    private String studentId;

    /**
     * 学校ID
     */
    private String schoolId;

    /**
     * 班级ID
     */
    private String classId;

    /**
     * 班级名称
     */
    private String clazzName;

    /**
     * 考试ID
     */
    private String examId;

    /**
     * 得分
     */
    private Double scoring;

    /**
     *是否缺考
     */
    private Integer missExam;

    /**
     * 学科
     */
    private Integer subjectId;

    /**
     * 试卷总分
     */
    private Double totalScore;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public Double getScoring() {
        return scoring;
    }

    public void setScoring(Double scoring) {
        this.scoring = scoring;
    }

    public Integer getMissExam() {
        return missExam;
    }

    public void setMissExam(Integer missExam) {
        this.missExam = missExam;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }
}
