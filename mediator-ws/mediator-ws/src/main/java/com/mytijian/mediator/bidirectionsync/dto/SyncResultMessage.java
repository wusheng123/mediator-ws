package com.mytijian.mediator.bidirectionsync.dto;

import java.util.Map;

/**
 *描述:请添加一段类名描述
 *日期:2017年12月18日
 *时间:下午4:16:13
 *@author huangwei
*/
public class SyncResultMessage {
	
	private String resultCode;
	
	private String resultMessage;
	
	private Map<String,Object> dynamicAttribute;

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public Map<String, Object> getDynamicAttribute() {
		return dynamicAttribute;
	}

	public void setDynamicAttribute(Map<String, Object> dynamicAttribute) {
		this.dynamicAttribute = dynamicAttribute;
	}
	
}
