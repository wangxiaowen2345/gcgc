package com.coffcat.index;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.coffcat.common.config.Conts;
import com.coffcat.common.model.Datatransmission;
import com.coffcat.common.model.Lt;
import com.coffcat.common.model.User;
import com.coffcat.util.Sys;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 前台控制器
 * 
 * @author hero
 * @Email:mahaojie299@163.com
 */
public class IndexController extends BaseController {
	public void index() {
		getNavPosition();
		render("index.html");
	}
	        

	public void art() {
		getNavPosition();
		this.setAttr(
				"r",
				Db.findFirst("select * from content where id=?",
						this.getPara("id")));
		this.render("art.html");
	}

	public void moddle() {
		getNavPosition();
		this.setAttr(
				"r",
				Db.findFirst("select * from content where id=?",
						this.getPara("id")));
		this.setAttr("contentPage", Db.paginate(getParaToInt(0, 1),
				Conts.PageSize, "select * ",
				"from content where prent=? order by id asc",
				this.getPara("id")));
		this.render("moddle.html");
	}
	

//	public void dataTransmission() {
//		Map<String, String> map = new HashMap<String, String>();
//		try{
//			String jsonStr = getPara("request");
//			
//			//获取data数据
//			JSONObject jsondataObject = new JSONObject(jsonStr);
//			//JSONObject  dataObject = jsondataObject.getJSONObject("data");
//
//
//	        Map<String, String> m = new HashMap<String, String>();
//	        if(jsondataObject.has("tel")){
//	        	Record ruser =  Db.findFirst("select * from user where username=?", jsondataObject.getString("tel").toString());
//	        	if(ruser==null){
//	        		map.put("code", "404");
//		        	map.put("message", "无此用户");
//		        	this.renderJson(map);
//		        	return;
//	        	}else{
//	        		//两个都key存在
//	    	        if(jsondataObject.has("data")&&jsondataObject.has("img")){
//	    	        	Record r = new Record();
//	    		        JSONArray data = jsondataObject.getJSONArray("data");
//	    		        JSONArray img = jsondataObject.getJSONArray("img");
//	    		       
//	    		        //data数据处理
//	    				for (int i = 0; i < data.length(); i++) {
//	    					if("".equals(data.get(i+1))){
//	    						map.put("code", "500");
//	    			        	map.put("message", "格式错误1");
//	    					}
//	    					else if(data.length()%2!=0){
//	    						map.put("code", "500");
//	    			        	map.put("message", "格式错误2");
//	    					}
//	    					else{
//	    						m.put(data.get(i).toString(),data.get(i+1).toString());
//	    					}
//	    					i++;
//	    				}
//	    				JSONObject js = new JSONObject(m);
//	    				String s = js.toString();
//	    				r.set("data", s);
//	    		        
//	    				//img数据处理
//	    		        int j = 1;
//	    		        //做判断，img条数为零时img条数大于5时错误
//	    		        if("".equals(img.get(0))){
//	    		        	map.put("code", "500");
//	    		        	map.put("message", "格式错误3");
//	    		        }
//	    		        else if(img.length()>5){
//	    		        	map.put("code", "500");
//	    		        	map.put("message", "格式错误4");
//	    		        }
//	    		        else if(img.length()<6){
//	    		        	for (int i = 0; i < img.length(); i++) {
//	    		        		/**
//	    		        		 * 检查图片是否存在
//	    		        		 */
//	    		        		File file = new File(img.get(i).toString());
//	    		        		if(!file.exists()){
//	    		        			map.put("code", "400");
//	    	    		        	map.put("message", "图片不存在");
//	    	    		        	this.renderJson(map);
//	    	    		        	return;
//	    		        		}
//	    		        		else
//	    		        			r.set("img"+j, img.get(i));
//	    						j++;
//	    					}
//	    		        }
//	    		        r.set("telphone", jsondataObject.getString("tel"));
//	    				r.set("time", new Date());
//	    				Db.save("datatransmission", r);
//	    	        }//data存在
//	    	        else if(jsondataObject.has("data")){
//	    	        	Record r = new Record();
//	    		        JSONArray data = jsondataObject.getJSONArray("data");
//	    		        
//	    		        //data数据处理
//	    				for (int i = 0; i < data.length(); i++) {
//	    					if("".equals(data.get(i+1))){
//	    						map.put("code", "500");
//	    			        	map.put("message", "格式错误1");
//	    					}
//	    					else if(data.length()%2!=0){
//	    						map.put("code", "500");
//	    			        	map.put("message", "格式错误2");
//	    					}
//	    					else{
//	    						m.put(data.get(i).toString(),data.get(i+1).toString());
//	    					}
//	    					i++;
//	    				}
//	    				JSONObject js = new JSONObject(m);
//	    				String s = js.toString();
//	    				r.set("data", s);
//	    				r.set("telphone", jsondataObject.getString("tel"));
//	    				r.set("time", new Date());
//	    				Db.save("datatransmission", r);
//	    	        }//img存在
//	    	        else if(jsondataObject.has("img")){
//	    	        	Record r = new Record();
//	    		        JSONArray img = jsondataObject.getJSONArray("img");
//	    		        
//	    		      //img数据处理
//	    		        int j = 1;
//	    		        //做判断，img条数为零时img条数大于5时错误
//	    		        if("".equals(img.get(0))){
//	    		        	map.put("code", "500");
//	    		        	map.put("message", "格式错误3");
//	    		        }
//	    		        else if(img.length()>5){
//	    		        	map.put("code", "500");
//	    		        	map.put("message", "格式错误4");
//	    		        }
//	    		        else if(img.length()<6){
//	    		        	for (int i = 0; i < img.length(); i++) {
//	    		        		/**
//	    		        		 * 检查图片是否存在
//	    		        		 */
////	    		        		File file = new File(img.get(i).toString());
////	    		        		if(!file.exists()){
////	    		        			map.put("code", "400");
////	    	    		        	map.put("message", "图片不存在");
////	    	    		        	this.renderJson(map);
////	    	    		        	return;
////	    		        		}
////	    		        		else
//	    						r.set("img"+j, img.get(i));
//	    						j++;
//	    					}
//	    		        }
//	    		        r.set("telphone", jsondataObject.getString("tel"));
//	    				r.set("time", new Date());
//	    				Db.save("datatransmission", r);
//	    	        }else{
//	    				map.put("code", "500");
//	    	        	map.put("message", "格式错误");
//	    			}
//	    			
//	    		
//	    			//返回数据
//	    			Record rdata =  Db.findFirst("select * from datatransmission where telphone=? and data is not null order by time desc", jsondataObject.getString("tel").toString());
//	    			if(rdata!=null)
//	    				map.put("rdata", rdata.toJson());
//	    			else
//	    				map.put("rdata", "null");
//	    			map.put("code", "200");
//	    			map.put("message", "保存成功！");
//	        	}
//	        }
//	    }
//	    catch(Exception ex){
//	        //异常处理
//	    	Sys.out(ex);
//	    	map.put("code", "500");
//	    	map.put("message", "程序错误");
//			this.renderJson(map);
//	    }
//		this.renderJson(map);
//	}
	
//	public void showdataTransmission() {
//		Record rdata =  Db.findFirst("select * from datatransmission where id=?", this.getPara("id"));
//		this.setAttr("rdata", rdata);
//		
//		JSONObject js = new JSONObject(rdata.getStr("data"));
//
//		String sa[] = JSONObject.getNames(js);
//		
//		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
//		
//		for(String s : sa){
//			Map<String,Object> m1 = new HashMap<String,Object>();
//			if(js.has(s)){
//				m1.put("key", s);
//				m1.put("value", js.get(s));
//				list.add(m1);
//			}
//		}
//		this.setAttr("ltdata", list);
//		//this.render("showdata.html");
//		this.render("/ykly/showDataTransmission.html");
//	}
	
//	public void test() {
//		Record rdata =  Db.findFirst("select * from datatransmission where telphone=13012461269 ");
//		Sys.out(rdata);
//		JSONObject js = new JSONObject(rdata.getStr("data"));
//		JSONArray js1 = new JSONArray(rdata.getStr("img"));
//		//String r = rdata.get("img").toString();
//		
//		List<Map<String,Object>> list1 = new ArrayList<Map<String,Object>>();
//		for(int i =0;i<js1.length();i++){
//			Map<String,Object> m1 = new HashMap<String,Object>();
//			m1.put("value", "\\images\\"+js1.get(i));
//			list1.add(m1);
//		}
//		this.setAttr("imgdata", list1);
//		
//
//		String sa[] = JSONObject.getNames(js);
//		
//		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
//		
//		for(String s : sa){
//			Map<String,Object> m1 = new HashMap<String,Object>();
//			m1.put("key", s);
//			m1.put("value", js.get(s));
//			list.add(m1);
//		}
//		this.setAttr("ltdata", list);
//		//this.render("showdata.html");
//		this.render("/ykly/showDataTransmission.html");
//	}

	
	public void transmission() {
		Map<String, Object> m = new HashMap<String, Object>();
		if (null == this.getPara("user") || null == this.getPara("password")
				|| !"gcgc".equals(this.getPara("user"))
				|| !"gcgcykly1234".equals(this.getPara("password"))) {
			m.put("code", 501);
			m.put("msg", "验证错误");
			this.renderJson(m);
			return;
		}

		if (!val("telphone") || !val("data") || !val("img")) {
			m.put("code", 500);
			m.put("msg", "数据不完整");
			this.renderJson(m);
			return;
		}

		User user = User.me.findFirst("select * from user where username=?",
				this.getPara("telphone"));
		if (user == null) {
			m.put("code", 404);
			m.put("msg", "手机号非注册用户");
			this.renderJson(m);
			return;
		}

		String a = this.getPara("data");
		String[] arr = a.split(",");
		
		Map<String,String> map = new HashMap<String, String>();
		for (String s : arr) {
			String[] temp = s.split(":");
			map.put(temp[0], temp[1]);
		}

		List<String> l = new ArrayList<String>();
		String b = this.getPara("img");
		arr = b.split(",");
		
		for (String s : arr) {
			
			File f = new File("D:\\phpStudy\\WWW\\pic\\" + s.replace("/", "\\"));
			if (!f.exists()) {
				m.put("code", 400);
				m.put("msg", "图片文件不存在");
				this.renderJson(m);
				return;
			}
			l.add(s);
		}

		Datatransmission d = new Datatransmission();
		d.setImg(new JSONArray(l).toString());
		d.setData(new JSONObject(map).toString());
		d.setTelphone(this.getPara("telphone"));
		d.save();
		
		
		Lt lt = new Lt();
		
		lt.setUser(user.getInt("id"));
		lt.setData(new JSONObject(map).toString());
		lt.setPic1("http://www.gcgc.cn:88/"+l.get(0));
		lt.save();

		m.put("code", 200);
		m.put("msg", "成功");
		this.renderJson(m);
	}
	
	private boolean val(String value){
		if(this.getPara(value)==null || this.getPara(value).equals("")){
			return false;
		}
		return true;
	}
}
