package com.mytijian.mediator.order.inner.service;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mytijian.mediator.order.model.MediatorOrderPrintHist;
import com.mytijian.mediator.order.postDAO.MediatorOrderPrintHistDAO;

@Service("mediatorPrintOrderService")
public class MediatorPrintOrderServiceImpl implements MediatorPrintOrderService {

	private Logger logger = LoggerFactory.getLogger(MediatorPrintOrderServiceImpl.class);

	@Resource(name = "mediatorOrderPrintHistDAO")
	private MediatorOrderPrintHistDAO mediatorOrderPrintHistDAO;

	@Override
	public void addPrintOrder(List<MediatorOrderPrintHist> mediatorOrderPrintHist) {
		if (CollectionUtils.isEmpty(mediatorOrderPrintHist)) {
			logger.error("printOrders is null");
			return;
		}
		mediatorOrderPrintHistDAO.addPrintOrdersBatch(mediatorOrderPrintHist);
	}

}
