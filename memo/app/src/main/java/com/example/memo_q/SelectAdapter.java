package com.example.memo_q;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SelectAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private LayoutInflater inf;
    private DbOpenHelper mDbOpenHelper;
    private int dirId;
    private boolean isAllChecked;
    private boolean[] isChecked;

    public SelectAdapter(Context context, int layout, DbOpenHelper mDbOpenHelper, int dirId){
        this.context = context;
        this.layout = layout;
        this.mDbOpenHelper = mDbOpenHelper;
        this.dirId = dirId;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        isChecked = new boolean[getCount()];
    }

    public void setAllChecked(boolean check){
        for(int i=0; i<isChecked.length; i++){
            isChecked[i] = check;
        }
        this.notifyDataSetChanged();
    }

    public void setChecked(int position){
        isChecked[position] = !isChecked[position];
        isAllChecked = isAllChecked();
        this.notifyDataSetChanged();
    }

    public boolean isAllChecked(){
        for(int i=0; i<isChecked.length; i++){
            if(!isChecked[i]) return false;
        }
        return true;
    }

    public int getCheckedCount(){
        int count = 0;
        for(int i=0; i<isChecked.length; i++){
            if(isChecked[i]) count++;
        }
        return count;
    }

    public boolean isChecked(int position){ return isChecked[position]; }

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

            TextView tv1 = convertView.findViewById(R.id.select_textView1);
            tv1.setText(content);

            TextView tv2 = convertView.findViewById(R.id.select_textView2);
            tv2.setText(datetime);
            cursor.close();

            CheckBox checkBox = convertView.findViewById(R.id.checkBox);
            checkBox.setChecked(isChecked[position]);
        }

        return convertView;
    }

}
