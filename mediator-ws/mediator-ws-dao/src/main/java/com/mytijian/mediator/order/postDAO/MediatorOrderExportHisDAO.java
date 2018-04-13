package com.mytijian.mediator.order.postDAO;

import java.util.List;

import com.mytijian.mediator.order.model.MediatorOrderExportHis;
import com.mytijian.pulgin.mybatis.pagination.Page;

public interface MediatorOrderExportHisDAO {

	/**
	 * 批量添加订单导出记录
	 * 
	 * @param mediatorOrderExportHis
	 */
	public void addExportOrdersBatch(List<MediatorOrderExportHis> mediatorOrderExportHis);

	/**
	 * 更新导出订单状态
	 * 
	 * @param orderExportIds
	 * @param isExport
	 */
	public void updateOrderExportByOrderIds(List<Integer> orderIds, Integer isExport);
	
	/**
	 * 更新导出订单状态
	 * @param orderId
	 * @param isExport
	 */
	public void updateOrderExportByOrderId(Integer orderId, Integer isExport);
	

	/**
	 * 根据医院编号获取导出订单列表
	 * 
	 * @param hospitalId
	 * @param exportType
	 *            导出订单类型 0：默认，1：及时订单，2：排序订单  @See OrderExportType
	 * @param isExport
	 * @param page
	 * @return
	 */
	public List<MediatorOrderExportHis> getMediatorHospitalOrderExportByPage(Integer hospitalId, Integer exportType,
			Integer isExport, Page page);

	/**
	 * 获取当前最大值
	 * 
	 * @return
	 */
	public int getMaxSequence();
	
	/**
	 * 根据订单编号获取订单导出总数
	 * @param orderId 订单编号
	 * @return
	 */
	public int getMediatorOrderExportHisCountByOrderId(Integer orderId);
	
	/**
	 * 根据订单编号获取订单导出记录
	 * @param orderId
	 * @return
	 */
	public MediatorOrderExportHis getMediatorOrderExportHisByOrderId(Integer orderId);

}
