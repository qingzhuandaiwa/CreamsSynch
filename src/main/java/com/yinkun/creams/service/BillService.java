package com.yinkun.creams.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Record;
import com.yinkun.creams.bean.BillCashFlowModel;
import com.yinkun.creams.bean.BillCashMatchesModel;
import com.yinkun.creams.bean.BillModel;
import com.yinkun.creams.bean.BillRoomModel;
import com.yinkun.creams.bean.ContractModel;
import com.yinkun.creams.synch.FloorSynch;
import com.yinkun.creams.utils.DbHelper;

public class BillService {
	
	static Logger logger = Logger.getLogger(FloorSynch.class);

	private static String timeFormat = "yyyy-MM-dd HH:mm:ss";
	
	public static Date getLastUpdateDate() {
		String sql = "SELECT * from bill ORDER BY utime DESC LIMIT 1";
		Record rcd = DbHelper.getDb().findFirst(sql);
		if(rcd!=null) {
			return rcd.getDate("utime");
		}else {
			return null;
		}
	}
	
	/**
	 * 插入数据
	 * 
	 * @param parks
	 * @return
	 */
	public static void insertDataS(List<BillModel> bills) throws Exception {
		List<String> sqlList = new ArrayList<String>();
		StringBuilder billCashMatchDelSql = new StringBuilder("(");
		StringBuilder billRoomDelSql = new StringBuilder("(");
		StringBuilder cashFlowDelSql = new StringBuilder("(");

		try {
			for (Iterator<BillModel> it = bills.iterator(); it.hasNext();) {
				BillModel bill = it.next();

				billCashMatchDelSql.append(bill.getId() + ",");
				
				//拼接需要删除的room id
				billRoomDelSql.append(bill.getId() + ",");
				
				List<BillCashMatchesModel> cashMatches = bill.getBillCashMatches();
				if(cashMatches != null && cashMatches.size() > 0) {
					for(Iterator<BillCashMatchesModel> cashMatchIt = cashMatches.iterator(); cashMatchIt.hasNext();) {
						BillCashMatchesModel billCashMatch = cashMatchIt.next();
						Integer billCashMatchid = billCashMatch.getId();
						cashFlowDelSql.append(billCashMatchid + ",");
					}
				}


				String createdDate = null;
				if (bill.getCreatedDate()!= null) {
					createdDate = "'" + bill.getCreatedDate() + "'";
				}
				String endDate = null;
				if (bill.getEndDate() != null) {
					endDate = "'" + bill.getEndDate() + "'";
				}

				String payDate = null;
				if (bill.getPayDate() != null) {
					payDate = "'" + bill.getPayDate() + "'";
				}
				
				String settledDate = null;
				if (bill.getSettledDate() != null) {
					settledDate = "'" + bill.getSettledDate() + "'";
				}
				
				String startDate = null;
				if (bill.getStartDate() != null) {
					startDate = "'" + bill.getStartDate() + "'";
				}

				String sql = "INSERT INTO bill "
						+ "(bill_id,bill_no,action,adjusted_prime_amount,actual_amount,rate,over_due_fine_theory_amount,"
						+ "bill_type,building_id,building_name,closed_status,closed_status_name,"
						+ "created_date,date_scope,due_status_name,end_date,expired_day,generated_reminder_count,"
						+ "handler,invoice_amount,invoice_status,invoice_status_name,issue_receipt_count,match_id,matched_amount,monetary_unit,"
						+ "object_id,object_type,order_no,other,over_due_fine_status,overDueFineStatusName,pay_date,"
						+ "payed_amount,prime_amount,receipt_amount,remaining_amount,room_number,settle_status,settle_status_name,"
						+ "settledDate,start_date,tenant_id,term_id,termination_income_adjust,theory_amount,"
						+ "transfer_amount,transfer_to_other_bill_amount,type_nme,is_del,ctime,utime) VALUES " + "(" + bill.getId() + ", '"
						+ bill.getBillNo() + "', '" + bill.getAction() + "', " + bill.getAdjustedPrimeAmount() + ", " + bill.getActualAmount() + "," + bill.getRate() + "," + bill.getOverDueFineTheoryAmount() + ",'" 
						+ bill.getBillType() + "', '" + bill.getBuildingId() + "', '" + bill.getBuildingName() + "', '" + bill.getClosedStatus() + "','" + bill.getClosedStatusName() + "',"
						+ createdDate + ",'" + bill.getDateScope() + "','" + bill.getDueStatusName() + "', " + endDate + ", " + bill.getExpiredDay() + ", " + bill.getGeneratedReminderCount() + ", '"
						+ bill.getHandler() + "', '" + bill.getInvoiceAmount() + "', '" + bill.getInvoiceStatus() + "', '" + bill.getInvoiceStatusName() + "', " + bill.getIssueReceiptCount() + ", '" + bill.getMatchId() + "', "
						+ bill.getMatchedAmount() + ", '" + bill.getMonetaryUnit() + "', '" 
						+ bill.getObjectId() + "', '"  + bill.getObjectType() + "', '" + bill.getOrderNo() + "', '" + bill.getOther() + "', '"  + bill.getOverDueFineStatus() + "', '" + bill.getOverDueFineStatusName() + "', " + payDate + ", " 
						+ bill.getPayedAmount() + ", " + bill.getPrimeAmount() + ", " + bill.getReceiptAmount() + ", " + bill.getRemainingAmount() + ", '" + bill.getRoomNumber() + "', '" + bill.getSettleStatus() + "', '" + bill.getSettleStatusName() + "', "
						+ settledDate + ", " + startDate + ", '" + bill.getTenantId() + "', '" + bill.getTermId() + "', " + bill.getTerminationIncomeAdjust() + ", " + bill.getTheoryAmount() + ", "
						+ bill.getTransferAmount() + ", " + bill.getTransferToOtherBillAmount() + ", '" + bill.getTypeName() + "', '" + bill.getIsDel() + "', '" + bill.getCtime() + "', '" + bill.getUtime() + "');";
				sqlList.add(sql);
			}
			
			int lastIndx0 = billCashMatchDelSql.lastIndexOf(",");
//			int length = billCashMatchDelSql.length();
			if (lastIndx0 > 0) {
				billCashMatchDelSql.deleteCharAt(lastIndx0);
			}
			billCashMatchDelSql.append(")");
			
			int lastIndx1 = billRoomDelSql.lastIndexOf(",");
//			int length = billRoomDelSql.length();
			if (lastIndx1 > 0) {
				billRoomDelSql.deleteCharAt(lastIndx1);
			}
			billRoomDelSql.append(")");
			
			int lastIndx2 = cashFlowDelSql.lastIndexOf(",");
			if (lastIndx2 > 0) {
				cashFlowDelSql.deleteCharAt(lastIndx2);
				cashFlowDelSql.append(")");
				String delcashFlowSql = "delete from bill_cash_flow where bill_cash_match_id in " + cashFlowDelSql.toString();
				DbHelper.getDb().update(delcashFlowSql);
			}
			
			
			String delBillCatchMatchesSql = "delete from bill_cash_matches where bill_id in " + billCashMatchDelSql.toString();
			String delBillRoomSql = "delete from bill_room where bill_id in " + billRoomDelSql.toString();

			DbHelper.getDb().batch(sqlList, sqlList.size());//
			DbHelper.getDb().update(delBillCatchMatchesSql);
			DbHelper.getDb().update(delBillRoomSql);
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :bill 批量插入失败！");
			e.printStackTrace();
			throw e;
		}

	}
	
	
	/**
	 * 修改数据
	 * 
	 * @param parks
	 * @return
	 */
	public static void updateDataS(List<BillModel> bills) throws Exception {
		List<String> sqlList = new ArrayList<String>();
		StringBuilder billCashMatchDelSql = new StringBuilder("(");
		StringBuilder billRoomDelSql = new StringBuilder("(");
		StringBuilder cashFlowDelSql = new StringBuilder("(");

		try {
			for (Iterator<BillModel> it = bills.iterator(); it.hasNext();) {
				BillModel bill = it.next();

				billCashMatchDelSql.append(bill.getId() + ",");
				
				//拼接需要删除的room id
				billRoomDelSql.append(bill.getId() + ",");
				
				List<BillCashMatchesModel> cashMatches = bill.getBillCashMatches();
				if(cashMatches != null && cashMatches.size() > 0) {
					for(Iterator<BillCashMatchesModel> cashMatchIt = cashMatches.iterator(); cashMatchIt.hasNext();) {
						BillCashMatchesModel billCashMatch = cashMatchIt.next();
						Integer billCashMatchid = billCashMatch.getId();
						cashFlowDelSql.append(billCashMatchid + ",");
					}
				}


				String createdDate = null;
				if (bill.getCreatedDate()!= null) {
					createdDate = "'" + bill.getCreatedDate() + "'";
				}
				String endDate = null;
				if (bill.getEndDate() != null) {
					endDate = "'" + bill.getEndDate() + "'";
				}

				String payDate = null;
				if (bill.getPayDate() != null) {
					payDate = "'" + bill.getPayDate() + "'";
				}
				
				String settledDate = null;
				if (bill.getSettledDate() != null) {
					settledDate = "'" + bill.getSettledDate() + "'";
				}
				
				String startDate = null;
				if (bill.getStartDate() != null) {
					startDate = "'" + bill.getStartDate() + "'";
				}

				String sql = "update bill set bill_no='" + bill.getBillNo() + "', action='" + bill.getAction() + "',adjusted_prime_amount=" + bill.getAdjustedPrimeAmount() + ",actual_amount=" + bill.getActualAmount() + ",rate=" + bill.getRate() + ",over_due_fine_theory_amount=" + bill.getOverDueFineTheoryAmount() + 
						",bill_type='" + bill.getBillType() + "', building_id='" + bill.getBuildingId() + "', building_name='" + bill.getBuildingName() + "', closed_status='" + bill.getClosedStatus() + "', closed_status_name='" + bill.getClosedStatusName() 
						+ "', " + createdDate + ",date_scope='" + bill.getDateScope() + "',due_status_name='" + bill.getDueStatusName() + "', end_date=" + endDate + ", expired_day=" + bill.getExpiredDay() + ",generated_reminder_count=" + bill.getGeneratedReminderCount() 
						+ ",handler='" + bill.getHandler() + "',invoice_amount='" + bill.getInvoiceAmount() + "', invoice_status='" + bill.getInvoiceStatus() + "', invoice_status_name='" + bill.getInvoiceStatusName() + "', issue_receipt_count=" + bill.getIssueReceiptCount() + ",match_id='" + bill.getMatchId() 
						+ "', matched_amount=" + bill.getMatchedAmount() + ",monetary_unit='" + bill.getMonetaryUnit() 
						+ "', object_id='" + bill.getObjectId() + "', object_type='" + bill.getObjectType() + "', order_no='" + bill.getOrderNo() + "', other='" + bill.getOther() + "', over_due_fine_status='" + bill.getOverDueFineStatus() + "', overDueFineStatusName='" + "', pay_date=" + payDate 
						+ ",payed_amount=" + bill.getPayedAmount() + ",prime_amount=" + bill.getPrimeAmount() + ",receipt_amount=" + bill.getReceiptAmount() + ", remaining_amount=" + bill.getRemainingAmount() + ", room_number='" + bill.getRoomNumber() + "', settle_status='" + bill.getSettleStatus() + "', settle_status_name='" + bill.getSettleStatusName() 
						+ "', settledDate=" + bill.getSettledDate() + ", start_date=" + startDate + ",tenant_id='" + bill.getTenantId() + "', term_id='" + bill.getTermId() + "', termination_income_adjust=" + bill.getTerminationIncomeAdjust() + ", theory_amount=" + bill.getTheoryAmount() 
						+ ",transfer_amount=" + bill.getTransferAmount() + ",transfer_to_other_bill_amount=" + bill.getTransferToOtherBillAmount() + ",type_nme='" + bill.getTypeName() + "', is_del='" + bill.getIsDel() + "', ctime='" + bill.getCtime() + "',utime='" + bill.getUtime() + "');";
				
				sqlList.add(sql);
				
			}
			
			int lastIndx0 = billCashMatchDelSql.lastIndexOf(",");
//			int length = billCashMatchDelSql.length();
			if (lastIndx0 > 0) {
				billCashMatchDelSql.deleteCharAt(lastIndx0);
			}
			billCashMatchDelSql.append(")");
			
			int lastIndx1 = billRoomDelSql.lastIndexOf(",");
//			int length = billRoomDelSql.length();
			if (lastIndx1 > 0) {
				billRoomDelSql.deleteCharAt(lastIndx1);
			}
			billRoomDelSql.append(")");
			
			int lastIndx2 = cashFlowDelSql.lastIndexOf(",");
			if (lastIndx2 > 0) {
				cashFlowDelSql.deleteCharAt(lastIndx2);
				cashFlowDelSql.append(")");
				String delcashFlowSql = "delete from bill_cash_flow where bill_cash_match_id in " + cashFlowDelSql.toString();
				DbHelper.getDb().update(delcashFlowSql);
			}
			
			
			String delBillCatchMatchesSql = "delete from bill_cash_matches where bill_id in " + billCashMatchDelSql.toString();
			String delBillRoomSql = "delete from bill_room where bill_id in " + billRoomDelSql.toString();

			DbHelper.getDb().batch(sqlList, sqlList.size());//
			DbHelper.getDb().update(delBillCatchMatchesSql);
			DbHelper.getDb().update(delBillRoomSql);
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :bill 批量插入失败！");
			e.printStackTrace();
			throw e;
		}

	}
	
	
	
