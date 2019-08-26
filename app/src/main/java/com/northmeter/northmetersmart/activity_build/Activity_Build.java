package com.northmeter.northmetersmart.activity_build;

import java.util.ArrayList;
import java.util.List;


import com.andview.refreshview.XRefreshView;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity_build.I.IRequestShow;
import com.northmeter.northmetersmart.activity_build.I.RequestInterface;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.model.Main_GvModel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import androidx.fragment.app.Fragment;

public class Activity_Build extends Fragment implements XRefreshView.XRefreshViewListener, OnClickListener,OnItemClickListener,IRequestShow {
	private ListView listview;
	private List<BuildModel> models;
	private Build_ListviewAdp listview_adp;
	private String buildid,buildname,roleid;
	private CustomProgressDialog progressDialog;
	private RequestInterface requestInterface;
	private int msg_what;
	private String select_buindId;

	private XRefreshView xRefreshView;
	private ListView mPullToRefreshListView;
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	    view = inflater.inflate(R.layout.activity_build, null);
	    try{
	    	Intent intent = getActivity().getIntent();
			buildid = intent.getStringExtra("buildid");
			buildname = intent.getStringExtra("buildname");	
			roleid = intent.getStringExtra("roleid");
			requestInterface = new RequestInterface(this);
			
			init_view();
			getRoomDevice(buildid,0);
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    return view;
	    
	}

