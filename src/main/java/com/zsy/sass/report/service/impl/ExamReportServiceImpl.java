package com.zsy.sass.report.service.impl;

import com.google.common.collect.Lists;
import com.zsy.sass.common.enums.ZSYLevelCountType;
import com.zsy.sass.common.exception.ZSYServiceException;
import com.zsy.sass.model.bo.StudentExam;
import com.zsy.sass.model.dto.request.SchoolLevelCountReqDTO;
import com.zsy.sass.model.dto.response.SchoolLevelCountNewResDTO;
import com.zsy.sass.model.pojo.Exam;
import com.zsy.sass.report.dao.ExamReportDao;
import com.zsy.sass.report.service.ExamReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yqq on 2019/6/27.
 */
@Service
public class ExamReportServiceImpl implements ExamReportService {

    @Autowired
    private ExamReportDao examReportDao;

    //保留四位小数
    protected static final int SCALE_4 = 4;
    //保留两位小数
    protected static final int SCALE_2 = 2;

    //百分比换算率
    protected static final BigDecimal DECIMAL_TO_PERCENT = BigDecimal.valueOf(100);

    @Override
    public Exam findExamById(Long id) {
        return examReportDao.findExamById(id);
    }

    @Override
    public SchoolLevelCountNewResDTO levelCount(SchoolLevelCountReqDTO reqDTO) {
        //check args
        check(reqDTO);
        SchoolLevelCountNewResDTO resDTO = new SchoolLevelCountNewResDTO();
        SchoolLevelCountNewResDTO.LevelStudentAndScore levelA = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
        SchoolLevelCountNewResDTO.LevelStudentAndScore levelB = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
        SchoolLevelCountNewResDTO.LevelStudentAndScore levelC = new SchoolLevelCountNewResDTO.LevelStudentAndScore();

        String examId = reqDTO.getExamId();
        List<String> subjectIds = reqDTO.getSubjectIds();
        String schoolId = reqDTO.getSchoolId();

        List<StudentExam> studentExamList = examReportDao.selectByExamId(examId,schoolId,subjectIds);
        if(CollectionUtils.isEmpty(studentExamList)){
            throw new ZSYServiceException("查询不到考试成绩");
        }
        //查所选考试学科总分
        BigDecimal totalScore = examReportDao.getTotalScoreByExamIdAndSubjectIds(examId, subjectIds);
        //按所选学科合并
        List<StudentExam> sepList = separete(studentExamList);
        //设置所选学科总分
        for (StudentExam studentExam : sepList) {
            studentExam.setTotalScore(totalScore.doubleValue());
        }

        //去掉缺考学生
        List<StudentExam> examList = sepList.stream().filter(vo -> vo.getMissExam() == 0).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(examList)){
            throw new ZSYServiceException("查询不到考试成绩");
        }
        //查询方式一：按照分数分档
        if (reqDTO.getType() == ZSYLevelCountType.SCORE_COUNT.getValue()) {
            //找出所有分数 >= A档 的学生
            List<StudentExam> listA = examList.stream().filter(studentExam -> BigDecimal.valueOf(studentExam.getScoring()).compareTo(reqDTO.getLevelA()) >= 0)
                    .collect(Collectors.toList());
            levelA.setNum(listA.size());
            BigDecimal avglistA = BigDecimal.valueOf(listA.stream().mapToDouble(StudentExam::getScoring).average().orElse(0D))
                    .setScale(2, BigDecimal.ROUND_DOWN);
            levelA.setAvgScoring(avglistA);
            levelA.setScore(reqDTO.getLevelA());

            //找出所有分数 >= B档&&分数小于A档的学生 例如 A档 200分 B档 150 分 C档 100 分  B档就是200>score>=150
            List<StudentExam> listB = examList.stream().filter(studentExam -> BigDecimal.valueOf(studentExam.getScoring()).compareTo(reqDTO.getLevelB()) >= 0)
                    .filter(studentExam -> BigDecimal.valueOf(studentExam.getScoring()).compareTo(reqDTO.getLevelA()) < 0)
                    .collect(Collectors.toList());
            Long countB = examList.stream().filter(studentExam -> BigDecimal.valueOf(studentExam.getScoring()).compareTo(reqDTO.getLevelB()) >= 0).count();
            levelB.setNum(listB.size());
            levelB.setCountNum(countB.intValue());
            BigDecimal avgB = BigDecimal.valueOf(listB.stream().mapToDouble(StudentExam::getScoring).average().orElse(0D)).setScale(2, BigDecimal.ROUND_DOWN);
            levelB.setAvgScoring(avgB);
            levelB.setScore(reqDTO.getLevelB());

            //找出所有分数 >= C档 的学生
            List<StudentExam> listC = examList.stream()
                    .filter(studentExam -> BigDecimal.valueOf(studentExam.getScoring()).compareTo(reqDTO.getLevelC()) >= 0)
                    .filter(studentExam -> BigDecimal.valueOf(studentExam.getScoring()).compareTo(reqDTO.getLevelB()) < 0)
                    .collect(Collectors.toList());

            Long countC = examList.stream()
                    .filter(exam -> BigDecimal.valueOf(exam.getScoring()).compareTo(reqDTO.getLevelC()) >= 0).count();
            levelC.setNum(listC.size());
            levelC.setCountNum(countC.intValue());
            BigDecimal avgC = BigDecimal.valueOf(listC.stream().mapToDouble(StudentExam::getScoring).average().orElse(0D)).setScale(2, BigDecimal.ROUND_DOWN);
            levelC.setAvgScoring(avgC);
            levelC.setScore(reqDTO.getLevelC());

        }

