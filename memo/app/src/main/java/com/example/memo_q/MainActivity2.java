package com.example.memo_q;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {
    private int id;
    private int newDirId;

    ActivityResultLauncher<Intent> selectPopupResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 9003) {
                        Intent intent = result.getData();
                        newDirId = intent.getIntExtra("new_dir_id", 0);

                        Intent finishIntent = new Intent(MainActivity2.this, MainActivity.class);
                        finishIntent.putExtra("id", id);
                        finishIntent.putExtra("new_dir_id", newDirId);
                        setResult(9003, finishIntent);
                        finish();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        String content = intent.getStringExtra("content");
        String datetime = intent.getStringExtra("datetime");

        EditText editText = findViewById(R.id.editTextTextMultiLine);
        editText.setText(content);

        TextView textView = findViewById(R.id.textView);
        textView.setText(datetime+"에 생성됨");

        ImageButton checkButton = findViewById(R.id.ib_check);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent finishIntent = new Intent(getApplicationContext(), MainActivity.class);
                finishIntent.putExtra("content", editText.getText().toString());
                finishIntent.putExtra("id", id);
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
                                Intent finishIntent = new Intent(MainActivity2.this, MainActivity.class);
                                finishIntent.putExtra("id", id);
                                setResult(9002, finishIntent);
                                finish();
                            } break;
                            case R.id.etc_option_trasfer:{
                                Intent newIntent = new Intent(MainActivity2.this, SelectPopupActivity.class);
                                newIntent.putExtra("from", "MainActivity2");
                                selectPopupResultLauncher.launch(newIntent);
                            } break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent finishIntent = new Intent(getApplicationContext(), MainActivity.class);
        EditText editText = findViewById(R.id.editTextTextMultiLine);
        finishIntent.putExtra("content", editText.getText().toString());
        finishIntent.putExtra("id", id);
        setResult(9001, finishIntent);
        finish();
    }
}