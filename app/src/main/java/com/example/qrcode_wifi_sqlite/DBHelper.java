package com.example.qrcode_wifi_sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME="qr.db";
    private static final int DB_VERSION=1;
    private static final String TABLE_NAME="scanned_codes";
    private static final String COL_ID="id";
    private static final String COL_CONTENT="content";
    private static final String COL_IP="ip_address";
    private static final String COL_TIMESTAMP="timestamp";

    public DBHelper(Context context){

        super(context,DB_NAME,null,DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String sql= "CREATE TABLE "+TABLE_NAME+"" + " ("+
                COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COL_CONTENT+" TEXT NOT NULL, " +
                COL_IP+" TEXT, "+
                COL_TIMESTAMP+" TEXT NOT NULL);";
        sqLiteDatabase.execSQL(sql);

    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
    public boolean rogzites(String content, String ip, String timestamp){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COL_CONTENT,content);
        values.put(COL_IP,ip);
        values.put(COL_TIMESTAMP,timestamp);
        return db.insert(TABLE_NAME,null,values)!=-1;
    }
    public Cursor listaz(){

        SQLiteDatabase db= this.getReadableDatabase();

        return db.query(TABLE_NAME,new String[]{COL_ID,COL_CONTENT,COL_IP,COL_TIMESTAMP},
                null,null,
                null,null,null);
    }




}
