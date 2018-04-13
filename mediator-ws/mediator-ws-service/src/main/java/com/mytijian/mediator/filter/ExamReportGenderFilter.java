package com.mytijian.mediator.filter;

public class ExamReportGenderFilter {
	/**
	 * his:创业
	 * 性别转换。   1->0  2->1 0->1
	 * @param hisGender
	 * @return
	 */
	public String dofilter1(String hisGender) {
		if("1".equals(hisGender)){
			return "0";
		}
		if("2".equals(hisGender)){
			return "1";
		}
		if("0".equals(hisGender)){
			return "1";
		}
		return null;
	}
	
}
