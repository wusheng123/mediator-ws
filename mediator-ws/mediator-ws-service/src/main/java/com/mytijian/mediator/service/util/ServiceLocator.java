package com.mytijian.mediator.service.util;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.mytijian.mediator.api.model.HisInfo;
import com.mytijian.mediator.api.service.HisInfoService;

/**
 * 根据hospitalId获取为某个体检中心特定的service服务，以此来适应多个体检中心业务逻辑不同
 * bean命名方式是原来的bean名称前面加上此体检中心所用的engine名字，如hospitalId=5的使用创业的bean
 * 原来的bean命名首字母大写.
 * 如， 原来bean名称是 orderService， 相对创业的bean是 chuangyeOrderService
 * 
 * @author twu
 *
 */
@Service("serviceLocator")
public class ServiceLocator implements ApplicationContextAware  {
	
	private ApplicationContext context;

	@Resource(name="hisInfoService")
	private HisInfoService hisInfoService;

	public <T> T getService(Class<T> t, String beanName, Integer hospitalId) {
		String hosBeanName = getHospitalBeanName(beanName, hospitalId);
		T bean;
		try {
			bean = context.getBean(hosBeanName, t);
		} catch (org.springframework.beans.factory.NoSuchBeanDefinitionException e) {
			bean = context.getBean(beanName, t);
		}
		return bean;
	}

	private String getHospitalBeanName(String beanName, Integer hospitalId) {

		HisInfo hisInfo = hisInfoService.getByHospitalId(hospitalId);
		if (hisInfo != null && StringUtils.isNotBlank(hisInfo.getEngineName())) {
			String engineName = hisInfo.getEngineName();
			beanName = engineName
					.concat(beanName.substring(0, 1).toUpperCase()).concat(
							beanName.substring(1));
		}
		return beanName;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}

}
