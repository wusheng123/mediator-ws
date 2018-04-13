package com.mytijian.mediator.common.param;

import java.util.List;

/**
 * 任务医院查询
 * @author feng
 *
 */
public class ListTaskHospitalDaoQuery {
	
	/**
	 * 省份Ids
	 */
	private List<Integer> provinceIds;
	
	/**
	 * 医院名称
	 */
	private String hospitalName;
	
	/**
	 * 起始索引
	 */
	private Integer offset;
	
	/**
	 * 查询条数
	 */
	private Integer limit;

	public List<Integer> getProvinceIds() {
		return provinceIds;
	}

	public void setProvinceIds(List<Integer> provinceIds) {
		this.provinceIds = provinceIds;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	
}
