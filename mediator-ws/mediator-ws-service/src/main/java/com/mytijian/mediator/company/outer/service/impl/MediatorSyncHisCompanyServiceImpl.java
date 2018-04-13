/*
 * Copyright 2016 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.company.outer.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.mytijian.company.enums.CompanySyncStatus;
import com.mytijian.company.enums.SyncCrmHisCompanyOperEnum;
import com.mytijian.company.model.CompanyHisRelation;
import com.mytijian.distributed.lock.RedissonDistributedLock;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mytijian.company.service.SyncCrmHisCompanyService;
import com.mytijian.mediator.api.service.EnvironmentKeeper;
import com.mytijian.mediator.company.DAO.MediatorHisCompanyMapper;
import com.mytijian.mediator.company.DO.MediatorHisCompanyDO;
import com.mytijian.mediator.company.constants.CompanyExceptionCode;
import com.mytijian.mediator.company.constants.Creator;
import com.mytijian.mediator.company.constants.MediatorHisCompanyStatus;
import com.mytijian.mediator.company.model.MediatorHisCompanyModel;
import com.mytijian.mediator.company.outer.service.MediatorSyncHisCompanyService;
import com.mytijian.mediator.company.param.MediatorHisCompanyDAOQueryParam;
import com.mytijian.mediator.company.param.MediatorHisCompanyDAOUpdateParam;
import com.mytijian.mediator.exceptions.ExceptionFactory;
import com.mytijian.mediator.exceptions.ServiceExceptionCode;
import com.mytijian.shared.mediator.util.EnumUtil;

/**
 * 类MediatorSyncHisCompanyServiceImpl.java的实现描述：用于提供ws服务
 * 一个心跳周期流程如下，考虑断电情况：
 * 第一步：调用queryToCreateHisCompanyList 插叙需要创建的单位，并在his中创建单位
 * 第二步：调用SyncAllHisCompanyByPage将his单位全量同步会平台。（只有在医院单位信息有修改的情况下全量同步）
 * @author liangxing 2016年11月3日 下午4:55:00
 */
@Service("mediatorSyncHisCompanyService")
public class MediatorSyncHisCompanyServiceImpl implements MediatorSyncHisCompanyService {
	private final static String REFRESHING = "REFRESHING";
	private final static String REFRESHED = "REFRESHED";

	private Logger logger = LoggerFactory.getLogger(MediatorSyncHisCompanyServiceImpl.class);
	@Resource(name = "soapHeaderInterceptor")
	private EnvironmentKeeper envKeeper;

	@Resource(name = "mediatorHisCompanyMapper")
	private MediatorHisCompanyMapper 	mediatorHisCompanyMapper;

	@Resource(name = "syncCrmHisCompanyService")
	private SyncCrmHisCompanyService syncCrmHisCompanyService;


