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
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.yinkun.creams.app.SynchExecutor;
import com.yinkun.creams.bean.ContractContactModel;
import com.yinkun.creams.bean.ContractLeaseTermModel;
import com.yinkun.creams.bean.ContractModel;
import com.yinkun.creams.bean.ContractRoomModel;
import com.yinkun.creams.bean.TenantModel;
import com.yinkun.creams.service.ContractService;
import com.yinkun.creams.service.TenantService;
import com.yinkun.creams.utils.DbHelper;
import com.yinkun.workgo.test.kit.HttpHelper;

public class ContractSynch implements Runnable {

	private Date lastUptDate = null;

	private static String timeFormat = "yyyy-MM-dd HH:mm:ss";

	Logger logger = Logger.getLogger(ContractSynch.class);

	public List<ContractModel> insertDatas = new ArrayList<ContractModel>();
	public List<ContractModel> updateDatas = new ArrayList<ContractModel>();

	public List<ContractRoomModel> insertRoomDatas = new ArrayList<ContractRoomModel>();
//    public List<ContractRoomModel> updateRoomDatas = new ArrayList<ContractRoomModel>();
//    
	public List<ContractContactModel> insertContactDatas = new ArrayList<ContractContactModel>();
//    public List<ContractContactModel> updateContactDatas = new ArrayList<ContractContactModel>();
//    
	public List<ContractLeaseTermModel> insertLeaseTermDatas = new ArrayList<ContractLeaseTermModel>();
//    public List<ContractLeaseTermModel> updateLeaseTermDatas = new ArrayList<ContractLeaseTermModel>();

	private String token;

	public ContractSynch(String token) {
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
			now.add(Calendar.HOUR, -200);// 
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
//			result = HttpHelper.post(PropKit.use("config.properties").get("parkUrl"),paras,headers);
			long startTime=System.currentTimeMillis();
			result = HttpHelper.get(PropKit.use(SynchExecutor.getFile()).get("contractUrl"), params, headers);
//			System.out.println(result);
			logger.info(result);
			long endTime=System.currentTimeMillis(); 

			System.out.println("程序运行时间： "+(endTime-startTime)+"ms");

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 处理返回的数据
	 * 
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
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " : 解析数据内容失败！");
			return false;
		}

		if (jsonResult.getIntValue("code") != 200) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " : 网络接口调用出现问题,code: "
					+ jsonResult.getIntValue("code") + ", message=" + jsonResult.getString("message") + " ！");
			return false;
		}

		JSONArray arr = jsonResult.getJSONArray("data");// 获取的结果集合转换成数组
		String js = JSONObject.toJSONString(arr);// 将array数组转换成字符串
		List<ContractModel> contracts = JSONObject.parseArray(js, ContractModel.class);// 把字符串转换成集合

		if (contracts == null || contracts.size() <= 0) {
			logger.warn(new SimpleDateFormat(timeFormat).format(new Date()) + " :contract 数据为空！");
			return false;
		}

		StringBuilder sql = new StringBuilder("select * from ( ");

		try {
			for (Iterator<ContractModel> it = contracts.iterator(); it.hasNext();) {
				ContractModel contract = it.next();
				sql.append("select '" + contract.getId() + "' id UNION ");
			}

			int lastUnIndx = sql.lastIndexOf("UNION");
			int length = sql.length();
			if (sql.lastIndexOf("UNION") > 0) {
				sql.delete(lastUnIndx, length - 1);
			}

			sql.append("from dual) temp where EXISTS (SELECT 1 from contract where temp.id = contract_id)");
			List<String> rcdS = DbHelper.getDb().query(sql.toString());

			for (Iterator<ContractModel> it = contracts.iterator(); it.hasNext();) {
				ContractModel contractModel = it.next();
				if (rcdS.contains(contractModel.getId())) {
					this.updateDatas.add(contractModel);
				} else {
					this.insertDatas.add(contractModel);
				}
				List<ContractRoomModel> rooms = contractModel.getRooms();
				List<ContractContactModel> contacts = contractModel.getContacts();
				List<ContractLeaseTermModel> terms = contractModel.getLeaseTerms();
				StringBuilder sqlRoom = new StringBuilder("select * from ( ");
				if (rooms != null && rooms.size() > 0) {
					insertRoomDatas.addAll(rooms);
				}
				if (contacts != null && contacts.size() > 0) {
					insertContactDatas.addAll(contacts);
				}

				if (terms != null && terms.size() > 0) {
					insertLeaseTermDatas.addAll(terms);
				}

			}
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + "遍历contract 失败");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 同步数据
	 */
	public void SynchDatas() {
//		final List<ContractModel> inserts = insertDatas;
//		final List<ContractModel> updates = updateDatas;

		Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				try {
					if (insertDatas != null && insertDatas.size() > 0) {
						ContractService.insertDataS(insertDatas);
					}
					if (updateDatas != null && updateDatas.size() > 0) {
						ContractService.updateDataS(updateDatas);
					}
					if(insertRoomDatas != null && insertRoomDatas.size() > 0) {
						ContractService.insertRoomDataS(insertRoomDatas);
					}
					if(insertContactDatas != null && insertContactDatas.size() > 0) {
						ContractService.insertContactDataS(insertContactDatas);
					}
					if(insertLeaseTermDatas != null && insertLeaseTermDatas.size() > 0) {
						ContractService.insertLeaseTermDataS(insertLeaseTermDatas);
					}
					
					logger.error("同步contract成功");
					return true;
				} catch (Exception e) {
					logger.error("同步contract失败");
					return false;
				}

			}

		});

	}

	public void run() {
		System.out.println("ContractSynch thread is running ...");
		Date lastDate = ContractService.getLastUpdateDate();
		if (lastDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(lastDate);
			calendar.add(Calendar.MINUTE, -1);//
			lastDate = calendar.getTime();
		}

		String result = fetchFromWebApi(token, lastDate);
		if (StrKit.isBlank(result)) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :contract 网络接口调用异常！");
			return;
		}

		Boolean isSuccess = ProcessDataS(result);

		if (isSuccess) {
			SynchDatas();
		}

	}

}
