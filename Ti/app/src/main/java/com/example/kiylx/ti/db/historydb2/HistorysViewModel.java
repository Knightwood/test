package com.example.kiylx.ti.db.historydb2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.kiylx.ti.Xapplication;

/**
 * 创建者 kiylx
 * 创建时间 2020/5/8 11:50
 */
public class HistorysViewModel extends ViewModel {
    public LiveData<PagedList<HistoryEntity>> historiesLivePagedList;
    private HistoryDao historyDao;
    private MutableLiveData<String> queryText = new MutableLiveData<>();

    public HistorysViewModel() {
        super();
        historyDao = HistoryDbUtil.getDao(Xapplication.getInstance());
        initPagedList();
    }

    private void initPagedList() {

        PagedList.Config config = (new PagedList.Config.Builder())
                .setPageSize(10)
                .build();

        historiesLivePagedList = Transformations.switchMap(queryText, input -> {
            if (input == null || input.equals("")) {
                return new LivePagedListBuilder<>(historyDao.getAll(), config).build();
            } else {
                System.out.println("当前查询: " + input);
                return new LivePagedListBuilder<>(historyDao.getMatchersList("%"+input+"%"), config).build();
            }
        });

    }

    public void query(String s) {
        queryText.setValue(s);

    }

    public LiveData<PagedList<HistoryEntity>> getHistoriesLivePagedList() {
        return historiesLivePagedList;
    }


}