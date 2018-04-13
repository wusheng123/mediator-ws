package com.mytijian.mediator.bidirectionsync.dto;

/**
 * 
 * @author huangwei
 *
 */
public class Company {
	
	//所对应的团体单位代码  个人散客，则为空;	
	private String companyId;
	
	//团体单位名称
	private String companyName;
	
	//付款单位代码
	private String feeCompanyId;
	
	//付款单位名称
	private String feeCompanyName;
	
	//团体单位的二级部门名称
	private String department;
	
	//分组名称
	private String groupName;
	
	private Double discount;
	
	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getFeeCompanyId() {
		return feeCompanyId;
	}

	public void setFeeCompanyId(String feeCompanyId) {
		this.feeCompanyId = feeCompanyId;
	}

	public String getFeeCompanyName() {
		return feeCompanyName;
	}

	public void setFeeCompanyName(String feeCompanyName) {
		this.feeCompanyName = feeCompanyName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	
}
