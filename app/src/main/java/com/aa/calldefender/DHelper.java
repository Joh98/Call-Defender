package com.aa.calldefender;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 10/03/2018.
 */

public class DHelper extends SQLiteOpenHelper {

    public static final String D_NAME = "Call.db";

    public static final String T_NAME = "numbers_table";
    public static final String ID = "id";
    public static final String PHONE_NUMBER = "phone_number";

    public static final String T_NAME_2 = "area_code_table";
    public static final String ID_2 = "id_2";
    public static final String AREA_CODE = "area_code";

    public static final String T_NAME_3 = "map_table";
    public static final String ID_3 = "id_3";
    public static final String AREA_CODE_MAP = "area_map_code";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String AREA_NAME = "area_name";




    Context context;


    public DHelper(Context context) {
        super(context, D_NAME, null, 1); //Create database
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table " + T_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "phone_number TEXT)"); //Create table and set ID as primary key

        sqLiteDatabase.execSQL("create table " + T_NAME_2 + "(ID_2 INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "area_code TEXT)"); //Create table and set ID as primary key

        sqLiteDatabase.execSQL("create table " + T_NAME_3 + "(area_map_code TEXT, longitude TEXT, latitude TEXT, area_name TEXT)"); //Create table and set ID as primary key

        populate_map_db(sqLiteDatabase);



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + T_NAME);
        onCreate(sqLiteDatabase);

    }

     void insert_to_db(String value, int identifier) {

         SQLiteDatabase database = this.getWritableDatabase();
         ContentValues content = new ContentValues();

         if (identifier == 1){

             content.put(PHONE_NUMBER, value);
             database.insert(T_NAME, null, content);
             Log.d("Database Helper", "Phone No = " + content.getAsString(PHONE_NUMBER));

        }


        else
        {
            content.put(AREA_CODE, value);
            database.insert(T_NAME_2, null, content);

        }
    }

    String delete_from_db(String id_value, int identifier){

        SQLiteDatabase database = this.getWritableDatabase();
        if (identifier == 1)
        {

            database.delete(T_NAME, ID + "=" + id_value, null);
        }

        else
        {
            database.delete(T_NAME_2, ID_2 + "=" + id_value, null);
        }

        return "Data Deleted";

    }



    boolean num_exist(String number)
    {
        boolean flag;

        SQLiteDatabase database = this.getWritableDatabase();
        //RESEARCH CURSOR CLASS

        Cursor cursor = database.rawQuery("select * from " + T_NAME + " where " + PHONE_NUMBER + "=" +"'"+ number + "'",null);
        flag = cursor.getCount() > 0;
        cursor.close();
        return flag;

    }

    boolean area_code_exist(String number, int identifier)
    {
        Cursor cursor = null;
        SQLiteDatabase database = this.getReadableDatabase();
        int column_id;
        boolean flag = false;

        if (identifier == 1) {
             cursor = database.rawQuery("select * from " + T_NAME_2, null);
             column_id = 1;
        }

        else {
            cursor = database.rawQuery("select * from " + T_NAME_3, null);
            column_id = 0;
        }

        while (cursor.moveToNext()) {

            if (number.startsWith(cursor.getString(column_id)))
                flag = true;
        }
        cursor.close();
        return flag;
    }



    public Cursor getData(int identifier)
    {
        SQLiteDatabase database = this.getReadableDatabase();

        if (identifier == 1) { //RESEARCH CURSOR CLASS
            return database.rawQuery("select * from " + T_NAME,null);}

        else { //RESEARCH CURSOR CLASS
            return database.rawQuery("select * from " + T_NAME_2,null);}

    }

    private void populate_map_db(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("INSERT INTO " + T_NAME_3 + " VALUES('01282','-2.970721','56.462018', 'Dundee')");
        sqLiteDatabase.execSQL("INSERT INTO " + T_NAME_3 + " VALUES('01382','-4.629179','55.458565', 'Ayr')");
        sqLiteDatabase.execSQL("INSERT INTO " + T_NAME_3 + " VALUES('0141','-4.25','55.8333', 'Glasgow')");

    }


    public ArrayList getAreaCodeForMap(String number)
    {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<String> lister = new ArrayList<String>();
        cursor = database.rawQuery("select * from " + T_NAME_3, null);
        while (cursor.moveToNext()) {
            if (number.startsWith(cursor.getString(0)))
            {
                Log.d("Database Helper", "Found area code");

                lister.add(cursor.getString(0));
                lister.add(cursor.getString(1));
                lister.add(cursor.getString(2));
                lister.add(cursor.getString(3));

            }
        }
        cursor.close();
        return lister;
    }

}
