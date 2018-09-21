package com.yinkun.creams.synch;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.yinkun.creams.app.SynchExecutor;
import com.yinkun.creams.utils.Db;
import com.yinkun.workgo.test.kit.HttpUtils;

public class ParksSynch implements Runnable{
	
	
	private String token;
	
	public ParksSynch(String token) {
		this.token = token;
	}
	
	

	public void run() {
		System.out.println("ParksSynch thread is running ...");
		File file = SynchExecutor.getFile();
		String url = PropKit.use(file).get("workgo.url");
		String username = PropKit.use(file).get("workgo.username");
		String password = PropKit.use(file).get("workgo.password");
		String parkUrl = PropKit.use(file).get("parkUrl");
		
		
		Connection conn = null;
		try {
			conn = getConn(url, username, password);
		
			List list = Db.query(conn, "select parkId, parkName from t_park where collectId is null");
			for(int i=0;i<list.size(); i++){
				Object[] temp =  (Object[]) list.get(i);
				String parkName = (String)temp[1];
				//plist.add(new Park((int)temp[0], (String)temp[1], 0));
				String collectId = createPark(parkUrl, parkName,  token);
				if(!collectId.equals("")){
					Db.update(conn, "update t_park set collectId = "+ collectId + ", uTime = now() where parkId = " + temp[0]);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			System.out.println("<==== 执行...根据WORKG园区新建集合数据--END");
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}  
	
	public static Connection getConn(String url, String username, String password) throws SQLException{
        
        String driver = "com.mysql.jdbc.Driver";
        Connection conn = null;
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            conn = (Connection) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
	}
	
	public static String createPark(String parkUrl, String parkName, String token){
		Map<String, Object> params = new HashMap<String, Object>();
		int[] buildingIds = new int[0];
        params.put("buildingIds", buildingIds);
        params.put("name", parkName);
        String paras = JSONObject.toJSON(params).toString();
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("Content-Type", "application/json; charset=utf-8");
		headers.put("Authorization", token);
		String result = "";
		try {
			result = HttpUtils.post(parkUrl,paras,headers);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("-------新建园区--失败---"+parkName+"-");
		}finally{
			return result;
		}
	}
	

}
