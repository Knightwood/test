package com.example.kiylx.ti.downloadpack.adapter.complete;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crystal.customview.baseadapter1.BaseHolder;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.downloadpack.bean.DownloadInfo;

/**
 * 创建者 kiylx
 * 创建时间 2020/9/2 13:59
 * packageName：com.example.kiylx.ti.downloadpack.adapter.complete
 * 描述：
 */
public class DownloadCompleteHolder extends BaseHolder<DownloadInfo>{

    public DownloadCompleteHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(DownloadInfo data) {
        super.bind(data);
        TextView title=getView(R.id.itemTitle);
        TextView url=getView(R.id.itemurl);
        //ImageView optionView=getView(R.id.more_setting);

        title.setText(data.getFileName());
        url.setText(data.getUrl());
    }

    @Override
    public void setOnIntemClickListener(View.OnClickListener listener) {
        super.setOnIntemClickListener(listener);
        getView(R.id.more_setting).setOnClickListener(listener);
    }
}
