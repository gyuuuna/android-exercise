package com.example.memo_q;
import android.os.Build;
import androidx.annotation.RequiresApi;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Memo {
    String content;
    String datetime;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Memo(String content){
        this.content = content;
        datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"));
    }
}
