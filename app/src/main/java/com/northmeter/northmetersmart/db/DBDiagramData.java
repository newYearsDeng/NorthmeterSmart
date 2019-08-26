package com.northmeter.northmetersmart.db;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.northmeter.northmetersmart.device.MyDiagramData;

/**实时功率，温度，电能曲线*/
public class DBDiagramData {

	private DBHelper dbHelper;

	public DBDiagramData(Context context) {
		super();
		this.dbHelper = new DBHelper(context);
	}

	/**
	 * 查找一个 MyDeviceData，通过唯一标识符mac?????查找有问题
	 */
	// public MyDeviceData Query(String mac) {
	// SQLiteDatabase database = null;
	// Cursor cursor = null;
	// MyDeviceData mdd = new MyDeviceData();
	//
	// try {
	// database = dbHelper.getReadableDatabase();
	// cursor = database.rawQuery(
	// "Select * from MyDeviceData where mac=?",
	// new String[] { mac });
	// // 存在这个设备
	// if (cursor != null) {
	// if (cursor.moveToFirst()) {
	// System.out
	// .println("DBDeviceData exist!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	// // type,tableNum,stateLastTime,mac,ip,elec_type,name
	// mdd.setMac(cursor.getString(cursor.getColumnIndex("mac")));
	// mdd.setTimeNow(cursor.getString(cursor
	// .getColumnIndex("timeNow")));
	// mdd.setStateLastTime(cursor.getString(cursor
	// .getColumnIndex("stateLastTime")));
	// mdd.setZdn(cursor.getString(cursor.getColumnIndex("zdn")));
	// mdd.setDy(cursor.getString(cursor.getColumnIndex("dy")));
	// mdd.setDl(cursor.getString(cursor.getColumnIndex("dl")));
	// mdd.setGl(cursor.getString(cursor.getColumnIndex("gl")));
	// mdd.setWg(cursor.getString(cursor.getColumnIndex("wg")));
	// mdd.setPl(cursor.getString(cursor.getColumnIndex("pl")));
	// mdd.setGlys(cursor.getString(cursor.getColumnIndex("glys")));
	// mdd.setThzzt(cursor.getString(cursor
	// .getColumnIndex("thzzt")));
	// }
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// database.close();
	// cursor.close();
	// }
	//
	// // System.out.println("return mdd.zdn = " + mdd.getZdn());
	// return mdd;
	// }

	/**
	 * 修改一个 MyDiagramData
	 */
	public void Update(MyDiagramData dd) {
		SQLiteDatabase database = null;

		try {
			database = dbHelper.getReadableDatabase();
			database.execSQL(
					"update MyDiagramData set mac=?,timeNow=?,zdn=?,gl=?,temps=? where mac=?",
					new String[] { dd.getMac(), dd.getTimeNow(), dd.getZdn(),dd.getGl(),
							dd.getMac() ,dd.getTemp()});
			System.out
					.println("=============================update myDeviceData "
							+ dd.getZdn());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
		}
	}

	/**
	 * 添加一个 MyDiagramData
	 */
	public void Insert(MyDiagramData dd) {
		SQLiteDatabase database = null;

		try {

			database = dbHelper.getWritableDatabase();
			database.execSQL(
					"Insert into MyDiagramData(mac,timeNow,zdn,gl,temps)values(?,?,?,?,?)",
					new String[] { dd.getMac(), dd.getTimeNow(), dd.getZdn() ,dd.getGl() ,dd.getTemp()});
			System.out
					.println("=============================insert myDiagramData");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
		}
	}

	/**
	 * 删除 MyDiagramData所有数据(通过唯一标识mac)
	 */
	public void Delete(String mac) {
		SQLiteDatabase database = null;

		try {
			database = dbHelper.getWritableDatabase();
			database.execSQL("Delete from MyDiagramData where mac=?",
					new String[] { mac });

			System.out.println("delete diagramData all... mac=" + mac);

		} catch (Exception e) {
			System.out.println("error!!!!!!!!!!!!!!!!!!!" + e);
			e.printStackTrace();
		} finally {
			database.close();
		}
	}

