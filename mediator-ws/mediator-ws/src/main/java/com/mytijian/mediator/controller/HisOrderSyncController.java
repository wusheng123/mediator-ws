package com.mytijian.mediator.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.common.json.JSON;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.mytijian.account.enums.AccountRelationTypeEnum;
import com.mytijian.account.enums.AccountStatusEnum;
import com.mytijian.account.enums.AddAccountTypeEnum;
import com.mytijian.account.enums.IdTypeEnum;
import com.mytijian.account.enums.MarriageStatusEnum;
import com.mytijian.account.model.Account;
import com.mytijian.account.model.AccountRelationInCrm;
import com.mytijian.account.service.AccountImportRegisterService;
import com.mytijian.account.service.AccountService;
import com.mytijian.base.result.Result;
import com.mytijian.common.util.IdCardValidate;
import com.mytijian.company.service.SyncCrmHisCompanyService;
import com.mytijian.mediator.bidirectionsync.constant.ResultMessageEunm;
import com.mytijian.mediator.bidirectionsync.dto.ExamItem;
import com.mytijian.mediator.bidirectionsync.dto.GenericPEServiceTaskDto;
import com.mytijian.mediator.bidirectionsync.dto.Meal;
import com.mytijian.mediator.bidirectionsync.dto.Order;
import com.mytijian.mediator.bidirectionsync.dto.OrderRequestValue;
import com.mytijian.mediator.bidirectionsync.dto.SyncResultMessage;
import com.mytijian.mediator.common.model.GenericPEServiceTaskConfig;
import com.mytijian.mediator.common.service.GenericPEServiceTaskService;
import com.mytijian.mediator.service.util.RasSecurityUtil;
import com.mytijian.order.base.service.OrderSyncService;
import com.mytijian.order.base.service.model.HisOrderCreateWrapper;
import com.mytijian.order.dto.doublesync.ExamItemDTO;
import com.mytijian.order.dto.doublesync.MealRelationDTO;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.resource.model.Address;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.service.HospitalService;

/**
 *描述:请添加一段类名描述
 *日期:2017年12月8日
 *时间:上午11:25:07
 *@author huangwei
*/
@RestController
public class HisOrderSyncController {
	
	
	private Logger logger = LoggerFactory.getLogger(HisOrderSyncController.class);
	
	@Resource(name="rasSecurityUtil")
	private RasSecurityUtil rASSecurityUtil;
	
	@Resource(name="genericPEServiceTaskService")
	private GenericPEServiceTaskService genericPEServiceTaskService;
	
	@Resource(name="syncCrmHisCompanyService")
	private SyncCrmHisCompanyService syncCrmHisCompanyService;
	
	@Resource(name="accountImportRegisterService")
	private AccountImportRegisterService accountImportRegisterService;
	
	@Resource(name="hospitalService")
	private HospitalService hospitalService;
	
	@Resource(name="orderSyncService")
	private OrderSyncService orderSyncService;
	
