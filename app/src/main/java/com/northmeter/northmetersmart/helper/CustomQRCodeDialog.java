package com.northmeter.northmetersmart.helper;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;

import com.northmeter.northmetersmart.R;

/*
 * huzenan
 * 自定义一个导出二维码对话框
 */
public class CustomQRCodeDialog extends Dialog {

	private Context context = null;
	private static CustomQRCodeDialog customQRCodeDialog = null;

	public CustomQRCodeDialog(Context context) {
		super(context);
		this.context = context;
	}

	public static CustomQRCodeDialog createDialog(Context context, Bitmap QRCode) {
		customQRCodeDialog = new CustomQRCodeDialog(context);
		customQRCodeDialog.setContentView(R.layout.dialog_qrcode);
		customQRCodeDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

		// 点击屏幕时关闭二维码对话框
		customQRCodeDialog.setCanceledOnTouchOutside(true);
		customQRCodeDialog.setCancelable(true);

		// 加载传入的二维码图片
		ImageView imageView = (ImageView) customQRCodeDialog
				.findViewById(R.id.dialog_qrcode_image);
		imageView.setImageBitmap(QRCode);

		return customQRCodeDialog;
	}

	public void onWindowFocusChanged(boolean hasFocus) {

		if (customQRCodeDialog == null) {
			return;
		}
	}
}
