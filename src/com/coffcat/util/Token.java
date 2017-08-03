package com.coffcat.util;

import com.oreilly.servlet.Base64Decoder;
import com.oreilly.servlet.Base64Encoder;

public class Token {

	public static String gettoken(){
		long t = System.currentTimeMillis();
		String s = t+"";
		String r = "";
		for(int i=0;i<s.length();i++){
			char c = (char) (s.charAt(i)+66);
			r+=c;
		}
		
		return Base64Encoder.encode(r);		
	}
	
	public static Boolean verify(String s){
		String r = Base64Decoder.decode(s);
		String ss = "";
		
		for(int i=0;i<r.length();i++){
			char c = (char) (r.charAt(i)-66);
			ss+=c;
		}
		
		Long l = Long.parseLong(ss);
		long e = System.currentTimeMillis()-l;
		int sss = (int) (e/1000);
		if(sss>60)
			return false;
		else
			return true;
	}
	
	public static void main(String[] args) {
		System.out.println(verify(gettoken()));
	}
}
