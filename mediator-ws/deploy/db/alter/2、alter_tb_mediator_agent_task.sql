-- 加字段
alter table tb_mediator_agent_task add column task_bean_name varchar(100) DEFAULT null comment '任务类在spring中的名称';
alter table tb_mediator_agent_task add column crontab_expression varchar(100) DEFAULT null comment 'crontab时间表达式';
-- 修改status的默认值
alter table tb_mediator_agent_task modify status tinyint(1) default 2 not null comment '1:开启,2:关闭';
alter table tb_mediator_agent_task modify task_params varchar(2000) default null comment '任务参数';-- 增加列字符长度
-- 设置任务参数
update tb_mediator_agent_task set task_params = '{"pageSize":500}' where task_cmd = 'orderdone';
update tb_mediator_agent_task set task_params = '{"pageSize":500}' where task_cmd = 'deleteOrder';
update tb_mediator_agent_task set task_params = '{"hisOrderProcessTimeInMinute":3}' where task_cmd = 'alarmOrder';

-- 设置bean name和crontab expression
update tb_mediator_agent_task set task_bean_name = 'syncOrderTask',crontab_expression='0 0/1 * * * ?' where task_cmd = 'order';

update tb_mediator_agent_task set task_bean_name = 'syncImmediateOrderTask',crontab_expression='0/5 * * * * ?' where task_cmd = 'immediateorder';
-- 5s
update tb_mediator_agent_task set task_bean_name = 'syncOrderPrintTask',crontab_expression='0/5 * * * * ?' where task_cmd = 'orderPrint';

update tb_mediator_agent_task set task_bean_name = 'syncHisCompanyTask',crontab_expression='0 0/1 * * * ?' where task_cmd = 'newCompany';

-- 套餐同步 20s
update tb_mediator_agent_task set task_bean_name = 'syncMealTask',crontab_expression='0/20 * * * * ?' where task_cmd = 'meal';

update tb_mediator_agent_task set task_bean_name = 'syncExamItemTask',crontab_expression='0 0/1 * * * ?' where task_cmd = 'examitem';

update tb_mediator_agent_task set task_bean_name = 'syncOrderDoneTask',crontab_expression='0 0/1 * * * ?' where task_cmd = 'orderdone';

update tb_mediator_agent_task set task_bean_name = 'syncAlarmOrderTask',crontab_expression='0 0/1 * * * ?' where task_cmd = 'alarmOrder';

update tb_mediator_agent_task set task_bean_name = 'syncDeletedOrderTask',crontab_expression='0 0/1 * * * ?' where task_cmd = 'deleteOrder';

update tb_mediator_agent_task set task_bean_name = 'syncMytijianExamReportTask',crontab_expression='0 0/1 * * * ?' where task_cmd = 'syncMytijian';

update tb_mediator_agent_task set task_bean_name = 'syncAllExamReportTask',crontab_expression='0 0/1 * * * ?' where task_cmd = 'syncAll';


