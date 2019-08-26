package com.northmeter.northmetersmart.activity_build;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity_build.I.IRequestShow;
import com.northmeter.northmetersmart.activity_build.I.RequestInterface;
import com.northmeter.northmetersmart.androidmenutoabhost.ListViewCompattoDisplay;
import com.northmeter.northmetersmart.androidmenutoabhost.SlideView_toDisplay;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.helper.CustomQRCodeDialog;
import com.northmeter.northmetersmart.http.HttpRequest;
import com.northmeter.northmetersmart.model.Device_Display_Model;
import com.northmeter.northmetersmart.order.Type_Entity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 设备管理
 * */
public class DeviceDisplayAty extends Activity implements OnClickListener,OnItemClickListener,IRequestShow {
	private TextView edit_view;
	private RelativeLayout relativelayout3;//最下方编辑栏
	private boolean visibility = false;
	private SlideAdapter listview_adp;
	private SlideView_toDisplay mLastSlideViewWithStatusOn;
	private List<Device_Display_Model> display_models;
	private ListViewCompattoDisplay mListView;
	private String buildid,buildname,bundle_msg;
	private CustomProgressDialog progressDialog;
	private RequestInterface requestInterface;
	private String deleteType;
	private Device_Display_Model deleteDeviceModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.device_display_aty);
			if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
	            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	        }
			
			requestInterface = new RequestInterface(this);
			
			Intent intent = getIntent();
			buildid = intent.getStringExtra("buildid");
			buildname = intent.getStringExtra("buildname");
			bundle_msg = intent.getStringExtra("bundle_msg");
				
			
			inin_view();
			init_listview(visibility);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void inin_view(){
		try{
			display_models = new ArrayList<Device_Display_Model>();
			mListView = (ListViewCompattoDisplay)findViewById(R.id.listview1);
			
			edit_view = (TextView)findViewById(R.id.edit_view);//编辑按钮
			edit_view.setOnClickListener(this);
			
			findViewById(R.id.export).setOnClickListener(this);
			
			findViewById(R.id.button_back).setOnClickListener(this);
			
			relativelayout3 = (RelativeLayout) findViewById(R.id.relativelayout3);
			
			findViewById(R.id.img_choose).setOnClickListener(this);//全选按钮
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void init_listview(boolean visibility){
		try{
			display_models.clear();
			if(bundle_msg!=null){
				String[] list = bundle_msg.split(";");
				for(int i=1;i<list.length;i++){
					String buildid = list[i].split("/")[0];
					String roommsg = list[i].split("/")[1];
					if(roommsg.split("#").length>1){
						if(roommsg.split("#")[1].equals(Type_Entity.Gateway_type)||roommsg.split("#")[1].equals(Type_Entity.Socket_type)||
								roommsg.split("#")[1].equals(Type_Entity.Four_street_control)||
								roommsg.split("#")[1].equals(Type_Entity.Split_air_conditioning)||
								roommsg.split("#")[1].equals(Type_Entity.Central_air_conditioning)){
							Device_Display_Model model  = new Device_Display_Model();
							model.setDevice_name(roommsg.split("#")[0]);
							model.setDevice_type(roommsg.split("#")[1]);
							model.setDeivce_meter(roommsg.split("#")[2]);
							model.setMasternode_type(roommsg.split("#")[3]);
							model.setMasternode_num(roommsg.split("#")[4]);
							model.setVisibility(visibility);
							display_models.add(model);

						}
					}
				
					
				}

				listview_adp = new SlideAdapter(display_models); 
				mListView.setAdapter(listview_adp);
				mListView.setOnItemClickListener(this);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		try{
			// TODO Auto-generated method stub
			switch(v.getId()){		
			case R.id.edit_view:
		    	if(edit_view.getText().equals("编辑")){
		    		edit_view.setText("取消");
		    		visibility = true;
		    		relativelayout3.setVisibility(View.VISIBLE);
		    		init_listview(visibility);
		    	}else{
		    		edit_view.setText("编辑");
		    		visibility = false;
		    		relativelayout3.setVisibility(View.GONE);
		    		init_listview(visibility);
		    		
		    	}
		    	break;
			case R.id.button_back://返回
				finish();
				break;
			 case R.id.img_choose://全选
		    	for (int i = 0; i < display_models.size(); i++) {
		    		Device_Display_Model model = display_models.get(i);
					if (model.isCheck()){
						model.setCheck(false);
					}else{
						model.setCheck(true);
					}
				}
		    	listview_adp.notifyDataSetChanged();
			    break;		
			 case R.id.export://导出
				 int totalSelected = 0;
				 String codeString = "";
				 for(int i=0;i<display_models.size();i++){
					 if(display_models.get(i).isCheck()){
						 totalSelected ++;
						 Device_Display_Model export_mod = display_models.get(i);//设备名；设备类型；设备号；主节点类型；主节点表号；
						 int type = 0,elec_type = 0;
						 String version = "0002";
						 switch(export_mod.getDevice_type()){
						 case Type_Entity.Gateway_type:
							 type = 3;
							 elec_type = 9;
							 version = "0001";
							 break;
						 case Type_Entity.Split_air_conditioning:
							 type = 4;
							 elec_type = 10;
							 version = "0003";
							 break;
						 case Type_Entity.Central_air_conditioning:
							 type = 7;
							 elec_type = 11;
							 version = "0005";
							 break;
						 case Type_Entity.Socket_type:
							 type = 1;
							 elec_type = 8;
							 version = "0002";
							 break;
						 case Type_Entity.Four_street_control:
							 type = 8;
							 elec_type = 12;
							 version = "0002_1";
							 break;
						 case Type_Entity.Air_Quality_Qetector://空气质量检测仪
							 type = 9;
							 elec_type = 12;
							 version = "0006";
							 break;
						 }
						  
						 codeString = codeString + type+";"+export_mod.getDeivce_meter()+";"+export_mod.getDeivce_meter()
								 +";"+"show_on"+";"+elec_type+";"+export_mod.getDevice_name()+";"+version+";"+export_mod.getMasternode_num()+";";
					 } 
				 }
				 
//				 for(int i=0;i<display_models.size();i++){
//					 if(display_models.get(i).isCheck()){
//						 totalSelected ++;
//						 Device_Display_Model export_mod = display_models.get(i);//设备名；设备类型；设备号；主节点类型；主节点表号；
//						 codeString = codeString + export_mod.getDevice_name()+";"+export_mod.getDevice_type()+";"
//								 +export_mod.getDeivce_meter()+";"+export_mod.getMasternode_type()+";"+export_mod.getMasternode_num()+";";
//					 } 
//				 }

				 if(totalSelected==0||totalSelected>5){
					 Toast.makeText(this, "请选择一个设备导出且不多于5个", Toast.LENGTH_LONG).show();
					 return;
				 }else{
					 Bitmap qrcode = generateQRCode(codeString);

					 // 弹出二维码对话框
					 CustomQRCodeDialog QRCodeDialog = CustomQRCodeDialog
							.createDialog(this, qrcode);
					 QRCodeDialog.show();
				 }
				 
				 break;
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			try{
				super.handleMessage(msg);
				Message message_msg=new Message();
				Bundle bundle = new Bundle();
				progressDialog.dismiss();
				String bundle_msg = msg.getData().getString("buildMsg");
				if(bundle_msg.equals("exception")){
					Toast.makeText(DeviceDisplayAty.this, "网络连接异常", Toast.LENGTH_LONG).show();
				}else{
					//{"returnCode":"300","returnMsg":"select upper info failure"}
					//JSONArray jsonarray = JSONArray.parseArray(bundle_msg);
					JSONObject obj = JSONObject.parseObject(bundle_msg);
					Object detail = obj.get("returnCode");
					if(detail.equals("200")){
						listview_adp.notifyDataSetChanged();
						Toast.makeText(DeviceDisplayAty.this, "删除成功", Toast.LENGTH_LONG).show();
						
						if(deleteType.equals("deleteMeter")){
							deleteRecord(deleteDeviceModel);
						}
					}else{
						Object errmsg = obj.get("returnMsg");
						Toast.makeText(DeviceDisplayAty.this, errmsg.toString(), Toast.LENGTH_LONG).show();

					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};
	
	private void deleteRecord(Device_Display_Model model){
		try{
			String URL = URL_help.getInstance().getUrl_address();
		    String para = "{\"collectorId\":\""+model.getMasternode_num()+"\",\"collectorType\":\""+model.getMasternode_type()+"\",\"meterCount\":\""+1+"\",\"meterId\":\""+model.getDeivce_meter()+"\"}";
			System.out.println(para);
		    String result = HttpRequest.sendPost(URL+"/deleteArchive", para);
		    System.out.println(result);
			JSONArray jsonarray = JSONArray.parseArray(result);
			JSONObject obj = JSONObject.parseObject(jsonarray.get(0).toString());
			Object detail = obj.get("errcode");
			if(detail.equals("0")){
				Toast.makeText(getApplicationContext(), "档案删除成功", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(getApplicationContext(), "档案删除失败", Toast.LENGTH_LONG).show();
			}
				
		}catch(Exception e){
			Toast.makeText(DeviceDisplayAty.this, "档案删除失败", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	
	
	/**
	 * downloadArchive	采集器下表下载档案	采集设备id，采集设备类型，设备个数，设备id
	{"collectorId":"","collectorType":"","meterCount":"","meterId":"XX,XX"} 
	deleteArchive	删除采集器下表档案	采集设备id，采集设备类型，设备个数，设备id
	{"collectorId":"","collectorType":"","meterCount":整数,"meterId":"XX"}
	 * */
	private void deleteRoomDevice(final Device_Display_Model model){
		new Thread(){ 
			public void run(){
				try{
					String URL = URL_help.getInstance().getUrl_address();
					deleteDeviceModel = model;
					if(model.getDevice_type().equals(Type_Entity.Gateway_type)){
						deleteType = "deleteCollector";
					}else{
						deleteType = "deleteMeter";
					}
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("deviceId",  model.getDeivce_meter());//编号
					jsonObj.put("deviceType", model.getDevice_type());//类别
					
					requestInterface.getHttpRequestPost(URL+"/"+deleteType, jsonObj.toString());//10.168.1.165:8800
					for(int i=0;i<display_models.size();i++){
						if(display_models.get(i).getDeivce_meter().equals(model.getDeivce_meter())){
							display_models.remove(i);
						}
					}
					
				
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		}.start();
	}
	
	
	 private class SlideAdapter extends BaseAdapter implements SlideView_toDisplay.OnSlideListener {
	    	private List<Device_Display_Model> models;
	        private LayoutInflater mInflater;
	        private String show_hide_text;
	        SlideAdapter(List<Device_Display_Model> models) {
	            super();
	            mInflater = getLayoutInflater();
	            this.models = models;
	        }

	        @Override
	        public int getCount() {
	            return models.size();
	        }

	        @Override
	        public Object getItem(int position) {
	            return models.get(position);
	        }

	        @Override
	        public long getItemId(int position) {
	            return position;
	        }

	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	        	
	            ViewHolder holder;
	            SlideView_toDisplay slideViewtoDisplay = (SlideView_toDisplay) convertView;
	            try{
	            if (slideViewtoDisplay == null) {
	                View itemView = mInflater.inflate(R.layout.device_display_item, null);

	                slideViewtoDisplay = new SlideView_toDisplay(DeviceDisplayAty.this);
	                slideViewtoDisplay.setContentView(itemView);

	                holder = new ViewHolder(slideViewtoDisplay);
	                slideViewtoDisplay.setOnSlideListener(this);
	                slideViewtoDisplay.setTag(holder);
	            } else {
	                holder = (ViewHolder) slideViewtoDisplay.getTag();
	            }
	            
	            final Device_Display_Model model = models.get(position);
	            model.slideView = slideViewtoDisplay;
	            model.slideView.shrink();
	            
	            holder.text_metre.setText(model.getDeivce_meter());
	            holder.text_name.setText(model.getDevice_name());       
	            
	    		if(model.isVisibility()){
	    			holder.display_select.setVisibility(View.VISIBLE);
	    		}else{
	    			holder.display_select.setVisibility(View.GONE);
	    		}
	    		
	    		if(model.isCheck()){
	    			holder.display_select.setChecked(true);
	    		}else{
	    			holder.display_select.setChecked(false);
	    		}
	    		
	    		holder.showHolder.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						progressDialog = CustomProgressDialog.createDialog(DeviceDisplayAty.this);
						progressDialog.show();
						new Thread() {
							@Override
							public void run(){
									try{
										deleteRoomDevice(model);
									}catch(Exception e){
										e.printStackTrace();
										}
									
								}
						}.start();
					}
				});

	        	}catch(Exception e){
	        		e.printStackTrace();
	        	}
	            return slideViewtoDisplay;
	        }

	        @Override
	        public void onSlide(View view, int status) {
	            if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
	                mLastSlideViewWithStatusOn.shrink();
	            }

	            if (status == SLIDE_STATUS_ON) {
	                mLastSlideViewWithStatusOn = (SlideView_toDisplay) view;
	            }
	        }
	        
	 }
	 


	    private static class ViewHolder {
	        public TextView text_metre;
	        public TextView text_name;
	        public TextView descript_text;
	        public CheckBox display_select;//选择项
	        public ViewGroup showHolder;
	        public TextView show_text;

	        ViewHolder(View view) {
	        	text_metre = (TextView) view.findViewById(R.id.text_metre);
	        	text_name = (TextView) view.findViewById(R.id.text_name);
	        	descript_text = (TextView) view.findViewById(R.id.descript_text);
	        	display_select = (CheckBox) view.findViewById(R.id.display_select);
	        	showHolder  = (ViewGroup)view.findViewById(R.id.holder);
	        	show_text = (TextView) view.findViewById(R.id.show_text);
	        }
	  }



		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Device_Display_Model model = display_models.get(arg2);
			if(model.isCheck()==false){
				model.setCheck(true);
			}else{
				model.setCheck(false);
			}   
			listview_adp.notifyDataSetChanged();
		}

		
		// 矩阵转换为bitmap
		private Bitmap bitMatrix2Bitmap(BitMatrix matrix) {
			int w = matrix.getWidth();
			int h = matrix.getHeight();
			int[] rawData = new int[w * h];
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					int color = Color.WHITE;
					if (matrix.get(i, j)) {
						color = Color.BLACK;
					}
					rawData[i + (j * w)] = color;
				}
			}

			Bitmap bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
			return bitmap;
		}

		// 生成二维码bitmap
		private Bitmap generateQRCode(String content) {
			try {
				DisplayMetrics dm = new DisplayMetrics();
			    getWindowManager().getDefaultDisplay().getMetrics(dm);
			    int width = (int) (dm.widthPixels*0.6);    //得到宽度 
				
				Hashtable hints = new Hashtable();
	            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
	            
				QRCodeWriter writer = new QRCodeWriter();
				// MultiFormatWriter writer = new MultiFormatWriter();
				BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE,
						width, width, hints);
				return bitMatrix2Bitmap(matrix);
			} catch (WriterException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void requestShow(String msg) {
			// TODO Auto-generated method stub
			Message message = new Message();
		    Bundle bundle = new Bundle();
		    bundle.putString("buildMsg", msg);
		    message.setData(bundle);
		    DeviceDisplayAty.this.handler.sendMessage(message);
		}
		

	
	
}
