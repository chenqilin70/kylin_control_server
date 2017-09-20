package com.huwl.oracle.kylin_control_server.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.huwl.oracle.kylin_control_server.dao.UserDao;
import com.huwl.oracle.kylinremotecontrol.beans.NetMessage;
import com.huwl.oracle.kylinremotecontrol.beans.Terminal;
import com.huwl.oracle.kylinremotecontrol.beans.User;

public class MessageHandler {
	volatile static Map<Terminal,Socket> terminals=new HashMap<Terminal, Socket>();
	private static UserDao userDao=new UserDao();

	public static void handle(final Socket client) {
		LOOP :while(true){
			
			ObjectInputStream in=null;
			try {
				in=new ObjectInputStream(client.getInputStream());
			} catch (IOException e) {
				
				for(Terminal k:terminals.keySet()){
					if(client.getInetAddress().toString().equals(k.getIp())){
						terminals.remove(k);
						System.out.println(e.getMessage()+":客户端关闭了App"+k);
						break LOOP;
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
					}else if(forWhat==NetMessage.REQUEST_QR_CODE){
						requestQRCode(m,client);
					}
				}

				
			}.start();
			
		}
	}
	private static void requestQRCode(NetMessage m, Socket client) {
		Terminal terminal=(Terminal) m.getMap().get("terminal");
		String username=terminal.getUsername();
		if(username==null){
			//存下socket
			terminals.put(terminal, client);
			//要求其显示二维码
			NetMessage result=new NetMessage();
			result.setForWhat(NetMessage.PROVIDE_QR_CODE);
			result.send(client);
			
		}/*else{
			if(//Android设备已登录){
				//发送Socket，要求手机确认
					
			}else{
				//登录请求存入数据库
			}
			//返回信息要求等待手机确认
		}*/
	};
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
			terminals.put(((Terminal)m.getMap().get("terminal")),socket);
		}
		
		result.setForWhat(NetMessage.LOGIN);
		result.send(socket);
	}
}
