/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lvzi.wifidirect;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lvzi.wifidirect.DeviceListFragment.DeviceActionListener;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

	public static final String IP_SERVER = "192.168.49.1";
	public static int PORT = 8988;
	private static boolean server_running = false;

	protected static final int CHOOSE_FILE_RESULT_CODE = 20;
	private static View mContentView = null;
	private WifiP2pDevice device;
	private WifiP2pInfo info;
	ProgressDialog progressDialog = null;
    private static String client_IP;
//    private  ProgressBar progressBar;
    boolean flag=false;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mContentView = inflater.inflate(R.layout.device_detail, null);
		mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				WifiP2pConfig config = new WifiP2pConfig();
				config.deviceAddress = device.deviceAddress;
				config.wps.setup = WpsInfo.PBC;
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
						"Connecting to :" + device.deviceAddress, true, true
						//                        new DialogInterface.OnCancelListener() {
						//
						//                            @Override
						//                            public void onCancel(DialogInterface dialog) {
						//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
						//                            }
						//                        }
				);
				((DeviceActionListener) getActivity()).connect(config);

			}
		});

		mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						((DeviceActionListener) getActivity()).disconnect();
					}
				});
        //发送按钮
		mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// Allow user to pick an image from Gallery or other
						// registered apps
						System.out.println(" --->"+client_IP);
						
						System.out.println("客户端11111的ip----->"+client_IP);
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("*/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
					}
				});

		return mContentView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {


		
        String clientIP = null;
        if(client_IP!=null){
        	System.out.println("subString之前"+clientIP);
        clientIP=client_IP.substring(0, 13);
        System.out.println("client_IP is not null "+clientIP);
        }
        else System.out.println("client_IP is null");
        if(data!=null){
		Uri uri = data.getData();
		String LastName=null;
		long addr;
//		data.getCategories();
		//获得文件的绝对路径后转为文件后缀
		if(uri.toString().contains("content:")){
		ContentResolver cr=getActivity().getContentResolver();
		Cursor cursor=cr.query(uri, null, null, null, null);
		cursor.moveToFirst();
		LastName=cursor.getString(1);
		addr = new File(LastName).length();
		LastName=LastName.substring(LastName.lastIndexOf(".")+1);
		}else{
			LastName=uri.toString();
			addr =new File (LastName.substring(7)).length();
			System.out.println("dsds---"+LastName.substring(7));
			LastName=LastName.substring(LastName.lastIndexOf(".")+1);
		}
		System.out.println("ssssssssssssssssssss---"+LastName+"dddd"+addr);
		TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
		statusText.setText("Sending: " + uri);
		Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);
		Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
		serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
		serviceIntent.putExtra("LastName", LastName);
		serviceIntent.putExtra("addr", addr);
		serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());

		if(flag){
			serviceIntent.putExtra(FileTransferService.EXTRAS_ADDRESS, clientIP);
		}else{
			serviceIntent.putExtra(FileTransferService.EXTRAS_ADDRESS, IP_SERVER);
		}

		serviceIntent.putExtra(FileTransferService.EXTRAS_PORT, PORT);
		getActivity().startService(serviceIntent);
        }
	}

    private static Handler myHandler = new Handler(){ 
       public void dispatchMessage(Message msg) { 
            switch (msg.what) { 
            case 1: 
            	client_IP=(String)msg.obj;
    			System.out.println("hshshshsh---->"+client_IP);
                break; 
            default:System.out.println("没有");break;
            } 
        }; 
    };
	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		this.info = info;
		this.getView().setVisibility(View.VISIBLE);

		// The owner IP is now known.
		TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(getResources().getString(R.string.group_owner_text)
				+ ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
						: getResources().getString(R.string.no)));

		// InetAddress from WifiP2pInfo struct.
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());
		mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
       //如果是服务器，则创建，否则发送确认信息
		if(info.isGroupOwner == true){
			//创建代码
			System.out.println("我是服务器");
			flag=true;
			Thread th = new Thread(rb);
			 th.start();
			System.out.println("-------客户端的ip----->"+client_IP);
			
		}else{
			//发送信息代码
			flag=false;
			Utils.Create_client(info.groupOwnerAddress.getHostAddress());
			System.out.println("新建客户端");
		}
		
//		if (!server_running){
			System.out.println("在接收");
			new ServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text),mContentView.findViewById(R.id.ProgressBar01)).execute();
