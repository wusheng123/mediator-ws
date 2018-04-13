package com.mytijian.mediator.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mytijian.mediator.agentTask.constant.AgentTaskExceptionCode;
import com.mytijian.mediator.agentTask.constant.TaskParamConstants;
import com.mytijian.mediator.agentTask.model.AgentTaskConfig;
import com.mytijian.mediator.api.enums.AgentTaskStatus;
import com.mytijian.mediator.api.model.MediatorAgentTaskModel;
import com.mytijian.mediator.api.service.MediatorAgentTaskService;
import com.mytijian.mediator.api.service.param.ListTaskHospitalQuery;
import com.mytijian.mediator.common.DAO.AgentTaskConfigMapper;
import com.mytijian.mediator.common.DAO.MediatorAgentTaskMapper;
import com.mytijian.mediator.common.DO.AgentTaskConfigDO;
import com.mytijian.mediator.common.DO.MediatorAgentTaskDO;
import com.mytijian.mediator.common.param.ListTaskHospitalDaoQuery;
import com.mytijian.mediator.exceptions.ExceptionFactory;
import com.mytijian.mediator.exceptions.ServiceException;
import com.mytijian.mediator.service.util.TaskUtil;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.service.HospitalService;

@Service("mediatorAgentTaskService")
public class MediatorAgentTaskServiceImpl implements MediatorAgentTaskService {

	private Logger logger = LoggerFactory.getLogger(MediatorAgentTaskServiceImpl.class);
	
	@Resource(name = "mediatorAgentTaskMapper")
	private MediatorAgentTaskMapper mediatorAgentTaskMapper;

	@Resource(name = "hospitalService")
	private HospitalService hospitalService;
	
	@Resource(name = "agentTaskConfigMapper")
	private AgentTaskConfigMapper agentTaskConfigMapper;

	@Override
	public List<MediatorAgentTaskModel> getAgentTask(Integer hospitalId, Integer status, String taskCmd, Integer startIndex, Integer pageSize) {
		List<MediatorAgentTaskModel> mediatorAgentTaskModels = new ArrayList<>();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("hospitalId", hospitalId);
		paramMap.put("status", status);
		paramMap.put("taskCmd", taskCmd);
		paramMap.put("startIndex", startIndex);
		paramMap.put("pageSize", pageSize);
		List<MediatorAgentTaskDO> mediatorAgentTaskDOs = mediatorAgentTaskMapper.getAgentTask(paramMap);
		MediatorAgentTaskModel mediatorAgentTask = null;
		for (MediatorAgentTaskDO mediatorAgentTaskDO : mediatorAgentTaskDOs) {
			mediatorAgentTask = new MediatorAgentTaskModel();
			BeanUtils.copyProperties(mediatorAgentTaskDO, mediatorAgentTask);
			mediatorAgentTaskModels.add(mediatorAgentTask);
		}
		return mediatorAgentTaskModels;
	}

	@Override
	public int updateAgentTaskByHsId(String taskCmd, Integer hospitalId, int status) {
		// 不存在则新增，存在则更新
		// 查询医院是否存在
		Hospital hospital = hospitalService.getHospitalBaseInfoById(hospitalId);
		if (hospital == null) {
			logger.warn("医院不存在(updateAgentTaskByHsId), hospitalId : "
					+ hospitalId);
			throw ExceptionFactory.makeFault(
					AgentTaskExceptionCode.HOSPITAL_NOT_EXIST,
					new Object[] { hospitalId });
		}

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("hospitalId", hospitalId);
		paramMap.put("taskCmd", taskCmd);
		List<MediatorAgentTaskDO> agentTasks = mediatorAgentTaskMapper
				.getAgentTask(paramMap);
		if (CollectionUtils.isEmpty(agentTasks)) {
			AgentTaskConfig taskConfig = this.getConfigByTaskCmd(taskCmd);
			insertTask(hospitalId, taskConfig, status);
			return 1;
		}
		return mediatorAgentTaskMapper.updateAgentTaskStatus(status,
				hospitalId, taskCmd);
	}

	@Override
	public List<MediatorAgentTaskModel> getAllTasks() {
		List<MediatorAgentTaskDO> doList = mediatorAgentTaskMapper
				.selectAllTasks();
		return resolveTaskDo(doList);
	}

	@Override
	public List<MediatorAgentTaskModel> getTasksByHospId(Integer hospitalId) {
		if (hospitalId == null) {
			return Collections.emptyList();
		}
		List<MediatorAgentTaskDO> doList = mediatorAgentTaskMapper
				.selectByHospitalId(hospitalId);

		return resolveTaskDo(doList);
	}

