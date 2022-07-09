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
import androidx.cardview.widget.CardView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class GridAdapter extends BaseAdapter {
    Context context;
    int layout;
    List<Memo> memoes;
    LayoutInflater inf;

    public GridAdapter(Context context, int layout, List<Memo> memoes){
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

        CardView card = (CardView) convertView.findViewById(R.id.card_view);

        TextView tv4 = (TextView) convertView.findViewById(R.id.textView4);
        tv4.setText(memoes.get(position).title);

        TextView tv5 = (TextView) convertView.findViewById(R.id.textView5);
        tv5.setText(memoes.get(position).datetime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm")));

        TextView tv6 = (TextView) convertView.findViewById(R.id.textView6);
        tv6.setText(memoes.get(position).content);

        return convertView;
    }

}
