package com.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by eruvaka on 17-07-2017.
 */

public class DBHelper  extends SQLiteOpenHelper{
    public static final String DATABASE="deviceIds_database";
    public static final int VERSION=1;
    public static final String PGTABLE="pg_deviceIds";
    public static final String PMTABLE="pm_deviceIds";
    public static final String  devId="dev_id";

    public DBHelper(Context context) {
        super(context, DATABASE, null, VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table pg_deviceIds (ID integer,manufacSerialNo text,ManufacId text,ManufacDate text,DevType text," +
                "DevId text,HWVer text,BatchNo text,EncodedSerial text,status text,deliveryid text,deliverystatus text," +
                "successId text,successStatusMsg text,failedId text,failedStatusMsg text)");
        db.execSQL("create table pm_deviceIds (ID integer,manufacSerialNo text,ManufacId text,ManufacDate text,DevType text," +
                "DevId text,HWVer text,BatchNo text,EncodedSerial text,status text,deliveryid text,deliverystatus text," +
                "successId text,successStatusMsg text,failedId text,failedStatusMsg text)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }
}