	private void init_view(){
		try{
			listview_adp = new Build_ListviewAdp(getActivity());
			models = new ArrayList<BuildModel>();
			mPullToRefreshListView = (ListView) view.findViewById(R.id.listview);
			mPullToRefreshListView.setOnItemClickListener(Activity_Build.this);

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

			progressDialog=CustomProgressDialog.createDialog(getActivity());
		    progressDialog.show();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {//00010001/武汉大学后勤办公楼;000100010001/后勤办公楼1楼;000100010002/后勤办公楼2楼;000100010003/后勤办公楼3楼;00010001/addtest#0a0003a1c4#658989898989#0a00001001#101680010209526;
			try{
				super.handleMessage(msg);
				Bundle bundle = new Bundle();
				String bundle_msg = msg.getData().getString("buildMsg");
				if(bundle_msg.equals("exception")){
					Toast.makeText(getActivity(), "网络连接异常", Toast.LENGTH_LONG).show();
					progressDialog.dismiss();
					if(msg.what==2){
						xRefreshView.stopRefresh();
					}
					return;
				}
				switch(msg.what){
				case 0://建筑楼层或者房间数据
					models.clear();
					progressDialog.dismiss();
					init_ListViewAdp(bundle_msg);
					break;
				case 1://是楼层时弹窗显示房间列表
					progressDialog.dismiss();
					init_dialog_view(bundle_msg);
					break;
				case 2://下拉刷新数据
					xRefreshView.stopRefresh();
					models.clear();
					init_ListViewAdp(bundle_msg);
					break;
				}
							
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};
	
	private void init_ListViewAdp(String bundle_msg){
		String [] build_list = bundle_msg.replace("\n","").split(";");
		select_buindId = build_list[0].split("/")[0];
		boolean flag = false;
		for(int i=0;i<build_list.length;i++){
			BuildModel model = new BuildModel();
			String build_id = build_list[i].split("/")[0];
			String build_name = build_list[i].split("/")[1];
			if(build_id.equals(select_buindId)){
				flag = true;
			}else{
				model.setBuild_id(build_id);
				model.setBuild_name(build_name);
				models.add(model);
			}

		}
		if(flag){
			BuildModel model = new BuildModel();
			model.setBuild_id(select_buindId);
			model.setBuild_name("建筑设备");
			models.add(model);
		}
		
		listview_adp.setListviewAdp(models);
		mPullToRefreshListView.setAdapter(listview_adp);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		try{
			BuildModel model = models.get(arg2);
			if(model.getBuild_id().length() == 16 || model.getBuild_id().equals(buildid) || model.getBuild_id().equals(select_buindId)){
				String room_role = judgeRoleid(model.getBuild_id());
				Intent intent0 = new Intent(getActivity(), FragmentActivity_Room.class);
				intent0.putExtra("buildid", model.getBuild_id());
				intent0.putExtra("buildname", model.getBuild_name());
				intent0.putExtra("roleid", room_role);
				startActivity(intent0);
			}else if(model.getBuild_id().length() == 8){//如果选择的是建筑园区，则列表显示建筑id是8位
				progressDialog.show();
				getRoomDevice(model.getBuild_id(),0);
			}else {
				progressDialog.show();
				getRoomDevice(model.getBuild_id(),1);
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	

	private void getRoomDevice(String buildid,int what){
		msg_what = what;
		requestInterface.getTcp_Help("building/"+buildid);

	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try{
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**打开弹出时初始化数据视图*/
	private void init_dialog_view(String buildMsg){
		/**
		 * 000100020001/思创大厦4楼;0001000200010001/技术总监办公室;0001000200010002/王总办公室;0001000200010003/实验室;
		 * 0001000200010004/测试部;0001000200010005/储物室;0001000200010006/前台;0001000200010007/硬件组;0001000200010008/系统组;
		 * 0001000200010009/财务室;0001000200010010/会议室;0001000200010011/大厅;0001000200010012/测试;0001000200010013/陕西演示;
		 * 0001000200010014/小仓库;000100020001/42435c5573fe#0a0003ahup#42435c5573fe#0a00001001#101680010209526;
		 * 000100020001/思创大厦灯控网关#0a0003ahup#5a4c3eba7c89#0a00001001#101680010209526*/
		try{
			final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();  
			alertDialog.show();  
			Window window = alertDialog.getWindow();  
			window.setContentView(R.layout.dialog_build);
			
			alertDialog.setCanceledOnTouchOutside(true); 
			alertDialog.setCancelable(true);			
			// 可以在此设置显示动画
			window.setWindowAnimations(R.style.AnimBottom_Dialog);

			ListView listview_dialog = (ListView) window.findViewById(R.id.listview_dialog);
			TextView text_title = (TextView) window.findViewById(R.id.text_title);
			
			final List<BuildModel> dialog_models  = new ArrayList<BuildModel>();
			String [] build_list = buildMsg.replace("\n","").split(";");
			
			text_title.setText(build_list[0].split("/")[1]);
			String m_buildid = build_list[0].split("/")[0];
			boolean flag = false;
			for(int i=0;i<build_list.length;i++){
				BuildModel model = new BuildModel();
				String build_id = build_list[i].split("/")[0];
				String build_name = build_list[i].split("/")[1];
				if(build_id.equals(m_buildid)){
					flag = true;
				}else{
					model.setBuild_id(build_id);
					model.setBuild_name(build_name);
					dialog_models.add(model);
				}
				
			}
			if(flag){
				BuildModel model = new BuildModel();
				model.setBuild_id(m_buildid);
				model.setBuild_name("其他设备");
				dialog_models.add(model);
			}
			Build_DialogAdp listview_adp = new Build_DialogAdp(getActivity(), dialog_models);
			listview_dialog.setAdapter(listview_adp);
			listview_dialog.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					BuildModel model = dialog_models.get(arg2);
					String room_role = judgeRoleid(model.getBuild_id());
					Intent intent0 = new Intent(getActivity(), FragmentActivity_Room.class);
					intent0.putExtra("buildid", model.getBuild_id());
					intent0.putExtra("buildname", model.getBuild_name());
					intent0.putExtra("roleid", room_role);
					startActivity(intent0);
					alertDialog.cancel();
						
				}
			});
			
			
			window.findViewById(R.id.text_title_2).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					alertDialog.cancel();
				}
			});
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 判断房间权限*/
	private String judgeRoleid(String roomID){
		String room_roleid = roleid;
		try{
			List<Main_GvModel> models = URL_help.getInstance().getMain_GvModels();
			for(int i =0;i<models.size();i++){
				if(models.get(i).getBuilding_id().equals(roomID)){
					room_roleid = models.get(i).getBuild_roleid();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return room_roleid;
	}
	@Override
	public void requestShow(String msg) {
		// TODO Auto-generated method stub
		if(!getActivity().isFinishing()){
			Message message = new Message();
			message.what = msg_what;
		    Bundle bundle = new Bundle();
		    bundle.putString("buildMsg", msg);
		    message.setData(bundle);
		    Activity_Build.this.handler.sendMessage(message);
		}
	}


	@Override
	public void onRefresh() {
		getRoomDevice(buildid,2);
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
}
