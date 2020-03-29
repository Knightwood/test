package com.example.kiylx.ti.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentManager;
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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.kiylx.ti.core1.AboutHistory;
import com.example.kiylx.ti.myFragments.Bookmark_Dialog;
import com.example.kiylx.ti.myInterface.OpenOneWebpage;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.dateProcess.KindsofDate;
import com.example.kiylx.ti.dateProcess.TimeProcess;
import com.example.kiylx.ti.corebase.WebPage_Info;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    HistoryAdapter mAdapter;
    List<WebPage_Info> historyList;
    RecyclerView listView;
    AboutHistory sAboutHistory;
    ChipGroup mChipGroup;
    String[] mDateli;
    private static final String TAG = "历史记录";
    private static OpenOneWebpage sOpenOneWebpage;//打开网页的接口

    public static void setInterface(OpenOneWebpage openOneWebpage) {
        sOpenOneWebpage = openOneWebpage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        listView = findViewById(R.id.showHistory_1);
        listView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));

        sAboutHistory = AboutHistory.get(this);
        //初始化recyclerview为最近七天的数据
        mDateli = new String[2];
        mDateli = TimeProcess.getWeekorMonth_start(KindsofDate.THISWEEK, TimeProcess.getTime());
        updateUI(mDateli[0], mDateli[1]);
        CheckedChangeListener();
        //搜索记录
        searchHistory();
    }

    //监听chipgroup以更新recyclerview视图
    private void CheckedChangeListener() {
        mChipGroup = findViewById(R.id.Date_ChipGroup);
        mChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {

                switch (i) {
                    case R.id.thisweek:
                        mDateli = TimeProcess.getWeekorMonth_start(KindsofDate.THISWEEK, TimeProcess.getTime());
                        break;
                    case R.id.thismonth:
                        mDateli = TimeProcess.getWeekorMonth_start(KindsofDate.THISMONTH, TimeProcess.getTime());
                        break;
                    case R.id.month1:
                        mDateli = TimeProcess.getWeekorMonth_start(KindsofDate.MONTH1, TimeProcess.getTime());
                        break;
                    case R.id.month2:
                        mDateli = TimeProcess.getWeekorMonth_start(KindsofDate.MONTH2, TimeProcess.getTime());
                        break;
                    case R.id.month3:
                        mDateli = TimeProcess.getWeekorMonth_start(KindsofDate.MONTH3, TimeProcess.getTime());
                        break;
                    case R.id.month4:
                        mDateli = TimeProcess.getWeekorMonth_start(KindsofDate.MONTH4, TimeProcess.getTime());
                        break;
                    case R.id.month5:
                        mDateli = TimeProcess.getWeekorMonth_start(KindsofDate.MONTH5, TimeProcess.getTime());
                        break;
                }
                updateUI(mDateli[0], mDateli[1]);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * @param startDate 开始时间
     * @param endDate   结束时间
     *                  返回这段时间范围内的历史记录
     */
    private void updateUI(String startDate, String endDate) {
        //str1:开始日期。str2:结束日期
        Log.d(TAG, "updateUI: " + startDate + "---" + endDate);
        //获取数据
        historyList =sAboutHistory.getDataLists(startDate, endDate);

        if (null == mAdapter) {
            mAdapter = new HistoryAdapter(historyList);
            listView.setAdapter(mAdapter);
            Log.d("历史activity", "onClick: 创建adapter函数被触发");
        } else {
            mAdapter.setLists(historyList);
            mAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 用于查询时更新界面
     */
    private void updateUI() {
        //没有历史记录的话什么也不做
        if (historyList.isEmpty()) {
            return;
        }
        if (null == mAdapter) {
            mAdapter = new HistoryAdapter(historyList);
            listView.setAdapter(mAdapter);
            Log.d("历史activity", "onClick: 创建adapter函数被触发");
        } else {
            mAdapter.setLists(historyList);
            mAdapter.notifyDataSetChanged();
        }

    }


    private void addToBookMark(String url) {
        FragmentManager fm = getSupportFragmentManager();
        //把当前网页信息传给收藏dialog
        Bookmark_Dialog dialog = Bookmark_Dialog.newInstance(1, new WebPage_Info(url));
        dialog.show(fm, "收藏当前网页");
    }

    /**
     * 搜索历史记录
     */
    private void searchHistory() {
        SearchView searchView = findViewById(R.id.history_searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                historyList = sAboutHistory.query(query);
                updateUI();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {
        List<WebPage_Info> lists;

        HistoryAdapter(List<WebPage_Info> list) {
            this.lists = list;
            boolean ta = lists.isEmpty();
            Log.d("历史activity", "onClick: Adapter构造函数被触发  lists是否为空" + ta);
        }


        void setLists(List<WebPage_Info> lists) {
            this.lists = lists;
        }

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
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

    private class HistoryViewHolder extends ViewHolder implements View.OnClickListener {
        TextView title;
        TextView url;
        ImageView itemMenu;
        String URL;
        WebPage_Info info;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("历史activity", " HistoryViewHolder构造函数函数被触发");

            title = itemView.findViewById(R.id.itemTitle);
            url = itemView.findViewById(R.id.itemurl);
            itemMenu = itemView.findViewById(R.id.more_setting);
            title.setOnClickListener(this);
            itemMenu.setOnClickListener(this);

        }

        public void bind(WebPage_Info info) {
            this.info = info;
            URL = info.getUrl();
            title.setText(info.getTitle());
            url.setText(info.getUrl());
            Log.d("历史activity", "bind函数被触发");
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.more_setting:
                    itemPopmenu(v, info, getAdapterPosition());
                    break;
                case R.id.itemTitle:
                    sOpenOneWebpage.loadUrl(URL, true);
                    finish();
                    break;

            }
        }
    }

    private void itemPopmenu(View v, WebPage_Info info, int pos) {
        PopupMenu itemMenu = new PopupMenu(this, v);
        MenuInflater inflater = itemMenu.getMenuInflater();
        inflater.inflate(R.menu.history_item_option, itemMenu.getMenu());
        itemMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.open:
                        finish();
                        sOpenOneWebpage.loadUrl(info.getUrl(), false);
                        break;
                    case R.id.open1:
                        finish();
                        sOpenOneWebpage.loadUrl(info.getUrl(), true);
                        break;
                    case R.id.delete_hiatory:
                        sAboutHistory.delete(info.getUrl());
                        mAdapter.notifyItemRemoved(pos);
                        historyList.remove(info);
                        mAdapter.notifyItemRangeChanged(0, historyList.size());
                        break;
                    case R.id.addToBookmark:
                        addToBookMark(info.getUrl());
                        break;
                }
                return false;
            }
        });
        itemMenu.show();

    }
}
