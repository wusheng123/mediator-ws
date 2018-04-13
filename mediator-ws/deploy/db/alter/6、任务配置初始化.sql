--加字段。数据都初始化后，设置default_task_bean_name 和 default_crontab_expression不能为空。
alter table tb_mediator_agent_task_config 
add column default_task_bean_name varchar(100) comment '默认的taskbean名称';

alter table tb_mediator_agent_task_config 
add column default_task_params varchar(2000) DEFAULT null comment '默认任务参数';

alter table tb_mediator_agent_task_config 
add column default_crontab_expression varchar(200) comment '默认时间';

update `tb_mediator_agent_task_config`
set `default_crontab_expression`='0 0/1 * * * ?',
`default_task_bean_name`='syncOrderTask'
-- `default_task_params`=null
where task_cmd = 'order';

update `tb_mediator_agent_task_config`
set `default_crontab_expression`='0/5 * * * * ?',
`default_task_bean_name`='syncImmediateOrderTask'
-- `default_task_params`=null
where task_cmd = 'immediateorder';

update `tb_mediator_agent_task_config`
set `default_crontab_expression`='0/5 * * * * ?',
`default_task_bean_name`='syncOrderPrintTask'
-- `default_task_params`=null
where task_cmd = 'orderPrint';

update `tb_mediator_agent_task_config`
set `default_crontab_expression`='0 0/1 * * * ?',
`default_task_bean_name`='syncHisCompanyTask'
-- `default_task_params`=null
where task_cmd = 'newCompany';

update `tb_mediator_agent_task_config`
set `default_crontab_expression`='0/20 * * * * ?',
`default_task_bean_name`='syncMealTask'
-- `default_task_params`=null
where task_cmd = 'meal';


update `tb_mediator_agent_task_config`
set `default_crontab_expression`='0 0/1 * * * ?',
`default_task_bean_name`='syncExamItemTask'
-- `default_task_params`=null
where task_cmd = 'examitem';

update `tb_mediator_agent_task_config`
set `default_crontab_expression`='0 0/1 * * * ?',
`default_task_bean_name`='syncOrderDoneTask',
`default_task_params`= '{"pageSize":500}'
where task_cmd = 'orderdone';



update `tb_mediator_agent_task_config`
set `default_crontab_expression`='0 0/1 * * * ?',
`default_task_bean_name`='syncAlarmOrderTask',
`default_task_params`= '{"hisOrderProcessTimeInMinute":3}'
where task_cmd = 'alarmOrder';


update `tb_mediator_agent_task_config`
set `default_crontab_expression`='0 0/1 * * * ?',
`default_task_bean_name`='syncDeletedOrderTask',
`default_task_params`= '{"pageSize":500}'
where task_cmd = 'deleteOrder';



update `tb_mediator_agent_task_config`
set `default_crontab_expression`='0 0/1 * * * ?',
`default_task_bean_name`='syncMytijianExamReportTask'
 -- `default_task_params`= '{"pageSize":500}'
where task_cmd = 'syncMytijian';


update `tb_mediator_agent_task_config`
set `default_crontab_expression`='0 0/1 * * * ?',
`default_task_bean_name`='syncAllExamReportTask'
 -- `default_task_params`= '{"pageSize":500}'
where task_cmd = 'syncAll';

-- nonPlatformExamCount
update `tb_mediator_agent_task_config`
set `default_crontab_expression`='0 03 17 ? * *',
`default_task_bean_name`='databaseOperationTask',
`default_task_params`= '{"action":"订单统计","sql":"select COUNT(distinct his_bm) as 订单数量 from tbl_all_order_view where  order_type = \'hospital\' and trunc(exam_date) = trunc(sysdate)"}'
where task_cmd = 'nonPlatformExamCount';

update `tb_mediator_agent_task_config`
set `default_crontab_expression`='0 03 17 ? * *',
`default_task_bean_name`='databaseOperationTask',
`default_task_params`= '{"action":"订单统计","sql":"select COUNT(distinct his_bm) as 订单数量 from tbl_all_order_view where  order_type = \'hospital\' and trunc(insert_date) = trunc(sysdate)"}'
where task_cmd = 'nonPlatformBookCount';


update `tb_mediator_agent_task_config`
set `default_crontab_expression`='0 03 17 ? * *',
`default_task_bean_name`='databaseOperationTask',
`default_task_params`= '{"action":"订单统计","sql":"select COUNT(distinct his_bm) as 订单数量 from tbl_all_order_view where  order_type = \'platform\' and trunc(exam_date) = trunc(sysdate)"}'
where task_cmd = 'platformExamCount';

update `tb_mediator_agent_task_config`
set `default_crontab_expression`='0 03 17 ? * *',
`default_task_bean_name`='databaseOperationTask',
`default_task_params`= '{"action":"订单统计","sql":"select COUNT(distinct his_bm) as 订单数量 from tbl_all_order_view where  order_type = \'platform\' and trunc(insert_date) = trunc(sysdate)"}'
where task_cmd = 'platformBookCount';

ALTER TABLE `tb_mediator_agent_task_config` 
	MODIFY COLUMN `default_task_bean_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '默认的taskbean名称' AFTER `sequence`,
	MODIFY COLUMN `default_crontab_expression` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '默认时间' AFTER `default_task_params`;
