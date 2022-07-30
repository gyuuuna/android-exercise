package com.example.memo_q;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class DirPopupActivity extends AppCompatActivity {
    private DbOpenHelper mDbOpenHelper;
    private String from;

    private void startDB(){
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
    }

    private void setAdapterOfListView(){
        DirPopupAdapter dirPopupAdapter = new DirPopupAdapter(getApplicationContext(), R.layout.select_popup_item, mDbOpenHelper);
        ListView listView = findViewById(R.id.listView_select_menu);
        listView.setAdapter(dirPopupAdapter);
        setListViewHeightBasedOnChildren(listView);
    }

    private void setOnClickOfCancelBtn(){
        findViewById(R.id.select_popup_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });
    }

    private void setOnClickOfApproveBtn(){
        findViewById(R.id.select_dir_my).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent finishIntent;
                if(from.equals("MainActivity2"))
                    finishIntent = new Intent(getApplicationContext(), MainActivity2.class);
                else
                    finishIntent = new Intent(getApplicationContext(), SelectActivity.class);
                finishIntent.putExtra("new_dir_id", ResultCode.FAIL);
                setResult(ResultCode.APPROVE, finishIntent);
                finish();
            }
        });
    }

    private void setOnClickOfSelectDirBtn(){
        findViewById(R.id.select_dir_docu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if(from.equals("MainActivity2"))
                    intent = new Intent(getApplicationContext(), MainActivity2.class);
                else
                    intent = new Intent(getApplicationContext(), SelectActivity.class);
                intent.putExtra("new_dir_id", ResultCode.FAIL);
                setResult(ResultCode.APPROVE, intent);
                finish();
            }
        });
    }

    private void setOnItemClickOfListView(){
        ListView listView = findViewById(R.id.listView_select_menu);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent;
                if(from.equals("MainActivity2"))
                    intent = new Intent(getApplicationContext(), MainActivity2.class);
                else
                    intent = new Intent(getApplicationContext(), SelectActivity.class);
                intent.putExtra("new_dir_id", position+Dir.DEFAULT_DIRS_COUNT);
                setResult(ResultCode.APPROVE, intent);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_popup);

        Intent intent = getIntent();
        from = intent.getStringExtra("from");

        startDB();
        setAdapterOfListView();
        setOnItemClickOfListView();
        setOnClickOfCancelBtn();
        setOnClickOfApproveBtn();
        setOnClickOfSelectDirBtn();
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
}