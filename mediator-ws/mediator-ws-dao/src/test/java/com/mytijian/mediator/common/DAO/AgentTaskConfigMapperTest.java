package com.mytijian.mediator.common.DAO;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.mytijian.mediator.common.DO.AgentTaskConfigDO;
import com.mytijian.test.DbUnitTestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class AgentTaskConfigMapperTest extends DbUnitTestTemplate {

	@Resource(name = "agentTaskConfigMapper")
	private AgentTaskConfigMapper agentTaskConfigMapper;

	@Test
	public void testGetList() {

		List<AgentTaskConfigDO> list = agentTaskConfigMapper
				.selectTaskConfigList();
		Assert.assertEquals(1, list.size());
	}
	
	@Test
	public void testGetByTaskCmd() {
		AgentTaskConfigDO task = agentTaskConfigMapper
				.selectTaskConfigByTaskCmd("order");
		Assert.assertNotNull(task);
	}
	
}
