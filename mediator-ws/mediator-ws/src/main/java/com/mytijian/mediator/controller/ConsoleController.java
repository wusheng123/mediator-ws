package com.mytijian.mediator.controller;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mytijian.cache.RedisCacheClient;
import com.mytijian.cache.annotation.RedisClient;
import com.mytijian.mediator.api.enums.UpgradeStatus;
import com.mytijian.mediator.api.model.UpgradeMessage;
import com.mytijian.mediator.api.model.cmd.CmdInput;
import com.mytijian.mediator.api.model.cmd.CmdOutput;
import com.mytijian.mediator.api.service.CmdListener;
import com.mytijian.mediator.api.service.CommonService;
import com.mytijian.mediator.common.DAO.UpgradeMessageMapper;

@RestController
public class ConsoleController{
	private Logger logger = LoggerFactory.getLogger(ConsoleController.class);
	
	@Resource(name = "commonService")
	private CommonService commonService;

	@Resource(name = "defaultCmdListener")
	private CmdListener cmdListener;
	
	@Resource(name = "upgradeMessageMapper")
	private UpgradeMessageMapper upgradeMessageMapper;
	
	@RedisClient(nameSpace = CommonService.REDIS_KEY_SPACE_CMD_QUEUE, timeout = 60 * 60 * 24)
	private RedisCacheClient<LinkedBlockingDeque<CmdInput>> cmdQueuesCache;

	/**
	 * requested 表示已发出连接请求
	 * connected 表示已连接
	 * @param hospitalId
	 * @return
	 */
	@RequestMapping(value = "/connect/{hospitalId}", method = RequestMethod.GET, headers = "Accept=application/json")
	public String connect(@PathVariable("hospitalId") Integer hospitalId) {
		int result = commonService.startCommand(hospitalId);
		if (0 == result) {
			return "requested";
		} else if(2 == result){
			return "connected";
		}else if(1 == result){
			return "not in aliveHospital";
		}else{
			return "false" + result;
		}
	}
	@Deprecated
	@RequestMapping(value = "/start/{hospitalId}", method = RequestMethod.GET, headers = "Accept=application/json")
	public String start(@PathVariable("hospitalId") Integer hospitalId) {
		int result = commonService.startCommand(hospitalId);
		if (0 == result) {
			return "done";
		} else {
			return "false" + result;
		}
	}

	@RequestMapping(value = "/runCommand/{hospitalId}", method = RequestMethod.POST, headers = "Accept=application/json")
	public String runTasks(@PathVariable("hospitalId") Integer hospitalId, @RequestBody CmdInput input) {
		try {
			// 插入升级记录
			if ("download".equalsIgnoreCase(input.getCommand().trim())) {
				UpgradeMessage upgradeMessage = new UpgradeMessage();
				upgradeMessage.setHospitalId(hospitalId);
				upgradeMessage.setCurrentVersion(input.getParameter("currversion", String.class));
				upgradeMessage.setNewVersion(input.getParameter("newversion", String.class));
				upgradeMessage.setPackFile(input.getParameter("url", String.class));
				upgradeMessage.setStatus(UpgradeStatus.ReadyForUpgrade.getCode());
				upgradeMessageMapper.insert(upgradeMessage);
				
				input.setParameter("upgradeId", upgradeMessage.getId());
				cmdListener.addCommand(hospitalId, input);// cmd放到队列
			}else {
				cmdListener.addCommand(hospitalId, input);// cmd放到队列
			}
		} catch (InterruptedException e) {
			logger.error("运行命令失败", e);
			return "false" + 2;
		}
		return "done";
	}

	@RequestMapping(value = "/stop/{hospitalId}", method = RequestMethod.GET, headers = "Accept=application/json")
	public String stop(@PathVariable("hospitalId") Integer hospitalId) {
		commonService.stopCommand(hospitalId);
		CmdInput input = new CmdInput();
		input.setStop(true);// 结束control端取cmd
		try {
			cmdListener.addCommand(hospitalId, input);
		} catch (InterruptedException e) {
			logger.error("运行命令失败", e);
			return "false" + 2;
		}
		return "done";
	}
	
	@RequestMapping(value = "/hospitals", method = RequestMethod.GET, headers = "Accept=application/json")
	public String showHosp() {
		Set<Integer> hospSet = commonService.getOnlineHospitals();
		logger.info("在线医院：{}", hospSet);

		return "online hospitals:" + hospSet;
	}
	
	/**
	 * 接收升级时agent发送的消息，显示在console
	 * @return
	 */
	@RequestMapping(value = "/receive/upgradeMessage", method = RequestMethod.POST, headers = "Accept=application/json")
	public String receiveUpgradeMessage(@RequestBody Map<String, Object> map) {
		logger.info("receive upgrade message :" + map);

		CmdOutput output = new CmdOutput(0);
		output.setOutput(map.get("message").toString());
		
		Integer hospitalId = Integer.valueOf(map.get("hospitalId").toString());
		cmdListener.onMessage(hospitalId, output);
		
		if (map.get("message").toString().toLowerCase().contains("upgrade success")) {
			updateUpgradeStatus(hospitalId, UpgradeStatus.Success);
		} else {
			updateUpgradeStatus(hospitalId, UpgradeStatus.Failed);
		}
		return "done";
	}

	private void updateUpgradeStatus(Integer hospitalId, UpgradeStatus status) {
		UpgradeMessage upgradeMessage = upgradeMessageMapper.selectOneOrderByTime(hospitalId);
		if (upgradeMessage != null) {
			upgradeMessageMapper.updateStatus(upgradeMessage.getId(), status.getCode());
		}
	}
	
}
