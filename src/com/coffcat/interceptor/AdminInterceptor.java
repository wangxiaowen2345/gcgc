package com.coffcat.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.plugin.activerecord.Record;

public class AdminInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {

		System.out.println("Before Admin invoking " + inv.getActionKey());

		Record m = inv.getController().getSessionAttr("admin_user");

		if (m == null) {
			inv.getController().redirect("/admin/login");
		} else {
			inv.invoke();
		}

		System.out.println("After Admin invoking " + inv.getActionKey());

	}

}
