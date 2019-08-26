package com.northmeter.northmetersmart.adp;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.fragment.Add_Fragment_udp_2;
import com.northmeter.northmetersmart.model.UDP_model;

public class UDP_listviewAdp extends BaseAdapter {

	private Context context;
	private List<UDP_model> models;

	public UDP_listviewAdp(Context context, List<UDP_model> models) {
		super();
		this.context = context;
		this.models = models;
	}

	@Override
	public int getCount() {
		return models.size();
	}

	@Override
	public Object getItem(int i) {
		return models.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.listview_udp, null);
		CheckBox box = (CheckBox) view.findViewById(R.id.isCheck);
		TextView textView1 = (TextView) view.findViewById(R.id.textView1);
		TextView textView2 = (TextView) view.findViewById(R.id.textView2);
		box.setFocusable(false);
		box.setEnabled(false);
		if (models.get(position).getIscheck() == "false"  )
			box.setChecked(false);
		else
			box.setChecked(true);
		//"000"+getID(models.getTerminal_add().substring(2,10))
		textView1.setText("000"+Add_Fragment_udp_2.getID(models.get(position).getTerminal_add().substring(2,10)));
		textView2.setText(reverseRst("000"+ Add_Fragment_udp_2.getID(models.get(position).getTerminal_add().substring(2,10))));
		return view;
	}
	
	private String reverseRst(String rst) {/*检查是否存在已添加的设备*/
		String name="未添加";
		DBDevice device = new DBDevice(context);
		List<MyDevice> mydevice = device.GetMyDevices();
		if(!mydevice.isEmpty()){
			for(int i = 0;i < mydevice.size();i++){
				if(mydevice.get(i).getMac().equals(rst)){
					name=mydevice.get(i).getName();
				}
			}
		}
		return name;
	}

}
