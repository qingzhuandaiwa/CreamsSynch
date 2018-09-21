package com.yinkun.creams.synch;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.yinkun.creams.app.SynchExecutor;
import com.yinkun.creams.bean.BillCashFlowModel;
import com.yinkun.creams.bean.BillCashMatchesModel;
import com.yinkun.creams.bean.BillModel;
import com.yinkun.creams.bean.BillRoomModel;
import com.yinkun.creams.bean.ContractContactModel;
import com.yinkun.creams.bean.ContractLeaseTermModel;
import com.yinkun.creams.bean.ContractRoomModel;
import com.yinkun.creams.bean.FloorModel;
import com.yinkun.creams.bean.RoomModel;
import com.yinkun.creams.service.BillService;
import com.yinkun.creams.service.ContractService;
import com.yinkun.creams.service.FloorService;
import com.yinkun.creams.service.ParkService;
import com.yinkun.creams.utils.DbHelper;
import com.yinkun.workgo.test.kit.HttpHelper;

public class BillsSynch implements Runnable {

	private Date lastUptDate = null;

	private static String timeFormat = "yyyy-MM-dd HH:mm:ss";

	Logger logger = Logger.getLogger(ParkSynch.class);

	public List<BillModel> insertDatas = new ArrayList<BillModel>();
	public List<BillModel> updateDatas = new ArrayList<BillModel>();

	public List<BillCashMatchesModel> insertCashMatchDatas = new ArrayList<BillCashMatchesModel>();
	public List<BillRoomModel> insertRoomDatas = new ArrayList<BillRoomModel>();
	public List<BillCashFlowModel> insertCashFlowDatas = new ArrayList<BillCashFlowModel>();

	private String token;

	public BillsSynch(String token) {
		this.token = token;
	}

	/**
	 * 从webapi中获取数据
	 * 
	 * @param token
	 * @param lastUptDate 最近一次更新时间
	 */
	@SuppressWarnings("deprecation")
	public String fetchFromWebApi(String token, Date lastUptDate) {
		Date lastDate = lastUptDate;
		// 日期转字符串
		if (lastDate == null) {
			Calendar now = Calendar.getInstance();
			now.add(Calendar.MONTH, -1);// 月份减1
			lastDate = now.getTime();
		}

		this.lastUptDate = lastDate;

		Map<String, Object> params = new HashMap<String, Object>();
		int[] buildingIds = new int[0];
		params.put("queryDateFrom", new SimpleDateFormat(timeFormat).format(lastDate));
//        String paras = JSONObject.toJSON(params).toString();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json; charset=utf-8");
		headers.put("Authorization", token);
		String result = null;
		try {
			result = HttpHelper.get(PropKit.use(SynchExecutor.getFile()).get("billUrl"), params, headers);
			logger.info(result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 处理返回的数据
	 * @param result
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Boolean ProcessDataS(String result) {
		JSONObject jsonResult = null;
		
		try {
			jsonResult = JSONObject.parseObject(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :bills 解析数据内容失败！");
			return false;
		}
		
		if(jsonResult.getIntValue("code") != 200) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + 
					" :bill 网络接口调用出现问题,code: "+ jsonResult.getIntValue("code") +", message=" + jsonResult.getString("message") +" ！");
			return false;
		}
		
		JSONArray arr=jsonResult.getJSONArray("data");//获取的结果集合转换成数组
		String js=JSONObject.toJSONString(arr);//将array数组转换成字符串
		List<BillModel>  bills = JSONObject.parseArray(js, BillModel.class);//把字符串转换成集合
		if( bills == null || bills.size() <= 0) {
			logger.warn(new SimpleDateFormat(timeFormat).format(new Date()) + " :floor 数据为空！");
			return false;
		}
		
		StringBuilder sql = new StringBuilder("select * from ( ");
		
		
		try {
			for(Iterator<BillModel> it = bills.iterator();it.hasNext();) {
				BillModel billModel = it.next();
				sql.append("select '" + billModel.getId() + "' id UNION ");
			}
			
			int lastUnIndx = sql.lastIndexOf("UNION");
			int length = sql.length();
			if(sql.lastIndexOf("UNION") > 0) {
				sql.delete(lastUnIndx, length-1);
			}
			
			sql.append("from dual) temp where EXISTS (SELECT 1 from bill where temp.id = bill_id)");
			
			List<String> rcdS = DbHelper.getDb().query(sql.toString());
			for(Iterator<BillModel> it = bills.iterator();it.hasNext();) {
				BillModel billModel = it.next();
				if(rcdS.contains(billModel.getId())) {
					this.updateDatas.add(billModel);
				}else {
					this.insertDatas.add(billModel);
				}
				
				List<BillCashMatchesModel> billCashMatches = billModel.getBillCashMatches();
				List<BillRoomModel> billRooms = billModel.getRooms();
				
				if (billCashMatches != null && billCashMatches.size() > 0) {
					insertCashMatchDatas.addAll(billCashMatches);
					for(Iterator<BillCashMatchesModel> billCashMatchIt = billCashMatches.iterator(); billCashMatchIt.hasNext();) {
						BillCashMatchesModel billCashMatch = billCashMatchIt.next();
						Integer id = billCashMatch.getBillId();
						BillCashFlowModel cashFlowModel = billCashMatch.getCashFlow();
						cashFlowModel.setBillCashMatchId(id);
						insertCashFlowDatas.add(cashFlowModel);
					}
				}
				if (billRooms != null && billRooms.size() > 0) {
					insertRoomDatas.addAll(billRooms);
				}
			}
			
//			for(Iterator<BillCashMatchesModel> billCashMatchIt = insertCashMatchDatas.iterator(); billCashMatchIt.hasNext();) {
//				BillCashMatchesModel billCashMatch = billCashMatchIt.next();
//				
//				BillCashFlowModel CashFlowS = billCashMatch.getCashFlow();
//				for(CashFlowS) {
//					
//				}
//			}
			
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + "遍历bill失败");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 同步数据
	 */
	public void SynchDatas() {
		
		Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				try {
					if (insertDatas != null && insertDatas.size() > 0) {
						BillService.insertDataS(insertDatas);
					}
					if (updateDatas != null && updateDatas.size() > 0) {
						BillService.updateDataS(updateDatas);
					}
					if(insertCashMatchDatas != null && insertCashMatchDatas.size() > 0) {
						BillService.insertBillCashMatches(insertCashMatchDatas);
					}
					if(insertRoomDatas != null && insertRoomDatas.size() > 0) {
						BillService.insertBillRooms(insertRoomDatas);
					}
					if(insertCashFlowDatas != null && insertCashFlowDatas.size() > 0) {
						BillService.insertCashFlows(insertCashFlowDatas);
					}
					logger.error("同步bill成功");
					return true;
				} catch (Exception e) {
					logger.error("同步bill失败");
					return false;
				}

			}

		});
	}

	public void run() {
		System.out.println("BillsSynch thread is running ...");
		Date lastDate = BillService.getLastUpdateDate();

		Calendar calendar = Calendar.getInstance();
		if (lastDate != null) {
			calendar.setTime(lastDate);
			calendar.add(Calendar.MINUTE, -1);//
		}

		String result = fetchFromWebApi(token, lastDate);
		if (StrKit.isBlank(result)) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " : bill 网络接口调用异常！");
			return;
		}
		Boolean isSuccess = ProcessDataS(result);

		if (isSuccess) {
			SynchDatas();
		}

	}

}
