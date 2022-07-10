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

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    List<Memo> memoes;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 9001) {
                        Intent intent = result.getData();
                        String content = intent.getStringExtra("content");
                        int position = intent.getIntExtra("position", memoes.size() - 1);
                        Memo memo = memoes.get(position);
                        memo.content = content;
                    }
                }
            });

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        memoes = new ArrayList<>();

        GridAdapter gridAdapter = new GridAdapter(getApplicationContext(), R.layout.item_card, memoes);
        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("datetime", memoes.get(position).datetime);
                intent.putExtra("content", memoes.get(position).content);
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
                Memo memo = new Memo("");
                memoes.add(memo);
                intent.putExtra("datetime", memo.datetime);
                intent.putExtra("content", memo.content);
                intent.putExtra("position", memoes.size()-1);
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
        TextView textView = (TextView) findViewById(R.id.title);

        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                drawerLayout.openDrawer(GravityCompat.START);
                drawerLayout.bringChildToFront(navigationView);
                drawerLayout.requestLayout();
                return true;
            }
            case R.id.item_all:{

            }
            case R.id.item_mine:{

            }
            case R.id.item_doc:{

            }
            case R.id.item_my_folder:{

            }
            case R.id.item_trash:{

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