package com.northmeter.northmetersmart.fragment;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class udp_test {
	public static final String SERVERIP = "255.255.255.255"; // �㲥��ַ  
    public static final int SERVERPORT = 1093; // �˿ں�  
    static String acceptStr;
	public static String bytesToHexString(byte[] src){       

        StringBuilder stringBuilder = new StringBuilder();       
        if (src == null || src.length <= 0) {       
            return null;       
        }       
        for (int i = 0; i < src.length; i++) {       
            int v = src[i] & 0xFF;       
            String hv = Integer.toHexString(v);       
            if (hv.length() < 2) {       
                stringBuilder.append(0);
            }       
            stringBuilder.append(hv);       
        }       
        return stringBuilder.toString();       
    }
	
	public static void getUdp(){
	    	try{
	    	byte[] buf = new byte[10];
	  	  	InetAddress serverAddr = InetAddress.getByName(SERVERIP);  
	        DatagramSocket socket = new DatagramSocket();  
	        /*68 06 FF FF FF FF 81 00 EB 16*/
	        buf[0]= (byte)0x68;  buf[5] = (byte)0xFF;
	        buf[1]= (byte)0x06;  buf[6] = (byte)0x81;
	        buf[2]= (byte)0xFF;  buf[7] = (byte)0x00;
	        buf[3]= (byte)0xFF;  buf[8] = (byte)0xEB;
	        buf[4]= (byte)0xFF;  buf[9]=  (byte)0x16;
	        System.out.println("开始广播");
	        DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, SERVERPORT);  
	        socket.send(packet);  
	        System.out.println("接收数据");
	        byte[] buffer = new byte[100];  
	        DatagramPacket recvPacket = new DatagramPacket(buffer, buffer.length);  
	        socket.receive(recvPacket);  
	        InetAddress ad = recvPacket.getAddress();  
	        String s = ad.getHostAddress();  
	        acceptStr = new String(recvPacket.getData());  
	        bytesToHexString(buffer);
	        System.out.println("acceptStr:"+ bytesToHexString(buffer));
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	}
	
	public static void main(String [] args){
		
	} 
}
