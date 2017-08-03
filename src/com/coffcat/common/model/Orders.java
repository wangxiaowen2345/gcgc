package com.coffcat.common.model;

import com.coffcat.common.model.base.BaseOrders;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Orders extends BaseOrders<Orders> {
	public static final Orders dao = new Orders();
	public Orders ordersByid(String id){
		Orders orders= dao.findFirst("select * from orders where id=?",id);
		return orders;
	}
}