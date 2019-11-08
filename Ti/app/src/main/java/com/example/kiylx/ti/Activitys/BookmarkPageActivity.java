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

import com.example.kiylx.ti.Core1.AboutBookmark;
import com.example.kiylx.ti.Core1.AboutTag;
import com.example.kiylx.ti.Fragments.DeleteTag_Dialog;
import com.example.kiylx.ti.Fragments.Bookmark_Dialog;
import com.example.kiylx.ti.Fragments.EditBox_Dialog;
import com.example.kiylx.ti.INTERFACE.OpenOneWebpage;
import com.example.kiylx.ti.INTERFACE.RefreshBookMark;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.Corebase.WebPage_Info;

import java.util.ArrayList;

public class BookmarkPageActivity extends AppCompatActivity implements RefreshBookMark {
    private RecyclerView mRecyclerView;
    private ArrayList<WebPage_Info> mBookmarkArrayList;
    private AboutBookmark mAboutBookmark;
    private RecyclerAdapter adapter;
    private AboutTag mAboutTag;
    private Spinner mSpinner;
    private ArrayList<String> mTaglists;
    private String tagname;//指示当前是哪个tag,以及在taglists中的pos
    private static OpenOneWebpage mopenWeb;
    private TextView deleteTag_textview;
    private static final String TAG = "BookmarkActivity";


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_page);
        //获取tag列表
        mAboutTag = AboutTag.get(BookmarkPageActivity.this);
        mTaglists = mAboutTag.getTagListfromDB();

        //获取收藏item列表，并默认展示未tag的列表
        mAboutBookmark = AboutBookmark.get(BookmarkPageActivity.this);
        mBookmarkArrayList = mAboutBookmark.getBookmarks("未分类");

        mSpinner = findViewById(R.id.bc_qm_ul_xr);//标签选择spinner
        selectTagtoUpdate();//展示spinner

        //展示recyclerview
        mRecyclerView = findViewById(R.id.show_BookmarkItem);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(BookmarkPageActivity.this));//展示具体收藏item的recyclerview

        //更新lists，然后更新视图
        tagname = mTaglists.get(0);//一开始就初始化tag名称防止出错
        getBookmarksWithTagChanged(tagname);
        updateUI();
        //接口回调

        DeleteTag_Dialog.setInterface(this);
        Bookmark_Dialog.setRefresh(this);

        //删除tag按钮
        deleteTag_textview = findViewById(R.id.edit_tags);
        deleteTag_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMenutoEditTag(v);

            }
        });

    }

    private void updateUI() {
        /*一开始打开收藏页的activity，是会拿到存着所有的书签list，或是一个null，
        这时候，如果是拿到了null，那就表明没有书签，则什么也不显示
        如果没有拿到null，那根据这个时候适配器是null，那就显示所有书签，
        如果不是null，根据tag来更新视图*/
        /*if(mBookmarkArrayList.isEmpty()){
            //如果收藏夹没有任何内容，那什么也不做，且隐藏recyclerview
           mRecyclerView.setVisibility(View.GONE);
            return;
        }
        mRecyclerView.setVisibility(View.VISIBLE);*/
        if (null == adapter) {
            adapter = new RecyclerAdapter(mBookmarkArrayList);//这里的lists是包含未分类
            mRecyclerView.setAdapter(adapter);
            Log.d("收藏activity", "onClick: 创建adapter函数被触发");
        } else {
            adapter.setList(mBookmarkArrayList);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * @param str “标签”
     *            获取含有此标签的书签记录
     */
    private void getBookmarksWithTagChanged(String str) {

        mBookmarkArrayList = mAboutBookmark.getBookmarks(str);
    }

    /**
     * 在spinner中选择一个项目，然后更新含有此标签的书签列表
     */
    public void selectTagtoUpdate() {
        ArrayAdapter<String> madapter = new ArrayAdapter<>(BookmarkPageActivity.this, android.R.layout.simple_list_item_1, mTaglists);
        madapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(madapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //对tagname赋值，以它更新视图
                tagname = mTaglists.get(position);
                getBookmarksWithTagChanged(tagname);
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * @param arg1 书签，根据书签更新recycler
     *             更新BookmarkPageActivity里的那些书签recyclerview视图
     */
    @Override
    public void refresh(String arg1) {


        //在书签页面编辑详细一个书签条目后，应该用当前的tagname刷新视图。所以，此时传入的是null，直接用当前的tagname刷新视图
        if (arg1 != null)
            tagname = arg1;

        //在spinner中选择新的tagname，并更新书签记录的视图
        mSpinner.setSelection(mTaglists.indexOf(tagname));
        getBookmarksWithTagChanged(tagname);//标签已被传进去删除，所以重新获取list，并重新置为tagname
        updateUI();//更新书签记录的视图

    }

    /**
     * 更新BookmarkPageActivity里的那些书签recyclerview视图
     * 标签已被teTag_Dialog删除，所以重新获取list，并重新置为0号的“未分类”
     * 更新BookmarkPageActivity里的那些书签recyclerview视图
     */
    @Override
    public void refresh() {

        tagname = mTaglists.get(0);
        mSpinner.setSelection(0);
        getBookmarksWithTagChanged(tagname);
        updateUI();
    }

    public static void setInterface(OpenOneWebpage minterface) {
        BookmarkPageActivity.mopenWeb = minterface;
    }

    /**
     * @param v 要添加上popmenu的视图
     */
    private void addMenutoEditTag(View v) {
        //用来控制tag编辑的几个菜单选项
        PopupMenu menu = new PopupMenu(BookmarkPageActivity.this, v);
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.tagmanager_menu, menu.getMenu());
        menu.show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.editTag:
                        editTag(tagname);
                        //更新tag列表和书签记录列表
                        break;
                    case R.id.newTag:
                        newTag();
                        //更新tag列表
                        break;
                    case R.id.deleteTag:
                        deleteTag(tagname);
                        //更新tag列表和书签记录列表
                        break;
                }
                return false;
            }
        });
    }

    /**
     * @param arg 标签名称
     *            填入标签名称，启动标签编辑对话框，修改标签名称
     */
    private void editTag(String arg) {
        EditBox_Dialog editBox_dialog = EditBox_Dialog.getInstance(arg);
        FragmentManager fm = getSupportFragmentManager();
        editBox_dialog.show(fm, "编辑tag");

    }

    /**
     * 启动“标签”编辑对话框，添加新的“标签”
     */
    private void newTag() {
        EditBox_Dialog editBox_dialog = EditBox_Dialog.getInstance();
        FragmentManager fm = getSupportFragmentManager();
        editBox_dialog.show(fm, "新建一个tag");

    }

    private void deleteTag(String arg) {
        DeleteTag_Dialog fr = DeleteTag_Dialog.getInstance(arg);
        FragmentManager fm = getSupportFragmentManager();
        fr.show(fm, "删除标签dialog");
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<WebPage_Info> mList;

        RecyclerAdapter(ArrayList<WebPage_Info> lists) {
            mList = lists;
        }

        void setList(ArrayList<WebPage_Info> updatelists) {
            mList = updatelists;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
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

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView url;
        ImageView imageView;
        String title_1, url_1, tag_1;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("收藏activity", " HistoryViewHolder构造函数函数被触发");
            title = itemView.findViewById(R.id.itemTitle);
            url = itemView.findViewById(R.id.itemurl);
            title.setOnClickListener(this);
            imageView = itemView.findViewById(R.id.more_setting);
            imageView.setOnClickListener(this);
        }

        /**
         * @param v 要添加popmenu的视图
         * @param title1 标题
         * @param url1 网址
         */
        void addPopMenu(View v, final String title1, final String url1) {
            PopupMenu popupMenu = new PopupMenu(BookmarkPageActivity.this, v);
            MenuInflater menuInflater = popupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.bookmark_item_options, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.edit_Bookmark:
                            showBookmarkDialog(title1, url1, tag_1);
                            break;
                        case R.id.delete_Bookmark:
                            mAboutBookmark.delete(url1);
                            getBookmarksWithTagChanged(tag_1);
                            updateUI();
                            break;
                        case R.id.openPageinNewWindow:
                            finish();
                            //因为mainactivity里加载网页代码太烂，这里写true用新标签页打开会有bug
                            mopenWeb.loadUrl(url_1, true);
                    }
                    return false;
                }
            });
            popupMenu.show();

        }

        /**
         * @param info WebPage_Info实例
         *             绑定数据，把传入的对象数据和holder中定义的变量绑定，并把数据填充到布局
         */
        public void bind(WebPage_Info info) {
            title_1 = info.getTitle();
            url_1 = info.getUrl();
            tag_1 = info.getWebTags();

            title.setText(title_1);
            url.setText(url_1);

            Log.d("收藏activity", "bind函数被触发");
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.itemTitle:
                    //点击item后访问网址
                    if (url_1.equals("about:newTab")) {
                        finish();
                        mopenWeb.loadUrl(null, false);
                    } else {
                        finish();
                        mopenWeb.loadUrl(url_1, false);
                    }
                    break;
                case R.id.more_setting:
                    Log.d("BookmarkPageActivity", "more_setting被触发");
                    addPopMenu(itemView.findViewById(R.id.more_setting), title_1, url_1);
                    break;
            }

            Log.d("收藏activity", "onclick函数被触发");
        }

        /**
         * @param title 标题
         * @param url 网址
         * @param tag 标签
         *            显示书签编辑对话框
         */
        void showBookmarkDialog(String title, String url, String tag) {
            Bookmark_Dialog Bookmark_dialog = Bookmark_Dialog.newInstance(2,new WebPage_Info(title, url, tag, -1,null));
            FragmentManager fm = getSupportFragmentManager();
            Bookmark_dialog.show(fm, "changeBookmark");
        }
    }
}
/*public void showPopMenu(View v) {
        mPopupMenu=new PopupMenu(this,v);
        MenuBuilder menuBuilder= (MenuBuilder) mPopupMenu.getMenu();
        //存着tag的lists
        ArrayList<String> mItems=mAboutTag.getTagListfromDB();
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
//chipgroup
        /*ChipGroup mChipgroup=findViewById(R.id.Bookmark_chipgroup);
        mChipgroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {

            }
        });*/