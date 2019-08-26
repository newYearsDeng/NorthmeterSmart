package com.northmeter.northmetersmart.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.helper.PopupHelper;
import com.northmeter.northmetersmart.helper.WidgetHelper;
import com.northmeter.northmetersmart.wxapi.WXEntryActivity;

/**用户中心*/
public class UserCenterAty extends BaseActivity implements OnClickListener{
	private TextView user_number;//账号
	private String customer_Infor;
	private SharedPreferences sp;
	private TextView text_login;
	
	private PopupWindow popupWindow;
	private View view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercenter_aty);
		init_view();
		loadPopupWindow();
	}
	
	/**初始化视图数据*/
	private void init_view(){
		try{
			user_number = (TextView) findViewById(R.id.user_number);
			findViewById(R.id.user_layout_1).setOnClickListener(this);//账号信息
			findViewById(R.id.user_layout_2).setOnClickListener(this);//更新身份信息
			findViewById(R.id.user_layout_3).setOnClickListener(this);//消息及报警记录
			findViewById(R.id.user_layout_4).setOnClickListener(this);//档案添加密码
			findViewById(R.id.user_layout_5).setOnClickListener(this);//重新登录
			findViewById(R.id.but_back_1).setOnClickListener(this);//返回上一级
			text_login = (TextView) findViewById(R.id.text_login);
			
			
			sp = getSharedPreferences("Customer_Infor", MODE_PRIVATE);//存储用户登录的信息，包括存储微信unionid和手机号码
			String flag = sp.getString("flag", null);
			if(flag!=null){
				text_login.setText("注销当前账号");
				if(flag.equals("tel")){
					customer_Infor = sp.getString("tel", null);
					user_number.setText(customer_Infor.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
				}else{
					customer_Infor = null;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try{
			switch(v.getId()){
			case R.id.but_back_1:
				finish();
				break;
			case R.id.user_layout_1://账号信息
				startActivity(new Intent(UserCenterAty.this,UserCenterInformation.class));
				break;
				
			case R.id.user_layout_2://手机微信绑定
				startActivity(new Intent(UserCenterAty.this,UpdataUserInfo.class));
				break;
			case R.id.user_layout_3://消息报警和记录
				startActivity(new Intent(UserCenterAty.this,ReportDataAty.class));
				break;
			case R.id.user_layout_5://重新登录
				String login_text = text_login.getText().toString();
				if(login_text.equals("登录")){//登录
					Editor editor1 = sp.edit();
					editor1.putString("flag", null);
					editor1.putString("tel", null);
					editor1.putString("unionid", null);
					editor1.putString("userData", null);
					editor1.commit();

				    Intent intent = new Intent(UserCenterAty.this,WXEntryActivity.class)
				    	.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);  
				    startActivity(intent);  
				}else{//注销
					popupWindow.showAtLocation(v,Gravity.CENTER, 0, 0);
				}

				break;
							
			case R.id.button_cancel:
				popupWindow.dismiss();
				break;
			case R.id.button_submit:
				Editor editor = sp.edit();
				editor.putString("flag", null);
				editor.putString("tel", null);
				editor.putString("unionid", null);
				editor.commit();
				 Intent intent = new Intent(UserCenterAty.this,WXEntryActivity.class)
			    	.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);  
			    startActivity(intent);
				break;
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void loadPopupWindow() {
		view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.app_exit,null);
		popupWindow = new PopupHelper().getWindow_ALLWRAP(view,this);
		TextView title_text = (TextView) view.findViewById(R.id.title_text);
		title_text.setText("是否退出当前账号吗?");
		
		view.findViewById(R.id.button_cancel).setOnClickListener(this);
		view.findViewById(R.id.button_submit).setOnClickListener(this);
		popupWindow.setWidth((int) (WidgetHelper.getWindowWidth(this) * 0.8));
		
		
	}

}
