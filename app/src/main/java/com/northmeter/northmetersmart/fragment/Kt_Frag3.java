package com.northmeter.northmetersmart.fragment;

import java.util.ArrayList;
import java.util.List;



import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.control.GreeFrequency;
import com.northmeter.northmetersmart.control.GreeFrequencyComplement;
import com.northmeter.northmetersmart.control.GreeKTHW;
import com.northmeter.northmetersmart.control.MiderKTHW;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.db.DBInfraredCode;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.model.MyInfraredCode;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;
import com.northmeter.northmetersmart.order.MyOrder;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.OrderManager;
import com.northmeter.northmetersmart.timerpicker.PickerView;

import android.R.integer;
import android.annotation.SuppressLint;
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
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.NumberPicker.Formatter;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.Toast;
/**空调定时页面的控制指令设置
 * rand integer,mac text,timeNow text,version text,switch text,model text,temputer text,speed text,updown text,leftright text,weeks text
 * */
@SuppressLint("NewApi")
public class Kt_Frag3 extends BaseActivity implements OnClickListener,
		OnValueChangeListener, Formatter {
	private TextView txt_temp;
	private String[] model={"制冷","抽湿","送风","制热","自动"};
	private String[] speed={"一级","二级","三级","自动"};
	private String[] leftRightList = {"左右","左右关"};
	private String[] upDownList = {"上下","上下关"};
	private int model_id=0,speed_id=0,onoff=0,leftright=0,updown=0;
	private int temputer=17;
	private String upDown,leftRight;
	private ImageView imge_onoff , temp_down , temp_up , img_model_make , img_speed_make,chk_updownwind,chk_leftrightwind;
	private CheckBox check_0,check_1,check_2,check_3,check_4,check_5,check_6;
	private String week_0,week_1,week_2,week_3,week_4,week_5,week_6;
	private int time_1=8,time_2=30;
	private String str_mac,str_name,str_type,str_ver;
	private String hwa_1 = null;
	private Handler handler;
	private CustomProgressDialog progressDialog;
	private List weekdays=new ArrayList();
	private String rcb;
	private String hw_code_send,kaiguan;
	private int hw_code;
	private String URL_PATH;
	private PickerView minute_pv;
	private PickerView second_pv;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.f_kongtiao_2);
			
			Intent intent = getIntent();
			str_mac = intent.getStringExtra("mac");
			str_name = intent.getStringExtra("name");
			str_type = intent.getStringExtra("type");
			
			if(str_name.indexOf("格力定频")>=0){
				str_ver = "0003";
			}else if(str_name.indexOf("格力变频")>=0){
				str_ver = "0003_1";
			}else if(str_name.indexOf("华凌")>=0){
				str_ver = "0003_2";
			}else if(str_name.indexOf("美的")>=0){
				str_ver = "0004";
			}
			URL_PATH = URL_help.getInstance().getUrl_address();
			init_PickerView();
			init_first();
			
			handler=new Handler(){
				public void handleMessage(Message mesg){
					try{
						super.handleMessage(mesg);
						Bundle bundle=mesg.getData();
						String result=bundle.getString("SD");
						inintData();
						System.out.println("result"+result);
						if(result.equals("success")){
							progressDialog.cancel();
							finish();
							//((MessageAty)getActivity()).showkt_frg2();
							Toast.makeText(Kt_Frag3.this, "添加成功！", Toast.LENGTH_SHORT).show();
						}else if(result.equals("fail")){
							progressDialog.cancel();
							//((MessageAty)getActivity()).showkt_frg2();
							finish();
							Toast.makeText(Kt_Frag3.this, "添加失败！", Toast.LENGTH_LONG).show();
						}
					
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			};
					
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**初始化时间控件 */
	private void init_PickerView(){
		minute_pv = (PickerView) findViewById(R.id.minute_pv);
		second_pv = (PickerView) findViewById(R.id.second_pv);
		List<String> data = new ArrayList<String>();
		List<String> seconds = new ArrayList<String>();
		for (int i = 0; i < 24; i++)
		{
			data.add(i < 10? "0" + i : ""+i);
		}
		for (int i = 0; i < 60; i++)
		{
			seconds.add(i < 10 ? "0" + i : "" + i);
		}
		minute_pv.setData(data);
		minute_pv.setOnSelectListener(new PickerView.onSelectListener()
		{

			@Override
			public void onSelect(String text)
			{
				time_1 = Integer.parseInt(text);
			}
		});
		second_pv.setData(seconds);
		second_pv.setOnSelectListener(new PickerView.onSelectListener()
		{

			@Override
			public void onSelect(String text)
			{
				time_2 = Integer.parseInt(text);
			}
		});
		minute_pv.setSelected(8);
		second_pv.setSelected(30);
	}

	
	/**初始化界面控件*/
	private void init_first(){
	
		//设置，取消按钮
		findViewById(R.id.btn_submit).setOnClickListener(this);
		findViewById(R.id.button_back).setOnClickListener(this);
		//findViewById(R.id.btn_cancel).setOnClickListener(this);
		
		//开关选择
		imge_onoff=(ImageView) findViewById(R.id.imge_onoff);
		imge_onoff.setOnClickListener(this);
		imge_onoff.setImageResource(getOnOffResource(onoff));
		//降低温度
		temp_down=(ImageView) findViewById(R.id.temp_down);
		temp_down.setOnClickListener(this);
		//增加温度
		temp_up=(ImageView) findViewById(R.id.temp_up);
		temp_up.setOnClickListener(this);
		//模式
		img_model_make=(ImageView) findViewById(R.id.img_model_make);
		img_model_make.setOnClickListener(this);
		img_model_make.setImageResource(getModelResource(model[model_id]));
		//风速
		img_speed_make=(ImageView) findViewById(R.id.img_speed_make);
		img_speed_make.setOnClickListener(this);
		img_speed_make.setImageResource(getSpeedResource(speed[speed_id]));
		//温度显示
		txt_temp=(TextView)findViewById(R.id.txt_ten);
		//上下扫风
		chk_updownwind = (ImageView) findViewById(R.id.chk_updownwind);
		chk_updownwind.setOnClickListener(this);
		chk_updownwind.setImageResource(getUpDownResource(updown));
		//左右扫风
		chk_leftrightwind = (ImageView) findViewById(R.id.chk_leftrightwind);
		chk_leftrightwind.setOnClickListener(this);
		chk_leftrightwind.setImageResource(getLeftRightResource(leftright));
		
		
		//选择每周的天数按钮
		check_0=(CheckBox) findViewById(R.id.chk_0);
		check_0.setOnClickListener(this);
		check_1=(CheckBox) findViewById(R.id.chk_1);
		check_1.setOnClickListener(this);
		check_2=(CheckBox) findViewById(R.id.chk_2);
		check_2.setOnClickListener(this);
		check_3=(CheckBox) findViewById(R.id.chk_3);
		check_3.setOnClickListener(this);
		check_4=(CheckBox) findViewById(R.id.chk_4);
		check_4.setOnClickListener(this);
		check_5=(CheckBox) findViewById(R.id.chk_5);
		check_5.setOnClickListener(this);
		check_6=(CheckBox) findViewById(R.id.chk_6);
		check_6.setOnClickListener(this);
		
	}
	private String getReverseHwm(String hwm){
		String res_hwm ="";
		try{
			for (int i = hwm.length()/2; i > 0; i--)
	        {
			 res_hwm=res_hwm+hwm.substring(i * 2-2, i * 2);       	   
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
		return res_hwm;
	}
	
	@Override
	public void onClick(View v) {
		try{
		int rand=0;
		switch(v.getId()){
		case R.id.btn_submit:
		try{		
			if(weekdays.size()==0){
				Toast.makeText(Kt_Frag3.this, "您还未选择日时段！", Toast.LENGTH_SHORT).show();
				return;
			}
			progressDialog=CustomProgressDialog.createDialog(Kt_Frag3.this);
			progressDialog.setMessage("正在下载红外码");
			progressDialog.show();
			temputer=Integer.parseInt((String) txt_temp.getText());
			
			leftRight = leftRightList[leftright];
			upDown = upDownList[updown];

			if(onoff==0){
				kaiguan="开";
			}else{
				kaiguan="关";
			}
			if(str_ver.equals("0003")){
				String hwm_zl=kaiguan+","+model[model_id]+","+temputer+","+speed[speed_id]+","+upDown+","+leftRight;
			    hwa_1 = GreeKTHW.getKTHWM("GREE",hwm_zl);
			    hwa_1 =hwa_1.substring(0, hwa_1.length()-2);
			    hwa_1 =getReverseHwm(hwa_1);//字节反向
				System.out.println("hwa_1: "+hwa_1);
			}else if(str_ver.equals("0004")){
				String hwm_zl=kaiguan+","+model[model_id]+","+temputer+","+speed[speed_id];
			    hwa_1 = MiderKTHW.getMiderKthw(hwm_zl);
			    hwa_1 =hwa_1.substring(2, hwa_1.length());
				System.out.println("hwa_1: "+hwa_1);
			}else if(str_ver.equals("0003_1")){
				String hwm_zl=kaiguan+","+model[model_id]+","+temputer+","+speed[speed_id]+","+upDown+","+leftRight;
				if(model[model_id].equals("制冷")||model[model_id].equals("送风")){
					hwa_1 = GreeFrequency.getGreeFrequency(hwm_zl);
				}else{
					hwa_1 = GreeFrequencyComplement.getGreeFrequencyComplement(hwm_zl);
				}
			    hwa_1 =hwa_1.substring(2, hwa_1.length());
			}
			//判断数据库里面是否存在相同的红外码，存在则只发送定时任务到控制器，不存储此项红外编码；
			int checkcode=getCheckHwm(kaiguan,model[model_id],txt_temp.getText().toString(),speed[speed_id],upDown,leftRight);
			if(checkcode!=0){
				System.out.println("存在相同的编码！只发送定时日程表...");
				if(checkcode<10){
					hw_code_send="0"+checkcode;
				}else{
					hw_code_send=checkcode+"";
				}
				runThread();
				return;
			}
			
			DBInfraredCode dbinfrared=new DBInfraredCode(Kt_Frag3.this);
			List<MyInfraredCode> infrared=dbinfrared.GetMyInfraredCode();

			System.out.println("infrared**********"+infrared);
			if(!infrared.toString().equals("[]")){
				for(int i=0;i<infrared.size();i++){
					hw_code=infrared.get(infrared.size()-1).getRand()+1;
					System.out.println("hw_code---------------------"+hw_code);
				}
			}else{
				hw_code=3;
			}
			if(hw_code==0){
				hw_code=hw_code+3;
			}
			if(hw_code>50){
				hw_code=3;
			} 
			if(hw_code<10){ 
				hw_code_send="0"+hw_code;
			}else{
				hw_code_send=hw_code+"";
			}
			
			System.out.println("weekdays------------"+weekdays.toString());
			new  Thread(){
				public void run(){
					//读取设备信息
					try{
					//发送红外编码到中间件，存储特定红外编码-cctdhwbm
					MyOrder odToSend= OrderList.Send_Zigbee_ByDeviceType(str_type,
							str_mac, OrderList.KEEP_ZIGBEE_AIR_HWM,"","",hw_code_send+hwa_1);
					
					String rst_raw = OrderManager.getInstance().sendOrder(odToSend,
							OrderList.USER_PASSWORD, URL_PATH, "UTF-8");
					
					System.out.println("发送红外码到中间件====");
					System.out.println("rst_raw: "+rst_raw);
					//runThread();
					if(rst_raw==null){
						System.out.println("发送失败");
						Message mesg=new Message();
						Bundle data=new Bundle();
						data.putString("SD", "fail");
						mesg.setData(data);
						Kt_Frag3.this.handler.sendMessage(mesg);
						return;
					}else{ 
						String status = OrderManager.getInstance().getItemByOrder(rst_raw,
								"status", -1);
						if(status.equals("200")){
							runThread();
						}else{
							Message mesg=new Message();
							Bundle data=new Bundle();
							data.putString("SD", "fail");
							mesg.setData(data);
							Kt_Frag3.this.handler.sendMessage(mesg);
						}
						
					}
					
				}catch(Exception e){
					e.printStackTrace();
					progressDialog.cancel();	
				}
				}
			}.start();
			
			}catch(Exception e){
				e.printStackTrace();
			}
			break;
			
		case R.id.button_back:
			finish();
			break;
		case R.id.chk_0:
			if(check_0.isChecked()==true){
				weekdays.add("七");
				System.out.println("remove weekdays.add(7)");
			}else{
				if(weekdays!=null){
				for(int i=0;i<weekdays.size();i++){
					if(weekdays.get(i).equals("七")){
						weekdays.remove(i);
						--i;
							}
						}
					}
				}
			break;
		case R.id.chk_1:
			if(check_1.isChecked()==true){
				weekdays.add("一");
				System.out.println("remove weekdays.add(1)");
			}else{
				if(weekdays!=null){
				for(int i=0;i<weekdays.size();i++){
					if(weekdays.get(i).equals("一")){
						weekdays.remove(i);
						--i;
							}
						}
					}
				}
			break;
		case R.id.chk_2:
			if(check_2.isChecked()==true){
				weekdays.add("二");
				System.out.println("remove weekdays.add(2)");
			}else{
				if(weekdays!=null){
				for(int i=0;i<weekdays.size();i++){
					if(weekdays.get(i).equals("")){
						weekdays.remove(i);
						--i;
							}
						}
					}
				}
			break;
		case R.id.chk_3:
			if(check_3.isChecked()==true){
				weekdays.add("三");
				System.out.println("remove weekdays.add(3)");
			}else{
				if(weekdays!=null){
				for(int i=0;i<weekdays.size();i++){
					if(weekdays.get(i).equals("三")){
						weekdays.remove(i);
						--i;
							}
						}
					}
				}
			break;
		case R.id.chk_4:
			if(check_4.isChecked()==true){
				weekdays.add("四");
				System.out.println("remove weekdays.add(4)");
			}else{
				if(weekdays!=null){
				for(int i=0;i<weekdays.size();i++){
					if(weekdays.get(i).equals("四")){
						weekdays.remove(i);
						--i;
							}
						}
					}
				}
			break;
		case R.id.chk_5:
			if(check_5.isChecked()==true){
				weekdays.add("五");
				System.out.println("remove weekdays.add(5)");
			}else{
				if(weekdays!=null){ 
				for(int i=0;i<weekdays.size();i++){
					if(weekdays.get(i).equals("五")){
						weekdays.remove(i);
						--i;
							}
						}
					}
				}
			break;
		case R.id.chk_6:
			if(check_6.isChecked()==true){
				weekdays.add("六");
				System.out.println("remove weekdays.add(6)");
			}else{
				if(weekdays!=null){
				for(int i=0;i<weekdays.size();i++){
					if(weekdays.get(i).equals("六")){
						weekdays.remove(i);
						--i;
							}
						}
					}
				}
			break;
			
		case R.id.imge_onoff:
			onoff=onoff+1;
			if(onoff>1){
				onoff=0;
			}
			if(onoff==0){
				Toast.makeText(Kt_Frag3.this, "开机", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(Kt_Frag3.this, "关机", Toast.LENGTH_SHORT).show();
			}
			imge_onoff.setImageResource(getOnOffResource(onoff));
			break;
		case R.id.temp_down://降低温度
			temputer=Integer.parseInt((String) txt_temp.getText());  
			temputer=temputer-1;
			if(temputer<17){
				temputer=30;
			}
			txt_temp.setText(temputer+"");
			break;
		case R.id.temp_up://增加温度
			temputer=Integer.parseInt((String) txt_temp.getText());
			temputer=temputer+1;
			if(temputer>30){
				temputer=17;
			}
			txt_temp.setText(temputer+"");
			break;
		case R.id.img_model_make://模式
			model_id=model_id+1;
			if(model_id>4){
				model_id=0;
			}
			img_model_make.setImageResource(getModelResource(model[model_id]));
			break;
		case R.id.img_speed_make://风速
			speed_id=speed_id+1;
			if(speed_id>3){
				speed_id=0;
			}
			img_speed_make.setImageResource(getSpeedResource(speed[speed_id]));
			break;
			
		case R.id.chk_updownwind://上下扫风
			updown = updown+1;
			if(updown>1){
				updown=0;
			}
			chk_updownwind.setImageResource(getUpDownResource(updown));
			break;
			
		case R.id.chk_leftrightwind://左右扫风
			leftright = leftright+1;
			if(leftright>1){
				leftright=0;
			}
			chk_leftrightwind.setImageResource(getLeftRightResource(leftright));
			break;
		
		}}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	//点击模式图标循环选择模式
		private int getModelResource(String models){
			//String[] model={"自动","制冷","抽湿","送风","制热"};
			int img = 0;
			switch(models){
			case "自动":
				img=R.drawable.air_self_style;
				break;
			case "制冷":
				img=R.drawable.air_model_cold_style;
				break;
			case "抽湿":
				img=R.drawable.air_model_chushi_style;
				break;
			case "送风":
				img=R.drawable.air_model_songfeng_style;
				break;
			case "制热":
				img=R.drawable.air_model_hot_style;
				break;
			}
			return img;
		}
		
		//点击风速图标循环选择风速大小
		private int getSpeedResource(String speeds){
			//String[] speed={"自动","低风","中风","高风"};
			int img = 0;
			switch(speeds){
			case "自动":
				img= R.drawable.air_self_style;
				break;
			case "一级":
				img= R.drawable.air_speed_difeng_style;
				break;
			case "二级":
				img= R.drawable.air_speed_zhongfeng_style;
				break;
			case "三级":
				img= R.drawable.air_speed_gaofeng_style;
				break;
			}
			return img;
		}
		//获取开关对应图标
		private int getOnOffResource(int onoff){
			int imgs = 0;
			switch(onoff){
			case 0:
				imgs=R.drawable.cycle_air_on;
				break;
			case 1:
				imgs=R.drawable.cycle_air_off;
				break;
			}
			return imgs;
		}
		
		//获取左右扫风对应图标
		private int getLeftRightResource(int onoff){
			int imgs = 0;
			switch(onoff){
			case 0:
				imgs=R.drawable.air_leftright;
				break;
			case 1:
				imgs=R.drawable.air_leftright_p;
				break;
			}
			return imgs;
		}
		//获取上下扫风对应图标
		private int getUpDownResource(int onoff){
			int imgs = 0;
			switch(onoff){
			case 0:
				imgs=R.drawable.air_updown;
				break;
			case 1:
				imgs=R.drawable.air_updown_p;
				break;
			}
			return imgs;
		}

		//设置完后重新初始化定义的数据
		private void inintData(){
			onoff=0;
			speed_id=0;
			temputer=17;
			time_1=8;
			time_2=30;
			weekdays.clear();
			check_0.setChecked(false);
			check_1.setChecked(false);
			check_2.setChecked(false);
			check_3.setChecked(false);
			check_4.setChecked(false);
			check_5.setChecked(false);
			check_6.setChecked(false);
		}

	@Override
	public String format(int value) {
		String tmpStr = String.valueOf(value);
		if (value < 10)
			tmpStr = "0" + tmpStr;
		return tmpStr;
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		Toast.makeText(Kt_Frag3.this, "" + newVal, Toast.LENGTH_SHORT)
				.show();
	}
	class onvalchange_1 implements OnValueChangeListener{

		@Override
		public void onValueChange(NumberPicker arg0, int arg1, int newVal) {
			// TODO Auto-generated method stub
//			Toast.makeText(getActivity(), "1" + newVal, Toast.LENGTH_SHORT)
//			.show(); 
			time_1=newVal;	
		}
		
	}

	class onvalchange_2 implements OnValueChangeListener{
		@Override
		public void onValueChange(NumberPicker arg0, int arg1, int newVal) {
			// TODO Auto-generated method stub
//			Toast.makeText(getActivity(), "2" + newVal, Toast.LENGTH_SHORT)
//			.show();
			time_2=newVal;
		}
		
	}
	
	public void runThread(){//发送定时任务到控制器；
		try{
			for(int i=0;i<weekdays.size();i++){
				switch(weekdays.get(i).toString()){
				case "七":
					rcb="07";
					break;
				case "一":
					rcb="01";
					break;
				case "二":
					rcb="02";
					break;
				case "三":
					rcb="03";
					break;
				case "四":
					rcb="04";
					break;
				case "五":
					rcb="05";
					break;
				case "六":
					rcb="06";
					break;
				}
			//读取日程
			String rst_hw = null;
			MyOrder odToSend_1=OrderList.Send_Zigbee_ByDeviceType(str_type, 
					str_mac, OrderList.READ_ZIGBEE_WEEK_TASK,"","",rcb);
			String rst_raw_1 = OrderManager.getInstance().sendOrder(odToSend_1,
					OrderList.USER_PASSWORD, URL_PATH, "UTF-8");
			if(rst_raw_1==null){
				System.out.println("发送失败");
				Message mesg=new Message();
				Bundle data=new Bundle();
				data.putString("SD", "fail");
				mesg.setData(data);
				Kt_Frag3.this.handler.sendMessage(mesg);
			}else{
				String rst_status = OrderManager.getInstance().getItemByOrder(rst_raw_1,
						"status", -1);
				Message mesg=new Message();
				Bundle data=new Bundle();
				
				if(rst_status.equals("200")){
					String rst_1 = OrderManager.getInstance().getItemByOrder(rst_raw_1,
							"result", -1);
					if(rst_1!=null){ 
						int[] rcb_list=getRCB_tesk(rst_1);
						//红外码编号，加分钟，小时     hhmm
						String time_num=hw_code_send+format(time_2)+format(time_1);
						System.out.println("/*/*/*/*/*/*/*定时任务编号："+hw_code_send+"/"+format(time_1)+":"+format(time_2));
					    //截取日程表内一段数据替换为当前设置的日程表数据;(最新的控制器的日程表数据需要把时间按从小到大的顺序排列好)
						rst_hw=rst_1.substring(0,(int) rcb_list[0])+time_num+rst_1.substring((int) rcb_list[1],rst_1.length());
					}else{
						data.putString("SD", "fail");
						mesg.setData(data);
						Kt_Frag3.this.handler.sendMessage(mesg); 
						return;
					}
				}else{
					data.putString("SD", "fail");
					mesg.setData(data);
					Kt_Frag3.this.handler.sendMessage(mesg);  
					return;
				}
				
				
			}
			
				
			System.out.println("rst_hw//////////////"+rst_hw);
			/****************************************************/
			System.out.println("运行到此处1");
			rst_hw = rcb_sort(rst_hw);
			//发送任务日程 ：时间+编号
			MyOrder odToSend=OrderList.Send_Zigbee_ByDeviceType(str_type, 
					str_mac, OrderList.KEEP_ZIGBEE_WEEK_TASK,"","",rcb+rst_hw);	
			
			String rst_raw = OrderManager.getInstance().sendOrder(odToSend,
					OrderList.USER_PASSWORD, URL_PATH, "UTF-8");
			
			System.out.println("rst_raw: "+rst_raw);
			
			if(rst_raw==null){
				System.out.println("发送失败");
				Message mesg=new Message();
				Bundle data=new Bundle();
				data.putString("SD", "fail");
				mesg.setData(data);
				Kt_Frag3.this.handler.sendMessage(mesg);
			}else{
				Message mesg=new Message();
				Bundle data=new Bundle();			
				String status = OrderManager.getInstance().getItemByOrder(rst_raw,
						"status", -1);
				if(status.equals("200")){
					try{
					System.out.println("运行到此处2");
					if(i==weekdays.size()-1){
						//把红外码字段存储到数据库
						DBInfraredCode dbinfrared=new DBInfraredCode(Kt_Frag3.this);
						MyInfraredCode myinfrared=new MyInfraredCode(hw_code,str_mac,format(time_1)+":"+format(time_2), str_ver,kaiguan,model[model_id],txt_temp.getText().toString(),speed[speed_id],upDown,leftRight,weekdays.toString());
						dbinfrared.Insert(myinfrared);
						
						String task_str = "addCycleTask/"+str_mac+"#"+hw_code_send+"&"+str_mac+"&"+format(time_1)+":"+format(time_2)+"&"+"0a0001aa7k"+"&"+kaiguan+"&"+model[model_id]+
								"&"+txt_temp.getText().toString()+"&"+speed[speed_id]+"&"+upDown+"&"+leftRight+"&"+weekdays.toString();

						add_Task_Data(task_str);
						
						data.putString("SD", "success");
						mesg.setData(data);
						Kt_Frag3.this.handler.sendMessage(mesg);
					}else{
						continue;
					}
					}catch(Exception e){
						e.printStackTrace();
					}
				}else{
					System.out.println("运行到此处3");
					data.putString("SD", "fail");
					mesg.setData(data);
					Kt_Frag3.this.handler.sendMessage(mesg);
					return;
	
				}
			  }
			}//循环发送
		}catch(Exception e){
			e.printStackTrace();
			progressDialog.cancel();
		}
	}
	
	/**发送以设置好的定时任务到服务器*/
	private void add_Task_Data(final String data){//addCycleTask/3（红外码编号）&002014110119&08:15&0003（空调类型）&开&制冷（模式）&24（温度）&三级（风速）&上下（上下扫风开）&左右（左右扫风开）&[一, 二, 三, 四, 五]
		new Thread(){
			public void run(){
				try {
					PublishMqttMessageAndReceive.getInstance().PublishMessage(getApplicationContext(),
							"0a0001aa7k/"+str_mac,"tasks_/"+data);	
					//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord("0a0001aa7k/"+str_mac,"tasks_/"+data);
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();	
	}
	
	
	
	/**读取回来的日程表数据为一串的000000，3个字节为一个日程表*/                                                                                                                                                                                                        
	public int[] getRCB_tesk(String result){
		int[] rcb_data=new int[]{0,6};
		int s=0,e=6; 
		for(int i=0;i<result.length()/6;i++){
			if(result.substring(s, e).equals("000000")){
				rcb_data[0] = s;
				rcb_data[1] = e;
				break;
			}
			s=s+6;
			e=e+6;
		}
		if(result.indexOf("000000")<0){
			rcb_data[0] = 60;
			rcb_data[1] = 66;
		}
		return rcb_data;
	}
	/**检查数据库是否存在相同的定时红外，存在则返回编号，不存在则返回0*/
	public int getCheckHwm(String kaiguan,String model,String temp,String speed,String upDown,String leftRight){
		try{
		DBInfraredCode dbinfrared=new DBInfraredCode(Kt_Frag3.this);
		List<MyInfraredCode> infrared=dbinfrared.GetMyInfraredCode();

		System.out.println("infrared**********"+infrared);
		if(infrared.toString().equals("[]")){
			for(int i=0;i<infrared.size();i++){
				if(infrared.get(i).getSwitchs().equals(kaiguan)&infrared.get(i).getModel().equals(model)&
						infrared.get(i).getTemputer().equals(temp)&infrared.get(i).getSpeed().equals(speed)&
						infrared.get(i).getUpdown().equals(upDown)&infrared.get(i).getLeftright().equals(leftRight)){
					return infrared.get(i).getRand();
				}else{
					return 0;
				}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
		return 0;
	}
	
	/**把日程表数据按从小到大的顺序进行排列*/
	private String rcb_sort(String rcb_str){
		String[] rcb_list = new String[rcb_str.length()/6];
		String result_rcb="";
		int[] list = getRCB_tesk(rcb_str);
		if( (int) list[0]>6 ){
			for(int i=0 ; i<rcb_str.length()/6 ; i++){
				rcb_list[i] = rcb_str.substring(i*6, i*6+6);
			}
			for(int x=0;x<rcb_list.length;x++){
				System.out.println("======================================"+rcb_list[x]);
			}
			for(int j=0;j<list[1]/6;j++){
				for(int k=j+1;k<list[1]/6-1;k++){
					String str_ = rcb_list[j];
					int l1 = Integer.parseInt(receive_rcb_str(rcb_list[j]));
					int l2 = Integer.parseInt(receive_rcb_str(rcb_list[k]));
					if(l1>l2){
						rcb_list[j] = rcb_list[k];
						rcb_list[k] = str_;
					}
				}
					
			}
		}else{
			return rcb_str;
		}
		for(int i=0;i<rcb_list.length;i++){
			result_rcb =result_rcb+rcb_list[i];
		}
		return result_rcb;
	}
	
	private String receive_rcb_str(String rcb_str){
		String rcb_rece="";
		try{
			rcb_rece = rcb_str.substring(2, 6);
			rcb_rece = rcb_rece.substring(2, 4)+rcb_rece.substring(0, 2);
		}catch(Exception e){
			e.printStackTrace();
		}
		return rcb_rece;
	} 
	
}
