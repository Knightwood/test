package com.example.kiylx.ti.downloadpack.downloadcore;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.kiylx.ti.conf.WebviewConf;
import com.example.kiylx.ti.downloadpack.core.DownloadInfo;
import com.example.kiylx.ti.downloadpack.core.DownloadManager;
import com.example.kiylx.ti.downloadpack.core.OkhttpManager;
import com.example.kiylx.ti.downloadpack.dinterface.SendData;
import com.example.kiylx.ti.downloadpack.core.notifyAction;
import com.example.kiylx.ti.downloadpack.db.DownloadDao;
import com.example.kiylx.ti.downloadpack.db.DownloadEntity;
import com.example.kiylx.ti.downloadpack.db.DownloadInfoDatabaseUtil;
import com.example.kiylx.ti.downloadpack.db.InfoTransformToEntitiy;
import com.example.kiylx.ti.model.EventMessage;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.tool.preferences.PreferenceTools;
import com.example.kiylx.ti.xapplication.Xapplication;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

/**
 * 创建者 kiylx
 * 创建时间 2020/9/7 10:07
 * packageName：com.example.kiylx.ti.downloadpack.downloadcore
 * 描述：
 */
public class SimpleDownlaodManager extends DownloadManager {
    private static final String TAG = "下载管理器";
    private static volatile SimpleDownlaodManager mDownloadManager;
    private WeakReference<Context> mContext=null;
    private DownloadDao dao;
    private SendData mInterface;


    public static SimpleDownlaodManager newInstance(OkhttpManager mOkhttpManager, ExecutorService downloadTaskThreadPool, ExecutorService threadPool2, int downloadNumLimit, SendData mInterface) {
        if (mDownloadManager == null) {
            synchronized (DownloadManager.class) {
                if (mDownloadManager == null) {
                    mDownloadManager = new SimpleDownlaodManager(mOkhttpManager, downloadTaskThreadPool, threadPool2, downloadNumLimit, mInterface);
                }
            }
        }else {
            if (mDownloadManager.mInterface==null){
                mDownloadManager.setSendDataInterface(mInterface);
            }
        }
        return mDownloadManager;
    }

    private SimpleDownlaodManager(OkhttpManager mOkhttpManager, ExecutorService downloadTaskThreadPool, ExecutorService threadPool2, int downloadNumLimit, SendData mInterface) {
        super(mOkhttpManager, downloadTaskThreadPool, threadPool2, downloadNumLimit);
        dao = DownloadInfoDatabaseUtil.getDao(Xapplication.getInstance());
        this.mContext = new WeakReference<>(Xapplication.getInstance());
        //获取配置文件里的下载数量限制，赋值给downloadNumLimit
        //downloadNumLimit = PreferenceTools.getInt(mContext.get(), WebviewConf.defaultDownloadlimit, 3);

        this.mInterface = mInterface;
        //从数据库拿数据
        resumeInfoFromDB();
        scheduleWrite();
    }


    @NonNull
    @Override
    protected String getDownloadPath() {
        return PreferenceTools.getString(mContext.get(), WebviewConf.defaultDownloadPath, null);
    }

    @Override
    protected void notifytTaskChanged(String uuid, notifyAction notifyAction) {
        super.notifytTaskChanged(uuid, notifyAction);
        switch (notifyAction){
            case cancel:
            case complete:
            case delete:
                EventMessage msg = new EventMessage(1, "更新下载列表");
                EventBus.getDefault().post(msg);
                LogUtil.d(TAG, "下载数：" + downloading.size() + "暂停数：" + pausedownload.size());
                break;
        }

    }

