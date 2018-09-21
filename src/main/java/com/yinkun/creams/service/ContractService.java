package com.yinkun.creams.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Record;
import com.yinkun.creams.bean.ContractContactModel;
import com.yinkun.creams.bean.ContractLeaseTermModel;
import com.yinkun.creams.bean.ContractModel;
import com.yinkun.creams.bean.ContractRoomModel;
import com.yinkun.creams.bean.TenantModel;
import com.yinkun.creams.synch.FloorSynch;
import com.yinkun.creams.utils.DbHelper;

public class ContractService {
	static Logger logger = Logger.getLogger(FloorSynch.class);

	private static String timeFormat = "yyyy-MM-dd HH:mm:ss";

	public static Date getLastUpdateDate() {
		String sql = "SELECT * from contract ORDER BY utime DESC LIMIT 1";
		Record rcd = DbHelper.getDb().findFirst(sql);
		if (rcd != null) {
			return rcd.getDate("utime");
		} else {
			return null;
		}
	}

	/**
	 * 插入数据
	 * 
	 * @param parks
	 * @return
	 */
	public static void insertDataS(List<ContractModel> contracts) throws Exception {
//		int[] count = null;
//		boolean isInsertSuccess = false;
		List<String> sqlList = new ArrayList<String>();
		StringBuilder sqlForDel = new StringBuilder("(");

		try {
			for (Iterator<ContractModel> it = contracts.iterator(); it.hasNext();) {
				ContractModel contract = it.next();

				sqlForDel.append(contract.getId() + ",");

				String leaseBeginDate = null;
				if (contract.getLeaseBeginDate() != null) {
					leaseBeginDate = "'" + contract.getLeaseBeginDate() + "'";
				}
				String leaseEndDate = null;
				if (contract.getLeaseEndDate() != null) {
					leaseEndDate = "'" + contract.getLeaseEndDate() + "'";
				}

				String signDate = null;
				if (contract.getSignDate() != null) {
					signDate = "'" + contract.getSignDate() + "'";
				}

				String sql = "INSERT INTO contract "
						+ "(contract_id,contract_no,enterprise_id,enterprise_name,contract_state,building_id,building_name,"
						+ "building_type,deposit,depositUnitEnum,lease_begin_date,lease_end_date,"
						+ "sign_date,is_del,ctime,utime) VALUES " + "(" + contract.getId() + ", '"
						+ contract.getContractNo() + "', '" + contract.getEnterpriseId() + "', '"
						+ contract.getEnterpriseName() + "', '" + contract.getContractState() + "', '"
						+ contract.getBuildingId() + "', '" + contract.getBuildingName() + "', '"
						+ contract.getBuildingType() + "', '" + contract.getDeposit() + "', '"
						+ contract.getDepositUnitEnum() + "', " + leaseBeginDate + ", " + leaseEndDate + "," + signDate
						+ ",'" + contract.getIsDel() + "', '" + contract.getCtime() + "','" + contract.getUtime()
						+ "');";
				sqlList.add(sql);
			}
			int lastIndx = sqlForDel.lastIndexOf(",");
			int length = sqlForDel.length();
			if (lastIndx > 0) {
//				sqlForDel.delete(lastIndx, length - 1);
				sqlForDel.deleteCharAt(lastIndx);
			}
			sqlForDel.append(")");
			String delRoomSql = "delete from contract_room where contract_id in " + sqlForDel.toString();
			String delContactSql = "delete from contract_contact where contract_id in " + sqlForDel.toString();
			String delLeaseTermSql = "delete from contract_lease_term where contract_id in " + sqlForDel.toString();

			DbHelper.getDb().batch(sqlList, sqlList.size());//
			DbHelper.getDb().update(delRoomSql);
			DbHelper.getDb().update(delContactSql);
			DbHelper.getDb().update(delLeaseTermSql);
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :contract 批量插入失败！");
			e.printStackTrace();
			throw e;
		}

//		if(count != null && count.length > 0) {
//			isInsertSuccess = true;
//		}
//		return isInsertSuccess;
	}

//	private static List<String> roomInsertSqlS = new ArrayList<String>();
//	private static List<String> roomUpdateSqlS = new ArrayList<String>();
//	List<String> contactInsertSqlS = new ArrayList<String>();
//	List<String> contactUpdateSqlS = new ArrayList<String>();
//	List<String> leaseTermInsertSqlS = new ArrayList<String>();
//	List<String> leaseTermUpdateSqlS = new ArrayList<String>();
//	

