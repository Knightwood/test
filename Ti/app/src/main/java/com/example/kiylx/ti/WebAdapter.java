package com.example.kiylx.ti;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Context;


public class WebAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private WebList list;

    public WebAdapter(Context context,WebList list){
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater =LayoutInflater.from(context);
        ViewHolder holder;
        if(convertView==null){
            convertView=layoutInflater.inflate(R.layout.item,null);
            holder= new ViewHolder();
            holder.imageButton= convertView.findViewById(R.id.imageButton);
            holder.textView=convertView.findViewById(R.id.webtext);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(list.Top.url);
        holder.imageButton.setOnClickListener(this);
        holder.imageButton.setTag(position);
        return convertView;
    }

    @Override
    public void onClick(View v) {

    }

    public class ViewHolder{
        TextView textView;
        ImageButton imageButton;
    }
}
