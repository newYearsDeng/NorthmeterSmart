package com.northmeter.northmetersmart.activity_build;


import com.alibaba.fastjson.JSONObject;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity_build.I.IRequestShow;
import com.northmeter.northmetersmart.activity_build.I.RequestInterface;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.helper.PopupHelper;
import com.northmeter.northmetersmart.http.HttpRequest;
import com.northmeter.northmetersmart.order.Type_Entity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
/**添加建筑档案*/
public class Add_BuildAty_2 extends BaseActivity implements OnClickListener,IRequestShow {
	
	private TextView text_air_type;//空调类型
	private EditText edittext_gateway_num,edittext_name,edittext_buildid;
	private TableRow tablerow_type,tablerow_gateway;
	private String buildid;//添加设备时所在建筑的建筑id
	private String type,tableNum;
	private PopupWindow popupWindowf1_p1;
	private View f1_p1View;
	private CustomProgressDialog progressDialog;
	private String first_airName="格力定频";
	private String url_msg="addMeter";
	private RequestInterface requestInterface;
	private String URL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.add_buildaty_2);
			URL = URL_help.getInstance().getUrl_address();
			init_view();
			loadPopupWindow();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void init_view(){
		try{
			requestInterface = new RequestInterface(this);
			
			Intent intent = getIntent();
			buildid = intent.getStringExtra("buildid");
			type = intent.getStringExtra("type");
			tableNum = intent.getStringExtra("tableNum");
			
			tablerow_type = (TableRow) findViewById(R.id.tablerow_type);
			tablerow_gateway = (TableRow) findViewById(R.id.tablerow_gateway);
			if(type.equals(Type_Entity.Gateway_type)){
				url_msg = "addCollector";
				tablerow_type.setVisibility(View.GONE); 
				tablerow_gateway.setVisibility(View.GONE);
			}else if(type.equals(Type_Entity.Split_air_conditioning)){
				tablerow_type.setVisibility(View.VISIBLE); 
			}
			
			findViewById(R.id.button_back).setOnClickListener(this);//返回
			findViewById(R.id.but_next).setOnClickListener(this);//完成
			text_air_type = (TextView) findViewById(R.id.text_air_type);
			text_air_type.setOnClickListener(this);
			edittext_gateway_num = (EditText) findViewById(R.id.edittext_gateway_num);//网关号
			edittext_name = (EditText) findViewById(R.id.edittext_name);//自定义名字
			edittext_buildid = (EditText) findViewById(R.id.edittext_buildid);//所在建筑id
			edittext_buildid.setText(buildid);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	 /**deviceId：采集器编号
		deviceName：采集器名称
		deviceType：采集器类别
		deviceLabel：采集器标签
		buildingId：下挂所在的建筑编号*/

	/**组装json格式的请求数据*/
	private String getSendJson(){/**{“deviceId”:“XX”,“deviceName”:“XX”,“deviceLabel”:“XX”,“buildingId”:“XX”,
									“collectorId”:“XX”,“deviceTab”:“XX”“baudrate”:“XX”,“commport”:“XX”}*/
		String jsonStr = null;
		try{
			JSONObject jsonObj = new JSONObject();
			if(type.equals(Type_Entity.Gateway_type)){//采集器的添加信息
				jsonObj.put("deviceId", tableNum);//采集器编号
				jsonObj.put("deviceName", edittext_name.getText().toString());//采集器名称
				jsonObj.put("deviceType", type);//采集器类别
				jsonObj.put("deviceLaber", "null");//采集器标签//deviceLabel
				jsonObj.put("buildingId", buildid);//下挂所在的建筑编号
			}else if(type.equals(Type_Entity.Split_air_conditioning)){//分体空调设备添加
				jsonObj.put("deviceId", tableNum);//计量设备编号  
				jsonObj.put("deviceType",type);//计量表类型
				jsonObj.put("deviceName", first_airName+"-"+edittext_name.getText().toString());//计量表名称
				jsonObj.put("deviceLaber","ac");//能源标签（ac, socket等）//deviceLabel
				jsonObj.put("buildingId", buildid);//下挂所在的建筑编号
				jsonObj.put("collectorId", edittext_gateway_num.getText().toString());//下挂所在的采集器  编号
				jsonObj.put("deviceTab", "null");//设备其他标签属性如办公，宿舍设备
				jsonObj.put("baudrate", "2400bps");//波特率
				jsonObj.put("commport", "1F");//通讯端口号
			}else{
				jsonObj.put("deviceId", tableNum);//计量设备编号  
				jsonObj.put("deviceType",type);//计量表类型
				jsonObj.put("deviceName", edittext_name.getText().toString());//计量表名称
				jsonObj.put("deviceLaber","ac");//能源标签（ac, socket等）
				jsonObj.put("buildingId", buildid);//下挂所在的建筑编号
				jsonObj.put("collectorId", edittext_gateway_num.getText().toString());//下挂所在的采集器编号
				jsonObj.put("deviceTab", "null");//设备其他标签属性如办公，宿舍设备
				jsonObj.put("baudrate", "2400bps");//波特率
				jsonObj.put("commport", "1F");//通讯端口号
			}
			jsonStr = jsonObj.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return jsonStr;
	}
	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {//0001000100010001/后勤办公楼1楼101房;0001000100010001/150721023218#0a0001aa7k#150721023218#0a0003ahup#5a4ce2e221e7;
			try{
				super.handleMessage(msg);
				progressDialog.dismiss();
				String bundle_msg = msg.getData().getString("buildMsg");
				if(bundle_msg.equals("exception")){
					Toast.makeText(Add_BuildAty_2.this, "添加失败，请稍后再试", Toast.LENGTH_LONG).show();
					return;
				}else{
					//{"returnCode":"300","returnMsg":"select upper info failure"}
					//JSONArray jsonarray = JSONArray.parseArray(bundle_msg);
					JSONObject obj = JSONObject.parseObject(bundle_msg);
					Object detail = obj.get("returnCode");
				
					if(detail.equals("200")){
						Toast.makeText(Add_BuildAty_2.this, "正在下载档案", Toast.LENGTH_LONG).show();
						if(!type.equals(Type_Entity.Gateway_type)){
							String para = "{\"collectorId\":\""+edittext_gateway_num.getText().toString()+"\",\"collectorType\":\""+Type_Entity.Gateway_type+"\",\"meterCount\":\""+1+"\",\"meterId\":\"\'"+tableNum+"\'\"}";
							String result = HttpRequest.sendPost(URL+"/downloadArchive", para);
							//JSONArray json = JSONArray.parseArray(result);
							JSONObject object = JSONObject.parseObject(result);
							Object detail_add = object.get("returnCode");
							if(detail_add.equals("200")){
								Toast.makeText(Add_BuildAty_2.this, "添加成功", Toast.LENGTH_LONG).show();
								finish();
							}else{

							}
						}else{
							Toast.makeText(Add_BuildAty_2.this, "添加成功", Toast.LENGTH_LONG).show();
							finish();
						}
					}else{
						Object errmsg = obj.get("returnMsg");
						Toast.makeText(Add_BuildAty_2.this, errmsg.toString(), Toast.LENGTH_LONG).show();
						return;
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};

	private void add_RoomDevice(){
		String jsonStr = getSendJson();
		requestInterface.getHttpRequestPost(URL+"/"+url_msg, jsonStr);//http://218.17.157.121:8821/url_msg
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try{
			switch(v.getId()){
			case R.id.button_back://返回
				finish();
				break;
			case R.id.but_next://完成
				progressDialog= CustomProgressDialog.createDialog(this);
			    progressDialog.show();
				add_RoomDevice();
				break;
			case R.id.text_air_type:
				popupWindowf1_p1.showAsDropDown(v);
				break;
			case R.id.btton_1:
				text_air_type.setText(((Button) v).getText());
				first_airName = "格力定频";
				popupWindowf1_p1.dismiss();
				break;
			case R.id.btton_2:
				text_air_type.setText(((Button) v).getText());
				first_airName = "格力变频";
				popupWindowf1_p1.dismiss();
				break;
			case R.id.btton_3:
				text_air_type.setText(((Button) v).getText());
				first_airName = "美的定频";
				popupWindowf1_p1.dismiss();
				break;
			case R.id.btton_4:
				text_air_type.setText(((Button) v).getText());
				first_airName = "奥克斯";
				popupWindowf1_p1.dismiss();
				break;
			case R.id.btton_5:
				text_air_type.setText(((Button) v).getText());
				first_airName = "华凌";
				popupWindowf1_p1.dismiss();
				break;
				}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void loadPopupWindow() {
		f1_p1View = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_buildaty_popu,null);
		popupWindowf1_p1 = new PopupHelper().getWindow_ALLWRAP(f1_p1View,this);
		f1_p1View.findViewById(R.id.btton_1).setOnClickListener(this);
		f1_p1View.findViewById(R.id.btton_2).setOnClickListener(this);
		f1_p1View.findViewById(R.id.btton_3).setOnClickListener(this);
		f1_p1View.findViewById(R.id.btton_4).setOnClickListener(this);
		f1_p1View.findViewById(R.id.btton_5).setOnClickListener(this);
	}

	@Override
	public void requestShow(String msg) {
		// TODO Auto-generated method stub
		Message message = new Message();
	    Bundle bundle = new Bundle();
	    bundle.putString("buildMsg", msg);
	    message.setData(bundle);
	    Add_BuildAty_2.this.handler.sendMessage(message);
	}
	

	
	

	
}
