package com.mytijian.mediator.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mongodb.DBObject;
import com.mytijian.cache.RedisCacheClient;
import com.mytijian.cache.annotation.RedisClient;
import com.mytijian.mediator.api.enums.AgentStatus;
import com.mytijian.mediator.api.enums.CmdState;
import com.mytijian.mediator.api.model.AgentInfo;
import com.mytijian.mediator.api.model.HeartbeatLog;
import com.mytijian.mediator.api.model.SyncResponse;
import com.mytijian.mediator.api.model.cmd.CallbackInfo;
import com.mytijian.mediator.api.model.cmd.CmdInput;
import com.mytijian.mediator.api.model.cmd.CmdOutput;
import com.mytijian.mediator.api.service.AgentInfoService;
import com.mytijian.mediator.api.service.CmdListener;
import com.mytijian.mediator.api.service.CommonService;
import com.mytijian.mediator.api.service.EnvironmentKeeper;
import com.mytijian.mediator.api.service.MediatorAgentTaskService;
import com.mytijian.mediator.common.DAO.HeartbeatLogMapper;
import com.mytijian.mediator.exceptions.MediatorException;
import com.mytijian.mediator.service.util.AgentInfoCacheManager;
import com.mytijian.mediator.service.util.ServiceLocator;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.service.HospitalService;

@Service("commonService")
public class CommonServiceImpl implements CommonService {

	private Logger log = LoggerFactory.getLogger(CommonServiceImpl.class);
	
	@Value("${upload}")
	private String uploadpath;

	@Resource(name = "heartbeatLogMapper")
	private HeartbeatLogMapper heartbeatLogMapper;

	@Resource(name = "serviceLocator")
	private ServiceLocator serviceLocator;

	@Resource(name = "soapHeaderInterceptor")
	private EnvironmentKeeper envKeeper;
	
	@Resource(name = "agentInfoService")
	private AgentInfoService agentInfoService;
	
	@Resource(name = "defaultCmdListener")
	private CmdListener cmdListener;
	
	// key - hospital, value - start command or not
	@RedisClient(nameSpace = CommonService.REDIS_KEY_SPACE_CMD_STATE, timeout = 60 * 24 * 60)
	private RedisCacheClient<CmdState> cmdStateCache;

	// key - hospital,value - cmd
	@RedisClient(nameSpace = CommonService.REDIS_KEY_SPACE_CMD_QUEUE, timeout = 60 * 60 * 24)
	private RedisCacheClient<LinkedBlockingDeque<CmdInput>> cmdQueuesCache;

	@Resource(name = "agentInfoCacheManager")
	private AgentInfoCacheManager agentInfoCacheManager;
	
	@Resource(name = "mediatorAgentTaskService")
	private MediatorAgentTaskService mediatorAgentTaskService;
	
	@Resource(name = "hospitalService")
	private HospitalService hospitalService;
	
	// 体检中心信息
	private Map<Integer, String> hospMap = new HashMap<Integer, String>();
	
	@Override
	public String register(String period, String version) {
		String uuid = UUID.randomUUID().toString();
		Integer hospitalId = envKeeper.getHospital();
		// 记录心跳
		logHeartbeat(period, hospitalId);

		logAgentInfo(period, version, hospitalId);
		
		agentInfoCacheManager.put(hospitalId, period, System.currentTimeMillis());

		System.out.println("register: " + hospitalId + ",version: " + version);
		log.info("Successful registered by hospitalId: {}, the heartbeat period is {}, token = {},version = {}",
				hospitalId, period, uuid, version);
		return uuid;
	}

	private void logAgentInfo(String period, String version, Integer hospitalId) {
		DBObject obj = agentInfoService.getByHospital(hospitalId);

		if (obj == null) {
			AgentInfo info = new AgentInfo();
			info.setHospitalId(hospitalId);
			info.setVersion(version);
			info.setHeartBeatPeriod(period);
			info.setRecentHeartBeatTimeMills(System.currentTimeMillis());
			info.setStatus(AgentStatus.ALIVE.getCode());
			info.setUpdateTime(Calendar.getInstance().getTime());
			info.setCreateTime(Calendar.getInstance().getTime());
			agentInfoService.addAgentInfo(info);
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("createTime", Calendar.getInstance().getTime());
			map.put("updateTime", Calendar.getInstance().getTime());
			map.put("version", version);
			map.put("heartBeatPeriod", period);
			map.put("recentHeartBeatTimeMills", System.currentTimeMillis());
			map.put("status", AgentStatus.ALIVE.getCode());
			agentInfoService.updateAgentInfo(hospitalId, map);
		}

	}
	