	/**
	 * 获取所有 MyDiagramData
	 */
	public List<MyDiagramData> GetMyDiagramData() {

		SQLiteDatabase database = null;
		Cursor cursor = null;
		List<MyDiagramData> diagramData = null;

		try {
			database = dbHelper.getReadableDatabase();
			cursor = database.rawQuery("Select * from MyDiagramData", null);

			diagramData = new ArrayList<MyDiagramData>();
			while (cursor.moveToNext()) {
				MyDiagramData dd = new MyDiagramData();

				// mac,timeNow,zdn.gl
				dd.setMac(cursor.getString(cursor.getColumnIndex("mac")));
				dd.setTimeNow(cursor.getString(cursor.getColumnIndex("timeNow")));
				dd.setZdn(cursor.getString(cursor.getColumnIndex("zdn")));
				dd.setGl (cursor.getString(cursor.getColumnIndex("gl")));
				dd.setTemp(cursor.getString(cursor.getColumnIndex("temps")));
				
				if (dd.getMac() != null && dd.getMac().length() > 0)
					diagramData.add(dd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
			cursor.close();
		}

		return diagramData;
	}

	/**
	 * 根据 mac 值，根据时间从后往前排序，获取所有 MyDiagramData
	 */
	public List<MyDiagramData> GetMyDiagramDataByMac(String mac) {

		SQLiteDatabase database = null;
		Cursor cursor = null;
		List<MyDiagramData> diagramData = null;

		try {
			database = dbHelper.getReadableDatabase();
			cursor = database.rawQuery(
					"Select * from MyDiagramData where mac=?",// order by
																// datetime(timeNow)",
					new String[] { mac });

			diagramData = new ArrayList<MyDiagramData>();
			while (cursor.moveToNext()) {
				MyDiagramData dd = new MyDiagramData();

				// mac,timeNow,zdn,gl
				dd.setMac(cursor.getString(cursor.getColumnIndex("mac")));
				dd.setTimeNow(cursor.getString(cursor.getColumnIndex("timeNow")));
				dd.setZdn(cursor.getString(cursor.getColumnIndex("zdn")));
				dd.setGl(cursor.getString(cursor.getColumnIndex("gl")));
				dd.setTemp(cursor.getString(cursor.getColumnIndex("temps")));
				if (dd.getMac() != null && dd.getMac().length() > 0) {
					diagramData.add(dd);

					System.out.println("查询中...找到一个数据 mac=" + dd.getMac()
							+ "timeNow=" + dd.getTimeNow()+" temps"+dd.getTemp());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
			cursor.close();
		}

		return diagramData;
	}
	
	/**
	 * 根据 mac 值，根据时间从后往前排序，获取所有 MyDiagramData
	 */
	public List<MyDiagramData> GetMyDiagramDataByMacTop(String mac) {

		SQLiteDatabase database = null;
		Cursor cursor = null;
		List<MyDiagramData> diagramData = null;

		try {
			database = dbHelper.getReadableDatabase();
			cursor = database.rawQuery(
					"Select * from (Select * from MyDiagramData where mac=? order by _id desc limit 0,30) order by _id asc ",// order by
																// datetime(timeNow)",
					new String[] { mac });

			diagramData = new ArrayList<MyDiagramData>();
			while (cursor.moveToNext()) {
				MyDiagramData dd = new MyDiagramData();

				// mac,timeNow,zdn,gl
				dd.setMac(cursor.getString(cursor.getColumnIndex("mac")));
				dd.setTimeNow(cursor.getString(cursor.getColumnIndex("timeNow")));
				dd.setZdn(cursor.getString(cursor.getColumnIndex("zdn")));
				dd.setGl(cursor.getString(cursor.getColumnIndex("gl")));
				dd.setTemp(cursor.getString(cursor.getColumnIndex("temps")));
				if (dd.getMac() != null && dd.getMac().length() > 0) {
					diagramData.add(dd);

					System.out.println("查询中...找到一个数据 mac=" + dd.getMac()
							+ " timeNow=" + dd.getTimeNow()+" Zdn="+dd.getZdn()+" gl="+dd.getGl()+" temps"+dd.getTemp());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
			cursor.close();
		}
 
		return diagramData;
	}
}
