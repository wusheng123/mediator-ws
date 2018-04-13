package com.mytijian.mediator.common.DO;

import java.util.Date;

/**
 *描述:请添加一段类名描述
 *日期:2017年12月19日
 *时间:下午5:44:19
 *@author huangwei
*/
public class GenericPEServiceTaskDO {
	
	private Integer id;
	
	private Integer hospitalId;
	
	private String taskName;
	
	private String taskParams;
	
	private Integer taskSwitch;
	
	private String crontabExpression;
	
	private Date gmtCreated;
	
	private Date gmtModified;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskParams() {
		return taskParams;
	}

	public void setTaskParams(String taskParams) {
		this.taskParams = taskParams;
	}

	public Integer getTaskSwitch() {
		return taskSwitch;
	}

	public void setTaskSwitch(Integer taskSwitch) {
		this.taskSwitch = taskSwitch;
	}

	public String getCrontabExpression() {
		return crontabExpression;
	}

	public void setCrontabExpression(String crontabExpression) {
		this.crontabExpression = crontabExpression;
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
	
}
