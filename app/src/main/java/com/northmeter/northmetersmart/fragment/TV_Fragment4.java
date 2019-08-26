package com.northmeter.northmetersmart.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import androidx.fragment.app.Fragment;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.order.Type_Entity;
import com.northmeter.northmetersmart.wxpay.WXPayActivity;

/**zigbee插座服务界面，提供用电策略*/

public class TV_Fragment4 extends Fragment implements OnClickListener {

	private View view;
	private ListView list;
	private ArrayAdapter<String> arr_adapter;
	private String str_mac,str_name,str_type,roleid;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try{
		view = inflater.inflate(R.layout.tv_f4, container, false);
		Intent intent = getActivity().getIntent();
		str_mac = intent.getStringExtra("mac");
		str_name = intent.getStringExtra("name");
		str_type = intent.getStringExtra("type");
		roleid = intent.getStringExtra("roleid");

		list = (ListView)view.findViewById(R.id.listView1);
		if(str_type.equals(Type_Entity.Socket_type)||str_type.equals(Type_Entity.Four_street_control)){
			String [] arr_data = {"一次性定时任务","月度定额任务","设备信息","设备充值"};
			//定义数据源作为ListView内容
			arr_adapter=new ArrayAdapter<String>(getActivity(),R.layout.tv_f4_item,R.id.textView1,arr_data);
		}else{
			String [] arr_data = {"用电策略","定时控制","延时控制","一次性定时任务","月度定额任务","设备信息","设备充值"};
			//定义数据源作为ListView内容
			arr_adapter=new ArrayAdapter<String>(getActivity(),R.layout.tv_f4_item,R.id.textView1,arr_data);
		}
		//定义数据源作为ListView内容
		//arr_adapter=new ArrayAdapter<String>(getActivity(),R.layout.tv_f4_item,R.id.textView1,arr_data);
		list.setAdapter(arr_adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView txt=(TextView)view.findViewById(R.id.textView1);
				switch(txt.getText().toString()){
				case "用电策略":
					Intent intent1=new Intent(getActivity(),TV_Fragment4_1.class);
					intent1.putExtra("mac",str_mac);
					startActivity(intent1);
					break;
				case "定时控制":
					Intent intent2=new Intent(getActivity(),TV_Fragment4_2.class);
					intent2.putExtra("mac",str_mac);
					startActivity(intent2);
					break;
				case "延时控制":
					Intent intent3=new Intent(getActivity(),TV_Fragment4_3.class);
					intent3.putExtra("mac",str_mac);
					startActivity(intent3);
					break;
				case "一次性定时任务":
					try{
						Intent intent_3 = new Intent(getActivity(),Kt_Frag_Task_one_View.class);
						intent_3.putExtra("mac", str_mac);
						intent_3.putExtra("type", str_type);
						intent_3.putExtra("name", str_name);
						startActivity(intent_3);
						}catch(Exception e){
							e.printStackTrace();
						}
					break;
				case "设备信息":
					try{
						Intent intent_4 = new Intent(getActivity(),TV_Fragment3_1.class);
						intent_4.putExtra("name", str_name);
						intent_4.putExtra("type", str_type);
						intent_4.putExtra("mac", str_mac);
						startActivity(intent_4);
						}catch(Exception e){
							e.printStackTrace();
						}
					break;
				case "月度定额任务":
					try{
						Intent intent_3 = new Intent(getActivity(),Task_Quota_Add.class);
						intent_3.putExtra("mac", str_mac);
						intent_3.putExtra("type", str_type);
						intent_3.putExtra("name", str_name);
						intent_3.putExtra("roleid", roleid);
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
	
		return view;
	
	
}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}