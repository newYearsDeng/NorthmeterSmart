package com.northmeter.northmetersmart.fragment;

import java.util.Random;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.device.DeviceManager;
import com.northmeter.northmetersmart.helper.FragmentHelper;
import com.northmeter.northmetersmart.helper.RadioHelper;

/**wifi插座*/
public class Add_Fragment2 extends Fragment implements OnClickListener {
	private View view;
	private EditText et_ssid;
	private EditText et_pswd;
	private EditText et_tableNum;
	private CheckBox checkbox_save;

	private ProgressDialog smartcongfigDialog;
	private MySmartconfigTask scTask;

	Chronometer timer;
	SharedPreferences get;
	// scan
	private final static int SCANNIN_GREQUEST_CODE = 1;
	TextView tv_scan_result;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.f_add_2, container, false);
		view.findViewById(R.id.button_next1).setOnClickListener(this);
		checkbox_save = (CheckBox) view.findViewById(R.id.checkbox_save);
		//checkbox_save.setChecked(true);
		checkbox_save.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					 if(isChecked){ 
						 SharedPreferences set=getActivity().getSharedPreferences("biaozhi", 0);
						   Editor editor=set.edit();
						   editor.putString("biaozhi", "1");
						   editor.commit();
		                }else{ 
		                 SharedPreferences set=getActivity().getSharedPreferences("biaozhi", 0);
		     			   Editor editor=set.edit();
		     			   editor.putString("biaozhi", "0");
		     			   editor.commit(); 
		                } 
				}
			});
		
		//view.findViewById(R.id.btn_add_search).setOnClickListener(this);//扫秒按钮

		et_ssid = (EditText) view.findViewById(R.id.editText1);
		et_pswd = (EditText) view.findViewById(R.id.editText2);
		String ssidNow = DeviceManager.getInstance().getPhoneSsid();
		et_ssid.setText(ssidNow);
		String pswd = null;
		get=getActivity().getSharedPreferences("biaozhi", 0);
		String biaozhi=get.getString("biaozhi","0");
		System.out.println("---------------------"+get.getString("biaozhi","0"));
		if(biaozhi.equals("1")){
			checkbox_save.setChecked(true);
		if (ssidNow != null) {
				SharedPreferences getsp=getActivity().getSharedPreferences(ssidNow, 0);
				pswd = getsp.getString(ssidNow, "0");
				et_pswd.setText(pswd);
				System.out.println("............mima"+ssidNow+pswd);
			}
		}else{
			checkbox_save.setChecked(false);
		}

		
        //et_tableNum = (EditText) view.findViewById(R.id.editText3);//表号输入框，这里注销掉

		// et_tableNum.setInputType(InputType.TYPE_NULL); // 禁止输入
		// et_tableNum.setOnFocusChangeListener(new OnFocusChangeListener() {
		//
		// @Override
		// public void onFocusChange(View arg0, boolean isFocused) {
		// if (isFocused) {
		// // 打开扫描二维码界面
		// Intent intent = new Intent();
		// intent.setClass(getActivity(), MipcaActivityCapture.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// getActivity().startActivityForResult(intent,
		// SCANNIN_GREQUEST_CODE);
		// }
		// }
		// });
		TextView test=(TextView)view.findViewById(R.id.textout);
		test.setText(Html.fromHtml("<b><font color=#1111EE>请按一下插座面板上的配置按钮</font></b> "+ "<font>,插座液晶的电话通讯符号变为快速闪烁，开始进入配置状态</font>"));

		initTimer();

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

	//et_tableNum.setText(AddAty.str_scan_result);//再次打开页面时显示输入的表号
	}

	/**
	 * 初始化 Chronometer
	 */
	private void initTimer() {
		timer = new Chronometer(getActivity());
	}
	

	/**
	 * 初始化 smartconfig 时的 等待dialog
	 */
	private void initDialog() {
		smartcongfigDialog = new ProgressDialog(this.getActivity(),
				ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
		smartcongfigDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		smartcongfigDialog.setIndeterminate(false); // 设置进度条是否为不明确，false表示圆形的进度条
		smartcongfigDialog.setCancelable(false);
		smartcongfigDialog.setTitle("正在配置中...");
//		smartcongfigDialog.incrementProgressBy(1);
//		smartcongfigDialog.setMax(120);
//		 new Thread(){
//	        	public void run(){
//	        		try{
//	        			for(int i=0;i<120;i++){
//	        				smartcongfigDialog.setProgress(i);
//	        				sleep(1000);
//	        			}
//	        		}catch(Exception e){
//	        			e.printStackTrace();
//	        		}
//	        	}
//	        }.start();
		smartcongfigDialog.setMessage("2分钟后自动取消...");
		smartcongfigDialog.setIcon(R.drawable.icon_wait);
		// scTask = new MySmartconfigTask();
		scTask = new MySmartconfigTask(this.getActivity());
		scTask.smartcongfigDialog = smartcongfigDialog;
		scTask.timer = timer;
		scTask.execute();
	}

	@Override
	public void onDestroyView() {
		ViewGroup viewGroup = (ViewGroup) view.getParent();
		viewGroup.removeView(view);
		super.onDestroyView();
	}


	 String ssid=null;
	 String pswd=null; 
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_next1:
			// 完成设备的表号填写，及wifi信息的填写
			/*DeviceManager.getInstance().getTempDevice()
					.setTableNum(et_tableNum.getText().toString());//存入表号 */
			 ssid = et_ssid.getText().toString();
			 pswd = et_pswd.getText().toString();
			 get=getActivity().getSharedPreferences("biaozhi", 0);
			 String biaozhi=get.getString("biaozhi","0");
			 if(biaozhi.equals("1")){
			   SharedPreferences setsp=getActivity().getSharedPreferences(ssid, 0);
			   Editor editor=setsp.edit();
			   editor.putString(ssid, pswd);
			   System.out.println("********"+ssid+pswd);
			   editor.commit();
			 	
			 }
			DeviceManager.getInstance().doSmartCongfigStart(ssid, pswd);
			// 弹出等待dialog
			initDialog();
			break;

		/*case R.id.btn_add_search:
			// 开始扫描二维码
			Intent intent = new Intent();
			intent.setClass(this.getActivity(), MipcaActivityCapture.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			getActivity().startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			break;*/
		}
	}
	/**
	 * 异步进行 smartconfig 120s超时
	 *
	 * @author Carl
	 *
	 */
	class MySmartconfigTask extends AsyncTask<Object, Object, Object> implements
			Chronometer.OnChronometerTickListener {

		ProgressDialog smartcongfigDialog;
		Chronometer timer;

		private Context context;
		private int timeCount;
		private int TIME_OUT = 120;

		private Time timeLast;
		private Time timeCur;

		public MySmartconfigTask() {

		}

		public MySmartconfigTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			smartcongfigDialog.show();

			timeCount = 0;

			timeLast = new Time();
			timeLast.setToNow();
			timeCur = new Time();

			// ==============================================
			timer = new Chronometer(context);
			timer.setOnChronometerTickListener(this);
			timer.setBase(0);
			timer.start();
		}

		/**
		 * 更新时间，检测 DeviceManager.isFound，2分钟内如果没有找到，则超时
		 *
		 * @param arg0
		 * @return
		 */
		@Override
		protected Boolean doInBackground(Object... arg0) {
			while (!DeviceManager.getInstance().isFoundDevice) {

				//System.out.println("timeCount = " + timeCount);

				if (timeCount == TIME_OUT)
					break;
				else {
					timeCur.setToNow();
					int now = timeCur.second;
					int nowMinite = timeCur.minute;

					if ((now - timeLast.second) == 1) {
						++timeCount;
						timeLast.setToNow();
					}
					else if (nowMinite > timeLast.minute)
					{
						if ((timeLast.second - now) == 59) {
							++timeCount;
							timeLast.setToNow();
						}
					}
				}
			}

			if (timeCount == TIME_OUT) {
				System.out.println("Time out!!!!!!!!!!!!");
				return false;
			} else
				return true;
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			smartcongfigDialog.hide();

			if ((Boolean) result == false) { // 配置超时
				Toast.makeText(context, "配置超时", Toast.LENGTH_SHORT).show();
				DeviceManager.getInstance().doSmartConfigStop();
				DeviceManager.getInstance().doDeviceClear();
			} else
//			((RadioButton) RadioHelper.radioGroup.getChildAt(3))
//					.setChecked(true); // 成功连接设备，跳转至下一页
				FragmentHelper.loadFragment(FragmentHelper.fragments.get(3));//通过Fragment设置选中
			((RadioButton) RadioHelper.radioGroup.getChildAt(2)).setChecked(true);
		}

		/**
		 * 每秒调用一次
		 */
		@Override
		public void onChronometerTick(Chronometer arg0) {
			timeCount = timeCount + 1;
			//System.out.println("time = " + timeCount);
		}
	}
}


