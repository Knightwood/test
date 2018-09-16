package com.example.kiylx.sidebar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class apps_Adapter extends BaseAdapter {
    private Context context;
    private List<AppInfo> appInfos;
    public  apps_Adapter(Context context,List<AppInfo> appInfos){
        super();
        this.appInfos = appInfos;
        this.context = context;

    }
    @Override
    public int getCount(){
        if (appInfos!=null)
        {return appInfos.size();}
        else {
            return 0;
        }
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
public View getView(int position,View convertView, ViewGroup parent){
        //LayoutInflater inflater_view = LayoutInflater.from(context);
        ViewHolder holder;
        if(convertView == null){
            //convertView = inflater_view.inflate(R.layout.sidebar_apps, parent, false);
            convertView = LayoutInflater.from(context).inflate(R.layout.sidebar_apps,parent,false);
            holder = new ViewHolder();
            holder.img =convertView.findViewById(R.id.apps_image);
            holder.text = convertView.findViewById(R.id.apps_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.img.setBackground(appInfos.get(position).getDrawable());
        holder.text.setText(appInfos.get(position).getAppName());

        return convertView;
    }
    public class ViewHolder{
        ImageView img;
        TextView text;

    }

}
