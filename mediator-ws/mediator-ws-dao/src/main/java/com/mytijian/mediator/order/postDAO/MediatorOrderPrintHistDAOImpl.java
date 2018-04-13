package com.mytijian.mediator.order.postDAO;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mytijian.mediator.order.DAO.MediatorOrderPrintHistMapper;
import com.mytijian.mediator.order.model.MediatorOrderPrintHist;
import com.mytijian.mediator.order.postDAO.MediatorOrderPrintHistDAO;
import com.mytijian.pulgin.mybatis.pagination.Page;

@Service("mediatorOrderPrintHistDAO")
public class MediatorOrderPrintHistDAOImpl implements MediatorOrderPrintHistDAO {
	
	private static Logger logger = LoggerFactory.getLogger(MediatorOrderPrintHistDAOImpl.class);
	
	@Resource(name="mediatorOrderPrintHistMapper")
	private MediatorOrderPrintHistMapper mediatorOrderPrintHistMapper;
	
	@Override
	public void addPrintOrdersBatch(List<MediatorOrderPrintHist> mediatorOrderPrintHist) {
		if (CollectionUtils.isEmpty(mediatorOrderPrintHist)) {
			return;
		}
		mediatorOrderPrintHist.forEach(mediatorOrderPrint->{
			try {
				// 查询是否存在
				int count = mediatorOrderPrintHistMapper.getMediatorOrderPrintHisByOrderNum(mediatorOrderPrint.getOrderNum());
				if (count == 0) {
					// 不存在新增
					mediatorOrderPrintHistMapper.insert(mediatorOrderPrint);
				} else {
					mediatorOrderPrint.setPrintStatus(0);
					// 存在则更新
					mediatorOrderPrintHistMapper.updateMediatorOrderPrint(mediatorOrderPrint);
				}
			} catch (Exception e) {
				logger.error("添加/更新打印订单失败, orderId : " + mediatorOrderPrint.getOrderId() , e.getMessage());
			}
		});
	}

	@Override
	public void updateOrderPrintByOrderNums(List<String> orderNums, Integer printStatus) {
		if (CollectionUtils.isEmpty(orderNums)) {
			return;
		}
		mediatorOrderPrintHistMapper.updateOrderPrintByOrderNums(orderNums, printStatus);
	}
	
	@Override
	public void updateOrderPrintByOrderIds(List<Integer> orderIds, Integer printStatus) {
		if (CollectionUtils.isEmpty(orderIds)) {
			return;
		}
		mediatorOrderPrintHistMapper.updateOrderPrintByOrderIds(orderIds, printStatus);
	}
	
	@Override
	public List<MediatorOrderPrintHist> getMediatorHospitalOrderPrintByPage(Integer hospitalId, Integer printStatus,
			Page page) {
		return mediatorOrderPrintHistMapper.getMediatorHospitalOrderPrintByPage(hospitalId, printStatus, page);
	}

	@Override
	public void updateOrderPrintByOrderNum(String orderNum, Integer printStatus) {
		mediatorOrderPrintHistMapper.updateOrderPrintByOrderNum(orderNum, printStatus);
	}
	
}
