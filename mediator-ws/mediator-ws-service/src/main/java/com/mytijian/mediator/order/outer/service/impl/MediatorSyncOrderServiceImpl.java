/*
 * Copyright 2017 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.order.outer.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mytijian.account.model.Account;
import com.mytijian.mediator.api.service.EnvironmentKeeper;
import com.mytijian.mediator.api.service.ImmediateOrderService;
import com.mytijian.mediator.order.DAO.OrderLogMapper;
import com.mytijian.mediator.order.base.service.MediatorOrderService;
import com.mytijian.mediator.order.base.service.MediatorRefundService;
import com.mytijian.mediator.order.dto.OrderDto;
import com.mytijian.mediator.order.dto.OrderSyncError;
import com.mytijian.mediator.order.dto.OrderSyncLog;
import com.mytijian.mediator.order.enums.OrderErrorCode;
import com.mytijian.mediator.order.enums.OrderExportType;
import com.mytijian.mediator.order.model.MediatorOrderExportHis;
import com.mytijian.mediator.order.outer.service.MediatorSyncOrderService;
import com.mytijian.mediator.order.postDAO.MediatorOrderExportHisDAO;
import com.mytijian.mediator.service.util.ServiceLocator;
import com.mytijian.order.base.service.OrderForMediatorService;
import com.mytijian.order.base.service.dto.UpdateOrderAfterExportDTO;
import com.mytijian.order.model.AccomplishOrder;
import com.mytijian.order.model.Order;
import com.mytijian.order.service.OrderService;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.pulgin.mybatis.pagination.PageView;

/**
 * 类MediatorOrderServiceImpl.java的实现描述：订单导出服务
 * @author liangxing 2017年2月14日 下午6:11:35
 */
@Service("mediatorSyncOrderService")
public class MediatorSyncOrderServiceImpl implements MediatorSyncOrderService {
	private Logger logger = LoggerFactory.getLogger(MediatorSyncOrderServiceImpl.class);

	@Resource(name = "orderService")
	private OrderService orderService;

	@Resource(name = "serviceLocator")
	private ServiceLocator serviceLocator;

	@Resource(name = "orderLogMapper")
	private OrderLogMapper orderLogMapper;

	// @Value("#{auth[defaultManagerId]}")
	private Integer defaultManagerId = -1;

	@Resource(name = "soapHeaderInterceptor")
	private EnvironmentKeeper envKeeper;

	@Resource(name = "immediateOrderService")
	private ImmediateOrderService immediateOrderService;

	@Resource(name = "mediatorOrderExportHisDAO")
	private MediatorOrderExportHisDAO mediatorOrderExportHisDAO;

	@Value("${orderExportPageSzie}")
	private Integer orderExportPageSzie;
	
	@Resource(name = "orderForMediatorService")
	private OrderForMediatorService orderForMediatorService;

	@Override
	public PageView<OrderDto> getOrder(Page page) throws Exception {
		Integer hospitalId = envKeeper.getHospital();
		PageView<OrderDto> pageOrder =  getMediatorOrderService().getExportableOrder(hospitalId, page);
		
		if (CollectionUtils.isNotEmpty(pageOrder.getRecords())) {
			List<Integer> orderIdList = Lists.newArrayList();
			pageOrder.getRecords().stream().forEach(item -> {
				orderIdList.add(item.getOrderId());
			});
			mediatorOrderExportHisDAO.updateOrderExportByOrderIds(orderIdList, 1);
		}
		
		return pageOrder;
	}

