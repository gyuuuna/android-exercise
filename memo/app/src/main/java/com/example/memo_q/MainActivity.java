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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DbOpenHelper mDbOpenHelper;
    private GridAdapter gridAdapter;
    private MenuAdapter menuAdapter;
    private int dirId = Dir.MENU_ALL;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent intent = result.getData();
                    int id = intent.getIntExtra("id", mDbOpenHelper.lastMemoId());
                    Memo memo = new Memo(id, mDbOpenHelper);
                    String content = intent.getStringExtra("content");
                    int newDirId = intent.getIntExtra("new_dir_id", dirId);

                    if (result.getResultCode() == ResultCode.UPDATE){
                        memo.update(content);
                        gridAdapter.notifyDataSetChanged();
                    }
                    else if(result.getResultCode() == ResultCode.DELETE){
                        memo.delete();
                        gridAdapter.notifyDataSetChanged();
                    }
                    else if(result.getResultCode() == ResultCode.MOVE){
                        memo.changeDir(newDirId);
                        String dirName = getDirName(newDirId);
                        gridAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), dirName+" 폴더로 이동되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    ActivityResultLauncher<Intent> popUpResultLauncher = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent intent = result.getData();
                    String name = intent.getStringExtra("name");

                    if(result.getResultCode() == ResultCode.CANCEL){
                    } else if (result.getResultCode() == ResultCode.APPROVE){
                        mDbOpenHelper.insertColumnToDirTable(name, DbOpenHelper.createdAt());
                        menuAdapter.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(findViewById(R.id.listView_menu));
                    }
                }
            });

    ActivityResultLauncher<Intent> selectResultLauncher = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onActivityResult(ActivityResult result) {
                    gridAdapter.notifyDataSetChanged();
                    Intent intent = result.getData();
                    int newDirId = intent.getIntExtra("new_dir_id", ResultCode.FAIL);

                    if(newDirId == ResultCode.FAIL){
                    } else{
                        String dirName = getDirName(newDirId);
                        Toast.makeText(getApplicationContext(), dirName+" 폴더로 이동되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startDB();

        gridAdapter = new GridAdapter(getApplicationContext(), R.layout.item_card, mDbOpenHelper, dirId);
        menuAdapter = new MenuAdapter(getApplicationContext(), R.layout.item, mDbOpenHelper);

        setAdapterOfMemoGridView();
        setOnItemClickOfMemoGridView();
        setOnItemLongClickOfMemoGridView();

        setAdapterOfMenuListView();
        setOnItemClickOfMenuListView();
        setDirId(Dir.MENU_ALL);

        setOnClickOfAddMemoBtn();
        setOnClickOfDrawerBtn(R.id.menu_all, Dir.MENU_ALL);
        setOnClickOfDrawerBtn(R.id.menu_my, Dir.MENU_MY);
        setOnClickOfDrawerBtn(R.id.menu_docu, Dir.MENU_DOCU);
        setOnClickOfDrawerBtn(R.id.menu_trash, Dir.MENU_TRASH);
        setOnClickOfAddMenuBtn();
    }

    private void startDB(){
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
    }

    private void setOnMenuItemClickOfPopupInTrashDir(PopupMenu popup, int memoId){
        getMenuInflater().inflate(R.menu.trash_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.trash_delete:
                        mDbOpenHelper.deleteColumnOfMemoTable(memoId);
                        gridAdapter.notifyDataSetChanged();
                        break;
                    case R.id.trash_restore:
                        mDbOpenHelper.updateIsDeletedToFalse(memoId);
                        gridAdapter.notifyDataSetChanged();
                        break;
                } return true;
            }
        });
    }
    private void setOnMenuItemClickOfPopupNotInTrashDir(PopupMenu popup, int memoId){
        getMenuInflater().inflate(R.menu.memo_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.memo_delete:
                        mDbOpenHelper.updateIsDeletedToTrue(memoId);
                        gridAdapter.notifyDataSetChanged();
                } return true;
            }
        });
    }

    private void setAdapterOfMemoGridView(){
        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(gridAdapter);
    }
    private void setOnItemClickOfMemoGridView(){
        GridView gridView = findViewById(R.id.grid_view);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(dirId==2) return;
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(dirId, position);
                intent.putExtra("datetime", cursor.getString(cursor.getColumnIndex("created_at")));
                intent.putExtra("content", cursor.getString(cursor.getColumnIndex("content")));
                intent.putExtra("id", cursor.getInt(cursor.getColumnIndex("_id")));
                intent.putExtra("dir_id", dirId);
                activityResultLauncher.launch(intent);
            }
        });
    }
    private void setOnItemLongClickOfMemoGridView(){
        GridView gridView = findViewById(R.id.grid_view);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @SuppressLint("Range")
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(dirId, position);
                int memoId = cursor.getInt(cursor.getColumnIndex("_id"));
                PopupMenu popup = new PopupMenu(getApplicationContext(), view);
                if(dirId== Dir.MENU_TRASH)
                    setOnMenuItemClickOfPopupInTrashDir(popup, memoId);
                else
                    setOnMenuItemClickOfPopupNotInTrashDir(popup, memoId);
                popup.show();
                return true;
            }
        });
    }

    private void setOnMenuItemClickOfPopupNotInTrashDir(PopupMenu popup){
        getMenuInflater().inflate(R.menu.main_option_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.main_option_trasfer:
                        Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                        intent.putExtra("mode", ResultCode.MOVE);
                        intent.putExtra("dir_id", dirId);
                        selectResultLauncher.launch(intent);
                } return true;
            }
        });
    }
    private void setOnMenuItemClickOfPopupInTrashDir(PopupMenu popup){
        getMenuInflater().inflate(R.menu.trash_option_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.trash_option_restore:
                        Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                        intent.putExtra("mode", ResultCode.RESTORE);
                        intent.putExtra("dir_id", dirId);
                        selectResultLauncher.launch(intent);
                } return true;
            }
        });
    }

    public void onMenuSelected(View view){
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else{
            drawerLayout.openDrawer(GravityCompat.START);
            drawerLayout.bringChildToFront(navigationView);
            drawerLayout.requestLayout();
        }
    }
    public void onEtcSelected(View view){
        PopupMenu popup = new PopupMenu(getApplicationContext(), view);
        if(dirId== Dir.MENU_TRASH)
            setOnMenuItemClickOfPopupInTrashDir(popup);
        else
            setOnMenuItemClickOfPopupNotInTrashDir(popup);
        popup.show();
    }
    public void onDeleteSelected(View view){
        findViewById(R.id.ib_toolbar_trash).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                if(dirId== Dir.MENU_TRASH) intent.putExtra("mode", ResultCode.REAL_DELETE);
                else intent.putExtra("mode", ResultCode.DELETE);
                intent.putExtra("dir_id", dirId);
                selectResultLauncher.launch(intent);
            }
        });
    }

    private void setAdapterOfMenuListView(){
        ListView listView = findViewById(R.id.listView_menu);
        listView.setAdapter(menuAdapter);
        setListViewHeightBasedOnChildren(listView);
    }
    private void setOnItemClickOfMenuListView(){
        ListView listView = findViewById(R.id.listView_menu);
        DrawerLayout drawerLayout = findViewById(R.id.drawer);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                setDirId(position+ Dir.DEFAULT_DIRS_COUNT);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }
    private static void setListViewHeightBasedOnChildren(ListView listView) {
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

    private void setOnClickOfAddMemoBtn(){
        ImageButton addMemoBtn = (ImageButton) findViewById(R.id.ib_add_memo);
        addMemoBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                String datetime = DbOpenHelper.createdAt();
                long result = mDbOpenHelper.insertColumnToMemoTable("", datetime, dirId);
                if (result<0) Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();

                intent.putExtra("datetime", datetime);
                intent.putExtra("content", "");
                intent.putExtra("id", mDbOpenHelper.lastMemoId());
                intent.putExtra("dir_id", dirId);
                activityResultLauncher.launch(intent);
            }
        });
    }
    private void setOnClickOfDrawerBtn(int id, int dirId){
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                setDirId(dirId);
                drawerLayout.closeDrawer(GravityCompat.START);
                gridAdapter.setDirId(dirId);
                gridAdapter.notifyDataSetChanged();
            }
        });
    }
    private void setOnClickOfAddMenuBtn(){
        findViewById(R.id.menu_add).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DirNamePopupActivity.class);
                intent.putExtra("name", "");
                intent.putExtra("dir_id", dirId);
                popUpResultLauncher.launch(intent);
            }
        });
    }

    @SuppressLint("Range")
    String getDirName(int dirId){
        if(dirId== Dir.MENU_ALL || dirId== Dir.MENU_MY || dirId== Dir.MENU_DOCU || dirId== Dir.MENU_TRASH)
            return Dir.getDefaultDirName(dirId);
        else{
            Cursor cursor = mDbOpenHelper.selectColumnsFromDirTable(dirId- Dir.DEFAULT_DIRS_COUNT);
            String dirName = cursor.getString(cursor.getColumnIndex("name"));
            return dirName;
        }
    }
    private void changeBarTitle(int dirId){
        TextView barTitle = findViewById(R.id.title);
        barTitle.setText(getDirName(dirId));
    }
    private void setVisibilityOfAddMemoBtn(int dirId){
        ImageButton addMemoButton = findViewById(R.id.ib_add_memo);
        if(dirId== Dir.MENU_TRASH){
            addMemoButton.setEnabled(false);
            addMemoButton.setVisibility(View.INVISIBLE);
        }
        else{
            addMemoButton.setEnabled(true);
            addMemoButton.setVisibility(View.VISIBLE);
        }
    }
    private void setDirId(int dirId){
        this.dirId = dirId;
        gridAdapter.setDirId(dirId);
        gridAdapter.notifyDataSetChanged();
        changeBarTitle(dirId);
        setVisibilityOfAddMemoBtn(dirId);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}