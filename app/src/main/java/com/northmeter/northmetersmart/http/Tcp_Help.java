package com.northmeter.northmetersmart.http;

import com.northmeter.northmetersmart.control.URL_help;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;


public class Tcp_Help {
	
	public static String getTcpMsg(String send) throws IOException{
		String tcpMsg = "";
		Socket socket = null;
    	//DeviceManager.getInstance().getTempDevice().getIp()//获取ip
  
			DataOutputStream mBufferedReader = null;
    		DataInputStream  mPrintWriter = null; 
    	        try {// 创建一个Socket对象，并指定服务端的IP及端口号  
    	        	System.out.println("开始连接......");
//    	        	Thread.sleep(3000);
    	        	String tcp_address = URL_help.getInstance().getTcp_address();
    				socket = getsockets(tcp_address.split(":")[0],Integer.parseInt(tcp_address.split(":")[1]));//"10.168.1.165",8010
    	        	
    	            System.out.println("socket.isClosed():"+socket.isClosed()+" socket.isConnected():"+socket.isConnected());
    	            System.out.println("连接成功......");
    	            mBufferedReader = new DataOutputStream(socket.getOutputStream());
    	            mPrintWriter    = new DataInputStream(socket.getInputStream()); 
    	            
    	            mBufferedReader.flush();
    	            System.out.println("开始发送......"); 
    	            mBufferedReader.write(send.getBytes());
    	            mBufferedReader.flush();
    	            
    	            
  	            
//    	            BufferedReader reader = new BufferedReader(
//    						new InputStreamReader(socket.getInputStream(), "UTF-8"));
//   	            char [] s = new char[1024];
//    	            int len;
//    	            while((len = reader.read(s))!=-1){
//   	            	System.out.println();
//    	            }
    	            
//    	            BufferedReader reader = new BufferedReader(
//    						new InputStreamReader(socket.getInputStream(), "UTF-8"));
//    				String inputLine;
//    				while ((inputLine = reader.readLine()) != null) {
//    					tcpMsg += inputLine;
//    				}
    	            
    	            ByteArrayOutputStream baos = new ByteArrayOutputStream();//可以动态扩展byte的大小 
    	         
    	            DataInputStream dis = new DataInputStream(socket.getInputStream());
    	            InputStream in = socket.getInputStream();
    	            byte[] b = new byte[1];  
    				while(-1 != (dis.read(b))){  
    					 if(b.length==in.available()){
     	                    break; 
     	                 }  
    	                 else {
    	                	baos.write(b); 
    	                }  
    	                	 
    	            }  
    				tcpMsg = new String(baos.toByteArray(),"UTF-8");
//    				DataInputStream dis = new DataInputStream(socket.getInputStream());
//     	            byte [] b = new byte[8094];
//     	            dis.read(b);
//     	            tcpMsg = new String(b,"utf-8");
    				System.out.println("readLine:"+tcpMsg);
    	        }finally {
    				if (socket != null)
						try {
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    			}
    	        return tcpMsg;
	}
	

	private static Socket getsockets(String ip_address,int port){
		Socket socket = null;
		System.out.println("建立连接......");
    	//DeviceManager.getInstance().getTempDevice().getIp()//获取ip
        try {// 创建一个Socket对象，并指定服务端的IP及端口号  
			socket = new Socket(ip_address, port);
			socket.setSoTimeout(15000);
			return socket;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        	
        	return null;	
	}
}