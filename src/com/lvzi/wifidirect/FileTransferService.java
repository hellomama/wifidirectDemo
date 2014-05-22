// Copyright 2011 Google Inc. All Rights Reserved.

package com.lvzi.wifidirect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.w3c.dom.ls.LSException;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService {

	private static final int SOCKET_TIMEOUT = 5000;
	public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE";
	public static final String EXTRAS_FILE_PATH = "file_url";
	public static final String EXTRAS_ADDRESS = "go_host";
	public static final String EXTRAS_PORT = "go_port";

	public FileTransferService(String name) {
		super(name);
	}

	public FileTransferService() {
		super("FileTransferService");
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {

		Context context = getApplicationContext();
		if (intent.getAction().equals(ACTION_SEND_FILE)) {
			String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
			System.out.println("地址为："+fileUri);
			long len =new File(fileUri).length();
			System.out.println("long ----"+len);
//			///解析fileUri，判断是否为音频，图片
			String lastName=null;
				lastName=intent.getExtras().getString("LastName");
			String host = intent.getExtras().getString(EXTRAS_ADDRESS);
			long addr =intent.getExtras().getLong("addr");
			Socket socket = new Socket();
			
			int port = intent.getExtras().getInt(EXTRAS_PORT);
 System.out.println("ip地址-----？"+host+":"+port);
			try {
				socket.setReuseAddress(true);
				Log.d(WiFiDirectActivity.TAG, "Opening client socket - ");
				socket.bind(null);
				socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                System.out.println("新建接收socket，用来发送 ");
				Log.d(WiFiDirectActivity.TAG, "Client socket - " + socket.isConnected());
				OutputStream stream = socket.getOutputStream();
				ContentResolver cr = context.getContentResolver();
				InputStream is = null;
				lastName = lastName+"."+String.valueOf(addr);
				System.out.println("ssssssssss"+lastName);
				Writer writer = new OutputStreamWriter(stream);  
			      writer.write(lastName+"\n");
			      writer.flush();
				try {
					is = cr.openInputStream(Uri.parse(fileUri));
				} catch (FileNotFoundException e) {
					Log.d(WiFiDirectActivity.TAG, e.toString());
				}
				DeviceDetailFragment.copyFile(is, stream);
				Log.d(WiFiDirectActivity.TAG, "Client: Data written");
			} catch (IOException e) {
				Log.e(WiFiDirectActivity.TAG, e.getMessage());
				System.out.println(e.toString());
			} finally {
				if (socket != null) {
					if (socket.isConnected()) {
						try {
							socket.close();
						} catch (IOException e) {
							// Give up
							e.printStackTrace();
						}
					}
				}
			}

		}
	}
}
