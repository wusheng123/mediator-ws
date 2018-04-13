/*
 * Copyright 2016 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mytijian.mediator.agentTask.constant.TaskParamConstants;
import com.mytijian.mediator.agentTask.constant.TaskResponseConstants;
import com.mytijian.mediator.agentTask.enums.DataBaseOperationTaskActionEnum;
import com.mytijian.mediator.agentTask.model.TaskResponse;
import com.mytijian.mediator.api.model.MediatorAgentTaskModel;
import com.mytijian.mediator.api.service.EnvironmentKeeper;
import com.mytijian.mediator.api.service.MediatorTaskManagerService;
import com.mytijian.mediator.common.DAO.MediatorAgentTaskMapper;
import com.mytijian.mediator.common.DO.MediatorAgentTaskDO;
import com.mytijian.mediator.order.DO.OrderStatisticDO;
import com.mytijian.mediator.order.postDAO.OrderStatisticDAO;
import com.mytijian.mediator.service.util.TaskUtil;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.shared.mediator.util.TaskParamParser;

/**
 * 类MediatorTaskManagerImpl.java的实现描述：TODO 类实现描述 
 * @author liangxing 2016年10月18日 下午4:09:59
 */
@Service("mediatorTaskManagerService")
public class MediatorTaskManagerServiceImpl implements MediatorTaskManagerService{
	private Logger logger = LoggerFactory.getLogger(MediatorTaskManagerServiceImpl.class);
	
	@Resource(name = "mediatorAgentTaskMapper")
	private MediatorAgentTaskMapper mediatorAgentTaskMapper;
	@Resource(name = "soapHeaderInterceptor")
	private EnvironmentKeeper envKeeper;
	@Resource(name = "hospitalService")
	private HospitalService hospitalService;
	
	@Resource(name = "orderStatisticDAO")
	private OrderStatisticDAO orderStatisticDAO;
	
	@Override
	public List<MediatorAgentTaskModel> queryTaskList() {
		
		Integer hospitalId = envKeeper.getHospital();
		List<MediatorAgentTaskDO> mediatorAgentTaskList = mediatorAgentTaskMapper.selectMediatorAgentTaskDOByHospitalId(hospitalId);
		if(mediatorAgentTaskList == null){
			return Lists.newArrayList();
		}
		List<MediatorAgentTaskModel> result = Lists.newArrayList();
		mediatorAgentTaskList.forEach(mediatorAgentTask->{
			MediatorAgentTaskModel task = buildTaskModel(mediatorAgentTask);
			if(mediatorAgentTask.getInit() != null && mediatorAgentTask.getInit().equals(1)){
				mediatorAgentTaskMapper.updateInit(mediatorAgentTask.getId(), 0);
			}
			result.add(task);
		});
		return result;
	}
	@SuppressWarnings("unchecked")
	private MediatorAgentTaskModel buildTaskModel(MediatorAgentTaskDO mediatorAgentTaskDO){
		if(mediatorAgentTaskDO == null){
			return null;
		}
		MediatorAgentTaskModel mediatorAgentTaskModel = new MediatorAgentTaskModel();
		mediatorAgentTaskModel.setGmtCreated(mediatorAgentTaskDO.getGmtCreated());
		mediatorAgentTaskModel.setGmtModified(mediatorAgentTaskDO.getGmtModified());
		mediatorAgentTaskModel.setHospitalId(mediatorAgentTaskDO.getHospitalId());
		mediatorAgentTaskModel.setId(mediatorAgentTaskDO.getId());
		mediatorAgentTaskModel.setInit(mediatorAgentTaskDO.getInit());
		mediatorAgentTaskModel.setTaskCmd(mediatorAgentTaskDO.getTaskCmd());
		
		mediatorAgentTaskModel.setNewTaskParams(TaskUtil.parseTaskParam(
				mediatorAgentTaskDO.getHospitalId(),
				mediatorAgentTaskDO.getTaskParams()));
		
		mediatorAgentTaskModel.setTaskBeanName(mediatorAgentTaskDO.getTaskBeanName());
		mediatorAgentTaskModel.setCronExpression(mediatorAgentTaskDO
				.getCronExpression());
		return mediatorAgentTaskModel;
	}
	
	@Override
	public void syncTaskResult(TaskResponse taskResponse) {
		if (!checkParam(taskResponse)) {
			return;
		}
		
		MediatorAgentTaskModel taskModel = taskResponse.getTaskModel();
		
		String taskAction = TaskParamParser.getStringParamValue(
				taskModel.getNewTaskParams(), TaskParamConstants.ACTION);
		
		if (StringUtils.isBlank(taskAction)) {
			logger.warn("未配置action,hosp id : {},task : {}",
					envKeeper.getHospital(), taskModel);
			return;
		}
		
		// 订单统计
		if (DataBaseOperationTaskActionEnum.ORDER_STATISTIC.getName().equals(
				taskAction.trim())) {
			OrderStatisticDO orderStatisticDO = new OrderStatisticDO();
			orderStatisticDO.setHospitalId(envKeeper.getHospital());
			orderStatisticDO.setStatisticsItem(taskModel.getTaskCmd());// 设置任务cmd
			orderStatisticDO.setTaskId(taskModel.getId());
			orderStatisticDO.setParam(JSONObject.toJSONString(taskModel.getNewTaskParams()));
			
			Map<String, Object> map = taskResponse.getResultMap();
			orderStatisticDO.setResult(map.get(TaskResponseConstants.RESULT)
					.toString());
			orderStatisticDO.setFormat(map.get(TaskResponseConstants.FORMAT)
					.toString());
			
			orderStatisticDAO.add(orderStatisticDO);
		}else{
			logger.warn("未找到action,task : {}", taskModel);
		}
		
	}
	
	private boolean checkParam(TaskResponse taskResponse) {
		if (taskResponse == null || taskResponse.getTaskModel() == null
				|| taskResponse.getResultMap() == null) {
			return false;
		}

		return true;
	}
}
