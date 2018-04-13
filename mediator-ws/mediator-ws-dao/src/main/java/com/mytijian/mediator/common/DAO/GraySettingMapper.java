/*
 * Copyright 2017 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.common.DAO;
/**
 * 类fd.java的实现描述：TODO 类实现描述 
 * @author liangxing 2017年3月2日 下午4:46:10
 */

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.common.model.GraySetting;

@Repository("graySettingMapper")
public interface GraySettingMapper {

	List<GraySetting> selectByHospitalId(@Param("hospitalId") Integer hospitalId);
	
}
