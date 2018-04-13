package com.mytijian.mediator.order.base.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.mytijian.order.base.mongo.MongoOrderWriteService;
import com.mytijian.order.dto.ExportOrderDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Objects;
import com.mytijian.account.enums.GenderEnum;
import com.mytijian.account.model.AccountRelationInCrm;
import com.mytijian.company.hospital.service.HospitalCompanyService;
import com.mytijian.company.hospital.service.model.HospitalCompany;
import com.mytijian.company.model.AccountCompany;
import com.mytijian.company.model.CompanyHisRelation;
import com.mytijian.company.service.constant.CompanyConstant;
import com.mytijian.mediator.company.inner.service.impl.SyncCrmHisCompanyServiceAdapter;
import com.mytijian.mediator.order.DAO.MediatorOrderExportHisMapper;
import com.mytijian.mediator.order.base.service.MediatorOrderService;
import com.mytijian.mediator.order.constants.OrderExtPropertyKey;
import com.mytijian.mediator.order.dto.OrderDto;
import com.mytijian.mediator.order.dto.OrderSyncError;
import com.mytijian.mediator.order.enums.OrderErrorCode;
import com.mytijian.mediator.order.enums.OrderExportState;
import com.mytijian.mediator.order.enums.OrderExportType;
import com.mytijian.mediator.order.model.MediatorOrderExportHis;
import com.mytijian.mediator.service.util.Offer2ResourceAPIResolver;
import com.mytijian.offer.meal.model.MealSnap;
import com.mytijian.order.base.service.OrderForMediatorService;
import com.mytijian.order.base.service.dto.UpdateMediatorMongoDTO;
import com.mytijian.order.base.service.model.ExportableOrderQuery;
import com.mytijian.order.base.service.model.MongoOrderQuery;
import com.mytijian.order.base.snapshot.model.AccountCompanySnapshot;
import com.mytijian.order.enums.OrderStatusEnum;
import com.mytijian.order.model.MongoOrder;
import com.mytijian.order.model.Order;
import com.mytijian.order.service.OrderService;
import com.mytijian.payment.model.Invoice;
import com.mytijian.payment.service.InvoiceService;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.report.exceptions.SyncException;
import com.mytijian.shared.mediator.util.CollectionUtil;

@Service("mediatorOrderService")
public class MediatorOrderServiceImpl implements MediatorOrderService {

	private Logger logger = LoggerFactory.getLogger(MediatorOrderServiceImpl.class);

	@Resource(name = "orderService")
	private OrderService orderService;

	@Resource(name = "mongoOrderWriteService")
	private MongoOrderWriteService mongoOrderWriteService;
	
	@Value("${orderExportPageSzie}")
	private Integer orderExportPageSzie;

	@Resource(name = "syncCrmHisCompanyServiceAdapter")
	private SyncCrmHisCompanyServiceAdapter syncCrmHisCompanyServiceAdapter;
	
	@Resource(name="mediatorOrderExportHisMapper")
	private MediatorOrderExportHisMapper mediatorOrderExportHisMapper;
	
	@Resource(name = "invoiceService")
	private InvoiceService invoiceService;
	
	@Resource(name = "orderForMediatorService")
	private OrderForMediatorService orderForMediatorService;
	
	@Resource(name = "hospitalCompanyService")
	private HospitalCompanyService hospitalCompanyService;
	
	@Override
	public PageView<OrderDto> getExportableOrder(Integer hospitalId, Page page) {

		List<OrderDto> totallist = new ArrayList<OrderDto>();
		page.setRowCount(totallist.size());
		PageView<OrderDto> view = new PageView<OrderDto>(totallist, page);
		try {

			// 查询排序订单
			totallist.addAll(getOrderByType(hospitalId,
					OrderExportType.SortAsXls, orderExportPageSzie));

			if (totallist.size() >= orderExportPageSzie) {
				return view;
			}

			// 查询mongodb
			totallist.addAll(getOrder(hospitalId, orderExportPageSzie
					- totallist.size()));

			totallist = totallist.stream()
					.filter(CollectionUtil.distinctByKey(f -> f.getOrderNum()))
					.collect(Collectors.toList());
		} catch (Throwable e) {
			logger.error("getExportableOrder error hospitalId :" + hospitalId,
					e);
		}

		return view;

	}
	
