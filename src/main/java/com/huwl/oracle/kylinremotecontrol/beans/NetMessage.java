package com.huwl.oracle.kylinremotecontrol.beans;

import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class NetMessage implements Serializable{
	public static final int LOGIN=0,REGISTER=1,REQUEST_QR_CODE=3
			,PROVIDE_QR_CODE=4,VALIDATE_LOGIN=5,AUTO_LOGIN=6;
	private int forWhat;
	private User user;
	private Map map=new HashMap();;

	public int getForWhat() {
		return forWhat;
	}
	public void setForWhat(int forWhat) {
		this.forWhat = forWhat;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public Map getMap() {
		return map;
	}
	public void setMap(Map map) {
		this.map = map;
	}
	public static int getLogin() {
		return LOGIN;
	}
	public static int getRegister() {
		return REGISTER;
	}
	public void send(Socket socket){
		System.out.println("isClosed 结果为："+socket.isClosed());
		if(!socket.isClosed()){
			try {
				ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(this);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void  transformNetMessage(){
		final Map map=getMap();
		LinkedTreeMap terminalMap= (LinkedTreeMap) map.get("terminal");
		final Terminal t=new Terminal();
		if(terminalMap!=null){
			t.setIp((String) terminalMap.get("ip"));
			t.setId((String) terminalMap.get("id"));
			t.setName((String) terminalMap.get("name"));
			t.setRemark((String) terminalMap.get("name"));
			t.setSystemType((String) terminalMap.get("systemType"));
			map.put("terminal",t);
			LinkedTreeMap userMap= (LinkedTreeMap) terminalMap.get("user");
			if(userMap!=null){
				final User user=new User((String) userMap.get("userId"),(String) userMap.get("password"));
				t.setUser(user);
			}
		}
	}
}
