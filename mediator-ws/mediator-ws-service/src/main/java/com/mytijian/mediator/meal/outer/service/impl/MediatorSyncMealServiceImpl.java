package com.mytijian.mediator.meal.outer.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.mytijian.company.hospital.service.HospitalCompanyService;
import com.mytijian.company.model.CompanyHisRelation;
import com.mytijian.mediator.api.enums.MealSyncStatus;
import com.mytijian.mediator.api.service.EnvironmentKeeper;
import com.mytijian.mediator.company.inner.service.impl.SyncCrmHisCompanyServiceAdapter;
import com.mytijian.mediator.meal.inner.service.MealSyncLogService;
import com.mytijian.mediator.meal.model.MealSyncLog;
import com.mytijian.mediator.meal.outer.service.MediatorSyncMealService;
import com.mytijian.mediator.service.util.Offer2ResourceAPIResolver;
import com.mytijian.offer.examitem.exception.ItemSelectException.ConflictType;
import com.mytijian.offer.examitem.model.ExamItem;
import com.mytijian.offer.examitem.service.ExamItemService;
import com.mytijian.offer.meal.model.Meal;
import com.mytijian.offer.meal.param.MealQuerySelector;
import com.mytijian.offer.meal.service.MealService;
import com.mytijian.shared.mediator.util.CollectionUtil;

@Service("mediatorSyncMealService")
public class MediatorSyncMealServiceImpl implements MediatorSyncMealService {

	private Logger logger =LoggerFactory.getLogger(MediatorSyncMealServiceImpl.class);
	
	@Resource(name = "soapHeaderInterceptor")
	private EnvironmentKeeper envKeeper;

	@Resource(name = "mealSyncLogService")
	private MealSyncLogService mealSyncLogService;

	@Resource(name = "mealService")
	private MealService mealService;

	@Resource(name = "examItemService")
	private ExamItemService examItemService;
	
	@Resource(name = "syncCrmHisCompanyServiceAdapter")
	private SyncCrmHisCompanyServiceAdapter syncCrmHisCompanyServiceAdapter;
	
	@Resource(name = "hospitalCompanyService")
	private HospitalCompanyService hospitalCompanyService;
	
	@Override
	public List<MealSyncLog> syncMeal() {

		List<MealSyncLog> mealSyncLogs = mealSyncLogService.getMealToSync(
				MealSyncStatus.NotComplete, envKeeper.getHospital());

		mealSyncLogs = mealSyncLogs.stream().filter(f -> f.getMealId() != null)
				.filter(CollectionUtil.distinctByKey(f -> f.getMealId()))
				.collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(mealSyncLogs)) {
			for (MealSyncLog mealSyncLog : mealSyncLogs) {
				MealQuerySelector mealQuerySelector = new MealQuerySelector(true,false);
				Optional<Meal>  optionalMeal = mealService.getMealById(mealSyncLog.getMealId(), mealQuerySelector);
				//套餐不存在，改变
				if(!optionalMeal.isPresent()){
					mealSyncLogService.updateHisMealId(mealSyncLog.getMealId(), null,
							MealSyncStatus.Complete);
					mealSyncLogs.remove(mealSyncLog);
					continue;
				}
				
				//套餐存在
				Meal meal = optionalMeal.get();
				mealSyncLog
						.setOriginalPrice(getPrice(meal.getInitPrice()));
				mealSyncLog.setDiscountPrice(getPrice(meal.getPrice()));
				mealSyncLog.setDiscount(meal.getDiscount());
				
				// 取his company id
				mealSyncLog.setHisCompanyId(getHisCompanyCode(mealSyncLog
						.getCompanyId()));

				List<ExamItem> examItems = meal.getMealExamItemList();
				
				// 过滤可同步单项
				examItems = filterExamitem(examItems);
				examItems.stream().forEach(
						f -> f.setPrice(getPrice(f.getPrice())));
				// 兼容resource api
				mealSyncLog.setExamItemList(Offer2ResourceAPIResolver.resolveOffer2ResourceExamitemList(examItems));
				
				// 增加性别
				mealSyncLog.setMyMealGender(meal.getGender());
			}
		} else {
			return Collections.emptyList();
		}

		return mealSyncLogs;
	}
	
	private List<ExamItem> filterExamitem(List<ExamItem> examItems) {
		List<ExamItem> list = new ArrayList<ExamItem>();
		for (ExamItem item : examItems) {
			if (StringUtils.isNotBlank(item.getHisItemId())
					&& item.getItemType() <= 2) {
				list.add(item);
			} else if (StringUtils.isBlank(item.getHisItemId())) {
				// 获取合并项子项
				List<Integer> children = examItemService.getConflictItems(
						item.getId(), ConflictType.COMPOSE);

				if (CollectionUtils.isNotEmpty(children)) {
					for (Integer itemId : children) {
						ExamItem childItem = examItemService
								.getExamItemById(itemId);

						if (childItem != null
								&& StringUtils.isNotBlank(childItem
										.getHisItemId())
								&& childItem.getItemType() <= 2) {
							list.add(childItem);
						}
					}
				}

			}
		}

		return list;
	}
	
	// 到客户端转成元
	private Integer getPrice(Integer price) {
		if (price == null) {
			return 0;
		}
		return price;
	}
	
	private String getHisCompanyCode(Integer companyId) {
		if (companyId != null) {
			try {
				CompanyHisRelation companyHisRelation = syncCrmHisCompanyServiceAdapter
						.getHisCompany(companyId);
				if (companyHisRelation != null) {
					return companyHisRelation.getHisCompanyCode();
				}
			} catch (Exception e) {
				logger.error("get his comp error", e);
			}
		}

		return null;
	}
	
	@Override
	public void updateHisMealId(Integer mealId, String hisMealId) {
		mealSyncLogService.updateHisMealId(mealId, hisMealId,
				MealSyncStatus.Complete);
	}

	@Override
	public void updateHisMealIdList(List<MealSyncLog> mealSyncLogs) {
		if (CollectionUtils.isNotEmpty(mealSyncLogs)) {
			for (MealSyncLog log : mealSyncLogs) {
				mealSyncLogService.updateHisMealId(log.getMealId(),
						log.getHisMealId(), MealSyncStatus.Complete);
			}

		}

	}

}
