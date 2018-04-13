package com.mytijian.mediator.ws;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.ReadHeadersInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.InterceptorChain;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public class SecurityInInterceptor extends SecurityCommonInterceptor{
	
	public SecurityInInterceptor(){
		this(Phase.READ);
	}
	
	public SecurityInInterceptor(String phase) {
		super(phase);
		addAfter(ReadHeadersInterceptor.class.getName());
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		InterceptorChain interceptorChain = message.getInterceptorChain();
		
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		if(!isEncrypt(message)){
			WSS4JInInterceptor wss4JInInterceptor = wac.getBean("wss4jInInterceptor",WSS4JInInterceptor.class);
			interceptorChain.remove(wss4JInInterceptor);
		}
	}

}
