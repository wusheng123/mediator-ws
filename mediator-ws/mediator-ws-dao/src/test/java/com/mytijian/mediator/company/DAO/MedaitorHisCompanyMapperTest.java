/*
 * Copyright 2016 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.company.DAO;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Lists;
import com.mytijian.mediator.company.DAO.MediatorHisCompanyMapper;
import com.mytijian.mediator.company.DO.MediatorHisCompanyDO;
import com.mytijian.mediator.company.constants.Creator;
import com.mytijian.mediator.company.constants.MediatorHisCompanyStatus;
import com.mytijian.mediator.company.param.MediatorHisCompanyDAOQueryParam;
import com.mytijian.mediator.company.param.MediatorHisCompanyDAOUpdateParam;
import com.mytijian.test.DbUnitTestTemplate;

import junit.framework.TestCase;

/**
 * 类MedaitorHisCompanyMapperTest.java的实现描述：TODO 类实现描述 
 * @author Administrator 2016年11月3日 下午4:33:13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class MedaitorHisCompanyMapperTest  extends DbUnitTestTemplate {
	@Resource(name="mediatorHisCompanyMapper")
	private MediatorHisCompanyMapper mediatorHisCompanyMapper;
	@Test
	public void insertHisCompanyTest(){
		MediatorHisCompanyDO mediatorHisCompanyDO = new MediatorHisCompanyDO();
		mediatorHisCompanyDO.setCreator(Creator.HIS.name());
		mediatorHisCompanyDO.setHisCompanyCode("1");
		mediatorHisCompanyDO.setHisCompanyName("test");
		mediatorHisCompanyDO.setHisCompanyStatus(MediatorHisCompanyStatus.SUCCESS.name());
		mediatorHisCompanyDO.setMyCompanyId(1);
		int num = mediatorHisCompanyMapper.insertHisCompany(mediatorHisCompanyDO);
		TestCase.assertEquals(1, num);
	}
	@Test
	public void selectHisCompanyListTest(){
		MediatorHisCompanyDAOQueryParam mediatorHisCompanyDAOQueryParam = new MediatorHisCompanyDAOQueryParam();
		List<MediatorHisCompanyDO> mediatorHisCompanyDOList = mediatorHisCompanyMapper.selectHisCompanyList(mediatorHisCompanyDAOQueryParam);
		TestCase.assertEquals(1, mediatorHisCompanyDOList.size());
	}
	
	@Test
	public void updateHisCompanyTest(){
		MediatorHisCompanyDAOUpdateParam mediatorHisCompanyDAOUpdateParam = new MediatorHisCompanyDAOUpdateParam();
		//mediatorHisCompanyDAOUpdateParam.setIds(Lists.newArrayList(1));
		mediatorHisCompanyDAOUpdateParam.setHospitalId(1);
		mediatorHisCompanyDAOUpdateParam.setToHisCompanyName("nihao");
		int n = mediatorHisCompanyMapper.updateHisCompany(mediatorHisCompanyDAOUpdateParam);
		TestCase.assertEquals(1, n);
	}
	@Test
	public void deleteHisCompanyTest(){
		int n = mediatorHisCompanyMapper.deleteHisCompany(Lists.newArrayList(1));
		TestCase.assertEquals(1, n);
	}
}
