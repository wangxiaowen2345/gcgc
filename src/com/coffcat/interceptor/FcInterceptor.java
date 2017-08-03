package com.coffcat.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.plugin.activerecord.Record;

public class FcInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {

		System.out.println("Before FC invoking " + inv.getActionKey());

		Record m = inv.getController().getSessionAttr("admin_fc_user");

		if (m == null) {
			inv.getController().redirect("/admin/login");
		} else {
			inv.invoke();
		}

		System.out.println("After FC invoking " + inv.getActionKey());

	}

}