	/**
	 * 修改数据
	 * 
	 * @param parks
	 * @return
	 */
	public static void updateDataS(List<ContractModel> contracts) throws Exception {
//		int[] count = null;
//		boolean isUptSuccess = false;
		List<String> sqlList = new ArrayList<String>();
		StringBuilder sqlForDel = new StringBuilder("(");
		try {
			for (Iterator<ContractModel> it = contracts.iterator(); it.hasNext();) {
				ContractModel contract = it.next();

				sqlForDel.append(contract.getId() + ",");

				String leaseBeginDate = null;
				if (contract.getLeaseBeginDate() != null) {
					leaseBeginDate = "'" + contract.getLeaseBeginDate() + "'";
				}
				String leaseEndDate = null;
				if (contract.getLeaseEndDate() != null) {
					leaseEndDate = "'" + contract.getLeaseEndDate() + "'";
				}

				String signDate = null;
				if (contract.getSignDate() != null) {
					signDate = "'" + contract.getSignDate() + "'";
				}


				String sql = "update contract set contract_no = '" + contract.getContractNo() + "' ,enterprise_id='"
						+ contract.getEnterpriseId() + "', enterprise_name='" + contract.getEnterpriseName()
						+ "', contract_state='" + contract.getContractState() + "', building_id='"
						+ contract.getBuildingId() + "', building_name='" + contract.getBuildingName()
						+ "', building_type='" + contract.getBuildingType() + "', deposit='" + contract.getDeposit()
						+ "', depositUnitEnum='" + contract.getDepositUnitEnum() + "', lease_begin_date="
						+ leaseBeginDate + ", lease_end_date=" + leaseEndDate + ",sign_date=" + signDate
						+ ",is_del = '" + contract.getIsDel() + "' ,ctime = '" + contract.getCtime() + "', utime= '"
						+ contract.getUtime() + "' where contract_id = " + contract.getId() + ";";
				sqlList.add(sql);
			}
			int lastIndx = sqlForDel.lastIndexOf(",");
			int length = sqlForDel.length();
			if (lastIndx > 0) {
//				sqlForDel.delete(lastIndx, length - 1);
				sqlForDel.deleteCharAt(lastIndx);
			}
			sqlForDel.append(")");
			String delRoomSql = "delete from contract_room where contract_id in " + sqlForDel.toString();
			String delContactSql = "delete from contract_contact where contract_id in " + sqlForDel.toString();
			String delLeaseTermSql = "delete from contract_lease_term where contract_id in " + sqlForDel.toString();

			DbHelper.getDb().batch(sqlList, sqlList.size());//
			DbHelper.getDb().update(delRoomSql);
			DbHelper.getDb().update(delContactSql);
			DbHelper.getDb().update(delLeaseTermSql);
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " : contract 批量更新失败！");
			e.printStackTrace();
			throw e;
		}
//		if(count != null && count.length > 0) {
//			isUptSuccess = true;
//		}
//		return isUptSuccess;
	}

	public static void insertRoomDataS(List<ContractRoomModel> rooms) throws Exception {
		List<String> sqlList = new ArrayList<String>();

		try {
			for (Iterator<ContractRoomModel> it = rooms.iterator(); it.hasNext();) {
				ContractRoomModel room = it.next();

				String sql = "INSERT INTO contract_room "
						+ "(id,room_id,room_number,floor,price,area_size,contract_id) VALUES " + "(" + room.getId()
						+ ", '" + room.getRoomId() + "', '" + room.getRoomNumber() + "', '" + room.getFloor() + "', '" + room.getPrice() + "', '" + room.getAreaSize() + "', '"
						+ room.getContractId() + "');";
				sqlList.add(sql);
			}
			DbHelper.getDb().batch(sqlList, sqlList.size());//
		} catch (Exception e) {
			logger.error("contract_room 批量添加失败！");
			e.printStackTrace();
			throw e;
		}
	}

	public static void insertContactDataS(List<ContractContactModel> contacts) throws Exception {
		List<String> sqlList = new ArrayList<String>();

		try {
			for (Iterator<ContractContactModel> it = contacts.iterator(); it.hasNext();) {
				ContractContactModel contact = it.next();

				String sql = "INSERT INTO contract_contact "
						+ "(contact_id,address,company_name,email,name,tel,contract_id) VALUES " + "(" + contact.getId()
						+ ", '" + contact.getAddress() + "', '" + contact.getCompanyName() + "', '" + contact.getEmail()
						+ "', '" + contact.getName() + "', '" + contact.getTel() + "', '" + contact.getContractId()
						+ "');";
				sqlList.add(sql);
			}
			DbHelper.getDb().batch(sqlList, sqlList.size());//
		} catch (Exception e) {
			logger.error("contract_contact 批量添加失败！");
			e.printStackTrace();
			throw e;
		}
	}

	public static void insertLeaseTermDataS(List<ContractLeaseTermModel> leaseTerms) throws Exception {
		List<String> sqlList = new ArrayList<String>();

		try {
			for (Iterator<ContractLeaseTermModel> it = leaseTerms.iterator(); it.hasNext();) {
				ContractLeaseTermModel term = it.next();
				String termBeginDate = null;
				if (term.getTermBeginDate() != null) {
					termBeginDate = "'" + term.getTermBeginDate() + "'";
				}
				String termEndDate = null;
				if (term.getTermEndDate() != null) {
					termEndDate = "'" + term.getTermEndDate() + "'";
				}
				
				
				String sql = "INSERT INTO contract_lease_term "
						+ "(lease_id,term_begin_date,term_end_date,contract_pay_enum,day_number_for_year,calculate_enum,month_price_convert_role_enum,"
						+ "lease_divide_role_enum,interval_month,pay_in_advanceDay,payment_date_enum,price,price_unit_enum,monetary_unit,contract_id) VALUES " + 
						"(" + term.getId()+ ", " + termBeginDate + ", " + termEndDate + ", '" + term.getContractPayEnum() + "', '"
						+ term.getDayNumberForYear() + "', '" + term.getCalculateEnum() + "', '" + term.getMonthPriceConvertRoleEnum() + "', '" + term.getLeaseDivideRoleEnum() + "', '" + 
						term.getIntervalMonth() + "', '" + term.getPayInAdvanceDay() + "','" + term.getPaymentDateEnum() + "', '" + term.getPrice() + "', '" + term.getPriceUnitEnum() + "', '" +
						term.getMonetaryUnit() + "', '"+ term.getContractId() + "');";
				sqlList.add(sql);
			}
			DbHelper.getDb().batch(sqlList, sqlList.size());//
		} catch (Exception e) {
			logger.error("contract_lease_term 批量添加失败！");
			e.printStackTrace();
			throw e;
		}
	}
}
