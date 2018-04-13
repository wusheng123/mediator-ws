package com.mytijian.mediator.service.impl;

import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.mytijian.cache.RedisCacheClient;
import com.mytijian.cache.annotation.RedisClient;
import com.mytijian.mediator.api.enums.ConsoleSession;
import com.mytijian.mediator.api.model.cmd.CmdInput;
import com.mytijian.mediator.api.model.cmd.CmdOutput;
import com.mytijian.mediator.api.service.CmdListener;
import com.mytijian.mediator.api.service.CommonService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@Component("defaultCmdListener")
public class CmdListenerImpl implements CmdListener {
	private Logger log = LoggerFactory.getLogger(CmdListenerImpl.class);
	
	@RedisClient(nameSpace = CommonService.REDIS_KEY_SPACE_CMD_QUEUE, timeout = 60 * 60 * 24)
	private RedisCacheClient<LinkedBlockingDeque<CmdInput>> cmdQueuesCache;

	@Resource(name = "amqpTemplate")
	AmqpTemplate amqpTemplate;
	
	@Value("${mq.host}")
	private String mqHost;

	@Value("${mq.exchange}")
	private String exchange;
	
	private ConnectionFactory factory = new ConnectionFactory();
	@Override
	public void addCommand(Integer hospitalId, CmdInput input) throws InterruptedException {
		LinkedBlockingDeque<CmdInput> queue;
		if (cmdQueuesCache.contains(hospitalId)) {
			queue = cmdQueuesCache.get(hospitalId);
		} else {
			queue = new LinkedBlockingDeque<CmdInput>(20);
		}
		queue.put(input);
		cmdQueuesCache.put(hospitalId, queue);
	}

	@Override
	public CmdInput nextCommand(Integer hospitalId) {
		if (cmdQueuesCache.contains(hospitalId)) {
			LinkedBlockingDeque<CmdInput> queue = cmdQueuesCache.get(hospitalId);
			CmdInput cmdInput = queue.poll();
			cmdQueuesCache.put(hospitalId, queue);
			return cmdInput;
		} else {
			return null;
		}
	}

	@Override
	public void onMessage(Integer hospitalId, CmdOutput output) {
		
		try {
			factory.setHost(this.mqHost);
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.exchangeDeclare(exchange, ExchangeTypes.DIRECT, true);
			String queueName = "hospital_"+hospitalId+"_cmd_queue";
			channel.queueDeclare(queueName, true, false, false, null);
			channel.queueBind(queueName, exchange, hospitalId.toString());

			String message = JSON.toJSONString(output);
			
			Map<String , Object> session = output.getSession();
			Object sessionId = session.get(ConsoleSession.SESSIONID);
			
			channel.basicPublish(exchange, sessionId.toString(), null, message.getBytes());
			channel.close();
			connection.close();
		} catch (Exception e) {
			log.error("Send mssage to " + mqHost + ":" + exchange + " failed!", e);
		}
	}

}
