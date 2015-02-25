package com.fasterbids.ryan.accessoriespage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Ryan on 11/13/2014.
 */
public class AccSQLiteHelper extends SQLiteOpenHelper {

    boolean DONOTADD = false;
    boolean DOADD = true;

    Context context;
    /* Accessory types table */
    public static final String TABLE_TYPES = "acctypes";
    public static final String COLUMN_IDT = "_id";
    public static final String COLUMN_TITLET = "title";
    public static final String COLUMN_TYPET = "type";
    /* Accessories table */
    public static final String TABLE_ACCS = "accs";
    public static final String COLUMN_IDA = "_id";
    public static final String COLUMN_TITLEA = "title";
    public static final String COLUMN_TYPEA = "type";
    public static final String COLUMN_COUNTA = "count";
    public static final String COLUMN_COSTA = "cost";
    public static final String COLUMN_SELECTED = "selected";
    SQLiteDatabase ourDb;

    private static final String DATABASE_NAME = "accessories.db";
    private static final int DATABASE_VERSION = 1;

    /* Database creation sql statement */
    /* type db */
    private static final String DB_TYPE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + (TABLE_TYPES) + "(" + (COLUMN_IDT)
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + (COLUMN_TITLET)
            + " SECONDARY KEY, " + (COLUMN_TYPET) + ")";
    /* acc db */
    private static final String DB_ACC_CREATE = "CREATE TABLE IF NOT EXISTS "
            + (TABLE_ACCS) + "(" + (COLUMN_IDA) + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + (COLUMN_TITLEA) + " SECONDARY KEY, "
            + (COLUMN_TYPEA) + ", "
            + (COLUMN_COUNTA) + ", "
            + (COLUMN_COSTA) + ", "
            + (COLUMN_SELECTED) + " INTEGER)";

    public AccSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DB_TYPE_CREATE);
        database.execSQL(DB_ACC_CREATE);
        Log.d("onCreate", "Database Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(AccSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TYPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCS);
        onCreate(db);
    }

    public void addType(AccessoryType accT) {
        Log.d("addType", accT.id);
        ourDb = getWritableDatabase();
        String title = accT.title.getText().toString().replaceAll(" ", "_");
        String type = accT.type.replaceAll(" ", "_");
        String query = "INSERT INTO " + TABLE_TYPES + "(title, type) VALUES('"
                + (title) + "', '" + (type) + "')";
        ourDb.execSQL(query);
    }
    public void addAcc(Accessory acc) {
        Log.d("addAcc", acc.id);
        ourDb = getWritableDatabase();
        String title = acc.title.getText().toString().replaceAll(" ", "_");
        String type = acc.parentTitle.replaceAll(" ", "_");
        String count = acc.count.getText().toString();
        String cost = acc.costAmount.getText().toString();
        int selected = 0;       // 0 = false
        if (acc.selected) selected = 1;  // 1 = true
        String query = "INSERT INTO " + TABLE_ACCS + "(title, type, count, cost, selected) VALUES('"
                + (title) + "', '" + (type) + "', '" + (count) + "', '" + (cost) + "', '" + (selected) + "')";
                ourDb.execSQL(query);
        Log.d("worked", "true");
    }

    public void populateAccessoriesFromDB(ArrayList<AccessoryType> types) {
        ourDb = this.getReadableDatabase();
        String typeQuery = "SELECT * FROM " + TABLE_TYPES;
        String baseAccQuery = "SELECT * FROM " + TABLE_ACCS + " WHERE type = '";
        Cursor typeCursor = ourDb.rawQuery(typeQuery, null);
        if (typeCursor.moveToFirst()) {
            do {
                String titleT = typeCursor.getString(typeCursor.getColumnIndex("title")).replaceAll("_", " ");
                String typeT = typeCursor.getString(typeCursor.getColumnIndex("type")).replaceAll("_", " ");
                AccessoryType accT = AccessoryType.addAccessoryType(titleT, typeT, DONOTADD);
                if (accT != null) {
                    Cursor accCursor = ourDb.rawQuery(baseAccQuery + titleT + "'", null);
                    if (accCursor.moveToFirst()) {
                        do {
                            String titleA = accCursor.getString(accCursor.getColumnIndex("title")).replaceAll("_", " ");
                            String count = accCursor.getString(accCursor.getColumnIndex("count"));
                            String cost = accCursor.getString(accCursor.getColumnIndex("cost"));
                            int selected = accCursor.getInt(accCursor.getColumnIndex("selected"));
                            boolean select = false;
                            if (selected > 0) select = true;
                            Accessory acc = Accessory.AddAccessory(titleA, cost, count, accT.container, DONOTADD, select);
                            Log.d("populate " + acc.id, ": " + acc.selected);
                        } while (accCursor.moveToNext());
                    } else {

                    }
                }
            } while (typeCursor.moveToNext());
        } else {

        }
    }

    public void removeAcc(Accessory acc) {
        ourDb = getWritableDatabase();
        String val = acc.title.getText().toString().replaceAll(" ", "_");
        ourDb.delete(TABLE_ACCS, "title = ?", new String[] { val });
    }

    public void removeAccT(AccessoryType accT) {
        ourDb = getWritableDatabase();
        /* we need to remove all accs first */
        String val = accT.title.getText().toString().replaceAll(" ", "_");
        ourDb.delete(TABLE_ACCS, "type = ?", new String[] { val });
        ourDb.delete(TABLE_TYPES, "title = ?", new String[] { val });
    }

    public void updateAcc(Accessory acc) {
        Log.d("udpateAcc", acc.id);
        ContentValues cv = new ContentValues();
        String countt = "0";
        if (acc.count.getText().toString() != "") countt = acc.count.getText().toString();
        cv.put("count", countt);
        cv.put("cost", acc.costAmount.getText().toString());
        int selected = 0;
        if (acc.selected) {
            selected = 1;
            Log.d(acc.id, ": " + acc.selected);
        }
        cv.put("selected", selected);
        String val = acc.title.getText().toString().replace(" ", "_");
        ourDb.update(TABLE_ACCS, cv, "title = ?", new String[] { val });
    }

}
