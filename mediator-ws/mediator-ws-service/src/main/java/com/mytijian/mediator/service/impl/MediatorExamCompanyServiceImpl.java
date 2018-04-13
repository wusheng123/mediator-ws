package com.mytijian.mediator.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Service;

import com.mytijian.company.enums.CompanyChangeStatusEnum;
import com.mytijian.company.hospital.service.HospitalCompanyService;
import com.mytijian.company.hospital.service.model.HospitalCompany;
import com.mytijian.company.model.ExamCompanyChangeLog;
import com.mytijian.company.service.CompanyChangeLogService;
import com.mytijian.company.service.constant.CompanyConstant;
import com.mytijian.mediator.api.dto.HisNameUpdateDto;
import com.mytijian.mediator.api.enums.CompanySyncType;
import com.mytijian.mediator.api.model.SyncCompanyChangeLog;
import com.mytijian.mediator.api.service.EnvironmentKeeper;
import com.mytijian.mediator.api.service.MediatorExamCompanyService;
import com.mytijian.mediator.exceptions.MediatorException;
import com.mytijian.mediator.service.util.ServiceLocator;
import com.mytijian.mongodb.MongoException;
import com.mytijian.resource.service.HospitalService;

@Service("mediatorExamCompanyService")
public class MediatorExamCompanyServiceImpl implements MediatorExamCompanyService {
	
	private static Logger logger = LoggerFactory.getLogger(MediatorExamCompanyServiceImpl.class);

	@Resource(name = "soapHeaderInterceptor")
	private EnvironmentKeeper envKeeper;
	
	@Resource(name = "companyChangeLogService")
	private CompanyChangeLogService companyChangeLogService;
	
	@Resource(name = "hospitalService")
	private HospitalService hospitalService;
	
	@Resource(name = "serviceLocator")
	private ServiceLocator serviceLocator;
	
	@Resource(name = "hospitalCompanyService")
	private HospitalCompanyService  hospitalCompanyService;
	
	private static final Integer OLD_NEW_COMPANY_PARTITION_ID = 4400000;
	
	@Override
	public List<SyncCompanyChangeLog> getAllSpiCompany() {

		Integer hospitalId = envKeeper.getHospital();

		List<HospitalCompany> hosCompnayList = hospitalCompanyService
				.listCompanyByHospital(hospitalId);
		List<SyncCompanyChangeLog> logList = new ArrayList<SyncCompanyChangeLog>();

		hosCompnayList.forEach(company -> {
			SyncCompanyChangeLog log = new SyncCompanyChangeLog();
			log.setCompanyId(this.getCompanyId(company));
			log.setName(company.getName());
			log.setChangeType(CompanySyncType.SpiNew.getCode());
			log.setOldName(company.getName());
			log.setIsComplete(SyncCompanyChangeLog.NOT_COMPLETE);
			logList.add(log);
			logger.info("初始化单位到体检中心 name={},company_id={}", company.getName(),
					company.getId());
		});

		return logList;
	}

	private Integer getCompanyId(HospitalCompany hospitalCompany) {
		if (hospitalCompanyService.isGuestCompany(hospitalCompany.getId())) {
			return CompanyConstant.EXAM_COMPANY_ID;
		}
		if (hospitalCompany.getTbExamCompanyId() != null) {
			return hospitalCompany.getTbExamCompanyId();
		}
		return hospitalCompany.getId();
	}

	@Override
	public List<SyncCompanyChangeLog> getChangedSpiCompany() throws MediatorException {
		List<SyncCompanyChangeLog> syncLogList = new ArrayList<SyncCompanyChangeLog>();
		try {
			List<ExamCompanyChangeLog> spiLog = companyChangeLogService.getChangeLogsToSync(envKeeper.getHospital());
			// 改为同步中
			companyChangeLogService.updateStatus(envKeeper.getHospital(), CompanyChangeStatusEnum.UnSync,
					CompanyChangeStatusEnum.Syncing);

			spiLog.forEach(log -> {
				SyncCompanyChangeLog syncLog = new SyncCompanyChangeLog();
				BeanUtils.copyProperties(log, syncLog);
				syncLogList.add(syncLog);
				logger.info("同步单位到体检中心 name={},company_id={}", log.getName(), log.getCompanyId());
			});
		} catch (MongoException | BeansException e) {
			logger.error("get changed spi company error", e);
			throw new MediatorException(e);
		}
		return syncLogList;
	}

	@Override
	public void updateStatusAfterComplete()  throws MediatorException{
		try {
			companyChangeLogService.updateStatus(envKeeper.getHospital(), CompanyChangeStatusEnum.Syncing,
					CompanyChangeStatusEnum.SyncComplete);
		} catch (MongoException e) {
			logger.error("update status failed",e);
			throw new MediatorException(e);
		}
	}

	@Override
	public void sendChangedHisCompany(List<HisNameUpdateDto> list) {
		logger.info("changed his comp size :" + list.size());
		for (HisNameUpdateDto hisLog : list) {
			if (hisLog.getCompanyId() >= OLD_NEW_COMPANY_PARTITION_ID) {
				hospitalCompanyService.updateHisName(hisLog.getCompanyId(), hisLog.getHisName());
			} else {
				hospitalCompanyService.oldUpdateHisName(envKeeper.getHospital(), hisLog.getCompanyId(),
						hisLog.getHisName());
			}
		}
	}
	
	@Override
	public int countChangedSpiCompany() {
		try {
			return companyChangeLogService.getChangeLogsToSync(envKeeper.getHospital()).size();
		} catch (MongoException e) {
			logger.error("getChangeLogsToSync error", e);
			return 0;
		}
	}

	@Override
	public void updateChangeStatus(Integer companyId, String name, CompanyChangeStatusEnum status) {
		try {
			companyChangeLogService.updateStatus(envKeeper.getHospital(), companyId, name, status);
		} catch (MongoException e) {
			logger.error("update Change Status error", e);
		}
	}

}
