package com.yinkun.creams.bean;

public class BillRoomModel {
	
	private Integer id;
	
	private Integer billId;
	
	private Integer roomId;

	private String roomNumber;

	private String floor;

	private Number areaSize;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBillId() {
		return billId;
	}

	public void setBillId(Integer billId) {
		this.billId = billId;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
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
	

}
