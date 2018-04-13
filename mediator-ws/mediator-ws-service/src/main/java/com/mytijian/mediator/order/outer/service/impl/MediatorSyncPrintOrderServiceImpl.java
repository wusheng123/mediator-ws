package com.mytijian.mediator.order.outer.service.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.mytijian.mediator.api.service.EnvironmentKeeper;
import com.mytijian.mediator.order.dto.PrintOrderResultDto;
import com.mytijian.mediator.order.enums.OrderPrintStatus;
import com.mytijian.mediator.order.model.MediatorOrderExportHis;
import com.mytijian.mediator.order.model.MediatorOrderPrintHist;
import com.mytijian.mediator.order.outer.service.MediatorSyncPrintOrderService;
import com.mytijian.mediator.order.postDAO.MediatorOrderExportHisDAO;
import com.mytijian.mediator.order.postDAO.MediatorOrderPrintHistDAO;
import com.mytijian.pulgin.mybatis.pagination.Page;


@Service("mediatorSyncPrintOrderService")
public class MediatorSyncPrintOrderServiceImpl implements MediatorSyncPrintOrderService {

	private Logger logger =LoggerFactory.getLogger(MediatorSyncPrintOrderServiceImpl.class);
	
	@Resource(name = "mediatorOrderPrintHistDAO")
	private MediatorOrderPrintHistDAO mediatorOrderPrintHistDAO;
	
	@Resource(name = "mediatorOrderExportHisDAO")
	private MediatorOrderExportHisDAO mediatorOrderExportHisDAO;
	
	@Resource(name = "soapHeaderInterceptor")
	private EnvironmentKeeper envKeeper;
	
	private final static String ADDMSG = "ADD";
	
	@Override
	public List<MediatorOrderPrintHist> getMediatorOrderPrintHisList() {
		Page page = new Page(0, 50);
		List<MediatorOrderPrintHist> mediatorOrderPrintHists = mediatorOrderPrintHistDAO.getMediatorHospitalOrderPrintByPage(envKeeper.getHospital(), 0, page);
		// 过滤掉未导出的订单
		getExportedOrders(mediatorOrderPrintHists);
		// 更新状态
		updatePrintOrderStatus(mediatorOrderPrintHists, OrderPrintStatus.PRINTING.getCode());
		return mediatorOrderPrintHists;
	}

	@Override
	public void updateMediatorOrderPrintStatus(String orderNum, String code, String msg) {
		try {
			mediatorOrderPrintHistDAO.updateOrderPrintByOrderNum(orderNum, OrderPrintStatus.PRINTED.getCode());
		} catch (Exception e) {
			logger.error("MediatorSyncPrintOrderServiceImpl.updateMediatorOrderPrintStatus error, orderNum : " + orderNum, e.getMessage());
		}
	}
	
	private void getExportedOrders (List<MediatorOrderPrintHist> mediatorOrderPrintHists) {
		if (!CollectionUtils.isEmpty(mediatorOrderPrintHists)) {
			Iterator<MediatorOrderPrintHist> iter = mediatorOrderPrintHists.iterator();
			while (iter.hasNext()) {
				MediatorOrderPrintHist mediatorOrderPrintHist = iter.next();
				MediatorOrderExportHis mediatorOrderExportHis = mediatorOrderExportHisDAO
						.getMediatorOrderExportHisByOrderId(mediatorOrderPrintHist.getOrderId());
				if (mediatorOrderExportHis != null && mediatorOrderExportHis.getIsExport() != null
						&& mediatorOrderExportHis.getIsExport().intValue() == 0) {
					iter.remove();
				}
			}
		}
	}
	
	private void updatePrintOrderStatus (List<MediatorOrderPrintHist> mediatorOrderPrintHists, int printStatus) {
		if (!CollectionUtils.isEmpty(mediatorOrderPrintHists)) {
			List<Integer> orderIds = Lists.newArrayList();
			mediatorOrderPrintHists.forEach(mediatorOrderPrint->orderIds.add(mediatorOrderPrint.getOrderId()));
			mediatorOrderPrintHistDAO.updateOrderPrintByOrderIds(orderIds, printStatus);
		}
	}
}
