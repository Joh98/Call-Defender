package com.aa.calldefender;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

//Database helper class
public class DHelper extends SQLiteOpenHelper {

    //Declare variables
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
        this.context = context; //Set context

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table " + T_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "phone_number TEXT)"); //Create table and set ID as primary key

        sqLiteDatabase.execSQL("create table " + T_NAME_2 + "(ID_2 INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "area_code TEXT)"); //Create table and set ID as primary key

        sqLiteDatabase.execSQL("create table " + T_NAME_3 + "(area_map_code TEXT, longitude TEXT, latitude TEXT, area_name TEXT)"); //Create table and set ID as primary key

        populate_map_db(sqLiteDatabase); //Call function to populate the map DB table
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + T_NAME);
        onCreate(sqLiteDatabase);

    }

     void insert_to_db(String value, int identifier) { //Function for inserting data to the DB

         //Setup DB for writing
         SQLiteDatabase database = this.getWritableDatabase();
         ContentValues content = new ContentValues();

         //If passed in identifier is 1, add 'value' to the phone numbers tables
         if (identifier == 1){

             content.put(PHONE_NUMBER, value);
             database.insert(T_NAME, null, content);
             Log.d("Database Helper", "Phone No = " + content.getAsString(PHONE_NUMBER));
        }

        else //else add 'value' into the area codes table
        {
            content.put(AREA_CODE, value);
            database.insert(T_NAME_2, null, content);
        }
    }

    String delete_from_db(String id_value, int identifier){ //Function for deleting data from the DB

       //Set up DB for writing
        SQLiteDatabase database = this.getWritableDatabase();

        //If passed in identifier is 1, use 'id_value' delete the required phone number using ID
        if (identifier == 1)
        {
            database.delete(T_NAME, ID + "=" + id_value, null);
        }

        else //else use 'id_value' to delete the required area code using ID
        {
            database.delete(T_NAME_2, ID_2 + "=" + id_value, null);
        }

        return "Data Deleted"; //Return string to async task
    }

    boolean num_exist(String number) //Function for determining if a phone number exists in the DB
    {
        boolean flag; //declare boolean

        //Set up DB
        SQLiteDatabase database = this.getWritableDatabase();

        //Save query result to a cursor
        Cursor cursor = database.rawQuery("select * from " + T_NAME + " where " + PHONE_NUMBER + "=" +"'"+ number + "'",null);
        flag = cursor.getCount() > 0; //set boolean value (if number exists flag = 1, else = 0)
        cursor.close(); //close cursor
        return flag; //Return boolean to async task

    }

    boolean area_code_exist(String number, int identifier) //Function for determining if an area code exists in the DB
    {
        //Declare variables
        Cursor cursor;
        SQLiteDatabase database = this.getReadableDatabase();
        int column_id;
        boolean flag = false;

        //If passed in 'identifier' = 1 query area codes table
        if (identifier == 1) {
             cursor = database.rawQuery("select * from " + T_NAME_2, null);
             column_id = 1;
        }

        else { //else query maps area code table
            cursor = database.rawQuery("select * from " + T_NAME_3, null);
            column_id = 0;
        }

        while (cursor.moveToNext()) { //Move through each query result

            if (number.startsWith(cursor.getString(column_id))) //If the result starts with a known area code
                flag = true; //set true

        }
        cursor.close(); //close the cursor
        return flag; //return boolean to async task
    }

    public Cursor getData(int identifier) //Function that retrieves phone number/area code data from the DB
    {
        //Setup DB for reading
        SQLiteDatabase database = this.getReadableDatabase();

        //If passed in 'identifier' = 1 query phone numbers table and return its contents to async task
        if (identifier == 1) { //RESEARCH CURSOR CLASS
            return database.rawQuery("select * from " + T_NAME,null);}

        else { //else query area codes table and return its contents to async task
            return database.rawQuery("select * from " + T_NAME_2,null);}

    }

    private void populate_map_db(SQLiteDatabase sqLiteDatabase) //Function for populating the DB with map information on specific area codes
    {
        sqLiteDatabase.execSQL("INSERT INTO " + T_NAME_3 + " VALUES('01382','-2.970721','56.462018', 'Dundee')");
        sqLiteDatabase.execSQL("INSERT INTO " + T_NAME_3 + " VALUES('01292','-4.629179','55.458565', 'Ayr')");
        sqLiteDatabase.execSQL("INSERT INTO " + T_NAME_3 + " VALUES('0141','-4.25','55.8333', 'Glasgow')");

    }


    public ArrayList getAreaCodeForMap(String number) //Function for retrieving data that will be used for the Google map
    {
        //Set up DB for writing and declare variables
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor;
        ArrayList<String> lister = new ArrayList<String>();

        //Select data from maps area code table and save to a cursor
        cursor = database.rawQuery("select * from " + T_NAME_3, null);

        //Loop through the results and if passed in 'number' starts with any of the results add to list
        while (cursor.moveToNext()) {
            if (number.startsWith(cursor.getString(0)))
            {
                Log.d("Database Helper", "Found area code"); //Debug

                lister.add(cursor.getString(0));
                lister.add(cursor.getString(1));
                lister.add(cursor.getString(2));
                lister.add(cursor.getString(3));

            }
        }
        cursor.close(); //close cursor
        return lister; //return list to async task
    }

}
