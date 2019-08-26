package com.northmeter.northmetersmart.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.widget.DatePicker;

public class PromptHelper {
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	public static DatePicker  createTimePicker(Context context, final boolean isShowDay, final boolean isShowMonth,
			final boolean isShowYear, DatePickerDialog.OnDateSetListener listener, DialogInterface.OnDismissListener listener2) {
        final Calendar calendar = Calendar.getInstance();
        
        TimerPickerDialog pickerDialog = new TimerPickerDialog(context, listener, calendar.get(Calendar.YEAR), 
        		calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), isShowDay, isShowMonth, isShowYear);
        
        DatePicker picker = pickerDialog.getDatePicker();
        
        picker.setMaxDate(strToDate("yyyy-MM-dd", new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime())).getTime());
        pickerDialog.show();
        pickerDialog.setOnDismissListener(listener2);
        return picker;
    }
    
    public static Date strToDate(String style, String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(style);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
}