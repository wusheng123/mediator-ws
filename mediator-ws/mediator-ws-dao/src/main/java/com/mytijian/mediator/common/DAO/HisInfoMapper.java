package com.mytijian.mediator.common.DAO;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.api.model.HisInfo;

@Repository("hisInfoMapper")
public interface HisInfoMapper {
	
	HisInfo selectByHospitalId(@Param("hospitalId") Integer hospitalId);
}
