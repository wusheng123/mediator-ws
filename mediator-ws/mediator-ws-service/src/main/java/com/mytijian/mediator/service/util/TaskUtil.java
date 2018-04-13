package com.mytijian.mediator.service.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class TaskUtil {
	private static Logger logger = LoggerFactory.getLogger(TaskUtil.class);

	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseTaskParam(Integer hospitalId,
			String taskParam) {
		try {
			return JSON.parseObject(taskParam, Map.class);
		} catch (Exception e) {
			logger.error("解析任务参数失败,hosp id :{},错误:{}", hospitalId, e);
			return null;
		}
	}
}
