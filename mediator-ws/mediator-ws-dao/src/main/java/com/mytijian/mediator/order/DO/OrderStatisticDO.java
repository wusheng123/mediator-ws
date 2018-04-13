package com.mytijian.mediator.order.DO;

import java.io.Serializable;
import java.util.Date;
/**
 * 订单统计DO
 * @author yuefengyang
 *
 */
public class OrderStatisticDO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8728854563427752650L;

	private Integer id;

	private Date gmtCreated;

	private Date gmtModified;

	private Integer hospitalId;

	private Integer taskId;

	/**
	 * 统计项
	 */
	private String statisticsItem;

	/**
	 * 统计参数
	 */
	private String param;

	/**
	 * 结果
	 */
	private String result;

	/**
	 * 格式
	 */
	private String format;

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

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getStatisticsItem() {
		return statisticsItem;
	}

	public void setStatisticsItem(String statisticsItem) {
		this.statisticsItem = statisticsItem;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
