package com.zsy.sass.report.service;

import com.zsy.sass.model.dto.request.SchoolLevelCountReqDTO;
import com.zsy.sass.model.dto.response.SchoolLevelCountNewResDTO;
import com.zsy.sass.model.pojo.Exam;

import java.util.List;

/**
 * Created by yqq on 2019/6/27.
 */
public interface ExamReportService {

    /**
     *find exam by id
     */
    Exam findExamById(Long id);

    /**
     * sass本校报告-分档达线统计
     * @param countReqDTO
     * @return
     */
    SchoolLevelCountNewResDTO levelCount(SchoolLevelCountReqDTO countReqDTO);

    /**
     * sass本校报告-分档达线统计列表
     * @param countReqDTO
     * @return
     */
    List<SchoolLevelCountNewResDTO> levelCountList(SchoolLevelCountReqDTO countReqDTO);
}
