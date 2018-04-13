package com.mytijian.mediator.order.inner.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.mytijian.account.enums.AccountRelationTypeEnum;
import com.mytijian.mediator.order.constants.OrderExceptionCode;
import com.mytijian.mediator.order.dto.OrderSortDto;
import com.mytijian.mediator.order.enums.OrderExportType;
import com.mytijian.mediator.order.model.MediatorOrderExportHis;
import com.mytijian.mediator.order.postDAO.MediatorOrderExportHisDAO;
import com.mytijian.order.base.service.OrderForMediatorService;
import com.mytijian.order.base.service.model.MongoOrderQuery;
import com.mytijian.order.model.MongoOrder;
import com.mytijian.shared.mediator.util.CollectionUtil;

@Service("mediatorExportOrderService")
public class MediatorExportOrderServiceImpl implements MediatorExportOrderService {

	private Logger logger = LoggerFactory.getLogger(MediatorExportOrderServiceImpl.class);

	@Resource(name = "mediatorOrderExportHisDAO")
	private MediatorOrderExportHisDAO mediatorOrderExportHisDAO;
	
	@Resource(name = "orderForMediatorService")
	private OrderForMediatorService orderForMediatorService;

	/**
	 * crm导入使用同步方式
	 */
	@Override
	public void addExportOrders(
			List<MediatorOrderExportHis> mediatorOrderExportHis,
			OrderExportType orderExportType) {
		try {
			if (!isDeal(mediatorOrderExportHis, orderExportType)) {
				return;
			}

			// 默认值
			orderExportType = Optional.ofNullable(orderExportType).orElse(
					OrderExportType.SortAsXls);

			logger.info("order size : {},export type : {}",
					mediatorOrderExportHis.size(), orderExportType.getMessage());

			List<OrderSortDto> orderSortDtoList = getOrders(mediatorOrderExportHis);
			if (orderExportType == OrderExportType.SortAsXls) {
				sortAndSetSeq(orderSortDtoList);
				sortMedicalUser(orderSortDtoList);
			}

			List<MediatorOrderExportHis> exportOrderList = Lists.newArrayList();
			for (OrderSortDto dto : orderSortDtoList) {
				MediatorOrderExportHis order = new MediatorOrderExportHis();
				BeanUtils.copyProperties(dto, order);
				order.setExportType(orderExportType.getCode());
				exportOrderList.add(order);
			}

			mediatorOrderExportHisDAO.addExportOrdersBatch(exportOrderList);
		} catch (Exception e) {
			logger.error("addExportOrders error", e);
		}
	}

	/**
	 * 根据参数判断是否执行导出操作，当导入订单列表为null或是empty时，返回false 否则返回 true
	 * @param mediatorOrderExportHis
	 * @param orderExportType
	 * @return
	 */
	private Boolean isDeal(List<MediatorOrderExportHis> mediatorOrderExportHis, OrderExportType orderExportType) {
		if (CollectionUtils.isEmpty(mediatorOrderExportHis)) {
			logger.warn(OrderExceptionCode.NO_ORDER_TO_EXPORT.getMessage(orderExportType.getMessage()));
			return false;
		}
		return true;
	}

