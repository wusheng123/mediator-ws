/*
 * Copyright 2017 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.common.DAO;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.mytijian.mediator.common.model.GraySetting;
import com.mytijian.test.DbUnitTestTemplate;

import junit.framework.TestCase;

/**
 * 类GraySettingMapperTest.java的实现描述：TODO 类实现描述 
 * @author liangxing 2017年3月2日 下午5:00:53
 */

@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class GraySettingMapperTest extends DbUnitTestTemplate {
	@Resource(name="graySettingMapper")
	private GraySettingMapper graySettingMapper;
	@Test
	public void selectByHospitalIdTest(){
		List<GraySetting> list = graySettingMapper.selectByHospitalId(1);
		TestCase.assertEquals(true, list!=null && !list.isEmpty());
	}
}
