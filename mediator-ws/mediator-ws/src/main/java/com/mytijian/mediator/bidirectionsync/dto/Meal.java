package com.mytijian.mediator.bidirectionsync.dto;

import java.util.List;

/**
 * 
 * @author huangwei
 *
 */
public class Meal {
	
	//套餐在体检软件中的代码，唯一
	private String mealCode;
	
	private String mealName;
	
	//0-男；1-女；2-全部
	private Integer gender;
	
	private Double originalPrice;
	
	private Double discount;
	
	//套餐折后价
	private Double discountPrice;
	
	private List<ExamItem> examItemList;

	public String getMealCode() {
		return mealCode;
	}

	public void setMealCode(String mealCode) {
		this.mealCode = mealCode;
	}

	public String getMealName() {
		return mealName;
	}

	public void setMealName(String mealName) {
		this.mealName = mealName;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Double getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(Double originalPrice) {
		this.originalPrice = originalPrice;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(Double discountPrice) {
		this.discountPrice = discountPrice;
	}

	public List<ExamItem> getExamItemList() {
		return examItemList;
	}

	public void setExamItemList(List<ExamItem> examItemList) {
		this.examItemList = examItemList;
	}
	
	
}
