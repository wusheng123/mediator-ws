/*
 * Copyright 2016 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.order.DAO;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Lists;
import com.mytijian.mediator.order.DAO.MediatorOrderExportHisMapper;
import com.mytijian.mediator.order.model.MediatorOrderExportHis;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.test.DbUnitTestTemplate;

import junit.framework.Assert;

/**
 * 类MediatorAgentTaskMapperTest.java的实现描述：TODO 类实现描述 
 * @author Administrator 2016年10月24日 上午11:03:31
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class MediatorOrderExportHisMapperTest extends DbUnitTestTemplate {
	
	@Resource(name = "mediatorOrderExportHisMapper")
	private MediatorOrderExportHisMapper mediatorOrderExportHisMapper;
	
	@Test
	public void insertBatchTest(){
		List<MediatorOrderExportHis> mediatorOrderExportHiss = Lists.newArrayList();
		for (int i = 10; i < 20; i++) {
			MediatorOrderExportHis mediatorOrderExportHis = new MediatorOrderExportHis();
			mediatorOrderExportHis.setHospitalId(i);
			mediatorOrderExportHis.setOrderId(i);
			mediatorOrderExportHis.setExportType(1);
			mediatorOrderExportHis.setSequence(5);
			mediatorOrderExportHiss.add(mediatorOrderExportHis);
		}
		mediatorOrderExportHisMapper.insertBatch(mediatorOrderExportHiss);
	}
	
	@Test
	public void getMediatorHospitalOrderExportByPage(){
		Page page = new Page(1,5);
		List<MediatorOrderExportHis> m = mediatorOrderExportHisMapper.getMediatorHospitalOrderExportByPage(100, null, 0, page);
		Assert.assertEquals(3, m.size());
	}
	
	@Test
	public void updateOrderExportByOrderIds(){
		List<Integer> orderIds = Lists.newArrayList();
		orderIds.add(1);
		orderIds.add(2);
		orderIds.add(3);
		orderIds.add(4);
		mediatorOrderExportHisMapper.updateOrderExportByOrderIds(Lists.newArrayList(), 2);;
	}
	
	@Test
	public void updateOrderExportByOrderId(){
		mediatorOrderExportHisMapper.updateOrderExportByOrderId(1, 2);
	}
	
	@Test
	public void insertTest() {
		MediatorOrderExportHis mediatorOrderExportHis = new MediatorOrderExportHis();
		mediatorOrderExportHis.setHospitalId(1);
		mediatorOrderExportHis.setOrderId(2);
		mediatorOrderExportHis.setExportType(1);
		mediatorOrderExportHis.setSequence(5);
		int index = mediatorOrderExportHisMapper.insert(mediatorOrderExportHis);
		Assert.assertEquals(1, index);
	}
	
	@Test
	public void getMediatorOrderExportHisCountByOrderId() {
		int totalCount = mediatorOrderExportHisMapper.getMediatorOrderExportHisCountByOrderId(88);
		Assert.assertEquals(1, totalCount);
	}
	
	@Test
	public void getMediatorOrderExportHisByOrderId() {
		MediatorOrderExportHis mediatorOrderExportHis = mediatorOrderExportHisMapper.getMediatorOrderExportHisByOrderId(88);
		Assert.assertEquals(1, mediatorOrderExportHis.getIsExport().intValue());
	}
}
