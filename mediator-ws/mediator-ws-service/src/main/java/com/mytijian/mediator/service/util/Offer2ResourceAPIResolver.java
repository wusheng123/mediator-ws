package com.mytijian.mediator.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.mytijian.offer.examitem.model.ExamItem;
import com.mytijian.offer.examitem.model.ExamItemSnap;
import com.mytijian.order.model.AccomplishOrder;
import com.mytijian.order.model.AccomplishOrderResult;

/**
 * 因为部分webservice接口依赖了resource-api,为了agent避免全量升级，需要将offer的模型 转化为resource包下面的api。
 * 
 * @author yuefengyang
 *
 */
public class Offer2ResourceAPIResolver {
	
	private static Logger logger = LoggerFactory.getLogger(Offer2ResourceAPIResolver.class);

	// offer-> resource
	public static List<com.mytijian.resource.model.ExamItem> resolveOffer2ResourceExamitemList(
			List<ExamItem> list) {

		if (CollectionUtils.isEmpty(list)) {
			return new ArrayList<com.mytijian.resource.model.ExamItem>();
		}
		List<com.mytijian.resource.model.ExamItem> resultList = new ArrayList<com.mytijian.resource.model.ExamItem>();
		for (ExamItem source : list) {
			com.mytijian.resource.model.ExamItem target = new com.mytijian.resource.model.ExamItem();
			BeanUtils.copyProperties(source, target);
			resultList.add(target);
		}
		return resultList;
	}

	public static ExamItem resolveResource2OfferExamitem(
			com.mytijian.resource.model.ExamItem examitem) {
		ExamItem target = new ExamItem();
		BeanUtils.copyProperties(examitem, target);
		return target;
	}

	// offer -> resource
	public static com.mytijian.resource.model.ExamItem resolveOffer2ResourceExamitem(
			ExamItem examItem) {
		com.mytijian.resource.model.ExamItem target = new com.mytijian.resource.model.ExamItem();
		BeanUtils.copyProperties(examItem, target);
		return target;
	}

	// offer->resource List<ExamItemSnap> examItemSnapList
	public static List<com.mytijian.resource.model.ExamItemSnap> resolveOffer2ResourceItemSnapList(
			List<ExamItemSnap> list) {
		if (CollectionUtils.isEmpty(list)) {
			return new ArrayList<com.mytijian.resource.model.ExamItemSnap>();
		}

		List<com.mytijian.resource.model.ExamItemSnap> targetList = new ArrayList<com.mytijian.resource.model.ExamItemSnap>();
		for (ExamItemSnap source : list) {
			com.mytijian.resource.model.ExamItemSnap target = new com.mytijian.resource.model.ExamItemSnap();
			BeanUtils.copyProperties(source, target);
			targetList.add(target);
		}

		return targetList;
	}

	// AccomplishOrder -> 交易模块的AccomplishOrderResult
	public static AccomplishOrderResult resolveAccomplishOrder(
			AccomplishOrder source) {
		AccomplishOrderResult target = new AccomplishOrderResult();
		BeanUtils.copyProperties(source, target);
		// List<ExamItem>必需循环拷贝，否则会转成com.mytijian.resource包下面的模型，导致在撤单接口转换失败
		List<ExamItem> examItemList = new ArrayList<ExamItem>();
		if (CollectionUtils.isNotEmpty(source.getExamItemList())) {
			for (com.mytijian.resource.model.ExamItem examItem : source
					.getExamItemList()) {
				ExamItem targetItem = new ExamItem();
				BeanUtils.copyProperties(examItem, targetItem);
				examItemList.add(targetItem);
			}
		}
		target.setExamItemList(examItemList);
		
		return target;
	}


	public static List<AccomplishOrderResult> resolveAccomplishOrderList(
			List<AccomplishOrder> sourceList) {

		if (CollectionUtils.isEmpty(sourceList)) {
			return new ArrayList<AccomplishOrderResult>();
		}

		List<AccomplishOrderResult> targetList = new ArrayList<AccomplishOrderResult>();
		for (AccomplishOrder order : sourceList) {
			if(order == null){
				logger.warn("AccomplishOrder is null");
				continue;
			}
			targetList.add(resolveAccomplishOrder(order));
		}

		return targetList;
	}

	public static void main(String[] args) {
		AccomplishOrderResult target = new AccomplishOrderResult();

		AccomplishOrder order = new AccomplishOrder();
		order.setId(111);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("aa", 111);

		order.setDynamicAttributes(map);

		List<com.mytijian.resource.model.ExamItem> items = new ArrayList<com.mytijian.resource.model.ExamItem>();
		com.mytijian.resource.model.ExamItem item = new com.mytijian.resource.model.ExamItem();
		item.setName("222");
		items.add(item);

		order.setExamItemList(items);

		BeanUtils.copyProperties(order, target);

		System.out.println("22");

	}
}
