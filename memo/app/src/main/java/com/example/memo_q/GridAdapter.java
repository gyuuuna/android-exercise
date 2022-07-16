package com.example.memo_q;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.service.autofill.ImageTransformation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class GridAdapter extends BaseAdapter {
    Context context;
    int layout;
    LayoutInflater inf;
    DbOpenHelper mDbOpenHelper;

    public GridAdapter(Context context, int layout, DbOpenHelper mDbOpenHelper){
        this.context = context;
        this.layout = layout;
        this.mDbOpenHelper = mDbOpenHelper;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return mDbOpenHelper.getCountOfRows(); }

    @Override
    public Object getItem(int position){ return null; }

    @Override
    public long getItemId(int position){ return 0; }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            convertView = inf.inflate(layout, null);
        }

        Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(position);
        if(cursor != null && cursor.moveToFirst()){
            String content = cursor.getString(1);
            String datetime = cursor.getString(2);

            TextView tv5 = (TextView) convertView.findViewById(R.id.textView5);
            tv5.setText(datetime);

            TextView tv6 = (TextView) convertView.findViewById(R.id.textView6);
            tv6.setText(content);
            cursor.close();
        }

        return convertView;
    }

}
