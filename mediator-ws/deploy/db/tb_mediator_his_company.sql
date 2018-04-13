CREATE TABLE `tb_mediator_his_company` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `hospital_id` int(11) DEFAULT NULL COMMENT '医院id',
  `my_company_id` int(11) DEFAULT NULL COMMENT '平台单位id',
  `his_company_code` varchar(255) DEFAULT NULL COMMENT 'his单位code',
  `his_company_name` varchar(255) DEFAULT NULL COMMENT 'his单位名',
  `his_company_status` varchar(32) DEFAULT NULL COMMENT 'his单位状态：WAITTINGADD ADDING SUCCESS FAILED',
  `creator` varchar(32) DEFAULT NULL COMMENT '单位类型,标示谁创建：mytijian，his',
  `refresh` varchar(32) DEFAULT NULL COMMENT '表示是否在同步中：refreshing，refreshed',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0表示未删除；1表示已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
