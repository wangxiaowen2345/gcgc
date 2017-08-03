package com.coffcat.common.model;

import com.jfinal.plugin.activerecord.Model;

public class User extends Model<User> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final User me = new User();

}
