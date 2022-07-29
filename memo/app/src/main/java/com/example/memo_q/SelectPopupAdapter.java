package com.example.memo_q;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class SelectPopupAdapter extends BaseAdapter {
    Context context;
    int layout;
    LayoutInflater inf;
    DbOpenHelper mDbOpenHelper;

    public SelectPopupAdapter(Context context, int layout, DbOpenHelper mDbOpenHelper){
        this.context = context;
        this.layout = layout;
        this.mDbOpenHelper = mDbOpenHelper;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return mDbOpenHelper.getCountOfDir(); }

    @Override
    public Object getItem(int position){ return null; }

    @Override
    public long getItemId(int position){ return 0; }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inf.inflate(layout, null);
        }

        Cursor cursor = mDbOpenHelper.selectColumnsFromDirTable(position);
        if (cursor != null) {
            String content = cursor.getString(1);
            TextView tv = (TextView) convertView.findViewById(R.id.textView_select_popup);
            tv.setText(content);
            cursor.close();
        }

        return convertView;
    }
}
