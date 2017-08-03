package com.coffcat.index;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.coffcat.common.config.Conts;
import com.coffcat.common.model.Admin;
import com.coffcat.common.model.Makeorder;
import com.coffcat.common.model.Orders;
import com.coffcat.common.model.User;
import com.coffcat.interceptor.MInterceptor;
import com.coffcat.util.HttpSenderTest;
import com.coffcat.util.Rondom;
import com.coffcat.util.Sys;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * @author hero
 * @email:mahaojie299@163.com
 * @version 创建时间：2016-12-30 上午10:59:05 程序的简单说明
 */

public class MController extends BaseController {
	static Log log = Log.getLog(MController.class);

	/**
	 * 关于我们
	 */
	public void aboutus() {
		this.render("aboutus.html");
	}

	/**
	 * 登陆
	 */
	public void login() {
		this.render("login.html");
	}

	/**
	 * 登陆操作
	 */
	public void dologin() {
		String username = this.getPara("username");
		User user = User.me.findFirst("select * from user where username=?",
				username);
		Map<String, Object> m = new HashMap<String, Object>();

		if (this.getSession() == null
				|| this.getSessionAttr("valicode") == null
				|| !this.getPara("valicode").equals(
						this.getSessionAttr("valicode").toString())) {
			m.put("code", 501);
			m.put("msg", "验证码错误！");
			this.renderJson(m);
			return;
		}

		this.getSession().removeAttribute("user");

		if (user == null) {
			// user not find,create new user
			user = new User();
			user.set("username", username);
			user.save();
			user = User.me.findFirst("select * from user where username=?",
					username);
		}

		this.setSessionAttr("user", user);

		m.put("code", 200);
		m.put("url", "");

		this.renderJson(m);
	}

	/**
	 * 获取验证码
	 */
	public void getvalicode() {
		String username = this.getPara("username");

		String code = "" + Rondom.getRandNum(100000, 999999);

		this.setSessionAttr("valicode_" + username, code);

		HttpSenderTest.send(username, code);

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("code", 200);

		this.renderJson(m);
	}

	/**
	 * 退出登陆
	 */
	public void outLogin() {
		this.getSession().removeAttribute("user");

		this.redirect("/m/login");
	}

	/**
	 * 产品
	 */
	public void product() {
		this.render("product.html");
	}

	@Before(MInterceptor.class)
	public void shopexamine() {
		
		Record r = this.getSessionAttr("user");

		this.setAttr("shop", Admin.dao
				.find("select * from admin where power='sp'"));
		this.setAttr("user", r);
		this.render("shopexamine.html");
	}

