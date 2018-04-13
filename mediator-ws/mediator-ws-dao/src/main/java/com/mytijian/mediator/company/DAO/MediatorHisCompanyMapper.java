/*
 * Copyright 2016 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.company.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.company.DO.MediatorHisCompanyDO;
import com.mytijian.mediator.company.param.MediatorHisCompanyDAOQueryParam;
import com.mytijian.mediator.company.param.MediatorHisCompanyDAOUpdateParam;

/**
 * 类MediatorHisCompanyMapper.java的实现描述：TODO 类实现描述 
 * @author Administrator 2016年11月2日 下午8:03:09
 */
@Repository("mediatorHisCompanyMapper")
public interface MediatorHisCompanyMapper {
	
	/**
	 * 写入mediatorhiscompany信息
	 * @param mediatorHisCompanyDO
	 */
	int insertHisCompany(MediatorHisCompanyDO mediatorHisCompanyDO);
	
	/**
	 * 查询医院单位列表
	 * @return
	 */
	
	List<MediatorHisCompanyDO> selectHisCompanyList(MediatorHisCompanyDAOQueryParam queryParam);
	
	/**
	 * 根据ids and hospitalId 更新mediatorHisCompany表
	 * 当ids[] 时，不处理
	 * 当ids与hospitalId同时为null时不处理
	 * @param mediatorHisCompanyDAOUpdateParam
	 * @return
	 */
	int updateHisCompany(MediatorHisCompanyDAOUpdateParam mediatorHisCompanyDAOUpdateParam);
	
	/**
	 * 删除his单位
	 * @param ids
	 */
	int deleteHisCompany(@Param(value="ids")List<Integer> ids);
	
}
