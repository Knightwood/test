package com.example.kiylx.ti.downloadpack.core;

import androidx.annotation.NonNull;

import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.tool.observable.Observable;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

/*
 * 暂停写下载信息数据库，用“//-”先注释，完善以下功能在继续写数据库。
 * 要增加一个观察者模式推送各个下载list给downloadactivity
 * 要增加代码更新item的xml的进度条
 * 增加前台服务，不再写绑定到mainactivity等啰嗦的代码
 * 关于下载list为null的情况的处理*/
public class DownloadManager extends Observable {
    private static final String TAG = "下载管理器";

    private OkhttpManager mOkhttpManager;

    private ExecutorService downloadTaskThreadPool;//下载任务所需要的线程池
    protected ExecutorService threadPool2;//记录下载信息之类的线程池

    protected static List<DownloadInfo> readyDownload;
    protected static List<DownloadInfo> downloading;
    protected static List<DownloadInfo> pausedownload;
    protected static List<DownloadInfo> completeDownload;

    private int downloadNumLimit;

    public DownloadManager(OkhttpManager mOkhttpManager,@NonNull ExecutorService downloadTaskThreadPool,@NonNull ExecutorService threadPool2, int downloadNumLimit) {
        if (mOkhttpManager==null) {
            this.mOkhttpManager = mOkhttpManager;}
        else{
            this.mOkhttpManager=OkhttpManager.getInstance();
        }
        this.downloadTaskThreadPool = downloadTaskThreadPool;
        this.threadPool2 = threadPool2;
        this.downloadNumLimit = downloadNumLimit;
        initDownloadManager();
    }

    private void initDownloadManager() {
        downloading = new Vector<>();//正在下载列表
        pausedownload = new Vector<>();//暂停下载列表
        readyDownload = new Vector<>();//准备下载列表

        //获取流的管理器
        mOkhttpManager = OkhttpManager.getInstance();
        //存入数据库和通知更新进度的线程
        completeDownload = new Vector<>();
    }

    /**
     * 子类可实现此方法，在此类实例化时将数据库数据添加到合适的list
     */
    protected void resumeInfoFromDB(){

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

                updateInfo(info);
            }
            LogUtil.d(TAG, "下载暂停成功");
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
                        startDownload(readyDownload.remove(0), true);
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
        if (info.getContentLength() < 10000000) {
            //1kb=1024b,1m=1024kb,1m=
            threadNum = info.setThreadNum(1);
        }

        //校验下载到文件夹的路径是否存在，若不存在，给予一个默认值
        if (info.getPath()==null){
            info.setPath(getDownloadPath());
        }

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
     * @return 提供下载文件到此文件夹。
     * 在不同设备系统上都不一致，因此此方法必须要实现
     */
    @NonNull
    protected String getDownloadPath() {
        //默认路径
        //例如： return "C:\\Users\\Kiylx\\Desktop\\新建文件夹";
        return null;
    }

    /**
     * 下载完成，把任务从正在下载列表移除，放进下载完成列表，并更新数据库数据。
     */
    private void afterDownloadComplete(DownloadInfo info) {
        completeDownload.add(info);
        downloading.remove(info);
        updateInfo(info);//更新数据库数据

        notifytTaskChanged(info.getUuid(),notifyAction.complete);//通知别的类，这里的列表发生了改变
    }

    /**
     *
     * @param uuid 发生改状态变的downloadinfo的uuid
     * @param notifyAction 发生的行为变化的信息
     *                     通知订阅者数据更新变化
     */
    protected void notifytTaskChanged(String uuid, notifyAction notifyAction) {
        notifyObservers(uuid,notifyAction);
    }
    /**
     * @param info 文件下载信息
     * @throws IOException 抛出异常
     */
    public void startDownload(@NonNull DownloadInfo info, boolean isResume) throws IOException {
        /*
         * “暂停”为假,“准备下载”为真(刚构建的下载任务中“暂停”是假，“等待下载”是假),
         * 说明这是暂停之后要恢复下载
         * 若恢复下载时下载列表满了，加入准备下载列表
         * */
        if (isResume) {
            //注：恢复下载
            if (downloading.size() != SomeRes.downloadLimit) {
                execDownload(info);
            }
        } else {
            //处理下载信息
            processInfo(info);

            //注：全新开始的下载
            //注：限制下载，超过限制的放入readyDownload的零号位置，等当前下载任务完成再从0位置取出来下载
            if (downloading.size() == downloadNumLimit) {
                //加入准备下载
                readyDownload.add(info);
                info.setWaitDownload(true);
            } else {
                //注：还没到下载数量限制，这是第一次开始下载
                //执行下载
                execDownload(info);

                //加入下载队列
                downloading.add(info);
            }
            //全新开始的下载任务,不论是加入准备下载列表还是加入正在下载列表,都要把info要加入数据库
            insertInfo(info);
        }
        //执行更新线程，只要开始下载就要开始更新
        //注：<T> T[] toArray(T[] a);此toArray方法接受一个类型为T的数组，
        updateInfo(info);//不论是新添加的任务还是旧的任务,在这里更新一次

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
            downloadTaskThreadPool.execute(new DownloadTaskRunnable(info, j, mTASK_fun));
        }
        notifytTaskChanged(info.getUuid(),notifyAction.startDwonload);
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
        downloading.remove(info);
        pausedownload.add(info);

