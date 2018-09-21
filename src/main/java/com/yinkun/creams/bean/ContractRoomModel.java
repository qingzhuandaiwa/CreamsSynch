package com.yinkun.creams.bean;

import java.math.BigDecimal;

public class ContractRoomModel {
	
	private String id;
	
	private String roomId;
	
	private String roomNumber;
	
	private String floor;
	
	private Number areaSize;
	
	private Number price;
	
	private String contractId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}


	public Number getAreaSize() {
		return areaSize;
	}

	public void setAreaSize(Number areaSize) {
		this.areaSize = areaSize;
	}

	public Number getPrice() {
		return price;
	}

	public void setPrice(Number price) {
		this.price = price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	
}
