package com.coffcat.index;

import java.util.List;

import com.coffcat.common.model.Makeorder;
import com.coffcat.interceptor.FcInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class FcController extends BaseController{

	@Before(FcInterceptor.class)
	public void index(){
		
		Record r = this.getSessionAttr("admin_fc_user");
		
		String s = "";
		
		if(null==this.getPara("type")){
			this.setAttr("type", 0);
		}else{
			if(this.getPara("type").equals("1")){
				s = " and state=10 ";
			}else if(this.getPara("type").equals("2")){
				s = " and state<>10 ";
			}else{
				s = "";
			}
			this.setAttr("type", this.getPara("type"));
		}
		
		
		Page<Makeorder> list = Makeorder.dao.paginate(this.getParaToInt(0, 1),20, "select * ","from makeorder where fc=? "+s+" order by id desc",r.get("id"));
		
		this.setAttr("page", list);
		
		this.render("fcindex.html");
	}
}
