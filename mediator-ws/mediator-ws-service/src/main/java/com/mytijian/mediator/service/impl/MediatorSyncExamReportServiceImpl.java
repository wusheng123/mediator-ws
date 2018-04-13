package com.mytijian.mediator.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.mytijian.mediator.api.dto.ExamReportInfoDto;
import com.mytijian.mediator.api.dto.ExportExamReportDto;
import com.mytijian.mediator.api.dto.SyncExamReportDto;
import com.mytijian.mediator.api.enums.SyncExamReportStateEnum;
import com.mytijian.mediator.api.model.ReportDeptBasicInfo;
import com.mytijian.mediator.api.service.EnvironmentKeeper;
import com.mytijian.mediator.api.service.MediatorSyncExamReportService;
import com.mytijian.mediator.report.DAO.SyncExamReportMapper;
import com.mytijian.util.DateUtils;
@Service("mediatorSyncExamReportService")
public class MediatorSyncExamReportServiceImpl implements MediatorSyncExamReportService {

	private Logger logger = LoggerFactory.getLogger(MediatorSyncExamReportServiceImpl.class);
	@Resource(name = "syncExamReportMapper")
	private SyncExamReportMapper syncExamReportMapper;

	@Resource(name = "soapHeaderInterceptor")
	private EnvironmentKeeper envKeeper;
	
	@Override
	public List<String> batchSyncExamReport(List<ExportExamReportDto> dtoList) {
		logger.info("batch sync exam report ,hosp id : {}", envKeeper.getHospital());
		if (CollectionUtils.isEmpty(dtoList)) {
			return Lists.newArrayList();
		}

		dtoList = dtoList.stream().filter(f -> f.getExamReportInfoDto() != null).collect(Collectors.toList());// 过滤getExamReportInfoDto
																												// 为null数据

		// 打印report no
		List<String> reportNoBefore = dtoList.stream().collect(() -> new ArrayList<String>(),
				(list, item) -> list.add(item.getExamReportInfoDto().getReportNo()),
				(list, subList) -> list.addAll(subList));
		logger.info("hosp id : {},report no list : {}", envKeeper.getHospital(), reportNoBefore);

		dtoList = dtoList.stream().filter(distinctByKey(f -> f.getExamReportInfoDto()// 过滤重复的reportno
				.getReportNo())).collect(Collectors.toList());

		List<String> reportNoAfter = dtoList.stream().collect(() -> new ArrayList<String>(),
				(list, item) -> list.add(item.getExamReportInfoDto().getReportNo()),
				(list, subList) -> list.addAll(subList));

		logger.info("hosp id : {},report no list : {}", envKeeper.getHospital(), reportNoAfter);

		for (ExportExamReportDto exportExamReportDto : dtoList) {
			
			ExamReportInfoDto examReportInfoDto = exportExamReportDto.getExamReportInfoDto();
			if (examReportInfoDto.getExamDate() == null || examReportInfoDto.getReportTime() == null) {
				logger.error("the report_id:{},examDate or reportTime is null",examReportInfoDto.getReportNo());
				continue;
			}

			SyncExamReportDto syncExamReportDto = initSyncExamReportDto(exportExamReportDto);
			syncExamReportMapper.insert(syncExamReportDto);
		}

		return Lists.newArrayList();
	}
	
	private <T> Predicate<T> distinctByKey(
			Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	private SyncExamReportDto initSyncExamReportDto(ExportExamReportDto exportExamReportDto){
		SyncExamReportDto syncExamReportDto = new SyncExamReportDto();
		ExamReportInfoDto examReportInfoDto = exportExamReportDto.getExamReportInfoDto();
		syncExamReportDto.setCompanyName(examReportInfoDto.getExamCompany());
		String dateStr = DateUtils.format("yyyy-MM-dd HH:mm:ss",examReportInfoDto.getExamDate());
		syncExamReportDto.setExamTime(dateStr);
		syncExamReportDto.setHospitalId(examReportInfoDto.getHospitalId());
		syncExamReportDto.setIdcard(examReportInfoDto.getIdCard());
		syncExamReportDto.setMobile(examReportInfoDto.getMobile());
		syncExamReportDto.setOrderNum(examReportInfoDto.getOrderNum());
		String examReportInfoDtoJson = JSON.toJSONString(exportExamReportDto);
		syncExamReportDto.setReportContext(examReportInfoDtoJson);
		syncExamReportDto.setReportNo(examReportInfoDto.getReportNo());
		String reportTime = DateUtils.format("yyyy-MM-dd HH:mm:ss",examReportInfoDto.getReportTime());
		syncExamReportDto.setReportTime(reportTime);
		syncExamReportDto.setState(SyncExamReportStateEnum.INIT.value());
		return syncExamReportDto;
	}
	@Override
	public Boolean syncExamReport(ExportExamReportDto exportExamReportDto) {
		List<String>  result = batchSyncExamReport(Lists.newArrayList(exportExamReportDto));
		if(result != null && !result.isEmpty()){
			return false;
		}
		return true;
	}

	@Override
	public void syncTplBasicInfo(List<ReportDeptBasicInfo> reportDeptBasicInfoList) {
	}

}
