package com.example.memo_q;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 9001) {
                        Intent intent = result.getData();
                        String content = intent.getStringExtra("content");
                        int position = intent.getIntExtra("position", mDbOpenHelper.lastId());
                        Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(position);
                        cursor.moveToFirst();
                        mDbOpenHelper.updateColumnToMemoTable(position, content, cursor.getString(2), cursor.getInt(3));

                        Intent reIntent = new Intent(MainActivity.this, MainActivity.class);
                        finish();
                        startActivity(reIntent);
                    }
                }
            });

    private DbOpenHelper mDbOpenHelper;
    private int dirId = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        GridAdapter gridAdapter = new GridAdapter(getApplicationContext(), R.layout.item_card, mDbOpenHelper);
        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(position);
                cursor.moveToFirst();
                intent.putExtra("datetime", cursor.getString(2));
                intent.putExtra("content", cursor.getString(1));
                intent.putExtra("position", position);
                activityResultLauncher.launch(intent);
            }
        });

        ImageButton addMemoBtn = (ImageButton) findViewById(R.id.ib_add_memo);
        addMemoBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                String datetime = DbOpenHelper.createdAt();
                long result = mDbOpenHelper.insertColumnToMemoTable("", datetime, dirId);
                if (result == -1)
                {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                }

                int lastId = mDbOpenHelper.lastId();

                intent.putExtra("datetime", datetime);
                intent.putExtra("content", "");
                intent.putExtra("position", lastId-1);
                activityResultLauncher.launch(intent);
            }
        });

    }

    public void onMenuSelected(View view){
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else{
            drawerLayout.openDrawer(GravityCompat.START);
            drawerLayout.bringChildToFront(navigationView);
            drawerLayout.requestLayout();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        TextView title = (TextView) findViewById(R.id.title);

        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                drawerLayout.openDrawer(GravityCompat.START);
                drawerLayout.bringChildToFront(navigationView);
                drawerLayout.requestLayout();
                return true;
            }
            case R.id.item_all:{
                title.setText("모든 메모");
            }
            case R.id.item_mine:{
                title.setText("내 메모");

            }
            case R.id.item_doc:{
                title.setText("문서");

            }
            case R.id.item_my_folder:{
                title.setText("내 폴더");

            }
            case R.id.item_trash:{
                title.setText("휴지통");

            }
            case R.id.item_new:{

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() { //뒤로가기 했을 때
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}