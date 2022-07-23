package com.example.memo_q;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

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
                        int id = intent.getIntExtra("id", mDbOpenHelper.lastMemoId());
                        Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(id);
                        cursor.moveToFirst();
                        // mDbOpenHelper.updateColumnToMemoTable(cursor.getInt(0), content, cursor.getString(2), cursor.getInt(3));
                        mDbOpenHelper.updateColumnToMemoTable(cursor.getInt(0), content);

                        int previousDirId = intent.getIntExtra("dir_id", -1);
                        Intent reIntent = new Intent(MainActivity.this, MainActivity.class);
                        reIntent.putExtra("dir_id", previousDirId);
                        finish();
                        startActivity(reIntent);
                    }
                    else if(result.getResultCode() == 9002) {
                        Intent intent = result.getData();
                        int id = intent.getIntExtra("id", mDbOpenHelper.lastMemoId());
                        mDbOpenHelper.updateIsDeletedToTrue(id);

                        int previousDirId = intent.getIntExtra("dir_id", -1);
                        Intent reIntent = new Intent(MainActivity.this, MainActivity.class);
                        reIntent.putExtra("dir_id", previousDirId);
                        finish();
                        startActivity(reIntent);
                    }
                }
            });

    ActivityResultLauncher<Intent> popUpResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 9001) {
                        Intent intent = result.getData();
                        String name = intent.getStringExtra("name");
                        mDbOpenHelper.insertColumnToDirTable(name, DbOpenHelper.createdAt());

                        int previousDirId = intent.getIntExtra("dir_id", -1);
                        Intent reIntent = new Intent(MainActivity.this, MainActivity.class);
                        reIntent.putExtra("dir_id", previousDirId);
                        reIntent.putExtra("re", true);
                        finish();
                        startActivity(reIntent);
                    }
                    else if(result.getResultCode() == 9000) {
                        Intent intent = result.getData();
                        int previousDirId = intent.getIntExtra("dir_id", -1);
                        Intent reIntent = new Intent(MainActivity.this, MainActivity.class);
                        reIntent.putExtra("dir_id", previousDirId);
                        reIntent.putExtra("re", true);
                        finish();
                        startActivity(reIntent);
                    }
                }
            });

    private DbOpenHelper mDbOpenHelper;
    private int dirId;

    @SuppressLint("ResourceAsColor")
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

        if(getIntent().getBooleanExtra("re", false)){
            drawerLayout.openDrawer(GravityCompat.START);
            drawerLayout.bringChildToFront(navigationView);
            drawerLayout.requestLayout();
        }
        dirId = getIntent().getIntExtra("dir_id", -1);
        setDirId(dirId);

        updateGridview();

        ImageButton addMemoBtn = (ImageButton) findViewById(R.id.ib_add_memo);
        addMemoBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                String datetime = DbOpenHelper.createdAt();
                long result = mDbOpenHelper.insertColumnToMemoTable("", datetime, dirId);
                if (result == -1) Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();

                intent.putExtra("datetime", datetime);
                intent.putExtra("content", "");
                intent.putExtra("id", mDbOpenHelper.lastMemoId());
                intent.putExtra("dir_id", dirId);
                activityResultLauncher.launch(intent);
            }
        });

        MenuAdapter menuAdapter = new MenuAdapter(getApplicationContext(), R.layout.item, mDbOpenHelper);
        ListView listView = (ListView) findViewById(R.id.listView_menu);
        listView.setAdapter(menuAdapter);
        setListViewHeightBasedOnChildren(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                setDirId(position+3);
                drawerLayout.closeDrawer(GravityCompat.START);
                updateGridview();
            }
        });

        findViewById(R.id.menu_all).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                setDirId(-1);
                drawerLayout.closeDrawer(GravityCompat.START);
                updateGridview();
            }
        });
        findViewById(R.id.menu_my).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                setDirId(0);
                drawerLayout.closeDrawer(GravityCompat.START);
                updateGridview();
            }
        });
        findViewById(R.id.menu_docu).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                setDirId(1);
                drawerLayout.closeDrawer(GravityCompat.START);
                updateGridview();
            }
        });
        findViewById(R.id.menu_trash).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                setDirId(2);
                drawerLayout.closeDrawer(GravityCompat.START);
                updateGridview();
            }
        });
        findViewById(R.id.menu_add).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PopUpActivity.class);
                String datetime = DbOpenHelper.createdAt();
                intent.putExtra("name", "");
                intent.putExtra("dir_id", dirId);
                popUpResultLauncher.launch(intent);
            }
        });

    }

    private static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight=0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            view = listAdapter.getView(i, view, listView);

            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewGroup.LayoutParams.MATCH_PARENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()) * (listAdapter.getCount()));

        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    private void setDirId(int dirId){
        this.dirId = dirId;
        String title;
        TextView barTitle = (TextView) findViewById(R.id.title);
        ImageButton addMemoButton = findViewById(R.id.ib_add_memo);
        switch(dirId){
            case -1:
                barTitle.setText("모든 메모");
                addMemoButton.setEnabled(true);
                addMemoButton.setVisibility(View.VISIBLE);
                break;
            case 0:
                barTitle.setText("내 메모");
                addMemoButton.setEnabled(true);
                addMemoButton.setVisibility(View.VISIBLE);
                break;
            case 1:
                barTitle.setText("문서");
                addMemoButton.setEnabled(true);
                addMemoButton.setVisibility(View.VISIBLE);
                break;
            case 2:
                barTitle.setText("휴지통");
                addMemoButton.setEnabled(false);
                addMemoButton.setVisibility(View.INVISIBLE);
                break;
            default:
                Cursor cursor = mDbOpenHelper.selectColumnsFromDirTable(dirId-3);
                String name = cursor.getString(1);
                barTitle.setText(name);
                addMemoButton.setEnabled(true);
                addMemoButton.setVisibility(View.VISIBLE);
        }
    }

    private void updateGridview(){
        GridAdapter gridAdapter = new GridAdapter(getApplicationContext(), R.layout.item_card, mDbOpenHelper, dirId);
        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(dirId, position);
                intent.putExtra("datetime", cursor.getString(2));
                intent.putExtra("content", cursor.getString(1));
                intent.putExtra("id", cursor.getInt(cursor.getColumnIndex("_id")));
                intent.putExtra("dir_id", dirId);
                activityResultLauncher.launch(intent);
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @SuppressLint("Range")
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(dirId, position);
                int memoId = cursor.getInt(cursor.getColumnIndex("_id"));
                String content = cursor.getString(1);

                PopupMenu popup = new PopupMenu(getApplicationContext(), view);
                if(dirId==2){
                    getMenuInflater().inflate(R.menu.trash_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch(menuItem.getItemId()){
                                case R.id.trash_delete:
                                    mDbOpenHelper.deleteColumnOfMemoTable(memoId);
                                    updateGridview();
                                    break;
                                case R.id.trash_restore:
                                    mDbOpenHelper.updateIsDeletedToFalse(memoId);
                                    updateGridview();
                                    break;
                            } return true;
                        }
                    });
                }
                else{
                    getMenuInflater().inflate(R.menu.memo_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch(menuItem.getItemId()){
                                case R.id.memo_delete:
                                    mDbOpenHelper.updateIsDeletedToTrue(memoId);
                                    updateGridview();
                            } return true;
                        }
                    });
                }
                popup.show(); return true;
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
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}