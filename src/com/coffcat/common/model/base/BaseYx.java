package com.coffcat.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseYx<M extends BaseYx<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setType(java.lang.Integer type) {
		set("type", type);
	}

	public java.lang.Integer getType() {
		return get("type");
	}

	public void setIsprice(java.lang.Integer isprice) {
		set("isprice", isprice);
	}

	public java.lang.Integer getIsprice() {
		return get("isprice");
	}

	public void setIshave(java.lang.String ishave) {
		set("ishave", ishave);
	}

	public java.lang.String getIshave() {
		return get("ishave");
	}

}