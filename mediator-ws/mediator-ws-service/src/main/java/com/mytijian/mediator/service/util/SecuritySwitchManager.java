package com.mytijian.mediator.service.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.mytijian.mediator.common.DAO.SecuritySettingMapper;
import com.mytijian.mediator.common.DO.SecuritySettingDO;

@Component("securitySwitchManager")
public class SecuritySwitchManager {
	
	@Resource(name = "securitySettingMapper")
	private SecuritySettingMapper securitySettingMapper;
	
	public Map<Integer,Integer> getSecurityVersionMap(){
		
		List<SecuritySettingDO> settingList = securitySettingMapper.selectAllSetting();
		
		if(CollectionUtils.isEmpty(settingList)){
			return new HashMap<Integer,Integer>();
		}
		
		return settingList.stream().collect(Collectors.toMap(SecuritySettingDO::getHospitalId, SecuritySettingDO::getVersion));
	}
}
