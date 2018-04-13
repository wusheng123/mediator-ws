package com.mytijian.mediator.order.DAO;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.order.dto.OrderSyncLog;


@Repository("orderLogMapper")
public interface OrderLogMapper {
	void insertOrderLog(OrderSyncLog orderLog);

	OrderSyncLog getById(Integer id);

	void delete(@Param(value = "id") Integer id);
}