        notifytTaskChanged(info.getUuid(),notifyAction.paused);

        LogUtil.d(TAG, "暂停下载：" + pausedownload.size() + "\n"
                + "正在下载：" + downloading.size() + "\n"
                + "准备下载：" + readyDownload.size() + "\n"
                + "完成下载：" + completeDownload.size());

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

            notifytTaskChanged(info.getUuid(),notifyAction.paused);
            updateInfo(info);
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
        pausedownload.remove(info);//移除暂停列表中的这个条目

        if (downloading.size() == downloadNumLimit) {
            info.setWaitDownload(true);
            readyDownload.add(info);

        } else {
            info.setWaitDownload(false);
            downloading.add(info);
            try {
                startDownload(info, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        notifytTaskChanged(info.getUuid(),notifyAction.resume);
        updateInfo(info);

        LogUtil.d(TAG, "暂停下载：" + pausedownload.size() + "\n"
                + "正在下载：" + downloading.size() + "\n"
                + "准备下载：" + readyDownload.size() + "\n"
                + "完成下载：" + completeDownload.size());
    }

    /**
     * @param info 下载信息
     *             <p>
     *             如果任务“正在下载”，修改下载信息的取消下载标记，使得下载文件的所有线程取消文件块的下载
     *             <p>
     *             取消下载的流程实现暂停，然后再删除文件
     *             1.正在下载：调用暂停，然后删除文件。
     *             2.暂停，直接删除文文件。
     *             3.准备下载，调用暂停，然后删除。
     *             <p>
     *             先暂停，然后从暂停列表中移除info。
     */
    public void cancelDownload(@NonNull DownloadInfo info) {
        if (!info.isPause()) {
            //如果是非暂停状态需要进行暂停。（非暂停状态包括正在下载和准备下载状态）
            pauseDownload(info);
        }
        info.setCancel(true);
        pausedownload.remove(info);
        deleteFile(info);

        notifytTaskChanged(info.getUuid(),notifyAction.cancel);//通知别的类，这里的列表发生了改变
    }

    /**
     * 取消全部下载
     */
    public void canaelAll() {
        //暂停全部任务。
        pauseAll();
        //删除文件
        DownloadInfo info;
        for (int i = 0; i < pausedownload.size(); i++) {
            info=pausedownload.get(i);
            notifytTaskChanged(info.getUuid(),notifyAction.cancel);
            deleteFile(info);
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
        notifytTaskChanged(info.getUuid(),notifyAction.delete);
        deleteInfo(info);
    }

    /**
     * @param info 下载信息
     * @return 返回下载进度, float类型
     * 文件的下载线程数就是文件分块的标号，
     * 那么分块的结束减去分块的开始就是未下载的部分
     */
    public float getPercentage(DownloadInfo info) {
        return info.getPercent();
    }

    @NotNull
    public List<DownloadInfo> getDownloading() {
        return downloading;
    }

    public List<DownloadInfo> getPausedownload() {

        return pausedownload;
    }

    public List<DownloadInfo> getReadyDownload() {
        return readyDownload;
    }

    public List<DownloadInfo> getCompleteDownload() {
        return completeDownload;
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

    //-----------------------数据库操作---------------------//


    /**
     * @return 返回downloadInfo类型的list
     * <p>
     * 从数据库获取所有条目,然后翻译成downloadInfo类型的list并返回.
     * 如果获取的数据是空的,什么也不做,返回空的arraylist
     */
    @NonNull
    protected List<DownloadInfo> getData() {
        return null;
    }

    /**
     * 在线程中执行
     *
     * @param info 把下载数据写入数据库
     */
    protected void insertInfo(DownloadInfo info) {
    }

    /**
     * 在线程中执行
     *
     * @param info 把数据从数据库删除
     */
    protected void deleteInfo(DownloadInfo info) {
    }

    /**
     * 在线程中执行
     *
     * @param info 更新下载数据库
     */
    protected void updateInfo(DownloadInfo info) {
    }


    /**
     * 遍历list列表,把下载任务写入数据库以更新数据
     * 这个方法要在非主线程里使用
     */
    protected void writeDataTodb(List<DownloadInfo> list) {
    }

    /**
     * 遍历list列表,把下载任务写入数据库以更新数据
     * 这个方法要在非主线程里使用
     */
    protected void writeData() {
        this.writeDataTodb(downloading);
    }

}