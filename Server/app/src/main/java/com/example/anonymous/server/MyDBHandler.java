package com.example.anonymous.server;

/**
 * Created by payal on 21/06/2015.
 */


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.os.Build;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database.db";
    public static final String TABLE_DATABASE = "database";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_DATA1 = "empCheckinTime";
    private static final String COLUMN_DATA2 = "empCheckoutTime";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "Create Table" + TABLE_DATABASE + "(" +
                COLUMN_ID + " TEXT PRIMARY KEY " +
                COLUMN_DATA1 + " TIME " +
                COLUMN_DATA2 + " TIME " +
                ")";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_DATABASE);
        onCreate(db);
    }

    //add new row to the database
    public void addData(Database data){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID,data.get_empid());
        //values.put(COLUMN_DATA1,"");
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_DATABASE,null, values);
        db.close();
    }

    //delete
    public void deleteData(String data){
//        SQLiteDatabase db = getWritableDatabase();
        //db.execSQL("DELETE FROM" + TABLE_DATABASE + "WHERE" + COLUMN_DATA + "=\"" + data +"\";" );
    }

    // print data as string
    public String databaseToString()
    {
        String dbString = "";
        SQLiteDatabase db= getWritableDatabase();
        String query = "SELECT * FROM" + TABLE_DATABASE +"WHERE 1";

        //cursor point to location of ur result
        Cursor c =db.rawQuery(query,null);
        //move to first ror
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            if(c.getString(c.getColumnIndex("data"))!=null){
                dbString += c.getString(c.getColumnIndex("data"));
                dbString += "\n";
            }
        }
        db.close();
        return dbString;
    }
}
