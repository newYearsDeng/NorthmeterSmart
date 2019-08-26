package com.northmeter.northmetersmart.activity;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.andview.refreshview.XRefreshView;
import com.nineoldandroids.view.ViewHelper;
import com.northmeter.northmetersmart.I.IRequestShow;
import com.northmeter.northmetersmart.I.RequestInterface;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity_build.Activity_Build_Garden;
import com.northmeter.northmetersmart.activity_build.Activity_Visitor;
import com.northmeter.northmetersmart.activity_build.FragmentActivity_Build;
import com.northmeter.northmetersmart.activity_build.FragmentActivity_Room;
import com.northmeter.northmetersmart.adp.Main_GridviewAdp;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.helper.PopupHelper;
import com.northmeter.northmetersmart.helper.WidgetHelper;
import com.northmeter.northmetersmart.http.HttpRequest;
import com.northmeter.northmetersmart.mainhelp.ContentAdapter;
import com.northmeter.northmetersmart.mainhelp.ContentModel;
import com.northmeter.northmetersmart.model.Main_GvModel;
import com.northmeter.northmetersmart.mqtt.PushService;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import androidx.drawerlayout.widget.DrawerLayout;

/**
 * create by dyd on 2015/5/20
 * 增加设备主页面横向滑屏
 * 功能增加：在3.4版本的基础上增加活动流界面，这里只增加插座的活动流显示
 * */
