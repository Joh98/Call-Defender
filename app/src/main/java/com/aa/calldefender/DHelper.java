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
    public static final String PHONE_NUMBER_TABLE = "numbers_table";
    public static final String ID = "id";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String AREA_CODE_TABLE = "area_code_table";
    public static final String ID_2 = "id_2";
    public static final String AREA_CODE = "area_code";
    public static final String AREA_CODE_MAP_TABLE = "map_table";
    Context context;


    public DHelper(Context context) {
        super(context, D_NAME, null, 1); //Create database
        this.context = context; //Set context
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table " + PHONE_NUMBER_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "phone_number TEXT)"); //Create phone number  table and set ID as primary key

        sqLiteDatabase.execSQL("create table " + AREA_CODE_TABLE + "(ID_2 INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "area_code TEXT)"); //Create area code table and set ID as primary key

        sqLiteDatabase.execSQL("create table " + AREA_CODE_MAP_TABLE + "(area_map_code TEXT, longitude TEXT, latitude TEXT, area_name TEXT)"); //Create map area code table

        populateMapDb(sqLiteDatabase); //Call function to populate the map area code table
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PHONE_NUMBER_TABLE);
        onCreate(sqLiteDatabase);
    }

     void insertToDb(String value, int identifier) { //Function for inserting data to the DB

         //Setup DB for writing
         SQLiteDatabase database = this.getWritableDatabase();
         ContentValues content = new ContentValues();

         //If passed in identifier is 1, add 'value' to the phone numbers tables
         if (identifier == 1){
             content.put(PHONE_NUMBER, value);
             database.insert(PHONE_NUMBER_TABLE, null, content);
        }

        else { //Else add 'value' into the area codes table

            content.put(AREA_CODE, value);
            database.insert(AREA_CODE_TABLE, null, content);
        }
    }

    String deleteFromDb(String id_value, int identifier){ //Function for deleting data from the DB

       //Set up DB for writing
        SQLiteDatabase database = this.getWritableDatabase();

        //If passed in identifier is 1, use 'id_value' to delete the required phone number using ID
        if (identifier == 1) {
            database.delete(PHONE_NUMBER_TABLE, ID + "=" + id_value, null);
        }
        else { //else use 'id_value' to delete the required area code using ID
            database.delete(AREA_CODE_TABLE, ID_2 + "=" + id_value, null);
        }

        return "Data Deleted"; //Return string to async task
    }

    boolean numExist(String number) { //Function for determining if a phone number exists in the DB

        boolean flag; //declare boolean flag

        //Set up DB for reading
        SQLiteDatabase database = this.getReadableDatabase();

        //Save query result to a cursor
        Cursor cursor = database.rawQuery("select * from " + PHONE_NUMBER_TABLE + " where " + PHONE_NUMBER + "=" +"'"+ number + "'",null);
        flag = cursor.getCount() > 0; //set boolean value (if number exists flag = 1, else = 0)
        cursor.close(); //Close cursor
        return flag; //Return boolean to async task
    }

    boolean areaCodeExist(String number, int identifier) //Function for determining if an area code exists in the DB
    {
        //Set up DB for reading and declare variables
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor;
        int column_id;
        boolean flag = false;

        //If passed in 'identifier' = 1 query area codes table
        if (identifier == 1) {
             cursor = database.rawQuery("select * from " + AREA_CODE_TABLE, null);
             column_id = 1;
        }
        else { //Else query maps area code table
            cursor = database.rawQuery("select * from " + AREA_CODE_MAP_TABLE, null);
            column_id = 0;
        }

        while (cursor.moveToNext()) { //Move through each query result

            if (number.startsWith(cursor.getString(column_id))) //If the result starts with a known area code
                flag = true; //set true
        }
        cursor.close(); //close the cursor
        return flag; //return boolean to async task
    }

    Cursor getData(int identifier) //Function that retrieves phone number/area code data from the DB
    {
        //Setup DB for reading
        SQLiteDatabase database = this.getReadableDatabase();

        //If passed in 'identifier' = 1 query phone numbers table and return its contents to async task
        if (identifier == 1) { //RESEARCH CURSOR CLASS
            return database.rawQuery("select * from " + PHONE_NUMBER_TABLE,null);
        }
        else { //else query area codes table and return its contents to async task
            return database.rawQuery("select * from " + AREA_CODE_TABLE,null);
        }
    }

    private void populateMapDb(SQLiteDatabase sqLiteDatabase) { //Function for populating the DB with map information on specific area codes

        sqLiteDatabase.execSQL("INSERT INTO " + AREA_CODE_MAP_TABLE + " VALUES('01382','-2.970721','56.462018', 'Dundee')");
        sqLiteDatabase.execSQL("INSERT INTO " + AREA_CODE_MAP_TABLE + " VALUES('01292','-4.629179','55.458565', 'Ayr')");
        sqLiteDatabase.execSQL("INSERT INTO " + AREA_CODE_MAP_TABLE + " VALUES('0141','-4.25','55.8333', 'Glasgow')");

    }

    ArrayList getAreaCodeForMap(String number) { //Function for retrieving data that will be used for the Google map

        //Set up DB for reading and declare variables
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor;
        ArrayList<String> lister = new ArrayList<String>();

        //Select data from maps area code table and save to a cursor
        cursor = database.rawQuery("select * from " + AREA_CODE_MAP_TABLE, null);

        //Loop through the results and if passed in 'number' starts with any of the results add to list
        while (cursor.moveToNext()) {
            if (number.startsWith(cursor.getString(0))) {

                Log.d("Database Helper", "Found area code"); //Debug

                lister.add(cursor.getString(0));
                lister.add(cursor.getString(1));
                lister.add(cursor.getString(2));
                lister.add(cursor.getString(3));

            }
        }
        cursor.close(); //Close cursor
        return lister; //return list to async task
    }
}
