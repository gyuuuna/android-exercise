package com.example.memo_q;
import android.provider.BaseColumns;

public final class MemoDatabase{
    public static final class CreateDB implements BaseColumns{
        public static final String CONTENT = "content";
        public static final String CREATED_AT = "created_at";
        public static final String DIR_ID = "dir_id";
        public static final String IS_DELETED = "is_deleted";
        public static final String _TABLENAME0 = "memotable";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +CONTENT+" text, "
                +CREATED_AT+" text, "
                +DIR_ID+" integer, "
                +IS_DELETED+" text )";
    }
}