	@Before(MInterceptor.class)
	public void doshopexamine() {
		Orders order = this.getModel(Orders.class, "dao");
		order.setUser(((Record) this.getSessionAttr("user")).getInt("id"));

		if (((Record) this.getSessionAttr("user")).get("name").equals("")) {
			Db.update("update user set name=? where id=?", order.getName(),
					((Record) this.getSessionAttr("user")).getInt("id"));
			this.setSessionAttr("user", Db.findById("user", ((Record) this
					.getSessionAttr("user")).getInt("id")));
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");// 小写的mm表示的是分钟
		String dstr = this.getPara("dao.emtime");
		try {
			Date date = sdf.parse(dstr);
			order.setEmtime(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		order.set("type", "到店量体");
		order.save();

		HttpSenderTest.sendmsg(((Record) this.getSessionAttr("user"))
				.getStr("username"), Conts.ExamineBeforeMsg);
		this.redirect("/m/personal");
	}

	@Before(MInterceptor.class)
	public void homeexamine() {
		Record r = this.getSessionAttr("user");
		this.setAttr("user", r);
		this.render("homeexamine.html");
	}

	@Before(MInterceptor.class)
	public void dataTransmission() {
		Record rdata = Db
				.findFirst(
						"select * from datatransmission where telphone=? and data is not null order by time desc",
						this.getPara("telphone"));

		JSONObject js = new JSONObject(rdata.getStr("data"));
		JSONArray js1 = new JSONArray(rdata.getStr("img"));
		//String r = rdata.get("img").toString();

		List<Map<String,Object>> list1 = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < js1.length(); i++) {
			Map<String, Object> m1 = new HashMap<String, Object>();
			m1.put("value", "http://www.gcgc.cn:88/" + js1.get(i));
			list1.add(m1);
		}
		this.setAttr("imgdata", list1);

		String sa[] = JSONObject.getNames(js);

		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

		for (String s : sa) {
			Map<String, Object> m1 = new HashMap<String, Object>();
			m1.put("key", s);
			m1.put("value", js.get(s));
			list.add(m1);
		}
		this.setAttr("ltdata", list);
		// this.render("showdata.html");
		this.render("showDataTransmission.html");
	}

	@Before(MInterceptor.class)
	public void dohomeexamine() {
		Orders order = this.getModel(Orders.class, "dao");
		order.setUser(((Record) this.getSessionAttr("user")).getInt("id"));

		if (((Record) this.getSessionAttr("user")).get("name").equals("")) {
			Db.update("update user set name=? where id=?", order.getName(),
					((Record) this.getSessionAttr("user")).getInt("id"));
			this.setSessionAttr("user", Db.findById("user", ((Record) this
					.getSessionAttr("user")).getInt("id")));
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");// 小写的mm表示的是分钟
		String dstr = this.getPara("dao.emtime");
		try {
			Date date = sdf.parse(dstr);
			order.setEmtime(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		order.setPrentqjd(Db.findById("position", order.getPositionid())
				.getInt("sp"));

		order.set("type", "上门量体");
		order.save();
		HttpSenderTest.sendmsg(((Record) this.getSessionAttr("user"))
				.getStr("username"), Conts.ExamineBeforeMsg);
		this.redirect("/m/personal");
	}

	/**
	 * 个人中心
	 */
	public void person() {

		Record r = Db.findFirst("select * from user where wxopenid=?", this
				.getPara("openid"));

		if (r == null) {
			this.redirect("/m/bd?openid=" + this.getPara("openid"));
			return;
		}

		this.setSessionAttr("user", r);
		this.redirect("/m/personal");
	}

	public void personal() {
		List<String[]> typefield = new ArrayList<String[]>();
		String[] ss = null;
		
		char a = 'a';
		if (this.getSessionAttr("user") == null) {
			this.redirect(Conts.wxoauth.replace("*state*", "person"));
			return;
		}
		if (this.getSessionAttr("redirect") != null) {
			this.redirect(this.getSessionAttr("redirect").toString());
			this.removeSessionAttr("redirect");
			return;
		}
		Map<String, Object> m = this.getSessionAttr("wxdata_"
				+ ((Record) this.getSessionAttr("user")).getStr("wxopenid"));

		Record r = this.getSessionAttr("user");
		Record r1 = Db
				.findFirst("select * from lt where user=? order by time desc",
						r.get("id"));
		//System.out.println(r1.getStr("data"));
		boolean r1datatag = false;
		if (r1 != null) {
			r1datatag = true;

			JSONObject js = new JSONObject(r1.getStr("data"));
			Sys.out(r1.getStr("data"));
			
			JSONArray ja = new JSONArray(PropKit.get("persondatatemp"));
			StringBuffer data = new StringBuffer();
			for (int i = 0; i < ja.length(); i++) {
				JSONObject js1 = (JSONObject) ja.get(i);
				 data.append("/"+js1.getString("data"));
				System.out.println(data);
			}
			for (int i = 0; i < Conts.type.length; i++) {
				String s = PropKit.get("typetemp" + (char) (a + i))+data;
				ss = s.split("/");
				Sys.out(s);
			}
			
			typefield.add(ss);
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

			for (String s : ss) {
				Map<String, Object> m1 = new HashMap<String, Object>();
				if(js.has(s)){
					m1.put("key", s);
					m1.put("value", js.get(s));
					list.add(m1);
				}
			}
			this.setAttr("ltdata", list);
		}

		Record rdata = Db
				.findFirst(
						"select * from datatransmission where telphone=? and data is not null order by time desc",
						r.get("username"));
		boolean rdatatag = false;

		if (rdata != null) {
			rdatatag = true;
			Sys.out(rdata);
			JSONObject datajs = new JSONObject(rdata.getStr("data"));
			JSONArray imgjs = new JSONArray(rdata.getStr("img"));

			List<Map<String,Object>> list1 = new ArrayList<Map<String,Object>>();
			for (int i = 0; i < imgjs.length(); i++) {
				Map<String, Object> m1 = new HashMap<String, Object>();
				m1.put("value", "http://www.gcgc.cn:88/" + imgjs.get(i));
				list1.add(m1);
			}
			this.setAttr("imgdata", list1);

			String sadata[] = JSONObject.getNames(datajs);

			List<Map<String,Object>> list2 = new ArrayList<Map<String,Object>>();

			for (String s : sadata) {
				Map<String, Object> m1 = new HashMap<String, Object>();
				m1.put("key", s);
				m1.put("value", datajs.get(s));
				list2.add(m1);
			}
			this.setAttr("ltdata1", list2);
		} 

		this.setAttr("ltdatatag", r1datatag);
		this.setAttr("datatag", rdatatag);
		
		this.setAttr("shop", Admin.dao
				.find("select * from admin where power='sp'"));
		this
				.setAttr(
						"order",
						Orders.dao
								.find(
										"select * from orders where user=?  and state!=-1 order by id desc",
										r.get("id")));
		this
				.setAttr(
						"makeorder",
						Makeorder.dao
								.find(
										"select * from makeorder where user=?  and state!=-1 order by id desc",
										r.get("id")));
		this.setAttr("wx", m);
		this.setAttr("user", r);
		this.render("personal.html");
	}

	/**
	 * 绑定
	 */
	public void bd() {
		Map<String, Object> m = this.getSessionAttr("wxdata_"
				+ this.getPara("openid"));
		this.setAttr("wx", m);
		this.render("login_bd.html");
	}

	/**
	 * 更新信息
	 */
	public void updateduserinfo() {
		Map<String, Object> m = new HashMap<String, Object>();

		String value = this.getPara("value");
		String id = this.getPara("id");
		String updateKey = this.getPara("key").substring(5);

		User user = User.me.findById(id);
		user.set(updateKey, value);
		user.update();
		m.put("code", 200);

		m.put("msg", "更新成功！");
		System.out.println(m);
		Record r = Db.findFirst("select * from user where id=?", id);
		this.setSessionAttr("user", r);
		this.renderJson(m);
	}

	/**
	 * 绑定操作
	 */
	@SuppressWarnings("unchecked")
	public void dobd() {
		String username = this.getPara("username");
		String valicode = this.getPara("valicode");
		Map<String, Object> m = new HashMap<String, Object>();

		if (!this.getSessionAttr("valicode_" + username).equals(valicode)) {
			m.put("code", 500);
			m.put("msg", "验证码错误！");
			this.renderJson(m);
			return;
		}

		Map<String, Object> wx = (Map<String, Object>) this
				.getSessionAttr("wxdata_" + this.getPara("openid"));
		Sys.out(wx);
		User user = User.me.findFirst("select * from user where username=?",
				username);
		if (user != null) {
			user.set("wxopenid", wx.get("openid"));
			user.set("wxdata", new JSONObject(wx).toString());
			user.update();
		} else {
			user = new User();
			user.set("username", username);
			user.set("tel", username);
			user.set("address", wx.get("province").toString()
					+ wx.get("city").toString());
			user.set("wxopenid", wx.get("openid"));
			user.set("wxdata", new JSONObject(wx).toString());
			user.save();

			user = User.me.findFirst("select * from user where username=?",
					username);
		}

		this.setAttr("user", user);

		m.put("code", 200);
		m.put("url", "/m/person?openid=" + wx.get("openid"));

		this.renderJson(m);
	}

	public void shirt() {
		this.render("shirt.html");
	}

	public void suit() {
		this.render("suit.html");
	}

	public void loosecoat() {
		this.render("loosecoat.html");
	}

	public void cheongsam() {
		this.render("cheongsam.html");
	}

	public void jeans() {
		this.render("jeans.html");
	}

	public void getcity3() {

		List<Record> l = Db.find("select * from position where prent=0");
		List<Map<String, Object>> lm = new ArrayList<Map<String, Object>>();

		for (Record r : l) {
			// 省级
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("value", r.get("id"));
			map.put("text", r.get("name"));

			List<Record> ll = Db.find("select * from position where prent=?", r
					.get("id"));
			List<Map<String, Object>> mlm = new ArrayList<Map<String, Object>>();
			for (Record rr : ll) {
				// 市级
				Map<String, Object> mmap = new HashMap<String, Object>();
				mmap.put("value", rr.get("id"));
				mmap.put("text", rr.get("name"));

				List<Record> lll = Db.find(
						"select * from position where prent=?", rr.get("id"));
				List<Map<String, Object>> mmlm = new ArrayList<Map<String, Object>>();
				for (Record rrr : lll) {
					// 区级
					Map<String, Object> mmmap = new HashMap<String, Object>();
					mmmap.put("value", rrr.get("id"));
					mmmap.put("text", rrr.get("name"));

					List<Record> llll = Db.find(
							"select * from position where prent=?", rrr
									.get("id"));
					List<Map<String, Object>> mmmlm = new ArrayList<Map<String, Object>>();
					for (Record rrrr : llll) {
						// 分区级
						Map<String, Object> mmmmap = new HashMap<String, Object>();
						mmmmap.put("value", rrrr.get("id"));
						mmmmap.put("text", rrrr.get("name"));
						mmmlm.add(mmmmap);
					}
					mmmap.put("children", mmmlm);
					mmlm.add(mmmap);
				}
				mmap.put("children", mmlm);
				mlm.add(mmap);
			}

			map.put("children", mlm);
			lm.add(map);
		}

		JSONArray js = new JSONArray(lm);

		this.renderJson("var cityData3 = " + js.toString());

	}
	
	
	/**
	 * 个人中心
	 */
	public void startltdata() {

		Record r = Db.findFirst("select * from user where wxopenid=?", this
				.getPara("openid"));

		if (r == null) {
			this.redirect("/m/bd?openid=" + this.getPara("openid"));
			return;
		}

		this.setSessionAttr("user", r);
		this.redirect("/m/ltdata");
	}

	public void ltdata() {
		List<String[]> typefield = new ArrayList<String[]>();
		String[] ss = null;
		
		char a = 'a';
		if (this.getSessionAttr("user") == null) {
			this.redirect(Conts.wxoauth.replace("*state*", "ltdata"));
			return;
		}
		if (this.getSessionAttr("redirect") != null) {
			this.redirect(this.getSessionAttr("redirect").toString());
			this.removeSessionAttr("redirect");
			return;
		}
		Map<String, Object> m = this.getSessionAttr("wxdata_"
				+ ((Record) this.getSessionAttr("user")).getStr("wxopenid"));

		Record r = this.getSessionAttr("user");
		Record r1 = Db
				.findFirst("select * from lt where user=? order by time desc",
						r.get("id"));
		//System.out.println(r1.getStr("data"));
		boolean r1datatag = false;
		if (r1 != null) {
			r1datatag = true;

			JSONObject js = new JSONObject(r1.getStr("data"));
			Sys.out(r1.getStr("data"));
			
			JSONArray ja = new JSONArray(PropKit.get("persondatatemp"));
			StringBuffer data = new StringBuffer();
			for (int i = 0; i < ja.length(); i++) {
				JSONObject js1 = (JSONObject) ja.get(i);
				 data.append("/"+js1.getString("data"));
				System.out.println(data);
			}
			for (int i = 0; i < Conts.type.length; i++) {
				String s = PropKit.get("typetemp" + (char) (a + i))+data;
				ss = s.split("/");
				Sys.out(s);
			}
			
			typefield.add(ss);
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

			for (String s : ss) {
				Map<String, Object> m1 = new HashMap<String, Object>();
				if( js.has(s)){
					m1.put("key", s);
					m1.put("value", js.get(s));
					list.add(m1);
				}
			}
			this.setAttr("ltdata", list);
		}

		Record rdata = Db
				.findFirst(
						"select * from datatransmission where telphone=? and data is not null order by time desc",
						r.get("username"));
		boolean rdatatag = false;

		if (rdata != null) {
			rdatatag = true;
			Sys.out(rdata);
			JSONObject datajs = new JSONObject(rdata.getStr("data"));
			JSONArray imgjs = new JSONArray(rdata.getStr("img"));

			List<Map<String,Object>> list1 = new ArrayList<Map<String,Object>>();
			for (int i = 0; i < imgjs.length(); i++) {
				Map<String, Object> m1 = new HashMap<String, Object>();
				m1.put("value", "http://www.gcgc.cn:88/" + imgjs.get(i));
				list1.add(m1);
			}
			this.setAttr("imgdata", list1);

			String sadata[] = JSONObject.getNames(datajs);

			List<Map<String,Object>> list2 = new ArrayList<Map<String,Object>>();

			//之前是sadaata现改为ss顺序改了下
			for (String s : ss) {
				Map<String, Object> m1 = new HashMap<String, Object>();
				if(datajs.has(s)){
					m1.put("key", s);
					m1.put("value", datajs.get(s));
					list2.add(m1);
				}
			}
			this.setAttr("ltdata1", list2);
		} 

		this.setAttr("ltdatatag", r1datatag);
		this.setAttr("datatag", rdatatag);
	
		
		this.setAttr("wx", m);
		this.setAttr("user", r);
		this.render("ltdata.html");
	}
	
	
	public void mshop(){
		this.renderText("敬请期待！");
	}
}