	@Resource(name="accountService")
	private AccountService accountService;
	/**
	 * @PathVariable("hospitalId")
	 * @RequestParam(value = "sourceDate", required = true)
	 * @param sourceDate
	 *            加密的源数据,该数据为Order的Json String加密数据
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/hisOrderSync", method = RequestMethod.POST)
	public String uploadHisOrder(@RequestBody(required = false) OrderRequestValue requestData) throws Exception {

		SyncResultMessage resultMessage;
		String sourceData = requestData.getSourceData();
		String decodeString;
		Gson gson = new Gson();
		// 解密数据,获取解密后的数据String
		try {
			decodeString = rASSecurityUtil.decrypt(sourceData);
		} catch (Exception e) {
			logger.error("数据解密失败,接受到的数据为:{}", sourceData, e);
			resultMessage = new SyncResultMessage();
			resultMessage.setResultCode(ResultMessageEunm.DECODE_FAILED.getCode());
			resultMessage.setResultMessage(ResultMessageEunm.DECODE_FAILED.getMessage() + "," + sourceData);
			String jStr = gson.toJson(resultMessage);
			return rASSecurityUtil.encrypt(jStr);
		}

		Order order;
		try {
			order = gson.fromJson(decodeString, Order.class);
		} catch (Exception e) {
			logger.error("Json 数据转成Order对象失败:{}", decodeString, e);
			resultMessage = new SyncResultMessage();
			resultMessage.setResultCode(ResultMessageEunm.CONVER_FAILED.getCode());
			resultMessage.setResultMessage(ResultMessageEunm.CONVER_FAILED.getMessage() + "," + decodeString);
			String jStr = JSON.json(resultMessage);
			return rASSecurityUtil.encrypt(jStr);
		}

		// 校验数据完整性
		resultMessage = validateOrder(order);
		if (StringUtils.isNotEmpty(resultMessage.getResultCode())) {
			String jStr = JSON.json(resultMessage);
			return rASSecurityUtil.encrypt(jStr);
		}
		// 调用单位模块接口
		Result<Integer> companyResult = syncCrmHisCompanyService.getCompanyIdByHospitalIdAndHisCompanyCode(
				order.getHospitalId(), order.getCompanyRelation().getCompanyId());
		
		if(!companyResult.isSuccess()){
			logger.error("单位创建失败,错误信息为:{}", companyResult.getErrorMsg());
			resultMessage = new SyncResultMessage();
			resultMessage.setResultCode(ResultMessageEunm.INSIDE_ERROR.getCode());
			resultMessage.setResultMessage(ResultMessageEunm.INSIDE_ERROR.getMessage());
			String jStr = JSON.json(resultMessage);
			return rASSecurityUtil.encrypt(jStr);
		}

		// 调用用户模块接口
		AccountRelationInCrm accountRelationInCrm = new AccountRelationInCrm();
		assembleAccountRelationInCrm(accountRelationInCrm, order, companyResult);
		Result<Integer> accountResult = accountImportRegisterService.accountCheckAndCreate(accountRelationInCrm);
		if(!accountResult.isSuccess()){
			logger.error("账户创建失败,错误信息为:{}", accountResult.getErrorMsg());
			resultMessage = new SyncResultMessage();
			resultMessage.setResultCode(ResultMessageEunm.INSIDE_ERROR.getCode());
			resultMessage.setResultMessage(ResultMessageEunm.INSIDE_ERROR.getMessage());
			String jStr = JSON.json(resultMessage);
			return rASSecurityUtil.encrypt(jStr);
		}
		// 调用订单接口
		HisOrderCreateWrapper orderWarperr = new HisOrderCreateWrapper();
		assembleHisOrderCreateWrapper(orderWarperr, order, companyResult, accountResult);
		Result result = orderSyncService.orderSync(orderWarperr);

		// 封装返回对象，转成json String,加密返回
		if (!result.isSuccess()) {
			logger.error("订单模块抛出错误,错误码:{},错误信息:{}.", result.getErrorCode(), result.getErrorMsg());
			resultMessage = new SyncResultMessage();
			resultMessage.setResultCode(ResultMessageEunm.INSIDE_ERROR.getCode());
			resultMessage.setResultMessage(ResultMessageEunm.INSIDE_ERROR.getMessage());
			String jStr = JSON.json(resultMessage);
			return rASSecurityUtil.encrypt(jStr);
		}
		resultMessage = new SyncResultMessage();
		resultMessage.setResultCode(ResultMessageEunm.SUCCEED.getCode());
		resultMessage.setResultMessage(ResultMessageEunm.SUCCEED.getMessage());
		Map resultMap = new HashMap();
		resultMap.put("orderNum", result.getResult());
		resultMessage.setDynamicAttribute(resultMap);
		String jStr = JSON.json(resultMessage);
		return rASSecurityUtil.encrypt(jStr);
	}

	
	/**
	 * 校验订单数据
	 * @param order
	 * @return
	 */
	private SyncResultMessage validateOrder(Order order) {
		SyncResultMessage resultMessage = new SyncResultMessage();
		if (order.getHospitalId() == null) {

			resultMessage.setResultCode(ResultMessageEunm.HOSPITAL_ID_IS_NULL.getCode());
			resultMessage.setResultMessage(ResultMessageEunm.HOSPITAL_ID_IS_NULL.getMessage());
			logger.error("hisExamNumber is :{},orderNum is :{},error message : {}",order.getHisExamNumber(),order.getOrderNum(),ResultMessageEunm.HOSPITAL_ID_IS_NULL.getMessage());
			return resultMessage;
		}
		if (order.getHisExamNumber() == null && order.getOrderNum() == null) {

			resultMessage.setResultCode(ResultMessageEunm.HISBM_ORDERNUM_BOTH_NULL.getCode());
			resultMessage.setResultMessage(ResultMessageEunm.HISBM_ORDERNUM_BOTH_NULL.getMessage());
			logger.error("hisExamNumber is :{},orderNum is :{},error message : {}",order.getHisExamNumber(),order.getOrderNum(),ResultMessageEunm.HISBM_ORDERNUM_BOTH_NULL.getMessage());
			return resultMessage;
		}
		if (order.getCustomerInfo() == null) {

			resultMessage.setResultCode(ResultMessageEunm.IDCARD_ILLEGAL.getCode());
			resultMessage.setResultMessage(ResultMessageEunm.IDCARD_ILLEGAL.getMessage());
			logger.error("hisExamNumber is :{},orderNum is :{},error message : {}",order.getHisExamNumber(),order.getOrderNum(),"用户信息为空");
			return resultMessage;
		}
		if (order.getCustomerInfo().getIdcard() == null) {

			resultMessage.setResultCode(ResultMessageEunm.IDCARD_ILLEGAL.getCode());
			resultMessage.setResultMessage(ResultMessageEunm.IDCARD_ILLEGAL.getMessage());
			logger.error("hisExamNumber is :{},orderNum is :{},error message : {}",order.getHisExamNumber(),order.getOrderNum(),"身份证ID为空");
			return resultMessage;
		}
		if (!IdCardValidate.isIdcard(order.getCustomerInfo().getIdcard())) {

			resultMessage.setResultCode(ResultMessageEunm.IDCARD_ILLEGAL.getCode());
			resultMessage.setResultMessage(
					ResultMessageEunm.IDCARD_ILLEGAL.getMessage() + "," + order.getCustomerInfo().getIdcard());
			logger.error("hisExamNumber is :{},orderNum is :{},error message : {}",order.getHisExamNumber(),order.getOrderNum(),"身份证ID校验失败");
			return resultMessage;
		}
		if (!IdCardValidate.getGender(order.getCustomerInfo().getIdcard())
				.equals(order.getCustomerInfo().getGender())) {

			resultMessage.setResultCode(ResultMessageEunm.SEX_UNCONFORMITY.getCode());
			resultMessage.setResultMessage(ResultMessageEunm.SEX_UNCONFORMITY.getMessage() + ",身份证号: "
					+ order.getCustomerInfo().getIdcard() + ",性别: " + order.getCustomerInfo().getGender());
			logger.error("hisExamNumber is :{},orderNum is :{},error message : {}",order.getHisExamNumber(),order.getOrderNum(),ResultMessageEunm.SEX_UNCONFORMITY.getMessage());
			return resultMessage;
		}
		if (CollectionUtils.isEmpty(order.getListExamItem())) {
			resultMessage.setResultCode(ResultMessageEunm.EXAM_ITME_IS_NULL.getCode());
			resultMessage.setResultMessage(ResultMessageEunm.EXAM_ITME_IS_NULL.getMessage());
			logger.error("hisExamNumber is :{},orderNum is :{},error message : {}",order.getHisExamNumber(),order.getOrderNum(),"体检项目为空");
			return resultMessage;
		}
		return resultMessage;
	}
	
