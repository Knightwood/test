package com.example.kiylx.ti.db.historydb2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.kiylx.ti.xapplication.Xapplication;

/**
 * 创建者 kiylx
 * 创建时间 2020/5/8 11:50
 */
public class HistorysViewModel extends ViewModel {
    public LiveData<PagedList<HistoryEntity>> historiesLivePagedList;
    private HistoryDao historyDao;
    private MutableLiveData<String> queryText = new MutableLiveData<>();//存储查询字符串

    public HistorysViewModel() {
        super();
        historyDao = HistoryDbUtil.getDao(Xapplication.getInstance());
        initPagedList();
    }

    /**
     * 初始化数据
     * 根据queryText的变化生成不同的数据
     */
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

    /**
     * @param s 要查询的字符串
     *          修改queryText这个liveData的值，让switchmap获取新的数据，达到更新的目的
     */
    public void query(String s) {
        queryText.setValue(s);

    }

    public LiveData<PagedList<HistoryEntity>> getHistoriesLivePagedList() {
        return historiesLivePagedList;
    }


}
