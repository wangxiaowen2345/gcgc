package com.coffcat.index;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 基本控制器 所有需要初始化的导入都从这里开始
 * 
 * @author hero
 * @Email:mahaojie299@163.com
 */
public class BaseController extends Controller {
	
	static Log log = Log.getLog(BaseController.class);

	public Map<String, Object> K;

	public BaseController() {

		
		//log.info("访客IP："+this.getRequest().getRemoteAddr()+" 端口："+this.getRequest().getRemotePort()+" Host:"+this.getRequest().getRemoteHost());
		//Sys.out("访客IP："+this.getRequest().getRemoteAddr()+" 端口："+this.getRequest().getRemotePort()+" Host:"+this.getRequest().getRemoteHost());
		
		
		
		List<Record> list = Db.find("select * from website");
		// CacheKit.get("blog", "Record", new
		// IDataLoader(){
		// public Object load() {
		// return Db.find("select * from website");
		// }
		// });

		K = new HashMap<String, Object>();
		for (Record x : list) {

			System.out.println("key:" + x.getStr("key") + ",value:"
					+ x.getStr("value") + ",remark:" + x.getStr("remark"));

			K.put(x.getStr("key"), x.getStr("value"));
			K.put(x.getStr("key") + "_remark", x.getStr("remark"));
		}

		List<Record> l = Db
				.find("select * from content where type=? and prent=0 order by id asc",
						"moddle");
		List<Map<String, Object>> mlist = new ArrayList<Map<String, Object>>();

		for (Record r : l) {

			List<Record> ll = Db
					.find("select * from content where type=? and prent=? order by id asc",
							"moddle", r.get("id"));

			Map<String, Object> m = new HashMap<String, Object>();
			m.put("own", r);
			m.put("son", ll);
			mlist.add(m);
		}
		
		
		/*
		 * gcgc产品
		 */
		List<Record> productlist = Db.find("select * from content where prent=22 order by id asc");
		K.put("product",productlist);
		
		
		/*
		 * 基本旗舰店
		 */
		List<Record> qjd = Db.find("select * from admin where power='qjd' order by id asc");
		K.put("baseqjd",qjd);
		
		K.put("nav", mlist);
	}

	/**
	 * 获取nav 位置
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void getNavPosition() {
		Map<String, String[]> m = this.getParaMap();
		if (m.size() == 0) {
			K.put("isindex", true);
			K.put("id", 0);

		} else if (null != this.getPara() && null == this.getPara("id")) {
			K.put("isindex", true);
			K.put("id", 0);
		} else {
			K.put("isindex", false);
			try{
				K.put("id", Integer.parseInt(this.getPara("id")));
			}catch (Exception e) {
				K.put("id", 0);
			}
			
		}
		this.setAttr("K", K);
	}

	/**
	 * 获取系统信息
	 * 
	 * @author 豪杰
	 * 
	 */
	public void getSystemInfo() {
		double total = (Runtime.getRuntime().totalMemory()) / (1024.0 * 1024);
		double max = (Runtime.getRuntime().maxMemory()) / (1024.0 * 1024);
		double free = (Runtime.getRuntime().freeMemory()) / (1024.0 * 1024);

		Map<String, Double> m = new HashMap<String, Double>();

		m.put("maxMemory", max);
		m.put("totalMemory", total);
		m.put("freeMemory", free);
		m.put("JVM", (max - total + free));

		File[] roots = File.listRoots();

		double usedisk = 0;
		double totaldisk = 0;
		double freedisk = 0;

		// 获取磁盘分区列表
		for (File file : roots) {
			usedisk += file.getUsableSpace() / 1024 / 1024 / 1024;
			freedisk += file.getFreeSpace() / 1024 / 1024 / 1024;
			totaldisk += file.getTotalSpace() / 1024 / 1024 / 1024;
		}

		m.put("usedisk", usedisk);
		m.put("totaldisk", totaldisk);
		m.put("freedisk", freedisk);

		K.put("SystemInfo", m);
		this.setAttr("K", K);
	}

}
