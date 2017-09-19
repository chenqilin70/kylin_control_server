package com.huwl.oracle.kylin_control_server.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.huwl.oracle.kylinremotecontrol.beans.MessageHandler;

public class MainServer {
	
	
	public static void main(String[] args) {
		ServerSocket socket=null;
		try {
			socket=new ServerSocket(5544);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("服务器已开启！");
		
		while(true){
			try {
				final Socket client=socket.accept();
				new Thread(){
					public void run() {
						MessageHandler.handle(client);
					};
				}.start();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		

	}

}
