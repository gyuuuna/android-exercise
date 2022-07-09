package com.example.memo_q;
import android.os.Build;
import androidx.annotation.RequiresApi;
import java.time.LocalDateTime;

public class Memo {
    String title;
    String content;
    LocalDateTime datetime;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Memo(String title, String content){
        this.title = title;
        this.content = content;
        datetime = LocalDateTime.now();
    }
}
