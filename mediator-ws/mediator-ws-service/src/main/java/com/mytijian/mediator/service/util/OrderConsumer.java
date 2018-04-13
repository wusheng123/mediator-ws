package com.mytijian.mediator.service.util;

import javax.annotation.Resource;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mytijian.mediator.api.service.ImmediateOrderService;
import com.mytijian.order.dto.ImmediateOrderDto;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

@Component("orderConsumer")
public class OrderConsumer {

	private Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

	@Value("${mq.host}")
	private String mqHost;

	@Value("${mq.order.exchange}")
	private String exchange;

	@Resource(name = "immediateOrderService")
	private ImmediateOrderService immediateOrderService;

	public void receiveMessage() {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(mqHost);

		try {
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.exchangeDeclare(exchange, ExchangeTypes.FANOUT);
			String queueName = channel.queueDeclare().getQueue();
			channel.queueBind(queueName, exchange, "");

			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(queueName, true, consumer);

			while (true) {

				try {
					QueueingConsumer.Delivery delivery = consumer
							.nextDelivery();
					ImmediateOrderDto orderDto = (ImmediateOrderDto) SerializationUtils
							.deserialize(delivery.getBody());
					// Set<Integer> hospitalSet =
					// commonService.getOnlineHospitals();
					// if (CollectionUtils.isNotEmpty(hospitalSet) &&
					// hospitalSet.contains(orderDto.getHospitalId())) {

					if (orderDto != null) {
						logger.info(
								"order consumer,order id : {},hosp id : {}",
								orderDto.getOrderId(), orderDto.getHospitalId());
						immediateOrderService
								.addOrder(orderDto.getHospitalId(),
										orderDto.getOrderId());
					}

					// }

				} catch (Exception e) {
					logger.error("order consumer error", e);
				}

			}

		} catch (Exception e) {
			logger.error("receive message from " + mqHost + ",exchange: " + exchange + " failed", e);
		}
	}

}
