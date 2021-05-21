package com.zsy.sass.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by yqq on 2019/6/27.
 */
@ApiModel
public class SchoolLevelCountReqDTO {

    @ApiModelProperty("考试ID")
    @NotBlank(message = "考试ID不能为空")
    private String examId;

    @ApiModelProperty("学校ID")
    @NotBlank(message = "学校ID不能为空")
    private String schoolId;

    @ApiModelProperty("0:按分数; 1:按人数;2:恢复默认")
    @NotNull(message = "类型不能为空")
    @Min(value = 0,message = "类型不合法")
    @Max(value = 2,message = "类型不合法")
    private Integer type;

    @ApiModelProperty("A档线分数")
    private BigDecimal levelA;

    @ApiModelProperty("B档线分数")
    private BigDecimal levelB;

    @ApiModelProperty("C档线分数")
    private BigDecimal levelC;

    @ApiModelProperty("A档线人数")
    private Integer numA;

    @ApiModelProperty("B档线人数")
    private Integer numB;

    @ApiModelProperty("C档线人数")
    private Integer numC;

    @ApiModelProperty("考试学科集合")
    @NotNull(message = "学科ID集合不能为空")
    @NotEmpty(message = "学科ID集合不能为空")
    private List<String> subjectIds;

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getLevelA() {
        return levelA;
    }

    public void setLevelA(BigDecimal levelA) {
        this.levelA = levelA;
    }

    public BigDecimal getLevelB() {
        return levelB;
    }

    public void setLevelB(BigDecimal levelB) {
        this.levelB = levelB;
    }

    public BigDecimal getLevelC() {
        return levelC;
    }

    public void setLevelC(BigDecimal levelC) {
        this.levelC = levelC;
    }

    public Integer getNumA() {
        return numA;
    }

    public void setNumA(Integer numA) {
        this.numA = numA;
    }

    public Integer getNumB() {
        return numB;
    }

    public void setNumB(Integer numB) {
        this.numB = numB;
    }

    public Integer getNumC() {
        return numC;
    }

    public void setNumC(Integer numC) {
        this.numC = numC;
    }

    public List<String> getSubjectIds() {
        return subjectIds;
    }

    public void setSubjectIds(List<String> subjectIds) {
        this.subjectIds = subjectIds;
    }
}