public class MainActivity extends BaseActivity implements OnClickListener,XRefreshView.XRefreshViewListener,
        OnItemClickListener,OnTouchListener,IRequestShow {
	private View view;
	private PopupWindow popupWindow,popupWindow_1;
	private DrawerLayout drawerLayout;
	private ListView listView;
	private Main_GridviewAdp main_adapter;
	private static List<Main_GvModel> models = new ArrayList<Main_GvModel>();
	private String mDeviceID;
	private ReceiveTool receiver;
	private List<ContentModel> list;
	
	private String network_address = "218.17.157.121:8821";//10.168.1.165:8800
	private String mqtt_address = "218.17.157.121:1883";//10.168.1.165:1883
	private String tcp_address = "218.17.157.121:8822";//10.168.1.165:8010
	public static String str_scan_result;
	private SharedPreferences sp;
	private GestureDetector gesturedelector;   
	private ContentAdapter adapter;
	private String login_msg,login_mode;
	private int width;
	private RequestInterface requestInterface;
	
	private CustomProgressDialog progressDialog;
	private GridView mPullToRefreshGridview;
	private XRefreshView xRefreshView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isAvailable()){
            //当前无可用网络
            Toast.makeText(MainActivity.this, "当前无网络连接可用", Toast.LENGTH_LONG).show();
        }
        StartServices();
        RegisterBroad();
        /**目前监听的滑动事件会对gridview的上下滑动造成影响*/
        gesturedelector = new GestureDetector(MainActivity.this,mylistener);//滑动监听，打开侧拉菜单

		startView();
	}

    @Override
    public void initData() {
        Intent login_intent = getIntent();
        login_mode = login_intent.getStringExtra("mode");
        login_msg  = login_intent.getStringExtra("login_infor");

        sp = getSharedPreferences("URL_ADD", MODE_PRIVATE);
        String URL_PATH = "http://"+sp.getString("URL_ADD", network_address);
        URL_help.getInstance().setUrl_address(URL_PATH);//HTTP主站地址

        String MQTT_PATH = sp.getString("MQTT_ADD", mqtt_address);
        URL_help.getInstance().setEmqtt_address(MQTT_PATH);//MQTT地址

        String TCP_PATH = sp.getString("TCP_ADD",tcp_address);
        URL_help.getInstance().setTcp_address(TCP_PATH);//tcp地址

        /**侧拉菜单list数据添加*/
        list = new ArrayList<ContentModel>();
        list.add(new ContentModel(0, "菜单", 0));
        list.add(new ContentModel(R.drawable.fty_net, "网络设置", 1));
        list.add(new ContentModel(R.drawable.fty_center, "用户中心", 2));
        list.add(new ContentModel(R.drawable.fty_manager, "了解天气", 3));
        list.add(new ContentModel(R.drawable.tab_server_p, "关于我们", 4));

		main_adapter = new Main_GridviewAdp(this);
    }

    @Override
    public void initView() {
        DisplayMetrics  dm = new DisplayMetrics();
        //取得窗口属性
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //窗口的宽度
        width = dm.widthPixels;

        init_popupWindow_1();//右边菜单栏弹出

        //按压返回键时弹出退出选择框；
        view = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.app_exit, null);
        popupWindow = new PopupHelper().getWindow_ALLWRAP(view,
                getApplicationContext());
        view.findViewById(R.id.button_cancel).setOnClickListener(this);
        view.findViewById(R.id.button_submit).setOnClickListener(this);
        popupWindow.setWidth((int) (WidgetHelper.getWindowWidth(this) * 0.8));

        //侧拉菜单list
        listView = (ListView) findViewById(R.id.left_listview);
        adapter = new ContentAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        //添加设备按钮
        findViewById(R.id.popupwindow_1).setOnClickListener(this);

        xRefreshView = (XRefreshView)findViewById(R.id.xRefreshView);
        // 设置是否可以下拉刷新
        xRefreshView.setPullRefreshEnable(true);
        // 设置是否可以上拉加载
        xRefreshView.setPullLoadEnable(false);
        // 设置上次刷新的时间
        xRefreshView.restoreLastRefreshTime(xRefreshView.getLastRefreshTime());
        // 设置时候可以自动刷新
        xRefreshView.setAutoRefresh(false);
        xRefreshView.setXRefreshViewListener(this);

        mPullToRefreshGridview = (GridView) findViewById(R.id.pull_to_refresh_gridview);
        mPullToRefreshGridview.setOnItemClickListener(new Myitemclick(this));

        //菜单按钮
        findViewById(R.id.menu_but).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        drawerLayout.setScrimColor(0x00FFFFFF);
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            // 打开菜单的时候，先执行onDrawerStateChanged，然后不断执行onDrawerSlide，第三步会执行onDrawerOpened，最后执行onDrawerStateChanged
            // 关闭菜单的时候，先执行onDrawerStateChanged，然后不断执行onDrawerSlide，第三步会执行onDrawerClosed，最后执行onDrawerStateChanged
            @Override
            public void onDrawerStateChanged(int newState) {
                Log.i("lenve", "onDrawerStateChanged");
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                slideAnim(drawerView, slideOffset);
                Log.i("lenve", "onDrawerSlide");
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Log.i("lenve", "onDrawerOpened");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                Log.i("lenve", "onDrawerClosed");
            }
        });

		get_loginMsg(1);
    }

    @Override
    public void onRefresh() {
        login_mode = "Already";
        get_loginMsg(2);
    }

	@Override
	public void onRefresh(boolean isPullDown) {

	}

	@Override
    public void onLoadMore(boolean isSilence) {

    }

    @Override
    public void onRelease(float direction) {

    }

    @Override
    public void onHeaderMove(double headerMovePercent, int offsetY) {

    }
	
	/**获取登录用户权限范围内的建筑信息*/
	private void get_loginMsg(int handle_what){
		if(login_mode.equals("Already")){//已经登录过的账户
			SharedPreferences sp1 = getSharedPreferences("Customer_Infor", MODE_PRIVATE);//存储用户登录的信息，包括存储微信unionid和手机号码
			String flag = sp1.getString("flag", null);
			String cust = sp1.getString(flag, null);
			String para = flag;
			if(flag.equals("unionid")){
				para = "weixin";
			}
			//http://218.17.157.121:4003/users/getUser
			getRequestGet(URL_help.getInstance().getUser_address()+"/users/getUser","flag="+para+"&"+flag+"="+cust,handle_what);
			if(handle_what==1){
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.show();
			}
		}else{
			user_Msg_json(login_msg);
		}
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			try{
				super.handleMessage(msg);
				String bundle_msg = msg.getData().getString("buildMsg");
				if(bundle_msg.equals("exception")){
					Toast.makeText(MainActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
					switch(msg.what){
					case 1:
						if(progressDialog.isShowing()){
							progressDialog.dismiss();
						}
						break;
					case 2:
						xRefreshView.stopRefresh();
						break;
					}
					//user_Msg_json("exception");
					return;
				}
				switch(msg.what){
				case 0:
					if(bundle_msg!=null){
						String buildid = bundle_msg.split("/")[0];
						String buildname = bundle_msg.split("/")[1];
						for(int i=0;i<models.size();i++){
							if(models.get(i).getBuilding_id().equals(buildid)){
								models.get(i).setBuild_name(buildname);
								main_adapter.notifyDataSetChanged();
							}
						}
					}
					break;
				case 1:
					progressDialog.dismiss();
					if(bundle_msg!=null){
						user_Msg_json(bundle_msg);
					}
					break;
				case 2:
					xRefreshView.stopRefresh();
					if(bundle_msg!=null){
						user_Msg_json(bundle_msg);
					}
					break;
				}

			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	};
	
	@Override
	public void requestShow(String msg) {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.what = 0;
	    Bundle bundle = new Bundle();
	    bundle.putString("buildMsg", msg.split(";")[0]);
	    message.setData(bundle);
	    MainActivity.this.handler.sendMessage(message);
	}
	
	private void user_Msg_json(String login_msg){/**
	[{"userid":"17","username":"dyd","tel":"17876148387","dptid":"00010001","departmentname":"系统组",
	"createtime":"2017-02-17 09:49:50","updatetime":null,"role":[{"buildingid":"000100010001",
	"roleid":"observer"},{"buildingid":"000100020001","roleid":"manager"},
	{"buildingid":"000100030001","roleid":"analyser"},{"buildingid":"000100030002","roleid":"controller"}]}]
	 */
		try{
			requestInterface = new RequestInterface(this);
			models.clear();
			System.out.println(login_msg);
			if(login_msg.equals("[]")||login_msg.equals("exception")){
				Main_GvModel model = new Main_GvModel("0",R.drawable.build_1,"visitor","访客房间","visitor",width);
				models.add(model);
			}else{
				Main_GvModel model = new Main_GvModel("0",R.drawable.build_1,"visitor","访客房间","visitor",width);
				models.add(model);
				
				JSONArray json_array = JSONObject.parseArray(login_msg);
		        JSONObject msg_bject = (JSONObject) json_array.get(0);
		        if(json_array.size()==1){
			        String userid = msg_bject.getString("jsonobject");
			        String username = msg_bject.getString("username");
			        String tel = msg_bject.getString("tel");
			        String dptid = msg_bject.getString("dptid");
			        String departmentname = msg_bject.getString("departmentname");
			        String createtime = msg_bject.getString("createtime");
			                
			        //role角色权限信息
			        JSONArray role_array = JSONArray.parseArray(msg_bject.getString("role"));
			        for(int i=0; i<role_array.size(); i++){
			        	JSONObject role_msg = JSONObject.parseObject(role_array.get(i).toString());
			        	String buildingid = role_msg.getString("buildingid");
			        	String roleid = role_msg.getString("roleid");
			        	System.out.println(buildingid);
			        	
			        	Main_GvModel model0 = new Main_GvModel(buildingid,getBuildRes(buildingid),buildingid,"",roleid,width);
						models.add(model0);
						
						requestInterface.getTcp_Help("building/"+buildingid);
			        }
			        URL_help.getInstance().setMain_GvModels(models);
		        }

			}
			main_adapter.setGridviewAdp(models);
	        mPullToRefreshGridview.setAdapter(main_adapter);
		}catch(Exception e){
			e.printStackTrace();
		}  
	}
	
	public void getRequestGet(final String url, final String para,final int handle_what) {
		// TODO Auto-generated method stub
		 new Thread(){
	    	  public void run(){
	    		  try{
	    			  System.out.println(url);
	    			  String result = HttpRequest.sendGet(url, para);
	    			  Message message = new Message();
	    			  message.what = handle_what;
	    			  Bundle bundle = new Bundle();
	    			  bundle.putString("buildMsg", result);
	    			  message.setData(bundle);
	    			  MainActivity.this.handler.sendMessage(message);
	    			  
	    		  }catch(Exception e){
	    			  Message message = new Message();
	    			  Bundle bundle = new Bundle();
	    			  message.what = handle_what;
	    			  bundle.putString("buildMsg", "exception");
	    			  message.setData(bundle);
	    			  MainActivity.this.handler.sendMessage(message);
	    			  e.printStackTrace();
	    		  }
	    		 
	    	  }
	      }.start();
		
	}
	
	
	/**获取建筑在主界面的显示图片*/
	private int getBuildRes(String id){
		int resource = 0;
		try{
			if(id.length() == 16){
				resource = R.drawable.build_1;
			}else{
				Random ran = new Random();
				int i = ran.nextInt(3);
				switch(i){
				case 0:
					resource = R.drawable.build_2;
					break;
				case 1:
					resource = R.drawable.build_3;
					break;
				case 2:
					resource = R.drawable.build_4;
					break;
					default:
						resource = R.drawable.build_2;
						break;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return resource;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub  
		super.onConfigurationChanged(newConfig);
	}

	
	/**手势监听*/
	private GestureDetector.OnGestureListener mylistener = new GestureDetector.SimpleOnGestureListener(){

//		@Override
//		public boolean onDown(MotionEvent e) {
//			// TODO Auto-generated method stub
//			return true;
//		}
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			try{
				float xExcusion =e2.getX() - e1.getX();
				float yExcusion =e2.getY() - e1.getY();
				if(xExcusion>0 & Math.abs(xExcusion)>Math.abs(yExcusion)){//左滑
					drawerLayout.openDrawer(Gravity.LEFT);
				}else if(Math.abs(xExcusion)<Math.abs(yExcusion)){//上下滑动则释放监听到的事件；
					return false;
				}else{
					drawerLayout.closeDrawer(Gravity.LEFT);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return true;
		}
		
	};
	
	
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		// TODO Auto-generated method stub
		return gesturedelector.onTouchEvent(event);
	}


	
	private void init_popupWindow_1(){
		View view_1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_main_menu, null);
		popupWindow_1 = new PopupHelper().getWindow_ALLWRAP(view_1,getApplicationContext());
		view_1.findViewById(R.id.menu_btn_1).setOnClickListener(this);//系统设置
		view_1.findViewById(R.id.menu_btn_2).setOnClickListener(this);//用户中心
		
	}
	
	/**实现侧拉菜单时的动画效果*/
	private void slideAnim(View drawerView, float slideOffset) {
        View contentView = drawerLayout.getChildAt(0);
        // slideOffset表示菜单项滑出来的比例，打开菜单时取值为0->1,关闭菜单时取值为1->0
        float scale = 1 - slideOffset;
        float rightScale = 0.8f + scale * 0.2f;
        float leftScale = 1 - 0.3f * scale;

        ViewHelper.setScaleX(drawerView, leftScale);
        ViewHelper.setScaleY(drawerView, leftScale);
        ViewHelper.setAlpha(drawerView, 0.6f + 0.4f * (1 - scale));
        ViewHelper.setTranslationX(contentView, drawerView.getMeasuredWidth()
                * (1 - scale));
        ViewHelper.setPivotX(contentView, 0);
        ViewHelper.setPivotY(contentView, contentView.getMeasuredHeight() / 2);
        contentView.invalidate();
        ViewHelper.setScaleX(contentView, rightScale);
        ViewHelper.setScaleY(contentView, rightScale);
	}



	/**菜单item监听事件*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int id,
			long arg3) {
		// TODO Auto-generated method stub
		switch ((int) id) {
		case 1:
			startActivity(new Intent(MainActivity.this,UrlSettinAty.class));
			break;
		case 2:
			startActivity(new Intent(MainActivity.this,UserCenterAty.class));
			break;
		case 3:
			//startActivity(new Intent(MainActivity.this,WeatherActivity.class));
			break;
		case 4:
			startActivity(new Intent(MainActivity.this,About_Activity.class));
			break;
		default:
			break;
		}
		drawerLayout.closeDrawer(Gravity.LEFT);
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.popupwindow_1://跳转到添加设备
			popupWindow_1.showAtLocation(v, Gravity.TOP|Gravity.RIGHT, 0, 0);
			break;
		case R.id.button_cancel://选择取消
			popupWindow.dismiss();
			break;
		case R.id.button_submit://选择退出
			System.exit(0); 
			break;
		case R.id.menu_btn_1://系统设置
			startActivity(new Intent(MainActivity.this,UrlSettinAty.class));
			popupWindow_1.dismiss();
			break;
		case R.id.menu_btn_2://用户中心
			startActivity(new Intent(MainActivity.this,UserCenterAty.class));
			popupWindow_1.dismiss();
			break;
		default:
			break;
		}
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {//手机返回按钮触发事件
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			popupWindow.showAtLocation(mPullToRefreshGridview, Gravity.CENTER, 0, 0);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		try{
			SharedPreferences sp_login = getSharedPreferences("Customer_Infor", MODE_PRIVATE);//存储用户登录的信息，包括存储微信unionid和手机号码
			String flag = sp_login.getString("flag", null);
			if(flag==null){
				models.clear();
				Main_GvModel model = new Main_GvModel("0",R.drawable.build_1,"visitor","访客房间","visitor",width);
				models.add(model);
				main_adapter.notifyDataSetChanged();
			}
			StartServices();
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	/** 打开页面 */
    class Myitemclick implements OnItemClickListener {
        private Context context;

        public Myitemclick(Context context) {
            super();
            this.context = context;
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
            if(arg2==0){//访客房间
                Intent intent = new Intent(MainActivity.this, Activity_Visitor.class);
                intent.putExtra("roleid", "visitor");
                intent.putExtra("buildid", "visitor");
                intent.putExtra("buildname", "访客房间");
                context.startActivity(intent);
            }
            else if (v.getTag() != null) {
                Main_GvModel model = models.get(arg2);
                switch(model.getBuilding_id().length()){
                    case 4:
                        Intent intent3 = new Intent(MainActivity.this, Activity_Build_Garden.class);
                        intent3.putExtra("roleid", model.getBuild_roleid());
                        intent3.putExtra("buildid", model.getBuilding_id());
                        intent3.putExtra("buildname", model.getBuild_name());
                        context.startActivity(intent3);
                        break;
                    case 8:
                        Intent intent1 = new Intent(MainActivity.this, FragmentActivity_Build.class);
                        intent1.putExtra("roleid", model.getBuild_roleid());
                        intent1.putExtra("buildid", model.getBuilding_id());
                        intent1.putExtra("buildname", model.getBuild_name());
                        context.startActivity(intent1);
                        break;
                    case 12:
                        Intent intent2 = new Intent(MainActivity.this, FragmentActivity_Build.class);
                        intent2.putExtra("roleid", model.getBuild_roleid());
                        intent2.putExtra("buildid", model.getBuilding_id());
                        intent2.putExtra("buildname", model.getBuild_name());
                        context.startActivity(intent2);
                        break;
                    case 16:
                        Intent intent0 = new Intent(MainActivity.this, FragmentActivity_Room.class);
                        intent0.putExtra("roleid", model.getBuild_roleid());
                        intent0.putExtra("buildid", model.getBuilding_id());
                        intent0.putExtra("buildname", model.getBuild_name());
                        context.startActivity(intent0);
                        break;

                }

            }
        }
    }
		

		
		
    /**
     * 启动服务*/
    @SuppressLint("NewApi")
    private void StartServices(){
        try{
            //获取设备唯一id,可能有些设备获取为unknow，在一般情况下暂时先使用这个id
            String SerialNumber = android.os.Build.SERIAL;

            //Toast.makeText(getApplicationContext(), SerialNumber, Toast.LENGTH_LONG).show();
            // 启动mqtt推送服务，接收推送消息；
            mDeviceID = SerialNumber;//"yzq125";
            SharedPreferences sp = getSharedPreferences(PushService.TAG,MODE_PRIVATE);
            Editor editor = sp.edit();
            editor.putString(PushService.PREF_DEVICE_ID, mDeviceID);
            editor.commit();


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        try{
            super.onDestroy();
            unregisterReceiver(receiver);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**注册接收广播*/
    public void RegisterBroad(){
        //界面更新广播接收;
        new Thread(){
            public void run(){
                try{
                    receiver = new ReceiveTool();
                    IntentFilter intentfilter = new IntentFilter("Intent.UPDATA");
                    registerReceiver(receiver, intentfilter);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    class ReceiveTool extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            try{
            if(intent.getStringExtra("msg").indexOf("UPDATA")>=0){//主动上报
                //Toast.makeText(getApplicationContext(), intent.getStringExtra("msg"), Toast.LENGTH_LONG).show();
            }else if(intent.getStringExtra("msg").indexOf("warningmsg")>=0){

            }else if(intent.getStringExtra("msg").indexOf("statusMsg")>=0) {//登录包  statusMsg/类型#网关号#0（或1）	0：下线    1：上线
                            //statusMsg/0a0003ahup#000663007098#1    //statusMsg/北电以太网-Zigbee采集器#测试办公物联#系统部#online
                String msg = intent.getStringExtra("msg").split("/")[1];
//					/**存储通讯质量网关在线*/
//					SharedPreferences sp = getSharedPreferences("cp_state", MODE_PRIVATE);
//					Editor editor = sp.edit();
//					editor.putString(msg.split("#")[1],"1");
//					editor.commit();

                switch(msg.split("#")[3]){
                case "offline":
                    Toast.makeText(getApplicationContext(), msg.split("#")[2]+msg.split("#")[0]+":"+msg.split("#")[1]+"下线!", Toast.LENGTH_LONG).show();
                    break;
                case "online":
                    Toast.makeText(getApplicationContext(), msg.split("#")[2]+msg.split("#")[0]+":"+msg.split("#")[1]+"上线!", Toast.LENGTH_LONG).show();
                    break;
                }
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        }

    }



	
}
