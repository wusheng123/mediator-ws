package com.mytijian.mediator.common.DAO;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.api.model.HeartbeatLog;

@Repository("heartbeatLogMapper")
public interface HeartbeatLogMapper {

	void insert(HeartbeatLog heartbeatLog);

	HeartbeatLog getByHospitalId(
			@Param(value = "hospitalId") Integer hospitalId);

	void delete(@Param(value = "id") Integer id);
}