    /**
     * 在创建manager时,获取数据库里的信息.
     * 把获取到的信息合并进暂停下载列表
     * firstCreate: 每次创建manager时获取数据库数据.之后
     */
    @Override
    protected void resumeInfoFromDB() {
            threadPool2.execute(new Runnable() {
            /**
             * 获取数据库里的所有条目,
             * 把每个未完成下载条目配置为paused状态,并加入暂停列表.
             * 如果是完成下载的条目,加入completeDownload列表
             */
            @Override
            public void run() {
                List<DownloadInfo> infos = getData();//获取数据库里的数据
                if (infos.isEmpty()) {
                    return;
                } else {
                    for (DownloadInfo info : infos) {
                        if (!info.isDownloadSuccess()) {
                            info.setWaitDownload(false);
                            info.setPause(true);
                            pausedownload.add(info);
                        } else {
                            completeDownload.add(info);
                        }

                    }
                }
                LogUtil.d(TAG, "从数据库读取数据 " + pausedownload.size() + "/" + downloading.size() + "/" + readyDownload.size());

                //通知已经完成数据的初始化
                if (mInterface!=null){
                    List<DownloadInfo> all=new ArrayList<>();
                    all.addAll(downloading);
                    all.addAll(pausedownload);
                    all.addAll(readyDownload);
                    mInterface.allDownloading(all);
                    mInterface.completeDownload(completeDownload);
                }
            }
        });



    }

    /**
     * @param info 更新下载数据库
     */
    @Deprecated
    private void updateData(DownloadInfo info) {
        dao.update(InfoTransformToEntitiy.transformToEntity(info));
    }

    /**
     * @return 返回downloadInfo类型的list
     * <p>
     * 从数据库获取所有条目,然后翻译成downloadInfo类型的list并返回.
     * 如果获取的数据是空的,什么也不做,返回空的arraylist
     */
    @NonNull
    protected List<DownloadInfo> getData() {
        List<DownloadEntity> list = dao.getAll();
        List<DownloadInfo> result = new ArrayList<>();
        if (list == null || list.isEmpty()) {

        } else {
            for (DownloadEntity en : list) {
                result.add(InfoTransformToEntitiy.transformToInfo(en));
            }
        }

        return result;
    }

    /**
     * 在线程中执行
     *
     * @param info 把下载数据写入数据库
     */
    @Override
    protected void insertInfo(DownloadInfo info) {
          threadPool2.execute(new Runnable() {
            @Override
            public void run() {
                dao.insertAll(InfoTransformToEntitiy.transformToEntity(info));
            }
        });
    }

    /**
     * 在线程中执行
     *
     * @param info 把数据从数据库删除
     */
    @Override
    protected void deleteInfo(DownloadInfo info) {
          threadPool2.execute(new Runnable() {
            @Override
            public void run() {
                dao.delete(InfoTransformToEntitiy.transformToEntity(info));
            }
        });

    }

    /**
     * 在线程中执行
     *
     * @param info 更新下载数据库
     */
    @Override
    protected void updateInfo(DownloadInfo info) {
      /*  Thread baseThread = new BaseThread(info, this::updateData);//方法引用
        baseThread.start();*/
          threadPool2.execute(new Runnable() {
            @Override
            public void run() {
                dao.update(InfoTransformToEntitiy.transformToEntity(info));
            }
        });

    }


    /**
     * 遍历list列表,把下载任务写入数据库以更新数据
     * 这个方法要在非主线程里使用
     */
    @Override
   protected void writeDataTodb(List<DownloadInfo> list) {
        for (DownloadInfo info : list) {
            updateInfo(info);
        }
    }

    /**
     * 以500毫秒的间隔不停地把正在下载的数据写入数据库更新数据
     * 正在下载列表最大也就5个任务
     */
    private void scheduleWrite() {
        Timer timer = new Timer();
        UpdateTask updateTask = new UpdateTask();
        timer.schedule(updateTask, 500L, 500L);
    }

    public void setSendDataInterface(SendData dataInterface) {
        this.mInterface = mInterface;
    }

    class UpdateTask extends TimerTask {

        @Override
        public void run() {
            if (!downloading.isEmpty()) {
                writeData();
            }

        }
    }


}