	/**
	 * ********************************这个接口调用存在风险一定要谨慎，会删除单位信息************************************
	 * 单位同步核心如何  hiscompany->mycompany(mytijiancompany)
	 * 每次分页同步到平台，isend标志是否为最后一页
	 * 每次全量同步中，将refresh执为1 ，同步结束后执为0（同步过程中服务中断，refresh保留为1，直至下次全量完整同步处理refresh执为0）
	 * 
	 */
	@Override
	public void SyncAllHisCompanyByPage(List<MediatorHisCompanyModel> hisCompanyList, Boolean isEnd) {
		logger.info("SyncAllHisCompanyByPage 单位同步，入参: hisCompanyList :{},  isEnd: {}", JSONObject.toJSONString(hisCompanyList), isEnd);

		if(hisCompanyList == null || hisCompanyList.isEmpty()){
			logger.info("hisCompanyList is null or empty");
			return;
		}
		if(isEnd == null){
			 logger.error(ServiceExceptionCode.INVALID_PARAM.getMessage("isEnd"));
	         throw ExceptionFactory.makeFault(ServiceExceptionCode.INVALID_PARAM,"isEnd is null");
		}
		//增改
		dispatcher(hisCompanyList);
		//删除
		if(isEnd){
			deleteHisCompany();
		}
	}
	private void deleteHisCompany(){
		MediatorHisCompanyDAOQueryParam mediatorHisCompanyDAOQueryParam = new MediatorHisCompanyDAOQueryParam();
		mediatorHisCompanyDAOQueryParam.setHospitalId(getHospitalId());
		mediatorHisCompanyDAOQueryParam.setRefresh(REFRESHED);
		mediatorHisCompanyDAOQueryParam.setHisCompanyCodeNotNull(true);
		logger.info("deleteHisCompany run delete mediatorHisCompanyDAOQueryParam is {}", JSONObject.toJSONString(mediatorHisCompanyDAOQueryParam));
		List<MediatorHisCompanyDO> toDeleteList = mediatorHisCompanyMapper.selectHisCompanyList(mediatorHisCompanyDAOQueryParam);
		if(toDeleteList != null && !toDeleteList.isEmpty()){
			List<Integer> ids = getIdsFromDO(toDeleteList);
			mediatorHisCompanyMapper.deleteHisCompany(ids);
			//发送删除消息
			toDeleteList.stream().forEach(item->{
				MediatorHisCompanyModel mediatorHisCompanyModel = new MediatorHisCompanyModel();
				BeanUtils.copyProperties(item,mediatorHisCompanyModel);
				//his 删除 记录异常
				syncCrmHisCompanyService.updateSyncStatusHisCompanyFromHis(
						createCompanyHisRelation(mediatorHisCompanyModel),
						SyncCrmHisCompanyOperEnum.DELMSG.name()
				);

			});
		}
		
		
		MediatorHisCompanyDAOUpdateParam mediatorHisCompanyDAOUpdateParam = new MediatorHisCompanyDAOUpdateParam();
		mediatorHisCompanyDAOUpdateParam.setHospitalId(getHospitalId());
		mediatorHisCompanyDAOUpdateParam.setToRefresh(REFRESHED);
		mediatorHisCompanyMapper.updateHisCompany(mediatorHisCompanyDAOUpdateParam);
	}
	
