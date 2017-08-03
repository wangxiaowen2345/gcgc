package com.coffcat.util;

import com.coffcat.common.config.Conts;

public class HttpSenderTest {
	public static void send(String mobile,String code) {
		String url = "http://sapi.253.com/msg/";// 应用地址
		String account = "viphh88_ykly";// 账号
		String pswd = "Hhjy1314";// 密码
		String msg = "【优客良依】您好，您的验证码是"+code;// 短信内容
		boolean needstatus = true;// 是否需要状态报告，需要true，不需要false
		String extno = null;// 扩展码

		try {
			String returnString = HttpSender.batchSend(url, account, pswd,
					mobile, msg, needstatus, extno);
			System.out.println(returnString);
			// TODO 处理返回值,参见HTTP协议文档
		} catch (Exception e) {
			// TODO 处理异常
			e.printStackTrace();
		}
	}
	
	
	public static void sendmsg(String mobile,String msg) {
		String url = "http://sapi.253.com/msg/";// 应用地址
		String account = "viphh88_ykly";// 账号
		String pswd = "Hhjy1314";// 密码
		boolean needstatus = true;// 是否需要状态报告，需要true，不需要false
		String extno = null;// 扩展码

		try {
			String returnString = HttpSender.batchSend(url, account, pswd,
					mobile, Conts.MsgTitle+msg, needstatus, extno);
			System.out.println(returnString);
			// TODO 处理返回值,参见HTTP协议文档
		} catch (Exception e) {
			// TODO 处理异常
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		HttpSenderTest.send("18701188852", ""+Rondom.getRandNum(100000, 999999));
	}
}
