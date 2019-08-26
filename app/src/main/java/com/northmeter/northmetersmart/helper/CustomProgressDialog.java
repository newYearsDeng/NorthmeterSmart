package com.northmeter.northmetersmart.helper;

import java.util.Timer;
import java.util.TimerTask;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.northmeter.northmetersmart.R;

/*
 * 自定义一个进度条样类
 * */
public class CustomProgressDialog extends Dialog {
		private Context context = null;
		private static CustomProgressDialog customProgressDialog = null;
		private static int timeout=20;
		public CustomProgressDialog(Context context){
			super(context);
			this.context = context;
		}
		
		public CustomProgressDialog(Context context, int theme) {
	        super(context, theme);
	    }
		
		public static CustomProgressDialog createDialog(final Context context){
			customProgressDialog = new CustomProgressDialog(context,R.style.CustomProgressDialog);
			customProgressDialog.setContentView(R.layout.customprogressdialog);
			customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
			customProgressDialog.setCanceledOnTouchOutside(false);
			customProgressDialog.setCancelable(true);
			new Thread(){
				public void run(){
					while(true){
						try {
							Thread.sleep(1000);
							timeout=timeout-1;
							if(!customProgressDialog.isShowing()){
								System.out.println("当前进度条已经消失，进程结束。。。");
								timeout=20;
								break;
							}
							if(timeout==0){
								if(customProgressDialog.isShowing()){
									System.out.println("当前存在未取消的进度条，可以取消。。。");
									customProgressDialog.dismiss();
									ToastUtil.showToast(context, "服务器无响应", 5000);
								}
								timeout=20;
								break;
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
			}.start();
			
			return customProgressDialog;
		}
	 
	    public void onWindowFocusChanged(boolean hasFocus){
	    	
	    	if (customProgressDialog == null){
	    		return;
	    	}
	    	
	        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
	        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
	        animationDrawable.start();
	    }
	 
	    /**
	     * 
	     * [Summary]
	     *       setTitile 标题
	     * @param strTitle
	     * @return
	     *
	     */
	    public CustomProgressDialog setTitile(String strTitle){
	    	return customProgressDialog;
	    }
	    
	    /**
	     * 
	     * [Summary]
	     *       setMessage 提示内容
	     * @param strMessage
	     * @return
	     *
	     */
	    public CustomProgressDialog setMessage(String strMessage){
	    	TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
	    	
	    	if (tvMsg != null){
	    		tvMsg.setText(strMessage);
	    	}
	    	
	    	return customProgressDialog;
	    }

		@Override
		public void dismiss() {
			// TODO Auto-generated method stub
				super.dismiss();
			
		}
	    
}


