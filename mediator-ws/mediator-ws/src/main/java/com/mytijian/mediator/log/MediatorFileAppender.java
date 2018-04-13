package com.mytijian.mediator.log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.mytijian.mediator.api.service.EnvironmentKeeper;
import com.mytijian.mediator.listener.GlobalSpringContextListener;

public class MediatorFileAppender extends RollingFileAppender {
	private Logger logger = LoggerFactory.getLogger(MediatorFileAppender.class);

	private EnvironmentKeeper envKeeper;
	private Map<Integer, RollingFileAppender> appenders = new HashMap<Integer, RollingFileAppender>();

	private String getFileNameWithId(String logfileName) {
		Integer hospitalId = getHospitalId();
		if (hospitalId != null) {
			int idx = logfileName.lastIndexOf('/') + 1;
			logfileName = logfileName.substring(0, idx) + hospitalId + "." + logfileName.substring(idx);
		}
		return logfileName;
	}

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
		if (hospitalId != null) {
			RollingFileAppender appender = appenders.get(hospitalId);
			if (appender == null) {
				try {
					appender = new RollingFileAppender(this.layout,
							getFileNameWithId(this.fileName), false);
					appender.setThreshold(this.threshold);
					appender.setMaximumFileSize(maxFileSize);
					appender.setMaxBackupIndex(this.maxBackupIndex);
					appender.setAppend(this.fileAppend);
					appenders.put(hospitalId, appender);
				} catch (IOException e1) {
					logger.error("创建日志文件失败,hosp id :" + hospitalId, e1);
					return;
				}
			}
			
			appender.append(event);
		} else {
			super.append(event);
		}
	}

}
