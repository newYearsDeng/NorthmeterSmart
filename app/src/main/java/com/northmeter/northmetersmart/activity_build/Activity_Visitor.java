package com.northmeter.northmetersmart.activity_build;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.renderscript.Type;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity.AirQualityQetectorAty;
import com.northmeter.northmetersmart.activity.MessageAty;
import com.northmeter.northmetersmart.activity.MessageAty_Center;
import com.northmeter.northmetersmart.activity.TVAty;
import com.northmeter.northmetersmart.activity.TVAty_Four_street;
import com.northmeter.northmetersmart.activity.TVAty_ZGB;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.camera.activity.CaptureActivity;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.order.Type_Entity;

/**访客房间*/
public class Activity_Visitor extends BaseActivity implements OnClickListener,OnItemClickListener{
	private GridView gridview_visitor;
	private Room_GridviewAdp roomAdapter;
	private List<RoomModel> models;
	private int width;
	private final static int SCANNIN_GREQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.activity_visitor);
			DisplayMetrics  dm = new DisplayMetrics();  
	        //取得窗口属性  
	        getWindowManager().getDefaultDisplay().getMetrics(dm);  
	        //窗口的宽度  
	        width = dm.widthPixels;
	        init_view();
	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void init_view(){
		try{
			gridview_visitor = (GridView) findViewById(R.id.gridview_visitor);
			gridview_visitor.setOnItemClickListener(this);
			findViewById(R.id.img_back).setOnClickListener(this);//返回
			findViewById(R.id.imageview_add).setOnClickListener(this);//添加按钮
			models = new ArrayList<RoomModel>();
			
			models = URL_help.getInstance().getRoomModels();

			roomAdapter = new Room_GridviewAdp(Activity_Visitor.this);
			roomAdapter.setRoom_GridviewAdp(models);
			gridview_visitor.setAdapter(roomAdapter);
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.img_back://返回
			finish();
			break;
		case R.id.imageview_add://扫描
			//开始扫描二维码，得到的信息会存储在 str_scan_result
			Intent intent = new Intent();
			intent.setClass(this, CaptureActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			break;
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		try{
			RoomModel model = models.get(arg2);
			switch(model.getType()){
			case Type_Entity.Gateway_type://zigbee网关
				Intent intent1 = new Intent(this,TVAty_ZGB.class);
				intent1.putExtra("mac", model.getTableNum());
				intent1.putExtra("name", model.getName());
				intent1.putExtra("type", model.getType());
				intent1.putExtra("buildid","");
				intent1.putExtra("roleid", Type_Entity.visitor);
				startActivity(intent1);
				break;
			case Type_Entity.Split_air_conditioning://分体空调控制器
				Intent intent2 = new Intent(this,MessageAty.class);
				intent2.putExtra("name", model.getName());
				intent2.putExtra("mac", model.getTableNum());
				intent2.putExtra("type", model.getType());
				intent2.putExtra("buildid","");
				intent2.putExtra("roleid",Type_Entity.visitor);
				startActivity(intent2);
				break;
			case Type_Entity.Central_air_conditioning://中央空调控制器
				Intent intent5 = new Intent(this,MessageAty_Center.class);
				intent5.putExtra("name", model.getName());
				intent5.putExtra("mac", model.getTableNum());
				intent5.putExtra("type", model.getType());
				intent5.putExtra("buildid","");
				intent5.putExtra("roleid",Type_Entity.visitor);
				startActivity(intent5);
				break;
			case Type_Entity.Socket_type://zigbee插座
				Intent intent3 = new Intent(this,TVAty.class);
				intent3.putExtra("name", model.getName());
				intent3.putExtra("mac", model.getTableNum());
				intent3.putExtra("type", model.getType());
				intent3.putExtra("buildid","");
				intent3.putExtra("roleid",Type_Entity.visitor);
				startActivity(intent3);
				break;
			case Type_Entity.Four_street_control://zigbee四路灯控
				Intent intent3_1 = new Intent(this,TVAty_Four_street.class);
				intent3_1.putExtra("name", model.getName());
				intent3_1.putExtra("mac", model.getTableNum());
				intent3_1.putExtra("type", model.getType());
				intent3_1.putExtra("buildid","");
				intent3_1.putExtra("roleid",Type_Entity.visitor);
				startActivity(intent3_1);
				break;
			case Type_Entity.Air_Quality_Qetector://空气质量检测仪
				Intent intent3_2 = new Intent(this,AirQualityQetectorAty.class);
				intent3_2.putExtra("name", model.getName());
				intent3_2.putExtra("mac", model.getTableNum());
				intent3_2.putExtra("type", model.getType());
				intent3_2.putExtra("buildid","");
				intent3_2.putExtra("roleid",Type_Entity.visitor);
				startActivity(intent3_2);
				break;
				}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {//0001000100010001/后勤办公楼1楼101房;0001000100010001/150721023218#0a0001aa7k#150721023218#0a0003ahup#5a4ce2e221e7;
			try{
				super.handleMessage(msg);
				Message message_msg = new Message();
				Bundle bundle = new Bundle();
				String buildMsg = msg.getData().getString("buildMsg");
				//type+";"+export_mod.getDeivce_meter()+";"+export_mod.getDeivce_meter()
				 //+";"+"show_on"+";"+elec_type+";"+export_mod.getDevice_name()+";"+version+";"+export_mod.getMasternode_num()+";"
				String [] buildList = buildMsg.split(";");
				for(int i=0;i<buildList.length/8;i++){
					RoomModel model  = new RoomModel();
					String type = null;
					switch(buildList[i*8]){
					case "3":
						type = Type_Entity.Gateway_type;
						break;
					case "4":
						type = Type_Entity.Split_air_conditioning;
						break;
					case "7":
						type = Type_Entity.Central_air_conditioning;
						break;
					case "1":
						type = Type_Entity.Socket_type;
						break;
					case "8":
						type = Type_Entity.Four_street_control;
						break;
					}
					
					
					model.setId("");
					model.setName(buildList[i*8+5]);
					model.setType(type);
					model.setTableNum(buildList[i*8+1]);
					model.setMasternode_type(Type_Entity.Gateway_type);
					model.setMasternode_num(buildList[i*8+7]);
					model.setResource(Type_Entity.getResource(type));
					model.setWidth(width);
					
					if(models.size()==0){
						models.add(model);
					}else{
						boolean flag = true;
						for(int j=0;j<models.size();j++){
							if(model.getTableNum().equals(models.get(j).getTableNum())){
								flag = false;
								break;
							}

						}
						
						if(flag){
							models.add(model);
						}else{
							Toast.makeText(Activity_Visitor.this, "设备重复添加", Toast.LENGTH_LONG).show();
						}
						
					}
					
				}
				roomAdapter.notifyDataSetChanged();
				URL_help.getInstance().setRoomModels(models);
			}catch(Exception e){
				e.printStackTrace();
				}
			}
				
		};
	
	
	/*扫面完成后关闭扫面界面返回时接收传回的数据*/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			try{
				if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_FIRST_USER) {
					if (data != null) {
						if (data.hasExtra("result")) {//设备名；设备类型；设备号；主节点类型；主节点表号；
							String buildMsg = data.getStringExtra("result").toString();
							System.out.println("requestCode::"+buildMsg);
							Message message = new Message();
			    		    Bundle bundle = new Bundle();
			    		    bundle.putString("buildMsg", buildMsg);
			    		    message.setData(bundle);
			    		    Activity_Visitor.this.handler.sendMessage(message);
						
							return;
							}
					    }
					}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			break;
		}
	}

	
}
