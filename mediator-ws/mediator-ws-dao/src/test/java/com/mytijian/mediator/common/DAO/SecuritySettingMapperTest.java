package com.mytijian.mediator.common.DAO;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.mytijian.mediator.common.DO.SecuritySettingDO;
import com.mytijian.test.DbUnitTestTemplate;

import junit.framework.Assert;


@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class SecuritySettingMapperTest extends DbUnitTestTemplate {
	
	@Resource(name = "securitySettingMapper")
	private SecuritySettingMapper securitySettingMapper;
	
	@Test
	public void selectTest(){
		List<SecuritySettingDO> resultList = securitySettingMapper.selectAllSetting();
		Assert.assertEquals(1, resultList.size());
	}
}
