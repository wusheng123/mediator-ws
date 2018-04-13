CREATE TABLE `tb_mediator_order_print_hist` (
  `order_id` int(11) NOT NULL COMMENT '订单Id',
  `order_num` varchar(50) NOT NULL COMMENT '订单编号',
  `hospital_id` int(11) NOT NULL COMMENT '医院Id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `print_status` tinyint(1) DEFAULT '0' COMMENT '0:未打印，1:已打印, 2:打印中',
  `is_print` tinyint(1) DEFAULT '1' COMMENT '0:不打印，1：打印',
  `add_his_queue` tinyint(1) DEFAULT '0' COMMENT '是否发送HIS排队系统, 0:不加入队列,1加入队列',
  PRIMARY KEY (`order_id`),
  KEY `idx_hospital_id` (`hospital_id`),
  KEY `idx_order_num` (`order_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
