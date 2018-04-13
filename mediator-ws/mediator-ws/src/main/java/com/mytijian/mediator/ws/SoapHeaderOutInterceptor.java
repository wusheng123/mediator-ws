/*
 * Copyright 2017 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.ws;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mytijian.mediator.api.service.CommonService;
import com.mytijian.mediator.api.service.EnvironmentKeeper;
import com.mytijian.mediator.common.constant.Cookie;
import com.mytijian.mediator.common.model.MediatorVersion;
import com.mytijian.mediator.common.service.MediatorVersionService;

/**
 * 类SoapHeaderOutInterceptor.java的实现描述：TODO 类实现描述 
 * @author liangxing 2017年3月2日 下午4:03:06
 */
public class SoapHeaderOutInterceptor extends AbstractSoapInterceptor {
	private Logger logger = LoggerFactory.getLogger(SoapHeaderOutInterceptor.class);

	@Resource(name = "soapHeaderInterceptor")
	private EnvironmentKeeper envKeeper;
	
	@Resource(name = "mediatorVersionService")
	private MediatorVersionService mediatorVersionService;

	/**
	 * @param p
	 */
	public SoapHeaderOutInterceptor() {
		super(Phase.PRE_LOGICAL);
	}

	/* (non-Javadoc)
	 * @see org.apache.cxf.interceptor.Interceptor#handleMessage(org.apache.cxf.message.Message)
	 */
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		try {
			MediatorVersion version = mediatorVersionService.getVersionByHospitalId(envKeeper.getHospital());
			// 加上命名空间
			QName cookiesName = new QName(CommonService.NS_URL, Cookie.COOKIES);
			String cookies = StringUtils.EMPTY;
			if (version != null) {
				// 头信息不能为空。判断为空时，设置为空字符串
				cookies = version.getVersion() == null ? StringUtils.EMPTY
						: version.getVersion();
			}
			
			Header header = new Header(cookiesName, cookies, new JAXBDataBinding(String.class));
			message.getHeaders().add(header);

		} catch (JAXBException e) {
			logger.error("在SOAP消息头加入hospitalId失败！", e);
		}

	}

}
