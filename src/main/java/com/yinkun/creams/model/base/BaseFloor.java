package com.yinkun.creams.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseFloor<M extends BaseFloor<M>> extends Model<M> implements IBean {

	public M setFloorId(java.lang.String floorId) {
		set("floor_id", floorId);
		return (M)this;
	}
	
	public java.lang.String getFloorId() {
		return getStr("floor_id");
	}

	public M setFloor(java.lang.String floor) {
		set("floor", floor);
		return (M)this;
	}
	
	public java.lang.String getFloor() {
		return getStr("floor");
	}

	public M setBuildingId(java.lang.String buildingId) {
		set("building_id", buildingId);
		return (M)this;
	}
	
	public java.lang.String getBuildingId() {
		return getStr("building_id");
	}

	public M setBuildingName(java.lang.String buildingName) {
		set("building_name", buildingName);
		return (M)this;
	}
	
	public java.lang.String getBuildingName() {
		return getStr("building_name");
	}

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

	public M setRemark(java.lang.String remark) {
		set("remark", remark);
		return (M)this;
	}
	
	public java.lang.String getRemark() {
		return getStr("remark");
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
