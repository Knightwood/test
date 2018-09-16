package com.example.kiylx.sidebar;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filterable;
import android.widget.Filter;

public class AppInfosAdapter extends BaseAdapter implements Filterable {
     private Context context;
     private List<AppInfo> appInfos;
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

    
    //数据源是上面的appInfos.
    private final Object mLock = new Object();
    /*
    * This lock is also used by the filter
    * (see {@link #getFilter()} to make a synchronized copy of
    * the original array of data.
    * 过滤器上的锁可以同步复制原始数据。
    */
    private ArrayList<AppInfo> firstBackup_appInfos;
    //对象数组的备份，当调用ArrayFilter的时候初始化和使用。此时，对象数组只包含已经过滤的数据。
    private ArrayFilter mFilter;

    public  Filter getFilter(){
    if(mFilter == null){
        mFilter = new ArrayFilter();
        }
    return mFilter;
    }

    public class ArrayFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence prefix){
            FilterResults results = new FilterResults();
            // 过滤的结果

            if(firstBackup_appInfos == null){
                synchronized (mLock) {
                    firstBackup_appInfos = new ArrayList<AppInfo>(appInfos);
                }// 原始数据备份为空时，上锁，同步复制原始数据
            }

            if(prefix == null || prefix.length()==0){
                ArrayList<AppInfo> list;
                synchronized (mLock){
                    list = new ArrayList<AppInfo>(firstBackup_appInfos);
                }
                results.values = list;
                results.count = list.size();
                //首字母是空的，复制一个原始数据备份，返回的results就是原始数据，不进行过滤。
            }else{
                String prefixString = prefix.toString().toLowerCase();
                //将首字母转换成字符并且小写。
                ArrayList<AppInfo> backUp_appinfos;
                synchronized (mLock){
                    backUp_appinfos = new ArrayList<AppInfo>(firstBackup_appInfos);
                //此时，backUp_appinfos是原始数据备份,是list<appinfo>类型}
            }
            final  int count = backUp_appinfos.size();
                //count是values的条数
            final ArrayList<AppInfo> finallyResults = new ArrayList<AppInfo>();

            for(int i=0; i< count;i++)
            {
                final AppInfo value = backUp_appinfos.get(i);
                final String valueText = value.getAppName().toLowerCase();

                if (valueText.contains(prefixString.toString())){
                    //prefixString与valueText匹配，有一个字符相同就添加进结果。
                    //调用的indexOf方法，其中，返回值是-1就表示没有相同字符，所以，这里的“!=-1”就代表着只要有一个字符相同。
                    finallyResults.add(value);
                }else{
                    final String[] words = valueText.split(" ");
                    final int wordCount = words.length;

                    for(int k=0;k< wordCount; k++){
                        if(words[k].contains(prefixString)){
                            finallyResults.add(value);
                            break;
                        }
                    }
                }

            }
            results.values = finallyResults;
            results.count = finallyResults.size();
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence prefix, FilterResults results){
        appInfos = (List<AppInfo>) results.values;
        if (results.count>0){
        notifyDataSetChanged();}
        else{
            notifyDataSetInvalidated();
            Log.d("appInfoActivity", "jk");
        }
        }
    }



}
