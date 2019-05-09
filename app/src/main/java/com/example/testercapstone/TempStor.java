package com.example.testercapstone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

import java.util.ArrayList;
import java.util.List;

public class TempStor {
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE Photo (Photo_ID INTEGER PRIMARY KEY, Location TEXT, Inspection_ID int, Title TEXT, Comment TEXT, Drone_Name TEXT)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS Photo";

    DbHelper dbHelper;

    public TempStor(Context context){
        dbHelper = new DbHelper(context);
    }

    public class DbHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "TempStor.db";

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }

    public void recreate(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public long add(String location, int inspID, String title, String comment, String droneName){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Location", location);
        values.put("Inspection_ID", inspID);
        values.put("Title", title);
        values.put("Comment", comment);
        values.put("Drone_Name", droneName);

        long success = db.insert("Photo",null, values);

        while(db.isDbLockedByCurrentThread());
        return success;
    }

    public MetaData[] getAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("Photo",null,null,null,null,null,null);

        List<MetaData> entries = new ArrayList<MetaData>();

        while(cursor.moveToNext()){
            MetaData newMeta = new MetaData();
            newMeta.photoID = cursor.getInt(0);
            newMeta.location = cursor.getString(1);
            newMeta.inspID = cursor.getInt(2);
            newMeta.title = cursor.getString(3);
            newMeta.comment = cursor.getString(4);
            newMeta.droneName = cursor.getString(5);
            entries.add(newMeta);
        }
        cursor.close();

        MetaData[] temp = new MetaData[0];
        return entries.toArray(temp);
    }

    public MetaData[] getByInspID(int inspID){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] selectionArgs = {"" + inspID};

        Cursor cursor = db.query("Photo",null,"Inspection_ID = ?",selectionArgs,null,null,null);
        List<MetaData> entries = new ArrayList<MetaData>();

        while(cursor.moveToNext()){
            MetaData newMeta = new MetaData();
            newMeta.photoID = cursor.getInt(0);
            newMeta.location = cursor.getString(1);
            newMeta.inspID = cursor.getInt(2);
            newMeta.title = cursor.getString(3);
            newMeta.comment = cursor.getString(4);
            newMeta.droneName = cursor.getString(5);
            entries.add(newMeta);
        }
        cursor.close();

        MetaData[] temp = new MetaData[0];
        return entries.toArray(temp);
    }

    public MetaData getByPhotoID(int photoID){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] selectionArgs = {"" + photoID};

        Cursor cursor = db.query("Photo",null,"Photo_ID = ?",selectionArgs,null,null,null);
        cursor.moveToNext();

        MetaData newMeta = new MetaData();
        newMeta.photoID = cursor.getInt(0);
        newMeta.location = cursor.getString(1);
        newMeta.inspID = cursor.getInt(2);
        newMeta.title = cursor.getString(3);
        newMeta.comment = cursor.getString(4);
        newMeta.droneName = cursor.getString(5);

        return newMeta;
    }

    public MetaData[] getByLocation(String location){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] selectionArgs = {location};

        Cursor cursor = db.query("Photo",null,"Location = ?",selectionArgs,null,null,null);
        List<MetaData> entries = new ArrayList<MetaData>();

        while(cursor.moveToNext()){
            MetaData newMeta = new MetaData();
            newMeta.photoID = cursor.getInt(0);
            newMeta.location = cursor.getString(1);
            newMeta.inspID = cursor.getInt(2);
            newMeta.title = cursor.getString(3);
            newMeta.comment = cursor.getString(4);
            newMeta.droneName = cursor.getString(5);
            entries.add(newMeta);
        }
        cursor.close();

        MetaData[] temp = new MetaData[0];
        return entries.toArray(temp);
    }

    public MetaData[] getByDroneName(String location){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] selectionArgs = {location};

        Cursor cursor = db.query("Photo",null,"Drone_Name = ?",selectionArgs,null,null,null);
        List<MetaData> entries = new ArrayList<MetaData>();

        while(cursor.moveToNext()){
            MetaData newMeta = new MetaData();
            newMeta.photoID = cursor.getInt(0);
            newMeta.location = cursor.getString(1);
            newMeta.inspID = cursor.getInt(2);
            newMeta.title = cursor.getString(3);
            newMeta.comment = cursor.getString(4);
            newMeta.droneName = cursor.getString(5);
            entries.add(newMeta);
        }
        cursor.close();

        MetaData[] temp = new MetaData[0];
        return entries.toArray(temp);
    }

    public void editTitle(String title, int photoID){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("Title",title);
        String[] selectionArgs = {"" + photoID};

        db.update("Photo",cv,"Photo_ID = ?",selectionArgs);
    }

    public void editComment(String comment, int photoID){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("Comment",comment);
        String[] selectionArgs = {"" + photoID};

        db.update("Photo",cv,"Photo_ID = ?",selectionArgs);
    }

    public void editDroneName(String droneName, int photoID){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("Drone_Name",droneName);
        String[] selectionArgs = {"" + photoID};

        db.update("Photo",cv,"Photo_ID = ?",selectionArgs);
    }
}