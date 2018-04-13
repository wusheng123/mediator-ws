package com.mytijian.mediator.log;

import org.apache.log4j.spi.LoggingEvent;
import org.springframework.context.ApplicationContext;

import com.mytijian.mediator.api.service.EnvironmentKeeper;
import com.mytijian.mediator.listener.GlobalSpringContextListener;

import biz.paluch.logging.gelf.log4j.GelfLogAppender;

/**
 * 把med日志记录到graylog中
 * @author linzhihao
 */
public class MediatorGrayLogAppender extends GelfLogAppender {

	private EnvironmentKeeper envKeeper;
	
	private Integer getHospitalId() {
		if (envKeeper == null) {
			ApplicationContext context = GlobalSpringContextListener.context;
			if (context != null) {
				envKeeper = (EnvironmentKeeper) context.getBean("soapHeaderInterceptor");
			}
		}
		return envKeeper == null ? null : envKeeper.getHospital();
	}

	@Override
	public void append(LoggingEvent event) {
		Integer hospitalId = getHospitalId();
		Object logMsg = event.getMessage();
		if (hospitalId != null && logMsg instanceof String) {
			String myMsg  = logMsg + " HOSPITAL_ID:" + hospitalId;
			
			LoggingEvent eventWrap = new LoggingEvent(event.getFQNOfLoggerClass(),
					event.getLogger(), event.getTimeStamp(), event.getLevel(),
					myMsg, event.getThreadName(), event.getThrowableInformation(),
					event.getNDC(), event.getLocationInformation(), event.getProperties());
			super.append(eventWrap);
		} else {
			super.append(event);
		}
	}

}
