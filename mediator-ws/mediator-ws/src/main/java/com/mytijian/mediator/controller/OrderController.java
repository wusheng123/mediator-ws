package com.mytijian.mediator.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.mytijian.order.base.mongo.MongoOrderWriteService;
import com.mytijian.order.dto.ExportOrderDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mytijian.mediator.api.service.ImmediateOrderService;
import com.mytijian.mediator.order.enums.OrderExportState;
import com.mytijian.order.model.Order;
import com.mytijian.order.service.OrderService;

@RestController
public class OrderController {

	private Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Resource(name = "immediateOrderService")
	private ImmediateOrderService immediateOrderService;
	
	@Resource(name = "orderService")
	private OrderService orderService;

	@Resource(name = "mongoOrderWriteService")
	private MongoOrderWriteService mongoOrderWriteService;

	@RequestMapping(value = "/exportImmediately/{orderId}", method = RequestMethod.POST, headers = "Accept=application/json")
	public String addOrder(@PathVariable("orderId") Integer orderId, @RequestBody Integer hospitalId) {

		try {

			if (orderId != null && hospitalId != null) {
				immediateOrderService.addOrder(hospitalId, orderId);
				logger.info("add order to export queue, orderid is {},hospitalId is {}", orderId, hospitalId);
			}
		} catch (InterruptedException e) {
			logger.error("add order to export queue error,orderId is " + orderId + ",hospitalId is " + hospitalId, e);
			return "false";
		}
		return "success";
	}
	
	// 解除告警订单
	@RequestMapping(value = "/removeAlarm/{orderNum}", method = RequestMethod.POST, headers = "Accept=application/json")
	public String removeAlarm(@PathVariable("orderNum") String orderNum) {

		String result = "success";
		if (StringUtils.isNotBlank(orderNum)) {
			Order order = orderService.getOrderByOrderNum(orderNum);
			if (order != null) {
				ExportOrderDTO exportOrderDTO = new ExportOrderDTO();
				exportOrderDTO.setOrderNum(orderNum);
				exportOrderDTO.setExportState(OrderExportState.RemoveAlarm.getCode());
				exportOrderDTO.setExportMsg("");
				mongoOrderWriteService.updateMongoExportOrder(exportOrderDTO);
			} else {
				result = "order not found";
			}
		} else {
			result = "ordernum is blank";
		}

		return result;
	}

}
