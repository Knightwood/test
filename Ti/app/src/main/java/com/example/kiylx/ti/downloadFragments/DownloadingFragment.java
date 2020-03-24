package com.example.kiylx.ti.downloadFragments;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.corebase.DownloadInfo;
import com.example.kiylx.ti.myFragments.RecyclerViewBaseFragment;
import com.example.kiylx.ti.myInterface.DownloadClickMethod;

import java.util.List;

public class DownloadingFragment extends RecyclerViewBaseFragment {
    private DownloadClickMethod controlInterface;

    /**
     * @param minterface 控制下载任务的接口
     * @param list       下载任务列表
     * @return downloadingFragment
     */
    public static DownloadingFragment getInstance(DownloadClickMethod minterface, List<DownloadInfo> list) {
        return new DownloadingFragment(minterface, list);
    }

    public DownloadingFragment(DownloadClickMethod minterface, List<DownloadInfo> list) {
        super(list);
        controlInterface = minterface;
    }

    //重写的viewholder中的bind方法
    @Override
    public void bindItemView(View v, DownloadInfo info) {
        TextView title = v.findViewById(R.id.downloadTtitle);
        TextView url = v.findViewById(R.id.downloadUrl);
        ProgressBar progressBar = v.findViewById(R.id.downloadProgressBar);
        ImageView playButtom = v.findViewById(R.id.resumeDownload);
        //ImageView deleteButtom=v.findViewById(R.id.deleteDownloadinfo);

        title.setText(info.getFileName());
        url.setText(info.getUrl());

        new Thread(new updateProgress(info, progressBar)).start();
        playButtom.setImageResource(setPlayButtomBackgroud(info));

    }

    /**
     * 更新进度条进度
     */
    private class updateProgress implements Runnable {
        private final DownloadInfo info;
        private final ProgressBar bar;

        public updateProgress(DownloadInfo info, ProgressBar bar) {
            this.info = info;
            this.bar = bar;
        }

        @Override
        public void run() {
            while (!info.isPause()||!info.isDownloadSuccess()) {
                try {
                    Thread.sleep(500);
                    this.bar.setProgress((int) (info.getProcress())*100);
                    Log.d("下载进度", "run: "
                            +info.getFileName()
                            +"已下载"
                            +info.getCurrentLength()
                            +"总大小"
                            +info.getContentLength()
                            +"百分比"
                            +(info.getProcress())
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
        if (!info.isPause()) {
            return R.drawable.ic_pause_black_24dp;
        } else {
            return R.drawable.ic_play_arrow_black_24dp;
        }
    }

}
