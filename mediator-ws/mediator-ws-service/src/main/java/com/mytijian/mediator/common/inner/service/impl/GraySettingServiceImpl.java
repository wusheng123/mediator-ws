/*
 * Copyright 2017 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.common.inner.service.impl;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mytijian.mediator.common.DAO.GraySettingMapper;
import com.mytijian.mediator.common.model.GraySetting;
import com.mytijian.mediator.common.service.GraySettingService;

/**
 * 类GraySettingServiceImpl.java的实现描述：TODO 类实现描述 
 * @author liangxing 2017年3月2日 下午5:09:11
 */
@Service("graySettingService")
public class GraySettingServiceImpl implements GraySettingService {

	@Resource(name = "graySettingMapper")
	private GraySettingMapper graySettingMapper;
	
	private Cache<Integer, Optional<GraySetting>> graySettingCache = CacheBuilder.newBuilder()
			.maximumSize(1000).expireAfterWrite(10, TimeUnit.MINUTES).build();
	
	@Override
	public GraySetting getByHospitalId(Integer hospitalId){
		if (hospitalId == null) {
			return null;
		}
		try {
			Optional<GraySetting> cache = graySettingCache.get(hospitalId, new Callable<Optional<GraySetting>>(){
				public Optional<GraySetting> call(){
					return getByHospitalIdByDB(hospitalId);
				}
			});
			return cache.orNull();
			
		} catch (ExecutionException e) {
			return null;
		}
	}

	
	public Optional<GraySetting> getByHospitalIdByDB(Integer hospitalId) {
		if (hospitalId == null) {
			return Optional.absent();
		}
		List<GraySetting> list = graySettingMapper.selectByHospitalId(hospitalId);
		if (CollectionUtils.isNotEmpty(list)) {
			return Optional.of(list.get(0));
		} else {
			return Optional.absent();
		}

	}
	
}
