package com.northmeter.northmetersmart.helper;

import java.util.zip.Inflater;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.northmeter.northmetersmart.R;

/**
 * 自定义toast显示
 * */
public class ToastUtil {  
    private static Toast toast;  
    private static View view;  
  
    private ToastUtil() {  
    }  
  
    @SuppressLint("ShowToast")  
    private static void getToast(Context context,int duration) {  
        if (toast == null) {  
            toast = new Toast(context);  
        }  
        view = Toast.makeText(context, "", duration).getView();  
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.toast_layout,
		(ViewGroup) view.findViewById(R.id.toast_layout));
        toast.setDuration(duration);
        toast.setView(view);  
    }  
    
    private static void setText(CharSequence msg){
		TextView toast_text = (TextView) view.findViewById(R.id.toast_text);
		toast_text.setText(msg);
    }
  
    public static void showShortToast(Context context, CharSequence msg) {  
        showToast(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);  
    }  
  
    public static void showShortToast(Context context, int resId) {  
        showToast(context.getApplicationContext(), resId, Toast.LENGTH_SHORT);  
    }  
  
    public static void showLongToast(Context context, CharSequence msg) {  
        showToast(context.getApplicationContext(), msg, Toast.LENGTH_LONG);  
    }  
  
    public static void showLongToast(Context context, int resId) {  
        showToast(context.getApplicationContext(), resId, Toast.LENGTH_LONG);  
    }  
  
    public static void showToast(Context context, CharSequence msg,  
            int duration) {  
        try {  
            getToast(context,duration);  
            setText(msg);
            toast.setDuration(duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();  
        } catch (Exception e) {  
            e.printStackTrace(); 
        }  
    }  
  
    private static void showToast(Context context, int resId, int duration) {  
        try {  
            if (resId == 0) {  
                return;  
            }  
            getToast(context,duration); 
            toast.setDuration(duration);
            toast.setText(resId);  
            toast.show();  
        } catch (Exception e) {  
        	 e.printStackTrace();   
        }  
    }  
  
}