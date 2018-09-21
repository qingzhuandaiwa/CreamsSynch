package com.yinkun.creams.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.yinkun.creams.bean.ParkModel;
import com.yinkun.creams.model.Park;
import com.yinkun.creams.utils.DbHelper;

public class ParkService {
//	public static boolean savePark() {
//		String sql = "select * from park";
//		List<Object> paramsList = new ArrayList<Object>();
//		List<Park> parks = Park.dao.find(sql);
//		
//		for (Iterator<Park> iter= parks.iterator(); iter.hasNext();) {
//			Park rd = iter.next();
//			System.out.println(rd.getParkName());
//		}
//		return false;
//		
//	}
	
	public static Date getLastUpdateDate() {
//		Park lastData = Park.dao.findFirst("SELECT * from park ORDER BY utime DESC LIMIT 1");
//		Date date = lastData.getUtime();
//		return date;
		String sql = "SELECT * from park ORDER BY utime DESC LIMIT 1";
		Record rcd = DbHelper.getDb().findFirst(sql);
		if(rcd!=null) {
			return rcd.getDate("utime");
		}else {
			return null;
		}
	}
	
	
	public static void save(ParkModel park) throws Exception{
		System.out.println(park);
		String sql = "insert into park (park_id,park_name,is_del,ctime,utime) VALUES(123,'name1',0,NOW(),NOW())";
		DbHelper.getDb().update(sql);
	}
	
	public static void update(ParkModel park) throws Exception{
		System.out.println(park);
		String sql = "insert into park (park_id,park_name,is_del,ctime,utime) VALUES(1234,'name2',0,'11',NOW())";
		DbHelper.getDb().update(sql);
		
	}
	
	
	
	
}
