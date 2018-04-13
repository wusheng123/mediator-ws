package com.mytijian.mediator.ws;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.InterceptorChain;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public  class SecurityOutInterceptor extends SecurityCommonInterceptor  {
	
	public SecurityOutInterceptor(){
		this(Phase.PRE_LOGICAL);
	}
	
	public SecurityOutInterceptor(String phase) {
		super(phase);
	}
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		InterceptorChain interceptorChain = message.getInterceptorChain();
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		if(!isEncrypt(message)){
			WSS4JOutInterceptor wss4JOutInterceptor = wac.getBean("wss4jOutInterceptor",WSS4JOutInterceptor.class);
			interceptorChain.remove(wss4JOutInterceptor);
		}
	}
	
}