	private List<OrderDto> getOrderByType(Integer hospitalId,
			OrderExportType type, int pageSize) {
		// orderList已排序 
		List<MediatorOrderExportHis> orderList = mediatorOrderExportHisMapper
				.getMediatorHospitalOrderExportByPage(hospitalId,
						type.getCode(), 0, new Page(1, pageSize));

		List<OrderDto> orderDtos = new ArrayList<OrderDto>();

		if (CollectionUtils.isNotEmpty(orderList)) {
			// orderid 去重
			orderList = orderList.stream().filter(f -> f.getOrderId() != null)
					.filter(CollectionUtil.distinctByKey(f -> f.getOrderId()))
					.collect(Collectors.toList());

			// 从mongodb拿数据。为了使导出的订单顺序按照xls顺序，所以遍历取订单数据。
			for (MediatorOrderExportHis obj : orderList) {
				orderDtos.addAll(getExportableOrder(hospitalId,
						Arrays.asList(obj.getOrderId())));
			}
			
			
			return orderDtos;
		} else {
			return Collections.emptyList();
		}

	}
	
	@Override
	public PageView<OrderDto> getExportableOrderByExportType(Integer hospitalId, OrderExportType orderExportType,
			Page page) {
		// 默认, 兼容getExportableOrder
		int exportType = OrderExportType.SortAsXls.getCode();
		if (orderExportType != null && orderExportType.getCode() != null) {
			exportType = orderExportType.getCode();
		}
		
		// 先读数据库需要排序的订单，未满一页再读取Mongo
		List<MediatorOrderExportHis> sortedOrderList = mediatorOrderExportHisMapper
				.getMediatorHospitalOrderExportByPage(hospitalId, exportType, 0, new Page(1, orderExportPageSzie));

		List<OrderDto> orderDtos = new ArrayList<OrderDto>();

		if (CollectionUtils.isNotEmpty(sortedOrderList)) {
			// orderid 去重
			sortedOrderList = sortedOrderList.stream()
					.filter(f -> f.getOrderId() != null)
					.filter(CollectionUtil.distinctByKey(f -> f.getOrderId()))
					.collect(Collectors.toList());

			for (MediatorOrderExportHis obj : sortedOrderList) {
				orderDtos.addAll(getExportableOrder(hospitalId,
						Arrays.asList(obj.getOrderId())));
			}

		}

		// 读取mongo
		if (orderDtos.size() < orderExportPageSzie) {
			orderDtos.addAll(getOrder(hospitalId, orderExportPageSzie
					- orderDtos.size()));

		}

		page.setRowCount(orderDtos.size());
		PageView<OrderDto> view = new PageView<OrderDto>(orderDtos, page);
		return view;
	}
	
	private List<OrderDto> getOrder(Integer hospitalId, int size) {
		ExportableOrderQuery query = new ExportableOrderQuery();
		query.setHospitalId(hospitalId);
		query.setLimit(size);
		List<MongoOrder> mongoOrderList = null;
		try {
			mongoOrderList = orderForMediatorService.getExportableOrder(query);
		} catch (Exception e) {
			logger.error("getExportableOrder error.hospital id is {}",
					hospitalId, e);
			return Collections.emptyList();
		}

		List<OrderDto> orderDtos = new ArrayList<OrderDto>();
		for (MongoOrder mongoOrder : mongoOrderList) {
			OrderDto orderDto = this.resolve(mongoOrder, hospitalId);
			if (orderDto != null) {
				orderDtos.add(orderDto);
			}
		}

		return orderDtos;
	}
	
