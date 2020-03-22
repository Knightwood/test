package com.example.kiylx.ti.downloadInfo_storage;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/3/22 20:37
 * 持有数据库的引用，然后获取数据库的数据。
 * 把livedata提供给外部，这样就建立起观察者模式
 */
public class DownloadInfoViewModel extends AndroidViewModel {
    private DownloadInfoDatabase database;
    private static LiveData<List<DownloadEntity>> liveData;

    public DownloadInfoViewModel(@NonNull Application application) {
        super(application);
        database=DownloadInfoDatabase.getInstance(application);
        liveData= DownloadInfoDatabaseUtil.getDao(application).getAll();

    }


    public LiveData<List<DownloadEntity>> getiLiveData() {
        return liveData;
    }
}
