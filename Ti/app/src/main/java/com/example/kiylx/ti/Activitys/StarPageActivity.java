package com.example.kiylx.ti.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kiylx.ti.AboutStar;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;

import java.util.ArrayList;

public class StarPageActivity extends AppCompatActivity {
    private RecyclerView view;
    private ArrayList<WebPage_Info> lists;
    private AboutStar mAboutStar;
    private RecyclerAdapter adapter;
    private PopupMenu mPopupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_page);

        updateUI();

    }
    private void updateUI(){
        mAboutStar= AboutStar.get(StarPageActivity.this);
        lists = mAboutStar.getWebPageinfos();
        if(lists==null){
            //如果收藏夹没有任何内容，那什么也不做
            return;
        }

        view = findViewById(R.id.show_StarItem);
        view.setLayoutManager(new LinearLayoutManager(StarPageActivity.this));


        if(null==adapter){
            adapter = new RecyclerAdapter(lists);
            view.setAdapter(adapter);
            Log.d("收藏activity", "onClick: 创建adapter函数被触发");
        }else{
            adapter.notifyDataSetChanged();
        }


    }
    public void showPopMenu(View v) {
        mPopupMenu=new PopupMenu(this,v);
        mPopupMenu.inflate(R.menu.menu_tag_list_item);
        mPopupMenu.show();
    }


    public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder>{
        private ArrayList<WebPage_Info> mList;
        public RecyclerAdapter(ArrayList<WebPage_Info> lists){
            mList=lists;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }
    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        TextView url;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("收藏activity", " HistoryViewHolder构造函数函数被触发");
            title=itemView.findViewById(R.id.itemTitle);
            url=itemView.findViewById(R.id.itemurl);
            itemView.setOnClickListener(this);
        }
        public void bind(WebPage_Info info){
            title.setText(info.getTitle());
            url.setText(info.getUrl());
            Log.d("收藏activity", "bind函数被触发");
        }

        @Override
        public void onClick(View v) {
            TextView urlview=itemView.findViewById(R.id.itemurl);
            Log.d("收藏activity", "onclick函数被触发"+urlview.getText());
        }
    }
}