	private OrderDto resolve(MongoOrder mongoOrder, Integer hospitalId) {
		String orderNum = mongoOrder.getOrderNum();

		try {
			if (hospitalId == null) {
				throw new SyncException("订单的hospitalId为空,ordernum = "
						+ orderNum);
			}

			Order order = orderService.getOrderByOrderNum(orderNum);
			if (order == null) {
				logger.error("ORDER_NOT_EXIST,ordernum : {}", orderNum);
				return null;
			}

			List<Integer> statusList = Arrays.asList(
					OrderStatusEnum.appointmentSuccess.getCode(),
					OrderStatusEnum.exportFailed.getCode());

			if (!statusList.contains(order.getStatus())) {
				logger.error(
						"订单状态不是已预约/导出错误状态,order status is {},orderId is {}",
						order.getStatus(), order.getId());
				return null;

			}
			OrderDto orderDto = new OrderDto();
			Map<String, Object> attrMap = new HashMap<String, Object>();
			orderDto.setDynamicAttributes(attrMap);

			orderDto.setOrderNum(orderNum);
			orderDto.setOrderId(mongoOrder.getId());
			
			Integer newCompanyId = order.getExamCompanyId();
			HospitalCompany hospitalCompany = hospitalCompanyService
					.getHospitalCompanyById(newCompanyId);

			logger.info(
					"order id : {},order num : {},company id : {},company name : {}",
					mongoOrder.getId(), orderNum, newCompanyId,
					hospitalCompany.getName());

			boolean isSupport = syncCrmHisCompanyServiceAdapter
					.supportNewSyncCompany(hospitalId);

			if (isSupport) {
				
				CompanyHisRelation hisCompany = syncCrmHisCompanyServiceAdapter
						.getHisCompany(order.getExamCompanyId());
				if (hisCompany == null) {
					this.setExportFailed(hospitalId, orderNum, hospitalCompany);
					return null;
				} else {
					orderDto.setHisCompId(hisCompany.getHisCompanyCode());
					orderDto.setHisCompName(hisCompany.getHisCompanyName());
				}
			} else {
				logger.info("use old company sync");
			}
			
			HospitalCompany examCompany = this.getCompany(hospitalCompany);
			orderDto.setCompanyId(examCompany.getId());
			orderDto.setExamCompany(examCompany.getName());

			this.setBasicInfo(mongoOrder, orderDto);
			// 设置动态属性
			orderDto.setDynamicAttributes(this.generateDynamicAttrs(mongoOrder,
					orderDto, order));

			this.parseIdCard(mongoOrder, orderDto.getAccountRelation(),
					orderDto.getDynamicAttributes());
			orderDto.setExamItemSnapList(Offer2ResourceAPIResolver
					.resolveOffer2ResourceItemSnapList(orderForMediatorService
							.getExportExamItemsByOrderId(mongoOrder.getId())));
			this.handleOrderDto(orderDto);

			return orderDto;
		} catch (SyncException syncException) {
			// 在mongo记录服务端导出失败状态
			logger.error(
					"RESOLVE_ORDER_DTO_ERROR.order num is "
							+ mongoOrder.getOrderNum(), syncException);
			dealAlarmOrder(Arrays.asList(new OrderSyncError(mongoOrder
					.getOrderNum(), OrderExportState.ThrowExceptionWhenExport
					.getCode(), OrderExportState
					.getCodeString(OrderExportState.ThrowExceptionWhenExport
							.getCode()))), hospitalId);
			return null;
		} catch (Exception e) {
			logger.error(
					"RESOLVE_ORDER_DTO_ERROR.order num is "
							+ mongoOrder.getOrderNum(), e);
			dealAlarmOrder(Arrays.asList(new OrderSyncError(mongoOrder
					.getOrderNum(), OrderExportState.ThrowExceptionWhenExport
					.getCode(), OrderExportState
					.getCodeString(OrderExportState.ThrowExceptionWhenExport
							.getCode()))), hospitalId);
			return null;
		}
	}

	/**
	 * 对1585做兼容
	 * 
	 * @param hospitalCompany
	 * @return
	 */
	private HospitalCompany getCompany(HospitalCompany hospitalCompany) {
		if (hospitalCompanyService.isGuestCompany(hospitalCompany.getId())) {
			logger.info("is guest compamy,convert to 1585,company id is :{}",
					hospitalCompany.getId());
			HospitalCompany examCompany = new HospitalCompany();
			examCompany.setId(CompanyConstant.EXAM_COMPANY_ID);
			examCompany.setName(hospitalCompany.getName());
			return examCompany;
		}

		if (hospitalCompany.getTbExamCompanyId() != null) {
			logger.info("old company id is {}",
					hospitalCompany.getTbExamCompanyId());
			HospitalCompany examCompany = new HospitalCompany();
			examCompany.setId(hospitalCompany.getTbExamCompanyId());
			examCompany.setName(hospitalCompany.getName());
			return examCompany;
		}

		return hospitalCompany;
	}

	protected void handleOrderDto(OrderDto orderDto) {

	}
	
