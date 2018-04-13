CREATE TABLE `tb_archive_relation` (
  `hospital_id` int(11) NOT NULL COMMENT '体检中心',
  `account_id` int(11) NOT NULL DEFAULT '0' COMMENT '账户id',
  `his_archive` varchar(50) DEFAULT NULL COMMENT '体检人在体检系统的体检档案号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `state` tinyint(4) DEFAULT '0' COMMENT '档案号是否确认对照，0-未对照 1-已确认',
  PRIMARY KEY (`hospital_id`,`account_id`),
  KEY `fk_tb_archive_relation_account` (`account_id`),
  CONSTRAINT `fk_tb_archive_relation_account` FOREIGN KEY (`account_id`) REFERENCES `tb_account` (`id`),
  CONSTRAINT `fk_tb_archive_relation_hospital` FOREIGN KEY (`hospital_id`) REFERENCES `tb_hospital` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;