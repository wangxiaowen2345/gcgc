package com.coffcat.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BasePower<M extends BasePower<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setPowername(java.lang.String powername) {
		set("powername", powername);
	}

	public java.lang.String getPowername() {
		return get("powername");
	}

	public void setFunctions(java.lang.String functions) {
		set("functions", functions);
	}

	public java.lang.String getFunctions() {
		return get("functions");
	}

	public void setNickname(java.lang.String nickname) {
		set("nickname", nickname);
	}

	public java.lang.String getNickname() {
		return get("nickname");
	}

}
