package com.example.memo_q;

import android.database.Cursor;

public class Dir {
    static int MENU_ALL = -1;
    static int MENU_MY = 0;
    static int MENU_DOCU = 1;
    static int MENU_TRASH = 2;

    static int DEFAULT_DIRS_COUNT = 3;

    static String getDefaultDirName(int dirId){
        if(dirId==MENU_ALL) return "모든 메모";
        if(dirId==MENU_MY) return "내 메모";
        if(dirId==MENU_DOCU) return "문서";
        if(dirId==MENU_TRASH) return "휴지통";
        return null;
    }
}