	private void sortMedicalUser(List<OrderSortDto> orderSortDtoList) {
		// 过滤出体检人,查询预约人是否在list中，跟预约人排一起。
		List<OrderSortDto> medicalUserList = orderSortDtoList.stream()
				.filter(f -> f.getAccountRelation().getType() != null
						&& f.getAccountRelation().getType().intValue() == AccountRelationTypeEnum.MedicalUser.getCode())
				.collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(medicalUserList)) {
			logger.info("order列表里存在体检人");
			for (OrderSortDto medicalUser : medicalUserList) {
				logger.info("体检人:{}", ToStringBuilder.reflectionToString(medicalUser));
				Integer reserverId = medicalUser.getAccountRelation().getManagerId();// 预约人id

				if (reserverId != null) {
					Optional<OrderSortDto> reserverOptional = orderSortDtoList.stream()
							.filter(f -> f.getAccountRelation().getCustomerId() != null
									&& f.getAccountRelation().getCustomerId() == reserverId.intValue())
							.findFirst();

					// 存在预约人,把体检人的sequence设置成预约人的sequence,查询时排在一起
					if (reserverOptional.isPresent()) {
						OrderSortDto reserver = reserverOptional.get();// 预约人的sequence

						Optional<OrderSortDto> medicalUserOptional = orderSortDtoList.stream()
								.filter(f -> f.getOrderId().intValue() == medicalUser.getOrderId().intValue())
								.findFirst();

						if (medicalUserOptional.isPresent()) {
							medicalUserOptional.get().setSequence(reserver.getSequence());
						}

					}

				}
			}
		}
	}

	private List<OrderSortDto> getOrders(List<MediatorOrderExportHis> mediatorOrderExportHisList) {

		// 去重
		mediatorOrderExportHisList = mediatorOrderExportHisList.stream().filter(f -> f.getOrderId() != null)
				.filter(CollectionUtil.distinctByKey(f -> f.getOrderId())).collect(Collectors.toList());
		// 排序
		List<Integer> orderIds = mediatorOrderExportHisList.stream().map(MediatorOrderExportHis::getOrderId)
				.collect(Collectors.toList());

		MongoOrderQuery mongoOrderQuery = new MongoOrderQuery();
		mongoOrderQuery.setIds(orderIds);
		List<MongoOrder> mongoOrderList = orderForMediatorService
				.getOrders(mongoOrderQuery);
		
		logger.info("order size from mongo : {}", orderIds.size());
		List<OrderSortDto> orderSortDtoList = new ArrayList<OrderSortDto>();
		for (MongoOrder mongoOrder : mongoOrderList) {
			OrderSortDto orderSortDto = new OrderSortDto();
			
			orderSortDto.setOrderId(mongoOrder.getId());
			orderSortDto.setOrderNum(mongoOrder.getOrderNum());
			orderSortDto.setExamDate(mongoOrder.getExamDate());

			orderSortDto.setAccountRelation(mongoOrder.getAccountRelation());

			orderSortDto.setAccountId(mongoOrder.getAccountRelation()
					.getCustomerId());
			orderSortDto.setName(mongoOrder.getAccountRelation().getName());

			orderSortDto.setHospitalId(mongoOrder.getHospital().getId());
			orderSortDtoList.add(orderSortDto);
		}

		return orderSortDtoList;

	}

	private void sortAndSetSeq(List<OrderSortDto> orderSortDtoList) {
		Date currentTime = new Date();
		// 排序, 先根据创建时间排序， 如果创建时间一致则，根据sequence排序
		orderSortDtoList.sort(new Comparator<OrderSortDto>() {

			@Override
			public int compare(OrderSortDto o1, OrderSortDto o2) {

				Date seqTime1 = o1.getAccountRelation().getCreateTime();
				Date seqTime2 = o2.getAccountRelation().getCreateTime();

				seqTime1 = Optional.ofNullable(seqTime1).orElse(currentTime);
				seqTime2 = Optional.ofNullable(seqTime2).orElse(currentTime);
				
				int sort = seqTime1.compareTo(seqTime2);
				
				if (sort == 0) {
					Integer seq1 = o1.getAccountRelation().getSequence();
					Integer seq2 = o2.getAccountRelation().getSequence();
					return getSeqSort(seq1, seq2);
				}
				return sort;
			}

		});

		// 设置排序字段
		int maxSeq = mediatorOrderExportHisDAO.getMaxSequence();

		for (OrderSortDto orderSortDto : orderSortDtoList) {
			maxSeq += 1;
			orderSortDto.setSequence(maxSeq);
		}
	}
	
	private int getSeqSort(Integer seq1, Integer seq2) {
		
		if (seq1 == null && seq2 == null) {
			return 0;
		}

		// 体检人, sequence 为null的排最后
		if (seq1 == null && seq2 != null) {
			return 1;
		}

		if (seq1 != null && seq2 == null) {
			return -1;
		}
		
		return seq1.compareTo(seq2);
	}
	
}
