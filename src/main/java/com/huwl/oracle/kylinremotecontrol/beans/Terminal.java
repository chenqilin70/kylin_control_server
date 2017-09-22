package com.huwl.oracle.kylinremotecontrol.beans;

import java.io.Serializable;

public class Terminal implements Serializable{
	private String id;
	private String name;
	private String systemType;
	private String remark;
	private User user;
	private String ip;


	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSystemType() {
		return systemType;
	}
	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}
	public Terminal(String id, String name, String systemType) {
		super();
		this.id = id;
		this.name = name;
		this.systemType = systemType;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	@Override
	public String toString() {
		return "Terminal [id=" + id + ", name=" + name + ", systemType="
				+ systemType + ", remark=" + remark + "]";
	}
	@Override
	public boolean equals(Object arg0) {
		if(arg0 instanceof Terminal){
			Terminal arg=(Terminal) arg0;
			return this.getId()==arg.getId();
		}else{
			return false;
		}
	}
	
	
	

}
