package com.yinkun.creams.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.yinkun.creams.bean.RoomModel;
import com.yinkun.creams.bean.UserModel;
import com.yinkun.creams.model.Park;
import com.yinkun.creams.synch.TenantSynch;
import com.yinkun.creams.utils.DbHelper;

public class UserService {
	
	
	private static String timeFormat = "yyyy-MM-dd HH:mm:ss";

	static Logger logger = Logger.getLogger(UserService.class);
	
	public static Date getLastUpdateDate() {
		String sql = "SELECT * from user ORDER BY utime DESC LIMIT 1";
		Record rcd = DbHelper.getDb().findFirst(sql);
		if(rcd!=null) {
			return rcd.getDate("utime");
		}else {
			return null;
		}
	}

	/**
	 * 插入数据
	 * @param parks
	 * @return
	 */
	public static boolean insertDataS(List<UserModel> users) {
		int[] count = null;
		boolean isInsertSuccess = false;
		List<String> sqlList = new ArrayList<String>();

		try {
			for(Iterator<UserModel> it = users.iterator(); it.hasNext();) {
				UserModel user = it.next();
				String dateCreate = null;
				if(user.getDateCreate() != null) {
					dateCreate = "'" + user.getDateCreate()+ "'";
				}
//				System.out.println(user.getName());
				String sql = "INSERT INTO user "
						+ "(user_id,active,is_admin,name,tel,email,date_create,is_del,ctime,utime) VALUES "
						+ "(" +
						user.getId() + ", '"+ user.getActive() +"', '" + 
						user.getAdmin() + "', '" + user.getName() + "', '" + 
						user.getTel() + "', '" + user.getEmail() +  "', " + dateCreate + ", '" + user.getIsDel() + "','" + user.getCtime() + "','"+ user.getUtime() +"');";
				sqlList.add(sql);
			}
			count = DbHelper.getDb().batch(sqlList, sqlList.size());
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :user 批量插入失败！");
			e.printStackTrace();
		}
		
		if(count != null && count.length > 0) {
			isInsertSuccess = true;
		}
		return isInsertSuccess;
	}
	
	/**
	 * 修改数据
	 * @param parks
	 * @return
	 */
	public static boolean updateDataS(List<UserModel> users) {
		int[] count = null;
		boolean isUptSuccess = false;
		List<String> sqlList = new ArrayList<String>();
		try {
			for(Iterator<UserModel> it = users.iterator(); it.hasNext();) {
				UserModel user = it.next();
				String dateUpdate = null;
				if(user.getDateCreate() != null) {
					dateUpdate = "'" + user.getDateCreate()+ "'";
				}
				
				String sql = "update user set active = '" + user.getActive() + "', is_admin='" + user.getAdmin() + "', name='" + 
				user.getName() + "' , tel='" + user.getTel() + "', email='" + user.getEmail() + "', date_create=" + 
				dateUpdate +  ",is_del = '"+ user.getIsDel()+"' ,ctime = '"+ user.getCtime() +"', utime= '"+ 
				user.getUtime() +"' where user_id = " + user.getId() + ";";
				sqlList.add(sql);
			}
			
			count = DbHelper.getDb().batch(sqlList, sqlList.size());
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " : user 批量更新失败！");
			e.printStackTrace();
		}
		if(count != null && count.length > 0) {
			isUptSuccess = true;
		}
		return isUptSuccess;
	}
}
