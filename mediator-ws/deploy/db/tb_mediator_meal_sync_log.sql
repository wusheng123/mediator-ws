create table tb_mediator_meal_sync_log(
	meal_id int(11) not null primary key,
	his_meal_id varchar(500) default null COMMENT 'his套餐编码',
	hospital_id int(11) not null,
	company_id int(11) default null,
	status int(11) not null comment '同步状态，0:未同步,1:已同步',
	create_time datetime DEFAULT NULL,
	update_time timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	KEY `HOSPITAL_ID_IDX` (`hospital_id`)
)