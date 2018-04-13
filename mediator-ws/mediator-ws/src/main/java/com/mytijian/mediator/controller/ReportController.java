package com.mytijian.mediator.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mytijian.mediator.api.service.MediatorSyncExamReportService;
import com.mytijian.mediator.service.common.ValidationService;

@RestController
public class ReportController{

	@Resource(name = "mediatorSyncExamReportService")
	private MediatorSyncExamReportService mediatorSyncExamReportService;
	@Resource(name = "validationService")
	private ValidationService validationService;

	@RequestMapping(value = "/report/{hospitalId}", method = RequestMethod.GET, headers = "Accept=application/json")
	public String report(@PathVariable("hospitalId") Integer hospitalId,
			@RequestParam(value = "reportId", required = false) String reportId) {
//		StringBuilder errors = new StringBuilder();
//		if (reportId == null) {
//			mediatorExamReportService.parseFailedReports(hospitalId, (reportNo, errorMsg) -> {
//				errors.append(errorMsg);
//			});
//			logger.info("重新解析医院未完成报告! hospitalId: " + hospitalId);
//		} else {
//			mediatorExamReportService.parseReport(hospitalId, reportId, (reportNo, errorMsg) -> {
//				errors.append(errorMsg);
//			});
//			logger.info("重新解析医院未完成报告! hospitalId: " + hospitalId + ", reportId: " + reportId);
//		}
//		if(AssertUtil.isNotEmpty(errors.toString())){
//			return errors.toString();
//		} else {
//			return "done";
//		}
		return "done";
	}
}
