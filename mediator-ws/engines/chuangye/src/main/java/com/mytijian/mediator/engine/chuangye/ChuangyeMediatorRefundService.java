package com.mytijian.mediator.engine.chuangye;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mytijian.mediator.order.base.service.MediatorRefundService;
import com.mytijian.mediator.order.base.service.impl.MediatorOrderServiceImpl;
import com.mytijian.mediator.service.util.Offer2ResourceAPIResolver;
import com.mytijian.order.base.service.OrderForMediatorService;
import com.mytijian.order.model.AccomplishOrder;
import com.mytijian.order.model.Order;
import com.mytijian.order.service.OrderService;

@Service("chuangyeMediatorRefundService")
public class ChuangyeMediatorRefundService implements MediatorRefundService {

	private static Logger logger = LoggerFactory
			.getLogger(MediatorOrderServiceImpl.class);

	@Resource(name = "orderService")
	private OrderService orderService;

	@Resource(name = "orderForMediatorService")
	private OrderForMediatorService orderForMediatorService;

	@Override
	public List<String> orderRefund(List<AccomplishOrder> doneOrderList) {
		if (CollectionUtils.isNotEmpty(doneOrderList)) {
			List<String> orderNumList = new ArrayList<String>();
			for (AccomplishOrder ac : doneOrderList) {
				Order order = orderService.getOrderByOrderNum(ac.getOrderNum());
				if (order != null) {
					this.hosOrderItemFilter(ac, order);
					orderNumList.addAll(orderForMediatorService
							.orderRefund(Arrays
									.asList(Offer2ResourceAPIResolver
											.resolveAccomplishOrder(ac))));
				} else {
					logger.warn("ORDER_NOT_EXIST.ordernum : {}",
							ac.getOrderNum());
					orderNumList.add(ac.getOrderNum());
				}
			}
			return orderNumList;
		} else {
			return Collections.emptyList();
		}
	}

	/*
	 * 当传入的item中有itemId为 “全退” 字样的，返回空set，表示此订单全部退款
	 * 
	 * @see
	 * com.mytijian.order.service.impl.RefundServiceImpl#getHosOrderItemMap(
	 * com.mytijian.order.model.AccomplishOrder)
	 */
	private void hosOrderItemFilter(AccomplishOrder accomplishOrder, Order order) {
		if (accomplishOrder.getExamItemList() == null) {
			accomplishOrder.setExamItemList(Collections.emptyList());
		}

		for (com.mytijian.resource.model.ExamItem obj : accomplishOrder.getExamItemList()) {
			if ("全退".equals(obj.getHisItemId())) {
				accomplishOrder.setExamItemList(Collections.emptyList());
				return;
			}
		}

		orderForMediatorService.getHisItemIds(order.getOrderNum()).entrySet()
				.forEach(entry -> {
					if (entry.getKey().startsWith("C")) {
						com.mytijian.resource.model.ExamItem item = new com.mytijian.resource.model.ExamItem();
						item.setHisItemId(entry.getKey());
						item.setPrice(entry.getValue());
						accomplishOrder.getExamItemList().add(item);
					}
				});

	}

}
