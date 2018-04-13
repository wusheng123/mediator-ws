package com.mytijian.mediator.meal.inner.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mytijian.company.dto.crm.MealSync;
import com.mytijian.mediator.meal.inner.service.MealOperateLogService;
import com.mytijian.mediator.meal.inner.service.MealSyncLogService;
import com.mytijian.mediator.meal.model.MealSyncLog;
import com.mytijian.shared.mediator.util.CollectionUtil;

@Service("mealOperateLogService")
public class MealOperateLogServiceImpl implements MealOperateLogService {
	private Logger logger = LoggerFactory
			.getLogger(MealOperateLogServiceImpl.class);

	@Resource(name = "mealSyncLogService")
	private MealSyncLogService mealSyncLogService;

	@Override
	public void logMealToSync(List<MealSync> mealList) {
		if (CollectionUtils.isNotEmpty(mealList)) {
			logger.info("meal log size {}", mealList.size());
			mealList = mealList.stream().filter(f -> f != null)
					.filter(CollectionUtil.distinctByKey(f -> f.getMealId()))
					.collect(Collectors.toList());

			for (MealSync mealSync : mealList) {
				MealSyncLog syncLog = mealSyncLogService.getByMealId(mealSync
						.getMealId());

				if (syncLog == null) {
					logger.info("add sync log,mealid : {}",
							mealSync.getMealId());
					mealSyncLogService.add(mealSync);
				} else {
					logger.info("update sync log ,mealid : {}",
							mealSync.getMealId());
					mealSyncLogService.update(mealSync);
				}

			}
		}else {
			logger.info("log meal size is 0");
		}

	}

}
