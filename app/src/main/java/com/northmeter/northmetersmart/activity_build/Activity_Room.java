package com.northmeter.northmetersmart.activity_build;

import java.util.ArrayList;
import java.util.List;


import com.andview.refreshview.XRefreshView;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity.AirQualityQetectorAty;
import com.northmeter.northmetersmart.activity.MessageAty;
import com.northmeter.northmetersmart.activity.MessageAty_Center;
import com.northmeter.northmetersmart.activity.TVAty;
import com.northmeter.northmetersmart.activity.TVAty_Four_street;
import com.northmeter.northmetersmart.activity.TVAty_ZGB;
import com.northmeter.northmetersmart.activity_build.I.IRequestShow;
import com.northmeter.northmetersmart.activity_build.I.IRoomDeviceShow;
import com.northmeter.northmetersmart.activity_build.I.RequestInterface;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.order.Type_Entity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

/**最下级建筑，里面显示房间的设备图标*/
public class Activity_Room extends Fragment implements XRefreshView.XRefreshViewListener,OnClickListener,IRequestShow {
	private PopupWindow popupWindow_1;
	private String buildid,buildname,roleid;
	private Room_GridviewAdp roomAdapter;
	private List<RoomModel> models;
	private int width;
	private String bundle_msg;
	private CustomProgressDialog progressDialog;
	private RequestInterface requestInterface;
	
