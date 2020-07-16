package com.example.kiylx.ti.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.SearchView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.db.historydb2.HistoryListAdapter;
import com.example.kiylx.ti.db.historydb2.HistorysViewModel;
import com.example.kiylx.ti.interfaces.OpenOneWebpage;
import com.example.kiylx.ti.ui.fragments.Bookmark_Dialog;

public class HistorysActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerView;

    private HistoryListAdapter adapter;
    private HistorysViewModel model;

    private ClickMethod method;
    private static final String TAG = "历史activity";
    private static OpenOneWebpage sOpenOneWebpage;//打开网页的接口

    public static void setInterface(OpenOneWebpage openOneWebpage) {
        sOpenOneWebpage = openOneWebpage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historys);

        if (method == null) {
            method = new ClickMethod();
        }

        recyclerView = findViewById(R.id.histories_recyclerView);
        adapter = new HistoryListAdapter(method, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        model = new ViewModelProvider(this).get(HistorysViewModel.class);

        model.historiesLivePagedList.observe(this, historyEntities -> {
            try {
                Log.e(TAG, "Pageall: " + historyEntities.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter.submitList(historyEntities);//给adapter数据
            Log.d("重写后的历史记录activity", "onChanged: " + historyEntities);
        });
        model.query("");
        recyclerView.setAdapter(adapter);

//toolbar
        Toolbar toolbar=findViewById(R.id.historytoolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.historys_toolbar_menu,menu);
        searchView= (SearchView) menu.findItem(R.id.histories_searchview).getActionView();
        searchHistory();
        return super.onCreateOptionsMenu(menu);
    }

    private class ClickMethod implements HistoryListAdapter.AfterClick {

        @Override
        public void After(String url, boolean closeActivity, HistoryListAdapter.Action action) {
            switch (action) {
                case JUSTOPEN:
                    sOpenOneWebpage.loadUrl(url, false);
                    finish();
                    break;
                case ADDTOBOOKMARK:
                    addToBookMark(url);
                    break;
                case OPENINNEWWINDOW:
                    sOpenOneWebpage.loadUrl(url, true);
                    finish();
                    break;
            }
        }
    }

    public void addToBookMark(String url) {
        FragmentManager fm = getSupportFragmentManager();
        //把当前网页信息传给收藏dialog
        Bookmark_Dialog dialog = Bookmark_Dialog.newInstance(1, new WebPage_Info(url));
        dialog.show(fm, "收藏当前网页");
    }

    /**
     * 搜索历史记录
     */
    private void searchHistory() {
        searchView.setOnCloseListener(() -> {
            //关闭搜索时显示全部item
            model.query("");
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                model.query(query);
                return true;
            }

            @SuppressLint("CheckResult")
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);
                return false;
            }
        });
    }
}
