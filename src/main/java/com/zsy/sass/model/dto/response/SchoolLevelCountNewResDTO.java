package com.zsy.sass.model.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel
public class SchoolLevelCountNewResDTO {
    @ApiModelProperty("班级名称")
    private String clazzName;

    @ApiModelProperty("有效考试人数")
    private Integer studentNum;

    @ApiModelProperty("A档线信息")
    private LevelStudentAndScore levelA;

    @ApiModelProperty("B档线信息")
    private LevelStudentAndScore levelB;

    @ApiModelProperty("C档线信息")
    private LevelStudentAndScore levelC;

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public Integer getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(Integer studentNum) {
        this.studentNum = studentNum;
    }

    public LevelStudentAndScore getLevelA() {
        return levelA;
    }

    public void setLevelA(LevelStudentAndScore levelA) {
        this.levelA = levelA;
    }

    public LevelStudentAndScore getLevelB() {
        return levelB;
    }

    public void setLevelB(LevelStudentAndScore levelB) {
        this.levelB = levelB;
    }

    public LevelStudentAndScore getLevelC() {
        return levelC;
    }

    public void setLevelC(LevelStudentAndScore levelC) {
        this.levelC = levelC;
    }

    @ApiModel
    public static class LevelStudentAndScore {
        @ApiModelProperty("人数")
        private Integer num;

        @ApiModelProperty("累计人数")
        private Integer countNum;

        @ApiModelProperty("分数线")
        private BigDecimal score;

        @ApiModelProperty("达线率")
        private BigDecimal rate;

        @ApiModelProperty("平均分")
        private BigDecimal avgScoring;

        public Integer getCountNum() {
            return countNum;
        }

        public void setCountNum(Integer countNum) {
            this.countNum = countNum;
        }

        public BigDecimal getAvgScoring() {
            return avgScoring;
        }

        public void setAvgScoring(BigDecimal avgScoring) {
            this.avgScoring = avgScoring;
        }

        public Integer getNum() {
            return num;
        }

        public void setNum(Integer num) {
            this.num = num;
        }

        public BigDecimal getScore() {
            return score;
        }

        public void setScore(BigDecimal score) {
            this.score = score;
        }

        public BigDecimal getRate() {
            return rate;
        }

        public void setRate(BigDecimal rate) {
            this.rate = rate;
        }
    }
}
