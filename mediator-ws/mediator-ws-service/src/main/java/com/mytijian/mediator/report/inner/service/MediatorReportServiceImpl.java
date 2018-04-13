package com.mytijian.mediator.report.inner.service;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.mytijian.mediator.api.dto.ExportExamReportDto;
import com.mytijian.mediator.api.dto.SyncExamReportDto;
import com.mytijian.mediator.api.enums.SyncExamReportStateEnum;
import com.mytijian.mediator.api.service.MediatorSyncExamReportService;
import com.mytijian.mediator.base.BaseResult;
import com.mytijian.mediator.report.DAO.SyncExamReportMapper;
import com.mytijian.mediator.report.params.QuerySyncReportParam;
import com.mytijian.mediator.report.postDAO.SyncExamReportDAO;
import com.mytijian.mediator.service.impl.FilterManager;
import com.mytijian.pulgin.mybatis.pagination.Page;


@Service("mediatorReportService")
public class MediatorReportServiceImpl implements MediatorReportService {

	private Logger logger = LoggerFactory.getLogger(MediatorReportServiceImpl.class);

	@Resource(name = "mediatorSyncExamReportService")
	public MediatorSyncExamReportService mediatorSyncExamReportService;
	
	@Resource(name = "syncExamReportMapper")
	private SyncExamReportMapper syncExamReportMapper;

	@Resource(name = "syncExamReportDAO")
	public SyncExamReportDAO syncExamReportDAO;
	
	@Resource(name = "filterManager")
	private FilterManager filterManager;
	
	private static final Integer PAGESIZE = 50;
	
	@Override
	public void examReportImport(List<ExportExamReportDto> dtoList, Integer hospitalId) {
		mediatorSyncExamReportService.batchSyncExamReport(dtoList);
	}

	@Override
	public List<SyncExamReportDto> getSyncExamReports(Integer hospitalId,
			SyncExamReportStateEnum syncExamReportStateEnum, Page page) {
		if (page == null) {
			page = new Page(1, PAGESIZE);
		}
		if (page.getPageSize() > PAGESIZE || page.getPageSize() <= 0) {
			page.setPageSize(PAGESIZE);
		}
		
		List<SyncExamReportDto> syncExamReportDtos = syncExamReportDAO.getSyncExamReports(hospitalId, syncExamReportStateEnum, page);
		List<SyncExamReportDto> syncExamReportDtosNew = Lists.newArrayList();
		if (!CollectionUtils.isEmpty(syncExamReportDtos)) {
			syncExamReportDtos.forEach(syncExamReportDto -> {
				try {
					ExportExamReportDto exportExamReportDto = JSON.parseObject(syncExamReportDto.getReportContext(),
							ExportExamReportDto.class);
					filterManager.doFilter(exportExamReportDto.getExamReportInfoDto().getHospitalId(), exportExamReportDto);
					syncExamReportDto.setReportContext(JSON.toJSONString(exportExamReportDto));
					syncExamReportDtosNew.add(syncExamReportDto);
				} catch (Exception e) {
					logger.error("encapsule reportContext error,  reportContext : {}", syncExamReportDto.getReportContext(), e);
					BaseResult br = new BaseResult();
					br.setId(String.valueOf(syncExamReportDto.getId()));
					br.setCode(SyncExamReportStateEnum.FAIL.getValue());
					syncExamReportDAO.updateSyncExamReportsState(Lists.newArrayList(br));
					return;
				}
			});
		}
		return syncExamReportDtosNew;
	}

	@Override
	public void updateSyncExamReportsState(List<BaseResult> baseResults) {
		if (!CollectionUtils.isEmpty(baseResults)) {
			syncExamReportDAO.updateSyncExamReportsState(baseResults);
		}
	}

	@Override
	public void delSyncExamReportByIds(List<Long> examReportIds) {
		if (!CollectionUtils.isEmpty(examReportIds)) {
			syncExamReportDAO.delSyncExamReportByIds(examReportIds);
		}
	}

	public List<Long> querySyncReportId(QuerySyncReportParam queryParam, Long offset ,Integer size) {
		List<Long>  ids = syncExamReportMapper.selectSyncExamReportIdList(queryParam, offset, size);
		return ids;
	}

	@Override
	public void reSyncReport(Long id) {
	}

	@Override
	public void delDoneSyncExamReportByIds(List<Long> examReportIds) {
		if (!CollectionUtils.isEmpty(examReportIds)) {
			for (Long examReportId : examReportIds) {
				SyncExamReportDto syncExamReportDto = syncExamReportDAO.selectByPrimaryKey(examReportId);
				if (syncExamReportDto != null && null != syncExamReportDto.getState()) {
					if (!SyncExamReportStateEnum.DONE.getValue().equalsIgnoreCase(syncExamReportDto.getState())) {
						logger.error("当前报告状态不为DONE，不能删除。  id : {}", examReportId);
						continue;
					}
					int hospitalId = syncExamReportDto.getHospitalId();
					long id = syncExamReportDto.getId();
					String reportNo = syncExamReportDto.getReportNo();
					syncExamReportDAO.delDoneSyncExamReport(id, reportNo, hospitalId);
				}
			}
		}
	}

	@Override
	public List<SyncExamReportDto> getSyncExamReports(Integer hospitalId, Integer startIndex, Integer pageSize) {
		List<SyncExamReportDto> syncExamReportDtos =  syncExamReportMapper
				.getSyncExamReportsByStartIndex(hospitalId, startIndex, pageSize);
		List<SyncExamReportDto> syncExamReportDtosNew = Lists.newArrayList();
		if (!CollectionUtils.isEmpty(syncExamReportDtos)) {
			for(SyncExamReportDto dto : syncExamReportDtos){
				try {
					ExportExamReportDto exportExamReportDto = JSON.parseObject(dto.getReportContext(),
							ExportExamReportDto.class);
					filterManager.doFilter(exportExamReportDto.getExamReportInfoDto().getHospitalId(), exportExamReportDto);
					dto.setReportContext(JSON.toJSONString(exportExamReportDto));
					syncExamReportDtosNew.add(dto);
				} catch (Exception e) {
					logger.error("encapsule reportContext error,  reportContext : {}", dto.getReportContext(), e);
					continue;
				}
			}
		}
		return syncExamReportDtosNew;
	}

	@Override
	public Integer getMaxSyncReportId() {
		return syncExamReportMapper.selectMaxId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeDoneSyncExamReportBefore30DaysAgo() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 30);
		syncExamReportMapper.deleteDoneExamReportWithNDaysAgo(calendar
				.getTime());
	}
}
