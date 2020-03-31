package com.example.kiylx.ti.downloadInfo_storage;

import android.content.Context;

/**
 * 只有一个静态方法，提取获取DAO接口的方法方便使用。
 * <p>
 * 这样，在其他地方操作数据库的时候，直接:
 * DownloadInfoDatabaseUtil.getDao(mContext).insertAll();
 * <p>
 * 而不必先获取database再获取dao，然后再操作：
 * DownloadInfoDatabase mDatabase;
 * mDatabase = DownloadInfoDatabase.newInstance(mContext);
 * mDatabase.getDao().insertAll();//这个getDao是@Database注释下的database类里的获取数据库操作接口的方法
 */
public class DownloadInfoDatabaseUtil {
    public static DownloadDao getDao(Context context) {
        return DownloadInfoDatabase.getInstance(context).downloadDao();
    }
}
