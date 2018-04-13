package com.mytijian.mediator.order.postDAO;

import java.util.List;

import com.mytijian.mediator.order.model.MediatorOrderPrintHist;
import com.mytijian.pulgin.mybatis.pagination.Page;

public interface MediatorOrderPrintHistDAO {

	/**
	 * 批量添加订单打印记录
	 * 
	 * @param mediatorOrderExportHist
	 */
	public void addPrintOrdersBatch(List<MediatorOrderPrintHist> mediatorOrderPrintHist);

	/**
	 * 更新打印订单状态
	 * 
	 * @param orderNums
	 * @param printStatus
	 */
	public void updateOrderPrintByOrderNums(List<String> orderNums, Integer printStatus);
	
	public void updateOrderPrintByOrderIds(List<Integer> orderIds, Integer printStatus);
	
	public void updateOrderPrintByOrderNum(String orderNum, Integer printStatus);

	/**
	 * 分页获取订单打印列表
	 * 
	 * @param hospitalId
	 * @param printStatus
	 * @param page
	 * @return
	 */
	public List<MediatorOrderPrintHist> getMediatorHospitalOrderPrintByPage(Integer hospitalId, Integer printStatus,
			Page page);
}
