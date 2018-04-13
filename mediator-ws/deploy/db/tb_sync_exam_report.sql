CREATE TABLE `tb_sync_exam_report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `hospital_id` int(11) DEFAULT NULL COMMENT '医院id',
  `report_no` varchar(32) DEFAULT NULL COMMENT '体检报告号',
  `order_num` varchar(32) DEFAULT NULL COMMENT '订单号',
  `idcard` varchar(20) DEFAULT NULL COMMENT '身份证号',
  `exam_time` varchar(32) DEFAULT NULL COMMENT '体检日期',
  `report_time` varchar(32) DEFAULT NULL COMMENT '报告日期',
  `mobile` varchar(32) DEFAULT NULL COMMENT '体检人电话',
  `company_name` varchar(50) DEFAULT NULL COMMENT '体检人公司名',
  `state` varchar(32) DEFAULT NULL COMMENT '是否已经处理  done 为已处理 error 处理失败  ',
  `report_context` text,
  PRIMARY KEY (`id`),
  KEY `report_no` (`report_no`) USING HASH,
  KEY `order_num` (`order_num`) USING HASH,
  KEY `idcard` (`idcard`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

