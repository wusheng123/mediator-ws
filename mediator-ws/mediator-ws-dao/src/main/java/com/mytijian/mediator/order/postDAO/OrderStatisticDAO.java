package com.mytijian.mediator.order.postDAO;

import com.mytijian.mediator.order.DO.OrderStatisticDO;
/**
 * 
 * @author yuefengyang
 *
 */
public interface OrderStatisticDAO {
	
	/**
	 * 添加订单统计记录
	 * @param orderStatisticDO
	 */
	void add(OrderStatisticDO orderStatisticDO);
}
