package com.mytijian.mediator.common.inner.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.mytijian.mediator.common.DAO.GenericPEServiceTaskMapper;
import com.mytijian.mediator.common.DO.GenericPEServiceTaskDO;
import com.mytijian.mediator.common.model.GenericPEServiceTaskConfig;
import com.mytijian.mediator.common.service.GenericPEServiceTaskService;

/**
 *描述:请添加一段类名描述
 *日期:2017年12月20日
 *时间:下午3:45:44
 *@author huangwei
*/

@Service("genericPEServiceTaskService")
public class GenericPEServiceTaskServiceImpl implements GenericPEServiceTaskService {
	
	
	@Resource(name = "genericPEServiceTaskMapper")
	private GenericPEServiceTaskMapper genericPEServiceTaskMapper;
	
	@Override
	public List<GenericPEServiceTaskConfig> listGenericPEServiceTaskConfigByHospitalId(Integer hospitalId) {
		
		List<GenericPEServiceTaskConfig> resultList = Lists.newArrayList();
		
		List<GenericPEServiceTaskDO> taskDOList = genericPEServiceTaskMapper.selectTaskConfigByHospitalId(hospitalId);
		
		if(CollectionUtils.isNotEmpty(taskDOList)){
			for(GenericPEServiceTaskDO taskDO : taskDOList){
				GenericPEServiceTaskConfig taskConfig = new GenericPEServiceTaskConfig();
				BeanUtils.copyProperties(taskDO, taskConfig);
				resultList.add(taskConfig);
			}
		}
		return resultList;
	}

}
