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
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

public class SelectActivity extends AppCompatActivity {
    private DbOpenHelper mDbOpenHelper;
    private int dirId;
    private int mode;

    SelectAdapter selectAdapter;
    GridView gridView;

    ActivityResultLauncher<Intent> popUpResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() != ResultCode.APPROVE) return;

                    Intent intent = result.getData();
                    int newDirId = intent.getIntExtra("new_dir_id", 0);
                    executeCommandForAllCheckedItems(newDirId);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        startDB();
        Intent intent = getIntent();
        dirId = intent.getIntExtra("dir_id", -1);
        mode = intent.getIntExtra("mode", ResultCode.CANCEL);

        setAdapterOfGridView();
        setOnItemClickOfGridView();
        setOnClickOfCheckBoxAll();

        setOnClickOfCancelBtn();
        setOnClickOfApproveBtn();
    }

    private void startDB(){
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
    }

    private void setAdapterOfGridView(){
        selectAdapter = new SelectAdapter(getApplicationContext(), R.layout.select_item_card, mDbOpenHelper, dirId);
        gridView = findViewById(R.id.select_grid_view);
        gridView.setAdapter(selectAdapter);
    }

    private void setOnItemClickOfGridView(){
        CheckBox checkBoxAll = findViewById(R.id.select_all);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectAdapter.setChecked(position);
                checkBoxAll.setChecked(selectAdapter.isAllChecked());
                TextView tv = findViewById(R.id.select_count);
                tv.setText(""+selectAdapter.getCheckedCount()+"개 선택됨");
            }
        });
    }

    private void setOnClickOfCheckBoxAll(){
        CheckBox checkBoxAll = findViewById(R.id.select_all);
        checkBoxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAdapter.setAllChecked(!selectAdapter.isAllChecked());
                TextView tv = findViewById(R.id.select_count);
                tv.setText(""+selectAdapter.getCheckedCount()+"개 선택됨");
            }
        });
    }

    private void setOnClickOfCancelBtn(){
        findViewById(R.id.select_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });
    }

    private void setOnClickOfApproveBtn(){
        findViewById(R.id.select_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode==ResultCode.MOVE){
                    Intent newIntent = new Intent(getApplicationContext(), DirPopupActivity.class);
                    newIntent.putExtra("from", "SelectActivity");
                    popUpResultLauncher.launch(newIntent);
                }
                else executeCommandForAllCheckedItems();
            }
        });
    }

    private void executeCommandForAllCheckedItems(int newDirId){
        int count = selectAdapter.getCount();
        int previousPosition = 0;
        int position = 0;

        while(position<count){
            if(!selectAdapter.isChecked(previousPosition++)){
                position++; continue;
            }
            Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(dirId, position);
            int id = cursor.getInt(0);
            Memo memo = new Memo(id, mDbOpenHelper);
            memo.changeDir(newDirId);

            if(dirId== Dir.MENU_ALL) position++;
            else count--;
        }

        Intent finishIntent = new Intent(getApplicationContext(), MainActivity.class);
        finishIntent.putExtra("new_dir_id", newDirId);
        finish();
    }

    private void executeCommandForAllCheckedItems(){
        int count = selectAdapter.getCount();
        int previousPosition = 0;
        int position = 0;

        while(position<count){
            if(!selectAdapter.isChecked(previousPosition++)){
                position++; continue;
            }
            Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(dirId, position);
            int id = cursor.getInt(0);
            Memo memo = new Memo(id, mDbOpenHelper);

            if(mode==ResultCode.DELETE) memo.delete();
            else if(mode==ResultCode.RESTORE) memo.restore();
            else if(mode==ResultCode.REAL_DELETE) memo.realDelete();
            count--;
        }
        finish();
    }

}