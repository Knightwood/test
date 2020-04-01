package com.example.kiylx.ti.downloadFragments;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.corebase.DownloadInfo;
import com.example.kiylx.ti.model.EventMessage;
import com.example.kiylx.ti.myInterface.DownloadClickMethod;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class DownloadingFragment extends RecyclerViewBaseFragment {
    private static DownloadClickMethod controlInterface;
    private static final String TAG = "正在下载fragment";
    private static List<DownloadInfo> downloadInfoList;

    /**
     * @param minterface 控制下载任务的接口
     * @return downloadingFragment
     */
    public static DownloadingFragment newInstance(DownloadClickMethod minterface) {
        controlInterface=minterface;
        return new DownloadingFragment();
    }
    public DownloadingFragment(){
        super();
    }

    @Override
    public void onStart() {
        super.onStart();
        //注册eventbus，用于downloadManager中数据发生改变时，在这里重新获取数据更新界面
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        downloadInfoList=null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMsg(EventMessage message) {
        if (message.getType() == 1) {
            Log.d(TAG, "eventbus接受到了事件，正在更新视图");
            downloadInfoList= controlInterface.getAllDownload();
            updateUI(downloadInfoList);
        }
    }

    @Override
    public List<DownloadInfo> downloadInfoList() {
        if (downloadInfoList==null){
            downloadInfoList=controlInterface.getAllDownload();
        }
        return downloadInfoList;
    }

    //重写的viewholder中的bind方法
    @Override
    public void bindItemView(View v, DownloadInfo info) {
        TextView title = v.findViewById(R.id.downloadTtitle);
        //TextView url = v.findViewById(R.id.downloadUrl);

        ProgressBar progressBar = v.findViewById(R.id.downloadProgressBar);
        progressBar.setProgress(info.getIntPercent());

        ImageView playButtom = v.findViewById(R.id.resumeDownload);
        playButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v12) {
                if (info.isPause()) {
                    controlInterface.reasume(info);
                    playButtom.setImageResource(DownloadingFragment.this.setPlay_Arrow(false));
                } else {
                    controlInterface.pause(info);
                    playButtom.setImageResource(DownloadingFragment.this.setPlay_Arrow(true));
                }

            }
        });

        ImageView deleteButtom = v.findViewById(R.id.deleteDownloadinfo);
        deleteButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                controlInterface.cancel(info);
            }
        });

        title.setText(info.getFileName());
        //url.setText(info.getUrl());

        new Thread(new updateProgress(info, progressBar)).start();
        playButtom.setImageResource(setPlayButtomBackgroud(info));

    }

    /**
     * 更新进度条进度
     */
    private static class updateProgress implements Runnable {
        private final DownloadInfo info;
        private final ProgressBar bar;

        public updateProgress(DownloadInfo info, ProgressBar bar) {
            this.info = info;
            this.bar = bar;
        }

        @Override
        public void run() {
            //正在下载时更新进度条
            while (!(info.isPause() || info.isDownloadSuccess() || info.isWaitDownload())) {
                try {
                    Thread.sleep(500);
                    this.bar.setProgress(info.getIntPercent());
                    Log.d(TAG, "下载进度: "
                            + info.getFileName()
                            + "已下载"
                            + info.getCurrentLength()
                            + "总大小"
                            + info.getContentLength()
                            + "百分比"
                            + (info.getPercent())
                    );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param info 下载任务
     * @return 根据暂停与否返回不同的资源id
     * <p>
     * 下载任务不在暂停状态，返回一张“暂停”的图片id，反之，返回一张“播放”的图片id
     */
    private int setPlayButtomBackgroud(DownloadInfo info) {
        if (!(info.isPause() || info.isDownloadSuccess())) {
            return R.drawable.ic_pause_black_24dp;
        } else {
            return R.drawable.ic_play_arrow_black_24dp;
        }
    }
    /**
     * @return 根据暂停与否返回不同的资源id
     * <p>
     * true则设置play_arrow图片，false设置pause图片
     */
    private int setPlay_Arrow(boolean b) {
        return b? R.drawable.ic_play_arrow_black_24dp:R.drawable.ic_pause_black_24dp;
    }
}
