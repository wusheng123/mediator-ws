alter table tb_mediator_agent_task add column status tinyint(1) DEFAULT 1 comment '1:开启,2关闭';
alter table tb_mediator_agent_task add column province_id int(11) DEFAULT null comment '省份Id';