package com.northmeter.northmetersmart.db;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.northmeter.northmetersmart.device.MyDeviceData;

public class DBDeviceData {

	private DBHelper dbHelper;

	public DBDeviceData(Context context) {
		super();
		this.dbHelper = new DBHelper(context);
	}

	/**
	 * 查找一个 MyDeviceData，通过唯一标识符mac
	 */
	public MyDeviceData Query(String mac) {
		SQLiteDatabase database = null;
		Cursor cursor = null;
		MyDeviceData mdd = new MyDeviceData();

		try {
			database = dbHelper.getReadableDatabase();
			cursor = database.rawQuery(
					"Select * from MyDeviceData where mac=?",
					new String[] { mac });
			// 存在这个设备
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					System.out.println("DBDeviceData exist!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					// type,tableNum,stateLastTime,mac,ip,elec_type,name
					mdd.setMac(cursor.getString(cursor.getColumnIndex("mac")));
					mdd.setTimeNow(cursor.getString(cursor
							.getColumnIndex("timeNow")));
					mdd.setStateLastTime(cursor.getString(cursor
							.getColumnIndex("stateLastTime")));
					mdd.setZdn(cursor.getString(cursor.getColumnIndex("zdn")));
					mdd.setDy(cursor.getString(cursor.getColumnIndex("dy")));
					mdd.setDl(cursor.getString(cursor.getColumnIndex("dl")));
					mdd.setGl(cursor.getString(cursor.getColumnIndex("gl")));
					mdd.setWg(cursor.getString(cursor.getColumnIndex("wg")));
					mdd.setPl(cursor.getString(cursor.getColumnIndex("pl")));
					mdd.setGlys(cursor.getString(cursor.getColumnIndex("glys")));
					mdd.setThzzt(cursor.getString(cursor.getColumnIndex("thzzt")));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
			cursor.close();
		}

		// System.out.println("return mdd.zdn = " + mdd.getZdn());
		return mdd;
	}

	/**
	 * 修改一个 MyDeviceData
	 */
	public void Update(MyDeviceData mdd) {
		SQLiteDatabase database = null;

		try {
			database = dbHelper.getReadableDatabase();
			database.execSQL(
					"update MyDeviceData set mac=?,timeNow=?,stateLastTime=?,zdn=?,dy=?,dl=?,gl=?,wg=?,pl=?,glys=?,thzzt=? where mac=?",
					new String[] { mdd.getMac(), mdd.getTimeNow(),
							mdd.getStateLastTime(), mdd.getZdn(), mdd.getDy(),
							mdd.getDl(), mdd.getGl(), mdd.getWg(), mdd.getPl(),
							mdd.getGlys(), mdd.getThzzt(), mdd.getMac() });
			System.out
					.println("=============================update myDeviceData "
							+ mdd.getZdn());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
		}
	}

	/**
	 * 添加一个 MyDeviceData
	 */
	public void Insert(MyDeviceData mdd) {
		SQLiteDatabase database = null;

		try {

			database = dbHelper.getWritableDatabase();
			database.execSQL(
					"Insert into MyDeviceData(mac,timeNow,stateLastTime,zdn,dy,dl,gl,wg,pl,glys,thzzt)values(?,?,?,?,?,?,?,?,?,?,?)",
					new String[] { mdd.getMac(), mdd.getTimeNow(),
							mdd.getStateLastTime(), mdd.getZdn(), mdd.getDy(),
							mdd.getDl(), mdd.getGl(), mdd.getWg(), mdd.getPl(),
							mdd.getGlys(), mdd.getThzzt() });
			System.out
					.println("=============================insert myDeviceData");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
		}
	}

	/**
	 * 删除一个 MyDeviceData(通过唯一标识mac)
	 */
	public void Delete(String mac) {
		SQLiteDatabase database = null;

		try {
			database = dbHelper.getWritableDatabase();
			database.execSQL("Delete from MyDeviceData where mac=?",
					new String[] { mac });
			
			System.out.println("delete a deviceData... mac=" + mac);

		} catch (Exception e) {
			System.out.println("error!!!!!!!!!!!!!!!!!!!" + e);
			e.printStackTrace();
		} finally {
			database.close();
		}
	}

	/**
	 * 获取所有 MyDeviceData
	 */
	public List<MyDeviceData> GetMyDeviceData() {

		SQLiteDatabase database = null;
		Cursor cursor = null;
		List<MyDeviceData> myDeviceData = null;

		try {
			database = dbHelper.getReadableDatabase();
			cursor = database.rawQuery("Select * from MyDeviceData", null);

			myDeviceData = new ArrayList<MyDeviceData>();
			while (cursor.moveToNext()) {
				MyDeviceData mdd = new MyDeviceData();

				// mac,timeNow,stateLastTime,zdn,dy,dl,gl,wg,pl,glys,thzzt
				mdd.setMac(cursor.getString(cursor.getColumnIndex("mac")));
				mdd.setTimeNow(cursor.getString(cursor
						.getColumnIndex("timeNow")));
				mdd.setStateLastTime(cursor.getString(cursor
						.getColumnIndex("stateLastTime")));
				mdd.setZdn(cursor.getString(cursor.getColumnIndex("zdn")));
				mdd.setDy(cursor.getString(cursor.getColumnIndex("dy")));
				mdd.setDl(cursor.getString(cursor.getColumnIndex("dl")));
				mdd.setGl(cursor.getString(cursor.getColumnIndex("gl")));
				mdd.setWg(cursor.getString(cursor.getColumnIndex("wg")));
				mdd.setPl(cursor.getString(cursor.getColumnIndex("pl")));
				mdd.setGlys(cursor.getString(cursor.getColumnIndex("glys")));
				mdd.setThzzt(cursor.getString(cursor.getColumnIndex("thzzt")));

				if (mdd.getMac() != null && mdd.getMac().length() > 0)
					myDeviceData.add(mdd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
			cursor.close();
		}

		return myDeviceData;
	}
}
