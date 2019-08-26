package com.northmeter.northmetersmart.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	
	 	private String MyDiagramData = "create table MyDiagramData(_id integer primary key autoincrement,mac text,timeNow text,zdn text,gl text)";  
	  
	    private String CREATE_TEMP_MyDiagramData = "alter table MyDiagramData rename to _temp_MyDiagramData";  
	  
	    private String INSERT_DATA = "insert into MyDiagramData select *,'' from to _temp_MyDiagramData";  
	  
	    private String DROP_BOOK = "drop table _temp_MyDiagramData"; 
	    
	    

	public DBHelper(Context context) {
		// 创建数据库
		super(context, DBStrings.DBName, null, DBStrings.DBVersion);
	} 

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("创建表===============");
		// 创建表
		// 设备基本信息 id tableNum mac ip elec_type(-1,0,..) name
		db.execSQL("create table MyDevices(_id integer primary key autoincrement,type integer,tableNum text,mac text,ip text,elec_type integer,name text,version text,contact text)");
		// 设备数据信息 mac,timeNow,stateLastTime,zdn,dy,dl,gl,wg,pl,glys,thzzt
		db.execSQL("create table MyDeviceData(_id integer primary key autoincrement,mac text,timeNow text,stateLastTime text,zdn text,dy text,dl text,gl text,wg text,pl text,glys text,thzzt text)");
		// 图标数据信息 mac,timeNow,zdn
		db.execSQL("create table MyDiagramData (_id integer primary key autoincrement,mac text,timeNow text,zdn text,gl text,temps text)");
		db.execSQL("create table MyInfraredCode(_id integer primary key autoincrement,rand integer,mac text,timeNow text,version text,switch text,model text,temputer text,speed text,updown text,leftright text,weeks text)");
		db.execSQL("create table MyReportData (_id integer primary key autoincrement,time text,data text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
}
