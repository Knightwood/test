package com.example.kiylx.ti.downloadCore;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.example.kiylx.ti.corebase.DownloadInfo;
import com.example.kiylx.ti.corebase.SomeRes;
import com.example.kiylx.ti.downloadInfo_storage.DatabaseUtil;
import com.example.kiylx.ti.downloadInfo_storage.InfoTransformToEntitiy;
import com.example.kiylx.ti.myInterface.DOWNLOAD_TASK_FUN;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static io.reactivex.Flowable.fromArray;

/*
 * 暂停写下载信息数据库，用“//-”先注释，完善以下功能在继续写数据库。
 * 要增加一个观察者模式推送各个下载list给downloadactivity
 * 要增加代码更新item的xml的进度条
 * 增加前台服务，不再写绑定到mainactivity等啰嗦的代码
 * 关于下载list为null的情况的处理*/
public class DownloadManager {
    private static final String TAG = "下载管理器";

    private volatile static DownloadManager mDownloadManager;
    private OkhttpManager mOkhttpManager;
    private static StorgeTask mStorgeTask;

    private List<DownloadInfo> readyDownload;
    private List<DownloadInfo> downloading;
    private List<DownloadInfo> pausedownload;
    private List<DownloadInfo> completeDownload;

    private int downloadNumLimit;
    private Context mContext;

    public static DownloadManager getInstance() {
        if (mDownloadManager == null) {
            synchronized (DownloadManager.class) {
                if (mDownloadManager == null) {
                    mDownloadManager = new DownloadManager();

                }
            }
        }
        return mDownloadManager;
    }


    /**
     * 超过这个限制，把任务加入readydownload
     */
    private DownloadManager() {
        //获取配置文件里的下载数量限制，赋值给downloadNumLimit
        downloadNumLimit = SomeRes.downloadLimit;

        downloading = new ArrayList<>();//正在下载列表
        pausedownload = new ArrayList<>();//暂停下载列表
        readyDownload = new LinkedList<>();//准备下载列表

        //获取流的管理器
        mOkhttpManager = OkhttpManager.getInstance();
        //存入数据库和通知更新进度的线程
        //-mStorgeTask = new StorgeTask();
        completeDownload = new ArrayList<>();
        //completeDownload从数据库获取数据
        //pausedownload从数据库拿数据
    }

