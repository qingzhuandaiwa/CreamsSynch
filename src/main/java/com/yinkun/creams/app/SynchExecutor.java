package com.yinkun.creams.app;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.yinkun.creams.config.Config;
import com.yinkun.creams.config.ImConfig;
import com.yinkun.creams.service.BillService;
import com.yinkun.creams.synch.BillsSynch;
import com.yinkun.creams.synch.BuildingSynch;
import com.yinkun.creams.synch.ContractSynch;
import com.yinkun.creams.synch.FloorSynch;
import com.yinkun.creams.synch.ParkSynch;
import com.yinkun.creams.synch.ParksSynch;
import com.yinkun.creams.synch.RoomSynch;
import com.yinkun.creams.synch.TenantSynch;
import com.yinkun.creams.synch.UserSynch;
import com.yinkun.creams.utils.AccessToken;
import com.yinkun.creams.utils.DbHelper;

public class SynchExecutor {
	
//	public static Log logger = Log.getLog(SynchExecutor.class);

	private static String token;
	
	private static File configFile = null;
	
	
	public static File getFile() {
		return configFile;
	}

	public static void main(String[] args){
		String filePath = "D:\\config.properties";
//		executeSysch(filePath);
		
		
//        String path = Thread.currentThread().getContextClassLoader ().getResource("").getPath();
//        //System.out.println("path = " + path);
//        String filename = path + "/config.properties";
//        if (args.length > 0) {
//            filename = args[0];
//        }
        executeSysch(filePath);
        
        
        
	}

	private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);

	
	/**
	 * 寮�濮嬪悓姝�
	 */
	public static void executeSysch(String filePath) {
		File file = new File(filePath);
		configFile = file;
		Config.prop = PropKit.use(file);
		
		DbHelper.getDb();
		
		getToken();
//		BillsSynch bs = new BillsSynch(token);
		BuildingSynch buildingSynch = new BuildingSynch(token);
		FloorSynch floorSynch = new FloorSynch(token);
		RoomSynch roomSynch = new RoomSynch(token);
		TenantSynch tenantSynch = new TenantSynch(token);
		ParksSynch parksSynch = new ParksSynch(token);
		ContractSynch contractSynch = new ContractSynch(token);
		BillsSynch billSynch = new BillsSynch(token);
		UserSynch userSynch = new UserSynch(token);
//		
		fixedThreadPool.execute(parksSynch);
		fixedThreadPool.execute(buildingSynch);
		fixedThreadPool.execute(floorSynch);
		fixedThreadPool.execute(roomSynch);
		fixedThreadPool.execute(tenantSynch);
		fixedThreadPool.execute(contractSynch);
		fixedThreadPool.execute(billSynch);
//		fixedThreadPool.execute(bs);
		fixedThreadPool.execute(userSynch);
		
	}

	public static void getToken() { 
		token = AccessToken.getToken();
		System.out.println("token is : " + token);
	}
}
