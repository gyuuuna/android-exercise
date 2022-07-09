package com.example.memo_q;
import android.content.Context;
import android.os.Build;
import android.service.autofill.ImageTransformation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    Context context;
    int layout;
    List<Memo> memoes;
    LayoutInflater inf;

    public CustomAdapter(Context context, int layout, List<Memo> memoes){
        this.context = context;
        this.layout = layout;
        this.memoes = memoes;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return memoes.size(); }

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

        TextView tv1 = (TextView) convertView.findViewById(R.id.textView1);
        tv1.setText(memoes.get(position).title);

        TextView tv2 = (TextView) convertView.findViewById(R.id.textView2);
        tv2.setText(memoes.get(position).datetime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm")));

        TextView tv3 = (TextView) convertView.findViewById(R.id.textView3);
        tv3.setText(memoes.get(position).content);

        return convertView;
    }

}
