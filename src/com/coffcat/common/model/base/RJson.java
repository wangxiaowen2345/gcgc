package com.coffcat.common.model.base;

/** 
 * @author hero 
 * @email:mahaojie299@163.com
 * @version 创建时间：2017-1-12 下午8:12:30 
 * 程序的简单说明 
 */

public class RJson {
	private int code = 0;
	private String msg = "";
	private String data = "";
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
}