	private List<MediatorAgentTaskModel> resolveTaskDo(
			List<MediatorAgentTaskDO> doList) {
		if (CollectionUtils.isEmpty(doList)) {
			return Collections.emptyList();
		}

		List<MediatorAgentTaskModel> taskList = new ArrayList<MediatorAgentTaskModel>();
		for (MediatorAgentTaskDO taskDo : doList) {
			MediatorAgentTaskModel targetTask = new MediatorAgentTaskModel();
			BeanUtils.copyProperties(taskDo, targetTask);
			
			targetTask.setNewTaskParams(TaskUtil.parseTaskParam(
					taskDo.getHospitalId(), taskDo.getTaskParams()));
			
			taskList.add(targetTask);
		}
		return taskList;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void initHospTask(Integer hospitalId) throws ServiceException{
		if (hospitalId == null) {
			throw ExceptionFactory.makeFault(
					AgentTaskExceptionCode.HOSPITAL_NOT_EXIST,
					new Object[] { null });
		}
		
		Hospital hospital = hospitalService.getHospitalById(hospitalId);
		if (hospital == null) {
			throw ExceptionFactory.makeFault(
					AgentTaskExceptionCode.HOSPITAL_NOT_EXIST,
					new Object[] { hospitalId });
		}
		
		
		List<MediatorAgentTaskDO> taskDos = mediatorAgentTaskMapper
				.selectByHospitalId(hospitalId);
		
		if (CollectionUtils.isNotEmpty(taskDos)) {
			throw ExceptionFactory
					.makeFault(AgentTaskExceptionCode.HOSPITAL_TASK_ALREADY_ADDED);
		}
		
		// 初始化任务
		List<AgentTaskConfig> taskConfigs = this.getAgentTaskConfigList();
		for(AgentTaskConfig taskConfig : taskConfigs){
			insertTask(hospitalId, taskConfig, AgentTaskStatus.Closed.getCode());
		}
		
	}

	private void insertTask(Integer hospitalId, AgentTaskConfig taskConfig,int status) {
		MediatorAgentTaskDO task = new MediatorAgentTaskDO();
		task.setHospitalId(hospitalId);
		task.setStatus(status);
		task.setInit(Boolean.FALSE);
		task.setTaskCmd(taskConfig.getTaskCmd());
		task.setTaskBeanName(taskConfig.getDefaultTaskBeanName());
		task.setCronExpression(taskConfig.getDefaultCrontabExp());
		task.setTaskParams(taskConfig.getDefaultTaskParam());
		
		mediatorAgentTaskMapper.insert(task);
	}
	
	private void insertTask(Integer hospitalId, Integer provinceId, AgentTaskConfig taskConfig,int status) {
		MediatorAgentTaskDO task = new MediatorAgentTaskDO();
		task.setHospitalId(hospitalId);
		task.setStatus(status);
		task.setProvinceId(provinceId);
		task.setInit(Boolean.FALSE);
		task.setTaskCmd(taskConfig.getTaskCmd());
		task.setTaskBeanName(taskConfig.getDefaultTaskBeanName());
		task.setCronExpression(taskConfig.getDefaultCrontabExp());
		task.setTaskParams(taskConfig.getDefaultTaskParam());
		
		mediatorAgentTaskMapper.insert(task);
	}
	
	@Override
	public List<Integer> getHospitalIdHasTask() {
		return mediatorAgentTaskMapper.selectHospitalIds();
	}

	@Override
	public List<AgentTaskConfig> getAgentTaskConfigList() {
		List<AgentTaskConfigDO> list = agentTaskConfigMapper
				.selectTaskConfigList();
		if (CollectionUtils.isEmpty(list)) {
			return Collections.emptyList();
		}

		List<AgentTaskConfig> configList = new ArrayList<AgentTaskConfig>();
		for (AgentTaskConfigDO sourceTask : list) {
			AgentTaskConfig targetTask = new AgentTaskConfig();
			BeanUtils.copyProperties(sourceTask, targetTask);
			configList.add(targetTask);
		}
		return configList;
	}

	@Override
	public void updateTask(Integer hospitalId, String taskCmd,
			String taskParam, String crontabExp) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("hospitalId", hospitalId);
		paramMap.put("taskCmd", taskCmd);
		List<MediatorAgentTaskDO> agentTasks = mediatorAgentTaskMapper
				.getAgentTask(paramMap);

		if (CollectionUtils.isNotEmpty(agentTasks)) {
			MediatorAgentTaskDO taskDO = agentTasks.get(0);
			
			// 更新任务参数
			if (!TaskParamConstants.NO_TASK_PARAM.equals(taskParam)
					&& !Objects.equals(taskDO.getTaskParams(), taskParam)) {
				mediatorAgentTaskMapper.updateTaskParam(hospitalId, taskCmd,
						taskParam);
			}

			// 更新时间
			if (!Objects.equals(crontabExp, taskDO.getCronExpression())) {
				mediatorAgentTaskMapper.updateTaskCrontabExp(hospitalId,
						taskCmd, crontabExp);
			}

		}
		else {
			// 为空
			AgentTaskConfig taskConfig = this.getConfigByTaskCmd(taskCmd);
			insertTask(hospitalId, taskConfig, AgentTaskStatus.Closed.getCode());
		}
		

	}