//			server_running = true;
//		}

		// hide the connect button
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
	}

	/**
	 * Updates the UI with device data
	 * 
	 * @param device the device to be displayed
	 */
	public void showDetails(WifiP2pDevice device) {
		this.device = device;
		this.getView().setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		view.setText(device.deviceAddress);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(device.toString());

	}

	/**
	 * Clears the UI fields after a disconnect or direct mode disable operation.
	 */
	public void resetViews() {
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.status_text);
		view.setText(R.string.empty);
		mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
		this.getView().setVisibility(View.GONE);
	}

	/**
	 * A simple server socket that accepts connection and writes some data on
	 * the stream.
	 */
	public static class ServerAsyncTask extends AsyncTask<Void, Integer, String> {

		private final Context context;
		private final TextView statusText;
		ProgressBar progressBar;

		/**
		 * @param context
		 * @param statusText
		 */
		public ServerAsyncTask(Context context, View statusText ,View progressBar) {
			this.context = context;
			this.statusText = (TextView) statusText;
			this.progressBar = (ProgressBar) progressBar;
		}

		@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(Void... params) {
			
			try {
				ServerSocket serverSocket = new ServerSocket(PORT);
				Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
				Socket client = serverSocket.accept();
				Log.d(WiFiDirectActivity.TAG, "Server: connection done");
				InputStream inputstream = client.getInputStream();
				String lastName;
				DataInputStream dis=new DataInputStream(inputstream);
				lastName=dis.readLine();
//				lastName=s.toString();
				String ss[] = lastName.split("\\.");
				System.out.println(ss.length);
				System.out.println("得到的后缀："+ss[0]);
				final File f = new File(Environment.getExternalStorageDirectory() + "/"
						+ "lvzi" + "/wifip2pshared-" + System.currentTimeMillis()
						+ "."+ss[0]);

				File dirs = new File(f.getParent());
				if (!dirs.exists())
					dirs.mkdirs();
				f.createNewFile();

				Log.d(WiFiDirectActivity.TAG, "server: copying files " + f.toString());
				
				
//				copyFile(inputstream, new FileOutputStream(f)); 
				OutputStream out = new FileOutputStream(f);
				byte buf[] = new byte[1024];
				int len;
				long total=0;
				try {
					while ((len = inputstream.read(buf)) != -1) {
						total+=len;
						publishProgress((int)total*100/Integer.parseInt(ss[1]));
						out.write(buf, 0, len);

					}
					out.close();
					inputstream.close();
				} catch (IOException e) {
					Log.d(WiFiDirectActivity.TAG, e.toString());
					
				}
				playMusic(context);
				
				serverSocket.close();
				server_running = false;
				
				return f.getAbsolutePath();
			} catch (IOException e) {
				Log.e(WiFiDirectActivity.TAG, e.getMessage());
				
				return null;
			}
		}
	

		private void playMusic(Context context) {
			// TODO Auto-generated method stub
			AssetManager assetManager =context.getAssets();
			
			try {
				
				AssetFileDescriptor fileDescriptor = assetManager.openFd("voice.mp3");
				 MediaPlayer myplayer=new MediaPlayer();
				 if(myplayer.isPlaying()==true){
						myplayer.reset();
						myplayer.release();
					}
				myplayer.setDataSource(fileDescriptor.getFileDescriptor(),
				        fileDescriptor.getStartOffset(),
				        fileDescriptor.getLength());
				myplayer.prepare();
				myplayer.start();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				statusText.setText("File copied - " + result);
//				Intent intent = new Intent();
//				intent.setAction(android.content.Intent.ACTION_VIEW);
//				intent.setDataAndType(Uri.parse("file://" + result), "image/*");
//				context.startActivity(intent);
				server_running = false;
				System.out.println("shiyonghou-----"+server_running);
				progressBar.setProgress(0);
				progressBar.setVisibility(View.INVISIBLE);
				new ServerAsyncTask(context, mContentView.findViewById(R.id.status_text),mContentView.findViewById(R.id.ProgressBar01)).execute();
			}

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			progressBar.setProgress(values[0]);
			System.out.println("进度值-----"+values[0]);
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			statusText.setText("Opening a server socket");
			progressBar.setVisibility(View.VISIBLE);
		}

	}

	public static boolean copyFile(InputStream inputStream, OutputStream out) {
		byte buf[] = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				out.write(buf, 0, len);

			}
			out.close();
			inputStream.close();
		} catch (IOException e) {
			Log.d(WiFiDirectActivity.TAG, e.toString());
			return false;
		}
		return true;
	}
	private Runnable rb = new Runnable(){
		  @Override
		  public void run(){
			  //实现run方法
			  DatagramSocket ds = null;
          	
			     try {
				if(ds==null){
					 ds = new DatagramSocket(null);
					 ds.setReuseAddress(true);
					 ds.bind(new InetSocketAddress(8888));
					 byte[] buf=new byte[100];
			          DatagramPacket dp=new DatagramPacket(buf,100);//创建长度为100的数据接收包
			          ds.receive(dp);
			          client_IP=dp.getAddress().getHostAddress();
			          System.out.println("新建服务器获得的地址---->"+client_IP);
			          Message msg = myHandler.obtainMessage(); 
			          msg.what=1;
						msg.obj = client_IP;  
			          myHandler.sendMessage(msg);
			          ds.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		  }

		};

}
