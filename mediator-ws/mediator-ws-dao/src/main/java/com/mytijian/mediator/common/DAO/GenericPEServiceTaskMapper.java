package com.mytijian.mediator.common.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.common.DO.GenericPEServiceTaskDO;

/**
 *描述:请添加一段类名描述
 *日期:2017年12月19日
 *时间:下午5:43:43
 *@author huangwei
*/
@Repository("genericPEServiceTaskMapper")
public interface GenericPEServiceTaskMapper {
	
	List<GenericPEServiceTaskDO> selectTaskConfigByHospitalId(@Param(value = "hospitalId") Integer hospitalId);
}