	private void setExportFailed(Integer hospitalId, String orderNum,
			HospitalCompany hospitalCompany) {
		logger.warn(
				"NO_HIS_COMPANY_ASSOCIATED.order num : {},crm company id : {}",
				orderNum, hospitalCompany.getId());
		// 设置订单状态为导出错误
		OrderSyncError error = new OrderSyncError();
		error.setOrderNum(orderNum);
		error.setErrorCode(OrderErrorCode.NoHisCompanyAssociated.getCode());
		String msg = String.format("crm单位[id=%d,name=%s]未关联his单位",
				hospitalCompany.getId(), hospitalCompany.getName());
		error.setErrorText(msg);
		dealErrorOrder(Arrays.asList(error), hospitalId);
	}
	
	private void setBasicInfo(MongoOrder mongoOrder, OrderDto orderDto) {
		orderDto.setExamDate(mongoOrder.getExamDate());
		orderDto.setMealName(mongoOrder.getMealName());
		orderDto.setExportDiscount(String.valueOf(mongoOrder.getExportDiscount()));

		// 是否需要纸质报告的设置
		orderDto.setNeedPaperReport(mongoOrder.getNeedPaperReport() == null ? true : mongoOrder.getNeedPaperReport());

		orderDto.setRemark(StringUtils.isEmpty(mongoOrder.getRemark()) ? null : mongoOrder.getRemark());

		orderDto.setHospital(mongoOrder.getHospital());

		orderDto.setPayType(mongoOrder.getPayType());
		orderDto.setRetire(mongoOrder.getRetireLabel());

		BigDecimal oneHundred = new BigDecimal("100");
		orderDto.setOrderMoney(new BigDecimal(mongoOrder.getExportOrderPrice()).multiply(oneHundred).doubleValue());
		orderDto.setVip(mongoOrder.getVip());

		// 线上金额
		boolean isLocalePay = mongoOrder.getIsLocalePay() == null ? false : mongoOrder.getIsLocalePay();

		orderDto.setOnlinePayMoney(
				isLocalePay ? 0d : new BigDecimal(mongoOrder.getExportSelfMoney()).multiply(oneHundred).doubleValue());

		// 线下需要支付金额
		;
		orderDto.setOfflinePayMoney(StringUtils.isEmpty(mongoOrder.getOfflinePayMoney()) ? 0d
				: new BigDecimal(mongoOrder.getOfflinePayMoney()).multiply(oneHundred).doubleValue());

		AccountRelationInCrm accountRelationInCrm = mongoOrder.getAccountRelation();
		accountRelationInCrm.setMobile(accountRelationInCrm.getInitialMobile() == null
				? accountRelationInCrm.getMobile() : accountRelationInCrm.getInitialMobile());
		orderDto.setAccountRelation(accountRelationInCrm);

		AccountCompanySnapshot acSnap = mongoOrder.getAccountCompany();
		AccountCompany accountCompany = new AccountCompany();
		accountCompany.setName(acSnap.getName());
		accountCompany.setOwner(acSnap.getOwner());
		orderDto.setAccountCompany(accountCompany);
	}

	protected Map<String, Object> generateDynamicAttrs(MongoOrder mongoOrder,
			OrderDto orderDto,Order order) throws SyncException{
		
		// 套餐id
		MealSnap mealSnap = JSON.parseObject(order.getMealDetail(),
				MealSnap.class);
		orderDto.getDynamicAttributes().put(OrderExtPropertyKey.MY_MEAL_ID,
				mealSnap.getId());
		
		// 体检时间段		
		String examPeriod = mongoOrder.getExamTimeIntervalName();
		orderDto.getDynamicAttributes().put(OrderExtPropertyKey.EXAM_PERIOD,
				examPeriod == null ? StringUtils.EMPTY : examPeriod);
		
		// C端下单,batchId为0,CRM下单,batchId不为0
		orderDto.getDynamicAttributes().put(OrderExtPropertyKey.BATCH_ID,
				String.valueOf(mongoOrder.getBatchId()));
				
		// 是否需要发票
		Invoice invoice = invoiceService
				.getInvoiceByOrderId(mongoOrder.getId());
		orderDto.getDynamicAttributes().put(OrderExtPropertyKey.HAS_INVOICE,
				invoice != null ? "1" : "0");
		
		// 体检中心id
		orderDto.getDynamicAttributes().put(OrderExtPropertyKey.HOSPITAL_ID,
				orderDto.getHospital().getId());
		
		// 预约人姓名
		String operator = mongoOrder.getOperator();
		orderDto.getDynamicAttributes().put(OrderExtPropertyKey.OPERATOR_NAME,
				operator != null ? operator : StringUtils.EMPTY);
		
		// 职级
		orderDto.getDynamicAttributes().put(OrderExtPropertyKey.POSITION,
				resolveAttri(orderDto.getAccountRelation().getPosition()));
		
		// 退休状态
		orderDto.getDynamicAttributes().put(OrderExtPropertyKey.RETIRE_STATUS,
				resolveAttri(orderDto.getRetire()));
		
		// 保健级别
		orderDto.getDynamicAttributes().put(OrderExtPropertyKey.HEALTH_LEVEL,
				resolveAttri(orderDto.getAccountRelation().getHealthLevel()));
		
		// 婚姻状态
		orderDto.getDynamicAttributes().put(
				OrderExtPropertyKey.MARRIAGE_STATUS,
				toMarriageStatus(orderDto.getAccountRelation()
						.getMarriageStatus()));
		
		return orderDto.getDynamicAttributes();
	}
	
