package com.mytijian.mediator.meal.inner.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mytijian.mediator.api.enums.MealSyncStatus;
import com.mytijian.mediator.meal.DAO.MealSyncLogMapper;
import com.mytijian.mediator.meal.inner.service.MealSyncLogService;
import com.mytijian.mediator.meal.model.MealSyncLog;
import com.mytijian.offer.meal.constant.enums.MealTypeEnum;
import com.mytijian.offer.meal.model.Meal;
import com.mytijian.offer.meal.service.MealService;

@Service("mealSyncLogService")
public class MealSyncLogServiceImpl implements MealSyncLogService{
	private Logger logger = LoggerFactory
			.getLogger(MealSyncLogServiceImpl.class);

	@Resource(name = "mealSyncLogMapper")
	private MealSyncLogMapper mealSyncLogMapper;
	
	@Resource(name = "mealService")
	private MealService mealService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void add(com.mytijian.company.dto.crm.MealSync mealSync) {
		Meal meal = mealService.getMealById(mealSync.getMealId());
		// 可用的套餐才同步
		if (meal != null) {
			logger.info("add meal sync,meal id : {}", mealSync.getMealId());
			MealSyncLog log = new MealSyncLog();
			log.setMealId(mealSync.getMealId());
			log.setHospitalId(meal.getHospitalId());
			Integer companyId = getCompanyId(meal.getId(), meal.getType());
			logger.info("meal id is {},company id is {}", meal.getId(),
					companyId);
			log.setCompanyId(companyId);
			log.setStatus(MealSyncStatus.NotComplete.getCode());
			log.setHisName(mealSync.getHisMealName());
			mealSyncLogMapper.insertMealSyncLog(log);
		} else {
			logger.info("meal not exist ,meal id : {}", mealSync.getMealId());
		}

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(com.mytijian.company.dto.crm.MealSync mealSync) {
		Meal meal = mealService.getMealById(mealSync.getMealId());
		if (meal != null) {
			logger.info("update sync log,meal id : {}", meal.getId());
			mealSyncLogMapper.updateMealSyncLog(mealSync.getMealId(),
					getCompanyId(meal.getId(), meal.getType()),
					MealSyncStatus.NotComplete.getCode(),
					mealSync.getHisMealName());
		}else {
			logger.info("meal not exist,meal id : {}", mealSync.getMealId());
		}

	}
	
	private Integer getCompanyId(Integer mealId, Integer type) {
		// type maybe null
		if (type != null) {
			// company meal
			if (type.intValue() == MealTypeEnum.COMPANY_MEAL.getCode()
					.intValue()) {
				Meal customMeal = mealService
						.getCustomizedMealByMealId(mealId);

				return customMeal != null ? customMeal.getNewCompanyId() : null;
			} else {
				return null;
			}
		}

		return null;
	}

	@Override
	public MealSyncLog getByMealId(Integer mealId) {
		List<MealSyncLog> mealList = mealSyncLogMapper.selectByMealId(mealId);
		if (CollectionUtils.isNotEmpty(mealList)) {
			return mealList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<MealSyncLog> getMealToSync(MealSyncStatus status,
			Integer hospitalId) {
		return mealSyncLogMapper.selectByStatusAndHospital(status.getCode(),
				hospitalId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateHisMealId(Integer mealId, String hisMealId,
			MealSyncStatus status) {
		int count = mealSyncLogMapper.updateHisMealId(mealId, hisMealId,
				MealSyncStatus.Complete.getCode());
		logger.info("update count :{},meal id : {},hismealid : {}", count,
				mealId, hisMealId);
	}

}
