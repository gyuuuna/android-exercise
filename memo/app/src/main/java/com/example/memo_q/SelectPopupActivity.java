package com.example.memo_q;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SelectPopupActivity extends AppCompatActivity {

    private DbOpenHelper mDbOpenHelper;
    private int newDirId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_popup);

        Intent intent = getIntent();
        String from = intent.getStringExtra("from");

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();

        SelectPopupAdapter selectPopupAdapter = new SelectPopupAdapter(getApplicationContext(), R.layout.select_popup_item, mDbOpenHelper);
        ListView listView = findViewById(R.id.listView_select_menu);
        listView.setAdapter(selectPopupAdapter);
        setListViewHeightBasedOnChildren(listView);

        findViewById(R.id.select_popup_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.select_dir_my).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent finishIntent;
                if(from.equals("MainActivity2")){
                    finishIntent = new Intent(getApplicationContext(), MainActivity2.class);
                }
                else{
                    finishIntent = new Intent(getApplicationContext(), SelectActivity.class);
                }
                finishIntent.putExtra("new_dir_id", 0);
                setResult(9003, finishIntent);
                finish();
            }
        });

        findViewById(R.id.select_dir_docu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent finishIntent;
                if(from.equals("MainActivity2")){
                    finishIntent = new Intent(getApplicationContext(), MainActivity2.class);
                    finishIntent.putExtra("new_dir_id", 1);
                }
                else{
                    finishIntent = new Intent(getApplicationContext(), SelectActivity.class);
                    finishIntent.putExtra("new_dir_id", 1);
                }
                setResult(9003, finishIntent);
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent finishIntent;
                if(from.equals("MainActivity2")){
                    finishIntent = new Intent(getApplicationContext(), MainActivity2.class);
                    finishIntent.putExtra("new_dir_id", position+3);
                }
                else{
                    finishIntent = new Intent(getApplicationContext(), SelectActivity.class);
                    finishIntent.putExtra("new_dir_id", position+3);
                }
                setResult(9003, finishIntent);
                finish();
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
}