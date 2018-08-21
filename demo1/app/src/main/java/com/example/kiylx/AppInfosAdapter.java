package com.example.kiylx;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class AppInfosAdapter extends BaseAdapter {
     Context context;
     List<AppInfo> appInfos;
    public AppInfosAdapter(Context context, List<AppInfo> appInfos){
    super();
    this.context = context;
    this.appInfos =appInfos;
    }
    @Override
    public int getCount(){
        int count =0;
        if(appInfos!= null){
            return appInfos.size();
        }
        return count;

    }
    @Override
    public Object getItem(int position){
    return null;
    }
    @Override
    public long getItemId(int position){
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder holder;
        if(convertView == null ){
            convertView = inflater.inflate(R.layout.app_info_item, null);
            holder = new ViewHolder();
            holder.textView =convertView.findViewById(R.id.app_name);
            holder.imageView =convertView.findViewById(R.id.app_icon);
            holder.button = convertView.findViewById(R.id.button_1);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

            holder.imageView.setBackground(appInfos.get(position).getDrawable());
            holder.textView.setText(appInfos.get(position).getAppName());
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("appInfoActivity", "click");
                }
            });


        return convertView;


    }
    private class ViewHolder{
        ImageView imageView;
        TextView textView;
        Button button;
    }
}
