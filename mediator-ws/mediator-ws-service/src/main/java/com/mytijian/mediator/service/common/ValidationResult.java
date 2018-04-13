/*
 * Copyright 2016 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.service.common;
/**
 * 类ValidationResult.java的实现描述：TODO 类实现描述 
 * @author liang 2016年8月28日 上午11:23:11
 */
/**
 * 
 * <pre>
 * 验证结果，携带了Reason和是否成功
 * 
 * </pre>
 * 类ValidationResult.java的实现描述：TODO 类实现描述 
 * @author liangxing 2016年4月13日 下午7:21:33
 */
public class ValidationResult {
    
    /**
     * 是否通过
     */
    private boolean pass;
    
    /**
     * 失败原因
     */
    private String  failedReason;

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

}