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
    private int dirId=-1;

    private class Memo {
        private int id;
        Memo(int id){ this.id = id; }
        void update(String content){
            Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(id);
            cursor.moveToFirst();
            mDbOpenHelper.updateColumnToMemoTable(cursor.getInt(0), content);
        }
        void deleteToTrashCan(){ mDbOpenHelper.updateIsDeletedToTrue(id); }
        void changeDir(int newDirId){ mDbOpenHelper.updateColumnToMemoTable(id, newDirId); }
    }

    String getDirName(int dirId){
        switch(dirId){
            case -1: return "모든 메모";
            case 0: return "내 메모";
            case 1: return "문서";
            default:
                Cursor dir = mDbOpenHelper.selectColumnsFromDirTable(dirId-3);
                String dirName = dir.getString(1);
                return dirName;
        }
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent intent = result.getData();
                    int id = intent.getIntExtra("id", mDbOpenHelper.lastMemoId()), restartDirId = dirId;
                    Memo memo = new Memo(id);
                    String content = intent.getStringExtra("content");
                    int newDirId = intent.getIntExtra("new_dir_id", dirId);

                    if (result.getResultCode() == 9001){
                        // 메모 수정
                        memo.update(content);
                        gridAdapter.notifyDataSetChanged();
                    }
                    else if(result.getResultCode() == 9002){
                        // 메모 삭제
                        memo.deleteToTrashCan();
                        gridAdapter.notifyDataSetChanged();
                    }
                    else if(result.getResultCode() == 9003){
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
                    if (result.getResultCode() == 9001){
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
                    int newDirId = intent.getIntExtra("new_dir_id", -100);
                    if(newDirId!=-100){
                        String dirName = getDirName(newDirId);
                        Toast.makeText(getApplicationContext(), dirName+" 폴더로 이동되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void startDB(){
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
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
                if (result == -1) Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();

                intent.putExtra("datetime", datetime);
                intent.putExtra("content", "");
                intent.putExtra("id", mDbOpenHelper.lastMemoId());
                intent.putExtra("dir_id", dirId);
                activityResultLauncher.launch(intent);
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
                setDirId(position+3);
                drawerLayout.closeDrawer(GravityCompat.START);
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
                Intent intent = new Intent(MainActivity.this, PopUpActivity.class);
                intent.putExtra("name", "");
                intent.putExtra("dir_id", dirId);
                popUpResultLauncher.launch(intent);
            }
        });
    }


    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startDB();

        gridAdapter = new GridAdapter(getApplicationContext(), R.layout.item_card, mDbOpenHelper, dirId);
        setAdapterOfMemoGridView();
        setOnItemClickOfMemoGridView();
        setOnItemLongClickOfMemoGridView();
        menuAdapter = new MenuAdapter(getApplicationContext(), R.layout.item, mDbOpenHelper);
        setAdapterOfMenuListView();
        setOnItemClickOfMenuListView();

        setDirId(-1);

        setOnClickOfAddMemoBtn();
        setOnClickOfDrawerBtn(R.id.menu_all, -1);
        setOnClickOfDrawerBtn(R.id.menu_my, 0);
        setOnClickOfDrawerBtn(R.id.menu_docu, 1);
        setOnClickOfDrawerBtn(R.id.menu_trash, 2);
        setOnClickOfAddMenuBtn();
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

    private void setDirId(int dirId){
        this.dirId = dirId;
        gridAdapter.setDirId(dirId);
        gridAdapter.notifyDataSetChanged();

        TextView barTitle = findViewById(R.id.title);
        ImageButton addMemoButton = findViewById(R.id.ib_add_memo);
        switch(dirId){
            case -1:
            case 0:
            case 1:
                barTitle.setText(getDirName(dirId));
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
                intent.putExtra("datetime", cursor.getString(2));
                intent.putExtra("content", cursor.getString(1));
                intent.putExtra("id", cursor.getInt(cursor.getColumnIndex("_id")));
                intent.putExtra("dir_id", dirId);
                activityResultLauncher.launch(intent);
            }
        });
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

    private void setOnItemLongClickOfMemoGridView(){
        GridView gridView = findViewById(R.id.grid_view);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @SuppressLint("Range")
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(dirId, position);
                int memoId = cursor.getInt(cursor.getColumnIndex("_id"));
                PopupMenu popup = new PopupMenu(getApplicationContext(), view);
                if(dirId==2)
                    setOnMenuItemClickOfPopupInTrashDir(popup, memoId);
                else
                    setOnMenuItemClickOfPopupNotInTrashDir(popup, memoId);
                popup.show();
                return true;
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

    private void setOnMenuItemClickOfPopupNotInTrashDir(PopupMenu popup){
        getMenuInflater().inflate(R.menu.main_option_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.main_option_trasfer:
                        Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                        intent.putExtra("mode", "transfer");
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
                        intent.putExtra("mode", "restore");
                        intent.putExtra("dir_id", dirId);
                        selectResultLauncher.launch(intent);
                } return true;
            }
        });
    }

    public void onEtcSelected(View view){
        PopupMenu popup = new PopupMenu(getApplicationContext(), view);
        if(dirId==2)
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
                intent.putExtra("mode", "delete");
                intent.putExtra("dir_id", dirId);
                selectResultLauncher.launch(intent);
            }
        });
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