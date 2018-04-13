/*
 * Copyright 2016 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.company.param;

import java.util.List;

/**
 * 类MediatorHisCompanyDAOUpdateParam.java的实现描述：
 * 用户更新mediatorHisCompany表
 * @author liangxing 2016年11月6日 上午10:54:09
 */
public class MediatorHisCompanyDAOUpdateParam {

	/**
	 * 需要更新数据的idList
	 * 为NULL表示更新全部
	 * empty 表示不更新
	 */
	private List<Integer> ids;

	/**
	 * 需要更新的医院id
	 * null表示更新全部
	 */
	private Integer hospitalId;
	private String toHisCompanyName;
	private String toHisCompanysStatus;
	private String toRefresh;
	private String toHisCompanyCode;

	/**
	 * @return the ids
	 */
	public List<Integer> getIds() {
		return ids;
	}

	/**
	 * @param ids the ids to set
	 */
	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}

	/**
	 * @return the hospitalId
	 */
	public Integer getHospitalId() {
		return hospitalId;
	}

	/**
	 * @param hospitalId the hospitalId to set
	 */
	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	/**
	 * @return the toHisCompanyName
	 */
	public String getToHisCompanyName() {
		return toHisCompanyName;
	}

	/**
	 * @param toHisCompanyName the toHisCompanyName to set
	 */
	public void setToHisCompanyName(String toHisCompanyName) {
		this.toHisCompanyName = toHisCompanyName;
	}

	/**
	 * @return the toHisCompanysStatus
	 */
	public String getToHisCompanysStatus() {
		return toHisCompanysStatus;
	}

	/**
	 * @param toHisCompanysStatus the toHisCompanysStatus to set
	 */
	public void setToHisCompanysStatus(String toHisCompanysStatus) {
		this.toHisCompanysStatus = toHisCompanysStatus;
	}

	/**
	 * @return the toRefresh
	 */
	public String getToRefresh() {
		return toRefresh;
	}

	/**
	 * @param toRefresh the toRefresh to set
	 */
	public void setToRefresh(String toRefresh) {
		this.toRefresh = toRefresh;
	}

	/**
	 * @return the toHisCompanyCode
	 */
	public String getToHisCompanyCode() {
		return toHisCompanyCode;
	}

	/**
	 * @param toHisCompanyCode the toHisCompanyCode to set
	 */
	public void setToHisCompanyCode(String toHisCompanyCode) {
		this.toHisCompanyCode = toHisCompanyCode;
	}

}