	/**
	 * 获取id列表，参数为null 或[] 返回 []
	 * @param hisCompanyDOList
	 * @return
	 */
	private List<Integer> getIdsFromDO(List<MediatorHisCompanyDO> hisCompanyDOList){
		if(hisCompanyDOList == null || hisCompanyDOList.isEmpty()){
			return Lists.newArrayList();
		}
		List<Integer> resultList = Lists.newArrayList();
		hisCompanyDOList.stream().forEach(item->{
			resultList.add(item.getId());
		});
		return resultList;
	}
	/**
	 * 处理his单位  增加与修改
	 * @param hisCompanyList
	 */
	private void dispatcher(List<MediatorHisCompanyModel> hisCompanyList){
		Map<String , MediatorHisCompanyDO> mediatorHisCompanyDOMap = getHisCompanyCode(hisCompanyList);
		List<Integer> toRefreshList = Lists.newArrayList();
		for(MediatorHisCompanyModel mediatorHisCompanyModel : hisCompanyList){
			String hisCompanyCode = mediatorHisCompanyModel.getHisCompanyCode();
			String hisCompanyName = mediatorHisCompanyModel.getHisCompanyName();
			//his单位没有变化  elseif  his单位改变 elseif his单位新增    else  his单位删除
			if(mediatorHisCompanyDOMap.containsKey(hisCompanyCode)&&
					mediatorHisCompanyDOMap.get(hisCompanyCode).getHisCompanyName().equals(hisCompanyName)){
				toRefreshList.add(mediatorHisCompanyDOMap.get(hisCompanyCode).getId());
			}else if(mediatorHisCompanyDOMap.containsKey(hisCompanyCode)&&
					!mediatorHisCompanyDOMap.get(hisCompanyCode).getHisCompanyName().equals(hisCompanyName)){
				//更新名字，并通知crm更新数据
				MediatorHisCompanyDO mediatorHisCompanyDO = mediatorHisCompanyDOMap.get(hisCompanyCode);
				mediatorHisCompanyModel.setMyCompanyId(mediatorHisCompanyDO.getMyCompanyId());
				Integer id = mediatorHisCompanyDO.getId();
				MediatorHisCompanyDAOUpdateParam updateParam = new MediatorHisCompanyDAOUpdateParam();
				updateParam.setIds(Lists.newArrayList(id));
				updateParam.setToHisCompanyName(hisCompanyName);
				mediatorHisCompanyMapper.updateHisCompany(updateParam);
				toRefreshList.add(mediatorHisCompanyDO.getId());
				//通知更新
				mediatorHisCompanyModel.setHospitalId(getHospitalId());

				CompanyHisRelation companyHisRelation = createCompanyHisRelation(mediatorHisCompanyModel);
				companyHisRelation.setSyncStatus(CompanySyncStatus.finish.getStatus());
				syncCrmHisCompanyService.updateHisCompanyFromHis(companyHisRelation);
			}else{
				MediatorHisCompanyDAOQueryParam mediatorHisCompanyDAOQueryParam = new MediatorHisCompanyDAOQueryParam();
				mediatorHisCompanyDAOQueryParam.setHisCompanyNameList(Lists.newArrayList(mediatorHisCompanyModel.getHisCompanyName()));
				mediatorHisCompanyDAOQueryParam.setHospitalId(getHospitalId());
				mediatorHisCompanyDAOQueryParam.setHisCompanyCodeNotNull(false);
				List<MediatorHisCompanyDO> list = mediatorHisCompanyMapper.selectHisCompanyList(mediatorHisCompanyDAOQueryParam);
				if(list != null && !list.isEmpty()){
					MediatorHisCompanyDAOUpdateParam mediatorHisCompanyDAOUpdateParam = new MediatorHisCompanyDAOUpdateParam();
					MediatorHisCompanyDO mediatorHisCompanyDO = list.get(0);
					mediatorHisCompanyDAOUpdateParam.setIds(Lists.newArrayList(mediatorHisCompanyDO.getId()));
					mediatorHisCompanyDAOUpdateParam.setToHisCompanysStatus(MediatorHisCompanyStatus.SUCCESS.name());
					mediatorHisCompanyDAOUpdateParam.setToHisCompanyCode(mediatorHisCompanyModel.getHisCompanyCode());
					mediatorHisCompanyMapper.updateHisCompany(mediatorHisCompanyDAOUpdateParam);
					toRefreshList.add(mediatorHisCompanyDO.getId());
					
					mediatorHisCompanyModel.setMyCompanyId(mediatorHisCompanyDO.getMyCompanyId());
					mediatorHisCompanyModel.setHospitalId(getHospitalId());

					CompanyHisRelation companyHisRelation = createCompanyHisRelation(mediatorHisCompanyModel);
					companyHisRelation.setSyncStatus(CompanySyncStatus.finish.getStatus());
					syncCrmHisCompanyService.updateHisCompanyFromHis(companyHisRelation);
				}else{
					MediatorHisCompanyDO mediatorHisCompanyDO = new MediatorHisCompanyDO();
					BeanUtils.copyProperties(mediatorHisCompanyModel,mediatorHisCompanyDO);
					mediatorHisCompanyDO.setCreator(Creator.HIS.name());
					mediatorHisCompanyDO.setHisCompanyStatus(MediatorHisCompanyStatus.SUCCESS.name());
					mediatorHisCompanyDO.setHospitalId(getHospitalId());
					mediatorHisCompanyDO.setRefresh(REFRESHING);
					mediatorHisCompanyMapper.insertHisCompany(mediatorHisCompanyDO);
					mediatorHisCompanyModel.setHospitalId(getHospitalId());

					CompanyHisRelation companyHisRelation = createCompanyHisRelation(mediatorHisCompanyModel);
					syncCrmHisCompanyService.dealHisCompanyAfterSync(companyHisRelation);
				}
			}
		}
		//更新刷新状态，为删除单位准备。
		MediatorHisCompanyDAOUpdateParam mediatorHisCompanyDAOUpdateParam = new MediatorHisCompanyDAOUpdateParam();
		mediatorHisCompanyDAOUpdateParam.setIds(toRefreshList);
		mediatorHisCompanyDAOUpdateParam.setToRefresh(REFRESHING);
		mediatorHisCompanyMapper.updateHisCompany(mediatorHisCompanyDAOUpdateParam);
	}

