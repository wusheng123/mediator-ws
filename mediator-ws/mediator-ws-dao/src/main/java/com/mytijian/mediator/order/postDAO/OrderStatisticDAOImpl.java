package com.mytijian.mediator.order.postDAO;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mytijian.mediator.order.DAO.OrderStatisticMapper;
import com.mytijian.mediator.order.DO.OrderStatisticDO;

@Service("orderStatisticDAO")
public class OrderStatisticDAOImpl implements OrderStatisticDAO {

	@Resource(name = "orderStatisticMapper")
	private OrderStatisticMapper orderStatisticMapper;

	@Override
	public void add(OrderStatisticDO orderStatisticDO) {
		orderStatisticMapper.insert(orderStatisticDO);
	}

}
