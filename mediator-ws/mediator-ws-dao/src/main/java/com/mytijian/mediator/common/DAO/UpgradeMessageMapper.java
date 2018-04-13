package com.mytijian.mediator.common.DAO;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.mytijian.mediator.api.model.UpgradeMessage;

@Repository("upgradeMessageMapper")
public interface UpgradeMessageMapper {

	UpgradeMessage selectById(@Param(value = "id") Integer id);
	
	UpgradeMessage selectByHospitalId(@Param(value = "hospitalId") Integer hospitalId);

	void updateStatus(@Param(value = "id") Integer id, @Param(value = "status") Integer status);
	
	void insert(@Param(value="message")UpgradeMessage message);
	
	//记录agent版本
	UpgradeMessage selectOneOrderByTime(@Param(value = "hospitalId") Integer hospitalId);

}
