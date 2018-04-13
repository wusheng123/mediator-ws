package com.mytijian.mediator.filter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mytijian.mediator.api.dto.ExportExamReportDto;
import com.mytijian.util.AssertUtil;

public class ExamReportMarriageFilter {
	private static Logger logger = LoggerFactory.getLogger(ExamReportMarriageFilter.class);
	/**
	 * 对应数据库中配置为1
	 * @param hisMarriage
	 * @return
	 */
	public static void dofilter1(ExportExamReportDto exportExamReportDto) {
		String hisMarriage = exportExamReportDto.getExamReportInfoDto().getMarriageStatus();
		if(AssertUtil.isNotEmpty(hisMarriage)){
			if (hisMarriage.contains("已婚")) {
				hisMarriage = "1";
			}
			if (hisMarriage.contains("未婚")) {
				hisMarriage = "0";
			}
		}
		exportExamReportDto.getExamReportInfoDto().setMarriageStatus(hisMarriage);
		return ;
	}
	
	public static void dofilter(ExportExamReportDto exportExamReportDto) {
		String hisMarriage = exportExamReportDto.getExamReportInfoDto().getMarriageStatus();
		if(hisMarriage == null){
			exportExamReportDto.getExamReportInfoDto().setMarriageStatus(null);
		}else if("".equals(hisMarriage)){
			exportExamReportDto.getExamReportInfoDto().setMarriageStatus(null);
		}else if(hisMarriage.contains("已婚")){
			exportExamReportDto.getExamReportInfoDto().setMarriageStatus("1");
		}else if(hisMarriage.contains("未婚")){
			exportExamReportDto.getExamReportInfoDto().setMarriageStatus("0");
		}else if(hisMarriage.contains("再婚")){
			exportExamReportDto.getExamReportInfoDto().setMarriageStatus("1");
			logger.info("再婚" + "hospital_id=" + exportExamReportDto.getExamReportInfoDto().getHospitalId());
		}else if(StringUtils.isNumeric(hisMarriage)){
			exportExamReportDto.getExamReportInfoDto().setMarriageStatus(hisMarriage);
		}else{
			exportExamReportDto.getExamReportInfoDto().setMarriageStatus(null);
			logger.info(hisMarriage + "hospital_id=" + exportExamReportDto.getExamReportInfoDto().getHospitalId());
		}
		dofilterGender(exportExamReportDto);
		return ;
	}
	
	public static void dofilterGender(ExportExamReportDto exportExamReportDto) {
		String gender = exportExamReportDto.getExamReportInfoDto().getGender();
		String genderStr = "-1";
		// 性别 0 男 1女 3未知
		if(AssertUtil.isNotEmpty(gender)){
			if (gender.contains("男") || gender.contains("0")) {
				genderStr = "0";
			} else if (gender.contains("女") || gender.contains("1")) {
				genderStr = "1";
			}
		}
		exportExamReportDto.getExamReportInfoDto().setGender(genderStr);;
		return ;
	}
}
