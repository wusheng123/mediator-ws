package com.mytijian.mediator.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.mytijian.mediator.api.dto.OrderDto;
import com.mytijian.mediator.api.enums.ExamItemManipulationType;
import com.mytijian.mediator.api.model.OrderSyncError;
import com.mytijian.mediator.api.model.OrderSyncLog;
import com.mytijian.mediator.api.model.SyncExamItem;
import com.mytijian.mediator.api.service.EnvironmentKeeper;
import com.mytijian.mediator.api.service.MediatorService;
import com.mytijian.mediator.order.outer.service.MediatorSyncOrderService;
import com.mytijian.mediator.service.util.Offer2ResourceAPIResolver;
import com.mytijian.mediator.service.util.ServiceLocator;
import com.mytijian.offer.examitem.constant.enums.ExamItemTypeEnum;
import com.mytijian.offer.examitem.constant.enums.PriceEffectPropertyEnum;
import com.mytijian.offer.examitem.model.ExamItem;
import com.mytijian.offer.examitem.service.ExamItemManageService;
import com.mytijian.offer.examitem.service.ExamItemService;
import com.mytijian.order.model.AccomplishOrder;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.util.PinYinUtil;

/**
 * 类MediatorServiceImpl.java的实现描述：通过委托方式渐渐重构此类
 * @author liangxing 2017年2月14日 下午6:29:33
 */
@Service("mediatorService")
public class MediatorServiceImpl implements MediatorService {

	private Logger logger = LoggerFactory.getLogger(MediatorServiceImpl.class);

	@Resource(name = "serviceLocator")
	private ServiceLocator serviceLocator;

	// @Value("#{auth[defaultManagerId]}")
	private Integer defaultManagerId = -1;

	@Resource(name = "soapHeaderInterceptor")
	private EnvironmentKeeper envKeeper;

	@Resource(name = "examItemService")
	private ExamItemService examItemService;

	@Resource(name = "examItemManageService")
	private ExamItemManageService examItemManageService;

	@Resource(name = "mediatorSyncOrderService")
	private MediatorSyncOrderService mediatorSyncOrderService;

	@Deprecated
	@Override
	public PageView<OrderDto> getOrder(Page page) throws Exception {
		logger.warn("getOrder  move to  mediatorSyncOrderService");
		PageView<com.mytijian.mediator.order.dto.OrderDto> pageOrder = mediatorSyncOrderService.getOrder(page);
		// pageOrder 不可能为null
		if (CollectionUtils.isNotEmpty(pageOrder.getRecords())) {
			List<OrderDto> result = new ArrayList<OrderDto>();
			pageOrder.getRecords().stream().forEach(item -> {
				OrderDto orderDto = new OrderDto();
				BeanUtils.copyProperties(item, orderDto);
				result.add(orderDto);
			});
			return new PageView<OrderDto>(result, pageOrder.getPage());
		}

		return new PageView<OrderDto>(Lists.newArrayList(), pageOrder.getPage());

	}

	@Deprecated
	@Override
	public void sendOrderLog(List<OrderSyncLog> list) {
		logger.warn("sendOrderLog  move to  mediatorSyncOrderService");
		List<com.mytijian.mediator.order.dto.OrderSyncLog> toList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(list)) {
			list.stream().forEach(item -> {
				com.mytijian.mediator.order.dto.OrderSyncLog temp = new com.mytijian.mediator.order.dto.OrderSyncLog();
				BeanUtils.copyProperties(item, temp);
				toList.add(temp);
			});
		}

