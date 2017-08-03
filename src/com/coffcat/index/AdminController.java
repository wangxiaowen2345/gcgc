package com.coffcat.index;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import com.coffcat.common.config.CommonUtils;
import com.coffcat.common.config.Conts;
import com.coffcat.common.config.Key;
import com.coffcat.common.model.Admin;
import com.coffcat.common.model.Content;
import com.coffcat.common.model.Field;
import com.coffcat.common.model.Lt;
import com.coffcat.common.model.Makeorder;
import com.coffcat.common.model.Material;
import com.coffcat.common.model.Orders;
import com.coffcat.common.model.Position;
import com.coffcat.common.model.Requirement;
import com.coffcat.common.model.User;
import com.coffcat.common.model.Yx;
import com.coffcat.common.model.base.BaseOrders;
import com.coffcat.common.model.base.RJson;
import com.coffcat.interceptor.AdminInterceptor;
import com.coffcat.util.HttpSenderTest;
import com.coffcat.util.MD5Util;
import com.coffcat.util.SearchUtil;
import com.coffcat.util.Sys;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.ext.render.excel.PoiRender;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.render.Render;
import com.jfinal.upload.UploadFile;

/**
 * 后台控制器
 * 
 * @author hero
 * @Email:mahaojie299@163.com
 */
@Before(AdminInterceptor.class)
public class AdminController extends BaseController {

	/**
	 * 主页 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void index() {
		setData();
		this.render("index.html");
	}

	/**
	 * 登陆 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	@Clear
	public void login() {
		setData();
		this.render("login.html");
	}

	/**
	 * 登陆提交 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	@Clear
	public void dologin() {
		String username = this.getPara("username");
		String password = this.getPara("password");

		Map<String, String> m = new HashMap<String, String>();

		if (username == null || password == null || username.equals("")
				|| password.equals("")) {
			m.put("code", "400");
			m.put("message", "用户名或密码不能为空！");
			this.renderJson(m);
			return;
		}

		// 登陆操作
		Record r = Db.findFirst(
				"select * from admin where username=? and password=?",
				username, MD5Util.string2MD5(password));
		if (r == null) {
			m.put("code", "401");
			m.put("message", "用户名或密码不正确！");
			this.renderJson(m);
			return;
		}
		
		if(r.get("power").equals("fc")){
			// 保存Session 登陆状态
			this.setSessionAttr("admin_fc_user", r);
			// 返回登陆成功及跳转网址
			m.put("code", "200");
			m.put("message", "/fc");
			this.renderJson(m);
			return;
		}else{
			// 保存Session 登陆状态
			this.setSessionAttr("admin_user", r);
		}
		// 返回登陆成功及跳转网址
		m.put("code", "200");
		m.put("message", "/admin");
		this.renderJson(m);

	}

	/**
	 * cms模块 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void cms() {
		setData();
		setAttr("contentPage",
				Db.paginate(getParaToInt(0, 1), Conts.PageSize, "select * ",
						"from content where type='moddle' order by id asc"));

		this.render("cms.html");
	}

	/**
	 * @author hero 添加分类跳转
	 * 
	 *         已测试
	 */
	public void addmoddle() {
		setData();

		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		this.setAttr("time", dateFormat.format(now));

		this.render("addmoddle.html");
	}

	/**
	 * @author hero 添加分类操作
	 * 
	 *         已测试
	 */
	public void doaddmoddle() {
		Content content = this.getModel(Content.class, "dao");

		content.set("publish",
				((Record) this.getSessionAttr("admin_user")).getInt("id")).set(
				"type", "moddle");
		content.save();
		this.redirect("cms");
	}

	/**
	 * @author hero
	 * 
	 *         编辑分类跳转
	 * 
	 *         已测试
	 */
	public void editmoddle() {
		setData();

		String id = this.getPara("id");

		Record r = Db.findFirst("select * from content where id=?", id);

		this.setAttr("r", r);

		this.setAttr("user", Db.findFirst("select * from admin where id=?",
				r.get("publish")));

		this.render("editmoddle.html");
	}

	/**
	 * 编辑分类操作 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void doeditmoddle() {
		Content content = this.getModel(Content.class, "dao");
		content.update();
		this.redirect("cms");
	}

	/**
	 * 退出登陆 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void outlog() {
		this.getSession().removeAttribute("admin_user");
		this.redirect("login");
	}

	/**
	 * 添加文章跳转 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void addart() {
		setData();

		List<Record> list = Db
				.find("select * from content where type='moddle' order by id asc");

		this.setAttr("list", list);

		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		this.setAttr("time", dateFormat.format(now));

		this.render("addart.html");
	}

	/**
	 * 添加文章操作 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void doaddart() {
		Content content = this.getModel(Content.class, "dao");
		content.save();

		this.redirect("cms");
	}

	public void artlist() {
		setData();

		setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
				"select * ",
				"from content where type='art' and prent=? order by id asc",
				this.getPara("id")));

	this.setAttr("urlParas", "?id="+this.getPara("id"));	
		this.render("artlist.html");
	}

	/**
	 * 编辑文章跳转 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void editart() {
		setData();

		Content content = Content.dao.findById(this.getPara("id"));

		List<Record> list = Db
				.find("select * from content where type='moddle' order by id asc");

		this.setAttr("list", list);

		this.setAttr("r", content);

		this.setAttr(
				"user",
				Db.findFirst("select * from admin where id=?",
						content.get("publish")));

		this.render("editart.html");
	}

	/**
	 * 编辑文章操作 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void doeditart() {
		Content content = this.getModel(Content.class, "dao");
		content.update();
		this.redirect("cms");
	}

	/**
	 * json删除分类 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void deletemoddle() {
		String id = this.getPara("id");
		Map<String, String> m = new HashMap<String, String>();
		if (id.equals("1")) {
			m.put("code", "400");
			m.put("message", "本分类是默认，不可删除，可更改栏目名称。");
			this.renderJson(m);
			return;
		}
		Db.update("update content set prent=1 where prent=?", id);
		Db.deleteById("content", id);

		m.put("code", "200");
		m.put("message", "删除成功，此分类下的文章已转移到默认分类。");
		this.renderJson(m);
	}

	/**
	 * json删除文章 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void deleteart() {
		String id = this.getPara("id");
		Map<String, String> m = new HashMap<String, String>();
		Db.deleteById("content", id);
		m.put("code", "200");
		m.put("message", "删除成功。");
		this.renderJson(m);
	}

	/**
	 * 用户模糊搜索 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void user() {
		setData();
		com.jfinal.plugin.activerecord.Page<Record> p = SearchUtil.FuzzySearch(this, "user", new String[] { "username",
				"tel", "name" });
		List<Record> l = p.getList();
		//List<Record> list = new ArrayList<Record>();
		
		for(Record r : l){
			if(Db.find("select * from lt where user=?",r.get("id")).size()>0){
				r.set("viptype", "正式会员");
			}else{
				r.set("viptype","预备会员");
			}
		}
		
	
		//com.jfinal.plugin.activerecord.Page<Record> page = new com.jfinal.plugin.activerecord.Page<Record>(list, p.getPageNumber(), p.getPageSize(), p.getTotalPage(), p.getTotalRow());
		this.setAttr(
				"userPage",
				p);
		
		setPageUtils("search");
		this.render("user.html");
	}

	/**
	 * 公用方法 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	private void setData() {
		this.setAttr("K", K);
		this.setAttr("title", this.getRequest().getRequestURI());
		this.setAttr("admin_user", this.getSessionAttr("admin_user"));
		/** this.setAttr("user", this.getSessionAttr("admin_user")); **/
		String url = this.getRequest().getRequestURL().toString()
				.replace("http://", "");
		String[] p = url.split("/");
		String method = "";
		if (p.length == 2) {
			method = "index";
		} else {
			if (getCharIndex(p[2], '?') == -1) {
				method = p[2];
			} else {
				method = p[2].substring(0, getCharIndex(p[2], '?'));
			}
		}

		Record r = Db.findFirst("select * from page where method=?", method);

