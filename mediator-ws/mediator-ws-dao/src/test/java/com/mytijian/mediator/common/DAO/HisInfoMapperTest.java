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
import com.mytijian.mediator.api.model.HisInfo;
import com.mytijian.mediator.common.DAO.HisInfoMapper;
import com.mytijian.test.DbUnitTestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class HisInfoMapperTest extends DbUnitTestTemplate {

	@Resource(name = "hisInfoMapper")
	private HisInfoMapper hisInfoMapper;

	@Test
	public void testGetByHospitalId() {

		HisInfo log = hisInfoMapper.selectByHospitalId(1);

		assertNotNull(log);
		assertEquals("lianzhong", log.getEngineName());
	}

}
