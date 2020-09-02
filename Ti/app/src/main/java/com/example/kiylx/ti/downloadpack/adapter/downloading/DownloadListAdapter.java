package com.example.kiylx.ti.downloadpack.adapter.downloading;

import android.view.View;

import com.crystal.customview.baseadapter1.BaseAdapter;
import com.crystal.customview.baseadapter1.BaseHolder;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.downloadpack.bean.DownloadInfo;
import com.example.kiylx.ti.downloadpack.dinterface.DownloadClickMethod;
import com.example.kiylx.ti.model.WebPage_Info;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/9/2 13:54
 * packageName：com.example.kiylx.ti.downloadpack.adapter
 * 描述：
 */
public class DownloadListAdapter extends BaseAdapter<DownloadInfo, BaseHolder<DownloadInfo>> {

    private DownloadClickMethod controlInterface;

    public DownloadListAdapter(List<DownloadInfo> list) {
        super(list);
    }

    @Override
    protected int getItemViewResId(int viewType) {
        return R.layout.download_item;
    }

    @Override
    public int getViewType(DownloadInfo bean) {
        return 0;
    }

    @Override
    public BaseHolder<DownloadInfo> createHolder(View itemView, int viewType) {
        return new DownloadHolder(itemView);
    }

    @Override
    public void bind(BaseHolder<DownloadInfo> holder, DownloadInfo data, int viewType) {
        holder.bind(data);
        holder.setOnIntemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.resumeDownload:
                        if (data.isPause()) {
                            controlInterface.reasume(data);
                            holder.setImageResource(R.id.resumeDownload, ((DownloadHolder) holder).setPlay_Arrow(false));
                        } else {
                            controlInterface.pause(data);
                            holder.setImageResource(R.id.resumeDownload, ((DownloadHolder) holder).setPlay_Arrow(true));
                        }
                        break;
                    case R.id.deleteDownloadinfo:
                        controlInterface.cancel(data);
                        break;
                }


            }
        });
    }

    public void setInterface(DownloadClickMethod controlInterface){
        this.controlInterface=controlInterface;
    }
}
