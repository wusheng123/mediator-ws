/*
 * Copyright 2016 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.order.DAO;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.mytijian.mediator.order.DAO.MediatorOrderPrintHistMapper;
import com.mytijian.mediator.order.model.MediatorOrderPrintHist;
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
public class MediatorOrderPrintHisMapperTest extends DbUnitTestTemplate {
	
	@Resource(name = "mediatorOrderPrintHistMapper")
	private MediatorOrderPrintHistMapper mediatorOrderPrintHistMapper;
	
	@Test
	public void insertBatchTest(){
		MediatorOrderPrintHist mediatorOrderPrintHis = new MediatorOrderPrintHist();
		mediatorOrderPrintHis.setHospitalId(1);
		mediatorOrderPrintHis.setOrderId(1);
		mediatorOrderPrintHis.setOrderNum("10002014455201612141041");
		mediatorOrderPrintHistMapper.insert(mediatorOrderPrintHis);
	}
	
	@Test
	public void getMediatorHospitalOrderPrintByPage(){
		Page page = new Page(1,5);
		//hospitalId, printStatus, page
		List<MediatorOrderPrintHist> mediatorOrderPrintHis = mediatorOrderPrintHistMapper.getMediatorHospitalOrderPrintByPage(100, 0, page);
		Assert.assertEquals(2, mediatorOrderPrintHis.size());
	}
	
	@Test
	public void updateOrderPrintByOrderNums(){
		List<String> orderIds = Arrays.asList("011012000201612141512","011012000201612141513","011012000201612141514");
		mediatorOrderPrintHistMapper.updateOrderPrintByOrderNums(orderIds, 1);
	}
	
	@Test
	public void updateOrderPrintByOrderIds(){
		List<Integer> orderIds = Arrays.asList(1,2,3);
		mediatorOrderPrintHistMapper.updateOrderPrintByOrderIds(orderIds, 1);
	}
	
	@Test
	public void updateOrderPrintByOrderNum(){
		mediatorOrderPrintHistMapper.updateOrderPrintByOrderNum("011012000201612141512", 1);
	}
	
	@Test
	public void getMediatorOrderPrintHisByOrderNum(){
		mediatorOrderPrintHistMapper.getMediatorOrderPrintHisByOrderNum("011012000201612141512");
	}
	
}
