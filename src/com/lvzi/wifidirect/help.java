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
    
    private String content="ʹ��˵����\n" +
		"1��ʹ��ǰ��ȷ������豸��֧��wifiֱ���ģ�������ʾ��Available��\n" +
		"2����Ҫ������豸ͬʱ������Ͻ�����ͼ�������ֿ����豸\n" +
		"3�����֡����������ϣ��յ����ļ�������Ϊlvzi���ļ�����\n";
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
		// ����������ӿ�
	        adView.setAdListener(new AdViewLinstener() {
	            
	            @Override
	            public void onSwitchedAd(AdView arg0) {
	                Log.i("YoumiSample", "������л�");
	            }
	            
	            @Override
	            public void onReceivedAd(AdView arg0) {
	                Log.i("YoumiSample", "������ɹ�");
	                
	            }
	            
	            @Override
	            public void onFailedToReceivedAd(AdView arg0) {
	                Log.i("YoumiSample", "������ʧ��");
	            }
	        });
//	      ���ز岥��Դ
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
