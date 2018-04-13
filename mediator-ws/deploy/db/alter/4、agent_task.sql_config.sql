CREATE TABLE `tb_mediator_agent_task_config` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `gmt_created` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  `task_cmd` varchar(100) NOT NULL COMMENT '任务cmd,如order',
  `task_name` varchar(100) DEFAULT NULL COMMENT '任务名称',
  `sequence` int(11) unsigned DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`),
  UNIQUE KEY `task_cmd` (`task_cmd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `tb_mediator_agent_task_config` (`id`, `gmt_created`, `gmt_modified`, `task_cmd`, `task_name`, `sequence`)
VALUES
	(1, '2017-03-01 11:43:11', '2017-03-01 11:43:11', 'order', '订单同步', 0),
	(2, '2017-03-01 11:43:11', '2017-03-01 11:43:11', 'immediateorder', '即时订单', 1),
	(3, '2017-03-01 11:43:11', '2017-03-01 11:43:11', 'orderdone', '回单', 2),
	(4, '2017-03-01 11:43:11', '2017-03-01 11:43:11', 'examitem', '单项同步', 3),
	(5, '2017-03-01 11:43:11', '2017-03-01 11:43:11', 'alarmOrder', '告警订单', 4),
	(6, '2017-03-01 11:43:11', '2017-03-01 11:43:11', 'deleteOrder', '删除订单', 5),
	(7, '2017-03-01 11:43:11', '2017-03-01 11:43:11', 'newCompany', '新单位同步', 6),
	(8, '2017-03-01 11:43:11', '2017-03-01 11:43:11', 'meal', '套餐同步', 7),
	(9, '2017-03-01 11:43:11', '2017-03-01 11:43:11', 'syncAll', '全量报告', 8),
	(10, '2017-03-01 11:43:11', '2017-03-01 11:43:11', 'syncMytijian', '增量报告', 9),
	(11, '2017-03-01 11:43:11', '2017-03-01 11:43:11', 'orderPrint', '订单打印', 10),
	(12, '2017-03-01 11:43:11', '2017-03-01 11:43:11', 'examreport', '老的体检报告', 11),
	(13, '2017-03-01 11:43:11', '2017-03-01 11:43:11', 'examqueue', '体检排队任务', 12);

INSERT INTO `tb_mediator_agent_task_config` (`id`, `gmt_created`, `gmt_modified`, `task_cmd`, `task_name`, `sequence`)
VALUES
	(14, '2017-03-01 11:43:11', '2017-03-01 11:43:11', 'nonPlatformExamCount', '当天非平台体检量', 13),
	(15, '2017-03-02 09:07:34', '2017-03-02 09:07:34', 'nonPlatformBookCount', '当天非平台下单量', 14);

