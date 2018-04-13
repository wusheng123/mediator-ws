CREATE TABLE `tb_mediator_order_export_his` (
  `export_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL COMMENT '订单编号',
  `hospital_id` int(11) NOT NULL COMMENT '医院编号',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_export` tinyint(1) DEFAULT '0' COMMENT '0:待导出,1:已导出',
  `export_type` tinyint(1) DEFAULT '0' COMMENT '0:自动导出,1:立即导出,2:导出时排序',
  `sequence` int(11) DEFAULT '0' COMMENT '排序权重,值越小排序越靠前',
  `order_num` varchar(300) DEFAULT NULL COMMENT '订单号',
  `exam_date` datetime DEFAULT NULL COMMENT '体检日期',
  `account_id` int(11) DEFAULT NULL COMMENT '体检人账户id',
  `name` varchar(300) DEFAULT NULL COMMENT '体检人姓名',
  PRIMARY KEY (`export_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_hospital_id` (`hospital_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;