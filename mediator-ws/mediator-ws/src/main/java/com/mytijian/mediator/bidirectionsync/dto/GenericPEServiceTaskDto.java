package com.mytijian.mediator.bidirectionsync.dto;
/**
 *描述:请添加一段类名描述
 *日期:2017年12月20日
 *时间:下午4:05:11
 *@author huangwei
*/
public class GenericPEServiceTaskDto {
	
	private String taskName;
	
	private String taskParams;
	
	private Integer taskSwitch;
	
	private String crontabExpression;

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
}
