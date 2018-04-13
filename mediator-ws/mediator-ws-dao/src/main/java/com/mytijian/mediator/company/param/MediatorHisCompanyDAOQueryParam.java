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
 * 类MediatorHisCompanyDAOQueryParam.java的实现描述：TODO 类实现描述 
 * @author liangxing 2016年11月3日 下午5:23:28
 */
public class MediatorHisCompanyDAOQueryParam {

	/**
	 * 查询id in idList中的数据
	 * 为NULL 表示查询全部
	 * empty 不查询数据
	 */
	private List<Integer> idList;

	/**
	 * 查询 hisCompanyStatus in hisCompanyStatusList 中的数据
	 * 为NULL 表示查询全部
	 * empty 不查询数据
	 */
	private List<String> hisCompanyStatusList;

	/**
	 * 查询 hisCompanyCode in hisCompanyCodeList 中的数据
	 * 为NULL表示查询全部
	 * empty 不查询数据
	 */
	private List<String> hisCompanyCodeList;

	/**
	 * hisCompanyCode是否为null  true 表示不可以为null   null 或是false 不对null做判断
	 */
	private Boolean hisCompanyCodeNotNull;

	/**
	 * 查询 hisCompanyName in hisCompanyNameList 中的数据
	 * 为NULL表示查询全部
	 * empty 不查询数据
	 */
	private List<String> hisCompanyNameList;

	/**
	 * 查询由creator创建的单位
	 * 为NULL表示查询全部
	 */
	private String creator;

	/**
	 * 查询由hospitalId的单位
	 * 为NULL表示查询全部
	 */
	private Integer hospitalId;

	private String refresh;

	/**
	 * @return the idList
	 */
	public List<Integer> getIdList() {
		return idList;
	}

	/**
	 * @param idList the idList to set
	 */
	public void setIdList(List<Integer> idList) {
		this.idList = idList;
	}

	/**
	 * @return the hisCompanyStatusList
	 */
	public List<String> getHisCompanyStatusList() {
		return hisCompanyStatusList;
	}

	/**
	 * @param hisCompanyStatusList the hisCompanyStatusList to set
	 */
	public void setHisCompanyStatusList(List<String> hisCompanyStatusList) {
		this.hisCompanyStatusList = hisCompanyStatusList;
	}

	/**
	 * @return the hisCompanyCodeList
	 */
	public List<String> getHisCompanyCodeList() {
		return hisCompanyCodeList;
	}

	/**
	 * @param hisCompanyCodeList the hisCompanyCodeList to set
	 */
	public void setHisCompanyCodeList(List<String> hisCompanyCodeList) {
		this.hisCompanyCodeList = hisCompanyCodeList;
	}

	/**
	 * @return the hisCompanyNameList
	 */
	public List<String> getHisCompanyNameList() {
		return hisCompanyNameList;
	}

	/**
	 * @param hisCompanyNameList the hisCompanyNameList to set
	 */
	public void setHisCompanyNameList(List<String> hisCompanyNameList) {
		this.hisCompanyNameList = hisCompanyNameList;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
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
	 * @return the refresh
	 */
	public String getRefresh() {
		return refresh;
	}

	/**
	 * @param refresh the refresh to set
	 */
	public void setRefresh(String refresh) {
		this.refresh = refresh;
	}

	/**
	 * @return the hisCompanyCodeNotNull
	 */
	public Boolean getHisCompanyCodeNotNull() {
		return hisCompanyCodeNotNull;
	}

	/**
	 * @param hisCompanyCodeNotNull the hisCompanyCodeNotNull to set
	 */
	public void setHisCompanyCodeNotNull(Boolean hisCompanyCodeNotNull) {
		this.hisCompanyCodeNotNull = hisCompanyCodeNotNull;
	}

}
