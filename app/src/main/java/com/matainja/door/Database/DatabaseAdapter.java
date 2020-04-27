package com.matainja.door.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.matainja.door.model.Devices;


public class DatabaseAdapter  {

	// Database fields

	static final String DEVICE_TABLE ="devices";

	
	
	private Context context;
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase gDb;

	public DatabaseAdapter(Context context) {
		this.context = context;
	}





	public DatabaseAdapter open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();

		
		
		
		return this;
	}

	public boolean doesTableExist(String tableName) {
		Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);

		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.close();
				return true;
			}
			cursor.close();
		}
		return false;
	}

	public boolean isDbValid() {


		String SQL_L = "SELECT *  " +
				"FROM save_station" ;

		Log.e("is DBValidLocal Sql", SQL_L);

		try {
            database.rawQuery(SQL_L, null,null);

			return true;
		}catch(SQLiteException e)
		{
			String err = e.toString();
			return false;
		}
	}


	public boolean getDeviceExist() {



		Cursor isDeviceExist =  database.rawQuery("SELECT * FROM " + DEVICE_TABLE + " where 1 LIMIT 0, 1", null);

		if (isDeviceExist.getCount() > 0) {

			return true;

		}

		return false;

	}

	public Cursor getDevice() {



		Cursor device =  database.rawQuery("SELECT * FROM " + DEVICE_TABLE + " where 1 LIMIT 0, 1", null);



		return device;

	}

	public boolean emptyDevice() {





		database.delete(DEVICE_TABLE, "1", null);

		return true;

	}



public boolean DeviceRegister(Devices device) {

		/*

		CREATE TABLE `device` (
	`id`	INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,
	`name`	TEXT,
	`serial_no`	TEXT UNIQUE,
	`wifi_name`	TEXT,
	`wifi_password`	TEXT,
	`status`	INTEGER,
	`online`	INTEGER,
	`mac_address`	TEXT,
	`join_date`	TEXT
);
		 */

    boolean createSuccessful = false;

    ContentValues values = new ContentValues();

  //  values.put(KEY_ID, information.getId());
    values.put("name", device.getName());
//Log.v("_ID", station.getStationCode());
//Log.v("stationCode", station.getStationCode());
    values.put("serial_no", device.getDevice_serial());
    values.put("wifi_name", device.getDevice_wifi_name());
    values.put("wifi_password", device.getDevice_wifi_password());
	values.put("mac_address", device.getDevice_mac_address());
	values.put("status", device.getStatus());
	values.put("join_date", device.getJoin_date());

    createSuccessful = database.insert(DEVICE_TABLE, null, values) > 0;
    //database.close();

    return createSuccessful;
}



	public void close() {
		database.close();
	}
}