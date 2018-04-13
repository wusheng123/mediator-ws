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
import com.mytijian.mediator.common.DO.GenericPEServiceTaskDO;
import com.mytijian.test.DbUnitTestTemplate;

/**
 *描述:请添加一段类名描述
 *日期:2017年12月20日
 *时间:下午3:23:01
 *@author huangwei
*/

@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class GenericPEServiceTaskMapperTest extends DbUnitTestTemplate{
	
	@Resource(name = "genericPEServiceTaskMapper")
	private GenericPEServiceTaskMapper genericPEServiceTaskMapper;
	
	@Test
	public void testGetList() {
		List<GenericPEServiceTaskDO> resultList = genericPEServiceTaskMapper.selectTaskConfigByHospitalId(147);
		Assert.assertEquals(1, resultList.size());
	}
}
