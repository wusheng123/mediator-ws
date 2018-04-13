/*
 * Copyright 2016 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.console.spi;
/**
 * 类CmdInput.java的实现描述：TODO 类实现描述 
 * @author liang 2016年9月9日 下午2:53:12
 */
public interface CmdInput {
	CmdInput getNextCmd(Integer hospitalId);
}
