package com.example.memo_q;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

public class SelectAdapter extends BaseAdapter {
    Context context;
    int layout;
    LayoutInflater inf;
    DbOpenHelper mDbOpenHelper;
    int dirId;

    public SelectAdapter(Context context, int layout, DbOpenHelper mDbOpenHelper, int dirId){
        this.context = context;
        this.layout = layout;
        this.mDbOpenHelper = mDbOpenHelper;
        this.dirId = dirId;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return mDbOpenHelper.getCountOfMemo(dirId); }

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

        Cursor cursor = mDbOpenHelper.selectColumnsFromMemoTable(dirId, position);
        if(cursor != null){
            String content = cursor.getString(1);
            String datetime = cursor.getString(2);

            TextView tv1 = (TextView) convertView.findViewById(R.id.select_textView1);
            tv1.setText(content);

            TextView tv2 = (TextView) convertView.findViewById(R.id.select_textView2);
            tv2.setText(datetime);
            cursor.close();
        }

        return convertView;
    }

}
