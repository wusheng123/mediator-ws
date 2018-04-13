/*
 * Copyright 2017 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.order.DAO.noruntest;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mytijian.mediator.order.DAO.MediatorOrderExportHisMapper;
import com.mytijian.mediator.order.DAO.OrderLogMapper;
import com.mytijian.mediator.order.dto.OrderSyncLog;
import com.mytijian.mediator.order.model.MediatorOrderExportHis;

import junit.framework.TestCase;

/**
 * 类OrderTest.java的实现描述：TODO 类实现描述 
 * @author liangxing 2017年2月13日 下午8:14:52
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager="transactionManager",defaultRollback=true)
@Transactional
@ContextConfiguration(locations = "classpath:applicationContext-noruntest.xml")

public class OrderLogMapperNoRunTest  {
	@Resource
	private OrderLogMapper orderLogMapper;
	@Resource
	private MediatorOrderExportHisMapper mediatorOrderExportHisMapper;
	@Test
	public void getById(){
		OrderSyncLog orderSyncLog = orderLogMapper.getById(1);
		TestCase.assertEquals(orderSyncLog == null , false);
	}
	@Test
	public void insertBatch(){
		List<MediatorOrderExportHis> mediatorOrderExportHis = Lists.newArrayList();
		MediatorOrderExportHis orderExportHis = new MediatorOrderExportHis();
		orderExportHis.setOrderId(1);
		orderExportHis.setHospitalId(1);
		orderExportHis.setExportType(1);
		orderExportHis.setSequence(1);
		orderExportHis.setOrderNum("1");
		orderExportHis.setExamDate(new Date());
		orderExportHis.setAccountId(1);	
		orderExportHis.setName("1");
		for(int i = 0 ; i < 1000 ; i ++){
			mediatorOrderExportHis.add(orderExportHis);
		}
		
		Date now = new Date();
		mediatorOrderExportHisMapper.insertBatch(mediatorOrderExportHis);
		Date one = new Date();
		System.out.println(one.getTime()-now.getTime());
		
	}
	@Test
	public void insert(){
		MediatorOrderExportHis orderExportHis = new MediatorOrderExportHis();
		orderExportHis.setOrderId(1);
		orderExportHis.setHospitalId(1);
		orderExportHis.setExportType(1);
		orderExportHis.setSequence(1);
		orderExportHis.setOrderNum("1");
		orderExportHis.setExamDate(new Date());
		orderExportHis.setAccountId(1);	
		orderExportHis.setName("1");
		Date now = new Date();
		for(int i = 0 ; i < 1000 ; i ++){
			mediatorOrderExportHisMapper.insert(orderExportHis);
		}
		Date one = new Date();
		System.out.println(one.getTime()-now.getTime());
		
	}
}
