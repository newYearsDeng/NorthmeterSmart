package com.northmeter.northmetersmart.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.northmeter.northmetersmart.model.ReportData_Model;

/**红外码字段存储*/
public class DBReportData {

		private DBHelper dbHelper;

		public DBReportData(Context context) {
			super();
			this.dbHelper = new DBHelper(context);
		}

		
		/**
		 * 添加一个 ReportData_Model
		 */
		public void Insert(ReportData_Model rdm) {
			SQLiteDatabase database = null;

			try {
				System.out.println("***********************添加信息");
				database = dbHelper.getWritableDatabase();
				database.execSQL(
						"Insert into MyReportData(time,data)values(?,?)",
						new String[] {rdm.getTime(),rdm.getReportData()});

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				database.close();
			}
		}
		
		/**
		 * 删除 MyReportData所
		 */
		public void Delete() {
			SQLiteDatabase database = null;

			try {
				database = dbHelper.getWritableDatabase();
				database.execSQL("Delete from MyReportData");


			} catch (Exception e) {
				System.out.println("error!!!!!!!!!!!!!!!!!!!" + e);
				e.printStackTrace();
			} finally {
				database.close();
			}
		}


		/**
		 * 获取所有 ReportData_Model
		 */
		public List<ReportData_Model> GetReportData_Models() {

			SQLiteDatabase database = null;
			Cursor cursor = null;
			List<ReportData_Model> reportData_Model = null;

			try {
				database = dbHelper.getReadableDatabase();
				cursor = database.rawQuery("Select * from MyReportData",null);

				reportData_Model = new ArrayList<ReportData_Model>();
				while (cursor.moveToNext()) {
					ReportData_Model rdm = new ReportData_Model();
					rdm.setId(cursor.getString(cursor.getColumnIndex("_id")));
					rdm.setTime(cursor.getString(cursor.getColumnIndex("time")));
					rdm.setReportData(cursor.getString(cursor.getColumnIndex("data")));
					
					if (rdm.getTime() != null && rdm.getTime().length() > 0)		
						reportData_Model.add(rdm);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				database.close();
				cursor.close();
			}

			return reportData_Model;
		}
	}


