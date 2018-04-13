package com.mytijian.mediator.bidirectionsync.dto;

/**
 * 
 * @author huangwei
 *
 */
public class Examiner {

	//体检客户编号，唯一
	private String customerId;
	
	//所属单位的员工号	
	private String workno;
	
	//体检人姓名
	private String name;
	
	//姓名拼音码
	private String pinYin;
	
	//身份证号
	private String idcard;
	
	//年龄
	private Integer age;
	
	//性别: 0—男 1—女
	private Integer gender;
	
	private String mobile;
	
	//婚姻状况
	private String marriage;
	
	//在职状态
	private Integer retire;
	
	//职位
	private String position;
	
	private String address;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getWorkno() {
		return workno;
	}

	public void setWorkno(String workno) {
		this.workno = workno;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPinYin() {
		return pinYin;
	}

	public void setPinYin(String pinYin) {
		this.pinYin = pinYin;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMarriage() {
		return marriage;
	}

	public void setMarriage(String marriage) {
		this.marriage = marriage;
	}

	public Integer getRetire() {
		return retire;
	}

	public void setRetire(Integer retire) {
		this.retire = retire;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
	
}
