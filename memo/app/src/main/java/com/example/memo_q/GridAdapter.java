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

public class GridAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private LayoutInflater inf;
    private DbOpenHelper mDbOpenHelper;
    private int dirId;

    public GridAdapter(Context context, int layout, DbOpenHelper mDbOpenHelper, int dirId){
        this.context = context;
        this.layout = layout;
        this.mDbOpenHelper = mDbOpenHelper;
        this.dirId = dirId;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setDirId(int dirId){ this.dirId = dirId; }

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

            TextView tv5 = (TextView) convertView.findViewById(R.id.textView5);
            tv5.setText(datetime);

            TextView tv6 = (TextView) convertView.findViewById(R.id.textView6);
            tv6.setText(content);
            cursor.close();
        }

        return convertView;
    }

}
