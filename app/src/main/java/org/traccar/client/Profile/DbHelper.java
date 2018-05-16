package org.traccar.client.Profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private final String TABLE_USER = "user";
    private final String USER_ID = "id";
    private final String USER_NAME = "name";
    private final String USER_LASTNAME = "lastname";
    private final String USER_PHONE = "phone";
    private final String USER_DEVICE = "device";
    private final String USER_IMAGE = "image";


    public DbHelper(Context context) {
        super(context, "db", null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE "+TABLE_USER+"("+USER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+USER_NAME+","+USER_LASTNAME+","+USER_IMAGE+","+USER_PHONE+","+USER_DEVICE+");";
        db.execSQL(createUserTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertUser(UserItem user){

        // First delete already user
        db.execSQL("DELETE FROM "+TABLE_USER);

        ContentValues values = new ContentValues();
        values.put(USER_DEVICE,user.getDevice());
        if(user.getImg()!=null) values.put(USER_IMAGE,user.getImg());
        values.put(USER_LASTNAME,user.getLast());
        values.put(USER_NAME,user.getName());
        values.put(USER_PHONE,user.getPhone());

        db.insert(TABLE_USER,null,values);
    }

    public UserItem getUser(){
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_USER+";",null);
        c.moveToFirst();
        if(c.getCount()>0){
            UserItem user = new UserItem();
            user.setPhone(c.getString(c.getColumnIndex(USER_PHONE)));
            user.setName(c.getString(c.getColumnIndex(USER_NAME)));
            user.setLast(c.getString(c.getColumnIndex(USER_LASTNAME)));
            user.setImg(c.getString(c.getColumnIndex(USER_IMAGE)));
            c.close();
            return user;
        }else return null;
    }
}
