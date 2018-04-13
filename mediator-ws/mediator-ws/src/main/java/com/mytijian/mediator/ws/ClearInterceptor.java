package com.mytijian.mediator.ws;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.mytijian.mediator.api.service.EnvironmentKeeper;

/**
 * 修改日志打印文件，清除hospitalId，防止ws controller的日志打印在体检中心日志文件中
 * @author Administrator
 *
 */
public class ClearInterceptor extends HandlerInterceptorAdapter{
	
	@Resource(name = "soapHeaderInterceptor")
	private EnvironmentKeeper envKeeper;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		envKeeper.setHospital(null);
		return true;
	}

}