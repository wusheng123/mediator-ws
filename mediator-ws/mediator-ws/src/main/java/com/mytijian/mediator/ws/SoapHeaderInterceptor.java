package com.mytijian.mediator.ws;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.x500.X500Principal;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mytijian.mediator.api.service.EnvironmentKeeper;
import com.mytijian.mediator.exceptions.MediatorException;

public class SoapHeaderInterceptor extends AbstractSoapInterceptor implements EnvironmentKeeper {
	private Logger log = LoggerFactory.getLogger(SoapHeaderInterceptor.class);
	String namespaceURI = "http://service.api.mediator.mytijian.com/";

	private Map<Integer, String> agentClients = new HashMap<Integer, String>();
	private Set<String> kickedClient = new HashSet<String>();
	
	private ThreadLocal<Integer> hospital = new ThreadLocal<Integer>();

	public SoapHeaderInterceptor() {
		super(Phase.PRE_LOGICAL);
	}

	/* 
	 * 当有新的Agent连上来时，把旧的连接断开
	 * @see org.apache.cxf.interceptor.Interceptor#handleMessage(org.apache.cxf.message.Message)
	 */
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		Header hospitalHeader = message.getHeader(new QName("hospitalId"));
		if(hospitalHeader == null) {
			return;
		}

		Integer hospitalId = new Integer(((org.w3c.dom.Element) hospitalHeader.getObject()).getTextContent());
		hospital.set(hospitalId);

		Header tokenHeader = message.getHeader(new QName(namespaceURI, "token"));
		if(tokenHeader == null) {
			// 检查agent的ssl证书
			checkSSLCertificate(message);
			return;
		}
		
		String token = ((org.w3c.dom.Element) tokenHeader.getObject()).getTextContent();
		if (token != null) {
			String oldToken = this.agentClients.get(hospitalId);			
			if (oldToken != null && !token.equals(oldToken)) {
				kickedClient.add(oldToken);
			}
			this.agentClients.put(hospitalId, token);
			
			if(kickedClient.contains(token)) {
				kickedClient.remove(token);
				log.warn("hospital client have been replaced! hospitalId = {}", hospitalId);
				MediatorException mediatorException = new MediatorException(
						"此医院的客户端被替换了，hospitalId = " + hospitalId + ", 新Token = " + token);
				mediatorException.setStop(true);
				throw new Fault(mediatorException);
			}
		}
	}

	/**
	 * 证书主题示例
	 * CN=MyTijian Agent hzlyy, OU=1, O=hzlyy, L=HangZhou, ST=ZheJiang, C=CN
	 * 
	 * 证书中OU字段表示HospitalId
	 * 如果 OU字段的ID和参数中的HospitalId不同 抛出异常
	 * @param msg
	 */
	private void checkSSLCertificate(SoapMessage msg) {
		HttpServletRequest req = (HttpServletRequest) msg.get(AbstractHTTPDestination.HTTP_REQUEST);
		String requestUrl = req.getRequestURL().toString();
		if (requestUrl.startsWith("https")) {
			X509Certificate[] cres = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");
			if (cres!=null && cres.length>0) {
				for (X509Certificate cre : cres) {
					X500Principal p = cre.getSubjectX500Principal();
					String name = p.getName();
					String findString = String.format("OU=%s", this.getHospital());
					if (name.indexOf(findString)!=-1) {
						return;
					}
				}
			}
			MediatorException mediatorException = new MediatorException("agent 证书错误! 找不到id为 "+this.getHospital()+" 的证书");
			mediatorException.setStop(true);
			throw new Fault(mediatorException);
		}
	}
	
	@Override
	public Integer getHospital() {
		return this.hospital.get();
	}

	@Override
	public void setHospital(Integer hospitalId) {
		this.hospital.set(hospitalId);
	}

}
