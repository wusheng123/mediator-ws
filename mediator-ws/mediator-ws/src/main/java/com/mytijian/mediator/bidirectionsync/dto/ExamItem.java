package com.mytijian.mediator.bidirectionsync.dto;

/**
 * 
 * @author huangwei
 *
 */
public class ExamItem {
	
	//内网单项Code
	private String itemCode;
	
	private String itemName;
	
	private Double price;
	
	private Double discount;
	
	//折扣后价格
	private Double discountPrice;
	
	//”套餐项目”、 “增加项目”、 ” 不显示项目”
	private String itemType;
	
	//0-未检，1 -据检，2-完成体检
	private Integer itemStatus;

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
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

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public Integer getItemStatus() {
		return itemStatus;
	}

	public void setItemStatus(Integer itemStatus) {
		this.itemStatus = itemStatus;
	}
	
	
}
