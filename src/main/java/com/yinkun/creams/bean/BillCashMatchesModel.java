package com.yinkun.creams.bean;

import java.sql.Timestamp;

public class BillCashMatchesModel {
	
	private Integer id;

	private Integer billId;
	
	private Number matchedAmount;
	
	private Timestamp createdDate;
	
	private BillCashFlowModel cashFlow;

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

	public Number getMatchedAmount() {
		return matchedAmount;
	}

	public void setMatchedAmount(Number matchedAmount) {
		this.matchedAmount = matchedAmount;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public BillCashFlowModel getCashFlow() {
		return cashFlow;
	}

	public void setCashFlow(BillCashFlowModel cashFlow) {
		this.cashFlow = cashFlow;
	}
	
}
