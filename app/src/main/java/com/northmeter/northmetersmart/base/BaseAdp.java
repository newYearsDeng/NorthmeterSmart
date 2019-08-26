package com.northmeter.northmetersmart.base;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.northmeter.northmetersmart.helper.ViewHolder;

public abstract class BaseAdp<T> extends BaseAdapter {
	
	protected List<T> list = new ArrayList<T>();
	protected Context context;
	
	public BaseAdp(List<T> list, Context context) {
		super();
		this.list = list;
		this.context = context;
	}
	
	public List<T> getList() {
		return list;
	}
	
	public void setList(List<T> list) {
		this.list = list;
		notifyDataSetChanged();
	}
	
	public void appendToList(List<T> list) {
		if (list == null)
			return;
		list.addAll(list);
		notifyDataSetChanged();
	}
	
	public void appendToTopList(List<T> list) {
		if (list == null)
			return;
		list.addAll(0, list);
		notifyDataSetChanged();
	}
	
	public void clear() {
		list.clear();
		notifyDataSetChanged();
	} 
	
	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}
	
	@Override
	public Object getItem(int position) {
		if (position > list.size() - 1)
			return null;
		return list.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// if (position == getCount() - 1) {
		// onReachBottom();
		// }
		return getExView(position, view);
	};
	
	protected abstract View getExView(int position, View view);
	
	public void st(View view, int id, String text) {
		((TextView) ViewHolder.get(view, id)).setText(text);
	}
	
	public void sc(View view, int id, boolean check) {
		((CheckBox) ViewHolder.get(view, id)).setChecked(check);
	}
	
	public void si(View view, int id, int img, boolean isShow) {
		((ImageView) ViewHolder.get(view, id)).setImageResource(img);
		((ImageView) ViewHolder.get(view, id)).setVisibility(isShow == true ? View.VISIBLE : View.GONE);
	}
	// protected abstract void onReachBottom();
	
}
