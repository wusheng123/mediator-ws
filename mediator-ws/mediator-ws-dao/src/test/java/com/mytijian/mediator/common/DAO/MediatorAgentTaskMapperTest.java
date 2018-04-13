/*
 * Copyright 2016 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.common.DAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.mytijian.mediator.common.DO.MediatorAgentTaskDO;
import com.mytijian.mediator.common.param.ListTaskHospitalDaoQuery;
import com.mytijian.test.DbUnitTestTemplate;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * 类MediatorAgentTaskMapperTest.java的实现描述：TODO 类实现描述 
 * @author Administrator 2016年10月24日 上午11:03:31
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class MediatorAgentTaskMapperTest extends DbUnitTestTemplate {
	@Resource(name = "mediatorAgentTaskMapper")
	private MediatorAgentTaskMapper mediatorAgentTaskMapper;
	@Test
	public void insertTest(){
		MediatorAgentTaskDO task = new MediatorAgentTaskDO();
		task.setHospitalId(1);
		task.setTaskCmd("sync");
		task.setProvinceId(320000);
		mediatorAgentTaskMapper.insert(task);
		TestCase.assertEquals(true, true);
	}
	@Test
	public void selectMediatorAgentTaskDOByHospitalIdTest(){
		List<MediatorAgentTaskDO> list = mediatorAgentTaskMapper.selectMediatorAgentTaskDOByHospitalId(1);
		TestCase.assertEquals(true, list.size()>0);
	}
	@Test
	public void updateInit(){
		mediatorAgentTaskMapper.updateInit(1,1);
		TestCase.assertEquals(true, true);
	}
	
	@Test
	public void testGetAllTasks(){
		List<MediatorAgentTaskDO> list = mediatorAgentTaskMapper
				.selectAllTasks();
		
		Assert.assertEquals(1, list.size());
	}
	
	@Test
	public void testCountByHosp(){
		Integer count = mediatorAgentTaskMapper.countByHospId(1);
		Assert.assertEquals(1, count.intValue());
	}
	
	@Test
	public void testSelectByHospitalId(){
		List<MediatorAgentTaskDO> list =  mediatorAgentTaskMapper.selectByHospitalId(1);
		Assert.assertEquals(1, list.size());
	}
	
	@Test
	public void testSelectHospitalIds(){
		List<Integer> list = mediatorAgentTaskMapper.selectHospitalIds();
		Assert.assertEquals(1, list.size());
	}
	
	@Test
	public void testUpdateTaskParam(){
		mediatorAgentTaskMapper.updateTaskParam(1, "1", "taskparam");
		List<MediatorAgentTaskDO> list =  mediatorAgentTaskMapper.selectByHospitalId(1);
		Assert.assertEquals(1, list.size());
		
		Assert.assertEquals("taskparam", list.get(0).getTaskParams());
		
	}
	
	@Test
	public void testUpdateTaskCrontab(){
		mediatorAgentTaskMapper.updateTaskCrontabExp(1, "1", "cron");
		List<MediatorAgentTaskDO> list =  mediatorAgentTaskMapper.selectByHospitalId(1);
		Assert.assertEquals(1, list.size());
		
		Assert.assertEquals("cron", list.get(0).getCronExpression());
		
	}
	
	@Test
	public void selectTaskHospitals(){
		ListTaskHospitalDaoQuery listTaskHospitalDaoQuery = new ListTaskHospitalDaoQuery();
		listTaskHospitalDaoQuery.setProvinceIds(Arrays.asList(120000,1));
		List<Integer> list =  mediatorAgentTaskMapper.selectTaskHospitals(listTaskHospitalDaoQuery);
		Assert.assertEquals(1, list.size());
	}
	
	@Test
	public void countTaskHospitals(){
		ListTaskHospitalDaoQuery listTaskHospitalDaoQuery = new ListTaskHospitalDaoQuery();
		// listTaskHospitalDaoQuery.setProvinceIds(Arrays.asList(120000,1));
		listTaskHospitalDaoQuery.setProvinceIds(new ArrayList<>());
		int count =  mediatorAgentTaskMapper.countTaskHospitals(listTaskHospitalDaoQuery);
		Assert.assertEquals(1, count);
	}
}
