package com.example.kiylx.ti.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kiylx.ti.AboutHistory;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity{
    HistoryAdapter mAdapter;
    ArrayList<WebPage_Info> mWebPage_infos;
    View view;
    RecyclerView listView;
    AboutHistory sAboutHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        listView=findViewById(R.id.showHistory_1);
        listView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));

        updateUI();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    private void updateUI(){
        sAboutHistory=AboutHistory.get(this);
        //mWebPage_infos=sAboutHistory.getHistoryInfos();
        mWebPage_infos=sAboutHistory.getInfoFromDate("2019-07-01","2019-09-11");
        if(null==mAdapter){
            mAdapter=new HistoryAdapter(mWebPage_infos);
            listView.setAdapter(mAdapter);
            Log.d("历史activity", "onClick: 创建adapter函数被触发");
        }else{
            mAdapter.notifyDataSetChanged();
        }


    }
    private void itemPopmenu(View v){
        PopupMenu itemMenu=new PopupMenu(this,v);
        MenuInflater inflater = itemMenu.getMenuInflater();
        inflater.inflate(R.menu.history_item_option,itemMenu.getMenu());
        itemMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.open:
                        break;
                    case R.id.open1:
                        break;
                    case R.id.delete_hiatory:
                        break;
                    case R.id.addToStar:
                        break;
                }
                return false;
            }
        });
        itemMenu.show();
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder>{
        ArrayList<WebPage_Info> lists;
        HistoryAdapter(ArrayList<WebPage_Info> list){
            this.lists=list;
            boolean ta=lists.isEmpty();
            Log.d("历史activity", "onClick: Adapter构造函数被触发  lists是否为空"+ta+lists.get(0).getUrl());
        }

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item,parent,false);
            Log.d("历史activity", "onCreateViewHolder函数被触发");
            return new HistoryViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
            holder.bind(lists.get(position));
            Log.d("历史activity", " onBindViewHolder函数被触发");
        }


        @Override
        public int getItemCount() {
            return lists.size();
        }
    }
    private class HistoryViewHolder extends ViewHolder implements View.OnClickListener{
        TextView title;
        TextView url;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("历史activity", " HistoryViewHolder构造函数函数被触发");
            title=itemView.findViewById(R.id.itemTitle);
            url=itemView.findViewById(R.id.itemurl);
            itemView.setOnClickListener(this);
        }
        public void bind(WebPage_Info info){
            title.setText(info.getTitle());
            url.setText(info.getUrl());
            Log.d("历史activity", "bind函数被触发");
        }

        @Override
        public void onClick(View v) {

        }
    }
}
