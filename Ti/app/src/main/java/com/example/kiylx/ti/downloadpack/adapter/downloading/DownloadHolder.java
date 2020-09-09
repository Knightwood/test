package com.example.kiylx.ti.downloadpack.adapter.downloading;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.crystal.customview.baseadapter1.BaseHolder;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.downloadpack.core.DownloadInfo;
import com.example.kiylx.ti.tool.LogUtil;

/**
 * 创建者 kiylx
 * 创建时间 2020/9/2 13:56
 * packageName：com.example.kiylx.ti.downloadpack.adapter
 * 描述：
 */
public class DownloadHolder extends BaseHolder<DownloadInfo> {
    private static final String TAG = "DownlaodHolder";

    public DownloadHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(DownloadInfo data) {
        super.bind(data);

        ProgressBar progressBar = getView(R.id.downloadProgressBar);
        progressBar.setProgress(data.getIntPercent());

        ImageView playButton=getView(R.id.resumeDownload);// playButtom
        getView(R.id.deleteDownloadinfo);//deleteButtom

        setText(R.id.downloadTtitle, data.getFileName());//下载标题
        //setText(R.id.downloadUrl,data.getUrl());//下载网址


        new Thread(new updateProgress(data, progressBar)).start();
        playButton.setImageResource(setPlayButtomBackgroud(data));
    }

    @Override
    public void setOnIntemClickListener(View.OnClickListener listener) {
        super.setOnIntemClickListener(listener);
        getView(R.id.resumeDownload).setOnClickListener(listener);
        getView(R.id.deleteDownloadinfo).setOnClickListener(listener);

    }

    /**
     * 更新进度条进度
     */
    private static class updateProgress implements Runnable {
        private final DownloadInfo info;
        private final ProgressBar bar;

        updateProgress(DownloadInfo info, ProgressBar bar) {
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
                    LogUtil.d(TAG, "下载进度: "
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
    protected int setPlayButtomBackgroud(DownloadInfo info) {
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
    protected int setPlay_Arrow(boolean b) {
        return b ? R.drawable.ic_play_arrow_black_24dp : R.drawable.ic_pause_black_24dp;
    }
}
