/*
 * Copyright 2016 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.company.DO;

import java.util.Date;

/**
 * 类MediatorHisCompanyDO.java的实现描述：TODO 类实现描述 
 * @author Administrator 2016年11月1日 下午3:28:13
 */
public class MediatorHisCompanyDO {
	private Integer id;
	private Date gmtCreated;
	private Date gmtModified;

	/**
	 * 平台单位id
	 */
	private Integer myCompanyId;
	/**
	 * his单位code
	 */
	private String hisCompanyCode;
	/**
	 * his单位名
	 */
	private String hisCompanyName;
	/**
	 * his单位同步状态
	 */
	private String hisCompanyStatus;

	/**
	 * 创建者
	 */
	private String creator;

	/**
	 * 医院id
	 */
	private Integer hospitalId;
	
	/**
	 * 刷新状态  refreshing  与 refreshed
	 */
	private String refresh;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the myCompanyId
	 */
	public Integer getMyCompanyId() {
		return myCompanyId;
	}

	/**
	 * @param myCompanyId the myCompanyId to set
	 */
	public void setMyCompanyId(Integer myCompanyId) {
		this.myCompanyId = myCompanyId;
	}

	/**
	 * @return the hisCompanyCode
	 */
	public String getHisCompanyCode() {
		return hisCompanyCode;
	}

	/**
	 * @param hisCompanyCode the hisCompanyCode to set
	 */
	public void setHisCompanyCode(String hisCompanyCode) {
		this.hisCompanyCode = hisCompanyCode;
	}

	/**
	 * @return the hisCompanyName
	 */
	public String getHisCompanyName() {
		return hisCompanyName;
	}

	/**
	 * @param hisCompanyName the hisCompanyName to set
	 */
	public void setHisCompanyName(String hisCompanyName) {
		this.hisCompanyName = hisCompanyName;
	}

	/**
	 * @return the hisCompanyStatus
	 */
	public String getHisCompanyStatus() {
		return hisCompanyStatus;
	}

	/**
	 * @param hisCompanyStatus the hisCompanyStatus to set
	 */
	public void setHisCompanyStatus(String hisCompanyStatus) {
		this.hisCompanyStatus = hisCompanyStatus;
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
	 * @return the gmtCreated
	 */
	public Date getGmtCreated() {
		return gmtCreated;
	}

	/**
	 * @param gmtCreated the gmtCreated to set
	 */
	public void setGmtCreated(Date gmtCreated) {
		this.gmtCreated = gmtCreated;
	}

	/**
	 * @return the gmtModified
	 */
	public Date getGmtModified() {
		return gmtModified;
	}

	/**
	 * @param gmtModified the gmtModified to set
	 */
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
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

}
