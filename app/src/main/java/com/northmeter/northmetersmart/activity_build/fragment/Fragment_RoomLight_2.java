package com.northmeter.northmetersmart.activity_build.fragment;

import java.util.ArrayList;
import java.util.List;


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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.andview.refreshview.XRefreshView;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity_build.I.IRequestShow;
import com.northmeter.northmetersmart.activity_build.I.RequestInterface;
import com.northmeter.northmetersmart.adp.RoomLight_GroupAdapter;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.model.RoomLight_GroupModel;

/**
 * 灯光子页面--群组*/
public class Fragment_RoomLight_2 extends Fragment implements XRefreshView.XRefreshViewListener,IRequestShow {
	private View view;
	private List<RoomLight_GroupModel> models;
	private RoomLight_GroupAdapter tacticsAdapter;
	private int width;
	private String buildid,buildname,roleid;
	private TextView light_num;

	private XRefreshView xRefreshView;
	private ListView mPullToRefreshListView;
	private SeekBar seekbar;
	private CheckBox checkbox;
	
	private RequestInterface requestInterface;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_roomlight_2, null);
		try{
			requestInterface = new RequestInterface(this);
			init_view();
			init_seekBar();
			getGroup();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return view;
	}
	
	private void init_seekBar(){
		seekbar = (SeekBar) view.findViewById(R.id.seekbar_1);
		seekbar.setProgress(50);
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				 float pro = seekbar.getProgress();
		         float num = seekbar.getMax();
		         float result = (pro / num) * 100;
		         light_num.setText("亮度"+Math.round(result)+"%");
			}
		});
	}
	
	private void init_view(){
		Intent intent = getActivity().getIntent();
		buildid = intent.getStringExtra("buildid");
		buildname = intent.getStringExtra("buildname");	
		roleid = intent.getStringExtra("roleid");
		
		DisplayMetrics  dm = new DisplayMetrics();  
        //取得窗口属性  
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);  
        //窗口的宽度  
        width = dm.widthPixels;

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

		
        mPullToRefreshListView = (ListView) view.findViewById(R.id.pull_listview);
        mPullToRefreshListView.setOnItemClickListener(new Myitemclick(getActivity()));
        

        light_num = (TextView) view.findViewById(R.id.light_num);
        checkbox = (CheckBox) view.findViewById(R.id.checkbox);
        
        models = new ArrayList<RoomLight_GroupModel>();
        tacticsAdapter = new RoomLight_GroupAdapter(getActivity());
        tacticsAdapter.setRoomLight_GroupAdapter(models);
		mPullToRefreshListView.setAdapter(tacticsAdapter);    
	}

	@Override
	public void onRefresh() {
		getGroup();
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
					for(RoomLight_GroupModel model:models){
						if(models.get(arg2-1)==model){
							if(model.getCheck()){
								model.setCheck(false);
							}else{
								model.setCheck(true);
							}
						}else{
							model.setCheck(false);
						}
					}

					tacticsAdapter.notifyDataSetChanged();
				}
	
			}catch(Exception e){
			e.printStackTrace();
			}
		}
	}
	
	private void getGroup(){
		requestInterface.getHttpRequestGetSet_Cookie("http://10.168.1.165:8081/group/getGroup", "buildingId="+buildid, URL_help.getInstance().getSet_Cookie());
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			try{
				super.handleMessage(msg);
				String bundle_msg = msg.getData().getString("buildMsg");
			
				if(bundle_msg.equals("exception")){
					Toast.makeText(getActivity(), "网络连接异常", Toast.LENGTH_LONG).show();
					xRefreshView.stopRefresh();
					return;
				}
				
				if(msg!=null||!msg.equals("[]")){
					models.clear();
					JSONArray jsonarray = JSONArray.parseArray(bundle_msg);
					for(int i=0;i<jsonarray.size();i++){
						JSONObject jsonobject = JSONObject.parseObject(jsonarray.get(i).toString());
						RoomLight_GroupModel model = new RoomLight_GroupModel();
						model.setId(jsonobject.getString("id"));
						model.setGroupId(jsonobject.getString("groupId"));
						model.setGroupName(jsonobject.getString("groupName"));
						model.setBuildingId(jsonobject.getString("buildingId"));
						model.setBuildingName(jsonobject.getString("buildingName"));
						model.setCreateTime(jsonobject.getString("createTime"));
				        models.add(model);
					}
					tacticsAdapter.notifyDataSetChanged();
					xRefreshView.stopRefresh();
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
	    bundle.putString("buildMsg", msg);
	    message.setData(bundle);
	    Fragment_RoomLight_2.this.handler.sendMessage(message);
	}

}
