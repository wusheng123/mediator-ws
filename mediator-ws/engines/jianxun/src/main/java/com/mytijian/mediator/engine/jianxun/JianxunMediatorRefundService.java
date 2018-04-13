package com.mytijian.mediator.engine.jianxun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mytijian.mediator.order.base.service.MediatorRefundService;
import com.mytijian.mediator.order.base.service.impl.MediatorOrderServiceImpl;
import com.mytijian.order.base.service.OrderForMediatorService;
import com.mytijian.order.base.service.dto.UpdateMediatorMongoDTO;
import com.mytijian.order.model.AccomplishOrder;
import com.mytijian.order.model.MongoOrder;
import com.mytijian.order.model.Order;
import com.mytijian.order.service.OrderService;

@Service("jianxunMediatorRefundService")
public class JianxunMediatorRefundService implements MediatorRefundService {
	private static Logger logger = LoggerFactory
			.getLogger(MediatorOrderServiceImpl.class);

	@Resource(name = "orderService")
	private OrderService orderService;

	@Resource(name = "orderForMediatorService")
	private OrderForMediatorService orderForMediatorService;

	@Override
	public List<String> orderRefund(List<AccomplishOrder> doneOrderList) {
		if (doneOrderList != null && !doneOrderList.isEmpty()) {
			List<String> orderNumList = new ArrayList<String>();
			for (AccomplishOrder ac : doneOrderList) {
				this.updateHisOrderPrice(ac);
				orderNumList.add(ac.getOrderNum());
			}
			return orderNumList;
		} else {
			return Collections.emptyList();
		}
	}

	// 更新mongoOrder的hisOrderPrice
	private void updateHisOrderPrice(AccomplishOrder ac) {
		if (ac.getDynamicAttributes() != null
				&& ac.getDynamicAttributes().get("hisOrderPrice") != null) {
			Order order = orderService.getOrderByOrderNum(ac
					.getOrderNum());
			if (order != null) {
				this.updateHisOrderPrice((Double) ac
						.getDynamicAttributes().get("hisOrderPrice"),
						order.getId());
			} else {
				logger.warn("ORDER_NOT_EXIST.ordernum : {}",
						ac.getOrderNum());
			}
		} else {
			logger.warn("hisOrderPrice is null");
		}
	}

	private void updateHisOrderPrice(Double hisOrderPrice, Integer orderId) {
		UpdateMediatorMongoDTO dto = new UpdateMediatorMongoDTO();
		MongoOrder where = new MongoOrder();
		where.setId(orderId);

		MongoOrder set = new MongoOrder();
		set.setHisOrderPrice(hisOrderPrice);

		dto.setWhere(where);
		dto.setSet(set);
		orderForMediatorService.updateMediatorMongo(dto);
	}

}