	private String toMarriageStatus(Integer marriageStatus) {
		return marriageStatus == null ? StringUtils.EMPTY : (marriageStatus
				.intValue() == 0 ? "未婚" : "已婚");
	}
	
	private String resolveAttri(String value) {
		return StringUtils.isNotBlank(value) ? value : StringUtils.EMPTY;
	}
	
	private void parseIdCard(MongoOrder mongoOrder, AccountRelationInCrm ar,
			Map<String, Object> dynamicAttributes) {
		Integer gender = "男".equals(mongoOrder.getGenderLabel()) ? GenderEnum.MALE
				.getCode() : GenderEnum.FEMALE.getCode();
		if (StringUtils.isBlank(ar.getIdCard())) {

			// 无身份证的情况，根据birthYear计算年龄
			Integer birthYear = ar.getBirthYear() == null ? Calendar
					.getInstance().get(Calendar.YEAR) : ar.getBirthYear();
			ar.setAge(Calendar.getInstance().get(Calendar.YEAR) - birthYear);
			ar.setIdCard(lpad(
					4,
					Integer.parseInt(Integer.toString(ar.getAge())
							+ Integer.toString(gender))));
			dynamicAttributes.put(
					OrderExtPropertyKey.BIRTH_DAY,
					ar.getBirthYear() == null ? (Calendar.getInstance().get(
							Calendar.YEAR) + "-01-01")
							: (ar.getBirthYear() + "-01-01"));
		} else {
			dynamicAttributes.put(OrderExtPropertyKey.BIRTH_DAY,
					mongoOrder.getBirthDate());
		}

		dynamicAttributes.put(OrderExtPropertyKey.GENDER, gender);
		dynamicAttributes.put(OrderExtPropertyKey.AGE, ar.getAge());
	}
	
	/* (non-Javadoc)
	 * @see com.mytijian.order.service.MediatorOrderService#countExportableOrder(java.lang.Integer)
	 */
	@Override
	public int countExportableOrder(Integer hospitalId) {
		return 0;
	}

	private String lpad(int length, int number) {
		String f = "%0" + length + "d";
		return String.format(f, number);
	}

	@Override
	public List<OrderDto> getExportableOrder(Integer hospitalId,
			List<Integer> orderList) {
		MongoOrderQuery mongoOrderQuery = new MongoOrderQuery();
		mongoOrderQuery.setIds(orderList);
		List<MongoOrder> mongoOrderList = orderForMediatorService
				.getOrders(mongoOrderQuery);
		List<OrderDto> orderDtos = new ArrayList<OrderDto>();

		for (MongoOrder mongoOrder : mongoOrderList) {
			OrderDto orderDto = this.resolve(mongoOrder, hospitalId);
			if (orderDto != null) {
				orderDtos.add(orderDto);
			}
		}
		return orderDtos;
	}

