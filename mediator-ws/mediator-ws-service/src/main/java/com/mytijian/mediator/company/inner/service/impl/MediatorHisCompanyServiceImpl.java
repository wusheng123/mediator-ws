/*
 * Copyright 2016 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.company.inner.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.redisson.core.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.mytijian.distributed.lock.RedissonDistributedLock;
import com.mytijian.mediator.company.DAO.MediatorHisCompanyMapper;
import com.mytijian.mediator.company.DO.MediatorHisCompanyDO;
import com.mytijian.mediator.company.constants.Creator;
import com.mytijian.mediator.company.constants.MediatorHisCompanyStatus;
import com.mytijian.mediator.company.inner.service.MediatorHisCompanyService;
import com.mytijian.mediator.company.param.MediatorHisCompanyDAOQueryParam;
import com.mytijian.mediator.company.params.CreateMediatorHisCompanyParam;
import com.mytijian.mediator.service.common.ValidationService;

/**
 * 类MediatorHisCompanyServiceImpl.java的实现描述：TODO 类实现描述 
 * @author Administrator 2016年11月3日 下午3:36:52
 */
@Service("mediatorHisCompanyService")
public class MediatorHisCompanyServiceImpl implements MediatorHisCompanyService {

	private Logger logger = LoggerFactory
			.getLogger(MediatorHisCompanyServiceImpl.class);
	
	@Resource(name = "validationService")
	private ValidationService 			validationService;
	@Resource(name = "mediatorHisCompanyMapper")
	private MediatorHisCompanyMapper 	mediatorHisCompanyMapper;
	@Resource(name = "distributedLockService")
	private RedissonDistributedLock redissonDistributedLock;

	/**
	 * 分布式锁防止瞬间重复提交
	 */
	@Override
	public void createMediatorHisCompany(CreateMediatorHisCompanyParam createParam) {
		//校验参数是否合法
		validationService.validateParam(createParam);
		RLock rlock = redissonDistributedLock.getLock("MEDHISCOMADD" + createParam.getHospitalId());
		try {
			rlock.lock(10, TimeUnit.SECONDS);
			createMedHisCompany(createParam);
		}catch(Exception e) {
			logger.error("MEDHISADD error :param{}",createParam,e);
		}finally {
			rlock.unlock();
        }
	}
	/**
	 * 创建单位，保证数据库层面的幂等（查询存在不写入，不存在写入）
	 * @param createParam
	 */
	private void createMedHisCompany(CreateMediatorHisCompanyParam createParam){

		//构建查询，该记录是否存在
		MediatorHisCompanyDAOQueryParam queryParam = new MediatorHisCompanyDAOQueryParam();
		queryParam.setHospitalId(createParam.getHospitalId());
		queryParam.setHisCompanyNameList(Lists.newArrayList(createParam.getHisCompanyName()));
		queryParam.setCreator(Creator.MYTIJIAN.name());

		//构建新增记录
		MediatorHisCompanyDO mediatorHisCompanyDO = new MediatorHisCompanyDO();
		BeanUtils.copyProperties(createParam, mediatorHisCompanyDO);
		mediatorHisCompanyDO.setHisCompanyStatus(MediatorHisCompanyStatus.WAITTINGADD.name());
		
		List<MediatorHisCompanyDO> list = mediatorHisCompanyMapper.selectHisCompanyList(queryParam);
		if(list != null && !list.isEmpty()){
			logger.warn("****the mediatorHisCompany has exist in call createMediatorHisCompany the param:{}" ,createParam);
			return;
		}
		mediatorHisCompanyMapper.insertHisCompany(mediatorHisCompanyDO);
	}

}
