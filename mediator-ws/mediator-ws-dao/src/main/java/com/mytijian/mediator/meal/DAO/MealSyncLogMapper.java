package com.mytijian.mediator.meal.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.meal.model.MealSyncLog;

@Repository("mealSyncLogMapper")
public interface MealSyncLogMapper {
	
	List<MealSyncLog> selectByStatus(@Param("status") Integer status);
	
	List<MealSyncLog> selectByStatusAndHospital(
			@Param("status") Integer status,
			@Param("hospitalId") Integer hospitalId);
	
	int updateHisMealId(@Param("mealId") Integer mealId,
			@Param("hisMealId") String hisMealId,
			@Param("status") Integer status);
	
	int insertMealSyncLog(MealSyncLog mealSyncLog);
	
	int insertMealSyncLogList(@Param("list") List<MealSyncLog> list);
	
	List<MealSyncLog> selectByMealId(@Param("mealId") Integer mealId);

	// crm使用
	int updateMealSyncLog(@Param("mealId") Integer mealId,
			@Param("companyId") Integer companyId,
			@Param("status") Integer status,@Param("hisName")String hisName);
}
