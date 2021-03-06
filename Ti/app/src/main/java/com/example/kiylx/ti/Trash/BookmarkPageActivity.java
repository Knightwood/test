package com.example.kiylx.ti.trash;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.fragments.dialogfragment.DeleteBookmarkFolder_Dialog;
import com.example.kiylx.ti.interfaces.OpenOneWebpage;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;

import java.util.List;

public class BookmarkPageActivity extends AppCompatActivity implements RefreshBookMark {
    private RecyclerView mRecyclerView;
    private List<WebPage_Info> mBookmarkArrayList;
    private AboutBookmark mAboutBookmark;
    ArrayAdapter<String> mSpinnerAdapter;
    private RecyclerAdapter adapter;
    private BookMarkFolderManager mBookmarkFolderManager;
    private Spinner mSpinner;
    private List<String> mbookmarkFolderLists;
    private String bookmarkFolderName;//指示当前是哪个书签文件夹,以及在书签文件夹lists中的pos
    private static OpenOneWebpage mopenWeb;
    private static final String TAG = "BookmarkActivity";
    SearchView searchView;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_page);
        //获取书签文件夹列表
        mBookmarkFolderManager = BookMarkFolderManager.get(BookmarkPageActivity.this);
        getAllFolder();

        //获取收藏item列表，并默认展示未书签文件夹的列表
        mAboutBookmark = AboutBookmark.get(BookmarkPageActivity.this);
        //mBookmarkArrayList = mAboutBookmark.getBookmarks("未分类");


        mSpinner = findViewById(R.id.spinner3);
        selectOneFolderUpdate();//展示spinner

        //展示recyclerview
        mRecyclerView = findViewById(R.id.show_BookmarkItem);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(BookmarkPageActivity.this));//展示具体收藏item的recyclerview

        //获取指定文件夹下的书签
        getlistWithfolderpos(0);
        //接口回调
        DeleteBookmarkFolder_Dialog.setInterface(this);
        Bookmark_Dialog.setRefresh(this);


        Toolbar toolbar = findViewById(R.id.bookmark_toolbar);
        setSupportActionBar(toolbar);

    }

    /**
     * 使用多线程获取所有的文件夹对象
     */
    private void getAllFolder() {
        mbookmarkFolderLists = mBookmarkFolderManager.getBookmarkFolderlists();

    }

    //工具栏toolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookmark_toolbar, menu);
        searchView = (SearchView) menu.findItem(R.id.bookmark_Searchview).getActionView();
        //搜索书签
        search();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editBookmarkFolderName:
                editOrNewBookmarkFolder(bookmarkFolderName);
                //更新书签文件夹列表和书签记录列表
                break;
            case R.id.newBookmarkFolder:
                editOrNewBookmarkFolder(null);
                //更新书签文件夹列表
                break;
            case R.id.deleteBookmarkFolder:
                deletebookmarkFolder(bookmarkFolderName);
                //更新书签文件夹列表和书签记录列表
                break;
        }
        return false;
    }


    private void getlistWithfolderpos(int pos) {
        //更新lists，然后更新视图
        bookmarkFolderName = mbookmarkFolderLists.get(pos);//一开始就初始化书签文件夹名称防止出错
        getBookmarksWithFolderChanged(bookmarkFolderName);
        updateUI();
    }

    private void getlistWithFolderName(String name) {
        getBookmarksWithFolderChanged(name);
        updateUI();
    }

    private void updateUI() {
        if (null == adapter) {
            adapter = new RecyclerAdapter(mBookmarkArrayList);//这里的lists是包含未分类
            mRecyclerView.setAdapter(adapter);
            LogUtil.d("收藏activity", "onClick: 创建adapter函数被触发");
        } else {
            adapter.setList(mBookmarkArrayList);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * @param str “文件夹名称”
     *            获取含有此标签的书签记录
     */
    private void getBookmarksWithFolderChanged(String str) {
        mBookmarkArrayList = mAboutBookmark.getBookmarks(str);
    }

    /**
     * 在spinner中选择一个项目，然后更新含有此标签的书签列表
     */
    public void selectOneFolderUpdate() {
        if (mSpinnerAdapter == null) {
            mSpinnerAdapter = new ArrayAdapter<>(BookmarkPageActivity.this, android.R.layout.simple_list_item_1, mbookmarkFolderLists);
            mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //对bookmarkFolderName赋值，以它更新视图
                bookmarkFolderName = mbookmarkFolderLists.get(position);
                getBookmarksWithFolderChanged(bookmarkFolderName);
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * @param folderName 书签文件夹名称，根据文件夹名称更新recycler
     *                   更新BookmarkPageActivity里的那些书签recyclerview视图
     *                   <p>
     *                   1.在Bookmark_Dialog编辑详细一个书签条目后，会用当前的bookmarkFolderName刷新视图（这时传入的是参数null）。
     *                   2.在BookmarkPageActivity编辑文件夹名称后，会传入新的文件夹名称，用它刷新视图
     */
    @Override
    public void refresh(String folderName) {
        if (folderName != null) {
            bookmarkFolderName = folderName;
            mSpinnerAdapter.notifyDataSetChanged();//通知spinner的适配器更新数据
        }

        //在spinner中选择新的bookmarkFolderName，并更新书签记录的视图
        mSpinner.setSelection(mbookmarkFolderLists.indexOf(bookmarkFolderName));
        getBookmarksWithFolderChanged(bookmarkFolderName);//标签已被传进去删除，所以重新获取list，并重新置为bookmarkFolderName
        updateUI();//更新书签记录的视图

    }

    /**
     * 更新BookmarkPageActivity里的那些书签recyclerview视图
     * 文件夹被teTag_Dialog删除，所以重新获取list，并重新置为0号的“未分类”
     * 更新BookmarkPageActivity里的那些书签recyclerview视图
     */
    @Override
    public void refresh() {
        //重新获取文件夹列表
        getAllFolder();
        bookmarkFolderName = mbookmarkFolderLists.get(0);//置为“未分类”
        mSpinnerAdapter.notifyDataSetChanged();//通知适配器数据改变
        mSpinner.setSelection(0);
        getBookmarksWithFolderChanged(bookmarkFolderName);//重新获取书签列表
        updateUI();//更新视图
    }

    public static void setInterface(OpenOneWebpage minterface) {
        BookmarkPageActivity.mopenWeb = minterface;
    }

    /**
     * @param arg 文件夹名称,填入null则是新建文件夹
     *            填入文件夹名称，启动文件夹编辑对话框，修改文件夹名称
     */
    private void editOrNewBookmarkFolder(@Nullable String arg) {
        EditBookmarkFolder_Dialog editBookmarkFolder_dialog = EditBookmarkFolder_Dialog.getInstance(arg);
        editBookmarkFolder_dialog.setFlashBookmark(this);
        FragmentManager fm = getSupportFragmentManager();
        editBookmarkFolder_dialog.show(fm, "编辑书签文件夹");

    }


    private void deletebookmarkFolder(String arg) {
        DeleteBookmarkFolder_Dialog fr = DeleteBookmarkFolder_Dialog.getInstance(arg);
        FragmentManager fm = getSupportFragmentManager();
        fr.show(fm, "删除标签dialog");
    }



    public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
        private List<WebPage_Info> mList;

        RecyclerAdapter(List<WebPage_Info> lists) {
            mList = lists;
        }

        void setList(List<WebPage_Info> updatelists) {
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
        String title_1, url_1, bookmarkFolderName_1, id;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            LogUtil.d("收藏activity", " HistoryViewHolder构造函数函数被触发");
            title = itemView.findViewById(R.id.itemTitle);
            url = itemView.findViewById(R.id.itemurl);
            title.setOnClickListener(this);
            imageView = itemView.findViewById(R.id.more_setting);
            imageView.setOnClickListener(this);
        }

        /**
         * @param v      要添加popmenu的视图
         * @param title1 标题
         * @param url1   网址
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
                            showBookmarkDialog(id, title1, url1, bookmarkFolderName_1);
                            break;
                        case R.id.delete_Bookmark:
                            mAboutBookmark.deleteItem(id);
                            getBookmarksWithFolderChanged(bookmarkFolderName_1);
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
            id = info.getUuid();
            bookmarkFolderName_1 = info.getBookmarkFolderUUID();

            title.setText(title_1);
            url.setText(url_1);

            LogUtil.d("收藏activity", "bind函数被触发");
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.itemTitle:
                    //点击item后访问网址
                    finish();
                    mopenWeb.loadUrl(url_1, false);
                    break;
                case R.id.more_setting:
                    LogUtil.d("BookmarkPageActivity", "more_setting被触发");
                    addPopMenu(itemView.findViewById(R.id.more_setting), title_1, url_1);
                    break;
            }

            LogUtil.d("收藏activity", "onclick函数被触发");
        }

        /**
         * @param title              标题
         * @param url                网址
         * @param bookmarkFolderName 标签
         *                           显示书签编辑对话框
         */
        void showBookmarkDialog(String id, String title, String url, String bookmarkFolderName) {
            Bookmark_Dialog Bookmark_dialog = Bookmark_Dialog.newInstance(2, new WebPage_Info.Builder(url).uuid(id).title(title).bookmarkFolderUUID(bookmarkFolderName).build());
            FragmentManager fm = getSupportFragmentManager();
            Bookmark_dialog.show(fm, "changeBookmark");
        }
    }

    /**
     * searchview，搜索书签。
     */
    private void search() {

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpinner.setVisibility(View.GONE);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                LogUtil.d(TAG, "searchview查询文本1: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.equals("")) {
                    getlistWithFolderName(bookmarkFolderName);
                } else {
                    mBookmarkArrayList = mAboutBookmark.queryBookmark(newText);
                    LogUtil.d(TAG, "searchview查询文本2: " + newText);
                    if (mBookmarkArrayList.isEmpty()) {
                        Toast.makeText(BookmarkPageActivity.this, "未找到结果", Toast.LENGTH_LONG).show();
                    } else {
                        updateUI();
                    }

                }
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mSpinner.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }
}
/*public void showPopMenu(View v) {
        mPopupMenu=new PopupMenu(this,v);
        MenuBuilder menuBuilder= (MenuBuilder) mPopupMenu.getMenu();
        //存着tag的lists
        List<String> mItems=mBookmarkFolderManager.getfolderListfromDB();
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
            menuBuilder.InsertItem(0,i,0,mItems.get(i));
        }
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i=item.getItemId();
                LogUtil.d("Popmenu", String.valueOf(i));
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
        });


        /*
private void addMenuForEditBookmarkFolder(View v) {
    //用来控制bookmarkFolderName编辑的几个菜单选项
    PopupMenu menu = new PopupMenu(BookmarkPageActivity.this, v);
    MenuInflater inflater = menu.getMenuInflater();
    inflater.inflate(R.menu.tagmanager_menu, menu.getMenu());
    menu.show();
    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.editBookmarkFolderName:
                    editOrNewBookmarkFolder(bookmarkFolderName);
                    //更新书签文件夹列表和书签记录列表
                    break;
                case R.id.newBookmarkFolder:
                    editOrNewBookmarkFolder(null);
                    //更新书签文件夹列表
                    break;
                case R.id.deleteBookmarkFolder:
                    deletebookmarkFolder(bookmarkFolderName);
                    //更新书签文件夹列表和书签记录列表
                    break;
            }
            return false;
        }
    });
}

        */