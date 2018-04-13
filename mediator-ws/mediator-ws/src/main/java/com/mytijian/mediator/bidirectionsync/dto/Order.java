package com.mytijian.mediator.bidirectionsync.dto;

import java.util.List;

/**
 * 
 * @author huangwei
 *
 */
public class Order {
	
	
	private Integer hospitalId;
	
	private String orderNum;
	
	//体检编号
	private String hisExamNumber;
	
	private String examDate;
	
	//体检操作状态 0-预登记,1-正式登记,（无现场确认，则以打印导引单为准）2-体检中,3-体检完成,4-暂停体检,5-删除订单
	private Integer examStatus;
	
	//体检类型：个人体检/单位体检
	private String examType;
	
	//体检类别：健康体检，公务员体检，职业体检，入职体检
	private String examClass;
	
	//本次体检是否VIP体检，  1-是VIP，0-非VIP订单
	private Integer vip;
	
	//介绍人名称
	private String owner;
	
	//付款类型：挂账/现付
	private String payType;
	
	//订单核算价格，单位元
	private Double orderMoney;
	
	//订单折扣 ，不打折为1
	private Double orderDiscount;
	
	private String remark;
	
	//客户基本信息
	private Examiner customerInfo;
	
	//单位信息
	private Company companyRelation;
	
	private Meal mealRelation;
	
	private List<ExamItem> listExamItem;

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getHisExamNumber() {
		return hisExamNumber;
	}

	public void setHisExamNumber(String hisExamNumber) {
		this.hisExamNumber = hisExamNumber;
	}

	public String getExamDate() {
		return examDate;
	}

	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}

	public Integer getExamStatus() {
		return examStatus;
	}

	public void setExamStatus(Integer examStatus) {
		this.examStatus = examStatus;
	}

	public String getExamType() {
		return examType;
	}

	public void setExamType(String examType) {
		this.examType = examType;
	}

	public String getExamClass() {
		return examClass;
	}

	public void setExamClass(String examClass) {
		this.examClass = examClass;
	}

	public Integer getVip() {
		return vip;
	}

	public void setVip(Integer vip) {
		this.vip = vip;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public Double getOrderMoney() {
		return orderMoney;
	}

	public void setOrderMoney(Double orderMoney) {
		this.orderMoney = orderMoney;
	}

	public Double getOrderDiscount() {
		return orderDiscount;
	}

	public void setOrderDiscount(Double orderDiscount) {
		this.orderDiscount = orderDiscount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Examiner getCustomerInfo() {
		return customerInfo;
	}

	public void setCustomerInfo(Examiner customerInfo) {
		this.customerInfo = customerInfo;
	}

	public Company getCompanyRelation() {
		return companyRelation;
	}

	public void setCompanyRelation(Company companyRelation) {
		this.companyRelation = companyRelation;
	}

	public Meal getMealRelation() {
		return mealRelation;
	}

	public void setMealRelation(Meal mealRelation) {
		this.mealRelation = mealRelation;
	}

	public List<ExamItem> getListExamItem() {
		return listExamItem;
	}

	public void setListExamItem(List<ExamItem> listExamItem) {
		this.listExamItem = listExamItem;
	}
	
}