        //查询方式二：按照人数分档
        //需求：A档线 设置 100人，就是年级前100名。 B档线设置150人，那就是去掉前100名。在往后取150名(简单来说，就是后面的不包含前面的)
        //全年级 成绩 倒序 200  157  150  135 126 110
        List<StudentExam> sortList = examList.stream().sorted(Comparator.comparing(StudentExam::getScoring).reversed()).collect(Collectors.toList());
        if (reqDTO.getType() == ZSYLevelCountType.NUM_COUNT.getValue()) {
            if (reqDTO.getNumA() + reqDTO.getNumB() + reqDTO.getNumC() > sortList.size()) {
                throw new ZSYServiceException("人数填写错误，本次考试一共：" + sortList.size() + "人");
            }
            //找出全年级 前xx名  A档人数
            List<StudentExam> listA = sortList.stream().skip(0).limit(reqDTO.getNumA()).collect(Collectors.toList());
            levelA.setScore(BigDecimal.valueOf(listA.get(listA.size() - 1).getScoring()));//A档最后一名的成绩
            levelA.setNum(listA.size());
            BigDecimal avgA = BigDecimal.valueOf(listA.stream().mapToDouble(StudentExam::getScoring).average().orElse(0D)).setScale(2, BigDecimal.ROUND_DOWN);
            levelA.setAvgScoring(avgA);

            //找出全年级 前xx名  B档人数
            List<StudentExam> listB = sortList.stream().skip(reqDTO.getNumA()).limit(reqDTO.getNumB()).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(listB)) {
                levelB.setScore(BigDecimal.valueOf(listB.get(listB.size() - 1).getScoring()));//B档最后一名的成绩
                levelB.setNum(listB.size());
                BigDecimal avgB = BigDecimal.valueOf(listB.stream().mapToDouble(StudentExam::getScoring).average().orElse(0D)).setScale(2, BigDecimal.ROUND_DOWN);
                levelB.setAvgScoring(avgB);
                levelB.setCountNum(listB.size() + listA.size());
            }

