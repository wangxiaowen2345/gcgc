package com.coffcat.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseRequirement<M extends BaseRequirement<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setMateId(java.lang.Integer mateId) {
		set("mate_id", mateId);
	}

	public java.lang.Integer getMateId() {
		return get("mate_id");
	}

	public void setAdminuserId(java.lang.Integer adminuserId) {
		set("adminuser_id", adminuserId);
	}

	public java.lang.Integer getAdminuserId() {
		return get("adminuser_id");
	}

	public void setTime(java.util.Date time) {
		set("time", time);
	}

	public java.util.Date getTime() {
		return get("time");
	}

	public void setState(java.lang.Integer state) {
		set("state", state);
	}

	public java.lang.Integer getState() {
		return get("state");
	}

}