	private GridView mPullToRefreshGridview;
	private IRoomDeviceShow iRoomDeviceShow;
	private XRefreshView xRefreshView;
	
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.activity_room, null);
		try{	
			DisplayMetrics  dm = new DisplayMetrics();  
	        //取得窗口属性  
	        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);  
	        //窗口的宽度  
	        width = dm.widthPixels;
			
			
			Intent intent = getActivity().getIntent();
			buildid = intent.getStringExtra("buildid");
			buildname = intent.getStringExtra("buildname");
			roleid = intent.getStringExtra("roleid");
			
			requestInterface = new RequestInterface(this);
			iRoomDeviceShow = new FragmentActivity_Room();
			
			init_view();
			
			getRoomDevice();
		}catch(Exception e){
			e.printStackTrace();
		}
		return view;
		
	}
	
	private void init_view(){
		try{
			roomAdapter = new Room_GridviewAdp(getActivity());
			mPullToRefreshGridview = (GridView) view.findViewById(R.id.gridview_1);
			mPullToRefreshGridview.setOnItemClickListener(new Myitemclick(getActivity()));

			xRefreshView = (XRefreshView)view.findViewById(R.id.xRefreshView);
			// 设置是否可以下拉刷新
			xRefreshView.setPullRefreshEnable(true);
			// 设置是否可以上拉加载
			xRefreshView.setPullLoadEnable(false);
			// 设置上次刷新的时间
			xRefreshView.restoreLastRefreshTime(xRefreshView.getLastRefreshTime());
			// 设置时候可以自动刷新
			xRefreshView.setAutoRefresh(false);

			xRefreshView.setXRefreshViewListener(this);

			models = new ArrayList<RoomModel>();

			progressDialog = CustomProgressDialog.createDialog(getActivity());
		    progressDialog.show();

		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 000100020001/思创大厦4楼;0001000200010001/技术总监办公室;0001000200010002/王总办公室;0001000200010003/实验室;
	 * 0001000200010004/测试部;0001000200010005/储物室;0001000200010006/前台;0001000200010007/硬件组;0001000200010008/系统组;
	 * 0001000200010009/财务室;0001000200010010/会议室;0001000200010011/大厅;0001000200010012/测试;0001000200010013/陕西演示;
	 * 0001000200010014/小仓库;000100020001/42435c5573fe#0a0003ahup#42435c5573fe#0a00001001#101680010209526;*/
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {//0001000100010001/后勤办公楼1楼101房;0001000100010001/150721023218#0a0001aa7k#150721023218#0a0003ahup#5a4ce2e221e7;
			try{
				super.handleMessage(msg);
				Message message_msg=new Message();
				Bundle bundle = new Bundle();
				bundle_msg = msg.getData().getString("buildMsg");
				iRoomDeviceShow.showRoomDevice(bundle_msg);
				
				if(bundle_msg.equals("exception")){
					Toast.makeText(getActivity(), "网络连接异常", Toast.LENGTH_LONG).show();
					xRefreshView.stopRefresh();
					progressDialog.dismiss();
					return;
				}
				if(bundle_msg!=null){
					models.clear();
					String[] list = bundle_msg.split(";");
					for(int i=1;i<list.length;i++){
						String buildid = list[i].split("/")[0];
						String roommsg = list[i].split("/")[1];
						if(roommsg.split("#").length>1){//如果是从其他设备选项进来的话，查询的是建筑楼层和设备0001000200010014/小仓库;000100020001/42435c5573fe#0a0003ahup#42435c5573fe#0a00001001#101680010209526;
							if(roommsg.split("#")[1].equals(Type_Entity.Gateway_type)||roommsg.split("#")[1].equals(Type_Entity.Socket_type)||
									roommsg.split("#")[1].equals(Type_Entity.Four_street_control)||
									roommsg.split("#")[1].equals(Type_Entity.Split_air_conditioning)||
									roommsg.split("#")[1].equals(Type_Entity.Central_air_conditioning)||
									roommsg.split("#")[1].equals(Type_Entity.Air_Quality_Qetector)){
								RoomModel model  = new RoomModel();
								model.setId(buildid);
								model.setName(roommsg.split("#")[0]);
								model.setType(roommsg.split("#")[1]);
								model.setTableNum(roommsg.split("#")[2]);
								model.setMasternode_type(roommsg.split("#")[3]);
								model.setMasternode_num(roommsg.split("#")[4]);
								model.setResource(Type_Entity.getResource(roommsg.split("#")[1]));
								model.setWidth(width);
								models.add(model);

							}
						}
					
					}
				}
				roomAdapter.setRoom_GridviewAdp(models);
				mPullToRefreshGridview.setAdapter(roomAdapter);
				xRefreshView.stopRefresh();
				progressDialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};
	
	
	private void getRoomDevice(){
		requestInterface.getTcp_Help("building/"+buildid);

	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void onRefresh() {
		getRoomDevice();
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

	/** 打开页面 */
	class Myitemclick implements OnItemClickListener {
		private Context context;

		public Myitemclick(Context context) {
			super();
			this.context = context;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
			try{
				if (v.getTag() != null) {
					RoomModel model = models.get(arg2);
					switch(model.getType()){
					case Type_Entity.Gateway_type://zigbee网关
						Intent intent1 = new Intent(getActivity(),TVAty_ZGB.class);
						intent1.putExtra("mac", model.getTableNum());
						intent1.putExtra("name", model.getName());
						intent1.putExtra("type", model.getType());
						intent1.putExtra("buildid",buildid);
						intent1.putExtra("roleid", roleid);
						context.startActivity(intent1);
						break;
					case Type_Entity.Split_air_conditioning://分体空调控制器
						Intent intent2 = new Intent(getActivity(),MessageAty.class);
						intent2.putExtra("name", model.getName());
						intent2.putExtra("mac", model.getTableNum());
						intent2.putExtra("type", model.getType());
						intent2.putExtra("buildid",buildid);
						intent2.putExtra("roleid", roleid);
						context.startActivity(intent2);
						break;
					case Type_Entity.Central_air_conditioning://中央空调控制器
						Intent intent5 = new Intent(getActivity(),MessageAty_Center.class);
						intent5.putExtra("name", model.getName());
						intent5.putExtra("mac", model.getTableNum());
						intent5.putExtra("type", model.getType());
						intent5.putExtra("buildid",buildid);
						intent5.putExtra("roleid", roleid);
						context.startActivity(intent5);
						break;
					case Type_Entity.Socket_type://zigbee插座
						Intent intent3 = new Intent(getActivity(),TVAty.class);
						intent3.putExtra("name", model.getName());
						intent3.putExtra("mac", model.getTableNum());
						intent3.putExtra("type", model.getType());
						intent3.putExtra("buildid",buildid);
						intent3.putExtra("roleid", roleid);
						context.startActivity(intent3);
						break;
					case Type_Entity.Four_street_control://zigbee四路灯控
						Intent intent3_1 = new Intent(getActivity(),TVAty_Four_street.class);
						intent3_1.putExtra("name", model.getName());
						intent3_1.putExtra("mac", model.getTableNum());
						intent3_1.putExtra("type", model.getType());
						intent3_1.putExtra("buildid",buildid);
						intent3_1.putExtra("roleid", roleid);
						context.startActivity(intent3_1);
						break;
					case Type_Entity.Air_Quality_Qetector://空气质量检测仪
						Intent intent3_2 = new Intent(getActivity(),AirQualityQetectorAty.class);
						intent3_2.putExtra("name", model.getName());
						intent3_2.putExtra("mac", model.getTableNum());
						intent3_2.putExtra("type", model.getType());
						intent3_2.putExtra("buildid",buildid);
						intent3_2.putExtra("roleid", roleid);
						context.startActivity(intent3_2);
						break;
						
					}
				}
	
			}catch(Exception e){
			e.printStackTrace();
			}
		}
	}

	@Override
	public void requestShow(String msg) {
		// TODO Auto-generated method stub
		if(!getActivity().isFinishing()){
			Message message = new Message();
		    Bundle bundle = new Bundle();
		    bundle.putString("buildMsg", msg);
		    message.setData(bundle);
		    Activity_Room.this.handler.sendMessage(message);
		}
		
	}
	
	
}