	/**
	 *  组装账户管关联信息
	 * @param accountRelationInCrm
	 * @param order
	 * @param companyResult
	 */
	private void assembleAccountRelationInCrm(AccountRelationInCrm accountRelationInCrm, Order order,
			Result<Integer> companyResult) {
		accountRelationInCrm.setNewCompanyId(companyResult.getResult());
		accountRelationInCrm.setName(order.getCustomerInfo().getName());
		accountRelationInCrm.setMobile(order.getCustomerInfo().getMobile());
		accountRelationInCrm.setIdCard(order.getCustomerInfo().getIdcard());
		accountRelationInCrm.setStatus(AccountStatusEnum.NORMAL.getCode());
		accountRelationInCrm.setEmployeeId(order.getCustomerInfo().getWorkno());
		accountRelationInCrm.setIdType(IdTypeEnum.IDCARD.getCode());
		accountRelationInCrm.setGender(order.getCustomerInfo().getGender());
		accountRelationInCrm.setType(AccountRelationTypeEnum.MedicalReserver.getCode());
		if ("已婚".equals(order.getCustomerInfo().getMarriage())) {
			accountRelationInCrm.setMarriageStatus(MarriageStatusEnum.MARRIED.getCode());
		} else if ("未婚".equals(order.getCustomerInfo().getMarriage())) {
			accountRelationInCrm.setMarriageStatus(MarriageStatusEnum.UNMARRIED.getCode());
		} else {
			accountRelationInCrm.setMarriageStatus(null);
		}
		Address address = new Address();
		address.setAddress(order.getCustomerInfo().getAddress());
		accountRelationInCrm.setAddress(address);
		Hospital hospital = hospitalService.getHospitalById(order.getHospitalId());
		accountRelationInCrm.setManagerId(hospital.getDefaultManagerId());
		accountRelationInCrm.setBirthYear(IdCardValidate.getBirthYear(order.getCustomerInfo().getIdcard()));
		accountRelationInCrm.setOrganizationId(order.getHospitalId());
		accountRelationInCrm.setOrganizationType(OrganizationTypeEnum.HOSPITAL.getCode());
		accountRelationInCrm.setAddAccountType(AddAccountTypeEnum.idCard.name());
		Account account = accountService.getAccountById(hospital.getDefaultManagerId());
		if(account != null){
			accountRelationInCrm.setOperator(account.getName());
		}
		if(order.getCompanyRelation().getGroupName() == null){
			accountRelationInCrm.setGroup("双向同步");
		}else{
			accountRelationInCrm.setGroup(order.getCompanyRelation().getGroupName());
		}
		accountRelationInCrm.setDepartment(order.getCompanyRelation().getDepartment());
		accountRelationInCrm.setPosition(order.getCustomerInfo().getPosition());
		accountRelationInCrm.setIsRetire(order.getCustomerInfo().getRetire());
		
		accountRelationInCrm.setCreateTime(new Date());
	}
	
