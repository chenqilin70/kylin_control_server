package com.huwl.oracle.kylinremotecontrol.beans;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.huwl.oracle.kylinremotecontrol.dao.UserDao;

public class MessageHandler {
	static Map<String,Socket> terminals=new HashMap<String, Socket>();
	private static UserDao userDao=new UserDao();

	public static void handle(final Socket client) {
		while(true){
			
			ObjectInputStream in=null;
			try {
				in=new ObjectInputStream(client.getInputStream());
			} catch (IOException e) {
				
				for(String k:terminals.keySet()){
					if(k.endsWith(client.getInetAddress().toString())){
						terminals.remove(k);
						System.out.println(e.getMessage()+":客户端关闭了App"+k);
					}
				}
				break;
			}
			final NetMessage m;
			NetMessage o = null;
			try {
				o=(NetMessage) in.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			}
			m=o;
			new Thread(){
				public void run() {
					
					System.out.println("接收到Message为："+m);
					int forWhat=m.getForWhat();
					if(forWhat==NetMessage.LOGIN){
						login(m,client);
					}else if(forWhat==NetMessage.REGISTER){
						register(m,client);
					}
				};
			}.start();
			
		}
	}

	private static void register(NetMessage m, Socket client) {
		User user=m.getUser();
		NetMessage result=new NetMessage();
		
		user=userDao.register(user);
		result.getMap().put("isRegister", user!=null);
		
		
		result.setForWhat(NetMessage.REGISTER);
		result.send(client);
	}

	private static void login(NetMessage m,Socket socket) {
		User user=m.getUser();
		NetMessage result=new NetMessage();
		boolean flag=userDao.login(user);
		result.getMap().put("isLogin", flag);
		if(flag){
			System.out.println("登录一个设备"+user.getUserId()+":"+socket.getInetAddress());
			terminals.put(user.getUserId()+":"+socket.getInetAddress(),socket);
		}
		
		result.setForWhat(NetMessage.LOGIN);
		result.send(socket);
	}
}
