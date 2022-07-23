package com.example.memo_q;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        int dirId = intent.getIntExtra("dir_id", -1);
        String content = intent.getStringExtra("content");
        String datetime = intent.getStringExtra("datetime");

        EditText editText = (EditText) findViewById(R.id.editTextTextMultiLine);
        editText.setText(content);

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(datetime+"에 생성됨");

        ImageButton checkButton = (ImageButton) findViewById(R.id.ib_check);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent finishIntent = new Intent(getApplicationContext(), MainActivity.class);
                finishIntent.putExtra("content", editText.getText().toString());
                finishIntent.putExtra("id", id);
                finishIntent.putExtra("dir_id", dirId);
                setResult(9001, finishIntent);
                finish();
            }
        });

        ImageButton threeDotsButton = (ImageButton) findViewById(R.id.ib_three_dots);
        threeDotsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), view);
                getMenuInflater().inflate(R.menu.etc_option_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch(menuItem.getItemId()){
                            case R.id.etc_option_delete:{
                                Intent finishIntent = new Intent(getApplicationContext(), MainActivity.class);
                                finishIntent.putExtra("id", id);
                                finishIntent.putExtra("dir_id", dirId);
                                setResult(9002, finishIntent);
                                finish();
                                break;
                            }
                            case R.id.etc_option_trasfer:{
                                break;
                            }
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });


    }
}