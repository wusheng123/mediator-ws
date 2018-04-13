CREATE TABLE `tb_his_info` (
  `hospital_id` int(11) NOT NULL COMMENT '医院id',
  `his_name` varchar(500) default null comment 'his名称',
  `engine_name` varchar(50) default null comment '服务端处理引擎的名称',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`hospital_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


insert into tb_his_info(hospital_id,his_name,engine_name,gmt_created,gmt_modified) values(9,"chuangye","chuangye",now(),now());
insert into tb_his_info(hospital_id,his_name,engine_name,gmt_created,gmt_modified) values(31,"chuangye","chuangye",now(),now());		
insert into tb_his_info(hospital_id,his_name,engine_name,gmt_created,gmt_modified) values(2,"huojianwa","huojianwa",now(),now());
insert into tb_his_info(hospital_id,his_name,engine_name,gmt_created,gmt_modified) values(7,"fanjing","fanjing",now(),now());
insert into tb_his_info(hospital_id,his_name,engine_name,gmt_created,gmt_modified) values(23,"fanjing","fanjing",now(),now());
insert into tb_his_info(hospital_id,his_name,engine_name,gmt_created,gmt_modified) values(21,"fanjing","fanjing",now(),now());
		
insert into tb_his_info(hospital_id,his_name,engine_name,gmt_created,gmt_modified) values(20,"shaoyifu","shaoyifu",now(),now());
		
insert into tb_his_info(hospital_id,his_name,engine_name,gmt_created,gmt_modified) values(22,"jianxun","jianxun",now(),now());
		
insert into tb_his_info(hospital_id,his_name,engine_name,gmt_created,gmt_modified) values(3,"lianzhong","lianzhong",now(),now());
insert into tb_his_info(hospital_id,his_name,engine_name,gmt_created,gmt_modified) values(4,"lianzhong","lianzhong",now(),now());
insert into tb_his_info(hospital_id,his_name,engine_name,gmt_created,gmt_modified) values(5,"lianzhong","lianzhong",now(),now());
insert into tb_his_info(hospital_id,his_name,engine_name,gmt_created,gmt_modified) values(51,"kangruan","kangruan",now(),now());
insert into tb_his_info(hospital_id,his_name,engine_name,gmt_created,gmt_modified) values(50,"kangruan","kangruan",now(),now());
insert into tb_his_info(hospital_id,his_name,engine_name,gmt_created,gmt_modified) values(52,"biaoruan","biaoruan",now(),now());



