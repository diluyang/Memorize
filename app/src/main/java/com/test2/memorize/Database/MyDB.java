package com.test2.memorize.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDB extends SQLiteOpenHelper {
    public final static String TABLE_NAME = "record";

    public final static String ID = "_id";
    public final static String TITLE = "title_name";
    public final static String BODY = "text_body";
    public final static String TIME = "create_time";


    public MyDB(Context context) {
        super(context, "test.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE record (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title_name VARCHAR(30)," +
                "text_body TEXT," +
                "create_time DATETIME NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
