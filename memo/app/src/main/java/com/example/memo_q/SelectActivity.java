package com.example.memo_q;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectActivity extends AppCompatActivity {
    private DbOpenHelper mDbOpenHelper;
    private int dirId;
    private String mode;

    SelectAdapter selectAdapter;
    GridView gridView;

    ActivityResultLauncher<Intent> popUpResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 9003) {
                        Intent intent = result.getData();
                        int newDirId = intent.getIntExtra("new_dir_id", 0);

                        int count = selectAdapter.getCount();
                        int previousPosition = 0;
                        int position = 0;

                        while(position<count){
                            if(!selectAdapter.isChecked(previousPosition++)){
                                position++; continue;
                            }
                            Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(dirId, position);
                            int id = cursor.getInt(0);
                            mDbOpenHelper.updateColumnToMemoTable(id, newDirId);
                            if(dirId==-1) position++;
                            else count--;
                        }

                        Intent finishIntent = new Intent(getApplicationContext(), MainActivity.class);
                        finishIntent.putExtra("new_dir_id", newDirId);
                        setResult(9001, finishIntent);
                        finish();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();

        Intent intent = getIntent();
        dirId = intent.getIntExtra("dir_id", -1);
        mode = intent.getStringExtra("mode");

        selectAdapter = new SelectAdapter(getApplicationContext(), R.layout.select_item_card, mDbOpenHelper, dirId);
        gridView = findViewById(R.id.select_grid_view);
        gridView.setAdapter(selectAdapter);

        CheckBox all_checkBox = findViewById(R.id.select_all);
        all_checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAdapter.setAllChecked(!selectAdapter.isAllChecked());
                TextView tv = findViewById(R.id.select_count);
                tv.setText(""+selectAdapter.getCheckedCount()+"개 선택됨");
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectAdapter.setChecked(position);
                all_checkBox.setChecked(selectAdapter.isAllChecked());
                TextView tv = findViewById(R.id.select_count);
                tv.setText(""+selectAdapter.getCheckedCount()+"개 선택됨");
            }
        });

        findViewById(R.id.select_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent finishIntent = new Intent(getApplicationContext(), MainActivity.class);
                setResult(9000, finishIntent);
                finish();
            }
        });

        findViewById(R.id.select_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 삭제
                if(mode.equals("delete")){
                    int count = selectAdapter.getCount();
                    int previousPosition = 0;
                    int position = 0;

                    while(position<count){
                        if(!selectAdapter.isChecked(previousPosition++)){
                            position++; continue;
                        }
                        Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(dirId, position);
                        int id = cursor.getInt(0);
                        if(dirId==2)
                            mDbOpenHelper.deleteColumnOfMemoTable(id);
                        else
                            mDbOpenHelper.updateIsDeletedToTrue(id);
                        count--;
                    }
                    Intent finishIntent = new Intent(getApplicationContext(), MainActivity.class);
                    setResult(9001, finishIntent);
                    finish();
                }
                // 폴더 이동
                else if(mode.equals("transfer")){
                    Intent newIntent = new Intent(getApplicationContext(), SelectPopupActivity.class);
                    newIntent.putExtra("from", "SelectActivity");
                    popUpResultLauncher.launch(newIntent);
                }
                else if(mode.equals("restore")){
                    int count = selectAdapter.getCount();
                    int previousPosition = 0;
                    int position = 0;

                    while(position<count){
                        if(!selectAdapter.isChecked(previousPosition++)){
                            position++; continue;
                        }
                        Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(dirId, position);
                        int id = cursor.getInt(0);
                        mDbOpenHelper.updateIsDeletedToFalse(id);
                        count--;
                    }
                    Intent finishIntent = new Intent(getApplicationContext(), MainActivity.class);
                    setResult(9001, finishIntent);
                    finish();
                }
            }
        });
    }
}