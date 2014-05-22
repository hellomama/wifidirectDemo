package com.lvzi.wifidirect;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.conn.util.InetAddressUtils;

import android.os.Handler;
import android.os.Message;

public class Utils {

	private final static String p2pInt = "p2p-p2p0";
	private static final int PORT = 8888;
	static String client_IP;
	private static Socket socket = null;
	private static String result=null;
    private static Handler myhandler;


	public static String getLocalIPAddress() {
		
		
		 try {  
	            String ipv4;  
	            ArrayList<NetworkInterface>  nilist = Collections.list(NetworkInterface.getNetworkInterfaces());  
	            for (NetworkInterface ni: nilist)   
	            {  
	                ArrayList<InetAddress>  ialist = Collections.list(ni.getInetAddresses());  
	                for (InetAddress address: ialist){  
	                    if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4=address.getHostAddress()))   
	                    {   
	                        return ipv4;  
	                    }  
	                }  
	   
	            }  
	   
	        } catch (SocketException ex) {  
	            System.out.println(ex.toString());
	        }  
	        return null;  
		    }

//	public static String Create_Server (){
	  
//		final Handler myhandler = new Handler() {  
//            @Override  
//            // 当有消息发送出来的时候就执行Handler的这个方法  
//            public void handleMessage(Message msg) {  
//            switch (msg.what) {
//			case 1:
//				result=(String)msg.obj;
//				break;
//
//			default:
//				break;
//			}
//            }  
//        }; 
		 
//		new Thread() {  
//            @Override  
//            public void run() { 
//            	DatagramSocket ds = null;
//            	myhandler =new Handler();
//		     try {
//			/*
//			 *建立TCPsocket
//			System.out.println("新建服务器");
//			ServerSocket serverSocket = new ServerSocket();
//			serverSocket.setReuseAddress(true);
//			serverSocket.bind(new InetSocketAddress(PORT)); 
//			Socket client = serverSocket.accept();
//			client.setReuseAddress(true);
//			client_IP=client.getRemoteSocketAddress();
//			System.out.println("Utils中的地址--->"+client_IP);
//			serverSocket.close();
//			*/
//			//建立UDPsocket
//			if(ds==null){
//				 ds = new DatagramSocket(null);
//				 ds.setReuseAddress(true);
//				 ds.bind(new InetSocketAddress(PORT));
//				 byte[] buf=new byte[100];
//		          DatagramPacket dp=new DatagramPacket(buf,100);//创建长度为100的数据接收包
//		          ds.receive(dp);
//		          client_IP=dp.getAddress().getHostAddress();
//		          System.out.println("新建服务器获得的地址---->"+client_IP);
//		          Message msg = new Message(); 
//		          msg.what=1;
//					msg.obj = client_IP;  
//		          myhandler.sendMessage(msg);
//		          ds.close();
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//            }  
//        }.start();
//		return result;
//        
//	}
	public static void Create_client(final String HOST){
		
		 new Thread() {  
             @Override  
             public void run() {  
                 // 执行完毕后给handler发送一个空消息  
                 try {  
                	 /*
                     // 实例化Socket  
                     Socket socket = new Socket(HOST, PORT);  
                     // 获得输入流  
                     BufferedReader br = new BufferedReader(  
                             new InputStreamReader(socket.getInputStream()));  
//                     line = br.readLine();  
                     System.out.println("已发");
                     System.out.println("新建客户端的socket");
                     br.close();  
                     */
                	 DatagramSocket ds=new DatagramSocket();
                	 String str="hello";
                     DatagramPacket dp=new DatagramPacket(str.getBytes(),str.length(),
                              InetAddress.getByName(HOST),
                              PORT);
                     ds.send(dp);
                     System.out.println("已发");
                     ds.close();
                 } catch (UnknownHostException e) {  
                     // TODO Auto-generated catch block  
                     e.printStackTrace();  
                 } catch (IOException e) {  
                     // TODO Auto-generated catch block  
                     e.printStackTrace();  
                 }  
             }  
         }.start(); 
          
	}
//	public static class MyHandler extends Handler {
//		String result=null;
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 1:
//				result=(String)msg.obj;
//				break;
//
//			default:
//				break;
//			}
//		}
//		
//	}
}
