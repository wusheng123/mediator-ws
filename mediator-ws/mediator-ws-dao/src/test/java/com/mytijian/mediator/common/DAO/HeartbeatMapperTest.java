package com.mytijian.mediator.common.DAO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.mytijian.mediator.api.model.HeartbeatLog;
import com.mytijian.mediator.common.DAO.HeartbeatLogMapper;
import com.mytijian.test.DbUnitTestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class HeartbeatMapperTest extends DbUnitTestTemplate {

	@Resource(name = "heartbeatLogMapper")
	private HeartbeatLogMapper heartbeatLogMapper;

	@Test
	public void testGetByHospitalId() {

		HeartbeatLog log = heartbeatLogMapper.getByHospitalId(1);

		assertNotNull(log);
		assertEquals("11", log.getPeriod());
	}

}
