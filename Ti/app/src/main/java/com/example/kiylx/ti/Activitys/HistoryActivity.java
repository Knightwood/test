package com.example.kiylx.ti.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
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
        view = LayoutInflater.from(HistoryActivity.this).inflate(R.layout.activity_history,null,false);
        listView=view.findViewById(R.id.showHistory);
        sAboutHistory=AboutHistory.get(HistoryActivity.this);
        updateUI(listView);
        setContentView(R.layout.activity_history);

    }

    private void updateUI(RecyclerView view){
        view.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
        mWebPage_infos=sAboutHistory.getHistoryInfos();
        if(null==mAdapter){
            mAdapter=new HistoryAdapter(mWebPage_infos);
            view.setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
        }

    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder>{
        ArrayList<WebPage_Info> lists;
        public HistoryAdapter(ArrayList<WebPage_Info> lists){
            this.lists=lists;
        }

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item,null,false);
            return new HistoryViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
            holder.bind(lists.get(position));
        }


        @Override
        public int getItemCount() {
            return lists.size();
        }
    }
    private class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        TextView url;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.itemTitle);
            url=itemView.findViewById(R.id.itemurl);
            itemView.setOnClickListener(this);
        }
        public void bind(WebPage_Info info){
            title.setText(info.getTitle());
            url.setText(info.getUrl());
        }

        @Override
        public void onClick(View v) {

        }
    }
}
