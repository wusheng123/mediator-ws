package com.mytijian.mediator.common.DO;

import java.util.Date;

public class MediatorAgentTaskDO {

	private Integer id;

	private Date gmtCreated;

	private Date gmtModified;

	private Integer hospitalId;

	private String taskCmd;

	private String taskParams;

	private Boolean init;
	
	private Integer status;
	
	/**
	 * crontab表达式
	 */
	private String cronExpression;
	
	/**
	 * 在spring容器中任务的bean name
	 */
	private String taskBeanName;
	
	/**
	 * 省份Id
	 */
	private Integer provinceId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getGmtCreated() {
		return gmtCreated;
	}

	public void setGmtCreated(Date gmtCreated) {
		this.gmtCreated = gmtCreated;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getTaskCmd() {
		return taskCmd;
	}

	public void setTaskCmd(String taskCmd) {
		this.taskCmd = taskCmd;
	}

	public String getTaskParams() {
		return taskParams;
	}

	public void setTaskParams(String taskParams) {
		this.taskParams = taskParams;
	}

	/**
	 * @return the init
	 */
	public Boolean getInit() {
		return init;
	}

	/**
	 * @param init the init to set
	 */
	public void setInit(Boolean init) {
		this.init = init;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getTaskBeanName() {
		return taskBeanName;
	}

	public void setTaskBeanName(String taskBeanName) {
		this.taskBeanName = taskBeanName;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}
	
}