package com.example.kiylx.sidebar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;

import java.util.List;


public class nameAdapter extends BaseAdapter implements Filterable {
    List<AppInfo> appInfos;
    String name;

    public nameAdapter(List<AppInfo> appInfos, String name) {
        this.appInfos = appInfos;
        this.name = name;
    }
    @Override
    public int getCount(){
        return appInfos.size();
    }
    @Override
    public Object getItem(int q){
        return appInfos.get(q);
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        

    }
}