	@Override
	public AgentTaskConfig getConfigByTaskCmd(String taskCmd) {
		AgentTaskConfigDO taskDo = agentTaskConfigMapper
				.selectTaskConfigByTaskCmd(taskCmd);
		AgentTaskConfig config = new AgentTaskConfig();
		BeanUtils.copyProperties(taskDo, config);
		return config;
	}

	@Override
	public int countTaskHospitals(ListTaskHospitalQuery listTaskHospitalQuery) {
		ListTaskHospitalDaoQuery listTaskHospitalDaoQuery = listTaskHospitalQueryToListTaskHospitalDaoQuery(listTaskHospitalQuery);
		return mediatorAgentTaskMapper.countTaskHospitals(listTaskHospitalDaoQuery);
	}

	@Override
	public List<Integer> listTaskHospitalIds(ListTaskHospitalQuery listTaskHospitalQuery) {
		ListTaskHospitalDaoQuery listTaskHospitalDaoQuery = listTaskHospitalQueryToListTaskHospitalDaoQuery(listTaskHospitalQuery);
		return mediatorAgentTaskMapper.selectTaskHospitals(listTaskHospitalDaoQuery);
	}
	
	
	private ListTaskHospitalDaoQuery listTaskHospitalQueryToListTaskHospitalDaoQuery (ListTaskHospitalQuery listTaskHospitalQuery) {
		ListTaskHospitalDaoQuery listTaskHospitalDaoQuery = null;
		if (listTaskHospitalQuery != null) {
			listTaskHospitalDaoQuery = new ListTaskHospitalDaoQuery();
			BeanUtils.copyProperties(listTaskHospitalQuery, listTaskHospitalDaoQuery);
		}
		return listTaskHospitalDaoQuery;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void initHospTask(Integer hospitalId, Integer provinceId) throws ServiceException {
		if (hospitalId == null) {
			throw ExceptionFactory.makeFault(
					AgentTaskExceptionCode.HOSPITAL_NOT_EXIST,
					new Object[] { null });
		}
		
		Hospital hospital = hospitalService.getHospitalById(hospitalId);
		if (hospital == null) {
			throw ExceptionFactory.makeFault(
					AgentTaskExceptionCode.HOSPITAL_NOT_EXIST,
					new Object[] { hospitalId });
		}
		
		
		List<MediatorAgentTaskDO> taskDos = mediatorAgentTaskMapper
				.selectByHospitalId(hospitalId);
		
		if (CollectionUtils.isNotEmpty(taskDos)) {
			throw ExceptionFactory
					.makeFault(AgentTaskExceptionCode.HOSPITAL_TASK_ALREADY_ADDED);
		}
		
		// 初始化任务
		List<AgentTaskConfig> taskConfigs = this.getAgentTaskConfigList();
		for(AgentTaskConfig taskConfig : taskConfigs){
			insertTask(hospitalId, provinceId, taskConfig, AgentTaskStatus.Closed.getCode());
		}
	}

	@Override
	public void updateTaskParam(Integer hospitalId, String taskCmd, String taskParam) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("hospitalId", hospitalId);
		paramMap.put("taskCmd", taskCmd);
		List<MediatorAgentTaskDO> agentTasks = mediatorAgentTaskMapper.getAgentTask(paramMap);

		if (CollectionUtils.isEmpty(agentTasks)) {
			return;
		}
		mediatorAgentTaskMapper.updateTaskParam(hospitalId, taskCmd, taskParam);
	}
}
