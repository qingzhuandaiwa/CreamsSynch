package com.yinkun.creams.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BasePark<M extends BasePark<M>> extends Model<M> implements IBean {

	public M setParkId(java.lang.String parkId) {
		set("park_id", parkId);
		return (M)this;
	}
	
	public java.lang.String getParkId() {
		return getStr("park_id");
	}

	public M setParkName(java.lang.String parkName) {
		set("park_name", parkName);
		return (M)this;
	}
	
	public java.lang.String getParkName() {
		return getStr("park_name");
	}

	public M setIsDel(java.lang.String isDel) {
		set("is_del", isDel);
		return (M)this;
	}
	
	public java.lang.String getIsDel() {
		return getStr("is_del");
	}

	public M setCtime(java.util.Date ctime) {
		set("ctime", ctime);
		return (M)this;
	}
	
	public java.util.Date getCtime() {
		return get("ctime");
	}

	public M setUtime(java.util.Date utime) {
		set("utime", utime);
		return (M)this;
	}
	
	public java.util.Date getUtime() {
		return get("utime");
	}

}
