package com.northmeter.northmetersmart.activity;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.northmeter.northmetersmart.I.IRequestShow;
import com.northmeter.northmetersmart.I.RequestInterface;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.http.HttpRequest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


/**账户信息 */
public class UserCenterInformation extends BaseActivity implements OnClickListener,IRequestShow {


	RequestInterface requestInterface;
	private TextView user_name,user_tel,user_department,user_createtime;
	private CustomProgressDialog progressDialog;
	private String userid,dptid;
	private String userURL;
	private SharedPreferences sp1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.center_aty);
		requestInterface = new RequestInterface(this);
		init_view();
	}
	
	private void init_view(){
		userURL = URL_help.getInstance().getUser_address();
		user_name = (TextView) findViewById(R.id.user_name);
		user_tel = (TextView) findViewById(R.id.user_tel);
		user_department = (TextView) findViewById(R.id.user_department);
		user_createtime = (TextView) findViewById(R.id.user_createtime);
		findViewById(R.id.but_back_1).setOnClickListener(this);//返回
		findViewById(R.id.button_change).setOnClickListener(this);//确认
		
		progressDialog = CustomProgressDialog.createDialog(this);
		progressDialog.show();
		
		sp1 = getSharedPreferences("Customer_Infor", MODE_PRIVATE);//存储用户登录的信息，包括存储微信unionid和手机号码
		String flag = sp1.getString("flag", null);
		String cust = sp1.getString(flag, null);
		String para = flag;
		if(flag.equals("unionid")){
			para = "weixin";
		}
		requestInterface.getHttpRequestGet(userURL+"/users/getUser", "flag="+para+"&"+flag+"="+cust);//http://218.17.157.121:4003/users/getUser
	}
	
	/**
	 * Options : username, tel, dptid, dptname, userid
		username :用户姓名
		tel :电话号码
		dptid :部门ID
		dptname :部门名称
		userid : 用户ID
*/
	private void updataUserInfo(){
		 new Thread(){
	    	  public void run(){
	    		  try{
	    			  JSONObject jsonObj = new JSONObject();
    				  jsonObj.put("username", user_name.getText().toString());
    				  jsonObj.put("tel", user_tel.getText().toString());
    				  jsonObj.put("dptid", dptid);
    				  jsonObj.put("departmentname", user_department.getText().toString());
    				  jsonObj.put("userid", userid);
	    			  String jsonStr = jsonObj.toString();
	    			  System.out.println(jsonStr);
	    			  String result = HttpRequest.sendPost(userURL+"/users/updateUser", jsonStr);//http://218.17.157.121:4003/users/updateUser
	    			  System.out.println("result======"+result);
	    			  
	    			  Message message = new Message();
	    			  message.what = 1;
	    			  Bundle bundle = new Bundle();
	    			  bundle.putString("userMsg", result);
	    			  message.setData(bundle);
	    			  UserCenterInformation.this.handelr.sendMessage(message);
	    		  }catch(Exception e){
	    			 e.printStackTrace();
	    			 Message message = new Message();
	    			 message.what = 1;
	    			 Bundle bundle = new Bundle();
	    		 	 bundle.putString("userMsg", "exception");
	    			 message.setData(bundle);
	    			 UserCenterInformation.this.handelr.sendMessage(message);

	    		  }
	    		 
	    	  }
	      }.start();
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.but_back_1:
			finish();
			break;
		case R.id.button_change:
			progressDialog.show();
			updataUserInfo();
			break;
		}
		
	}

	/**
	 * [{"userid":"17","username":"dyd","tel":"17876148387","dptid":"00010001","departmentname":"系统组",
	 * "createtime":"2017-02-17 09:49:50","updatetime":null,"role":[{"buildingid":"0001","roleid":"manager"},
	 * {"buildingid":"000100010001","roleid":"observer"},{"buildingid":"00010002","roleid":"manager"},
	 * {"buildingid":"000100020001","roleid":"manager"},{"buildingid":"000100030001","roleid":"analyser"},
	 * {"buildingid":"000100030002","roleid":"controller"}]}]*/
	Handler handelr = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			try{
				super.handleMessage(msg);
				String userMsg = msg.getData().getString("userMsg");
				progressDialog.dismiss();
				if(userMsg.equals("exception")){
					Toast.makeText(UserCenterInformation.this, "网络连接异常", Toast.LENGTH_LONG).show();
					return;
				}
				
				switch(msg.what){
				case 0:
					if(!userMsg.equals("exception")){
						JSONArray json_array = JSONObject.parseArray(userMsg);
				        JSONObject msg_bject = (JSONObject) json_array.get(0);
				        userid = msg_bject.getString("userid");
				        String username = msg_bject.getString("username");
				        String tel = msg_bject.getString("tel");
				        dptid = msg_bject.getString("dptid");
				        String departmentname = msg_bject.getString("departmentname");
				        String createtime = msg_bject.getString("createtime");
				        
				        user_name.setText(username);
				        user_tel.setText(tel.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
				        user_department.setText(departmentname);
				        user_createtime.setText(createtime);
				        
				        Editor editor = sp1.edit();
						editor.putString("userData", userMsg);
						editor.commit();
					}
					break;
				case 1:
					//{"returnCode":"300","returnMsg":"select upper info failure"}
					JSONObject jsonarray = JSONObject.parseObject(userMsg);
					Object detail = jsonarray.get("returnCode");
					if(detail.toString().equals("200")){
						Toast.makeText(UserCenterInformation.this, "更新成功", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(UserCenterInformation.this, "更新失败", Toast.LENGTH_LONG).show();
					}
					break;
				
				}
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};
	
	/**[{"userid":"17","username":"dyd","tel":"17876148387","dptid":"00010001","departmentname":"系统组",
	"createtime":"2017-02-17 09:49:50","updatetime":null,"role":[{"buildingid":"000100010001",
	"roleid":"observer"},{"buildingid":"000100020001","roleid":"manager"},
	{"buildingid":"000100030001","roleid":"analyser"},{"buildingid":"000100030002","roleid":"controller"}]}]
	 */
	@Override
	public void requestShow(String msg) {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.what = 0;
		Bundle bundle = new Bundle();
		bundle.putString("userMsg", msg);
		message.setData(bundle);
		this.handelr.sendMessage(message);
	}


}
