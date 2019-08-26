package com.northmeter.northmetersmart.db;


import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.northmeter.northmetersmart.device.MyDevice;

import java.util.ArrayList;
import java.util.List;

public class DBDevice {

	private DBHelper dbHelper;

	public DBDevice(Context context) {
		super();
		this.dbHelper = new DBHelper(context);
	}

	/**
	 * 查找一个 MyDevice，通过唯一标识符mac
	 */
	public MyDevice Query(String mac) {
		SQLiteDatabase database = null;
		Cursor cursor = null;
		MyDevice md = new MyDevice();

		try {
			database = dbHelper.getReadableDatabase();
			cursor = database.rawQuery("Select * from MyDevices where mac=?",
					new String[] { mac });
			 
			// 存在这个设备
			if (cursor != null) {
				if (cursor.moveToFirst()) {
				// type,tableNum,mac,ip,elec_type,name
				md.setType(Integer.valueOf(cursor.getString(cursor
						.getColumnIndex("type"))));
				md.setTableNum(cursor.getString(cursor
						.getColumnIndex("tableNum")));
				md.setMac(cursor.getString(cursor.getColumnIndex("mac")));
				md.setIp(cursor.getString(cursor.getColumnIndex("ip")));
				md.setElec_type(Integer.valueOf(cursor.getString(cursor
						.getColumnIndex("elec_type"))));
				md.setName(cursor.getString(cursor.getColumnIndex("name")));
				md.setVersion(cursor.getString(cursor.getColumnIndex("version")));
				md.setContact(cursor.getString(cursor.getColumnIndex("contact")));
				}
			}else{ 
				return null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			database.close();
			cursor.close();
		}

		return md;
	}

	/**
	 * 修改一个 MyDevice
	 */
	public int Update(MyDevice md) {
		SQLiteDatabase database = null;
		try {
			database = dbHelper.getReadableDatabase();
			database.execSQL(
					"update MyDevices set type=?,tableNum=?,mac=?,ip=?,elec_type=?,name=?,version=?,contact=? where mac=?",
					new String[] { String.valueOf(md.getType()),
							md.getTableNum(), md.getMac(), md.getIp(),
							String.valueOf(md.getElec_type()), md.getName(),md.getVersion(),md.getContact(),
							md.getMac() });

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			database.close();
		}
		return 1;
	}
	
	/**
	 * 添加一个 MyDevice
	 */
	public void Insert(MyDevice md) {
		SQLiteDatabase database = null;

		try {

			database = dbHelper.getWritableDatabase();
			database.execSQL(
					"Insert into MyDevices(type,tableNum,mac,ip,elec_type,name,version,contact)values(?,?,?,?,?,?,?,?)",
					new String[] { String.valueOf(md.getType()),
							md.getTableNum(), md.getMac(), md.getIp(),
							String.valueOf(md.getElec_type()), md.getName() ,md.getVersion() ,md.getContact()});

			System.out
					.println("=============================== Insert MyDevice mac="
							+ md.getMac());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
		}
	}

	/**
	 * 删除一个 MyDevice(通过唯一标识mac)
	 */
	public void Delete(String mac) {
		SQLiteDatabase database = null;

		try {
			database = dbHelper.getWritableDatabase();
			database.execSQL("Delete from MyDevices where mac=?",
					new String[] { mac });

			System.out.println("delete a device... mac=" + mac);

		} catch (Exception e) {
			System.out.println("error!!!!!!!!!!!!!!!!!!!" + e);
			e.printStackTrace();
		} finally {
			database.close();
		}
	}

	/**
	 * 获取所有 MyDevice
	 */
	public List<MyDevice> GetMyDevices() {

		SQLiteDatabase database = null;
		Cursor cursor = null;
		List<MyDevice> myDevices = null;

		try {
			database = dbHelper.getReadableDatabase();
			cursor = database.rawQuery("Select * from MyDevices", null);

			myDevices = new ArrayList<MyDevice>();
			while (cursor.moveToNext()) {
				MyDevice md = new MyDevice();

				// type,tableNum,mac,ip,elec_type,name
				md.setType(Integer.valueOf(cursor.getString(cursor
						.getColumnIndex("type"))));
				md.setTableNum(cursor.getString(cursor
						.getColumnIndex("tableNum")));
				md.setMac(cursor.getString(cursor.getColumnIndex("mac")));
				md.setIp(cursor.getString(cursor.getColumnIndex("ip")));
				md.setElec_type(Integer.valueOf(cursor.getString(cursor
						.getColumnIndex("elec_type"))));
				md.setName(cursor.getString(cursor.getColumnIndex("name")));
				md.setVersion(cursor.getString(cursor.getColumnIndex("version")));
				md.setContact(cursor.getString(cursor.getColumnIndex("contact")));
				if (md.getMac() != null && md.getMac().length() > 0)
					myDevices.add(md);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
			cursor.close();
		}

		return myDevices;
	}
	
	/**
	 * 获取所有 MyDevice
	 */
	public List<MyDevice> GetMyDevicesByType(int type) {

		SQLiteDatabase database = null;
		Cursor cursor = null;
		List<MyDevice> myDevices = null;

		try {
			database = dbHelper.getReadableDatabase();
			cursor = database.rawQuery("Select * from MyDevices", null);

			myDevices = new ArrayList<MyDevice>();
			while (cursor.moveToNext()) {
				if(Integer.valueOf(cursor.getString(cursor.getColumnIndex("type")))==type){
				MyDevice md = new MyDevice();

				// type,tableNum,mac,ip,elec_type,name
				md.setType(Integer.valueOf(cursor.getString(cursor
						.getColumnIndex("type"))));
				md.setTableNum(cursor.getString(cursor
						.getColumnIndex("tableNum")));
				md.setMac(cursor.getString(cursor.getColumnIndex("mac")));
				md.setIp(cursor.getString(cursor.getColumnIndex("ip")));
				md.setElec_type(Integer.valueOf(cursor.getString(cursor
						.getColumnIndex("elec_type"))));
				md.setName(cursor.getString(cursor.getColumnIndex("name")));
				md.setVersion(cursor.getString(cursor.getColumnIndex("version")));
				md.setContact(cursor.getString(cursor.getColumnIndex("contact")));
				if (md.getMac() != null && md.getMac().length() > 0)
					myDevices.add(md);
			}
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
			cursor.close();
		}

		return myDevices;
	}
}