    /**
     * 线程中执行的任务，在遇到下载成功，失败，取消，暂停，更新进度时所使用
     */
    private DOWNLOAD_TASK_FUN mTASK_fun = new DOWNLOAD_TASK_FUN() {

        /**
         * @param info 下载信息
         * @return
         * 每条线程都会调用，那么每次一个线程调用，当threaduse累加等于线程数时
         * 文件的多个线程下载都已经暂停，把其从正在下载中移除，放入暂停下载
         */
        @Override
        public synchronized boolean downloadPaused(DownloadInfo info) {
            info.setblockPauseNum(1);
            //所有下载文件的线程已经暂停
            if (info.getblockPause()) {
                //暂停,一种情况是正在下载时暂停，另一种是准备下载时暂停
                downloading.remove(info);
                pausedownload.add(info);
            }
            Log.d(TAG, "下载暂停成功");
            return true;
        }

        /**
         * @param info 下载信息
         * @return
         * 每一个线程完成下载都会使完成数量加一，标志着文件块的下载成功，累计为线程数个数的文件块下载完成，下载成功。
         * completeNum不应被清零。
         */
        @Override
        public synchronized boolean downloadSucess(DownloadInfo info) {

            info.setCompleteNum();//这个方法,每个分块都下载成功就会设置成功标志为真
            if (info.isDownloadSuccess()) {
                //下载完成，触发通知
                //并且，把downloading中的此downloadinfo移除，且把完成信息放入“持久化存储”
                afterDownloadComplete(info);
                //调入新的下载任务开始下载
                if (!readyDownload.isEmpty()) {
                    try {
                        startDownload(readyDownload.remove(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                return false;
            }
            System.out.println("下载成功");
            return true;
        }
    };


//--------------------------------------以上是接口方法多线程会调用它，以下是一些下载方法------------------------------//


    /**
     * @param info downloadinfo
     *             加工信息，生成文件分块下载信息等。
     */
    public void processInfo(@NonNull DownloadInfo info) throws IOException {
        //用于文件下载的线程数
        int threadNum = info.getThreadNum();
        //如果下载文件的大小已经给出了（也就是info里contentLength不等于0），就直接赋值。否则，调用getFileLength获取
        info.setContentLength(info.getContentLength() == 0 ? mOkhttpManager.getFileLength(info.getUrl()) : info.getContentLength());

        //文件的分块大小
        long blocksize = info.getContentLength() / threadNum;
        info.setBlockSize(blocksize);

        //建立数组准备存储分块信息
        info.splitEnd = new long[threadNum];
        info.splitStart = new long[threadNum];

        //开始块和结束块。
        for (int i = 0; i < threadNum; i++) {
            info.splitStart[i] = i * blocksize;
            info.splitEnd[i] = (i + 1) * blocksize - 1;
        }
        //文件结尾
        info.splitEnd[threadNum - 1] = info.getContentLength() - 1;
    }

    /**
     * 下载完成，把任务从正在下载列表移除，放进下载完成列表，并更新数据库数据。
     */
    private void afterDownloadComplete(DownloadInfo info) {
        completeDownload.add(info);
        downloading.remove(info);
        //更新数据库数据
    }

    /**
     * @param info 文件下载信息
     * @throws IOException 抛出异常
     */
    public void startDownload(@NonNull DownloadInfo info) throws IOException {
        /*
         * “暂停”为假,“准备下载”为真(刚构建的下载任务中“暂停”是假，“等待下载”是假),
         * 说明这是暂停之后要恢复下载
         * 若恢复下载时下载列表满了，加入准备下载列表
         * */
        if (!info.isPause() && info.isWaitDownload()) {
            //注：恢复下载
            if (downloading.size() == SomeRes.downloadLimit)
                readyDownload.add(info);
            else {
                downloading.add(info);
                /*int i = info.getThreadNum();
                for (int j = 0; j < i; j++) {
                    TaskPool.getInstance().executeTask(new DownloadTaskRunnable(info, j, mTASK_fun));
                }*/
                execDownload(info);
            }
        } else {
            //注：全新开始的下载
            //注：限制下载，超过限制的放入readyDownload的零号位置，等当前下载任务完成再从0位置取出来下载
            if (downloading.size() == downloadNumLimit) {
                //加入准备下载
                readyDownload.add(info);
                info.setPause(true);
            } else {
                //注：还没到下载数量限制，这是第一次开始下载
                //处理下载信息
                processInfo(info);
                //执行下载
                execDownload(info);

                //加入下载队列
                downloading.add(info);
                //第一次开始下载就该存入数据库
                //-insertData(info);
            }
        }
        //执行更新线程，只要开始下载就要开始更新
        //注：<T> T[] toArray(T[] a);此toArray方法接受一个类型为T的数组，
        //-mStorgeTask.execute(downloading);
        //重置threadUse
        info.setblockPauseNum(0);

    }

    /**
     * @param info 下载信息
     *             使用线程池执行下载任务
     */
    private void execDownload(DownloadInfo info) {
        int i = info.getThreadNum();
        for (int j = 0; j < i; j++) {
            TaskPool.getInstance().executeTask(new DownloadTaskRunnable(info, j, mTASK_fun));
        }
    }

    /**
     * @param info 下载信息
     *             <p>
     *             若是正在下载，则修改下载信息的暂停标记，使得下载文件的所有线程暂停文件块的下载
     *             <p>
     *             暂停的时候因为有下载数量限制，所以有一些调用暂停时是暂态等待下载的任务。
     */
    public void pauseDownload(@NonNull DownloadInfo info) {
        if (info.isWaitDownload()) {//如果是等待下载（等待下载时也算是“正在下载的一种”）
            info.setWaitDownload(false);
        }
        //正在下载或是等待下载，都要设置"暂停"为真
        info.setPause(true);

    }

    /**
     * 先把准备下载队列清空，然后把正在下载列表清空。
     */
    public void pauseAll() {
        for (int i = 0; i < readyDownload.size(); i++) {
            DownloadInfo info = readyDownload.get(i);
            info.setPause(true);
            info.setWaitDownload(false);
            pausedownload.add(info);
        }
        readyDownload.clear();
        for (int i = 0; i < downloading.size(); i++) {

            downloading.get(i).setPause(true);
        }

    }

    /**
     * @param info 下载信息
     *             继续下载任务，在resume()方法中使任务继续下载，
     *             在resumeDownload(@NonNull DownloadInfo info)中移除暂停下载列表中的任务
     */
    public void resumeDownload(@NonNull DownloadInfo info) {

        resume(info);
        pausedownload.remove(info);//移除暂停列表中的这个条目
    }

    /**
     * 遍历暂停下载列表，调用resume(DownloadInfo info)恢复下载，然后清空暂停下载列表
     */
    public void resumeAll() {
        for (int i = 0; i < pausedownload.size(); i++) {
            resume(pausedownload.get(i));
        }
        pausedownload.clear();
    }

    /**
     * 恢复下载的逻辑部分，可以处理单个的下载条目的恢复下载的过程。
     * 由上面的resumeDownload分别处理更精细的逻辑
     * <p>
     * 如果下载列表已达最大数量，直接把条目加入准备下载列表。若有条目下载成功，会在下载成功的方法里把准备列表中的一个开始下载
     * 否则，设置pause标志为假，加入正在下载列表，开始下载
     */
    private void resume(DownloadInfo info) {
        //如果正在下载列表满了，把这个条目加入准备列表。否则加入正在下载列表开始下载
        info.setPause(false);
        if (downloading.size() == downloadNumLimit) {
            info.setWaitDownload(true);
            readyDownload.add(info);

        } else {
            info.setWaitDownload(false);
            downloading.add(info);
            try {
                startDownload(info);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * @param info       下载信息
     *                   <p>
     *                   如果任务“正在下载”，修改下载信息的取消下载标记，使得下载文件的所有线程取消文件块的下载
     *                   <p>
     *                   取消下载的流程实现暂停，然后再删除文件
     *                   1.正在下载：调用暂停，然后删除文件。
     *                   2.暂停，直接删除文文件。
     *                   3.准备下载，调用暂停，然后删除。
     *                   <p>
     *                   先暂停，然后从暂停列表中移除info。
     */
    public void cancelDownload(@NonNull DownloadInfo info) {
            if (!info.isPause()) {
                //如果是非暂停状态需要进行暂停。（非暂停状态包括正在下载和准备下载状态）
                pauseDownload(info);
            }
            info.setCancel(true);
            pausedownload.remove(info);
            deleteFile(info);

    }

    /**
     * 取消全部下载
     */
    public void canaelAll() {
        //暂停全部任务。
        pauseAll();
        //删除文件
        for (int i = 0; i < pausedownload.size(); i++) {
            deleteFile(pausedownload.get(i));
        }
        //清除暂停下载列表
        pausedownload.clear();

    }

    /**
     * @param info 下载信息
     *             删除文件
     */
    private void deleteFile(DownloadInfo info) {
        File file = new File(info.getPath() + info.getFileName());
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    /**
     * @param info 下载信息
     * @return 返回下载进度, float类型
     * 文件的下载线程数就是文件分块的标号，
     * 那么分块的结束减去分块的开始就是未下载的部分
     */
    float getPercentage(DownloadInfo info) {
        /*long unDownloadPart = 0;//未下载的部分
        for (int i = 0; i < info.getThreadNum(); i++) {
            unDownloadPart += (info.splitEnd[i] - info.splitStart[i] + 1);
        }
        //设置已下载的长度
        info.setCurrentLength(info.getContentLength() - unDownloadPart);
        //返回已下载百分比
        return (float) (info.getCurrentLength() / info.getContentLength());*/

        return info.getProcress();
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    @NotNull
    public List<DownloadInfo> getDownloading() {
        return downloading;
    }

    /**
     * @return 返回正在下载的任务数量
     */
    public int getDownloadingNum() {
        if (downloading.isEmpty()) {
            return 0;
        } else {
            return downloading.size();
        }
    }

    /**
     * @return 返回暂停下载的个数
     */
    public int getPauseNum() {
        if (pausedownload.isEmpty()) {
            return 0;
        } else {
            return pausedownload.size();
        }
    }

    /*废弃
    private void getDatabase() {
        mDatabase = DownloadInfoDatabase.getInstance(mContext);
    }*/
//-----------------------数据库操作---------------------//
    private void insertData(DownloadInfo info) {
        DatabaseUtil.getDao(mContext).insertAll(InfoTransformToEntitiy.transformInfo(info));
    }

    private void updateData(DownloadInfo info) {
        DatabaseUtil.getDao(mContext).update(InfoTransformToEntitiy.transformInfo(info));
    }

    private void delete(DownloadInfo info) {
        DatabaseUtil.getDao(mContext).delete(InfoTransformToEntitiy.transformInfo(info));
    }


    /**
     * 在downloading列表不为空的时候不停的更新里面 “ 每一个条目 ” 的数据。
     * 1.不断地把下载条目的下载进度记录进数据库，
     * 2.通知每一个下载条目的下载进度，用于下载界面的进度条更新
     */
    //---------------------在线程里存储进数据库-----------------------//


    private class StorgeTask extends AsyncTask<List<DownloadInfo>, Integer, Integer> {
        // 类中参数为3种泛型类型
// 整体作用：控制AsyncTask子类执行线程任务时各个阶段的返回类型
// 具体说明：
        // a. Params：开始异步任务执行时传入的参数类型，对应excute（）中传递的参数
        // b. Progress：异步任务执行过程中，返回下载进度值的类型
        // c. Result：异步任务执行完成后，返回的结果类型，与doInBackground()的返回值类型保持一致
// 注：
        // a. 使用时并不是所有类型都被使用
        // b. 若无被使用，可用java.lang.Void类型代替
        // c. 若有不同业务，需额外再写1个AsyncTask的子类


        /**
         * @param lists 可变参数，类型是List<DownloadInfo>
         * @return 返回 ？？
         * <p>
         * StorgeTask的泛型参数第一个是List<DownloadInfo>类型，
         * 但是DoinBackground接受的是一个可变长参数，所以可以只传入一个参数：downloading列表。
         */
        /*
         * 在声明具有模糊类型（比如：泛型）的可变参数的构造函数或方法时，
         * Java编译器会报unchecked警告。
         * 鉴于这些情况，如果程序员断定声明的构造函数和方法的主体不会对其varargs参数执行潜在的不安全的操作，
         * 可使用@SafeVarargs进行标记，这样的话，Java编译器就不会报unchecked警告。
         * 使用的时候要注意：@SafeVarargs注解，对于非static或非final声明的方法，不适用，会编译不通过。
         * 方法未声明为static或final方法，不能使用@SafeVarargs,如果要抑制unchecked警告，可以使用@SuppressWarnings注解。
         * */
        @SuppressWarnings("unchecked")
        @Override
        protected Integer doInBackground(List<DownloadInfo>... lists) {

            //注：迭代器，用于遍历downloading列表
            Iterator<DownloadInfo> iterator = lists[0].iterator();

            while (iterator.hasNext()) {
                try {
                    Thread.sleep(2 * 1000);//2秒更新一次数据
                    //-updateData(iterator.next());
                    //publishProgress((int)downloadInfos[0].getCurrentLength());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return 1;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }

    private void storge() {

        //interval 操作符用于间隔时间执行某个操作，其接受三个参数，分别是第一次发送延迟，间隔时间，时间单位

        Observer<DownloadInfo> observer = new Observer<DownloadInfo>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DownloadInfo downloadInfo) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

    }

}
/*
 *
 * */