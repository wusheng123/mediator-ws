package com.mytijian.mediator.order.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.order.model.MediatorOrderExportHis;
import com.mytijian.pulgin.mybatis.pagination.Page;

@Repository("mediatorOrderExportHisMapper")
public interface MediatorOrderExportHisMapper {
	
	/**
	 * 批量添加订单导出记录
	 * @param mediatorOrderExportHis
	 */
	public void insertBatch(List<MediatorOrderExportHis> mediatorOrderExportHis);
	
	/**
	 * 添加订单导出记录
	 * @param mediatorOrderExportHis
	 */
	public int insert(MediatorOrderExportHis mediatorOrderExportHis);
	
	/**
	 * 更新导出订单状态
	 * @param orderIds
	 * @param isExport
	 */
	public void updateOrderExportByOrderIds(@Param(value="orderIds") List<Integer> orderIds, @Param(value="isExport") Integer isExport);
	
	/**
	 * 更新导出订单状态
	 * @param orderId
	 * @param isExport
	 */
	public void updateOrderExportByOrderId(@Param(value="orderId") Integer orderId, @Param(value="isExport") Integer isExport);
	
	/**
	 * 根据医院编号获取导出订单列表
	 * @param hospitalId
	 * @param exportType 导出订单类型 0：默认，1：及时订单，2：排序订单
	 * @param isExport
	 * @param page
	 * @return
	 */
	public List<MediatorOrderExportHis> getMediatorHospitalOrderExportByPage(
			@Param(value = "hospitalId") Integer hospitalId, @Param(value = "exportType") Integer exportType,
			@Param(value = "isExport") Integer isExport, Page page);
	
	/**
	 * 获取当前最大值
	 * @return
	 */
	public int getMaxSequence();
	
	/**
	 * 根据订单Id获取订单导出总数
	 * @param orderId
	 * @return
	 */
	public int getMediatorOrderExportHisCountByOrderId(@Param(value="orderId") Integer orderId);
	
	/**
	 * 根据订单Id获取订单导出信息
	 * @param orderId
	 * @return
	 */
	public MediatorOrderExportHis getMediatorOrderExportHisByOrderId(@Param(value="orderId") Integer orderId);
	
}
