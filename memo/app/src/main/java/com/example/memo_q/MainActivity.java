package com.example.memo_q;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.GridView;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        memoes = new ArrayList<>();
        memoes.add(new Memo("title1", "content1"));
        memoes.add(new Memo("title2", "content2"));
        memoes.add(new Memo("title3", "content3"));

        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), R.layout.row, memoes);
        Gallery gallery = findViewById(R.id.gallery1);
        gallery.setAdapter(customAdapter);


        GridAdapter gridAdapter = new GridAdapter(getApplicationContext(), R.layout.item_card, memoes);
        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(gridAdapter);

        TextView tt = (TextView) findViewById(R.id.title);
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
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