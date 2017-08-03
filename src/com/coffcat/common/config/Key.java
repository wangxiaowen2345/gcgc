package com.coffcat.common.config;

/** 
 * @author hero 
 * @email:mahaojie299@163.com
 * @version 创建时间：2017-1-12 下午7:00:03 
 * 程序的简单说明 
 */

public class Key {
	
	//预约量体相关状态码
	//无效订单
	public static final int orders_invalid = -1;
	//新订单
	public static final int orders_neworder = 0;
	//通过审核
	public static final int orders_passexamine = 1;
	//改约订单
	public static final int orders_retime = 2;
	//已派单
	public static final int orders_sendlist = 3;
	//量体结束
	public static final int orders_end = 10;
	
	
	//生产订单相关状态码
	
	//已退货
	public static final int make_returngoods = -200;	
	//已重做
	public static final int make_agenrepair = -100;
	//重做中
	public static final int make_agenrepairing = -101;
	//重做结束
	public static final int make_agenrepaired = -102;
	//已返修
	public static final int make_repair = -10;
	//维修中
	public static final int make_repairing = -11;
	//维修结束
	public static final int make_repaired = -12;
	//无效订单
	public static final int make_invalid = -1;
	//新订单
	public static final int make_neworder = 0;
	//通过审核
	public static final int make_passexamine = 1;
	//已付款
	public static final int make_orderpay = 2;
	//生产中
	public static final int make_makeing = 10;
	//已生产
	public static final int make_makeed = 20;
	//已发货
	public static final int make_shipped = 30;
	//已送达
	public static final int make_delivered = 50;
	//客户取货或已发送给顾客
	public static final int make_lastdelivered = 60;
	//一次返修收货
	public static final int make_repairdelivered = 70;
	//重做收货
	public static final int make_agenmakedelivered = 80; 
	//订单结束
	public static final int make_end = 100;
	
	
	






}
