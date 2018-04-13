package com.mytijian.mediator.report.DAO;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.mytijian.mediator.api.dto.SyncExamReportDto;
import com.mytijian.mediator.report.params.QuerySyncReportParam;
import com.mytijian.test.DbUnitTestTemplate;

import junit.framework.TestCase;
@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class SyncExamreportMapperTest extends DbUnitTestTemplate{
	@Resource(name = "syncExamReportMapper")
	private SyncExamReportMapper syncExamReportMapper;
	
	@Test
	public void insert (){
		SyncExamReportDto syncExamReportDto = new SyncExamReportDto();
		syncExamReportDto.setCompanyName("12");
		syncExamReportDto.setExamTime("12");
		syncExamReportMapper.insert(syncExamReportDto);
		syncExamReportMapper.insert(syncExamReportDto);
		TestCase.assertEquals(true, 3==syncExamReportDto.getId());
	}
	@Test
	public void selectByPrimaryKey (){
		SyncExamReportDto syncExamReportDto = syncExamReportMapper.selectByPrimaryKey(1L);
		TestCase.assertEquals(true, syncExamReportDto.getHospitalId() == 2);
		
	}
	
	@Test
	public void getSyncExamReports (){
		List<SyncExamReportDto> syncExamReportDtos = syncExamReportMapper.getSyncExamReports(null, "done", 0, 2);
		TestCase.assertEquals(1, syncExamReportDtos.size());
	}
	
	@Test
	public void selectSyncExamReportIdList(){
		QuerySyncReportParam query = new QuerySyncReportParam();
		query.setHospitalId(2);
		query.setState("done");
		List<Long> ids = syncExamReportMapper.selectSyncExamReportIdList(query, 0L, 1);
		TestCase.assertEquals(false, ids.isEmpty());
	}
	
	@Test
	public void delDoneSyncExamReport(){
		syncExamReportMapper.delDoneSyncExamReport(1L, "1212", 2);
		SyncExamReportDto syncExamReportDto = syncExamReportMapper.selectByPrimaryKey(1L);
		TestCase.assertEquals(null, syncExamReportDto);
	}
	
	@Test
	public void deleteBatchByIds(){
		SyncExamReportDto syncExamReportDto = syncExamReportMapper.selectByPrimaryKey(1L);
		syncExamReportMapper.deleteBatchByIds(Arrays.asList(1l,2l));
		syncExamReportDto = syncExamReportMapper.selectByPrimaryKey(1L);
		TestCase.assertEquals(null, syncExamReportDto);
	}
	
	@Test
	public void updateStatusBatchByIds(){
		syncExamReportMapper.updateStatusBatchByIds(Arrays.asList(1l,2l), "SUCCESS");
		SyncExamReportDto syncExamReportDto = syncExamReportMapper.selectByPrimaryKey(1L);
		TestCase.assertEquals("SUCCESS", syncExamReportDto.getState());
	}
}
