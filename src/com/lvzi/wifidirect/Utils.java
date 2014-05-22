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
//            // ������Ϣ���ͳ�����ʱ���ִ��Handler���������  
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
//			 *����TCPsocket
//			System.out.println("�½�������");
//			ServerSocket serverSocket = new ServerSocket();
//			serverSocket.setReuseAddress(true);
//			serverSocket.bind(new InetSocketAddress(PORT)); 
//			Socket client = serverSocket.accept();
//			client.setReuseAddress(true);
//			client_IP=client.getRemoteSocketAddress();
//			System.out.println("Utils�еĵ�ַ--->"+client_IP);
//			serverSocket.close();
//			*/
//			//����UDPsocket
//			if(ds==null){
//				 ds = new DatagramSocket(null);
//				 ds.setReuseAddress(true);
//				 ds.bind(new InetSocketAddress(PORT));
//				 byte[] buf=new byte[100];
//		          DatagramPacket dp=new DatagramPacket(buf,100);//��������Ϊ100�����ݽ��հ�
//		          ds.receive(dp);
//		          client_IP=dp.getAddress().getHostAddress();
//		          System.out.println("�½���������õĵ�ַ---->"+client_IP);
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
                 // ִ����Ϻ��handler����һ������Ϣ  
                 try {  
                	 /*
                     // ʵ����Socket  
                     Socket socket = new Socket(HOST, PORT);  
                     // ���������  
                     BufferedReader br = new BufferedReader(  
                             new InputStreamReader(socket.getInputStream()));  
//                     line = br.readLine();  
                     System.out.println("�ѷ�");
                     System.out.println("�½��ͻ��˵�socket");
                     br.close();  
                     */
                	 DatagramSocket ds=new DatagramSocket();
                	 String str="hello";
                     DatagramPacket dp=new DatagramPacket(str.getBytes(),str.length(),
                              InetAddress.getByName(HOST),
                              PORT);
                     ds.send(dp);
                     System.out.println("�ѷ�");
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
