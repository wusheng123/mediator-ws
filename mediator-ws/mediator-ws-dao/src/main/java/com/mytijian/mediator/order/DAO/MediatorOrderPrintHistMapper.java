package com.mytijian.mediator.order.DAO;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.mytijian.mediator.order.model.MediatorOrderPrintHist;
import com.mytijian.pulgin.mybatis.pagination.Page;

@Repository("mediatorOrderPrintHistMapper")
public interface MediatorOrderPrintHistMapper {

	/**
	 * 添加订单打印记录
	 * 
	 * @param printOrder
	 */
	public int insert(MediatorOrderPrintHist mediatorOrderPrintHis);

	/**
	 * 批量更新订单打印状态
	 * 
	 * @param orderNums
	 * @param printStatus
	 */
	public void updateOrderPrintByOrderNums(@Param(value = "orderNums") List<String> orderNums,
			@Param(value = "printStatus") Integer printStatus);
	
	public void updateOrderPrintByOrderIds(@Param(value = "orderIds") List<Integer> orderIds,
			@Param(value = "printStatus") Integer printStatus);
	
	
	public void updateOrderPrintByOrderNum(@Param(value = "orderNum") String orderNum,
			@Param(value = "printStatus") Integer printStatus);

	/**
	 * 分页获取订单打印列表
	 * 
	 * @param hospitalId
	 * @param printStatus
	 * @param page
	 * @return
	 */
	public List<MediatorOrderPrintHist> getMediatorHospitalOrderPrintByPage(@Param(value = "hospitalId") Integer hospitalId,
			@Param(value = "printStatus") Integer printStatus, Page page);
	
	/**
	 * 根据orderNum获取订单打印总数
	 * @param orderNum
	 * @return
	 */
	public int getMediatorOrderPrintHisByOrderNum(@Param(value = "orderNum") String orderNum);
	
	
	
	public int updateMediatorOrderPrint(MediatorOrderPrintHist mediatorOrderPrintHis);

}
