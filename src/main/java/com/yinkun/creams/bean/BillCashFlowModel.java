package com.yinkun.creams.bean;

import java.sql.Timestamp;

public class BillCashFlowModel {
	
	private Integer id;

	private String action;
	
	private Number amount;
	
	private String digest;
	
	private Timestamp enterDate;
	
	private String flowNo;
	
	private String matchId;
	
	private Number matchedAmount;
	
	private String otherAccount;
	
	private String receiptNo;
	
	private String remittanceMethod;
	
	private String tenantName;
	
	private Integer billCashMatchId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Number getAmount() {
		return amount;
	}

	public void setAmount(Number amount) {
		this.amount = amount;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public Timestamp getEnterDate() {
		return enterDate;
	}

	public void setEnterDate(Timestamp enterDate) {
		this.enterDate = enterDate;
	}

	public String getFlowNo() {
		return flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

	public String getMatchId() {
		return matchId;
	}

	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}

	public Number getMatchedAmount() {
		return matchedAmount;
	}

	public void setMatchedAmount(Number matchedAmount) {
		this.matchedAmount = matchedAmount;
	}

	public String getOtherAccount() {
		return otherAccount;
	}

	public void setOtherAccount(String otherAccount) {
		this.otherAccount = otherAccount;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getRemittanceMethod() {
		return remittanceMethod;
	}

	public void setRemittanceMethod(String remittanceMethod) {
		this.remittanceMethod = remittanceMethod;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public Integer getBillCashMatchId() {
		return billCashMatchId;
	}

	public void setBillCashMatchId(Integer billCashMatchId) {
		this.billCashMatchId = billCashMatchId;
	}
	
}
