package com.fasterbids.ryan.accessoriespage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ryan on 11/13/2014.
 */
public class AccSQLiteHelper extends SQLiteOpenHelper {

    Context context;
    public static final String TABLE_TYPES = "acctypes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_ACCS = "accs";
    // add more fields
    SQLiteDatabase AccTypes;

    private static final String DATABASE_NAME = "types.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + (TABLE_TYPES) + "(" + (COLUMN_ID)
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + (COLUMN_NAME)
            + " SECONDARY KEY, " + (COLUMN_TITLE) + ", " + (COLUMN_TYPE)
            + ", " + (COLUMN_ACCS) + ")";

    public AccSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        Log.d("onCreate", "Database Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(AccSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TYPES);
        onCreate(db);
    }



}
