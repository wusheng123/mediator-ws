package com.mytijian.mediator.common.DAO;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.common.DO.MediatorAgentTaskDO;
import com.mytijian.mediator.common.param.ListTaskHospitalDaoQuery;
@Repository("mediatorAgentTaskMapper")
public interface MediatorAgentTaskMapper {

    int insert(MediatorAgentTaskDO record);
    
    /**
	 * 查询的是状态为Open(开启)的数据，@see com.mytijian.mediator.api.enums.AgentTaskStatus
	 * 
	 * @param hospitalId
	 * @return
	 */
    List<MediatorAgentTaskDO> selectMediatorAgentTaskDOByHospitalId(Integer hospitalId);
    
    void updateInit(@Param("taskId")Integer taskId,@Param("init")Integer init);
    
    /**
	 * 获取医院任务
	 * @param hospitalId
	 * @param status
	 * @param taskCmd
	 * @param startIndex
	 * @param pageSize
	 * @return
	 */
	List<MediatorAgentTaskDO> getAgentTask(Map<String, Object> paramMap);
	
	/**
	 * 更新订单任务状态
	 * @param hospitalId
	 * @param taskCmd
	 * @param openStatus
	 */
	int updateAgentTaskStatus(
			@Param(value = "status") Integer status,
			@Param(value = "hospitalId") Integer hospitalId,
			@Param(value = "taskCmd") String taskCmd);
	
	/**
	 * 查询所有记录
	 * 
	 * @return
	 */
	List<MediatorAgentTaskDO> selectAllTasks();
	
	/**
	 * 根据医院查询记录数
	 * 
	 * @param hospitalId
	 * @return
	 */
	int countByHospId(@Param(value = "hospitalId") Integer hospitalId);
	
	/**
	 * 根据医院id查询
	 * @param hospitalId
	 * @return
	 */
	List<MediatorAgentTaskDO> selectByHospitalId(Integer hospitalId);
	
	/**
	 * 查询医院id
	 * 
	 * @return
	 */
	List<Integer> selectHospitalIds();
	
	/**
	 * 更新任务参数
	 * @param hospitalId
	 * @param taskCmd
	 * @param taskParam
	 */
	void updateTaskParam(@Param(value = "hospitalId") Integer hospitalId,
			@Param(value = "taskCmd") String taskCmd,
			@Param(value = "taskParam") String taskParam);
	
	/**
	 * 更新任务执行时间
	 * 
	 * @param hospitalId
	 * @param taskCmd
	 * @param crontabExp
	 */
	void updateTaskCrontabExp(@Param(value = "hospitalId") Integer hospitalId,
			@Param(value = "taskCmd") String taskCmd,
			@Param(value = "crontabExp") String crontabExp);
	
	/**
	 * 根据省份获取医院Id
	 * @return
	 */
	List<Integer> selectTaskHospitals(ListTaskHospitalDaoQuery listTaskHospitalDaoQuery);
	
	/**
	 * 获取省份医院总数
	 * @param listTaskHospitalDaoQuery
	 * @return
	 */
	int countTaskHospitals(ListTaskHospitalDaoQuery listTaskHospitalDaoQuery);
}