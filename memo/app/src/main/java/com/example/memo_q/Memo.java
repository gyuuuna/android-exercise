package com.example.memo_q;
import android.database.Cursor;

public class Memo {

    private int id;
    private DbOpenHelper mDbOpenHelper;
    Memo(int id, DbOpenHelper dbOpenHelper){ this.id = id; this.mDbOpenHelper = dbOpenHelper; }

    void update(String content){
        Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(id);
        cursor.moveToFirst();
        mDbOpenHelper.updateColumnToMemoTable(cursor.getInt(0), content);
    }
    void delete(){ mDbOpenHelper.updateIsDeletedToTrue(id); }
    void realDelete(){ mDbOpenHelper.deleteColumnOfMemoTable(id); }
    void restore(){ mDbOpenHelper.updateIsDeletedToFalse(id); }
    void changeDir(int newDirId){ mDbOpenHelper.updateColumnToMemoTable(id, newDirId); }
}