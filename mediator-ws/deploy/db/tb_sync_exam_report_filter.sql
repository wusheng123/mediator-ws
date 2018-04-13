CREATE TABLE `tb_sync_exam_report_filter` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  `hospital_id` int(11) DEFAULT NULL COMMENT '医院id',
  `charset_filter` int(11) DEFAULT NULL COMMENT '字符转换器 0表示不需要',
  `marriage_filter` int(11) DEFAULT NULL COMMENT '结婚状态转换器0表示不需要 1表示中文已婚 未婚',
  `gender_filter` int(11) DEFAULT NULL COMMENT '性别过滤转换器0表示不需要',
  PRIMARY KEY (`id`),
  KEY `hospital_id` (`hospital_id`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

