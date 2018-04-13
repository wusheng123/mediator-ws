package com.mytijian.mediator.service.util;

import java.text.ParseException;

import com.mytijian.util.DateUtils;

public class ConvertUtil {
	public static Integer convertInteger(String i){
		return i == null ? null : Integer.valueOf(i);
	}
	public static void main(String[] args) throws ParseException{
		System.out.println(DateUtils.parse("yyyy-MM-dd HH:mm:ss", "2012-10-11 12:13:14"));
	}
}
