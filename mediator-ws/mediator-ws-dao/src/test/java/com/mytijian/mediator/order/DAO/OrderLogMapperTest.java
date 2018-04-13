package com.mytijian.mediator.order.DAO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.mytijian.mediator.order.DAO.OrderLogMapper;
import com.mytijian.mediator.order.dto.OrderSyncLog;
import com.mytijian.test.DbUnitTestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
public class OrderLogMapperTest extends DbUnitTestTemplate {

	@Resource(name = "orderLogMapper")
	private OrderLogMapper orderLogMapper;

	@Test
	@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
	@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
	public void test() {

		OrderSyncLog log = new OrderSyncLog();
		log.setOrderId(1);
		log.setErrorCode(11);
		log.setErrorText("success");
		log.setOperator("jdij");
		log.setHospitalId(5);
		orderLogMapper.insertOrderLog(log);

		OrderSyncLog log2 = orderLogMapper.getById(log.getId());
		assertNotNull(log2);
		assertEquals(1, log2.getOrderId().intValue());

		orderLogMapper.delete(log.getId());

		OrderSyncLog log3 = orderLogMapper.getById(log.getId());
		assertNull(log3);
	}
}
