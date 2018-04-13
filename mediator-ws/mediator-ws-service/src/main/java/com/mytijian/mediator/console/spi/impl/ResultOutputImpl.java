/*
 * Copyright 2016 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.console.spi.impl;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import com.mytijian.mediator.api.enums.ConsoleSession;
import com.mytijian.mediator.api.model.cmd.CmdOutput;
import com.mytijian.mediator.console.spi.ResultOutput;

/**
 * 类ResultOutputImpl.java的实现描述：TODO 类实现描述 
 * @author liang 2016年9月9日 下午2:59:27
 */
@Component("resultOutput")
public class ResultOutputImpl implements ResultOutput {
	@Resource(name="amqpTemplate")
	private AmqpTemplate amqpTemplate;
	@Override
	public void putResult(CmdOutput output) {
		amqpTemplate.convertAndSend((String)output.getSession().get(ConsoleSession.SESSIONID), output.getOutput());
	}
	
}