	/**
	 * 外层方法已经确保，参数hisCompanyList不为null
	 * 根据hisCompanyList 中hisCompanyCode 查询mediatorHisCompany
	 * @param hisCompanyList
	 */
	private Map<String , MediatorHisCompanyDO> getHisCompanyCode(List<MediatorHisCompanyModel> hisCompanyList){
		List<String> hisCompanyCodeList = Lists.newArrayList();
		hisCompanyList.stream().forEach(item->{
			if(item.getHisCompanyCode() == null){
				logger.error("hisCompanyCode is null,"+ item);
				return;
			}
			hisCompanyCodeList.add(item.getHisCompanyCode());
		});
		
		MediatorHisCompanyDAOQueryParam queryParam = new MediatorHisCompanyDAOQueryParam();
		queryParam.setHisCompanyCodeList(hisCompanyCodeList);
		queryParam.setHospitalId(getHospitalId());
		List<MediatorHisCompanyDO> mediatorHisCompanyDOList = mediatorHisCompanyMapper.selectHisCompanyList(queryParam);
		Map<String , MediatorHisCompanyDO>  resultMap = getHisCompanyCodeMap(mediatorHisCompanyDOList);
		return resultMap;
	}
	@Override
	public List<MediatorHisCompanyModel> queryToCreateHisCompanyList() {
		Integer hospitalId = getHospitalId();
		
		List<MediatorHisCompanyModel> toCreateHisCompany = queryToCreateMediatorHisCompany(hospitalId);
		//更新状态
		List<Integer> ids = getIds(toCreateHisCompany);
		if(ids == null || ids.isEmpty()){
			return Lists.newArrayList();
		}
		MediatorHisCompanyDAOUpdateParam updateParam = new MediatorHisCompanyDAOUpdateParam();
		updateParam.setIds(ids);
		updateParam.setToHisCompanysStatus(MediatorHisCompanyStatus.ADDING.name());
		mediatorHisCompanyMapper.updateHisCompany(updateParam);
		return toCreateHisCompany;
	}
	
	/**
	 * 从envKeeper 中获取hospitalId ，如果获取失败直接异常
	 * @return
	 */
	private Integer getHospitalId(){
		Integer hospitalId = envKeeper.getHospital();
		if(hospitalId == null){
			logger.error(CompanyExceptionCode.NO_HOSPITAL_ID.getMessage());
			throw ExceptionFactory.makeFault(CompanyExceptionCode.NO_HOSPITAL_ID);
		}
		return hospitalId;
	}
	/**
	 * 获取his单位列表的Id列表
	 * 如果 mediatorHisCompanyModelList 为null或是[] 则返回[]
	 * @param mediatorHisCompanyModelList
	 * @return 
	 */
	private List<Integer> getIds(List<MediatorHisCompanyModel> mediatorHisCompanyModelList){
		if(mediatorHisCompanyModelList == null || mediatorHisCompanyModelList.isEmpty()){
			return Lists.newArrayList();
		}
		List<Integer> resultIds = Lists.newArrayList();
		mediatorHisCompanyModelList.stream().forEach(item->{
			resultIds.add(item.getId());
		});
		return resultIds;
	}
	
	/**
	 * 查询his需要创建的单位
	 * @param hospitalId
	 * @return
	 */
	private List<MediatorHisCompanyModel> queryToCreateMediatorHisCompany(int hospitalId){
		MediatorHisCompanyDAOQueryParam queryParam = new MediatorHisCompanyDAOQueryParam();
		List<String> names = EnumUtil.getNames(MediatorHisCompanyStatus.WAITTINGADD,MediatorHisCompanyStatus.FAILED);
		queryParam.setHisCompanyStatusList(names);
		queryParam.setHospitalId(hospitalId);
		List<MediatorHisCompanyDO> list = mediatorHisCompanyMapper.selectHisCompanyList(queryParam);
		List<MediatorHisCompanyModel> resultList = convert(list);
		return resultList;
	}
	
	/**
	 * DO -> Model
	 * @param companyDoList
	 * @return
	 */
	private List<MediatorHisCompanyModel> convert(List<MediatorHisCompanyDO> companyDoList){
		if(companyDoList == null || companyDoList.isEmpty()){
			return Lists.newArrayList();
		}
		List<MediatorHisCompanyModel> resultList = Lists.newArrayList();
		companyDoList.stream().forEach(item->{
			MediatorHisCompanyModel mediatorHisCompanyModel = new MediatorHisCompanyModel();
			BeanUtils.copyProperties(item, mediatorHisCompanyModel);
			resultList.add(mediatorHisCompanyModel);
		});
		return resultList;
	}
	
