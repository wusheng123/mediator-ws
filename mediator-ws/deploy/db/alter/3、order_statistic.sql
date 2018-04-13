CREATE TABLE `tb_mediator_order_statistics` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `gmt_created` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  `hospital_id` int(11) DEFAULT NULL,
  `task_id` int(11) DEFAULT NULL COMMENT 'tb_mediator_agent_task主键',
  `statistics_item` varchar(100) DEFAULT NULL COMMENT '统计项',
  `param` varchar(2000) DEFAULT NULL COMMENT '统计参数',
  `result` varchar(2000) DEFAULT NULL COMMENT '结果',
  `format` varchar(20) DEFAULT NULL COMMENT '格式，JSON XML TEXT',
  PRIMARY KEY (`id`),
  KEY `hospital_id` (`hospital_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;