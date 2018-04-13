package com.mytijian.mediator.ws;

import java.util.Map;

import javax.annotation.Resource;
import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;

import com.mytijian.mediator.service.util.SecuritySwitchManager;

public abstract class SecurityCommonInterceptor extends AbstractSoapInterceptor{
	
	@Resource(name = "securitySwitchManager")
	private SecuritySwitchManager securitySwitchManager;
	
	public SecurityCommonInterceptor(String phase) {
		super(phase);
	}
	
	protected boolean isEncrypt(SoapMessage message){
		Header hospitalHeader = message.getHeader(new QName("hospitalId"));
		Integer hospitalId = new Integer(((org.w3c.dom.Element) hospitalHeader.getObject()).getTextContent());
		Map<Integer,Integer> securityVersionMap =securitySwitchManager.getSecurityVersionMap();
		if(securityVersionMap != null && securityVersionMap.containsKey(hospitalId) && securityVersionMap.get(hospitalId).equals(2)){
			return false;
		}
		return true;
	}
	
}	
