package com.mytijian.mediator.order.DAO;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.mytijian.mediator.order.DO.OrderStatisticDO;
import com.mytijian.test.DbUnitTestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
public class OrderStatisticMapperTest extends DbUnitTestTemplate {

	@Resource(name = "orderStatisticMapper")
	private OrderStatisticMapper orderStatisticMapper;

	@Test
	@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
	@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
	public void testInsert() {

		OrderStatisticDO orderStatisticDO = new OrderStatisticDO();
		orderStatisticMapper.insert(orderStatisticDO);
		
	}
}
