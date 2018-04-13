package com.mytijian.mediator.common.DAO;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.mytijian.mediator.api.model.UpgradeMessage;
import com.mytijian.mediator.common.DAO.UpgradeMessageMapper;
import com.mytijian.test.DbUnitTestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class UpgradeMessageMapperTest extends DbUnitTestTemplate {

	@Resource(name = "upgradeMessageMapper")
	private UpgradeMessageMapper upgradeMessageMapper;

	@Test
	public void testGetById() {
		UpgradeMessage log = upgradeMessageMapper.selectById(1);
		Assert.notNull(log);
	}

	@Test
	public void testGetByHospId() {
		UpgradeMessage log = upgradeMessageMapper.selectByHospitalId(1);
		Assert.notNull(log);
	}

	@Test
	public void testUpdate() {
		upgradeMessageMapper.updateStatus(1, 2);

		UpgradeMessage log = upgradeMessageMapper.selectById(1);

		assertEquals(2, log.getStatus().intValue());
	}
	
	@Test
	public void testInsert() {
		UpgradeMessage message = new UpgradeMessage();
		message.setHospitalId(6);
		message.setCurrentVersion("1.0");
		message.setNewVersion("1.1");
		message.setPackFile("/Users/xx");
		message.setStatus(0);
		upgradeMessageMapper.insert(message);

		UpgradeMessage log = upgradeMessageMapper.selectById(message.getId());
		assertEquals(0, log.getStatus().intValue());
	}
	
	@Test
	public void testSelectOne() {
		UpgradeMessage message = upgradeMessageMapper.selectOneOrderByTime(1);
		Assert.notNull(message);
	}

}
