package com.lvzi.wifidirect;

import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import net.youmi.android.banner.AdViewLinstener;
import net.youmi.android.spot.SpotManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class help extends Activity{

    private TextView question_text;
    
    private String content="使用说明：\n" +
		"1、使用前请确认你的设备是支持wifi直连的，就是显示“Available”\n" +
		"2、需要传输的设备同时点击右上角搜索图标来发现可用设备\n" +
		"3、“嘀”声响后传输完毕，收到的文件放在名为lvzi的文件夹下\n";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question);
		question_text = (TextView)findViewById(R.id.question);
		question_text.setText(content);
		LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
		Button addButton = (Button) findViewById(R.id.add);
		AdManager.getInstance(this).init("597e828e33d09dd2",
				"c9218331cab237c9", false);
		
		AdView adView = new AdView(this, AdSize.SIZE_320x50);
		 adLayout.addView(adView);
		// 监听广告条接口
	        adView.setAdListener(new AdViewLinstener() {
	            
	            @Override
	            public void onSwitchedAd(AdView arg0) {
	                Log.i("YoumiSample", "广告条切换");
	            }
	            
	            @Override
	            public void onReceivedAd(AdView arg0) {
	                Log.i("YoumiSample", "请求广告成功");
	                
	            }
	            
	            @Override
	            public void onFailedToReceivedAd(AdView arg0) {
	                Log.i("YoumiSample", "请求广告失败");
	            }
	        });
//	      加载插播资源
	        SpotManager.getInstance(this).loadSpotAds();
	        addButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SpotManager.getInstance(help.this).showSpotAds(help.this);
				}
			});
	        	
	        
	        
		
	}

}
