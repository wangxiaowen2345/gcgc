package com.coffcat.index;

import com.coffcat.common.config.Key;
import com.coffcat.common.model.base.MsgNum;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * @author hero
 * @email:mahaojie299@163.com
 * @version 创建时间：2017-1-13 上午9:40:00 程序的简单说明
 */

public class AdminMsgController extends BaseController {

	public void index() {

		Record r = (Record) this.getSessionAttr("admin_user");

		MsgNum m = new MsgNum();

		m.setAll_ing_order(Db.find(
				"select * from orders where state>? and state<?",
				Key.orders_invalid, Key.orders_end).size());

		if (r.getStr("power").equals("su")) {

			m.setExamine_order(Db.find("select * from orders where state=?",
					Key.orders_neworder).size());

			m.setSendlist_order(Db.find("select * from orders where state=?",
					Key.orders_passexamine).size());
			
			m.setLt_order(Db.find("select * from orders where state=?",Key.orders_sendlist).size());	


		}

		if (r.getStr("power").equals("gl")) {

			m.setExamine_order(Db.find("select * from orders where state=?",
					Key.orders_neworder).size());

			m.setSendlist_order(Db.find("select * from orders where state=?",
					Key.orders_passexamine).size());

		}

		if (r.getStr("power").equals("lt") || r.getStr("power").equals("sp")) {
			
			m.setLt_order(Db.find("select * from orders where state=? and pd=?",Key.orders_sendlist,r.get("id")).size());	

		}

		
		this.renderJson(m);
	}

}
