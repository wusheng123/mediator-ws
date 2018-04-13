package com.mytijian.appops.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestParam;

import com.mytijian.webservice.SmsSender;

//@RestController
public class MessageController {
//	@Resource(name="smsSender")
	private SmsSender smsSender;

//	@RequestMapping(value = "/message/smsSender", method = RequestMethod.POST)
	public void smsSender(HttpServletRequest request,@RequestParam(value = "alertphone")String alertphone){
		if(alertphone == null){
			return;
		}
		String[] phones = alertphone.split(",");
		if(phones == null){
			return ;
		}
		StringBuilder builder = new StringBuilder();
		try {
			request.getReader().lines().forEach(line -> builder.append(line));
		} catch (IOException e) {
			
		}
		String message = builder.toString();
		for(String phone : phones){
			smsSender.send(phone,message);
		}
	}
}
