package com.mytijian.mediator.common.DAO;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.common.model.MediatorVersion;
@Repository("mediatorVersionMapper")
public interface MediatorVersionMapper {
	MediatorVersion selectByHospitalId(@Param("hospitalId") Integer hospitalId);
}
