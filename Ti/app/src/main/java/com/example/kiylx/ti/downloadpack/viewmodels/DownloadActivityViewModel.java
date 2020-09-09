package com.example.kiylx.ti.downloadpack.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.kiylx.ti.downloadpack.core.DownloadInfo;
import com.example.kiylx.ti.tool.LogUtil;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/9/4 10:46
 * packageName：com.example.kiylx.ti.downloadpack.viewmodels
 * 描述：
 */
public class DownloadActivityViewModel extends ViewModel {
    private static final String TAG = "DownloadingViewModel";
    private MutableLiveData<List<DownloadInfo>> downloadingList;
    private MutableLiveData<List<DownloadInfo>> downloadcompleteList;


    public DownloadActivityViewModel() {
        super();
        downloadingList = new MutableLiveData<>();
        downloadcompleteList = new MutableLiveData<>();
    }

    public LiveData<List<DownloadInfo>> getDownloadingList() {
        if (downloadingList == null) {
            downloadingList = new MutableLiveData<>();
        }
        return downloadingList;
    }

    public void setDownloadingList(List<DownloadInfo> downloadingList) {
        this.downloadingList.postValue(downloadingList);
        boolean b=downloadingList==null;
        LogUtil.d(TAG, "设置viewmodel数据,数据是null？"+b);
    }

    public LiveData<List<DownloadInfo>> getDownloadcompleteList() {
        if (downloadcompleteList == null) {
            downloadcompleteList = new MutableLiveData<>();
        }
        return downloadcompleteList;
    }

    public void setDownloadcompleteList(List<DownloadInfo> downloadcompleteList) {
        this.downloadcompleteList.postValue(downloadcompleteList);
        boolean b=downloadcompleteList==null;
        LogUtil.d(TAG, "设置viewmodel数据,数据是null？"+b);
    }

    //工厂类
 /*   public static class ViewModelFactory implements ViewModelProvider.Factory {

        private DownloadClickMethod cInterface;

        public ViewModelFactory(DownloadClickMethod cInterface) {
            this.cInterface = cInterface;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            try {
                return modelClass.getConstructor(DownloadClickMethod.class).newInstance(cInterface);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }
    }*/
}
