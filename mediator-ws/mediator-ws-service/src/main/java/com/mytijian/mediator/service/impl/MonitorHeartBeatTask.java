package com.mytijian.mediator.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mongodb.DBObject;
import com.mytijian.mediator.api.enums.AgentStatus;
import com.mytijian.mediator.api.service.AgentInfoService;
import com.mytijian.mediator.service.util.CronExpression;

@Service("monitorHeartBeatTask")
public class MonitorHeartBeatTask  {
	private final static Logger logger = LoggerFactory.getLogger(MonitorHeartBeatTask.class);

	private static ScheduledExecutorService executorService;

	@Resource(name = "agentInfoService")
	private AgentInfoService agentInfoService;

	private int count = 3;

	private int delay = 1 * 60 * 1000;
	
	@PostConstruct
	public void startTask() {
		
		executorService = new ScheduledThreadPoolExecutor(5);
		// 1s之后启动调度器，每隔1分钟执行一次任务
		executorService.scheduleWithFixedDelay(getTask(), 1000, delay, TimeUnit.MILLISECONDS);
	}
	
	private Runnable getTask() {
		Runnable task = () -> {

			agentInfoService.getAll().forEach(obj -> {
				checkAlive(obj);
			});

		};

		return task;
	}

	private void checkAlive(DBObject obj) {
		try {
			Integer hospitalId = Integer.parseInt(obj.get("hospitalId").toString());
			String cronExp = obj.get("heartBeatPeriod").toString();
			long recentHeartBeatTimeMills = Long.parseLong(obj.get("recentHeartBeatTimeMills").toString());
			if ((System.currentTimeMillis() - recentHeartBeatTimeMills) > (count * getHeartBeatMills(cronExp))) {

				updateAgentStatus(hospitalId, AgentStatus.NOT_ALIVE);
				logger.warn("AGENT_IS_OFF.hospital id is {},heart beat is {}",
						hospitalId, cronExp);
			} else {

				updateAgentStatus(hospitalId, AgentStatus.ALIVE);
//				logger.info("Agent is online,hospital id is {},heart beat is {}", hospitalId, cronExp);
			}
		} catch (Exception e) {
			logger.error(
					"check alive error,hospital id :"
							+ Integer
									.parseInt(obj.get("hospitalId").toString()),
					e);
		}
	}
	
	private void updateAgentStatus(Integer hospitalId, AgentStatus status) {
		Map<String, Object> setMap = new HashMap<String, Object>();
		setMap.put("status", status.getCode());
		agentInfoService.updateAgentInfo(hospitalId, setMap);
	}

	// 解析crontab表达式,返回心跳周期的毫秒数
	private long getHeartBeatMills(String heartBeat) {

		CronExpression cron = new CronExpression(heartBeat);
		DateTime currentTime = DateTime.now().withMinuteOfHour(0).withSecondOfMinute(0);
		DateTime nextTime = cron.nextTimeAfter(currentTime);
		return nextTime.getMillis() - currentTime.getMillis();
	}

	@PreDestroy
	public void stopTask() {
		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdown();
		}
	}

}
