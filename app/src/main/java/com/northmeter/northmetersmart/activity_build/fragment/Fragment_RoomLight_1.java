package com.northmeter.northmetersmart.activity_build.fragment;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.andview.refreshview.XRefreshView;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity_build.I.IRequestSet;
import com.northmeter.northmetersmart.activity_build.I.IRequestShow;
import com.northmeter.northmetersmart.activity_build.I.RequestInterface;
import com.northmeter.northmetersmart.activity_build.I.RequestInterfaceSet;
import com.northmeter.northmetersmart.adp.RoomLight_TacticsAdapter;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.model.RoomLight_TacticsModel;

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
 * 灯光子页面-场景*/
public class Fragment_RoomLight_1 extends Fragment implements XRefreshView.XRefreshViewListener,IRequestShow,IRequestSet {
	private View view;
	private List<RoomLight_TacticsModel> models;
	private RoomLight_TacticsAdapter tacticsAdapter;
	private int width;
	private String buildid,buildname,roleid;
	private CustomProgressDialog progressDialog;

	private GridView mPullToRefreshGridview;
	private XRefreshView xRefreshView;
	
	private RequestInterface requestInterface;
	private RequestInterfaceSet requestInterfaceSet;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_roomlight_1, null);
		requestInterface = new RequestInterface(this);
		requestInterfaceSet = new RequestInterfaceSet(this);
		init_view();
		getTactics();
		return view;
	}
	
	private void init_view(){
		// 获取activity传入的数据  
		Intent intent = getActivity().getIntent();
		buildid = intent.getStringExtra("buildid");
		buildname = intent.getStringExtra("buildname");	
		roleid = intent.getStringExtra("roleid");
		
		DisplayMetrics  dm = new DisplayMetrics();  
        //取得窗口属性  
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);  
        //窗口的宽度  
        width = dm.widthPixels;
		
		mPullToRefreshGridview = (GridView) view.findViewById(R.id.pull_gridview);
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

        models = new ArrayList<RoomLight_TacticsModel>();
        tacticsAdapter = new RoomLight_TacticsAdapter(getActivity());
        tacticsAdapter.setRoomLight_TacticsAdapter(models);
        mPullToRefreshGridview.setAdapter(tacticsAdapter);
        
        progressDialog = CustomProgressDialog.createDialog(getActivity());
	    progressDialog.show();
	}

	@Override
	public void onRefresh() {
		getTactics();
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
					String  tacticsId = models.get(arg2).getTacticsId();
					triggerTactics(tacticsId);
				}
	
			}catch(Exception e){
			e.printStackTrace();
			}
		}
	}
	
	/**获取场景列表*/
	private void getTactics(){
		requestInterface.getHttpRequestGetSet_Cookie("http://10.168.1.165:8081/tactics/getTactics", "buildingId="+buildid, URL_help.getInstance().getSet_Cookie());
	}
	
	/**立即执行任务*/
	private void triggerTactics(String  tacticsId){
		progressDialog.show();
		requestInterfaceSet.getHttpRequestPostSet_Cookie("http://10.168.1.165:8081/tactics/triggerTactics", "{\"tacticsId\":\""+tacticsId+"\"}", URL_help.getInstance().getSet_Cookie());
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			try{
				super.handleMessage(msg);
				String bundle_msg = msg.getData().getString("buildMsg");
				if(progressDialog.isShowing()){
					progressDialog.dismiss();
				}
				if(bundle_msg.equals("exception")){
					Toast.makeText(getActivity(), "网络连接异常", Toast.LENGTH_LONG).show();
					xRefreshView.stopRefresh();
					return;
				}
				
				int what_code = msg.what;
				switch(what_code){
				case 0:
					if(bundle_msg!=null){
						models.clear();
						JSONArray jsonarray = JSONArray.parseArray(bundle_msg);
						for(int i=0;i<jsonarray.size();i++){
							JSONObject jsonobject = JSONObject.parseObject(jsonarray.get(i).toString());
							RoomLight_TacticsModel model = new RoomLight_TacticsModel();
					        model.setTacticsId(jsonobject.getString("tacticsId"));
					        model.setTacticsName(jsonobject.getString("tacticsName"));
					        model.setBuildingId(jsonobject.getString("buildingId"));
					        model.setBuildingName(jsonobject.getString("buildingName"));
					        model.setStartDate(jsonobject.getString("startDate"));
					        model.setEndDate(jsonobject.getString("endDate"));
					        model.setEffective(jsonobject.getBoolean("isEffective"));
					        model.setWidth(width);
					        models.add(model);
						}
						tacticsAdapter.notifyDataSetChanged();
						xRefreshView.stopRefresh();
					}
					break;
				case 1:
					if(bundle_msg!=null||!bundle_msg.equals("[]")){
						JSONObject json = JSONObject.parseObject(bundle_msg);
						for(Object key:json.keySet()){
							Object v = json.get(key);
							Toast.makeText(getActivity(), v.toString(), Toast.LENGTH_SHORT).show();
						}
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
	    Bundle bundle = new Bundle();
	    message.what = 0;
	    bundle.putString("buildMsg", msg);
	    message.setData(bundle);
	    Fragment_RoomLight_1.this.handler.sendMessage(message);
		
	}

	@Override
	public void showRequestSet(String result) {
		// TODO Auto-generated method stub
		Message message = new Message();
	    Bundle bundle = new Bundle();
	    message.what = 1;
	    bundle.putString("buildMsg", result);
	    message.setData(bundle);
	    Fragment_RoomLight_1.this.handler.sendMessage(message);

	}

}
