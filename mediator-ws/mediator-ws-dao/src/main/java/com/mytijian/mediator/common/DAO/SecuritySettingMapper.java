package com.mytijian.mediator.common.DAO;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.mytijian.mediator.common.DO.SecuritySettingDO;

@Repository("securitySettingMapper")
public interface SecuritySettingMapper {
	
	List<SecuritySettingDO> selectAllSetting();
}
