package com.mytijian.mediator.common.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.common.DO.AgentTaskConfigDO;

@Repository("agentTaskConfigMapper")
public interface AgentTaskConfigMapper {
	
	/**
	 * 查询所有任务配置列表
	 * @return
	 */
	List<AgentTaskConfigDO> selectTaskConfigList();
	
	/**
	 * 根据taskcmd查询
	 * 
	 * @return
	 */
	AgentTaskConfigDO selectTaskConfigByTaskCmd(
			@Param(value = "taskCmd") String taskCmd);
}
