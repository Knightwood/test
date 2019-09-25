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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kiylx.ti.AboutStar;
import com.example.kiylx.ti.AboutTag;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;

import java.util.ArrayList;

public class StarPageActivity extends AppCompatActivity {
    private RecyclerView view;
    private ArrayList<WebPage_Info> lists;
    private AboutStar mAboutStar;
    private RecyclerAdapter adapter;
    private PopupMenu mPopupMenu;
    private AboutTag mAboutTag;
    private Spinner mSpinner;
    private ArrayList<String> mTaglists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_page);
        //获取tag列表
        mAboutTag=AboutTag.get(StarPageActivity.this);
        mTaglists =mAboutTag.getItems();
        mTaglists.add(0,"未分类");

        //获取收藏item列表，并默认展示未tag的列表
        mAboutStar= AboutStar.get(StarPageActivity.this);
        lists = mAboutStar.getinfos_TAG("未分类");

        mSpinner=findViewById(R.id.bc_qm_ul_xr);//标签选择spinner
        showTags();//展示spinner

        //展示recyclerview
        view = findViewById(R.id.show_StarItem);
        view.setLayoutManager(new LinearLayoutManager(StarPageActivity.this));//展示具体收藏item的recyclerview
        updateUI(mTaglists.get(0));


    }
    private void updateUI(String str){
        /*一开始打开收藏页的activity，是会拿到存着所有的书签list，或是一个null，
        这时候，如果是拿到了null，那就表明没有书签，则什么也不显示
        如果没有拿到null，那根据这个时候适配器是null，那就显示所有书签，
        如果不是null，根据tag来更新视图*/
        if(lists.isEmpty()){
            //如果收藏夹没有任何内容，那什么也不做
            return;
        }
        if(null==adapter){
            adapter = new RecyclerAdapter(lists);//这里的lists是包含未分类
            view.setAdapter(adapter);
            Log.d("收藏activity", "onClick: 创建adapter函数被触发");
        }else{
            adapter.setList(getChangeLists(str));
            adapter.notifyDataSetChanged();
        }
    }

    private ArrayList<WebPage_Info> getChangeLists(String str) {
        lists=mAboutStar.getinfos_TAG(str);
        return lists;
    }

    public void setTags(String str){
        //TextView textView=findViewById(R.id.bc_qm_ul_xr);
        //“标签筛选textview”被设置好是显示那个标签后要更新recyclerview
        //textView.setText(str);
        if(!str.equals("所有书签")){
            //如果tag不是“所有书签”，那是可以根据tag更新视图的
            updateUI(str);}
    }

    public void showTags(){

        ArrayAdapter<String> madapter=new ArrayAdapter<>(StarPageActivity.this,android.R.layout.simple_list_item_1, mTaglists);
        madapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(madapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUI(mTaglists.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*public void showPopMenu(View v) {
        mPopupMenu=new PopupMenu(this,v);
        MenuBuilder menuBuilder= (MenuBuilder) mPopupMenu.getMenu();
        //存着tag的lists
        ArrayList<String> mItems=mAboutTag.getItems();
        if(mItems==null){
            //如果tag的lists是null，也就是空的，那什么tag也不会显示
            mPopupMenu.show();
            return;
        }
        for(int i=0;i<mItems.size();i++){
            //group通常为0
            //第二个参数是自己赋予item的id
            //第三个选项通常为0
            //第四个选项是item的名称
            menuBuilder.add(0,i,0,mItems.get(i));
        }
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i=item.getItemId();
                Log.d("Popmenu", String.valueOf(i));
                //设置标签筛选的标题
                setTags(item.getTitle().toString());
                return false;
            }
        });
        mPopupMenu.show();
    }*/


    public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder>{
        private ArrayList<WebPage_Info> mList;
        public RecyclerAdapter(ArrayList<WebPage_Info> lists){
            mList=lists;
        }
        public void setList(ArrayList<WebPage_Info> updatelists){
            mList=updatelists;
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
