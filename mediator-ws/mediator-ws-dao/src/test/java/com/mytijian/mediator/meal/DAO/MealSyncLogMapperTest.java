package com.mytijian.mediator.meal.DAO;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.mytijian.mediator.api.enums.MealSyncStatus;
import com.mytijian.mediator.meal.DAO.MealSyncLogMapper;
import com.mytijian.mediator.meal.model.MealSyncLog;
import com.mytijian.test.DbUnitTestTemplate;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class MealSyncLogMapperTest extends DbUnitTestTemplate {

	@Resource(name = "mealSyncLogMapper")
	private MealSyncLogMapper mealSyncLogMapper;

	@Test
	public void testSelectByStatus() {
		List<MealSyncLog> list = mealSyncLogMapper
				.selectByStatus(MealSyncStatus.NotComplete.getCode());

		Assert.assertEquals(2, list.size());
	}
	
	@Test
	public void testSelectByStatusAndHosp(){
		List<MealSyncLog> list = mealSyncLogMapper.selectByStatusAndHospital(
				MealSyncStatus.NotComplete.getCode(), 1);
		
		Assert.assertEquals(2, list.size());
	}

	@Test
	public void testInsert() {
		MealSyncLog log = new MealSyncLog();
		log.setMealId(333);
		log.setHospitalId(1);
		log.setStatus(MealSyncStatus.NotComplete.getCode());
		int i = mealSyncLogMapper.insertMealSyncLog(log);
		Assert.assertEquals(1, i);
	}

	@Test
	public void testInsertList() {

		MealSyncLog log = new MealSyncLog();
		log.setMealId(444);
		log.setHospitalId(1);
		log.setStatus(MealSyncStatus.NotComplete.getCode());

		MealSyncLog log2 = new MealSyncLog();
		log2.setMealId(555);
		log2.setHospitalId(1);
		log2.setStatus(MealSyncStatus.NotComplete.getCode());

		List<MealSyncLog> list = new ArrayList<MealSyncLog>();
		list.add(log);
		list.add(log2);
		int i = mealSyncLogMapper.insertMealSyncLogList(list);
		Assert.assertEquals(2, i);
	}

	@Test
	public void testSelectByMealId() {
		List<MealSyncLog> list = mealSyncLogMapper.selectByMealId(11);
		Assert.assertEquals(1, list.size());
		Assert.assertEquals("meal11", list.get(0).getHisMealId());

	}

	@Test
	public void testUpdateHisMealId() {
		int i = mealSyncLogMapper.updateHisMealId(11, null, 1);
		Assert.assertEquals(1, i);

		List<MealSyncLog> list = mealSyncLogMapper.selectByMealId(11);
		Assert.assertEquals(1, list.size());
		Assert.assertEquals(null, list.get(0).getHisMealId());
		Assert.assertEquals(1, list.get(0).getStatus().intValue());

	}

	@Test
	public void testUpdateMealSyncLog() {
		int i = mealSyncLogMapper.updateMealSyncLog(11, 890, 0,"11");
		Assert.assertEquals(1, i);

		List<MealSyncLog> list = mealSyncLogMapper.selectByMealId(11);
		Assert.assertEquals(1, list.size());
		Assert.assertEquals(890, list.get(0).getCompanyId().intValue());
		Assert.assertEquals(0, list.get(0).getStatus().intValue());
	}

}
