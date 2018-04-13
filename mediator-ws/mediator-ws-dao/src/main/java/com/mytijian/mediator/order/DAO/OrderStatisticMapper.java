package com.mytijian.mediator.order.DAO;

import org.springframework.stereotype.Repository;

import com.mytijian.mediator.order.DO.OrderStatisticDO;
/**
 * 订单统计mapper
 * @author yuefengyang
 *
 */
@Repository("orderStatisticMapper")
public interface OrderStatisticMapper {
	/**
	 * insert
	 * @param orderStatisticDO
	 */
	void insert(OrderStatisticDO orderStatisticDO);
}
