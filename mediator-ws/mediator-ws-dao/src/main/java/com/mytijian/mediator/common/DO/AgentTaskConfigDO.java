package com.mytijian.mediator.common.DO;

import java.io.Serializable;
import java.util.Date;
/**
 * 任务基本信息
 * @author yuefengyang
 *
 */
public class AgentTaskConfigDO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -488134546204656834L;

	private Integer id;

	private Date gmtCreated;

	private Date gmtModified;
	
	/**
	 * 任务cmd，类似别名
	 */
	private String taskCmd;
	
	/**
	 * 任务名称
	 */
	private String taskName;
	
	/**
	 * 排序
	 */
	private Integer sequence;
	/**
	 * 默认bean name
	 */
	private String defaultTaskBeanName;
	/**
	 * 默认时间
	 */
	private String defaultCrontabExp;
	/**
	 * 默认参数
	 */
	private String defaultTaskParam;

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

	public String getTaskCmd() {
		return taskCmd;
	}

	public void setTaskCmd(String taskCmd) {
		this.taskCmd = taskCmd;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getDefaultTaskBeanName() {
		return defaultTaskBeanName;
	}

	public void setDefaultTaskBeanName(String defaultTaskBeanName) {
		this.defaultTaskBeanName = defaultTaskBeanName;
	}

	public String getDefaultCrontabExp() {
		return defaultCrontabExp;
	}

	public void setDefaultCrontabExp(String defaultCrontabExp) {
		this.defaultCrontabExp = defaultCrontabExp;
	}

	public String getDefaultTaskParam() {
		return defaultTaskParam;
	}

	public void setDefaultTaskParam(String defaultTaskParam) {
		this.defaultTaskParam = defaultTaskParam;
	}
	
}