	@Override
	public List<String> dealErrorOrder(List<OrderSyncError> errorOrderList, Integer hospitalId) {
		List<String> orderList = new ArrayList<String>();
		for (OrderSyncError orderSyncError : errorOrderList) {
			
			MongoOrder order = getOrderByOrderNum(orderSyncError.getOrderNum());
			if (order == null) {
				logger.warn("在mongo中找不到该订单，订单号为{}", orderSyncError.getOrderNum());
				continue;
			}

			// 过滤状态为导出失败的订单
			if (order.getStatus() == OrderStatusEnum.exportFailed.getCode()) {
				continue;
			}

			// 检查hospitalid是否一致
			if (!isSameHosp(order, hospitalId.intValue())) {
				logger.warn("mongo与心跳的hospitalid不一致，订单号 :{},心跳hospitalid:{}", orderSyncError.getOrderNum(), hospitalId);
				continue;
			}

			// 更新订单和mongo状态，在mongo中写入错误信息
			updateExportFailed(order.getOrderNum(),order.getId(), OrderStatusEnum.exportFailed,
					orderSyncError.getErrorText());
			
			orderList.add(orderSyncError.getOrderNum());

			logger.info("体检中心ID is {}, 错误订单 is {}", hospitalId, orderSyncError);
		}
		
		return orderList;

	}

	private MongoOrder getOrderByOrderNum(String orderNum) {
		MongoOrderQuery mongoOrderQuery = new MongoOrderQuery();
		mongoOrderQuery.setOrderNum(orderNum);
		List<MongoOrder> mongoOrderList = orderForMediatorService
				.getOrders(mongoOrderQuery);

		return CollectionUtils.isEmpty(mongoOrderList) ? null : mongoOrderList
				.get(0);
	}
	
	private void updateExportFailed(String orderNum,Integer orderId, OrderStatusEnum status, String errorMsg) {
		orderService.updateOrderStatus(orderId, status);
		ExportOrderDTO exportOrderDTO = new ExportOrderDTO();
		exportOrderDTO.setOrderNum(orderNum);
		exportOrderDTO.setStatus(status.getCode());
		exportOrderDTO.setExportFailedMsg(errorMsg);
		mongoOrderWriteService.updateMongoExportOrder(exportOrderDTO);
	}

	@Override
	public List<String> dealAlarmOrder(List<OrderSyncError> alarmOrderList,
			Integer hospitalId) {
		List<String> orderList = new ArrayList<String>();
		for (OrderSyncError orderSyncError : alarmOrderList) {

			MongoOrder order = getOrderByOrderNum(orderSyncError.getOrderNum());
			if (order == null) {
				logger.warn("在mongo中找不到该订单，订单号为{}",
						orderSyncError.getOrderNum());
				orderList.add(orderSyncError.getOrderNum());
				continue;
			}

			// 检查hospitalid是否一致
			if (!isSameHosp(order, hospitalId.intValue())) {
				logger.warn("mongo与心跳的hospitalid不一致，订单号 :{},心跳hospitalid:{}",
						orderSyncError.getOrderNum(), hospitalId);
				orderList.add(orderSyncError.getOrderNum());
				continue;
			}

			// 已导入his的订单不再更新mongodb,下次心跳不再同步该订单
			if (order.getExportState() != null
					&& order.getExportState().intValue() == orderSyncError
							.getErrorCode()
					&& orderSyncError.getErrorCode().intValue() == OrderExportState.ImportedIntoHis
							.getCode()) {
				orderList.add(orderSyncError.getOrderNum());
				continue;
			}

			// 已有告警，且未变，不再更新mongodb
			if (order.getExportState() != null
					&& order.getExportState().intValue() == orderSyncError
							.getErrorCode()) {
				continue;
			}
			
			this.updateExportState(order.getId(),
					orderSyncError.getErrorCode(),
					orderSyncError.getErrorText());

			
		}
		return orderList;
	}
	
	private boolean isSameHosp(MongoOrder order, Integer soapHospId) {
		return Objects.equal(order.getHospital().getId(), soapHospId);
	}
	
	private void updateExportState(Integer orderId, Integer exportState,
			String exportMsg) {
		UpdateMediatorMongoDTO dto = new UpdateMediatorMongoDTO();
		MongoOrder where = new MongoOrder();
		where.setId(orderId);

		MongoOrder set = new MongoOrder();
		set.setExportState(exportState);
		set.setExportMsg(exportMsg);

		dto.setWhere(where);
		dto.setSet(set);

		orderForMediatorService.updateMediatorMongo(dto);
	}
	
//	public static void main(String[] args) {
//		BigDecimal b1 = new BigDecimal("2167.99");
//		BigDecimal b2 = new BigDecimal("100");
//		System.out.println(b1.multiply(b2).doubleValue());
//	}
	
}
