package com.example.victor.quiescence;

/**
 * Created by victor on 2017/3/4.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
//import java.util.Date;

/**
 * Created by victor on 2017/2/24.
 */

public class dataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1; //version
    private static final String LOG = "DatabaseHelper";

    // Database Name
    private static final String DATABASE_NAME = "roomManager";
    private static final String TABLE_ROOM = "rooms";
    private static final String TABLE_SCHEDULE = "schdule";
    private static final String TABLE_LOCATION = "campus";
    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TIME = "creat_time";

    //column name for rooms
    private static final String KEY_VOLLUM = "noise";
    //private static final String KEY_LOCATION="campus";
    //private static final String KEY_BUILDING = "building";
    //column name for schedule
    private static final String KEY_SCHEDULE = "day";
    private static final String KEY_SLOT = "time";
    private static final String KEY_DAY ="TEXT";

    //column name for location

    // private static final String KEY_LOCATION="campus";
    private static final String KEY_BUILDING = "building";
    private static final String KEY_ROOM = "room";


    // Table Create Statements
    // Todo table create statement
    private static final String CREATE_TABLE_ROOM = "CREATE TABLE "
            + TABLE_ROOM + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE
            + " TEXT," + KEY_VOLLUM + " REAL, " + KEY_BUILDING+" TEXT, "+ KEY_TIME
            + " DATETIME" + ")";

    // Tag table create statement
   private static final String CREATE_TABLE_SCHEDULE = "CREATE TABLE if not exists " + TABLE_SCHEDULE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT," + KEY_VOLLUM + " REAL,"
            + KEY_TIME + " DATETIME" + ")";
  /*  private static final String CREATE_TABLE_SCHEDULE = "CREATE TABLE if not exists " + TABLE_SCHEDULE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY,"  +KEY_DAY+" TEXT "+ KEY_TITLE+" TEXT "+ KEY_TIME + " DATETIME" + ")";*/
    private static final String CREATE_TABLE_LOCATION = "CREATE TABLE " + TABLE_LOCATION
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT," + KEY_BUILDING + " TEXT,"
            + KEY_ROOM + " TEXT" + ")";
    //  SQLiteDatabase db = this.getReadableDatabase();

    public dataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public int getDatabaseVersion( ){return DATABASE_VERSION;};

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ROOM);
        db.execSQL(CREATE_TABLE_SCHEDULE);
        db.execSQL(CREATE_TABLE_LOCATION);
    }

    //TODO on upgrade drop older tables
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);

        // create new tables
        onCreate(db);
      //  db.close();
    }
    public void deleteTables( ) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.close();


        // create new tables
       // onCreate(db);
    }



    public long createRoom(String loc, String type, String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, name);
        values.put(KEY_VOLLUM, 0);
        values.put(KEY_BUILDING,type);
        values.put(KEY_TIME, dateFormat.format(new Date()));

        // insert row
        long class_id = db.insert(TABLE_ROOM, null, values);



       return class_id;
    }

    public long addItem(String type, String name, float noise) {
        SQLiteDatabase db = this.getWritableDatabase();

        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, name);
        values.put(KEY_VOLLUM, noise);

        values.put(KEY_TIME, dateFormat.format(new Date()));

        // insert row
       long temp= db.insert(TABLE_SCHEDULE, null, values);

        db.close();
        return temp;


    }


    public long createRoom(String loc, String type, String name, float noise) {
        SQLiteDatabase db = this.getWritableDatabase();

        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, name);
        values.put(KEY_VOLLUM, noise);
        values.put(KEY_BUILDING,type);
        values.put(KEY_TIME, dateFormat.format(new Date()));

        // insert row
        long class_id = db.insert(TABLE_ROOM, null, values);
        db.close();
      /*  for (int i=0; i<course.getAssignment().size(); i++)
            createAssign(class_id,course.getAssignment().get(i));*/

        return class_id;
    }
    public int updateRoom(String building,String room,float noise ) {
        SQLiteDatabase db = this.getWritableDatabase();
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String selectQuery = "SELECT * FROM " + TABLE_ROOM +" WHERE " + KEY_TITLE + " = " + "?" ;

        Cursor c = db.rawQuery( selectQuery, new String[]{room});

        if (c.getCount()<=0)
            return (int) createRoom("SGW",building,room,noise);


        ContentValues values = new ContentValues();
        values.put(KEY_VOLLUM, noise);
        values.put(KEY_BUILDING,building);
        values.put(KEY_TIME, dateFormat.format(new Date()) );

        int temp= db.update(TABLE_ROOM, values, String.format("%s = ?", KEY_TITLE), new String[]{room});
        db.close();
        return temp;
      //  return db.update(TABLE_ROOM, values, KEY_ROOM + " =?", new String[] { room });
    }

    public long addLocation(String loc, String type, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, loc);
        values.put(KEY_BUILDING, type);
        values.put(KEY_ROOM, name);
        long id = db.insert(TABLE_LOCATION, null, values);
        return id;

    }


    public ArrayList<Room> getRooms(String location, int noise)
    {
        ArrayList < Room> quietRooms =new ArrayList<>();
        ArrayList<String> roomList=new ArrayList<>();
        // roomList= roomList(location);

       String selectQuery = "SELECT * FROM " + TABLE_ROOM +" WHERE " + KEY_BUILDING + " = " + "?" ;
        SQLiteDatabase db = this.getReadableDatabase();
       Cursor c = db.rawQuery( selectQuery, new String[]{location});
        //long temp=0;
        int hour;
        //  java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //  Date date = new Date();
       // String temp="";


        if (c.moveToFirst()) {
            do {
                if (c.getFloat(c.getColumnIndex(KEY_VOLLUM))< noise)
                quietRooms.add(new Room(c.getString(c.getColumnIndex(KEY_TITLE)), (int) c.getFloat(c.getColumnIndex(KEY_VOLLUM)),c.getString(c.getColumnIndex(KEY_TIME))));
               // quietRooms.add(new Room(c.getString(c.getColumnIndex(KEY_TITLE)), (int) c.getFloat(c.getColumnIndex(KEY_VOLLUM))));
                // temp="";
            } while (c.moveToNext());
        }
        c.close();
        if (quietRooms.size()==0)
            quietRooms.add(new Room("---",0,"1900-01-01"));
        return quietRooms;

    }
    // public void renew(String room)
    public ArrayList<String> roomList(String building)
    {
        ArrayList<String> list =new ArrayList<>();
    /*    String selectQuery = "SELECT  TITLE FROM " + TABLE_LOCATION+" WHERE "
                + KEY_BUILDING + " == " + building ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                // adding to assignment list
                //temp=c.getString(c.getColumnIndex(KEY_TIME));

                list.add(new String(c.getString(c.getColumnIndex(KEY_TITLE))));
                // temp="";


            } while (c.moveToNext());
        }
        c.close();*/

        if(list.size()==0)
            list.add(new String("--"));

        return list;
    }

    public void deleteOld(Calendar today) {
        SQLiteDatabase db = this.getWritableDatabase();
        String start;
        java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        today.set(Calendar.DATE, today.get(Calendar.DATE) -6);

        start =sdf.format(today.getTime());
        //  String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE+" WHERE " + KEY_TITLE + " = ?"+" and "+"datetime( "+
        //     KEY_TIME + ") >= '?' and "+"datetime( "+KEY_TIME+" )< '? '";
        //  String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE+" WHERE " + KEY_TITLE + " = ?"+" and "+
        //    "datetime('"+KEY_TIME+"')>=datetime("+start+") and datetime('"+KEY_TIME+"') < datetime("+end+")";
       // String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE+" WHERE " + KEY_TITLE + " = ? and "+
         //       KEY_TIME+">='"+start+"' and "+KEY_TIME+"< '" +end+"'";



        db.delete(TABLE_SCHEDULE, KEY_TIME + " < ?",
                new String[] { start });
        db.close();
    }

    public ArrayList<roomLog> getroomLog(String room)
    {
        ArrayList<roomLog> list =new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE+" WHERE " + KEY_TITLE + " = ?"  ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery( selectQuery, new String[]{room});
       if (c.moveToFirst()) {
            do {
                list.add(new roomLog(c.getFloat(c.getColumnIndex(KEY_VOLLUM)),c.getString(c.getColumnIndex(KEY_TIME))));
            } while (c.moveToNext());
        }
        c.close();

        if(list.size()==0)
            list.add(new roomLog(0,"--"));
        return list;
    }

    public ArrayList<roomLog> getRoomLog(String room,int year, int month, int day)
    {
        ArrayList<roomLog> list =new ArrayList<>();

        String start ,end;
        java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cur_Calender=Calendar.getInstance();
          cur_Calender.set(year, month-1, day,8,0,0);
          start =sdf.format(cur_Calender.getTime());
        cur_Calender.set(year, month-1, day,23,0,0);
                   end= sdf.format(cur_Calender.getTime());



       //  String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE+" WHERE " + KEY_TITLE + " = ?"+" and "+"datetime( "+
         //     KEY_TIME + ") >= '?' and "+"datetime( "+KEY_TIME+" )< '? '";
        //  String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE+" WHERE " + KEY_TITLE + " = ?"+" and "+
          //    "datetime('"+KEY_TIME+"')>=datetime("+start+") and datetime('"+KEY_TIME+"') < datetime("+end+")";
        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE+" WHERE " + KEY_TITLE + " = ? and "+
                KEY_TIME+">='"+start+"' and "+KEY_TIME+"< '" +end+"'";

        Log.i("data",selectQuery);

       SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery( selectQuery, new String[]{room});
      if (c.moveToFirst()) {
            do {
                list.add(new roomLog(c.getFloat(c.getColumnIndex(KEY_VOLLUM)),c.getString(c.getColumnIndex(KEY_TIME))));
            } while (c.moveToNext());
        }
        c.close();

        if(list.size()==0)
            list.add(new roomLog(10,"--"));
        return list;
    }



}