	public static void insertBillCashMatches(List<BillCashMatchesModel> dataS) throws Exception {
		List<String> sqlList = new ArrayList<String>();
		try {
			for (Iterator<BillCashMatchesModel> it = dataS.iterator(); it.hasNext();) {
				BillCashMatchesModel billCashMatch =  it.next();
				
				String createdDate = null;
				if (billCashMatch.getCreatedDate()!= null) {
					createdDate = "'" + billCashMatch.getCreatedDate() + "'";
				}
				
				String sql = "INSERT INTO bill_cash_matches "
						+ "(id,bill_id,matched_amount,created_date) VALUES " + "(" + billCashMatch.getId() + ", '"
						+ billCashMatch.getBillId() + "', " + billCashMatch.getMatchedAmount() + ", " + createdDate +  ");";
				sqlList.add(sql);
			}
			
			DbHelper.getDb().batch(sqlList, sqlList.size());
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :billCashMatches 批量插入失败！");
			e.printStackTrace();
			throw e;
		}
		
	}
	
	public static void insertBillRooms(List<BillRoomModel> dataS) throws Exception {
		List<String> sqlList = new ArrayList<String>();
		try {
			for (Iterator<BillRoomModel> it = dataS.iterator(); it.hasNext();) {
				BillRoomModel billRoom =  it.next();
				
				String sql = "INSERT INTO bill_room "
						+ "(id,room_id,bill_id,room_number,floor,area_size) VALUES " + "(" + billRoom.getId() + ", "
						+ billRoom.getRoomId() + ", " + billRoom.getBillId() + ", '" + billRoom.getRoomNumber() + "', '" + billRoom.getFloor() + "', " + billRoom.getAreaSize() +  ");";
				sqlList.add(sql);
			}
			
			DbHelper.getDb().batch(sqlList, sqlList.size());
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :billRooms 批量插入失败！");
			e.printStackTrace();
			throw e;
		}
	}
	
	
	public static void insertCashFlows(List<BillCashFlowModel> dataS) throws Exception {
		List<String> sqlList = new ArrayList<String>();
		try {
			for (Iterator<BillCashFlowModel> it = dataS.iterator(); it.hasNext();) {
				BillCashFlowModel cashFlow =  it.next();
				
				String enterDate = null;
				if (cashFlow.getEnterDate()!= null) {
					enterDate = "'" + cashFlow.getEnterDate() + "'";
				}
				
				
				String sql = "INSERT INTO bill_cash_flow "
						+ "(id,action,amount,digest,enter_date,flow_no,match_id,matched_amount,other_account,receipt_no,remittance_method,tenant_name,bill_cash_match_id) VALUES " 
						+ "(" + cashFlow.getId() + ", '"+ cashFlow.getAction()+ "', " + cashFlow.getAmount() + ", '" + cashFlow.getDigest() + "', " 
						+ enterDate + ", '" + cashFlow.getFlowNo() + "', '" + cashFlow.getMatchId() + "', " + cashFlow.getMatchedAmount() 
						+ ", '" + cashFlow.getOtherAccount() + "', '" + cashFlow.getReceiptNo() + "', '" + cashFlow.getRemittanceMethod() + "', '" 
						+ cashFlow.getTenantName() + "', '" + cashFlow.getBillCashMatchId() + "');";
				sqlList.add(sql);
			}
			
			DbHelper.getDb().batch(sqlList, sqlList.size());
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :billRooms 批量插入失败！");
			e.printStackTrace();
			throw e;
		}
	}
	
}
