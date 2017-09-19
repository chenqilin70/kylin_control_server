package com.huwl.oracle.kylinremotecontrol.beans;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class NetMessage implements Serializable{
	public static final int LOGIN=0,REGISTER=1;
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
}
