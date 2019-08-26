package com.northmeter.northmetersmart.activity_build;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.camera.activity.CaptureActivity;
import com.northmeter.northmetersmart.helper.PopupHelper;
import com.northmeter.northmetersmart.order.Type_Entity;

/**添加建筑档案*/
public class Add_BuildAty_1 extends BaseActivity implements OnClickListener{
	private final static int SCANNIN_GREQUEST_CODE = 1;
	private EditText edittext_num;
	private String buildid,type;//添加设备时所在建筑的建筑id
	private PopupWindow popupWindowf1_p1;
	private View f1_p1View;
	private TextView layout_type_change;//设备类型选择

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.add_buildaty_1);
			init_view();
			loadPopupWindow();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void init_view(){
		Intent intent = getIntent();
		buildid = intent.getStringExtra("buildid");
		
		findViewById(R.id.button_back).setOnClickListener(this);
		findViewById(R.id.but_scan).setOnClickListener(this);//扫描
		edittext_num = (EditText) findViewById(R.id.edittext_num);
		findViewById(R.id.but_next).setOnClickListener(this);//下一步
		layout_type_change  = (TextView) findViewById(R.id.layout_type_change);//选择类型
		layout_type_change.setOnClickListener(this);
		layout_type_change.setText("以太网智能网关");
		type = Type_Entity.Gateway_type;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try{
			switch(v.getId()){
			case R.id.button_back://返回
				finish();
				break;
			case R.id.but_scan:
				// 开始扫描二维码，得到的信息会存储在 str_scan_result
				Intent intent = new Intent();
				intent.setClass(this, CaptureActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				this.startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
				break;
			case R.id.but_next://下一步
				String tableNum = edittext_num.getText().toString();
				if(tableNum.equals("")){
					Toast.makeText(this, "请输入或扫描设备号", Toast.LENGTH_LONG).show();
				}else{
					if(tableNum.length()==9){
						tableNum = "000"+tableNum;
					}
					
					Intent intent1 = new Intent(Add_BuildAty_1.this,Add_BuildAty_2.class);
					intent1.putExtra("type", type);
					intent1.putExtra("tableNum", edittext_num.getText().toString());
					intent1.putExtra("buildid", buildid);
					startActivity(intent1);
				}
				break;
				
			case R.id.layout_type_change:
				popupWindowf1_p1.showAsDropDown(v);
				break;
			case R.id.btn1:
				layout_type_change.setText(((Button) v).getText());
				type = Type_Entity.Gateway_type;
				popupWindowf1_p1.dismiss();
				break;
			case R.id.btn2:
				layout_type_change.setText(((Button) v).getText());
				type = Type_Entity.Socket_type;
				popupWindowf1_p1.dismiss();
				break;
			case R.id.btn3:
				layout_type_change.setText(((Button) v).getText());
				type = Type_Entity.Four_street_control;
				popupWindowf1_p1.dismiss();
				break;
			case R.id.btn4:
				layout_type_change.setText(((Button) v).getText());
				type = Type_Entity.Split_air_conditioning;
				popupWindowf1_p1.dismiss();
				break;
			case R.id.btn5:
				layout_type_change.setText(((Button) v).getText());
				type = Type_Entity.Central_air_conditioning;
				popupWindowf1_p1.dismiss();
				break;
				}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/*扫面完成后关闭扫面界面返回时接收传回的数据*/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_FIRST_USER) {
				if (data != null) {
					if (data.hasExtra("result")) {
						 data.getStringExtra("result");
						 edittext_num.setText(data.getStringExtra("result").toString());
						 System.out.println("==="+data.getStringExtra("result"));
						 return;
						}
				    }
				}
			break;
		}
	}
	
	private void loadPopupWindow() {
		f1_p1View = LayoutInflater.from(getApplicationContext()).inflate(R.layout.f_add_1p1,null);
		popupWindowf1_p1 = new PopupHelper().getWindow_ALLWRAP(f1_p1View,this);
		f1_p1View.findViewById(R.id.btn1).setOnClickListener(this);
		f1_p1View.findViewById(R.id.btn2).setOnClickListener(this);
		f1_p1View.findViewById(R.id.btn3).setOnClickListener(this);
		f1_p1View.findViewById(R.id.btn4).setOnClickListener(this);
		f1_p1View.findViewById(R.id.btn5).setOnClickListener(this);
	}
	
	
	
}
