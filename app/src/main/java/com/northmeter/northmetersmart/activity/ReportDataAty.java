package com.northmeter.northmetersmart.activity;

import java.util.List;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.adp.ReportData_ListviewAdp;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.db.DBReportData;
import com.northmeter.northmetersmart.model.ReportData_Model;

/**消息及报警记录*/
public class ReportDataAty extends BaseActivity implements OnClickListener,OnItemClickListener{
	private ListView listview1;
	private List<ReportData_Model> models;
	private DBReportData drd;
	private ReportData_ListviewAdp reportData_ListviewAdp;
	private ReceiveTool receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reportdata_aty);
		init_view();
		init_listview();
		RegisterBroad();

	}
	private void init_view(){
		findViewById(R.id.button_back).setOnClickListener(this);
		findViewById(R.id.edit_view).setOnClickListener(this);
		listview1 = (ListView) findViewById(R.id.listview1);
		drd = new DBReportData(this);
	}
	
	private void init_listview(){
		models = drd.GetReportData_Models();
		reportData_ListviewAdp = new ReportData_ListviewAdp(this, models);
		listview1.setAdapter(reportData_ListviewAdp);
		listview1.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try{
			switch(v.getId()){
			case R.id.button_back:
				finish();
				break;
			case R.id.edit_view:
				drd.Delete();
				models.clear();
				reportData_ListviewAdp.notifyDataSetChanged();
				break;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	
	public void RegisterBroad(){
		//界面更新广播接收;
		new Thread(){
			public void run(){
			receiver = new ReceiveTool();
			IntentFilter intentfilter = new IntentFilter();  
			intentfilter.addAction("Intent.ReportData");  
			registerReceiver(receiver, intentfilter); 
			}
		}.start();	

	}
	
	class ReceiveTool extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			try{
				String message = intent.getStringExtra("msg");
				ReportData_Model  model = new ReportData_Model();
				model.setTime(message.split("#")[0]);
				model.setReportData(message.split("#")[1]);
				models.add(model);
				reportData_ListviewAdp.notifyDataSetChanged();
			}catch(Exception e){
				e.printStackTrace();
			}

		}
		
	}
	
	
	
}
