create table tb_mediator_genericpeservice_task(
  id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  hospital_id INT(11) NOT NULL,
  task_name VARCHAR(128) NOT NULL COMMENT '任务名称',
  task_params VARCHAR(1024) COMMENT '任务参数',
  task_switch TINYINT(1) NOT NULL DEFAULT 2 COMMENT '1:开启,2:关闭',
  crontab_expression VARCHAR(128)  COMMENT 'crontab时间表达式',
  gmt_created datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY  mediator_genericpeservice_task_hospital_id_index (hospital_id) USING BTREE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO tb_mediator_genericpeservice_task(hospital_id,task_name,task_params,task_switch,crontab_expression)
VALUES(656,'searchCheckInNewOrder',NULL,2,'0 0/1 * * * ?'),
(656,'searchCheckInModifyOrder',NULL,2,'0 0/1 * * * ?'),
(656,'searchPrepareNewOrder',NULL,2,'0 0/1 * * * ?'),
(656,'searchPrepareModifyOrder',NULL,2,'0 0/1 * * * ?'),
(656,'searchHistoryOrder',NULL,2,'0 0/1 * * * ?'),
(656,'searchAccomplishOrder',NULL,2,'0 0/1 * * * ?'),
(656,'uploadOrderSender',NULL,2,'0 0/1 * * * ?');