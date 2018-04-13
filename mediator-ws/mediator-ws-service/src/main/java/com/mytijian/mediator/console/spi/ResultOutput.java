/*
 * Copyright 2016 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.mediator.console.spi;

import com.mytijian.mediator.api.model.cmd.CmdOutput;

/**
 * 类ResultOutput.java的实现描述：TODO 类实现描述 
 * @author liang 2016年9月9日 下午2:53:32
 */
public interface ResultOutput {
	void putResult(CmdOutput output);
}