            //找出全年级 前xx名  C档人数
            List<StudentExam> listC = sortList.stream().skip(reqDTO.getNumA() + reqDTO.getNumB()).limit(reqDTO.getNumC()).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(listC)) {
                levelC.setScore(BigDecimal.valueOf(listC.get(listC.size() - 1).getScoring()));//C档最后一名的成绩
                levelC.setNum(listC.size());
                BigDecimal avgC = BigDecimal.valueOf(listC.stream().mapToDouble(StudentExam::getScoring).average().orElse(0D)).setScale(2, BigDecimal.ROUND_DOWN);
                levelC.setAvgScoring(avgC);
                levelC.setCountNum(listB.size() + listA.size() + listC.size());
            }

        }

        //默认分档
        if (reqDTO.getType() == ZSYLevelCountType.DEFAYLT_COUNT.getValue()) {
            return defaultCount(examList);
        }

        resDTO.setLevelA(levelA);
        resDTO.setLevelB(levelB);
        resDTO.setLevelC(levelC);
        return resDTO;
    }

    /**
     *
     * @param reqDTO
     * @return
     */
    @Override
    public List<SchoolLevelCountNewResDTO> levelCountList(SchoolLevelCountReqDTO reqDTO) {
        //check args
        check(reqDTO);

        String examId = reqDTO.getExamId();
        List<String> subjectIds = reqDTO.getSubjectIds();
        String schoolId = reqDTO.getSchoolId();

        List<StudentExam> studentExamList = examReportDao.selectByExamId(examId,schoolId,subjectIds);
        if(CollectionUtils.isEmpty(studentExamList)){
            throw new ZSYServiceException("查询不到考试成绩");
        }
        //查所选考试学科总分
        BigDecimal totalScore = examReportDao.getTotalScoreByExamIdAndSubjectIds(examId, subjectIds);
        //按所选学科合并
        List<StudentExam> sepList = separete(studentExamList);
        //设置所选学科总分
        for (StudentExam studentExam : sepList) {
            studentExam.setTotalScore(totalScore.doubleValue());
        }

        //filter this students of missing exam
        List<StudentExam> examList = sepList.stream().filter(vo -> vo.getMissExam() == 0).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(examList)){
            throw new ZSYServiceException("查询不到考试成绩");
        }

        //response resalut data
        List<SchoolLevelCountNewResDTO> resDTOList = Lists.newArrayList();

        //查询方式一：按照分数分档
        if (reqDTO.getType() == ZSYLevelCountType.SCORE_COUNT.getValue()) {

            SchoolLevelCountNewResDTO schoolLevelCountNewResDTO = new SchoolLevelCountNewResDTO();
            SchoolLevelCountNewResDTO.LevelStudentAndScore levelA = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
            SchoolLevelCountNewResDTO.LevelStudentAndScore levelB = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
            SchoolLevelCountNewResDTO.LevelStudentAndScore levelC = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
            schoolLevelCountNewResDTO.setClazzName("全年级");
            schoolLevelCountNewResDTO.setStudentNum(examList.size());


            List<StudentExam> sortList = examList.stream().sorted(Comparator.comparing(StudentExam::getScoring).reversed()).collect(Collectors.toList());

            //满足找出分数 >= A 档线的学生
            List<StudentExam> listA = sortList.stream().filter(studentExam -> BigDecimal.valueOf(studentExam.getScoring()).compareTo(reqDTO.getLevelA()) >= 0).collect(Collectors.toList());
            levelA.setNum(listA.size());
            levelA.setRate(getRatioPercent(listA.size(), sortList.size()));//达线率=达线人数/考试总人数
            schoolLevelCountNewResDTO.setLevelA(levelA);

            // B档线的学生
            List<StudentExam> listB = sortList.stream()
                    .filter(studentExam -> BigDecimal.valueOf(studentExam.getScoring()).compareTo(reqDTO.getLevelB()) >= 0)
                    .filter(studentExam -> BigDecimal.valueOf(studentExam.getScoring()).compareTo(reqDTO.getLevelA()) < 0)
                    .collect(Collectors.toList());

            levelB.setNum(listB.size() + listA.size());
            levelB.setRate(getRatioPercent(listB.size() + listA.size(), sortList.size()));//达线率=达线人数/考试总人数
            schoolLevelCountNewResDTO.setLevelB(levelB);
            // C档线的学生
            List<StudentExam> listC = sortList.stream()
                    .filter(studentExam -> BigDecimal.valueOf(studentExam.getScoring()).compareTo(reqDTO.getLevelC()) >= 0)
                    .filter(studentExam -> BigDecimal.valueOf(studentExam.getScoring()).compareTo(reqDTO.getLevelB()) < 0)
                    .collect(Collectors.toList());

            levelC.setNum(listC.size() + listB.size() + listA.size());
            levelC.setRate(getRatioPercent(listC.size() + listB.size() + listA.size(), sortList.size()));//达线率=达线人数/考试总人数
            schoolLevelCountNewResDTO.setLevelC(levelC);
            //添加列表第一行数据
            resDTOList.add(schoolLevelCountNewResDTO);
            //按班级统计
            Map<String, List<StudentExam>> map = examList.stream().collect(Collectors.groupingBy(StudentExam::getClassId, LinkedHashMap::new, Collectors.toList()));

            map.forEach((key, value) -> {
                SchoolLevelCountNewResDTO clazzInfo = new SchoolLevelCountNewResDTO();
                SchoolLevelCountNewResDTO.LevelStudentAndScore clazzLevelA = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
                SchoolLevelCountNewResDTO.LevelStudentAndScore clazzLevelB = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
                SchoolLevelCountNewResDTO.LevelStudentAndScore clazzLevelC = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
                clazzInfo.setClazzName(value.get(0).getClazzName());
                clazzInfo.setStudentNum(value.size());
                List<StudentExam> clazzListA = value.stream().filter(v -> BigDecimal.valueOf(v.getScoring()).compareTo(reqDTO.getLevelA()) >= 0).collect(Collectors.toList());
                clazzLevelA.setNum(clazzListA.size());
                clazzLevelA.setRate(getRatioPercent(clazzListA.size(), value.size()));

                List<StudentExam> clazzListB = value.stream()
                        .filter(v -> BigDecimal.valueOf(v.getScoring()).compareTo(reqDTO.getLevelB()) >= 0)
                        .filter(v -> BigDecimal.valueOf(v.getScoring()).compareTo(reqDTO.getLevelA()) < 0)
                        .collect(Collectors.toList());
                clazzLevelB.setNum(clazzListB.size() + clazzListA.size());
                clazzLevelB.setRate(getRatioPercent(clazzListB.size() + clazzListA.size(), value.size()));

                List<StudentExam> clazzListC = value.stream()
                        .filter(v -> BigDecimal.valueOf(v.getScoring()).compareTo(reqDTO.getLevelC()) >= 0)
                        .filter(v -> BigDecimal.valueOf(v.getScoring()).compareTo(reqDTO.getLevelB()) < 0)
                        .collect(Collectors.toList());
                clazzLevelC.setNum(clazzListC.size() + clazzListB.size() + clazzListA.size());
                clazzLevelC.setRate(getRatioPercent(clazzListC.size() + clazzListB.size() + clazzListA.size(), value.size()));

                clazzInfo.setLevelA(clazzLevelA);
                clazzInfo.setLevelB(clazzLevelB);
                clazzInfo.setLevelC(clazzLevelC);
                resDTOList.add(clazzInfo);
            });

        }

        //查询方式二：按照人数分档
        if (reqDTO.getType() == ZSYLevelCountType.NUM_COUNT.getValue()) {

            if (reqDTO.getNumA()+reqDTO.getNumB()+reqDTO.getNumC() > examList.size()) {
                throw new ZSYServiceException("人数填写错误，本次考试一共"+examList.size()+"人");
            }

            SchoolLevelCountNewResDTO schoolLevelCountNewResDTO = new SchoolLevelCountNewResDTO();
            SchoolLevelCountNewResDTO.LevelStudentAndScore levelA = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
            SchoolLevelCountNewResDTO.LevelStudentAndScore levelB = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
            SchoolLevelCountNewResDTO.LevelStudentAndScore levelC = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
            schoolLevelCountNewResDTO.setClazzName("全年级");
            schoolLevelCountNewResDTO.setStudentNum(examList.size());
            List<StudentExam> sortList = examList.stream().sorted(Comparator.comparing(StudentExam::getScoring).reversed()).collect(Collectors.toList());
            //累计达线
            List<StudentExam> countListABC = Lists.newArrayList();
            List<StudentExam> countListAB = Lists.newArrayList();
            //A档累计达线
            List<StudentExam> listA = sortList.stream().skip(0).limit(reqDTO.getNumA()).collect(Collectors.toList());
            countListABC.addAll(listA);
            countListAB.addAll(listA);
            levelA.setNum(listA.size());
            levelA.setRate(getRatioPercent(listA.size(), sortList.size()));//达线率=达线人数/考试总人数
            //levelA.setScore(BigDecimal.valueOf(listA.get(listA.size() - 1).getScoring()));
            schoolLevelCountNewResDTO.setLevelA(levelA);

            //B档累计达线
            List<StudentExam> listB = sortList.stream().skip(reqDTO.getNumA()).limit(reqDTO.getNumB()).collect(Collectors.toList());
            countListABC.addAll(listB);
            countListAB.addAll(listB);
            levelB.setNum(listA.size()+listB.size());
            levelB.setRate(getRatioPercent(listA.size()+listB.size(), sortList.size()));//达线率=达线人数/考试总人数
            schoolLevelCountNewResDTO.setLevelB(levelB);

            //C档累计达线
            List<StudentExam> listC = sortList.stream().skip(reqDTO.getNumA()+reqDTO.getNumB()).limit(reqDTO.getNumC()).collect(Collectors.toList());
            countListABC.addAll(listC);
            levelC.setNum(listA.size()+listB.size()+listC.size());
            levelC.setRate(getRatioPercent(listA.size()+listB.size()+listC.size(), sortList.size()));//达线率=达线人数/考试总人数
            schoolLevelCountNewResDTO.setLevelB(levelC);

            //添加列表第一行数据
            resDTOList.add(schoolLevelCountNewResDTO);

            //按班级统计
            //达到分档线的学生
            Map<String, List<StudentExam>> map = countListABC.stream().collect(Collectors.groupingBy(StudentExam::getClassId, LinkedHashMap::new, Collectors.toList()));
            //所有学生
            Map<String, List<StudentExam>> mapAll = examList.stream().collect(Collectors.groupingBy(StudentExam::getClassId, LinkedHashMap::new, Collectors.toList()));

            map.forEach((key, value) -> {
                SchoolLevelCountNewResDTO clazzInfo = new SchoolLevelCountNewResDTO();
                clazzInfo.setClazzName(value.get(0).getClazzName());
                clazzInfo.setStudentNum(mapAll.get(key).size());

                SchoolLevelCountNewResDTO.LevelStudentAndScore clazzLevelA = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
                SchoolLevelCountNewResDTO.LevelStudentAndScore clazzLevelB = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
                SchoolLevelCountNewResDTO.LevelStudentAndScore clazzLevelC = new SchoolLevelCountNewResDTO.LevelStudentAndScore();

                clazzLevelA.setNum(listA.stream().filter(c -> c.getClassId().equals(key)).collect(Collectors.toList()).size());
                clazzLevelA.setRate(getRatioPercent(clazzLevelA.getNum(), clazzInfo.getStudentNum()));
                clazzLevelB.setNum(countListAB.stream().filter(c -> c.getClassId().equals(key)).collect(Collectors.toList()).size());
                clazzLevelB.setRate(getRatioPercent(clazzLevelB.getNum(), clazzInfo.getStudentNum()));
                clazzLevelC.setNum(countListABC.stream().filter(c -> c.getClassId().equals(key)).collect(Collectors.toList()).size());
                clazzLevelC.setRate(getRatioPercent(clazzLevelC.getNum(), clazzInfo.getStudentNum()));
                clazzInfo.setLevelA(clazzLevelA);
                clazzInfo.setLevelB(clazzLevelB);
                clazzInfo.setLevelC(clazzLevelC);
                resDTOList.add(clazzInfo);
                mapAll.remove(key);
            });

            mapAll.forEach((k, v) -> {
                SchoolLevelCountNewResDTO clazzInfo = new SchoolLevelCountNewResDTO();
                clazzInfo.setClazzName(v.get(0).getClazzName());
                clazzInfo.setStudentNum(v.size());
                SchoolLevelCountNewResDTO.LevelStudentAndScore clazzLevelA = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
                SchoolLevelCountNewResDTO.LevelStudentAndScore clazzLevelB = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
                SchoolLevelCountNewResDTO.LevelStudentAndScore clazzLevelC = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
                clazzLevelA.setNum(0);
                clazzLevelA.setRate(new BigDecimal(0));
                clazzLevelB.setNum(0);
                clazzLevelB.setRate(new BigDecimal(0));
                clazzLevelC.setNum(0);
                clazzLevelC.setRate(new BigDecimal(0));
                clazzInfo.setLevelA(clazzLevelA);
                clazzInfo.setLevelB(clazzLevelB);
                clazzInfo.setLevelC(clazzLevelC);
                resDTOList.add(clazzInfo);
            });
        }

        //默认分档 达线统计列表
        if (reqDTO.getType() == ZSYLevelCountType.DEFAYLT_COUNT.getValue()) {
            return defaultLevelList(examList);
        }

        return resDTOList;
    }

    /**
     * 分档统计默认设置
     * @param examList
     * @return
     */
    private SchoolLevelCountNewResDTO defaultCount(List<StudentExam> examList) {
        SchoolLevelCountNewResDTO resDTO = new SchoolLevelCountNewResDTO();
        SchoolLevelCountNewResDTO.LevelStudentAndScore levelA = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
        SchoolLevelCountNewResDTO.LevelStudentAndScore levelB = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
        SchoolLevelCountNewResDTO.LevelStudentAndScore levelC = new SchoolLevelCountNewResDTO.LevelStudentAndScore();

        //全年级 成绩 倒序
        List<StudentExam> sortList = examList.stream().sorted(Comparator.comparing(StudentExam::getScoring).reversed()).collect(Collectors.toList());
        //全年级  实际考试人数
        long studentNum = examList.size();
        if (studentNum < 10) {
            throw new ZSYServiceException("人数少于10，无法统计");
        }
        Double num = studentNum * 0.1;
        //3个维度 10% 20% 50%
        //计算公式 总人数的10%,看这10%里最后一名的得分，在查询分数 >= 这个得分的人数
        StudentExam studentAndScoreBO = sortList.get(num.intValue() - 1 < 0 ? 0 : num.intValue() - 1);
        List<StudentExam> collect = sortList.stream().filter(list -> list.getScoring().compareTo(studentAndScoreBO.getScoring()) >= 0).collect(Collectors.toList());
        levelA.setScore(BigDecimal.valueOf(studentAndScoreBO.getScoring()));//A档最后一名的成绩
        levelA.setNum(collect.size());
        BigDecimal avgA = BigDecimal.valueOf(collect.stream().mapToDouble(StudentExam::getScoring).average().orElse(0D)).setScale(2, BigDecimal.ROUND_DOWN);
        levelA.setAvgScoring(avgA);
        levelA.setRate(new BigDecimal(10));
        resDTO.setLevelA(levelA);

        //20%
        Double numB = studentNum * 0.2;
        StudentExam studentAndScoreBOB = sortList.get(numB.intValue() - 1 < 0 ? 0 : numB.intValue() - 1);
        List<StudentExam> listB = examList.stream()
                .filter(exam -> BigDecimal.valueOf(exam.getScoring()).compareTo(BigDecimal.valueOf(studentAndScoreBOB.getScoring())) >= 0)
                .filter(exam -> BigDecimal.valueOf(exam.getScoring()).compareTo(BigDecimal.valueOf(studentAndScoreBO.getScoring())) < 0)
                .collect(Collectors.toList());

        List<StudentExam> listAB = sortList.stream().filter(list -> list.getScoring().compareTo(studentAndScoreBOB.getScoring()) >= 0).collect(Collectors.toList());
        BigDecimal avgB = BigDecimal.valueOf(listB.stream().mapToDouble(StudentExam::getScoring).average().orElse(0D)).setScale(2, BigDecimal.ROUND_DOWN);

        levelB.setAvgScoring(avgB);
        levelB.setRate(new BigDecimal(20));
        levelB.setNum(listB.size());
        levelB.setCountNum(listAB.size());//20%的人数
        levelB.setScore(BigDecimal.valueOf(studentAndScoreBOB.getScoring()));

        //50%
        Double numC = studentNum * 0.5;
        StudentExam studentAndScoreBOC = sortList.get(numC.intValue() - 1);
        List<StudentExam> lisABC = sortList.stream().filter(list -> list.getScoring().compareTo(studentAndScoreBOC.getScoring()) >= 0).collect(Collectors.toList());
        List<StudentExam> listC = examList.stream()
                .filter(exam -> BigDecimal.valueOf(exam.getScoring()).compareTo(BigDecimal.valueOf(studentAndScoreBOC.getScoring())) >= 0)
                .filter(exam -> BigDecimal.valueOf(exam.getScoring()).compareTo(BigDecimal.valueOf(studentAndScoreBOB.getScoring())) < 0)
                .collect(Collectors.toList());
        BigDecimal avgC = BigDecimal.valueOf(listC.stream().mapToDouble(StudentExam::getScoring).average().orElse(0D)).setScale(2, BigDecimal.ROUND_DOWN);

        levelC.setAvgScoring(avgC);
        levelC.setRate(new BigDecimal(50));
        levelC.setNum(listC.size());
        levelC.setCountNum(lisABC.size());//50%的人数
        levelC.setScore(BigDecimal.valueOf(studentAndScoreBOC.getScoring()));

        resDTO.setLevelA(levelA);
        resDTO.setLevelB(levelB);
        resDTO.setLevelC(levelC);

        return resDTO;
    }

    /**
     *  同一考生各个学科考试成绩累加，所有学科缺考为缺考，
     *  对多门学科统计，只要有一门学科有考试，则算不缺考
     * @param list
     * @return
     */
    private List<StudentExam> separete(List<StudentExam> list){

        Map<String,StudentExam> map = new HashMap<>();
        for(StudentExam vo:list){
            String studentId = vo.getStudentId();
            if(map.containsKey(studentId)){
                StudentExam studentExam = map.get(studentId);
                //合并得分
                studentExam.setScoring(studentExam.getScoring()+vo.getScoring());
                //缺考，所有考试缺考为缺考
                if(studentExam.getMissExam()==0 || vo.getMissExam() == 0){
                    studentExam.setMissExam(0);
                }else {
                    studentExam.setMissExam(1);
                }
                map.put(studentId,studentExam);
            }else {
                map.put(studentId,vo);
            }
        }

        List<StudentExam> listNew = new ArrayList<>(map.values());
        return listNew;
    }

    /**
     * check args
     * @param reqDTO
     */
    private void check(SchoolLevelCountReqDTO reqDTO) {
        //学科不能为空
        List<String> subjectIds = reqDTO.getSubjectIds();
        if(subjectIds == null || subjectIds.isEmpty()){
            throw new ZSYServiceException("考试学科不能为空");
        }
        //如果是按照分数分档
        if (reqDTO.getType() == ZSYLevelCountType.SCORE_COUNT.getValue()) {
            if (reqDTO.getLevelA() == null || reqDTO.getLevelB() == null || reqDTO.getLevelC() == null) {
                throw new ZSYServiceException("分数不能为空");
            }
            if (reqDTO.getLevelA().compareTo(new BigDecimal(0)) <= 0
                    || reqDTO.getLevelB().compareTo(new BigDecimal(0)) <= 0
                    || reqDTO.getLevelC().compareTo(new BigDecimal(0)) <= 0) {
                throw new ZSYServiceException("分数必须大于0");
            }

            if (reqDTO.getLevelA().compareTo(reqDTO.getLevelB()) <= 0 || reqDTO.getLevelB().compareTo(reqDTO.getLevelC()) <= 0) {
                throw new ZSYServiceException("A档线必须大于B档线，B档线必须大于C档线");
            }
        }

        //如果是按照人数分档
        if (reqDTO.getType() == ZSYLevelCountType.NUM_COUNT.getValue()) {
            if (reqDTO.getNumA() == null || reqDTO.getNumB() == null || reqDTO.getNumC() == null) {
                throw new ZSYServiceException("人数不能为空");
            }
            if (reqDTO.getNumA() <= 0 || reqDTO.getNumB() <= 0 || reqDTO.getNumC() <= 0) {
                throw new ZSYServiceException("人数必须大于0");
            }
        }



    }

    /**
     * 达线率
     *
     * @param divisor  除数
     * @param dividend 被除数
     * @return
     */
    private BigDecimal getRatioPercent(int divisor, int dividend) {
        if (dividend == 0) {
            return BigDecimal.ZERO.setScale(SCALE_2, BigDecimal.ROUND_DOWN);
        }
        return BigDecimal.valueOf(divisor)
                .divide(BigDecimal.valueOf(dividend), SCALE_4, BigDecimal.ROUND_DOWN)
                .multiply(DECIMAL_TO_PERCENT).setScale(SCALE_2, BigDecimal.ROUND_DOWN);
    }


    /**
     * 默认分档 达线统计列表
     * 10% 20% 50%
     * @param examList
     * @return
     */
    private List<SchoolLevelCountNewResDTO> defaultLevelList(List<StudentExam> examList){

        List<SchoolLevelCountNewResDTO> resDTOList = Lists.newArrayList();
        SchoolLevelCountNewResDTO schoolLevelCountNewResDTO = new SchoolLevelCountNewResDTO();
        SchoolLevelCountNewResDTO.LevelStudentAndScore levelA = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
        SchoolLevelCountNewResDTO.LevelStudentAndScore levelB = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
        SchoolLevelCountNewResDTO.LevelStudentAndScore levelC = new SchoolLevelCountNewResDTO.LevelStudentAndScore();

        schoolLevelCountNewResDTO.setClazzName("全年级");
        int studentNum = examList.size();
        if (studentNum < 10) {
            throw new ZSYServiceException("人数少于10，无法统计");
        }
        schoolLevelCountNewResDTO.setStudentNum(studentNum);
        //全年级按照分数 倒序 排序
        List<StudentExam> sortList = examList.stream().sorted(Comparator.comparing(StudentExam::getScoring).reversed()).collect(Collectors.toList());
        //10%
        Double numA = studentNum*0.1;
        StudentExam studentExamA = sortList.get(numA.intValue() - 1 < 0 ? 0 : numA.intValue() - 1);
        List<StudentExam> listA = sortList.stream().filter(list -> list.getScoring().compareTo(studentExamA.getScoring()) >= 0).collect(Collectors.toList());
        levelA.setScore(BigDecimal.valueOf(studentExamA.getScoring()));//A档最后一名的成绩
        levelA.setNum(listA.size());
        BigDecimal avgA = BigDecimal.valueOf(listA.stream().mapToDouble(StudentExam::getScoring).average().orElse(0D)).setScale(2, BigDecimal.ROUND_DOWN);
        levelA.setAvgScoring(avgA);
        levelA.setRate(getRatioPercent(listA.size(), studentNum));
        //20%
        Double numB = studentNum * 0.2;
        StudentExam studentExamB = sortList.get(numB.intValue() - 1 < 0 ? 0 : numB.intValue() - 1);
        List<StudentExam> listB = sortList.stream()
                .filter(exam -> BigDecimal.valueOf(exam.getScoring()).compareTo(BigDecimal.valueOf(studentExamB.getScoring())) >= 0)
                .filter(exam -> BigDecimal.valueOf(exam.getScoring()).compareTo(BigDecimal.valueOf(studentExamA.getScoring())) < 0)
                .collect(Collectors.toList());

        List<StudentExam> listAB = sortList.stream().filter(list -> list.getScoring().compareTo(studentExamB.getScoring()) >= 0).collect(Collectors.toList());
        BigDecimal avgB = BigDecimal.valueOf(listB.stream().mapToDouble(StudentExam::getScoring).average().orElse(0D)).setScale(2, BigDecimal.ROUND_DOWN);

        levelB.setAvgScoring(avgB);
        levelB.setRate(getRatioPercent(listB.size(), studentNum));
        levelB.setNum(listB.size());// B档分数人数
        levelB.setCountNum(listAB.size());//B档分数累计人数 20%的人数
        levelB.setScore(BigDecimal.valueOf(studentExamB.getScoring()));

        //50%
        Double numC = studentNum * 0.5;
        StudentExam studentExamC = sortList.get(numC.intValue() - 1 < 0 ? 0 : numC.intValue() - 1);
        List<StudentExam> listABC = sortList.stream().filter(list -> list.getScoring().compareTo(studentExamC.getScoring()) >= 0).collect(Collectors.toList());
        List<StudentExam> listC = sortList.stream()
                .filter(exam -> BigDecimal.valueOf(exam.getScoring()).compareTo(BigDecimal.valueOf(studentExamC.getScoring())) >= 0)
                .filter(exam -> BigDecimal.valueOf(exam.getScoring()).compareTo(BigDecimal.valueOf(studentExamB.getScoring())) < 0)
                .collect(Collectors.toList());

        BigDecimal avgC = BigDecimal.valueOf(listC.stream().mapToDouble(StudentExam::getScoring).average().orElse(0D)).setScale(2, BigDecimal.ROUND_DOWN);
        levelC.setAvgScoring(avgC);
        levelC.setRate(getRatioPercent(listABC.size(), studentNum));
        levelC.setNum(listC.size());
        levelC.setCountNum(listABC.size());//50%的人数
        levelC.setScore(BigDecimal.valueOf(studentExamC.getScoring()));
        schoolLevelCountNewResDTO.setLevelA(levelA);
        schoolLevelCountNewResDTO.setLevelB(levelB);
        schoolLevelCountNewResDTO.setLevelC(levelC);
        //列表第一行数据 全年级
        resDTOList.add(schoolLevelCountNewResDTO);
        //按班级统计
        Map<String, List<StudentExam>> map = examList.stream().collect(Collectors.groupingBy(StudentExam::getClassId, LinkedHashMap::new, Collectors.toList()));


        map.forEach((key, value) -> {
            SchoolLevelCountNewResDTO clazzInfo = new SchoolLevelCountNewResDTO();
            SchoolLevelCountNewResDTO.LevelStudentAndScore clazzLevelA = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
            SchoolLevelCountNewResDTO.LevelStudentAndScore clazzLevelB = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
            SchoolLevelCountNewResDTO.LevelStudentAndScore clazzLevelC = new SchoolLevelCountNewResDTO.LevelStudentAndScore();
            clazzInfo.setClazzName(value.get(0).getClazzName());
            clazzInfo.setStudentNum(value.size());
            List<StudentExam> clazzListA = value.stream().filter(v -> BigDecimal.valueOf(v.getScoring()).compareTo(levelA.getScore()) >= 0).collect(Collectors.toList());
            clazzLevelA.setNum(clazzListA.size());
            clazzLevelA.setRate(getRatioPercent(clazzListA.size(), value.size()));

            List<StudentExam> clazzListB = value.stream()
                    .filter(v -> BigDecimal.valueOf(v.getScoring()).compareTo(levelB.getScore()) >= 0)
                    .filter(v -> BigDecimal.valueOf(v.getScoring()).compareTo(levelA.getScore()) < 0)
                    .collect(Collectors.toList());
            clazzLevelB.setNum(clazzListB.size() + clazzListA.size());
            clazzLevelB.setRate(getRatioPercent(clazzListB.size() + clazzListA.size(), value.size()));

            List<StudentExam> clazzListC = value.stream()
                    .filter(v -> BigDecimal.valueOf(v.getScoring()).compareTo(levelC.getScore()) >= 0)
                    .filter(v -> BigDecimal.valueOf(v.getScoring()).compareTo(levelB.getScore()) < 0)
                    .collect(Collectors.toList());
            clazzLevelC.setNum(clazzListC.size() + clazzListB.size() + clazzListA.size());
            clazzLevelC.setRate(getRatioPercent(clazzListC.size() + clazzListB.size() + clazzListA.size(), value.size()));

            clazzInfo.setLevelA(clazzLevelA);
            clazzInfo.setLevelB(clazzLevelB);
            clazzInfo.setLevelC(clazzLevelC);
            resDTOList.add(clazzInfo);
        });

        return resDTOList;
    }


}
