package com.example.kiylx.ti.adapters.bookmarkadapter;

import android.view.View;

import com.crystal.customview.baseadapter1.BaseHolder;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/25 7:25
 * packageName：com.example.kiylx.ti.adapters.bookmarkadapter
 * 描述：
 */
public class bookmarkFolderHolder extends BaseHolder<WebPage_Info> {
    public bookmarkFolderHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(WebPage_Info data) {
        super.bind(data);
        setImageResource(R.id.Bookmarkimage, R.drawable.ic_folder_black_24dp)
                .setText(R.id.itemTitle, data.getTitle())
                .setImageResource(R.id.more_setting, R.drawable.chevronright);
        getView(R.id.itemurl).setVisibility(View.GONE);
    }

    @Override
    public void setOnIntemClickListener(View.OnClickListener listener) {
        super.setOnIntemClickListener(listener);
        getView(R.id.itemTitle).setOnClickListener(listener);
    }

    @Override
    public void setOnIntemLongClickListener(View.OnLongClickListener listener) {
        super.setOnIntemLongClickListener(listener);
    }
}
