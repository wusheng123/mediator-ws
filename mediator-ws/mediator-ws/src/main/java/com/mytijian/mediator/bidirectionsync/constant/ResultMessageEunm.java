package com.mytijian.mediator.bidirectionsync.constant;
/**
 *描述:请添加一段类名描述
 *日期:2017年12月18日
 *时间:下午4:24:22
 *作者:huangwei
*/
public enum ResultMessageEunm {
	
	
	SUCCEED("0001","订单创建成功"),
	
	DECODE_FAILED("0002","数据解密失败"),
	
	CONVER_FAILED("0003","json转换订单对象失败"),
	
	HOSPITAL_ID_IS_NULL("0004","医院ID为空"),
	
	HISBM_ORDERNUM_BOTH_NULL("0005","医院体检编号和订单号至少一个不为空"),
	
	IDCARD_ILLEGAL("0006","身份证为空或身份证格式不符"),
	
	SEX_UNCONFORMITY("0007","性别不符"),
	
	EXAM_ITME_IS_NULL("0008","体检单项内容为空"),
	
	INSIDE_ERROR("0009","系统内部错误");
	
    private String code;
	
	private String message;
    
    private ResultMessageEunm(String code,String message){
        this.message = message;
        this.code = code;
    }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	
}
