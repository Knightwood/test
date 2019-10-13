package com.example.kiylx.ti.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kiylx.ti.AboutStar;
import com.example.kiylx.ti.AboutTag;
import com.example.kiylx.ti.Fragments.Star_Dialog;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class StarPageActivity extends AppCompatActivity implements Star_Dialog.Star_DialogF_interface {
    private RecyclerView mRecyclerView;
    private ArrayList<WebPage_Info> mPageInfoArrayList;
    private AboutStar mAboutStar;
    private RecyclerAdapter adapter;
    private AboutTag mAboutTag;
    private Spinner mSpinner;
    private ArrayList<String> mTaglists;
    private String tagname;//指示当前是哪个tag在taglists中的pos
    private static SatrPageA_interface sSatrPageA_interface;


    @Override
    protected void onStart() {
        super.onStart();
    }

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
        mPageInfoArrayList = mAboutStar.getinfos_TAG("未分类");

        mSpinner=findViewById(R.id.bc_qm_ul_xr);//标签选择spinner
        showTags();//展示spinner

        //展示recyclerview
        mRecyclerView = findViewById(R.id.show_StarItem);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(StarPageActivity.this));//展示具体收藏item的recyclerview

        //chipgroup
        /*ChipGroup mChipgroup=findViewById(R.id.star_chipgroup);
        mChipgroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {

            }
        });*/

        //更新lists，然后更新视图
        getChangeLists(mTaglists.get(0));
        updateUI();
        //接口回调
        Star_Dialog.setInterface(this);

    }
    private void updateUI(){
        /*一开始打开收藏页的activity，是会拿到存着所有的书签list，或是一个null，
        这时候，如果是拿到了null，那就表明没有书签，则什么也不显示
        如果没有拿到null，那根据这个时候适配器是null，那就显示所有书签，
        如果不是null，根据tag来更新视图*/
        if(mPageInfoArrayList.isEmpty()){
            //如果收藏夹没有任何内容，那什么也不做，且隐藏recyclerview
           mRecyclerView.setVisibility(View.GONE);
            return;
        }
        mRecyclerView.setVisibility(View.VISIBLE);
        if(null==adapter){
            adapter = new RecyclerAdapter(mPageInfoArrayList);//这里的lists是包含未分类
            mRecyclerView.setAdapter(adapter);
            Log.d("收藏activity", "onClick: 创建adapter函数被触发");
        }else{
            adapter.setList(mPageInfoArrayList);
            adapter.notifyDataSetChanged();
        }
    }

    private void getChangeLists(String str) {
        mPageInfoArrayList =mAboutStar.getinfos_TAG(str);
    }

    public void showTags(){

        ArrayAdapter<String> madapter=new ArrayAdapter<>(StarPageActivity.this,android.R.layout.simple_list_item_1, mTaglists);
        madapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(madapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tagname=mTaglists.get(position);//更新tagname
                //对tagname赋值，以它更新视图
                getChangeLists(tagname);
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void updatelistui() {
        //在修改完item后刷新视图
        getChangeLists(tagname);
        updateUI();
    }

    public interface SatrPageA_interface {
        void loadUrl(String urlname,boolean flags);
        //flags,是否使用新的标签页打开网页
    }

    public static void setInterface(SatrPageA_interface minterface){
        StarPageActivity.sSatrPageA_interface =minterface;
    }


    public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder>{
        private ArrayList<WebPage_Info> mList;
        RecyclerAdapter(ArrayList<WebPage_Info> lists){
            mList=lists;
        }
        void setList(ArrayList<WebPage_Info> updatelists){
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
        ImageView imageView;
        String title_1,url_1,tag_1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("收藏activity", " HistoryViewHolder构造函数函数被触发");
            title=itemView.findViewById(R.id.itemTitle);
            url=itemView.findViewById(R.id.itemurl);
            title.setOnClickListener(this);
            imageView= itemView.findViewById(R.id.more_setting);
            imageView.setOnClickListener(this);
        }
        void addPopMenu(View v, final String title1, final String url1){
            PopupMenu popupMenu=new PopupMenu(StarPageActivity.this,v);
            MenuInflater menuInflater=popupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.star_item_options,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.edit_star:
                            showStarDialog(title1,url1,tag_1);
                            break;
                        case R.id.delete_star:
                            mAboutStar.delete(url1);
                            getChangeLists(tag_1);
                            updateUI();
                            break;
                        case R.id.openPageinNewWindow:
                            finish();
                            //因为mainactivity里加载网页代码太烂，这里写true用新标签页打开会有bug
                            sSatrPageA_interface.loadUrl(url_1,false);
                    }
                    return false;
                }
            });
            popupMenu.show();

        }
        public void bind(WebPage_Info info){
            title_1=info.getTitle();
            url_1=info.getUrl();
            tag_1=info.getWebTags();

            title.setText(title_1);
            url.setText(url_1);

            Log.d("收藏activity", "bind函数被触发");
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.itemTitle:
                    //点击item后访问网址
                    if(url_1.equals("about:newTab")){
                        finish();
                        sSatrPageA_interface.loadUrl(null,false);
                    }else{
                        finish();
                        sSatrPageA_interface.loadUrl(url_1,false);
                    }
                    break;
                case R.id.more_setting:
                    Log.d("StarPageActivity","more_setting被触发");
                    addPopMenu(itemView.findViewById(R.id.more_setting),title_1,url_1);
                    break;
            }

            Log.d("收藏activity", "onclick函数被触发");
        }
        void showStarDialog(String str1, String str2,String str3){
            Star_Dialog star_dialog = Star_Dialog.newInstance();
            FragmentManager fm = getSupportFragmentManager();
            star_dialog.show(fm,"changeStar");
            star_dialog.putInfo(new WebPage_Info(str1,str2,str3,-1));
        }
    }
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