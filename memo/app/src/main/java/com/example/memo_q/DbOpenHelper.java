package com.example.memo_q;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DbOpenHelper {
    private static final String DATABASE_NAME = "InnerDatabase(SQLite).db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            Log.d(TAG, "DataBaseHelper 생성자 호출");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "Table Create");
            db.execSQL(DirDatabase.CreateDB._CREATE0);
            db.execSQL(MemoDatabase.CreateDB._CREATE0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            Log.d(TAG, "Table onUpgrade");
            // 테이블 재정의하기 위해 현재의 테이블 삭제
            String createQuery;
            createQuery = "DROP TABLE IF EXISTS " + DirDatabase.CreateDB._TABLENAME0 + ";";
            sqLiteDatabase.execSQL(createQuery);
            createQuery = "DROP TABLE IF EXISTS " + MemoDatabase.CreateDB._TABLENAME0 + ";";
            sqLiteDatabase.execSQL(createQuery);
            onCreate(sqLiteDatabase);
        }
    }

    public DbOpenHelper(Context context){ mCtx = context; }
    public DbOpenHelper open() throws SQLException{
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }
    public void create(){ mDBHelper.onCreate(mDB); }
    public void close(){ mDB.close(); }

    // INSERT

    public long insertColumnToDirTable(String name, String created_at){
        ContentValues values = new ContentValues();
        values.put(DirDatabase.CreateDB.NAME, name);
        values.put(DirDatabase.CreateDB.CREATED_AT, created_at);
        return mDB.insert(DirDatabase.CreateDB._TABLENAME0, null, values);
    }

    public long insertColumnToMemoTable(String content, String created_at, long dir_id){
        ContentValues values = new ContentValues();
        values.put(MemoDatabase.CreateDB.CONTENT, content);
        values.put(MemoDatabase.CreateDB.CREATED_AT, created_at);
        values.put(MemoDatabase.CreateDB.DIR_ID, dir_id);
        values.put(MemoDatabase.CreateDB.IS_DELETED, "false"); // 추가됨!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        return mDB.insert(MemoDatabase.CreateDB._TABLENAME0, null, values);
    }

    // SELECT

    public Cursor selectDir(){
        return mDB.query(DirDatabase.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }

    public Cursor selectColumnsFromDirTable(int position){
        Cursor selected = selectDir();
        selected.moveToPosition(position);
        return selected;
    }

    public Cursor selectMemo(){
        return mDB.query(MemoDatabase.CreateDB._TABLENAME0, null, "is_deleted=?", new String[]{"false"},  null, null, null);
    }

    public Cursor selectMemo(int dirId){
        return mDB.query(MemoDatabase.CreateDB._TABLENAME0, null, "dir_id=? AND is_deleted=?", new String[]{Integer.toString(dirId), "false"}, null, null, null);
    }

    public Cursor selectTrashMemo(){
        return mDB.query(MemoDatabase.CreateDB._TABLENAME0, null, "is_deleted=?", new String[]{"true"},  null, null, null);
    }

    public Cursor selectColumnsFromMemoTable(int dirId, int position){
        Cursor selected;
        if(dirId==2)
            selected = selectTrashMemo();
        else if(dirId<0)
            selected = selectMemo();
        else
            selected = selectMemo(dirId);

        selected.moveToPosition(position);
        return selected;
    }

    public Cursor selectColumnsFromMemoTable(int id){
        return mDB.query(MemoDatabase.CreateDB._TABLENAME0, null, "_id=?", new String[]{Integer.toString(id)}, null, null, null);
    }




    public Cursor sortColumnOfDirTable(String sort){
        Cursor c = mDB.rawQuery( "SELECT * FROM dirtable ORDER BY " + sort + ";", null);
        return c;
    }

    public Cursor sortColumnOfMemoTable(String sort){
        Cursor c = mDB.rawQuery( "SELECT * FROM memotable ORDER BY " + sort + ";", null);
        return c;
    }

    // UPDATE

    public boolean updateColumnToDirTable(int id, String name, String created_at){
        ContentValues values = new ContentValues();
        values.put(DirDatabase.CreateDB.NAME, name);
        values.put(DirDatabase.CreateDB.CREATED_AT, created_at);
        return mDB.update(DirDatabase.CreateDB._TABLENAME0, values, "_id=?", new String[]{Integer.toString(id)}) > 0;
    }

    /*
    public boolean updateColumnToMemoTable(int id, String content, String created_at, int dir_id){
        ContentValues values = new ContentValues();
        values.put(MemoDatabase.CreateDB.CONTENT, content);
        values.put(MemoDatabase.CreateDB.CREATED_AT, created_at);
        values.put(MemoDatabase.CreateDB.DIR_ID, dir_id);
        return mDB.update(MemoDatabase.CreateDB._TABLENAME0, values, "_id=?", new String[]{Integer.toString(id)}) > 0;
    }
     */

    public boolean updateColumnToMemoTable(int id, String content){
        ContentValues values = new ContentValues();
        values.put(MemoDatabase.CreateDB.CONTENT, content);
        return mDB.update(MemoDatabase.CreateDB._TABLENAME0, values, "_id=? AND is_deleted=?", new String[]{Integer.toString(id), "false"}) > 0;
    }

    public boolean updateColumnToMemoTable(int id, int dir_id){
        ContentValues values = new ContentValues();
        values.put(MemoDatabase.CreateDB.DIR_ID, dir_id);
        return mDB.update(MemoDatabase.CreateDB._TABLENAME0, values, "_id=? AND is_deleted=?", new String[]{Integer.toString(id), "false"}) > 0;
    }

    public boolean updateIsDeletedToTrue(int id){
        ContentValues values = new ContentValues();
        values.put(MemoDatabase.CreateDB.IS_DELETED, "true");
        return mDB.update(MemoDatabase.CreateDB._TABLENAME0, values, "_id=? AND is_deleted=?", new String[]{Integer.toString(id), "false"}) > 0;
    }

    public boolean updateIsDeletedToFalse(int id){
        ContentValues values = new ContentValues();
        values.put(MemoDatabase.CreateDB.IS_DELETED, "false");
        return mDB.update(MemoDatabase.CreateDB._TABLENAME0, values, "_id=? AND is_deleted=?", new String[]{Integer.toString(id), "true"}) > 0;
    }


    // DELETE ALL

    public void deleteAllColumnsOfDirTable() {
        mDB.delete(DirDatabase.CreateDB._TABLENAME0, null, null);
    }

    public void deleteAllColumnsOfMemoTable() {
        mDB.delete(MemoDatabase.CreateDB._TABLENAME0, null, null);
    }

    // DELETE COLUMN

    public boolean deleteColumnOfDirTable(long id){
        return mDB.delete(DirDatabase.CreateDB._TABLENAME0, "_id="+id, null) > 0;
    }

    public boolean deleteColumnOfMemoTable(long id){
        return mDB.delete(MemoDatabase.CreateDB._TABLENAME0, "_id=? AND is_deleted=?", new String[]{Long.toString(id), "true"}) > 0;
    }

    // Created at

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String createdAt(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"));
    }

    @SuppressLint("Range")
    public int lastDirId(){
        Cursor cursor = mDB.rawQuery( "SELECT MAX(_id) FROM dirtable;", null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    @SuppressLint("Range")
    public int lastMemoId(){
        Cursor cursor = mDB.rawQuery( "SELECT MAX(_id) FROM memotable;", null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    @SuppressLint("Range")
    public int getCountOfMemo(){
        Cursor cursor = mDB.rawQuery( "SELECT COUNT(_id) FROM memotable WHERE is_deleted=?;", new String[]{"false"});
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public int getCountOfMemo(int dirId){
        if(dirId<0) return getCountOfMemo();
        if(dirId==2){
            Cursor cursor = mDB.rawQuery( "SELECT COUNT(_id) FROM memotable WHERE is_deleted=?;", new String[]{"true"});
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        Cursor cursor = mDB.rawQuery( "SELECT COUNT(_id) FROM memotable WHERE dir_id=? AND is_deleted=?;", new String[]{Integer.toString(dirId), "false"});
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public int getCountOfDir(){
        Cursor cursor = mDB.rawQuery( "SELECT COUNT(_id) FROM dirtable;", null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

}
