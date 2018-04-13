package com.mytijian.mediator.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class GlobalSpringContextListener implements ServletContextListener {

	public static ApplicationContext context;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		context = null;
	}

}