		mediatorSyncOrderService.sendOrderLog(toList);
	}

	@Deprecated
	@Override
	public List<String> sendDoneOrder(List<AccomplishOrder> list) {
		logger.warn("sendDoneOrder  move to  mediatorSyncOrderService");
		return mediatorSyncOrderService.sendDoneOrder(list);
	}

	// 没有移动，待定期所属模块 TODO:
	@Override
	public List<com.mytijian.resource.model.ExamItem> getExamItems() {
		Integer hospitalId = envKeeper.getHospital();
		return Offer2ResourceAPIResolver.resolveOffer2ResourceExamitemList(examItemService
				.getHospitalExamItems(hospitalId));
	}

	@Override
	public void sendChangedExamitem(List<SyncExamItem> hisItemList) {
		logger.info("同步{}条单项", hisItemList.size());
		for (SyncExamItem hisItem : hisItemList) {
			if (StringUtils.isBlank(hisItem.getHisItemId())) {
				logger.warn(hisItem.getName() + " 'hisitemid 为空");
				continue;
			}
			hisItem.setHospitalId(envKeeper.getHospital());
			ExamItem oldItem = examItemService.getExamItemByHospitalAndHisItemId(hisItem.getHospitalId(),
					hisItem.getHisItemId());

			// 废除项不同步
			if (oldItem != null && oldItem.getItemType().intValue() == ExamItemTypeEnum.DELETE.getCode()) {
				logger.warn("not sync when item is deleted.itemid is {} ", oldItem.getId());
				continue;
			}

			if (ExamItemManipulationType.ADD.getCode() == hisItem.getOperationType()) {
				if (oldItem != null) {

					// 删除已存在的废除项日志
					examItemManageService.deleteUncompletedLog(oldItem.getId(),
							Arrays.asList(PriceEffectPropertyEnum.DELETE.getCode()));
					// offer->resource
					modifyItem2(hisItem, oldItem);
				} else {
					addItem(hisItem);
				}
			} else if (ExamItemManipulationType.DELETE.getCode() == hisItem.getOperationType()) {
				examItemManageService.deleteExamItem(oldItem.getId(), -1);
			} else if (ExamItemManipulationType.MODIFY.getCode() == hisItem.getOperationType()) {
				if (oldItem != null) {
					modifyItem2(hisItem, oldItem);
				} else {
					addItem(hisItem);
				}
			}
			logger.info("体检中心ID is {}, 单项 is {}, 操作：{}", hisItem.getHospitalId(), hisItem, hisItem.getOperationType());
		}
	}

	@Deprecated
	private void modifyItem(SyncExamItem hisItem, com.mytijian.resource.model.ExamItem oldItem) {
		oldItem.setName(hisItem.getName());
		// 单项可同步价格，设置his最新的价格
		if (oldItem.getSyncPrice()) {
			oldItem.setPrice(hisItem.getPrice());
		}
		// 忽略his的性别、折扣
		// oldItem.setGender(hisItem.getGender());
		// oldItem.setDiscount(hisItem.isDiscount());
		oldItem.setPinyin(PinYinUtil.getFirstSpell(hisItem.getName()));
		examItemManageService.updateItemInfo(Offer2ResourceAPIResolver.resolveResource2OfferExamitem(oldItem), -1);
	}
	
	private void modifyItem2(SyncExamItem hisItem, ExamItem oldItem) {
		oldItem.setName(hisItem.getName());
		// 单项可同步价格，设置his最新的价格
		if (oldItem.getSyncPrice()) {
			oldItem.setPrice(hisItem.getPrice());
		}
		// 忽略his的性别、折扣
		// oldItem.setGender(hisItem.getGender());
		// oldItem.setDiscount(hisItem.isDiscount());
		oldItem.setPinyin(PinYinUtil.getFirstSpell(hisItem.getName()));
		examItemManageService.updateItemInfo(oldItem, -1);
	}

	private void addItem(SyncExamItem hisItem) {
		hisItem.setItemType(ExamItemTypeEnum.HOSPITAL.getCode());
		hisItem.setPinyin(PinYinUtil.getFirstSpell(hisItem.getName()));
		hisItem.setSyncPrice(true);// 默认同步价格，应在新增单项接口里处理这个逻辑。后面去掉这行代码
		
		// resource->offer
		examItemManageService.addExamItem(Offer2ResourceAPIResolver
				.resolveResource2OfferExamitem(hisItem), -1);
	}

	@Deprecated
	@Override
	public List<OrderDto> getImmediateOrder() {
		logger.warn("getImmediateOrder  move to  mediatorSyncOrderService");
		List<com.mytijian.mediator.order.dto.OrderDto> tempResult = mediatorSyncOrderService.getImmediateOrder();

		if (CollectionUtils.isNotEmpty(tempResult)) {
			List<OrderDto> result = new ArrayList<OrderDto>();
			tempResult.stream().forEach(item -> {
				OrderDto orderDto = new OrderDto();
				BeanUtils.copyProperties(item, orderDto);
				result.add(orderDto);
			});
			return result;
		}
		return Lists.newArrayList();
	}

	@Deprecated
	@Override
	public List<String> sendErrorOrder(List<OrderSyncError> errorOrderList) {
		logger.warn("sendErrorOrder  move to  mediatorSyncOrderService");
		List<com.mytijian.mediator.order.dto.OrderSyncError> toList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(errorOrderList)) {
			errorOrderList.stream().forEach(item -> {
				com.mytijian.mediator.order.dto.OrderSyncError temp = new com.mytijian.mediator.order.dto.OrderSyncError();
				BeanUtils.copyProperties(item, temp);
				toList.add(temp);
			});
		}

		return mediatorSyncOrderService.sendErrorOrder(toList);

	}

	@Deprecated
	@Override
	public List<String> sendAlarmOrder(List<OrderSyncError> alarmOrdersList) {
		logger.warn("sendAlarmOrder  move to  mediatorSyncOrderService");
		List<com.mytijian.mediator.order.dto.OrderSyncError> toList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(alarmOrdersList)) {
			alarmOrdersList.stream().forEach(item -> {
				com.mytijian.mediator.order.dto.OrderSyncError temp = new com.mytijian.mediator.order.dto.OrderSyncError();
				BeanUtils.copyProperties(item, temp);
				toList.add(temp);
			});
		}
		return mediatorSyncOrderService.sendAlarmOrder(toList);
	}

	@Override
	public void sendExamItem(List<SyncExamItem> list) {
		List<ExamItem> oldExamItems = examItemService
				.getHospitalExamItems(envKeeper.getHospital());
		Map<String, ExamItem> oldExamItemMap = oldExamItems.stream()
				.collect(Collectors.toMap(ExamItem::getHisItemId, ExamItem -> ExamItem));

		logger.info("Start SyncExamItem,HospitalId is {},List size is {}", envKeeper.getHospital(), list.size());
		
		if (CollectionUtils.isNotEmpty(oldExamItems)) {
			oldExamItemMap = oldExamItems.stream()
					.collect(Collectors.toMap(ExamItem::getHisItemId, ExamItem -> ExamItem));
		}

		Map<String, SyncExamItem> newExamItemMap = list.stream()
				.collect(Collectors.toMap(SyncExamItem::getHisItemId, SyncExamItem -> SyncExamItem));

		for (SyncExamItem hisExamItem : list) {
			if (StringUtils.isBlank(hisExamItem.getHisItemId()) || StringUtils.isBlank(hisExamItem.getName())) {
				logger.warn("hisExamItemId 或 hisExamItemName 为空, hisExamItemId:{},hisExamItemName:{}",hisExamItem.getHisItemId(),hisExamItem.getName());
				continue;
			}
			hisExamItem.setHospitalId(envKeeper.getHospital());
			String hisItemId = hisExamItem.getHisItemId();
			if (oldExamItemMap.containsKey(hisItemId)) {
				// 存在判断是否修改
				SyncExamItem newExamItem = newExamItemMap.get(hisItemId);
				ExamItem oldExamItem = oldExamItemMap.get(hisItemId);
				boolean isChanged = examItemHadChanged(newExamItem, oldExamItem);
				if (isChanged) {
					modifyItem2(newExamItem, oldExamItem);
					oldExamItemMap.remove(hisItemId);
				} else {
					oldExamItemMap.remove(hisItemId);
				}
			} else {
				// 不存在添加
				ExamItem oldItem = examItemService.getExamItemByHospitalAndHisItemId(envKeeper.getHospital(),
						hisItemId);
				if (oldItem != null) {
					continue;
				} else {
					addItem(hisExamItem);
				}
				oldExamItemMap.remove(hisItemId);
			}

		}

		// oldExamItemMap中还有的进行删除
		if (oldExamItemMap.size() != 0) {
			for (Map.Entry<String, ExamItem> delExamItem : oldExamItemMap.entrySet()) {
				examItemManageService.deleteExamItem(delExamItem.getValue().getId(), -1);
			}
		}
	}
	
	/**
	 * 判断单项价格和名字是否改变
	 * @param newItem 来自体检中心
	 * @param oldItem 来自mytijian
	 * @return
	 */
	private boolean examItemHadChanged(com.mytijian.resource.model.ExamItem newItem, ExamItem oldItem) {
		return newItem.getPrice().intValue() != oldItem.getPrice().intValue()
				|| !oldItem.getName().equals(newItem.getName());
	}

}
