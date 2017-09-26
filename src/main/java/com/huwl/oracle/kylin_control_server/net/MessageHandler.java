package com.huwl.oracle.kylin_control_server.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.huwl.oracle.kylin_control_server.dao.UserDao;
import com.huwl.oracle.kylinremotecontrol.beans.NetMessage;
import com.huwl.oracle.kylinremotecontrol.beans.Terminal;
import com.huwl.oracle.kylinremotecontrol.beans.User;

public class MessageHandler {
	volatile static Map<String,Set<Terminal>> ids=new HashMap(); 
	volatile static Map<Terminal,Socket> terminals=new HashMap<Terminal, Socket>();
	private static UserDao userDao=new UserDao();

	public static void handle(final Socket client) {
		LOOP :while(true){
			
			ObjectInputStream in=null;
			try {
				in=new ObjectInputStream(client.getInputStream());
			} catch (IOException e) {
				System.out.println("发现有app关闭");
				for(Terminal k:terminals.keySet()){
					System.out.println(client.getInetAddress().toString()+"==="+k.getIp());
					if(client.getInetAddress().toString().equals(k.getIp())){
						terminals.remove(k);
						User user=k.getUser();
						if(user!=null){
							Set<Terminal> ters=ids.get(user.getUserId());
							for(Terminal t:ters){
								if(t.getId()==k.getId()){
									ters.remove(t);
									if(ters.size()==0){
										ids.remove(user.getUserId());
									}
									break;
								}
							}
						}
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
					}else if(forWhat==NetMessage.AUTO_LOGIN){
						autoLogin(m,client);
					}else if(forWhat==NetMessage.VALIDATE_LOGIN){
						validateLogin(m,client);
					}
				}
			}.start();
		}
	}
	private static void addToPool(Terminal t,Socket socket,String userId){
		terminals.put(t, socket);
		
		Set<Terminal> ters=ids.get(userId);
		if(ters==null){
			System.out.println("该用户第一次登陆  创建新的Set存入");
			ters=new HashSet();
			ters.add(t);
			ids.put(userId, ters);
		}else{
			System.out.println("该用户不是第一次登陆  使用原来的Set存入");
			ters.add(t);
		}
	}
	private static void validateLogin(NetMessage m, Socket client) {
		Terminal t=(Terminal) m.getMap().get("terminal");
		Socket socket=terminals.get(t);
		terminals.remove(t);
		addToPool(t, socket, m.getUser().getUserId());
		m.send(socket);
	}
	private static void autoLogin(NetMessage m, Socket client) {
		User user=m.getUser();
		NetMessage result=new NetMessage();
		boolean flag=userDao.login(user);
		result.getMap().put("isLogin", flag);
		result.setUser(user);
		if(flag){
			System.out.println("登录一个设备"+user.getUserId()+":"+client.getInetAddress());
			Terminal t=((Terminal)m.getMap().get("terminal"));
			t.setIp(client.getInetAddress().toString());
			addToPool(t, client, m.getUser().getUserId());
		}
		result.getMap().put("otherTerminal", ids.get(m.getUser().getUserId()));
		result.setForWhat(NetMessage.AUTO_LOGIN);
		result.send(client);
	}
	private static void requestQRCode(NetMessage m, Socket client) {
		Terminal terminal=(Terminal) m.getMap().get("terminal");
		User user=terminal.getUser();
		if(terminal.getUser()==null){
			System.out.print("登陆时存下了Terminal"+terminal.getId());
			//存下socket
			terminal.setIp(client.getInetAddress().toString());
			terminals.put(terminal, client);
			//要求其显示二维码
			NetMessage result=new NetMessage();
			result.setForWhat(NetMessage.PROVIDE_QR_CODE);
			result.getMap().put("date", new Date().getTime()+"");
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
		result.setUser(user);
		if(flag){
			System.out.println("登录一个设备"+user.getUserId()+":"+socket.getInetAddress());
			Terminal t=((Terminal)m.getMap().get("terminal"));
			t.setIp(socket.getInetAddress().toString());
			addToPool(t,socket,m.getUser().getUserId());
		}
		result.getMap().put("otherTerminal", ids.get(m.getUser().getUserId()));
		result.setForWhat(NetMessage.LOGIN);
		result.send(socket);
	}
}
