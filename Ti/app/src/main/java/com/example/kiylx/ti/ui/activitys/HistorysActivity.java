package com.example.kiylx.ti.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.Xapplication;
import com.example.kiylx.ti.db.historydb2.HistoryDao;
import com.example.kiylx.ti.db.historydb2.HistoryDbUtil;
import com.example.kiylx.ti.db.historydb2.HistoryEntity;
import com.example.kiylx.ti.db.historydb2.HistoryListAdapter;

import java.util.ArrayList;

public class HistorysActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerView;

    private HistoryListAdapter adapter;
    private HistoryDao historyDao;
    private LiveData<PagedList<HistoryEntity>> historiesLivePagedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historys);

        searchView = findViewById(R.id.histories_searchview);
        recyclerView = findViewById(R.id.histories_recyclerView);
        adapter = new HistoryListAdapter(new HistoryListAdapter.AfterClick() {
            @Override
            public void After(String url, boolean closeActivity, HistoryListAdapter.Action action) {

            }
        },this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        historyDao = HistoryDbUtil.getDao(Xapplication.getInstance());

        //获取数据
        historiesLivePagedList = new LivePagedListBuilder<>(historyDao.getAll(), 5).build();
        historiesLivePagedList.observe(this, new Observer<PagedList<HistoryEntity>>() {
            @Override
            public void onChanged(PagedList<HistoryEntity> historyEntities) {
                adapter.submitList(historyEntities);//给adapter数据
                Log.d("重写后的历史记录activity", "onChanged: " + historyEntities);
            }
        });


    }
}
