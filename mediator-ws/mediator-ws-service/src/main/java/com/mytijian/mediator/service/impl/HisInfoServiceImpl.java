package com.mytijian.mediator.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mytijian.mediator.api.model.HisInfo;
import com.mytijian.mediator.api.service.HisInfoService;
import com.mytijian.mediator.common.DAO.HisInfoMapper;

@Service("hisInfoService")
public class HisInfoServiceImpl implements HisInfoService {

	@Resource(name = "hisInfoMapper")
	private HisInfoMapper hisInfoMapper;

	@Override
	public HisInfo getByHospitalId(Integer hospitalId) {
		return hisInfoMapper.selectByHospitalId(hospitalId);
	}

}
