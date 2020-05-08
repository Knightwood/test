package com.example.kiylx.ti.db.historydb2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.Xapplication;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/5/6 23:43
 */
public class HistoryListAdapter extends PagedListAdapter<HistoryEntity, HistoryListAdapter.HViewHolder> {
    private AfterClick afterClick;
    private WeakReference<Context> weakReference;

    public HistoryListAdapter(@NonNull AfterClick afterClick, Context context) {
        super(new DiffUtil.ItemCallback<HistoryEntity>() {
            @Override
            public boolean areItemsTheSame(@NonNull HistoryEntity oldItem, @NonNull HistoryEntity newItem) {
                return oldItem.uid == newItem.uid;//判断是否是同一个
            }

            @Override
            public boolean areContentsTheSame(@NonNull HistoryEntity oldItem, @NonNull HistoryEntity newItem) {
                return oldItem.date.equals(newItem.date);//判断内容是否一样
            }
        });

        this.afterClick=afterClick;
        weakReference=new WeakReference<>(context);
        Log.d("历史记录", "HistoryListAdapter:构造函数 ");

    }


    @NonNull
    @Override
    public HViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.history_item, parent, false);
        return new HViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HViewHolder holder, int position) {
        holder.bind(getItem(position));
        Log.d("历史记录", "绑定布局时item数量: "+getItemCount());
        Log.d("历史activity", " onBindViewHolder函数被触发");
    }


    class HViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        TextView url;
        ImageView itemMenu;
        String URL;
        HistoryEntity info;

        public HViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("历史activity", " HistoryViewHolder构造函数函数被触发");

            title = itemView.findViewById(R.id.itemTitle);
            url = itemView.findViewById(R.id.itemurl);
            itemMenu = itemView.findViewById(R.id.more_setting);
            title.setOnClickListener(this);
            itemMenu.setOnClickListener(this);
        }

        public void bind(HistoryEntity info) {
            if (info==null){
                title.setText("loading");//使用分页后，容器的数量可能会大于数据的数量，所以如果容器数量多，传入的info会是null，所以在此装饰成正在加载的样子
            }else{
                this.info = info;
                URL = info.url;
                title.setText(info.title);
                url.setText(info.url);
                Log.d("历史activity", "bind函数被触发");
            }

        }
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.more_setting:
                    itemPopmenu(v, info);
                    break;
                case R.id.itemTitle:
                    /*sOpenOneWebpage.loadUrl(URL, true);
                    finish();*/
                    if (afterClick != null) {
                        afterClick.After(URL,true,Action.OPENINNEWWINDOW);
                    }
                    break;

            }
        }

    }

    public void itemPopmenu(View v, HistoryEntity info) {
        PopupMenu itemMenu = new PopupMenu(weakReference.get(), v);//使用applicationContext会报错，原因是属性问题
        MenuInflater inflater = itemMenu.getMenuInflater();
        inflater.inflate(R.menu.history_item_option, itemMenu.getMenu());
        itemMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.open:
                        /*finish();
                        sOpenOneWebpage.loadUrl(info.getUrl(), false);*/
                        if (afterClick != null) {
                            afterClick.After(info.url,true,Action.JUSTOPEN);
                        }
                        break;
                    case R.id.open1:
                        /*finish();
                        sOpenOneWebpage.loadUrl(info.getUrl(), true);*/
                        if (afterClick != null) {
                            afterClick.After(info.url,true,Action.OPENINNEWWINDOW);
                        }
                        break;
                    case R.id.delete_hiatory:
                        /*sAboutHistory.delete(info.getUrl());
                        mAdapter.notifyItemRemoved(pos);
                        historyList.remove(info);
                        mAdapter.notifyItemRangeChanged(0, historyList.size());*/
                        HistoryDbUtil.getDao(Xapplication.getInstance()).deleteWithUrl(info.url);
                        //notifyItemRemoved(pos);
                        break;
                    case R.id.addToBookmark:
                        /*addToBookMark(info.getUrl());*/
                        if (afterClick != null) {
                            afterClick.After(info.url,false,Action.ADDTOBOOKMARK);
                        }
                        break;
                }
                return true;
            }
        });
        itemMenu.show();

    }

    /**
     * 点击item或是点击item的popmenu中的选项后，触发对于该webpageinfo的操作
     */
    public interface AfterClick{
        void After(String url,boolean closeActivity,Action action);
    }
    public enum Action{
        OPENINNEWWINDOW,JUSTOPEN,ADDTOBOOKMARK;
    }
}