	@Override
	public SyncResponse heartbeat() throws MediatorException {
		Integer hospitalId = envKeeper.getHospital();
		
		agentInfoCacheManager.put(hospitalId, System.currentTimeMillis());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recentHeartBeatTimeMills", System.currentTimeMillis());
		map.put("updateTime", Calendar.getInstance().getTime());
		agentInfoService.updateAgentInfo(hospitalId, map);

		try {
			SyncResponse response = new SyncResponse();
			response.setAttribute("orderCount", 0);

			if (CmdState.Request.equals(cmdStateCache.get(hospitalId))) {
				response.setStartCmd(true);
				//去掉sent状态，对于连接请求处理失败的可以重试连接。此状态不可查看没什么意义
			} else if (CmdState.Close.equals(cmdStateCache.get(hospitalId))) {//此处尽量避免使用，回带来相互间的干扰
				response.setAttribute(INTERRUPT_COMMAND, Boolean.TRUE);
				cmdStateCache.remove(hospitalId);
				cmdQueuesCache.remove(hospitalId);
			}
			return response;
		} catch (Throwable t) {
			MediatorException e = new MediatorException(t);
			log.error("error occurs when generate response of heartbeat on hospitalId: {}", hospitalId, e);
			throw e;
		}
	}

	@Override
	public void testConnect() {
		log.info("connect successful! hospitalId: {}", envKeeper.getHospital());
	}

	/**
	 * 记录心跳
	 * 
	 * @param period
	 * @param hospitalId
	 * @return
	 */
	private void logHeartbeat(String period, Integer hospitalId) {
		HeartbeatLog lastLog = heartbeatLogMapper.getByHospitalId(hospitalId);
		if (lastLog == null || !lastLog.getPeriod().equals(period)) {

			HeartbeatLog log = new HeartbeatLog();
			log.setPeriod(period);
			log.setHospitalId(hospitalId);
			log.setCreateTime(Calendar.getInstance().getTime());

			heartbeatLogMapper.insert(log);
		}

	}

	@Override
	public void syncMessage(String message, int type) {
		Integer hospitalId = envKeeper.getHospital();
		String hospName = this.getHospName(hospitalId);
		if (type == 0) {
			log.info("远程信息\n HospitalId: {} HospitalName: {} \n {}",
					hospitalId, hospName, message);
		} else if (type == 1) {
			log.error("远程错误\n HospitalId: {} HospitalName: {} \n {}",
					hospitalId, hospName, message);
		}

	}
	
	private String getHospName(Integer hospitalId) {
		if (StringUtils.isNotBlank(hospMap.get(hospitalId))) {
			return hospMap.get(hospitalId);
		}
		Hospital hosp = hospitalService.getHospitalBaseInfoById(hospitalId);
		hospMap.put(hospitalId, hosp.getName());
		return hosp.getName();
	}

	@Override
	public CmdInput process(CmdOutput output) {

		Integer hospitalId = envKeeper.getHospital();
		if (StringUtils.isNotEmpty(output.getOutput())
				|| output.getCallback()!=null) {
			// save binary
			CallbackInfo callback = output.getCallback();
			if (callback!=null) {
				log.info("require callback. responseId={}", callback.getResponseId());
				callback.setHospitalId(hospitalId);
				String dataId = callback.getDataId();
				if (dataId!=null) {
					log.info("require load data. dataId={}", dataId);
					byte[] binary = callback.getBinary();
					String fileName = String.format("%s/%s", uploadpath, dataId);
					File file = new File(fileName);
					try {
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(binary);
						fos.flush();
						fos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			//执行结果放入队列
			cmdListener.onMessage(hospitalId, output);
			
			log.info("Output for hospital " + hospitalId 
					+ ": return code: " + output.getCode() 
					+ (output.getCallback()==null?"":
					": callback responseId " + output.getCallback().getResponseId())
					+ "\n"
					+ output.getOutput());
		}

		if (CmdOutput.CLOSE == output.getCode()) {
			cmdQueuesCache.remove(hospitalId);
			cmdStateCache.put(hospitalId, CmdState.Close);
		} else if (CmdState.Request.equals(cmdStateCache.get(hospitalId))) {
			cmdStateCache.put(hospitalId, CmdState.Connected);
		}
		CmdInput nextCommand = cmdListener.nextCommand(hospitalId);
		return nextCommand == null ? new CmdInput() : nextCommand;
	}

	@Override
	public int startCommand(Integer hospitalId) {
		if (!agentInfoCacheManager.getAliveHospital().contains(hospitalId)) {
			return 1;
		}
		if (CmdState.Connected.equals(cmdStateCache.get(hospitalId))) {
			return 2;
		}
		cmdStateCache.put(hospitalId, CmdState.Request);
		return 0;
	}

	@Override
	public void stopCommand(Integer hospitalId) {
		cmdStateCache.put(hospitalId, CmdState.Close);
	}
	
	@Override
	public Set<Integer> getOnlineHospitals() {
		return agentInfoCacheManager.getAliveHospital();
	}

	@Override
	public void updateMediatorAgentTaskParamsById(String taskCmd, String newTaskParams, String crontabExp) {
		Integer hospitalId = envKeeper.getHospital();
		mediatorAgentTaskService.updateTaskParam(hospitalId, taskCmd, newTaskParams);

		// 客服端不修改执行周期
		// mediatorAgentTaskService.updateTask(hospitalId, taskCmd,
		// newTaskParams, crontabExp);
	}

}
