package com.northmeter.northmetersmart.wxapi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import cn.smssdk.SMSSDK;
import cn.smssdk.EventHandler;

import com.alibaba.fastjson.JSONObject;
import com.mob.MobSDK;
import com.northmeter.northmetersmart.I.IRequestShow;
import com.northmeter.northmetersmart.I.RequestInterface;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity.MainActivity;
import com.northmeter.northmetersmart.activity.UrlSettinAty;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.utils.Utils;
import com.northmeter.northmetersmart.wxpay.Constants;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

/**微信登录接入类*/

public class WXEntryActivity extends BaseActivity implements OnClickListener,IWXAPIEventHandler,IRequestShow {
	
	private EditText edit_tel,edit_code;
	private SharedPreferences sp;
	private Button to_get_code;
	int i = 60;
	private CustomProgressDialog progressDialog;
	private RequestInterface requestInterface;
	private String address = "218.17.157.121:1531";//10.168.1.165:8081
	
	//IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI api;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wxentryactivity);
		requestInterface = new RequestInterface(this);
		init_view();
		regToWx();
		reg_smssdk();

	}
	/**
	 * 短信验证注册
	 */
	private void reg_smssdk() {
		// 如果希望在读取通信录的时候提示用户，可以添加下面的代码，并且必须在其他代码调用之前，否则不起作用；如果没这个需求，可以不加这行代码
		// SMSSDK.setAskPermisionOnReadContact(boolShowInDialog)
		MobSDK.init(this, Constants.MOB_APP_KEY, Constants.MOB_secret);
		// 创建EventHandler对象
		EventHandler eventHandler = new EventHandler() {
			@Override
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler_1.sendMessage(msg);
			}
		};

		// 注册监听器
		SMSSDK.registerEventHandler(eventHandler);
	}

	/**
	 * 
	 */
	Handler handler_1 = new Handler() {
		public void handleMessage(Message msg) {
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
						Toast.makeText(WXEntryActivity.this, "验证成功",
								Toast.LENGTH_SHORT).show();
						
						Editor editor = sp.edit();
						editor.putString("flag", "tel");
					    editor.putString("tel", edit_tel.getText().toString());
					    editor.commit();
					    progressDialog.show();
						get_userMessage("tel",edit_tel.getText().toString());
						
					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						Toast.makeText(WXEntryActivity.this, "验证码已经发送",
								Toast.LENGTH_SHORT).show();
					} else {
						((Throwable) data).printStackTrace();
						Toast.makeText(WXEntryActivity.this, data.toString(),
								Toast.LENGTH_SHORT).show();
					}
				}else {
					int state = data.toString().indexOf("{");
					String json = data.toString().substring(state,data.toString().length());
					
					JSONObject jsonobject = JSONObject.parseObject(json);
					Object detail = jsonobject.get("detail");
					Toast.makeText(WXEntryActivity.this, detail.toString(),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	
	
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

	/**
	 * progressbar
	 */
	private void createProgressBar() {
		FrameLayout layout = (FrameLayout) findViewById(android.R.id.content);
		LayoutParams layoutParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER;
		ProgressBar mProBar = new ProgressBar(this);
		mProBar.setLayoutParams(layoutParams);
		mProBar.setVisibility(View.VISIBLE);
		layout.addView(mProBar);
	}
	
	@Override
	protected void onDestroy() {
		SMSSDK.unregisterAllEventHandler();
		super.onDestroy();
	}
	
	/**----------------------------------------*/
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
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
		case R.id.login_button://手机号登录
				
			SMSSDK.submitVerificationCode("86", edit_tel.getText().toString(), edit_code
					.getText().toString());
			//createProgressBar();
//			Editor editor = sp.edit();
//			editor.putString("flag", "tel");
//		    editor.putString("tel", edit_tel.getText().toString());
//		    editor.commit();
//			
//			progressDialog.show();	
//			get_userMessage("tel",edit_tel.getText().toString());
			break;
		
		case R.id.wx_login://微信登录
			if (!api.isWXAppInstalled()) {  
			    //提醒用户没有安装微信  
			    Toast.makeText(this, "请先安装微信!", Toast.LENGTH_SHORT).show();  
			    return;  
			}
			
			String unionid = sp.getString("unionid", null);
			if(unionid==null){
				SendAuth.Req req = new SendAuth.Req();
		    	//授权读取用户信息
		    	req.scope = "snsapi_userinfo";
		    	//自定义信息
		    	req.state = "wechat_sdk_demo_test";
		    	//向微信发送请求
		    	api.sendReq(req);
		    	
		    	finish();
			}else{
				progressDialog.show();	
				get_userMessage("unionid",unionid);
			}
			
			break;
			
		case R.id.linearLayout3://更多
			startActivity(new Intent(WXEntryActivity.this,UrlSettinAty.class));
			break;
		}
		
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			progressDialog.dismiss();
			Message message_msg=new Message();
			Bundle bundle = new Bundle();
			String bundle_msg = msg.getData().getString("wx_result");
			if(bundle_msg.equals("exception")){
				Toast.makeText(WXEntryActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
			}else{
				Intent intent = new Intent(WXEntryActivity.this,
						MainActivity.class);
				intent.putExtra("mode", "Non-existent");
				intent.putExtra("login_infor", bundle_msg);
				startActivity(intent);
				WXEntryActivity.this.finish();
			}
			
			
		}
	};
	
	
	/**从web后台获取用户数据*/
	private void get_userMessage(final String flag,final String number){//flag=unionid或者tel
		String para = flag;
		if(flag.equals("unionid")){
			para = "weixin";
		}
	    requestInterface.getHttpRequestGet(URL_help.getInstance().getUser_address()+"/users/getUser", "flag="+para+"&"+flag+"="+number);//http://218.17.157.121:4003/users/getUser

	}
	
	
	
	
	
	/**微信注册*/
	private void regToWx(){
    	//通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
    	//将应用的appid注册到微信
    	api.registerApp(Constants.APP_ID);
    	
    	api.handleIntent(getIntent(), this);
    	//handleIntent(getIntent());  
    	
    }
	

	private void init_view(){
		SharedPreferences sp_url = getSharedPreferences("URL_ADD", MODE_PRIVATE);
		String userURL = "http://"+sp_url.getString("USER_ADD", address);
		URL_help.getInstance().setUser_address(userURL);//账户地址
		
		findViewById(R.id.linearLayout3).setOnClickListener(this);//更多
		findViewById(R.id.wx_login).setOnClickListener(this);//微信登录
		findViewById(R.id.login_button).setOnClickListener(this);//登录按钮
		to_get_code = (Button) findViewById(R.id.to_get_code);//获取验证码
		to_get_code.setOnClickListener(this);
		edit_tel = (EditText) findViewById(R.id.edit_tel);//手机号输入框
		
		edit_tel.addTextChangedListener(textWatcher);  
		edit_code = (EditText) findViewById(R.id.edit_code);//手机验证码
		
		sp = getSharedPreferences("Customer_Infor", MODE_PRIVATE);//存储用户登录的信息，包括存储微信unionid和手机号码
		String tel = sp.getString("tel", null);
		edit_tel.setText(tel);
		
		String flag = sp.getString("flag", null);
		if(flag!=null){
			String cust = sp.getString(flag, null);
			if(cust!=null){
				//get_userMessage(flag,cust);
				Intent intent = new Intent(WXEntryActivity.this,
						MainActivity.class);
				intent.putExtra("mode", "Already");
				startActivity(intent);
				finish();
			}
		}
		
		progressDialog=CustomProgressDialog.createDialog(this);
	    progressDialog.setMessage("登录...");
	}


	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		
	}           

	@Override
	public void onResp(BaseResp resp) {
		// TODO Auto-generated method stub
		 if(resp instanceof SendAuth.Resp){
		      SendAuth.Resp newResp = (SendAuth.Resp) resp;
		      //获取微信传回的code
		      String code = newResp.code;
		      String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+Constants.APP_ID+"&secret="+Constants.WX_secret+"&code="+code+"&grant_type=authorization_code";
		      String token = getHtmlByteArray(url);
		      
		      boolean flag = true;
		      JSONObject jsononject1 = JSONObject.parseObject(token); 
		      for(Object k: jsononject1.keySet()){
		    	  Object v1 = jsononject1.get(k);
		    	  if(v1.toString().equals("errcode")){
		    		  flag = false;
		    	  }
		      }
		      if(flag){
		    	  Object v1 = jsononject1.getString("access_token");
			      Object v2 = jsononject1.getString("refresh_token");
			      Object v3 = jsononject1.getString("openid");
			      Object v4 = jsononject1.getString("scope");
			      Object v5 = jsononject1.getString("unionid");
			      Editor editor = sp.edit();
			      editor.putString("flag", "unionid");
			      editor.putString("unionid", v5.toString());
			      editor.commit(); 
			      get_userMessage("unionid",v5.toString());
			      
		      }
				
		     

		    }
	}
		
	/**根据code像微信请求数据*/
	public static String getHtmlByteArray(final String url) {
		 URL htmlUrl = null;     
		 InputStream inStream = null;     
		 try {         
			 htmlUrl = new URL(url);         
			 URLConnection connection = htmlUrl.openConnection();         
			 HttpURLConnection httpConnection = (HttpURLConnection)connection;         
			 int responseCode = httpConnection.getResponseCode();         
			 if(responseCode == HttpURLConnection.HTTP_OK){ 
				 inStream = httpConnection.getInputStream();         
			  }     
		 } catch (MalformedURLException e) {               
			 e.printStackTrace();     
		 } catch (IOException e) {              
			e.printStackTrace();    
		 } 
		byte[] data = inputStreamToByte(inStream);
		String takes = Utils.Ascii_To_String(Utils.bytesToHexString(data));
		return takes;
	}
	
	public static byte[] inputStreamToByte(InputStream is) {
		try{
			ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
			int ch;
			while ((ch = is.read()) != -1) {
				bytestream.write(ch);
			}
			byte imgdata[] = bytestream.toByteArray();
			bytestream.close();
			return imgdata;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	@Override  
    protected void onNewIntent(Intent intent) {  
        super.onNewIntent(intent);  
        handleIntent(intent);  
    }   
	  
    private void handleIntent(Intent intent) {  
        SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());  
        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {  
            //用户同意  
        }  
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
	public void requestShow(String msg) {
		// TODO Auto-generated method stub
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("wx_result", msg);
		message.setData(bundle);
		WXEntryActivity.this.handler.sendMessage(message);
	}  
}
