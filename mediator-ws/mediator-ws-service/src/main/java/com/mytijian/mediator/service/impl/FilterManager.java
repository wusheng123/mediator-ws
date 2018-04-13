package com.mytijian.mediator.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mytijian.mediator.api.dto.ExportExamReportDto;
import com.mytijian.mediator.api.dto.SyncExamReportFilter;
import com.mytijian.mediator.filter.ExamReportMarriageFilter;
import com.mytijian.mediator.report.DAO.SyncExamReportFilterMapper;

@Service("filterManager")
public class FilterManager {
	@Resource(name="syncExamReportFilterMapper")
	private SyncExamReportFilterMapper syncExamReportFilterMapper;
	public void doFilter(Integer hospitalId , ExportExamReportDto exportExamReportDto){
		SyncExamReportFilter syncExamReportFilter = syncExamReportFilterMapper.selectByHospitalId(hospitalId);
		
		/*********************通用过滤*************************/
		ExamReportMarriageFilter.dofilter(exportExamReportDto);
		
		
		if(syncExamReportFilter == null){
			return ;
		}
		/***********************程序对异常配置健壮处理*************************/
		if(syncExamReportFilter.getCharsetFilter() == null){
			syncExamReportFilter.setCharsetFilter(0);
		}
		if(syncExamReportFilter.getGenderFilter() == null){
			syncExamReportFilter.setGenderFilter(0);
		}
		if(syncExamReportFilter.getMarriageFilter() == null){
			syncExamReportFilter.setMarriageFilter(0);
		}
		/***************************对各医院过滤处理***************************/
		switch(syncExamReportFilter.getCharsetFilter()){
		
		}
		switch(syncExamReportFilter.getGenderFilter()){
		
		}
		switch(syncExamReportFilter.getMarriageFilter()){
			case 1: ExamReportMarriageFilter.dofilter1(exportExamReportDto);break;
		}
	}
	
}
