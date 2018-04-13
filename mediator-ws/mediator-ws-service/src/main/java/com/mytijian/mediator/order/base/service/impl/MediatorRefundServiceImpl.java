package com.mytijian.mediator.order.base.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.mytijian.mediator.order.base.service.MediatorRefundService;
import com.mytijian.mediator.service.util.Offer2ResourceAPIResolver;
import com.mytijian.order.base.service.OrderForMediatorService;
import com.mytijian.order.model.AccomplishOrder;
import com.mytijian.order.model.AccomplishOrderResult;

@Service("mediatorRefundService")
public class MediatorRefundServiceImpl implements MediatorRefundService {

	@Resource(name = "orderForMediatorService")
	private OrderForMediatorService orderForMediatorService;

	@Override
	public List<String> orderRefund(List<AccomplishOrder> doneOrderList) {
		List<AccomplishOrderResult> accomplishOrderResults = Offer2ResourceAPIResolver
				.resolveAccomplishOrderList(doneOrderList);
		if (CollectionUtils.isEmpty(accomplishOrderResults)) {
			return new ArrayList<String>();
		}
		return orderForMediatorService.orderRefund(accomplishOrderResults);
	}

}
