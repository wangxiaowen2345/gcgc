package com.coffcat.util;

import com.coffcat.common.config.Conts;

public class Sys {

	public static void out(Object o) {
		if (Conts.test)
			System.out.println(o);
	}

}
