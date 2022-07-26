package com.example.memo_q;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.GridView;

public class SelectActivity extends AppCompatActivity {
    private DbOpenHelper mDbOpenHelper;
    private int dirId;
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();

        Intent intent = getIntent();
        dirId = intent.getIntExtra("dir_id", -1);
        mode = intent.getStringExtra("mode");

        SelectAdapter selectAdapter = new SelectAdapter(getApplicationContext(), R.layout.select_item_card, mDbOpenHelper, dirId);
        GridView gridView = findViewById(R.id.select_grid_view);
        gridView.setAdapter(selectAdapter);

        findViewById(R.id.select_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent finishIntent = new Intent(getApplicationContext(), MainActivity.class);
                finishIntent.putExtra("dir_id", dirId);
                setResult(9000, finishIntent);
                finish();
            }
        });

        findViewById(R.id.select_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode.equals("delete")){
                    SparseBooleanArray checkedItems = gridView.getCheckedItemPositions();
                    int count = selectAdapter.getCount();
                    for(int i=0; i<count; i++){
                        int position = i+1;
                        Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(dirId, position);
                        int id = cursor.getInt(0);
                        mDbOpenHelper.deleteColumnOfMemoTable(id);
                    }
                    Intent finishIntent = new Intent(getApplicationContext(), MainActivity.class);
                    finishIntent.putExtra("dir_id", dirId);
                    setResult(9001, finishIntent);
                    finish();
                }
                else if(mode.equals("transfer")){
                    SparseBooleanArray checkedItems = gridView.getCheckedItemPositions();
                    int count = selectAdapter.getCount();
                    for(int i=0; i<count; i++){
                        int position = i+1;
                        Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(dirId, position);
                        int id = cursor.getInt(0);
                        mDbOpenHelper.updateColumnToMemoTable(id, dirId); // 이걸 바꾸고 싶은 dir id로 바꾸도록 하기!!!!!!!!!
                    }
                    Intent finishIntent = new Intent(getApplicationContext(), MainActivity.class);
                    finishIntent.putExtra("dir_id", dirId);
                    setResult(9001, finishIntent);
                    finish();
                }
            }
        });
    }
}