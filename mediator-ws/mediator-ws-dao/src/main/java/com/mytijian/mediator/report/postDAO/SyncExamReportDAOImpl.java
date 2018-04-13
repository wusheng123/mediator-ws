package com.mytijian.mediator.report.postDAO;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.mytijian.mediator.api.dto.SyncExamReportDto;
import com.mytijian.mediator.api.enums.SyncExamReportStateEnum;
import com.mytijian.mediator.base.BaseResult;
import com.mytijian.mediator.report.DAO.SyncExamReportMapper;
import com.mytijian.pulgin.mybatis.pagination.Page;

@Service("syncExamReportDAO")
public class SyncExamReportDAOImpl implements SyncExamReportDAO {
	
	@Resource(name = "syncExamReportMapper")
	private SyncExamReportMapper syncExamReportMapper;

	@Override
	public List<SyncExamReportDto> getSyncExamReports(Integer hospitalId,
			SyncExamReportStateEnum syncExamReportStateEnum, Page page) {
		int pageSize = page.getPageSize();
		int currentPage = page.getCurrentPage(); 
		pageSize = pageSize >= 0 ? pageSize : 0;
		currentPage = currentPage > 0 ? currentPage : 1;
		Integer startIndex = (currentPage - 1 ) * pageSize;
		return syncExamReportMapper.getSyncExamReports(hospitalId, 
				syncExamReportStateEnum == null ? null : syncExamReportStateEnum.getValue(), startIndex, pageSize);
	}

	@Override
	public void updateSyncExamReportsState(List<BaseResult> baseResults) {
		List<Long> successIds = Lists.newArrayList(); 
		List<Long> failedIds = Lists.newArrayList(); 
		getFailedAndSuccessIds(baseResults, successIds, failedIds);
		if (CollectionUtils.isNotEmpty(failedIds) && failedIds.size() > 0) {
			syncExamReportMapper.updateStatusBatchByIds(failedIds, SyncExamReportStateEnum.FAIL.getValue());
		}
		
		if (CollectionUtils.isNotEmpty(successIds) && successIds.size() > 0) {
			syncExamReportMapper.updateStatusBatchByIds(successIds, SyncExamReportStateEnum.DONE.getValue());
		}
	}

	@Override
	public void delSyncExamReportByIds(List<Long> examReportIds) {
		// examReportIds.forEach(examReportId -> syncExamReportMapper.deleteByPrimaryKey(examReportId));
		if (CollectionUtils.isNotEmpty(examReportIds)) {
			syncExamReportMapper.deleteBatchByIds(examReportIds);
		}
	}

	@Override
	public void delDoneSyncExamReport(Long examReportId, String reportNo, Integer hospitalId) {
		syncExamReportMapper.delDoneSyncExamReport(examReportId, reportNo, hospitalId);
	}
	
	@Override
	public SyncExamReportDto selectByPrimaryKey(Long syncExamReportId) {
		return syncExamReportMapper.selectByPrimaryKey(syncExamReportId);
	}
	
	private void getFailedAndSuccessIds(List<BaseResult> baseResults, List<Long> successIds, List<Long> failedIds) {
		successIds = Optional.ofNullable(successIds).orElse(Lists.newArrayList());
		failedIds = Optional.ofNullable(failedIds).orElse(Lists.newArrayList());
		if (CollectionUtils.isNotEmpty(baseResults)) {
			for (BaseResult baseResult : baseResults) {
				if (null == baseResult.getCode()
						|| !baseResult.getCode().equalsIgnoreCase(SyncExamReportStateEnum.DONE.getValue())) {
					failedIds.add(Long.parseLong(baseResult.getId()));
				} else {
					successIds.add(Long.parseLong(baseResult.getId()));
				}
			}
		}
	}
	
}
