package com.mytijian.mediator.company.inner.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.mytijian.company.model.CompanyHisRelation;
import com.mytijian.company.service.SyncCrmHisCompanyService;

@Service("syncCrmHisCompanyServiceAdapter")
public class SyncCrmHisCompanyServiceAdapter {
	
	@Resource(name = "syncCrmHisCompanyService")
	private SyncCrmHisCompanyService syncCrmHisCompanyService;
	
	public boolean supportNewSyncCompany(Integer hospitalId) {
		List<Integer> hospList = syncCrmHisCompanyService.supportSyncHospital();

		return CollectionUtils.isNotEmpty(hospList)
				&& hospList.contains(hospitalId);
	}
	
	public CompanyHisRelation getHisCompany(Integer newCompanyId)
			throws Exception {
		List<CompanyHisRelation> companyHisRelList = syncCrmHisCompanyService
				.getHisCompanyCodeByNewCompanyId(newCompanyId);
		if (CollectionUtils.isEmpty(companyHisRelList)) {
			return null;
		}
		return companyHisRelList.get(0);
	}
}
