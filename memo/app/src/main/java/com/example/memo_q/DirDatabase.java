package com.example.memo_q;
import android.provider.BaseColumns;

public final class DirDatabase{
    public static final class CreateDB implements BaseColumns{
        public static final String NAME = "name";
        public static final String CREATED_AT = "created_at";
        public static final String _TABLENAME0 = "dirtable";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +NAME+" text not null, "
                +CREATED_AT+" text not null );";
    }
}
