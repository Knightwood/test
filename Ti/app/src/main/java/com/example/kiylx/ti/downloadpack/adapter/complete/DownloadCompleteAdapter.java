package com.example.kiylx.ti.downloadpack.adapter.complete;

import android.view.View;

import com.crystal.customview.baseadapter1.BaseAdapter;
import com.crystal.customview.baseadapter1.BaseHolder;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.downloadpack.bean.DownloadInfo;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/9/2 13:58
 * packageName：com.example.kiylx.ti.downloadpack.adapter.complete
 * 描述：
 */
public class DownloadCompleteAdapter extends BaseAdapter<DownloadInfo, BaseHolder<DownloadInfo>> {

    public DownloadCompleteAdapter(List<DownloadInfo> list) {
        super(list);
    }

    @Override
    protected int getItemViewResId(int viewType) {
        return R.layout.history_item;
    }

    @Override
    public int getViewType(DownloadInfo bean) {
        return 0;
    }

    @Override
    public BaseHolder<DownloadInfo> createHolder(View itemView, int viewType) {
        return new DownloadCompleteHolder(itemView);
    }

    @Override
    public void bind(BaseHolder<DownloadInfo> holder, DownloadInfo data, int viewType) {
        ((DownloadCompleteHolder)holder).bind(data);
        holder.setOnIntemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.more_setting:
                        //itempopmenu()
                        break;

                }
            }
        });
    }
}
