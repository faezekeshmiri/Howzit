package com.example.faridam.howzit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Image;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by farida.M on 6/29/2020.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super( context, "howzit.db", null, 9 );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "create table user(name text NOT NULL,id text primary key,img blob )" );
        db.execSQL( "create table chat(id INTEGER primary key AUTOINCREMENT,deleteTime INTEGER,creatorID text,FOREIGN KEY(creatorID) REFERENCES user(id) ON UPDATE CASCADE)" );
        db.execSQL( "create table message(text text,id INTEGER primary key AUTOINCREMENT,chatID INTEGER, senderID text,FOREIGN KEY(chatID) REFERENCES chat(id) ON UPDATE CASCADE,FOREIGN KEY(senderID) REFERENCES user(id) ON UPDATE CASCADE)" );
        db.execSQL( "create table chat_member(id INTEGER primary key AUTOINCREMENT,chatID INTEGER, memberID text,FOREIGN KEY(chatID) REFERENCES chat(id) ON UPDATE CASCADE,FOREIGN KEY(memberID) REFERENCES user(id) ON UPDATE CASCADE)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "drop table if exists user" );
        db.execSQL( "drop table if exists chat" );
        db.execSQL( "drop table if exists message" );
        db.execSQL( "drop table if exists chat_member" );
        onCreate(db);

    }

    public User insertUser(String name, String id , String imgLocation){
        SQLiteDatabase db = this.getWritableDatabase();
        User user = new User();
        try {
            FileInputStream fs = null;
            byte[] imgByte;
            if(imgLocation == null){
                imgByte = null;
            }else {
                fs = new FileInputStream(imgLocation);
                imgByte = new byte[fs.available()];
                fs.read(imgByte);
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put("name" , name);
            contentValues.put("id" , id);
            contentValues.put("img",imgByte);
            db.insert("user" , null , contentValues);
            user.setName(name);
            user.setUid(id);
            user.setProfile(imgByte);
            if (fs!=null){
                fs.close();
            }

        }  catch (IOException e) {
            e.printStackTrace();
            user = null;
        }
        return user;

    }

    public User checkUser(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT name , id FROM user WHERE id = ? ",new String[]{id});
        //Cursor profileCursor = db.rawQuery("SELECT img FROM user WHERE id = ? ",new String[]{id});
        User user = new User();
        if (result.moveToFirst()){
            String userID = result.getString(result.getColumnIndex("id"));
            String name = result.getString(result.getColumnIndex("name"));
            //byte[] profile = profileCursor.getBlob(result.getColumnIndex("img"));
            user.setName(name);
            user.setUid(userID);
            //user.setProfile(profile);
        }else {
            user = null;
        }
        result.close();
        //profileCursor.close();
        return user;
    }
/////////////////////////create chat and get all contactIds//////////////////////
    public int createChat(String creatorID , String contactID , String contactName){
        SQLiteDatabase db = this.getWritableDatabase();
        //insertUser(contactName,contactID,null);
        //insert in chat table
        ContentValues contentValues = new ContentValues();
        contentValues.put("creatorID",creatorID);
        contentValues.put("deleteTime",0);//set defult delete time to 0 ==> never
        db.insert("chat" , null , contentValues);
        //insert contact into chat_member table
        int chatID = getChatTableLastID();
        ContentValues contentValues2 = new ContentValues();
        contentValues2.put("memberID",contactID);
        contentValues2.put("chatID",chatID);
        db.insert("chat_member" , null , contentValues2);
        //chat created and new contact added
        return chatID;

    }

    public int getChatTableLastID(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor chatIdCursor = db.rawQuery("SELECT MAX(id) FROM chat",null);
        int chatId = -1;
        if (chatIdCursor.moveToFirst())
            chatId = chatIdCursor.getInt(0);
        chatIdCursor.close();

        return chatId;
    }

    public ArrayList<User> getAllContacts(String userID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT name , id FROM user WHERE id != ? ",new String[]{userID});
        ArrayList<User> contacts= new ArrayList<>();
        if (result.moveToFirst()){
            do {
                User user = new User();
                String ID = result.getString(result.getColumnIndex("id"));
                String name = result.getString(result.getColumnIndex("name"));
                //byte[] profile = profileCursor.getBlob(result.getColumnIndex("img"));
                user.setName(name);
                user.setUid(ID);
                contacts.add(user);
            }while (result.moveToNext());
        }
        return contacts;

    }


}
