package com.zsy.sass.report.controller;

import com.zsy.sass.common.tools.ZSYResult;
import com.zsy.sass.model.dto.request.SchoolLevelCountReqDTO;
import com.zsy.sass.model.dto.response.SchoolLevelCountNewResDTO;
import com.zsy.sass.report.service.ExamReportService;
import io.swagger.annotations.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by yqq on 2019/6/27.
 */
@RestController
@RequestMapping("/exam-report")
public class ExamReportController {

    private static final Log log = LogFactory.getLog(ExamReportController.class);

    @Autowired
    private ExamReportService examReportService;

    @ApiOperation("通过id查询考试")
    @RequestMapping(value = "/find-exam/{id}", method = RequestMethod.GET)
    public String findExamById(@PathVariable("id") Long id){
        log.info("find a exam");
        return ZSYResult.success().data(examReportService.findExamById(id)).build();
    }


    /**
     * 本校报告-分档达线统计
     */
    @ApiOperation("sass本校报告-分档达线统计")
    @ApiResponses({@ApiResponse(code = 200, message = "执行成功",response = SchoolLevelCountNewResDTO.class)})
    @PostMapping("/school/level")
    public String levelCount(@Valid @RequestBody SchoolLevelCountReqDTO countReqDTO){
        SchoolLevelCountNewResDTO vo = examReportService.levelCount(countReqDTO);
        return ZSYResult.success().data(vo).build();
    }

    /**
     * 本校报告-分档达线统计列表
     */
    @ApiOperation("sass本校报告-分档达线统计")
    @PostMapping("/school/level/list")
    public String levelCountList(@Valid @RequestBody SchoolLevelCountReqDTO countReqDTO){
        List<SchoolLevelCountNewResDTO> list = examReportService.levelCountList(countReqDTO);
        return ZSYResult.success().data(list).build();
    }


    /**
     * 本校报告-分档达线统计列表导出
     */

}
