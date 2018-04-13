package com.mytijian.mediator.common.inner.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mytijian.mediator.common.DAO.MediatorVersionMapper;
import com.mytijian.mediator.common.model.MediatorVersion;
import com.mytijian.mediator.common.service.MediatorVersionService;
@Service("mediatorVersionService")
public class MediatorVersionServiceImpl implements MediatorVersionService {

	@Resource(name = "mediatorVersionMapper")
	private MediatorVersionMapper mediatorVersionMapper;
	
	@Override
	public MediatorVersion getVersionByHospitalId(Integer hospitalId) {
		return mediatorVersionMapper.selectByHospitalId(hospitalId);
	}

}
