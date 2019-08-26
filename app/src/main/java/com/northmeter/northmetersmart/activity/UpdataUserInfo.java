package com.northmeter.northmetersmart.activity;


import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mob.MobSDK;
import com.northmeter.northmetersmart.I.IRequestShow;
import com.northmeter.northmetersmart.I.RequestInterface;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.wxpay.Constants;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**绑定用户微信号和手机号码*/
public class UpdataUserInfo extends BaseActivity implements OnClickListener,IRequestShow {
	private static final String MOB_APP_KEY = "1b4769ff38434";
	private static final String MOB_secret = "84e9c3d6c4d4213d832b36e8f1eae458";
	private EditText edit_tel,edit_code;
	private Button to_get_code;
	private CustomProgressDialog progressDialog;
	private int i = 60;
	private RequestInterface requestInterface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.updata_userinfo);
            initView();
			reg_smssdk();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

    @Override
    public void initView() {
        super.initView();
		requestInterface = new RequestInterface(this);
		progressDialog=CustomProgressDialog.createDialog(this);
		
		findViewById(R.id.but_back_1).setOnClickListener(this);//返回
		findViewById(R.id.login_button).setOnClickListener(this);//下一步
		
		to_get_code = (Button) findViewById(R.id.to_get_code);//获取验证码
		to_get_code.setOnClickListener(this);
		
		edit_tel = (EditText) findViewById(R.id.edit_tel);//手机号输入
		edit_tel.addTextChangedListener(textWatcher);
		edit_code = (EditText) findViewById(R.id.edit_code);//验证码输入
	}
	
	 /**监听edittext输入变化时间*/
    private TextWatcher textWatcher = new TextWatcher() {  
    	  
    	@Override  
    	public void onTextChanged(CharSequence s, int start, int before,  
    	int count) {  

    		}  
    	  
    	@Override  
    	public void beforeTextChanged(CharSequence s, int start, int count,  
    	int after) {  
  
    	  
    		}  
    	  
		@Override  
    	public void afterTextChanged(Editable s) {
    		if(edit_tel.getText().toString().length()==11){
    			to_get_code.setTextColor(Color.parseColor("#4bbdec"));
    		}else{
    			to_get_code.setTextColor(Color.WHITE);
    		}  
    	}  
    };  
    	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try{
			switch(v.getId()){
			case R.id.but_back_1:
				finish();
				break;
				
			case R.id.to_get_code://手机号获取验证码
				// 1. 通过规则判断手机号
				if (!judgePhoneNums(edit_tel.getText().toString())) {
					return;
				} // 2. 通过sdk发送短信验证
				SMSSDK.getVerificationCode("86", edit_tel.getText().toString());

				// 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
				to_get_code.setClickable(false);
				to_get_code.setText("重新发送(" + i + ")");
				new Thread(new Runnable() {
					@Override
					public void run() {
						for (; i > 0; i--) {
							handler_1.sendEmptyMessage(-9);
							if (i <= 0) {
								break;
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						handler_1.sendEmptyMessage(-8);
					}
				}).start();
				break;
			case R.id.login_button:
				SMSSDK.submitVerificationCode("86", edit_tel.getText().toString(), edit_code.getText().toString());
				break;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**短信验证注册*/
	private void reg_smssdk(){
		// 如果希望在读取通信录的时候提示用户，可以添加下面的代码，并且必须在其他代码调用之前，否则不起作用；如果没这个需求，可以不加这行代码
		// SMSSDK.setAskPermisionOnReadContact(boolShowInDialog)
		MobSDK.init(this, Constants.MOB_APP_KEY, Constants.MOB_secret);
		EventHandler eventHandler = new EventHandler(){
			@Override
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler_1.sendMessage(msg);
			}
		};
		//注册回调监听接口
		SMSSDK.registerEventHandler(eventHandler);
	    
	}
	
	/**
	 * 
	 */
	Handler handler_1 = new Handler() {
		public void handleMessage(Message msg) {
			try{
				if (msg.what == -9) {
					to_get_code.setText("重新发送(" + i + ")");
				} else if (msg.what == -8) {
					to_get_code.setText("获取验证码");
					to_get_code.setClickable(true);
					i = 60;
				} else {
					int event = msg.arg1;
					int result = msg.arg2;
					Object data = msg.obj;
					Log.e("event", "event=" + event);
					if (result == SMSSDK.RESULT_COMPLETE) {
						// 短信注册成功后，返回MainActivity,然后提示
						if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
							Toast.makeText(UpdataUserInfo.this, "验证成功",
									Toast.LENGTH_SHORT).show();
							
							progressDialog.show();
							SharedPreferences sp = getSharedPreferences("Customer_Infor", MODE_PRIVATE);//存储用户登录的信息，包括存储微信unionid和手机号码
							String flag = sp.getString("flag", null);
							if(flag.equals("unionid")){
								String cust = sp.getString(flag, null);
								if(cust!=null){
									//get_userMessage(flag,cust);
									get_userMessage(edit_tel.getText().toString(),cust);
								}
							}else{
								Toast.makeText(UpdataUserInfo.this, "当前账号不是微信登录，不需要验证", Toast.LENGTH_LONG).show();
							}
							
							
							
							
						} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
							Toast.makeText(UpdataUserInfo.this, "验证码已经发送",
									Toast.LENGTH_SHORT).show();
						} else {
							((Throwable) data).printStackTrace();
							Toast.makeText(UpdataUserInfo.this, data.toString(),
									Toast.LENGTH_SHORT).show();
						}
					}else {
						int state = data.toString().indexOf("{");
						String json = data.toString().substring(state,data.toString().length());
						
						JSONObject jsonobject = JSONObject.parseObject(json);
						Object detail = jsonobject.get("detail");
						Toast.makeText(UpdataUserInfo.this, detail.toString(),
								Toast.LENGTH_SHORT).show();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}	
		}
	};
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try{
				progressDialog.dismiss();
				String bundle_msg = msg.getData().getString("wx_result");
				if(bundle_msg.equals("exception")){
					Toast.makeText(UpdataUserInfo.this, "网络连接异常", Toast.LENGTH_LONG).show();
				}else{
					//{"returnCode":"300","returnMsg":"select upper info failure"}
					JSONObject jsonarray = JSONObject.parseObject(bundle_msg);
					Object detail = jsonarray.get("returnCode");
					
					if(detail.toString().equals("200")){
						Toast.makeText(UpdataUserInfo.this, "恭喜你！身份信息更新成功了", Toast.LENGTH_LONG).show();
						finish();
					}else{
						Object errmsg = jsonarray.get("returnMsg");
						Toast.makeText(UpdataUserInfo.this, errmsg.toString(), Toast.LENGTH_LONG).show();
						return;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}	
		}
	};
	
	/**更新微信用户登录*/
	private void get_userMessage(final String phoneNum,final String unionid){//flag=unionid或者tel  http://218.17.157.121:4003/users/registerUser
	     requestInterface.getHttpRequestPost(URL_help.getInstance().getUser_address()+"/users/registerUser",
	    		 "{\"tel\":\""+phoneNum+"\",\"unionid\":\""+unionid+"\"}");
	}
	
	/**
	 * 判断手机号码是否合理
	 * 
	 * @param phoneNums
	 */
	private boolean judgePhoneNums(String phoneNums) {
		if (isMatchLength(phoneNums, 11)
				&& isMobileNO(phoneNums)) {
			return true;
		}
		Toast.makeText(this, "手机号码输入有误！",Toast.LENGTH_SHORT).show();
		return false;
	}

	/**
	 * 判断一个字符串的位数
	 * @param str
	 * @param length
	 * @return
	 */
	@SuppressLint("NewApi")
	public static boolean isMatchLength(String str, int length) {
		if (str.isEmpty()) {
			return false;
		} else {
			return str.length() == length ? true : false;
		}
	}
	
	/**
	 * 验证手机格式
	 */
	public static boolean isMobileNO(String mobileNums) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][3578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobileNums))
			return false;
		else
			return mobileNums.matches(telRegex);
	}

	@Override
	protected void onDestroy() {
		SMSSDK.unregisterAllEventHandler();
		super.onDestroy();
	}

	@Override
	public void requestShow(String msg) {
		// TODO Auto-generated method stub
		  Message message = new Message();
		  Bundle bundle = new Bundle();
		  bundle.putString("wx_result", msg);
		  message.setData(bundle);
		  UpdataUserInfo.this.handler.sendMessage(message);
	}
	
}
