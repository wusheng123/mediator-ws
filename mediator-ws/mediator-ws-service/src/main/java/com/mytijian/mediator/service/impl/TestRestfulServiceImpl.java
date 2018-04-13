package com.mytijian.mediator.service.impl;

import org.springframework.stereotype.Service;

import com.mytijian.mediator.api.service.TestRestfulService;

@Service("testRestfulService")
public class TestRestfulServiceImpl implements TestRestfulService {

	@Override
	public String sayHello() {
		return "hello";
	}

}
