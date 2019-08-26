package com.northmeter.northmetersmart.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.order.Type_Entity;
import com.northmeter.northmetersmart.wxpay.WXPayActivity;

/**服务页面，配置定时任务，其他功能的界面*/
public class Kt_Frag_Serve  extends Fragment implements OnClickListener{
	private View view;
	private ListView listview;
	private String str_mac,str_name,str_type,roleid;
	private ArrayAdapter<String> arrayadapter;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.f_yaokong_serve, container, false);
		
		Intent intent = getActivity().getIntent();
		str_mac = intent.getStringExtra("mac");
		str_name = intent.getStringExtra("name");
		str_type = intent.getStringExtra("type");
		roleid = intent.getStringExtra("roleid");
		
		init_view();
		return view;
	}
	
	private void init_view(){
		try{
			if(roleid.equals(Type_Entity.manager)){
				String [] arr_data = new String[]{"周循环定时任务","一次性定时任务","月度定额任务","其他设置","设备信息","设备充值"};
				arrayadapter = new ArrayAdapter<String>(getActivity(), R.layout.tv_f4_item,R.id.textView1,arr_data);
			}else{
				String [] arr_data = new String[]{"周循环定时任务","一次性定时任务","月度定额任务","设备信息","设备充值"};
				arrayadapter = new ArrayAdapter<String>(getActivity(),R.layout.tv_f4_item,R.id.textView1,arr_data);
			}
			
			listview  = (ListView) view.findViewById(R.id.listView_serve);
			listview.setAdapter(arrayadapter);
			listview.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					TextView txt=(TextView)view.findViewById(R.id.textView1);
					switch(txt.getText().toString()){
					case "周循环定时任务":
						Intent intent_1 = new Intent(getActivity(),Kt_Frag2.class);
						intent_1.putExtra("mac", str_mac);
						intent_1.putExtra("name", str_name);
						intent_1.putExtra("type", str_type);
						startActivity(intent_1);
						//((MessageAty) getActivity()).showkt_frg2();//跳回定时、其他选项 的选择
						break;
					case "其他设置":
						try{
						Intent intent_2 = new Intent(getActivity(),Kt_Frag_Other.class);
						intent_2.putExtra("name", str_name);
						intent_2.putExtra("type", str_type);
						intent_2.putExtra("mac", str_mac);
						startActivity(intent_2);
						}catch(Exception e){
							e.printStackTrace();
						}
						break;
						
					case "一次性定时任务":
						try{
							Intent intent_3 = new Intent(getActivity(),Kt_Frag_Task_one_View.class);
							intent_3.putExtra("name", str_name);
							intent_3.putExtra("type", str_type);
							intent_3.putExtra("mac", str_mac);
							startActivity(intent_3);
							}catch(Exception e){
								e.printStackTrace();
							}
						break;
					case "月度定额任务":
						try{
							Intent intent_3 = new Intent(getActivity(),Task_Quota_Add.class);
							intent_3.putExtra("name", str_name);
							intent_3.putExtra("type", str_type);
							intent_3.putExtra("mac", str_mac);
							intent_3.putExtra("roleid", roleid);
							startActivity(intent_3);
						}catch(Exception e){
							e.printStackTrace();
						}
						break;
						
					case "设备信息":
						try{
							Intent intent_3 = new Intent(getActivity(),TV_Fragment3_1.class);
							intent_3.putExtra("name", str_name);
							intent_3.putExtra("type", str_type);
							intent_3.putExtra("mac", str_mac);
							startActivity(intent_3);
							}catch(Exception e){
								e.printStackTrace();
							}
						break;
					case "设备充值":
						Intent intent_4 = new Intent(getActivity(),WXPayActivity.class);
						intent_4.putExtra("name", str_name);
						intent_4.putExtra("type", str_type);
						intent_4.putExtra("mac", str_mac);
						startActivity(intent_4);
						break;
						
					}
					
				}
				
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

}
