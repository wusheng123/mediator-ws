package com.mytijian.mediator.report.DAO;

import org.springframework.stereotype.Repository;

import com.mytijian.mediator.api.dto.SyncExamReportFilter;
@Repository("syncExamReportFilterMapper")
public interface SyncExamReportFilterMapper {

	SyncExamReportFilter selectByHospitalId(Integer hospitalId);

}