		this.setAttr("page", r);

	}

	/**
	 * 获取字符串中某个char第一次出现的位置
	 * @param s
	 * @param c
	 * @return
	 */
	private int getCharIndex(String s, char c) {
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == c)
				return i;
		}
		return -1;
	}

	/**
	 * 添加用户跳转 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void adduser() {
		setData();

		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		

		this.setAttr("time", dateFormat.format(now));

		this.render("adduser.html");
	}

	/**
	 * 检查用户是否存在 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void checkuser() {
		String username = this.getPara("username");
		Map<String, String> m = new HashMap<String, String>();

		if (Db.find("select * from user where username=?", username).size() == 0) {
			m.put("code", "200");
		} else {
			m.put("code", "400");
		}
		this.renderJson(m);
	}

	/**
	 * 添加用户 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void doadduser() {

		User user = this.getModel(User.class);

		if (Db.find("select * from user where username=?", user.get("username"))
				.size() == 0) {
			System.out.println("插入用户");
			user.set("password",
					MD5Util.string2MD5(user.get("password").toString()));
			user.set("time", new Date());
			user.save();
		}
		this.redirect("user");
	}

	/**
	 * 编辑用户跳转 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void edituser() {
		setData();

		User user = User.me.findById(this.getPara("id"));
		this.setAttr("r", user);

		this.render("edituser.html");
	}

	/**
	 * 编辑用户操作 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void doedituser() {
		setData();
		User user = this.getModel(User.class, "dao");
		
		

		if (user.get("password") == null || user.get("password").equals(""))
			user.set("password",
					null);
		else
			user.set("password", MD5Util.string2MD5(user.getStr("password")));
		
		
		System.out.println(user);

		user.update();

		this.redirect("user");
	}
	
	/**
	 * 编辑量体师操作 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void doeditlt() {
		setData();
		Admin ad = this.getModel(Admin.class, "dao");
		
		

		if (ad.get("password") == null || ad.get("password").equals(""))
			ad.set("password",
					null);
		else
			ad.set("password", MD5Util.string2MD5(ad.getStr("password")));
		
		
		System.out.println(ad);

		ad.update();

		this.redirect("/admin");
	}
	
	
	
	/**
	 * 预约订单列表
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void examine() {

		setData();
		if(this.getPara("type")==null)
			setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
					"select * ",
					"from orders where state=? or state=? or state=? order by id asc",Key.orders_neworder,Key.orders_neworder,Key.orders_passexamine));
		else if(this.getPara("type").equals("new"))
			setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
					"select * ",
					"from orders where state=? order by id desc",Key.orders_neworder));
		else if(this.getPara("type").equals("retime"))
			setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
					"select * ",
					"from orders where state=? order by id desc",Key.orders_retime));

		this.render("examine.html");
	}

	/**
	 * 修改预约订单页面
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void editexamine() {
		setData();

		this.setAttr("r", Orders.dao.findById(this.getPara("id")));
		this.setAttr("shoplist",
				Db.find("select * from admin where power='sp'"));
		this.render("editexamine.html");
	}

	/**
	 * 修改预约订单参数
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void editexaminevalue() {
		Map<String, Object> m = new HashMap<String, Object>();

		Db.update("update orders set " + this.getPara("key") + "=? where id=?",
				this.getPara("value"), this.getPara("id"));
		m.put("code", 200);
		this.renderJson(m);
	}
	
	public void editexaminevalueforltdata() {
		Map<String, Object> m = new HashMap<String, Object>();
		
		if(this.getPara("type").equals("上门量体")){
			Record r = new Record();
			r.set("id", this.getPara("id"));
			r.set("type", this.getPara("type"));
			r.set("prentqjd", this.getPara("prentqjd"));
			r.set("wantsex", this.getPara("wantsex"));
			Db.update("orders", r);
		}else{
			Record r = new Record();
			r.set("id", this.getPara("id"));
			r.set("type", this.getPara("type"));
			r.set("shop", this.getPara("shop"));
			Db.update("orders", r);
		}
		
		
		
		m.put("code", 200);
		this.renderJson(m);
	}
	
	/**
	 * 订单通过预审
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void editexaminepass() {
		RJson x = new RJson();
		if(!power_kf()){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			Record r = Db.findById("orders", this.getPara("id"));
			
			if(r.get("type").equals("到店量体"))
				Db.update("update orders set state=? , pd=? where id=?", Key.orders_sendlist,r.get("shop"),this.getPara("id"));
			else			
				Db.update("update orders set state=? where id=?", Key.orders_passexamine ,this.getPara("id"));
			x.setCode(200);
			x.setMsg("成功！");	
		}
		
		
		this.renderJson(x);
	}

	/**
	 * 订单无效
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void editexamineinvalid() {
		RJson x = new RJson();
		if(!power_kf()){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			Db.update("update orders set state=? where id=?", Key.orders_invalid ,this.getPara("id"));
			x.setCode(200);
			x.setMsg("成功！");
		}
		this.renderJson(x);
	}
	
	/**
	 * 订单需审核
	 * 
	 */
	public void editexamineneworder() {
		RJson x = new RJson();
		
			Db.update("update makeorder set state=? where id=?", Key.make_neworder ,this.getPara("id"));
			x.setCode(200);
			x.setMsg("添加审核订单成功！");
		
		this.renderJson(x);
	}
	
	/**
	 */
	public void makeorderinvalid() {
		RJson x = new RJson();
		String a = this.getPara("num");
		if("1".equals(this.getPara("num"))){
			Db.update("update makeorder set state=? where id=?", Key.make_returngoods ,this.getPara("id"));
			x.setCode(200);
			x.setMsg("成功！");
		}else{
			Db.update("update makeorder set state=? where id=?", Key.orders_invalid ,this.getPara("id"));
			x.setCode(200);
			x.setMsg("成功！");
		}
		this.renderJson(x);
	}

	/**
	 * 改约订单
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void editexamineredata() {
		RJson x = new RJson();
		if(!power_kf()){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			Db.update("update orders set state=? where id=?", Key.orders_retime ,this.getPara("id"));
			x.setCode(200);
			x.setMsg("成功！");
		}
		this.renderJson(x);
	}

	/**
	 * 后台用户模糊搜索 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void admin() {
		Record a = this.getSessionAttr("admin_user");
		setData();
		String s = " or 1=1";
		if("fc".equals(this.getPara("type"))&&"su".equals(a.get("power"))){
			s = "power='fc'";
			System.out.println(s);
			this.setAttr("userPage",
					Admin.dao.paginate(getParaToInt(0,1), Conts.PageSize, "select *", "from admin where "+s));
			this.setAttr("userPagelist",
					Admin.dao.find("select * from admin where "+s));
			setAttr("power", Db.find("select * from power"));
			setPageUtils("type");
			this.render("admin.html");
			return;
		}
		if("sp".equals(this.getPara("type"))&&"qjd".equals(a.get("power"))){
			s = "power='"+this.getPara("type")+"' and sp_prent='"+a.get("id")+"'";
			System.out.println(s);
			this.setAttr("userPage",
					Admin.dao.paginate(getParaToInt(0,1), Conts.PageSize, "select *", "from admin where "+s));
			
			setAttr("power", Db.find("select * from power"));
			setPageUtils("type");
			this.render("qjdadmin.html");
			return;
		}
		if("lt".equals(this.getPara("type"))&&"qjd".equals(a.get("power"))){
			s = "power='"+this.getPara("type")+"' and lt_prent='"+a.get("id")+"'";
			System.out.println(s);
			this.setAttr("userPage",
					Admin.dao.paginate(getParaToInt(0,1), Conts.PageSize, "select *", "from admin where "+s));
			
			setAttr("power", Db.find("select * from power"));
			setPageUtils("type");
			this.render("qjdadmin.html");
			return;
		}
		if(this.getPara("type")!=null && !this.getPara("type").equals("")){
			s = " or power='"+this.getPara("type")+"'";
		}
		System.out.println(s);
		this.setAttr("userPage",
				Admin.dao.paginate(getParaToInt(0,1), Conts.PageSize, "select *", "from admin where 1=0"+ s));
		this.setAttr("userPagelist",
				Admin.dao.find("select * from admin where 1=0"+ s));
		setAttr("power", Db.find("select * from power"));
		setPageUtils("type");
		
		
			
		this.render("admin.html");
	}
	
	private List<Map<String, String>> getMinPosition(){
		List<Record> a = Db.find("select * from position");
		List<Map<String,String>> l = new ArrayList<Map<String,String>>();
		
		for(Record r : a){
			if(2==getPositionGrade(r.get("id").toString())){
				HashMap<String, String> m = new HashMap<String, String>();
				m.put("id", r.get("id")+"");
				m.put("name", getLongPositionName(r.getInt("id")));
				l.add(m);
			}
		}
		
		return l;
	}
	
	private String getLongPositionName(int id){
		String s = "";
			Position p = Position.dao.findById(id);
		s=p.getName();
		for(/* 无条件  */;p.getPrent()!=0;/* 无条件  */){
			p = Position.dao.findById(p.getPrent());
			s=(p.getName()+"-")+s;
		}
		
		return s;
	}
	
	
	public void adminuserstate(){
		Admin admin = new Admin();
		admin.setId(Integer.parseInt(this.getPara("id")));
		admin.setState(this.getParaToInt("state"));
		admin.update();
		this.renderText("OK"); 
	}
	
	
	

	/**
	 * 添加后台用户跳转 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void addadmin() {
		setData();

		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		setAttr("power", Db.find("select * from power where id > 1"));

		this.setAttr("time", dateFormat.format(now));
		this.setAttr("positionlist", getMinPosition());
		
		this.setAttr("qjd", Db.find("select * from admin where power='qjd'"));
		
		this.render("addadmin.html");
	}

	/**
	 * 检查后台用户是否存在 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void checkadmin() {
		String username = this.getPara("username");
		Map<String, String> m = new HashMap<String, String>();

		if (Db.find("select * from admin where username=?", username).size() == 0) {
			m.put("code", "200");
		} else {
			m.put("code", "400");
		}
		this.renderJson(m);
	}

	/**
	 * 添加后台用户 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void doaddadmin() {

		Admin user = this.getModel(Admin.class,"dao");

		if (Db.find("select * from admin where username=?",
				user.get("username")).size() == 0) {
			System.out.println("插入后台用户");
			user.set("password",
					MD5Util.string2MD5(user.getPassword()));
			user.set("time", new Date());
			user.save();
		}
		this.redirect("admin");
	}

	
	/**
	 * 编辑后台用户跳转 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void editadmin() {
		setData();

		Admin user = Admin.dao.findById(this.getPara("id"));
		this.setAttr("r", user);

		this.render("editadmin.html");
	}

	/**
	 * 编辑后台用户操作 已测试
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void doeditadmin() {
		setData();
		Admin user = this.getModel(Admin.class, "dao");
		if (user.get("password") == null)
			user.set("password",
					user.findById(user.get("id")).getStr("password"));
		else
			user.set("password", MD5Util.string2MD5(user.getStr("password")));
		user.update();
		this.redirect("admin");
	}

	/**
	 * 订单派单列表
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void orderpd() {
		setData();
		if(Login_Admin_user_Data().get("power").equals("gl") || Login_Admin_user_Data().get("power").equals("su")){
			if(this.getPara("type")==null){
				setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
						"select * ",
						"from orders where state=? or state=? order by id desc",Key.orders_passexamine,Key.orders_sendlist));
			}else if(this.getPara("type").equals("uncompleted"))
				setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
						"select * ",
						"from orders where state=? order by id desc",Key.orders_passexamine));
			else if(this.getPara("type").equals("ready"))
				setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
						"select * ",
						"from orders where state=? order by id desc",Key.orders_sendlist));
			
			this.render("orderpd.html");
		}else if(Login_Admin_user_Data().get("power").equals("qjd")){
			
			if(this.getPara("type")==null){
				System.out.println(Login_Admin_user_Data().get("id"));
				setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
						"select * ",
						"from orders where ( state=? or state=? ) and prentqjd=? order by id desc",Key.orders_passexamine,Key.orders_sendlist,Login_Admin_user_Data().get("id")));
			}else if(this.getPara("type").equals("uncompleted"))
				setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
						"select * ",
						"from orders where state=? and prentqjd=? order by id desc",Key.orders_passexamine,Login_Admin_user_Data().get("id")));
			else if(this.getPara("type").equals("ready"))
				setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
						"select * ",
						"from orders where state=? and prentqjd=? order by id desc",Key.orders_sendlist,Login_Admin_user_Data().get("id")));
			this.render("orderpd.html");
		}else{
			setAttr("contentPage",Db.paginate(getParaToInt(0, 1), Conts.PageSize,
					"select * ",
					"from orders where 1=0"));
			this.render("orderpd.html");
		}	
	}

	/**
	 * 订单派单页面
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void editorderpd() {
		
		setData();
		
		this.setAttr("shoplist",
				Db.find("select * from admin where power='sp'"));
		
		
		//Orders o = Orders.dao.findById(this.getPara("id"));
			
		//Position p = Position.dao.findById(o.getPositionid());
		List<Record> l;
		if(power_qjd())	
			l = Db.find("select * from admin where power='lt' and lt_prent=?",Login_Admin_user_Data().get("id"));	
		else
			l = Db.find("select * from admin");	
		this.setAttr("pdlist", l);
		
		this.setAttr("r", Orders.dao.findById(this.getPara("id")));
		
		this.setAttr("power", power_qjd()?"true":"false");
		
		
		this.render("editorderpd.html");
	}

	/**
	 * 订单派单
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void doorderpd() {
		RJson x = new RJson();
		if(!power_qjd()){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			Db.update("update orders set state=?,pd=? where id=?",Key.orders_sendlist,
					this.getPara("topdid"), this.getPara("id"));
			x.setCode(200);
			x.setMsg("派单成功！");
			Record r = Db.findById("orders", this.getPara("id"));
			Admin admin =  Admin.dao.findById(r.get("pd"));	
			String msg = Conts.OrderPdMsg.replace("%orderid%", r.get("id")+"")
					.replace("%name%", admin.getName()).replace("%tel%", admin.getTel())
					.replace("%time%", r.get("emtime").toString()).replace("%position%", r.getStr("address"));
			HttpSenderTest.sendmsg(r.getStr("tel"), msg);
		}
		
		
		this.renderJson(x);
	}

	/**
	 * 量体列表
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void orderlt() {
		setData();
		if(power_su() || power_gl())
			setAttr("contentPage",
					Db.paginate(
							getParaToInt(0, 1),
							Conts.PageSize,
							"select * ",
							"from orders where state=? or state=? order by id desc",
							Key.orders_sendlist,Key.orders_end));
		else
			setAttr("contentPage",
					Db.paginate(
							getParaToInt(0, 1),
							Conts.PageSize,
							"select * ",
							"from orders where pd=? and (state=? or state=?) order by id desc",
							((Record) this.getSessionAttr("admin_user")).get("id"),Key.orders_sendlist,Key.orders_end));
		this.render("orderlt.html");
	}

	/**
	 * 系统信息
	 */
	public void systeminfo() {
		getSystemInfo();
		this.renderJson(K.get("SystemInfo"));
	}

	/**
	 * 所有预约订单
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void orderslist() {
		String s = "";
		if (this.getPara("type") != null && !this.getPara("type").equals("")) {
			String p = this.getPara("type");
			s = SearchUtil.getSql("where state=?", p);
		}
		setData();

		setAttr("state", Db.find("select * from state"));

		setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
				"select * ", "from orders " + s + " order by id desc"));

		this.render("orderslist.html");
	}
	
	
	public void showorder(){
		setData();
		String id = this.getPara("id");
		Orders o = Orders.dao.findById(id);
		
		this.setAttr("r", o);
		this.setAttr("shoplist",
				Db.find("select * from admin where power='sp'"));
		
		this.render("showorder.html");
		
	}
	
	

	/**
	 * 量体页面
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void ly() {
		setData();
		
		
		String[] s = Conts.type;

		List<String> l = new ArrayList<String>();
		for (String son : s)
			l.add(son);
		this.setAttr("typelist", l);
		this.setAttr("r", Orders.dao.findById(this.getPara("id")));
		this.setAttr("shoplist",
				Db.find("select * from admin where power='sp'"));
		this.setAttr("pdlist",
				Db.find("select * from admin"));
		this.render("ly.html");
	}

	/**
	 * 量体第二步
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void ly2() {
		setData();
		
		if(!(power_sp() || power_lt()|| power_qjd())){
			this.setAttr("js", "alert('没有权限！');");
			this.render("index.html");
			return;
		}


		String from = this.getPara("from");

		int type = Integer.parseInt(this.getPara("ctype"));

		String[] ss = Conts.typefield.get(type);
		String[] s = Conts.type;

		List<String> ll = new ArrayList<String>();
		for (String son : ss)
			ll.add(son);

		this.setAttr("ctype", type);
		List<String> l = new ArrayList<String>();
		for (String son : s)
			l.add(son);
		this.setAttr("typelist", l);
		this.setAttr("sontypelist", ll);
		this.setAttr("from", from);

		this.setAttr("persondata", Conts.persondata);

		if (from.equals("ly1")) {
			this.setAttr("r", Orders.dao.findById(this.getPara("id")));
			this.setAttr("shoplist",
					Db.find("select * from admin where power='sp'"));
			this.setAttr("pdlist", Db
					.find("select * from admin where power='sp' or power='lt'"));
			this.render("ly2.html");
		} else {

			this.setAttr("r", User.me.findById(this.getPara("id")));
			this.setAttr("shoplist",
					Db.find("select * from admin where power='sp'"));
			this.setAttr("pdlist", Db
					.find("select * from admin where power='sp' or power='lt'"));
			this.render("ly2.html");
		}

	}

	/**
	 * save lt data and updata
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void ly3() {

		// for get data
		Map<String, String> m = new HashMap<String, String>();
		int num = Integer.parseInt(this.getPara("num"));
		for (int i = 0; i < num; i++) {
			m.put(Conts.typefield.get(Integer.parseInt(this.getPara("ctype")))[i],
					this.getPara("k_" + i));
		}

		int personnum = Integer.parseInt(this.getPara("persondatanum"));
		for (int i = 0; i < personnum; i++) {
			m.put(Conts.persondata.get(i).get("data").toString(),
					this.getPara("pk_" + i));
		}

		JSONObject js = new JSONObject(m);
		String s = js.toString();

		Record r = new Record();
		setData();

		if (this.getPara("from").equals("ly1")) {
			// find old order data
			BaseOrders<Orders> orders = Orders.dao.findById(this.getPara("id"));

			// sava lt data
			r.set("user", orders.get("user"))
					.set("type", this.getPara("ctype")).set("data", s)
					.set("ordersid", Integer.parseInt(this.getPara("id")));
			
			//sava lt pic
			r.set("pic1", this.getPara("file1"));
			r.set("pic2", this.getPara("file2"));
			r.set("pic3", this.getPara("file3"));
			
			r.set("douser",Login_Admin_user_Data().getInt("id"));
			Db.save("lt", r);

			// update examine state to end
			orders.set("state", Key.orders_end);
			orders.update();

			// set userid to view
			setAttr("userid", orders.get("user"));
		} else if (this.getPara("from").equals("createmakeorder")) {
			// sava lt data
			r.set("user", this.getPara("id"))
					.set("type", this.getPara("ctype")).set("data", s);
			
			//sava lt pic
			r.set("pic1", this.getPara("file1"));
			r.set("pic2", this.getPara("file2"));
			r.set("pic3", this.getPara("file3"));
			r.set("douser",Login_Admin_user_Data().getInt("id"));
			Db.save("lt", r);

			// set userid to view
			setAttr("userid", this.getPara("id"));

			// jump to createmakeorder next

		}

		this.render("ly3.html");
	}

	/**
	 * 添加物料或工艺
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void addwlorgy() {
		Material r = this.getModel(Material.class, "dao");
		if (r.get("name") != null) {
			r.save();
			this.redirect("/admin/wlorgy?id="+this.getPara("prentid"));
			return;
		}
		List<Record> temp = Db.find("select * from field where id=?",this.getPara("prentid"));
		List<Record> typeclass = new ArrayList<Record>();
		for(Record rr : temp){
			boolean isprice = false;
			
			Record t = Db.findFirst("select * from yx where isprice=?",rr.get("id"));
			if(t!=null)
				isprice=true;
			rr.set("isprice",isprice);
			typeclass.add(rr);
		}
		setAttr("typeclass", typeclass);
		setData();
		this.render("addmaterial.html");
	}

	/**
	 * 物料或工艺列表
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void wlorgy() {
		if(this.getPara("id")==null || "".equals(this.getPara("id")))
			setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
					"select * ", "from material order by id asc"));
		else{
			setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
					"select * ", "from material where prent=? order by id asc",this.getPara("id")));
			this.setAttr("id", this.getPara("id"));
			}
		
		setAttr("prentid", this.getPara("id"));
		
		setData();
		this.render("wlorgy.html");
	}

	public void deletewlorgy(){
		Material.dao.deleteById(this.getPara("id"));
		this.renderText("ok");
	}
	
	

	public void deletefield(){
		Db.update("delete from material where prent=?",this.getPara("id"));
		Field.dao.deleteById(this.getPara("id"));
		this.renderText("ok");
	}
	


	public void deleteyx(){
		List<Record> list =  Db.find("select * from field where prent=?",this.getPara("id"));
		for(Record r : list){
			Db.update("delete from material where prent=?",r.get("id"));
			Field.dao.deleteById(r.get("id"));
		}
		Yx.dao.deleteById(this.getPara("id"));
		
		
		this.renderText("ok");
	}
	
	
	/**
	 * 编辑物料或工艺
	 * 
	 */
	public void editwlorgy() {
		Material m = this.getModel(Material.class, "dao");
		if (m.get("id") != null) {
			m.update();
			this.redirect("/admin/wlorgy?id="+this.getPara("prentid"));
			return;
		}

		setData();
		String id = this.getPara("id");
		m = Material.dao.findById(id);
		this.setAttr("r", m);
		
		List<Record> temp = Db.find("select * from field where id=?",this.getPara("prentid"));
		List<Record> typeclass = new ArrayList<Record>();
		for(Record r : temp){
			boolean isprice = false;
			
			Record t = Db.findFirst("select * from yx where isprice=?",r.get("id"));
			if(t!=null)
				isprice=true;
			r.set("isprice",isprice);
			typeclass.add(r);
		}
		setAttr("typeclass", typeclass);
		
		
		
		this.render("editmaterial.html");
	}

	/**
	 * 商品列表
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void yx() {
		setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
				"select * ", "from yx order by id asc"));
		setData();
		this.render("yx.html");
	}

	/**
	 * 添加商品
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void addyx() {
		Yx r = this.getModel(Yx.class, "dao");
		if (r.get("name") != null) {
			r.save();
			this.redirect("/admin/yx");
			return;
		}

		String[] s = Conts.type;
		List<String> l = new ArrayList<String>();
		for (String son : s)
			l.add(son);

		setData();
		this.setAttr("typelist", l);
		this.render("addyx.html");
	}

	/**
	 * 编辑商品
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void edityx() {
		Yx m = this.getModel(Yx.class, "dao");
		if (m.get("id") != null) {
			m.update();
			this.redirect("/admin/yx");
			return;
		}
		String[] s = Conts.type;
		List<String> l = new ArrayList<String>();
		for (String son : s)
			l.add(son);
		setData();
		String id = this.getPara("id");
		m = Yx.dao.findById(id);
		
		this.setAttr("isprice", Db.find("select * from field where prent=?",this.getPara("id")));
		
		this.setAttr("r", m);
		this.setAttr("typelist", l);
		this.render("edityx.html");
	}

	/**
	 * 物料或工艺分类
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void field() {
		String a =this.getPara("id");
		if(this.getPara("id") == null)
			setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
					"select * ", "from field order by id asc"));
		else{
			setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
					"select * ", "from field where prent=? order by id asc",this.getPara("id")));
			this.setAttr("id", this.getPara("id"));
		}
		setAttr("prentid", this.getPara("id"));
		setData();
		this.render("field.html");
	}
	

	/**
	 * 编辑物料或工艺分类
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void editfield() {
		Field m = this.getModel(Field.class, "dao");
		if (m.get("id") != null) {
			m.update();
			this.redirect("/admin/field?id="+this.getPara("prentid"));
			return;
		}

		setData();
		String id = this.getPara("id");
		m = Field.dao.findById(id);
		this.setAttr("r", m);
		this.setAttr("yxlist", Db.find("select * from yx where id=?",this.getPara("prentid")));
		this.render("editfield.html");
	}

	/**
	 * 添加物料或工艺分类
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void addfield() {
		Field m = this.getModel(Field.class, "dao");
		if (m.get("name") != null) {
			m.save();
			this.redirect("/admin/field?id="+this.getPara("prentid"));
			return;
		}
		this.setAttr("yxlist", Db.find("select * from yx where id=?",this.getPara("prentid")));
		setData();
		this.render("addfield.html");
	}

	/**
	 * makeorder list
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void makeorder() {
		setData();
		Record r = this.getSessionAttr("admin_user");
		String rid = r.get("id").toString();
		String urlParas = "";
		String s1 = "where 1=1 ";
		String s = "";
		if (this.getPara("type") != null && !this.getPara("type").equals("")) {
			String p = this.getPara("type");
			s = SearchUtil.getSql("and state=? ", p);
			urlParas = "?type="+this.getPara("type");
		}
		setData();

		if(power_lt()||power_sp()){
			String ss = SearchUtil.getSql("and order_createrid=? ", rid);
			setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
					"select * ", "from makeorder " + s1 + s + ss + " order by id desc"));
		}else if(power_qjd()){
			String ss = SearchUtil.getSql("and order_creater_prentid=? ", rid);
			setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
					"select * ", "from makeorder " + s1 + s + ss + " order by id desc"));
		}
		else{
			setAttr("contentPage", Db.paginate(getParaToInt(0, 1), Conts.PageSize,
				"select * ", "from makeorder " + s1 + s + " order by id desc"));
		}

		
		
		
		this.setAttr("urlParas", urlParas);
		
		
		this.render("makeorder.html");
	}

	/**
	 * 创建生产订单
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void createmakeorder() {
		String from = this.getPara("from");

		if (from == null) {
			from = "createmakeorder";
		} else if (from.equals("ly")) {
			setAttr("user",
					Db.findFirst("select * from user where id=?",
							this.getPara("userid")));
		}

		setData();

		// find yx list from database set to view
		setAttr("yxlist", Db.find("select * from yx"));
		setAttr("from", from);
		this.render("createmakeorder.html");
	}

	/**
	 * find user where tel
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void finduser() {
		String phone = this.getPara("phone");

		Record r = Db.findFirst(
				"select username,name,tel,address,id from user where tel=?",
				phone);

		if (r == null) {
			r = new Record();
			r.set("code", 404);
		} else {
			r.set("code", 200);
		}

		this.renderJson(r);

	}
	
	public void findtel() {
		String id = this.getPara("id");

		Record r = Db.findFirst(
				"select * from admin where id=?",
				id);

		if (r == null) {
			r = new Record();
			r.set("code", 404);
		} else {
			r.set("code", 200);
		}

		this.renderJson(r);

	}

	/**
	 * create make order step1
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void createmakeorder1() {

		setData();
		if(!(power_sp() || power_lt()|| power_qjd())){
			this.setAttr("js", "alert('没有权限！');");
			this.render("index.html");
			return;
		}
		
		this.setAttr("power", Login_Admin_user_Data().get("power"));
		
		setData();
		String yx = this.getPara("yx");
		String userid = this.getPara("userid");
		String isvip = this.getPara("isvip");

		this.setAttr("userid", userid);
		Record r = Db.findFirst("select * from yx where id=?", yx);
		this.setAttr("ctype", r.get("type"));
		this.setAttr("yx", yx);
		this.setAttr("yxdata", r);
		
		

		if (isvip.equals("0")) {
			// 非会员逻辑
			this.setAttr("otype", "no vip");
			this.setAttr("userid", 0);

			// 需返回量体id 或 data

		} else {
			Record rr = Db
					.findFirst(
							"select * from lt where user=? and type=? order by id desc",
							userid, r.get("type"));
			if (rr == null) {
				this.setAttr("otype", "vip no data");
				this.render("createmakeorder1.html");
				return;
			} else {
				this.setAttr("otype", "next");
				this.setAttr("userid", userid);
				this.setAttr("ltid", rr.get("id"));
			}
		}

		List<Field> f = Field.dao.find("select * from field where prent=?",
				r.get("id"));
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (Field son : f) {

			List<Material> M = Material.dao.find(
					"select * from Material where prent=?", son.get("id"));

			List<Material> lm = new ArrayList<Material>();

			for (Material sonson : M) {
				lm.add(sonson);
			}
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("name", son.get("name"));
			m.put("id", son.get("id"));
			m.put("list", lm);
			list.add(m);
		}
		System.out.println(Conts.orderpersontrousersdata);
		System.out.println(Conts.orderpersondata);
		if("0".equals(r.get("ishave"))){
			this.setAttr("orderpersondata", Conts.orderpersontrousersdata);
		}
		else{
			this.setAttr("orderpersondata", Conts.orderpersondata);
		}
		

		this.setAttr("user", User.me.findById(userid));

		this.setAttr("mlist", list);

		this.setAttr("shoplist",
				Db.find("select * from admin where power='sp'"));

		this.render("createmakeorder1.html");
	}

	/**
	 * create make orders step2
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void createmakeorder2() {

		setData();
		if(!(power_sp() || power_lt()|| power_qjd())){
			this.setAttr("js", "alert('没有权限！');");
			this.render("index.html");
			return;
		}
		
		this.setAttr("power", Login_Admin_user_Data().get("power"));
		
		String userid = this.getPara("userid");
		String ltid = this.getPara("ltid");
		String yx = this.getPara("yx");

		Makeorder M = this.getModel(Makeorder.class, "dao");

		Yx yxr = Yx.dao.findById(yx);
		Lt lt = Lt.dao.findById(ltid);

		// for get data
		Map<String, String> m = new HashMap<String, String>();
		int num = Integer.parseInt(this.getPara("num"));
		for (int i = 0; i < num; i++) {
			Record f = Db.findFirst("select * from field where id=?",
					this.getPara("key_" + i));
			Record Mr = Db.findFirst("select * from material where id=?",
					this.getPara("value_" + i));
			m.put(f.getStr("name"), Mr.getStr("name"));
		}

		m.put("品类", yxr.getStr("name"));

		int orderpersonnum = Integer.parseInt(this
				.getPara("orderpersondatanum"));
		for (int i = 0; i < orderpersonnum; i++) {
			m.put(Conts.orderpersondata.get(i).get("data").toString(),
					this.getPara("opk_" + i));
		}

		JSONObject js = new JSONObject(m);
		String s = js.toString();

		M.setData(lt.getStr("data"));
		M.setUser(Integer.parseInt(userid));
		M.setYxdata(s);
		M.setLtdouser(lt.getInt("douser"));
		M.setYx(yxr.getId());
		
		//sava lt pic
		M.setLtpic1(lt.getPic1());
		if(lt.getPic2()!=null || !lt.getPic2().equals("")){
			M.setLtpic2(lt.getPic2());
		}
		if(lt.getPic3()!=null || !lt.getPic3().equals("")){
			M.setLtpic3(lt.getPic3());
		}
		
		
		M.setOrderCreaterid(this.Login_Admin_user_Data().getInt("id"));
		if(power_sp()){
			M.setOrderCreaterPrentid(Login_Admin_user_Data().getInt("sp_prent"));
		}
		
		if(power_lt()){
			M.setOrderCreaterPrentid(Login_Admin_user_Data().getInt("lt_prent"));
		}
		

		M.save();

		this.redirect("/admin/makeorder");
	}
	
	/**
	 * 更新生产订单信息
	 */
	public void updatemarkorder() {

		setData();
		if(!(power_sp() || power_lt()|| power_qjd())){
			this.setAttr("js", "alert('没有权限！');");
			this.render("index.html");
			return;
		}
		

		Makeorder M = this.getModel(Makeorder.class, "dao");


		// for get data
		Map<String, String> m = new HashMap<String, String>();
		int num = Integer.parseInt(this.getPara("num"));
		for (int i = 0; i < num; i++) {
			Record f = Db.findFirst("select * from field where id=?",
					this.getPara("key_" + i));
			Record Mr = Db.findFirst("select * from material where id=?",
					this.getPara("value_" + i));
			m.put(f.getStr("name"), Mr.getStr("name"));
		}

		m.put("品类", this.getPara("yxdataname"));

		int orderpersonnum = Integer.parseInt(this
				.getPara("orderpersondatanum"));
		for (int i = 0; i < orderpersonnum; i++) {
			m.put(Conts.orderpersondata.get(i).get("data").toString(),
					this.getPara("opk_" + i));
		}

		JSONObject js = new JSONObject(m);
		String s = js.toString();

		
		M.setYxdata(s);
		M.setTime(new Date());
		
		
		M.setId(Integer.parseInt(this.getPara("id")));
		if(power_sp()){
			M.setOrderCreaterPrentid(Login_Admin_user_Data().getInt("sp_prent"));
		}
		
		if(power_lt()){
			M.setOrderCreaterPrentid(Login_Admin_user_Data().getInt("lt_prent"));
		}
		

		M.update();

		this.redirect("/admin/showmakeorder?id="+this.getPara("id"));
	}
	

	/**
	 * 生产订单详情
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void showmakeorder() {
		setData();

		Makeorder m = Makeorder.dao.findById(this.getPara("id"));

		setAttr("r", m);
		Yx yx = Yx.dao.findById(m.getYx());

		JSONObject js = new JSONObject(m.getYxdata());

		Iterator it = js.keys();

		List<Map<String, String>> data = new ArrayList<Map<String, String>>();

		List<Field> mlist = Field.dao.find("select * from field where prent=?",yx.getId());
		
		List<String> mlist1 = new ArrayList<String>(); ;
		for(Field mate : mlist){
			mlist1.add(mate.getName());
		}
		while (it.hasNext()) {
			Map<String, String> map = new HashMap<String, String>();
			String key = it.next().toString();
			Sys.out(key);
			if(!mlist1.contains(key)){
				map.put("name", key);
				map.put("value", js.getString(key));
				data.add(map);
			}
			
			
		}
		
		if(yx!=null){
			for(Field mate : mlist){
				Map<String, String> map = new HashMap<String, String>();
				Sys.out(mate.getName());
				if(!js.isNull(mate.getName())){
					map.put("name", mate.getName());
					map.put("value", js.getString(mate.getName()));
					data.add(map);
				}
			}
		}

		this.setAttr("data", data);

		js = new JSONObject(m.getData());

		it = js.keys();

//		List<Map<String, String>> ltdata = new ArrayList<Map<String, String>>();
//
//		while (it.hasNext()) {
//
//			Map<String, String> map = new HashMap<String, String>();
//			String key = it.next().toString();
//			Sys.out(key);
//			map.put("name", key);
//			map.put("value", js.getString(key));
//			ltdata.add(map);
//		}
		
		List<Map<String,Object>> ltdata = new ArrayList<Map<String,Object>>();
		
		JSONArray ja = new JSONArray(PropKit.get("persondatatemp"));
		StringBuffer datas = new StringBuffer();
		for (int i = 0; i < ja.length(); i++) {
			JSONObject js1 = (JSONObject) ja.get(i);
			 datas.append("/"+js1.getString("data"));
			System.out.println(datas);
		}
		
		char a = 'a';
		String [] ss = {};
		for (int i = 0; i < Conts.type.length; i++) {
			String s = PropKit.get("typetemp" + (char) (a + i))+datas.toString();
			ss = s.split("/");
			Sys.out(s);
		}
		
		

		for (String s : ss) {
			Map<String, Object> map = new HashMap<String, Object>();
			if( js.has(s)){
				
				map.put("name", s);
				map.put("value", js.get(s));
				ltdata.add(map);
			}
		}
		
		this.setAttr("admin_user", this.getSessionAttr("admin_user"));

		this.setAttr("ltdata", ltdata);
		
		this.setAttr("ltdouser", Admin.dao.findById(m.getLtdouser()));
		
		this.setAttr("fc1", Db.find("select * from admin where power='fc' and id=?",m.getFc()));

		this.setAttr("fc", Db.find("select * from admin where power='fc'"));
		
		this.setAttr("sp", Db.find("select * from admin where power='sp'"));

		this.render("showmakeorder.html");

	}
	
	
	/**
	 * 生产订单编辑
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	public void editshowmakeorder() {
		setData();

		Makeorder m = Makeorder.dao.findById(this.getPara("id"));

		setAttr("r", m);

		JSONObject js = new JSONObject(m.getYxdata());

		Iterator it = js.keys();

		List<Map<String, String>> data = new ArrayList<Map<String, String>>();

		String yx="";
		while (it.hasNext()) {

			Map<String, String> map = new HashMap<String, String>();
			String key = it.next().toString();
			Sys.out(key);
			map.put("name", key);
			map.put("value", js.getString(key));
			data.add(map);
			if(key.equals("品类")){
				yx = js.getString(key);
				this.setAttr("yxdataname", yx);
			}
		}

		this.setAttr("data", data);

		js = new JSONObject(m.getData());

		it = js.keys();
		
		List<Map<String,Object>> ltdata = new ArrayList<Map<String,Object>>();
		
		JSONArray ja = new JSONArray(PropKit.get("persondatatemp"));
		StringBuffer datas = new StringBuffer();
		for (int i = 0; i < ja.length(); i++) {
			JSONObject js1 = (JSONObject) ja.get(i);
			 datas.append("/"+js1.getString("data"));
			System.out.println(datas);
		}
		
		char a = 'a';
		String [] ss = {};
		for (int i = 0; i < Conts.type.length; i++) {
			String s = PropKit.get("typetemp" + (char) (a + i))+datas.toString();
			ss = s.split("/");
			Sys.out(s);
		}
		
		

		for (String s : ss) {
			Map<String, Object> map = new HashMap<String, Object>();
			if( js.has(s)){
				
				map.put("name", s);
				map.put("value", js.get(s));
				ltdata.add(map);
			}
		}
		
		
		Record r = Db.findFirst("select * from yx where name=?", yx);
		List<Field> f = Field.dao.find("select * from field where prent=?",
				r.get("id"));
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (Field son : f) {

			List<Material> M = Material.dao.find(
					"select * from Material where prent=?", son.get("id"));

			List<Material> lm = new ArrayList<Material>();

			for (Material sonson : M) {
				lm.add(sonson);
			}
			Map<String, Object> m1 = new HashMap<String, Object>();
			m1.put("name", son.get("name"));
			m1.put("id", son.get("id"));
			m1.put("list", lm);
			list.add(m1);
		}
		this.setAttr("mlist", list);
		this.setAttr("orderpersondata", Conts.orderpersondata);
		
		this.setAttr("admin_user", this.getSessionAttr("admin_user"));

		this.setAttr("ltdata", ltdata);

		this.setAttr("ltdouser", Admin.dao.findById(m.getLtdouser()));

		this.setAttr("ltdouserall", Db.find("select * from admin where power='lt' or power='sp'"));
		
		this.setAttr("fc1", Db.find("select * from admin where power='fc' and id=?",m.getFc()));

		this.setAttr("fc", Db.find("select * from admin where power='fc'"));
		
		this.setAttr("sp", Db.find("select * from admin where power='sp'"));

		this.render("editshowmakeorder.html");

	}
	
	
	
	/**
	 * orderinvalid
	 * @author hero
	 * @Email:mahaojie299@163.com
	 * 
	 * 已加权限
	 * 
	 */
	public void orderinvalid(){
		RJson x = new RJson();
		if(!(power_sp())){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			String id = this.getPara("id");
			Makeorder m = Makeorder.dao.findById(id);
			m.setState(Key.make_invalid);
			m.update();
			x.setCode(200);
			x.setMsg("状态更改成功！");
		}
		this.renderJson(x);
		
	}
	
	/**
	 * order pay
	 * @author hero
	 * @Email:mahaojie299@163.com
	 * 
	 * 已加权限
	 * 
	 */
	public void orderpay(){
		RJson x = new RJson();
		if(!(power_sp() || power_lt())){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			String id = this.getPara("id");
			String payment = this.getPara("payment");
			Makeorder m = Makeorder.dao.findById(id);
			m.setPayee(((Record)this.getSessionAttr("admin_user")).getInt("id"));
			m.setPayment(payment);
			m.setPaytime(new Date());
			m.setState(Key.make_orderpay);
			m.update();
			x.setCode(200);
			x.setMsg("收款成功！");
		}
		this.renderJson(x);
		
	}
	
	public void passmakeorderexamine(){
		RJson x = new RJson();
		if(!power_qjd()){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			String id = this.getPara("id");
			double price;
			
			try{
				price = Double.parseDouble(this.getPara("price"));
				Makeorder m = Makeorder.dao.findById(id);
				
				if(m.getOrderCreaterPrentid()!=Login_Admin_user_Data().getInt("id")){
					x.setCode(500);
					x.setMsg("非本店订单,没有权限修改！");
					this.renderJson(x);
				}
				
				
				m.setState(Key.make_passexamine);
				m.setPrice(price);
				m.update();
				x.setCode(200);
				x.setMsg("已通过审核！");
			}catch (Exception e) {
				x.setCode(500);
				x.setMsg("金额必须为数字！");
			}
		}
		
		this.renderJson(x);
	}
	
	public void makeing(){
		RJson x = new RJson();
		if(!power_gl()){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			String id = this.getPara("id");
			String fc = this.getPara("fc");
			Makeorder m = Makeorder.dao.findById(id);
			m.setFc(Integer.parseInt(fc));
			m.setState(Key.make_makeing);
			m.update();
			x.setCode(200);
			x.setMsg("订单状态已更正为生产中！");
		}
		
		this.renderJson(x);
	}
	
	
	public void makeed(){
		RJson x = new RJson();
		if(!power_gl()){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			String id = this.getPara("id");
			Makeorder m = Makeorder.dao.findById(id);
			m.setState(Key.make_makeed);
			m.update();
			x.setCode(200);
			x.setMsg("订单状态已更正为生产完毕！");
		}
		
		this.renderJson(x);
	}
	
	
	public void shipped(){
		RJson x = new RJson();
		if(!power_gl()){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			String id = this.getPara("id");
			Makeorder m = Makeorder.dao.findById(id);
			m.setState(Key.make_shipped);
			m.setKddh(this.getPara("kddh"));
			m.update();
			x.setCode(200);
			x.setMsg("订单状态更改为已发货至店铺！");
		}
		this.renderJson(x);
	}
	
	
	public void delivered(){
		RJson x = new RJson();
		String id = this.getPara("id");
		Makeorder m = Makeorder.dao.findById(id);
		if(!power_qjd()  && Login_Admin_user_Data().get("id")!=m.getShop() ){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			m.setState(Key.make_delivered);
			m.update();
			x.setCode(200);
			x.setMsg("订单状态更改为实体店收到货物！");
		}
		this.renderJson(x);
	}
	
	public void lastdelivered(){
		RJson x = new RJson();
		String id = this.getPara("id");
		Makeorder m = Makeorder.dao.findById(id);
		if(!(power_sp()||power_qjd()) && Login_Admin_user_Data().get("id")!=m.getShop() ){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			m.setState(Key.make_lastdelivered);
			m.update();
			x.setCode(200);
			x.setMsg("订单状态更改为店铺已发货至客户或客户收到货物！");
		}
		this.renderJson(x);
	}
	
	//需要发送短信告知门店地址
	public void repair(){
		RJson x = new RJson();
		String id = this.getPara("id");
		Makeorder m = Makeorder.dao.findById(id);
		if(!power_qjd()){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			m.setState(Key.make_repair);
			m.setTag(1);
			m.setRemark(this.getPara("remark"));
			m.update();
			
			x.setCode(200);
			x.setMsg("订单状态更改为返修中！");
		}
		this.renderJson(x);
	}
	
	
	
	
	public void repairshopdelivered(){
		RJson x = new RJson();
		String id = this.getPara("id");
		Makeorder m = Makeorder.dao.findById(id);
		if(!(power_sp()||power_qjd()) && Login_Admin_user_Data().get("id")!=m.getShop() ){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			m.setState(Key.make_repairing);
			m.update();
			x.setCode(200);
			x.setMsg("店铺收到返修货物订单状态更改为维修中！请门店跟踪进行后续处理。");
		}
		this.renderJson(x);
	}
	
	
	public void repaired(){
		RJson x = new RJson();
		String id = this.getPara("id");
		Makeorder m = Makeorder.dao.findById(id);
		if(!power_qjd() && Login_Admin_user_Data().get("id")!=m.getShop() ){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			m.setState(Key.make_repaired);
			m.update();
			x.setCode(200);
			x.setMsg("店铺收到返修完成衣服！请门店联系客户取货或快递给客户。");
		}
		this.renderJson(x);
	}
	
	
	
	
	
	public void repairdelivered(){
		RJson x = new RJson();
		String id = this.getPara("id");
		Makeorder m = Makeorder.dao.findById(id);
		if(!(power_sp()||power_qjd()) && Login_Admin_user_Data().get("id")!=m.getShop() ){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			m.setRemark(this.getPara("remark"));
			m.setState(Key.make_repairdelivered);
			m.update();
			x.setCode(200);
			x.setMsg("订单状态更改为维修结束！");
		}
		this.renderJson(x);
	}
	

	
	
	
	
	
	
	
	
	//需要发送短信告知门店地址
	public void agenrepair(){
		RJson x = new RJson();
		String id = this.getPara("id");
		Makeorder m = Makeorder.dao.findById(id);
		if(!power_qjd()){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			m.setState(Key.make_agenrepair);
			m.setRemark(this.getPara("remark"));
			m.setTag(1);
			m.update();
			
			x.setCode(200);
			x.setMsg("订单状态更改为重做中！");
		}
		this.renderJson(x);
	}
	
	
	//添加备注，目前是生产管理员和旗舰店店长能添加备注
	public void addonclick(){
		RJson x = new RJson();
		String id = this.getPara("id");
		Makeorder m = Makeorder.dao.findById(id);
		if(!(power_sp()||power_qjd())){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			m.setRemark(this.getPara("remark"));
			
			m.update();
			x.setCode(200);
			x.setMsg("添加成功！");
		}
		this.renderJson(x);
	}
	
	
	
	
	public void agenrepairshopdelivered(){
		RJson x = new RJson();
		String id = this.getPara("id");
		Makeorder m = Makeorder.dao.findById(id);
		if(!(power_sp()||power_qjd()) && Login_Admin_user_Data().get("id")!=m.getShop() ){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			m.setState(Key.make_agenrepairing);
			m.update();
			x.setCode(200);
			x.setMsg("店铺收到返修货物订单状态更改为重做中！请门店跟踪进行后续处理。");
		}
		this.renderJson(x);
	}
	
	public void editltdata(){
		setData();
		
		String id = this.getPara("id");
		Record r = Db.findById("lt", id);
		
		String lt = r.getStr("data");
		
		for(int i =1;i<4;i++){
			
			if(!"".equals(r.getStr("pic"+i))){
				this.setAttr("pic"+i,r.getStr("pic"+i));
			}
			
		}
		
		
		JSONObject js = new JSONObject(lt);
		
		String sa[] = JSONObject.getNames(js);
		
		
		JSONArray ja = new JSONArray(PropKit.get("persondatatemp"));
		StringBuffer datas = new StringBuffer();
		for (int i = 0; i < ja.length(); i++) {
			JSONObject js1 = (JSONObject) ja.get(i);
			 datas.append("/"+js1.getString("data"));
			System.out.println(datas);
		}
		
		char a = 'a';
		String [] ss1 = {};
		for (int i = 0; i < Conts.type.length; i++) {
			String s = PropKit.get("typetemp" + (char) (a + i))+datas.toString();
			ss1 = s.split("/");
			Sys.out(s);
		}
		System.out.println(ss1);
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> list1 = new ArrayList<Map<String,Object>>();
		
			
		for(String s : ss1){
			Map<String,Object> m = new HashMap<String,Object>();
			Map<String,Object> m1 = new HashMap<String,Object>();
			
			System.out.println(s);
			int tag = 0;
			for(Map<String,Object> ss : Conts.persondata){
				if(ss.get("data").equals(s)){
					tag = 1;
					m1.put("key", s);
					if(!js.isNull(s))
						m1.put("value", js.get(s));
					else
						m1.put("value", "");
					list1.add(m1);
				}
			}
			
			if(tag==1)
				continue;
			
			m.put("key", s);
			if(!js.isNull(s))
				m.put("value", js.get(s));
			else
				m.put("value", "");
			list.add(m);
		}
		this.setAttr("persondata", Conts.persondata);
		this.setAttr("persondatalist",list1);
		this.getAttr("msg");
		this.setAttr("ltdata", list);
		this.setAttr("id", id);
		this.render("editltdata.html");
		
	}
	
	
	
	public void imageUpload(){
		List<UploadFile> u = this.getFiles();
		JSONObject json = new JSONObject();
		File uploadFile = null;
		for(int i = 0; i<u.size();i++){
			uploadFile = u.get(i).getFile(); // 最大上传20M的图片
			// 异步上传时，无法通过uploadFile.getFileName()获取文件名
	        String fileName = uploadFile.getName();
	        //fileName = fileName.substring(fileName.lastIndexOf("\\") + 1); // 去掉路径

	        // 异步上传时，无法通过File source = uploadFile.getFile();获取文件
	        File source = new File(PathKit.getWebRootPath() + "/upload/" + fileName); // 获取临时文件对象

	        String extension = fileName.substring(fileName.lastIndexOf("."));
	        String savePath = PathKit.getWebRootPath() + "/upload";
	        

	        if (".png".equals(extension) || ".jpg".equals(extension)
	                || ".gif".equals(extension) || "jpeg".equals(extension)
	                || "bmp".equals(extension)) {
	            fileName = CommonUtils.getCurrentTime() + extension;

	            try {
	                FileInputStream fis = new FileInputStream(source);

	                File targetDir = new File(savePath);
	                if (!targetDir.exists()) {
	                    targetDir.mkdirs();
	                }

	                File target = new File(targetDir, fileName);
	                if (!target.exists()) {
	                    target.createNewFile();
	                }

	                FileOutputStream fos = new FileOutputStream(target);
	                byte[] bts = new byte[1024 * 20];
	                while (fis.read(bts, 0, 1024 * 20) != -1) {
	                    fos.write(bts, 0, 1024 * 20);
	                }
	                
	                try {
	                    Thread.sleep(1000);              
	                } catch(InterruptedException ex) {
	                    Thread.currentThread().interrupt();
	                }   
	                fos.close();
	                fis.close();
	                json.put("code"+i, 200);
	                json.put("src"+i, "/upload/" + fileName); // 相对地址，显示图片用
	                source.delete();

	            } catch (FileNotFoundException e) {
	                json.put("code"+i, 500);
	                json.put("message"+i, "上传出现错误，请稍后再上传");
	            } catch (IOException e) {
	            	json.put("code"+i, 500);
	                json.put("message"+i, "文件写入服务器出现错误，请稍后再上传");
	            }
	        } else {
	            source.delete();
	            json.put("code"+i, 500);
	            json.put("message"+i, "只允许上传png,jpg,jpeg,gif,bmp类型的图片文件");
	        }
		}
        

        renderJson(json.toString());
    }
	
	
	public void showltdata(){
		setData();
		String id = this.getPara("id");
		Record r = Db.findById("lt", id);

		
		String lt = r.getStr("data");
		StringBuffer sb = new StringBuffer();
		for(int i =1;i<4;i++){
			
			if(!"".equals(r.getStr("pic"+i))){
				sb.append(r.getStr("pic"+i)).append(",");
			}
			
		}
		String img[] = sb.toString().split(",");
		Lt userr1 = Lt.dao.ltByid(id);
		
		
		JSONObject js = new JSONObject(lt);
		
		//用新数据，旧数据暂时搁置
		//String sa[] = JSONObject.getNames(js);
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<String> pic = new ArrayList<String>();
		
		for (String s : img) {
			pic.add(s);
			
		}
		
		JSONArray ja = new JSONArray(PropKit.get("persondatatemp"));
		StringBuffer datas = new StringBuffer();
		for (int i = 0; i < ja.length(); i++) {
			JSONObject js1 = (JSONObject) ja.get(i);
			 datas.append("/"+js1.getString("data"));
			System.out.println(datas);
		}
		
		char a = 'a';
		String [] ss = {};
		for (int i = 0; i < Conts.type.length; i++) {
			String s = PropKit.get("typetemp" + (char) (a + i))+datas.toString();
			ss = s.split("/");
			Sys.out(s);
		}
		
		

		for (String s : ss) {
			Map<String, Object> map = new HashMap<String, Object>();
			if( js.has(s)){
				
				map.put("key", s);
				map.put("value", js.get(s));
				list.add(map);
			}
		}
			
//		for(String s : sa){
////			int tag = 0;
////			for(Map ss : Conts.persondata){
////				if(ss.get("data").equals(s)){
////					tag = 1;
////				}
////			}
////			
////			if(tag==1)
////				continue;
//			Map<String,Object> m = new HashMap<String,Object>();
//			m.put("key", s);
//			m.put("value", js.get(s));
//			list.add(m);
//		}
		
		this.setAttr("ltdata", list);
		this.setAttr("pic", pic);
		this.setAttr("id", id);
		this.setAttr("userr1", userr1.getUser());
		this.render("showltdata.html");
		
	}
	
	
	public void savaeditltdata(){
		//String id = this.getPara("id");
		int num = this.getParaToInt("num1");
		Record r = Db.findById("lt", this.getPara("id"));
		JSONObject js = new JSONObject(r.getStr("data"));
		for(int i=0;i<num;i++){
			String key = this.getPara("key_"+i);
			
			String value = this.getPara("value_"+i);
			js.put(key, value);
		}
		
		if(!(power_sp()||power_lt())){
			this.redirect("/admin/editltdata?id="+r.get("id"));
		}else{
			r.set("id", Db.findFirst("select max(id) +1 as id from lt").get("id"));
			r.set("time", new Date());
			r.set("data",js.toString());
			r.set("pic1",this.getPara("pic1"));
			r.set("pic2",this.getPara("pic2"));
			r.set("pic3",this.getPara("pic3"));
			Db.save("lt", r);
			// find old order data
			BaseOrders<Orders> orders = Orders.dao.ordersByid(Lt.dao.ltByid(this.getPara("id")).getOrdersid().toString());
			// update examine state to end
			orders.set("state", Key.orders_end);
			orders.update();
			this.redirect("/admin/showltdata?id="+r.get("id"));
		}
	}
	
	
	public void agenrepaired(){
		RJson x = new RJson();
		String id = this.getPara("id");
		Makeorder m = Makeorder.dao.findById(id);
		if(!power_qjd() && Login_Admin_user_Data().get("id")!=m.getShop() ){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			m.setState(Key.make_agenrepaired);
			m.update();
			x.setCode(200);
			x.setMsg("店铺收到重做完成衣服！请门店联系客户取货或快递给客户。");
		}
		this.renderJson(x);
	}
	
	
	
	
	
	public void agenrepairdelivered(){
		RJson x = new RJson();
		String id = this.getPara("id");
		Makeorder m = Makeorder.dao.findById(id);
		if(!power_qjd() && Login_Admin_user_Data().get("id")!=m.getShop() ){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			m.setRemark(this.getPara("remark"));
			m.setState(Key.make_agenmakedelivered);
			m.update();
			x.setCode(200);
			x.setMsg("订单状态更改为重做结束！");
		}
		this.renderJson(x);
	}
	
	
	public void m_ordersend(){
		RJson x = new RJson();
		String id = this.getPara("id");
		Makeorder m = Makeorder.dao.findById(id);
		if(!power_qjd()){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			m.setState(Key.make_end);
			m.update();
			x.setCode(200);
			x.setMsg("订单状态更改为订单结束！");
		}
		this.renderJson(x);
	}

	public void returngoods(){
		RJson x = new RJson();
		String id = this.getPara("id");
		Makeorder m = Makeorder.dao.findById(id);
		if(!power_qjd()){
			x.setCode(500);
			x.setMsg("没有权限！");
		}else{
			m.setState(Key.make_returngoods);
			m.update();
			x.setCode(200);
			x.setMsg("订单状态更改为订单结束，请尽快将客户账户信息记录，提交至财务！");
		}
		this.renderJson(x);
	}
	
	@Clear
	public void showmake() {
		setData();

		Makeorder m = Makeorder.dao.findById(this.getPara("id"));
		
		

		setAttr("r", m);
		
		Yx yx = Yx.dao.findById(m.getYx());
		
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		JSONObject js = new JSONObject(m.getYxdata());
		Iterator it = js.keys();
		
		List<Field> mlist = Field.dao.find("select * from field where prent=?",yx.getId());
		
		List<String> mlist1 = new ArrayList<String>(); ;
		for(Field mate : mlist){
			mlist1.add(mate.getName());
		}
		while (it.hasNext()) {
			Map<String, String> map = new HashMap<String, String>();
			String key = it.next().toString();
			Sys.out(key);
			if(!mlist1.contains(key)){
				map.put("name", key);
				map.put("value", js.getString(key));
				data.add(map);
			}
			
			
		}
		
		if(yx!=null){
			for(Field mate : mlist){
				Map<String, String> map = new HashMap<String, String>();
				Sys.out(mate.getName());
				if(!js.isNull(mate.getName())){
					map.put("name", mate.getName());
					map.put("value", js.getString(mate.getName()));
					data.add(map);
				}
			}
		}
		
		this.setAttr("data", data);
		
		
		

		js = new JSONObject(m.getData());

		it = js.keys();

		List<Map> ltdata = new ArrayList<Map>();

//		while (it.hasNext()) {
//
//			Map<String, String> map = new HashMap<String, String>();
//			String key = it.next().toString();
//			Sys.out(key);
//			
//			map.put("name", key);
//			map.put("value", js.getString(key));
//			ltdata.add(map);
//		}
		JSONArray ja = new JSONArray(PropKit.get("persondatatemp"));
		StringBuffer datas = new StringBuffer();
		for (int i = 0; i < ja.length(); i++) {
			JSONObject js1 = (JSONObject) ja.get(i);
			 datas.append("/"+js1.getString("data"));
			System.out.println(datas);
		}
		
		char a = 'a';
		String [] ss = {};
		for (int i = 0; i < Conts.type.length; i++) {
			String s = PropKit.get("typetemp" + (char) (a + i))+datas.toString();
			ss = s.split("/");
			Sys.out(s);
		}
		
		

		for (String s : ss) {
			Map<String, Object> map = new HashMap<String, Object>();
			if( js.has(s)){
				
				map.put("name", s);
				map.put("value", js.get(s));
				ltdata.add(map);
			}
		}
		
		this.setAttr("admin_user", this.getSessionAttr("admin_user"));
		
		this.setAttr("ltdata", ltdata);
		
		this.setAttr("fc1", Db.find("select * from admin where power='fc' and id=?",m.getFc()));
		
		this.setAttr("fc", Db.find("select * from admin where power='fc'"));

		this.render("showmake.html");

	}
	
	public void ltdata(){
		setAttr("list", Db.find("select * from lt where user=? order by time desc",this.getPara("id")));
		setData();
		this.render("ltdata.html");
	}
	
	public void makedatebyuser(){
		setAttr("list", Db.find("select * from makeorder where user=?",this.getPara("user_id")));
		setData();
		this.render("makeorderbyuser.html");
	}
	
	
	/*
	 * 以下为位置管理
	 */
	public void position(){
		String prent = "0";
		
		if(this.getPara("prent")!=null&&!"0".equals(this.getPara("prent"))){
			prent = this.getPara("prent");
			Position p = Position.dao.findById(prent);
			String title = "";
			title = p.getName()+"-"+getPositionGradeName(getPositionGrade(prent));
			this.setAttr("name", title);
		}
		
		
		setAttr("grade", getPositionGrade(prent));
		setAttr("position", Db.find("select * from position where prent=?",prent));
		setData();
		this.render("position.html");
	}
	
	public void addposition(){
		String id = this.getPara("prent");
		String title = "";
		if(id.equals("0"))
			title = "添加省级";
		else{
			Position p = Position.dao.findById(id);
			title = p.getName()+"-"+getPositionGradeName(getPositionGrade(id));
		}
		
		
		System.out.println("区域等级："+(getPositionGrade(id)+1));
		this.setAttr("grade",getPositionGrade(id)+1);
		this.setAttr("qjdlist", Db.find("select * from admin where power='qjd'"));
		this.setAttr("name", title);
		this.setAttr("prent", id);
		setData();
		this.render("addposition.html");
	}
	
	public void editposition(){
		setData();
		this.setAttr("grade",getPositionGrade(this.getPara("id")));
		this.setAttr("r", Db.findFirst("select * from position where id=?",this.getPara("id")));
		this.setAttr("qjdlist", Db.find("select * from admin where power='qjd'"));
		this.render("editposition.html");
	}
	
	public void doeditposition(){
		Position p = this.getModel(Position.class,"dao");
		p.update();
		this.redirect("/admin/position?prent="+p.getPrent());
	}
	
	public void doaddposition(){
		Position p = this.getModel(Position.class,"dao");
		p.save();
		this.redirect("/admin/position?prent="+p.getPrent());
	}
	
	public void delposition(){
		Position.dao.deleteById(this.getPara("id"));
		this.redirect("/admin/position");
	}
	private int getPositionGrade(String id){
		int grade = -1;
		if(!id.equals("0")){
			Position p = Position.dao.findById(id);
			for(grade = 0;p.getPrent()!=0;grade++){
				p = Position.dao.findById(p.getPrent());
			}
		}
		return grade;
	}
	
	
	private String getPositionGradeName(int i){
		String s = "";
		switch (i) {
		case -1:
			s = "省级";
			break;
			
		case 0:
			s = "地级市";
			break;

		case 1:
			s = "区域";
			break;
			
		case 2:
			s = "自主分区";
			break;
			
		default:
			s = "";
			break;
		}
		return s;
	}
	
	
	
	
	
	/*
	 * 以下为新功能     布料的最后更新状态
	 * @author hero
	 * @Email:mahaojie299@163.com
	 */
	
	public void requirement(){
		
		setData();
		this.render("requirement.html");
	}
	
	public void searchrequirement(){
		String search = this.getPara("search");
		List<Material> l = Material.dao.find("select * from material where name like ? or name like ? or name like ?",
				"%"+search,search+"%","%"+search+"%");
		this.renderJson(l);
	}
	
	public void subrequirement(){
		String id = this.getPara("id");
		
		Record r = this.getSessionAttr("admin_user");
		int user_id = r.getInt("id");
		Requirement req = new Requirement();
		req.setAdminuserId(user_id);
		req.setMateId(Integer.parseInt(id));
		
		req.save();
		
		
		System.out.println("userid为"+user_id+"的后台用户申请id为"+id+"的面料状态");
				
		Map<String,Object> m = new HashMap<String,Object>();
		m.put("code", 200);
		
		this.renderJson(m);
	}
		
	public void showrequirement(){
		Record r = this.getSessionAttr("admin_user");
		int user_id = r.getInt("id");
		Date d = new Date();
		d.setTime(d.getTime() - 20*60*1000);
		List<Requirement> l = Requirement.dao.find("select m.name as name,r.state as state,r.time as time from requirement as r,material as m where r.mate_id=m.id and time>? and adminuser_id=?",d,user_id);
		this.setAttr("list", l);
		setData();
		this.render("showrequirement.html");
	}
	
	public void examinerequirement(){
		List<Requirement> l = Requirement.dao.find("select r.id as id, m.name as name,r.time as time,u.name as user_name from requirement as r,material as m,admin as u where r.mate_id=m.id and r.adminuser_id=u.id and r.state=0");
		this.setAttr("list", l);
		setData();
		this.render("examinerequirement.html");
	}
	
	public void doexaminerequirement(){
		int id = this.getParaToInt("id");
		int state = this.getParaToInt("state");
		
		Requirement req = Requirement.dao.findById(id);
		
		req.setState(state);
		req.setTime(new Date());
		
		req.update();
		
		Map<String,Object> m = new HashMap<String,Object>();
		m.put("code", 200);
		
		this.renderJson(m);	
	}
	@Clear
	public void excelout(){
        // Demo1();  
          
        Date d=new Date();  
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");  
        String title=dateFormat.format(d);  
        System.out.println(title);
        File file = new File(title+"_统计表"+".xls");
        JSONArray head = new JSONArray(this.getPara("head"));
        JSONArray dataList = new JSONArray(this.getPara("dataList"));
        System.out.println(head);
        System.out.println(dataList);
        
        String heads = head.toString();
        String s[] = heads.substring(1,heads.length()-1).replace("\"", "").split(",");
        
        List<String[]> list = new ArrayList<String[]>();
        String dataLists = dataList.toString();
        String data[] = dataLists.substring(1,dataLists.length()-1).replace("\"", "").split(",");
        list.add(data);
        
        saveFile(s,list,file);
        System.out.println(file.getAbsolutePath());
        renderFile(file);
    }
	private FileOutputStream excelStream; // 输出流变量

	public FileOutputStream getExcelStream() {
		return excelStream;
	}
    //新增一行就累加  
    private int count = 0;  
  
    public  void saveFile(String [] s,List<String[]> list,File file) {  
        // 创建工作薄  
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();  
        // sheet:一张表的简称  
        // row:表里的行  
        // 创建工作薄中的工作表  
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("表格");  
        // 创建行  
        HSSFRow row = hssfSheet.createRow(0);  
        // 创建单元格，设置表头 创建列  
        HSSFCell cell = null;  
        //便利表头  
        for (int i = 0; i < s.length; i++) {  
            //创建传入进来的表头的个数  
            cell = row.createCell(i);  
            //表头的值就是传入进来的值  
            cell.setCellValue(s[i]);  
  
        }  
        //新增一个行就累加  
        row = hssfSheet.createRow(++count);  
//        C3p0Plugin c3p0Plugin = new C3p0Plugin("jdbc:mysql://127.0.0.1/weijia",  
//                "root", "root", "com.mysql.jdbc.Driver");  
//  
//        c3p0Plugin.start();  
//        // 配置ActiveRecord插件  
//        ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);  
//        arp.addMapping("weijia_user", User.class);  
//        arp.start();  
  
        // 得到所有记录 行：列  
        
        String[] record = null;  
  
        if (list != null) {  
            //获取所有的记录 有多少条记录就创建多少行  
            for (int i = 0; i < list.size(); i++) {  
                //row = hssfSheet.createRow(++count);  
                // 得到所有的行 一个record就代表 一行  
                record = list.get(i);  
                //在有所有的记录基础之上，便利传入进来的表头,再创建N行  
                for (int j = 0; j < s.length; j++) {  
                    cell = row.createCell(j);  
                    //把每一行的记录再次添加到表头下面 如果为空就为 "" 否则就为值  
                    cell.setCellValue(record[j]==null?"":record[j].toString());  
                }  
            }  
        }  
        try {  
            FileOutputStream fileOutputStreane = new FileOutputStream(file);
            hssfWorkbook.write(fileOutputStreane);
            fileOutputStreane.flush();
            fileOutputStreane.close();
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } 
    }  
	

	
	
	
	/*
	 * 权限相关
	 */
	private Record Login_Admin_user_Data(){
		return this.getSessionAttr("admin_user");
	}
	
	private boolean power_gl(){
		if(!Login_Admin_user_Data().get("power").equals("gl")){
			return false;
		}else{
			return true;
		}
	}
	
	private boolean power_lt(){
		if(!Login_Admin_user_Data().get("power").equals("lt")){
			return false;
		}else{
			return true;
		}
	}
	
	private boolean power_sp(){
		if(!Login_Admin_user_Data().get("power").equals("sp")){
			return false;
		}else{
			return true;
		}
	}
	
	private boolean power_qjd(){
		if(!Login_Admin_user_Data().get("power").equals("qjd")){
			return false;
		}else{
			return true;
		}
	}
	
	private boolean power_kf(){
		if(!Login_Admin_user_Data().get("power").equals("kf")){
			return false;
		}else{
			return true;
		}
	}
	
	private boolean power_su(){
		if(!Login_Admin_user_Data().get("power").equals("su")){
			return false;
		}else{
			return true;
		}
	}

	
	private void setPageUtils(String para){
		String urlParas = "";
		if (this.getPara(para) != null && !this.getPara(para).equals("")) {
			urlParas = "?"+para+"="+this.getPara(para);
		}
		this.setAttr("urlParas", urlParas);
	}
	
	
	public void yxtree(){
		List<Object> l =  new ArrayList<Object>();
		
		for(Yx x : Yx.dao.find("select * from yx")){
			Map<String,Object> m = new HashMap<String, Object>();
			m.put("id", x.getId());
			m.put("name", x.getName());
			m.put("ml", x.getIsprice());
			m.put("tree", Field.dao.find("select * from field where prent=?",x.getId()));
			l.add(m);
		}
		
		this.renderJson(l);
	}
	
	
	public void wlorgyutil(){
		setData();
		if(power_su())
			this.render("wlorgyutil.html");
		else{
			this.setAttr("js", "alert('没有权限！');location.href='/admin/cms'");
			
			}
	}
	
	
	public void selectwlorgy(){
		List l = Db.find("select a.id, a.name ,a.price,b.name as fieldname ,b.id as fieldid ,c.name as yx,c.id as yxid  from material as a,field as b, yx as c where a.prent=b.id and b.prent=c.id and a.name=?",this.getPara("name"));
		this.renderJson(l);
	}
	
	public void updatewlorgyprice(){
		String id = this.getPara("id");
		String price = this.getPara("price");
		
		
		Db.update("update material set price=? where id=?",price,id);
		this.renderText("ok");
	}
	
	public void addmaterial(){
		this.getModel(Material.class,"dao").save();
		this.renderText("ok");
	}
	
	

}