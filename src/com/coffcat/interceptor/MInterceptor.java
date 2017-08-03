package com.coffcat.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.plugin.activerecord.Record;

public class MInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {

		System.out.println("Before M invoking " + inv.getActionKey());

		Record m = inv.getController().getSessionAttr("user");

		if (m == null) {
			String url = inv.getController().getRequest().getRequestURI();
			
			inv.getController().setSessionAttr("redirect", url);
			
			inv.getController().redirect("/m/personal");
		} else {
			inv.invoke();
		}

		System.out.println("After M invoking " + inv.getActionKey());

	}

}
