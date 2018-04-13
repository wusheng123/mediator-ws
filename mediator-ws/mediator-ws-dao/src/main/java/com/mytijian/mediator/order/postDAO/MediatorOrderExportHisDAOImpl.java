package com.mytijian.mediator.order.postDAO;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mytijian.mediator.order.DAO.MediatorOrderExportHisMapper;
import com.mytijian.mediator.order.model.MediatorOrderExportHis;
import com.mytijian.mediator.order.postDAO.MediatorOrderExportHisDAO;
import com.mytijian.pulgin.mybatis.pagination.Page;

@Service("mediatorOrderExportHisDAO")
public class MediatorOrderExportHisDAOImpl implements MediatorOrderExportHisDAO {
	
	private Logger logger = LoggerFactory.getLogger(MediatorOrderExportHisDAOImpl.class);
	
	@Resource(name="mediatorOrderExportHisMapper")
	private MediatorOrderExportHisMapper mediatorOrderExportHisMapper;
	

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void addExportOrdersBatch(List<MediatorOrderExportHis> mediatorOrderExportHis) {
		if (mediatorOrderExportHis == null || mediatorOrderExportHis.isEmpty()) {
			logger.info("addExportOrdersBatch.order size is 0");
			return;
		}
		
		
		logger.info("addExportOrdersBatch order size : {}", mediatorOrderExportHis.size());
		mediatorOrderExportHisMapper.insertBatch(mediatorOrderExportHis);
	}
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateOrderExportByOrderIds(List<Integer> orderIds, Integer isExport) {
		mediatorOrderExportHisMapper.updateOrderExportByOrderIds(orderIds, isExport);
	}

	@Override
	public List<MediatorOrderExportHis> getMediatorHospitalOrderExportByPage(Integer hospitalId, Integer exportType,
			Integer isExport, Page page) {
		return mediatorOrderExportHisMapper.getMediatorHospitalOrderExportByPage(hospitalId, exportType, isExport, page);
	}

	@Override
	public int getMaxSequence() {
		return mediatorOrderExportHisMapper.getMaxSequence();
	}

	@Override
	public void updateOrderExportByOrderId(Integer orderId, Integer isExport) {
		mediatorOrderExportHisMapper.updateOrderExportByOrderId(orderId, isExport);
	}
	
	@Override
	public int getMediatorOrderExportHisCountByOrderId(Integer orderId) {
		return mediatorOrderExportHisMapper.getMediatorOrderExportHisCountByOrderId(orderId);
	}

	@Override
	public MediatorOrderExportHis getMediatorOrderExportHisByOrderId(Integer orderId) {
		return mediatorOrderExportHisMapper.getMediatorOrderExportHisByOrderId(orderId);
	}

}
