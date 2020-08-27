package com.example.kiylx.ti.adapters.bookmarkadapter;

import android.view.View;

import com.crystal.customview.baseadapter1.BaseHolder;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.activitys.BookmarkManagerActivity;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/25 7:24
 * packageName：com.example.kiylx.ti.adapters.bookmarkadapter
 * 描述：
 */
public class bookmarkholder extends BaseHolder<WebPage_Info> {
    private static final String TAG = "BookmarkActivity2";

    public bookmarkholder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(WebPage_Info data){
       setImageResource(R.id.Bookmarkimage, R.drawable.ic_bookmark_black_24dp)
                .setText(R.id.itemTitle, data.getTitle())
                .setText(R.id.itemurl, data.getUrl())
                .setImageResource(R.id.more_setting, R.drawable.morevert);

        LogUtil.d(TAG,"书签bind"+data.getUuid());
    }

    @Override
    public void setOnIntemClickListener(View.OnClickListener listener) {
        super.setOnIntemClickListener(listener);
        getView(R.id.more_setting).setOnClickListener(listener);
        getView(R.id.itemTitle).setOnClickListener(listener);
    }

    @Override
    public void setOnIntemLongClickListener(View.OnLongClickListener listener) {
        super.setOnIntemLongClickListener(listener);
        getView(R.id.itemTitle).setOnLongClickListener(listener);
    }
}
