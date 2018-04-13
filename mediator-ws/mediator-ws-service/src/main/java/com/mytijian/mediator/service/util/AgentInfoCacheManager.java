package com.mytijian.mediator.service.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.mytijian.cache.RedisCacheClient;
import com.mytijian.cache.annotation.RedisClient;
import com.mytijian.mediator.api.model.AgentInfo;
import com.mytijian.mediator.api.service.CommonService;

@Component("agentInfoCacheManager")
public class AgentInfoCacheManager {

	// 缓存过期时间需要大于3个心跳周期，设置缓存时间10mins
	@RedisClient(nameSpace = CommonService.REDIS_KEY_SPACE_AGENT_INFO, timeout = 60 * 10)
	private RedisCacheClient<AgentInfo> agentInfoCache;

	private String DEFAULT_PERIOD = "0 0/1 * * * ?";// 默认心跳周期1分钟

	public void put(Integer hospitalId, long heartBeatTimes) {

		AgentInfo agentInfo;
		if (agentInfoCache.contains(hospitalId)) {
			agentInfo = agentInfoCache.get(hospitalId);
		} else {
			agentInfo = new AgentInfo();
			agentInfo.setHospitalId(hospitalId);
			agentInfo.setHeartBeatPeriod(DEFAULT_PERIOD);
		}
		agentInfo.setRecentHeartBeatTimeMills(heartBeatTimes);

		agentInfoCache.put(hospitalId, agentInfo);
	}

	public void put(Integer hospitalId, String period, long heartBeatTimes) {

		AgentInfo agentInfo = new AgentInfo();
		agentInfo.setHospitalId(hospitalId);
		agentInfo.setHeartBeatPeriod(period);
		agentInfo.setRecentHeartBeatTimeMills(heartBeatTimes);
		agentInfoCache.put(hospitalId, agentInfo);
	}

	public AgentInfo get(Integer hospitalId) {
		return agentInfoCache.get(hospitalId);
	}

	public Set<Integer> getAliveHospital() {
		List<String> aliveHosp = agentInfoCache.keys("*");// space_hospId
		if (CollectionUtils.isNotEmpty(aliveHosp)) {
			Set<Integer> set = new HashSet<Integer>();
			aliveHosp.forEach(key -> {
				set.add(Integer.parseInt(key.split("_")[1]));
			});

			return set;
		} else {
			return Collections.emptySet();
		}
	}

}
