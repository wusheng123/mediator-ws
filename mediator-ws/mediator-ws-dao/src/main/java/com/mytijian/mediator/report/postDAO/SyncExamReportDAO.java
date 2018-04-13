package com.mytijian.mediator.report.postDAO;

import java.util.List;

import com.mytijian.mediator.api.dto.SyncExamReportDto;
import com.mytijian.mediator.api.enums.SyncExamReportStateEnum;
import com.mytijian.mediator.base.BaseResult;
import com.mytijian.pulgin.mybatis.pagination.Page;

public interface SyncExamReportDAO {

	public List<SyncExamReportDto> getSyncExamReports(Integer hospitalId,
			SyncExamReportStateEnum syncExamReportStateEnum, Page page);

	public void updateSyncExamReportsState(List<BaseResult> baseResults);

	public void delSyncExamReportByIds(List<Long> examReportIds);
	
	public SyncExamReportDto selectByPrimaryKey(Long syncExamReportId);

	void delDoneSyncExamReport(Long examReportId, String reportNo, Integer hospitalId);
}