	/**
	 *  组装同步订单信息
	 * @param orderWarperr
	 * @param order
	 * @param companyResult
	 * @param accountResult
	 * @throws ParseException
	 */
	private void assembleHisOrderCreateWrapper(HisOrderCreateWrapper orderWarperr, Order order,
			Result<Integer> companyResult, Result<Integer> accountResult) throws ParseException {
		orderWarperr.setHisbm(order.getHisExamNumber());
		orderWarperr.setOrderNum(order.getOrderNum());
		orderWarperr.setAccountId(accountResult.getResult());
		orderWarperr.setHospitalId(order.getHospitalId());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));
		orderWarperr.setExamDate(dateFormat.parse(order.getExamDate()));
		orderWarperr.setHospitalCompanyId(companyResult.getResult());
		orderWarperr.setHisExamStatus(order.getExamStatus());
		orderWarperr.setExamType(order.getExamType());
		orderWarperr.setExamClass(order.getExamClass());
		orderWarperr.setOwner(order.getOwner());
		orderWarperr.setPayType(order.getPayType());
		orderWarperr.setVip(order.getVip());
		orderWarperr.setOrderMoney(order.getOrderMoney());
		orderWarperr.setOrderDiscount(order.getOrderDiscount());
		orderWarperr.setRemark(order.getRemark());

		MealRelationDTO meal = new MealRelationDTO();
		Meal mealRel = order.getMealRelation();
		if (mealRel != null) {
			meal.setMealCode(mealRel.getMealCode());
			meal.setMealName(mealRel.getMealName());
			meal.setOriginalPrice(mealRel.getOriginalPrice());
			meal.setGender(mealRel.getGender());
			meal.setDiscount(mealRel.getDiscount());
			meal.setDiscountPrice(mealRel.getDiscountPrice());
		}

		List<ExamItemDTO> examItemList = Lists.newArrayList();

		for (ExamItem examItem : order.getListExamItem()) {
			ExamItemDTO examItemDTO = new ExamItemDTO();
			BeanUtils.copyProperties(examItem, examItemDTO);
			examItemList.add(examItemDTO);
		}
		meal.setExamItemDTOList(examItemList);
		orderWarperr.setMealRelationDTO(meal);
	}
	
	@RequestMapping(value = "/taskConfig/{hospitalId}", method = RequestMethod.GET)
	public List<GenericPEServiceTaskDto> getTaskConfig(@PathVariable("hospitalId") Integer hospitalId) {
		List<GenericPEServiceTaskConfig> configList = genericPEServiceTaskService
				.listGenericPEServiceTaskConfigByHospitalId(hospitalId);

		List<GenericPEServiceTaskDto> taskDtoList = Lists.newArrayList();

		if (CollectionUtils.isNotEmpty(configList)) {
			for (GenericPEServiceTaskConfig config : configList) {
				GenericPEServiceTaskDto taskDto = new GenericPEServiceTaskDto();
				BeanUtils.copyProperties(config, taskDto);
				taskDtoList.add(taskDto);
			}
		}
		return taskDtoList;

	}
}
