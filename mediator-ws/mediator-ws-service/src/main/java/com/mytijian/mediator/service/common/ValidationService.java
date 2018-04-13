/*
 * Copyright 2016 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.service.common;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.mytijian.mediator.exceptions.ExceptionFactory;
import com.mytijian.mediator.exceptions.ServiceException;
import com.mytijian.mediator.exceptions.ServiceExceptionCode;

/**
 * 类ValidationService.java的实现描述：TODO 类实现描述 
 * @author liang 2016年8月28日 上午11:23:44
 */
@Service("validationService")
public class ValidationService implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(ValidationService.class);
    /**
     *  <pre>
     * 验证服务，能够支持Fast-failed式的验证
     * 
     * </pre>
     * 验证入口
     */
    private Validator validator;
    
    
    /**
     * <pre>
     * 验证参数，如果验证没有通过会抛出异常
     * 
     * </pre>
     * 
     * @param param
     * @throws ServiceException
     */
    public void validateParam(Object param) throws ServiceException {
        if (param == null) {
            logger.error(ServiceExceptionCode.INVALID_PARAM.getMessage(param));
            throw ExceptionFactory.makeFault(ServiceExceptionCode.INVALID_PARAM);
        }


        ValidationResult vr = validate(param);

        if (!vr.isPass()) {
            logger.error(ServiceExceptionCode.INVALID_PARAM.getMessage(param));
            throw ExceptionFactory.makeFault(ServiceExceptionCode.INVALID_PARAM,vr.getFailedReason());
        }
    }
    
    
    /**
     * <pre>
     * 给定一个参数，验证其是否符合约束，通过JSR303加以实现
     * 
     * </pre>
     * 
     * @param param
     * @return
     */
    private ValidationResult validate(Object param) {
        ValidationResult result = new ValidationResult();

        Set<ConstraintViolation<Object>> violations = validator.validate(param);
        boolean isEmpty = violations.isEmpty();
        result.setPass(isEmpty);

        // 这里设置违反的约束，设定了快速失败，其实只有一个失败项
        if (!isEmpty) {
            for (ConstraintViolation<Object> violation : violations) {
                StringBuilder sb = new StringBuilder(violation.getPropertyPath().toString());
                sb.append(violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName());
                String reason = sb.toString();
                result.setFailedReason(reason);
                break;
            }
        }
        return result;
    }

    public void afterPropertiesSet() {
        HibernateValidatorConfiguration configuration = Validation.byProvider(HibernateValidator.class).configure();
        ValidatorFactory factory = configuration.failFast(true).buildValidatorFactory();
        validator = factory.getValidator();
    }
}