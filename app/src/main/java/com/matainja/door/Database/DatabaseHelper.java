package com.matainja.door.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "door";

	
	private static final int DATABASE_VERSION = 1;// 200007;
	
	
	
	static final String TABLE_DEVICE ="devices";
	Context context;
	// Database creation sql statement

	private final String SQL_DEVICE_TABLE="CREATE TABLE `"+TABLE_DEVICE+"`  (\n" +
            "\t`id`\tINTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
            "\t`name`\tTEXT,\n" +
            "\t`serial_no`\tTEXT UNIQUE,\n" +
            "\t`wifi_name`\tTEXT,\n" +
            "\t`wifi_password`\tTEXT,\n" +
            "\t`status`\tINTEGER,\n" +
            "\t`online`\tINTEGER,\n" +
            "\t`mac_address`\tTEXT,\n" +
            "\t`join_date`\tTEXT\n" +
            ")";
			//AUTOINCREMENT


public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		Log.v("Database","Database Helper Create"+DATABASE_NAME);
	}


    @Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		db.disableWriteAheadLogging();



	}

	@Override
	public void onCreate(SQLiteDatabase database) {

        int version = database.getVersion();

        Log.e("on Open Local Helper","==New Version "+DATABASE_VERSION+"==oldversion"+version);

	    Log.e("On create helper","OnCREATE");

		database.execSQL(SQL_DEVICE_TABLE);

        //Intent intent = new Intent(context, InstallDbLocal.class);

        //context.startActivity(intent);

        /*try {
            //CopyLocalDB();
        } catch (IOException e) {
           Log.e("database","local database copy Error");
        }*/


    }




		//database.execSQL(SQL_WITHDRAW_BOTTLE);
		
		
	

	
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {






		


		
		
	       
	}
	
	 
	

	 
	
	

}