	@Override
	public void sendOrderLog(List<OrderSyncLog> list) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}

		for (OrderSyncLog log : list) {
			Order order = orderService.getOrderByOrderNum(log.getOrderNum());
			if (order != null) {

				log.setOrderId(order.getId());
				log.setHospitalId(order.getHospital().getId());
				orderLogMapper.insertOrderLog(log);

				if (OrderErrorCode.Success.getCode().intValue() == log
						.getErrorCode()) {
					this.updateAfterExport(order.getId(), this.defaultManagerId);
					logger.info("订单导入成功,orderNum is {}", log.getOrderNum());
					// 订单已经导入到体检中心，且状态仍为未导出，更新订单状态。
				} else if (OrderErrorCode.OrderExist.getCode().intValue() == log
						.getErrorCode() && !order.getIsExport()) {
					this.updateAfterExport(order.getId(), this.defaultManagerId);
					logger.info("订单导入成功,orderNum is {}",log.getOrderNum());
					// 单位不存在,设置订单状态为导入失败
				} else if (OrderErrorCode.CompanyNotExist.getCode().intValue() == log
						.getErrorCode()) {
					logger.error("订单导入失败,错误原因:{},orderNum is {}",
							log.getErrorText(), log.getOrderNum());
					sendErrorOrder(Arrays.asList(new OrderSyncError(log
							.getOrderNum(), log.getErrorCode(), log
							.getErrorText())));
				} else {
					logger.error("订单导入失败,错误原因:{},orderNum is {}",
							log.getErrorText(), log.getOrderNum());
				}
			}

			logger.info("orderId is {},orderNum is {},hospitalId is {}", log.getOrderId(), log.getOrderNum(),
					log.getHospitalId());

		}

	}

	private void updateAfterExport(Integer orderId, Integer operatorId) {
		UpdateOrderAfterExportDTO updateOrderAfterExportDTO = new UpdateOrderAfterExportDTO();
		updateOrderAfterExportDTO.setOrderId(orderId);
		updateOrderAfterExportDTO.setExport(true);

		Account account = new Account();
		account.setId(-1);
		updateOrderAfterExportDTO.setOperator(account);
		orderForMediatorService
				.updateOrderAfterExport(updateOrderAfterExportDTO);
	}

	@Override
	public List<String> sendDoneOrder(List<AccomplishOrder> list) {
		return getMediatorRefundService().orderRefund(list);
	}

	@Override
	public List<OrderDto> getImmediateOrder() {

		List<MediatorOrderExportHis> exportorder = mediatorOrderExportHisDAO.getMediatorHospitalOrderExportByPage(
				envKeeper.getHospital(), OrderExportType.ImmediateAppointment.getCode(), 0,
				new Page(1, orderExportPageSzie));
		List<Integer> orderIdList = Lists.newArrayList();
		List<Integer> orderEx = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(exportorder)) {
			exportorder.stream().forEach(item -> {
				orderIdList.add(item.getOrderId());
			});
			Set<Integer> set = Sets.newHashSet();
			set.addAll(orderIdList);
			orderEx = new ArrayList<Integer>(set);
			
			List<OrderDto> result = getMediatorOrderService().getExportableOrder(envKeeper.getHospital(), orderEx);
			mediatorOrderExportHisDAO.updateOrderExportByOrderIds(orderEx, 1);
			return result;
		}else {
			return Collections.emptyList();
		}

	}

	@Override
	public List<String> sendErrorOrder(List<OrderSyncError> errorOrderList) {
		logger.info("同步{}条导入失败订单", errorOrderList.size());
		return getMediatorOrderService().dealErrorOrder(errorOrderList, envKeeper.getHospital());
	}

	@Override
	public List<String> sendAlarmOrder(List<OrderSyncError> alarmOrdersList) {
		logger.info("同步{}条告警订单", alarmOrdersList.size());
		return getMediatorOrderService().dealAlarmOrder(alarmOrdersList, envKeeper.getHospital());
	}

	private MediatorOrderService getMediatorOrderService() {
		return this.serviceLocator.getService(MediatorOrderService.class,
				"mediatorOrderService", envKeeper.getHospital());
	}

	private MediatorRefundService getMediatorRefundService() {
		return this.serviceLocator.getService(MediatorRefundService.class,
				"mediatorRefundService", envKeeper.getHospital());
	}
}
