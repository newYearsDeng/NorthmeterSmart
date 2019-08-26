package com.northmeter.northmetersmart.db;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.northmeter.northmetersmart.model.MyInfraredCode;

/**红外码字段存储*/
public class DBInfraredCode {

		private DBHelper dbHelper;

		public DBInfraredCode(Context context) {
			super();
			this.dbHelper = new DBHelper(context);
		}

		/**
		 * 查找一个 MyInfraredCode，通过唯一标识符mac
		 */
		public List<MyInfraredCode> Query(String mac) {
			SQLiteDatabase database = null;
			Cursor cursor = null;
			List<MyInfraredCode> myinfrared;
			try {
				database = dbHelper.getReadableDatabase();
				cursor = database.rawQuery("Select * from MyInfraredCode where mac=?",
						new String[] { mac });
				myinfrared=new ArrayList<MyInfraredCode>();
				// 存在这个设备		
				if (cursor != null) {
					while (cursor.moveToNext()) {
						MyInfraredCode md = new MyInfraredCode();
						md.setRand(Integer.valueOf(cursor.getString(cursor.getColumnIndex("rand"))));
						md.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex("_id"))));
						md.setMac(cursor.getString(cursor.getColumnIndex("mac")));
						md.setTime(cursor.getString(cursor.getColumnIndex("timeNow")));
						md.setVersion(cursor.getString(cursor.getColumnIndex("version")));
						md.setSwitchs(cursor.getString(cursor.getColumnIndex("switch")));
						md.setModel(cursor.getString(cursor.getColumnIndex("model")));
						md.setTemputer(cursor.getString(cursor.getColumnIndex("temputer")));
						md.setSpeed(cursor.getString(cursor.getColumnIndex("speed")));
						md.setUpdown(cursor.getString(cursor.getColumnIndex("updown")));
						md.setLeftright(cursor.getString(cursor.getColumnIndex("leftright")));
						md.setWeeks(cursor.getString(cursor.getColumnIndex("weeks")));
						if (md.getMac() != null && md.getMac().length() > 0)
							myinfrared.add(md);
					}
				}else{
					System.out.println("没有读取到数据");
					return null;
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				database.close();
				cursor.close();
			}

			return myinfrared;
		}
		
		/**
		 * 查找一个 MyInfraredCode，通过唯识符mac和id
		 */
		public MyInfraredCode QueryBy_Mac_Id(int id,String mac) {
			SQLiteDatabase database = null;
			Cursor cursor = null;
			MyInfraredCode md = new MyInfraredCode();
			try {
				database = dbHelper.getReadableDatabase();
				cursor = database.rawQuery("Select * from MyInfraredCode where _id=? and mac=?",
						new String[] { String.valueOf(id),mac });
				// 存在这个设备		
				if (cursor != null) {
					while (cursor.moveToNext()) {
						md.setRand(Integer.valueOf(cursor.getString(cursor.getColumnIndex("rand"))));
						md.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex("_id"))));
						md.setMac(cursor.getString(cursor.getColumnIndex("mac")));
						md.setTime(cursor.getString(cursor.getColumnIndex("timeNow")));
						md.setVersion(cursor.getString(cursor.getColumnIndex("version")));
						md.setSwitchs(cursor.getString(cursor.getColumnIndex("switch")));
						md.setModel(cursor.getString(cursor.getColumnIndex("model")));
						md.setTemputer(cursor.getString(cursor.getColumnIndex("temputer")));
						md.setSpeed(cursor.getString(cursor.getColumnIndex("speed")));
						md.setUpdown(cursor.getString(cursor.getColumnIndex("updown")));
						md.setLeftright(cursor.getString(cursor.getColumnIndex("leftright")));
						md.setWeeks(cursor.getString(cursor.getColumnIndex("weeks")));
					}
				}else{
					System.out.println("没有读取到数据");
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
		 * 修改一个 MyInfraredCode
		 */
		public int Update(MyInfraredCode md) {
			SQLiteDatabase database = null;
			try {
				System.out.println("////////////////修改信息");
				database = dbHelper.getReadableDatabase();
				database.execSQL(
						"update MyDevices set timeNow=?,switch=?,model=?,temputer=?,speed=?,updown=?,leftright=?,weeks=? where id=? and mac=?",
						new String[] { String.valueOf(md.getTime()),
								md.getSwitchs(), md.getModel(), md.getTemputer(),
								md.getSpeed(), md.getUpdown(),md.getLeftright(),md.getWeeks(),String.valueOf(md.getId()),
								md.getMac()});

			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			} finally {
				database.close();
			}
			
			return 1;
		}
		
		/**
		 * 添加一个 MyInfraredCode
		 */
		public void Insert(MyInfraredCode md) {
			SQLiteDatabase database = null;

			try {
				System.out.println("***********************添加信息");
				database = dbHelper.getWritableDatabase();
				database.execSQL(
						"Insert into MyInfraredCode(rand,mac,timeNow,version,switch,model,temputer,speed,updown,leftright,weeks)values(?,?,?,?,?,?,?,?,?,?,?)",
						new String[] {String.valueOf(md.getRand()),md.getMac(),md.getTime(),md.getVersion(),md.getSwitchs(),
								md.getModel(),md.getTemputer(),md.getSpeed(),md.getUpdown(),md.getLeftright(),md.getWeeks()});

				System.out.println("=============================== Insert MyDevice mac="
								+ md.getMac());

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				database.close();
			}
		}

		/**
		 * 删除一个 MyInfraredCode(通过唯一标识id,mac)
		 */
		public void Delete(int id,String mac) {
			SQLiteDatabase database = null;

			try {
				database = dbHelper.getWritableDatabase();
				database.execSQL("Delete from MyInfraredCode where _id=? and mac=?",
						new String[] { String.valueOf(id),mac });

				System.out.println("delete a device... id/mac= "+id+"/"+ mac);

			} catch (Exception e) {
				System.out.println("error!!!!!!!!!!!!!!!!!!!" + e);
				e.printStackTrace();
			} finally {
				database.close();
			}
		}
		/**
		 * 删除一个红外设备所有的红外MyInfraredCode(通过唯一标识mac)
		 */
		public void DeleteAll(String mac) {
			SQLiteDatabase database = null;

			try {
				database = dbHelper.getWritableDatabase();
				database.execSQL("Delete from MyInfraredCode where mac=?",
						new String[] { mac });

				System.out.println("delete a device... id/mac= /"+ mac);

			} catch (Exception e) {
				System.out.println("error!!!!!!!!!!!!!!!!!!!" + e);
				e.printStackTrace();
			} finally {
				database.close();
			}
		}

		/**
		 * 获取所有 MyInfraredCode
		 */
		public List<MyInfraredCode> GetMyInfraredCode() {

			SQLiteDatabase database = null;
			Cursor cursor = null;
			List<MyInfraredCode> myinfrared = null;

			try {
				database = dbHelper.getReadableDatabase();
				cursor = database.rawQuery("Select * from MyInfraredCode",null);

				myinfrared = new ArrayList<MyInfraredCode>();
				while (cursor.moveToNext()) {
					MyInfraredCode md = new MyInfraredCode();
					md.setRand(Integer.valueOf(cursor.getString(cursor.getColumnIndex("rand"))));
					md.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex("_id"))));
					md.setMac(cursor.getString(cursor.getColumnIndex("mac")));
					md.setTime(cursor.getString(cursor.getColumnIndex("timeNow")));
					md.setVersion(cursor.getString(cursor.getColumnIndex("version")));
					md.setSwitchs(cursor.getString(cursor.getColumnIndex("switch")));
					md.setModel(cursor.getString(cursor.getColumnIndex("model")));
					md.setTemputer(cursor.getString(cursor.getColumnIndex("temputer")));
					md.setSpeed(cursor.getString(cursor.getColumnIndex("speed")));
					md.setUpdown(cursor.getString(cursor.getColumnIndex("updown")));
					md.setLeftright(cursor.getString(cursor.getColumnIndex("leftright")));
					md.setWeeks(cursor.getString(cursor.getColumnIndex("weeks")));
					if (md.getMac() != null && md.getMac().length() > 0)		
						myinfrared.add(md);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				database.close();
				cursor.close();
			}

			return myinfrared;
		}
	}


