package com.northmeter.northmetersmart.activity_build.fragment;

import java.util.ArrayList;
import java.util.List;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.andview.refreshview.XRefreshView;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity_build.I.ILightDevieShow;
import com.northmeter.northmetersmart.activity_build.I.RoomLightDevicePresenter;
import com.northmeter.northmetersmart.adp.RoomLight_DeviceAdapter;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.model.RoomLight_DeviceModel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import androidx.fragment.app.Fragment;

/**
 * 灯光子页面-单灯*/
public class Fragment_RoomLight_3 extends Fragment implements XRefreshView.XRefreshViewListener,ILightDevieShow {
	private View view;
	private List<RoomLight_DeviceModel> models;
	private RoomLight_DeviceAdapter deviceAdapter;
	private int width;
	private String buildid,buildname,roleid;
	private int page=0;
	private int xRefreshType;

	private GridView mPullToRefreshGridview;
	private XRefreshView xRefreshView;
	
	private RoomLightDevicePresenter roomLightDevicePresenter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_roomlight_1, null);
		init_view();
		getDevice_onRefresh();
		return view;
	}
	
	private void init_view(){
		// 获取activity传入的数据
		Intent intent = getActivity().getIntent();
		buildid = intent.getStringExtra("buildid");
		buildname = intent.getStringExtra("buildname");	
		roleid = intent.getStringExtra("roleid");
		
		roomLightDevicePresenter = new RoomLightDevicePresenter(this);
		
		DisplayMetrics  dm = new DisplayMetrics();  
        //取得窗口属性  
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);  
        //窗口的宽度  
        width = dm.widthPixels;

		xRefreshView = (XRefreshView)view.findViewById(R.id.xRefreshView);
		// 设置是否可以下拉刷新
		xRefreshView.setPullRefreshEnable(true);
		// 设置是否可以上拉加载
		xRefreshView.setPullLoadEnable(true);
		// 设置上次刷新的时间
		xRefreshView.restoreLastRefreshTime(xRefreshView.getLastRefreshTime());
		// 设置时候可以自动刷新
		xRefreshView.setAutoRefresh(false);
		xRefreshView.setXRefreshViewListener(this);
		
		mPullToRefreshGridview = (GridView) view.findViewById(R.id.pull_gridview);
		mPullToRefreshGridview.setOnItemClickListener(new Myitemclick(getActivity()));
        models = new ArrayList<RoomLight_DeviceModel>();
        deviceAdapter = new RoomLight_DeviceAdapter(getActivity());
        deviceAdapter.setRoomLight_DeviceAdapter(models);
		mPullToRefreshGridview.setAdapter(deviceAdapter);
	}

	@Override
	public void onRefresh() {
		getDevice_onRefresh();
	}

	@Override
	public void onRefresh(boolean isPullDown) {

	}

	@Override
	public void onLoadMore(boolean isSilence) {
		getDevice_onLoadMore();
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
					RoomLight_DeviceModel model = models.get(arg2);
					Intent intent = new Intent(getActivity(),Activity_LightControl.class);
					intent.putExtra("title", model.getEqptName());
					startActivity(intent);
					
					IShowIntentData iShowIntentData = (IShowIntentData) new Activity_LightControl();
					iShowIntentData.showIntentData(model);
					
					
				}
	
			}catch(Exception e){
			e.printStackTrace();
			}
		}
	}
	
	public interface IShowIntentData{
		void showIntentData(RoomLight_DeviceModel deviceModel);
	}
	
	
	/**下拉刷新*/
	private void getDevice_onRefresh(){
		xRefreshType = 0;
		page = 0;
		String para  = "buildingId="+buildid+"&deviceType[0]="+"pwm"+"&getChild="+"1"+"&page="+page+"&pageSize=10";
		System.out.println(para);
		roomLightDevicePresenter.getHttpRequestGetSet_Cookie(xRefreshType,"http://10.168.1.165:8081/device/getMeter", para, URL_help.getInstance().getSet_Cookie());
	}
	
	/**上拉加载更多*/
	private void getDevice_onLoadMore(){
		xRefreshType = 1;
		String para  = "buildingId="+buildid+"&deviceType[0]="+"pwm"+"&getChild="+"1"+"&page="+(++page)+"&pageSize=10";
		System.out.println(para);
		roomLightDevicePresenter.getHttpRequestGetSet_Cookie(xRefreshType,"http://10.168.1.165:8081/device/getMeter", para, URL_help.getInstance().getSet_Cookie());
	}
	
	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			try{
				super.handleMessage(msg);
				String bundle_msg = msg.getData().getString("buildMsg");
				int xRefreshType = msg.getData().getInt("xRefreshType");
			
				if(bundle_msg.equals("exception")){
					Toast.makeText(getActivity(), "网络连接异常", Toast.LENGTH_LONG).show();
					xRefreshView.stopRefresh();
					return;
				}
				
				if(msg!=null||!msg.equals("[]")){
					JSONObject jsonobject = JSONObject.parseObject(bundle_msg);
					String returnCode = jsonobject.getString("returnCode");//返回状态字
					if(returnCode.equals("200")){
						String countRow = jsonobject.getString("countRow");//总条数
						String data = jsonobject.getString("data");//照明灯控模块Json数组
						
						if(xRefreshType==0){
							models.clear();
						}
						
						
						JSONArray jsonarray = JSONArray.parseArray(data);
						for(int i=0;i<jsonarray.size();i++){
							JSONObject jsonDevice = JSONObject.parseObject(jsonarray.get(i).toString());
							RoomLight_DeviceModel model = new RoomLight_DeviceModel();
							model.setEqptIdCode(jsonDevice.getString("eqptIdCode"));
							model.setEqptName(jsonDevice.getString("eqptName"));
							model.setEqptType(jsonDevice.getString("eqptType"));
							model.setEqptTypeName(jsonDevice.getString("eqptTypeName"));
							model.setEqptBuildingId(jsonDevice.getString("eqptBuildingId"));
							model.setEqptBuildingName(jsonDevice.getString("eqptBuildingName"));
							model.setEqptShortNum(jsonDevice.getString("eqptShortNum"));
							model.setEqptStatus(jsonDevice.getString("eqptStatus"));
							model.setCommport(jsonDevice.getString("commport"));
							model.setBaudrate(jsonDevice.getString("baudrate"));
							model.setCollectorId(jsonDevice.getString("collectorId"));
							model.setCollectorType(jsonDevice.getString("collectorType"));
							model.setLabel(jsonDevice.getString("label"));
							model.setTab(jsonDevice.getString("tab"));
							model.setCreateTime(jsonDevice.getString("createTime"));
							model.setWidth(width);
							
							models.add(model);
						}
						
						deviceAdapter.notifyDataSetChanged();
					}	
				}
				xRefreshView.stopRefresh();
			}catch(Exception e){
				e.printStackTrace();
				}
			}
	};


	@Override
	public void showLightDevice(int xRefreshType, String data) {
		// TODO Auto-generated method stub
		Message message = new Message();
	    Bundle bundle = new Bundle();
	    bundle.putInt("xRefreshType", xRefreshType);
	    bundle.putString("buildMsg", data);
	    message.setData(bundle);
	    Fragment_RoomLight_3.this.handler.sendMessage(message);
	}
	
	/**
	 * result======
	 * {"returnCode":"200","countRow":161,"data":[{"eqptIdCode":"031703030142","eqptName":"031703030142","commport":31,
	 * "baudrate":2400,"eqptType":"0a0001a840","eqptTypeName":"照明灯控模块","eqptBuildingName":"老化室","eqptBuildingId":"0001000100010001",
	 * "collectorId":"000200000000","collectorType":"0a0003ahup","label":"lighting","tab":"null","createTime":"2017-06-28 13:56:30","eqptStatus":"1","eqptShortNum":"3a"},
	 * 
	 * {"eqptIdCode":"031703030143","eqptName":"031703030143","commport":31,"baudrate":2400,"eqptType":"0a0001a840",
	 * "eqptTypeName":"照明灯控模块","eqptBuildingName":"老化室","eqptBuildingId":"0001000100010001","collectorId":"000200000000",
	 * "collectorType":"0a0003ahup","label":"lighting","tab":"null","createTime":"2017-06-28 13:56:29","eqptStatus":"1","eqptShortNum":"03"},
	 * 
	 * {"eqptIdCode":"031703030144","eqptName":"031703030144","commport":31,"baudrate":2400,"eqptType":"0a0001a840",
	 * "eqptTypeName":"照明灯控模块","eqptBuildingName":"老化室","eqptBuildingId":"0001000100010001","collectorId":"000200000000",
	 * "collectorType":"0a0003ahup","label":"lighting","tab":"null","createTime":"2017-06-28 13:56:30","eqptStatus":"1","eqptShortNum":"2d"},
	 * 
	 * {"eqptIdCode":"031703030146","eqptName":"031703030146","commport":31,"baudrate":2400,"eqptType":"0a0001a840",
	 * "eqptTypeName":"照明灯控模块","eqptBuildingName":"老化室","eqptBuildingId":"0001000100010001","collectorId":"000200000000",
	 * "collectorType":"0a0003ahup","label":"lighting","tab":"null","createTime":"2017-06-28 13:56:30","eqptStatus":"1","eqptShortNum":"21"},
	 * 
	 * {"eqptIdCode":"031703030147","eqptName":"031703030147","commport":31,"baudrate":2400,"eqptType":"0a0001a840",
	 * "eqptTypeName":"照明灯控模块","eqptBuildingName":"老化室","eqptBuildingId":"0001000100010001","collectorId":"000200000000",
	 * "collectorType":"0a0003ahup","label":"lighting","tab":"null","createTime":"2017-06-28 13:56:30","eqptStatus":"1","eqptShortNum":"2c"},
	 * 
	 * {"eqptIdCode":"031703030148","eqptName":"031703030148","commport":31,"baudrate":2400,"eqptType":"0a0001a840",
	 * "eqptTypeName":"照明灯控模块","eqptBuildingName":"老化室","eqptBuildingId":"0001000100010001","collectorId":"000200000000",
	 * "collectorType":"0a0003ahup","label":"lighting","tab":"null","createTime":"2017-06-28 13:56:30","eqptStatus":"1","eqptShortNum":"20"},
	 * 
	 * {"eqptIdCode":"031703030149","eqptName":"031703030149","commport":31,"baudrate":2400,"eqptType":"0a0001a840",
	 * "eqptTypeName":"照明灯控模块","eqptBuildingName":"老化室","eqptBuildingId":"0001000100010001","collectorId":"000200000000",
	 * "collectorType":"0a0003ahup","label":"lighting","tab":"null","createTime":"2017-06-28 13:56:29","eqptStatus":"1","eqptShortNum":"05"},
	 * 
	 * {"eqptIdCode":"031703030150","eqptName":"031703030150","commport":31,"baudrate":2400,"eqptType":"0a0001a840",
	 * "eqptTypeName":"照明灯控模块","eqptBuildingName":"老化室","eqptBuildingId":"0001000100010001","collectorId":"000200000000",
	 * "collectorType":"0a0003ahup","label":"lighting","tab":"null","createTime":"2017-06-28 13:56:30","eqptStatus":"1","eqptShortNum":"27"},
	 * 
	 * {"eqptIdCode":"031703030151","eqptName":"031703030151","commport":31,"baudrate":2400,"eqptType":"0a0001a840",
	 * "eqptTypeName":"照明灯控模块","eqptBuildingName":"老化室","eqptBuildingId":"0001000100010001","collectorId":"000200000000",
	 * "collectorType":"0a0003ahup","label":"lighting","tab":"null","createTime":"2017-06-28 13:56:30","eqptStatus":"1","eqptShortNum":"36"},
	 * 
	 * {"eqptIdCode":"031703030152","eqptName":"031703030152","commport":31,"baudrate":2400,"eqptType":"0a0001a840",
	 * "eqptTypeName":"照明灯控模块","eqptBuildingName":"老化室","eqptBuildingId":"0001000100010001","collectorId":"000200000000",
	 * "collectorType":"0a0003ahup","label":"lighting","tab":"null","createTime":"2017-06-28 13:56:29","eqptStatus":"1","eqptShortNum":"04"}]}*/
	
	

}