	/**
	 * 将mediatorHisCompanyDOList 转换为以hisCompayCode为key 的map
	 * @param mediatorHisCompanyDOList
	 * @return
	 */
	private Map<String , MediatorHisCompanyDO> getHisCompanyCodeMap(List<MediatorHisCompanyDO> mediatorHisCompanyDOList){
		if(mediatorHisCompanyDOList == null){
			return Maps.newHashMap();
		}
		Map<String , MediatorHisCompanyDO> resultMpa = Maps.newHashMap();
		mediatorHisCompanyDOList.stream().forEach(item->{
			resultMpa.put(item.getHisCompanyCode(), item);
		});
		return resultMpa;
	}

	@Override
	public void returnHisCompany(Integer id, String hisCompanyCode,Boolean isSuccess) {
		
		MediatorHisCompanyDAOUpdateParam mediatorHisCompanyDAOUpdateParam = new MediatorHisCompanyDAOUpdateParam();
		mediatorHisCompanyDAOUpdateParam.setIds(Lists.newArrayList(id));
		mediatorHisCompanyDAOUpdateParam.setToHisCompanyCode(hisCompanyCode);
		if(isSuccess == null || isSuccess == false){
			mediatorHisCompanyDAOUpdateParam.setToHisCompanysStatus(MediatorHisCompanyStatus.FAILED.name());
		}else{
			mediatorHisCompanyDAOUpdateParam.setToHisCompanysStatus(MediatorHisCompanyStatus.SUCCESS.name());
		}
		mediatorHisCompanyDAOUpdateParam.setToRefresh(REFRESHING);
		mediatorHisCompanyMapper.updateHisCompany(mediatorHisCompanyDAOUpdateParam);
		
		MediatorHisCompanyDAOQueryParam queryParam = new MediatorHisCompanyDAOQueryParam();
		queryParam.setIdList(Lists.newArrayList(id));
		List<MediatorHisCompanyDO> list = mediatorHisCompanyMapper.selectHisCompanyList(queryParam);
		
		if(list != null && !list.isEmpty()) {
			MediatorHisCompanyDO mediatorHisCompanyDO = list.get(0);
			MediatorHisCompanyModel mediatorHisCompanyModel = new MediatorHisCompanyModel();
			BeanUtils.copyProperties(mediatorHisCompanyDO, mediatorHisCompanyModel);
			CompanyHisRelation companyHisRelation = createCompanyHisRelation(mediatorHisCompanyModel);
			if (isSuccess == null || isSuccess == false) {
				companyHisRelation.setSyncStatus(CompanySyncStatus.crmexception.getStatus());
				syncCrmHisCompanyService.updateSyncStatusHisCompanyFromHis(companyHisRelation, SyncCrmHisCompanyOperEnum.READDFAILED.name());
			} else {
				companyHisRelation.setSyncStatus(CompanySyncStatus.finish.getStatus());
				companyHisRelation.setSyncStatus(CompanySyncStatus.finish.getStatus());
				syncCrmHisCompanyService.updateSyncStatusHisCompanyFromHis(companyHisRelation, SyncCrmHisCompanyOperEnum.READDSUCCESS.name());
			}
		}
	}
	
	@Override
	public boolean supportNewSyncCompany(Integer hospitalId) {
		List<Integer> hospList = syncCrmHisCompanyService.supportSyncHospital();
		return CollectionUtils.isNotEmpty(hospList)
				&& hospList.contains(hospitalId);
	}

	private CompanyHisRelation createCompanyHisRelation(MediatorHisCompanyModel mediatorHisCompanyModel){
		CompanyHisRelation companyHisRelation = new CompanyHisRelation();
		companyHisRelation.setHisCompanyCode(mediatorHisCompanyModel.getHisCompanyCode());
		companyHisRelation.setHisCompanyName(mediatorHisCompanyModel.getHisCompanyName());
		companyHisRelation.setHospitalId(mediatorHisCompanyModel.getHospitalId());
		companyHisRelation.setCrmCompanyId(mediatorHisCompanyModel.getMyCompanyId());
		return companyHisRelation;

	}
}
